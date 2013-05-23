package com.atex.plugins.paweather;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.HttpState;

import com.polopoly.common.concurrent.AlterableFIFOSemaphore;
import com.polopoly.common.concurrent.Permit;

/**
 *
 *Client to connect to the PAWeather web service
 * 
 */
public class WeatherClient {

    private static final Logger LOG = Logger.getLogger(WeatherClient.class.getName());

    protected static final String HTTP_REFERER = "Referer";

    protected static final String USER_AGENT_HEADER = "User-Agent";
    
    protected static final String USER_AGENT_VALUE = "Polopoly Weather Client/1.0";

    private HttpClient httpClient;

    private AlterableFIFOSemaphore connectionAvailable;

    /**
     * Construct an instance to work with PAWeather.
     * 
     * @param socketTimeOut
     *            The default socket timeout (SO_TIMEOUT) in milliseconds which
     *            is the timeout for waiting for data. A timeout value of zero
     *            is interpreted as an infinite timeout.
     * @param connectionTimeOut           
     * @param connectionAvailable 
     * @throws IllegalArgumentException
     *             If the User key is <code>null</code>
     */
    protected WeatherClient(int socketTimeOut, int connectionTimeout,
                             AlterableFIFOSemaphore connectionAvailable,
                             String username, String password)
        throws IllegalArgumentException
    {    
        this.connectionAvailable = connectionAvailable;
        
        if (connectionAvailable == null) {
            throw new IllegalArgumentException("Connection semaphore cannot be null");
        }

        httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        HttpClientParams httpClientParams = new HttpClientParams();
        DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(
                0, false);
        
        httpClientParams.setParameter(USER_AGENT_HEADER, USER_AGENT_VALUE);
        httpClientParams.setParameter(HttpClientParams.RETRY_HANDLER,
                defaultHttpMethodRetryHandler);
        
        httpClient.setParams(httpClientParams);

        HttpConnectionManager httpConnectionManager =
            new SimpleHttpConnectionManager();
        
        // create connection defaults
        HttpConnectionManagerParams connectionManagerParams = 
            new HttpConnectionManagerParams();
        connectionManagerParams.setSoTimeout(socketTimeOut);
        connectionManagerParams.setConnectionTimeout(connectionTimeout);
        httpConnectionManager.setParams(connectionManagerParams);
        
        httpClient.setHttpConnectionManager(httpConnectionManager);
    }

    public String call(String function, Map<String, String> other)
        throws WeatherException, WeatherTooManyConnectionsException
    {
        GetMethod get = new GetMethod(function);
        get.addRequestHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

/*        if (other != null && other.size() > 0) {
            Iterator<String> keyIterator = other.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                if (key != null && other.get(key) != null) {
                    get.addParameter(new NameValuePair(key, other.get(key)));
                }
            }
        }
*/
       return executeMethod(get); 
    }

    private String executeMethod(GetMethod get)
        throws WeatherException, WeatherTooManyConnectionsException
    {
        Permit permit = null;
        try {
            if ((permit = connectionAvailable.attempt()) != null) {
                try {
                    int responseCode = httpClient.executeMethod(get);
                    String result = get.getResponseBodyAsString();
                    
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        String msg = get.getPath()
                                + " failed with repsonse code: " + responseCode;
                        LOG.log(Level.WARNING, msg);
                        throw new WeatherException(msg);
                    }
                    
                    return result.trim();
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to execute Weather method: "
                            + get.getPath() + ": " + e.getMessage());
                    throw new WeatherException(e.getMessage(), e);
                }               
            } else {
                throw new WeatherTooManyConnectionsException();
            }
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "The requesting thread "
                    + Thread.currentThread().getName()
                    + " was interrupted. Returning null result", e);
        } finally {
            connectionAvailable.release(permit);
        }
        
        return null;
    }
}
