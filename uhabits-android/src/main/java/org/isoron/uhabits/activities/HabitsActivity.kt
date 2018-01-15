/*
 * Copyright (C) 2017 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import org.isoron.androidbase.activities.*
import org.isoron.uhabits.*
import org.isoron.uhabits.core.models.*

abstract class HabitsActivity : BaseActivity() {
    lateinit var component: HabitsActivityComponent
    lateinit var appComponent: HabitsApplicationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent = (applicationContext as HabitsApplication).component

        val habit = getHabitFromIntent(appComponent.habitList)
                    ?: appComponent.modelFactory.buildHabit()

        component = DaggerHabitsActivityComponent
                .builder()
                .activityContextModule(ActivityContextModule(this))
                .baseActivityModule(BaseActivityModule(this))
                .habitModule(HabitModule(habit))
                .habitsApplicationComponent(appComponent)
                .build()

        component.themeSwitcher.apply()

        getPermissions();

    }

    private fun getPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
        if (permission != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {

            } else {
                // No explanation needed, we can request the permission.
                val PERMISSIONS_REQUEST_GET_ACCOUNTS = 0
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.GET_ACCOUNTS), PERMISSIONS_REQUEST_GET_ACCOUNTS)
            }
        }
    }

    private fun getHabitFromIntent(habitList: HabitList): Habit? {
        val data = intent.data ?: return null
        val habit = habitList.getById(ContentUris.parseId(data))
                    ?: throw RuntimeException("habit not found")
        return habit
    }
}
