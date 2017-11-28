package com.example.hannah.songle;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by s1518196 on 28/11/17.
 */

public class DownloadLyrics extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        //System.out.println(">>>>>>>>>>>>>> In doInBackground");
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            return null;
        } catch (XmlPullParserException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Do something with result
        //System.out.println(">>>>>>>>>>>>>> In onPostExecute");
    }

    //Method loadXmlFromNetwork, returns a string
    private String loadXmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        //System.out.println(">>>>>>>>>>>>>> In loadXmlFromNetwork");
        //String result = "";
        //Log.e("urlString", urlString);
        String streamString = downloadUrl(urlString);
        InputStream stream = new ByteArrayInputStream(streamString.getBytes(StandardCharsets.UTF_8));
        Scanner sc = new Scanner(stream).useDelimiter("\\z");
            if (sc.hasNext()) {
                return sc.next();
            } else {
                return "";
            }

//        BufferedReader br = null;
//        StringBuilder sb = new StringBuilder();
//        String line;
//        try {
//            br = new BufferedReader(new InputStreamReader(stream));
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        result = sb.toString();
//        return result;
    }


    //Method downloadUrl, returns an input stream
    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private String downloadUrl(String urlString) throws IOException {
        String result = null;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        try {
            result = readStream(conn.getInputStream(),2000000);
        }
        finally {
            conn.getInputStream().close();
            conn.disconnect();
        }
        return result;
    }

    public String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

}
