package com.in_sync.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.in_sync.common.Settings;
import com.in_sync.interfaces.VolleyArrayRequestCallback;
import com.in_sync.interfaces.VolleyObjectRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class ValidatePasswordRequest {
    public static void ValidatePassword(String userId, String password, JSONArray data, Context context, final VolleyObjectRequestCallback callback){
        // Create a new json
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("password", password);
        }catch (JSONException e){
            Log.e("ValidatePassword: ", "Error creating JSON object");
        }
        JsonObjectRequest verificationRequest = new JsonObjectRequest(Request.Method.POST, Settings.URL + Settings.VERIFY_PASSWORD_HEAD + userId + Settings.VERIFY_PASSWORD_TAIL, jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.length() > 0) {
                    try {

                        callback.onSuccess(jsonObject, data); // Pass the result to the callback
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage()); // Pass the error to the callback
                    }
                } else {
                    callback.onError("Received empty jsonObject"); // Handle empty array
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString()); // Handle empty array
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
        queue.add(verificationRequest);
    }
}
