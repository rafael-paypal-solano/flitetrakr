package com.assessment.data;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.*;

import com.assessment.util.StringIO;


/**
 * <p>Adjacency matrix whose weights are the flight fares and its row/columns coordinates are mapped to airport codes.</p>
 * <p>The format of each connection will be defined by <b><code>&lt;code-of-departure-airport&gt;-&lt;code-of-arrival-airport&gt;-&lt;price-in-euro&gt;</code></b>; so e.g. AMS-PDX-617. Multiple values will be separated by a comma and an optional whitespace. The line containing the price list will have the prefix <b><code>Connections:</code></b></p>
 * @author rsolano
 *
 */
public class AdjacencyMatrix {

	/**
	 * Regular expression that describes single connection in the connections string.
	 */
	public static String CONNECTION_PATTERN="\\w+\\-\\w+\\-\\d+";
	
	/**
	 * <p>Regular expression that describes separators</p> 
	 * <p>Multiple values will be separated by a comma (',') and an optional whitespace (\s).</p>
	 */
	public static String CONNECTION_SEPARATOR_PATTERN=",";
	
	/**
	 * Regular expression that describes the whole connections string.
	 */
	public static Pattern CONNECTIONS_TABLE_PATTERN = Pattern.compile(
		String.format(
			"^Connections:%s(%s%s)*$",
			CONNECTION_PATTERN,
			CONNECTION_SEPARATOR_PATTERN,
			CONNECTION_PATTERN
		)
	);

	
		
	/**
	 * <p>Connections table.</p>
	 * <p>This two dimensional array contains the fare prices (in euros) between airports. 
	 */
	int[][] connectionsTable;
	
	/**
	 *  This map indexes <b><code>this.connectionsTable</code></b>'s row/columns by airport codes.
	 */
	Map<String, Integer> connectionsIndex;
	
	/**
	 *  This map indexes airport codes to <b><code>this.connectionsTable</code></b>'s row/columns.
	 */
	Map<Integer, String> connectionsReverseIndex;
	
	/**
	 * Directed graph used to keep track of source and destination ends. 
	 */
	DirectedGraph directedGraph;
	
	/**
	 * <p>Parses the string representing the connections table. Airport codes are regarded as case insensitive.</p>
	 * 
	 * @param connections A string representing a price list; this string is the first line in the input stream.
	 * @throws java.text.ParseException If <b><code>connections</code></b> is not a valid connections table.
	 */
	public AdjacencyMatrix(String connections) throws ParseException {
		String trimmed = StringIO.removeSpaces(connections);
		Matcher matcher = CONNECTIONS_TABLE_PATTERN.matcher(trimmed);
		
		if(!matcher.matches()){
			throw new ParseException("Invalid connections table '"+connections+"\'", 0);
		}
		
		String[][] connectionRecords = extractConnectionRecords(trimmed);
		
		this.connectionsIndex = createConnectionsIndex(connectionRecords);
		this.connectionsReverseIndex = createConnectionsReverseIndex(this.connectionsIndex);
		this.connectionsTable = createConnectionsTable(connectionRecords, this.connectionsIndex);
		this.directedGraph = createDirectedGraph(connectionRecords);
	}
	
	/**
	 * <p>Creates the directed graph used to keep track of source and destination ends. 
	 * 
	 * @param connectionRecords Adjacency matrix whose weights are the flight fares and its row/columns coordinates are mapped to airport codes.
	 * @return A new instance of <code><b>com.assessment.data.DirectedGraph</b></code>.
	 */
	private DirectedGraph createDirectedGraph(String[][] connectionRecords) {
		DirectedGraph graph = new DirectedGraph();
		
		for(String[] record: connectionRecords) {
			String sourceCode = record[0];
			String destinationCode = record[1];
			
			graph.addUnidirectionalLink(sourceCode, destinationCode);
		}
		
		return graph;
	}
	
	/**
	 * <p>Creates a map that helps to find row/col offsets by its corresponding airport code.</p>
	 * 
	 * @param connectionsIndex A map that indexes airport codes to <b><code>this.connectionsTable</code></b>'s row/columns.
	 * @return This map indexes <b><code>this.connectionsTable</code></b>'s row/columns by airport codes.
	 */
	private Map<Integer, String> createConnectionsReverseIndex(Map<String, Integer> connectionsIndex) {
		Map<Integer, String> connectionsReverseIndex = new HashMap<Integer, String>();
		
		for(Entry<String, Integer> entry: this.connectionsIndex.entrySet()) {
			connectionsReverseIndex.put(entry.getValue(), entry.getKey());
		}
		
		return connectionsReverseIndex;
	}

