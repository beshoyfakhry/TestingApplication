package com.beshoy.testingapplication.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.beshoy.testingapplication.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/*
* Small test  as we say that this will be a  unit test
* Medium test as we say that this will be a  integrated test
* Large test UI test
* */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ShoppingDaoTest {

    /*
    this line to tell the Junit to run the methods line by line
    * */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var database: ShoppingItemDatabase
    lateinit var dao: ShoppingDao


    /*
    * Before annotation is used to force the function "setup" to run before each test case
    * */
    @Before
    fun setup() {
        /*
        * -inMemoryDatabaseBuilder means that this will bs a database in ROM ,, just for the testing
        * -Why we use "allowMainThreadQueries" , because in our testing we don`t want to use threads , as this may not work as expected as threads behaviour is not predicted , so we will run all this on the main thread, to make sure of the execution behaviour.
        * */
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.shoppingDao()
    }

    /*
    * After annotation is used to force the function "tearDown" to run after each test case
    * */

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertShoppingItem() =
        /*
        * we used this to force the coroutine to run on the main thread*/
        runBlockingTest {
            val shoppingItem = ShoppingItem("name", 1, 2f, "Image url", 0)

            dao.insertShoppingItem(shoppingItem)
            /***
             *  getOrAwaitValue  method will wait till liveData return the value from the background thread
             * */
            val itemsList = dao.observeAllShoppingItems().getOrAwaitValue()

            Truth.assertThat(itemsList.contains(shoppingItem))
        }

    @Test
    fun deleteShoppingItem() =
        runBlockingTest {

            val shoppingItem = ShoppingItem("name", 1, 2f, "Image url", 0)
            dao.insertShoppingItem(shoppingItem)

            val lastShoppingItemAdded = dao.observeAllShoppingItems().getOrAwaitValue().last()
            dao.deleteShoppingItem(lastShoppingItemAdded)

            Truth.assertThat(
                (dao.observeAllShoppingItems().getOrAwaitValue())
            ).doesNotContain(shoppingItem)
        }


    /*
    * Make sure to change the id of each item added , in order to avoid the replacment of items
    * */
    @Test
    fun totalAmount() {
        runBlockingTest {
            val shoppingItem = ShoppingItem("name", 10, 2.25f, "Image url", 0)
            val shoppingItem2 = ShoppingItem("name", 10, 1f, "Image url", 1)
            dao.insertShoppingItem(shoppingItem)
            dao.insertShoppingItem(shoppingItem2)
            val totalAmount = dao.observeTotalPrice().getOrAwaitValue().toString()

            Truth.assertThat(totalAmount).isEqualTo("32.5")

        }
    }
}