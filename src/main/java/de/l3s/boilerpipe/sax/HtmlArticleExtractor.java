/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 *       
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.l3s.boilerpipe.sax;

import com.google.gson.Gson;
import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.DocResult;
import de.l3s.boilerpipe.document.TextDocument;
import net.htmlparser.jericho.*;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * an Extractor for extracting an article from an document with its basic HTML structure.
 *
 * @author manuel.codiga@gmail.com
 */
public class HtmlArticleExtractor {
    public static final HtmlArticleExtractor INSTANCE = new HtmlArticleExtractor();

    private static final Set<String> NOT_ALLOWED_HTML_TAGS = new HashSet<String>(Arrays.asList(
            HTMLElementName.HEAD,
            HTMLElementName.HTML,
            HTMLElementName.SCRIPT,
            HTMLElementName.STYLE,
            HTMLElementName.FORM,
            HTMLElementName.BODY,
            HTMLElementName.DIV,
            HTMLElementName.SPAN)
    );

    private HtmlArticleExtractor() {
    }

    /**
     * Returns the singleton instance
     *
     * @return
     */
    public static HtmlArticleExtractor getInstance() {
        return INSTANCE;
    }

    /**
     * returns the article from an url with its basic html structure.
     */
    public String process(final BoilerpipeExtractor extractor, final URL url)
            throws IOException, BoilerpipeProcessingException, SAXException, URISyntaxException {
        HTMLDocument htmlDoc = HTMLFetcher.fetch(url);


        URL ampUrl = getAmpUrl(htmlDoc);
        if (ampUrl != null) {
            htmlDoc = HTMLFetcher.fetch(ampUrl);
        } else {
            return "";
        }


        DocResult result = new DocResult();
        result.ampUrl = ampUrl.toString();
        result.url = url.toString();

        try {
            final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
            hh.setOutputHighlightOnly(true);

            TextDocument doc;
            String text = "";
            doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            extractor.process(doc);

            result.title = doc.getTitle();

            final InputSource is = htmlDoc.toInputSource();
            text = hh.process(doc, is);

            result.content =  removeNotAllowedTags(text, ampUrl.toURI());
            //result.content = process(htmlDoc, ampUrl.toURI(), extractor);

            AmpImageExtractor imagExtractor = AmpImageExtractor.INSTANCE;
            imagExtractor.parse(htmlDoc.toInputSource());

            List<String> images = imagExtractor.getImages();
            if (images.size() > 0) {
                result.image = images.get(0);
            }

        } catch (Exception e) {

        }

        Gson gson = new Gson();
        return gson.toJson(result);
    }


    /**
     * returns the article from an document with its basic html structure.
     *
     * @param HTMLDocument
     * @param URI          the uri from the document for resolving the relative anchors in the document to absolute anchors
     * @return String
     */
    public String process(HTMLDocument htmlDoc, URI docUri, final BoilerpipeExtractor extractor) {

        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
        hh.setOutputHighlightOnly(true);

        TextDocument doc;

        String text = "";
        try {
            doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            extractor.process(doc);
            final InputSource is = htmlDoc.toInputSource();
            text = hh.process(doc, is);
        } catch (Exception ex) {
            return null;
        }

        return removeNotAllowedTags(text, docUri);
    }


    /**
     * returns the amp url from an document
     *
     * @param HTMLDocument
     * @return String
     */

    private URL getAmpUrl(HTMLDocument htmlDoc) throws MalformedURLException {
        final InputSource is = htmlDoc.toInputSource();
        AmpUrlExtractor extractor = AmpUrlExtractor.INSTANCE;

        try {
            extractor.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (StringUtils.isEmpty(extractor.getAmpUrl())) {
            return null;
        }
        return new URL(extractor.getAmpUrl());
    }


    private String removeNotAllowedTags(String htmlFragment, URI docUri) {
        Source source = new Source(htmlFragment);
        OutputDocument outputDocument = new OutputDocument(source);
        List<Element> elements = source.getAllElements();


        for (Element element : elements) {
            Attributes attrs = element.getAttributes();
            Map<String, String> attrsUpdate = outputDocument.replace(attrs, true);
            if (!element.getName().contains("a")) {
                attrsUpdate.clear();
            } else {
                if (attrsUpdate.get("href") != null) {
                    String link = attrsUpdate.get("href");
                    if (!link.contains("http")) {
                        URI documentUri = docUri;

                        URI anchorUri;
                        try {
                            anchorUri = new URI(link);
                            URI result = documentUri.resolve(anchorUri);

                            attrsUpdate.put("href", result.toString());
                        } catch (URISyntaxException e) {
                            outputDocument.remove(element);
                        }
                    }
                }
            }

            if (NOT_ALLOWED_HTML_TAGS.contains(element.getName())) {
                Segment content = element.getContent();
                if (element.getName() == "script"
                        || element.getName() == "style"
                        || element.getName() == "form") {
                    outputDocument.remove(content);
                }
                outputDocument.remove(element.getStartTag());

                if (!element.getStartTag().isSyntacticalEmptyElementTag()) {
                    outputDocument.remove(element.getEndTag());
                }
            }
        }

        String out = outputDocument.toString();
        out = out.replaceAll("\\n", "");
        out = out.replaceAll("\\t", "");

        return out;
    }

}
