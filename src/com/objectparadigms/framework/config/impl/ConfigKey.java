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

import com.objectparadigms.framework.config.ConfigManager;
import com.objectparadigms.util.EfficientStringTokenizer;

class ConfigKey {
	private EfficientStringTokenizer tokenizer;
	private String projectId;

	ConfigKey(String key) {
		tokenizer = new EfficientStringTokenizer(key, ConfigManager.SEPARATOR);

		if (tokenizer.hasMoreTokens()) {
			projectId = tokenizer.nextToken();
		}
		else {
			throw new IllegalArgumentException(
				"Invalid config key, must contain a project id: " + key);
		}
	}

	public String toString() {
		return "com.objectparadigms.config.impl.ConfigKey{" + "projectId='" +
		projectId + "'" + ", tokenizer=" + tokenizer + "}";
	}

	String getNextKey() {
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}

		//if no more keys...
		return null;
	}

	String getProjectId() {
		return projectId;
	}

	boolean hasMoreKeys() {
		return tokenizer.hasMoreTokens();
	}
}