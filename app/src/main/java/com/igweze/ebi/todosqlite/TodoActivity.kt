package com.igweze.ebi.todosqlite

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.igweze.ebi.todosqlite.databinding.ActivityTodoBinding
import kotlinx.android.synthetic.main.activity_main.*

class TodoActivity : AppCompatActivity() {
    companion object {
        const val CONTENT_KEY = "Content"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_todo)

        val content = intent.getSerializableExtra(CONTENT_KEY) as TodoModel
        val binding = DataBindingUtil.setContentView<ActivityTodoBinding>(this, R.layout.activity_todo)
        binding.todo = content

    }
}
