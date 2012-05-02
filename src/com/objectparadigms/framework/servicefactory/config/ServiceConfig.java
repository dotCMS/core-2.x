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

package com.objectparadigms.framework.servicefactory.config;

/**
 * <a href="ServiceConfig.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class ServiceConfig {
	private Class implClass;
	private Class interfaceClass;
	private ServiceMode serviceMode;
	private String name;
	private boolean cached;

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public boolean isCached() {
		return cached;
	}

	public void setImpl(String implName) {
		try {
			this.implClass = Class.forName(implName);
		}
		catch (ClassNotFoundException cnfe) {
			throw new IllegalArgumentException(
				"Unable to locate implementation: " + implName);
		}
	}

	public Class getImpl() {
		return implClass;
	}

	public Class getInterface() {
		return interfaceClass;
	}

	public void setInterfaceName(String interfaceName) {
		try {
			this.interfaceClass = Class.forName(interfaceName);
		}
		catch (ClassNotFoundException cnfe) {
			throw new IllegalArgumentException("Unable to locate interface: " +
				interfaceName);
		}
	}

	public void setMode(String mode) {
		serviceMode = ServiceMode.parse(mode);
	}

	public ServiceMode getMode() {
		return serviceMode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "com.objectparadigms.framework.servicefactory.config.ServiceConfig{" +
		"cached=" + cached + ", interfaceClass=" + interfaceClass +
		", implClass=" + implClass + ", serviceMode=" + serviceMode +
		", name='" + name + "'" + "}";
	}
}