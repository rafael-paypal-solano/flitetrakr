package com.assessment.flitetrakr;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.assessment.data.AdjacencyMatrix;

public class AdjacencyMatrixTest {
	
	//TODO: Add test cases to validate that suggestion received from Mona are correctly implemented
	/**
	 * 
	 */
	final static String[] connections = {
		"Connections: NUE-FRA-43,NUE-AMS-67,FRA-AMS-17,FRA-LHR-27,LHR-NUE-23",
		"Connections: NUE-FRA-43,NUE-AMS-67, FRA-AMS-17,FRA-LHR-27, LHR-NUE-23"
	};

	
	
	/**
	 * This method ensures that all valid strings are accepted by <b><code>com.assessment.flitetrakr.DataSet</code></b>'s constructor.
	 * @throws ParseException 
	 */
	@Test
	public void testValidConnectionStrings() throws ParseException {
		
		System.out.println(String.format("DataSetTest.testValidConnectionStrings with '%s'", AdjacencyMatrix.CONNECTIONS_TABLE_PATTERN));
		
		for(String connection: connections) {
			System.out.println(String.format("DataSetTest.testValidConnectionStrings(%s)", connection));
			new AdjacencyMatrix(connection);
		}
	}
	
	/**
	 * <p>This method verifies that datasets are symmetric.</p>
	 * <p>The symmetry feature speeds up search algorithms.</p>
	 * @throws ParseException
	 */
	@Test
	public void testSymmetricDataset() throws ParseException {
		
		System.out.println(String.format("DataSetTest.testSymmetricDataset with '%s'", AdjacencyMatrix.CONNECTIONS_TABLE_PATTERN));
		
		for(String connection: connections) {			
			AdjacencyMatrix dataSet = new AdjacencyMatrix(connection);
			int length = dataSet.length();
			
			System.out.println(String.format("DataSetTest.testSymmetricDataset(%s)", connection));
			
			for(int x = 0; x < length; x++) {
				for(int y = 0; y < length; y++) {
					Assert.assertEquals(dataSet.get(x, y) , dataSet.get(y, x));
				}
			}
		}
	}	
	
}
