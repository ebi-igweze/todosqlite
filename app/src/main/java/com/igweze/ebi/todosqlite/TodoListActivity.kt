package com.igweze.ebi.todosqlite

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView

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
}
