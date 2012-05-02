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

package com.objectparadigms.framework.config;

import java.io.Serializable;
import java.util.Properties;

/**
 * <a href="ConfigurationValue.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class ConfigurationValue implements Serializable {
	private Properties attributes;
	private String elementName;
	private String elementValue;

	public ConfigurationValue(String name, String value) {
		this(name, value, null);
	}

	public ConfigurationValue(String name, String value, Properties attribs) {
		elementName = name;
		elementValue = value;
		attributes = attribs;
	}

	public Properties getAttributes() {
		return attributes;
	}

	public String getElementName() {
		return elementName;
	}

	public String getElementValue() {
		return elementValue;
	}

	public void addAttributes(String name, String value) {
		if (attributes == null) {
			attributes = new Properties();
		}

		attributes.put(name, value);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		try {
			ConfigurationValue value = (ConfigurationValue)obj;

			return value.getElementName().equals(getElementName()) &&
			value.getElementValue().equals(getElementValue());
		}
		catch (ClassCastException cce) {
		}

		return false;
	}

	public String toString() {
		return "com.objectparadigms.config.ConfigurationValue{" +
		"attributes=" +
		((attributes == null) ? null : ("size:" + attributes.size() +
		attributes)) + ", elementName='" + elementName + "'" +
		", elementValue='" + elementValue + "'" + "}";
	}
}