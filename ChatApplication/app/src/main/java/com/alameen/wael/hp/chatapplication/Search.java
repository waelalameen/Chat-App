package com.alameen.wael.hp.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Search extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private List<Chats> chats = new ArrayList<>();
    private List<Chats> filtered = new ArrayList<>();
    private TextView title;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String name, image, token;
    private static boolean pointer = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);

        loadFromDatabase();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chats.clear();
                loadFromDatabase();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.chat_rooms);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initialize() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(this, chats, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void loadFromDatabase() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.keepSynced(true);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                HashMap<String, String> map = new HashMap<>();

                while (iterator.hasNext()) {
                    String user = ((DataSnapshot) iterator.next()).getKey();
                    DataSnapshot data = dataSnapshot.child(user);

                    try {
                        map = (HashMap<String, String>) data.getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    name = map.get("userName");
                    image = map.get("userPhoto");
                    token = map.get("token");

                    chats.add(new Chats(name, image, "Last Seen Recently", token, 0));
                    setChatRooms(chats);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("databaseError", databaseError.getMessage());
            }
        });
    }

    private void setChatRooms(List<Chats> chats) {
        RecyclerView.Adapter adapter = new CustomAdapter(this, chats, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search for Friends");
        searchView.setQuery(searchView.getQuery(), true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pointer = false;
                setItemsVisibility(menu, item, true);
                title.setText(R.string.search_for_friends);
                MenuItemCompat.collapseActionView(item);
                initialize();
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemsVisibility(menu, item, false);
                title.setText("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        });
        return true;
    }

    private void setItemsVisibility(Menu menu, MenuItem search, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            if(item != search) {
                item.setVisible(visible);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(Search.this, ChatRoom.class);
        if (pointer) {
            intent.putExtra("roomName", filtered.get(i).getUserName());
            intent.putExtra("roomImage", filtered.get(i).getUserPhoto());
            intent.putExtra("token", filtered.get(i).getToken());
        } else {
            intent.putExtra("roomName", chats.get(i).getUserName());
            intent.putExtra("roomImage", chats.get(i).getUserPhoto());
            intent.putExtra("token", chats.get(i).getToken());
        }
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(TextUtils.isEmpty(query)) {
            adapter.notifyDataSetChanged();
        } else {
            pointer = true;
            filtered = filter(chats, query);
            Log.d("query", query);
            adapter = new CustomAdapter(this, filtered, Search.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    private List<Chats> filter(List<Chats> chats, String query) {
        query = query.toLowerCase();
        Log.d("query", query);
        List<Chats> filteredList = new ArrayList<>();
        if (chats != null && chats.size() > 0) {
            for (Chats chat : chats) {
                try {
                    if (chat.getUserName().toLowerCase().contains(query)) {
                        filteredList.add(chat);
                    }
                } catch (Exception e) {
                    e.getCause();
                    e.getMessage();
                }

            }
        }
        return filteredList;
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (chats.get(position).getUserName() != null) {
                holder.name.setText(chats.get(position).getUserName());
                holder.time.setText(chats.get(position).getLastSeen());
                Picasso.with(context).load(chats.get(position).getUserPhoto()).into(holder.image);
            } else {
                holder.itemView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name, time;
            ImageView image;

            ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
                name = (TextView) itemView.findViewById(R.id.room_name);
                time = (TextView) itemView.findViewById(R.id.room_time);
                name.setTypeface(typeface);
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
