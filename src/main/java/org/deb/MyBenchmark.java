/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.deb;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.deb.dao.Converter;
import org.deb.dao.FieldMapping;
import org.deb.dao.Mapping;
import org.deb.dao.RecordType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

// @State annotation defines the scope in which an instance of a given class will be available. 
// Scope.Benchmark - 	An instance will be shared across all threads running the same test. 
// Could be used to test multithreaded performance of a state object (or just mark your benchmark with this scope).
@State(Scope.Benchmark)
public class MyBenchmark {

	private final String CDR_CSV = "12345678,87654321,1473349724,70,12345678,2683,1,019,Voice";

	private final String CDR_FIXED_LENGTH = "12345678876543211473349724701234567826831019Voice";

	/**
	 * Mapping for CSV delimited record.
	 */
	private Mapping delimitedCDRMapping;

	/**
	 * Mapping for fixed length CDR.
	 */
	private Mapping fixedLengthCDRMapping;
	
	/**
	 * Converts input records to output record map.
	 */
	private Converter converter;

	/**
	 * Like JUnit tests, you can annotate your state class methods with @Setup
	 * and @TearDown annotations (these methods called fixtures in JMH
	 * documentation. You can have any number of setup/teardown methods. These
	 * methods do not contribute anything to test times (but Level.Invocation
	 * may affect precision of measurements).
	 * 
	 * Level.Invocation	Before/after every method call (this level is not recommended until you know what you are doing).
	 * Level.Trial	This is a default level. Before/after entire benchmark run (group of iteration).
	 * Level.Iteration	Before/after an iteration (group of invocations)
	 */
	@Setup
	public void setUp() {
		Map<String,FieldMapping> delmitedFieldMapping = new LinkedHashMap<>();
		
		FieldMapping delimitedCDRAParty = new FieldMapping("AParty",1,-1,-1);
		FieldMapping delimitedCDRBParty = new FieldMapping("BParty",2,-1,-1);
		FieldMapping delimitedCDRTimeOfCall = new FieldMapping("TimeOfCall",3,-1,-1);
		FieldMapping delimitedCDRCallDuration = new FieldMapping("CallDuration",4,-1,-1);
		FieldMapping delimitedCDRBillingPhoneNumber = new FieldMapping("BillingPhoneNumber",5,-1,-1);
		FieldMapping delimitedCDRBExchange = new FieldMapping("Exchange",6,-1,-1);
		FieldMapping delimitedCDRSequence = new FieldMapping("Sequence",7,-1,-1);
		FieldMapping delimitedCDRAdditional = new FieldMapping("AdditionalDigit",8,-1,-1);
		FieldMapping delimitedCDRCallType = new FieldMapping("CallType",9,-1,-1);
		
		delmitedFieldMapping.put("AParty", delimitedCDRAParty);
		delmitedFieldMapping.put("BParty", delimitedCDRBParty);
		delmitedFieldMapping.put("TimeOfCall", delimitedCDRTimeOfCall);
		delmitedFieldMapping.put("Duration", delimitedCDRCallDuration);
		delmitedFieldMapping.put("BillingPhoneNumber",delimitedCDRBillingPhoneNumber);
		delmitedFieldMapping.put("Exchange",delimitedCDRBExchange);
		delmitedFieldMapping.put("Sequence",delimitedCDRSequence);
		delmitedFieldMapping.put("AdditionalDigit",delimitedCDRAdditional);
		delmitedFieldMapping.put("CallType",delimitedCDRCallType);
		
		delimitedCDRMapping = new Mapping(RecordType.DELIMITER, delmitedFieldMapping,",");
		
		Map<String,FieldMapping> fixedFieldMapping = new LinkedHashMap<>();
		
		FieldMapping fixedCDRAParty = new FieldMapping("AParty",-1,0,7);
		FieldMapping fixedCDRBParty = new FieldMapping("BParty",-1,8,15);
		FieldMapping fixedCDRTimeOfCall = new FieldMapping("TimeOfCall",-1,16,25);
		FieldMapping fixedCDRCallDuration = new FieldMapping("CallDuration",-1,26,27);
		FieldMapping fixedCDRBillingPhoneNumber = new FieldMapping("BillingPhoneNumber",-1,28,36);
		FieldMapping fixedCDRBExchange = new FieldMapping("Exchange",-1,37,41);
		FieldMapping fixedCDRSequence = new FieldMapping("Sequence",-1,42,42);
		FieldMapping fixedCDRAdditional = new FieldMapping("AdditionalDigit",-1,43,45);
		FieldMapping fixedCDRCallType = new FieldMapping("CallType",-1,46,51);
		
		fixedFieldMapping.put("AParty", fixedCDRAParty);
		fixedFieldMapping.put("BParty", fixedCDRBParty);
		fixedFieldMapping.put("TimeOfCall", fixedCDRTimeOfCall);
		fixedFieldMapping.put("Duration", fixedCDRCallDuration);
		fixedFieldMapping.put("BillingPhoneNumber",fixedCDRBillingPhoneNumber);
		fixedFieldMapping.put("Exchange",fixedCDRBExchange);
		fixedFieldMapping.put("Sequence",fixedCDRSequence);
		fixedFieldMapping.put("AdditionalDigit",fixedCDRAdditional);
		fixedFieldMapping.put("CallType",fixedCDRCallType);
		
		fixedLengthCDRMapping = new Mapping(RecordType.FIXED_LENGTH,fixedFieldMapping,null);
		
		converter = new Converter(fixedFieldMapping.size());

	}

	@TearDown
	public void reset(){
		converter.shutDown();
	}
	@Benchmark
	public void testSingleThreaded() {
		converter.convert(CDR_CSV, delimitedCDRMapping);
	}
	
	@Benchmark
	public void testExecutors() {
		try {
			converter.convertExecutor(CDR_CSV, delimitedCDRMapping);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

// Other Scopes Scope.Thread This is a default state. An instance will be
// allocated for each thread running the given test.
// Scope.Group An instance will be allocated per thread group.
// For further study please refer, http://java-performance.info/jmh/