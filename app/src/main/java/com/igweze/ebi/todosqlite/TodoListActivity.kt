package com.igweze.ebi.todosqlite

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class TodoListActivity : AppCompatActivity() {

    private val itemNames = listOf(
        "Get theatre tickets",
        "Order pizza for tonight",
        "Running session at 19.30",
        "Call Uncle Sam"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        lvTodos.adapter = ArrayAdapter(this,  R.layout.todo_list_item, R.id.tvNote, itemNames)

        // add click listener for listView
        lvTodos.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, TodoActivity::class.java)
            val content = lvTodos.getItemAtPosition(position)
            intent.putExtra(TodoActivity.CONTENT_KEY, content.toString())
            startActivity(intent)
        }

        // create instance of dbHelper class
//        val helper = DBHelper(this)
//        val db: SQLiteDatabase = helper.readableDatabase

        // add item to database
//        createTodo()
        // query database
//        getTodosInCategory(1)

        // update database
        updateTodo()

        // delete todo in database
        deleteTodo()

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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createTodo() {
        val helper = DBHelper(this)
        val text = "'Go to the gym'"
        val categoryId = 1
        val created = "'2018-11-10'"
        val expired = "''"
        val done = "0"
        val insertQuery = """
            INSERT INTO ${TodoContract.TodosEntry.TABLE_NAME} (
                ${TodoContract.TodosEntry.COLUMN_TEXT},
                ${TodoContract.TodosEntry.COLUMN_CATEGORY_ID},
                ${TodoContract.TodosEntry.COLUMN_CREATED},
                ${TodoContract.TodosEntry.COLUMN_EXPIRED},
                ${TodoContract.TodosEntry.COLUMN_DONE}
            )
            VALUES ($text, $categoryId, $created, $expired, $done)
        """.trimIndent()

        // execute insert
        helper.readableDatabase.execSQL(insertQuery)

        val data = Todo(id=0, categoryId = categoryId, text = text, created = created, expired = expired, done = false)
        val values = getContentValues(data)
        val todoId = helper.readableDatabase.insert(TodoContract.TodosEntry.TABLE_NAME, null, values)
    }

    private fun getContentValues(todo: Todo): ContentValues {
        val values = ContentValues()

        val done = if (todo.done) 1 else 0
        values.put(TodoContract.TodosEntry.COLUMN_TEXT, todo.text)
        values.put(TodoContract.TodosEntry.COLUMN_CREATED, todo.created)
        values.put(TodoContract.TodosEntry.COLUMN_EXPIRED, todo.expired)
        values.put(TodoContract.TodosEntry.COLUMN_DONE, done)
        values.put(TodoContract.TodosEntry.COLUMN_CATEGORY_ID, todo.categoryId)

        return values
    }

    private fun getTodosInCategory(categoryId: Int) {
        val helper = DBHelper(this)

        val projection = arrayOf(
            TodoContract.TodosEntry.COLUMN_TEXT,
            TodoContract.TodosEntry.COLUMN_CREATED,
            TodoContract.TodosEntry.COLUMN_EXPIRED,
            TodoContract.TodosEntry.COLUMN_DONE,
            TodoContract.TodosEntry.COLUMN_CATEGORY_ID)

        val selection = "${TodoContract.TodosEntry.COLUMN_CATEGORY_ID} = ?"
        val selectionArgs = arrayOf("$categoryId")
        val cursor = helper.readableDatabase.query(TodoContract.TodosEntry.TABLE_NAME,
                projection, selection, selectionArgs, null,  null, null)

        Log.d("Record Count", (cursor.count.toString()))

        var rowContent = ""
        while (cursor.moveToNext()) {
            for (i in 0..4) {
                rowContent += "${cursor.getString(i)} - "
            }
            Log.d("Row Content", rowContent)
        }
        cursor.close()
    }

    private fun updateTodo() {
        val id = 1
        val helper = DBHelper(this)
        val args = arrayOf("$id")
        val values = ContentValues()
        val whereClause = TodoContract.TodosEntry._ID + " = ?"
        values.put(TodoContract.TodosEntry.COLUMN_TEXT, "Call Mr Bond")
        val numRows = helper.readableDatabase.update(TodoContract.TodosEntry.TABLE_NAME,
                values, whereClause, args)
        Log.d("Update Rows", numRows.toString())
        helper.readableDatabase.close()
    }

    private fun deleteTodo() {
        val id = 1
        val helper = DBHelper(this)
        val args = arrayOf("$id")
        val whereClause = TodoContract.TodosEntry._ID + " = ?"
        val numRows = helper.readableDatabase.delete(TodoContract.TodosEntry.TABLE_NAME,
                whereClause, args)
        Log.d("Delete rows", numRows.toString())
    }
}
