package com.ibrahim.mymemoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<EventDAO> mData;
    private LayoutInflater mInflater;
    private ItemClickListener editEventClickListener;
    private ItemClickListener deleteEventClickListener;

    // data is passed into the constructor
    CustomAdapter(Context context, List<EventDAO> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.text_row_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String eventTitle = mData.get(position).getTitle();
        String eventDate = mData.get(position).getDate();
        holder.tv_event_title.setText(eventTitle);
        holder.tv_event_date.setText(eventDate);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_event_title, tv_event_date;
        ImageButton ibtn_delete_event;
        LinearLayout ll_edit_event;

        ViewHolder(View itemView) {
            super(itemView);
            tv_event_title = itemView.findViewById(R.id.event_title);
            tv_event_date = itemView.findViewById(R.id.event_date);
            ibtn_delete_event = itemView.findViewById(R.id.delete_event_btn);
            ll_edit_event = itemView.findViewById(R.id.ll_edit_event);
            //itemView.setOnClickListener(this);
            ll_edit_event.setOnClickListener(this);
            ibtn_delete_event.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.delete_event_btn:
                    if (deleteEventClickListener != null) deleteEventClickListener.onEventDeleteClick(view, getAdapterPosition());
                    break;
                case R.id.ll_edit_event:
                    if (editEventClickListener != null) editEventClickListener.onItemClick(view, getAdapterPosition());
                    break;
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return String.valueOf(mData.get(id).getId());
    }

    // allows clicks events to be caught
    void setEditEventClickListener(ItemClickListener itemClickListener) {
        this.editEventClickListener = itemClickListener;
    }
    void setDeleteEventClickListener(ItemClickListener deleteEventClickListener){
        this.deleteEventClickListener = deleteEventClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onEventDeleteClick(View view, int adapterPosition);
    }
}