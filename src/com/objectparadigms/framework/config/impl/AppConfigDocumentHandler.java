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

package com.objectparadigms.framework.config.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.objectparadigms.framework.config.ConfigManager;
import com.objectparadigms.util.xml.LocalEntityResolver;

/**
 * <a href="AppConfigDocumentHandler.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class AppConfigDocumentHandler extends DefaultHandler {
	private static final String _APPLICATION_ID_ATT = "id";
	private static final String _APPLICATION_NODE_NAME = "application";
	private static final String _XML_FILE_NAME_ATT = "configFile";
	private ConfigManager configManager; //map of project ids to their respective config doms

	public AppConfigDocumentHandler(ConfigManager manager)
		throws SAXException {
		super();
		configManager = manager;
	}

	public void startElement(String uri, String localName, String qName,
		Attributes attributes) throws SAXException {
		if (qName.equals(_APPLICATION_NODE_NAME)) {
			String projectId = attributes.getValue(_APPLICATION_ID_ATT);
			String xmlFileName = attributes.getValue(_XML_FILE_NAME_ATT);
			Document document = createDOM(xmlFileName);
			configManager.addManagedConfiguration(projectId, document);
		}
	}

	private Document createDOM(String xmlFileName) throws SAXException {
		InputStream appConfigStream = null;

		try {
			appConfigStream = ClassLoader.getSystemResourceAsStream(xmlFileName);

			if (appConfigStream == null) {
				throw new SAXException("Unable to load config file: " +
					xmlFileName);
			}

			EntityResolver resolver = new LocalEntityResolver();
			Document dom = load(appConfigStream, resolver);

			return dom;
		}
		catch (Exception fne) {
			throw new SAXException(fne);
		}
		finally {
			try {
				if (appConfigStream != null) {
					appConfigStream.close();
				}
			}
			catch (IOException ie) {
			}
		}
	}

	private Document load(InputStream appConfigStream, EntityResolver resolver)
		throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(true);

		DocumentBuilder domParser = factory.newDocumentBuilder();
		domParser.setEntityResolver(resolver);

		Document dom = domParser.parse(appConfigStream);

		return dom;
	}
}