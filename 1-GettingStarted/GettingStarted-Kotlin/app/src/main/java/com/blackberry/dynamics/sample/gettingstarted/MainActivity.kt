/* Copyright (c) 2021 BlackBerry Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.blackberry.dynamics.sample.gettingstarted

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        /**
         * The [android.support.v4.view.PagerAdapter] that will provide
         * fragments for each of the sections. We use a
         * [FragmentPagerAdapter] derivative, which will keep every
         * loaded fragment in memory. If this becomes too memory intensive, it
         * may be best to switch to a
         * [android.support.v4.app.FragmentStatePagerAdapter].
         */

        val mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        //The [ViewPager] that will host the section contents.
        val mViewPager: ViewPager = findViewById(R.id.container) as ViewPager
        mViewPager.setAdapter(mSectionsPagerAdapter)

        val tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        val restrictionsFilter = IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)

        val restrictionsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("MainActivity", "Policy update")
                PolicyFragment.newInstance().updatePolicy()
            }
        }

        registerReceiver(restrictionsReceiver, restrictionsFilter)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a fragment (defined as a static inner class below).

            return when (position) {
                0 -> FileFragment.newInstance()
                1 -> SqlFragment.newInstance()
                2 -> HttpFragment.newInstance()
                3 -> SocketFragment.newInstance()
                4 -> PolicyFragment.newInstance()
                else -> throw RuntimeException()
            }
        }

        override fun getCount(): Int {
            // Show 5 total pages.
            return 5
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Storage - File"
                1 -> "Storage - SQL"
                2 -> "Network - HTTP"
                3 -> "Network - Socket"
                4 -> "App - Policy"
                else -> null
            }
        }
    }
}
