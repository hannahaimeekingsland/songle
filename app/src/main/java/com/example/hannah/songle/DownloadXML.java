package com.example.hannah.songle;

import android.os.AsyncTask;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by s1518196 on 31/10/17.
 */

public class DownloadXML extends AsyncTask<String, Void, ArrayList<DownloadXML.Entry>> {

    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            //publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            //publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 5000);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
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
    public ArrayList<DownloadXML.Entry> doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                String stringURL = downloadUrl(url);
                InputStream stream = new ByteArrayInputStream(stringURL.getBytes(StandardCharsets.UTF_8.name()));
                DownloadXML xml_parser = new DownloadXML();
                return xml_parser.parse(stream);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

    private static final String ns = null;
    public static final String unique = "xml";

    public static ArrayList<DownloadXML.Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        System.out.println(">>>>>>> We are in parse");
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private static ArrayList<DownloadXML.Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<DownloadXML.Entry> entries = new ArrayList<DownloadXML.Entry>();
        System.out.println(">>>>>> WE ARE IN READFEED");
        parser.require(XmlPullParser.START_TAG, ns, "Songs");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the Song tag
            if (name.equals("Song")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final String title;
        public final String link;
        public final String number;
        public final String artist;

        private Entry(String number, String title, String artist, String link) {
            this.number = number;
            this.title = title;
            this.artist = artist;
            this.link = link;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private static DownloadXML.Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Song");
        String number = null;
        String title = null;
        String artist = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Title")) {
                title = readTitle(parser);
            } else if (name.equals("Artist")) {
                artist = readArtist(parser);
            } else if (name.equals("Link")) {
                link = readLink(parser);
            } else if (name.equals("Number")){
                number = readNumber(parser);
            } else {
                skip(parser);
                System.out.println(">>>>>>>>>>>skipped in readEntry");

            }
        }
        return new DownloadXML.Entry(number, title, artist, link);
    }


    private static String readNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
        System.out.println(">>>>>>>>>>" + "readNumber");
        parser.require(XmlPullParser.START_TAG, ns, "Number");
        String number = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Number");
        System.out.println(">>>>>>>>>>>number" + number);
        return number;
    }

    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        System.out.println(">>>>>>>>>>" + "readTitle");
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
        System.out.println(">>>>>>>>>>>title" + title);
        return title;
    }

    // Processes link tags in the feed.
    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        System.out.println(">>>>>>>>>>" + "readLink");
        parser.require(XmlPullParser.START_TAG, ns, "Link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Link");
        System.out.println(">>>>>>>>>>>Link" + link);
        return link;
    }

    // Processes artist tags in the feed.
    private static String readArtist(XmlPullParser parser) throws IOException, XmlPullParserException {
        System.out.println(">>>>>>>>>>" + "readArtist");
        parser.require(XmlPullParser.START_TAG, ns, "Artist");
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Artist");
        System.out.println(">>>>>>>>>>>Artist" + artist);
        return artist;
    }

    // For the tags title and artist, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        System.out.println(">>>>>>>>>>" + "readTitle");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        System.out.println(">>>>>>>>>>>result" + result);
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
