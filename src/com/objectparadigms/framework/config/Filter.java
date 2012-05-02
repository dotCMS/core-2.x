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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

/**
 * <a href="Filter.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class Filter implements Serializable {
	private List attributeValues;
	private String elementName;

	public Filter(String elementName) {
		this(elementName, null);
	}

	public Filter(String elementName, List values) {
		this.elementName = elementName;
		attributeValues = new ArrayList();

		if (values != null) {
			attributeValues.addAll(values);
		}
	}

	public String getElementName() {
		return elementName;
	}

	public boolean isValid(Element element) {
		Iterator iter = attributeValues.iterator();

		while (iter.hasNext()) {
			AttributeValue value = (AttributeValue)iter.next();

			if (!element.getAttribute(value.getAttributeName()).equals(value.getValue())) {
				return false;
			}
		}

		return true;
	}

	public void addAttributeValues(String attributeName, String value) {
		attributeValues.add(new AttributeValue(attributeName, value));
	}

	public String toString() {
		return "com.cibc.objectparadigms.config.Filter{" + "attributeValues=" +
		((attributeValues == null) ? null
								   : ("size:" + attributeValues.size() +
		attributeValues)) + ", elementName='" + elementName + "'" + "}";
	}
}