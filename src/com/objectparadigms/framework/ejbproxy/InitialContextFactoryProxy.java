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

package com.objectparadigms.framework.ejbproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import com.objectparadigms.framework.ejbproxy.config.ProxyConfigs;

/**
 * <a href="InitialContextFactoryProxy.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class InitialContextFactoryProxy implements InitialContextFactory {
	private InitialContextFactory delegate;

	public InitialContextFactoryProxy() {
		String realFactory = ProxyConfigs.getInstance().getContextConfig()
										 .getRealFactory();

		try {
			delegate = (InitialContextFactory)Class.forName(realFactory)
												   .newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	public Context getInitialContext(Hashtable environment)
		throws NamingException {
		Context context = delegate.getInitialContext(environment);
		InvocationHandler contextHandler = new ContextInvocationHandler(context);

		return (Context)Proxy.newProxyInstance(context.getClass()
													  .getClassLoader(),
			new Class[] { Context.class }, contextHandler);
	}
}