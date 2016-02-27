import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    public METAR parseMetar(String address) {
        try {
            //Create the stream
            URL url = new URL(address);
            InputStream in = url.openStream();

            //Generate the SAX Parser and get it to parse the file
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            METARHandler handler = new METARHandler();
            parser.parse(in, handler);
            return handler.getData();
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String,String> parseAirports(String address) {
        try {
            //Create the stream
            File file = new File("res/GlobalAerodromes.xml");

            //Generate the SAX Parser and get it to parse the file
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            AirportHandler handler = new AirportHandler();
            parser.parse(file, handler);
            return handler.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class METARHandler extends DefaultHandler {
        private METAR metar;
        private int airTemperature;
        private int dewpointTemperature;
        private String weather;
        private boolean bAir, bDewpoint;
        private boolean bAssignedWeather;

        public METAR getData() {
            return metar;
        }

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) throws SAXException {
            if (qName.equals("iwxxm:airTemperature")) {
                bAir = true;
            } else if (qName.equals("iwxxm:dewpointTemperature")) {
                bDewpoint = true;
            } else if (qName.equals("iwxxm:presentWeather")) {
                weather = attributes.getValue("xlink:title");
                bAssignedWeather = true;
            }
        }

        @Override
        public void characters(char ch[],
                               int start, int length) throws SAXException {
            if (bAir) {
                airTemperature = Integer.parseInt(new String(ch, start, length));
                bAir = false;
            } else if (bDewpoint) {
                dewpointTemperature = Integer.parseInt(new String(ch, start, length));
                bDewpoint = false;
            }
        }

        @Override
        public void endElement(String uri, String localName,
                               String qName) throws SAXException {
            if (qName.equals("iwxxm:METAR")) {
                if(bAssignedWeather){
                    metar = new METAR(airTemperature,dewpointTemperature,weather);
                    bAssignedWeather = false;
                }else {
                    metar = new METAR(airTemperature,dewpointTemperature);
                }
            }
        }
    }

    private class AirportHandler extends DefaultHandler {
        private Map<String,String> airports = new HashMap<>();
        private String airportName, airportICAO;
        private boolean bName, bICAO;

        public Map<String,String> getData() {
            return airports;
        }

        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) throws SAXException {
            if (qName.equals("aixm:name")) {
                bName = true;
            } else if (qName.equals("aixm:locationIndicatorICAO")) {
                bICAO = true;
            }
        }

        @Override
        public void characters(char ch[],
                               int start, int length) throws SAXException {
            if (bName) {
                airportName = new String(ch, start, length);
                //Get rid of non-standard characters
                airportName.replaceAll("[^a-zA-Z ]", "");
                bName = false;
            } else if (bICAO) {
                airportICAO = new String(ch, start, length);
                bICAO = false;
            }
        }

        @Override
        public void endElement(String uri, String localName,
                               String qName) throws SAXException {
            if (qName.equals("wfs:member")) {
                airports.put(airportName,airportICAO);
            }
        }
    }
}

