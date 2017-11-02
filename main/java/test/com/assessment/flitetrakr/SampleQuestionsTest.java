package com.assessment.flitetrakr;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.assessment.data.AdjacencyMatrix;


public class SampleQuestionsTest {
	public static final String CONNECTION1="Connections: NUE-FRA-43, NUE-AMS-67, FRA-AMS-17, FRA-LHR-27, LHR-NUE-23";
	
	AdjacencyMatrix adjacencyMatrix;
	Query query;
	PrintWriter writer;
	NLQuery nlQuery;	
	
	public SampleQuestionsTest() throws ParseException, NoSuchMethodException, SecurityException {
		adjacencyMatrix = new AdjacencyMatrix(CONNECTION1);
		query = new Query(adjacencyMatrix);
		writer = new PrintWriter(System.out);
		nlQuery = new NLQuery(query);		
	}
	
	/* *
	@Test //^(What\\s+is\\s+)?(the\\s+)cheapest\\s+connection\\s+from\\s+\\w+\\s+to\\s+\\w+\\s*\\?$
	public void testQuestion1() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String questionVariants[] = {
			"What  is  the  price  of  the  connection  NUE-FRA-LHR ?",
			"price  of  the  connection  NUE-FRA-LHR?",
			"price  of  connection  NUE-FRA-LHR?",
			"price  connection  NUE-FRA-LHR?",
			"price  connection  NUE-FRA-LHR",
		};
		
		for(String question: questionVariants) {
			System.out.println(String.format("SampleQuestionsTest.testQuestion1: %s = %s", question,  nlQuery.evaluate(question)));
		}
	}
	
	@Test
	public void testQuestion2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String questionVariants[] = {
			"What  is  the  cheapest  connection  from  NUE to  AMS?",
			"the  cheapest  connection  from  NUE to  AMS?",
			"cheapest  connection  from  NUE to  AMS?",
		};
		
		
		for(String question: questionVariants) {			
			System.out.println(String.format("SampleQuestionsTest.testQuestion2: %s = %s", question,  nlQuery.evaluate(question)));
		}
	}
		
	@Test
	public void testQuestion3() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String questionVariants[] = {
			"How  many  different  connections  with  maximum  3  stops  exists  between  NUE  and  FRA ?",
			"How many different connections with maximum 3 stops exist between NUE and FRA?",
			"How many different connections maximum 3 stops exist between NUE and FRA?",
			"How many different connections maximum 3 stop exist between NUE and FRA?",
			"How many connections maximum 3 stop exist between NUE and FRA?",
			"connections maximum 3 stop exist between NUE and FRA?",
			"connections maximum 3 stop between NUE and FRA?",
		};
		
		for(String question: questionVariants) {
			System.out.println(String.format("SampleQuestionsTest.testQuestion3: %s = %s", question,  nlQuery.evaluate(question)));
		}
	}
	
	@Test
	public void testQuestion4() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String questionVariants[] = {
			"How  many  different  connections  with  minimum 1  stops  exists  between  FRA  and  NUE ?",
			"How many different connections with minimum 1 stops exist between FRA and NUE?",
			"How many different connections minimum 1 stops exist between FRA and NUE?",
			"How many different connections minimum 1 stop exist between FRA and NUE?",
			"How many connections minimum 1 stop exist between FRA and NUE?",
			"connections minimum 1 stop between FRA and FRA?"
		};
		
		for(String question: questionVariants) {			
			System.out.println(String.format("SampleQuestionsTest.testQuestion4: %s = %s", question,  nlQuery.evaluate(question)));
		}
	}
	
	@Test
	public void testQuestion5() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String questionVariants[] = {
			"How many different connections with exactly 1 stop exists between LHR and AMS?",
			"connections exactly 1 stops exist between LHR and AMS?",
		};
				
		for(String question: questionVariants) {			
			System.out.println(String.format("SampleQuestionsTest.testQuestion5: %s = %s", question,  nlQuery.evaluate(question)));
		}
	}
	
	@Test
	public void testQuestion6() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String questionVariants[] = {
			"Find all connections from NUE to LHR below 170 euros?",
			"all connections from NUE to LHR below 170 euros?",
			"Find connections from NUE to LHR below 170 euros?",
			"connections from NUE to LHR below 170 euros?"
		};
		
		for(String question: questionVariants) {
			System.out.println(String.format("SampleQuestionsTest.testQuestion6: %s = %s", question,  nlQuery.evaluate(question)));
		}
	}
	/* */
	
	@Test
	public void testSampleInputWithQuery() throws ParseException {
		AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(CONNECTION1);
		Query query = new Query(adjacencyMatrix);
		
		//#1
		Assert.assertEquals(70, query.connectionPrice(new String[] {"NUE", "FRA", "LHR"}));
		
		//#2
		Assert.assertEquals(-1, query.connectionPrice(new String[] {"NUE", "AMS", "LHR"}));	
		
		//#3
		Assert.assertEquals(93, query.connectionPrice(new String[] {"NUE", "FRA", "LHR", "NUE"}));			
		
		//#4:
		Assert.assertEquals("NUE-FRA-AMS-60", query.cheapestConnection("NUE", "AMS"));
		
		//#5
		Assert.assertEquals("No such connection found!", query.cheapestConnection("AMS", "FRA"));
		
		//#6
		Assert.assertEquals("LHR-NUE-FRA-LHR-93", query.cheapestConnection("LHR", "LHR"));	
		
		//#7:
		Assert.assertEquals(2, query.connectionsWithMaximumStops(3, "NUE", "FRA"));
		
		//#8
		Assert.assertEquals(1, query.connectionsWithExactStops(1, "LHR", "AMS"));
		
		Assert.assertEquals("NUE-FRA-LHR-70, NUE-FRA-LHR-NUE-FRA-LHR-163", query.connectionsBelowPrice(170, "NUE", "LHR"));
	}
	
	@Test
	public void testSampleInputWithNLQuery() throws ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		//
		Assert.assertEquals("70", nlQuery.evaluate("What is the price of the connection NUE-FRA-LHR?"));
		
		Assert.assertEquals(Query.CONNECTION_NOT_FOUND_ERROR, nlQuery.evaluate("What is the price of the connection NUE-AMS-LHR?"));
		
		Assert.assertEquals("93", nlQuery.evaluate("What is the price of the connection NUE-FRA-LHR-NUE?"));
		
		Assert.assertEquals("NUE-FRA-AMS-60", nlQuery.evaluate("What is the cheapest connection from NUE to AMS?"));
		
		Assert.assertEquals(Query.CONNECTION_NOT_FOUND_ERROR, nlQuery.evaluate("What is the cheapest connection from AMS to FRA?"));
		
		Assert.assertEquals("LHR-NUE-FRA-LHR-93", nlQuery.evaluate("What is the cheapest connection from LHR to LHR?"));
	
		Assert.assertEquals("2", nlQuery.evaluate("How many different connections with maximum 3 stops exists between NUE and FRA?"));
		
		Assert.assertEquals("1", nlQuery.evaluate("How many different connections with exactly 1 stop exists between LHR and AMS?"));
		
		Assert.assertEquals("NUE-FRA-LHR-70, NUE-FRA-LHR-NUE-FRA-LHR-163", nlQuery.evaluate("Find all connections from NUE to LHR below 170 Euros!"));
		
	}
	
}
