package com.alameen.wael.hp.chatapplication;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View itemView;
    public onChatRoomClickListener listener;

    public ViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.itemView.setOnClickListener(this);
    }

    public void setChatName(String s) {
        TextView roomName = (TextView) itemView.findViewById(R.id.room_name);
        Typeface typeface = Typeface.createFromAsset(ChatsFragment.context.getAssets(), "Jannal.ttf");
        roomName.setTypeface(typeface);
        roomName.setText(s);
    }

    public void setChatImage(String s) {
        CircleImageView roomImage = (CircleImageView) itemView.findViewById(R.id.room_image);
        Picasso.with(ChatsFragment.context).load(s).into(roomImage);
    }

    public void setOnChatRoomClickListener(onChatRoomClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onChatRoomClick(view, getLayoutPosition());
        }
    }

    public interface onChatRoomClickListener {
        void onChatRoomClick(View view, int position);
    }
}
