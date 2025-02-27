package enjoyhac.jumpingstone.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.IllegalStateException

/**
 * アプリケーションの基本のデータベースクラス
 *
 * これを使うべきクラスは[AppProvider]だけ。
 */

private const val TAG = "AppDatabase"

private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 1

internal class AppDatabase private constructor(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    init {
        Log.d(TAG, "AppDatabase: 初期化しています。。")
    }

    override fun onCreate(db: SQLiteDatabase) {
        // CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER);
        Log.d(TAG, "onCreate: スタート")
        val sSQL = """CREATE TABLE ${TasksContract.TABLE_NAME} (
            ${TasksContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TasksContract.Columns.TASK_NAME} TEXT NOT NULL,
            ${TasksContract.Columns.TASK_DESCRIPTION} TEXT,
            ${TasksContract.Columns.TASK_SORT_ORDER} INTEGER
        )""".replaceIndent(" ")
        Log.d(TAG, sSQL)
        db.execSQL(sSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: スタート")
        when(oldVersion) {
            1 -> {
                //
            }
            else -> throw IllegalStateException("onUpgrade() 未知のnewVersion: $newVersion")
        }
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)
}