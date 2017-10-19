package com.example.hannah.songle;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

    /**
     * Created by s1518196 on 18/10/17.
     */

    public class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "Unable to load content.Check your network connection";
            } catch (XmlPullParserException e) {
                return "Error parsing XML";
            }
        }

        //Method loadXmlFromNetwork, returns a string
        private String loadXmlFromNetwork(String urlString) throws
                XmlPullParserException, IOException {
            StringBuilder result = new StringBuilder();
            try (InputStream stream = downloadUrl(urlString)) {
                // Do something with stream e.g. parse as XML, build result
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Do something with result
        }

        //Method downloadUrl, returns an input stream
        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Also available: HttpsURLConnection
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }
    }

