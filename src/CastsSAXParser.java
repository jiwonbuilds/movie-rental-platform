
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class CastsSAXParser extends DefaultHandler {

    private HashMap<String, Movie> movies;
    private Set<String> updatedMovies = new HashSet<>();
    private Set<String> missingMovies = new HashSet<>();

    private String tempVal;

    private Movie tempMovie;

    private int missingMovieCount = 0;
    private BufferedWriter missingWriter;
    private BufferedWriter noStarWriter;

    public CastsSAXParser(HashMap<String, Movie> parsedMovies) {
        this.movies = parsedMovies;

        try {
            missingWriter = new BufferedWriter(new FileWriter("MissingMovies.txt"));
            noStarWriter = new BufferedWriter(new FileWriter("NoStarMovies.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runParser() {
        parseDocument();
        printData();
        recordMissingMovies();
        recordNoStarMovies();
        closeFileWriter();
    }

    private void closeFileWriter() {
        try {
            missingWriter.close();
            noStarWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordMissingMovies() {
        try {
            for (String movieId : missingMovies) {
                missingWriter.write(movieId);
                missingWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordNoStarMovies() {
        try {
            for (String movieId : movies.keySet()) {
                if (!updatedMovies.contains(movieId)) {
                    noStarWriter.write(movieId);
                    noStarWriter.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("stanford-movies/casts124.xml", this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void printData() {
        System.out.println("No of Star-Added Movies '" + updatedMovies.size() + "'.");
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
    }



    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            tempMovie = null;
        } else if (qName.equalsIgnoreCase("f")) {
            if (movies.containsKey(tempVal)) {
                tempMovie = movies.get(tempVal);
            } else {
                missingMovies.add(tempVal);
            }
        } else if (tempMovie != null) {
            if (qName.equalsIgnoreCase("a")) {
                tempMovie.addStar(new Star(tempVal));
                movies.put(tempMovie.getMovieId(), tempMovie);
                updatedMovies.add(tempMovie.getMovieId());
            }
        }
    }

    public HashMap<String, Movie> getMovies() {
        return this.movies;
    }

}