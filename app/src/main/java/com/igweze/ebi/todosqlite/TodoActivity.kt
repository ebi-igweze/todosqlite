package com.igweze.ebi.todosqlite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_todo.*

class TodoActivity : AppCompatActivity() {
    companion object {
        const val CONTENT_KEY = "Content"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        val content = intent.getStringExtra(CONTENT_KEY)
        editTodo.setText(content)
    }
}
