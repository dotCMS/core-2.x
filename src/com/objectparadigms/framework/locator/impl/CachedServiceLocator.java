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

import java.util.Map;
import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.sql.DataSource;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.objectparadigms.framework.locator.LocatorException;
import com.objectparadigms.framework.locator.ServiceLocator;

/**
 * <a href="CachedServiceLocator.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.6 $
 *
 */
public class CachedServiceLocator implements ServiceLocator {
	private static final String _HOME_PREFIX = "EJBH_";
	private static final String _LOCAL_HOME_PREFIX = "EJBLH_";
	private static final String _DATASOURCE_PREFIX = "DS_";
	private static final String _TOPIC_PREFIX = "JMST_";
	private static final String _TOPIC_FACTORY_PREFIX = "JMSTF_";
	private static final String _QUEUE_PREFIX = "JMSQ_";
	private static final String _QUEUE_FACTORY_PREFIX = "JMSQF_";
	private static Log log = LogFactory.getLog(ServiceLocator.LOCATOR_LOG);
	private Map referenceCache;
	private ServiceLocator delegate;

	public CachedServiceLocator(ServiceLocator locator) {
		if (log.isDebugEnabled()) {
			log.debug(getClass().getName() + " - Constructor");
		}

		delegate = locator;
		referenceCache = new FastHashMap();
		((FastHashMap)referenceCache).setFast(true);
	}

	public CachedServiceLocator(Properties props) throws LocatorException {
		this(new DefaultServiceLocator(props));
	}

	public synchronized void flush() throws LocatorException {
		delegate.flush();
		referenceCache.clear();
	}

	public DataSource locateDataSource(String jndiName)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateDataSource()");
		}

		String cacheName = _DATASOURCE_PREFIX + jndiName;
		DataSource ds = (DataSource)referenceCache.get(cacheName);

		if (ds == null) {
			if (log.isDebugEnabled()) {
				log.debug(getClass().getName() + " - locateHome() - " +
					jndiName + " not found in cache, " +
					"retrieving from JNDI");
			}

			ds = delegate.locateDataSource(jndiName);
			referenceCache.put(cacheName, ds);
		}

		return ds;
	}

	public Queue locateQueue(String jndiName) throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateQueue()");
		}

		String cacheName = _QUEUE_PREFIX + jndiName;
		Queue queue = (Queue)referenceCache.get(cacheName);

		if (queue == null) {
			if (log.isDebugEnabled()) {
				log.debug(getClass().getName() + " - locateQueue() - " +
					jndiName + " not found in cache, " +
					"retrieving from JNDI");
			}

			queue = delegate.locateQueue(jndiName);
			referenceCache.put(cacheName, queue);
		}

		return queue;
	}

	public QueueConnectionFactory locateQueueFactory(String jndiName)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateQueueFactory()");
		}

		String cacheName = _QUEUE_FACTORY_PREFIX + jndiName;
		QueueConnectionFactory obj = (QueueConnectionFactory)referenceCache.get(cacheName);

		if (obj == null) {
			if (log.isDebugEnabled()) {
				log.debug(getClass().getName() + " - locateQueueFactory() - " +
					jndiName + " not found in cache, " +
					"retrieving from JNDI");
			}

			obj = delegate.locateQueueFactory(jndiName);
			referenceCache.put(cacheName, obj);
		}

		return obj;
	}

	public Topic locateTopic(String jndiName) throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateTopic()");
		}

		String cacheName = _TOPIC_PREFIX + jndiName;
		Topic topic = (Topic)referenceCache.get(cacheName);

		if (topic == null) {
			if (log.isDebugEnabled()) {
				log.debug(getClass().getName() + " - locateTopic() - " +
					jndiName + " not found in cache, " +
					"retrieving from JNDI");
			}

			topic = delegate.locateTopic(jndiName);
			referenceCache.put(cacheName, topic);
		}

		return topic;
	}

	public TopicConnectionFactory locateTopicFactory(String jndiName)
		throws LocatorException {
		if (log.isTraceEnabled()) {
			log.trace(getClass().getName() + " - locateTopicFactory()");
		}

		String cacheName = _TOPIC_FACTORY_PREFIX + jndiName;
		TopicConnectionFactory obj = (TopicConnectionFactory)referenceCache.get(cacheName);

		if (obj == null) {
			if (log.isDebugEnabled()) {
				log.debug(getClass().getName() + " - locateTopicFactory() - " +
					jndiName + " not found in cache, " +
					"retrieving from JNDI");
			}

			obj = delegate.locateTopicFactory(jndiName);
			referenceCache.put(cacheName, obj);
		}

		return obj;
	}
}