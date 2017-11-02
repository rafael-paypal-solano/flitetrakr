package com.assessment.util;

import java.io.*;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <P>Helper class containing convenience methods for reading, writing and processing character strings.</P>
 * @author rsolano
 *
 */
public class StringIO {

	/**
	 * Convenience constant equals to &quot;&quot; value. 
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * Convenience constant equals to &quot;\\s+&quot; value. 
	 */
	public static final String CONTINUOUS_WHITESPACE_EXPR = "\\s+";
	
	/**
	 * Convenience constant equals to &quot; &quot; value. 
	 */
	public static final String SINGLE_WHITESPACE_STRING = " ";
	
	/**
	 * Default list separator.
	 */
	public static final String DEFAULT_LIST_SEPARATOR = ", ";
	
	/**
	 * <p>Reads all lines in a text file and stores them into a list of strings</p> 
	 * 
	 * @param input Valid input Stream
	 * @return A new instance of <code>List&lt;String&gt;</code> containing all lines loaded <code>input</code>.
	 * @throws IOException If Input stream can't be read
	 */
	public static List<String> readLines(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));		
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(line.length() > 0)
				lines.add(line);
		}
		
		return lines;
		
	}
	
	/**
	 * Converts an array of strings into a linked list of strings. Null strings are ignored.
	 * 
	 * @param strings A non-null array of strings.
	 * @return A new linked list containing all strings contained in <code><b>strings</b></code> array.
	 */
	public static LinkedList<String> toLinkedList(String[] strings) {
		LinkedList<String> list = new LinkedList<String>();
		
		for(String string: strings) {
			if(string != null) {
				list.add(string);
			}
		}
		
		return list;
	}	
	
	/**
	 * Joins all strings contained in an array into a single string.
	 * 
	 * @param strings The array containing all strings being merged. 
	 * @param separator Optional (null == means not provided) string used as value separator in the new string.
	 * @return A new string containing all strings received via <code><b>strings</b></code> array.
	 */
	public static String join(String[] strings, String separator) {
		StringBuilder buffer = new StringBuilder();
		
		
		for(String string: strings) {
			if(string != null) {
				
				buffer.append(string);
				
				if(separator != null) {
					buffer.append(separator);
				}
					
			}
		}
		
		if(separator != null && buffer.length() > 0) {
			buffer.delete(buffer.length() - separator.length(), buffer.length()-1);
		}
		return buffer.toString().trim();
	}	

	/**
	 * <p>Remove all whitespace characters (\\s+)</p>
	 * 
	 * @param string Source string.
	 * @return <code><b>null</b></code> if string is null or a new string without any whitespace.
	 */
	public static String removeSpaces(String string) {
		if(string == null) {
			return string;
		}
		
		return string.trim().replaceAll(CONTINUOUS_WHITESPACE_EXPR, EMPTY_STRING);
	}

	/**
	 * This function is used in <code><b>NLQuery</b> </code>to process the ending question mark (?).
	 * 
	 * @param source A parsed question.
	 * @return  <code><b>questionText.lastIndexOf('?')</b></code> if questionText has a question mark ('?'), otherwise it returns <code><b>questionText.length()</b></code>
	 */
	public static int questionMarkPos(String source) {
		int end = source.lastIndexOf('?');
		
		if(end == -1) {
			end = source.length();
		} 		
		
		return end;
	}
	
	/**
	 * <p>This function is used in <code><b>NLQuery</b> </code>to extract connection's starting and ending airports.</p>
	 * <p>Examples</p>
	 * <ul>
	 * <li><code><b>getTerminals("FROM sdq TO nyc", "FROM SDQ TO NYC", "FROM", "TO")</b></code> --&gt; {&quot;sdq&quot;,&quot;nyc&quot;}</li>
	 * <li><code><b>getTerminals("BETWEEN sdq AND nyc", "BETWEEN SDQ AND NYC", "BETWEEN", "AND)</b></code> --&gt; {&quot;sdq&quot;,&quot;nyc&quot;}</li>
	 * </ul>
	 * 
	 * @param mirror
	 * @param source A text containing a substring matching the regular expression resulting from the next string concatenation: <code><b>startKeyWord + &quot;\\w+&quot; + delimiterKeyWord + &quot;\\w+&quot;</b></code> 
	 * @param startKeyWord Substring behind the 1st airport code
	 * @param delimiterKeyWord Substring between 1st and 2nd airport code.
	 * @return A array of strings containing the starting and ending point
	 */
	public static String[] mirrorSubString(String mirror, String source, String startKeyWord, String delimiterKeyWord) {
		Pattern delimiterKeyWordExp = Pattern.compile(delimiterKeyWord, Pattern.CASE_INSENSITIVE);
		int start = source.lastIndexOf(startKeyWord) + startKeyWord.length();
		int end = questionMarkPos(source);
		int p;
		if(end == -1) {
			end = source.length();
		} 
		
		
		
		String [] terminals = delimiterKeyWordExp.split(mirror.substring(start, end).trim());
		String second = terminals[1].trim();
		
		if(second.length() > 0 && (p = second.indexOf(' ')) > -1) {
			terminals[1] = second.substring(0,p);
		}
		return terminals;
	}
	
	/**
	 * <p>Return the content containing between two delimiter substrings.</p>
	 * 
	 * @param source Non null/empty string containing the text we want to extract.
	 * @param fromIndex the index from which to start the search.
	 * @param start Starting delimiter.
	 * @param end Ending delimiter.
	 * @return The substring between <code><b>start</b></code> and <code><b>end</b></code> delimiers.
	 */
	public static String substring(String source, int fromIndex, String start, String end) {
		int startPos = source.indexOf(start, fromIndex);
		int endPos = source.indexOf(end, fromIndex);
		
		if(startPos == -1 || endPos == -1 || startPos > endPos) {
			return null;
		}
		
		return source.substring(startPos + start.length(), endPos).trim();
	}		
	
	/**
	 * <p>Return the content containing between two delimiter substrings.</p>
	 * 
	 * @param source source Non null/empty string containing the text we want to extract.
	 * @param start Starting delimiter.
	 * @param end Ending delimiter.
	 * @return Equivalent to <code><b>substring(source, 0, start, end);</b></code>.
	 */
	public static String substring(String source, String start, String end) {
		return substring(source, 0, start, end);
	}
	
}
