package org.isoron.uhabits.activities

import org.isoron.androidbase.AndroidUserEmailFinder
import org.isoron.uhabits.core.ui.screens.habits.list.ListHabitsBehavior
import org.isoron.uhabits.core.ui.screens.habits.show.ShowHabitMenuBehavior
import javax.inject.*

class UsernameFinder @Inject
constructor(
        private val androidUsernameFinder: AndroidUserEmailFinder
) : ListHabitsBehavior.UsernameFinder {

    override fun getUserEmail(): String {
        return androidUsernameFinder.getUserEmail()!!
    }
}
