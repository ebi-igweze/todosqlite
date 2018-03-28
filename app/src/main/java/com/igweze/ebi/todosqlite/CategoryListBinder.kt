package com.igweze.ebi.todosqlite

import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.widget.ListView

class CategoryListBinder {
    companion object {
        @JvmStatic()
        @BindingAdapter("bind:items")
        fun bindList(listView: ListView, list: ObservableArrayList<CategoryModel>?) {
            val xlist = list ?: ObservableArrayList()
            val adapter = CategoryListAdapter(xlist)
            listView.adapter = adapter
        }
    }

}
