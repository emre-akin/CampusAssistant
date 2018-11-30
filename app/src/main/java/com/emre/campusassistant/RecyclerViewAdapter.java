package com.emre.campusassistant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mDeviceNames = new ArrayList<>();
    private Context mContext;

    //Declare interface
    private OnItemClicked onClick;
    //Make interface
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public RecyclerViewAdapter(Context context, ArrayList<String> mDeviceNames) {
        this.mContext = context;
        this.mDeviceNames = mDeviceNames;
    }

    //Use this every time when creating RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");

        viewHolder.deviceName.setText(mDeviceNames.get(i));

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mDeviceNames.get(i));
                //Toast.makeText(mContext, mDeviceNames.get(i), Toast.LENGTH_SHORT).show();
                //Assign click to interface
                onClick.onItemClick(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        //This tells the adapter how many items are in your list
        return mDeviceNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView deviceName;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
    //Method to assign itemClick to interface
    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
