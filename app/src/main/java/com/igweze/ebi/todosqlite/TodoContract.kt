package com.igweze.ebi.todosqlite

import android.net.Uri
import android.provider.BaseColumns


class TodoContract {
    companion object {
        const val CONTENT_AUTHORITY = "com.igweze.ebi.todos.todosprovider"
        const val PATH_TODOS = "todos"
        const val PATH_CATEGORIES = "categories"
        val BASE_CONTENT_URI: Uri? = Uri.parse("content://$CONTENT_AUTHORITY")
    }


    class TodosEntry: BaseColumns {
        companion object {
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODOS)
            const val TABLE_NAME = "todo"
            const val _ID = BaseColumns._ID
            const val COLUMN_TEXT = "text"
            const val COLUMN_CREATED = "created"
            const val COLUMN_EXPIRED = "expired"
            const val COLUMN_DONE = "done"
            const val COLUMN_CATEGORY_ID = "category_id"
        }
    }

    class CategoriesEntry: BaseColumns {
        companion object {
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES)
            const val TABLE_NAME ="category"
            const val _ID = BaseColumns._ID;
            const val COLUMN_DESCRIPTION = "description"
        }
    }
}