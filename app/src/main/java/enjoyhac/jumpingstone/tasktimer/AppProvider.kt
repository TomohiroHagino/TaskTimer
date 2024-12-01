package enjoyhac.jumpingstone.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

/**
 * TaskTimerアプリのプロバイダー
 *
 * [AppDatabase]のことを知っている唯一のクラス
 */

private const val TAG = "AppProvider"

const val CONTENT_AUTHORITY = "enjoyhac.jumpingstone.tasktimer.provider"

private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201

private const val TASK_DURATIONS = 400
private const val TASK_DURATIONS_ID = 401

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

class AppProvider: ContentProvider() {

    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher() : UriMatcher {
        Log.d(TAG, "buildUrlMatcher starts")
        val matcher = UriMatcher(UriMatcher.NO_MATCH)


        // e.g
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS)
        matcher.addURI(CONTENT_AUTHORITY, "${TasksContract.TABLE_NAME}/#", TASKS_ID)
        // e.g
        // matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS)
        // matcher.addURI(CONTENT_AUTHORITY, "${TimingsContract.TABLE_NAME}/#", TIMINGS_ID)
        // e.g
        // matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS)
        // matcher.addURI(CONTENT_AUTHORITY, "${DurationsContract.TABLE_NAME}/#", TASK_DURATIONS_ID)

        return matcher
    }

    override fun onCreate(): Boolean {
        Log.d(TAG,"onCreate: starts")
        return true
    }

    override fun getType(uri: Uri): String {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val match = uriMatcher.match(uri)
        Log.d(TAG,"query called with uri: $match")

        val queryBuilder: SQLiteQueryBuilder = SQLiteQueryBuilder()

        when(match) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                queryBuilder.appendWhereEscapeString("${TasksContract.Columns.ID} = $taskId" )

            }

//            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME
//
//            TIMINGS_ID -> {
//                queryBuilder.tables = TimingsContract.TABLE_NAME
//                val timingId = TasksContract.getId(uri)
//                queryBuilder.appendWhere("${TimingsContract.Columns.ID} = $timingId" )
//
//            }
//
//            TASK_DURATIONS -> queryBuilder.tables = DurationsContract.TABLE_NAME
//
//            TASK_DURATIONS_ID -> {
//                queryBuilder.tables = DurationsContract.TABLE_NAME
//                val taskDurationsId = DurationsContract.Contract.getId(uri)
//                queryBuilder.appendWhere("${DurationsContract.Columns.ID} = $taskDurationsId" )
//
//            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = AppDatabase.getInstance(context!!).readableDatabase
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG,"query: rows in returned cursor = ${cursor.count}")

        return cursor
    }

    // ContentValuesがNullableだからオプショナルにしないとだめだった
    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val match = uriMatcher.match(uri)
        Log.d(TAG,"onUpdate: $match")
        TODO("Not yet implemented")
    }

    // ContentValuesがNullableだからオプショナルにしないとだめだった
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }
}