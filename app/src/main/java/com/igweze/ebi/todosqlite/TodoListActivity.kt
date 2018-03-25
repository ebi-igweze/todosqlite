package com.igweze.ebi.todosqlite

import android.app.LoaderManager
import android.content.ContentValues
import android.content.Intent
import android.content.Loader
import android.content.CursorLoader
import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class TodoListActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        const val URL_LOADER = 0
        const val ALL_RECORDS = -1
    }

    private lateinit var cursor: Cursor
    private lateinit var todoAdapter: TodosCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // initialize loader
        loaderManager.initLoader(URL_LOADER, null, this)
//        // add click listener for listView
//        lvTodos.setOnItemClickListener { _, _, position, _ ->
//            val intent = Intent(this, TodoActivity::class.java)
//            val content = lvTodos.getItemAtPosition(position)
//            intent.putExtra(TodoActivity.CONTENT_KEY, content.toString())
//            startActivity(intent)
//        }
        cursor = getTodosCursor()
        todoAdapter = TodosCursorAdapter(this, cursor, false)
        lvTodos.adapter = todoAdapter

        fab.setOnClickListener { view ->
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
            R.id.action_categories -> {
                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
                super.onOptionsItemSelected(item)
            }
            R.id.action_delete_all_todos -> {
                deleteTodo(ALL_RECORDS)
                super.onOptionsItemSelected(item)
            }
            R.id.action_create_test_todos -> {
                createTestTodos()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getTodosCursor(): Cursor {
        val projection = arrayOf(
                TodoContract.TodosEntry.TABLE_NAME + "." + TodoContract.TodosEntry._ID,
                TodoContract.TodosEntry.COLUMN_TEXT,
                TodoContract.TodosEntry.COLUMN_CREATED,
                TodoContract.TodosEntry.COLUMN_EXPIRED,
                TodoContract.TodosEntry.COLUMN_DONE,
                TodoContract.CategoriesEntry.TABLE_NAME + "." +
                        TodoContract.CategoriesEntry.COLUMN_DESCRIPTION)

        return contentResolver!!.query(TodoContract.TodosEntry.CONTENT_URI, projection, null, null, null)
    }

    private fun createCategory() {
        val category = Category(0, "Work")
        val uri = TodoContract.CategoriesEntry.CONTENT_URI
        // contentResolver provides access to app content model,
        // in turn giving access to the app providers
        val resultUri = contentResolver.insert(uri,category.contentValues)
        Log.d("MainActivity", "Category added $resultUri")
    }

    private fun createTestTodos() {
        for (i in 1 until 20) {
            val day = if (i < 10) "0$i" else "$i"
            val date = "2018-03-$day"
            val data = Todo(
                id=0, text="Todo Item $i",
                created = date, expired = "",
                categoryId = 1, done = false )
            val uri = contentResolver.insert(TodoContract.TodosEntry.CONTENT_URI, data.contentValues)
            Log.d("Test Todo Insert", uri.toString())
        }
    }


    private fun updateTodo(id: Int, text: String, expired: String) {
        val args = arrayOf("$id")
        val values = ContentValues()
        val whereClause = TodoContract.TodosEntry._ID + " = ?"
        values.put(TodoContract.TodosEntry.COLUMN_TEXT, "Call Mr Bond")
        val numRows = contentResolver.update(TodoContract.TodosEntry.CONTENT_URI,
                values, whereClause, args)
        Log.d("Update Rows", numRows.toString())
    }

    private fun deleteTodo(id: Int) {
        val args =  if (id == ALL_RECORDS) null else arrayOf("$id")
        val whereClause = TodoContract.TodosEntry._ID + " = ?"
        val numRows = contentResolver.delete(TodoContract.TodosEntry.CONTENT_URI, whereClause, args)
        Log.d("Delete rows", numRows.toString())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
                TodoContract.TodosEntry.TABLE_NAME + "." + TodoContract.TodosEntry._ID,
                TodoContract.TodosEntry.COLUMN_TEXT,
                TodoContract.TodosEntry.COLUMN_CREATED,
                TodoContract.TodosEntry.COLUMN_EXPIRED,
                TodoContract.TodosEntry.COLUMN_DONE,
                TodoContract.CategoriesEntry.TABLE_NAME + "." +
                        TodoContract.CategoriesEntry.COLUMN_DESCRIPTION)

        return CursorLoader(this,
                TodoContract.TodosEntry.CONTENT_URI, projection,
                null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        todoAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        todoAdapter.swapCursor(null)
    }

}
