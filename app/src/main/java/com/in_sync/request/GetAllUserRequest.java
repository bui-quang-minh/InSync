package com.in_sync.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.in_sync.common.Settings;
import com.in_sync.interfaces.VolleyArrayRequestCallback;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class GetAllUserRequest {
    private static String URL = Settings.URL + Settings.GET_ALL_USERS;
    private static Boolean responseResult = false;
    public static void GetUsersList(Context context, final VolleyArrayRequestCallback callback){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray.length() > 0) {
                    try {
                        callback.onSuccess(jsonArray); // Pass the result to the callback
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage()); // Pass the error to the callback
                    }
                } else {
                    callback.onError("Received empty JSONArray"); // Handle empty array
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Error fetching data");
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMsg = new String(error.networkResponse.data);
                    Log.e("onErrorResponse", "Status code: " + error.networkResponse.statusCode + " Error message: " + errorMsg);
                } else {
                    Log.e("onErrorResponse", "Unexpected error: " + error.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Settings.BEARER_TOKEN);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
