package com.example.hannah.songle;

/**
 * Created by s1518196 on 28/10/17.
 */
import android.content.Context;
import android.util.Xml;

import com.google.android.gms.maps.GoogleMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class KmlLayer {

    /**
     * Document class allows for users to input their KML data and output it onto the map
     */
        /**
         * Creates a new KmlLayer object - addLayerToMap() must be called to trigger rendering onto a map.
         *
         * @param map        GoogleMap object
         * @param resourceId Raw resource KML file
         * @param context    Context object
         * @throws XmlPullParserException if file cannot be parsed
         */
        public KmlLayer(GoogleMap map, int resourceId, Context context)
                throws XmlPullParserException, IOException {
            this(map, context.getResources().openRawResource(resourceId), context);
        }

        /**
         * Creates a new KmlLayer object
         *
         * @param map    GoogleMap object
         * @param stream InputStream containing KML file
         * @throws XmlPullParserException if file cannot be parsed
         */
        public KmlLayer(GoogleMap map, InputStream stream, Context context)
                throws XmlPullParserException, IOException {
            if (stream == null) {
                throw new IllegalArgumentException("KML InputStream cannot be null");
            }
            KmlRenderer mRenderer = new KmlRenderer(map, context);
            XmlPullParser xmlPullParser = createXmlParser(stream);
            KmlParser parser = new KmlParser(xmlPullParser);
            parser.parseKml();
            stream.close();
            mRenderer.storeKmlData(parser.getStyles(), parser.getStyleMaps(), parser.getPlacemarks(),
                    parser.getContainers(), parser.getGroundOverlays());
            storeRenderer(mRenderer);
        }

        /**
         * Creates a new XmlPullParser to allow for the KML file to be parsed
         *
         * @param stream InputStream containing KML file
         * @return XmlPullParser containing the KML file
         * @throws XmlPullParserException if KML file cannot be parsed
         */
        private static XmlPullParser createXmlParser(InputStream stream) throws XmlPullParserException {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(stream, null);
            return parser;
        }

        /**
         * Adds the KML data to the map
         */
        public void addLayerToMap() throws IOException, XmlPullParserException {
            super.addKMLToMap();
        }

        /**
         * Checks if the layer contains placemarks
         *
         * @return true if there are placemarks, false otherwise
         */
        public boolean hasPlacemarks() {
            return hasFeatures();
        }

        /**
         * Gets an iterable of KmlPlacemark objects
         *
         * @return iterable of KmlPlacemark objects
         */
        public Iterable<KmlPlacemark> getPlacemarks() {
            return (Iterable<KmlPlacemark>) getFeatures();
        }

        /**
         * Checks if the layer contains any KmlContainers
         *
         * @return true if there is at least 1 container within the KmlLayer, false otherwise
         */
        public boolean hasContainers() {
            return super.hasContainers();
        }

        /**
         * Gets an iterable of KmlContainerInterface objects
         *
         * @return iterable of KmlContainerInterface objects
         */
        public Iterable<KmlContainer> getContainers() {
            return super.getContainers();
        }

        /**
         * Gets an iterable of KmlGroundOverlay objects
         *
         * @return iterable of KmlGroundOverlay objects
         */
        public Iterable<KmlGroundOverlay> getGroundOverlays() {
            return super.getGroundOverlays();
        }

    }

    /*private static final String ns = null;

    public static ArrayList<KmlParser.Point> parse(InputStream in) throws XmlPullParserException, IOException {
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
            // Starts by looking for the Style tag
            if (name.equals("Style")) {
                //PARSE id
                points.add(readPoint(parser));
            } else {
                skip(parser);
            }
        }
        return points;
    }

    public static class Point {
        //variables here
        public final String styleid;
        public final String scale;
        public final String icon;

        private Point(String styleid, String scale, String icon) {
            this.styleid = styleid;
            this.scale = scale;
            this.icon = icon;
        }
    }

    // Parses the contents of an Point. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private static KmlParser.Point readPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Style");
        String styleid = null;
        String scale = null;
        String icon = null;
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
        return new KmlParser.Point(number, title, artist, link);
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
    }*/



}
