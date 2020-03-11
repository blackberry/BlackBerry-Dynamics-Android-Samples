/* Copyright (c) 2017 - 2020 BlackBerry Limited.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package com.good.automated.test.screenFinder.parsing;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing UI XML map.
 * https://developer.android.com/training/basics/network-ops/xml
 */
public class Parser {

    private final String TAG = this.getClass().getSimpleName();
    private static final String NS = null;

    /**
     * Parses an XML InputStream to the {@link List} of {@link Node} objects.
     *
     * @param in            {@link InputStream} with the content of the XML dump
     * @return              list of xml nodes as corresponding objects
     * @throws IOException  in case of InputStream closure failure
     */
    public List<Node> parse(InputStream in) throws IOException {

        List<Node> nodes = new ArrayList<>();

        // TODO: 12/14/18 replace with try-with-resources once Java 1.8 is available
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            nodes = readUIXml(parser);
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "Failed to parse InputStream to an XML!", e);
        } finally {
            in.close();
        }
        return nodes;
    }

    /**
     * Parses an XML to the {@link List} of {@link Node} objects.
     *
     * @param parser                    {@link XmlPullParser} parser with the current xml read
     * @return                          list of xml nodes as corresponding objects
     * @throws IOException              if <hierarchy> start tag was not found
     * @throws XmlPullParserException   if xml fails to be parsed
     */
    private List<Node> readUIXml(XmlPullParser parser) throws IOException, XmlPullParserException {

        List<Node> nodes = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, NS, "hierarchy");

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals("node")) {
                nodes.add(readNode(parser));
            } else {
                skip(parser);
            }
        }

        return nodes;
    }

    /**
     * Reads current parser node to a {@link Node} object.
     * Node object contains next xml tag attributes:
     *  - package
     *  - resource-id
     *  - text
     *
     * @param parser                    {@link XmlPullParser} parser with the current xml read
     * @return                          {@link Node} object of the xml node read
     * @throws IOException              if <node> start tag was not found
     * @throws XmlPullParserException   if xml fails to be parsed
     */
    private Node readNode(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, NS, "node");
        String nodeResourceId = parser.getAttributeValue(null, "resource-id");
        String nodePackage = parser.getAttributeValue(null, "package");
        String nodeText = parser.getAttributeValue(null, "text");

        return new Node(nodeResourceId, nodePackage, nodeText);

    }

    /**
     * Skips current XML tag from parsing.
     *
     * @param parser                    {@link XmlPullParser} parser with the current xml read
     * @throws XmlPullParserException   if xml fails to be parsed
     * @throws IOException              if start tag was not found
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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


    /**
     * Class that represents an XML tag <node>.
     * Currently contains only next node attributes:
     *  - package
     *  - resource-id
     *  - text
     * Could be expanded if needed.
     */
    private static class Node {

        private String nResourceId;
        private String nPackage;
        private String nText;

        private Node(String nodeResourceId, String nodePackage, String nodeText) {
            this.nResourceId = nodeResourceId;
            this.nPackage = nodePackage;
            this.nText = nodeText;
        }

        public String getnResourceId() {
            return nResourceId;
        }

        public void setnResourceId(String nResourceId) {
            this.nResourceId = nResourceId;
        }

        public String getnPackage() {
            return nPackage;
        }

        public void setnPackage(String nPackage) {
            this.nPackage = nPackage;
        }

        public String getnText() {
            return nText;
        }

        public void setnText(String nText) {
            this.nText = nText;
        }
    }

}
