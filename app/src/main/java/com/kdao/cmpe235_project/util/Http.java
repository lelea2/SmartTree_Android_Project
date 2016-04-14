package com.kdao.cmpe235_project.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {

    /**
     * GET request return stringify JSON object
     * @param httpUrl
     * @return
     * @throws IOException
     */
    public String read(String httpUrl) throws IOException {
        Log.i(">>>>Http<<<<", "Calling http read class");
        String httpData = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            httpData = stringBuffer.toString();
            Log.i("httpData", httpData);
            bufferedReader.close();
        } catch (Exception e) {
            Log.e("http err:", e.toString());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return httpData;
    }

    /**
     * GET request to download image
     * @param strUrl
     * @return
     * @throws IOException
     */
    public Bitmap downloadImage(String strUrl) throws IOException {
        Bitmap bitmap=null;
        InputStream iStream = null;
        try {
            URL url = new URL(strUrl);
            /** Creating an http connection to communicate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            /** Connecting to url */
            urlConnection.connect();
            /** Reading data from url */
            iStream = urlConnection.getInputStream();
            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);
        } catch(Exception e) {
            Log.d("url exception", e.toString());
        } finally {
            iStream.close();
        }
        return bitmap;
    }

}

