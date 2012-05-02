/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.objectparadigms.framework.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.objectparadigms.framework.config.impl.AppConfigDocumentHandler;
import com.objectparadigms.util.StackTraceUtil;

/**
 * <a href="ConfigManagerFactory.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class ConfigManagerFactory {
	private static Log log = LogFactory.getLog(ConfigManager.CONFIG_LOG);
	private static final String _APP_CONFIG_XML = "app-config.xml";
	private static final String _VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
	private static final String _NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
	private static ConfigManagerFactory configInstance = null;
	private static final String _SERVICE_ID = "META-INF/services/com.objectparadigms.framework.config.ConfigManager";
	private static final String _DEFAULT_CONFIG_MGR_CLASS = "com.objectparadigms.framework.config.impl.DefaultConfigManager";
	private ConfigManager manager = null;

	private ConfigManagerFactory(InputStream in) throws ConfigException {
		BufferedReader br = null;
		String configMgrImplClass = null;

		try {
			InputStream serviceIs = ClassLoader.getSystemResourceAsStream(_SERVICE_ID);

			try {
				br = new BufferedReader(new InputStreamReader(serviceIs, "UTF-8"));
			}
			catch (UnsupportedEncodingException uee) {
				br = new BufferedReader(new InputStreamReader(serviceIs));
			}

			configMgrImplClass = br.readLine();
		}
		catch (Exception e) {
			log.error("No config manager specified defined in: " + _SERVICE_ID +
				".  Using " + _DEFAULT_CONFIG_MGR_CLASS);
			configMgrImplClass = _DEFAULT_CONFIG_MGR_CLASS;
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException ie) {
				}
			}
		}

		try {
			manager = (ConfigManager)Class.forName(configMgrImplClass)
										  .newInstance();
		}
		catch (Exception e) {
			log.error(StackTraceUtil.getStackTrace(e));
			throw new ConfigException("Unable to instantiate an instance of: " +
				configMgrImplClass + ": ", e);
		}

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(_VALIDATION_FEATURE, true);
			factory.setFeature(_NAMESPACE_FEATURE, false);

			XMLReader reader = factory.newSAXParser().getXMLReader();

			AppConfigDocumentHandler handler = new AppConfigDocumentHandler(manager);

			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);
			reader.parse(new InputSource(in));
		}
		catch (IOException ie) {
			throw new ConfigException("Error initializing: " +
				StackTraceUtil.getStackTrace(ie));
		}
		catch (SAXException se) {
			throw new ConfigException("Error initializing: " +
				StackTraceUtil.getStackTrace(se));
		}
		catch (ParserConfigurationException pce) {
			throw new ConfigException("Error initializing: " +
				StackTraceUtil.getStackTrace(pce));
		}
	}

	public static ConfigManagerFactory getInstance() {
		if (configInstance == null) {
			throw new IllegalStateException("Cannot initialize config manager");
		}

		return configInstance;
	}

	public static void init() throws ConfigException {
		URL url = ClassLoader.getSystemClassLoader().getResource(_APP_CONFIG_XML);
		InputStream in = null;

		try {
			in = url.openStream();
			configInstance = new ConfigManagerFactory(in);
		}
		catch (IOException ie) {
			throw new ConfigException("Error retrieving config file: " +
				StackTraceUtil.getStackTrace(ie));
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException ie) {
				}
			}
		}
	}

	public ConfigManager getManager() throws ConfigException {
		return manager;
	}
}