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
package org.deb.task;

import java.util.concurrent.Callable;

import org.deb.dao.FieldMapping;
import org.deb.dao.NameNValue;
import org.deb.dao.RecordType;

/**
 * @author debmalyajash
 *
 */
public class CallableFieldMapper implements Callable<NameNValue>{
	
	private FieldMapping fieldMapping;
	
	private String[] values;
	
	private String record;
	
	private RecordType recordType;
	
	private String key;
	

	public CallableFieldMapper(FieldMapping fieldMapping,String[] values,String record,RecordType recordType,String key) {
		this.fieldMapping = fieldMapping;
		this.record = record;
		this.recordType = recordType;
		this.values = values;
		this.key = key;
	}

	public CallableFieldMapper(){
		
	}


	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public NameNValue call() throws Exception {
//		fieldMapping
		switch(recordType){
		case DELIMITER:
			return new NameNValue(key, values[fieldMapping.fieldPosition - 1]);
		
		case FIXED_LENGTH:
			break;
		}
		return null;
	}



	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
	}




}
