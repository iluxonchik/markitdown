package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;


public class MainActivity extends Activity {

    private final String TOP_FRAGMENT_TAG = "TopFragment";

    private String[] drawerOptions;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerOptions = getResources().getStringArray(R.array.drawer_options);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.drawer);

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

        // Set the adapter on the ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,
                drawerOptions));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }


    /* Called whenever invalidateOptionsMenu() is invoked */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // If the drawer is open, hide the "Share As" option from the menu
        boolean isDrawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_shareAs).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
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
       switch (item.getItemId()) {
           case R.id.action_shareAs:
               displaySaveAsDialog();
               return true;

           default:
             return  super.onOptionsItemSelected(item);
       }

    }


    private void displaySaveAsDialog() {
        DialogFragment shareAsDialog = new ShareAsDialogFragment();
        shareAsDialog.show(getFragmentManager(),null);
    }

    private void selectItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                // Home
                fragment = new TopFragment();
                break;
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
                fragment = new TagsFragment();
                break;
            case 4:
                // Settings
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new TopFragment();
                break;
        }

        // Replace current fragment
        replaceFragment(R.id.action_bar_container, fragment, TOP_FRAGMENT_TAG);

        // Highlight the selected item, update the title and close the drawer
        drawerList.setItemChecked(position, true);
        updateActionBarTitle(position);
        drawerLayout.closeDrawer(drawerList);

    }

    private void replaceFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    /**
     * Update ActionBar's title based on the position of the item in the NavigationDrawer's
     * ListView.
     * @param position position of the item in the NavigationDrawer's ListView.
     */
    private void updateActionBarTitle(int position) {
        String title;
        if (position == 0) {
            // TODO: replace with a more appropriate title
            title = getResources().getString(R.string.app_name);
        }
        else {
            title = drawerOptions[position];
        }
        getActionBar().setTitle(title);
    }
}