	/**
	 * <p>Creates a two dimensional array whose elements are sub arrays that represent connection records</p>
	 * <p>For example: if <b><code>connections == &quot;Connections: AMS-PDX-617,NUE-AMS-123, AMS-LHR-43&quot;</code></b>, then this function returns
	 * <b><code>{{"AMS",PDX",617}, {"NUE","AMS",123}, {"AMS","LHR",43}}</code></b></p>
	 * 
	 * @param connections A string representing price list.
	 * @return A two dimensional array complying the aforementioned requirements.
	 */
	private String[][] extractConnectionRecords(String connections) {
		int colon = connections.indexOf(':');
		int index = 0;
		String[] lines = connections.substring(colon+1).trim().split(CONNECTION_SEPARATOR_PATTERN);
		String[][] connectionRecords = new String[lines.length][];
		
		for(String line: lines) {
			connectionRecords[index] = line.split("\\-");
			index++;
		}
		
		return connectionRecords;
	}
	
	/**
	 * <p>Creates a map that helps to find airport code offsets by its corresponding row/col offset.</p>
	 * 
	 * @param connectionRecords A two dimensional array whose elements are sub arrays that represent connection records. This parameter is value returned by <b><code>createConnectionsIndex</code></b>.
	 * @return A map that indexes <b><code>this.connectionsTable</code></b>'s row/columns by airport codes.
	 */
	private Map<String, Integer> createConnectionsIndex(String[][] connectionRecords) {
		Map<String, Integer> connectionsIndex = new HashMap<String, Integer>();
		int offset = 0;
		
		for(String[] record: connectionRecords) {
			String firstCode = record[0];
			String secondCode = record[1];
			
			if(!connectionsIndex.containsKey(firstCode)) {
				connectionsIndex.put(firstCode, offset);
				offset++;
			}
			
			if(!connectionsIndex.containsKey(secondCode)) {
				connectionsIndex.put(secondCode, offset);
				offset++;
			}			
		}
		
		return connectionsIndex;
	}
	
	/**
	 * <p>Creates the internal two dimensional array that stores fare prices.</p>
	 * 
	 * @param connectionRecords A two dimensional array whose elements are sub arrays that represent connection records. This parameter is value returned by <b><code>createConnectionsIndex</code></b>.
	 * @param connectionsIndex  A map that indexes <b><code>this.connectionsTable</code></b>'s row/columns by airport codes.
	 * @return A two dimensional array contains the fare prices (in euros) between airports. 
	 * @throws ParseException Distance between airports is not greater than zero.
	 */
	private int[][] createConnectionsTable(String[][] connectionRecords, Map<String, Integer> connectionsIndex) throws ParseException {
		int size = connectionsIndex.size();
		int [][] connectionsTable = new int[size][size];

		
		for(String[] record: connectionRecords) {
			String firstCode = record[0];
			String secondCode = record[1];
			int price = Integer.parseInt(record[2]);			
			int x = connectionsIndex.get(firstCode);
			int y = connectionsIndex.get(secondCode);
			
			if(price == 0)
				throw new ParseException("Distance between airports must be greather than zero.", 0);
			
			connectionsTable[x][y] = price;
			connectionsTable[y][x] = price;
		}
		
		return connectionsTable;
	}
	
	/**
	 * <p>Looks for the price between airports <code><b>x</b></code> and <code><b>y</b></code>.</p>
	 * 
	 * @param x Row number.
	 * @param y Column number. 
	 * @return <b><code>this.connectionsTable[x][y]</code></b>
	 */
	public int get(int x, int y) {
		return connectionsTable[x][y];
	}
	
	/**
	 * <p>Looks for the price between airports <code><b>a</b></code> and <code><b>a</b></code>.</p>
	 * 
	 * @param a Airport code
	 * @param b Airport code 
	 * @return The price corresponding to direct connection between airports a and b.
	 */
	public int get(String a, String b) {
		Integer x = connectionsIndex.get(a);
		Integer y = connectionsIndex.get(b);
		
		if(x == null || y == null){
			throw new ArrayIndexOutOfBoundsException(
				String.format(
					"No entry exists for a='%s', b='%s'", 
					a,
					b
				)		
			);
		}
		
		return get(x, y);
	}	
	
	/**
	 * <p>Returns the connections table size.</p>
	 * 
	 * @return connectionsTable.length.
	 */
	public int length() {
		return connectionsTable.length;
	}
	
	/**
	 * 
	 * @param code airport code.
	 * @return Returns the row/column offset for the given airport code.
	 */
	public int getIndex(String code) {
		Integer index = connectionsIndex.get(code);
		if(index == null) {
			throw new ArrayIndexOutOfBoundsException(
				String.format(
					"No entry exists for code='%s", 
					code
				)	
			);
		}
			
		return index.intValue();
	}

	/**
	 * 
	 * @param index row/col offset
	 * @return Returns the row/column offset for the given airport code.
	 */
	public String getCode(int index) {
		String code = connectionsReverseIndex.get(index);
		if(code == null) {
			throw new ArrayIndexOutOfBoundsException(
				String.format(
					"No entry exists for index='%d", 
					index
				)	
			);
		}
			
		return code;
	}
	
	/**
	 * 
	 * @return The two dimensional array containing the fare prices (in euros) between airports. 
	 */
	public int[][] getConnectionsTable() {
		return connectionsTable;
	}

	/**
	 * 
	 * @return com.assessment.data
	 */
	public DirectedGraph getDirectedGraph() {
		return directedGraph;
	}
	
	
}
