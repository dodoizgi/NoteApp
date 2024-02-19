package com.task.noteapp

import androidx.annotation.UiThread
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.task.noteapp.domain.model.Note
import com.task.noteapp.ui.activity.MainActivity
import com.task.noteapp.ui.dialog.AddNoteDialog
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class AddNoteDialogTest {

    @JvmField
    @Rule
    var activityScenarioRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    private lateinit var addNoteDialog : AddNoteDialog

    @Before
    fun before() {
        val activityScenario = ActivityScenario.launchActivityForResult(MainActivity::class.java)
        val testNote = Note(
            id = 1,
            title = "Modified Test Title",
            description = "Modified Test Description",
            url = "Modified Test Image URL",
            createdDate = "2024-02-03",
            isChanged = false
        )

        activityScenario.onActivity { activity ->
            addNoteDialog = AddNoteDialog(
                activity,
                activity,
                onSaveClicked = {  },
                onUpdateClicked = {  }
            )
            addNoteDialog.show(testNote, isUpdate = true)
        }
    }

    @Test
    @UiThread
    fun testButtonApplyOnClickWhenUpdateIsTrue() {

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withText("Modified Test Title")).check(matches(isDisplayed()))
        onView(withText("Modified Test Description")).check(matches(isDisplayed()))
        onView(withText("Modified Test Image URL")).check(matches(isDisplayed()))

        onView(withId(R.id.button_apply)).perform(click())

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val validationResult = addNoteDialog.validateUIData()
        assert(validationResult)
    }
}