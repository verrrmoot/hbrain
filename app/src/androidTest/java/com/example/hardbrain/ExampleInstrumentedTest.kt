package com.example.hardbrain

import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.*
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class AppFunctionalTest {

    @get:Rule
    val mainActivityRule = ActivityScenarioRule(MainActivity::class.java)


    @get:Rule
    val shareCollectionActivityRule = ActivityScenarioRule(ShareCollectionActivity::class.java)

    @Test
    fun testAppFunctionality() {
        // Проверка отображения MainActivity
        Espresso.onView(ViewMatchers.withId(R.id.profile))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка перехода из MainActivity в CollectionActivity
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_collections))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка нажатия на кнопку "Назад" в CollectionActivity
        Espresso.onView(ViewMatchers.withId(R.id.back))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.profile))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка перехода из MainActivity в ShareCollectionActivity
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_to_share))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recycler_share))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка нажатия на кнопку "Назад" в ShareCollectionActivity
        Espresso.onView(ViewMatchers.withId(R.id.back))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.profile))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка перехода из MainActivity в CollectionActivity
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_collections))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Выбор коллекции
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))

        // Проверка перехода к просмотру карточек
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_card))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка нажатия на кнопку "Назад" в CardViewActivity
        Espresso.onView(ViewMatchers.withId(R.id.back))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Проверка нажатия на кнопку "Назад" в CardActivity
        Espresso.onView(ViewMatchers.withId(R.id.back))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.profile))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }
}
