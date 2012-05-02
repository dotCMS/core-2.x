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

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="EJBFactoryConfig.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class EJBFactoryConfig {
	private static EJBFactoryConfig instance;
	private Map containers;
	private Map ejbs;
	private int retries;

	private EJBFactoryConfig() {
		ejbs = new HashMap();
		containers = new HashMap();
	}

	public static synchronized EJBFactoryConfig getInstance() {
		if (instance == null) {
			instance = new EJBFactoryConfig();
		}

		return instance;
	}

	public EJBContainerConfig getContainer(String friendlyName) {
		return (EJBContainerConfig)containers.get(friendlyName);
	}

	public EJBConfig getEJB(String jndiName) {
		return (EJBConfig)ejbs.get(jndiName);
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getRetries() {
		return retries;
	}

	public void addContainer(EJBContainerConfig config) {
		containers.put(config.getFriendlyName(), config);
	}

	public void addEJB(EJBConfig config) {
		ejbs.put(config.getJndiName(), config);
	}

	public String toString() {
		return "com.objectparadigms.framework.ejbfactory.config.EJBFactoryConfig{" +
		", ejbs=" + ((ejbs == null) ? null : ("size:" + ejbs.size() + ejbs)) +
		", containers=" +
		((containers == null) ? null : ("size:" + containers.size() +
		containers)) + "}";
	}
}