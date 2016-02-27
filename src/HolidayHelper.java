
import java.util.Scanner;

public class HolidayHelper {

    public static void main(String[] args){
        System.out.println("Welcome to the Destination Weather Adviser.");
        Model model = new Model();
        boolean wantToExit = false;
        while(!wantToExit){
            System.out.println("Please write the destination that you would like to know the weather for!");
            System.out.print("> ");
            Scanner userInputScanner = new Scanner(System.in);
            String readInput = userInputScanner.nextLine().replaceAll("[^a-zA-Z ]", "");
            System.out.println("Processing...");
            model.getWeather(readInput);
        }
    }
}
