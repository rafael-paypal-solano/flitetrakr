package com.assessment.flitetrakr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import com.assessment.data.AdjacencyMatrix;

/** FliteTrakr's command line interface.
 * @author rsolano
 *
 */
public class CLI {
	
	/**
	 * Input stream through which the application receives connections data and questions. 
	 */
	Reader input;
	
	/**
	 * Output stream used to provide feedback.
	 */
	PrintWriter output;
	
	/**
	 * This value indicates what type of input is providing the data (CONSOLE, FILE, PIPE).
	 */
	CLIInputType cliType;
	
	/**
	 * <p>Initializes instance fields whose names match parameters'.</p>
	 * 
	 * @param cliType This value indicates what type of input is providing the data (CONSOLE, FILE, PIPE).
	 * @param input Input stream through which the application receives connections data and questions.
	 * @param output Output stream used to provide feedback. 
	 */
	public CLI(CLIInputType cliType, Reader input, PrintWriter output) {
		this.cliType = cliType;
		this.input = input;
		this.output = output;
	}
	
	
	/**
	 * <p>Reads connections data and questions from <code>this.input</code> and the prints expected feedback.</p>
	 * 
	 * <p>This method must be called after successful instantiation.</p>
	 * @return How many question were accepted by the evaluator.
	 * @throws IOException If <code>this.input</code> is not available anymore.
	 * @throws ParseException If the 1st line in the input stream doesn't define a connections graph.
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public int process() throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(this.input);
		String line = reader.readLine();
		AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(line);
		Query query = new Query(adjacencyMatrix);
		int count = 1;
		
		
		try {
			NLQuery nlQuery = new NLQuery(query);
			
			while((line = reader.readLine()) != null) {
				
				try {
					String result = nlQuery.evaluate(line);
					output.println(String.format("%d: %s", count, line));
					output.println(String.format("%d: %s", count, result));
					output.println();
					output.flush();
					count++;
					
				}catch(ParseException e) {
					
					System.out.println(String.format("WARN: %s Can't evaluate '%s'.", e.getMessage(), line));
				}
			}
			
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			
			throw new IOException("FATAL: The NL evaluator is unable to accept more input", e);
		}
		
		return count;
		
	}
	/**
	 * 
	 * @param args Command line arguments
	 * @throws IOException When the input stream (stdin or file) can't be read.
	 */
	public static void main(String args[])  {
		InputStream input = null;
		CLI instance;
		CLIInputType inputType = CLIInputType.CONSOLE;
		PrintWriter writer = new PrintWriter(System.out);;
		Reader reader;
		
		try{
			
			if(args.length > 0){ //The input stream is a physical file.
				input = new FileInputStream(args[0]);
				reader = new InputStreamReader(input);
				inputType = CLIInputType.FILE;
			}
			else {
				reader = new InputStreamReader(System.in);
			}
			
			instance = new CLI(inputType, reader, writer);
			instance.process();
			
		}catch(IOException | ParseException  e) {
			
			System.console().writer().println(String.format("ERROR: %s. Can't start interpreter", e.getMessage()));
		}
		finally {
			
			if(input != null){
				try{
					input.close();
				}catch(IOException e) {
					System.console().writer().println(String.format("ERROR: Can't close the input stream %s", e.getMessage()));
				}
			}
		}
		
		
	}
}
