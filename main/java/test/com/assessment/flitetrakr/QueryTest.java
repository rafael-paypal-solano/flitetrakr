package com.assessment.flitetrakr;

import java.text.ParseException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import com.assessment.data.AdjacencyMatrix;
import com.assessment.util.StringIO;

/**
 * <p>This test case validates that <b><code>com.assessment.flitetrakr.Query</code></b> is working properly.</p>
 * @author rsolano
 *
 */
public class QueryTest {
	/**
	 * Convenience constant to avoid repetition of &quot;No such connection found!&quot;
	 */
	private static final String CONNECTION_NOT_FOUND_ERR="No such connection found!";
	
	/**
	 * Data used in this test class
	 */
	final static String[] connections = {
		"Connections: NUE-FRA-43, NUE-AMS-67, FRA-AMS-17, FRA-LHR-27, LHR-NUE-23",
		"Connections: a-x-3, a-b-1, a-c-2, b-d-2, c-d-3, d-e-1, x-b-1, x-c-5, c-z-3, e-z-1, c-y-3, y-d-1, z-y-2"
	};

	
	


	
	/**
	 * This method ensures that Query class can calculate the total distance for a given path.
	 * @throws ParseException 
	 */	
	@Test
	public void testConnectionPrice() throws ParseException { //What is the price of the connection ...?
		String[] codeArray1 = {"NUE", "FRA", "LHR"};
		String[] codeArray2 = {"NUE", "AMS", "LHR"};
		String[] codeArray3 = {"NUE", "FRA", "LHR", "NUE"};
		LinkedList<String> codeList1 = StringIO.toLinkedList(codeArray1);
		LinkedList<String> codeList2 = StringIO.toLinkedList(codeArray2);
		LinkedList<String> codeList3 = StringIO.toLinkedList(codeArray3);
		
		AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(connections[0]);
		Query query = new Query(adjacencyMatrix);
		
		System.out.println(String.format("QueryTest.testConnectionPrice"));
		Assert.assertEquals(70, query.connectionPrice(codeArray1));
		Assert.assertEquals(-1, query.connectionPrice(codeArray2));
		Assert.assertEquals(93, query.connectionPrice(codeArray3));
		
		Assert.assertEquals(70, query.connectionPrice(codeList1));
		Assert.assertEquals(-1, query.connectionPrice(codeList2));
		Assert.assertEquals(93, query.connectionPrice(codeList3));		
	}
	
	
	/**
	 * This method ensures that Query class is able to find shortest distances for valid connections..
	 * @throws ParseException 
	 */
	@Test
	public void testCheapestConnection() throws ParseException { //What is the cheapest connection from ??? to ???? Q
		AdjacencyMatrix adjacencyMatrix1 = new AdjacencyMatrix(connections[0]);
		AdjacencyMatrix adjacencyMatrix2 = new AdjacencyMatrix(connections[1]);	
		Query query1 = new Query(adjacencyMatrix1);
		Query query2 = new Query(adjacencyMatrix2);
		
		System.out.println(String.format("QueryTest.testCheapestConnection"));
		
		Assert.assertEquals("NUE-FRA-AMS-60", query1.cheapestConnection("NUE", "AMS"));
		Assert.assertEquals(CONNECTION_NOT_FOUND_ERR, query1.cheapestConnection("AMS", "FRA"));		
		Assert.assertEquals("LHR-NUE-FRA-LHR-93", query1.cheapestConnection("LHR", "LHR"));
		Assert.assertEquals("a-b-d-e-4", query2.cheapestConnection("a", "e"));
		Assert.assertEquals("x-b-d-e-4", query2.cheapestConnection("x", "e"));
		
	}
		
	@Test
	public void testConnectionsWithMaximumStops() throws ParseException { //What is the price of the connection ...? 
		AdjacencyMatrix adjacencyMatrix1 = new AdjacencyMatrix(connections[0]);
		AdjacencyMatrix adjacencyMatrix2 = new AdjacencyMatrix(connections[1]);	
		Query query1 = new Query(adjacencyMatrix1);
		Query query2 = new Query(adjacencyMatrix2);
		
		System.out.println(String.format("QueryTest.testConnectionsWithMaximumStops"));
		Assert.assertEquals(2, query1.connectionsWithMaximumStops(3, "NUE", "FRA"));
		Assert.assertEquals(5, query2.connectionsWithMaximumStops(3, "a", "e"));
		Assert.assertEquals(2, query2.connectionsWithMaximumStops(2, "a", "e"));
		Assert.assertEquals(1, query2.connectionsWithMaximumStops(0, "a", "b"));
		Assert.assertEquals(1, query2.connectionsWithMaximumStops(0, "a", "c"));		
		Assert.assertEquals(2, query2.connectionsWithMaximumStops(1, "a", "d"));
		
		
		
	}	
	
