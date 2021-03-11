package com.enablex.multiconferencequickapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enablex.multiconferencequickapp.R;

import java.util.List;

public class BreakoutRoomAdapter extends RecyclerView.Adapter<BreakoutRoomAdapter.BreakoutRoomAdapterHolder> {

    RoomItemClickListener roomItemClickListener;
    List<String> stringList;
    Context context;
    String mOption;


    public BreakoutRoomAdapter(Context context, List<String> list, BreakoutRoomAdapter.RoomItemClickListener itemClickListener, String options) {
        this.roomItemClickListener = itemClickListener;
        this.stringList = list;
        this.context = context;
        this.mOption = options;
    }

    @NonNull
    @Override
    public BreakoutRoomAdapter.BreakoutRoomAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, null);
        return new BreakoutRoomAdapter.BreakoutRoomAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BreakoutRoomAdapter.BreakoutRoomAdapterHolder holder, int position) {

        holder.userTV.setText(checkNullValue(stringList.get(position)));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class BreakoutRoomAdapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userTV;
        LinearLayout llItem;

        public BreakoutRoomAdapterHolder(View itemView) {
            super(itemView);
            userTV = (TextView) itemView.findViewById(R.id.userTV);
            llItem = (LinearLayout) itemView.findViewById(R.id.llItem);

            llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llItem:
                    roomItemClickListener.onRoomItemClick(getLayoutPosition(),mOption);
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

    public interface RoomItemClickListener {
        void onRoomItemClick(int position, String options);
    }
}
