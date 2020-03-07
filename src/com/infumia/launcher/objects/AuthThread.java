/**
 *    Copyright 2019-2020 Infumia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.infumia.launcher.objects;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class AuthThread extends Thread {

    String username;
    String password;
    Callback callback;

    public AuthThread(String username, String password, Callback callback) {
        this.username = username;
        this.password = password;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("https://authserver.mojang.com/authenticate");
            String content = MakeJSONRequest(username, password);
            byte[] contentBytes = content.getBytes("UTF-8");

            URLConnection connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(contentBytes.length));

            OutputStream requestStream = connection.getOutputStream();
            requestStream.write(contentBytes, 0, contentBytes.length);
            requestStream.close();
            BufferedReader responseStream;
            if (((HttpURLConnection) connection).

                    getResponseCode() == 200) {
                responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                responseStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getErrorStream(), "UTF-8"));
            }

            if (((HttpURLConnection) connection).getResponseCode() != 200) {
                callback.response("-1");
                responseStream.close();
                return;
            }

            callback.response(responseStream.readLine());
            responseStream.close();

        }catch (Exception ex) {
            ex.printStackTrace();
            callback.response("-1");
        }
    }

    private static String MakeJSONRequest(String username, String password){
        JSONObject json1 = new JSONObject();
        json1.put("name", "Minecraft");
        json1.put("version", 1);
        JSONObject json = new JSONObject();
        json.put("agent", json1);
        json.put("username", username);
        json.put("password", password);
        json.put("clientToken", "");

        return json.toString();
    }
}
