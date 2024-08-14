package com.in_sync.interfaces;

import org.json.JSONArray;

public interface VolleyArrayRequestCallback {
    void onSuccess(JSONArray result);
    void onError(String error);
}