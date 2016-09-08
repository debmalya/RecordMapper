/**
 * Copyright 2015-2016 Debmalya Jash
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deb.dao;

/**
 * How a field will be mapped, field name, field position (for delimited
 * records) or startPosition and endPosition for fixed length record.
 * 
 * @author debmalyajash
 */
public class FieldMapping {

	public String fieldName;
	/**
	 * Applicable in case 'DELIMITER' record type.
	 */
	public int fieldPosition;
	/**
	 * Applicable in case of FIXED length record.
	 */
	public int startPosition;
	/**
	 * Applicable in case of FIXED length record.
	 */
	public int endPosition;

	/**
	 * 
	 */
	public FieldMapping() {
	}

	public FieldMapping(String fieldName, int fieldPosition, int startPosition,
			int endPosition) {
		this.fieldName = fieldName;
		this.fieldPosition = fieldPosition;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

}