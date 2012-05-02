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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.objectparadigms.framework.config.ConfigException;
import com.objectparadigms.framework.config.ConfigManager;
import com.objectparadigms.framework.config.ConfigManagerFactory;
import com.objectparadigms.framework.config.ConfigurationValue;
import com.objectparadigms.framework.config.Filter;
import com.objectparadigms.framework.config.Filters;
import com.objectparadigms.util.StackTraceUtil;

/**
 * <a href="ServiceFactoryConfig.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.5 $
 *
 */
public class ServiceFactoryConfig {
	private static final String _CONFIG_ID = "service-factory";
	private static final String _SERVICE_GROUP_PARAM = "service-group";
	private static final String _SERVICE_MODE_PARAM = "mode";
	private static final String _NAME_PARAM = "name";
	private static final String _SERVICE_PARAM = "service";
	private static final String _CACHED_PARAM = "cached";
	private static final String _INTERFACE_PARAM = "interface";
	private static final String _IMPL_PARAM = "impl";
	private static final String _SERVICE_FACTORY_GROUP_KEY = _CONFIG_ID +
		ConfigManager.SEPARATOR + "active-groups" + ConfigManager.SEPARATOR +
		"group";
	private static final String _SERVICE_GROUP_MODE_KEY = _CONFIG_ID +
		ConfigManager.SEPARATOR + _SERVICE_GROUP_PARAM +
		ConfigManager.SEPARATOR + _SERVICE_MODE_PARAM;
	private static final String _SERVICE_KEY = _CONFIG_ID +
		ConfigManager.SEPARATOR + _SERVICE_GROUP_PARAM +
		ConfigManager.SEPARATOR + _SERVICE_PARAM;
	private static ServiceFactoryConfig instance;
	private Map services;

	private ServiceFactoryConfig() {
		services = new HashMap();
		init();
	}

	public static synchronized ServiceFactoryConfig getInstance() {
		if (instance == null) {
			instance = new ServiceFactoryConfig();
		}

		return instance;
	}

	public ServiceConfig get(String serviceName) {
		ServiceConfig config = (ServiceConfig)services.get(serviceName);

		if (config == null) {
			throw new IllegalArgumentException("No such service configured: " +
				serviceName);
		}

		return config;
	}

	public String toString() {
		return "com.objectparadigms.framework.servicefactory.config.ServiceFactoryConfig{" +
		"services=" +
		((services == null) ? null : ("size:" + services.size() + services)) +
		"}";
	}

	void add(ServiceConfig config) {
		services.put(config.getName(), config);
	}

	private void init() {
		try {
			ConfigManager cfgMgr = ConfigManagerFactory.getInstance()
													   .getManager();

			//determine the active service groups
			List groups = cfgMgr.getAllConfigurations(_SERVICE_FACTORY_GROUP_KEY);
			Iterator groupsIter = groups.iterator();

			//iterate through each service group
			while (groupsIter.hasNext()) {
				ConfigurationValue value = (ConfigurationValue)groupsIter.next();

				//create a service group filter
				String activeGroup = value.getElementValue();
				Filter groupFilter = new Filter(_SERVICE_GROUP_PARAM);
				groupFilter.addAttributeValues(_NAME_PARAM, activeGroup);

				//determine the service mode for the specified service group
				Filters groupModeFilter = new Filters();
				groupModeFilter.addFilter(groupFilter);

				ServiceMode mode = ServiceMode.parse(cfgMgr.getConfiguration(
							_SERVICE_GROUP_MODE_KEY, groupModeFilter)
														   .getElementValue());

				//create filter for the service mode
				Filter modeFilter = new Filter(_SERVICE_PARAM);
				modeFilter.addAttributeValues(_SERVICE_MODE_PARAM,
					mode.getMode());

				Filters serviceFilters = new Filters();
				serviceFilters.addFilter(groupFilter);
				serviceFilters.addFilter(modeFilter);

				//retrieve configurations for all services belonging to the current service group
				//and operating under the specified service mode (e.g. production or simulator)
				Iterator servicesIter = cfgMgr.getAllConfigurations(_SERVICE_KEY,
						serviceFilters).iterator();

				while (servicesIter.hasNext()) {
					ConfigurationValue serviceInfo = (ConfigurationValue)servicesIter.next();
					Properties props = serviceInfo.getAttributes();
					ServiceConfig config = new ServiceConfig();
					config.setCached(Boolean.valueOf(props.getProperty(
								_CACHED_PARAM)).booleanValue());
					config.setName(props.getProperty(_NAME_PARAM));
					config.setMode(mode.getMode());
					config.setInterfaceName(props.getProperty(_INTERFACE_PARAM));
					config.setImpl(props.getProperty(_IMPL_PARAM));
					add(config);
				}
			}
		}
		catch (ConfigException ce) {
			throw new IllegalStateException(
				"Unable to initialize Service Factory Configurations: " +
				StackTraceUtil.getStackTrace(ce));
		}
	}
}