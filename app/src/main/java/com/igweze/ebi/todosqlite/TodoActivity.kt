package com.igweze.ebi.todosqlite

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.igweze.ebi.todosqlite.databinding.ActivityTodoBinding
import kotlinx.android.synthetic.main.activity_todo.*
import com.igweze.ebi.todosqlite.TodoContract.TodosEntry
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem


class TodoActivity : AppCompatActivity() {
    companion object {
        const val CONTENT_KEY = "Content"
        const val CATEGORIES_KEY = "Categories"
    }

    private lateinit var todo: TodoModel
    private lateinit var categoryList: CategoryListModel
    private var categoryListAdapter: CategoryListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityTodoBinding>(this, R.layout.activity_todo)

        todo = intent.getSerializableExtra(CONTENT_KEY) as TodoModel
        binding.todo = todo

        categoryList = intent.getSerializableExtra(CATEGORIES_KEY) as CategoryListModel
        categoryListAdapter = CategoryListAdapter(categoryList.categories)

        spCategories.adapter = categoryListAdapter
        setSpinnerPosition()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_todo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete_todo -> {
                AlertDialog.Builder(this@TodoActivity)
                    .setTitle(getString(R.string.delete_todo_dialog))
                    .setMessage(getString(R.string.delete_todo_message))
                    .setPositiveButton(android.R.string.yes, { _, _ -> deleteTodo() })
                    .setNegativeButton(android.R.string.no, null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()

        val cat = spCategories.selectedItem as CategoryModel
        val values = ContentValues()
        values.put(TodosEntry.COLUMN_TEXT, todo.text.get())
        values.put(TodosEntry.COLUMN_CATEGORY_ID, cat.id.get())
        values.put(TodosEntry.COLUMN_DONE, todo.done.get())
        values.put(TodosEntry.COLUMN_EXPIRED, todo.expired.get())

        val queryHandler = TodosQueryHandler(contentResolver)
        val todoId = todo.id.get()
        if (todoId != 0)
            queryHandler.startUpdate(1, null, TodosEntry.CONTENT_URI, values,
                    TodosEntry._ID + "=$todoId",null)
        else if (todoId == 0)
            queryHandler.startInsert(1, null, TodosEntry.CONTENT_URI, values)
    }

    private fun setSpinnerPosition() {
        var position = 0
        for (category in categoryList.categories) {
            if (category.id.get() == todo.categoryId.get()) break
            position++
        }
        spCategories.setSelection(position)
    }

    private fun deleteTodo() {
        val todoId = todo.id.get()
        if (todoId != 0) {
            val uri = Uri.withAppendedPath(TodosEntry.CONTENT_URI, todoId.toString())
            val queryHandler = TodosQueryHandler(contentResolver)
            queryHandler.startDelete(1, null, uri,
                    TodosEntry._ID+"=$todoId", null)

            val intent = Intent(this, TodoListActivity::class.java)
            startActivity(intent)
        }
    }
}
