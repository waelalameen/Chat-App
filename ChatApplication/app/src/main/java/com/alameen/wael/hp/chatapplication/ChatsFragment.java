package com.alameen.wael.hp.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatsFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static Context context;
    SharedPreferences preferences;
    private List<String> info = new ArrayList<>();
    private List<Chats> chats = new ArrayList<>();
    private String userName, userImage, token, lastMessage, lastTime;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFromDatabase();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chats.clear();
                loadFromDatabase();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.chat_rooms);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadFromDatabase() {
        context = getContext();
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.keepSynced(true);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                String id = null;
                SharedPreferences mePreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                String me = mePreferences.getString("userName", "");

                Map<String, Object> map = null;

                while (iterator.hasNext()) {
                    String conversations = ((DataSnapshot) iterator.next()).getKey();
                    DataSnapshot ds = dataSnapshot.child(conversations);

                    try {
                        map = (Map<String, Object>) ds.getValue();
                    } catch (Exception ignored) {

                    }

                    userName = (String) map.get("userName");
                    userImage = (String) map.get("userPhoto");
                    token = (String) map.get("token");
                    lastMessage = (String) map.get("lastMessage");
                    lastTime = (String) map.get("lastTime");

                    if (lastMessage != null) {
                        id = lastMessage.substring(lastMessage.lastIndexOf('%') + 1, lastMessage.length());
                    }

                    if (id != null) {
                        if (!id.contains(me)) {
                            lastMessage = "No Conversation";
                            lastTime = "";
                        } else {
                            if (lastMessage != null && lastTime != null) {
                                lastMessage = lastMessage.substring(0, lastMessage.lastIndexOf('%'));
                                lastTime = lastTime.substring(0, lastTime.lastIndexOf('%'));
                            }
                        }
                    }

                    info.add(conversations);

                    if (!me.equals(userName)) {
                        chats.add(new Chats(userName, userImage, token, lastMessage, lastTime));
                        setChatRooms(chats);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DatabaseError", databaseError.getDetails());
            }
        });
    }

    private void setChatRooms(List<Chats> chats) {
        RecyclerView.Adapter adapter = new CustomAdapter(getContext(), chats, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ChatRoom.class);
        intent.putExtra("roomName", chats.get(i).getUserName());
        intent.putExtra("roomImage", chats.get(i).getUserPhoto());
        intent.putExtra("roomToken", chats.get(i).getToken());
        startActivity(intent);
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private List<Chats> chats;
        private AdapterView.OnItemClickListener onItemClick;
        private Context context;

        CustomAdapter(Context context, List<Chats> chats, AdapterView.OnItemClickListener onItemClick) {
            LayoutInflater.from(context);
            this.context = context;
            this.chats = chats;
            this.onItemClick = onItemClick;
        }

        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_rooms_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(chats.get(position).getUserName());
            Picasso.with(context).load(chats.get(position).getUserPhoto()).into(holder.image);
            holder.shortMessage.setText(chats.get(position).getLastMessage());
            holder.time.setText(chats.get(position).getLastTime());
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name, shortMessage, time;
            ImageView image;

            ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Jannal.ttf");
                name = (TextView) itemView.findViewById(R.id.room_name);
                name.setTypeface(typeface);
                shortMessage = (TextView) itemView.findViewById(R.id.short_message);
                shortMessage.setTypeface(typeface);
                time = (TextView) itemView.findViewById(R.id.chat_date);
                time.setTypeface(typeface);
                image = (ImageView) itemView.findViewById(R.id.room_image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                onItemClick.onItemClick(null, view, getLayoutPosition(), getItemId());
            }
        }
    }
}
