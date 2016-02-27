public class METAR {
    private int airTemperature;
    private int dewpointTemperature;

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
}
