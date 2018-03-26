package com.igweze.ebi.todosqlite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        // enable the back arrow on the top action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
