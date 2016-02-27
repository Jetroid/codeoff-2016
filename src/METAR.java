public class METAR {
    private int airTemperature;
    private int dewpointTemperature;
    private String weather = null;

    public METAR(int airTemperature, int dewpointTemperature, String weather){
        this.weather = weather;
    }

    public METAR(int airTemperature, int dewpointTemperature) {
        this.airTemperature = airTemperature;
        this.dewpointTemperature = dewpointTemperature;
    }

    public int getDewpointTemperature() {
        return dewpointTemperature;
    }

    public int getAirTemperature() {
        return airTemperature;
    }

    public String getWeather() {
        return weather;
    }
}
