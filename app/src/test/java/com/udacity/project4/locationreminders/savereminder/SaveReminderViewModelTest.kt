package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource
    private lateinit var applicationContext: Application
    private lateinit var reminder: ReminderDataItem

    @Before
    fun setUpViewModel() {
        stopKoin()
        applicationContext = ApplicationProvider.getApplicationContext()
        reminder = ReminderDataItem("Reminder Title", "Reminder Description","Null Island", 0.0, 0.0)
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(applicationContext, dataSource)
    }

    @Test
    fun check_loading() {
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)

        mainCoroutineRule.pauseDispatcher()

        viewModel.validateAndSaveReminder(reminder)

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun shouldReturnError() {
        dataSource = FakeDataSource(null)
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)

        reminder.title = null
        viewModel.validateAndSaveReminder(reminder)

        val value = viewModel.showSnackBarInt.getOrAwaitValue()

        assertThat(value, `is`(R.string.err_enter_title))
    }

}