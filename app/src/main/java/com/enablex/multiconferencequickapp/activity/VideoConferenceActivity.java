package com.enablex.multiconferencequickapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enablex.multiconferencequickapp.adapter.BottomAdapter;
import com.enablex.multiconferencequickapp.adapter.BreakoutRoomAdapter;
import com.enablex.multiconferencequickapp.adapter.HorizontalViewAdapter;
import com.enablex.multiconferencequickapp.model.HorizontalViewModel;
import com.enablex.multiconferencequickapp.model.UserListModels;
import com.enablex.multiconferencequickapp.model.UserModel;
import com.enablex.multiconferencequickapp.utilities.OnDragTouchListener;
import com.enablex.multipartyquickapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import enx_rtc_android.Controller.EnxActiveTalkerViewObserver;
import enx_rtc_android.Controller.EnxBreakoutRoomObserver;
import enx_rtc_android.Controller.EnxPlayerView;
import enx_rtc_android.Controller.EnxReconnectObserver;
import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxRtc;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;

public class VideoConferenceActivity extends AppCompatActivity implements EnxRoomObserver, EnxStreamObserver, View.OnClickListener, EnxReconnectObserver, EnxActiveTalkerViewObserver,
        EnxBreakoutRoomObserver, BreakoutRoomAdapter.RoomItemClickListener, BottomAdapter.ItemClickListener {
    EnxRtc enxRtc;
    String token;
    String name;
    EnxPlayerView enxPlayerView;
    FrameLayout moderator;
    FrameLayout participant;
    ImageView disconnect;
    ImageView mute, video, camera, volume, more_items;
    private TextView audioOnlyText, dummyText;
    EnxRoom enxRooms;
    boolean isVideoMuted = false;
    boolean isAudioMuted = false;
    boolean muteBreakoutFlags;
    boolean breakoutAudioEnable;
    RelativeLayout rl;
    ArrayList<UserListModels> userArrayList;
    Gson gson;
    EnxStream localStream;
    int PERMISSION_ALL = 1;

    List<HorizontalViewModel> list;
    private RecyclerView mHorizontalRecyclerView;
    private HorizontalViewAdapter horizontalAdapter;
    private LinearLayoutManager horizontalLayoutManager;
    private int screenWidth;
    ActionBar actionBar;
    RelativeLayout bottomView;
    boolean touchView;
    ProgressDialog progressDialog;
    RecyclerView mRecyclerView;
    boolean touch = false;

    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
    };
    boolean isFrontCamera = true;
    private JSONArray breakoutRoomsArray;
    View breakoutRoomView;
    EnxRoom breakoutRoom;
    String invitationBreakoutRoomId;
    ImageButton disconnectBreakoutRoom;
    ImageButton joinBreakoutRoom;
    ImageButton audioMuteBreakoutRoom;
    RelativeLayout first_view;
    PopupWindow popupWindow;
    ArrayList<String> bottomViewItems;
    private BottomAdapter bottomViewAdapter;
    PopupWindow bottomItemsPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_conference);

        actionBar = getSupportActionBar();

        getPreviousIntent();
        actionBar.setTitle(name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                initialize();
            }
        }
    }

    private void initialize() {
        setUI();
        setClickListener();
        userArrayList = new ArrayList<>();
        bottomViewItems = new ArrayList<>();
        bottomViewItems.add("Create Breakout Room");
        bottomViewAdapter = new BottomAdapter(this, bottomViewItems, this);
        list = new ArrayList<>();
        gson = new Gson();
        enxRtc = new EnxRtc(this, this, this);
        localStream = enxRtc.joinRoom(token, getLocalStreamJsonObjet(), getReconnectInfo(), new JSONArray());

        progressDialog = new ProgressDialog(this);
        mHorizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontalRecyclerView);

        LocalBroadcastManager.getInstance(this).registerReceiver(mParticipantEventReceiver, new IntentFilter("participant-event-name"));

        horizontalAdapter = new HorizontalViewAdapter(list, this, screenWidth, screenWidth, false);

        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mHorizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        mHorizontalRecyclerView.setAdapter(horizontalAdapter);
    }

    private void setClickListener() {
        disconnect.setOnClickListener(this);
        mute.setOnClickListener(this);
        video.setOnClickListener(this);
        camera.setOnClickListener(this);
        volume.setOnClickListener(this);
        more_items.setOnClickListener(this);
        moderator.setOnTouchListener(new OnDragTouchListener(moderator));

        participant.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (bottomItemsPopupWindow != null && bottomItemsPopupWindow.isShowing()) {
                    bottomItemsPopupWindow.dismiss();
                    return false;
                }

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return false;
                }


                handleTouchListner();
                return false;
            }
        });

    }

    private void setUI() {
        moderator = (FrameLayout) findViewById(R.id.moderator);
        participant = (FrameLayout) findViewById(R.id.participant);
        disconnect = (ImageView) findViewById(R.id.disconnect);
        mute = (ImageView) findViewById(R.id.mute);
        video = (ImageView) findViewById(R.id.video);
        camera = (ImageView) findViewById(R.id.camera);
        volume = (ImageView) findViewById(R.id.volume);
        more_items = (ImageView) findViewById(R.id.more_items);
        dummyText = (TextView) findViewById(R.id.dummyText);
        audioOnlyText = (TextView) findViewById(R.id.audioonlyText);
        rl = (RelativeLayout) findViewById(R.id.rl);
        bottomView = (RelativeLayout) findViewById(R.id.bottomView);
        first_view = (RelativeLayout) findViewById(R.id.first_view);

        audioOnlyText.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels / 3;
    }

    private JSONObject getLocalStreamJsonObjet() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("audio", true);
            jsonObject.put("video", true);
            jsonObject.put("data", true);
            JSONObject videoSize = new JSONObject();
            videoSize.put("minWidth", 320);
            videoSize.put("minHeight", 180);
            videoSize.put("maxWidth", 1280);
            videoSize.put("maxHeight", 720);
            jsonObject.put("videoSize", videoSize);
            jsonObject.put("audioMuted", "false");
            jsonObject.put("videoMuted", "false");
            JSONObject attributes = new JSONObject();
            attributes.put("name", "myStream");
            jsonObject.put("attributes", attributes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void getPreviousIntent() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            name = getIntent().getStringExtra("name");
        }
    }


    private void showMoreItems() {
        if (enxRooms != null) {

            if (bottomViewItems.size() > 0) {
                LayoutInflater inflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.user_list_dialog, null);
                TextView TV = (TextView) view.findViewById(R.id.TV);
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_listRV);
                TV.setText("More Options");
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                if (bottomViewAdapter != null) {
                    bottomViewAdapter = new BottomAdapter(this, bottomViewItems, this);
                }
                recyclerView.setAdapter(bottomViewAdapter);

                bottomItemsPopupWindow = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, false);
                bottomItemsPopupWindow.showAtLocation(first_view, Gravity.CENTER, 0, 0);
            }
        }
    }

    private void showBreakoutRoomDialog(String optionName) throws JSONException {
        if (breakoutRoomsArray.length() > 0) {
            LayoutInflater inflater = (LayoutInflater)
                    getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.user_list_dialog, null);
            TextView TV = (TextView) view.findViewById(R.id.TV);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_listRV);
            TV.setText("Breakout Rooms");
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<String> rooms = new ArrayList<>();
            for (int i = 0; i < breakoutRoomsArray.length(); i++) {
                rooms.add(String.valueOf(breakoutRoomsArray.get(i)));
            }

            BreakoutRoomAdapter breakoutRoomAdapter = new BreakoutRoomAdapter(this, rooms, this, optionName);
            recyclerView.setAdapter(breakoutRoomAdapter);

            popupWindow = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, false);
            popupWindow.showAtLocation(first_view, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void onBottomItemClick(int position) {
        String value = bottomViewItems.get(position).trim().toString();
        switch (value) {
            case "Create Breakout Room":
                if (enxRooms != null) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("participants", 3);
                        jsonObject.put("audio", true);
                        jsonObject.put("video", false);
                        jsonObject.put("canvas", false);
                        jsonObject.put("share", false);
                        jsonObject.put("max_rooms", 2);
                        enxRooms.createBreakOutRoom(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                bottomItemsPopupWindow.dismiss();
                break;
            case "Join Breakout Room":
                if (enxRooms != null) {
                    if (breakoutRoomsArray.length() > 0) {
                        try {
                            showBreakoutRoomDialog("join");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Breakout Room is not created", Toast.LENGTH_SHORT).show();
                    }
                }
                bottomItemsPopupWindow.dismiss();
                break;
            case "Invite Breakout Room":
                if (enxRooms != null) {
                    if (breakoutRoomsArray.length() > 0) {
                        try {
                            showBreakoutRoomDialog("invite");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Breakout Room is not created", Toast.LENGTH_SHORT).show();
                    }
                }
                bottomItemsPopupWindow.dismiss();
                break;
            default:
                bottomItemsPopupWindow.dismiss();
                break;
        }
    }

    private BroadcastReceiver mParticipantEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String eventName = intent.getStringExtra("eventName");
            int position = intent.getIntExtra("position", 0);
            if (eventName.equalsIgnoreCase("InvitedUser")) {
                String bId = intent.getStringExtra("breakoutRoomId");
                List<UserListModels> userList;
                JSONArray jsonArray = new JSONArray();
                if (intent.getSerializableExtra("userList") != null) {
                    userList = (ArrayList<UserListModels>) intent.getSerializableExtra("userList");
                    for (int i = 0; i < userList.size(); i++) {
                        jsonArray.put(userList.get(i).getClientId());
                    }
                }

                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("clients", jsonArray);
                        jsonObject.put("room_id", bId);
                        enxRooms.inviteToBreakOutRoom(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(VideoConferenceActivity.this, "Select minimum one participant", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onRoomConnected(EnxRoom enxRoom, JSONObject jsonObject) {
//received when user connected with Enablex room
        enxRooms = enxRoom;
        if (enxRooms != null) {
            enxPlayerView = new EnxPlayerView(this, EnxPlayerView.ScalingType.SCALE_ASPECT_BALANCED, true);
            localStream.attachRenderer(enxPlayerView);
            moderator.addView(enxPlayerView);
            enxRooms.publish(localStream);
            enxRooms.setReconnectObserver(this);
            enxRoom.setActiveTalkerViewObserver(this);
            enxRoom.setBreakoutRoomObserver(this);

            try {
                String localClientId = jsonObject.getString("clientId");
                JSONArray userList = jsonObject.getJSONArray("userList");

                for (int i = 0; i < userList.length(); i++) {
                    JSONObject userListJsonObject = userList.getJSONObject(i);
                    String clientId = userListJsonObject.getString("clientId");
                    if (!clientId.equalsIgnoreCase(localClientId)) {
                        UserListModels userModel = gson.fromJson(userListJsonObject.toString(), UserListModels.class);
                        userArrayList.add(userModel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        //received when any error occurred while connecting to the Enablex room
        Toast.makeText(VideoConferenceActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUserConnected(JSONObject jsonObject) {
        // received when a new remote participant joins the call
        UserListModels userModel = gson.fromJson(jsonObject.toString(), UserListModels.class);
        userArrayList.add(userModel);
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
        // received when a  remote participant left the call
        UserModel userModel = gson.fromJson(jsonObject.toString(), UserModel.class);
        for (UserListModels userModel1 : userArrayList) {
            if (userModel1.getClientId().equalsIgnoreCase(userModel.getClientId())) {
                userArrayList.remove(userModel1);
            }
        }
    }

    @Override
    public void onPublishedStream(EnxStream enxStream) {
        //received when audio video published successfully to the other remote users
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {
//received when audio video unpublished successfully to the other remote users
    }

    @Override
    public void onStreamAdded(EnxStream enxStream) {
        //received when a new stream added
        if (enxStream != null) {
            enxRooms.subscribe(enxStream);
        }
    }

    @Override
    public void onSubscribedStream(EnxStream enxStream) {
        //received when a remote stream subscribed successfully
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {
//received when a remote stream unsubscribed successfully
    }

    public void onRoomDisConnected(JSONObject jsonObject) {
        //received when Enablex room successfully disconnected
        this.finish();
    }

    @Override
    public void onActiveTalkerView(RecyclerView recyclerView) {

        mRecyclerView = recyclerView;
        if (recyclerView == null) {
            participant.removeAllViews();
            dummyText.setVisibility(View.VISIBLE);

        } else {
            dummyText.setVisibility(View.GONE);
            participant.removeAllViews();
            participant.addView(recyclerView);

        }

        if (touch) {
            return;
        }
        if (mRecyclerView != null) {
            touch = true;
            mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                    if (bottomItemsPopupWindow != null && bottomItemsPopupWindow.isShowing()) {
                        bottomItemsPopupWindow.dismiss();
                        return false;
                    }

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        return false;
                    }

                    if (motionEvent.getAction() == 1) {
                        handleTouchListner();
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean b) {

                }
            });
        }

    }

    @Override
    public void onActiveTalkerView(RecyclerView recyclerView, EnxRoom enxRoom) {

    }

    @Override
    public void onAvailable(Integer integer) {

    }

    @Override
    public void onEventError(JSONObject jsonObject) {
//received when any error occurred for any room event
        Toast.makeText(VideoConferenceActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventInfo(JSONObject jsonObject) {
// received for different events update
    }

    @Override
    public void onNotifyDeviceUpdate(String s) {
// received when when new media device changed
    }

    @Override
    public void onAcknowledgedSendData(JSONObject jsonObject) {
// received your chat data successfully sent to the other end
    }

    @Override
    public void onMessageReceived(JSONObject jsonObject) {
// received when chat data received at room
    }

    @Override
    public void onACKSendMessage(JSONObject jsonObject) {

    }

    @Override
    public void onMessageDelete(JSONObject jsonObject) {

    }

    @Override
    public void onACKDeleteMessage(JSONObject jsonObject) {

    }

    @Override
    public void onMessageUpdate(JSONObject jsonObject) {

    }

    @Override
    public void onACKUpdateMessage(JSONObject jsonObject) {

    }


    @Override
    public void onUserDataReceived(JSONObject jsonObject) {
// received when custom data received at room
    }

    @Override
    public void onUserStartTyping(JSONObject jsonObject) {

    }




    @Override
    public void onConferencessExtended(JSONObject jsonObject) {

    }

    @Override
    public void onConferenceRemainingDuration(JSONObject jsonObject) {

    }

    @Override
    public void onAckDropUser(JSONObject jsonObject) {

    }

    @Override
    public void onAckDestroy(JSONObject jsonObject) {

    }

    @Override
    public void onAudioEvent(JSONObject jsonObject) {
        //received when audio mute/unmute happens
        try {
            String message = jsonObject.getString("msg");
            boolean isBreakoutRoom = jsonObject.getBoolean("breakoutroom");
            if (!isBreakoutRoom) {
                if (!isAudioMuted) {
                    if (message.equalsIgnoreCase("Audio Off")) {
                        mute.setImageResource(R.drawable.mute);
                        isAudioMuted = true;
                    }
                } else {
                    if (message.equalsIgnoreCase("Audio On")) {
                        mute.setImageResource(R.drawable.unmute);
                        isAudioMuted = false;
                    }
                }
            } else {
                if (!breakoutAudioEnable) {
                    if (message.equalsIgnoreCase("Audio Off")) {
                        audioMuteBreakoutRoom.setBackgroundResource(R.drawable.mute);
                        breakoutAudioEnable = true;
                    }
                } else {
                    if (message.equalsIgnoreCase("Audio On")) {
                        audioMuteBreakoutRoom.setBackgroundResource(R.drawable.unmute);
                        breakoutAudioEnable = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoEvent(JSONObject jsonObject) {
        //received when video mute/unmute happens
        try {
            String message = jsonObject.getString("msg");
            if (message.equalsIgnoreCase("Video On")) {
                video.setImageResource(R.drawable.ic_videocam);
                isVideoMuted = false;
            } else if (message.equalsIgnoreCase("Video Off")) {
                video.setImageResource(R.drawable.ic_videocam_off);
                isVideoMuted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceivedData(JSONObject jsonObject) {
//received when chat data received at room level
    }

    @Override
    public void onRemoteStreamAudioMute(JSONObject jsonObject) {
//received when any remote stream mute audio
    }

    @Override
    public void onRemoteStreamAudioUnMute(JSONObject jsonObject) {
//received when any remote stream unmute audio
    }

    @Override
    public void onRemoteStreamVideoMute(JSONObject jsonObject) {
//received when any remote stream mute video
    }

    @Override
    public void onRemoteStreamVideoUnMute(JSONObject jsonObject) {
//received when any remote stream unmute video
    }

    @Override
    public void onAckPinUsers(JSONObject jsonObject) {

    }

    @Override
    public void onAckUnpinUsers(JSONObject jsonObject) {

    }

    @Override
    public void onPinnedUsers(JSONObject jsonObject) {

    }

    @Override
    public void onClick(View view) {
        if (bottomItemsPopupWindow != null && bottomItemsPopupWindow.isShowing()) {
            bottomItemsPopupWindow.dismiss();
            return;
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        int id=view.getId();
        if(id== R.id.disconnect){

                if (enxRooms != null) {
                    if (enxPlayerView != null) {
                        enxPlayerView.release();
                        enxPlayerView = null;
                    }
                    enxRooms.disconnect();
                } else {
                    finish();
                }
                }

       else if(id== R.id.mute) {
            if (localStream != null) {
                if (!isAudioMuted) {
                    localStream.muteSelfAudio(true);
                } else {
                    localStream.muteSelfAudio(false);
                }
            }
        }

      else  if(id== R.id.video) {
            if (localStream != null) {
                if (!isVideoMuted) {
                    localStream.muteSelfVideo(true);
                } else {
                    localStream.muteSelfVideo(false);
                }
            }
        }
       else if(id== R.id.camera) {
            if (localStream != null) {
                if (!isVideoMuted) {
                    if (isFrontCamera) {
                        localStream.switchCamera();
                        camera.setImageResource(R.drawable.rear_camera);
                        isFrontCamera = false;
                    } else {
                        localStream.switchCamera();
                        camera.setImageResource(R.drawable.front_camera);
                        isFrontCamera = true;
                    }
                } else {
                    Toast.makeText(VideoConferenceActivity.this, "Please turn on the video to switch camera", Toast.LENGTH_LONG).show();
                }
            }
        }

       else if(id== R.id.volume) {
            if (enxRooms != null) {
                showRadioButtonDialog();
            }
        }
       else if(id== R.id.more_items) {
            showMoreItems();
        }
      else  if(id== R.id.join_breakoutRoom) {
            if (invitationBreakoutRoomId != null)
                joinBreakoutRoom(invitationBreakoutRoomId);
        }
      else  if(id== R.id.audio_mute_breakoutRoom) {
            audioMuteBreakoutRoom();
        }
       else if(id== R.id.disconnect_breakoutRoom) {
            disconnectBreakoutRoom();


        }
    }

    private void audioMuteBreakoutRoom() {
        if (!breakoutAudioEnable) {
            breakoutRoom.getLocalStream().muteSelfAudio(true);
        } else {
            breakoutRoom.getLocalStream().muteSelfAudio(false);
        }
    }

    private void disconnectBreakoutRoom() {
        if (breakoutRoom != null) {
            breakoutRoom.disconnect();
        } else {
            if (breakoutRoomView != null) {
                first_view.removeView(breakoutRoomView);
                breakoutRoomView = null;
            }
        }
    }

    @Override
    public void onRoomItemClick(int position, String options){

        popupWindow.dismiss();
        if (options.equalsIgnoreCase("join")) {
            try {
                joinBreakoutRoom(String.valueOf(breakoutRoomsArray.get(position)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            if (userArrayList != null && userArrayList.size() > 0) {
                Intent intent = new Intent(this, ParticipantsScreen.class);
                intent.putExtra("userList", (ArrayList) userArrayList);
                intent.putExtra("inviteBreakoutRoom", true);
                try {
                    intent.putExtra("breakoutRoomId",String.valueOf(breakoutRoomsArray.get(position)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            } else {
                Toast.makeText(this, "No Participant Present", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void joinBreakoutRoom(String roomId) {
        try {
            if (enxRooms != null && roomId != null) {
                enxRooms.pause();
                if (!isAudioMuted) {
                    if (localStream != null) {
                        localStream.muteSelfAudio(true);
                        localStream.muteSelfVideo(true);
                        muteBreakoutFlags = true;
                    }
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("role", "participant");
                jsonObject.put("room_id", roomId);
                JSONObject streamInfo = new JSONObject();
                streamInfo.put("audio", true);
                streamInfo.put("video", false);
                enxRooms.joinBreakOutRoom(jsonObject, streamInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBreakoutRoomUI(boolean remotecalling) {
        if (breakoutRoomView == null) {
            breakoutRoomView = LayoutInflater.from(this).inflate(R.layout.breakoutroomnotification, null);
            disconnectBreakoutRoom = breakoutRoomView.findViewById(R.id.disconnect_breakoutRoom);
            joinBreakoutRoom = breakoutRoomView.findViewById(R.id.join_breakoutRoom);
            audioMuteBreakoutRoom = breakoutRoomView.findViewById(R.id.audio_mute_breakoutRoom);

            joinBreakoutRoom.setOnClickListener(this);
            audioMuteBreakoutRoom.setOnClickListener(this);
            disconnectBreakoutRoom.setOnClickListener(this);
            first_view.addView(breakoutRoomView);
        }
        if (remotecalling) {
            if (audioMuteBreakoutRoom != null && joinBreakoutRoom != null) {
                audioMuteBreakoutRoom.setVisibility(View.GONE);
                joinBreakoutRoom.setVisibility(View.VISIBLE);
            }
        } else {
            if (audioMuteBreakoutRoom != null && joinBreakoutRoom != null) {
                audioMuteBreakoutRoom.setVisibility(View.VISIBLE);
                joinBreakoutRoom.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                       ) {
                    initialize();
                } else {
                    Toast.makeText(this, "Please enable permissions to further proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleTouchListner() {
        if (touchView) {
            bottomView.setVisibility(View.VISIBLE);
            touchView = false;
        } else {
            bottomView.setVisibility(View.GONE);
            touchView = true;
        }
    }

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(VideoConferenceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiogroup);
        List<String> stringList = new ArrayList<>();  // here is list

        List<String> deviceList = enxRooms.getDevices();
        for (int i = 0; i < deviceList.size(); i++) {
            stringList.add(deviceList.get(i));
        }
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        String selectedDevice = enxRooms.getSelectedDevice();
        if (selectedDevice != null) {
            for (int i = 0; i < stringList.size(); i++) {
                RadioButton rb = new RadioButton(VideoConferenceActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
                rb.setText(stringList.get(i));
                rg.addView(rb);
                if (selectedDevice.equalsIgnoreCase(stringList.get(i))) {
                    rb.setChecked(true);
                }

            }
            dialog.show();
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        enxRooms.switchMediaDevice(btn.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (enxRooms != null) {
            enxRooms = null;
        }
        if (enxRtc != null) {
            enxRtc = null;
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mParticipantEventReceiver);
    }

    public JSONObject getReconnectInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("allow_reconnect", true);
            jsonObject.put("number_of_attempts", 3);
            jsonObject.put("timeout_interval", 15);
            jsonObject.put("activeviews", "view");//view

            JSONObject object = new JSONObject();
            object.put("audiomute", true);
            object.put("videomute", true);
            object.put("bandwidth", true);
            object.put("screenshot", true);
            object.put("avatar", true);

            object.put("iconColor", getResources().getColor(R.color.colorPrimary));
            object.put("iconHeight", 30);
            object.put("iconWidth", 30);
            object.put("avatarHeight", 200);
            object.put("avatarWidth", 200);
            jsonObject.put("playerConfiguration", object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onReconnect(String message) {
        // received when room tries to reconnect due to low bandwidth or any connection interruption
        try {
            if (message.equalsIgnoreCase("Reconnecting")) {
                progressDialog.setMessage("Wait, Reconnecting");
                progressDialog.show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserReconnectSuccess(EnxRoom enxRoom, JSONObject jsonObject) {
        // received when reconnect successfully completed
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    EnxPlayerView playerView = list.get(i).getEnxPlayerView();
                    EnxStream enxStream = list.get(i).getEnxStream();
                    if (playerView != null) {
                        playerView.release();
                    }
                    if (enxStream != null) {
                        enxStream.detachRenderer();
                    }
                }
                list.removeAll(list);
                list = null;
            }
            Toast.makeText(this, "Reconnect Success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomAwaited(EnxRoom enxRoom, JSONObject jsonObject) {

    }

    @Override
    public void onUserAwaited(JSONObject jsonObject) {

    }

    @Override
    public void onAckForApproveAwaitedUser(JSONObject jsonObject) {

    }

    @Override
    public void onAckForDenyAwaitedUser(JSONObject jsonObject) {

    }

    @Override
    public void onAckAddSpotlightUsers(JSONObject jsonObject) {

    }

    @Override
    public void onAckRemoveSpotlightUsers(JSONObject jsonObject) {

    }

    @Override
    public void onUpdateSpotlightUsers(JSONObject jsonObject) {

    }

    @Override
    public void onRoomBandwidthAlert(JSONObject jsonObject) {

    }

    @Override
    public void onStopAllSharingACK(JSONObject jsonObject) {

    }







    @Override
    public void onAckCreateBreakOutRoom(JSONObject jsonObject) {
        try {

            if (breakoutRoomsArray == null) {
                JSONObject msg = jsonObject.getJSONObject("msg");
                breakoutRoomsArray = msg.getJSONArray("rooms");
                if (breakoutRoomsArray.length() > 0) {
                    bottomViewItems.add("Join Breakout Room");
                    bottomViewItems.add("Invite Breakout Room");
                }
            } else {
                if (breakoutRoomsArray.length() == 0) {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    breakoutRoomsArray = msg.getJSONArray("rooms");
                    if (breakoutRoomsArray.length() > 0) {
                        bottomViewItems.add("Join Breakout Room");
                        bottomViewItems.add("Invite Breakout Room");
                    }
                } else {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    for (int i = 0; i < msg.getJSONArray("rooms").length(); i++) {
                        breakoutRoomsArray.put(msg.getJSONArray("rooms").getJSONObject(i));
                    }
                }
            }

            Toast.makeText(this, "Breakout Room is created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAckCreateAndInviteBreakOutRoom(JSONObject jsonObject) {
        try {
            JSONObject msg = jsonObject.getJSONObject("msg");
            breakoutRoomsArray = msg.getJSONArray("rooms");
            if (breakoutRoomsArray.length() > 0) {
                bottomViewItems.add("Join Breakout Room");
                bottomViewItems.add("Invite Breakout Room");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAckInviteBreakOutRoom(JSONObject jsonObject) {
        try {
            int result = jsonObject.getInt("result");
            if (result == 0) {
                Toast.makeText(this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFailedJoinBreakOutRoom(JSONObject jsonObject) {

    }

    @Override
    public void onConnectedBreakoutRoom(EnxRoom enxRoom, JSONObject jsonObject) {
        try {
            this.breakoutRoom = enxRoom;
            showBreakoutRoomUI(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnectedBreakoutRoom(JSONObject jsonObject) {
        if (breakoutRoomView != null) {
            enxRooms.resume();
            if (muteBreakoutFlags) {
                if (localStream != null) {
                    localStream.muteSelfAudio(false);
                    localStream.muteSelfVideo(false);
                    muteBreakoutFlags = false;
                }
            }

            first_view.removeView(breakoutRoomView);
            breakoutRoomView = null;
            breakoutRoom = null;
        }
    }


    @Override
    public void onUserJoinedBreakoutRoom(EnxRoom enxRoom, JSONObject jsonObject) {

    }

    @Override
    public void onInvitationForBreakoutRoom(JSONObject jsonObject) {
        try {
            invitationBreakoutRoomId = jsonObject.optString("room_id");
            showBreakoutRoomUI(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyedBreakoutRoom(JSONObject jsonObject) {
        try {
            if (breakoutRoomsArray != null) {
                for (int i = 0; i < breakoutRoomsArray.length(); i++) {
                    JSONObject object = breakoutRoomsArray.getJSONObject(i);
                    String selectedRoomId = object.getString("_id");
                    if (selectedRoomId.equalsIgnoreCase(jsonObject.getString("room_id"))) {
                        breakoutRoomsArray.remove(i);
                        break;
                    }
                }
                if (breakoutRoomsArray.length() == 0) {
                    bottomViewItems.remove("Join Breakout Room");
                    bottomViewItems.remove("Invite Breakout Room");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUserDisconnectedFromBreakoutRoom(EnxRoom enxRoom, JSONObject jsonObject) {

    }

    @Override
    public void onAckRejectBreakOutRoom(JSONObject jsonObject) {

    }

    @Override
    public void onBreakoutRoomCreated(JSONObject jsonObject) {

    }

    @Override
    public void onBreakoutRoomInvited(JSONObject jsonObject) {

    }

    @Override
    public void onBreakoutRoomInviteRejected(JSONObject jsonObject) {

    }

    @Override
    public void onBreakoutroomjoining(JSONObject jsonObject) {

    }
}
