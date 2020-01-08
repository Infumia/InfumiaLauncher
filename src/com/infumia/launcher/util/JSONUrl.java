package com.infumia.launcher.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class JSONUrl {

    public static JSONObject readURL(String jsonUrl) throws IOException {
        BufferedReader reader = null;
        try{
            URL url = new URL(jsonUrl);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars,0,read);

            JSONObject object = new JSONObject(buffer.toString());
            return object;
        }finally {
            if(reader != null){
                reader.close();
            }
        }
    }

}
