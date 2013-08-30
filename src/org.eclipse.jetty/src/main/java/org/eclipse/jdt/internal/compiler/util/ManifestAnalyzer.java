/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ManifestAnalyzer {
	private static final int
		START = 0,
		IN_CLASSPATH_HEADER = 1, // multistate
		PAST_CLASSPATH_HEADER = 2,
		SKIPPING_WHITESPACE = 3,
		READING_JAR = 4,
		CONTINUING = 5,
		SKIP_LINE = 6;
	private static final char[] CLASSPATH_HEADER_TOKEN =
		"Class-Path:".toCharArray(); //$NON-NLS-1$
	private int classpathSectionsCount;
	private ArrayList calledFilesNames;
	
	/**
	 * Analyze the manifest contents. The given input stream is read using a UTF-8 encoded reader.
	 * If the contents of the input stream is not encoded using a UTF-8 encoding, the analysis will fail.
	 * 
	 * @param inputStream the given input stream.
	 * 
	 * @return <code>true</code> if the analysis is successful, <code>false</code> otherwise.
	 * @throws IOException if an exception occurs while analyzing the file
	 */
	public boolean analyzeManifestContents(InputStream inputStream) throws IOException {
		char[] chars = Util.getInputStreamAsCharArray(inputStream, -1, Util.UTF_8);
		int state = START, substate = 0;
		StringBuffer currentJarToken = new StringBuffer();
		int currentChar;
		this.classpathSectionsCount = 0;
		this.calledFilesNames = null;
		for (int i = 0, max = chars.length; i < max;) {
			currentChar = chars[i++];
			if (currentChar == '\r') {
				// skip \r, will consider \n later (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=251079 )
				if (i < max) {
					currentChar = chars[i++];
				}
			}
			switch (state) {
				case START:
					if (currentChar == CLASSPATH_HEADER_TOKEN[0]) {
						state = IN_CLASSPATH_HEADER;
						substate = 1;
					} else {
						state = SKIP_LINE;
					}
					break;
				case IN_CLASSPATH_HEADER:
					if (currentChar == '\n') {
						state = START;
					} else if (currentChar != CLASSPATH_HEADER_TOKEN[substate++]) {
						state = SKIP_LINE;
					} else if (substate == CLASSPATH_HEADER_TOKEN.length) {
						state = PAST_CLASSPATH_HEADER;
					}
					break;
				case PAST_CLASSPATH_HEADER:
					if (currentChar == ' ') {
						state = SKIPPING_WHITESPACE;
						this.classpathSectionsCount++;
					} else {
						return false;
					}
					break;
				case SKIPPING_WHITESPACE:
					if (currentChar == '\n') {
						state = CONTINUING;
					} else if (currentChar != ' ') {
						currentJarToken.append((char) currentChar);
						state = READING_JAR;
					} else {
						// >>>>>>>>>>>>>>>>>> Add the latest jar read
						addCurrentTokenJarWhenNecessary(currentJarToken);
					}
					break;
				case CONTINUING:
					if (currentChar == '\n') {
						addCurrentTokenJarWhenNecessary(currentJarToken);
						state = START;
					} else if (currentChar == ' ') {
						state = SKIPPING_WHITESPACE;
					} else if (currentChar == CLASSPATH_HEADER_TOKEN[0]) {
						addCurrentTokenJarWhenNecessary(currentJarToken);
						state = IN_CLASSPATH_HEADER;
						substate = 1;
					} else if (this.calledFilesNames == null) {
						// >>>>>>>>>>>>>>>>>> Add the latest jar read
						addCurrentTokenJarWhenNecessary(currentJarToken);
						state = START;
					} else {
						// >>>>>>>>>>>>>>>>>> Add the latest jar read
						addCurrentTokenJarWhenNecessary(currentJarToken);
						state = SKIP_LINE;
					}
					break;
				case SKIP_LINE:
					if (currentChar == '\n') {
						state = START;
					}
					break;
				case READING_JAR:
					if (currentChar == '\n') {
						// appends token below
						state = CONTINUING;
						// >>>>>>>>>>> Add a break to not add the jar yet as it can continue on the next line
						break;
					} else if (currentChar == ' ') {
						// appends token below
						state = SKIPPING_WHITESPACE;
					} else {
						currentJarToken.append((char) currentChar);
						break;
					}
					addCurrentTokenJarWhenNecessary(currentJarToken);
					break;
			}
		}
		switch (state) {
			case START:
				return true;
			case IN_CLASSPATH_HEADER:
				return true;
			case PAST_CLASSPATH_HEADER:
				return false;
			case SKIPPING_WHITESPACE:
				// >>>>>>>>>>>>>>>>>> Add the latest jar read
				addCurrentTokenJarWhenNecessary(currentJarToken);
				return true;
			case CONTINUING:
				// >>>>>>>>>>>>>>>>>> Add the latest jar read
				addCurrentTokenJarWhenNecessary(currentJarToken);
				return true;
			case SKIP_LINE:
				if (this.classpathSectionsCount != 0) {
					if (this.calledFilesNames == null) {
						return false;
					}
				}
				return true;
			case READING_JAR:
				// >>>>>>>>>>>>>>>>>> Add the latest jar read
				return false;
		}
		return true;
	}

	// >>>>>>>>>>>>>>>> Method Extracted from analyzeManifestContents in the READING_JAR Block
	private boolean addCurrentTokenJarWhenNecessary(StringBuffer currentJarToken) {
		if (currentJarToken != null && currentJarToken.length() > 0) {
			if (this.calledFilesNames == null) {
				this.calledFilesNames = new ArrayList();
			}
			this.calledFilesNames.add(currentJarToken.toString());
			currentJarToken.setLength(0);
			return true;
		}
		return false;
	}
	// <<<<<<<<<<<<<<<<<<<<<<


	public int getClasspathSectionsCount() {
		return this.classpathSectionsCount;
	}
	public List getCalledFileNames() {
		return this.calledFilesNames;
	}
}
