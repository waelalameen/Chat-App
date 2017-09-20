package com.alameen.wael.hp.chatapplication;

class Chats {
    private String userName, userPhoto, token, lastSeen, lastMessage, lastTime;

    Chats(String userName, String userImage, String token, String lastMessage, String lastTime) {
        setUserName(userName);
        setUserPhoto(userImage);
        setToken(token);
        setLastMessage(lastMessage);
        setLastTime(lastTime);
    }

    Chats(String name, String image, String lastSeen, String token, int id) {
        setUserName(name);
        setUserPhoto(image);
        setLastSeen(lastSeen);
        setToken(token);
    }

    String getUserPhoto() {
        return userPhoto;
    }

    private void setUserPhoto(String chatImage) {
        this.userPhoto = chatImage;
    }

    String getUserName() {
        return userName;
    }

    private void setUserName(String chatName) {
        this.userName = chatName;
    }

    String getLastSeen() {
        return lastSeen;
    }

    private void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    String getLastMessage() {
        return lastMessage;
    }

    private void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    String getLastTime() {
        return lastTime;
    }

    private void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
