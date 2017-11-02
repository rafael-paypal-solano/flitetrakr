package com.assessment.data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * <p>This class implements a simple directed graph which is used to answer questions about connections.</p>
 * @author rsolano
 *
 */
public class DirectedGraph {
	
	/**
	 * <p>This map keeps track of adjacent nodes.</p>
	 */
    private Map<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();

    /**
     * <p>Adds an unidirectional link between two airports.</p>
     * 
     * @param sourceCode Starting airport code.
     * @param destinationCode Ending airport code.
     */
    public void addUnidirectionalLink(String sourceCode, String destinationCode) {
        LinkedHashSet<String> adjacent = map.get(sourceCode);
        if(adjacent==null) {
            adjacent = new LinkedHashSet<String>();
            map.put(sourceCode, adjacent);
        }
        adjacent.add(destinationCode);
    }

    /**
     * <p>Adds an bidirectional link between two airports.</p>
     * 
     * @param sourceCode Starting airport code.
     * @param destinationCode Ending airport code.
     */
    public void addBidirectionalLink(String sourceCode, String destinationCode) {
    	addUnidirectionalLink(sourceCode, destinationCode);
    	addUnidirectionalLink(destinationCode, sourceCode);
    }

    /**
     * <p>This function verifies that there is an unidirectional link between two airports.</p>
     * 
     * @param sourceCode Starting airport code.
     * @param destinationCode Ending airport code.
     * @return <code>true</code> if and only if there exist an unidirectional linke between <code><b>sourceCode</b></code> and <code><b>endCode</b></code>.
     */
    public boolean isConnected(String sourceCode, String destinationCode) {
        Set<String> adjacent = map.get(sourceCode);
        if(adjacent==null) {
            return false;
        }
        return adjacent.contains(destinationCode);
    }

