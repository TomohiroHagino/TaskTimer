package enjoyhac.jumpingstone.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
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
         matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS)
         matcher.addURI(CONTENT_AUTHORITY, "${TimingsContract.TABLE_NAME}/#", TIMINGS_ID)
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
        var match = uriMatcher.match(uri)
        return when (match) {
            TASKS -> TasksContract.CONTENT_TYPE
            TASKS_ID -> TasksContract.CONTENT_ITEM_TYPE

            TIMINGS -> TimingsContract.CONTENT_ITEM_TYPE
            TIMINGS_ID -> TimingsContract.CONTENT_ITEM_TYPE

            else -> throw IllegalArgumentException("unknown Uri: $uri")
//
//            TASK_DURATIONS -> DurationsContract.CONTENT_ITEM_TYPE
//            TASK_DURATIONS_ID -> DurationsContract.CONTENT_ITEM_TYPE
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        Log.d(TAG,"query: called with uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"query: match is: $match")

        val queryBuilder: SQLiteQueryBuilder = SQLiteQueryBuilder()

        when(match) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                queryBuilder.appendWhereEscapeString("$taskId" )

            }

            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME

            TIMINGS_ID -> {
                queryBuilder.tables = TimingsContract.TABLE_NAME
                val timingId = TasksContract.getId(uri)
                queryBuilder.appendWhere("${TimingsContract.Columns.ID} = $timingId" )

            }
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
        Log.d(TAG,"insert: called with uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"insert: match is: $match")

        val recordId: Long
        val returnUri: Uri

        when (match) {
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                recordId = db.insert(TasksContract.TABLE_NAME, null, values)

                if (recordId != -1L) {
                    returnUri = TasksContract.buildUriFromId(recordId)
                } else {
                    throw SQLException("Failed to insert, Uri was $uri")
                }
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                recordId = db.insert(TimingsContract.TABLE_NAME, null, values)

                if (recordId != -1L) {
                    returnUri = TimingsContract.buildUriFromId(recordId)
                } else {
                    throw SQLException("Failed to insert, Uri was $uri")
                }
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        Log.d(TAG, "Existing insert, returning $returnUri")
        return returnUri
    }

    // ContentValuesがNullableだからオプショナルにしないとだめだった
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG,"update: called with uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"update: match is: $match")

        var count = 0
        var selectionCriteria: String

        when (match) {
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs)
            }

            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"

                if (selection != null && selection.isNotEmpty()){
                    selectionCriteria = "AND ($selection)"
                }

                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs)
            }

            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"

                if (selection != null && selection.isNotEmpty()){
                    selectionCriteria = "AND ($selection)"
                }

                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        Log.d(TAG, "Existing update, returning $count")
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG,"delete: called with uri: $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"delete: match is: $match")

        var count = 0
        var selectionCriteria: String

        when (match) {
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs)
            }

            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"

                if (selection != null && selection.isNotEmpty()){
                    selectionCriteria = "AND ($selection)"
                }

                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs)
            }

            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"

                if (selection != null && selection.isNotEmpty()){
                    selectionCriteria = "AND ($selection)"
                }

                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        Log.d(TAG, "Existing update, returning $count")
        return count
    }
}