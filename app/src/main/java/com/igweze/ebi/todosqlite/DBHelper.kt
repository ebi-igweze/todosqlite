package com.igweze.ebi.todosqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Todo(val id: Int, val text: String, val created: String, val expired: String, val done: Boolean = false, val categoryId: Int) {

    val contentValues: ContentValues
    init {
        val values = ContentValues()
        val done = if (this.done) 1 else 0
        values.put(TodoContract.TodosEntry.COLUMN_TEXT, this.text)
        values.put(TodoContract.TodosEntry.COLUMN_CREATED, this.created)
        values.put(TodoContract.TodosEntry.COLUMN_EXPIRED, this.expired)
        values.put(TodoContract.TodosEntry.COLUMN_DONE, done)
        values.put(TodoContract.TodosEntry.COLUMN_CATEGORY_ID, this.categoryId)

        contentValues = values
    }

}

data  class Category(val id: Int, val description: String) {

    val contentValues: ContentValues

    init {
        val values = ContentValues()
        values.put(TodoContract.CategoriesEntry.COLUMN_DESCRIPTION, this.description)
        contentValues = values
    }

}

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "todos.db"
        const val DB_VERSION = 1

        val TABLE_CATEGORIES_CREATE =
            """
                CREATE TABLE ${TodoContract.CategoriesEntry.TABLE_NAME} (
                    ${TodoContract.CategoriesEntry._ID} INTEGER PRIMARY KEY,
                    ${TodoContract.CategoriesEntry.COLUMN_DESCRIPTION} TEXT
                )
            """.trimIndent()

        val TABLE_TODOS_CREATE =
            """
                CREATE TABLE ${TodoContract.TodosEntry.TABLE_NAME} (
                    ${TodoContract.TodosEntry._ID} INTEGER PRIMARY KEY,
                    ${TodoContract.TodosEntry.COLUMN_TEXT} TEXT,
                    ${TodoContract.TodosEntry.COLUMN_CREATED} TEXT default CURRENT_TIMESTAMP,
                    ${TodoContract.TodosEntry.COLUMN_EXPIRED} TEXT,
                    ${TodoContract.TodosEntry.COLUMN_DONE} INTEGER,
                    ${TodoContract.TodosEntry.COLUMN_CATEGORY_ID} INTEGER,
                    FOREIGN KEY (${TodoContract.TodosEntry.COLUMN_CATEGORY_ID})
                    REFERENCES ${TodoContract.CategoriesEntry.TABLE_NAME} (${TodoContract.CategoriesEntry._ID})
                )
            """.trimIndent();
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLE_CATEGORIES_CREATE)
        db?.execSQL(TABLE_TODOS_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${TodoContract.TodosEntry.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${TodoContract.CategoriesEntry.TABLE_NAME}")
        onCreate(db)
    }
}