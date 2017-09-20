package com.alameen.wael.hp.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.*;

public class ChatRoom extends AppCompatActivity implements View.OnClickListener {

    private EditText input;
    private RecyclerView.Adapter adapter;
    private List<Messages> message_list = new ArrayList<>();
    private RecyclerView recycler;
    private ImageButton sendMessage, attach;
    int previousLength = 0;
    private int numberOfMessages = 0;
    private DatabaseReference room, myRoom, root;
    private String roomName;
    private String roomToken;
    private TextView text;
    private RelativeLayout relativeLayout;
    String messageReceived, time, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
            getInstance().setPersistenceEnabled(true);
        }

        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        text = new TextView(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_chat_room);

        roomName = getIntent().getExtras().getString("roomName");
        String roomImage = getIntent().getExtras().getString("roomImage");
        roomToken = getIntent().getExtras().getString("roomToken");

        ((TextView) findViewById(R.id.room_name)).setTypeface(typeface);
        ((TextView) findViewById(R.id.room_name)).setText(roomName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recycler = (RecyclerView) findViewById(R.id.recycler_messages);
        recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(this, message_list);
        recycler.setAdapter(adapter);

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.room_image);
        Picasso.with(this).load(roomImage).into(circleImageView);

        input = (EditText) findViewById(R.id.enter_message);
        sendMessage = (ImageButton) findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

        SharedPreferences mePreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String me = mePreferences.getString("userName", "");

        room = FirebaseDatabase.getInstance().getReference().child(roomName);
        room.keepSynced(true);

        myRoom = FirebaseDatabase.getInstance().getReference().child(me);
        myRoom.keepSynced(true);

        room.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendToTheConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendToTheConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildRemoved", dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildMoved", s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", databaseError.getMessage());
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                attach.setVisibility(View.GONE);
                sendMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    sendMessage.setVisibility(View.GONE);
                    attach.setVisibility(View.VISIBLE);
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        sendMessage.setVisibility(View.GONE);
        attach = new ImageButton(this);
        attach.setLayoutParams(new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT));
        attach.setBackgroundColor(Color.WHITE);
        attach.setImageResource(R.drawable.ic_attachment_black_24dp);
        linearLayout.addView(attach);


    }

    private void appendToTheConversation(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        SharedPreferences mePreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String me = mePreferences.getString("userName", "");
        String roomImage = mePreferences.getString("userImage", "");

        root = FirebaseDatabase.getInstance().getReference();

        while (iterator.hasNext()) {
            final String key = ((DataSnapshot) iterator.next()).getKey();
            String str = (String) dataSnapshot.child(key).getValue();
            messageReceived = str.substring(0, str.lastIndexOf('_'));
            time = str.substring(str.lastIndexOf('_') + 1, str.lastIndexOf('?'));
            id = str.substring(str.lastIndexOf('?') + 1, str.length());
            numberOfMessages = (int) dataSnapshot.getChildrenCount();
            String status;

            if (key.equals(me)) {
                status = "sender";
            } else {
                status = "receiver";
            }

            if (id.equals(me+roomName) || id.equals(roomName+me)) {
                Messages messages = new Messages.Builder(status).getText(messageReceived).getLength(messageReceived.length()).getTime(time).build();
                message_list.add(messages);
                adapter.notifyItemInserted(0);
                adapter.notifyDataSetChanged();
                recycler.scrollToPosition(adapter.getItemCount() - 1);
            } else {
                showText();
            }

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    while (iterator.hasNext()) {
                        String keys = ((DataSnapshot) iterator.next()).getKey();
                        //DataSnapshot ds = dataSnapshot.child(keys);
                        //Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        //String userName = (String) map.get("userName");

                        if (id.contains(keys) && !keys.equals("Wael Al-ameen")) {
                            DatabaseReference mData = root.child(keys);
                            mData.child("lastMessage").setValue(messageReceived+"%"+id);
                            mData.child("lastTime").setValue(time+"%"+id);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("DatabaseError", databaseError.getDetails());
                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
            String myToken = sharedPreferences.getString("token", "");
            MainActivity.isInChat = "true";
            new Task().execute(messageReceived, roomName, roomImage, MainActivity.isInChat, myToken);
        }

        Database database = new Database(ChatRoom.this);
        database.insert(messageReceived, time, id);

        if (numberOfMessages == 0) {
            showText();
        } else {
            hideText();
        }

        database.close();
    }

    private void showText() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        text.setTypeface(typeface);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(80, 200, 80, 0); //left top right bottom
        text.setLayoutParams(layoutParams);
        text.setText("There's no messages yet");
        text.setTextSize(18f);
        text.setTextColor(Color.BLACK);
        text.setGravity(Gravity.CENTER);
        text.setBackgroundColor(getResources().getColor(R.color.gray));
        relativeLayout.removeView(text);
        relativeLayout.addView(text);
    }

    private void hideText() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        text.setTypeface(typeface);
        text.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.chat_room_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //startActivity(new Intent(this, MainActivity.class));
            onBackPressed();
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_message:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String messageText = input.getText().toString().trim();
        input.setText("");

        if (numberOfMessages == 0) {
            showText();
        } else {
            hideText();
        }

        root = FirebaseDatabase.getInstance().getReference();
        SharedPreferences mePreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String me = mePreferences.getString("userName", "");
        String image = mePreferences.getString("userImage", "");

        Map<String, Object> map = new HashMap<>();
        String key1 = room.push().getKey();
        map.put(key1, "");
        room.updateChildren(map);

        map = new HashMap<>();
        String key2 = myRoom.push().getKey();
        map.put(key2, "");
        myRoom.updateChildren(map);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        time = Integer.toString(hour)+":"+Integer.toString(minute);

        DatabaseReference messageSent = room.child(key1);
        map = new HashMap<>();
        map.put(me, messageText +"_"+time+"?"+me+roomName);
        messageSent.updateChildren(map);

        messageSent = myRoom.child(key2);
        map = new HashMap<>();
        map.put(me, messageText +"_"+time+"?"+me+roomName);
        messageSent.updateChildren(map);

        messageSent.keepSynced(true);
        MainActivity.isInChat = "true";
        new Task().execute(messageText, me, image, MainActivity.isInChat, roomToken);
        sendMessage.setVisibility(View.GONE);
        attach.setVisibility(View.VISIBLE);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.notif);
        mediaPlayer.start();
    }

    private class Task extends AsyncTask<String, Void, Void> {
        final String HOST_URL = "http://labsne.com/lbsne/fcm.php";

        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];
            String name = params[1];
            String image = params[2];
            String inChat = params[3];
            String token = params[4];

            try {
                URL url = new URL(HOST_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&"
                        + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8") + "&"
                        + URLEncoder.encode("isInChat", "UTF-8") + "=" + URLEncoder.encode(inChat, "UTF-8") + "&"
                        + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
