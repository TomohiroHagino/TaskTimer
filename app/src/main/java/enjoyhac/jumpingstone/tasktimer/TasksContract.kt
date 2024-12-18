package enjoyhac.jumpingstone.tasktimer

import android.provider.BaseColumns

object TasksContract {
    internal const val TABLE_NAME = "Tasks"

    object Columns {
        const val ID = BaseColumns._ID
        const val TASK_NAME = "Name"
        const val TASK_DESCRIPTION = "Description"
        const val TASK_SORT_ORDER = "SortOrder"
    }
}