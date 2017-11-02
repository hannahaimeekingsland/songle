package com.example.hannah.songle;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

/**
 * Created by s1518196 on 01/11/17.
 */

public class DownloadKml extends AsyncTask<String, Void, ArrayList<DownloadKml.Point>> implements Serializable {
    @Override
    protected ArrayList<Point> doInBackground(String... urls) {
        //System.out.println(">>>>>>>>>>>>>> In doInBackground");
        try {
            return loadKmlFromNetwork(urls[0]);
        } catch (IOException e) {
            return null;
        } catch (XmlPullParserException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Point> result) {
        // Do something with result
    }

    //Method loadXmlFromNetwork, returns a string
    private ArrayList<Point> loadKmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        ArrayList<Point> result = new ArrayList<Point>();
        String streamString = downloadUrl(urlString);
        InputStream stream = new ByteArrayInputStream(streamString.getBytes(StandardCharsets.UTF_8.name()));
        // Do something with stream e.g. parse as XML, build result
        result = parse(stream);
        for(Point point : result){
            Log.e("success", point.toString());
        }
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
        InputStream stream = conn.getInputStream();
        try {
            result = readStream(stream, 2000000);
        } finally {
            stream.close();
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

    private static final String ns = null;
    public static String unique = "kml";

    public ArrayList<Point> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<DownloadKml.Point> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<DownloadKml.Point> points = new ArrayList<DownloadKml.Point>();
        System.out.println(">>>>>>>>>>>>>>>>> in readFeed");
        parser.require(XmlPullParser.START_TAG, ns, "kml");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            Log.e("kml", parser.getName() + " past start tag");
            String name = parser.getName();
            // Starts by looking for the Placemark tag
            if (name.equals("Placemark")) {
                System.out.println(">>>>>>>>>>>>>>>>> in readFeed4");
                points.add(readPoint(parser));
            }
            else if(name.equals("Document")){
            }
            else {
                Log.e("skip", name);
                skip(parser);
            }
        }
        Log.e("points", points.toString());
        return points;
    }

    public static class Point implements Parcelable{
        //variables here
        public final String name;
        public final String description;
        public final String styleurl;
        public final String coordinates;

        private Point(String name, String description, String styleurl, String coordinates) {
            this.name = name;
            this.description = description;
            this.styleurl = styleurl;
            this.coordinates = coordinates;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(name);
            out.writeString(description);
            out.writeString(styleurl);
            out.writeString(coordinates);
        }

        public static final Parcelable.Creator<Point> CREATOR
                = new Parcelable.Creator<Point>() {
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            public Point[] newArray(int size) {
                return new Point[size];
            }
        };

        private Point(Parcel in) {
            name = in.readString();
            description = in.readString();
            styleurl = in.readString();
            coordinates = in.readString();
        }
    }

    // Parses the contents of a Point. If it encounters a name, description, styleurl, or coordinates
    // tag, hands them off to their respective "read" methods for processing. Otherwise, skips the tag.
    private Point readPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Placemark");
        String name = null;
        String description = null;
        String styleurl = null;
        System.out.println(">>>>>>>>>>>>>>>>> readplacemark ");
        String coordinates = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String n = parser.getName();
            if (n.equals("name")) {
                System.out.println(">>>>>>>>>>>>>>>>> readName ");
                name = readName(parser);
            } else if (n.equals("description")) {
                System.out.println(">>>>>>>>>>>>>>>>> readDescription ");
                description = readDescription(parser);
            } else if (n.equals("styleUrl")) {
                System.out.println(">>>>>>>>>>>>>>>>> readstyle ");
                styleurl = readStyleurl(parser);
            } else if (n.equals("Point")) {
                System.out.println(">>>>>>>>>>>>>>>>> readpoint");
                coordinates = readP(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Point(name, description, styleurl, coordinates);
    }

    private static String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        System.out.println(">>>>>>>>>>>>>>>>> readName " + name);
        return name;
    }

    // Processes title tags in the feed.
    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        System.out.println(">>>>>>>>>>>>>>>>> readDescription " + description);
        return description;
    }

    // Processes link tags in the feed.
    private static String readStyleurl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "styleUrl");
        String styleurl = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "styleUrl");
        System.out.println(">>>>>>>>>>>>>>>>> styleUrl " + styleurl);
        return styleurl;
    }

    // Processes artist tags in the feed.
    private static String readP(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coordinates = "";
        parser.require(XmlPullParser.START_TAG, ns, "Point");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String n = parser.getName();
            Log.e("coordinates", n);
            if (n.equals("coordinates")) {
                coordinates = readText(parser);
            } else {
                skip(parser);
            }
        }
        System.out.println(">>>>>>>>>>>>>>>>> coordinates " + coordinates);
        return coordinates;
    }

    // For the tags title and artist, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
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
