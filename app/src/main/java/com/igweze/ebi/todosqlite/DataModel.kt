package com.igweze.ebi.todosqlite

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import java.io.Serializable

class CategoryModel(i: Int = 0, d: String = ""): Serializable {

    val id = ObservableInt()
    val description = ObservableField<String>()

    init {
        id.set(i)
        description.set(d)
    }

}

class TodoModel(id:Int, text: String, done: Boolean, created: String,
                expired: String, category: Int) : Serializable {

    val id = ObservableInt()
    val text = ObservableField<String>()
    val done = ObservableBoolean()
    val created = ObservableField<String>()
    val expired = ObservableField<String>()
    val categoryId = ObservableInt()

    init {
        this.id.set(id)
        this.text.set(text)
        this.done.set(done)
        this.created.set(created)
        this.expired.set(expired)
        this.categoryId.set(category)
    }
}

class CategoryListModel(val categories: ObservableArrayList<CategoryModel> = ObservableArrayList()): Serializable
