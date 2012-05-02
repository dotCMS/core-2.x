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

import java.util.Properties;

import javax.naming.Context;

/**
 * <a href="EJBContainerConfig.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class EJBContainerConfig {
	private Properties jndiProps;
	private String friendlyName;

	public EJBContainerConfig() {
		jndiProps = new Properties();
	}

	public void setCredential(String credential) {
		jndiProps.setProperty(Context.SECURITY_CREDENTIALS, credential);
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setInitialContextFactory(String initialContextFactory) {
		jndiProps.setProperty(Context.INITIAL_CONTEXT_FACTORY,
			initialContextFactory);
	}

	public Properties getJndiProps() {
		return jndiProps;
	}

	public void setPrincipal(String principal) {
		jndiProps.setProperty(Context.SECURITY_PRINCIPAL, principal);
	}

	public String getPrincipal() {
		return jndiProps.getProperty(Context.SECURITY_PRINCIPAL);
	}

	public void setProviderUrl(String providerUrl) {
		jndiProps.setProperty(Context.PROVIDER_URL, providerUrl);
	}

	public String getProviderUrl() {
		return jndiProps.getProperty(Context.PROVIDER_URL);
	}

	public String toString() {
		return "com.objectparadigms.framework.ejbfactory.config.EJBContainerConfig{" +
		"jndiProps=" +
		((jndiProps == null) ? null : ("size:" + jndiProps.size() + jndiProps)) +
		", friendlyName='" + friendlyName + "'" + "}";
	}
}