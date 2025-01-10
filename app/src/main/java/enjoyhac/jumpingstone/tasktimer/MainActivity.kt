package enjoyhac.jumpingstone.tasktimer

import android.content.ContentValues
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        testInsert()

        val projection = arrayOf(TasksContract.Columns.TASK_NAME, TasksContract.Columns.TASK_SORT_ORDER)
        val sortOrder = TasksContract.Columns.TASK_SORT_ORDER

        val cursor = contentResolver.query(
            TasksContract.CONTENT_URI,
            null,
            null,
            null,
            sortOrder
            )

        Log.d(TAG, "*************************")
        cursor!!.use {
            while(it.moveToNext()) {
                //全レコードのCycle throgh
                with(cursor) {
                    val id = getLong(0)
                    val name = getString(1)
                    val description = getString(2)
                    val sortOrder = getString(3)
                    val result = "ID: $id Name: $name description: $description sortOrder: $sortOrder"
                    Log.d(TAG, "onCreate: 読み込みデータ $result")
                }
            }
        }

        Log.d(TAG, "*************************")

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun testInsert() {
        val values = ContentValues().apply{
            put(TasksContract.Columns.TASK_NAME, "New TASK 1")
            put(TasksContract.Columns.TASK_DESCRIPTION, "Description 1")
            put(TasksContract.Columns.TASK_SORT_ORDER, 2)
        }

        val uri = contentResolver.insert(TasksContract.CONTENT_URI, values)
        Log.d(TAG, "新しい行 id (in uri) は $uri")
        Log.d(TAG, "id (in uri) は ${TasksContract.getId(uri!!)}")
    }

    private fun testUpdate() {
        val values = ContentValues().apply{
            put(TasksContract.Columns.TASK_NAME, "Content Provider")
            put(TasksContract.Columns.TASK_DESCRIPTION, "Record content providers videos")
        }

        val uri = contentResolver.insert(TasksContract.CONTENT_URI, values)
        Log.d(TAG, "新しい行 id (in uri) は $uri")
        Log.d(TAG, "id (in uri) は ${TasksContract.getId(uri!!)}")
    }
}