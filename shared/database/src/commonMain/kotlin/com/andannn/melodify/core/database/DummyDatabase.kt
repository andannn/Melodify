package com.andannn.melodify.core.database

import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.SQLiteConnection

fun <T : RoomDatabase> RoomDatabase.Builder<T>.setUpDummyData() =
    apply {
        addCallback(addDummyDataCallback)
    }

private val addDummyDataCallback =
    object : Callback() {
        override fun onCreate(connection: SQLiteConnection) {
        }
    }
