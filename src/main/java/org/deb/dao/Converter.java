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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.deb.task.FieldMapper;

/**
 * Converts record from one from to another based on provided mapping.
 * 
 * @author debmalyajash
 *
 */
public class Converter {
	/**
	 * Converts an input record to output record based on the provided mapping.
	 * 
	 * @param record
	 *            - a record can be with delimiter or fixed length
	 * @param conversionMap
	 *            - mapping of input field to output field.
	 * @return map of field name and field value.
	 */
	public Map<String, String> convert(String record, Mapping conversionMap) {
		Map<String, String> rawFieldMap = new LinkedHashMap<>();
		if (conversionMap != null) {
			switch (conversionMap.getType()) {
			case DELIMITER:
				if (conversionMap.getFieldMapper() != null
						&& !conversionMap.getFieldMapper().isEmpty()) {
					String delimiter = conversionMap.getDelimiter();
					String[] values = null;
					if (conversionMap.getDelimiter().equals("|")) {
						values = record.split("\\|");
					} else {
						values = record.split(delimiter);
					}
					Iterator<Entry<String, FieldMapping>> mapIterator = conversionMap
							.getFieldMapper().entrySet().iterator();
					while (mapIterator.hasNext()) {
						Entry<String, FieldMapping> nextEntry = mapIterator
								.next();
						FieldMapping fieldMapping = nextEntry.getValue();
						rawFieldMap.put(nextEntry.getKey(),
								values[fieldMapping.fieldPosition - 1]);
					}
				}
				break;
			case FIXED_LENGTH:
				break;
			}
		}
		return rawFieldMap;
	}

	/**
	 * Converts an input record to output record based on the provided mapping.
	 * Here we are using executor service.
	 * 
	 * @param record
	 * @param conversionMap
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, String> convertExecutor(String record,
			Mapping conversionMap) throws InterruptedException,
			ExecutionException {
		Map<String, String> rawFieldMap = new LinkedHashMap<>();
		if (conversionMap != null) {
			ExecutorService executors = Executors
					.newFixedThreadPool(conversionMap.getFieldMapper().size());

			switch (conversionMap.getType()) {
			case DELIMITER:
				if (conversionMap.getFieldMapper() != null
						&& !conversionMap.getFieldMapper().isEmpty()) {
					String delimiter = conversionMap.getDelimiter();
					String[] values = null;
					if (conversionMap.getDelimiter().equals("|")) {
						values = record.split("\\|");
					} else {
						values = record.split(delimiter);
					}
					Iterator<Entry<String, FieldMapping>> mapIterator = conversionMap
							.getFieldMapper().entrySet().iterator();
					try {
						while (mapIterator.hasNext()) {
							Entry<String, FieldMapping> nextFieldMapping = mapIterator
									.next();
							String key = nextFieldMapping.getKey();
							FieldMapping value = nextFieldMapping.getValue();
							if (nextFieldMapping != null && key != null
									&& value != null) {
								FieldMapper mapper = new FieldMapper(
										value, values,
										record, conversionMap.getType());
								Future<NameNValue> mappedField = executors
										.submit(mapper);
								rawFieldMap.put(key, mappedField.get()
										.getValue());
							}

						}
					} catch (InterruptedException | ExecutionException ie) {
						throw ie;
					} finally {
						executors.shutdown();
					}

					if (executors != null) {
						executors.awaitTermination(1, TimeUnit.MILLISECONDS);
						if (executors.isTerminated()) {
							// Terminated successfully.
						} else {
							// what to do here
						}
					}
				}
				break;
			case FIXED_LENGTH:
				break;
			}

		}
		return rawFieldMap;
	}
}