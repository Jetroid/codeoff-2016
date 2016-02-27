import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Countries {
    private static Map<String,Set<String>> countries = new HashMap<>();
    static {
        try {
            File countriesFile = new File("res/compilation.csv");
            FileReader cFR = new FileReader(countriesFile);
            BufferedReader cBuff = new BufferedReader(cFR);
            Set<String> airports = new HashSet<>();

            boolean countryLine = false;
            String country = "";
            String line;
            while((line = cBuff.readLine()) != null){
                countryLine = !countryLine;
                if(countryLine){
                    if(line.equals(country)){
                        country = line;
                    }else{
                        countries.put(country,airports);
                        airports = new HashSet<>();
                        country = line;
                    }
                }else{
                    airports.add(line.substring(1));
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Set<String>> getMap() {
        return countries;
    }
}
