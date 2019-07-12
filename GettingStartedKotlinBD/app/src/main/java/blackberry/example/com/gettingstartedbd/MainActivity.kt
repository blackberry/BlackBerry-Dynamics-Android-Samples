package blackberry.example.com.gettingstartedbd

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.good.gd.GDAndroid
import com.good.gd.GDStateListener

class MainActivity : AppCompatActivity(), GDStateListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GDAndroid.getInstance().activityInit(this)
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
