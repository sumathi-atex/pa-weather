package com.atex.plugins.paweather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import com.polopoly.util.TimeoutCache;
import com.polopoly.cm.client.CMException;

public class WeatherElementAction {
	private static Logger LOG = Logger.getLogger(WeatherElementAction.class.getName());
	private static TimeoutCache timeoutCache;

    private List<PAWeatherForecast> forecasts = new ArrayList<PAWeatherForecast>();      

	protected List<PAWeatherForecast> getForecasts(ServletContext servletContext, String PAWeatherUrl, String siteAPIusername, String siteAPIpassword, String cacheTimeout) {
		long timeout = 1000 * 60 * Long.parseLong(cacheTimeout);
		String response = null;
		String cacheKey = PAWeatherUrl;
		boolean cacheRes = false;
		
        if(timeoutCache == null) {
            timeoutCache = new TimeoutCache();
            timeoutCache.setHardAgeTimeout(timeout);
        }
        
		if(timeoutCache.get(cacheKey) != null) {
			response = (String) timeoutCache.get(cacheKey);
			LOG.info("PA Weather information taken from cache");
		}
		else {
			try {
				WeatherClientFactory paWeatherClientFactory = WeatherClientFactory.getInstance(servletContext, true);
				paWeatherClientFactory.setConnectionUsername(siteAPIusername);
				paWeatherClientFactory.setConnectionPassword(siteAPIpassword);
				WeatherClient paWeatherClient = paWeatherClientFactory.getClient();
				response = paWeatherClient.call(PAWeatherUrl, null);
				if (response == null) {
					return null;
				}
				cacheRes = timeoutCache.put(cacheKey, response);
				LOG.info("PA Weather information received from PA Weather web service");
			} catch (IllegalArgumentException e) {
				LOG.log(Level.WARNING, "Problem connecting to PA Weather webservice" + e.getMessage());
				return null;
				} catch (CMException e) {
				LOG.log(Level.WARNING, "Problem connecting to PA Weather webservice" + e.getMessage());
				return null;
			} catch (WeatherException e) {
				LOG.log(Level.WARNING, "Problem connecting to PA Weather webservice" + e.getMessage());
				return null;
			} catch (WeatherTooManyConnectionsException e) {
				LOG.log(Level.WARNING, "Problem connecting to PA Weather webservice" + e.getMessage());
				return null;
			}
		}

		// Now parse the (XML) string response from the PA Weather http API call		
		try {
				PAWeatherXMLParser xmlParser = new PAWeatherXMLParser();		
				forecasts = xmlParser.parseDocument(response);
//		        LOG.info("Parsed and created "+ forecasts.size() +" weather forecasts");
		        return forecasts;	        
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Problem parsing response from PA Weather webservice" + e.getMessage());
		}		
		return null;
	}
	
}
