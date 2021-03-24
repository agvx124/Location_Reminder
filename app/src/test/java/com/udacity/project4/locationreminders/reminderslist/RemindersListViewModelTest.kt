package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource
    private lateinit var applicationContext: Application
    private lateinit var remindersList: MutableList<ReminderDTO>


    @Before
    fun setUpViewModel() {
        stopKoin()
        applicationContext = ApplicationProvider.getApplicationContext()
        remindersList = mutableListOf(ReminderDTO("Reminder Title", "Reminder Description","Null Island", 0.0, 0.0))
        dataSource = FakeDataSource(remindersList)
        viewModel = RemindersListViewModel(applicationContext, dataSource)
    }

    @Test
    fun getRemindersList_and_reminderList_not_empty() {
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)

        viewModel.loadReminders()

        val value = viewModel.remindersList.getOrAwaitValue()

        assertThat(value, (not(emptyList())))
        assertThat(value.size, `is`(remindersList.size))
    }

    @Test
    fun check_loading() {
        dataSource = FakeDataSource(mutableListOf())
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)

        mainCoroutineRule.pauseDispatcher()

        viewModel.loadReminders()

        val value = viewModel.showLoading.getOrAwaitValue()

        assertThat(value, `is`(true))
    }

    @Test
    fun shouldReturnError() {
        dataSource = FakeDataSource(null)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)

        viewModel.loadReminders()

        val value = viewModel.showSnackBar.getOrAwaitValue()

        assertThat(value, `is`("No Reminder"))
    }

}