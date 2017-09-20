package com.alameen.wael.hp.chatapplication;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class MySingleton {
    private Context context;
    private static MySingleton ourInstance;
    private RequestQueue requestQueue;

    private MySingleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    synchronized static MySingleton getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MySingleton(context);
        }
        return ourInstance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
