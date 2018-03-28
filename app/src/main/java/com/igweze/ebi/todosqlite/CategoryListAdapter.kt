package com.igweze.ebi.todosqlite

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import com.igweze.ebi.todosqlite.databinding.CategoryListItemBinding

class CategoryListAdapter(var list: ObservableArrayList<CategoryModel>? = ObservableArrayList()) : BaseAdapter() {

    private var inflater: LayoutInflater? = null
    lateinit var position: ObservableInt

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // if inflater is null get inflater from context system service
        inflater = inflater ?: parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding = DataBindingUtil.inflate<CategoryListItemBinding>(inflater, R.layout.category_list_item, parent, false)
        binding.category = list?.get(position)
        return binding.root
    }

    override fun getItem(position: Int): Any? {
        return  list?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return   list!![position].id.get().toLong()
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    fun getPosition(spinner: Spinner): Int {
        return spinner.selectedItemPosition
    }

    fun getPosition(): Int {
        return position.get()
    }

    fun setPosition(position: Int) {
        return this.position.set(position)
    }
}