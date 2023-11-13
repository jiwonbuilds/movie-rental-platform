import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MoviesSAXParser extends DefaultHandler {
    private HashMap<String, Movie> movies = new HashMap<>();
    private Movie tempMovie;
    private String tempVal;
    private Set<String> movieGenres = new HashSet<>();
    private int inconsistencyCount = 0;
    private int duplicateCount = 0;
    private BufferedWriter inconsistencyWriter;
    private BufferedWriter duplicateWriter;

    public MoviesSAXParser() {
        try {
            inconsistencyWriter = new BufferedWriter(new FileWriter("InconsistentMovies.txt"));
            duplicateWriter = new BufferedWriter(new FileWriter("DuplicateMovies.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void runParser() {
        parseDocument();
        printData();
        closeFileWriter();
    }

    private void closeFileWriter() {
        try {
            inconsistencyWriter.close();
            duplicateWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("stanford-movies/mains243.xml", this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void printData() {
        System.out.println(inconsistencyCount + " movies inconsistent.");
        System.out.println(duplicateCount + " movies duplicate.");
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
            if (tempMovie.getMovieId() == null || tempMovie.getMovieTitle() == null || tempMovie.getMovieDirector() == null || tempMovie.getMovieYear() == null) {
                try {
                    inconsistencyWriter.write(tempMovie.toString());
                    inconsistencyWriter.newLine();
                    inconsistencyCount++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (movies.get(tempMovie.getMovieId()) != null) {
                try {
                    duplicateWriter.write(tempMovie.toString());
                    duplicateWriter.newLine();
                    duplicateCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                movies.put(tempMovie.getMovieId(), tempMovie);

            }
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setMovieId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setMovieTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            try {
                tempMovie.setYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException e) {
                tempMovie.setYear(null);
            }
        } else if (qName.equalsIgnoreCase("dirn")) {
            tempMovie.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("cat")) {
            if (!tempVal.isEmpty()) {
                tempVal = tempVal.trim();
                tempVal = Character.toUpperCase(tempVal.charAt(0)) + tempVal.substring(1);
                movieGenres.add(tempVal);
                tempMovie.addGenre(new Genre(tempVal));
            }
        }
    }

    public HashMap<String, Movie> getMovies() {
        return this.movies;
    }
    public Set<String> getGenres() { return this.movieGenres; }

    public static void main(String[] args) {
        MoviesSAXParser moviesParser = new MoviesSAXParser();
        moviesParser.runParser();
        System.out.println("hello world");
    }
}

