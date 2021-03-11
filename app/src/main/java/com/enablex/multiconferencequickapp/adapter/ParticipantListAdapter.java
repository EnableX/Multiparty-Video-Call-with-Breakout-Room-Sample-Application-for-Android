package com.enablex.multiconferencequickapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enablex.multiconferencequickapp.R;
import com.enablex.multiconferencequickapp.model.UserListModels;

import java.util.ArrayList;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ParticipantViewHolder> {

    private Context mContext;
    ArrayList<UserListModels> userArrayList;
    ParticipantItemClickListener clickListener;
    boolean mInviteBreakoutRoom;

    public ParticipantListAdapter(Context context, ArrayList<UserListModels> userArrayList, ParticipantItemClickListener itemClickListener, boolean inviteBreakoutRoom/*, String role*/) {
        this.mContext = context;
        this.userArrayList = userArrayList;
        this.clickListener = itemClickListener;
        this.mInviteBreakoutRoom = inviteBreakoutRoom;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_list_item, null);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        holder.userTV.setText(userArrayList.get(position).getName());
        holder.userTV.setText(checkNullValue(userArrayList.get(position).getName()));

        if (checkNullValue(userArrayList.get(position).getRole()).equalsIgnoreCase("moderator")) {
            holder.roleIV.setImageResource(R.drawable.moderator_icon);
        } else {
            holder.roleIV.setImageResource(R.drawable.participant_icon);
        }

        if (mInviteBreakoutRoom) {
            holder.inviteBreakoutRoomIV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public interface ParticipantItemClickListener {

        void onToggleClick(int position, boolean toggleValue);
    }

    private String checkNullValue(String value) {
        if (value != null) {
            return value;
        }
        return "";
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView roleIV;
        TextView userTV;
        CheckBox inviteBreakoutRoomIV;

        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            roleIV = (ImageView) itemView.findViewById(R.id.roleIV);
            userTV = (TextView) itemView.findViewById(R.id.userTV);
            inviteBreakoutRoomIV = (CheckBox) itemView.findViewById(R.id.inviteBreakoutRoom);
            inviteBreakoutRoomIV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.inviteBreakoutRoom:
                    clickListener.onToggleClick(getLayoutPosition(), inviteBreakoutRoomIV.isChecked());
                    break;
            }
        }
    }

    public void setUserArrayList(ArrayList<UserListModels> list) {
        this.userArrayList = list;
    }
}