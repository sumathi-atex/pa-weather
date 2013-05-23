package com.atex.plugins.paweather;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


public class PAWeatherXMLParser extends DefaultHandler{
	private static Logger LOG = Logger.getLogger(PAWeatherXMLParser.class.getName());

    private List<PAWeatherForecast> forecasts = new ArrayList<PAWeatherForecast>();      

    private PAWeatherForecast tempForecast = null;
	private String temp_temperatureValueType = "";
	private String temp_temperatureUnit = "";
	private String xmlElementTempVal = "";

	public List<PAWeatherForecast> parseDocument(String xmlText) {
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			sp.parse(new InputSource( new StringReader( xmlText )), this);
			
			return forecasts;

		}catch(SAXException se) {
			LOG.log(Level.WARNING, "Problem parsing response from PA Weather webservice", se);
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			LOG.log(Level.WARNING, "Problem parsing response from PA Weather webservice", pce);
			pce.printStackTrace();
		}catch (IOException ie) {
			LOG.log(Level.WARNING, "Problem parsing response from PA Weather webservice", ie);
			ie.printStackTrace();
		}
		
		return null;
	}
	
	//Event Handlers
	public void startElement(String uri, String localName, String qName,
		Attributes attributes) throws SAXException {
		
		if(qName.equalsIgnoreCase("WeatherForecast")) {
			//create a new instance of PAWeatherForecast
			tempForecast = new PAWeatherForecast();
			tempForecast.setStationName(attributes.getValue("station_name"));
			tempForecast.setForecastDate(attributes.getValue("forecast_date"));
		}
		
		if(qName.equalsIgnoreCase("Temperature")) {
			temp_temperatureValueType = attributes.getValue("temperature_valuetype");
			temp_temperatureUnit = attributes.getValue("temperature_unit");
		}

		if(qName.equalsIgnoreCase("WindSpeed")) {
			tempForecast.setWindspeedUnit(attributes.getValue("windspeed_unit"));
		}
	}


	public void characters(char[] ch, int start, int length) throws SAXException {
		xmlElementTempVal = "";
		xmlElementTempVal = new String(ch,start,length);
	}

	public void endElement(String uri, String localName,
		String qName) throws SAXException {

		if(qName.equalsIgnoreCase("WeatherForecast")) {
/* 			System.out.println("Printing weather forecast for: "+ tempForecast.getStationName() + " for "+tempForecast.getForecastDate() );
 *			System.out.println(" MaxC = "+ tempForecast.getTemperatureMaxC());
 *			System.out.println(" MaxF = "+ tempForecast.getTemperatureMaxF());
 *			System.out.println(" MinC = "+ tempForecast.getTemperatureMinC());
 *			System.out.println(" MinF = "+ tempForecast.getTemperatureMinF());
 *			System.out.println(" WindSpeed = "+ tempForecast.getWindspeed());
 *			System.out.println(" WeatherTextShort = "+ tempForecast.getWeatherTextShort());
 *			System.out.println(" WeatherTextLong = "+ tempForecast.getWeatherTextLong());
*/
			//add parsed forecast to the list
			forecasts.add(tempForecast);

			//reset temp forecast values ready for parsing next/additional forecast XML
			temp_temperatureValueType = "";
			temp_temperatureUnit = "";
			tempForecast = null;

		}else if (qName.equalsIgnoreCase("Temperature")) {
			if (temp_temperatureValueType.equals("MAX") && temp_temperatureUnit.equals("C")) {
				tempForecast.setTemperatureMaxC(xmlElementTempVal);				
			}
			if (temp_temperatureValueType.equals("MAX") && temp_temperatureUnit.equals("F")) {
				tempForecast.setTemperatureMaxF(xmlElementTempVal);				
			}
			if (temp_temperatureValueType.equals("MIN") && temp_temperatureUnit.equals("C")) {
				tempForecast.setTemperatureMinC(xmlElementTempVal);				
			}
			if (temp_temperatureValueType.equals("MIN") && temp_temperatureUnit.equals("F")) {
				tempForecast.setTemperatureMinF(xmlElementTempVal);				
			}
		}else if (qName.equalsIgnoreCase("WindSpeed")) {
			tempForecast.setWindspeed(xmlElementTempVal);
		}else if (qName.equalsIgnoreCase("WeatherTextShort")) {
			tempForecast.setWeatherTextShort(xmlElementTempVal);
		}else if (qName.equalsIgnoreCase("WeatherTextLong")) {
			tempForecast.setWeatherTextLong(xmlElementTempVal);
		}

	}
}