	@Test
	public void testConnectionsWithExactStops() throws ParseException { //What is the price of the connection ...? 
		AdjacencyMatrix adjacencyMatrix1 = new AdjacencyMatrix(connections[0]);
		AdjacencyMatrix adjacencyMatrix2 = new AdjacencyMatrix(connections[1]);	
		Query query1 = new Query(adjacencyMatrix1);		
		Query query2 = new Query(adjacencyMatrix2);
		
		System.out.println(String.format("QueryTest.testConnectionsWithExactStops"));
		Assert.assertEquals(1, query1.connectionsWithExactStops(1, "LHR", "AMS"));
		Assert.assertEquals(1, query2.connectionsWithExactStops(0, "a", "b"));
		Assert.assertEquals(3, query2.connectionsWithExactStops(2, "a", "d"));
	}
	
	@Test
	public void testConnectionsWithMinimumStops() throws ParseException { //What is the price of the connection ...? 
		AdjacencyMatrix adjacencyMatrix1 = new AdjacencyMatrix(connections[0]);
		AdjacencyMatrix adjacencyMatrix2 = new AdjacencyMatrix(connections[1]);	
		Query query1 = new Query(adjacencyMatrix1);		
		Query query2 = new Query(adjacencyMatrix2);
		
		System.out.println(String.format("QueryTest.testConnectionsWithMinimumStops"));
		Assert.assertEquals(2, query1.connectionsWithMinimumStops(1, "NUE", "LHR"));
		Assert.assertEquals(3, query2.connectionsWithMinimumStops(8, "a", "e"));
		Assert.assertEquals(8, query2.connectionsWithMinimumStops(2, "x", "e"));
		Assert.assertEquals(1, query2.connectionsWithMinimumStops(1, "a", "b"));
	}	
	
		
	@Test
	public void testConnectionsBelowPrice() throws ParseException { //What is the cheapest connection from ??? to ???? Q
		AdjacencyMatrix adjacencyMatrix1 = new AdjacencyMatrix(connections[0]);
		AdjacencyMatrix adjacencyMatrix2 = new AdjacencyMatrix(connections[1]);	
		Query query1 = new Query(adjacencyMatrix1);
		Query query2 = new Query(adjacencyMatrix2);
		
		System.out.println(String.format("QueryTest.testConnectionsBelowPrice"));
		
		Assert.assertEquals(
			"NUE-FRA-LHR-70, NUE-FRA-LHR-NUE-FRA-LHR-163",
			query1.connectionsBelowPrice(170, "NUE", "LHR")
		);
		
		Assert.assertEquals(
			"a-b-d-3, a-c-d-5, a-x-b-d-6, a-c-y-d-6",
			query2.connectionsBelowPrice(7, "a", "d")
		);
		
		
		Assert.assertEquals(
			"a-b-d-3, a-c-d-5",
			query2.connectionsBelowPrice(6, "a", "d")
		);
		
		Assert.assertEquals(
			"c-z-3, c-d-e-z-5, c-y-d-e-z-6, c-z-y-d-e-z-8",
			query2.connectionsBelowPrice(10, "c", "z")
		);
		
		Assert.assertEquals(
			"c-z-3, c-d-e-z-5, c-y-d-e-z-6, c-z-y-d-e-z-8",
			query2.connectionsBelowPrice(10, "c", "z")
		);
		
		Assert.assertEquals(
			"c-z-3, c-d-e-z-5, c-y-d-e-z-6, c-z-y-d-e-z-8, c-d-e-z-y-d-e-z-10, c-y-d-e-z-y-d-e-z-11",
			query2.connectionsBelowPrice(12, "c", "z")
		);
		/* */
		
	}	
}
