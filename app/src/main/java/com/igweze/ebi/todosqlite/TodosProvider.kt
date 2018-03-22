package com.igweze.ebi.todosqlite

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.igweze.ebi.todosqlite.TodoContract.Companion.CONTENT_AUTHORITY
import com.igweze.ebi.todosqlite.TodoContract.Companion.PATH_CATEGORIES
import com.igweze.ebi.todosqlite.TodoContract.Companion.PATH_TODOS
import android.util.Log
import com.igweze.ebi.todosqlite.TodoContract.CategoriesEntry
import com.igweze.ebi.todosqlite.TodoContract.TodosEntry



@Suppress("NAME_SHADOWING")
class TodosProvider: ContentProvider() {

    companion object {
        const val TODOS = 1
        const val TODOS_ID = 2
        const val CATEGORIES = 3
        const val CATEGORIES_ID = 4

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    private lateinit var helper: DBHelper

    init {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODOS, TODOS)
        uriMatcher.addURI(CONTENT_AUTHORITY, "$PATH_TODOS/#", TODOS_ID)
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES, CATEGORIES)
        uriMatcher.addURI(CONTENT_AUTHORITY, "$PATH_CATEGORIES/#", CATEGORIES_ID)
    }

    override fun onCreate(): Boolean {
        helper = DBHelper(context)
        return false
    }

    override fun getType(uri: Uri?): String? = null

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, orderBy: String?): Cursor {
        val matchCode = uriMatcher.match(uri)
        val cursor: Cursor
        val joinQuery = ("""
                """ + TodoContract.TodosEntry.TABLE_NAME + """ inner join
                """ + TodoContract.CategoriesEntry.TABLE_NAME + """ on
                """ + TodoContract.TodosEntry.COLUMN_CATEGORY_ID + """ =
                """ + TodoContract.CategoriesEntry.TABLE_NAME + """.""" + TodoContract.CategoriesEntry._ID + """
            """).trimIndent()
        when (matchCode) {
            TODOS -> {
                val queryBuilder = SQLiteQueryBuilder()
                queryBuilder.tables = joinQuery
                cursor = queryBuilder.query(helper.readableDatabase,
                        projection, selection, selectionArgs, null, null, orderBy)
            }
            TODOS_ID -> {
                val queryBuilder = SQLiteQueryBuilder()
                queryBuilder.tables = joinQuery
                val selection = TodoContract.CategoriesEntry._ID + "=?"
                val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = queryBuilder.query(helper.readableDatabase,
                        projection, selection, selectionArgs, null, null, orderBy)
            }
            CATEGORIES -> {
                cursor = helper.readableDatabase.query(
                        TodoContract.CategoriesEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, orderBy)
            }
            CATEGORIES_ID -> {
                val selection = TodoContract.CategoriesEntry._ID + "= ?"
                val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = helper.readableDatabase.query(
                        TodoContract.CategoriesEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, orderBy)
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val match = uriMatcher.match(uri)
        return when (match) {
            TODOS -> insertRecord(uri, contentValues, TodosEntry.TABLE_NAME)
            CATEGORIES -> insertRecord(uri, contentValues, CategoriesEntry.TABLE_NAME)
            else -> throw IllegalArgumentException("Insert unknown URI: " + uri)
        }
    }

    private fun insertRecord(uri: Uri, values: ContentValues?, table: String): Uri? {
        //this time we need a writable database
        val id = helper.writableDatabase.insert(table, null, values)
        if (id == -1L) {
            Log.e("Error", "insert error for URI " + uri)
            return null
        }
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            TODOS -> deleteRecord(uri, null, null, TodosEntry.TABLE_NAME)
            TODOS_ID -> deleteRecord(uri, selection, selectionArgs, TodosEntry.TABLE_NAME)
            CATEGORIES -> deleteRecord(uri, null, null, CategoriesEntry.TABLE_NAME)
            CATEGORIES_ID -> {
                val id = ContentUris.parseId(uri)
                val selection = CategoriesEntry._ID + "=?"
                val sel = arrayOf(id.toString())
                return deleteRecord(uri, selection, sel,
                        CategoriesEntry.TABLE_NAME)
            }

            else -> throw IllegalArgumentException("Insert unknown URI: " + uri)
        }
    }

    private fun deleteRecord(uri: Uri, selection: String?, selectionArgs: Array<String>?, tableName: String): Int {
        //this time we need a writable database
        val db = helper.writableDatabase
        val id = db.delete(tableName, selection, selectionArgs)
        if (id == -1) {
            Log.e("Error", "delete unknown URI " + uri)
            return -1
        }
        context?.contentResolver?.notifyChange(uri, null)
        return id
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            TODOS -> updateRecord(uri, values, selection, selectionArgs, TodosEntry.TABLE_NAME)
            CATEGORIES -> updateRecord(uri, values, selection, selectionArgs, CategoriesEntry.TABLE_NAME)
            else -> throw IllegalArgumentException("Update unknown URI: " + uri)
        }
    }

    private fun updateRecord(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?, tableName: String): Int {
        //this time we need a writable database
        val db = helper.writableDatabase
        val id = db.update(tableName, values, selection, selectionArgs)
        if (id == 0) {
            Log.e("Error", "update error for URI " + uri)
            return -1
        }
        return id
    }
}