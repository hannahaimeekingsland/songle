package com.example.hannah.songle;

import android.os.AsyncTask;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class DownloadLyrics extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
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
    }

    //Method loadXmlFromNetwork, returns a String
    private String loadXmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        //Log.e("urlString", urlString);
        String streamString = downloadUrl(urlString);
        InputStream stream = new ByteArrayInputStream(streamString.getBytes(StandardCharsets.UTF_8));
        // Build String from input string, using a delimiter for the end of the input
        Scanner sc = new Scanner(stream).useDelimiter("\\z");
        if (sc.hasNext()) {
            return sc.next();
        } else {
            return "";
        }
    }


    // Method downloadUrl, returns a String
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

    private String readStream(InputStream stream, int maxReadSize)
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
