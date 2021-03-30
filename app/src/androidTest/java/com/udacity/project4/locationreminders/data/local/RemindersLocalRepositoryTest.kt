package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao
    private lateinit var dto : ReminderDTO


    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.reminderDao()
        repository = RemindersLocalRepository(dao, Dispatchers.Main)
        dto = ReminderDTO("Reminder Title", "Reminder Description","Null Island", 0.0, 0.0)
    }

    @After
    fun closeDB() = database.close()


    @Test
    fun testGetReminders() = runBlockingTest {
        repository.saveReminder(dto)
        val reminderList = repository.getReminders() as Result.Success<List<ReminderDTO>>

        assertEquals(1, reminderList.data.size)
        assertEquals(dto.id, reminderList.data.first().id)
        assertEquals(dto.description, reminderList.data.first().description)
        assertEquals(dto.location, reminderList.data.first().location)
        assertEquals(dto.latitude, reminderList.data.first().latitude)
        assertEquals(dto.longitude, reminderList.data.first().longitude)
    }

    @Test
    fun testSaveReminders() = runBlockingTest {
        repository.saveReminder(dto)
        val reminderList = repository.getReminders() as Result.Success<List<ReminderDTO>>

        assertEquals(1, reminderList.data.size)
        assertEquals(dto.id, reminderList.data.first().id)
        assertEquals(dto.description, reminderList.data.first().description)
        assertEquals(dto.location, reminderList.data.first().location)
        assertEquals(dto.latitude, reminderList.data.first().latitude)
        assertEquals(dto.longitude, reminderList.data.first().longitude)
    }

    @Test
    fun testGetReminder() = runBlockingTest {
        repository.saveReminder(dto)
        val reminder = repository.getReminder(dto.id) as Result.Success<ReminderDTO>

        assertNotNull(reminder)

        assertEquals(dto.id, reminder.data.id)
        assertEquals(dto.description, reminder.data.description)
        assertEquals(dto.location, reminder.data.location)
        assertEquals(dto.latitude, reminder.data.latitude)
        assertEquals(dto.longitude, reminder.data.longitude)
    }

    @Test
    fun testDeleteAllReminders() = runBlockingTest{
        repository.saveReminder(dto)
        repository.deleteAllReminders()

        val reminders = repository.getReminders() as Result.Success<List<ReminderDTO>>

        assertEquals(0, reminders.data.size)
    }

}