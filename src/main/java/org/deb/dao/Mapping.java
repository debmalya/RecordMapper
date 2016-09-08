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

import java.util.Map;

/**
 * @author debmalyajash
 * Describes how to map an input record to output record.
 * 
 * 
 */
public class Mapping {
	/**
	 * Record type can be "DELIMITER","FIXED".
	 */
	private RecordType type;
	/**
	 * Field mapping.
	 */
	private Map<String,FieldMapping> fieldMapper;
	
	/**
	 * Record delimiter in case type is DELIMITER.
	 */
	private String delimiter;
	
	/**
	 * Constructor
	 * @param type - record type.
	 * @param fieldMapper - mapping for each field.
	 */
	public Mapping(RecordType type, Map<String, FieldMapping> fieldMapper,String delimiter) {
		super();
		this.type = type;
		this.fieldMapper = fieldMapper;
		this.delimiter = delimiter;
	}

	/**
	 * @return the type
	 */
	public RecordType getType() {
		return type;
	}

	/**
	 * @return the fieldMapper
	 */
	public Map<String, FieldMapping> getFieldMapper() {
		return fieldMapper;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}
	
	
	
}
