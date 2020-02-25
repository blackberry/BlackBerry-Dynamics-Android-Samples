/* Copyright (c) 2019-2020 BlackBerry Limited
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

package blackberry.example.com.gettingstartedbd

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.good.gd.*

class MainActivity : AppCompatActivity(), GDStateListener, GDAppEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GDAndroid.getInstance().activityInit(this)
        setContentView(R.layout.activity_main)

        // Authorize for GDAppEventListener
        GDAndroid.getInstance().authorize(this)

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
     * Called on all GDAppEvent occurrences. However, only PolicyUpdate needs
     * to be handled for this application.
     */
    override fun onGDEvent(anEvent: GDAppEvent) {
        val eventType: GDAppEventType = anEvent.eventType

        if (eventType == GDAppEventType.GDAppEventPolicyUpdate) {
            // Notify the user and update the policy
            Toast.makeText(this, "Policy update", Toast.LENGTH_SHORT).show()
            PolicyFragment.newInstance().updatePolicy()
        }
    }

    override fun onLocked() {}

    override fun onWiped() {}

    override fun onUpdateConfig(p0: MutableMap<String, Any>?) {}

    override fun onUpdateServices() {}

    override fun onAuthorized() {}

    override fun onUpdateEntitlements() {}

    override fun onUpdatePolicy(p0: MutableMap<String, Any>?) {}

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
                4 -> PolicyFragment.newInstance()
                else -> null
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
                4 -> "GD - Policy"
                else -> null
            }
        }
    }
}
