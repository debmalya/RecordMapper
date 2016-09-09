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

import java.util.LinkedList;

/**
 * @author debmalyajash
 *
 */
public class FieldMapperThreadPool {
	private final int nThreads;
	private final Thread[] threads;
	private final LinkedList<Runnable> queue;

	public FieldMapperThreadPool(int size) {
		this.nThreads = size;
		queue = new LinkedList<>();
		threads = new Thread[nThreads];
		for (int i = 0; i < nThreads; i++) {
			threads[i] =new Thread(new RunnableFieldMapper());
			threads[i].start();
		}

	}
}
