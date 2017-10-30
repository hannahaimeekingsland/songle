package com.example.hannah.songle;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s1518196 on 23/10/17.
 */

public class XmlParser {
    private static final String ns = null;
    public static final String unique = "xml";

    public static ArrayList<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private static ArrayList<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Entry> entries = new ArrayList<Entry>();

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
    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
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
        return new Entry(number, title, artist, link);
    }

    private static String readNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Number");
        String number = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Number");
        return number;
    }

    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
        return title;
    }

    // Processes link tags in the feed.
    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "Link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("Link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "Link");
        return link;
    }

    // Processes artist tags in the feed.
    private static String readArtist(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Artist");
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Artist");
        return artist;
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
