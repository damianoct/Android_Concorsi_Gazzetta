package com.distesala.android_concorsi_gazzetta.ui;

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
import android.view.MenuItem;
import android.view.View;

import com.distesala.android_concorsi_gazzetta.R;

public class HomeActivity extends AppCompatActivity implements GazzetteListFragment.GazzetteListFragmentListener
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private AppBarLayout appBarLayout;

    //nelle support library 23 è presente la funzione setExpanded()
    //nelle v22 si è costretti a dichiarare questo metodo
    private void expandAppBarLayout()
    {
        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        assert behavior != null;
        behavior.onNestedFling(rootLayout, appBarLayout, null, 0, -10000, true);
    }

    private void setFragment(int tag)
    {
        //clear history of SEGUE transactions.
        getSupportFragmentManager().popBackStackImmediate("SEGUE", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //first drawer transaction?
        String backStackTag = getSupportFragmentManager().getBackStackEntryCount() == 0 ? "INIT" : "DRAWER";

        Fragment fragmentToAdd = getSupportFragmentManager().findFragmentByTag(String.valueOf(tag));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragmentToAdd = (fragmentToAdd != null) ? fragmentToAdd : createFragmentForTag(tag);
        transaction.addToBackStack(backStackTag).replace(R.id.content_frame, fragmentToAdd, String.valueOf(tag)).commit();
    }

    //SERVE UN FACTORY.....
    private Fragment createFragmentForTag(int tag)
    {
        switch (tag)
        {
            case R.id.gazzette:
                return new GazzetteListFragment();
            case R.id.concorsi:
                return new ConcorsiFragment();
            default:
                return null;
        }
    }

    private void initNavigationDrawer()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                expandAppBarLayout();
                setFragment(menuItem.getItemId());
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        //pulsante navigation drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBarDrawerToggle.syncState();

        initNavigationDrawer();

        /*

        int actualFragmentTag = (savedInstanceState != null) ?
                                    Integer.parseInt(getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment").getTag()) :
                                        R.id.gazzette;

        setFragment(actualFragmentTag);
         */

        /* restore state if needed */

        if (savedInstanceState != null)
        {
            setFragment(Integer.parseInt(getSupportFragmentManager()
                                            .getFragment(savedInstanceState, "currentFragment")
                                                .getTag()));
        }
        else //default fragment, first transition not added to backstack
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GazzetteListFragment(), String.valueOf(R.id.gazzette)).commit();
        }

        //only for debugging
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged()
            {
                Log.i("[MAIN] BackStack", "---------------------------------------------------" );
                Log.i("[MAIN] Backstack", "Count -> " + String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
                for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
                {
                    Log.i("[MAIN] Backstack", getSupportFragmentManager().getBackStackEntryAt(i).toString());
                }
                Log.i("[MAIN] BackStack", "---------------------------------------------------" );
            }
        });
    }

    //le ho provate tutte, non c'è altro modo di sistemare questa funzione per gestire il back button.
    @Override
    public void onBackPressed()
    {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            String headOfStack = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() -1).getName();
            Log.i("backstack", "cima dello stack --> " + headOfStack);
            if(headOfStack.equalsIgnoreCase("SEGUE")) //inner transaction
                super.onBackPressed();

            else if(headOfStack.equalsIgnoreCase("DRAWER")) //drawer transaction
                getSupportFragmentManager().popBackStackImmediate("INIT", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            else if(headOfStack.equalsIgnoreCase("INIT")) //first drawer transaction
                getSupportFragmentManager().popBackStack();
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //salvo lo stato
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }

    @Override
    public void onChoise()
    {
        //actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        expandAppBarLayout();
    }

    @Override
    public void onBackHome()
    {
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);


    }
}