    /**
     * <p>This function retrieves a list of all airports adjacent to <b><code>airportCode</code></b></p>,
     * 
     * @param airportCode Airport code whose adjacent airports we need to know.
     * @return a linked list of strings containing adjacent airport codes.
     */
    public LinkedList<String> adjacentAirportCodes(String airportCode) {
        LinkedHashSet<String> adjacent = map.get(airportCode);
        if(adjacent==null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(adjacent);
    }
    
    /**
     * <p>Retrieves all airport codes.</p>
     * 
     * @return Set containing all airport codes. This set is a copy those maintaned internally.
     */
    public Set<String> getAirportCodes() {
    	SortedSet<String> copy = new TreeSet<String>();
    	
    	copy.addAll(map.keySet());
    	return copy;
    }
    
    /**
     * <p>Detects if there is a connection from <code><b>airportCodes[0]</b></code> to <code><b>airportCodes[airportCodes.length-1]</b></code> that goes through the intermediate points</p>
     *  
     * @param airportCodes A non null array of strings.
     * @return Equivalent to <code><b>connectionExists(this, airportCodes)</b></code>.
     */
    public boolean connectionExists(String[] airportCodes) {
    	return connectionExists(this, airportCodes);    	
    }
    
    /**
     * <p>Detects if there is a connection from <code><b>airportCodes[0]</b></code> to <code><b>airportCodes[airportCodes.length-1]</b></code> that goes through the intermediate points</p>
     * @param graph The directed graph wherein paths are sought
     * @param airportCodes A non null array of strings.
     * @return <code><b>true</b></code> if and only if there exist a connection containing all the codes in <code><b>airportCodes</b></code> in the same order they are therein.
     */    
   static public boolean connectionExists(DirectedGraph graph, String... airportCodes) {
    	
    	String start = airportCodes[0];
    	String end = airportCodes[airportCodes.length-1];
    	List<LinkedList<String>> connections = depthFirst(graph, start, end);
    	
    	next: for(LinkedList<String> connection: connections) {
    		int i = 0;
    		
    		for(String node: connection) {
    			if(airportCodes[i].compareTo(node) != 0) {
    				continue next;
    			}
    			i++;
    		}
    		
    		if( i == connection.size())
    			return true;
    	}
    	
    	return false;
    }
    
    /**
     * <p>Finds all paths between two airports.</p>
     * 
     * @param sourceCode Starting airport code.
     * @param destinationCode Ending airport code.
     * @return A list whose elements are sublists. Each sublist represent a path from <code><b>start</b></code> to <code><b>end</b></code>.
     */
   	public List<LinkedList<String>> depthFirst(String sourceCode, String destinationCode){
   		return depthFirst(this, sourceCode, destinationCode);
   	}
   	
   	/**
   	 * <p>Finds all paths between two airports.</p>
   	 * 
   	 * @param graph The directed graph wherein paths are sought.
   	 * @param start Source node.
   	 * @param end Destination node.
   	 * @return A list whose elements are sublists. Each sublist represent a path from <code><b>start</b></code> to <code><b>end</b></code>.
   	 */
    static private List<LinkedList<String>> depthFirst(DirectedGraph graph, String start, String end) {
    	List<LinkedList<String>> result;
    	
		if (start.compareTo(end) == 0) {
    		result = depthFirstRoundTrip(graph, start);
    	} else {    	
    		result = depthFirstNoRoundTrip(graph, start, end);
    	}
    	
		return result;
    }
    

   	/**
   	 * <p>Finds all paths including those containing repeated stops.</p>
   	 * 
   	 * @param sourceCode Starting airport code.
   	 * @param destinationCode Ending airport code.
   	 * @return A list whose elements are sublists. Each sublist represent a path from <code><b>start</b></code> to <code><b>end</b></code>.
   	 */
    public List<LinkedList<String>> depthFirstAll( String sourceCode, String destinationCode) {
    	return depthFirstAll(this, sourceCode, destinationCode); 
    	
    }
		
   	/**
   	 * <p>Finds all paths including those containing repeated stops.</p>
   	 * 
   	 * @param graph The directed graph wherein paths are sought
   	 * @param sourceCode Starting airport code
   	 * @param destinationCode Ending airport code
   	 * @return A list whose elements are sublists. Each sublist represent a path from <code><b>start</b></code> to <code><b>end</b></code>.
   	 */
    static private List<LinkedList<String>> depthFirstAll(DirectedGraph graph, String sourceCode, String destinationCode) {
    	List<LinkedList<String>> connections;
    	
		if (sourceCode.compareTo(destinationCode) == 0) {
			connections = depthFirstRoundTrip(graph, sourceCode);
    		
    	} else {    	    		
    		connections = depthFirst(graph, sourceCode, destinationCode);    		
    	}
		
		List<LinkedList<String>> roundTripsFromDestination = depthFirst(graph, destinationCode, destinationCode);		
		List<LinkedList<String>> result = new ArrayList<LinkedList<String>>();
		
		for(LinkedList<String> connection: connections) {						

			result.add(connection);
			
			for(LinkedList<String> roundTrip: roundTripsFromDestination) {
				LinkedList<String> copy = new LinkedList<String>();
				
				copy.addAll(connection);
				copy.removeLast();				
				copy.addAll(roundTrip);    				
				result.add(copy);
			}
			
		}    	
		return result;
    }
    
    /**
     * <p>Find all roundtrips that start from a specific airport.</p>
     * 
     * @param graph The directed graph wherein paths are sought.
     * @param sourceCode Starting airport code.
     * @return  A list whose elements are sublists. Each sublist represent a roundtrip starting at <code><b>sourceCode</b></code>. 
     */
    static private List<LinkedList<String>> depthFirstRoundTrip(DirectedGraph graph, String sourceCode) {
		Collection<String> nodes = graph.getAirportCodes();
		HashSet<ComparableStringList> rounTrips = new HashSet<ComparableStringList>();
		List<LinkedList<String>> result = new ArrayList<LinkedList<String>>();
		
		nodes.remove(sourceCode);
		
		for(String end: nodes){
			List<LinkedList<String>> departures =  depthFirstNoRoundTrip(graph, sourceCode, end);			
			List<LinkedList<String>> arrivals =  depthFirstNoRoundTrip(graph, end, sourceCode);
				
			for(LinkedList<String> departure: departures) {
				
				for(LinkedList<String> arrival: arrivals ) {		
					ComparableStringList roundTrip = new ComparableStringList();
										
					roundTrip.addAll(departure);									
					roundTrip.addAll(arrival);
					roundTrip.remove(departure.size()-1);
					rounTrips.add(roundTrip);
				}								
			}			
		}

		/**
		 * 
		 */
		result.addAll(rounTrips);
		return result;
	}
    
    /**
     * <p>Finds all non-roundtrip paths between two nodes.</p>
     * 
     * @param graph The directed graph wherein paths are sought.
     * @param sourceCode Starting airport code.
     * @param destinationCode  Ending airport code.
     * @return A list whose elements are sublists. Each sublist represent a path from <code><b>start</b></code> to <code><b>end</b></code>.
     */
    static private List<LinkedList<String>> depthFirstNoRoundTrip(DirectedGraph graph, String sourceCode, String destinationCode) {
        LinkedList<String> visited = new LinkedList<String>();
        List<LinkedList<String>> result = new LinkedList<LinkedList<String>>();
        
        visited.add(sourceCode);
        depthFirstNoRoundTrip(graph, visited, result, destinationCode);
        
        return result;
        
    }  
    
    /**
     * <p>Recursive step for <code><b>depthFirstNoRoundTrip(DirectedGraph graph, String sourceCode, String destinationCode)</b></code>.</p>
     * @param graph he directed graph wherein paths are sought.
     * @param visited List of visited nodes.
     * @param result The resulting list.
     * @param destinationCode Ending airport code.
     */
    static private void depthFirstNoRoundTrip(DirectedGraph graph, LinkedList<String> visited, List<LinkedList<String>> result, String destinationCode) {
        LinkedList<String> nodes = graph.adjacentAirportCodes(visited.getLast());
        
        // examine adjacent nodes
        for (String node : nodes) {
        	
            if (visited.contains(node)) {
                continue;
            }
            
            if (node.equals(destinationCode)) {
            	LinkedList<String> list = new LinkedList<String>();
            	
                visited.add(node);
                list.addAll(visited);
                result.add(list);                
                
                visited.removeLast();
                break;
            }
        }
        
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(destinationCode)) {
                continue;
            }
            visited.addLast(node);
            depthFirstNoRoundTrip(graph, visited, result, destinationCode);
            visited.removeLast();
        }
    }
   
}