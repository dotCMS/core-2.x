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

package com.objectparadigms.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <a href="EfficientStringTokenizer.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class EfficientStringTokenizer implements Enumeration {
	private static final String _DEFAULT_TOKEN = " ";
	private String toTokenize;
	private String token;
	private int currentIndex;

	public EfficientStringTokenizer(String str) {
		this(str, _DEFAULT_TOKEN);
	}

	public EfficientStringTokenizer(String str, String token) {
		toTokenize = str;
		this.token = token;
	}

	public boolean hasMoreElements() {
		return (currentIndex != -1);
	}

	public boolean hasMoreTokens() {
		return hasMoreElements();
	}

	public Object nextElement() {
		return nextToken();
	}

	public String nextToken() {
		if (currentIndex == -1) {
			throw new NoSuchElementException("No more tokens remaining");
		}

		int nextIndex = toTokenize.indexOf(token, currentIndex);
		String next = null;

		if (nextIndex == -1) {
			next = toTokenize.substring(currentIndex, toTokenize.length());
			currentIndex = nextIndex;
		}
		else {
			next = toTokenize.substring(currentIndex, nextIndex);
			currentIndex = nextIndex + token.length();
		}

		return next;
	}

	public String toString() {
		return "com.objectparadigms.util.EfficientStringTokenizer{" +
		"toTokenize='" + toTokenize + "'" + ", token='" + token + "'" +
		", currentIndex=" + currentIndex + "}";
	}
}