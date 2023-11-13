
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class ActorsSAXParser extends DefaultHandler {

    private List<Star> stars;
    private Star tempStar;
    private String tempVal;


    public ActorsSAXParser() {
        stars = new ArrayList<Star>();
    }

    public void runParser() {
        parseDocument();
        // printData();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("stanford-movies/actors63.xml", this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void printData() {
        System.out.println("No of Stars '" + stars.size() + "'.");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
            stars.add(tempStar);
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setStarName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                tempStar.setStarYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public List<Star> getActors() {
        return this.stars;
    }

    public static void main(String[] args) {
        ActorsSAXParser asp = new ActorsSAXParser();
        asp.runParser();
    }

}