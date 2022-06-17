package com.ibrahim.mymemoapp;

import static android.app.Activity.RESULT_OK;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditEventFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private EditText et_event_title, et_event_date, et_event_time, et_event_place, et_event_priority;
    private DatePickerFragment datePicker;
    private TimePickerFragment timePicker;
    private Button btn_apply_edit_event, btn_close_edit_event, btn_take_photo;
    private EventDAO event;
    private CheckBox cb_set_reminder;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private ImageView iv_image_taken;
    private Bitmap imageBitmap;
    private Uri imageUri;

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
        cb_set_reminder = fragmentView.findViewById(R.id.cb_set_reminder);
        btn_take_photo = fragmentView.findViewById(R.id.btn_take_photo);
        iv_image_taken = fragmentView.findViewById(R.id.iv_taken_image);

        btn_apply_edit_event.setOnClickListener(this);
        btn_close_edit_event.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);

        event = new EventDAO(getArguments().getInt("id"), getArguments().getString("title"),
                getArguments().getString("date"), getArguments().getString("time"),
                getArguments().getString("place"), getArguments().getInt("priority"),
                getArguments().getInt("notify"), getArguments().getString("event_img_uri"));

        et_event_title.setText(event.getTitle());
        et_event_date.setText(event.getDate());
        et_event_time.setText(event.getTime());
        et_event_place.setText(event.getPlace());
        et_event_priority.setText(String.valueOf(event.getPriority()));

        // on edit fragment creation prepare the imageview with the stored image for the event
        // if null, skip.
        if(event.getEvent_img_uri() != null){
            Log.i("MyTag", "imageUri in init = "+event.getEvent_img_uri());
            Uri imageUri = Uri.parse(event.getEvent_img_uri());
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                iv_image_taken.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // set initial set reminder checkbox state based on its value in db
        if(event.getNotify() == 1){
            cb_set_reminder.setChecked(true);
        }else{
            cb_set_reminder.setChecked(false);
        }
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

                // if no photo has been taken, skip saving photo
                if(imageBitmap != null){
                    savePhoto();
                }

                // Alternate set reminder checkbox based on user updates
                if(cb_set_reminder.isChecked()){
                    dbHelper.updateNotify(getContext(), String.valueOf(event.getId()), 1);

                    // get date and time from the edit text fields, in this way we ensure that
                    // the reminder is set for the lastly updated date and time values
                    String [] date = et_event_date.getText().toString().split("/");
                    String [] time = et_event_time.getText().toString().split(":");

                    // prepare a calender instance with the given date and time
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.valueOf("20"+date[2]));
                    calendar.set(Calendar.MONTH, (Integer.valueOf(date[0])-1));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[1]));
                    calendar.set(Calendar.HOUR, Integer.valueOf(time[0]));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
                    calendar.set(Calendar.SECOND, 00);

                    // setup an alarm
                    alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getContext(), AlarmReceiver.class);
                    intent.putExtra("title", et_event_title.getText().toString().trim());
                    intent.putExtra("date", et_event_date.getText().toString().trim());
                    intent.putExtra("time", et_event_time.getText().toString().trim());
                    alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

                }else if (!cb_set_reminder.isChecked()){
                    // FIXME: Potentially the location of bug 7, https://github.com/hema-001/MyMemoApp/issues/7
                    dbHelper.updateNotify(getContext(), String.valueOf(event.getId()), 0);
                    if (alarmManager!= null) {
                        alarmManager.cancel(alarmIntent);
                    }
                }
                Toast.makeText(getContext(),"Event updated successfully",Toast.LENGTH_SHORT).show();
                dbHelper.close();
                getActivity().recreate();
                getActivity().onBackPressed();
                break;
            case R.id.btn_close_edit_event:
                // Close fragment safely
                getActivity().onBackPressed();
                break;
            case R.id.btn_take_photo:
                dispatchTakePictureIntent();
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
        String selectedDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
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

    // The following lines of codes are reused from previous camera capture implementation
    private String createImageFileName(){
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        return imageFileName;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (true/*takePictureIntent.resolveActivity(getContext().getPackageManager()) != null*/) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            iv_image_taken.setImageBitmap(imageBitmap);
        }
    }

    private void savePhoto() {
        File f;
        String cameraPath;
        String fileName = createImageFileName();
        Log.i("MyTag", "File name = "+fileName);
        try {
            cameraPath =
                    Environment.getExternalStorageDirectory().toString()
                            + File.separator
                            + "Pictures"
                            + File.separator
                            + fileName;
            Log.i("MyTag", "Camera path = "+cameraPath);
            f = new File(cameraPath);
            FileOutputStream out = new FileOutputStream(f);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, fileName, null));
            Log.i("MyLog", "uri = " + imageUri);
            DBHelper dbHelper = new DBHelper(getContext());
            int row = dbHelper.updateEventImgUri(getContext(),String.valueOf(event.getId()), imageUri.toString());
            Log.i("MyTag", "Addpicgallery rows affected = "+row);
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}