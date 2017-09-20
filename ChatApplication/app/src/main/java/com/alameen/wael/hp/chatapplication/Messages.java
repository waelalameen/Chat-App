package com.alameen.wael.hp.chatapplication;


import android.graphics.Bitmap;

class Messages {

    private String text, status, time;
    private Bitmap image;
    private int length;

    String getTextMessage() {
        return text;
    }

    Bitmap getImageMessage() {
        return image;
    }

    String getStatus() {
        return status;
    }

    int getLength() {
        return length;
    }

    public String getTime() {
        return time;
    }

    public static class Builder {
        private String textMessage, status, time;
        private Bitmap imageMessage;
        private int length;

        public Builder(String status) {
            this.status = status;
        }

        public Builder getText(String textMessage) {
            this.textMessage = textMessage;
            return this;
        }

        public Builder getImage(Bitmap imageMessage) {
            this.imageMessage = imageMessage;
            return this;
        }

        public Builder getLength(int length) {
            this.length = length;
            return this;
        }

        public Builder getTime(String time) {
            this.time = time;
            return this;
        }

        public Messages build() {
            Messages m = new Messages();
            m.text = textMessage;
            m.image = imageMessage;
            m.length = length;
            m.status = status;
            m.time = time;
            return m;
        }
    }
}
