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
package org.deb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.deb.dao.FieldMapping;
import org.deb.dao.Mapping;
import org.deb.dao.NameNValue;
import org.deb.task.CallableFieldMapper;

/**
 * Converts record from one from to another based on provided mapping.
 * 
 * @author debmalyajash
 *
 */
public class Converter {
	ExecutorService executors;
	
	private ThreadPoolExecutor threadPoolExecutor;
	private BlockingQueue<Runnable> workQueue = null; 
	
	private ExecutorService cachedThreadExecutors;
	
	private ExecutorService workStealingPool;
	
	private ExecutorService singleThreadedExecutorService;
	

	public Converter(int numberOfFields) {
		executors = Executors
				.newFixedThreadPool(numberOfFields);
		
		workQueue = new ArrayBlockingQueue<Runnable>(numberOfFields);
		threadPoolExecutor = new ThreadPoolExecutor(numberOfFields, numberOfFields*2, 10, TimeUnit.MILLISECONDS, workQueue , Executors.defaultThreadFactory() );
		threadPoolExecutor.prestartAllCoreThreads();
		
		cachedThreadExecutors = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
		
		workStealingPool = Executors.newWorkStealingPool();
		
		singleThreadedExecutorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
	}

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
								CallableFieldMapper mapper = new CallableFieldMapper(value,
										values, record, conversionMap.getType(),key);
								Future<NameNValue> mappedField = executors
										.submit(mapper);
								rawFieldMap.put(key, mappedField.get()
										.getValue());
							}

						}
					} catch (InterruptedException | ExecutionException ie) {
						throw ie;
					} finally {
//						executors.shutdown();
					}

//					if (executors != null) {
//						executors.awaitTermination(1, TimeUnit.MILLISECONDS);
//						if (executors.isTerminated()) {
//							// Terminated successfully.
//						} else {
//							// what to do here
//						}
//					}
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
	 * Here we are using executor service with concurrent hash map.
	 * 
	 * @param record
	 * @param conversionMap
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, String> convertExecutorConcurrentHashMap(String record,
			Mapping conversionMap) throws InterruptedException,
			ExecutionException {
		Map<String, String> rawFieldMap = new ConcurrentHashMap<>();
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
					try {
						while (mapIterator.hasNext()) {
							Entry<String, FieldMapping> nextFieldMapping = mapIterator
									.next();
							String key = nextFieldMapping.getKey();
							FieldMapping value = nextFieldMapping.getValue();
							if (nextFieldMapping != null && key != null
									&& value != null) {
								CallableFieldMapper mapper = new CallableFieldMapper(value,
										values, record, conversionMap.getType(),key);
								
								Future<NameNValue> mappedField = executors
										.submit(mapper);
								rawFieldMap.put(key, mappedField.get()
										.getValue());
							}

						}
					} catch (InterruptedException | ExecutionException ie) {
						throw ie;
					} finally {

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
	 * Here we are using parallel with concurrent hash map.
	 * 
	 * @param record
	 * @param conversionMap
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, String> convertList(String record,
			Mapping conversionMap) throws InterruptedException,
			ExecutionException {
		Map<String, String> rawFieldMap = new ConcurrentHashMap<>();
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
					try {
						List<Future<NameNValue>> submittedTaskList = new ArrayList<>();
						
						while (mapIterator.hasNext()) {
							Entry<String, FieldMapping> nextFieldMapping = mapIterator
									.next();
							String key = nextFieldMapping.getKey();
							FieldMapping fieldMapping = nextFieldMapping.getValue();
							if (nextFieldMapping != null && key != null
									&& fieldMapping != null) {
								
								CallableFieldMapper mapper = new CallableFieldMapper(fieldMapping,
										values, record, conversionMap.getType(),key);
								Future<NameNValue> mappedField = executors
										.submit(mapper);
								submittedTaskList.add(mappedField);	
							}
						}
						
						for (Future<NameNValue> eachField:submittedTaskList){
							NameNValue eachFieldDetails = eachField.get();
							rawFieldMap.put(eachFieldDetails.getName(), eachFieldDetails.getValue());
						}
					} catch (Throwable ie) {
						throw ie;
					} finally {

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
	 * Here we are using parallel with concurrent hash map.
	 * 
	 * @param record
	 * @param conversionMap
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, String> convertUsingStealingPool(String record,
			Mapping conversionMap) throws InterruptedException,
			ExecutionException {
		Map<String, String> rawFieldMap = new ConcurrentHashMap<>();
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
					try {
						List<Future<NameNValue>> submittedTaskList = new ArrayList<>();
						
						while (mapIterator.hasNext()) {
							Entry<String, FieldMapping> nextFieldMapping = mapIterator
									.next();
							String key = nextFieldMapping.getKey();
							FieldMapping fieldMapping = nextFieldMapping.getValue();
							if (nextFieldMapping != null && key != null
									&& fieldMapping != null) {
								
								CallableFieldMapper mapper = new CallableFieldMapper(fieldMapping,
										values, record, conversionMap.getType(),key);
								Future<NameNValue> mappedField = workStealingPool
										.submit(mapper);
								submittedTaskList.add(mappedField);	
							}
						}
						
						for (Future<NameNValue> eachField:submittedTaskList){
							NameNValue eachFieldDetails = eachField.get();
							rawFieldMap.put(eachFieldDetails.getName(), eachFieldDetails.getValue());
						}
					} catch (Throwable ie) {
						throw ie;
					} finally {

					}


				}
				break;
			case FIXED_LENGTH:
				break;
			}

		}
		return rawFieldMap;
	}
	
	public void shutDown(){
		executors.shutdown();
		threadPoolExecutor.shutdown();
		workStealingPool.shutdown();
		
	}
	
	/**
	 * Converts an input record to output record based on the provided mapping.
	 * Here we are using parallel with concurrent hash map.
	 * 
	 * @param record
	 * @param conversionMap
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, String> convertUsingCachedPool(String record,
			Mapping conversionMap) throws InterruptedException,
			ExecutionException {
		Map<String, String> rawFieldMap = new ConcurrentHashMap<>();
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
					try {
						List<Future<NameNValue>> submittedTaskList = new ArrayList<>();
						
						while (mapIterator.hasNext()) {
							Entry<String, FieldMapping> nextFieldMapping = mapIterator
									.next();
							String key = nextFieldMapping.getKey();
							FieldMapping fieldMapping = nextFieldMapping.getValue();
							if (nextFieldMapping != null && key != null
									&& fieldMapping != null) {
								
								CallableFieldMapper mapper = new CallableFieldMapper(fieldMapping,
										values, record, conversionMap.getType(),key);
								Future<NameNValue> mappedField = cachedThreadExecutors
										.submit(mapper);
								submittedTaskList.add(mappedField);	
							}
						}
						
						for (Future<NameNValue> eachField:submittedTaskList){
							NameNValue eachFieldDetails = eachField.get();
							rawFieldMap.put(eachFieldDetails.getName(), eachFieldDetails.getValue());
						}
					} catch (Throwable ie) {
						throw ie;
					} finally {

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
