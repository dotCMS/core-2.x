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

package com.objectparadigms.framework.ejbfactory.config;

import java.lang.reflect.Method;

import com.objectparadigms.framework.ejbproxy.EJBInvocationHandler;
import com.objectparadigms.util.StackTraceUtil;

/**
 * <a href="EJBConfig.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class EJBConfig {
	private static final String _CREATE_METHOD_NAME = "create";
	private Class homeClass;
	private Class proxyClass;
	private Method createMethod;
	private String containerName;
	private String jndiName;
	private boolean cachable;
	private boolean hasCustomProxy;

	public void setCachable(boolean cachable) {
		this.cachable = cachable;
	}

	public boolean isCachable() {
		return cachable;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getContainerName() {
		return containerName;
	}

	public Method getCreateMethod() {
		return createMethod;
	}

	public void setHomeClass(String homeClassName) {
		try {
			homeClass = Class.forName(homeClassName);
			createMethod = homeClass.getMethod(_CREATE_METHOD_NAME, new Class[0]);
		}
		catch (Exception cnfe) {
			throw new IllegalArgumentException(StackTraceUtil.getStackTrace(
					cnfe));
		}
	}

	public Class getHomeClass() {
		return homeClass;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setProxyClass(String className) {
		try {
			if ((className != null) && !className.equals("")) {
				proxyClass = Class.forName(className);

				if (!proxyClass.isAssignableFrom(EJBInvocationHandler.class)) {
					throw new IllegalArgumentException(
						"Specified proxy must implement the " +
						"com.objectparadigms.framework.ejbfactory.EJBInvocationHandler" +
						" interface.");
				}

				hasCustomProxy = true;
			}
		}
		catch (Exception cnfe) {
			throw new IllegalArgumentException(StackTraceUtil.getStackTrace(
					cnfe));
		}
	}

	public Class getProxyClass() {
		return proxyClass;
	}

	public boolean hasCustomProxy() {
		return hasCustomProxy;
	}

	public String toString() {
		return "com.objectparadigms.framework.ejbfactory.config.EJBConfig{" +
		"jndiName='" + jndiName + "'" + ", containerName='" + containerName +
		"'" + ", homeClass=" + homeClass + ", cachable=" + cachable + "}";
	}
}