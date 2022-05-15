package view;

import org.apache.log4j.xml.DOMConfigurator;

import java.util.Properties;

public class LoggerBuilder
{
	public void build(final String log4jConfPath)
	{
		Properties props = System.getProperties();
		props.setProperty("org.jboss.logging.provider", "slf4j");
		props.setProperty("org.apache.poi.util.POILogger","org.apache.poi.util.SLF4JLogger");

		DOMConfigurator.configure(log4jConfPath);
	}
}
