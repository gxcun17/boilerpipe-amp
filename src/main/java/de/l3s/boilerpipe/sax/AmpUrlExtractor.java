package de.l3s.boilerpipe.sax;

import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Created by gexiaocun on 2017/2/20.
 */


public class AmpUrlExtractor extends AbstractSAXParser implements
        ContentHandler {


    private String ampUrl = null;

    public static final AmpUrlExtractor INSTANCE = new AmpUrlExtractor();

    /**
     * Returns the singleton instance of {@link ImageExtractor}.
     *
     * @return
     */
    public static AmpUrlExtractor getInstance() {
        return INSTANCE;
    }

    public AmpUrlExtractor() {
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
        if (StringUtils.equalsIgnoreCase(localName, "LINK")) {
            String s = atts.getValue("rel");
            if (StringUtils.equalsIgnoreCase(s, "amphtml")) {
                this.ampUrl = atts.getValue("href");
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

    public String getAmpUrl() {
        return this.ampUrl;
    }

}
