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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DownloadXml extends AsyncTask<String, Void, ArrayList<DownloadXml.Entry>> {

    @Override
    protected ArrayList<Entry> doInBackground(String... urls) {
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            return null;
        } catch (XmlPullParserException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Entry> result) {
        // Do something with result
    }

    //Method loadXmlFromNetwork, returns a string
    private ArrayList<Entry> loadXmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        //System.out.println(">>>>>>>>>>>>>> In loadXmlFromNetwork");
        ArrayList<Entry> result = new ArrayList<Entry>();
        String streamString = downloadUrl(urlString);
        InputStream stream = new ByteArrayInputStream(streamString.getBytes(StandardCharsets.UTF_8));
    // Do something with stream e.g. parse as XML, build result
        result = parse(stream);
        return result;
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

    private static final String ns = null;

    private static ArrayList<DownloadXml.Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        //System.out.println(">>>>>>> We are in parse");
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

    private static ArrayList<DownloadXml.Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<DownloadXml.Entry> entries = new ArrayList<>();
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

    static class Entry {
        final String title;
        final String link;
        final String number;
        final String artist;

        private Entry(String number, String title, String artist, String link) {
            this.number = number;
            this.title = title;
            this.artist = artist;
            this.link = link;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private static DownloadXml.Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
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
            }
        }
        return new DownloadXml.Entry(number, title, artist, link);
    }


    private static String readNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
//        System.out.println(">>>>>>>>>>" + "readNumber");
        parser.require(XmlPullParser.START_TAG, ns, "Number");
        String number = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Number");
//        System.out.println(">>>>>>>>>>>number" + number);
        return number;
    }

    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
//        System.out.println(">>>>>>>>>>" + "readTitle");
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
//        System.out.println(">>>>>>>>>>>title" + title);
        return title;
    }

    // Processes link tags in the feed.
    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
//        System.out.println(">>>>>>>>>>" + "readLink");
        parser.require(XmlPullParser.START_TAG, ns, "Link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Link");
//        System.out.println(">>>>>>>>>>>Link" + link);
        return link;
    }

    // Processes artist tags in the feed.
    private static String readArtist(XmlPullParser parser) throws IOException, XmlPullParserException {
//        System.out.println(">>>>>>>>>>" + "readArtist");
        parser.require(XmlPullParser.START_TAG, ns, "Artist");
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Artist");
//        System.out.println(">>>>>>>>>>>Artist" + artist);
        return artist;
    }

    // For the tags title and artist, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
//        System.out.println(">>>>>>>>>>" + "readTitle");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
//        System.out.println(">>>>>>>>>>>result" + result);
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
