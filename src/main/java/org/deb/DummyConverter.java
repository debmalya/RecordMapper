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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.deb.dao.NameNValue;
import org.deb.task.DummyCallable;
import org.deb.task.DummyTask;

/**
 * @author debmalyajash
 *
 */
public class DummyConverter {

	private int numberOfFields = 10;
	ExecutorService executors;
	
	public DummyConverter(int noOfFields){
		numberOfFields = noOfFields;
		executors  = Executors.newFixedThreadPool(numberOfFields);
//		executors  = Executors.newCachedThreadPool();
	}

	/**
	 * Sequentially calling 10 times.
	 */
	public void singleThreaded() {
		for (int i = 0; i < numberOfFields; i++) {
			DummyTask dummyTask = new DummyTask();
			dummyTask.task();
		}
	}
	
	/**
	 * 
	 */
	public void multiThreaded(){
		List<Future<NameNValue>> submittedTaskList = new ArrayList<>();
		int delay = 20;
		for (int i = 0; i < numberOfFields; i++) {
			DummyCallable callable = new DummyCallable(delay);
			submittedTaskList.add(executors.submit(callable));
		}
		
		for (Future<NameNValue> eachTask: submittedTaskList){
			try {
				eachTask.get(delay*10, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * This is multi threaded execution, Number of threads is equal to number of fields.
	 */
	public void multiThreaded1(){
		List<Future<NameNValue>> submittedTaskList = new ArrayList<>();
		int delay = 20;
		for (int i = 0; i < numberOfFields; i++) {
			DummyCallable callable = new DummyCallable(delay);
			submittedTaskList.add(executors.submit(callable));
		}
		
		for (Future<NameNValue> eachTask: submittedTaskList){
			try {
				NameNValue nameNValue = eachTask.get();
				String attributeName = nameNValue.getName();
				String attributeValue = nameNValue.getValue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
	}
	
	public void multiThreadedBlocking(){
		
		int delay = 20;
		for (int i = 0; i < numberOfFields; i++) {
			DummyCallable callable = new DummyCallable(delay);
			try {
				NameNValue nameValue = executors.submit(callable).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	
	public void shutDown(){
		executors.shutdown();
	}
}
