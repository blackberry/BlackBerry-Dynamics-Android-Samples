/* Copyright (c) 2019 BlackBerry Ltd.
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

package blackberry.example.com.gettingstarted

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

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
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            // getItem is called to instantiate the fragment for the given page.
            // Return a fragment (defined as a static inner class below).

            return when (position) {
                0 -> FileFragment.newInstance()
                1 -> SqlFragment.newInstance()
                2 -> HttpFragment.newInstance()
                3 -> SocketFragment.newInstance()
                else -> null
            }
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Storage - File"
                1 -> "Storage - SQL"
                2 -> "Network - HTTP"
                3 -> "Network - Socket"
                else -> null
            }
        }
    }
}
