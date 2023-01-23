package com.abheekd.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

public class WebSrc {
    public String getUrl() {
        return url;
    }

    public Object getData() {
        return data;
    }

    private String url;
    private Object data;

    public WebSrc(String url) {
        this.url = url;
        this.data = null;
    }

    public void fetch() throws IOException {
        URL url = new URL(this.url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Getting the response code
        int responsecode = conn.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {
            try (InputStream stream = url.openStream()) {
                Scanner sc = new Scanner(stream).useDelimiter("\\A");
                String result = sc.hasNext() ? sc.next() : "";

                JSONTokener tokener = new JSONTokener(result);
                if (result.charAt(0) == '{') {
                    data = new JSONObject(tokener);
                } else if (result.charAt(0) == '[') {
                    data = new JSONArray(tokener);
                } else if (result.charAt(0) == '<') {
                    data = XML.toJSONObject(result);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public Object toObjWithKey(String ctorName, String key, String... params) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> cl = Class.forName(ctorName);

        Object[] args = new Object[params.length];

        JSONObject obj = (JSONObject)((JSONObject)data).get(key);

        for (int i = 0; i < args.length; i++) {
            args[i] = obj.get(params[i]);
        }

        Class<?>[] argTypes = new Class<?>[args.length];
        
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        Constructor<?> ctor = cl.getDeclaredConstructor(argTypes);
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    public Object toObj(String ctorName, String... params) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> cl = Class.forName(ctorName);

        Object[] args = new Class<?>[params.length];

        for (int i = 0; i < args.length; i++) {
            args[i] = ((JSONObject)data).get(params[i]);
        }

        Class<?>[] argTypes = new Class<?>[args.length];
        
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        Constructor<?> ctor = cl.getDeclaredConstructor(argTypes);
        return ctor.newInstance(args);
    }
}
