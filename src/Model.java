import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Model {
    /** Map of Airport Name to ICAO */
    private Map<String,String> airports = new HashMap<>();

    private static Map<String, Set<String>> countries = new HashMap<>();
    static{
        countries = Countries.getMap();
    }

    private Parser parser = new Parser();

    public void getWeather(String input){
        //Try if they put in a Country...
        if(countries.keySet().contains(input)){
            for(String airport : countries.get(input)){
                METAR metar = getMetar(airport);
                if(metar != null){
                    if (pollUserOnCorrection(airport, metar)){
                        return;
                    }
                }
            }
        }

        //Try an exact match for airport
        if(airports.containsKey(input)) {
            getAirportWeather(input);
            return;
        }
        //Try a contains match
        for(String airport : airports.keySet()){
            if(airport.contains(input)){
                METAR metar = getMetar(airport);
                if(metar != null){
                    if (pollUserOnCorrection(airport, metar)){
                        return;
                    }
                }
            }
        }

        //Try a spelling correction
        String correction = spellingCorrection(input);
        System.out.println("We think you meant " + correction + ".");
        getAirportWeather(correction);
    }

    private boolean pollUserOnCorrection(String airport, METAR metar) {
        System.out.print("Did you mean " + airport + "? (Y/N)\n> ");
        Scanner scanner = new Scanner(System.in);
        String userMessage = scanner.nextLine().toLowerCase().substring(0,1);
        if(userMessage.equals("y")){
            interpretWeather(metar);
            return true;
        }
        return false;
    }

    private String spellingCorrection(String input) {
        //Do some spelling correction
        DamerauLevenshteinAlgorithm damlev = new DamerauLevenshteinAlgorithm();
        String actualAirportName = "";
        int lowestOps = Integer.MAX_VALUE;
        int inputLength = input.split(" ").length;
        for(String a : airports.keySet()){
            if(inputLength == 1){
                for(String word : a.split(" ")){
                    int distance = damlev.distance(input,word);
                    if(distance < lowestOps){
                        actualAirportName = a;
                        lowestOps = distance;
                    }
                }
            }else {
                int distance = damlev.distance(input,a);
                if(distance < lowestOps){
                    actualAirportName = a;
                    lowestOps = distance;
                }
            }
        }
        return actualAirportName;
    }

    public void getAirportWeather(String airport){
        METAR metar = getMetar(airport);
        if(metar != null){
            interpretWeather(metar);
        }else {
            System.out.println("Sorry, couldn't find any data for that Airport.");
        }
    }

    private METAR getMetar(String airport) {
        String icao = airports.get(airport);
        String url = "https://api.laminardata.aero/v1/aerodromes/" + icao + "/metar?user_key=codeoff-8259ab01a5c228118596b202";
        return parser.parseMetar(url);
    }

    private void interpretWeather(METAR metar) {
        if(metar != null){
            int airtemp = metar.getAirTemperature();
            int dewtemp = metar.getDewpointTemperature();
            System.out.print("The air temperature was " + airtemp + " degrees Celsius ");
            System.out.print("and the dewpoint temperature is " + dewtemp + " degrees Celsius.\n");
            if(airtemp >= 25){
                System.out.println("Because of the high air temperature, we'd advise you not to wear black!");
            }else if(dewtemp >= 21){
                System.out.println("Because of the high dewpoint temperature, you'd be advised to pack light clothing.");
                System.out.println("A dewpoint temperature that high will make you feel hot and sticky!");
            }else if (airtemp < 15 && airtemp >= 0){
                System.out.println("The air is quite cold out! Wear something covering!");
            }else if (airtemp < 0){
                System.out.println("Woah! Those temperatures are Sub-Zero! Pack warm!!");
            }else{
                System.out.println("The temperature is quite mild. Wear you're regular clothes");
            }
            String weather = metar.getWeather();
            if(weather != null){
                System.out.println("There is a " + weather.toLowerCase() + ".");
                System.out.println("You should probably pack an umbrella!");
            }
        } else {
            System.out.println("We didn't have any data for that airport. Sorry!");
        }
        System.out.println();
    }

    public Model(){
        addAirports();
    }

    private void addAirports(){
        if(airports.isEmpty()){
            airports.putAll(parser.parseAirports("http://codeoff.laminardata.aero/xml/GlobalAerodromes.xml"));
        }
    }
}
