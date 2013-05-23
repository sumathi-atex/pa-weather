package com.atex.plugins.paweather;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.polopoly.application.Application;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.client.CMException;
import com.polopoly.common.concurrent.AlterableFIFOSemaphore;
import com.polopoly.management.DetailLevel;
import com.polopoly.management.ManagedBeanName;
import com.polopoly.management.ManagedBeanRegistry;
import com.polopoly.management.StandardManagedBeanName;

/**
 * Factory to create Weather clients.
 */
public class WeatherClientFactory {

    private static final Logger LOG = Logger.getLogger(WeatherClientFactory.class.getName());
    
    private static final String MBEAN_MODULE_NAME = "local";
    
    private static final String MBEAN_COMPONENT_NAME = "weather";

    private int socketTimeOut;
    
    private int connectionTimeout;
    
    private static final int DEFAULT_MAX_PERMITS = 10;
    
    private String connectionUsername;
    private String connectionPassword;
    
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;
    
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    
    private static volatile WeatherClientFactory clientFactory;
    
    private AlterableFIFOSemaphore connectionAvailable;
   
    /**
     * Creates a new factory for Weatherlink clients.
     * 
     * @param socketTimeOut
     *            The default socket timeout (SO_TIMEOUT) in milliseconds which
     *            is the timeout for waiting for data. A timeout value of zero
     *            is interpreted as an infinite timeout.
     * @param connectionTimeout
     *            Sets the timeout in milliseconds until a connection is
     *            established. A value of zero means the timeout is not used.           
     */
    private WeatherClientFactory(int socketTimeOut, int connectionTimeout, int maxPermits)
    {
        this.socketTimeOut = socketTimeOut;
        this.connectionTimeout = connectionTimeout;       
        connectionAvailable = new AlterableFIFOSemaphore(maxPermits, 0);
    }
     
    public static WeatherClientFactory getInstance(ServletContext servletContext, boolean registerMBean)
        throws CMException 
    { 
        if (clientFactory == null) {
            synchronized (WeatherClientFactory.class) {
                if (clientFactory == null) {
                    
                    Application application = ApplicationServletUtil
                            .getApplication(servletContext);
                    
                    clientFactory = 
                        new WeatherClientFactory(DEFAULT_SOCKET_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT,
                        		DEFAULT_MAX_PERMITS);
                    
                    // Register semaphore mBean
                    if (registerMBean) {
                        ManagedBeanRegistry mBeanRegistry = application
                                .getManagedBeanRegistry();
    
                        StandardManagedBeanName mBeanName = new StandardManagedBeanName();
                        mBeanName.set(ManagedBeanName.HOST, application
                                .getHostname().getDomainlessHostname());
                        mBeanName.set(ManagedBeanName.APPLICATION, application
                                .getName());
                        mBeanName.set(ManagedBeanName.MODULE, MBEAN_MODULE_NAME);
                        mBeanName.set(ManagedBeanName.COMPONENT, MBEAN_COMPONENT_NAME);
                        mBeanName.set(ManagedBeanName.DETAIL_LEVEL,
                                DetailLevel.FINE.toString());
                        mBeanName.set(ManagedBeanName.NAME, "WeatherClientManager");
                        
                        // Unregister any previous mBean
                        try {
                            mBeanRegistry.unregisterManagedBean(mBeanName);
                        } catch (Exception e) {
                            if (LOG.isLoggable(Level.FINE)) {
                                LOG.log(Level.FINE, "No previous semaphore mbean " +
                                        "for akismet clients to unregister");
                            }
                        }
                        
                        // Register the mBean
                        WeatherClientFactoryMBean mBean = new WeatherClientFactoryMBeanImpl(
                                clientFactory.connectionAvailable, clientFactory);
                        try {
                            mBeanRegistry.registerManagedBean(mBeanName,
                                    mBean,
                                    WeatherClientFactoryMBean.class);
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, "Unable to register semaphore "
                                    + "mbean for Weather clients", e);
                        }
                    }
                }
            }
        }
        return clientFactory;
    }
    
    /**
     * Return the time to wait at most for an Weather HTTP request.
     * 
     * @return timeout value in millisec.
     */
    int getSocketTimeout() {
        return socketTimeOut;
    }

    /**
     * Set the time to wait at most for an Weather HTTP request.
     * 
     * @param timeout
     *            timeout value in millisec.
     */
    void setSocketTimeout(int timeout) {
        socketTimeOut = timeout;
    }
    
    /**
     * Return the time to wait at most for establishing an Weather HTTP request.
     * 
     * @return timeout value in millisec.
     */
    int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set the time to wait at most for establishing an Weather HTTP request.
     * 
     * @param timeout
     *            timeout value in millisec.
     */
    void setConnectionTimeout(int timeout) {
        connectionTimeout = timeout;
    }

    /**
     * Set the username for establishing an Weather HTTP request.
     * 
     * @param username
     *            username value.
     */
    void setConnectionUsername(String username) {
        connectionUsername = username;
    }
    
    /**
     * Return the username for establishing an Weather HTTP request.
     * 
     * @return username value.
     */
    String getConnectionUsername() {
        return connectionUsername;
    }

    /**
     * Set the password for establishing an Weather HTTP request.
     * 
     * @param password
     *            username value.
     */
    void setConnectionPassword(String password) {
        connectionPassword = password;
    }
    
    /**
     * Return the password for establishing an Weather HTTP request.
     * 
     * @return password value.
     */
    String getConnectionPassword() {
        return connectionPassword;
    }

    /**
     * Get a new Weather client 
     * 
     * @throws IllegalArgumentException
     *             If either the User key <code>null</code> or
     *             invalid.
     * @throws WeatherException
     *             If failed to connect or request Weather service.
     */
    public WeatherClient getClient()
        throws IllegalArgumentException, WeatherException
    {
    	WeatherClient client =
            new WeatherClient(socketTimeOut, connectionTimeout,
                                  connectionAvailable,
                                  connectionUsername, connectionPassword);
        return client;
    }
}
