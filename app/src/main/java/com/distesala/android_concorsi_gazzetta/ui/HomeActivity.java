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
    private static final String HOME_FRAGMENT = String.valueOf(R.id.gazzette);

    private static final String INIT_TRANSACTION = "INIT";
    private static final String DRAWER_TRANSACTION = "DRAWER";
    private static final String SEGUE_TRANSACTION = "SEGUE";

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
        assert behavior != null;
        behavior.onNestedFling(rootLayout, appBarLayout, null, 0, -10000, true);
    }

    private void setFragment(int tag)
    {
        //clear history of SEGUE transactions.
        getSupportFragmentManager().popBackStackImmediate(SEGUE_TRANSACTION, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //first drawer transaction?
        String backStackTag = getSupportFragmentManager().getBackStackEntryCount() == 0 ? INIT_TRANSACTION : DRAWER_TRANSACTION;

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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

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

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                if(!menuItem.isChecked())
                {
                    menuItem.setChecked(true);
                    expandAppBarLayout();
                    setFragment(menuItem.getItemId());
                }

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

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        initNavigationDrawer();

        /* restore state if needed */

        if (savedInstanceState != null)
        {
            Fragment savedFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            setFragment(Integer.parseInt(savedFragment.getTag()));
            navigationView.getMenu().findItem(Integer.parseInt(savedFragment.getTag())).setChecked(true);
        }
        else //default fragment, first transition not added to backstack
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GazzetteListFragment(), HOME_FRAGMENT).commit();
            navigationView.getMenu().findItem(R.id.gazzette).setChecked(true);
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
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
        if (homeFragment != null && homeFragment.isVisible()) //if I am in home fragment, quit application.
            finish();

        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            //get head of back stack
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
