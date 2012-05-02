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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.Attributes;

/**
 * <a href="BootstrapRuleSet.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.6 $
 *
 */
public class BootstrapRuleSet extends RuleSetBase {
	public void addRuleInstances(Digester digester) {
		List list = new ArrayList();
		digester.push(list);
		digester.addRule("startup/component", new _ComponentRule());
	}

	private class _ComponentRule extends Rule {
		private Map _processors;

		_ComponentRule() {
			_processors = new HashMap();
			_processors.put(ComponentType.DIGESTER_INITIALIZER,
				new DigesterComponentProcessor());
			_processors.put(ComponentType.STATIC_INITIALIZER,
				new StaticComponentProcessor());
		}

		public void begin(String namespace, String name, Attributes attributes)
			throws Exception {
			ComponentType type = ComponentType.parse(attributes.getValue("type"));
			ComponentProcessor processor = (ComponentProcessor)_processors.get(type);
			Component component = processor.process(attributes);
			List components = (List)digester.peek();
			components.add(component);
		}
	}
}