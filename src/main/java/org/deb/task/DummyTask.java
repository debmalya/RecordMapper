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

import org.deb.dao.NameNValue;

/**
 * This is a dummy task. Want to measure what's the minimum duration of the task
 * so that multi threading will improve performance.
 * 
 * @author debmalyajash
 *
 */
public class DummyTask {

	/**
	 * Default delay for the dummy task.
	 */
	private static final int DEFAULT_DELAY = 20;
	private int delay = DEFAULT_DELAY;

	public DummyTask() {

	}

	public DummyTask(int delay) {
		this.delay = delay;
	}

	public NameNValue task() {
		NameNValue nameNValue = new NameNValue("RECORD_TIMESTAMP", Long.toString(System.currentTimeMillis()));
		try {
			Thread.sleep(delay);
		} catch (InterruptedException ignore) {

		}
		return nameNValue;
	}
}
