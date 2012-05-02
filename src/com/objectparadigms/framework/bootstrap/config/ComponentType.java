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

package com.objectparadigms.framework.bootstrap.config;

/**
 * <a href="ComponentType.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.6 $
 *
 */
public class ComponentType {
	private static final String _STATIC_TYPE = "static";
	private static final String _DIGESTER_TYPE = "digester";
	public static final ComponentType STATIC_INITIALIZER = new ComponentType(_STATIC_TYPE);
	public static final ComponentType DIGESTER_INITIALIZER = new ComponentType(_DIGESTER_TYPE);

	public static ComponentType parse(String value) {
		if (value.equals(_STATIC_TYPE)) {
			return STATIC_INITIALIZER;
		}

		if (value.equals(_DIGESTER_TYPE)) {
			return DIGESTER_INITIALIZER;
		}

		throw new IllegalArgumentException("Invalid component _type: " + value);
	}

	public String getType() {
		return _type;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		try {
			ComponentType componentType = (ComponentType)o;

			return componentType.getType().equals(getType());
		}
		catch (ClassCastException cce) {
		}

		return false;
	}

	public String toString() {
		return "com.objectparadigms.framework.bootstrap.config.ComponentType{" +
		"type='" + _type + "'" + "}";
	}

	private ComponentType(String type) {
		_type = type;
	}

	private String _type;
}