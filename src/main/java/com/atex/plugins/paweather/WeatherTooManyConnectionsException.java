package com.atex.plugins.paweather;

/**
 * Thrown when too many Weather clients try to access the PAWeather
 * servers at the same time. 
 */
public class WeatherTooManyConnectionsException extends Exception {

    public WeatherTooManyConnectionsException() {
        super();
    }
    
    public WeatherTooManyConnectionsException(String message) {
        super(message);
    }

    public WeatherTooManyConnectionsException(String message, Throwable t) {
        super(message, t);
    }

}
