package com.igweze.ebi.todosqlite

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView

class TodosCursorAdapter(private val ctx: Context, cursor: Cursor, autoRequery: Boolean): CursorAdapter(ctx, cursor, autoRequery) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View? {
        return LayoutInflater
                .from(ctx)
                .inflate(R.layout.todo_list_item, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val todoText = view?.findViewById<TextView>(R.id.tvNote)
        val textColumnNullable = cursor?.getColumnIndex(TodoContract.TodosEntry.COLUMN_TEXT)
        val textColumNotNull = textColumnNullable ?: -1

        val text = if (textColumNotNull == -1) "" else cursor?.getString(textColumNotNull)
        todoText?.text = text
    }

}