package com.assessment.flitetrakr;


import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import com.assessment.data.AdjacencyMatrix;
import com.assessment.util.StringIO;

/**
 * <p>This class contains the functions that perform queries against connections adjacency matrix.</p>
 * @author rsolano
 *
 */
public class Query {
	
	/**
	 * <p>Convenience constant to avoid literal repetitions.</p>
	 */
	public static final String CONNECTION_NOT_FOUND_ERROR= "No such connection found!";
	
	/**
	 * Adjacency matrix whose weights are the flight fares and its row/columns coordinates mapped to airport codes.
	 */
	AdjacencyMatrix adjacencyMatrix;
	
		
	/**
	 * @param adjacencyMatrix Adjacency matrix whose weights are the flight fares and its row/columns coordinates are mapped to airport codes.
	 */
	public Query(AdjacencyMatrix adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	/**
	 * <p>Detects whether the user wants the following functions to process multiple stops @ one place:</p>
	 * <ul>
	 * <li><a>connectionsBelowPrice</a></li>
	 * <li><a>connectionsWithMinimumStops</a></li>
	 * <li><a>connectionsWithMaximumStops</a></li>
	 * <li><a>connectionsWithExactStops</a></li>
	 * </ul>
	 * 
	 * @return Returns <code><b>true</b></code> if <code><b>System.getProperty("com.assessment.flitetrakr.multiple")</b></code> is not null and equals to <code><b>&quot;true&quot;</b></code>.
	 */
	private boolean processMultipleStops() {
		String multiple = System.getProperty("com.assessment.flitetrakr.multiple");
		
		if(multiple == null){
			return false;
		}
		
		return Boolean.parseBoolean(multiple.trim());
	}
	
	/**
	 * <p>Retrieves all connections between two airports.</p>
	 * 
	 * @param sourceCode Departure airport's code.
	 * @param destinationCode Destination airport's code.
	 * @return A linked list containing all connections between the two airports..
	 */
	private List<LinkedList<String>> connections(String sourceCode, String destinationCode) {
		if(processMultipleStops()) {
			return  this.adjacencyMatrix.
					getDirectedGraph().
					depthFirstAll(sourceCode, destinationCode);
		}
		
		
		return  this.adjacencyMatrix.
				getDirectedGraph().
				depthFirst(sourceCode, destinationCode);
	}
	/**
	 * <p>This function finds all connections from <b><code>sourceCode</code></b> to <b><code>destinationCode</code></b> below a specified price.</p>
	 * 
	 * @param upperPrice Upper limit of the price range.
	 * @param sourceCode Departure airport's code.
	 * @param destinationCode Destination airport's code.
	 * @return How many connections exist below the specified price ( connection price &lt; <b><code>upperPrice</code></b>).
	 */
	public String connectionsBelowPrice(int upperPrice, String sourceCode, String destinationCode) {		
		List<LinkedList<String>> connections = this.adjacencyMatrix.
				getDirectedGraph().
				depthFirstAll(sourceCode, destinationCode);
		
		String[] sorted = formatConnections(
			connections.stream().filter(
				c -> calculateTotalDistance(c) < upperPrice).collect(Collectors.toList()
			)
		);
		
		Arrays.sort(
			sorted,
			new Comparator<String>() {				
				public int compare(String o1, String o2) {
					int pos1 = o1.lastIndexOf('-');
					int pos2 = o2.lastIndexOf('-');
					int price1 = Integer.parseInt(o1.substring(pos1 + 1));					
					int price2 = Integer.parseInt(o2.substring(pos2 + 1));		
					
					return price1 - price2;
				}
			}
		);
		
		return StringIO.join(sorted, StringIO.DEFAULT_LIST_SEPARATOR);
	}
	
	
	
	/**
	 * <p>This function addresses the question of how many different connections with minimum <b><code>???</code></b> stops exist between <b><code>???</code></b> and <b><code>???</code></b>.</p>
	 * 
	 * @param stops Maximum number of stops. A stop is a landing in an intermediary city.
	 * @param sourceCode Departure airport's code.
	 * @param destinationCode Destination airport's code.
	 * @return How many connections comply with the aforementioned criteria.
	 */
	public int connectionsWithMinimumStops(int stops, String sourceCode, String destinationCode) {
		List<LinkedList<String>> connections = connections(sourceCode, destinationCode);			
		List<LinkedList<String>> selected = connections.stream().filter(c -> c.size()-2 >= stops).collect(Collectors.toList());
		
		/* *
		for(String c: this.formatConnections(selected)) {
			System.out.println(c);
		}
		/* */		
		return selected.size();
	}
	
	
	/**
	 * <p>This function addresses the question of how many different connections with maximum <b><code>???</code></b> stops exist between <b><code>???</code></b> and <b><code>???</code></b>.</p>
	 * 
	 * @param stops Maximum number of stops. A stop is a landing in an intermediary city.
	 * @param sourceCode Departure airport's code.
	 * @param destinationCode Destination airport's code.
	 * @return How many connections comply with the aforementioned criteria.
	 */
	public int connectionsWithMaximumStops(int stops, String sourceCode, String destinationCode) {
		List<LinkedList<String>> connections = connections(sourceCode, destinationCode);			
		List<LinkedList<String>> selected = connections.stream().filter(c -> c.size()-2 <= stops).collect(Collectors.toList());
		
		return selected.size();
	}
	
	/**
	 * <p>This function addresses the question of how many different connections with exactly <b><code>???</code></b> stops exist between <b><code>???</code></b> and <b><code>???</code></b>.</p>
	 * 
	 * @param stops Number of expected stops. A stop is a landing in an intermediary city.
	 * @param sourceCode Departure airport's code.
	 * @param destinationCode Destination airport's code.
	 * @return How many connections comply with the aforementioned criteria.
	 */
	public int connectionsWithExactStops(int stops, String sourceCode, String destinationCode) {
		List<LinkedList<String>> connections = connections(sourceCode, destinationCode);		
		List<LinkedList<String>> selected = connections.stream().filter(c -> c.size()-2 == stops).collect(Collectors.toList());	
		
		/* *
		for(String c: this.formatConnections(selected)) {
			System.out.println(c);
		}
		/* */
		return selected.size();
	}	
	
	/**
	 * <p>This function addresses the question of what is the price of the connection <b><code>???-???-???</code></b>... </p>
	 * 
	 * @param codes An array of airport codes representing a connection; 1st element is the connection's source and last one is the connection's destionation.
	 * @return -1 if no connection having those codes exist, or a positive integer indicating the connection cost. 
	 */
	public int connectionPrice(String[] codes) {
		if(!this.adjacencyMatrix.getDirectedGraph().connectionExists(codes)) {
			return -1;
		}
				
		String sourceCode = codes[0];
		String destinationCode = codes[1];
		int price = this.adjacencyMatrix.get(sourceCode, destinationCode);
		
		for(int i = 2; i < codes.length ; i++) {
			sourceCode = destinationCode;
			destinationCode = codes[i];
			price += this.adjacencyMatrix.get(sourceCode, destinationCode);
		}
		
		return price;
	}
	
	/**
	 * <p>This function addresses the question of what is the price of the connection <b><code>???-???-???</code></b>... ? </p>
	 * 
	 * @param connection A linked list of strings where each element is an airport code.
	 * @return -1 if no connection having those codes exist, or a positive integer indicating the connection PRICE. 
	 */
	public int connectionPrice(LinkedList<String> connection) {
		String[] codes = new String[connection.size()];
		
		connection.toArray(codes);
		
		return connectionPrice(codes);
	}	
	
	/**
	 * <p>Calculates the shortest path between two airports.</p>
	 * <p>This function addresses the question of what is the cheapest connection from <b><code>???</code></b> to <b><code>???</code></b> </p>
	 * 
	 * @param sourceCode Departure airport's code.
	 * @param destinationCode Destination airport's code.
	 * 
	 * @return The shortest path between <code><b>sourceCode</b></code> and <code><b>destinationCode</b></code>.
	 * The returned string contains a sequence of airport codes followed by the total distance. Example <code><b>NUE-FRA-AMS-60</b></code>.
	 */
	public String cheapestConnection(String sourceCode, String destinationCode) {
		
		String result;
		
		try {
			adjacencyMatrix.getIndex(sourceCode);
			adjacencyMatrix.getIndex(destinationCode);
			
			List<LinkedList<String>> connections = this.adjacencyMatrix.
					getDirectedGraph().
					depthFirstAll(sourceCode, destinationCode);
			
			if(connections.size() > 0) {
				LinkedList<String> connection = this.cheapestConnection(connections);
				
				result = this.formatConnection(connection);
			}else {
				result = CONNECTION_NOT_FOUND_ERROR;
			}
			
		}catch(ArrayIndexOutOfBoundsException e) {
			result = CONNECTION_NOT_FOUND_ERROR;
		}
		
		return result;
		
	}
	
	/**
	 * <p>Finds the cheapest connection the given connection list.</p>
	 * 
	 * @param connections A list whose elements are sublists. Each sublist represent a connection between two airports.
	 * @return The shortest path in the given path list or null if the list is empty.
	 */
	private LinkedList<String> cheapestConnection(List<LinkedList<String>> connections) {
		ListIterator<LinkedList<String>> iterator = connections.listIterator();
		LinkedList<String> shortestConnection = iterator.next();
		int currentTotalDistance = calculateTotalDistance(shortestConnection);
		
		
		while(iterator.hasNext()) {		
			LinkedList<String> path = iterator.next();
			int totalDistance = this.calculateTotalDistance(path);
			
			if(totalDistance < currentTotalDistance) {
				shortestConnection = path;
				currentTotalDistance = totalDistance;
			}
			
		}
		return shortestConnection; 
	}	
	
	/**
	 * <p>Formats linked list of string representing a connection.</p>
	 * <p>The resulting string will match the pattern <code><b>"&lt;code-of-departure-airport&gt;-&lt;code-of-arrival-airport&gt;-&lt;price-in-euro&gt;,.."</b></code></p>
	 * 
	 * @param connection A non null linked list of strings
	 * @return A string compliant to the aforementioned requirement.
	 */
	private String formatConnection(LinkedList<String> connection) {
		int totalDistance = calculateTotalDistance(connection); //TODO: This step is suboptimal.
		StringBuilder buffer = new StringBuilder();
		
		for(String code: connection) {
			buffer.append(code);
			buffer.append('-');
		}
		buffer.append(totalDistance);
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param connections Creates a array of strings where each element is list of airport codes separated by '-'.
	 * @return An array of strings where each element is a formatted connection.
	 */
	private String[] formatConnections(List<LinkedList<String>> connections) {
		String[] result = new String[connections.size()];
		int index = 0;
		
		for(LinkedList<String> connection: connections) {
			result[index++] = formatConnection(connection);
		}
		
		return result;
	}
	
	/**
	 * <p>Calculates the total distance between path's starting and ending node.</p>
	 * 
	 * @param connection A list of string representing airport codes.
	 * @return An non-negative integer.
	 */
	private int calculateTotalDistance(LinkedList<String> connection) {
		ListIterator<String> iterator = connection.listIterator();
		String sourceCode = iterator.next();
		String destinationCode = iterator.next();
		
		int totalDistance = adjacencyMatrix.get(sourceCode, destinationCode);
		
		while(iterator.hasNext()) {
			sourceCode = destinationCode;
			destinationCode = iterator.next();
			totalDistance += adjacencyMatrix.get(sourceCode, destinationCode);
		}
		return totalDistance;
		
	}
}
