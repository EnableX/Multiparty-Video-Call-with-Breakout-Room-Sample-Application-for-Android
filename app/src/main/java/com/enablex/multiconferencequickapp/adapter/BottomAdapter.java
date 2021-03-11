package com.enablex.multiconferencequickapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enablex.multiconferencequickapp.R;

import java.util.ArrayList;
import java.util.List;

public class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.ListViewHolder> {

    private ItemClickListener itemClickListener;
    private ArrayList<String> itemsList;
    private Context context;

    public BottomAdapter(Context context, List<String> itemsList, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.itemsList = (ArrayList<String>) itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.screenshot_list_item, null);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.userTV.setText(checkNullValue(itemsList.get(position)));
        switch (itemsList.get(position).trim().toString()) {
            case "Create Breakout Room":
                holder.imageView.setImageResource(R.drawable.create_breakout_room);
                break;
            case "Join Breakout Room":
                holder.imageView.setImageResource(R.drawable.join_breakout_room);
                break;
            case "Invite Breakout Room":
                holder.imageView.setImageResource(R.drawable.invite);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout llItem;
        TextView userTV;
        ImageView imageView;

        public ListViewHolder(View itemView) {
            super(itemView);
            userTV = (TextView) itemView.findViewById(R.id.userTV);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            llItem = (LinearLayout) itemView.findViewById(R.id.llItem);
            llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llItem:
                    itemClickListener.onBottomItemClick(getLayoutPosition());
                    break;
            }

        }
    }

    private String checkNullValue(String value) {
        if (value != null) {
            return value;
        }
        return "";
    }

    public interface ItemClickListener {
        void onBottomItemClick(int position);
    }
}
