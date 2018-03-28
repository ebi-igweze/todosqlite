package com.igweze.ebi.todosqlite

import android.app.AlertDialog
import android.app.LoaderManager
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.content.ContentValues
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Loader
import android.database.Cursor
import android.widget.AdapterView
import android.databinding.DataBindingUtil
import android.net.Uri
import com.igweze.ebi.todosqlite.databinding.ActivityCategoryBinding
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_category.*


class CategoryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        const val URL_LOADER = 0
    }

    var cursor: Cursor? = null
    private var category: CategoryModel? = null
    private lateinit var categories: CategoryListModel
    private lateinit var list: ObservableArrayList<CategoryModel>
    private lateinit var adapter: CategoryListAdapter
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var handler: TodosQueryHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflater
        val inflater = layoutInflater
        //adds the custom layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
        loaderManager.initLoader(URL_LOADER, null, this)
        //get the listView and add the onclicklistener
        handler = TodosQueryHandler(contentResolver)

        //EVENTS
        //add the click event to the list, so that the selected item goes to the
        lvCategories.setOnItemClickListener { _, _, position, _ ->
            category = categories.categories[position]
            binding.category = category
        }


        //New button will add a new line on the list
        btnAdd.setOnClickListener {
            category = CategoryModel()
//            categories.categories.add(category);
            binding.category = category
        }


        //delete button will delete an item from the list
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this@CategoryActivity)
                .setTitle(getString(R.string.delete_categories_dialog_title))
                .setMessage(getString(R.string.delete_categories_dialog))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, { _, _ ->
                    categories.categories.remove(category)
                    val categoryId = (category!!.id.get()).toString()
                    val uri = Uri.withAppendedPath(TodoContract.CategoriesEntry.CONTENT_URI, categoryId)
                    val selection = "${TodoContract.CategoriesEntry._ID}=$categoryId"
                    handler.startDelete(1, null, uri, selection, null)
                    category = null
                })
                .setNegativeButton(android.R.string.no, null)
                .show()
        }

        //save button
        btnSave.setOnClickListener {
            if (category != null && category?.id?.get() != null && category?.id?.get() != 0) {
                //update existing category
                val args = arrayOf(category!!.id.get().toString())
                val values = ContentValues()
                values.put(TodoContract.CategoriesEntry.COLUMN_DESCRIPTION, category!!.description.get())
                handler.startUpdate(1, null, TodoContract.CategoriesEntry.CONTENT_URI,
                        values, TodoContract.CategoriesEntry._ID + "=?", args)
            } else if (category != null && category?.id?.get() == null || category?.id?.get() == 0) {
                //add new category
                val values = ContentValues()
                values.put(TodoContract.CategoriesEntry.COLUMN_DESCRIPTION, category!!.description.get())
                handler.startInsert(1, null, TodoContract.CategoriesEntry.CONTENT_URI, values)
            }
        }
    }

    public override fun onResume() {
        loaderManager.restartLoader(URL_LOADER, null, this)
        super.onResume()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(TodoContract.CategoriesEntry.TABLE_NAME
                + "." + TodoContract.CategoriesEntry._ID, TodoContract.CategoriesEntry.COLUMN_DESCRIPTION)
        return CursorLoader(this,
                TodoContract.CategoriesEntry.CONTENT_URI,
                projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        val lv = findViewById<View>(R.id.lvCategories) as ListView
        list = ObservableArrayList()
        var i = 0
        //fills the observablelist of categories
        // Move cursor before first so we can still iterate after config change
        data.moveToPosition(-1)
        while (data.moveToNext()) {
            list.add(i, CategoryModel(data.getInt(0), data.getString(1)))
            i++
        }
        adapter = CategoryListAdapter(list)
        lv.adapter = adapter
        //set bindings
        //classes
        category = CategoryModel()
        categories = CategoryListModel(list)
        binding.categoryList = categories
        binding.category = category
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.list = null
    }
}