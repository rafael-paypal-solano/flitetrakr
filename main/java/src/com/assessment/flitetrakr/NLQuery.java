package com.assessment.flitetrakr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.regex.Pattern;

import com.assessment.util.StringIO;



/**
 * <p>This class provides a natural language interface to functions defined <code><b>com.assessment.flitetrakr.Query</b></code> class.</p>
 * @author rsolano
 *
 */
public class NLQuery {
	
	
	/**
	 * <p>&quot;CONNECTION&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */
	private static final String CONNECTION_KEYWORD="CONNECTION";
	
	/**
	 * <p>&quot;FROM&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String FROM_KEYWORD="FROM";
	
	/**
	 * <p>&quot;TO&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String TO_KEYWORD="TO";
	
	/**
	 * <p>&quot;BETWEEN&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String BETWEEN_KEYWORD="BETWEEN";
	
	/**
	 * <p>&quot;EXACTLY&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */		
	private static final String EXACTLY_KEYWORD="EXACTLY";
	
	/**
	 * <p>&quot;AND&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String AND_KEYWORD="AND";
	
	/**
	 * <p>&quot;BELOW&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String BELOW_KEYWORD="BELOW";
	
	/**
	 * <p>&quot;EURO&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String EURO_KEYWORD="EURO";
	
	/**
	 * <p>&quot;STOP&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String STOP_KEYWORD="STOP";
	
	/**
	 * <p>&quot;MINIMUM&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */		
	private static final String MINIMUM_KEYWORD="MINIMUM";
	
	/**
	 * <p>&quot;MAXIMUM&quot;</p>
	 * <p>Convenience constant to avoid literal repetition everywhere.</p>
	 */	
	private static final String MAXIMUM_KEYWORD="MAXIMUM";
	
	/**
	 * <p>An array of <code><b>java.util.regex.Pattern</b></code> instances. Each pattern addresses all the variants of a single question.
	 */
	private Pattern[] questions;	
	
	/**
	 * <p>An array of <code><b>ava.lang.reflect.Method</b></code> instances. Each method addresses all the variants of a single question.
	 */
	private Method[] methods;
	
	/**
	 * The <code><b>com.assessment.flitetrakr.Query</b></code> instance whose functions are called via this NL Interface.
	 * 
	 */
	Query query;
	
	/**
	 * <p>Builds <code><b>questions</b></code> and <code><b>methods</b></code> arrays.</p>
	 * @param query A reference to a <b>com.assessment.flitetrakr.Query</b> instance that is cached internally.
	 * 
	 * @throws NoSuchMethodException When a particular method can't be found. 
	 * @throws SecurityException Indicates that we can't a handle to a method.
	 */
	public NLQuery(Query query) throws NoSuchMethodException, SecurityException {
		this.query = query;
		this.questions = new Pattern[] {
			Pattern.compile("^(WHAT\\s+IS\\s+)?(THE\\s+)?PRICE\\s+(OF\\s+(THE\\s+)?)?CONNECTION\\s+\\w+(\\-\\w+)*\\s*\\??$"),
			Pattern.compile("^(WHAT\\s+IS\\s+)?(THE\\s+)?CHEAPEST\\s+CONNECTION\\s+FROM\\s+\\w+\\s+TO\\s+\\w+\\s*\\??$"),
			Pattern.compile("^(HOW\\s+MANY\\s+)?(DIFFERENT\\s+)?CONNECTIONS\\s+(WITH\\s+)?(MAXIMUM|MINIMUM|EXACTLY)\\s+\\d+\\s+(STOP(S)?\\s+)(EXIST(S)?\\s+)?BETWEEN\\s+\\w+\\s+AND\\s+\\w+\\s*\\??$"),
			Pattern.compile("^((FIND\\s+)?(ALL\\s+)?)?CONNECTIONS\\s+FROM\\s+\\w+\\s+TO\\s+\\w+\\s+BELOW\\s+\\d+\\s*EUROS?\\S??$")		
							  //Find all connections from NUE to LHR below 170 Euros!
		};
		this.methods = new Method[] {
			NLQuery.class.getMethod("connectionPrice", String.class, String.class),
			NLQuery.class.getMethod("cheapestConnection", String.class, String.class),
			NLQuery.class.getMethod("connectionsWithStops", String.class, String.class),
			NLQuery.class.getMethod("connectionsBelowPrice", String.class, String.class)
		};
		
	}
	
