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

package com.objectparadigms.framework.locator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.util.GetterUtil;
import com.liferay.util.SystemProperties;
import com.objectparadigms.framework.locator.impl.CachedServiceLocator;
import com.objectparadigms.util.StackTraceUtil;

/**
 * <a href="ServiceLocatorFactory.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.5 $
 *
 */
public class ServiceLocatorFactory {
	private static final String _SERVICE_ID = "META-INF/services/com.objectparadigms.framework.locator.ServiceLocator";
	public static final String _DEFAULT_LOCATOR_CLASS = GetterUtil.get(SystemProperties.get(ServiceLocator.class.getName() +
				".class"), CachedServiceLocator.class.getName());
	private static final String _LOCAL_LOCATOR_KEY = "locator.local";
	private static final Properties _EMPTY_PROPS = new Properties();
	private static ServiceLocatorFactory _instance;
	private static Log log = LogFactory.getLog(ServiceLocator.LOCATOR_LOG);
	private Class locatorClass;
	private Constructor locatorConstructor;
	private Map _cache;

	private ServiceLocatorFactory() {
		_cache = Collections.synchronizedMap(new HashMap());

		BufferedReader br = null;
		String className = _DEFAULT_LOCATOR_CLASS;

		try {
			InputStream serviceIs = ClassLoader.getSystemResourceAsStream(_SERVICE_ID);

			try {
				br = new BufferedReader(new InputStreamReader(serviceIs, "UTF-8"));
			}
			catch (UnsupportedEncodingException uee) {
				br = new BufferedReader(new InputStreamReader(serviceIs));
			}

			className = br.readLine();
		}
		catch (Exception e) {
			log.warn("No Locator implementation specified defined in: " +
				_SERVICE_ID + ".  Using " + _DEFAULT_LOCATOR_CLASS);
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
			locatorClass = Class.forName(className);
			locatorConstructor = locatorClass.getConstructor(new Class[] {
						Properties.class
					});
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to initialize factory: " +
				StackTraceUtil.getStackTrace(e));
		}
	}

	public static synchronized ServiceLocatorFactory getInstance() {
		if (_instance == null) {
			_instance = new ServiceLocatorFactory();
		}

		return _instance;
	}

	public ServiceLocator getLocator() throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + "getLocator()");
		}

		return getLocator(_EMPTY_PROPS);
	}

	public ServiceLocator getLocator(Properties jndiProps)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + "getLocator(Properties)");
		}

		String cacheKey = jndiProps.getProperty(Context.PROVIDER_URL);

		if (cacheKey == null) {
			cacheKey = _LOCAL_LOCATOR_KEY;
		}

		ServiceLocator locator = (ServiceLocator)_cache.get(cacheKey);

		if (locator == null) {
			if (log.isInfoEnabled()) {
				log.info(getClass().getName() + "getLocator(Properties) - " +
					"Creating new locator for " + cacheKey);
			}

			try {
				locator = (ServiceLocator)locatorConstructor.newInstance(new Object[] {
							jndiProps
						});
			}
			catch (Exception e) {
				throw new IllegalStateException(
					"Unable to instantiate locator: " +
					StackTraceUtil.getStackTrace(e));
			}

			_cache.put(cacheKey, locator);
		}

		return locator;
	}

	public synchronized void clear() {
		_cache.clear();
	}
}