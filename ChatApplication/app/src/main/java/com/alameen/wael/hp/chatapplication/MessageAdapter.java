package com.alameen.wael.hp.chatapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Messages> messagesList;
    private Context context;
    public static int NUMBER_OF_MESSAGES;

    MessageAdapter(Context context, List<Messages> messages_list) {
        LayoutInflater.from(context);
        this.messagesList = messages_list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setTextMessage(messagesList.get(position).getTextMessage(), holder.itemView);
    }

    @Override
    public int getItemCount() {
        NUMBER_OF_MESSAGES = messagesList.size();
        return messagesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView rightText, leftText, rightTime, leftTime;

        ViewHolder(View itemView) {
            super(itemView);
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Jannal.ttf");
            rightText = (TextView) itemView.findViewById(R.id.right_message);
            leftText = (TextView) itemView.findViewById(R.id.left_message);
            rightTime = (TextView) itemView.findViewById(R.id.right_time);
            leftTime = (TextView) itemView.findViewById(R.id.left_time);
            rightTime.setTypeface(typeface);
            leftTime.setTypeface(typeface);
        }

        void setTextMessage(String text, View itemView) {
            int length = messagesList.get(getLayoutPosition()).getLength();
            String status = messagesList.get(getLayoutPosition()).getStatus();
            RelativeLayout relativeLayoutRight = (RelativeLayout) itemView.findViewById(R.id.rel1);
            RelativeLayout relativeLayoutLeft = (RelativeLayout) itemView.findViewById(R.id.rel2);

            if (rightText == null || text == null)
                return;
            else {
                if (status.equals("sender")) {
                    if (length > 50) {
                        rightText.setMaxWidth(500);
                        rightText.setText(text);
                        rightTime.setText(messagesList.get(getLayoutPosition()).getTime());
                    } else {
                        rightText.setText(text);
                        rightTime.setText(messagesList.get(getLayoutPosition()).getTime());
                    }
                    relativeLayoutRight.setVisibility(View.VISIBLE);
                    relativeLayoutLeft.setVisibility(View.INVISIBLE);
                } else {
                    if (length > 50) {
                        leftText.setMaxWidth(500);
                        leftText.setText(text);
                        leftTime.setText(messagesList.get(getLayoutPosition()).getTime());
                    } else {
                        leftText.setText(text);
                        leftTime.setText(messagesList.get(getLayoutPosition()).getTime());
                    }
                    relativeLayoutLeft.setVisibility(View.VISIBLE);
                    relativeLayoutRight.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
