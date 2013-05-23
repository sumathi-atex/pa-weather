package com.atex.plugins.paweather;

public class PAWeatherForecast {
	
	private String stationName;
    private String forecastDate;
    private String temperatureMaxC;
    private String temperatureMinC;
    private String temperatureMaxF;
    private String temperatureMinF;
    private String windspeedUnit;
    private String windspeed;
    private String weatherTextShort;
    private String weatherTextLong;

    public PAWeatherForecast() {}

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getForecastDate() { return forecastDate; }
    public void setForecastDate(String forecastDate) { this.forecastDate = forecastDate; }

    public String getTemperatureMaxC() { return temperatureMaxC; }
    public void setTemperatureMaxC(String temperatureMaxC) { this.temperatureMaxC = temperatureMaxC; }

    public String getTemperatureMinC() { return temperatureMinC; }
    public void setTemperatureMinC(String temperatureMinC) { this.temperatureMinC = temperatureMinC; }

    public String getTemperatureMaxF() { return temperatureMaxF; }
    public void setTemperatureMaxF(String temperatureMaxF) { this.temperatureMaxF = temperatureMaxF; }

    public String getTemperatureMinF() { return temperatureMinF; }
    public void setTemperatureMinF(String temperatureMinF) { this.temperatureMinF = temperatureMinF; }

    public String getWindspeedUnit() { return windspeedUnit; }
    public void setWindspeedUnit(String windspeedUnit) { this.windspeedUnit = windspeedUnit; }

    public String getWindspeed() { return windspeed; }
    public void setWindspeed(String windspeed) { this.windspeed = windspeed; }

    public String getWeatherTextShort() { return weatherTextShort; }
    public void setWeatherTextShort(String weatherTextShort) { this.weatherTextShort = weatherTextShort; }

    public String getWeatherTextLong() { return weatherTextLong; }
    public void setWeatherTextLong(String weatherTextLong) { this.weatherTextLong = weatherTextLong; }

}
