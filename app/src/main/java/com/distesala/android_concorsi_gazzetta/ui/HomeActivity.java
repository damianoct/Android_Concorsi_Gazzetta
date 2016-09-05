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
import com.distesala.android_concorsi_gazzetta.ui.fragment.ConcorsiListFragment;
import com.distesala.android_concorsi_gazzetta.ui.fragment.FragmentListener;
import com.distesala.android_concorsi_gazzetta.ui.fragment.GazzetteListFragment;
import com.distesala.android_concorsi_gazzetta.ui.fragment.WebViewFragment;

public class HomeActivity extends AppCompatActivity implements FragmentListener
{
    public static final String HOME_FRAGMENT = String.valueOf(R.id.gazzette);
    private static final String SAVED_FRAGMENT = "currentFragment";

    private static final String INIT_TRANSACTION = "INIT";
    private static final String DRAWER_TRANSACTION = "DRAWER";

    public static final String SEGUE_TRANSACTION = "SEGUE";

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

        //check for existing fragment
        Fragment fragmentToAdd = getSupportFragmentManager().findFragmentByTag(String.valueOf(tag));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentToAdd == null)
            fragmentToAdd = createFragmentForTag(tag);

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
                return new ConcorsiListFragment();
            case R.id.settings:
                return new WebViewFragment();
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

        //hide nav drawer icon if a SEGUE transaction exists on top of the stack
        /*if(isSegueOnTop())
        {
            enableBackButton();
        }*/
    }

    private boolean isSegueOnTop()
    {
        return getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                getSupportFragmentManager()
                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                            .getName().equals(SEGUE_TRANSACTION);
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        initNavigationDrawer();

        /* restore state if needed */

        if (savedInstanceState != null) //restore fragment, non added to backstack
        {
            Fragment savedFragment = getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, savedFragment, savedFragment.getTag()).commit();
        }
        else //default fragment, first transition not added to backstack
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GazzetteListFragment()
                    , HOME_FRAGMENT).commit();
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
        Log.d("currentFragment", currentFragment.toString());
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
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    //restore menu checked item on backpress.
    @Override
    public void onFragmentDisplayed(String fragmentTag)
    {
        Log.d("onDisplayed", fragmentTag);
        navigationView.getMenu().findItem(Integer.parseInt(fragmentTag)).setChecked(true);
    }
}