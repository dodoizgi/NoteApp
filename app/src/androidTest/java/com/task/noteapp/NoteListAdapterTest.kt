package com.task.noteapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.task.noteapp.domain.model.Note
import com.task.noteapp.ui.activity.MainActivity
import com.task.noteapp.ui.adapter.NoteListAdapter
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteListAdapterTest {

    @JvmField
    @Rule
    var activityScenarioRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    private lateinit var noteListAdapter: NoteListAdapter
    private val testNoteList = listOf(
        Note(
            id = 1,
            title = "Başlık",
            description = "Notun açıklaması",
            url = "https://www.example.com",
            createdDate = "2024-02-04",
            isChanged = false
        ),
        Note(
            id = 2,
            title = "Başldsadsaık",
            description = "Notun açıklaması",
            url = "https://www.example.com",
            createdDate = "2024-02-04",
            isChanged = false
        ),
        Note(
            id = 4,
            title = "Başlıdasadsdaaaaaak",
            description = "Notun açıklaması",
            url = "https://www.example.com",
            createdDate = "2024-02-04",
            isChanged = false
        )
    )

    @Before
    fun setUp() {
        noteListAdapter = NoteListAdapter()
    }

    @Test
    fun testRecyclerViewItemClick() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val recyclerView = RecyclerView(context)
        recyclerView.adapter = noteListAdapter
        noteListAdapter.submitList(testNoteList)

        Espresso.onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<NoteListAdapter.NoteListViewHolder>(
                    0,
                    click()
                )
            )

    }

    @Test
    fun testRecyclerViewItemDeleteClick() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val recyclerView = RecyclerView(context)
        recyclerView.adapter = noteListAdapter
        noteListAdapter.submitList(testNoteList)

        fun clickOnViewChild() = object : ViewAction {
            override fun getConstraints() = null

            override fun getDescription() = "Click on a child view with specified id."

            override fun perform(uiController: UiController, view: View) =
                click().perform(uiController, view.findViewById(R.id.ivDelete))
        }


        Espresso.onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<NoteListAdapter.NoteListViewHolder>(
                    0,
                    clickOnViewChild()
                )
            )

        assertEquals((testNoteList.size - 1), noteListAdapter.itemCount)
    }
}