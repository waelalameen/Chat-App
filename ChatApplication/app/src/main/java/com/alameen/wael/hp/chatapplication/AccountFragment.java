package com.alameen.wael.hp.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Options> list = new ArrayList<>();

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Jannal.ttf");

        SharedPreferences pref = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        final String userName = pref.getString("userName", "");
        final String userImage = pref.getString("userImage", "");
        String mEmail = pref.getString("email", "");

        ImageView user = (ImageView) view.findViewById(R.id.user_image);
        final TextView name = (TextView) view.findViewById(R.id.user_name);
        TextView email = (TextView) view.findViewById(R.id.email);
        name.setTypeface(typeface);
        email.setTypeface(typeface);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getContext(), ShowUserPhoto.class);
                in.putExtra("userName", userName);
                in.putExtra("userImage", userImage);
                startActivity(in);
            }
        });

        Options op = new Options("Invite Friends to Chat", R.drawable.ic_person_add_black_24dp);
        list.add(op);
        op = new Options("Shared Files and Media", R.drawable.ic_share_black_24dp);
        list.add(op);

        RecyclerView options = (RecyclerView) view.findViewById(R.id.options);
        options.setHasFixedSize(true);
        RecyclerView.Adapter adapter = new OptionsAdapter(list, this);
        options.setLayoutManager(new LinearLayoutManager(getContext()));
        options.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Picasso.with(getContext()).load(userImage).into(user);
        name.setText(userName);
        email.append(" " + mEmail);

        Button signOut = (Button) view.findViewById(R.id.sign_out);
        signOut.setTypeface(typeface);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sign = getActivity().getSharedPreferences("sign", Context.MODE_PRIVATE);
                SharedPreferences.Editor signEditor = sign.edit();
                signEditor.putBoolean("isSignedIn", false);
                signEditor.apply();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });
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
                Snackbar.make(view, "You can't access to your storage files", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    class Options {
        private String name;
        private int image;

        Options(String name, int image) {
            setName(name);
            setImage(image);
        }

        public int getImage() {
            return image;
        }

        private void setImage(int image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }
    }

    private class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {
        private List<Options> list;
        private AdapterView.OnItemClickListener item;

        OptionsAdapter(List<Options> list, AdapterView.OnItemClickListener item) {
            this.list = list;
            this.item = item;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opt_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.optionName.setText(list.get(position).getName());
            holder.optionImage.setImageResource(list.get(position).getImage());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView optionImage;
            TextView optionName;

            ViewHolder(View itemView) {
                super(itemView);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Jannal.ttf");
                optionImage = (ImageView) itemView.findViewById(R.id.opt_image);
                optionName = (TextView) itemView.findViewById(R.id.opt_name);
                optionName.setTypeface(typeface);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                item.onItemClick(null, view, getLayoutPosition(), getItemId());
            }
        }
    }
}
