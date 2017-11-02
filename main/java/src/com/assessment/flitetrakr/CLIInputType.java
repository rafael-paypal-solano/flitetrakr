package com.assessment.flitetrakr;

/**
 * <p>This enumeration classifies input streamd through which the command line interface (namely 
 * <code><b>com.assessment.flitetrakr.CLI</b></code>) reads data.</p>
 * @author rsolano
 *
 */
public enum CLIInputType {
	
	/**
	 * The user is typing input.
	 */
	CONSOLE,
	
	/**
	 * The user provided a physical file.
	 */
	FILE
}
