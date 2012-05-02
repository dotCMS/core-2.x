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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.objectparadigms.framework.config.ConfigException;
import com.objectparadigms.framework.config.Filters;

/**
 * <a href="ConfigurationVisitor.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class ConfigurationVisitor {
	public void visit(Element configElement, ConfigKey key, Filters filters,
		ConfigurationValueBuilder builder) throws ConfigException {
		String nextElementInPath = key.getNextKey();
		NodeList nodes = configElement.getElementsByTagName(nextElementInPath);
		int numNodes = nodes.getLength();

		if (numNodes == 0) {
			throw new ConfigException(
				"Unable to find specified configuration at: " + key);
		}

		for (int i = 0; i < numNodes; i++) {
			Node node = nodes.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE) {
				throw new ConfigException("Malformed configuration.  Node " +
					node.getNodeName() + " must be an ELEMENT");
			}

			Element element = (Element)node;
			boolean processNode = true;

			if (filters != null) {
				processNode = filters.isValid(element);
			}

			if (processNode) {
				//no more keys left in config path
				if (!key.hasMoreKeys()) {
					builder.addConfiguration(element);
				}
				else {
					visit(element, key, filters, builder);
				}
			}
		}
	}
}