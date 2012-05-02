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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.objectparadigms.framework.config.ConfigException;
import com.objectparadigms.framework.config.ConfigurationValue;

class ConfigurationValueBuilder {
	private List configValues;

	ConfigurationValueBuilder() {
	}

	List getConfigValues() {
		return configValues;
	}

	void addConfiguration(Element element) throws ConfigException {
		if (configValues == null) {
			configValues = new ArrayList();
		}

		String elementName = element.getTagName();
		String elementValue = null;

		if (element.hasChildNodes()) {
			NodeList children = element.getChildNodes();

			if (children.getLength() > 1) {
				throw new ConfigException(
					"Invalid Configuration: Selected element, " + elementName +
					", contains more than 1 child.  Not a valid endpoint.");
			}

			Node child = children.item(0);

			if (child.getNodeType() != Node.TEXT_NODE) {
				throw new ConfigException(
					"Invalid Configuration: Child of selected element, " +
					elementName + ", must be a TEXT node.");
			}

			Text text = (Text)child;
			elementValue = text.getNodeValue();
		}

		ConfigurationValue value = new ConfigurationValue(elementName,
				elementValue);
		NamedNodeMap attributes = element.getAttributes();
		int numAttributes = attributes.getLength();

		for (int i = 0; i < numAttributes; i++) {
			Attr attribute = (Attr)attributes.item(i);
			value.addAttributes(attribute.getName(), attribute.getValue());
		}

		configValues.add(value);
	}
}