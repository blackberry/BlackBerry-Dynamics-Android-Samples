/* Copyright (c) 2021 BlackBerry Limited
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

package com.blackberry.dynamics.sample.gettingstartedbbd;

import android.os.Bundle;
import android.widget.Toast;

import com.good.gd.GDAppEvent;
import com.good.gd.GDAppEventListener;
import com.good.gd.GDAppEventType;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements GDStateListener
{

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onAuthorized()
    {

    }

    @Override
    public void onLocked()
    {

    }

    @Override
    public void onWiped()
    {

    }

    @Override
    public void onUpdateConfig(Map<String, Object> map)
    {

    }

    @Override
    public void onUpdatePolicy(Map<String, Object> map)
    {
        Toast.makeText(this, "Policy update", Toast.LENGTH_SHORT).show();
        PolicyFragment.newInstance().updatePolicy();
    }

    @Override
    public void onUpdateServices()
    {

    }

    @Override
    public void onUpdateEntitlements()
    {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a fragment (defined as a static inner class below).

            switch (position)
            {
                case 0:
                    return FileFragment.newInstance();
                case 1:
                    return SqlFragment.newInstance();
                case 2:
                    return HttpFragment.newInstance();
                case 3:
                    return SocketFragment.newInstance();
                case 4:
                    return PolicyFragment.newInstance();
            }

            return null;

        }

        @Override
        public int getCount()
        {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Storage - File";
                case 1:
                    return "Storage - SQL";
                case 2:
                    return "Network - HTTP";
                case 3:
                    return "Network - Socket";
                case 4:
                    return "BD - Policy";
            }
            return null;
        }
    }
}
