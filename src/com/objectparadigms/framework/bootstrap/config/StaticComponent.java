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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.objectparadigms.framework.InitializationException;
import com.objectparadigms.framework.bootstrap.FrameworkBootstrap;
import com.objectparadigms.util.StackTraceUtil;

/**
 * <a href="StaticComponent.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.6 $
 *
 */
public class StaticComponent extends Component {
	public StaticComponent(String name, String initializer) {
		super(name, ComponentType.STATIC_INITIALIZER);
		_initializerName = initializer;
	}

	public void setInitializerName(String initializerName) {
		this._initializerName = initializerName;
	}

	public String getInitializerName() {
		return _initializerName;
	}

	public void initialize() throws InitializationException {
		if (log.isInfoEnabled()) {
			log.info("--------------------------------------");
			log.info("Initializing: " + getName());
			log.info("--------------------------------------");
		}

		try {
			Class initializer = Class.forName(_initializerName);
			Method method = initializer.getDeclaredMethod(_INIT_METHOD_NAME,
					new Class[0]);
			method.invoke(initializer, new Object[0]);
		}
		catch (InvocationTargetException e) {
			throw new InitializationException(
				"Unable to initialize specified static intializer: " +
				StackTraceUtil.getStackTrace(e.getCause()));
		}
		catch (Exception e) {
			throw new InitializationException(
				"Unable to initialize specified static intializer: " +
				StackTraceUtil.getStackTrace(e));
		}

		if (log.isInfoEnabled()) {
			log.info("--------------------------------------");
			log.info("Completed initialize: " + getName());
			log.info("--------------------------------------");
		}
	}

	private static Log log = LogFactory.getLog(FrameworkBootstrap.LOG_CATEGORY);
	private static final String _INIT_METHOD_NAME = "init";
	private String _initializerName;
}