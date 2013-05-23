package com.atex.plugins.paweather;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.model.ModelWrite;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;
import com.polopoly.siteengine.structure.SitePolicy;

/**
 * This class is an example of what might reside in a plugin. This
 * controller will be auto injected into the output template since the
 * name of the class is the same as the name of the template.
 */
public class MainElementController extends RenderControllerBase {
	
	private static Logger LOG = Logger.getLogger(MainElementController.class.getName());
	
	@Override
	public void populateModelBeforeCacheKey(RenderRequest request, TopModel m,
			ControllerContext context) {
		// TODO Auto-generated method stub
		super.populateModelBeforeCacheKey(request, m, context);
		
		ModelWrite localModel = m.getLocal();
		String cacheTimeout = "240";     // 4 hours ??
		List<PAWeatherForecast> forecasts;
		
		String weatherUrl = (String) ModelPathUtil.get(localModel, "content/weatherfeedAPIURI/value");
		String username = (String) ModelPathUtil.get(localModel, "content/weatherfeedAPIURI/value");
		String password = (String) ModelPathUtil.get(localModel, "content/weatherfeedAPIURI/value");
		
		forecasts =  new ArrayList<PAWeatherForecast>();		
		ServletContext servletContext = ((HttpServletRequest) request).getSession().getServletContext();
		forecasts = new WeatherElementAction().getForecasts(servletContext, weatherUrl, username, password, cacheTimeout);
		if(forecasts != null && forecasts.size() > 0) {
	        ModelPathUtil.set(localModel, "forecasts", forecasts);
	    }
		
	}
	
}