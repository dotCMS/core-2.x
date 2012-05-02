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

package com.objectparadigms.framework.bootstrap;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dotmarketing.util.Logger;
import com.objectparadigms.framework.InitializationException;
import com.objectparadigms.framework.bootstrap.config.BootstrapRuleSet;
import com.objectparadigms.framework.bootstrap.config.Component;
import com.objectparadigms.util.StackTraceUtil;
import com.objectparadigms.util.xml.LocalEntityResolver;
import com.objectparadigms.util.xml.ParserConstants;

/**
 * <a href="FrameworkBootstrap.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.6 $
 *
 */
public class FrameworkBootstrap {
	public static final String LOG_CATEGORY = FrameworkBootstrap.class.getName();
	private static Log log = LogFactory.getLog(FrameworkBootstrap.LOG_CATEGORY);
	private static final String _CONFIG_FILE = "bootstrap.xml";

	public static void main(String[] args) {
		try {
			FrameworkBootstrap test = new FrameworkBootstrap();
			test.init();
		}
		catch (Exception e) {
			Logger.error(FrameworkBootstrap.class,e.getMessage(),e);
		}
	}

	public void init() throws InitializationException {
		XMLReader reader = null;

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(ParserConstants.VALIDATION_FEATURE, true);
			factory.setFeature(ParserConstants.NAMESPACES_FEATURE, false);

			reader = factory.newSAXParser().getXMLReader();
		}
		catch (SAXException se) {
			throw new InitializationException("Error initializing: " +
				StackTraceUtil.getStackTrace(se));
		}
		catch (ParserConfigurationException pce) {
			throw new InitializationException("Error initializing: " +
				StackTraceUtil.getStackTrace(pce));
		}

		EntityResolver resolver = new LocalEntityResolver();
		Digester digester = new Digester(reader);
		digester.setUseContextClassLoader(true);
		digester.setEntityResolver(resolver);
		digester.addRuleSet(new BootstrapRuleSet());

		Collection components = null;

		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(_CONFIG_FILE);
			components = (Collection)digester.parse(in);
		}
		catch (Exception e) {
			throw new InitializationException("Unable to parse bootstrap.xml: " +
				StackTraceUtil.getStackTrace(e));
		}

		Iterator iterator = components.iterator();
		boolean success = true;

		while (iterator.hasNext()) {
			Component component = (Component)iterator.next();

			try {
				component.initialize();
			}
			catch (InitializationException ie) {
				success = false;
				log.fatal("Unable to initialize framework component: " +
					component.getName(), ie);
			}
		}

		if (!success) {
			throw new InitializationException(
				"Unable to initialize application framework.  " +
				"Consult initialization logs.");
		}
	}
}