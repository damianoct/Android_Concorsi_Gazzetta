package com.distesala.android_concorsi_gazzetta.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.ui.fragment.BaseFragment;
import com.distesala.android_concorsi_gazzetta.ui.fragment.ContestListFragment;
import com.distesala.android_concorsi_gazzetta.ui.fragment.FragmentListener;
import com.distesala.android_concorsi_gazzetta.ui.fragment.GazzetteListFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HomeActivity extends AppCompatActivity implements FragmentListener,
                                                                NavigationView.OnNavigationItemSelectedListener
{
    public static final String HOME_FRAGMENT = String.valueOf(R.id.gazzette);
    private static final String SAVED_FRAGMENT = "currentFragment";

    private static final String INIT_TRANSACTION = "INIT";
    private static final String DRAWER_TRANSACTION = "DRAWER";

    public static final String SEGUE_TRANSACTION = "SEGUE";

    private FrameLayout content;
    private LinearLayout adLayout;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private AppBarLayout appBarLayout;
    private NavigationView navigationView;

    //nelle support library 23 è presente la funzione setExpanded()
    //nelle v22 si è costretti a dichiarare questo metodo
    private void expandAppBarLayout()
    {
        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior != null)
            behavior.onNestedFling(rootLayout, appBarLayout, null, 0, -10000, true);
    }

    private void setFragment(int tag)
    {
        //pop dallo stack delle segue transaction
        getSupportFragmentManager().popBackStackImmediate(SEGUE_TRANSACTION, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //first drawer transaction?
        String backStackTag = getSupportFragmentManager().getBackStackEntryCount() == 0 ? INIT_TRANSACTION : DRAWER_TRANSACTION;

        //check for existing fragment
        Fragment fragmentToAdd = getSupportFragmentManager().findFragmentByTag(String.valueOf(tag));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentToAdd == null)
            fragmentToAdd = createFragmentForTag(tag);

        //TODO quando clicco su impostazioni devo comunicarlo al fragment in cima allo stack.

        if(fragmentToAdd instanceof BaseFragment)
            ((BaseFragment) fragmentToAdd).onDrawerTransaction();

        transaction.addToBackStack(backStackTag).replace(R.id.content_frame, fragmentToAdd, String.valueOf(tag)).commit();
    }

    private Fragment createFragmentForTag(int tag)
    {
        switch (tag)
        {
            case R.id.gazzette:
                return GazzetteListFragment.newInstance(new Bundle());
            case R.id.concorsi:
                return ContestListFragment.newInstance(new Bundle());
            default:
                return null;
        }
    }

    private void initNavigationDrawer()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            //se la tastiera è aperta e apro il drawer, devo nasconderla.
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);


        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //preservare il colore originale delle icone del navigation view.
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void enableBackButton()
    {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //setupAdAtBottom();

        //solo al PRIMO avvio dell'app setto le preferences di default.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        initNavigationDrawer();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        if (settings.getBoolean(getString(R.string.first_launch), true))
        {
            startActivity(new Intent(this, IntroActivity.class));
            //settings.edit().putBoolean(getString(R.string.first_launch), false).apply();
        }

        /* restore state if needed */

        if (savedInstanceState != null) //restore fragment, non aggiungo al backstack.
        {
            Fragment savedFragment = getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, savedFragment, savedFragment.getTag()).commit();
        }
        else //default fragment, prima transazione con HOME FRAGMENT
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, GazzetteListFragment.newInstance(new Bundle())
                    , HOME_FRAGMENT).commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        adLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.ad_layout, null);

        AdView mAdView = (AdView) adLayout.findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded()
            {
                super.onAdLoaded();
                setupAdAtBottom();
            }

            @Override
            public void onAdLeftApplication()
            {
                super.onAdLeftApplication();
                removeAd();
            }

            @Override
            public void onAdFailedToLoad(int i)
            {
                super.onAdFailedToLoad(i);
                removeAd();
            }

            @Override
            public void onAdClosed()
            {
                super.onAdClosed();
                removeAd();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    @Override
    public void onBackPressed()
    {
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
        if (homeFragment != null && homeFragment.isVisible()) //se sono nella home, esci.
            finish();

        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            //prendo il primo elemento dello stack
            String headOfStack = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

            switch (headOfStack)
            {
                case SEGUE_TRANSACTION: //inner transaction
                    super.onBackPressed();
                    break;
                case DRAWER_TRANSACTION: //nav drawer transaction
                    getSupportFragmentManager().popBackStackImmediate(INIT_TRANSACTION, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    break;
                case INIT_TRANSACTION: //first nav drawer transaction
                    getSupportFragmentManager().popBackStack();
                    break;
                default:
                    super.onBackPressed();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //salvo lo stato
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        getSupportFragmentManager().putFragment(outState, SAVED_FRAGMENT, currentFragment);
    }

    @Override
    public void onSegueTransaction()
    {
        enableBackButton();
        expandAppBarLayout();
    }

    @Override
    public void onHomeTransaction()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    //ripristino l'highlight della relativa voce del menu.
    @Override
    public void onFragmentDisplayed(String fragmentTag)
    {
        navigationView.getMenu().findItem(Integer.parseInt(fragmentTag)).setChecked(true);
    }

    @Override
    public void expandAppBar()
    {
        expandAppBarLayout();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
            expandAppBarLayout();

            if(menuItem.getItemId() != R.id.settings)
                setFragment(menuItem.getItemId());
            else
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @SuppressLint("NewApi")
    protected void setupAdAtBottom()
    {
        content = (FrameLayout) findViewById(android.R.id.content);
        // inflate adLayout layout and set it to bottom by layouparams
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT,
                AppBarLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        adLayout.setLayoutParams(params);
        //adLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // adding viewtreeobserver to get height of adLayout layout , so that
        // android.R.id.content will set margin of that height
        ViewTreeObserver vto = adLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    adLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    adLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = adLayout.getMeasuredWidth();
                int height = adLayout.getMeasuredHeight();
                setSpaceForAd(height);

            }

        });
        content.removeView(adLayout);
        content.addView(adLayout);

    }

    private void setSpaceForAd(int height)
    {
        // content.getChildAt(0).setPadding(0, 0, 0, 50);
        View child0 = content.getChildAt(0);
        FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams) child0
                .getLayoutParams();
        layoutparams.bottomMargin = height;
        child0.setLayoutParams(layoutparams);
    }

    private void removeAd()
    {
        int height = adLayout.getMeasuredHeight();
        if(content != null)
        {
            View child0 = content.getChildAt(0);
            FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams) child0
                    .getLayoutParams();
            layoutparams.bottomMargin = -height;
            child0.setLayoutParams(layoutparams);
            content.removeView(adLayout);
        }
    }
}