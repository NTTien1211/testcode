package com.lutech.notepad.acivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.view.ActionMode;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.lutech.notepad.R;
import com.lutech.notepad.adapter.EditCategoriesAdapter;
import com.lutech.notepad.adapter.NoteShowAdapter;
import com.lutech.notepad.database.Database;
import com.lutech.notepad.fragment.NoteFragment;
import com.lutech.notepad.fragment.TrashFragment;
import com.lutech.notepad.fragment.EditCategoriesFragment;
import com.lutech.notepad.model.NotesModel;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EditCategoriesFragment.OnAddButtonClickListener, NoteShowAdapter.OnItemLongClick {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem deleteMenuItem;
    private Toolbar toolbar;
    private boolean isLongPressed;
    private Menu menu;
    private ActionMode actionMode;
    List<String> mlist;
    Database database;
    private ActionMode myActMode;
    private boolean isActionModeActive = false;

    boolean ktra = false;
    Fragment selectedFragment;
    String fragmenttitle;
    NoteShowAdapter adapter;
    private boolean isActionModeShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(this, "note.sqlite", null, 2);
        database.QueryData("CREATE TABLE IF NOT EXISTS NOTE " +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title VARCHAR(200), " +
                "Content VARCHAR(200), " +
                "DayNote VARCHAR(200), " +
                "TimeNote VARCHAR(200)," +
                "Status VARCHAR(200));");
        database.QueryData("CREATE TABLE IF NOT EXISTS Categories " +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name VARCHAR(200), " +
                "Id_note VARCHAR(200))");
        anhxa();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null && toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }

        mlist = new ArrayList<>();
        adapter = new NoteShowAdapter();
        adapter.setOnitemLongClick(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        replaceFagment(new NoteFragment(), "Notepad Free");
        menu = navigationView.getMenu();
        GetCatagori();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_select);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");

        MenuItem menuItem = menu.findItem(R.id.action_soft);
        if (menuItem != null) {
            SpannableString spannableString = new SpannableString(menuItem.getTitle());
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            menuItem.setTitle(spannableString);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                NoteFragment fragmentNote = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
                if (fragmentNote != null) {
                    fragmentNote.filter(newText);
                }
                return true;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_soft) {
            showSortingDialog();
            return true;
        } else if (id == R.id.action_selectAll) {
            // Trong Activity của bạn
            NoteFragment noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (noteFragment != null) {
                noteFragment.selectAllItems();
            }

            toolbar.getMenu().clear();
            getMenuInflater().inflate(R.menu.layout_menu_handle, toolbar.getMenu());
            return true;
        } else if (id == R.id.action_deletehandler) {
            NoteFragment noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (noteFragment != null) {
                noteFragment.deleteSelectedItems();
            }
            toolbar.getMenu().clear();
            getMenuInflater().inflate(R.menu.layout_menu_toolbar, toolbar.getMenu());
        } else if (id == R.id.action_choiceall) {
            NoteFragment noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (noteFragment != null) {
                noteFragment.selectAllItems();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        selectedFragment = null;
        fragmenttitle = "";

        if (id == R.id.item_notes) {
            selectedFragment = NoteFragment.newInstance(null);
            fragmenttitle = "Notepad Free";
        } else if (id == R.id.item_editcategory) {
            selectedFragment = new EditCategoriesFragment();
            fragmenttitle = "Edit categories";
        } else if (id == R.id.item_trach) {
            selectedFragment = new TrashFragment();
            fragmenttitle = "Trash";
        } else if (id == R.id.item_backup) {
            Intent intent = new Intent(MainActivity.this, BackupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.item_setting) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.item_help) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            String categoryName = item.getTitle().toString();
            selectedFragment = NoteFragment.newInstance(categoryName);
            Toast.makeText(this, "" + categoryName, Toast.LENGTH_SHORT).show();
            fragmenttitle = categoryName;
        }
        replaceFagment(selectedFragment, fragmenttitle);

        drawerLayout.closeDrawer(GravityCompat.START);
        invalidateOptionsMenu();
        return true;
    }


    private void replaceFagment(Fragment fragment, String fragmenttitle) {
        if (fragment != null) {
            Bundle args = new Bundle();
            args.putString("categoryName", fragmenttitle);
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment, fragment);
            transaction.commit();
            getSupportActionBar().setTitle(fragmenttitle);

            invalidateOptionsMenu(); // Di chuyển invalidateOptionsMenu() vào đây
        } else {
            Log.e("User_Main_Activity", "Attempted to replace fragment with null");
        }
    }


    private void showSortingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by");
        String[] options = {"Titil: (A-Z)", "Title: (Z-A)", "Day (Mới nhất)", "Day (Cũ nhất)"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                NoteFragment fragmentNote = (NoteFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
                if (fragmentNote != null) {
                    fragmentNote.sortData(i);
                }
            }
        });
        builder.create().show();
    }

    private void anhxa() {
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navi_view_left);
        toolbar = findViewById(R.id.toolbar_notes);
    }

    @Override
    public void onAddButtonClicked() {
        recreate();

    }

    public void GetCatagori() {
        Cursor dataCate = database.GetData("SELECT * FROM Categories");
        mlist.clear();
        if (dataCate != null) {
            while (dataCate.moveToNext()) {
                int id = dataCate.getInt(0);
                String name = dataCate.getString(1);
                mlist.add(name);
                navigationView.getMenu().add(R.id.menu_groups_add, id, id, name).setIcon(R.drawable.iconsfolder30);
            }
        }
    }


    @Override
    public void onItemLongClick() {
        myActMode = startSupportActionMode(myActModeCallback);
        Log.d("TAG", "onItemLongClick");
        toolbar.getMenu().clear();
        toolbar.setVisibility(View.GONE);
    }

    private ActionMode.Callback myActModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.layout_menu_handle, menu);
            mode.setTitle("Select option here");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            myActMode = null;
        }
    };

}