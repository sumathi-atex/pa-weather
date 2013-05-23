package com.atex.plugins.paweather;

import com.polopoly.common.concurrent.AlterableSemaphore;

/**
 * MBean implementation for {@link WeatherClientFactoryMBean}.
 */
public class WeatherClientFactoryMBeanImpl implements WeatherClientFactoryMBean {

    private final WeatherClientFactory paWeatherFactory;

    private final AlterableSemaphore semaphore;

    public WeatherClientFactoryMBeanImpl(AlterableSemaphore semaphore,
    		WeatherClientFactory paWeatherFactory) {
        this.semaphore = semaphore;
        this.paWeatherFactory = paWeatherFactory;
    }

    public long getMaxAllowedThread() {
        return (semaphore != null) ? semaphore.getMaxPermits() : -1;
    }
    
    public int getSocketTimeout() {
        return (paWeatherFactory != null) ? paWeatherFactory.getSocketTimeout()
                : -1;
    }
    
    public int getConnectionTimeout() {
        return (paWeatherFactory != null) ? paWeatherFactory.getConnectionTimeout()
                : -1;
    }

    public void setMaxAllowedThread(long permits) {
        if (semaphore != null) {
            semaphore.setMaxPermits(permits);
        }
    }

    public void setSocketTimeout(int timeout) {
        if (paWeatherFactory != null) {
            paWeatherFactory.setSocketTimeout(timeout);
        }
    }
    
    public void setConnectionTimeout(int timeout) {
        if (paWeatherFactory != null) {
            paWeatherFactory.setConnectionTimeout(timeout);
        }
    }

    public long getThreads() {
        long allowedThread = getMaxAllowedThread();
        long permits = (semaphore != null) ? semaphore.getPermits()
                : allowedThread;
        return allowedThread - permits;
    }
}
