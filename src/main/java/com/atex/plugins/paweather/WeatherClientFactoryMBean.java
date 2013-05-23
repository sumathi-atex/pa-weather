package com.atex.plugins.paweather;

/**
 * MBean for an {@link WeatherClientFactory}.
 */
public interface WeatherClientFactoryMBean {

    /**
     * Return the time to wait at most for an Weather HTTP request.
     * 
     * @return timeout value in millisec.
     */
    int getSocketTimeout();

    /**
     * Set the time to wait at most for an Weather HTTP request.
     * 
     * @param timeout
     *            timeout value in millisec.
     */
    void setSocketTimeout(int timeout);

    /**
     * Return the time to wait at most for an Weather HTTP request.
     * 
     * @return timeout value in millisec.
     */
    int getConnectionTimeout();

    /**
     * Set the time to wait at most for establishing an Weather HTTP request.
     * 
     * @param timeout
     *            timeout value in millisec.
     */
    void setConnectionTimeout(int timeout);
    
    /**
     * Get the max concurrent request threads to allow.
     */
    long getMaxAllowedThread();

    /**
     * Set the number of max concurrent request threads to allow. If set to 0,
     * then it will block. If set to 1 it will serve as a mutual exclusion lock
     * .
     * 
     * @throws IllegalArgumentException
     *             if maxPermits is less then 0.
     */
    void setMaxAllowedThread(long permits);
    
    /**
     * Return the current number of current requesting threads. Returns an
     * accurate, but possibly unstable value, that may change immediately after
     * returning.
     */
    long getThreads();
}
