package iluxonchik.github.io.markitdown;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import iluxonchik.github.io.markitdown.dialog.ShareAsDialogFragment;


public class MainActivity extends AppCompatActivity
        implements DatabaseCABListFragment.OnCABStatusChangedListener,
        FragmentCommunicationContract.OnMessageSendingNeeded {

    private final String TOP_FRAGMENT_TAG = "TopFragment";
    private final String CURR_POSITION = "currentPosition";

    private String[] drawerOptions;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private LinearLayout navigationDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    private boolean skipTitleChangeOnBackstackChage = false;
    private boolean skipNavDrawerPositionUpdate = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: remove line later, only for testing purposes.
        //deleteDatabase(MarkItDownDbContract.DB_NAME);

        // Set the Toolbar to be the ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        drawerOptions = getResources().getStringArray(R.array.drawer_options);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawer = (LinearLayout) findViewById(R.id.navigation_drawer);
        drawerList = (ListView) drawerLayout.findViewById(R.id.drawer_list);

        // DrawerListener - listens to drawer's open and close events
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            /* Called when a drawer has settled in a completely open state */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates a call to onPrepareOptionsMenu
            }

            /* Called when a drawer has settled in a completely open state */
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu(); // creates a call to onPrepareOptionsMenu
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set the adapter on the ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,
                drawerOptions));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURR_POSITION);
            updateActionBarTitle(currentPosition);
        } else {
            // No saved instance state, select TopFragment
            selectItem(0);
        }

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                /*
                     This call comes all the way at the end of the sequence – after the new
                     fragment’s onResume call is made when the fragment is being added, and after
                     the fragment’s onDetach call is made when the fragment is being removed.
                 */
                FragmentManager fragmentManager = getFragmentManager();
                Fragment activeFragment = fragmentManager.findFragmentByTag(TOP_FRAGMENT_TAG);

                if (activeFragment instanceof TopFragment) {
                    currentPosition = 0;
                }


                if (activeFragment instanceof NotesFragment) {
                    currentPosition = 1;
                }


                if (activeFragment instanceof NotebooksFragment) {
                    currentPosition = 2;
                }


                if (activeFragment instanceof TagsFragment) {
                    currentPosition = 3;
                }


                if (activeFragment instanceof SettingsFragment) {
                    currentPosition = 4;
                }

                if (fragmentManager.getBackStackEntryCount() == 0) {
                    /* If the back stack is empty, end the activity */
                    finish();
                }

                if(skipTitleChangeOnBackstackChage) {
                    skipTitleChangeOnBackstackChage = false;
                } else {
                    updateActionBarTitle(currentPosition);
                }

                if (skipNavDrawerPositionUpdate) {
                    skipNavDrawerPositionUpdate = false;
                } else {
                    drawerList.setItemChecked(currentPosition, true);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURR_POSITION, currentPosition);
        super.onSaveInstanceState(outState);
    }


    /* Called whenever invalidateOptionsMenu() is invoked */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide the "Share As" option from the menu
        boolean isDrawerOpen = drawerLayout.isDrawerOpen(navigationDrawer);
        return super.onPrepareOptionsMenu(menu);
    }

    /* Synchronize the ActionBarToggle right after the activity is created, so that
     * the state of the drawer icon is synchronized with the state of the DrawerLayout */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        // Pass the event to ActionBarDrawerToggle, if it returns true,
        // then it has handled the app icon touch event.
        // Let the ActionBarDrawerToggle handle being clicked
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle other action bar items clicks
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void displaySaveAsDialog() {
        DialogFragment shareAsDialog = new ShareAsDialogFragment();
        shareAsDialog.show(getFragmentManager(), null);
    }

    private void selectItem(int position) {
        currentPosition = position;
        Fragment fragment = null;

        switch (position) {
            case 0:
                // Home
                Intent in = new Intent(this, EditNoteActivity.class);
                //in.putExtra(EditNoteActivity.NOTE_ID_ARG, -1);
                startActivity(in);
                return;

            case 1:
                // Notes
                fragment = new NotesFragment();
                break;
            case 2:
                // Notebooks
                fragment = new NotebooksFragment();
                break;
            case 3:
                // Tags
                Intent inte = new Intent(this, ViewNoteActivity.class);
                inte.putExtra(ViewNoteActivity.EXTRA_NOTE_ID, 1);
                startActivity(inte);
                return;
            //fragment = new TagsFragment();
            //break;
            case 4:
                // Settings
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new TopFragment();
                break;
        }

        // Replace current fragment
        replaceFragment(R.id.content_frame, fragment, TOP_FRAGMENT_TAG);

        // Highlight the selected item, update the title and close the drawer
        drawerList.setItemChecked(position, true);
        updateActionBarTitle(position);
        drawerLayout.closeDrawer(navigationDrawer);

    }

    private void replaceFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    /**
     * Update ActionBar's title based on the position of the item in the NavigationDrawer's
     * ListView.
     *
     * @param position position of the item in the NavigationDrawer's ListView.
     */
    private void updateActionBarTitle(int position) {
        String title;
        if (position == 0) {
            // TODO: replace with a more appropriate title
            title = getResources().getString(R.string.app_name);
        } else {
            title = drawerOptions[position];
        }
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onCABCreate() {
        // Disable drawer layout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onCABDestroy() {
        // Enable drawer layout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onMessageSent(int msgCode, Bundle args) {
        if (msgCode == FragmentCommunicationContract.Message.MESSAGE_START_NOTES_FROM_NOTEBOOKS) {
            Fragment frag = new NotesFragment();
            frag.setArguments(args);
            replaceFragment(R.id.content_frame, frag, TOP_FRAGMENT_TAG);
        } else if (msgCode == FragmentCommunicationContract.Message.
                MESSAGE_CHANGE_ACTION_BAR_TITLE) {
            String title = args.getString(FragmentCommunicationContract.
                    SetTitleBarTitle.ARG_FRAGMENT_TITLE);
            getSupportActionBar().setTitle(title);
            skipTitleChangeOnBackstackChage = true;
            skipNavDrawerPositionUpdate = true;
        }
    }
}
