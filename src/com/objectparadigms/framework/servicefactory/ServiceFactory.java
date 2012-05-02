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

package com.objectparadigms.framework.servicefactory;

import java.util.HashMap;
import java.util.Map;

import com.objectparadigms.framework.exception.SystemException;
import com.objectparadigms.framework.servicefactory.config.ServiceConfig;
import com.objectparadigms.framework.servicefactory.config.ServiceFactoryConfig;

/**
 * <a href="ServiceFactory.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class ServiceFactory {
	public static final String LOG_CATEGORY = ServiceFactory.class.getName();
	private static ServiceFactory instance;
	private Map instanceCache;
	private ServiceFactoryConfig configs;

	private ServiceFactory() {
		configs = ServiceFactoryConfig.getInstance();
		instanceCache = new HashMap();
	}

	public static synchronized ServiceFactory getInstance() {
		if (instance == null) {
			instance = new ServiceFactory();
		}

		return instance;
	}

	public Object getService(String serviceName) throws SystemException {
		ServiceConfig config = configs.get(serviceName);
		Object service = null;

		if (config.isCached()) {
			synchronized (instanceCache) {
				service = instanceCache.get(serviceName);

				if (service == null) {
					service = _createService(config);
					service = instanceCache.put(serviceName, service);
				}

				return service;
			}
		}

		return _createService(config);
	}

	private Object _createService(ServiceConfig config)
		throws SystemException {
		try {
			return config.getImpl().newInstance();
		}
		catch (Exception e) {
			throw new SystemException("Unable to instantiate service.", e);
		}
	}
}