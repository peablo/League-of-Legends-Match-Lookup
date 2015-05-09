package com.qml.matchlookup;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.qml.matchlookup.constants.Constants;
import com.qml.matchlookup.provider.MySuggestionProvider;
import com.qml.matchlookup.util.Util;
import com.qml.matchlookup.view.CustomSlidingTabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {


    SharedPreferences mPref;

    ViewPager mViewPager;

    private Toolbar mToolbar;

    CustomSlidingTabLayout mSlidingTabLayout;

    TeamFragment mBlueTeamFragment, mRedTeamFragment;

    private MenuItem mSearchButtonItem, mSettingsItem, mRefreshItem, mDelHistoryItem;

    private LinearLayout mSearchParent;

    private SearchView mSearchView;
    private Spinner mRegionSpinner;

    public static final String KEY_SEARCH_EXPANDED = "KEY_SEARCH_EXPANDED";
    public static final String KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT";
    public static final String KEY_SEARCH_REGION = "KEY_SEARCH_REGION";
    public static final String KEY_SEARCH_MODE = "KEY_SEARCH_MODE";

    private boolean mSearchExpanded;
    private String mSearchText;
    private int mSearchRegion;

    private boolean mSearchMode;
    private String mSearchSummonerName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if ( !setToolbarNormalMode() )
                {
                    refreshNoSearch();
                }

            }
        });


        // init prefs
        mPref = getSharedPreferences( "com.qml.matchlookup", Context.MODE_PRIVATE );

        // init toolbar view fields
        mSearchParent = (LinearLayout) mToolbar.findViewById(R.id.search_parent);
        mSearchView = (SearchView) mToolbar.findViewById(R.id.summoner_search);
        mRegionSpinner = null;


        // enable search history
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));



        // load tabs mode
        loadTabs();


        // restore savedInstanceState if any
        if( savedInstanceState != null )
        {
             mSearchExpanded = savedInstanceState.getBoolean(KEY_SEARCH_EXPANDED, false);
             mSearchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "");
             mSearchRegion = savedInstanceState.getInt(KEY_SEARCH_REGION, 0);

             mSearchMode = savedInstanceState.getBoolean(KEY_SEARCH_MODE, false);

        }else{ // set default values and load game from server if no savedInstanceState
            mSearchExpanded = false;
            mSearchText = "";
            mSearchRegion = 0;

            loadGameByMode();

        }


    }

    private void refreshNoSearch() {
        mSearchMode = false;
        loadGameByMode();
    }

    private void loadTabs()
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        mBlueTeamFragment = TeamFragment.newInstance(100);
        mRedTeamFragment = TeamFragment.newInstance(200);

        fragments.add(mBlueTeamFragment);
        fragments.add(mRedTeamFragment);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), fragments));

        mSlidingTabLayout = (CustomSlidingTabLayout) findViewById(R.id.slidingTabs);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    private void loadGameByMode()
    {
        Intent intent = getIntent();

        if( mSearchMode ) // search button
        {

            String summ = mSearchSummonerName;
            showGame( summ );
            setBackButtonEnabled( true );

        } else {

            setBackButtonEnabled( false );

            if ( mPref.getBoolean(Constants.FIRST_TIME, true) ) { // first time


                Intent i = new Intent(this, SettingsActivity.class);

                startActivityForResult(i, 1);

            } else {                                            // normal usage

                String summ = mPref.getString(Constants.SUMM_NAME, "");
                showGame( summ );

            }
        }
    }

    private void showGame(String summoner)
    {
        if( summoner.equals("") )
        {
            noSummName();
        }else
        {

            setTitle( summoner );
        }

    }

    private void noSummName()
    {

        setTitle(getResources().getString(R.string.app_name));

    }





    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1)
        {

            if(resultCode == RESULT_OK)
            {
                mPref.edit().putBoolean(Constants.FIRST_TIME, false).apply();

                String result = data.getStringExtra("result");
                mPref.edit().putString(Constants.SUMM_NAME, result).apply();

                refreshNoSearch();

            }else {


                if (mPref.getBoolean(Constants.FIRST_TIME, true))
                {
                    mPref.edit().putBoolean(Constants.FIRST_TIME, false).apply();

                    refreshNoSearch();
                }
            }



        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        mSearchButtonItem = menu.findItem(R.id.action_search);
        mSettingsItem = menu.findItem(R.id.action_settings);
        mRefreshItem = menu.findItem(R.id.action_refresh);
        mDelHistoryItem = menu.findItem(R.id.action_delete_history);



//        mRegionsItem = menu.findItem(R.id.regions_spinner_item);
//
//        SearchView searchView = null;
//
//        Spinner s = null;
//
//        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
//
//        if (mSearchButtonItem != null) {
//            searchView = (SearchView) mSearchButtonItem.getActionView();
//        }
//        if (searchView != null) {
//
//
//            searchView.setOnSearchClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    mRegionsItem.setVisible(true);
//                }
//            });
//
//            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//
//                @Override
//                public boolean onClose()
//                {
//                    mRegionsItem.setVisible(false);
//                    return false;
//                }
//            });
//
//            //restoring searchview
//
//            searchView.setIconified( !mSearchExpanded );
//
//            searchView.setQuery(
//                    mSearchText,
//                    false
//            );
//        }
//
//        // restoring region spinner
//
//        if (mRegionsItem != null) {
//            s = ((Spinner) mRegionsItem.getActionView());
//        }
//
//        if (s != null) {
//            s.setSelection(
//                    mSearchRegion,
//                    false
//            );
//        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setToolbarSearchMode()
    {
        // clear other content
        if (mSearchButtonItem != null) {
            mSearchButtonItem.setVisible(false);
        }
        if(mSettingsItem != null) {
            mSettingsItem.setVisible(false);
        }
        if (mRefreshItem != null) {
            mRefreshItem.setVisible(false);
        }
        if (mDelHistoryItem != null) {
            mDelHistoryItem.setVisible(false);
        }

        // set back button
        setBackButtonEnabled( true );

        // enable searchView and region Spinner
        mSearchParent.setVisibility(View.VISIBLE);

        // focus textfield
        mSearchView.requestFocus();


        // spinner

        final ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(
                getSupportActionBar().getThemedContext(),
                R.array.regions,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(R.layout.custom_simple_spinner_dropdown_item);

        mRegionSpinner = new Spinner(getSupportActionBar().getThemedContext());
        mRegionSpinner.setAdapter(spinnerAdapter);

        mSearchParent.addView(mRegionSpinner);


        // remove mag icon
        ImageView magIcon = (ImageView) mSearchView.findViewById(R.id.search_mag_icon);
        if (magIcon != null) {
            magIcon.setVisibility(View.GONE);
        }

        // adjust edit text colors programmatically for api < 21
        EditText editText = (EditText) mSearchView.findViewById(R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.abc_primary_text_disable_only_material_dark));
        editText.setHintTextColor(getResources().getColor(R.color.hint_foreground_material_dark));

        // remove bottom focus bar
        View searchPlateView = mSearchView.findViewById(R.id.search_plate);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }


    }

    private void setBackButtonEnabled(boolean enable) {
        if(enable){
            mToolbar.setNavigationIcon(R.drawable.arrowback_drawable);
        }else{
            mToolbar.setNavigationIcon(null);
        }

    }


    private boolean setToolbarNormalMode()
    {
        // return false if the toolbar was in normal mode alredy
        if( mSearchParent.getVisibility() == View.GONE )
        {
            return false;
        }

        // remove spinner view
        if( mRegionSpinner != null )
        {
            mSearchParent.removeView(mRegionSpinner);
        }

        // hide search layout
        mSearchParent.setVisibility(View.GONE);

        // restore other content
        if (mSearchButtonItem != null) {
            mSearchButtonItem.setVisible(true);
        }
        if(mSettingsItem != null) {
            mSettingsItem.setVisible(true);
        }
        if (mRefreshItem != null) {
            mRefreshItem.setVisible(true);
        }
        if (mDelHistoryItem != null){
            mDelHistoryItem.setVisible(true);
        }

        // remove back button
        if ( !mSearchMode ) {
            setBackButtonEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.action_settings:

                Intent i = new Intent(this, SettingsActivity.class);

                i.putExtra(Constants.CONFIG_BUTTON,true);

                startActivityForResult(i, 1);

                return true;
            case R.id.action_search:

                setToolbarSearchMode();

                return true;
            case R.id.action_delete_history:

                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                        this,
                        MySuggestionProvider.AUTHORITY,
                        MySuggestionProvider.MODE
                );
                suggestions.clearHistory();

                Toast.makeText(
                        this,
                        getResources().getString(R.string.historyDeleted),
                        Toast.LENGTH_SHORT
                ).show();

                return true;

            case R.id.action_refresh:

                loadGameByMode();

                return true;
        }

        return super.onOptionsItemSelected(item);

    }



    @Override
    public void startActivity(Intent intent)
    {

        if( intent.getAction().equalsIgnoreCase(Intent.ACTION_SEARCH) ) // intercept search intent from SearchView
        {

            if( !Util.validName( intent.getStringExtra(SearchManager.QUERY) ) ) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.invalidNameWarning), Toast.LENGTH_SHORT).show();
            }else {

                searchSummoner( intent.getStringExtra(SearchManager.QUERY) );

            }
        }
        else
        {


            super.startActivity(intent);
        }
    }

    private void searchSummoner(String summName)
    {
        this.mSearchSummonerName = summName;
        mSearchMode = true;


        // save to history
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.saveRecentQuery(summName, null);

        setToolbarNormalMode();

        loadGameByMode();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

        super.onSaveInstanceState(outState);

//        if(mSearchButtonItem != null)
//        {
//            SearchView sv = (SearchView) mSearchButtonItem.getActionView();
//            Spinner sp = (Spinner)mRegionsItem.getActionView();
//
//            if ( !sv.isIconified() )
//            {
//                outState.putBoolean(KEY_SEARCH_EXPANDED, true);
//                outState.putString( KEY_SEARCH_TEXT, sv.getQuery().toString() );
//
//                outState.putInt(KEY_SEARCH_REGION, sp.getSelectedItemPosition());
//            }else{
//                outState.putBoolean(KEY_SEARCH_EXPANDED, false);
//            }
//        }


    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> list;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> list)
        {
            super(fm);
            this.list = list;
        }

        Fragment currentFragment = null;

        public Fragment getCurrentFragment() {
            return currentFragment;
        }

        @Override
        public Fragment getItem(int position) {
            currentFragment = list.get(position);
            return currentFragment;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {

                case 0:
                    return getResources().getString(R.string.blueteam);
                case 1:
                    return getResources().getString(R.string.redteam);

            }
            return null;
        }
    }

}








