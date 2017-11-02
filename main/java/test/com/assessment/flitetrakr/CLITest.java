package com.assessment.flitetrakr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>Ensures quality of <b><code>com.assessment.flitetrakr.CLI</code></b>'s methods.</p>
 * @author rsolano
 *
 */
public class CLITest {
	String dataDir = System.getProperty("CONNECTIONS_DATA") + File.separatorChar + "data";
	
	
	/**
	 * <p>Reads and processes the a physical file specified via the system property named as <code>CONNECTIONS_DATA</code>.</p>
	 * @throws IOException
	 * @throws ParseException 
	 */
	@Test
	public void testCLIWithFileStream() throws IOException, ParseException {		
		String dataFilePath = dataDir + File.separatorChar + "connections-2.txt";		
		CLI cli = new CLI(CLIInputType.FILE, new InputStreamReader(new FileInputStream(dataFilePath)), new PrintWriter(System.out));

		System.out.println(
				String.format(
					"testCLIWithFileStream('%s')",
					dataFilePath
				)
			);		
		
		Assert.assertEquals(10,cli.process());

		
	}
}
