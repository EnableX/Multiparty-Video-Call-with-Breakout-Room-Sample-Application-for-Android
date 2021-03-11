package com.enablex.multiconferencequickapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enablex.multiconferencequickapp.R;
import com.enablex.multiconferencequickapp.adapter.ParticipantListAdapter;
import com.enablex.multiconferencequickapp.model.UserListModels;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsScreen extends AppCompatActivity implements  ParticipantListAdapter.ParticipantItemClickListener {


    RecyclerView participantsRV;
    ParticipantListAdapter participantListAdapter;
    private LinearLayoutManager participantLM;
    ArrayList<UserListModels> userList;
//    Toolbar toolbar;
    public static boolean isParticipantScreen;
    boolean inviteBreakoutRoom;
    private String breakoutRoomId;
    List<UserListModels> invitedList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_screen);
        setUI();

        if (getIntent().getSerializableExtra("userList") != null) {
            userList = (ArrayList<UserListModels>) getIntent().getSerializableExtra("userList");
        }
        inviteBreakoutRoom = getIntent().getBooleanExtra("inviteBreakoutRoom",false);
        if(inviteBreakoutRoom){
            breakoutRoomId = getIntent().getStringExtra("breakoutRoomId");
        }

        participantListAdapter = new ParticipantListAdapter(this, userList,this,inviteBreakoutRoom);
        participantLM = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        participantsRV.setLayoutManager(participantLM);
        participantsRV.setAdapter(participantListAdapter);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isParticipantScreen = true;
        if (intent == null) {
            return;
        }

        if (intent.getSerializableExtra("userList") != null) {
            userList = (ArrayList<UserListModels>) intent.getSerializableExtra("userList");
            if(userList.size() != 0) {
                participantListAdapter.setUserArrayList(userList);
                participantListAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(this, "No Participants Present", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        isParticipantScreen = false;
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        isParticipantScreen = false;
        super.onBackPressed();
    }

    private void setUI() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        participantsRV = (RecyclerView) findViewById(R.id.participantsRV);
//        toolbar.setTitleTextColor(Color.WHITE);
//        toolbar.setTitle("Participants");
        isParticipantScreen = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.participant_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_invite_room);
        if(inviteBreakoutRoom){
            menuItem.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_invite_room:
                sendParticipants("InvitedUser");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onToggleClick(int position, boolean toggleValue) {
        if(toggleValue){
            invitedList.add(userList.get(position));
        }else {
            for (int i = 0; i < invitedList.size(); i++) {
                String cId = invitedList.get(i).getClientId();
                String clientId = userList.get(position).getClientId();
                if(cId.equalsIgnoreCase(clientId)){
                    invitedList.remove(invitedList.get(i));
                    break;
                }
            }
        }
    }

    private void sendParticipants(String eventName) {
        Intent intent = new Intent("participant-event-name");
        intent.putExtra("eventName", eventName);
        intent.putExtra("breakoutRoomId", breakoutRoomId);
        intent.putExtra("userList",(ArrayList)invitedList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}