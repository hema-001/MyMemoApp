package com.ibrahim.mymemoapp;

//import android.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
        bundle.putString("event_ing_uri", allEvents.get(position).getEvent_img_uri());

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
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}