	/**
	 * <p>Process question matching the following regex:</p>
	 * <p><code><b>&quot;^(WHAT\\s+IS\\s+)?(THE\\s+)?PRICE\\s+(OF\\s+(THE\\s+)?)?CONNECTION\\s+\\w+(\\-\\w+)*\\s*\\??$&quot;</b></code>.</p>
	 * 
	 * @param trimmedQuestionText The question text without multiple whitespaces between words ('    ' --&lt;' ')
	 * @param normalizedQuestionText A non null/empty string matching the specified regex.
	 * @return A string containing decimal digits.
	 */
	public String connectionPrice(String trimmedQuestionText, String normalizedQuestionText) {		
		int start = normalizedQuestionText.lastIndexOf(CONNECTION_KEYWORD) + CONNECTION_KEYWORD.length();
		int end = StringIO.questionMarkPos(normalizedQuestionText);
		
		if(end == -1) {
			end = normalizedQuestionText.length();
		} 
		
		String connection = normalizedQuestionText.substring(start, end).trim();
		int price = query.connectionPrice(connection.split("\\-"));
		
		if(price == -1) {
			return Query.CONNECTION_NOT_FOUND_ERROR;
		}
		return Integer.toString(price);
		
	}
	
	/**
	 * <p>Process question matching the following regex:</p>
	 * <p><code><b>&quot;^(WHAT\\s+IS\\s+)?(THE\\s+)?CHEAPEST\\s+CONNECTION\\s+FROM\\s+\\w+\\s+TO\\s+\\w+\\s*\\?$&quot;</b></code>.</p>
	 * 
	 * @param trimmedQuestionText The question text without multiple whitespaces between words ('    ' --&lt;' ')
	 * @param normalizedQuestionText A non null/empty string matching the specified regex.
	 * @return A string containing the cheapest connection path.
	 */
	public String cheapestConnection(String trimmedQuestionText, String normalizedQuestionText) {
		String terminalPoints[] = StringIO.mirrorSubString(trimmedQuestionText, normalizedQuestionText, FROM_KEYWORD, TO_KEYWORD);
		String connection = query.cheapestConnection(terminalPoints[0].trim(), terminalPoints[1].trim());
		
		return connection; 
	}

	/**
	 * <p>Process question matching the following regex:</p>
	 * <p><code><b>&quot;^(HOW\\s+MANY\\s+)?(DIFFERENT\\s+)?CONNECTIONS\\s+(WITH\\s+)?(MAXIMUM|MINIMUM|EXACTLY)\\s+\\d+\\s+(STOP(S)?\\s+)(EXIST(S)?\\s+)?BETWEEN\\s+\\w+\\s+AND\\s+\\w+\\s*\\?$&quot;</b></code>.</p>
	 * 
	 * @param trimmedQuestionText The question text without multiple whitespaces between words ('    ' --&lt;' ')
	 * @param normalizedQuestionText A non null/empty string matching the specified regex.
	 * @return string containing decimal digits.
	 */	
	public String connectionsWithStops(String trimmedQuestionText, String normalizedQuestionText) {
		String terminalPoints[] = StringIO.mirrorSubString(trimmedQuestionText, normalizedQuestionText, BETWEEN_KEYWORD, AND_KEYWORD);
		int stops;
		int result = 0;
		
		if(normalizedQuestionText.contains(MAXIMUM_KEYWORD)) {
			
			stops = Integer.parseInt(StringIO.substring(normalizedQuestionText, MAXIMUM_KEYWORD, STOP_KEYWORD));
			result = query.connectionsWithMaximumStops(stops, terminalPoints[0].trim(), terminalPoints[1].trim());
			
		} else if (normalizedQuestionText.contains(MINIMUM_KEYWORD)) {
			
			stops = Integer.parseInt(StringIO.substring(normalizedQuestionText, MINIMUM_KEYWORD, STOP_KEYWORD));
			result = query.connectionsWithMinimumStops(stops, terminalPoints[0].trim(), terminalPoints[1].trim());
			
		}else if (normalizedQuestionText.contains(EXACTLY_KEYWORD)) {
			
			stops = Integer.parseInt(StringIO.substring(normalizedQuestionText, EXACTLY_KEYWORD, STOP_KEYWORD));
			result = query.connectionsWithExactStops(stops, terminalPoints[0].trim(), terminalPoints[1].trim());
			
		}
		return Integer.toString(result);
	}

