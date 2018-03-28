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
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class TodoListActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        const val URL_LOADER = 0
        const val ALL_RECORDS = -1
        const val ALL_CATEGORIES = -1
    }

    private lateinit var todoAdapter: TodosCursorAdapter
    private val categoryList = CategoryListModel()
    private var categoryListAdapter: CategoryListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // set the categoryList
        setCategories()
        // initialize loader
        loaderManager.initLoader(URL_LOADER, savedInstanceState, this)

        todoAdapter = TodosCursorAdapter(this, null, false)
        lvTodos.adapter = todoAdapter

        // add click listener for list items
        lvTodos.setOnItemClickListener { adapterView, _, position, _ ->
            val intent = Intent(this, TodoActivity::class.java)
            val row = adapterView.getItemAtPosition(position) as Cursor
            val todo = TodoModel(
                id = row.getInt(row.getColumnIndex(TodoContract.TodosEntry._ID)),
                text = row.getString(row.getColumnIndex(TodoContract.TodosEntry.COLUMN_TEXT)),
                done = row.getInt(row.getColumnIndex(TodoContract.TodosEntry.COLUMN_DONE)) == 1,
                expired = row.getString(row.getColumnIndex(TodoContract.TodosEntry.COLUMN_EXPIRED)),
                created = row.getString(row.getColumnIndex(TodoContract.TodosEntry.COLUMN_CREATED)),
                category = row.getInt(row.getColumnIndex(TodoContract.TodosEntry.COLUMN_CATEGORY_ID))
            )
            intent.putExtra(TodoActivity.CONTENT_KEY, todo)
            intent.putExtra(TodoActivity.CATEGORIES_KEY, categoryList)
            startActivity(intent)
        }

        spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Categories Spinner", "Noting selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loaderManager.restartLoader(URL_LOADER, null, this@TodoListActivity)
            }
        }

        fab.setOnClickListener { _ ->
            val todo = TodoModel(
                id = 0,  category = 1,
                done = false, created = "2018-05-05",
                expired = "2018-06-06", text = ""
            )
            val intent = Intent(this, TodoActivity::class.java)
            intent.putExtra(TodoActivity.CONTENT_KEY, todo)
            intent.putExtra(TodoActivity.CATEGORIES_KEY, categoryList)
            startActivity(intent)
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
                deleteTodos(ALL_RECORDS)
                super.onOptionsItemSelected(item)
            }
            R.id.action_create_test_todos -> {
                createTestTodos()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createTestTodos() {
        categoryList.categories.forEach {
            for (i in 1 until 5) {
                val day = if (i < 10) "0$i" else "$i"
                val date = "2018-03-$day"
                val expired = "2018-04-$day"
                val done = (i%2) == 0
                val data = Todo(
                    id=0, text="Todo Item ${it.description.get()} #$i",
                    created = date, expired = expired,
                    categoryId = it.id.get(), done = done )
                val asyncHandler = TodosQueryHandler(contentResolver)
                asyncHandler.startInsert(1, null, TodoContract.TodosEntry.CONTENT_URI, data.contentValues)
            }
        }
    }

    private fun deleteTodos(id: Int) {
        val args =  if (id == ALL_RECORDS) null else arrayOf("$id")
        val whereClause = TodoContract.TodosEntry._ID + " = ?"
        val asyncQueryHandler = TodosQueryHandler(contentResolver)
        asyncQueryHandler.startDelete(2, null,
                TodoContract.TodosEntry.CONTENT_URI, whereClause, args)
    }

    private fun setCategories() {
        val queryHandler = object : TodosQueryHandler(contentResolver) {
            override fun onQueryComplete(token: Int, cookie: Any?,
                                         cursor: Cursor?) {
                try {
                    if (cursor != null) {
                        var i = 0
                        categoryList.categories.add(i, CategoryModel(ALL_CATEGORIES, "All Categories"))
                        i++
                        while (cursor.moveToNext()) {
                            categoryList.categories.add(i, CategoryModel(
                                    cursor.getInt(0),
                                    cursor.getString(1)
                            ))
                            i++
                        }
                    }
                } finally {
                    //cm = null;
                }
            }
        }

        queryHandler.startQuery(1, null, TodoContract.CategoriesEntry.CONTENT_URI,
                null, null, null, TodoContract.CategoriesEntry.COLUMN_DESCRIPTION)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
                TodoContract.TodosEntry.TABLE_NAME + "." + TodoContract.TodosEntry._ID,
                TodoContract.TodosEntry.COLUMN_TEXT,
                TodoContract.TodosEntry.COLUMN_CREATED,
                TodoContract.TodosEntry.COLUMN_EXPIRED,
                TodoContract.TodosEntry.COLUMN_DONE,
                TodoContract.TodosEntry.COLUMN_CATEGORY_ID,
                TodoContract.CategoriesEntry.TABLE_NAME + "." +
                        TodoContract.CategoriesEntry.COLUMN_DESCRIPTION)

        var selection =
                if (spinnerCategories.selectedItemId < 0) null
                else TodoContract.TodosEntry.COLUMN_CATEGORY_ID + "=${spinnerCategories.selectedItemId}"
        return CursorLoader(this,
                TodoContract.TodosEntry.CONTENT_URI, projection,
                selection, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        todoAdapter.swapCursor(data)

        if (categoryListAdapter == null){
            categoryListAdapter = CategoryListAdapter(categoryList.categories)
            spinnerCategories.adapter = categoryListAdapter
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        todoAdapter.swapCursor(null)
    }

}
