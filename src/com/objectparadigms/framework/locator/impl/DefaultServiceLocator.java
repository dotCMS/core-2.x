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

package com.objectparadigms.framework.locator.impl;

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.util.JNDIUtil;
import com.objectparadigms.framework.locator.LocatorException;
import com.objectparadigms.framework.locator.ServiceLocator;

/**
 * <a href="DefaultServiceLocator.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.5 $
 *
 */
public class DefaultServiceLocator implements ServiceLocator {
	private static Log log = LogFactory.getLog(ServiceLocator.LOCATOR_LOG);
	private Context initialCtx;
	private Properties jndiProps;

	public DefaultServiceLocator(Properties jndiProps)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - Constructor");
		}

		try {
			this.jndiProps = jndiProps;
			initialCtx = new InitialContext(jndiProps);
		}
		catch (NamingException ne) {
			throw new LocatorException(ne.getMessage(), ne);
		}
	}

	public synchronized void flush() throws LocatorException {
		try {
			initialCtx = new InitialContext(jndiProps);
		}
		catch (NamingException ne) {
			throw new LocatorException(ne.getMessage(), ne);
		}
	}

	public DataSource locateDataSource(String jndiName)
		throws LocatorException {
		try {
			return (DataSource)_lookup(jndiName);
		}
		catch (ClassCastException e) {
			throw new LocatorException(e.getMessage(), e);
		}
	}

	public Queue locateQueue(String jndiName) throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateQueue()");
		}

		try {
			Queue queue = (Queue)_lookup(jndiName);

			return queue;
		}
		catch (ClassCastException re) {
			throw new LocatorException(re.getMessage(), re);
		}
	}

	public QueueConnectionFactory locateQueueFactory(String jndiName)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateQueueFactory()");
		}

		try {
			QueueConnectionFactory connFactory = (QueueConnectionFactory)_lookup(jndiName);

			return connFactory;
		}
		catch (ClassCastException re) {
			throw new LocatorException(re.getMessage(), re);
		}
	}

	public Topic locateTopic(String jndiName) throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateTopic()");
		}

		try {
			Topic topic = (Topic)_lookup(jndiName);

			return topic;
		}
		catch (ClassCastException re) {
			throw new LocatorException(re.getMessage(), re);
		}
	}

	public TopicConnectionFactory locateTopicFactory(String jndiName)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateTopicFactory()");
		}

		try {
			TopicConnectionFactory connFactory = (TopicConnectionFactory)_lookup(jndiName);

			return connFactory;
		}
		catch (ClassCastException re) {
			throw new LocatorException(re.getMessage(), re);
		}
	}

	private Object _lookup(String jndiName) throws LocatorException {
		try {
			return JNDIUtil.lookup(initialCtx, jndiName);
		}
		catch (NamingException ne) {
			throw new LocatorException(ne.getMessage(), ne);
		}
	}
}