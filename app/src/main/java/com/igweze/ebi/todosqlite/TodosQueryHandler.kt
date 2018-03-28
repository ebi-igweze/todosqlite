package com.igweze.ebi.todosqlite

import android.content.AsyncQueryHandler
import android.content.ContentResolver

open class TodosQueryHandler(contentResolver: ContentResolver) : AsyncQueryHandler(contentResolver)