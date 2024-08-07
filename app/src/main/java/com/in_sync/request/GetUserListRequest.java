package com.in_sync.request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.in_sync.common.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetUserListRequest{
    private static String URL = Settings.URL + Settings.GET_ALL_USERS;
    private static Boolean responseResult = false;
    public static boolean GetLoginParameter(String username, Context context, TextInputLayout usernameLayout, TextInputLayout passwordLayout){

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL + username, null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                responseResult = processResponse(jsonArray, context, usernameLayout, passwordLayout);
                Log.d("onResponse", jsonArray.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        return responseResult;
    }

    private static boolean processResponse(JSONArray jsonArray, Context context, TextInputLayout usernameLayout, TextInputLayout passwordLayout) {
        if (1 != 1){
            Toast.makeText(context, "Login success", Toast.LENGTH_LONG).show();
            return true;
        } else{
            usernameLayout.setError("Invalid username");
            passwordLayout.setError("Invalid password");
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}