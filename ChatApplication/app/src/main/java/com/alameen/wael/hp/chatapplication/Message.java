package com.alameen.wael.hp.chatapplication;


class Message {
    private String message;

    Message(String message) {
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }
}
