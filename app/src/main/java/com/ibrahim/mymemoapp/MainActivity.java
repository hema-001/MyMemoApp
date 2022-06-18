package com.ibrahim.mymemoapp;

//import android.app.FragmentManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomAdapter.ItemClickListener, View.OnClickListener {

    private CustomAdapter adapter;
    private FloatingActionButton addEvent;
    private DBHelper dbHelper;
    private ArrayList<EventDAO> allEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        addEvent = findViewById(R.id.addEvent_btn);
        addEvent.setOnClickListener(this);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_events);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        dbHelper = new DBHelper(this);
        allEvents = dbHelper.listEvents();
        dbHelper.close();
        if (allEvents.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new CustomAdapter(this, allEvents);
            adapter.setEditEventClickListener(this);
            adapter.setDeleteEventClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        else {
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no events in the database. Start adding now", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        // Prepare a bundle with the current event values to show it on edit event fragment
        Bundle bundle = new Bundle();
        bundle.putInt("id", allEvents.get(position).getId());
        bundle.putString("title", allEvents.get(position).getTitle());
        bundle.putString("date", allEvents.get(position).getDate());
        bundle.putString("time", allEvents.get(position).getTime());
        bundle.putString("place", allEvents.get(position).getPlace());
        bundle.putInt("priority", allEvents.get(position).getPriority());
        bundle.putInt("notify", allEvents.get(position).getNotify());
        bundle.putString("event_img_uri", allEvents.get(position).getEvent_img_uri());

        // Show the edit event fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment editEventFragment = new EditEventFragment();
        editEventFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container_view, editEventFragment, null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onEventDeleteClick(View view, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_event_msg);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                dbHelper.deleteEvent(String.valueOf(allEvents.get(adapterPosition).getId()), MainActivity.this);
                Toast.makeText(MainActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                MainActivity.this.recreate();
                dbHelper.close();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addEvent_btn:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment eventFragment = new EventFragment();
                fragmentTransaction.replace(R.id.fragment_container_view, eventFragment, null);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("MyTag", "query = "+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    // This function is reused from GeeksForGeeks tutorial, see link
    // https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<EventDAO> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (EventDAO item : allEvents) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getPlace().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No events found matching \""+text+"\"", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}

