package de.l3s.boilerpipe.sax;

import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gexiaocun on 2017/2/20.
 */
public class AmpImageExtractor extends AbstractSAXParser implements
        ContentHandler {

    private List<String> images = new ArrayList<String>();


    public static final AmpImageExtractor INSTANCE = new AmpImageExtractor();

    /**
     * Returns the singleton instance of {@link ImageExtractor}.
     *
     * @return
     */
    public static AmpImageExtractor getInstance() {
        return INSTANCE;
    }

    public AmpImageExtractor() {
        super(new HTMLConfiguration());
        setContentHandler(this);
    }


    public void endDocument() throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {
        if (StringUtils.equalsIgnoreCase(localName, "amp-img")) {
            try {
                String s = atts.getValue("src");
                String height = atts.getValue("height");
                String width = atts.getValue("width");
                if (Integer.parseInt(height) > 200 && Integer.parseInt(width) > 200) {
                    this.images.add(s);
                }
            }catch(Exception e) {
                System.out.println(e);
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {

    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    public List<String> getImages() {
        return this.images;
    }
}
