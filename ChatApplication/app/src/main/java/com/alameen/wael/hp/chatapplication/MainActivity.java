package com.alameen.wael.hp.chatapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private DrawerLayout drawer;
    private List<NavItems> items = new ArrayList<>();
    private FloatingActionButton fab;
    public static String isInChat = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(this);
            getInstance().setPersistenceEnabled(true);
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Search.class));
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tab = (TabLayout) findViewById(R.id.main_tab);
        viewPager.setOffscreenPageLimit(2);
        Pager viewPagerAdapter = new Pager(getSupportFragmentManager());
        viewPagerAdapter.add(new ChatsFragment(), getString(R.string.chats));
        viewPagerAdapter.add(new AccountFragment(), getString(R.string.my_account));
        viewPager.setAdapter(viewPagerAdapter);
        tab.setupWithViewPager(viewPager, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("position", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        int count = 0;
        NavItems navItems;
        String[] names = getResources().getStringArray(R.array.item_names);
        for (String name : names) {
            if (count == 0) {
                navItems = new NavItems(name, R.drawable.ic_person_add_black_24dp);
                items.add(navItems);
                count++;
            } else if (count == 1) {
                navItems = new NavItems(name, R.drawable.ic_build_black_24dp);
                items.add(navItems);
                count++;
            } else {
                navItems = new NavItems(name, R.drawable.ic_phone_android_black_24dp);
                items.add(navItems);
                count++;
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.nav);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter navAdapter = new NavMenuAdapter(this, items, this);
        recyclerView.setAdapter(navAdapter);
        navAdapter.notifyDataSetChanged();

        ViewGroup vg = (ViewGroup) tab.getChildAt(0);
        for (int i = 0; i < vg.getChildCount(); i++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            for (int j = 0; j < vgTab.getChildCount(); j++) {
                View tabViewChild = vgTab.getChildAt(j);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        moveTaskToBack(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                String message = "Download Chat App Now\nfrom Google Play Store and get connected to you friends and people\n";
                Intent in = new Intent(Intent.ACTION_SEND);
                in.setType("text/plain");
                in.putExtra(Intent.EXTRA_SUBJECT, "Chat");
                //String googlePlayLink = message + "https://play.google.com/store/apps/details?id=com.wael.alameen.santaland&hl=en\n\n";
                in.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(in, "choose one"));
                break;
            case 1:
                //startActivity(new Intent(this, Settings.class));
                break;
            case 2:
                startActivity(new Intent(this, About.class));
                break;
            default:
                break;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}
