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

package com.objectparadigms.framework.bootstrap.config;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.objectparadigms.framework.InitializationException;
import com.objectparadigms.framework.bootstrap.FrameworkBootstrap;
import com.objectparadigms.util.EfficientStringTokenizer;
import com.objectparadigms.util.StackTraceUtil;
import com.objectparadigms.util.xml.LocalEntityResolver;
import com.objectparadigms.util.xml.ParserConstants;

/**
 * <a href="DigesterComponent.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.6 $
 *
 */
public class DigesterComponent extends Component {
	public DigesterComponent(String name, String ruleSetClass,
		String configFiles) {
		super(name, ComponentType.DIGESTER_INITIALIZER);
		_ruleSetClassName = ruleSetClass;
		this._configFiles = configFiles;
	}

	public void setConfigFiles(String configFiles) {
		this._configFiles = configFiles;
	}

	public String getConfigFiles() {
		return _configFiles;
	}

	public void setRuleSetClassName(String ruleSetClassName) {
		this._ruleSetClassName = ruleSetClassName;
	}

	public String getRuleSetClassName() {
		return _ruleSetClassName;
	}

	public void initialize() throws InitializationException {
		if (_log.isInfoEnabled()) {
			_log.info("--------------------------------------");
			_log.info("Initializing: " + getName());
			_log.info("--------------------------------------");
		}

		XMLReader reader = null;

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(ParserConstants.VALIDATION_FEATURE, true);
			factory.setFeature(ParserConstants.VALIDATION_FEATURE, false);

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

		//use a custom local entity resolver
		Digester digester = new Digester(reader);
		digester.setUseContextClassLoader(true);

		EntityResolver resolver = new LocalEntityResolver();
		digester.setEntityResolver(resolver);

		try {
			RuleSet set = (RuleSet)Class.forName(_ruleSetClassName).newInstance();
			digester.addRuleSet(set);
		}
		catch (Exception e) {
			throw new InitializationException("Unable to initialize ruleset: " +
				_ruleSetClassName);
		}

		EfficientStringTokenizer tokenizer = new EfficientStringTokenizer(_configFiles,
				_FILE_SEPARATOR);

		while (tokenizer.hasMoreElements()) {
			String fileName = tokenizer.nextToken();

			if (_log.isDebugEnabled()) {
				_log.debug("Loading configuration file: " + fileName);
			}

			try {
				InputStream in = ClassLoader.getSystemClassLoader()
											.getResourceAsStream(fileName);
				digester.parse(in);
			}
			catch (Exception e) {
				throw new InitializationException(
					"Unable to complete initialization for file " + fileName +
					" :" + StackTraceUtil.getStackTrace(e));
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("--------------------------------------");
			_log.info("Completed initialize: " + getName());
			_log.info("--------------------------------------");
		}
	}

	private static final String _FILE_SEPARATOR = ",";
	private static Log _log = LogFactory.getLog(FrameworkBootstrap.LOG_CATEGORY);
	private String _configFiles;
	private String _ruleSetClassName;
}