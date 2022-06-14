package com.ibrahim.mymemoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditEventFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private EditText et_event_title, et_event_date, et_event_time, et_event_place, et_event_priority;
    private DatePickerFragment datePicker;
    private TimePickerFragment timePicker;
    private Button btn_apply_edit_event, btn_close_edit_event;
    private EventDAO event;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_event_edit, container, false);

        // Non-dynamic fields that we need to only retrieve data from
        et_event_title = fragmentView.findViewById(R.id.et_event_title);
        et_event_place = fragmentView.findViewById(R.id.et_event_place);
        et_event_priority = fragmentView.findViewById(R.id.et_event_priority);

        // dynamic fields that get its value from pickers
        et_event_date = fragmentView.findViewById(R.id.et_event_date);
        et_event_date.setInputType(InputType.TYPE_NULL);
        et_event_date.setOnClickListener(this);

        et_event_time = fragmentView.findViewById(R.id.et_event_time);
        et_event_time.setInputType(InputType.TYPE_NULL);
        et_event_time.setOnClickListener(this);

        // map buttons on the fragment event to on click listeners
        btn_apply_edit_event = fragmentView.findViewById(R.id.btn_apply_edit_event);
        btn_close_edit_event = fragmentView.findViewById(R.id.btn_close_edit_event);

        btn_apply_edit_event.setOnClickListener(this);
        btn_close_edit_event.setOnClickListener(this);

        event = new EventDAO(getArguments().getInt("id"), getArguments().getString("title"),
                getArguments().getString("date"), getArguments().getString("time"),
                getArguments().getString("place"), getArguments().getInt("priority"));

        et_event_title.setText(event.getTitle());
        et_event_date.setText(event.getDate());
        et_event_time.setText(event.getTime());
        et_event_place.setText(event.getPlace());
        et_event_priority.setText(String.valueOf(event.getPriority()));

        return fragmentView;

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.et_event_date:
                // show date picker
                datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(),"datePicker");
                datePicker.setTargetFragment(this, 0);
                break;
            case R.id.et_event_time:
                timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "timePicker");
                timePicker.setTargetFragment(this, 1);
                break;
            case R.id.btn_apply_edit_event:
                // insert the new values into the DB
                event.setTitle(et_event_title.getText().toString().trim());
                event.setDate(et_event_date.getText().toString().trim());
                event.setTime(et_event_time.getText().toString().trim());
                event.setPlace(et_event_place.getText().toString().trim());
                event.setPriority(Integer.valueOf(et_event_priority.getText().toString().trim()));
                DBHelper dbHelper = new DBHelper(getContext());
                dbHelper.updateEvent(event, getContext());
                Toast.makeText(getContext(),"Event updated successfully",Toast.LENGTH_SHORT).show();
                getActivity().recreate();
                getActivity().onBackPressed();
                break;
            case R.id.btn_close_edit_event:
                // Close fragment safely
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Handle what should happen after the user picks a date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        et_event_date.setText(selectedDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        // Handle what should happen after the user picks a time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String strTime = sdf.format(calendar.getTime());
        et_event_time.setText(strTime);
    }
}