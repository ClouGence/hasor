/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class DefaultProblem extends CategorizedProblem {
	private char[] fileName;
	private int id;
	private int startPosition;
	private int endPosition;
	private int line;
	public int column;
	private int severity;
	private String[] arguments;
	private String message;

	// cannot directly point to IJavaModelMarker constants from within batch compiler
	private static final String MARKER_TYPE_PROBLEM = "org.eclipse.jdt.core.problem"; //$NON-NLS-1$
	private static final String MARKER_TYPE_TASK = "org.eclipse.jdt.core.task"; //$NON-NLS-1$

	public static final Object[] EMPTY_VALUES = {};

public DefaultProblem(
	char[] originatingFileName,
	String message,
	int id,
	String[] stringArguments,
	int severity,
	int startPosition,
	int endPosition,
	int line,
	int column) {

	this.fileName = originatingFileName;
	this.message = message;
	this.id = id;
	this.arguments = stringArguments;
	this.severity = severity;
	this.startPosition = startPosition;
	this.endPosition = endPosition;
	this.line = line;
	this.column = column;
}

public String errorReportSource(char[] unitSource) {
	//extra from the source the innacurate     token
	//and "highlight" it using some underneath ^^^^^
	//put some context around too.

	//this code assumes that the font used in the console is fixed size

	//sanity .....
	if ((this.startPosition > this.endPosition)
		|| ((this.startPosition < 0) && (this.endPosition < 0))
		|| unitSource.length == 0)
		return Messages.problem_noSourceInformation;

	StringBuffer errorBuffer = new StringBuffer();
	errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(this.line)));
	errorBuffer.append(Util.LINE_SEPARATOR);
	errorBuffer.append('\t');

	char c;
	final char SPACE = '\u0020';
	final char MARK = '^';
	final char TAB = '\t';
	//the next code tries to underline the token.....
	//it assumes (for a good display) that token source does not
	//contain any \r \n. This is false on statements !
	//(the code still works but the display is not optimal !)

	// expand to line limits
	int length = unitSource.length, begin, end;
	for (begin = this.startPosition >= length ? length - 1 : this.startPosition; begin > 0; begin--) {
		if ((c = unitSource[begin - 1]) == '\n' || c == '\r') break;
	}
	for (end = this.endPosition >= length ? length - 1 : this.endPosition ; end+1 < length; end++) {
		if ((c = unitSource[end + 1]) == '\r' || c == '\n') break;
	}

	// trim left and right spaces/tabs
	while ((c = unitSource[begin]) == ' ' || c == '\t') begin++;
	//while ((c = unitSource[end]) == ' ' || c == '\t') end--; TODO (philippe) should also trim right, but all tests are to be updated

	// copy source
	errorBuffer.append(unitSource, begin, end-begin+1);
	errorBuffer.append(Util.LINE_SEPARATOR).append("\t"); //$NON-NLS-1$

	// compute underline
	for (int i = begin; i <this.startPosition; i++) {
		errorBuffer.append((unitSource[i] == TAB) ? TAB : SPACE);
	}
	for (int i = this.startPosition; i <= (this.endPosition >= length ? length - 1 : this.endPosition); i++) {
		errorBuffer.append(MARK);
	}
	return errorBuffer.toString();
}
/**
 * Answer back the original arguments recorded into the problem.
 * @return java.lang.String[]
 */
public String[] getArguments() {
	return this.arguments;
}
/**
 * @see org.eclipse.jdt.core.compiler.CategorizedProblem#getCategoryID()
 */
public int getCategoryID() {
	return ProblemReporter.getProblemCategory(this.severity, this.id);
}

/**
 * Answer the type of problem.
 * @see org.eclipse.jdt.core.compiler.IProblem#getID()
 * @return int
 */
public int getID() {
	return this.id;
}

/**
 * Answers a readable name for the category which this problem belongs to,
 * or null if none could be found.
 * FOR TESTING PURPOSE
 * @return java.lang.String
 */
