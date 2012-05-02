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

/**
 * <a href="ServiceMode.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class ServiceMode {
	private static final String _SIMULATOR = "simulator";
	private static final String _PRODUCTION = "production";
	public static ServiceMode PRODUCTION = new ServiceMode(_PRODUCTION);
	public static ServiceMode SIMULATOR = new ServiceMode(_SIMULATOR);
	private String mode;

	private ServiceMode(String mode) {
		this.mode = mode;
	}

	public static ServiceMode parse(String value) {
		if (value.equals(_PRODUCTION)) {
			return PRODUCTION;
		}

		return SIMULATOR;
	}

	public String getMode() {
		return mode;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		try {
			ServiceMode sMode = (ServiceMode)obj;

			return sMode.getMode().equals(getMode());
		}
		catch (ClassCastException cce) {
		}

		return false;
	}
}