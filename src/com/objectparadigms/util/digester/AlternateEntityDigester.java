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

package com.objectparadigms.util.digester;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * <a href="AlternateEntityDigester.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Michael C. Han
 * @version $Revision: 1.4 $
 *
 */
public class AlternateEntityDigester extends Digester {
	public AlternateEntityDigester(XMLReader reader) {
		super(reader);
	}

	public synchronized XMLReader getXMLReader() throws SAXException {
		if (reader == null) {
			return super.getXMLReader();
		}

		reader.setContentHandler(this);
		reader.setDTDHandler(this);
		reader.setErrorHandler(this);

		//we do not want to override the entity resolver already established
		//on the reader.
		return reader;
	}

	protected void configure() {
		reader.setEntityResolver(getEntityResolver());
	}
}