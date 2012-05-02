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

package com.objectparadigms.framework.ejbproxy.config;

/**
 * <a href="EJBProxyConfig.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class EJBProxyConfig {
	private Class homeClass;
	private Class homeHandler;
	private Class remoteClass;
	private Class remoteHandler;
	private String name;

	public void setHomeClass(String homeClassName) {
		try {
			homeClass = Class.forName(homeClassName);
		}
		catch (ClassNotFoundException cnfe) {
			throw new IllegalStateException(cnfe.toString());
		}
	}

	public Class getHomeClass() {
		return homeClass;
	}

	public void setHomeHandler(String homeHandlerClass) {
		try {
			homeHandler = Class.forName(homeHandlerClass);
		}
		catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	public Class getHomeHandler() {
		return homeHandler;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRemoteClass(String remoteClassName) {
		try {
			remoteClass = Class.forName(remoteClassName);
		}
		catch (ClassNotFoundException cnfe) {
			throw new IllegalStateException(cnfe.toString());
		}
	}

	public Class getRemoteClass() {
		return remoteClass;
	}

	public void setRemoteHandler(String remoteHandlerClass) {
		try {
			remoteHandler = Class.forName(remoteHandlerClass);
		}
		catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	public Class getRemoteHandler() {
		return remoteHandler;
	}
}