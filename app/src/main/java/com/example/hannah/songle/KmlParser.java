package com.example.hannah.songle;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by s1518196 on 29/10/17.
 */

public class KmlParser {

    private static final String ns = null;

    public static ArrayList<Point> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private static ArrayList<KmlParser.Point> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<KmlParser.Point> points = new ArrayList<KmlParser.Point>();

        parser.require(XmlPullParser.START_TAG, ns, "Document");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the Placemark tag
            if (name.equals("Placemark")) {
                points.add(readPoint(parser));
            } else {
                skip(parser);
            }
        }
        return points;
    }

    public static class Point {
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
    }

    // Parses the contents of a Point. If it encounters a name, description, styleurl, or coordinates
    // tag, hands them off to their respective "read" methods for processing. Otherwise, skips the tag.
    private static KmlParser.Point readPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Placemark");
        String name = null;
        String description = null;
        String styleurl = null;
        String coordinates = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String n = parser.getName();
            if (n.equals("name")) {
                name = readName(parser);
            } else if (n.equals("description")) {
                description = readDescription(parser);
            } else if (n.equals("styleUrl")) {
                styleurl = readStyleurl(parser);
            } else if (n.equals("Point")){
                coordinates = readP(parser);
            } else {
                skip(parser);
            }
        }
        return new KmlParser.Point(name, description, styleurl, coordinates);
    }

    private static String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    // Processes title tags in the feed.
    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    // Processes link tags in the feed.
    private static String readStyleurl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "styleUrl");
        String styleUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "styleUrl");
        return styleUrl;
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
            if (n.equals("Point")) {
                parser.require(XmlPullParser.START_TAG, ns, "coordinates");
                coordinates = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "coordinates");
                return coordinates;
            } else {
                skip(parser);
            }
        }
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
