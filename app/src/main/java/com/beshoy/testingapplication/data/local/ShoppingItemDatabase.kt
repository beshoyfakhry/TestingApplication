package com.beshoy.testingapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/*
* Why we did not use the singleton pattern here , because we are using DI
* */
@Database(entities = [ShoppingItem::class], version = 1)

abstract class ShoppingItemDatabase : RoomDatabase() {

    abstract fun shoppingDao(): ShoppingDao
}