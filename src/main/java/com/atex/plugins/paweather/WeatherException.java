package com.atex.plugins.paweather;

/**
 * Thrown when a Weather call fails (for example when a HTTP connection fails).
 */
public class WeatherException extends Exception {

    public WeatherException(String message) {
        super(message);
    }
    
    public WeatherException(String message, Throwable t) {
        super(message, t);
    }
}