	/**
	 * <p>Process question matching the following regex:</p>
	 * <p><code><b>&quot;^((FIND\\s+)?(ALL\\s+)?)?CONNECTIONS\\s+FROM\\s+\\w+\\s+TO\\s+\\w+\\s+BELOW\\s+\\d+\\s+EUROS?\\S?\\s*\\??$&quot;</b></code>.</p>
	 * 
	 * @param normalizedQuestionText A non null/empty string matching the specified regex.
	 * @return string containing decimal digits.
	 */
	public String connectionsWithExactStops(String trimmedQuestionText, String normalizedQuestionText) {
		String terminalPoints[] = StringIO.mirrorSubString(trimmedQuestionText, normalizedQuestionText, BETWEEN_KEYWORD, AND_KEYWORD);
		int stops = Integer.parseInt(StringIO.substring(normalizedQuestionText, EXACTLY_KEYWORD, STOP_KEYWORD));
		
		String connections = Integer.toString(query.connectionsWithExactStops(stops, terminalPoints[0].trim(), terminalPoints[1].trim()));
		return connections;
	}
	
	/**
	 * <p>Process question matching the following regex:</p>
	 * <p><code><b>&quot;&quot;</b></code>.</p>
	 * 
	 * @param trimmedQuestionText The question text without multiple whitespaces between words ('    ' --&lt;' ')
	 * @param normalizedQuestionText A non null/empty string matching the specified regex.
	 * @return A list of connections separated by comma.
	 */	
	public String connectionsBelowPrice(String trimmedQuestionText, String normalizedQuestionText) {
		String questionTextWithNoEuro = normalizedQuestionText.replaceAll("EURO\\S*", StringIO.EMPTY_STRING);
		String terminalPoints[] = StringIO.mirrorSubString(trimmedQuestionText, questionTextWithNoEuro, FROM_KEYWORD, TO_KEYWORD);
		int upperPrice = Integer.parseInt(StringIO.substring(normalizedQuestionText, BELOW_KEYWORD, EURO_KEYWORD));
		
		String connections = query.connectionsBelowPrice(upperPrice, terminalPoints[0].trim(), terminalPoints[1].trim());
		
		if(connections.trim().length() == 0) {
			return Query.CONNECTION_NOT_FOUND_ERROR;
		}
		return connections;
	}
	
	/**
	 * <p>Evaluates a question and returns results.</p>
	 * @param questionText Any question text matching any of the regular expression specified in the constructor.
	 * 
	 * @return If the question is a valid one, it returns the expeced result according to question's nature.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ParseException
	 */
	public String evaluate(String questionText) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		String trimmed = questionText.trim().replaceAll(StringIO.CONTINUOUS_WHITESPACE_EXPR, StringIO.SINGLE_WHITESPACE_STRING);
		String upperCased = trimmed.toUpperCase();
		
		int index = 0;
		String result;
		
		for(Pattern question: questions) {
			
			if(question.matcher(upperCased).matches()) {
				Method method = methods[index];
				
				result = (String)method.invoke(this, trimmed, upperCased);
				return result;
			}
			
			index++;
		}
		
		throw  new ParseException("The received text doesn't comply the minimum requirements to be a valid question.", 0);
		
	}
	
}