public String getInternalCategoryMessage() {
	switch(getCategoryID()) {
		case CAT_UNSPECIFIED:
			return "unspecified"; //$NON-NLS-1$
		case CAT_BUILDPATH:
			return "buildpath"; //$NON-NLS-1$
		case CAT_SYNTAX:
			return "syntax"; //$NON-NLS-1$
		case CAT_IMPORT:
			return "import"; //$NON-NLS-1$
		case CAT_TYPE:
			return "type"; //$NON-NLS-1$
		case CAT_MEMBER:
			return "member"; //$NON-NLS-1$
		case CAT_INTERNAL:
			return "internal"; //$NON-NLS-1$
		case CAT_JAVADOC:
			return "javadoc"; //$NON-NLS-1$
		case CAT_CODE_STYLE:
			return "code style"; //$NON-NLS-1$
		case CAT_POTENTIAL_PROGRAMMING_PROBLEM:
			return "potential programming problem"; //$NON-NLS-1$
		case CAT_NAME_SHADOWING_CONFLICT:
			return "name shadowing conflict"; //$NON-NLS-1$
		case CAT_DEPRECATION:
			return "deprecation"; //$NON-NLS-1$
		case CAT_UNNECESSARY_CODE:
			return "unnecessary code"; //$NON-NLS-1$
		case CAT_UNCHECKED_RAW:
			return "unchecked/raw"; //$NON-NLS-1$
		case CAT_NLS:
			return "nls"; //$NON-NLS-1$
		case CAT_RESTRICTION:
			return "restriction"; //$NON-NLS-1$
	}
	return null;
}

/**
 * Returns the marker type associated to this problem.
 * @see org.eclipse.jdt.core.compiler.CategorizedProblem#getMarkerType()
 */
public String getMarkerType() {
	return this.id == IProblem.Task
		? MARKER_TYPE_TASK
		: MARKER_TYPE_PROBLEM;
}

/**
 * Answer a localized, human-readable message string which describes the problem.
 * @return java.lang.String
 */
public String getMessage() {
	return this.message;
}

/**
 * Answer the file name in which the problem was found.
 * @return char[]
 */
public char[] getOriginatingFileName() {
	return this.fileName;
}

/**
 * Answer the end position of the problem (inclusive), or -1 if unknown.
 * @return int
 */
public int getSourceEnd() {
	return this.endPosition;
}
/**
 * Answer the line number in source where the problem begins.
 * @return int
 */
public int getSourceColumnNumber() {
	return this.column;
}
/**
 * Answer the line number in source where the problem begins.
 * @return int
 */
public int getSourceLineNumber() {
	return this.line;
}
/**
 * Answer the start position of the problem (inclusive), or -1 if unknown.
 * @return int
 */
public int getSourceStart() {
	return this.startPosition;
}

/*
 * Helper method: checks the severity to see if the Error bit is set.
 * @return boolean
 */
public boolean isError() {
	return (this.severity & ProblemSeverities.Error) != 0;
}

/*
 * Helper method: checks the severity to see if the Error bit is not set.
 * @return boolean
 */
public boolean isWarning() {
	return (this.severity & ProblemSeverities.Error) == 0;
}

public void setOriginatingFileName(char[] fileName) {
	this.fileName = fileName;
}

/**
 * Set the end position of the problem (inclusive), or -1 if unknown.
 *
 * Used for shifting problem positions.
 * @param sourceEnd the new value of the sourceEnd of the receiver
 */
public void setSourceEnd(int sourceEnd) {
	this.endPosition = sourceEnd;
}

/**
 * Set the line number in source where the problem begins.
 * @param lineNumber the new value of the line number of the receiver
 */
public void setSourceLineNumber(int lineNumber) {

	this.line = lineNumber;
}

/**
 * Set the start position of the problem (inclusive), or -1 if unknown.
 *
 * Used for shifting problem positions.
 * @param sourceStart the new value of the source start position of the receiver
 */
public void setSourceStart(int sourceStart) {
	this.startPosition = sourceStart;
}

public String toString() {
	String s = "Pb(" + (this.id & IProblem.IgnoreCategoriesMask) + ") "; //$NON-NLS-1$ //$NON-NLS-2$
	if (this.message != null) {
		s += this.message;
	} else {
		if (this.arguments != null)
			for (int i = 0; i < this.arguments.length; i++)
				s += " " + this.arguments[i]; //$NON-NLS-1$
	}
	return s;
}
}
