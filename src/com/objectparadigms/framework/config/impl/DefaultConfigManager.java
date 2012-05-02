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

package com.objectparadigms.framework.config.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.objectparadigms.framework.config.ConfigException;
import com.objectparadigms.framework.config.ConfigManager;
import com.objectparadigms.framework.config.ConfigurationValue;
import com.objectparadigms.framework.config.Filters;

/**
 * <a href="DefaultConfigManager.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class DefaultConfigManager implements ConfigManager {
	private static Log log = LogFactory.getLog(CONFIG_LOG);
	private ConfigurationVisitor visitor;
	private Map configMap;

	public DefaultConfigManager() {
		visitor = new ConfigurationVisitor();
	}

	public List getAllConfigurations(String key) throws ConfigException {
		return getAllConfigurations(key, null);
	}

	public List getAllConfigurations(String key, Filters configFilters)
		throws ConfigException {
		ConfigKey configKey = new ConfigKey(key);
		Document dom = getConfigurationDOM(configKey);
		ConfigurationValueBuilder valueBuilder = new ConfigurationValueBuilder();
		visitor.visit(dom.getDocumentElement(), configKey, null, valueBuilder);

		return valueBuilder.getConfigValues();
	}

	public ConfigurationValue getConfiguration(String key)
		throws ConfigException {
		return getConfiguration(key, null);
	}

	public ConfigurationValue getConfiguration(String key, Filters configFilters)
		throws ConfigException {
		return (ConfigurationValue)getAllConfigurations(key, configFilters).get(0);
	}

	public synchronized void addManagedConfiguration(String projectId,
		Document configuration) {
		if (configMap == null) {
			configMap = new HashMap();
		}

		if (configMap.containsKey(projectId)) {
			log.warn(getClass().getName() +
				"::addManagedConfiguration() - Overriding configurations for: " +
				projectId);
		}

		configMap.put(projectId, configuration);
	}

	private Document getConfigurationDOM(ConfigKey key)
		throws ConfigException {
		Document dom = (Document)configMap.get(key.getProjectId());

		if (dom == null) {
			throw new ConfigException("No configurations found for project: " +
				key.getProjectId());
		}

		return dom;
	}
}