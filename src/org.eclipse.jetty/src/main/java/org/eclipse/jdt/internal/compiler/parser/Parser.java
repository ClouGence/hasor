/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Tromey - patch for readTable(String) as described in http://bugs.eclipse.org/bugs/show_bug.cgi?id=32196
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Parser implements  ParserBasicInformation, TerminalTokens, OperatorIds, TypeIds {
	
	protected static final int THIS_CALL = ExplicitConstructorCall.This;
	protected static final int SUPER_CALL = ExplicitConstructorCall.Super;
	public static final char[] FALL_THROUGH_TAG = "$FALL-THROUGH$".toCharArray(); //$NON-NLS-1$
	
	public static char asb[] = null;
	public static char asr[] = null;
	//ast stack
	protected final static int AstStackIncrement = 100;
	public static char base_action[] = null;
	public static final int BracketKinds = 3;

	public static short check_table[] = null;
	public static final int CurlyBracket = 2;
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_AUTOMATON = false;
	private static final String EOF_TOKEN = "$eof" ; //$NON-NLS-1$
	private static final String ERROR_TOKEN = "$error" ; //$NON-NLS-1$
	//expression stack
	protected final static int ExpressionStackIncrement = 100;

	protected final static int GenericsStackIncrement = 10;

	private final static String FILEPREFIX = "parser"; //$NON-NLS-1$
    public static char in_symb[] = null;
	private static final String INVALID_CHARACTER = "Invalid Character" ; //$NON-NLS-1$
	public static char lhs[] =  null;

	public static String name[] = null;
	public static char nasb[] = null;
	public static char nasr[] = null;
	public static char non_terminal_index[] = null;
	private final static String READABLE_NAMES_FILE = "readableNames"; //$NON-NLS-1$
	private final static String READABLE_NAMES_FILE_NAME =
		"org.eclipse.jdt.internal.compiler.parser." + READABLE_NAMES_FILE; //$NON-NLS-1$
	public static String readableName[] = null;

	public static byte rhs[] = null;

	public static int[] reverse_index = null;
	public static char[] recovery_templates_index = null;
	public static char[] recovery_templates = null;
	public static char[] statements_recovery_filter = null;

	public static long rules_compliance[] =  null;

	public static final int RoundBracket = 0;

    public static byte scope_la[] = null;
    public static char scope_lhs[] = null;

	public static char scope_prefix[] = null;
    public static char scope_rhs[] = null;
    public static char scope_state[] = null;

    public static char scope_state_set[] = null;
    public static char scope_suffix[] = null;
	public static final int SquareBracket = 1;

	//internal data for the automat
	protected final static int StackIncrement = 255;

	public static char term_action[] = null;
	public static byte term_check[] = null;

	public static char terminal_index[] = null;

	private static final String UNEXPECTED_EOF = "Unexpected End Of File" ; //$NON-NLS-1$
	public static boolean VERBOSE_RECOVERY = false;

	static {
		try{
			initTables();
		} catch(java.io.IOException ex){
			throw new ExceptionInInitializerError(ex.getMessage());
		}
	}
	public static int asi(int state) {
	
		return asb[original_state(state)];
	}
	public final static short base_check(int i) {
		return check_table[i - (NUM_RULES + 1)];
	}
	private final static void buildFile(String filename, List listToDump) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
	    	for (Iterator iterator = listToDump.iterator(); iterator.hasNext(); ) {
	    		writer.write(String.valueOf(iterator.next()));
	    	}
	    	writer.flush();
		} catch(IOException e) {
			// ignore
		} finally {
			if (writer != null) {
	        	try {
					writer.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
		System.out.println(filename + " creation complete"); //$NON-NLS-1$
	}
	private static void buildFileForCompliance(
			String file,
			int length,
			String[] tokens) {
	
			byte[] result = new byte[length * 8];
	
			for (int i = 0; i < tokens.length; i = i + 3) {
				if("2".equals(tokens[i])) { //$NON-NLS-1$
					int index = Integer.parseInt(tokens[i + 1]);
					String token = tokens[i + 2].trim();
					long compliance = 0;
					if("1.4".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_4;
					} else if("1.5".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_5;
					} else if("recovery".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK_DEFERRED;
					}
	
					int j = index * 8;
					result[j] = 	(byte)(compliance >>> 56);
					result[j + 1] = (byte)(compliance >>> 48);
					result[j + 2] = (byte)(compliance >>> 40);
					result[j + 3] = (byte)(compliance >>> 32);
					result[j + 4] = (byte)(compliance >>> 24);
					result[j + 5] = (byte)(compliance >>> 16);
					result[j + 6] = (byte)(compliance >>> 8);
					result[j + 7] = (byte)(compliance);
				}
			}
	
			buildFileForTable(file, result);
		}
	private final static String[] buildFileForName(String filename, String contents) {
		String[] result = new String[contents.length()];
		result[0] = null;
		int resultCount = 1;
	
		StringBuffer buffer = new StringBuffer();
	
		int start = contents.indexOf("name[]"); //$NON-NLS-1$
		start = contents.indexOf('\"', start);
		int end = contents.indexOf("};", start); //$NON-NLS-1$
	
		contents = contents.substring(start, end);
	
		boolean addLineSeparator = false;
		int tokenStart = -1;
		StringBuffer currentToken = new StringBuffer();
		for (int i = 0; i < contents.length(); i++) {
			char c = contents.charAt(i);
			if(c == '\"') {
				if(tokenStart == -1) {
					tokenStart = i + 1;
				} else {
					if(addLineSeparator) {
						buffer.append('\n');
						result[resultCount++] = currentToken.toString();
						currentToken = new StringBuffer();
					}
					String token = contents.substring(tokenStart, i);
					if(token.equals(ERROR_TOKEN)){
						token = INVALID_CHARACTER;
					} else if(token.equals(EOF_TOKEN)) {
						token = UNEXPECTED_EOF;
					}
					buffer.append(token);
					currentToken.append(token);
					addLineSeparator = true;
					tokenStart = -1;
				}
			}
			if(tokenStart == -1 && c == '+'){
				addLineSeparator = false;
			}
		}
		if(currentToken.length() > 0) {
			result[resultCount++] = currentToken.toString();
		}
	
		buildFileForTable(filename, buffer.toString().toCharArray());
	
		System.arraycopy(result, 0, result = new String[resultCount], 0, resultCount);
		return result;
	}
	private static void buildFileForReadableName(
		String file,
		char[] newLhs,
		char[] newNonTerminalIndex,
		String[] newName,
		String[] tokens) {
	
		ArrayList entries = new ArrayList();
	
		boolean[] alreadyAdded = new boolean[newName.length];
	
		for (int i = 0; i < tokens.length; i = i + 3) {
			if("1".equals(tokens[i])) { //$NON-NLS-1$
				int index = newNonTerminalIndex[newLhs[Integer.parseInt(tokens[i + 1])]];
				StringBuffer buffer = new StringBuffer();
				if(!alreadyAdded[index]) {
					alreadyAdded[index] = true;
					buffer.append(newName[index]);
					buffer.append('=');
					buffer.append(tokens[i+2].trim());
					buffer.append('\n');
					entries.add(String.valueOf(buffer));
				}
			}
		}
		int i = 1;
		while(!INVALID_CHARACTER.equals(newName[i])) i++;
		i++;
		for (; i < alreadyAdded.length; i++) {
			if(!alreadyAdded[i]) {
				System.out.println(newName[i] + " has no readable name"); //$NON-NLS-1$
			}
		}
		Collections.sort(entries);
		buildFile(file, entries);
	}
	private final static void buildFileForTable(String filename, byte[] bytes) {
		java.io.FileOutputStream stream = null;
		try {
			stream = new java.io.FileOutputStream(filename);
			stream.write(bytes);
		} catch(IOException e) {
			// ignore
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		System.out.println(filename + " creation complete"); //$NON-NLS-1$
	}
	private final static void buildFileForTable(String filename, char[] chars) {
		byte[] bytes = new byte[chars.length * 2];
		for (int i = 0; i < chars.length; i++) {
			bytes[2 * i] = (byte) (chars[i] >>> 8);
			bytes[2 * i + 1] = (byte) (chars[i] & 0xFF);
		}
	
		java.io.FileOutputStream stream = null;
		try {
			stream = new java.io.FileOutputStream(filename);
			stream.write(bytes);
		} catch(IOException e) {
			// ignore
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		System.out.println(filename + " creation complete"); //$NON-NLS-1$
	}
	private final static byte[] buildFileOfByteFor(String filename, String tag, String[] tokens) {
	
		//transform the String tokens into chars before dumping then into file
	
		int i = 0;
		//read upto the tag
		while (!tokens[i++].equals(tag)){/*empty*/}
		//read upto the }
	
		byte[] bytes = new byte[tokens.length]; //can't be bigger
		int ic = 0;
		String token;
		while (!(token = tokens[i++]).equals("}")) { //$NON-NLS-1$
			int c = Integer.parseInt(token);
			bytes[ic++] = (byte) c;
		}
	
		//resize
		System.arraycopy(bytes, 0, bytes = new byte[ic], 0, ic);
	
		buildFileForTable(filename, bytes);
		return bytes;
	}
	private final static char[] buildFileOfIntFor(String filename, String tag, String[] tokens) {
	
		//transform the String tokens into chars before dumping then into file
	
		int i = 0;
		//read upto the tag
		while (!tokens[i++].equals(tag)){/*empty*/}
		//read upto the }
	
		char[] chars = new char[tokens.length]; //can't be bigger
		int ic = 0;
		String token;
		while (!(token = tokens[i++]).equals("}")) { //$NON-NLS-1$
			int c = Integer.parseInt(token);
			chars[ic++] = (char) c;
		}
	
		//resize
		System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
	
		buildFileForTable(filename, chars);
		return chars;
	}
	private final static void buildFileOfShortFor(String filename, String tag, String[] tokens) {
	
		//transform the String tokens into chars before dumping then into file
	
		int i = 0;
		//read upto the tag
		while (!tokens[i++].equals(tag)){/*empty*/}
		//read upto the }
	
		char[] chars = new char[tokens.length]; //can't be bigger
		int ic = 0;
		String token;
		while (!(token = tokens[i++]).equals("}")) { //$NON-NLS-1$
			int c = Integer.parseInt(token);
			chars[ic++] = (char) (c + 32768);
		}
	
		//resize
		System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
	
		buildFileForTable(filename, chars);
	}
	private static void buildFilesForRecoveryTemplates(
		String indexFilename,
		String templatesFilename,
		char[] newTerminalIndex,
		char[] newNonTerminalIndex,
		String[] newName,
		char[] newLhs,
		String[] tokens) {
	
		int[] newReverse = computeReverseTable(newTerminalIndex, newNonTerminalIndex, newName);
	
		char[] newRecoveyTemplatesIndex = new char[newNonTerminalIndex.length];
		char[] newRecoveyTemplates = new char[newNonTerminalIndex.length];
		int newRecoveyTemplatesPtr = 0;
	
		for (int i = 0; i < tokens.length; i = i + 3) {
			if("3".equals(tokens[i])) { //$NON-NLS-1$
				int length = newRecoveyTemplates.length;
				if(length == newRecoveyTemplatesPtr + 1) {
					System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
				}
				newRecoveyTemplates[newRecoveyTemplatesPtr++] = 0;
	
				int index = newLhs[Integer.parseInt(tokens[i + 1])];
	
				newRecoveyTemplatesIndex[index] = (char)newRecoveyTemplatesPtr;
	
				String token = tokens[i + 2].trim();
				java.util.StringTokenizer st = new java.util.StringTokenizer(token, " ");  //$NON-NLS-1$
				String[] terminalNames = new String[st.countTokens()];
				int t = 0;
				while (st.hasMoreTokens()) {
					terminalNames[t++] = st.nextToken();
				}
	
				for (int j = 0; j < terminalNames.length; j++) {
					int symbol = getSymbol(terminalNames[j], newName, newReverse);
					if(symbol > -1) {
						length = newRecoveyTemplates.length;
						if(length == newRecoveyTemplatesPtr + 1) {
							System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
						}
						newRecoveyTemplates[newRecoveyTemplatesPtr++] = (char)symbol;
					}
				}
			}
		}
		newRecoveyTemplates[newRecoveyTemplatesPtr++] = 0;
		System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[newRecoveyTemplatesPtr], 0, newRecoveyTemplatesPtr);
	
		buildFileForTable(indexFilename, newRecoveyTemplatesIndex);
		buildFileForTable(templatesFilename, newRecoveyTemplates);
	}
	private static void buildFilesForStatementsRecoveryFilter(
			String filename,
			char[] newNonTerminalIndex,
			char[] newLhs,
			String[] tokens) {
	
			char[] newStatementsRecoveryFilter = new char[newNonTerminalIndex.length];
	
			for (int i = 0; i < tokens.length; i = i + 3) {
				if("4".equals(tokens[i])) { //$NON-NLS-1$
					int index = newLhs[Integer.parseInt(tokens[i + 1])];
	
					newStatementsRecoveryFilter[index] = 1;
				}
			}
			buildFileForTable(filename, newStatementsRecoveryFilter);
		}
	public final static void buildFilesFromLPG(String dataFilename, String dataFilename2) {
	
		//RUN THIS METHOD TO GENERATE PARSER*.RSC FILES
	
		//build from the lpg javadcl.java files that represents the parser tables
		//lhs check_table asb asr symbol_index
	
		//[org.eclipse.jdt.internal.compiler.parser.Parser.buildFilesFromLPG("d:/leapfrog/grammar/javadcl.java")]
		char[] contents = CharOperation.NO_CHAR;
		try {
			contents = Util.getFileCharContent(new File(dataFilename), null);
		} catch (IOException ex) {
			System.out.println(Messages.parser_incorrectPath);
			return;
		}
		java.util.StringTokenizer st =
			new java.util.StringTokenizer(new String(contents), " \t\n\r[]={,;");  //$NON-NLS-1$
		String[] tokens = new String[st.countTokens()];
		int j = 0;
		while (st.hasMoreTokens()) {
			tokens[j++] = st.nextToken();
		}
		final String prefix = FILEPREFIX;
		int i = 0;
	
		char[] newLhs = buildFileOfIntFor(prefix + (++i) + ".rsc", "lhs", tokens); //$NON-NLS-1$ //$NON-NLS-2$
		buildFileOfShortFor(prefix + (++i) + ".rsc", "check_table", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "asb", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "asr", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "nasb", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "nasr", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		char[] newTerminalIndex = buildFileOfIntFor(prefix + (++i) + ".rsc", "terminal_index", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		char[] newNonTerminalIndex = buildFileOfIntFor(prefix + (++i) + ".rsc", "non_terminal_index", tokens); //$NON-NLS-1$ //$NON-NLS-2$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "term_action", tokens); //$NON-NLS-2$ //$NON-NLS-1$
	
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_prefix", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_suffix", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_lhs", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_state_set", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_rhs", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_state", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "in_symb", tokens); //$NON-NLS-2$ //$NON-NLS-1$
	
		byte[] newRhs = buildFileOfByteFor(prefix + (++i) + ".rsc", "rhs", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfByteFor(prefix + (++i) + ".rsc", "term_check", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfByteFor(prefix + (++i) + ".rsc", "scope_la", tokens); //$NON-NLS-2$ //$NON-NLS-1$
	
		String[] newName = buildFileForName(prefix + (++i) + ".rsc", new String(contents)); //$NON-NLS-1$
	
		contents = CharOperation.NO_CHAR;
		try {
			contents = Util.getFileCharContent(new File(dataFilename2), null);
		} catch (IOException ex) {
			System.out.println(Messages.parser_incorrectPath);
			return;
		}
		st = new java.util.StringTokenizer(new String(contents), "\t\n\r#");  //$NON-NLS-1$
		tokens = new String[st.countTokens()];
		j = 0;
		while (st.hasMoreTokens()) {
			tokens[j++] = st.nextToken();
		}
	
		buildFileForCompliance(prefix + (++i) + ".rsc", newRhs.length, tokens);//$NON-NLS-1$
		buildFileForReadableName(READABLE_NAMES_FILE+".properties", newLhs, newNonTerminalIndex, newName, tokens);//$NON-NLS-1$
	
		buildFilesForRecoveryTemplates(
				prefix + (++i) + ".rsc", //$NON-NLS-1$
				prefix + (++i) + ".rsc", //$NON-NLS-1$
				newTerminalIndex,
				newNonTerminalIndex,
				newName,
				newLhs,
				tokens);
	
		buildFilesForStatementsRecoveryFilter(
				prefix + (++i) + ".rsc", //$NON-NLS-1$
				newNonTerminalIndex,
				newLhs,
				tokens);
	
	
		System.out.println(Messages.parser_moveFiles);
	}
	protected static int[] computeReverseTable(char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName) {
		int[] newReverseTable = new int[newName.length];
		for (int j = 0; j < newName.length; j++) {
			found : {
				for (int k = 0; k < newTerminalIndex.length; k++) {
					if(newTerminalIndex[k] == j) {
						newReverseTable[j] = k;
						break found;
					}
				}
				for (int k = 0; k < newNonTerminalIndex.length; k++) {
					if(newNonTerminalIndex[k] == j) {
						newReverseTable[j] = -k;
						break found;
					}
				}
			}
		}
		return newReverseTable;
	}

	private static int getSymbol(String terminalName, String[] newName, int[] newReverse) {
		for (int j = 0; j < newName.length; j++) {
			if(terminalName.equals(newName[j])) {
				return newReverse[j];
			}
		}
		return -1;
	}
	public static int in_symbol(int state) {
		return in_symb[original_state(state)];
	}
	public final static void initTables() throws java.io.IOException {
	
		final String prefix = FILEPREFIX;
		int i = 0;
		lhs = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		char[] chars = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		check_table = new short[chars.length];
		for (int c = chars.length; c-- > 0;) {
			check_table[c] = (short) (chars[c] - 32768);
		}
		asb = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		asr = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		nasb = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		nasr = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		terminal_index = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		non_terminal_index = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		term_action = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		scope_prefix = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		scope_suffix = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		scope_lhs = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		scope_state_set = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		scope_rhs = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		scope_state = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		in_symb = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		rhs = readByteTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		term_check = readByteTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		scope_la = readByteTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		name = readNameTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		rules_compliance = readLongTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		readableName = readReadableNameTable(READABLE_NAMES_FILE_NAME);
	
		reverse_index = computeReverseTable(terminal_index, non_terminal_index, name);
	
		recovery_templates_index = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
		recovery_templates = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		statements_recovery_filter = readTable(prefix + (++i) + ".rsc"); //$NON-NLS-1$
	
		base_action = lhs;
	}
	public static int nasi(int state) {
		return nasb[original_state(state)];
	}
	public static int ntAction(int state, int sym) {
		return base_action[state + sym];
	}
	protected static int original_state(int state) {
		return -base_check(state);
	}

	protected static byte[] readByteTable(String filename) throws java.io.IOException {
	
		//files are located at Parser.class directory
	
		InputStream stream = Parser.class.getResourceAsStream(filename);
		if (stream == null) {
			throw new java.io.IOException(Messages.bind(Messages.parser_missingFile, filename));
		}
		byte[] bytes = null;
		try {
			stream = new BufferedInputStream(stream);
			bytes = Util.getInputStreamAsByteArray(stream, -1);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return bytes;
	}
	protected static long[] readLongTable(String filename) throws java.io.IOException {
	
		//files are located at Parser.class directory
	
		InputStream stream = Parser.class.getResourceAsStream(filename);
		if (stream == null) {
			throw new java.io.IOException(Messages.bind(Messages.parser_missingFile, filename));
		}
		byte[] bytes = null;
		try {
			stream = new BufferedInputStream(stream);
			bytes = Util.getInputStreamAsByteArray(stream, -1);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	
		//minimal integrity check (even size expected)
		int length = bytes.length;
		if (length % 8 != 0)
			throw new java.io.IOException(Messages.bind(Messages.parser_corruptedFile, filename));
	
		// convert bytes into longs
		long[] longs = new long[length / 8];
		int i = 0;
		int longIndex = 0;
	
		while (true) {
			longs[longIndex++] =
			  (((long) (bytes[i++] & 0xFF)) << 56)
			+ (((long) (bytes[i++] & 0xFF)) << 48)
			+ (((long) (bytes[i++] & 0xFF)) << 40)
			+ (((long) (bytes[i++] & 0xFF)) << 32)
			+ (((long) (bytes[i++] & 0xFF)) << 24)
			+ (((long) (bytes[i++] & 0xFF)) << 16)
			+ (((long) (bytes[i++] & 0xFF)) << 8)
			+ (bytes[i++] & 0xFF);
	
			if (i == length)
				break;
		}
		return longs;
	}

	protected static String[] readNameTable(String filename) throws java.io.IOException {
		char[] contents = readTable(filename);
		char[][] nameAsChar = CharOperation.splitOn('\n', contents);
	
		String[] result = new String[nameAsChar.length + 1];
		result[0] = null;
		for (int i = 0; i < nameAsChar.length; i++) {
			result[i + 1] = new String(nameAsChar[i]);
		}
	
		return result;
	}
	protected static String[] readReadableNameTable(String filename) {
		String[] result = new String[name.length];
	
		ResourceBundle bundle;
		try {
			bundle = ResourceBundle.getBundle(filename, Locale.getDefault());
		} catch(MissingResourceException e) {
			System.out.println("Missing resource : " + filename.replace('.', '/') + ".properties for locale " + Locale.getDefault()); //$NON-NLS-1$//$NON-NLS-2$
			throw e;
		}
		for (int i = 0; i < NT_OFFSET + 1; i++) {
			result[i] = name[i];
		}
		for (int i = NT_OFFSET; i < name.length; i++) {
			try {
				String n = bundle.getString(name[i]);
				if(n != null && n.length() > 0) {
					result[i] = n;
				} else {
					result[i] = name[i];
				}
			} catch(MissingResourceException e) {
				result[i] = name[i];
			}
		}
		return result;
	}
	protected static char[] readTable(String filename) throws java.io.IOException {
	
		//files are located at Parser.class directory
	
		InputStream stream = Parser.class.getResourceAsStream(filename);
		if (stream == null) {
			throw new java.io.IOException(Messages.bind(Messages.parser_missingFile, filename));
		}
		byte[] bytes = null;
		try {
			stream = new BufferedInputStream(stream);
			bytes = Util.getInputStreamAsByteArray(stream, -1);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	
		//minimal integrity check (even size expected)
		int length = bytes.length;
		if ((length & 1) != 0)
			throw new java.io.IOException(Messages.bind(Messages.parser_corruptedFile, filename));
	
		// convert bytes into chars
		char[] chars = new char[length / 2];
		int i = 0;
		int charIndex = 0;
	
		while (true) {
			chars[charIndex++] = (char) (((bytes[i++] & 0xFF) << 8) + (bytes[i++] & 0xFF));
			if (i == length)
				break;
		}
		return chars;
	}
	public static int tAction(int state, int sym) {
		return term_action[term_check[base_action[state]+sym] == sym ? base_action[state] + sym : base_action[state]];
	}
	protected int astLengthPtr;

	protected int[] astLengthStack;
	protected int astPtr;
	protected ASTNode[] astStack = new ASTNode[AstStackIncrement];
	public CompilationUnitDeclaration compilationUnit; /*the result from parse()*/

	protected RecoveredElement currentElement;
	public int currentToken;
	protected boolean diet = false; //tells the scanner to jump over some parts of the code/expressions like method bodies
	protected int dietInt = 0; // if > 0 force the none-diet-parsing mode (even if diet if requested) [field parsing with anonymous inner classes...]
	protected int endPosition; //accurate only when used ! (the start position is pushed into intStack while the end the current one)
	protected int endStatementPosition;
	protected int expressionLengthPtr;
	protected int[] expressionLengthStack;
	protected int expressionPtr;
	protected Expression[] expressionStack = new Expression[ExpressionStackIncrement];
	public int firstToken ; // handle for multiple parsing goals
	// generics management
	protected int genericsIdentifiersLengthPtr;
	protected int[] genericsIdentifiersLengthStack = new int[GenericsStackIncrement];
	protected int genericsLengthPtr;
	protected int[] genericsLengthStack = new int[GenericsStackIncrement];
	protected int genericsPtr;
	protected ASTNode[] genericsStack = new ASTNode[GenericsStackIncrement];
	protected boolean hasError;
	protected boolean hasReportedError;
	//identifiers stacks
	protected int identifierLengthPtr;
	protected int[] identifierLengthStack;
	protected long[] identifierPositionStack;
	protected int identifierPtr;
	protected char[][] identifierStack;
	protected boolean ignoreNextOpeningBrace;

	//positions , dimensions , .... (int stacks)
	protected int intPtr;

	protected int[] intStack;
	public int lastAct ; //handle for multiple parsing goals
	//error recovery management
	protected int lastCheckPoint;
	protected int lastErrorEndPosition;
	protected int lastErrorEndPositionBeforeRecovery = -1;
	protected int lastIgnoredToken, nextIgnoredToken;

	protected int listLength; // for recovering some incomplete list (interfaces, throws or parameters)

	protected int listTypeParameterLength; // for recovering some incomplete list (type parameters)
	protected int lParenPos,rParenPos; //accurate only when used !
	protected int modifiers;
	protected int modifiersSourceStart;
	protected int[] nestedMethod; //the ptr is nestedType

	protected int nestedType, dimensions;
	ASTNode [] noAstNodes = new ASTNode[AstStackIncrement];

	Expression [] noExpressions = new Expression[ExpressionStackIncrement];
	//modifiers dimensions nestedType etc.......
	protected boolean optimizeStringLiterals =true;
	protected CompilerOptions options;

	protected ProblemReporter problemReporter;

	protected int rBraceStart, rBraceEnd, rBraceSuccessorStart; //accurate only when used !
protected int realBlockPtr;
protected int[] realBlockStack;
protected int recoveredStaticInitializerStart;
public ReferenceContext referenceContext;
public boolean reportOnlyOneSyntaxError = false;
public boolean reportSyntaxErrorIsRequired = true;
protected boolean restartRecovery;
protected boolean annotationRecoveryActivated = true;
protected int lastPosistion;
// statement recovery
public boolean methodRecoveryActivated = false;
protected boolean statementRecoveryActivated = false;
protected TypeDeclaration[] recoveredTypes;
protected int recoveredTypePtr;
protected int nextTypeStart;
protected TypeDeclaration pendingRecoveredType;
public RecoveryScanner recoveryScanner;
//scanner token
public Scanner scanner;
protected int[] stack = new int[StackIncrement];
protected int stateStackTop;
protected int synchronizedBlockSourceStart;

protected int[] variablesCounter;

protected boolean checkExternalizeStrings;

protected boolean recordStringLiterals;
// javadoc
public Javadoc javadoc;
public JavadocParser javadocParser;
// used for recovery
protected int lastJavadocEnd;
public org.eclipse.jdt.internal.compiler.ReadManager readManager;

public Parser(ProblemReporter problemReporter, boolean optimizeStringLiterals) {

	this.problemReporter = problemReporter;
	this.options = problemReporter.options;
	this.optimizeStringLiterals = optimizeStringLiterals;
	initializeScanner();
	this.astLengthStack = new int[50];
	this.expressionLengthStack = new int[30];
	this.intStack = new int[50];
	this.identifierStack = new char[30][];
	this.identifierLengthStack = new int[30];
	this.nestedMethod = new int[30];
	this.realBlockStack = new int[30];
	this.identifierPositionStack = new long[30];
	this.variablesCounter = new int[30];

	// javadoc support
	this.javadocParser = createJavadocParser();
}
protected void annotationRecoveryCheckPoint(int start, int end) {
	if(this.lastCheckPoint < end) {
		this.lastCheckPoint = end + 1;
	}
}
public void arrayInitializer(int length) {
	//length is the size of the array Initializer
	//expressionPtr points on the last elt of the arrayInitializer,
	// in other words, it has not been decremented yet.

	ArrayInitializer ai = new ArrayInitializer();
	if (length != 0) {
		this.expressionPtr -= length;
		System.arraycopy(this.expressionStack, this.expressionPtr + 1, ai.expressions = new Expression[length], 0, length);
	}
	pushOnExpressionStack(ai);
	//positionning
	ai.sourceEnd = this.endStatementPosition;
	ai.sourceStart = this.intStack[this.intPtr--];
}
protected void blockReal() {
	// See consumeLocalVariableDeclarationStatement in case of change: duplicated code
	// increment the amount of declared variables for this block
	this.realBlockStack[this.realBlockPtr]++;
}
/*
 * Build initial recovery state.
 * Recovery state is inferred from the current state of the parser (reduced node stack).
 */
public RecoveredElement buildInitialRecoveryState(){

	/* initialize recovery by retrieving available reduced nodes
	 * also rebuild bracket balance
	 */
	this.lastCheckPoint = 0;
	this.lastErrorEndPositionBeforeRecovery = this.scanner.currentPosition;

	RecoveredElement element = null;
	if (this.referenceContext instanceof CompilationUnitDeclaration){
		element = new RecoveredUnit(this.compilationUnit, 0, this);

		/* ignore current stack state, since restarting from the beginnning
		   since could not trust simple brace count */
		// restart recovery from scratch
		this.compilationUnit.currentPackage = null;
		this.compilationUnit.imports = null;
		this.compilationUnit.types = null;
		this.currentToken = 0;
		this.listLength = 0;
		this.listTypeParameterLength = 0;
		this.endPosition = 0;
		this.endStatementPosition = 0;
		return element;
	} else {
		if (this.referenceContext instanceof AbstractMethodDeclaration){
			element = new RecoveredMethod((AbstractMethodDeclaration) this.referenceContext, null, 0, this);
			this.lastCheckPoint = ((AbstractMethodDeclaration) this.referenceContext).bodyStart;
			if(this.statementRecoveryActivated) {
				element = element.add(new Block(0), 0);
			}
		} else {
			/* Initializer bodies are parsed in the context of the type declaration, we must thus search it inside */
			if (this.referenceContext instanceof TypeDeclaration){
				TypeDeclaration type = (TypeDeclaration) this.referenceContext;
				FieldDeclaration[] fieldDeclarations = type.fields;
				int length = fieldDeclarations == null ? 0 : fieldDeclarations.length;
				for (int i = 0; i < length; i++){
					FieldDeclaration field = fieldDeclarations[i];
					if (field != null
						&& field.getKind() == AbstractVariableDeclaration.INITIALIZER
						&& ((Initializer) field).block != null
						&& field.declarationSourceStart <= this.scanner.initialPosition
						&& this.scanner.initialPosition <= field.declarationSourceEnd
						&& this.scanner.eofPosition <= field.declarationSourceEnd+1){
						element = new RecoveredInitializer(field, null, 1, this);
						this.lastCheckPoint = field.declarationSourceStart;
						break;
					}
				}
			}
		}
	}

	if (element == null) return element;

	for(int i = 0; i <= this.astPtr; i++){
		ASTNode node = this.astStack[i];
		if (node instanceof AbstractMethodDeclaration){
			AbstractMethodDeclaration method = (AbstractMethodDeclaration) node;
			if (method.declarationSourceEnd == 0){
				element = element.add(method, 0);
				this.lastCheckPoint = method.bodyStart;
			} else {
				element = element.add(method, 0);
				this.lastCheckPoint = method.declarationSourceEnd + 1;
			}
			continue;
		}
		if (node instanceof Initializer){
			Initializer initializer = (Initializer) node;
			// ignore initializer with no block
			if (initializer.block == null) continue;
			if (initializer.declarationSourceEnd == 0){
				element = element.add(initializer, 1);
				this.lastCheckPoint = initializer.sourceStart;
			} else {
				element = element.add(initializer, 0);
				this.lastCheckPoint = initializer.declarationSourceEnd + 1;
			}
			continue;
		}
		if (node instanceof FieldDeclaration){
			FieldDeclaration field = (FieldDeclaration) node;
			if (field.declarationSourceEnd == 0){
				element = element.add(field, 0);
				if (field.initialization == null){
					this.lastCheckPoint = field.sourceEnd + 1;
				} else {
					this.lastCheckPoint = field.initialization.sourceEnd + 1;
				}
			} else {
				element = element.add(field, 0);
				this.lastCheckPoint = field.declarationSourceEnd + 1;
			}
			continue;
		}
		if (node instanceof TypeDeclaration){
			TypeDeclaration type = (TypeDeclaration) node;
			if ((type.modifiers & ClassFileConstants.AccEnum) != 0) {
				// do not allow enums to be build as recovery types
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=340691
				continue;
			}
			if (type.declarationSourceEnd == 0){
				element = element.add(type, 0);
				this.lastCheckPoint = type.bodyStart;
			} else {
				element = element.add(type, 0);
				this.lastCheckPoint = type.declarationSourceEnd + 1;
			}
			continue;
		}
		if (node instanceof ImportReference){
			ImportReference importRef = (ImportReference) node;
			element = element.add(importRef, 0);
			this.lastCheckPoint = importRef.declarationSourceEnd + 1;
		}
		if(this.statementRecoveryActivated) {
			if(node instanceof Block) {
				Block block = (Block) node;
				element = element.add(block, 0);
				this.lastCheckPoint = block.sourceEnd + 1;
			} else if(node instanceof LocalDeclaration) {
				LocalDeclaration statement = (LocalDeclaration) node;
				element = element.add(statement, 0);
				this.lastCheckPoint = statement.sourceEnd + 1;
			} else if(node instanceof Expression) {
				if(node instanceof Assignment ||
						node instanceof PrefixExpression ||
						node instanceof PostfixExpression ||
						node instanceof MessageSend ||
						node instanceof AllocationExpression) {
					// recover only specific expressions
					Expression statement = (Expression) node;
					element = element.add(statement, 0);
					if(statement.statementEnd != -1) {
						this.lastCheckPoint = statement.statementEnd + 1;
					} else {
						this.lastCheckPoint = statement.sourceEnd + 1;
					}
				}
			} else if(node instanceof Statement) {
				Statement statement = (Statement) node;
				element = element.add(statement, 0);
				this.lastCheckPoint = statement.sourceEnd + 1;
			}
		}
	}

	if (this.statementRecoveryActivated) {
		if (this.pendingRecoveredType != null &&
				this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
			// Add the pending type to the AST if this type isn't already added in the AST.
			element = element.add(this.pendingRecoveredType, 0);
			this.lastCheckPoint = this.pendingRecoveredType.declarationSourceEnd + 1;
			this.pendingRecoveredType = null;
		}
	}
	return element;
}

protected void checkAndSetModifiers(int flag){
	/*modify the current modifiers buffer.
	When the startPosition of the modifiers is 0
	it means that the modifier being parsed is the first
	of a list of several modifiers. The startPosition
	is zeroed when a copy of modifiers-buffer is push
	onto the this.astStack. */

	if ((this.modifiers & flag) != 0){ // duplicate modifier
		this.modifiers |= ExtraCompilerModifiers.AccAlternateModifierProblem;
	}
	this.modifiers |= flag;

	if (this.modifiersSourceStart < 0) this.modifiersSourceStart = this.scanner.startPosition;

	if (this.currentElement != null && this.annotationRecoveryActivated) {
		this.currentElement.addModifier(flag, this.modifiersSourceStart);
	}
}
public void checkComment() {

	// discard obsolete comments while inside methods or fields initializer (see bug 74369)
	if (!(this.diet && this.dietInt==0) && this.scanner.commentPtr >= 0) {
		flushCommentsDefinedPriorTo(this.endStatementPosition);
	}

	int lastComment = this.scanner.commentPtr;

	if (this.modifiersSourceStart >= 0) {
		// eliminate comments located after modifierSourceStart if positionned
		while (lastComment >= 0) {
			int commentSourceStart = this.scanner.commentStarts[lastComment];
			if (commentSourceStart < 0) commentSourceStart = -commentSourceStart;
			if (commentSourceStart <= this.modifiersSourceStart) break;
			lastComment--;
		}
	}
	if (lastComment >= 0) {
		// consider all remaining leading comments to be part of current declaration
		this.modifiersSourceStart = this.scanner.commentStarts[0];
		if (this.modifiersSourceStart < 0) this.modifiersSourceStart = -this.modifiersSourceStart;

		// check deprecation in last comment if javadoc (can be followed by non-javadoc comments which are simply ignored)
		while (lastComment >= 0 && this.scanner.commentStops[lastComment] < 0) lastComment--; // non javadoc comment have negative end positions
		if (lastComment >= 0 && this.javadocParser != null) {
			int commentEnd = this.scanner.commentStops[lastComment] - 1; //stop is one over,
			// do not report problem before last parsed comment while recovering code...
			if (this.javadocParser.shouldReportProblems) {
				this.javadocParser.reportProblems = this.currentElement == null || commentEnd > this.lastJavadocEnd;
			} else {
				this.javadocParser.reportProblems = false;
			}
			if (this.javadocParser.checkDeprecation(lastComment)) {
				checkAndSetModifiers(ClassFileConstants.AccDeprecated);
			}
			this.javadoc = this.javadocParser.docComment;	// null if check javadoc is not activated
			if (this.currentElement == null) this.lastJavadocEnd = commentEnd;
		}
	}
}
protected void checkNonNLSAfterBodyEnd(int declarationEnd){
	if(this.scanner.currentPosition - 1 <= declarationEnd) {
		this.scanner.eofPosition = declarationEnd < Integer.MAX_VALUE ? declarationEnd + 1 : declarationEnd;
		try {
			while(this.scanner.getNextToken() != TokenNameEOF){/*empty*/}
		} catch (InvalidInputException e) {
			// Nothing to do
		}
	}
}
protected void classInstanceCreation(boolean isQualified) {
	// ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt

	// ClassBodyopt produces a null item on the astStak if it produces NO class body
	// An empty class body produces a 0 on the length stack.....

	AllocationExpression alloc;
	int length;
	if (((length = this.astLengthStack[this.astLengthPtr--]) == 1)
		&& (this.astStack[this.astPtr] == null)) {
		//NO ClassBody
		this.astPtr--;
		if (isQualified) {
			alloc = new QualifiedAllocationExpression();
		} else {
			alloc = new AllocationExpression();
		}
		alloc.sourceEnd = this.endPosition; //the position has been stored explicitly

		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				alloc.arguments = new Expression[length],
				0,
				length);
		}
		alloc.type = getTypeReference(0);
		checkForDiamond(alloc.type);

		//the default constructor with the correct number of argument
		//will be created and added by the TC (see createsInternalConstructorWithBinding)
		alloc.sourceStart = this.intStack[this.intPtr--];
		pushOnExpressionStack(alloc);
	} else {
		dispatchDeclarationInto(length);
		TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
		anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
		anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
		if (anonymousTypeDeclaration.allocation != null) {
			anonymousTypeDeclaration.allocation.sourceEnd = this.endStatementPosition;
			checkForDiamond(anonymousTypeDeclaration.allocation.type);
		}
		if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
			anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
		}
		this.astPtr--;
		this.astLengthPtr--;
	}
}
protected void checkForDiamond(TypeReference allocType) {
	if (allocType instanceof ParameterizedSingleTypeReference) {
		ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference) allocType;
		if (type.typeArguments == TypeReference.NO_TYPE_ARGUMENTS) {
			if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
				problemReporter().diamondNotBelow17(allocType);
			}
			if (this.options.sourceLevel > ClassFileConstants.JDK1_4) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=351965
				type.bits |= ASTNode.IsDiamond;
			} // else don't even bother to recognize this as <>
		}
	} 
	else if (allocType instanceof ParameterizedQualifiedTypeReference) {
		ParameterizedQualifiedTypeReference type = (ParameterizedQualifiedTypeReference) allocType;
		if (type.typeArguments[type.typeArguments.length - 1] == TypeReference.NO_TYPE_ARGUMENTS) { // Don't care for X<>.Y<> and X<>.Y<String>
			if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
				problemReporter().diamondNotBelow17(allocType, type.typeArguments.length - 1);
			}
			if (this.options.sourceLevel > ClassFileConstants.JDK1_4) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=351965
				type.bits |= ASTNode.IsDiamond;
			} // else don't even bother to recognize this as <>
		}
	}
}
protected ParameterizedQualifiedTypeReference computeQualifiedGenericsFromRightSide(TypeReference rightSide, int dim) {
	int nameSize = this.identifierLengthStack[this.identifierLengthPtr];
	int tokensSize = nameSize;
	if (rightSide instanceof ParameterizedSingleTypeReference) {
		tokensSize ++;
	} else if (rightSide instanceof SingleTypeReference) {
		tokensSize ++;
	} else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
		tokensSize += ((QualifiedTypeReference) rightSide).tokens.length;
	} else if (rightSide instanceof QualifiedTypeReference) {
		tokensSize += ((QualifiedTypeReference) rightSide).tokens.length;
	}
	TypeReference[][] typeArguments = new TypeReference[tokensSize][];
	char[][] tokens = new char[tokensSize][];
	long[] positions = new long[tokensSize];
	if (rightSide instanceof ParameterizedSingleTypeReference) {
		ParameterizedSingleTypeReference singleParameterizedTypeReference = (ParameterizedSingleTypeReference) rightSide;
		tokens[nameSize] = singleParameterizedTypeReference.token;
		positions[nameSize] = (((long) singleParameterizedTypeReference.sourceStart) << 32) + singleParameterizedTypeReference.sourceEnd;
		typeArguments[nameSize] = singleParameterizedTypeReference.typeArguments;
	} else if (rightSide instanceof SingleTypeReference) {
		SingleTypeReference singleTypeReference = (SingleTypeReference) rightSide;
		tokens[nameSize] = singleTypeReference.token;
		positions[nameSize] = (((long) singleTypeReference.sourceStart) << 32) + singleTypeReference.sourceEnd;
	} else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
		ParameterizedQualifiedTypeReference parameterizedTypeReference = (ParameterizedQualifiedTypeReference) rightSide;
		TypeReference[][] rightSideTypeArguments = parameterizedTypeReference.typeArguments;
		System.arraycopy(rightSideTypeArguments, 0, typeArguments, nameSize, rightSideTypeArguments.length);
		char[][] rightSideTokens = parameterizedTypeReference.tokens;
		System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
		long[] rightSidePositions = parameterizedTypeReference.sourcePositions;
		System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
	} else if (rightSide instanceof QualifiedTypeReference) {
		QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference) rightSide;
		char[][] rightSideTokens = qualifiedTypeReference.tokens;
		System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
		long[] rightSidePositions = qualifiedTypeReference.sourcePositions;
		System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
	}

	int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
	TypeReference[] currentTypeArguments = new TypeReference[currentTypeArgumentsLength];
	this.genericsPtr -= currentTypeArgumentsLength;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, currentTypeArguments, 0, currentTypeArgumentsLength);

	if (nameSize == 1) {
		tokens[0] = this.identifierStack[this.identifierPtr];
		positions[0] = this.identifierPositionStack[this.identifierPtr--];
		typeArguments[0] = currentTypeArguments;
	} else {
		this.identifierPtr -= nameSize;
		System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, nameSize);
		System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, nameSize);
		typeArguments[nameSize - 1] = currentTypeArguments;
	}
	this.identifierLengthPtr--;
	return new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, positions);
}
protected void concatExpressionLists() {
	this.expressionLengthStack[--this.expressionLengthPtr]++;
}
protected void concatGenericsLists() {
	this.genericsLengthStack[this.genericsLengthPtr - 1] += this.genericsLengthStack[this.genericsLengthPtr--];
}
protected void concatNodeLists() {
	/*
	 * This is a case where you have two sublists into the this.astStack that you want
	 * to merge in one list. There is no action required on the this.astStack. The only
	 * thing you need to do is merge the two lengths specified on the astStackLength.
	 * The top two length are for example:
	 * ... p   n
	 * and you want to result in a list like:
	 * ... n+p
	 * This means that the p could be equals to 0 in case there is no astNode pushed
	 * on the this.astStack.
	 * Look at the InterfaceMemberDeclarations for an example.
	 */

	this.astLengthStack[this.astLengthPtr - 1] += this.astLengthStack[this.astLengthPtr--];
}
protected void consumeAdditionalBound() {
	pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
}
protected void consumeAdditionalBound1() {
	// nothing to be done.
	// The reference type1 is consumed by consumeReferenceType1 method.
}
protected void consumeAdditionalBoundList() {
	concatGenericsLists();
}
protected void consumeAdditionalBoundList1() {
	concatGenericsLists();
}
protected void consumeAllocationHeader() {
	// ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt

	// ClassBodyopt produces a null item on the astStak if it produces NO class body
	// An empty class body produces a 0 on the length stack.....

	if (this.currentElement == null){
		return; // should never occur, this consumeRule is only used in recovery mode
	}
	if (this.currentToken == TokenNameLBRACE){
		// beginning of an anonymous type
		TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
		anonymousType.name = CharOperation.NO_CHAR;
		anonymousType.bits |= (ASTNode.IsAnonymousType|ASTNode.IsLocalType);
		anonymousType.sourceStart = this.intStack[this.intPtr--];
		anonymousType.declarationSourceStart = anonymousType.sourceStart;
		anonymousType.sourceEnd = this.rParenPos; // closing parenthesis
		QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
		alloc.type = getTypeReference(0);
		alloc.sourceStart = anonymousType.sourceStart;
		alloc.sourceEnd = anonymousType.sourceEnd ;
		this.lastCheckPoint = anonymousType.bodyStart = this.scanner.currentPosition;
		this.currentElement = this.currentElement.add(anonymousType, 0);
		this.lastIgnoredToken = -1;
		this.currentToken = 0; // opening brace already taken into account
		return;
	}
	this.lastCheckPoint = this.scanner.startPosition; // force to restart at this exact position
	this.restartRecovery = true; // request to restart from here on
}
protected void consumeAnnotationAsModifier() {
	Expression expression = this.expressionStack[this.expressionPtr];
	int sourceStart = expression.sourceStart;
	if (this.modifiersSourceStart < 0) {
		this.modifiersSourceStart = sourceStart;
	}
}
protected void consumeAnnotationName() {
	if(this.currentElement != null) {
		int start = this.intStack[this.intPtr];
		int end = (int) (this.identifierPositionStack[this.identifierPtr] & 0x00000000FFFFFFFFL);
		annotationRecoveryCheckPoint(start, end);

		if (this.annotationRecoveryActivated) {
			this.currentElement = this.currentElement.addAnnotationName(this.identifierPtr, this.identifierLengthPtr, start, 0);
		}
	}
	this.recordStringLiterals = false;
}
protected void consumeAnnotationTypeDeclaration() {
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		//there are length declarations
		//dispatch according to the type of the declarations
		dispatchDeclarationInto(length);
	}

	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];

	//convert constructor that do not have the type's name into methods
	typeDecl.checkConstructors(this);

	//always add <clinit> (will be remove at code gen time if empty)
	if (this.scanner.containsAssertKeyword) {
		typeDecl.bits |= ASTNode.ContainsAssertion;
	}
	typeDecl.addClinit();
	typeDecl.bodyEnd = this.endStatementPosition;
	if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
		typeDecl.bits |= ASTNode.UndocumentedEmptyBlock;
	}
	typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeAnnotationTypeDeclarationHeader() {
	TypeDeclaration annotationTypeDeclaration = (TypeDeclaration) this.astStack[this.astPtr];
	if (this.currentToken == TokenNameLBRACE) {
		annotationTypeDeclaration.bodyStart = this.scanner.currentPosition;
	}
	if (this.currentElement != null) {
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
	// flush the comments related to the annotation type header
	this.scanner.commentPtr = -1;
}
protected void consumeAnnotationTypeDeclarationHeaderName() {
	// consumeAnnotationTypeDeclarationHeader ::= Modifiers '@' PushModifiers interface Identifier
	// consumeAnnotationTypeDeclarationHeader ::= '@' PushModifiers interface Identifier
	TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
	if (this.nestedMethod[this.nestedType] == 0) {
		if (this.nestedType != 0) {
			annotationTypeDeclaration.bits |= ASTNode.IsMemberType;
		}
	} else {
		// Record that the block has a declaration for local types
		annotationTypeDeclaration.bits |= ASTNode.IsLocalType;
		markEnclosingMemberWithLocalType();
		blockReal();
	}

	//highlight the name of the type
	long pos = this.identifierPositionStack[this.identifierPtr];
	annotationTypeDeclaration.sourceEnd = (int) pos;
	annotationTypeDeclaration.sourceStart = (int) (pos >>> 32);
	annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//compute the declaration source too
	// 'interface' push two int positions: the beginning of the class token and its end.
	// we want to keep the beginning position but get rid of the end position
	// it is only used for the ClassLiteralAccess positions.
	this.intPtr--; // remove the start position of the interface token
	this.intPtr--; // remove the end position of the interface token

	annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
	annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccAnnotation | ClassFileConstants.AccInterface;
	if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
		annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
		this.intPtr--; // remove the position of the '@' token as we have modifiers
	} else {
		int atPosition = this.intStack[this.intPtr--];
		// remove the position of the '@' token as we don't have modifiers
		annotationTypeDeclaration.declarationSourceStart = atPosition;
	}

	// Store secondary info
	if ((annotationTypeDeclaration.bits & ASTNode.IsMemberType) == 0 && (annotationTypeDeclaration.bits & ASTNode.IsLocalType) == 0) {
		if (this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
			annotationTypeDeclaration.bits |= ASTNode.IsSecondaryType;
		}
	}

	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			annotationTypeDeclaration.annotations = new Annotation[length],
			0,
			length);
	}
	annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.sourceEnd + 1;

	// javadoc
	annotationTypeDeclaration.javadoc = this.javadoc;
	this.javadoc = null;
	pushOnAstStack(annotationTypeDeclaration);
	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
		this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
		this.lastIgnoredToken = -1;
	}
}
protected void consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters() {
	// consumeAnnotationTypeDeclarationHeader ::= Modifiers '@' PushModifiers interface Identifier TypeParameters
	// consumeAnnotationTypeDeclarationHeader ::= '@' PushModifiers interface Identifier TypeParameters
	TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
	// consume type parameters
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, annotationTypeDeclaration.typeParameters = new TypeParameter[length], 0, length);

	problemReporter().invalidUsageOfTypeParametersForAnnotationDeclaration(annotationTypeDeclaration);

	annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.typeParameters[length-1].declarationSourceEnd + 1;

//	annotationTypeDeclaration.typeParameters = null;

	this.listTypeParameterLength = 0;

	if (this.nestedMethod[this.nestedType] == 0) {
		if (this.nestedType != 0) {
			annotationTypeDeclaration.bits |= ASTNode.IsMemberType;
		}
	} else {
		// Record that the block has a declaration for local types
		annotationTypeDeclaration.bits |= ASTNode.IsLocalType;
		markEnclosingMemberWithLocalType();
		blockReal();
	}

	//highlight the name of the type
	long pos = this.identifierPositionStack[this.identifierPtr];
	annotationTypeDeclaration.sourceEnd = (int) pos;
	annotationTypeDeclaration.sourceStart = (int) (pos >>> 32);
	annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//compute the declaration source too
	// 'interface' push two int positions: the beginning of the class token and its end.
	// we want to keep the beginning position but get rid of the end position
	// it is only used for the ClassLiteralAccess positions.
	this.intPtr--; // remove the start position of the interface token
	this.intPtr--; // remove the end position of the interface token

	annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
	annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccAnnotation | ClassFileConstants.AccInterface;
	if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
		annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
		this.intPtr--; // remove the position of the '@' token as we have modifiers
	} else {
		int atPosition = this.intStack[this.intPtr--];
		// remove the position of the '@' token as we don't have modifiers
		annotationTypeDeclaration.declarationSourceStart = atPosition;
	}

	// Store secondary info
	if ((annotationTypeDeclaration.bits & ASTNode.IsMemberType) == 0 && (annotationTypeDeclaration.bits & ASTNode.IsLocalType) == 0) {
		if (this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
			annotationTypeDeclaration.bits |= ASTNode.IsSecondaryType;
		}
	}

	// consume annotations
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			annotationTypeDeclaration.annotations = new Annotation[length],
			0,
			length);
	}
	// javadoc
	annotationTypeDeclaration.javadoc = this.javadoc;
	this.javadoc = null;
	pushOnAstStack(annotationTypeDeclaration);
	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
		this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
		this.lastIgnoredToken = -1;
	}
}
protected void consumeAnnotationTypeMemberDeclaration() {
	// AnnotationTypeMemberDeclaration ::= AnnotationTypeMemberDeclarationHeader AnnotationTypeMemberHeaderExtendedDims DefaultValueopt ';'
	AnnotationMethodDeclaration annotationTypeMemberDeclaration = (AnnotationMethodDeclaration) this.astStack[this.astPtr];
	annotationTypeMemberDeclaration.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
	// store the this.endPosition (position just before the '}') in case there is
	// a trailing comment behind the end of the method
	int declarationEndPosition = flushCommentsDefinedPriorTo(this.endStatementPosition);
	annotationTypeMemberDeclaration.bodyStart = this.endStatementPosition;
	annotationTypeMemberDeclaration.bodyEnd = declarationEndPosition;
	annotationTypeMemberDeclaration.declarationSourceEnd = declarationEndPosition;
}
protected void consumeAnnotationTypeMemberDeclarations() {
	// AnnotationTypeMemberDeclarations ::= AnnotationTypeMemberDeclarations AnnotationTypeMemberDeclaration
	concatNodeLists();
}
protected void consumeAnnotationTypeMemberDeclarationsopt() {
	this.nestedType-- ;
}
protected void consumeArgumentList() {
	// ArgumentList ::= ArgumentList ',' Expression
	concatExpressionLists();
}
protected void consumeArguments() {
	// Arguments ::= '(' ArgumentListopt ')'
	// nothing to do, the expression stack is already updated
	pushOnIntStack(this.rParenPos);
}
protected void consumeArrayAccess(boolean unspecifiedReference) {
	// ArrayAccess ::= Name '[' Expression ']' ==> true
	// ArrayAccess ::= PrimaryNoNewArray '[' Expression ']' ==> false


	//optimize push/pop
	Expression exp;
	if (unspecifiedReference) {
		exp =
			this.expressionStack[this.expressionPtr] =
				new ArrayReference(
					getUnspecifiedReferenceOptimized(),
					this.expressionStack[this.expressionPtr]);
	} else {
		this.expressionPtr--;
		this.expressionLengthPtr--;
		exp =
			this.expressionStack[this.expressionPtr] =
				new ArrayReference(
					this.expressionStack[this.expressionPtr],
					this.expressionStack[this.expressionPtr + 1]);
	}
	exp.sourceEnd = this.endStatementPosition;
}
protected void consumeArrayCreationExpressionWithInitializer() {
	// ArrayCreationWithArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs ArrayInitializer
	// ArrayCreationWithArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs ArrayInitializer

	int length;
	ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
	this.expressionLengthPtr -- ;
	arrayAllocation.initializer = (ArrayInitializer) this.expressionStack[this.expressionPtr--];

	arrayAllocation.type = getTypeReference(0);
	arrayAllocation.type.bits |= ASTNode.IgnoreRawTypeCheck; // no need to worry about raw type usage
	length = (this.expressionLengthStack[this.expressionLengthPtr--]);
	this.expressionPtr -= length ;
	System.arraycopy(
		this.expressionStack,
		this.expressionPtr+1,
		arrayAllocation.dimensions = new Expression[length],
		0,
		length);
	arrayAllocation.sourceStart = this.intStack[this.intPtr--];
	if (arrayAllocation.initializer == null) {
		arrayAllocation.sourceEnd = this.endStatementPosition;
	} else {
		arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd ;
	}
	pushOnExpressionStack(arrayAllocation);
}
protected void consumeArrayCreationExpressionWithoutInitializer() {
	// ArrayCreationWithoutArrayInitializer ::= 'new' ClassOrInterfaceType DimWithOrWithOutExprs
	// ArrayCreationWithoutArrayInitializer ::= 'new' PrimitiveType DimWithOrWithOutExprs

	int length;
	ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
	arrayAllocation.type = getTypeReference(0);
	arrayAllocation.type.bits |= ASTNode.IgnoreRawTypeCheck; // no need to worry about raw type usage
	length = (this.expressionLengthStack[this.expressionLengthPtr--]);
	this.expressionPtr -= length ;
	System.arraycopy(
		this.expressionStack,
		this.expressionPtr+1,
		arrayAllocation.dimensions = new Expression[length],
		0,
		length);
	arrayAllocation.sourceStart = this.intStack[this.intPtr--];
	if (arrayAllocation.initializer == null) {
		arrayAllocation.sourceEnd = this.endStatementPosition;
	} else {
		arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd ;
	}
	pushOnExpressionStack(arrayAllocation);
}
protected void consumeArrayCreationHeader() {
	// nothing to do
}
protected void consumeArrayInitializer() {
	// ArrayInitializer ::= '{' VariableInitializers '}'
	// ArrayInitializer ::= '{' VariableInitializers , '}'

	arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
}
protected void consumeArrayTypeWithTypeArgumentsName() {
	this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
	pushOnGenericsLengthStack(0); // handle type arguments
}
protected void consumeAssertStatement() {
	// AssertStatement ::= 'assert' Expression ':' Expression ';'
	this.expressionLengthPtr-=2;
	pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
}
protected void consumeAssignment() {
	// Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
	//optimize the push/pop

	int op = this.intStack[this.intPtr--] ; //<--the encoded operator

	this.expressionPtr -- ; this.expressionLengthPtr -- ;
	Expression expression = this.expressionStack[this.expressionPtr+1];
	this.expressionStack[this.expressionPtr] =
		(op != EQUAL ) ?
			new CompoundAssignment(
				this.expressionStack[this.expressionPtr] ,
				expression,
				op,
				expression.sourceEnd):
			new Assignment(
				this.expressionStack[this.expressionPtr] ,
				expression,
				expression.sourceEnd);

	if (this.pendingRecoveredType != null) {
		// Used only in statements recovery.
		// This is not a real assignment but a placeholder for an existing anonymous type.
		// The assignment must be replace by the anonymous type.
		if (this.pendingRecoveredType.allocation != null &&
				this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
			this.expressionStack[this.expressionPtr] = this.pendingRecoveredType.allocation;
			this.pendingRecoveredType = null;
			return;
		}
		this.pendingRecoveredType = null;
	}
}
protected void consumeAssignmentOperator(int pos) {
	// AssignmentOperator ::= '='
	// AssignmentOperator ::= '*='
	// AssignmentOperator ::= '/='
	// AssignmentOperator ::= '%='
	// AssignmentOperator ::= '+='
	// AssignmentOperator ::= '-='
	// AssignmentOperator ::= '<<='
	// AssignmentOperator ::= '>>='
	// AssignmentOperator ::= '>>>='
	// AssignmentOperator ::= '&='
	// AssignmentOperator ::= '^='
	// AssignmentOperator ::= '|='

	pushOnIntStack(pos);
}
protected void consumeBinaryExpression(int op) {
	// MultiplicativeExpression ::= MultiplicativeExpression '*' UnaryExpression
	// MultiplicativeExpression ::= MultiplicativeExpression '/' UnaryExpression
	// MultiplicativeExpression ::= MultiplicativeExpression '%' UnaryExpression
	// AdditiveExpression ::= AdditiveExpression '+' MultiplicativeExpression
	// AdditiveExpression ::= AdditiveExpression '-' MultiplicativeExpression
	// ShiftExpression ::= ShiftExpression '<<'  AdditiveExpression
	// ShiftExpression ::= ShiftExpression '>>'  AdditiveExpression
	// ShiftExpression ::= ShiftExpression '>>>' AdditiveExpression
	// RelationalExpression ::= RelationalExpression '<'  ShiftExpression
	// RelationalExpression ::= RelationalExpression '>'  ShiftExpression
	// RelationalExpression ::= RelationalExpression '<=' ShiftExpression
	// RelationalExpression ::= RelationalExpression '>=' ShiftExpression
	// AndExpression ::= AndExpression '&' EqualityExpression
	// ExclusiveOrExpression ::= ExclusiveOrExpression '^' AndExpression
	// InclusiveOrExpression ::= InclusiveOrExpression '|' ExclusiveOrExpression
	// ConditionalAndExpression ::= ConditionalAndExpression '&&' InclusiveOrExpression
	// ConditionalOrExpression ::= ConditionalOrExpression '||' ConditionalAndExpression

	//optimize the push/pop

	this.expressionPtr--;
	this.expressionLengthPtr--;
	Expression expr1 = this.expressionStack[this.expressionPtr];
	Expression expr2 = this.expressionStack[this.expressionPtr + 1];
	switch(op) {
		case OR_OR :
			this.expressionStack[this.expressionPtr] =
				new OR_OR_Expression(
					expr1,
					expr2,
					op);
			break;
		case AND_AND :
			this.expressionStack[this.expressionPtr] =
				new AND_AND_Expression(
					expr1,
					expr2,
					op);
			break;
		case PLUS :
			// look for "string1" + "string2"
			if (this.optimizeStringLiterals) {
				if (expr1 instanceof StringLiteral) {
					if (((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
						if (expr2 instanceof CharLiteral) { // string+char
							this.expressionStack[this.expressionPtr] =
								((StringLiteral) expr1).extendWith((CharLiteral) expr2);
						} else if (expr2 instanceof StringLiteral) { //string+string
							this.expressionStack[this.expressionPtr] =
								((StringLiteral) expr1).extendWith((StringLiteral) expr2);
						} else {
							this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
						}
					} else {
						this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
					}
				} else if (expr1 instanceof CombinedBinaryExpression) {
					CombinedBinaryExpression cursor;
					// left branch is comprised of PLUS BEs
					// cursor is shifted upwards, while needed BEs are added
					// on demand; past the arityMax-th
					// consecutive BE, a CBE is inserted that holds a
					// full-fledged references table
					if ((cursor = (CombinedBinaryExpression)expr1).arity < cursor.arityMax) {
						cursor.left = new BinaryExpression(cursor);
						cursor.arity++;
					} else {
						cursor.left = new CombinedBinaryExpression(cursor);
						cursor.arity = 0;
						cursor.tuneArityMax();
					}
					cursor.right = expr2;
					cursor.sourceEnd = expr2.sourceEnd;
					this.expressionStack[this.expressionPtr] = cursor;
					// BE_INSTRUMENTATION: neutralized in the released code
//					cursor.depthTracker = ((BinaryExpression)cursor.left).
//						depthTracker + 1;
				} else if (expr1 instanceof BinaryExpression &&
							// single out the a + b case, which is a BE
							// instead of a CBE (slightly more than a half of
							// strings concatenation are one-deep binary
							// expressions)
						((expr1.bits & ASTNode.OperatorMASK) >>
							ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
					this.expressionStack[this.expressionPtr] =
						new CombinedBinaryExpression(expr1, expr2, PLUS, 1);
				} else {
					this.expressionStack[this.expressionPtr] =
						new BinaryExpression(expr1, expr2, PLUS);
				}
			} else if (expr1 instanceof StringLiteral) {
				if (expr2 instanceof StringLiteral
						&& ((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
					// string + string
					this.expressionStack[this.expressionPtr] =
						((StringLiteral) expr1).extendsWith((StringLiteral) expr2);
				} else {
					// single out the a + b case
					this.expressionStack[this.expressionPtr] =
						new BinaryExpression(expr1, expr2, PLUS);
				}
			} else if (expr1 instanceof CombinedBinaryExpression) {
					CombinedBinaryExpression cursor;
					// shift cursor; create BE/CBE as needed
					if ((cursor = (CombinedBinaryExpression)expr1).arity < cursor.arityMax) {
						cursor.left = new BinaryExpression(cursor);
						// clear the bits on cursor
						cursor.bits &= ~ASTNode.ParenthesizedMASK;
						cursor.arity++;
					} else {
						cursor.left = new CombinedBinaryExpression(cursor);
						// clear the bits on cursor
						cursor.bits &= ~ASTNode.ParenthesizedMASK;
						cursor.arity = 0;
						cursor.tuneArityMax();
					}
					cursor.right = expr2;
					cursor.sourceEnd = expr2.sourceEnd;
					// BE_INSTRUMENTATION: neutralized in the released code
//					cursor.depthTracker = ((BinaryExpression)cursor.left).
//						depthTracker + 1;
					this.expressionStack[this.expressionPtr] = cursor;
			} else if (expr1 instanceof BinaryExpression
					&& ((expr1.bits & ASTNode.OperatorMASK) >>
							ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
				// single out the a + b case
				this.expressionStack[this.expressionPtr] =
					new CombinedBinaryExpression(expr1, expr2, PLUS, 1);
			} else {
				this.expressionStack[this.expressionPtr] =
					new BinaryExpression(expr1, expr2, PLUS);
			}
			break;
		case LESS :
		case MULTIPLY :
			this.intPtr--; // star end position or starting position of angle bracket
			this.expressionStack[this.expressionPtr] =
				new BinaryExpression(
					expr1,
					expr2,
					op);
			break;
		default :
			this.expressionStack[this.expressionPtr] =
				new BinaryExpression(
					expr1,
					expr2,
					op);
	}
}
/**
 * @param op binary operator
 */
protected void consumeBinaryExpressionWithName(int op) {
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
	this.expressionPtr--;
	this.expressionLengthPtr--;
	/*
	if (op == OR_OR) {
		this.expressionStack[this.expressionPtr] =
			new OR_OR_Expression(
				this.expressionStack[this.expressionPtr + 1],
				this.expressionStack[this.expressionPtr],
				op);
	} else {
		if (op == AND_AND) {
			this.expressionStack[this.expressionPtr] =
				new AND_AND_Expression(
					this.expressionStack[this.expressionPtr + 1],
					this.expressionStack[this.expressionPtr],
					op);
		} else {
			// look for "string1" + "string2"
			if ((op == PLUS) && this.optimizeStringLiterals) {
				Expression expr1, expr2;
				expr1 = this.expressionStack[this.expressionPtr + 1];
				expr2 = this.expressionStack[this.expressionPtr];
				if (expr1 instanceof StringLiteral) {
					if (expr2 instanceof CharLiteral) { // string+char
						this.expressionStack[this.expressionPtr] =
							((StringLiteral) expr1).extendWith((CharLiteral) expr2);
					} else if (expr2 instanceof StringLiteral) { //string+string
						this.expressionStack[this.expressionPtr] =
							((StringLiteral) expr1).extendWith((StringLiteral) expr2);
					} else {
						this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
					}
				} else {
					this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
				}
			} else {
				this.expressionStack[this.expressionPtr] =
					new BinaryExpression(
						this.expressionStack[this.expressionPtr + 1],
						this.expressionStack[this.expressionPtr],
						op);
			}
		}
	}
	*/
	Expression expr1 = this.expressionStack[this.expressionPtr + 1];
	Expression expr2 = this.expressionStack[this.expressionPtr];
	// Note: we do not attempt to promote BinaryExpression-s to
	//       IndexedBinaryExpression-s here since expr1 always holds a name
	switch(op) {
		case OR_OR :
			this.expressionStack[this.expressionPtr] =
				new OR_OR_Expression(
					expr1,
					expr2,
					op);
			break;
		case AND_AND :
			this.expressionStack[this.expressionPtr] =
				new AND_AND_Expression(
					expr1,
					expr2,
					op);
			break;
		case PLUS :
			// look for "string1" + "string2"
			if (this.optimizeStringLiterals) {
				if (expr1 instanceof StringLiteral
						&& ((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
					if (expr2 instanceof CharLiteral) { // string+char
						this.expressionStack[this.expressionPtr] =
							((StringLiteral) expr1).extendWith((CharLiteral) expr2);
					} else if (expr2 instanceof StringLiteral) { //string+string
						this.expressionStack[this.expressionPtr] =
							((StringLiteral) expr1).extendWith((StringLiteral) expr2);
					} else {
						this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
					}
				} else {
					this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, PLUS);
				}
			} else if (expr1 instanceof StringLiteral) {
				if (expr2 instanceof StringLiteral
						&& ((expr1.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) == 0) {
					// string + string
					this.expressionStack[this.expressionPtr] =
						((StringLiteral) expr1).extendsWith((StringLiteral) expr2);
				} else {
					this.expressionStack[this.expressionPtr] =
						new BinaryExpression(
							expr1,
							expr2,
							op);
				}
			} else {
				this.expressionStack[this.expressionPtr] =
					new BinaryExpression(
						expr1,
						expr2,
						op);
			}
			break;
		case LESS :
		case MULTIPLY :
			this.intPtr--; // star end position or starting position of angle bracket
			this.expressionStack[this.expressionPtr] =
				new BinaryExpression(
					expr1,
					expr2,
					op);
			break;
		default :
			this.expressionStack[this.expressionPtr] =
				new BinaryExpression(
					expr1,
					expr2,
					op);
	}
}
protected void consumeBlock() {
	// Block ::= OpenBlock '{' BlockStatementsopt '}'
	// simpler action for empty blocks

	int statementsLength = this.astLengthStack[this.astLengthPtr--];
	Block block;
	if (statementsLength == 0) { // empty block
		block = new Block(0);
		block.sourceStart = this.intStack[this.intPtr--];
		block.sourceEnd = this.endStatementPosition;
		// check whether this block at least contains some comment in it
		if (!containsComment(block.sourceStart, block.sourceEnd)) {
			block.bits |= ASTNode.UndocumentedEmptyBlock;
		}
		this.realBlockPtr--; // still need to pop the block variable counter
	} else {
		block = new Block(this.realBlockStack[this.realBlockPtr--]);
		this.astPtr -= statementsLength;
		System.arraycopy(
			this.astStack,
			this.astPtr + 1,
			block.statements = new Statement[statementsLength],
			0,
			statementsLength);
		block.sourceStart = this.intStack[this.intPtr--];
		block.sourceEnd = this.endStatementPosition;
	}
	pushOnAstStack(block);
}
protected void consumeBlockStatements() {
	// BlockStatements ::= BlockStatements BlockStatement
	concatNodeLists();
}
protected void consumeCaseLabel() {
	// SwitchLabel ::= 'case' ConstantExpression ':'
	this.expressionLengthPtr--;
	Expression expression = this.expressionStack[this.expressionPtr--];
	CaseStatement caseStatement = new CaseStatement(expression, expression.sourceEnd, this.intStack[this.intPtr--]);
	// Look for $fall-through$ tag in leading comment for case statement
	if (hasLeadingTagComment(FALL_THROUGH_TAG, caseStatement.sourceStart)) {
		caseStatement.bits |= ASTNode.DocumentedFallthrough;
	}
	pushOnAstStack(caseStatement);
}
protected void consumeCastExpressionLL1() {
	//CastExpression ::= '(' Expression ')' InsideCastExpressionLL1 UnaryExpressionNotPlusMinus
	// Expression is used in order to make the grammar LL1

	//optimize push/pop

	Expression cast;
	Expression exp;
	this.expressionPtr--;
	this.expressionStack[this.expressionPtr] =
		cast = new CastExpression(
			exp=this.expressionStack[this.expressionPtr+1] ,
			(TypeReference) this.expressionStack[this.expressionPtr]);
	this.expressionLengthPtr -- ;
	updateSourcePosition(cast);
	cast.sourceEnd=exp.sourceEnd;
}
protected void consumeCastExpressionWithGenericsArray() {
	// CastExpression ::= PushLPAREN Name TypeArguments Dims PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus

	Expression exp;
	Expression cast;
	TypeReference castType;
	int end = this.intStack[this.intPtr--];

	int dim = this.intStack[this.intPtr--];
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);

	this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(dim));
	this.intPtr--;
	castType.sourceEnd = end - 1;
	castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
	cast.sourceEnd = exp.sourceEnd;
}
protected void consumeCastExpressionWithNameArray() {
	// CastExpression ::= PushLPAREN Name Dims PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus

	Expression exp;
	Expression cast;
	TypeReference castType;
	int end = this.intStack[this.intPtr--];

	// handle type arguments
	pushOnGenericsLengthStack(0);
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);

	this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(this.intStack[this.intPtr--]));
	castType.sourceEnd = end - 1;
	castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
	cast.sourceEnd = exp.sourceEnd;
}
protected void consumeCastExpressionWithPrimitiveType() {
	// CastExpression ::= PushLPAREN PrimitiveType Dimsopt PushRPAREN InsideCastExpression UnaryExpression

	//this.intStack : posOfLeftParen dim posOfRightParen

	//optimize the push/pop

	Expression exp;
	Expression cast;
	TypeReference castType;
	int end = this.intStack[this.intPtr--];
	this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(this.intStack[this.intPtr--]));
	castType.sourceEnd = end - 1;
	castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
	cast.sourceEnd = exp.sourceEnd;
}
protected void consumeCastExpressionWithQualifiedGenericsArray() {
	// CastExpression ::= PushLPAREN Name OnlyTypeArguments '.' ClassOrInterfaceType Dims PushRPAREN InsideCastExpression UnaryExpressionNotPlusMinus
	Expression exp;
	Expression cast;
	TypeReference castType;
	int end = this.intStack[this.intPtr--];

	int dim = this.intStack[this.intPtr--];
	TypeReference rightSide = getTypeReference(0);

	ParameterizedQualifiedTypeReference qualifiedParameterizedTypeReference = computeQualifiedGenericsFromRightSide(rightSide, dim);
	this.intPtr--;
	this.expressionStack[this.expressionPtr] = cast = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = qualifiedParameterizedTypeReference);
	castType.sourceEnd = end - 1;
	castType.sourceStart = (cast.sourceStart = this.intStack[this.intPtr--]) + 1;
	cast.sourceEnd = exp.sourceEnd;
}
protected void consumeCatches() {
	// Catches ::= Catches CatchClause
	optimizedConcatNodeLists();
}
protected void consumeCatchFormalParameter() {
	// CatchFormalParameter ::= Modifiersopt CatchType VariableDeclaratorId
	this.identifierLengthPtr--;
	char[] identifierName = this.identifierStack[this.identifierPtr];
	long namePositions = this.identifierPositionStack[this.identifierPtr--];
	int extendedDimensions = this.intStack[this.intPtr--]; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=348369
	TypeReference type = (TypeReference) this.astStack[this.astPtr--];
	if (extendedDimensions > 0) {
		type = type.copyDims(type.dimensions() + extendedDimensions);
		type.sourceEnd = this.endPosition;
	}
	this.astLengthPtr--;
	int modifierPositions = this.intStack[this.intPtr--];
	this.intPtr--;
	Argument arg =
		new Argument(
			identifierName,
			namePositions,
			type,
			this.intStack[this.intPtr + 1] & ~ClassFileConstants.AccDeprecated); // modifiers
	arg.bits &= ~ASTNode.IsArgument;
	arg.declarationSourceStart = modifierPositions;
	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			arg.annotations = new Annotation[length],
			0,
			length);
	}
	pushOnAstStack(arg);
	/* if incomplete method header, this.listLength counter will not have been reset,
		indicating that some arguments are available on the stack */
	this.listLength++;
}
protected void consumeCatchHeader() {
	// CatchDeclaration ::= 'catch' '(' FormalParameter ')' '{'

	if (this.currentElement == null){
		return; // should never occur, this consumeRule is only used in recovery mode
	}
	// current element should be a block due to the presence of the opening brace
	if (!(this.currentElement instanceof RecoveredBlock)){
		if(!(this.currentElement instanceof RecoveredMethod)) {
			return;
		}
		RecoveredMethod rMethod = (RecoveredMethod) this.currentElement;
		if(!(rMethod.methodBody == null && rMethod.bracketBalance > 0)) {
			return;
		}
	}

	Argument arg = (Argument)this.astStack[this.astPtr--];
	// convert argument to local variable
	LocalDeclaration localDeclaration = new LocalDeclaration(arg.name, arg.sourceStart, arg.sourceEnd);
	localDeclaration.type = arg.type;
	localDeclaration.declarationSourceStart = arg.declarationSourceStart;
	localDeclaration.declarationSourceEnd = arg.declarationSourceEnd;

	this.currentElement = this.currentElement.add(localDeclaration, 0);
	this.lastCheckPoint = this.scanner.startPosition; // force to restart at this exact position
	this.restartRecovery = true; // request to restart from here on
	this.lastIgnoredToken = -1;
}
protected void consumeCatchType() {
	// CatchType ::= UnionType
	int length = this.astLengthStack[this.astLengthPtr--];
	if (length != 1) {
		TypeReference[] typeReferences;
		System.arraycopy(
				this.astStack,
				(this.astPtr -= length) + 1,
				(typeReferences = new TypeReference[length]),
				0,
				length);
		UnionTypeReference typeReference = new UnionTypeReference(typeReferences);
		pushOnAstStack(typeReference);
		if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
			problemReporter().multiCatchNotBelow17(typeReference);
		}
	} else {
		// push back the type reference
		pushOnAstLengthStack(1);
	}
}
protected void consumeClassBodyDeclaration() {
	// ClassBodyDeclaration ::= Diet NestedMethod CreateInitializer Block
	//push an Initializer
	//optimize the push/pop
	this.nestedMethod[this.nestedType]--;
	Block block = (Block) this.astStack[this.astPtr--];
	this.astLengthPtr--;
	if (this.diet) block.bits &= ~ASTNode.UndocumentedEmptyBlock; // clear bit since was diet
	Initializer initializer = (Initializer) this.astStack[this.astPtr];
	initializer.declarationSourceStart = initializer.sourceStart = block.sourceStart;
	initializer.block = block;
	this.intPtr--; // pop sourcestart left on the stack by consumeNestedMethod.
	initializer.bodyStart = this.intStack[this.intPtr--];
	this.realBlockPtr--; // pop the block variable counter left on the stack by consumeNestedMethod
	int javadocCommentStart = this.intStack[this.intPtr--];
	if (javadocCommentStart != -1) {
		initializer.declarationSourceStart = javadocCommentStart;
		initializer.javadoc = this.javadoc;
		this.javadoc = null;
	}
	initializer.bodyEnd = this.endPosition;
	initializer.sourceEnd = this.endStatementPosition;
	initializer.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeClassBodyDeclarations() {
	// ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
	concatNodeLists();
}
protected void consumeClassBodyDeclarationsopt() {
	// ClassBodyDeclarationsopt ::= NestedType ClassBodyDeclarations
	this.nestedType-- ;
}
protected void consumeClassBodyopt() {
	// ClassBodyopt ::= $empty
	pushOnAstStack(null);
	this.endPosition = this.rParenPos;
}
protected void consumeClassDeclaration() {
	// ClassDeclaration ::= ClassHeader ClassBody

	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		//there are length declarations
		//dispatch according to the type of the declarations
		dispatchDeclarationInto(length);
	}

	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];

	//convert constructor that do not have the type's name into methods
	boolean hasConstructor = typeDecl.checkConstructors(this);

	//add the default constructor when needed (interface don't have it)
	if (!hasConstructor) {
		switch(TypeDeclaration.kind(typeDecl.modifiers)) {
			case TypeDeclaration.CLASS_DECL :
			case TypeDeclaration.ENUM_DECL :
				boolean insideFieldInitializer = false;
				if (this.diet) {
					for (int i = this.nestedType; i > 0; i--){
						if (this.variablesCounter[i] > 0) {
							insideFieldInitializer = true;
							break;
						}
					}
				}
				typeDecl.createDefaultConstructor(!this.diet || insideFieldInitializer, true);
		}
	}
	//always add <clinit> (will be remove at code gen time if empty)
	if (this.scanner.containsAssertKeyword) {
		typeDecl.bits |= ASTNode.ContainsAssertion;
	}
	typeDecl.addClinit();
	typeDecl.bodyEnd = this.endStatementPosition;
	if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
		typeDecl.bits |= ASTNode.UndocumentedEmptyBlock;
	}

	typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeClassHeader() {
	// ClassHeader ::= ClassHeaderName ClassHeaderExtendsopt ClassHeaderImplementsopt

	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if (this.currentToken == TokenNameLBRACE) {
		typeDecl.bodyStart = this.scanner.currentPosition;
	}
	if (this.currentElement != null) {
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
	// flush the comments related to the class header
	this.scanner.commentPtr = -1;
}
protected void consumeClassHeaderExtends() {
	// ClassHeaderExtends ::= 'extends' ClassType
	//superclass
	TypeReference superClass = getTypeReference(0);
	// There is a class declaration on the top of stack
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	typeDecl.superclass = superClass;
	superClass.bits |= ASTNode.IsSuperType;
	typeDecl.bodyStart = typeDecl.superclass.sourceEnd + 1;
	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = typeDecl.bodyStart;
	}
}
protected void consumeClassHeaderImplements() {
	// ClassHeaderImplements ::= 'implements' InterfaceTypeList
	int length = this.astLengthStack[this.astLengthPtr--];
	//super interfaces
	this.astPtr -= length;
	// There is a class declaration on the top of stack
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	System.arraycopy(
		this.astStack,
		this.astPtr + 1,
		typeDecl.superInterfaces = new TypeReference[length],
		0,
		length);
	for (int i = 0, max = typeDecl.superInterfaces.length; i < max; i++) {
		typeDecl.superInterfaces[i].bits |= ASTNode.IsSuperType;
	}
	typeDecl.bodyStart = typeDecl.superInterfaces[length-1].sourceEnd + 1;
	this.listLength = 0; // reset after having read super-interfaces
	// recovery
	if (this.currentElement != null) { // is recovering
		this.lastCheckPoint = typeDecl.bodyStart;
	}
}
protected void consumeClassHeaderName1() {
	// ClassHeaderName1 ::= Modifiersopt 'class' 'Identifier'
	TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
	if (this.nestedMethod[this.nestedType] == 0) {
		if (this.nestedType != 0) {
			typeDecl.bits |= ASTNode.IsMemberType;
		}
	} else {
		// Record that the block has a declaration for local types
		typeDecl.bits |= ASTNode.IsLocalType;
		markEnclosingMemberWithLocalType();
		blockReal();
	}

	//highlight the name of the type
	long pos = this.identifierPositionStack[this.identifierPtr];
	typeDecl.sourceEnd = (int) pos;
	typeDecl.sourceStart = (int) (pos >>> 32);
	typeDecl.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//compute the declaration source too
	// 'class' and 'interface' push two int positions: the beginning of the class token and its end.
	// we want to keep the beginning position but get rid of the end position
	// it is only used for the ClassLiteralAccess positions.
	typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
	this.intPtr--; // remove the end position of the class token

	typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
	typeDecl.modifiers = this.intStack[this.intPtr--];
	if (typeDecl.modifiersSourceStart >= 0) {
		typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
	}

	// Store secondary info
	if ((typeDecl.bits & ASTNode.IsMemberType) == 0 && (typeDecl.bits & ASTNode.IsLocalType) == 0) {
		if (this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
			typeDecl.bits |= ASTNode.IsSecondaryType;
		}
	}

	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			typeDecl.annotations = new Annotation[length],
			0,
			length);
	}
	typeDecl.bodyStart = typeDecl.sourceEnd + 1;
	pushOnAstStack(typeDecl);

	this.listLength = 0; // will be updated when reading super-interfaces
	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = typeDecl.bodyStart;
		this.currentElement = this.currentElement.add(typeDecl, 0);
		this.lastIgnoredToken = -1;
	}
	// javadoc
	typeDecl.javadoc = this.javadoc;
	this.javadoc = null;
}
protected void consumeClassInstanceCreationExpression() {
	// ClassInstanceCreationExpression ::= 'new' ClassType '(' ArgumentListopt ')' ClassBodyopt
	classInstanceCreation(false);
}
protected void consumeClassInstanceCreationExpressionName() {
	// ClassInstanceCreationExpressionName ::= Name '.'
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
}
protected void consumeClassInstanceCreationExpressionQualified() {
	// ClassInstanceCreationExpression ::= Primary '.' 'new' SimpleName '(' ArgumentListopt ')' ClassBodyopt
	// ClassInstanceCreationExpression ::= ClassInstanceCreationExpressionName 'new' SimpleName '(' ArgumentListopt ')' ClassBodyopt
	classInstanceCreation(true);

	QualifiedAllocationExpression qae =
		(QualifiedAllocationExpression) this.expressionStack[this.expressionPtr];
	
	if (qae.anonymousType == null) {
		this.expressionLengthPtr--;
		this.expressionPtr--;
		qae.enclosingInstance = this.expressionStack[this.expressionPtr];
		this.expressionStack[this.expressionPtr] = qae;
	}
	qae.sourceStart = qae.enclosingInstance.sourceStart;
}
protected void consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() {
	// ClassInstanceCreationExpression ::= Primary '.' 'new' TypeArguments SimpleName '(' ArgumentListopt ')' ClassBodyopt
	// ClassInstanceCreationExpression ::= ClassInstanceCreationExpressionName 'new' TypeArguments SimpleName '(' ArgumentListopt ')' ClassBodyopt

	QualifiedAllocationExpression alloc;
	int length;
	if (((length = this.astLengthStack[this.astLengthPtr--]) == 1) && (this.astStack[this.astPtr] == null)) {
		//NO ClassBody
		this.astPtr--;
		alloc = new QualifiedAllocationExpression();
		alloc.sourceEnd = this.endPosition; //the position has been stored explicitly

		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				alloc.arguments = new Expression[length],
				0,
				length);
		}
		alloc.type = getTypeReference(0);
		checkForDiamond(alloc.type);
		length = this.genericsLengthStack[this.genericsLengthPtr--];
		this.genericsPtr -= length;
		System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
		this.intPtr--;

		//the default constructor with the correct number of argument
		//will be created and added by the TC (see createsInternalConstructorWithBinding)
		alloc.sourceStart = this.intStack[this.intPtr--];
		pushOnExpressionStack(alloc);
	} else {
		dispatchDeclarationInto(length);
		TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
		anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
		anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
		if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
			anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
		}
		this.astPtr--;
		this.astLengthPtr--;

		QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
		if (allocationExpression != null) {
			allocationExpression.sourceEnd = this.endStatementPosition;
			// handle type arguments
			length = this.genericsLengthStack[this.genericsLengthPtr--];
			this.genericsPtr -= length;
			System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
			allocationExpression.sourceStart = this.intStack[this.intPtr--];
			checkForDiamond(allocationExpression.type);
		}
	}
	
	QualifiedAllocationExpression qae =
		(QualifiedAllocationExpression) this.expressionStack[this.expressionPtr];
	
	if (qae.anonymousType == null) {
		this.expressionLengthPtr--;
		this.expressionPtr--;
		qae.enclosingInstance = this.expressionStack[this.expressionPtr];
		this.expressionStack[this.expressionPtr] = qae;
	}
	qae.sourceStart = qae.enclosingInstance.sourceStart;
}
protected void consumeClassInstanceCreationExpressionWithTypeArguments() {
	// ClassInstanceCreationExpression ::= 'new' TypeArguments ClassType '(' ArgumentListopt ')' ClassBodyopt
	AllocationExpression alloc;
	int length;
	if (((length = this.astLengthStack[this.astLengthPtr--]) == 1)
		&& (this.astStack[this.astPtr] == null)) {
		//NO ClassBody
		this.astPtr--;
		alloc = new AllocationExpression();
		alloc.sourceEnd = this.endPosition; //the position has been stored explicitly

		if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				alloc.arguments = new Expression[length],
				0,
				length);
		}
		alloc.type = getTypeReference(0);
		checkForDiamond(alloc.type);

		length = this.genericsLengthStack[this.genericsLengthPtr--];
		this.genericsPtr -= length;
		System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
		this.intPtr--;

		//the default constructor with the correct number of argument
		//will be created and added by the TC (see createsInternalConstructorWithBinding)
		alloc.sourceStart = this.intStack[this.intPtr--];
		pushOnExpressionStack(alloc);
	} else {
		dispatchDeclarationInto(length);
		TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
		anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
		anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
		if (length == 0 && !containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
			anonymousTypeDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
		}
		this.astPtr--;
		this.astLengthPtr--;

		QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
		if (allocationExpression != null) {
			allocationExpression.sourceEnd = this.endStatementPosition;
			// handle type arguments
			length = this.genericsLengthStack[this.genericsLengthPtr--];
			this.genericsPtr -= length;
			System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
			allocationExpression.sourceStart = this.intStack[this.intPtr--];
			checkForDiamond(allocationExpression.type);
		}
	}
}
protected void consumeClassOrInterface() {
	this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
	pushOnGenericsLengthStack(0); // handle type arguments
}
protected void consumeClassOrInterfaceName() {
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
	pushOnGenericsLengthStack(0); // handle type arguments
}
protected void consumeClassTypeElt() {
	// ClassTypeElt ::= ClassType
	pushOnAstStack(getTypeReference(0));
	/* if incomplete thrown exception list, this.listLength counter will not have been reset,
		indicating that some items are available on the stack */
	this.listLength++;
}
protected void consumeClassTypeList() {
	// ClassTypeList ::= ClassTypeList ',' ClassTypeElt
	optimizedConcatNodeLists();
}
protected void consumeCompilationUnit() {
	// CompilationUnit ::= EnterCompilationUnit InternalCompilationUnit
	// do nothing by default
}
protected void consumeConditionalExpression(int op) {
	// ConditionalExpression ::= ConditionalOrExpression '?' Expression ':' ConditionalExpression
	//optimize the push/pop
	this.intPtr -= 2;//consume position of the question mark
	this.expressionPtr -= 2;
	this.expressionLengthPtr -= 2;
	this.expressionStack[this.expressionPtr] =
		new ConditionalExpression(
			this.expressionStack[this.expressionPtr],
			this.expressionStack[this.expressionPtr + 1],
			this.expressionStack[this.expressionPtr + 2]);
}
/**
 * @param op
 */
protected void consumeConditionalExpressionWithName(int op) {
	// ConditionalExpression ::= Name '?' Expression ':' ConditionalExpression
	this.intPtr -= 2;//consume position of the question mark
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
	this.expressionPtr -= 2;
	this.expressionLengthPtr -= 2;
	this.expressionStack[this.expressionPtr] =
		new ConditionalExpression(
			this.expressionStack[this.expressionPtr + 2],
			this.expressionStack[this.expressionPtr],
			this.expressionStack[this.expressionPtr + 1]);
}
protected void consumeConstructorBlockStatements() {
	// ConstructorBody ::= NestedMethod '{' ExplicitConstructorInvocation BlockStatements '}'
	concatNodeLists(); // explictly add the first statement into the list of statements
}
protected void consumeConstructorBody() {
	// ConstructorBody ::= NestedMethod  '{' BlockStatementsopt '}'
	// ConstructorBody ::= NestedMethod  '{' ExplicitConstructorInvocation '}'
	this.nestedMethod[this.nestedType] --;
}
protected void consumeConstructorDeclaration() {
	// ConstructorDeclaration ::= ConstructorHeader ConstructorBody

	/*
	this.astStack : MethodDeclaration statements
	this.identifierStack : name
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	*/

	//must provide a default constructor call when needed

	int length;

	// pop the position of the {  (body of the method) pushed in block decl
	this.intPtr--;
	this.intPtr--;

	//statements
	this.realBlockPtr--;
	ExplicitConstructorCall constructorCall = null;
	Statement[] statements = null;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		this.astPtr -= length;
		if (!this.options.ignoreMethodBodies) {
			if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
				//avoid a isSomeThing that would only be used here BUT what is faster between two alternatives ?
				System.arraycopy(
					this.astStack,
					this.astPtr + 2,
					statements = new Statement[length - 1],
					0,
					length - 1);
				constructorCall = (ExplicitConstructorCall) this.astStack[this.astPtr + 1];
			} else { //need to add explicitly the super();
				System.arraycopy(
					this.astStack,
					this.astPtr + 1,
					statements = new Statement[length],
					0,
					length);
				constructorCall = SuperReference.implicitSuperConstructorCall();
			}
		}
	} else {
		boolean insideFieldInitializer = false;
		if (this.diet) {
			for (int i = this.nestedType; i > 0; i--){
				if (this.variablesCounter[i] > 0) {
					insideFieldInitializer = true;
					break;
				}
			}
		}

		if (!this.diet || insideFieldInitializer){
			// add it only in non-diet mode, if diet_bodies, then constructor call will be added elsewhere.
			constructorCall = SuperReference.implicitSuperConstructorCall();
		}
	}

	// now we know that the top of stack is a constructorDeclaration
	ConstructorDeclaration cd = (ConstructorDeclaration) this.astStack[this.astPtr];
	cd.constructorCall = constructorCall;
	cd.statements = statements;

	//highlight of the implicit call on the method name
	if (constructorCall != null && cd.constructorCall.sourceEnd == 0) {
		cd.constructorCall.sourceEnd = cd.sourceEnd;
		cd.constructorCall.sourceStart = cd.sourceStart;
	}

	if (!(this.diet && this.dietInt == 0)
			&& statements == null
			&& (constructorCall == null || constructorCall.isImplicitSuper())
			&& !containsComment(cd.bodyStart, this.endPosition)) {
		cd.bits |= ASTNode.UndocumentedEmptyBlock;
	}

	//watch for } that could be given as a unicode ! ( u007D is '}' )
	// store the this.endPosition (position just before the '}') in case there is
	// a trailing comment behind the end of the method
	cd.bodyEnd = this.endPosition;
	cd.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeConstructorHeader() {
	// ConstructorHeader ::= ConstructorHeaderName MethodHeaderParameters MethodHeaderThrowsClauseopt

	AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];

	if (this.currentToken == TokenNameLBRACE){
		method.bodyStart = this.scanner.currentPosition;
	}
	// recovery
	if (this.currentElement != null){
		if (this.currentToken == TokenNameSEMICOLON){ // for invalid constructors
			method.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
			method.declarationSourceEnd = this.scanner.currentPosition-1;
			method.bodyEnd = this.scanner.currentPosition-1;
			if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
				this.currentElement = this.currentElement.parent;
			}
		}
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeConstructorHeaderName() {

	/* recovering - might be an empty message send */
	if (this.currentElement != null){
		if (this.lastIgnoredToken == TokenNamenew){ // was an allocation expression
			this.lastCheckPoint = this.scanner.startPosition; // force to restart at this exact position
			this.restartRecovery = true;
			return;
		}
	}

	// ConstructorHeaderName ::=  Modifiersopt 'Identifier' '('
	ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);

	//name -- this is not really revelant but we do .....
	cd.selector = this.identifierStack[this.identifierPtr];
	long selectorSource = this.identifierPositionStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//modifiers
	cd.declarationSourceStart = this.intStack[this.intPtr--];
	cd.modifiers = this.intStack[this.intPtr--];
	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			cd.annotations = new Annotation[length],
			0,
			length);
	}
	// javadoc
	cd.javadoc = this.javadoc;
	this.javadoc = null;

	//highlight starts at the selector starts
	cd.sourceStart = (int) (selectorSource >>> 32);
	pushOnAstStack(cd);
	cd.sourceEnd = this.lParenPos;
	cd.bodyStart = this.lParenPos+1;
	this.listLength = 0; // initialize this.listLength before reading parameters/throws

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = cd.bodyStart;
		if ((this.currentElement instanceof RecoveredType && this.lastIgnoredToken != TokenNameDOT)
			|| cd.modifiers != 0){
			this.currentElement = this.currentElement.add(cd, 0);
			this.lastIgnoredToken = -1;
		}
	}
}
protected void consumeConstructorHeaderNameWithTypeParameters() {

	/* recovering - might be an empty message send */
	if (this.currentElement != null){
		if (this.lastIgnoredToken == TokenNamenew){ // was an allocation expression
			this.lastCheckPoint = this.scanner.startPosition; // force to restart at this exact position
			this.restartRecovery = true;
			return;
		}
	}

	// ConstructorHeaderName ::=  Modifiersopt TypeParameters 'Identifier' '('
	ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);

	//name -- this is not really revelant but we do .....
	cd.selector = this.identifierStack[this.identifierPtr];
	long selectorSource = this.identifierPositionStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	// consume type parameters
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, cd.typeParameters = new TypeParameter[length], 0, length);

	//modifiers
	cd.declarationSourceStart = this.intStack[this.intPtr--];
	cd.modifiers = this.intStack[this.intPtr--];
	// consume annotations
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			cd.annotations = new Annotation[length],
			0,
			length);
	}
	// javadoc
	cd.javadoc = this.javadoc;
	this.javadoc = null;

	//highlight starts at the selector starts
	cd.sourceStart = (int) (selectorSource >>> 32);
	pushOnAstStack(cd);
	cd.sourceEnd = this.lParenPos;
	cd.bodyStart = this.lParenPos+1;
	this.listLength = 0; // initialize this.listLength before reading parameters/throws

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = cd.bodyStart;
		if ((this.currentElement instanceof RecoveredType && this.lastIgnoredToken != TokenNameDOT)
			|| cd.modifiers != 0){
			this.currentElement = this.currentElement.add(cd, 0);
			this.lastIgnoredToken = -1;
		}
	}
}
protected void consumeCreateInitializer() {
	pushOnAstStack(new Initializer(null, 0));
}
protected void consumeDefaultLabel() {
	// SwitchLabel ::= 'default' ':'
	CaseStatement defaultStatement = new CaseStatement(null, this.intStack[this.intPtr--], this.intStack[this.intPtr--]);
	// Look for $fall-through$ tag in leading comment for case statement
	if (hasLeadingTagComment(FALL_THROUGH_TAG, defaultStatement.sourceStart)) {
		defaultStatement.bits |= ASTNode.DocumentedFallthrough;
	}	
	pushOnAstStack(defaultStatement);
}
protected void consumeDefaultModifiers() {
	checkComment(); // might update modifiers with AccDeprecated
	pushOnIntStack(this.modifiers); // modifiers
	pushOnIntStack(
		this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
	resetModifiers();
	pushOnExpressionStackLengthStack(0); // no annotation
}
protected void consumeDiet() {
	// Diet ::= $empty
	checkComment();
	pushOnIntStack(this.modifiersSourceStart); // push the start position of a javadoc comment if there is one
	resetModifiers();
	jumpOverMethodBody();
}
protected void consumeDims() {
	// Dims ::= DimsLoop
	pushOnIntStack(this.dimensions);
	this.dimensions = 0;
}
protected void consumeDimWithOrWithOutExpr() {
	// DimWithOrWithOutExpr ::= '[' ']'
	pushOnExpressionStack(null);

	if(this.currentElement != null && this.currentToken == TokenNameLBRACE) {
		this.ignoreNextOpeningBrace = true;
		this.currentElement.bracketBalance++;
	}
}
protected void consumeDimWithOrWithOutExprs() {
	// DimWithOrWithOutExprs ::= DimWithOrWithOutExprs DimWithOrWithOutExpr
	concatExpressionLists();
}
protected void consumeUnionType() {
	// UnionType ::= UnionType '|' Type
	pushOnAstStack(getTypeReference(this.intStack[this.intPtr--]));
	optimizedConcatNodeLists();
}
protected void consumeUnionTypeAsClassType() {
	// UnionType ::= Type
	pushOnAstStack(getTypeReference(this.intStack[this.intPtr--]));
}
protected void consumeEmptyAnnotationTypeMemberDeclarationsopt() {
	// AnnotationTypeMemberDeclarationsopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyArgumentListopt() {
	// ArgumentListopt ::= $empty
	pushOnExpressionStackLengthStack(0);
}
protected void consumeEmptyArguments() {
	// Argumentsopt ::= $empty
	final FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr];
	pushOnIntStack(fieldDeclaration.sourceEnd);
	pushOnExpressionStackLengthStack(0);
}
protected void consumeEmptyArrayInitializer() {
	// ArrayInitializer ::= '{' ,opt '}'
	arrayInitializer(0);
}
protected void consumeEmptyArrayInitializeropt() {
	// ArrayInitializeropt ::= $empty
	pushOnExpressionStackLengthStack(0);
}
protected void consumeEmptyBlockStatementsopt() {
	// BlockStatementsopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyCatchesopt() {
	// Catchesopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyClassBodyDeclarationsopt() {
	// ClassBodyDeclarationsopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyDimsopt() {
	// Dimsopt ::= $empty
	pushOnIntStack(0);
}
protected void consumeEmptyEnumDeclarations() {
	// EnumBodyDeclarationsopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyExpression() {
	// Expressionopt ::= $empty
	pushOnExpressionStackLengthStack(0);
}
protected void consumeEmptyForInitopt() {
	// ForInitopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyForUpdateopt() {
	// ForUpdateopt ::= $empty
	pushOnExpressionStackLengthStack(0);
}
protected void consumeEmptyInterfaceMemberDeclarationsopt() {
	// InterfaceMemberDeclarationsopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyInternalCompilationUnit() {
	// InternalCompilationUnit ::= $empty
	// nothing to do by default
	if (this.compilationUnit.isPackageInfo()) {
		this.compilationUnit.types = new TypeDeclaration[1];
		this.compilationUnit.createPackageInfoType();
	}
}
protected void consumeEmptyMemberValueArrayInitializer() {
	// MemberValueArrayInitializer ::= '{' ',' '}'
	// MemberValueArrayInitializer ::= '{' '}'
	arrayInitializer(0);
}
protected void consumeEmptyMemberValuePairsopt() {
	// MemberValuePairsopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeEmptyMethodHeaderDefaultValue() {
	// DefaultValueopt ::= $empty
	AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
	if(method.isAnnotationMethod()) { //'method' can be a MethodDeclaration when recovery is started
		pushOnExpressionStackLengthStack(0);
	}
	this.recordStringLiterals = true;
}
protected void consumeEmptyStatement() {
	// EmptyStatement ::= ';'
	char[] source = this.scanner.source;
	if (source[this.endStatementPosition] == ';') {
		pushOnAstStack(new EmptyStatement(this.endStatementPosition, this.endStatementPosition));
	} else {
		if(source.length > 5) {
			int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
			int pos = this.endStatementPosition - 4;
			while (source[pos] == 'u') {
				pos--;
			}
			if (source[pos] == '\\' &&
					!((c1 = ScannerHelper.getNumericValue(source[this.endStatementPosition - 3])) > 15
						|| c1 < 0
						|| (c2 = ScannerHelper.getNumericValue(source[this.endStatementPosition - 2])) > 15
						|| c2 < 0
						|| (c3 = ScannerHelper.getNumericValue(source[this.endStatementPosition - 1])) > 15
						|| c3 < 0
						|| (c4 = ScannerHelper.getNumericValue(source[this.endStatementPosition])) > 15
						|| c4 < 0) &&
					((char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4)) == ';'){
				// we have a Unicode for the ';' (/u003B)
				pushOnAstStack(new EmptyStatement(pos, this.endStatementPosition));
				return;
			}
		}
		pushOnAstStack(new EmptyStatement(this.endPosition + 1, this.endStatementPosition));
	}
}
protected void consumeEmptySwitchBlock() {
	// SwitchBlock ::= '{' '}'
	pushOnAstLengthStack(0);
}
protected void consumeEmptyTypeDeclaration() {
	// ClassMemberDeclaration ::= ';'
	// InterfaceMemberDeclaration ::= ';'
	// TypeDeclaration ::= ';'
	pushOnAstLengthStack(0);
	if(!this.statementRecoveryActivated) problemReporter().superfluousSemicolon(this.endPosition+1, this.endStatementPosition);
	flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeEnhancedForStatement() {
	// EnhancedForStatement ::= EnhancedForStatementHeader Statement
	// EnhancedForStatementNoShortIf ::= EnhancedForStatementHeader StatementNoShortIf

	//statements
	this.astLengthPtr--;
	Statement statement = (Statement) this.astStack[this.astPtr--];

	// foreach statement is on the ast stack
	ForeachStatement foreachStatement = (ForeachStatement) this.astStack[this.astPtr];
	foreachStatement.action = statement;
	// remember useful empty statement
	if (statement instanceof EmptyStatement) statement.bits |= ASTNode.IsUsefulEmptyStatement;

	foreachStatement.sourceEnd = this.endStatementPosition;
}
protected void consumeEnhancedForStatementHeader(){
	// EnhancedForStatementHeader ::= EnhancedForStatementHeaderInit ':' Expression ')'
	final ForeachStatement statement = (ForeachStatement) this.astStack[this.astPtr];
	//updates are on the expression stack
	this.expressionLengthPtr--;
	final Expression collection = this.expressionStack[this.expressionPtr--];
	statement.collection = collection;
	statement.sourceEnd = this.rParenPos;

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		problemReporter().invalidUsageOfForeachStatements(statement.elementVariable, collection);
	}
}
protected void consumeEnhancedForStatementHeaderInit(boolean hasModifiers) {
	TypeReference type;

	char[] identifierName = this.identifierStack[this.identifierPtr];
	long namePosition = this.identifierPositionStack[this.identifierPtr];

	LocalDeclaration localDeclaration = createLocalDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
	localDeclaration.declarationSourceEnd = localDeclaration.declarationEnd;

	int extraDims = this.intStack[this.intPtr--];
	this.identifierPtr--;
	this.identifierLengthPtr--;
	// remove fake modifiers/modifiers start
	int declarationSourceStart = 0;
	int modifiersValue  = 0;
	if (hasModifiers) {
		declarationSourceStart = this.intStack[this.intPtr--];
		modifiersValue = this.intStack[this.intPtr--];
	} else {
		this.intPtr-=2;
	}

	type = getTypeReference(this.intStack[this.intPtr--] + extraDims); // type dimension

	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--])!= 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			localDeclaration.annotations = new Annotation[length],
			0,
			length);
	}
	if (hasModifiers) {
		localDeclaration.declarationSourceStart = declarationSourceStart;
		localDeclaration.modifiers = modifiersValue;
	} else {
		localDeclaration.declarationSourceStart = type.sourceStart;
	}
	localDeclaration.type = type;

	ForeachStatement iteratorForStatement =
		new ForeachStatement(
			localDeclaration,
			this.intStack[this.intPtr--]);
	pushOnAstStack(iteratorForStatement);

	iteratorForStatement.sourceEnd = localDeclaration.declarationSourceEnd;
}
protected void consumeEnterAnonymousClassBody(boolean qualified) {
	// EnterAnonymousClassBody ::= $empty
	TypeReference typeReference = getTypeReference(0);

	TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
	anonymousType.name = CharOperation.NO_CHAR;
	anonymousType.bits |= (ASTNode.IsAnonymousType|ASTNode.IsLocalType);
	QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
	markEnclosingMemberWithLocalType();
	pushOnAstStack(anonymousType);

	alloc.sourceEnd = this.rParenPos; //the position has been stored explicitly
	int argumentLength;
	if ((argumentLength = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= argumentLength;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			alloc.arguments = new Expression[argumentLength],
			0,
			argumentLength);
	}
	
	if (qualified) {
		this.expressionLengthPtr--;
		alloc.enclosingInstance = this.expressionStack[this.expressionPtr--];
	}
	
	alloc.type = typeReference;

	anonymousType.sourceEnd = alloc.sourceEnd;
	//position at the type while it impacts the anonymous declaration
	anonymousType.sourceStart = anonymousType.declarationSourceStart = alloc.type.sourceStart;
	alloc.sourceStart = this.intStack[this.intPtr--];
	pushOnExpressionStack(alloc);

	anonymousType.bodyStart = this.scanner.currentPosition;
	this.listLength = 0; // will be updated when reading super-interfaces

	// flush the comments related to the anonymous
	this.scanner.commentPtr = -1;

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = anonymousType.bodyStart;
		this.currentElement = this.currentElement.add(anonymousType, 0);
		if (!(this.currentElement instanceof RecoveredAnnotation)) {
			this.currentToken = 0; // opening brace already taken into account
		} else {
			this.ignoreNextOpeningBrace = true;
			this.currentElement.bracketBalance++;
		}
		this.lastIgnoredToken = -1;
	}
}
protected void consumeEnterCompilationUnit() {
	// EnterCompilationUnit ::= $empty
	// do nothing by default
}
protected void consumeEnterMemberValue() {
	// EnterMemberValue ::= $empty
	if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
		RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
		recoveredAnnotation.hasPendingMemberValueName = true;
	}
}
protected void consumeEnterMemberValueArrayInitializer() {
	// EnterMemberValueArrayInitializer ::= $empty
	if(this.currentElement != null) {
		this.ignoreNextOpeningBrace = true;
		this.currentElement.bracketBalance++;
	}
}
protected void consumeEnterVariable() {
	// EnterVariable ::= $empty
	// do nothing by default

	char[] identifierName = this.identifierStack[this.identifierPtr];
	long namePosition = this.identifierPositionStack[this.identifierPtr];
	int extendedDimension = this.intStack[this.intPtr--];
	AbstractVariableDeclaration declaration;
	// create the ast node
	boolean isLocalDeclaration = this.nestedMethod[this.nestedType] != 0;
	if (isLocalDeclaration) {
		// create the local variable declarations
		declaration =
			createLocalDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
	} else {
		// create the field declaration
		declaration =
			createFieldDeclaration(identifierName, (int) (namePosition >>> 32), (int) namePosition);
	}

	this.identifierPtr--;
	this.identifierLengthPtr--;
	TypeReference type;
	int variableIndex = this.variablesCounter[this.nestedType];
	int typeDim = 0;
	if (variableIndex == 0) {
		// first variable of the declaration (FieldDeclaration or LocalDeclaration)
		if (isLocalDeclaration) {
			declaration.declarationSourceStart = this.intStack[this.intPtr--];
			declaration.modifiers = this.intStack[this.intPtr--];
			// consume annotations
			int length;
			if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
				System.arraycopy(
					this.expressionStack,
					(this.expressionPtr -= length) + 1,
					declaration.annotations = new Annotation[length],
					0,
					length);
			}
			type = getTypeReference(typeDim = this.intStack[this.intPtr--]); // type dimension
			if (declaration.declarationSourceStart == -1) {
				// this is true if there is no modifiers for the local variable declaration
				declaration.declarationSourceStart = type.sourceStart;
			}
			pushOnAstStack(type);
		} else {
			type = getTypeReference(typeDim = this.intStack[this.intPtr--]); // type dimension
			pushOnAstStack(type);
			declaration.declarationSourceStart = this.intStack[this.intPtr--];
			declaration.modifiers = this.intStack[this.intPtr--];
			// consume annotations
			int length;
			if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
				System.arraycopy(
					this.expressionStack,
					(this.expressionPtr -= length) + 1,
					declaration.annotations = new Annotation[length],
					0,
					length);
			}
			// Store javadoc only on first declaration as it is the same for all ones
			FieldDeclaration fieldDeclaration = (FieldDeclaration) declaration;
			fieldDeclaration.javadoc = this.javadoc;
		}
		this.javadoc = null;
	} else {
		type = (TypeReference) this.astStack[this.astPtr - variableIndex];
		typeDim = type.dimensions();
		AbstractVariableDeclaration previousVariable =
			(AbstractVariableDeclaration) this.astStack[this.astPtr];
		declaration.declarationSourceStart = previousVariable.declarationSourceStart;
		declaration.modifiers = previousVariable.modifiers;
		final Annotation[] annotations = previousVariable.annotations;
		if (annotations != null) {
			final int annotationsLength = annotations.length;
			System.arraycopy(annotations, 0, declaration.annotations = new Annotation[annotationsLength], 0, annotationsLength);
		}
	}

	if (extendedDimension == 0) {
		declaration.type = type;
	} else {
		int dimension = typeDim + extendedDimension;
		declaration.type = copyDims(type, dimension);
	}
	this.variablesCounter[this.nestedType]++;
	pushOnAstStack(declaration);
	// recovery
	if (this.currentElement != null) {
		if (!(this.currentElement instanceof RecoveredType)
			&& (this.currentToken == TokenNameDOT
				//|| declaration.modifiers != 0
				|| (Util.getLineNumber(declaration.type.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)
						!= Util.getLineNumber((int) (namePosition >>> 32), this.scanner.lineEnds, 0, this.scanner.linePtr)))){
			this.lastCheckPoint = (int) (namePosition >>> 32);
			this.restartRecovery = true;
			return;
		}
		if (isLocalDeclaration){
			LocalDeclaration localDecl = (LocalDeclaration) this.astStack[this.astPtr];
			this.lastCheckPoint = localDecl.sourceEnd + 1;
			this.currentElement = this.currentElement.add(localDecl, 0);
		} else {
			FieldDeclaration fieldDecl = (FieldDeclaration) this.astStack[this.astPtr];
			this.lastCheckPoint = fieldDecl.sourceEnd + 1;
			this.currentElement = this.currentElement.add(fieldDecl, 0);
		}
		this.lastIgnoredToken = -1;
	}
}
protected void consumeEnumBodyNoConstants() {
	// nothing to do
	// The 0 on the astLengthStack has been pushed by EnumBodyDeclarationsopt
}
protected void consumeEnumBodyWithConstants() {
	// merge the constants values with the class body
	concatNodeLists();
}
protected void consumeEnumConstantHeader() {
   FieldDeclaration enumConstant = (FieldDeclaration) this.astStack[this.astPtr];
   boolean foundOpeningBrace = this.currentToken == TokenNameLBRACE;
   if (foundOpeningBrace){
      // qualified allocation expression
      TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
      anonymousType.name = CharOperation.NO_CHAR;
      anonymousType.bits |= (ASTNode.IsAnonymousType|ASTNode.IsLocalType);
      final int start = this.scanner.startPosition;
      anonymousType.declarationSourceStart = start;
      anonymousType.sourceStart = start;
      anonymousType.sourceEnd = start; // closing parenthesis
      anonymousType.modifiers = 0;
      anonymousType.bodyStart = this.scanner.currentPosition;
      markEnclosingMemberWithLocalType();
      consumeNestedType();
      this.variablesCounter[this.nestedType]++;
      pushOnAstStack(anonymousType);
      QualifiedAllocationExpression allocationExpression = new QualifiedAllocationExpression(anonymousType);
      allocationExpression.enumConstant = enumConstant;

      // fill arguments if needed
      int length;
      if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
         this.expressionPtr -= length;
         System.arraycopy(
               this.expressionStack,
               this.expressionPtr + 1,
               allocationExpression.arguments = new Expression[length],
               0,
               length);
      }
      enumConstant.initialization = allocationExpression;
   } else {
      AllocationExpression allocationExpression = new AllocationExpression();
      allocationExpression.enumConstant = enumConstant;
      // fill arguments if needed
      int length;
      if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
         this.expressionPtr -= length;
         System.arraycopy(
               this.expressionStack,
               this.expressionPtr + 1,
               allocationExpression.arguments = new Expression[length],
               0,
               length);
      }
      enumConstant.initialization = allocationExpression;
   }
   // initialize the starting position of the allocation expression
   enumConstant.initialization.sourceStart = enumConstant.declarationSourceStart;

   // recovery
   if (this.currentElement != null) {
	  if(foundOpeningBrace) {
	  	TypeDeclaration anonymousType = (TypeDeclaration) this.astStack[this.astPtr];
	  	this.currentElement = this.currentElement.add(anonymousType, 0);
      	this.lastCheckPoint = anonymousType.bodyStart;
        this.lastIgnoredToken = -1;
        this.currentToken = 0; // opening brace already taken into account
	  } else {
	  	  if(this.currentToken == TokenNameSEMICOLON) {
		  	RecoveredType currentType = currentRecoveryType();
			if(currentType != null) {
				currentType.insideEnumConstantPart = false;
			}
		  }
		  this.lastCheckPoint = this.scanner.startPosition; // force to restart at this exact position
	      this.lastIgnoredToken = -1;
	      this.restartRecovery = true;
	  }
   }
}
protected void consumeEnumConstantHeaderName() {
	if (this.currentElement != null) {
		if (!(this.currentElement instanceof RecoveredType
					|| (this.currentElement instanceof RecoveredField && ((RecoveredField)this.currentElement).fieldDeclaration.type == null))
				|| (this.lastIgnoredToken == TokenNameDOT)) {
			this.lastCheckPoint = this.scanner.startPosition;
			this.restartRecovery = true;
			return;
		}
	}
   long namePosition = this.identifierPositionStack[this.identifierPtr];
   char[] constantName = this.identifierStack[this.identifierPtr];
   final int sourceEnd = (int) namePosition;
   FieldDeclaration enumConstant = createFieldDeclaration(constantName, (int) (namePosition >>> 32), sourceEnd);
   this.identifierPtr--;
   this.identifierLengthPtr--;
   enumConstant.modifiersSourceStart = this.intStack[this.intPtr--];
   enumConstant.modifiers = this.intStack[this.intPtr--];
   enumConstant.declarationSourceStart = enumConstant.modifiersSourceStart;

	// consume annotations
   int length;
   if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
      System.arraycopy(
         this.expressionStack,
         (this.expressionPtr -= length) + 1,
         enumConstant.annotations = new Annotation[length],
         0,
         length);
   }
   pushOnAstStack(enumConstant);
	if (this.currentElement != null){
		this.lastCheckPoint = enumConstant.sourceEnd + 1;
		this.currentElement = this.currentElement.add(enumConstant, 0);
	}
	// javadoc
	enumConstant.javadoc = this.javadoc;
	this.javadoc = null;
}
protected void consumeEnumConstantNoClassBody() {
	// set declarationEnd and declarationSourceEnd
	int endOfEnumConstant = this.intStack[this.intPtr--];
	final FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr];
	fieldDeclaration.declarationEnd = endOfEnumConstant;
	fieldDeclaration.declarationSourceEnd = endOfEnumConstant;
	// initialize the starting position of the allocation expression
	ASTNode initialization = fieldDeclaration.initialization;
	if (initialization != null) {
		initialization.sourceEnd = endOfEnumConstant;
	}
}
protected void consumeEnumConstants() {
	concatNodeLists();
}
protected void consumeEnumConstantWithClassBody() {
	dispatchDeclarationInto(this.astLengthStack[this.astLengthPtr--]);
	TypeDeclaration anonymousType = (TypeDeclaration) this.astStack[this.astPtr--]; // pop type
	this.astLengthPtr--;
	anonymousType.bodyEnd = this.endPosition;
	anonymousType.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
	final FieldDeclaration fieldDeclaration = ((FieldDeclaration) this.astStack[this.astPtr]);
	fieldDeclaration.declarationEnd = this.endStatementPosition;
	int declarationSourceEnd = anonymousType.declarationSourceEnd;
	fieldDeclaration.declarationSourceEnd = declarationSourceEnd;
	this.intPtr --; // remove end position of the arguments
	this.variablesCounter[this.nestedType] = 0;
	this.nestedType--;
	ASTNode initialization = fieldDeclaration.initialization;
	if (initialization != null) {
		initialization.sourceEnd = declarationSourceEnd;
	}
}
protected void consumeEnumDeclaration() {
	// EnumDeclaration ::= EnumHeader ClassHeaderImplementsopt EnumBody
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		//there are length declarations
		//dispatch according to the type of the declarations
		dispatchDeclarationIntoEnumDeclaration(length);
	}

	TypeDeclaration enumDeclaration = (TypeDeclaration) this.astStack[this.astPtr];

	//convert constructor that do not have the type's name into methods
	boolean hasConstructor = enumDeclaration.checkConstructors(this);

	//add the default constructor when needed
	if (!hasConstructor) {
		boolean insideFieldInitializer = false;
		if (this.diet) {
			for (int i = this.nestedType; i > 0; i--){
				if (this.variablesCounter[i] > 0) {
					insideFieldInitializer = true;
					break;
				}
			}
		}
		enumDeclaration.createDefaultConstructor(!this.diet || insideFieldInitializer, true);
	}

	//always add <clinit> (will be remove at code gen time if empty)
	if (this.scanner.containsAssertKeyword) {
		enumDeclaration.bits |= ASTNode.ContainsAssertion;
	}
	enumDeclaration.addClinit();
	enumDeclaration.bodyEnd = this.endStatementPosition;
	if (length == 0 && !containsComment(enumDeclaration.bodyStart, enumDeclaration.bodyEnd)) {
		enumDeclaration.bits |= ASTNode.UndocumentedEmptyBlock;
	}

	enumDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeEnumDeclarations() {
	// Do nothing by default
}
protected void consumeEnumHeader() {
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if (this.currentToken == TokenNameLBRACE) {
		typeDecl.bodyStart = this.scanner.currentPosition;
	}

	if (this.currentElement != null) {
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}

	// flush the comments related to the enum header
	this.scanner.commentPtr = -1;
}
protected void consumeEnumHeaderName() {
	// EnumHeaderName ::= Modifiersopt 'enum' Identifier
	TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
	if (this.nestedMethod[this.nestedType] == 0) {
		if (this.nestedType != 0) {
			enumDeclaration.bits |= ASTNode.IsMemberType;
		}
	} else {
		// Record that the block has a declaration for local types
//		markEnclosingMemberWithLocalType();
		blockReal();
	}
	//highlight the name of the type
	long pos = this.identifierPositionStack[this.identifierPtr];
	enumDeclaration.sourceEnd = (int) pos;
	enumDeclaration.sourceStart = (int) (pos >>> 32);
	enumDeclaration.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//compute the declaration source too
	// 'class' and 'interface' push two int positions: the beginning of the class token and its end.
	// we want to keep the beginning position but get rid of the end position
	// it is only used for the ClassLiteralAccess positions.
	enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
	this.intPtr--; // remove the end position of the class token

	enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
	enumDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccEnum;
	if (enumDeclaration.modifiersSourceStart >= 0) {
		enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
	}

	// Store secondary info
	if ((enumDeclaration.bits & ASTNode.IsMemberType) == 0 && (enumDeclaration.bits & ASTNode.IsLocalType) == 0) {
		if (this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
			enumDeclaration.bits |= ASTNode.IsSecondaryType;
		}
	}

	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			enumDeclaration.annotations = new Annotation[length],
			0,
			length);
	}
//	if (this.currentToken == TokenNameLBRACE) {
//		enumDeclaration.bodyStart = this.scanner.currentPosition;
//	}
	enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
	pushOnAstStack(enumDeclaration);

	this.listLength = 0; // will be updated when reading super-interfaces

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		//TODO this code will be never run while 'enum' is an identifier in 1.3 scanner
		problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = enumDeclaration.bodyStart;
		this.currentElement = this.currentElement.add(enumDeclaration, 0);
		this.lastIgnoredToken = -1;
	}
	// javadoc
	enumDeclaration.javadoc = this.javadoc;
	this.javadoc = null;
}
protected void consumeEnumHeaderNameWithTypeParameters() {
	// EnumHeaderNameWithTypeParameters ::= Modifiersopt 'enum' Identifier TypeParameters
	TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
	// consume type parameters
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, enumDeclaration.typeParameters = new TypeParameter[length], 0, length);

	problemReporter().invalidUsageOfTypeParametersForEnumDeclaration(enumDeclaration);

	enumDeclaration.bodyStart = enumDeclaration.typeParameters[length-1].declarationSourceEnd + 1;

//	enumDeclaration.typeParameters = null;

	this.listTypeParameterLength = 0;

	if (this.nestedMethod[this.nestedType] == 0) {
		if (this.nestedType != 0) {
			enumDeclaration.bits |= ASTNode.IsMemberType;
		}
	} else {
		// Record that the block has a declaration for local types
//		markEnclosingMemberWithLocalType();
		blockReal();
	}
	//highlight the name of the type
	long pos = this.identifierPositionStack[this.identifierPtr];
	enumDeclaration.sourceEnd = (int) pos;
	enumDeclaration.sourceStart = (int) (pos >>> 32);
	enumDeclaration.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//compute the declaration source too
	// 'class' and 'interface' push two int positions: the beginning of the class token and its end.
	// we want to keep the beginning position but get rid of the end position
	// it is only used for the ClassLiteralAccess positions.
	enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
	this.intPtr--; // remove the end position of the class token

	enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
	enumDeclaration.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccEnum;
	if (enumDeclaration.modifiersSourceStart >= 0) {
		enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
	}

	// Store secondary info
	if ((enumDeclaration.bits & ASTNode.IsMemberType) == 0 && (enumDeclaration.bits & ASTNode.IsLocalType) == 0) {
		if (this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
			enumDeclaration.bits |= ASTNode.IsSecondaryType;
		}
	}

	// consume annotations
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			enumDeclaration.annotations = new Annotation[length],
			0,
			length);
	}
//	if (this.currentToken == TokenNameLBRACE) {
//		enumDeclaration.bodyStart = this.scanner.currentPosition;
//	}
	enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
	pushOnAstStack(enumDeclaration);

	this.listLength = 0; // will be updated when reading super-interfaces

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		//TODO this code will be never run while 'enum' is an identifier in 1.3 scanner
		problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = enumDeclaration.bodyStart;
		this.currentElement = this.currentElement.add(enumDeclaration, 0);
		this.lastIgnoredToken = -1;
	}
	// javadoc
	enumDeclaration.javadoc = this.javadoc;
	this.javadoc = null;
}
protected void consumeEqualityExpression(int op) {
	// EqualityExpression ::= EqualityExpression '==' RelationalExpression
	// EqualityExpression ::= EqualityExpression '!=' RelationalExpression

	//optimize the push/pop

	this.expressionPtr--;
	this.expressionLengthPtr--;
	this.expressionStack[this.expressionPtr] =
		new EqualExpression(
			this.expressionStack[this.expressionPtr],
			this.expressionStack[this.expressionPtr + 1],
			op);
}
/*
 * @param op
 */
protected void consumeEqualityExpressionWithName(int op) {
	// EqualityExpression ::= Name '==' RelationalExpression
	// EqualityExpression ::= Name '!=' RelationalExpression
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
	this.expressionPtr--;
	this.expressionLengthPtr--;
	this.expressionStack[this.expressionPtr] =
		new EqualExpression(
			this.expressionStack[this.expressionPtr + 1],
			this.expressionStack[this.expressionPtr],
			op);
}
protected void consumeExitMemberValue() {
	// ExitMemberValue ::= $empty
	if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
		RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
		recoveredAnnotation.hasPendingMemberValueName = false;
		recoveredAnnotation.memberValuPairEqualEnd = -1;
	}
}
protected void consumeExitTryBlock() {
	//ExitTryBlock ::= $empty
	if(this.currentElement != null) {
		this.restartRecovery = true;
	}
}
protected void consumeExitVariableWithInitialization() {
	// ExitVariableWithInitialization ::= $empty
	// do nothing by default
	this.expressionLengthPtr--;
	AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration) this.astStack[this.astPtr];
	variableDecl.initialization = this.expressionStack[this.expressionPtr--];
	// we need to update the declarationSourceEnd of the local variable declaration to the
	// source end position of the initialization expression
	variableDecl.declarationSourceEnd = variableDecl.initialization.sourceEnd;
	variableDecl.declarationEnd = variableDecl.initialization.sourceEnd;

	recoveryExitFromVariable();
}
protected void consumeExitVariableWithoutInitialization() {
	// ExitVariableWithoutInitialization ::= $empty
	// do nothing by default

	AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration) this.astStack[this.astPtr];
	variableDecl.declarationSourceEnd = variableDecl.declarationEnd;
	if(this.currentElement != null && this.currentElement instanceof RecoveredField) {
		if(this.endStatementPosition > variableDecl.sourceEnd) {
			this.currentElement.updateSourceEndIfNecessary(this.endStatementPosition);
		}
	}
	recoveryExitFromVariable();
}
protected void consumeExplicitConstructorInvocation(int flag, int recFlag) {

	/* flag allows to distinguish 3 cases :
	(0) :
	ExplicitConstructorInvocation ::= 'this' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= 'super' '(' ArgumentListopt ')' ';'
	(1) :
	ExplicitConstructorInvocation ::= Primary '.' 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Primary '.' 'this' '(' ArgumentListopt ')' ';'
	(2) :
	ExplicitConstructorInvocation ::= Name '.' 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Name '.' 'this' '(' ArgumentListopt ')' ';'
	*/
	int startPosition = this.intStack[this.intPtr--];
	ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
	}
	switch (flag) {
		case 0 :
			ecc.sourceStart = startPosition;
			break;
		case 1 :
			this.expressionLengthPtr--;
			ecc.sourceStart = (ecc.qualification = this.expressionStack[this.expressionPtr--]).sourceStart;
			break;
		case 2 :
			ecc.sourceStart = (ecc.qualification = getUnspecifiedReferenceOptimized()).sourceStart;
			break;
	}
	pushOnAstStack(ecc);
	ecc.sourceEnd = this.endStatementPosition;
}
protected void consumeExplicitConstructorInvocationWithTypeArguments(int flag, int recFlag) {

	/* flag allows to distinguish 3 cases :
	(0) :
	ExplicitConstructorInvocation ::= TypeArguments 'this' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= TypeArguments 'super' '(' ArgumentListopt ')' ';'
	(1) :
	ExplicitConstructorInvocation ::= Primary '.' TypeArguments 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Primary '.' TypeArguments 'this' '(' ArgumentListopt ')' ';'
	(2) :
	ExplicitConstructorInvocation ::= Name '.' TypeArguments 'super' '(' ArgumentListopt ')' ';'
	ExplicitConstructorInvocation ::= Name '.' TypeArguments 'this' '(' ArgumentListopt ')' ';'
	*/
	int startPosition = this.intStack[this.intPtr--];
	ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
	}
	length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, ecc.typeArguments = new TypeReference[length], 0, length);
	ecc.typeArgumentsSourceStart = this.intStack[this.intPtr--];

	switch (flag) {
		case 0 :
			ecc.sourceStart = startPosition;
			break;
		case 1 :
			this.expressionLengthPtr--;
			ecc.sourceStart = (ecc.qualification = this.expressionStack[this.expressionPtr--]).sourceStart;
			break;
		case 2 :
			ecc.sourceStart = (ecc.qualification = getUnspecifiedReferenceOptimized()).sourceStart;
			break;
	}

	pushOnAstStack(ecc);
	ecc.sourceEnd = this.endStatementPosition;
}
protected void consumeExpressionStatement() {
	// ExpressionStatement ::= StatementExpression ';'
	this.expressionLengthPtr--;
	Expression expression = this.expressionStack[this.expressionPtr--];
	expression.statementEnd = this.endStatementPosition;
	expression.bits |= ASTNode.InsideExpressionStatement;
	pushOnAstStack(expression);
}
protected void consumeFieldAccess(boolean isSuperAccess) {
	// FieldAccess ::= Primary '.' 'Identifier'
	// FieldAccess ::= 'super' '.' 'Identifier'

	FieldReference fr =
		new FieldReference(
			this.identifierStack[this.identifierPtr],
			this.identifierPositionStack[this.identifierPtr--]);
	this.identifierLengthPtr--;
	if (isSuperAccess) {
		//considers the fieldReference beginning at the 'super' ....
		fr.sourceStart = this.intStack[this.intPtr--];
		fr.receiver = new SuperReference(fr.sourceStart, this.endPosition);
		pushOnExpressionStack(fr);
	} else {
		//optimize push/pop
		fr.receiver = this.expressionStack[this.expressionPtr];
		//field reference begins at the receiver
		fr.sourceStart = fr.receiver.sourceStart;
		this.expressionStack[this.expressionPtr] = fr;
	}
}
protected void consumeFieldDeclaration() {
	// See consumeLocalVariableDeclarationDefaultModifier() in case of change: duplicated code
	// FieldDeclaration ::= Modifiersopt Type VariableDeclarators ';'

	/*
	this.astStack :
	this.expressionStack: Expression Expression ...... Expression
	this.identifierStack : type  identifier identifier ...... identifier
	this.intStack : typeDim      dim        dim               dim
	 ==>
	this.astStack : FieldDeclaration FieldDeclaration ...... FieldDeclaration
	this.expressionStack :
	this.identifierStack :
	this.intStack :

	*/
	int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];

	for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
		FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr - i];
		fieldDeclaration.declarationSourceEnd = this.endStatementPosition;
		fieldDeclaration.declarationEnd = this.endStatementPosition;	// semi-colon included
	}

	updateSourceDeclarationParts(variableDeclaratorsCounter);
	int endPos = flushCommentsDefinedPriorTo(this.endStatementPosition);
	if (endPos != this.endStatementPosition) {
		for (int i = 0; i < variableDeclaratorsCounter; i++) {
			FieldDeclaration fieldDeclaration = (FieldDeclaration) this.astStack[this.astPtr - i];
			fieldDeclaration.declarationSourceEnd = endPos;
		}
	}
	// update the this.astStack, this.astPtr and this.astLengthStack
	int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
	System.arraycopy(
		this.astStack,
		startIndex,
		this.astStack,
		startIndex - 1,
		variableDeclaratorsCounter);
	this.astPtr--; // remove the type reference
	this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;

	// recovery
	if (this.currentElement != null) {
		this.lastCheckPoint = endPos + 1;
		if (this.currentElement.parent != null && this.currentElement instanceof RecoveredField){
			if (!(this.currentElement instanceof RecoveredInitializer)) {
				this.currentElement = this.currentElement.parent;
			}
		}
		this.restartRecovery = true;
	}
	this.variablesCounter[this.nestedType] = 0;
}
protected void consumeForceNoDiet() {
	// ForceNoDiet ::= $empty
	this.dietInt++;
}
protected void consumeForInit() {
	// ForInit ::= StatementExpressionList
	pushOnAstLengthStack(-1);
}
protected void consumeFormalParameter(boolean isVarArgs) {
	// FormalParameter ::= Type VariableDeclaratorId ==> false
	// FormalParameter ::= Modifiers Type VariableDeclaratorId ==> true
	/*
	this.astStack :
	this.identifierStack : type identifier
	this.intStack : dim dim
	 ==>
	this.astStack : Argument
	this.identifierStack :
	this.intStack :
	*/

	this.identifierLengthPtr--;
	char[] identifierName = this.identifierStack[this.identifierPtr];
	long namePositions = this.identifierPositionStack[this.identifierPtr--];
	int extendedDimensions = this.intStack[this.intPtr--];
	int endOfEllipsis = 0;
	if (isVarArgs) {
		endOfEllipsis = this.intStack[this.intPtr--];
	}
	int firstDimensions = this.intStack[this.intPtr--];
	final int typeDimensions = firstDimensions + extendedDimensions;
	TypeReference type = getTypeReference(typeDimensions);
	if (isVarArgs) {
		type = copyDims(type, typeDimensions + 1);
		if (extendedDimensions == 0) {
			type.sourceEnd = endOfEllipsis;
		}
		type.bits |= ASTNode.IsVarArgs; // set isVarArgs
	}
	int modifierPositions = this.intStack[this.intPtr--];
	this.intPtr--;
	Argument arg =
		new Argument(
			identifierName,
			namePositions,
			type,
			this.intStack[this.intPtr + 1] & ~ClassFileConstants.AccDeprecated); // modifiers
	arg.declarationSourceStart = modifierPositions;
	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			arg.annotations = new Annotation[length],
			0,
			length);
	}
	pushOnAstStack(arg);

	/* if incomplete method header, this.listLength counter will not have been reset,
		indicating that some arguments are available on the stack */
	this.listLength++;

	if(isVarArgs) {
		if (!this.statementRecoveryActivated &&
				this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
				this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
				problemReporter().invalidUsageOfVarargs(arg);
		} else if (!this.statementRecoveryActivated &&
				extendedDimensions > 0) {
			problemReporter().illegalExtendedDimensions(arg);
		}
	}
}
protected void consumeFormalParameterList() {
	// FormalParameterList ::= FormalParameterList ',' FormalParameter
	optimizedConcatNodeLists();
}
protected void consumeFormalParameterListopt() {
	// FormalParameterListopt ::= $empty
	pushOnAstLengthStack(0);
}
protected void consumeGenericType() {
	// GenericType ::= ClassOrInterface TypeArguments
	// nothing to do
	// Will be consume by a getTypeReference call
}
protected void consumeGenericTypeArrayType() {
	// nothing to do
	// Will be consume by a getTypeReference call
}
protected void consumeGenericTypeNameArrayType() {
	// nothing to do
	// Will be consume by a getTypeReference call
}
protected void consumeGenericTypeWithDiamond() {
	// GenericType ::= ClassOrInterface '<' '>'
	// zero type arguments == <>
	pushOnGenericsLengthStack(-1);
	concatGenericsLists();
	this.intPtr--;	// pop the null dimension pushed in by consumeReferenceType, as we have no type between <>, getTypeReference won't kick in 
}
protected void consumeImportDeclaration() {
	// SingleTypeImportDeclaration ::= SingleTypeImportDeclarationName ';'
	ImportReference impt = (ImportReference) this.astStack[this.astPtr];
	// flush annotations defined prior to import statements
	impt.declarationEnd = this.endStatementPosition;
	impt.declarationSourceEnd =
		flushCommentsDefinedPriorTo(impt.declarationSourceEnd);

	// recovery
	if (this.currentElement != null) {
		this.lastCheckPoint = impt.declarationSourceEnd + 1;
		this.currentElement = this.currentElement.add(impt, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true;
		// used to avoid branching back into the regular automaton
	}
}
protected void consumeImportDeclarations() {
	// ImportDeclarations ::= ImportDeclarations ImportDeclaration
	optimizedConcatNodeLists();
}
protected void consumeInsideCastExpression() {
	// InsideCastExpression ::= $empty
}
protected void consumeInsideCastExpressionLL1() {
	// InsideCastExpressionLL1 ::= $empty
	pushOnGenericsLengthStack(0); // handle type arguments
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
	pushOnExpressionStack(getTypeReference(0));
}
protected void consumeInsideCastExpressionWithQualifiedGenerics() {
	// InsideCastExpressionWithQualifiedGenerics ::= $empty
}
protected void consumeInstanceOfExpression() {
	// RelationalExpression ::= RelationalExpression 'instanceof' ReferenceType
	//optimize the push/pop

	//by construction, no base type may be used in getTypeReference
	Expression exp;
	this.expressionStack[this.expressionPtr] = exp =
		new InstanceOfExpression(
			this.expressionStack[this.expressionPtr],
			getTypeReference(this.intStack[this.intPtr--]));
	if (exp.sourceEnd == 0) {
		//array on base type....
		exp.sourceEnd = this.scanner.startPosition - 1;
	}
	//the scanner is on the next token already....
}
protected void consumeInstanceOfExpressionWithName() {
	// RelationalExpression_NotName ::= Name instanceof ReferenceType
	//optimize the push/pop

	//by construction, no base type may be used in getTypeReference
	TypeReference reference = getTypeReference(this.intStack[this.intPtr--]);
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
	Expression exp;
	this.expressionStack[this.expressionPtr] = exp =
		new InstanceOfExpression(
			this.expressionStack[this.expressionPtr],
			reference);
	if (exp.sourceEnd == 0) {
		//array on base type....
		exp.sourceEnd = this.scanner.startPosition - 1;
	}
	//the scanner is on the next token already....
}
protected void consumeInterfaceDeclaration() {
	// see consumeClassDeclaration in case of changes: duplicated code
	// InterfaceDeclaration ::= InterfaceHeader InterfaceBody
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		//there are length declarations
		//dispatch.....according to the type of the declarations
		dispatchDeclarationInto(length);
	}

	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];

	//convert constructor that do not have the type's name into methods
	typeDecl.checkConstructors(this);
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=212713, 
	// reject initializers that have been tolerated by the grammar.
	FieldDeclaration [] fields = typeDecl.fields;
	int fieldCount = fields == null ? 0 : fields.length;
	for (int i = 0; i < fieldCount; i++) {
		FieldDeclaration field = fields[i];
		if (field instanceof Initializer) {
			problemReporter().interfaceCannotHaveInitializers(typeDecl.name, field);
		}
	}

	//always add <clinit> (will be remove at code gen time if empty)
	if (this.scanner.containsAssertKeyword) {
		typeDecl.bits |= ASTNode.ContainsAssertion;
	}
	typeDecl.addClinit();
	typeDecl.bodyEnd = this.endStatementPosition;
	if (length == 0 && !containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
		typeDecl.bits |= ASTNode.UndocumentedEmptyBlock;
	}
	typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeInterfaceHeader() {
	// InterfaceHeader ::= InterfaceHeaderName InterfaceHeaderExtendsopt

	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if (this.currentToken == TokenNameLBRACE){
		typeDecl.bodyStart = this.scanner.currentPosition;
	}
	if (this.currentElement != null){
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
	// flush the comments related to the interface header
	this.scanner.commentPtr = -1;
}
protected void consumeInterfaceHeaderExtends() {
	// InterfaceHeaderExtends ::= 'extends' InterfaceTypeList
	int length = this.astLengthStack[this.astLengthPtr--];
	//super interfaces
	this.astPtr -= length;
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	System.arraycopy(
		this.astStack,
		this.astPtr + 1,
		typeDecl.superInterfaces = new TypeReference[length],
		0,
		length);
	for (int i = 0, max = typeDecl.superInterfaces.length; i < max; i++) {
		typeDecl.superInterfaces[i].bits |= ASTNode.IsSuperType;
	}
	typeDecl.bodyStart = typeDecl.superInterfaces[length-1].sourceEnd + 1;
	this.listLength = 0; // reset after having read super-interfaces
	// recovery
	if (this.currentElement != null) {
		this.lastCheckPoint = typeDecl.bodyStart;
	}
}
protected void consumeInterfaceHeaderName1() {
	// InterfaceHeaderName ::= Modifiersopt 'interface' 'Identifier'
	TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);

	if (this.nestedMethod[this.nestedType] == 0) {
		if (this.nestedType != 0) {
			typeDecl.bits |= ASTNode.IsMemberType;
		}
	} else {
		// Record that the block has a declaration for local types
		typeDecl.bits |= ASTNode.IsLocalType;
		markEnclosingMemberWithLocalType();
		blockReal();
	}

	//highlight the name of the type
	long pos = this.identifierPositionStack[this.identifierPtr];
	typeDecl.sourceEnd = (int) pos;
	typeDecl.sourceStart = (int) (pos >>> 32);
	typeDecl.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	//compute the declaration source too
	// 'class' and 'interface' push two int positions: the beginning of the class token and its end.
	// we want to keep the beginning position but get rid of the end position
	// it is only used for the ClassLiteralAccess positions.
	typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
	this.intPtr--; // remove the end position of the class token
	typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
	typeDecl.modifiers = this.intStack[this.intPtr--] | ClassFileConstants.AccInterface;
	if (typeDecl.modifiersSourceStart >= 0) {
		typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
	}

	// Store secondary info
	if ((typeDecl.bits & ASTNode.IsMemberType) == 0 && (typeDecl.bits & ASTNode.IsLocalType) == 0) {
		if (this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
			typeDecl.bits |= ASTNode.IsSecondaryType;
		}
	}

	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			typeDecl.annotations = new Annotation[length],
			0,
			length);
	}
	typeDecl.bodyStart = typeDecl.sourceEnd + 1;
	pushOnAstStack(typeDecl);
	this.listLength = 0; // will be updated when reading super-interfaces
	// recovery
	if (this.currentElement != null){ // is recovering
		this.lastCheckPoint = typeDecl.bodyStart;
		this.currentElement = this.currentElement.add(typeDecl, 0);
		this.lastIgnoredToken = -1;
	}
	// javadoc
	typeDecl.javadoc = this.javadoc;
	this.javadoc = null;
}
protected void consumeInterfaceMemberDeclarations() {
	// InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
	concatNodeLists();
}
protected void consumeInterfaceMemberDeclarationsopt() {
	// InterfaceMemberDeclarationsopt ::= NestedType InterfaceMemberDeclarations
	this.nestedType--;
}
protected void consumeInterfaceType() {
	// InterfaceType ::= ClassOrInterfaceType
	pushOnAstStack(getTypeReference(0));
	/* if incomplete type header, this.listLength counter will not have been reset,
		indicating that some interfaces are available on the stack */
	this.listLength++;
}
protected void consumeInterfaceTypeList() {
	// InterfaceTypeList ::= InterfaceTypeList ',' InterfaceType
	optimizedConcatNodeLists();
}
protected void consumeInternalCompilationUnit() {
	// InternalCompilationUnit ::= PackageDeclaration
	// InternalCompilationUnit ::= PackageDeclaration ImportDeclarations ReduceImports
	// InternalCompilationUnit ::= ImportDeclarations ReduceImports
	if (this.compilationUnit.isPackageInfo()) {
		this.compilationUnit.types = new TypeDeclaration[1];
		this.compilationUnit.createPackageInfoType();
	}
}
protected void consumeInternalCompilationUnitWithTypes() {
	// InternalCompilationUnit ::= PackageDeclaration ImportDeclarations ReduceImports TypeDeclarations
	// InternalCompilationUnit ::= PackageDeclaration TypeDeclarations
	// InternalCompilationUnit ::= TypeDeclarations
	// InternalCompilationUnit ::= ImportDeclarations ReduceImports TypeDeclarations
	// consume type declarations
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		if (this.compilationUnit.isPackageInfo()) {
			this.compilationUnit.types = new TypeDeclaration[length + 1];
			this.astPtr -= length;
			System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 1, length);
			this.compilationUnit.createPackageInfoType();
		} else {
			this.compilationUnit.types = new TypeDeclaration[length];
			this.astPtr -= length;
			System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 0, length);
		}
	}
}
protected void consumeInvalidAnnotationTypeDeclaration() {
	// BlockStatement ::= AnnotationTypeDeclaration
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if(!this.statementRecoveryActivated) problemReporter().illegalLocalTypeDeclaration(typeDecl);
	// remove the ast node created in interface header
	this.astPtr--;
	pushOnAstLengthStack(-1);
	concatNodeLists();
}
protected void consumeInvalidConstructorDeclaration() {
	// ConstructorDeclaration ::= ConstructorHeader ';'
	// now we know that the top of stack is a constructorDeclaration
	ConstructorDeclaration cd = (ConstructorDeclaration) this.astStack[this.astPtr];

	cd.bodyEnd = this.endPosition; // position just before the trailing semi-colon
	cd.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
	// report the problem and continue the parsing - narrowing the problem onto the method

	cd.modifiers |= ExtraCompilerModifiers.AccSemicolonBody; // remember semi-colon body
}
protected void consumeInvalidConstructorDeclaration(boolean hasBody) {
	// InvalidConstructorDeclaration ::= ConstructorHeader ConstructorBody ==> true
	// InvalidConstructorDeclaration ::= ConstructorHeader ';' ==> false

	/*
	this.astStack : modifiers arguments throws statements
	this.identifierStack : name
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	*/
	if (hasBody) {
		// pop the position of the {  (body of the method) pushed in block decl
		this.intPtr--;
	}

	//statements
	if (hasBody) {
		this.realBlockPtr--;
	}

	int length;
	if (hasBody && ((length = this.astLengthStack[this.astLengthPtr--]) != 0)) {
		this.astPtr -= length;
	}
	ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) this.astStack[this.astPtr];
	constructorDeclaration.bodyEnd = this.endStatementPosition;
	constructorDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
	if (!hasBody) {
		constructorDeclaration.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
	}
}
protected void consumeInvalidEnumDeclaration() {
	// BlockStatement ::= EnumDeclaration
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if(!this.statementRecoveryActivated) problemReporter().illegalLocalTypeDeclaration(typeDecl);
	// remove the ast node created in interface header
	this.astPtr--;
	pushOnAstLengthStack(-1);
	concatNodeLists();
}
protected void consumeInvalidInterfaceDeclaration() {
	// BlockStatement ::= InvalidInterfaceDeclaration
	//InterfaceDeclaration ::= Modifiersopt 'interface' 'Identifier' ExtendsInterfacesopt InterfaceHeader InterfaceBody
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if(!this.statementRecoveryActivated) problemReporter().illegalLocalTypeDeclaration(typeDecl);
	// remove the ast node created in interface header
	this.astPtr--;
	pushOnAstLengthStack(-1);
	concatNodeLists();
}
protected void consumeInvalidMethodDeclaration() {
	// InterfaceMemberDeclaration ::= InvalidMethodDeclaration

	/*
	this.astStack : modifiers arguments throws statements
	this.identifierStack : type name
	this.intStack : dim dim dim
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	this.intStack :
	*/

	// pop the position of the {  (body of the method) pushed in block decl
	this.intPtr--;
	// retrieve end position of method declarator

	//statements
	this.realBlockPtr--;
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		this.astPtr -= length;
	}

	//watch for } that could be given as a unicode ! ( u007D is '}' )
	MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
	md.bodyEnd = this.endPosition;
	md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);

	// report the problem and continue the parsing - narrowing the problem onto the method
	if(!this.statementRecoveryActivated) problemReporter().abstractMethodNeedingNoBody(md);
}
protected void consumeLabel() {
	// Do nothing
}
protected void consumeLeftParen() {
	// PushLPAREN ::= '('
	pushOnIntStack(this.lParenPos);
}
protected void consumeLocalVariableDeclaration() {
	// LocalVariableDeclaration ::= Modifiers Type VariableDeclarators ';'

	/*
	this.astStack :
	this.expressionStack: Expression Expression ...... Expression
	this.identifierStack : type  identifier identifier ...... identifier
	this.intStack : typeDim      dim        dim               dim
	 ==>
	this.astStack : FieldDeclaration FieldDeclaration ...... FieldDeclaration
	this.expressionStack :
	this.identifierStack :
	this.intStack :

	*/
	int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];

	// update the this.astStack, this.astPtr and this.astLengthStack
	int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
	System.arraycopy(
		this.astStack,
		startIndex,
		this.astStack,
		startIndex - 1,
		variableDeclaratorsCounter);
	this.astPtr--; // remove the type reference
	this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
	this.variablesCounter[this.nestedType] = 0;
}
protected void consumeLocalVariableDeclarationStatement() {
	// LocalVariableDeclarationStatement ::= LocalVariableDeclaration ';'
	// see blockReal in case of change: duplicated code
	// increment the amount of declared variables for this block
	this.realBlockStack[this.realBlockPtr]++;

	// update source end to include the semi-colon
	int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
	for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
		LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr - i];
		localDeclaration.declarationSourceEnd = this.endStatementPosition;
		localDeclaration.declarationEnd = this.endStatementPosition;	// semi-colon included
	}

}
protected void consumeMarkerAnnotation() {
	// MarkerAnnotation ::= '@' Name
	MarkerAnnotation markerAnnotation = null;

	int oldIndex = this.identifierPtr;

	TypeReference typeReference = getAnnotationType();
	markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[this.intPtr--]);
	markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
	pushOnExpressionStack(markerAnnotation);
	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		problemReporter().invalidUsageOfAnnotation(markerAnnotation);
	}
	this.recordStringLiterals = true;

	if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
		this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(markerAnnotation, oldIndex);
	}
}
protected void consumeMemberValueArrayInitializer() {
	// MemberValueArrayInitializer ::= '{' MemberValues ',' '}'
	// MemberValueArrayInitializer ::= '{' MemberValues '}'
	arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
}
protected void consumeMemberValueAsName() {
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
}
protected void consumeMemberValuePair() {
	// MemberValuePair ::= SimpleName '=' MemberValue
	char[] simpleName = this.identifierStack[this.identifierPtr];
	long position = this.identifierPositionStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	int end = (int) position;
	int start = (int) (position >>> 32);
	Expression value = this.expressionStack[this.expressionPtr--];
	this.expressionLengthPtr--;
	MemberValuePair memberValuePair = new MemberValuePair(simpleName, start, end, value);
	pushOnAstStack(memberValuePair);

	if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
		RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;

		recoveredAnnotation.setKind(RecoveredAnnotation.NORMAL);
	}
}
protected void consumeMemberValuePairs() {
	// MemberValuePairs ::= MemberValuePairs ',' MemberValuePair
	concatNodeLists();
}
protected void consumeMemberValues() {
	// MemberValues ::= MemberValues ',' MemberValue
	concatExpressionLists();
}
protected void consumeMethodBody() {
	// MethodBody ::= NestedMethod '{' BlockStatementsopt '}'
	this.nestedMethod[this.nestedType] --;
}
protected void consumeMethodDeclaration(boolean isNotAbstract) {
	// MethodDeclaration ::= MethodHeader MethodBody
	// AbstractMethodDeclaration ::= MethodHeader ';'

	/*
	this.astStack : modifiers arguments throws statements
	this.identifierStack : type name
	this.intStack : dim dim dim
	 ==>
	this.astStack : MethodDeclaration
	this.identifierStack :
	this.intStack :
	*/

	int length;
	if (isNotAbstract) {
		// pop the position of the {  (body of the method) pushed in block decl
		this.intPtr--;
		this.intPtr--;
	}

	int explicitDeclarations = 0;
	Statement[] statements = null;
	if (isNotAbstract) {
		//statements
		explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
		if (!this.options.ignoreMethodBodies) {
			if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
				System.arraycopy(
					this.astStack,
					(this.astPtr -= length) + 1,
					statements = new Statement[length],
					0,
					length);
			}
		} else {
			length = this.astLengthStack[this.astLengthPtr--];
			this.astPtr -= length;
		}
	}

	// now we know that we have a method declaration at the top of the ast stack
	MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
	md.statements = statements;
	md.explicitDeclarations = explicitDeclarations;

	// cannot be done in consumeMethodHeader because we have no idea whether or not there
	// is a body when we reduce the method header
	if (!isNotAbstract) { //remember the fact that the method has a semicolon body
		md.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
	} else if (!(this.diet && this.dietInt == 0) && statements == null && !containsComment(md.bodyStart, this.endPosition)) {
		md.bits |= ASTNode.UndocumentedEmptyBlock;
	}
	// store the this.endPosition (position just before the '}') in case there is
	// a trailing comment behind the end of the method
	md.bodyEnd = this.endPosition;
	md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
}
protected void consumeMethodHeader() {
	// MethodHeader ::= MethodHeaderName MethodHeaderParameters MethodHeaderExtendedDims ThrowsClauseopt
	// AnnotationMethodHeader ::= AnnotationMethodHeaderName FormalParameterListopt MethodHeaderRightParen MethodHeaderExtendedDims AnnotationMethodHeaderDefaultValueopt
	// RecoveryMethodHeader ::= RecoveryMethodHeaderName FormalParameterListopt MethodHeaderRightParen MethodHeaderExtendedDims AnnotationMethodHeaderDefaultValueopt
	// RecoveryMethodHeader ::= RecoveryMethodHeaderName FormalParameterListopt MethodHeaderRightParen MethodHeaderExtendedDims MethodHeaderThrowsClause

	// retrieve end position of method declarator
	AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];

	if (this.currentToken == TokenNameLBRACE){
		method.bodyStart = this.scanner.currentPosition;
	}
	// recovery
	if (this.currentElement != null){
//		if(method.isAnnotationMethod()) {
//			method.modifiers |= AccSemicolonBody;
//			method.declarationSourceEnd = this.scanner.currentPosition-1;
//			method.bodyEnd = this.scanner.currentPosition-1;
//			this.currentElement = this.currentElement.parent;
//		} else
		if (this.currentToken == TokenNameSEMICOLON /*&& !method.isAnnotationMethod()*/){
			method.modifiers |= ExtraCompilerModifiers.AccSemicolonBody;
			method.declarationSourceEnd = this.scanner.currentPosition-1;
			method.bodyEnd = this.scanner.currentPosition-1;
			if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
				this.currentElement = this.currentElement.parent;
			}
		} else if(this.currentToken == TokenNameLBRACE) {
			if (this.currentElement instanceof RecoveredMethod &&
					((RecoveredMethod)this.currentElement).methodDeclaration != method) {
				this.ignoreNextOpeningBrace = true;
				this.currentElement.bracketBalance++;
			}
		}
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeMethodHeaderDefaultValue() {
	// MethodHeaderDefaultValue ::= DefaultValue
	MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];


	int length = this.expressionLengthStack[this.expressionLengthPtr--];
	if (length == 1) {
		this.intPtr--; // we get rid of the position of the default keyword
		this.intPtr--; // we get rid of the position of the default keyword
		if(md.isAnnotationMethod()) {
			((AnnotationMethodDeclaration)md).defaultValue = this.expressionStack[this.expressionPtr];
			md.modifiers |=  ClassFileConstants.AccAnnotationDefault;
		}
		this.expressionPtr--;
		this.recordStringLiterals = true;
	}

	if(this.currentElement != null) {
		if(md.isAnnotationMethod()) {
			this.currentElement.updateSourceEndIfNecessary(((AnnotationMethodDeclaration)md).defaultValue.sourceEnd);
		}
	}
}
protected void consumeMethodHeaderExtendedDims() {
	// MethodHeaderExtendedDims ::= Dimsopt
	// now we update the returnType of the method
	MethodDeclaration md = (MethodDeclaration) this.astStack[this.astPtr];
	int extendedDims = this.intStack[this.intPtr--];
	if(md.isAnnotationMethod()) {
		((AnnotationMethodDeclaration)md).extendedDimensions = extendedDims;
	}
	if (extendedDims != 0) {
		TypeReference returnType = md.returnType;
		md.sourceEnd = this.endPosition;
		int dims = returnType.dimensions() + extendedDims;
		md.returnType = copyDims(returnType, dims);
		if (this.currentToken == TokenNameLBRACE){
			md.bodyStart = this.endPosition + 1;
		}
		// recovery
		if (this.currentElement != null){
			this.lastCheckPoint = md.bodyStart;
		}
	}
}
protected void consumeMethodHeaderName(boolean isAnnotationMethod) {
	// MethodHeaderName ::= Modifiersopt Type 'Identifier' '('
	// AnnotationMethodHeaderName ::= Modifiersopt Type 'Identifier' '('
	// RecoveryMethodHeaderName ::= Modifiersopt Type 'Identifier' '('
	MethodDeclaration md = null;
	if(isAnnotationMethod) {
		md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
		this.recordStringLiterals = false;
	} else {
		md = new MethodDeclaration(this.compilationUnit.compilationResult);
	}

	//name
	md.selector = this.identifierStack[this.identifierPtr];
	long selectorSource = this.identifierPositionStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	//type
	md.returnType = getTypeReference(this.intStack[this.intPtr--]);
	//modifiers
	md.declarationSourceStart = this.intStack[this.intPtr--];
	md.modifiers = this.intStack[this.intPtr--];
	// consume annotations
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			md.annotations = new Annotation[length],
			0,
			length);
	}
	// javadoc
	md.javadoc = this.javadoc;
	this.javadoc = null;

	//highlight starts at selector start
	md.sourceStart = (int) (selectorSource >>> 32);
	pushOnAstStack(md);
	md.sourceEnd = this.lParenPos;
	md.bodyStart = this.lParenPos+1;
	this.listLength = 0; // initialize this.listLength before reading parameters/throws

	// recovery
	if (this.currentElement != null){
		if (this.currentElement instanceof RecoveredType
			//|| md.modifiers != 0
			|| (Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)
					== Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr))){
			this.lastCheckPoint = md.bodyStart;
			this.currentElement = this.currentElement.add(md, 0);
			this.lastIgnoredToken = -1;
		} else {
			this.lastCheckPoint = md.sourceStart;
			this.restartRecovery = true;
		}
	}
}
protected void consumeMethodHeaderNameWithTypeParameters(boolean isAnnotationMethod) {
	// MethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
	// AnnotationMethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
	// RecoveryMethodHeaderName ::= Modifiersopt TypeParameters Type 'Identifier' '('
	MethodDeclaration md = null;
	if(isAnnotationMethod) {
		md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
		this.recordStringLiterals = false;
	} else {
		md = new MethodDeclaration(this.compilationUnit.compilationResult);
	}

	//name
	md.selector = this.identifierStack[this.identifierPtr];
	long selectorSource = this.identifierPositionStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	//type
	md.returnType = getTypeReference(this.intStack[this.intPtr--]);

	// consume type parameters
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, md.typeParameters = new TypeParameter[length], 0, length);

	//modifiers
	md.declarationSourceStart = this.intStack[this.intPtr--];
	md.modifiers = this.intStack[this.intPtr--];
	// consume annotations
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			md.annotations = new Annotation[length],
			0,
			length);
	}
	// javadoc
	md.javadoc = this.javadoc;
	this.javadoc = null;

	//highlight starts at selector start
	md.sourceStart = (int) (selectorSource >>> 32);
	pushOnAstStack(md);
	md.sourceEnd = this.lParenPos;
	md.bodyStart = this.lParenPos+1;
	this.listLength = 0; // initialize this.listLength before reading parameters/throws

	// recovery
	if (this.currentElement != null){
		boolean isType;
		if ((isType = this.currentElement instanceof RecoveredType)
			//|| md.modifiers != 0
			|| (Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)
					== Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr))){
			if(isType) {
				((RecoveredType) this.currentElement).pendingTypeParameters = null;
			}
			this.lastCheckPoint = md.bodyStart;
			this.currentElement = this.currentElement.add(md, 0);
			this.lastIgnoredToken = -1;
		} else {
			this.lastCheckPoint = md.sourceStart;
			this.restartRecovery = true;
		}
	}
}
protected void consumeMethodHeaderRightParen() {
	// MethodHeaderParameters ::= FormalParameterListopt ')'
	int length = this.astLengthStack[this.astLengthPtr--];
	this.astPtr -= length;
	AbstractMethodDeclaration md = (AbstractMethodDeclaration) this.astStack[this.astPtr];
	md.sourceEnd = 	this.rParenPos;
	//arguments
	if (length != 0) {
		System.arraycopy(
			this.astStack,
			this.astPtr + 1,
			md.arguments = new Argument[length],
			0,
			length);
	}
	md.bodyStart = this.rParenPos+1;
	this.listLength = 0; // reset this.listLength after having read all parameters
	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = md.bodyStart;
		if (this.currentElement.parseTree() == md) return;

		// might not have been attached yet - in some constructor scenarii
		if (md.isConstructor()){
			if ((length != 0)
				|| (this.currentToken == TokenNameLBRACE)
				|| (this.currentToken == TokenNamethrows)){
				this.currentElement = this.currentElement.add(md, 0);
				this.lastIgnoredToken = -1;
			}
		}
	}
}
protected void consumeMethodHeaderThrowsClause() {
	// MethodHeaderThrowsClause ::= 'throws' ClassTypeList
	int length = this.astLengthStack[this.astLengthPtr--];
	this.astPtr -= length;
	AbstractMethodDeclaration md = (AbstractMethodDeclaration) this.astStack[this.astPtr];
	System.arraycopy(
		this.astStack,
		this.astPtr + 1,
		md.thrownExceptions = new TypeReference[length],
		0,
		length);
	md.sourceEnd = md.thrownExceptions[length-1].sourceEnd;
	md.bodyStart = md.thrownExceptions[length-1].sourceEnd + 1;
	this.listLength = 0; // reset this.listLength after having read all thrown exceptions
	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = md.bodyStart;
	}
}
protected void consumeMethodInvocationName() {
	// MethodInvocation ::= Name '(' ArgumentListopt ')'

	// when the name is only an identifier...we have a message send to "this" (implicit)

	MessageSend m = newMessageSend();
	m.sourceEnd = this.rParenPos;
	m.sourceStart =
		(int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
	m.selector = this.identifierStack[this.identifierPtr--];
	if (this.identifierLengthStack[this.identifierLengthPtr] == 1) {
		m.receiver = ThisReference.implicitThis();
		this.identifierLengthPtr--;
	} else {
		this.identifierLengthStack[this.identifierLengthPtr]--;
		m.receiver = getUnspecifiedReference();
		m.sourceStart = m.receiver.sourceStart;
	}
	pushOnExpressionStack(m);
}
protected void consumeMethodInvocationNameWithTypeArguments() {
	// MethodInvocation ::= Name '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'

	// when the name is only an identifier...we have a message send to "this" (implicit)

	MessageSend m = newMessageSendWithTypeArguments();
	m.sourceEnd = this.rParenPos;
	m.sourceStart =
		(int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
	m.selector = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	// handle type arguments
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
	this.intPtr--;

	m.receiver = getUnspecifiedReference();
	m.sourceStart = m.receiver.sourceStart;
	pushOnExpressionStack(m);
}
protected void consumeMethodInvocationPrimary() {
	//optimize the push/pop
	//MethodInvocation ::= Primary '.' 'Identifier' '(' ArgumentListopt ')'

	MessageSend m = newMessageSend();
	m.sourceStart =
		(int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
	m.selector = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	m.receiver = this.expressionStack[this.expressionPtr];
	m.sourceStart = m.receiver.sourceStart;
	m.sourceEnd = this.rParenPos;
	this.expressionStack[this.expressionPtr] = m;
}
protected void consumeMethodInvocationPrimaryWithTypeArguments() {
	//optimize the push/pop
	//MethodInvocation ::= Primary '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'

	MessageSend m = newMessageSendWithTypeArguments();
	m.sourceStart =
		(int) ((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
	m.selector = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	// handle type arguments
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
	this.intPtr--;

	m.receiver = this.expressionStack[this.expressionPtr];
	m.sourceStart = m.receiver.sourceStart;
	m.sourceEnd = this.rParenPos;
	this.expressionStack[this.expressionPtr] = m;
}
protected void consumeMethodInvocationSuper() {
	// MethodInvocation ::= 'super' '.' 'Identifier' '(' ArgumentListopt ')'

	MessageSend m = newMessageSend();
	m.sourceStart = this.intStack[this.intPtr--]; // start position of the super keyword
	m.sourceEnd = this.rParenPos;
	m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
	m.selector = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	m.receiver = new SuperReference(m.sourceStart, this.endPosition);
	pushOnExpressionStack(m);
}
protected void consumeMethodInvocationSuperWithTypeArguments() {
	// MethodInvocation ::= 'super' '.' TypeArguments 'Identifier' '(' ArgumentListopt ')'

	MessageSend m = newMessageSendWithTypeArguments();
	this.intPtr--; // start position of the typeArguments
	m.sourceEnd = this.rParenPos;
	m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
	m.selector = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;

	// handle type arguments
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
	m.sourceStart = this.intStack[this.intPtr--]; // start position of the super keyword

	m.receiver = new SuperReference(m.sourceStart, this.endPosition);
	pushOnExpressionStack(m);
}
protected void consumeModifiers() {
	int savedModifiersSourceStart = this.modifiersSourceStart;
	checkComment(); // might update modifiers with AccDeprecated
	pushOnIntStack(this.modifiers); // modifiers
	if (this.modifiersSourceStart >= savedModifiersSourceStart) {
		this.modifiersSourceStart = savedModifiersSourceStart;
	}
	pushOnIntStack(this.modifiersSourceStart);
	resetModifiers();
}
protected void consumeModifiers2() {
	this.expressionLengthStack[this.expressionLengthPtr - 1] += this.expressionLengthStack[this.expressionLengthPtr--];
}
protected void consumeMultipleResources() {
	// Resources ::= Resources ';' Resource
	concatNodeLists();
}
protected void consumeNameArrayType() {
	pushOnGenericsLengthStack(0); // handle type arguments
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
}
protected void consumeNestedMethod() {
	// NestedMethod ::= $empty
	jumpOverMethodBody();
	this.nestedMethod[this.nestedType] ++;
	pushOnIntStack(this.scanner.currentPosition);
	consumeOpenBlock();
}
protected void consumeNestedType() {
	// NestedType ::= $empty
	int length = this.nestedMethod.length;
	if (++this.nestedType >= length) {
		System.arraycopy(
			this.nestedMethod, 0,
			this.nestedMethod = new int[length + 30], 0,
			length);
		// increase the size of the variablesCounter as well. It has to be consistent with the size of the nestedMethod collection
		System.arraycopy(
			this.variablesCounter, 0,
			this.variablesCounter = new int[length + 30], 0,
			length);
	}
	this.nestedMethod[this.nestedType] = 0;
	this.variablesCounter[this.nestedType] = 0;
}
protected void consumeNormalAnnotation() {
	// NormalAnnotation ::= '@' Name '(' MemberValuePairsopt ')'
	NormalAnnotation normalAnnotation = null;

	int oldIndex = this.identifierPtr;

	TypeReference typeReference = getAnnotationType();
	normalAnnotation = new NormalAnnotation(typeReference, this.intStack[this.intPtr--]);
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		System.arraycopy(
			this.astStack,
			(this.astPtr -= length) + 1,
			normalAnnotation.memberValuePairs = new MemberValuePair[length],
			0,
			length);
	}
	normalAnnotation.declarationSourceEnd = this.rParenPos;
	pushOnExpressionStack(normalAnnotation);

	if(this.currentElement != null) {
		annotationRecoveryCheckPoint(normalAnnotation.sourceStart, normalAnnotation.declarationSourceEnd);

		if (this.currentElement instanceof RecoveredAnnotation) {
			this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(normalAnnotation, oldIndex);
		}
	}

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		problemReporter().invalidUsageOfAnnotation(normalAnnotation);
	}
	this.recordStringLiterals = true;
}
protected void consumeOneDimLoop() {
	// OneDimLoop ::= '[' ']'
	this.dimensions++;
}
protected void consumeOnlySynchronized() {
	// OnlySynchronized ::= 'synchronized'
	pushOnIntStack(this.synchronizedBlockSourceStart);
	resetModifiers();
	this.expressionLengthPtr--;
}
protected void consumeOnlyTypeArguments() {
	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		int length = this.genericsLengthStack[this.genericsLengthPtr];
		problemReporter().invalidUsageOfTypeArguments(
			(TypeReference)this.genericsStack[this.genericsPtr - length + 1],
			(TypeReference)this.genericsStack[this.genericsPtr]);
	}
}
protected void consumeOnlyTypeArgumentsForCastExpression() {
	// OnlyTypeArgumentsForCastExpression ::= OnlyTypeArguments
}
protected void consumeOpenBlock() {
	// OpenBlock ::= $empty

	pushOnIntStack(this.scanner.startPosition);
	int stackLength = this.realBlockStack.length;
	if (++this.realBlockPtr >= stackLength) {
		System.arraycopy(
			this.realBlockStack, 0,
			this.realBlockStack = new int[stackLength + StackIncrement], 0,
			stackLength);
	}
	this.realBlockStack[this.realBlockPtr] = 0;
}
protected void consumePackageComment() {
	// get possible comment for syntax since 1.5
	if(this.options.sourceLevel >= ClassFileConstants.JDK1_5) {
		checkComment();
		resetModifiers();
	}
}
protected void consumePackageDeclaration() {
	// PackageDeclaration ::= 'package' Name ';'
	/* build an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt = this.compilationUnit.currentPackage;
	this.compilationUnit.javadoc = this.javadoc;
	this.javadoc = null;
	// flush comments defined prior to import statements
	impt.declarationEnd = this.endStatementPosition;
	impt.declarationSourceEnd = flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
}
protected void consumePackageDeclarationName() {
	// PackageDeclarationName ::= 'package' Name
	/* build an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt;
	int length;
	char[][] tokens =
		new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr--,
		positions,
		0,
		length);

	impt = new ImportReference(tokens, positions, false, ClassFileConstants.AccDefault);
	this.compilationUnit.currentPackage = impt;

	if (this.currentToken == TokenNameSEMICOLON){
		impt.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		impt.declarationSourceEnd = impt.sourceEnd;
	}
	impt.declarationEnd = impt.declarationSourceEnd;
	//this.endPosition is just before the ;
	impt.declarationSourceStart = this.intStack[this.intPtr--];

	// get possible comment source start
	if(this.javadoc != null) {
		impt.declarationSourceStart = this.javadoc.sourceStart;
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = impt.declarationSourceEnd+1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumePackageDeclarationNameWithModifiers() {
	// PackageDeclarationName ::= Modifiers 'package' Name
	/* build an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt;
	int length;
	char[][] tokens =
		new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
	System.arraycopy(
		this.identifierPositionStack,
		this.identifierPtr--,
		positions,
		0,
		length);

	int packageModifiersSourceStart = this.intStack[this.intPtr--]; // we don't need the modifiers start
	int packageModifiers = this.intStack[this.intPtr--];

	impt = new ImportReference(tokens, positions, false, packageModifiers);
	this.compilationUnit.currentPackage = impt;
	// consume annotations
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		System.arraycopy(
			this.expressionStack,
			(this.expressionPtr -= length) + 1,
			impt.annotations = new Annotation[length],
			0,
			length);
		impt.declarationSourceStart = packageModifiersSourceStart;
		this.intPtr--; // we don't need the position of the 'package keyword
	} else {
		impt.declarationSourceStart = this.intStack[this.intPtr--];
		// get possible comment source start
		if (this.javadoc != null) {
			impt.declarationSourceStart = this.javadoc.sourceStart;
		}
	}

	if (this.currentToken == TokenNameSEMICOLON){
		impt.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		impt.declarationSourceEnd = impt.sourceEnd;
	}
	impt.declarationEnd = impt.declarationSourceEnd;

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = impt.declarationSourceEnd+1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumePostfixExpression() {
	// PostfixExpression ::= Name
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
}
protected void consumePrimaryNoNewArray() {
	// PrimaryNoNewArray ::=  PushLPAREN Expression PushRPAREN
	final Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
	updateSourcePosition(parenthesizedExpression);
	int numberOfParenthesis = (parenthesizedExpression.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
	parenthesizedExpression.bits &= ~ASTNode.ParenthesizedMASK;
	parenthesizedExpression.bits |= (numberOfParenthesis + 1) << ASTNode.ParenthesizedSHIFT;
}
protected void consumePrimaryNoNewArrayArrayType() {
	// PrimaryNoNewArray ::= Name Dims '.' 'class'
	this.intPtr--; // remove the class start position

	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
	pushOnGenericsLengthStack(0);

	pushOnExpressionStack(
		new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(this.intStack[this.intPtr--])));
}
protected void consumePrimaryNoNewArrayName() {
	// PrimaryNoNewArray ::= Name '.' 'class'
	this.intPtr--; // remove the class start position

	// handle type arguments
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
	pushOnGenericsLengthStack(0);
	TypeReference typeReference = getTypeReference(0);

	pushOnExpressionStack(
		new ClassLiteralAccess(this.intStack[this.intPtr--], typeReference));
}
protected void consumePrimaryNoNewArrayNameSuper() {
	// PrimaryNoNewArray ::= Name '.' 'super'
	// handle type arguments
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
	pushOnGenericsLengthStack(0);
	TypeReference typeReference = getTypeReference(0);

	pushOnExpressionStack(
		new QualifiedSuperReference(
			typeReference,
			this.intStack[this.intPtr--],
			this.endPosition));
}
protected void consumePrimaryNoNewArrayNameThis() {
	// PrimaryNoNewArray ::= Name '.' 'this'
	// handle type arguments
	pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
	pushOnGenericsLengthStack(0); // handle type arguments

	TypeReference typeReference = getTypeReference(0);

	pushOnExpressionStack(
		new QualifiedThisReference(
			typeReference,
			this.intStack[this.intPtr--],
			this.endPosition));
}
protected void consumePrimaryNoNewArrayPrimitiveArrayType() {
	// PrimaryNoNewArray ::= PrimitiveType Dims '.' 'class'
	this.intPtr--; // remove the class start position
	pushOnExpressionStack(
		new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(this.intStack[this.intPtr--])));
}
protected void consumePrimaryNoNewArrayPrimitiveType() {
	// PrimaryNoNewArray ::= PrimitiveType '.' 'class'
	this.intPtr--; // remove the class start position
	pushOnExpressionStack(
		new ClassLiteralAccess(this.intStack[this.intPtr--], getTypeReference(0)));
}
protected void consumePrimaryNoNewArrayThis() {
	// PrimaryNoNewArray ::= 'this'
	pushOnExpressionStack(new ThisReference(this.intStack[this.intPtr--], this.endPosition));
}
protected void consumePrimaryNoNewArrayWithName() {
	// PrimaryNoNewArray ::=  PushLPAREN Expression PushRPAREN
	pushOnExpressionStack(getUnspecifiedReferenceOptimized());
	final Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
	updateSourcePosition(parenthesizedExpression);
	int numberOfParenthesis = (parenthesizedExpression.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
	parenthesizedExpression.bits &= ~ASTNode.ParenthesizedMASK;
	parenthesizedExpression.bits |= (numberOfParenthesis + 1) << ASTNode.ParenthesizedSHIFT;
}
protected void consumePrimitiveArrayType() {
	// nothing to do
	// Will be consume by a getTypeRefence call
}
protected void consumePrimitiveType() {
	// Type ::= PrimitiveType
	pushOnIntStack(0);
}
protected void consumePushLeftBrace() {
	pushOnIntStack(this.endPosition); // modifiers
}
protected void consumePushModifiers() {
	pushOnIntStack(this.modifiers); // modifiers
	pushOnIntStack(this.modifiersSourceStart);
	resetModifiers();
	pushOnExpressionStackLengthStack(0);
}
protected void consumePushModifiersForHeader() {
	checkComment(); // might update modifiers with AccDeprecated
	pushOnIntStack(this.modifiers); // modifiers
	pushOnIntStack(this.modifiersSourceStart);
	resetModifiers();
	pushOnExpressionStackLengthStack(0);
}
protected void consumePushPosition() {
	// for source managment purpose
	// PushPosition ::= $empty
	pushOnIntStack(this.endPosition);
}
protected void consumePushRealModifiers() {
	checkComment(); // might update modifiers with AccDeprecated
	pushOnIntStack(this.modifiers); // modifiers
	pushOnIntStack(this.modifiersSourceStart);
	resetModifiers();
}
protected void consumeQualifiedName() {
	// QualifiedName ::= Name '.' SimpleName
	/*back from the recursive loop of QualifiedName.
	Updates identifier length into the length stack*/

	this.identifierLengthStack[--this.identifierLengthPtr]++;
}
protected void consumeRecoveryMethodHeaderName() {
	// this method is call only inside recovery
	boolean isAnnotationMethod = false;
	if(this.currentElement instanceof RecoveredType) {
		isAnnotationMethod = (((RecoveredType)this.currentElement).typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
	} else {
		RecoveredType recoveredType = this.currentElement.enclosingType();
		if(recoveredType != null) {
			isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
		}
	}
	consumeMethodHeaderName(isAnnotationMethod);
}
protected void consumeRecoveryMethodHeaderNameWithTypeParameters() {
	// this method is call only inside recovery
	boolean isAnnotationMethod = false;
	if(this.currentElement instanceof RecoveredType) {
		isAnnotationMethod = (((RecoveredType)this.currentElement).typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
	} else {
		RecoveredType recoveredType = this.currentElement.enclosingType();
		if(recoveredType != null) {
			isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0;
		}
	}
	consumeMethodHeaderNameWithTypeParameters(isAnnotationMethod);
}
protected void consumeReduceImports() {
	// Consume imports
	int length;
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		this.astPtr -= length;
		System.arraycopy(
			this.astStack,
			this.astPtr + 1,
			this.compilationUnit.imports = new ImportReference[length],
			0,
			length);
	}
}
protected void consumeReferenceType() {
	pushOnIntStack(0); // handle array type
}
protected void consumeReferenceType1() {
	pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
}
protected void consumeReferenceType2() {
	pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
}
protected void consumeReferenceType3() {
	pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
}
protected void consumeResourceAsLocalVariableDeclaration() {
	// Resource ::= Type PushModifiers VariableDeclaratorId EnterVariable '=' ForceNoDiet VariableInitializer RestoreDiet ExitVariableWithInitialization
	// Resource ::= Modifiers Type PushRealModifiers VariableDeclaratorId EnterVariable '=' ForceNoDiet VariableInitializer RestoreDiet ExitVariableWithInitialization
	consumeLocalVariableDeclaration();
}
protected void consumeResourceSpecification() {
	// ResourceSpecification ::= '(' Resources ')'
}
protected void consumeResourceOptionalTrailingSemiColon(boolean punctuated) {
	// TrailingSemiColon ::= ';'
	LocalDeclaration localDeclaration = (LocalDeclaration) this.astStack[this.astPtr];
	if (punctuated) {
		localDeclaration.declarationSourceEnd = this.endStatementPosition;
	}
}
protected void consumeRestoreDiet() {
	// RestoreDiet ::= $empty
	this.dietInt--;
}
protected void consumeRightParen() {
	// PushRPAREN ::= ')'
	pushOnIntStack(this.rParenPos);
}
// This method is part of an automatic generation : do NOT edit-modify
protected void consumeRule(int act) {
  switch ( act ) {
    case 30 : if (DEBUG) { System.out.println("Type ::= PrimitiveType"); }  //$NON-NLS-1$
		    consumePrimitiveType();  
			break;
 
    case 44 : if (DEBUG) { System.out.println("ReferenceType ::= ClassOrInterfaceType"); }  //$NON-NLS-1$
		    consumeReferenceType();  
			break;
 
    case 48 : if (DEBUG) { System.out.println("ClassOrInterface ::= Name"); }  //$NON-NLS-1$
		    consumeClassOrInterfaceName();  
			break;
 
    case 49 : if (DEBUG) { System.out.println("ClassOrInterface ::= GenericType DOT Name"); }  //$NON-NLS-1$
		    consumeClassOrInterface();  
			break;
 
    case 50 : if (DEBUG) { System.out.println("GenericType ::= ClassOrInterface TypeArguments"); }  //$NON-NLS-1$
		    consumeGenericType();  
			break;
 
    case 51 : if (DEBUG) { System.out.println("GenericType ::= ClassOrInterface LESS GREATER"); }  //$NON-NLS-1$
		    consumeGenericTypeWithDiamond();  
			break;
 
    case 52 : if (DEBUG) { System.out.println("ArrayTypeWithTypeArgumentsName ::= GenericType DOT Name"); }  //$NON-NLS-1$
		    consumeArrayTypeWithTypeArgumentsName();  
			break;
 
    case 53 : if (DEBUG) { System.out.println("ArrayType ::= PrimitiveType Dims"); }  //$NON-NLS-1$
		    consumePrimitiveArrayType();  
			break;
 
    case 54 : if (DEBUG) { System.out.println("ArrayType ::= Name Dims"); }  //$NON-NLS-1$
		    consumeNameArrayType();  
			break;
 
    case 55 : if (DEBUG) { System.out.println("ArrayType ::= ArrayTypeWithTypeArgumentsName Dims"); }  //$NON-NLS-1$
		    consumeGenericTypeNameArrayType();  
			break;
 
    case 56 : if (DEBUG) { System.out.println("ArrayType ::= GenericType Dims"); }  //$NON-NLS-1$
		    consumeGenericTypeArrayType();  
			break;
 
    case 61 : if (DEBUG) { System.out.println("QualifiedName ::= Name DOT SimpleName"); }  //$NON-NLS-1$
		    consumeQualifiedName();  
			break;
 
    case 62 : if (DEBUG) { System.out.println("CompilationUnit ::= EnterCompilationUnit..."); }  //$NON-NLS-1$
		    consumeCompilationUnit();  
			break;
 
    case 63 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= PackageDeclaration"); }  //$NON-NLS-1$
		    consumeInternalCompilationUnit();  
			break;
 
    case 64 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= PackageDeclaration..."); }  //$NON-NLS-1$
		    consumeInternalCompilationUnit();  
			break;
 
    case 65 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= PackageDeclaration..."); }  //$NON-NLS-1$
		    consumeInternalCompilationUnitWithTypes();  
			break;
 
    case 66 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= PackageDeclaration..."); }  //$NON-NLS-1$
		    consumeInternalCompilationUnitWithTypes();  
			break;
 
    case 67 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= ImportDeclarations..."); }  //$NON-NLS-1$
		    consumeInternalCompilationUnit();  
			break;
 
    case 68 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= TypeDeclarations"); }  //$NON-NLS-1$
		    consumeInternalCompilationUnitWithTypes();  
			break;
 
    case 69 : if (DEBUG) { System.out.println("InternalCompilationUnit ::= ImportDeclarations..."); }  //$NON-NLS-1$
		    consumeInternalCompilationUnitWithTypes();  
			break;
 
    case 70 : if (DEBUG) { System.out.println("InternalCompilationUnit ::="); }  //$NON-NLS-1$
		    consumeEmptyInternalCompilationUnit();  
			break;
 
    case 71 : if (DEBUG) { System.out.println("ReduceImports ::="); }  //$NON-NLS-1$
		    consumeReduceImports();  
			break;
 
    case 72 : if (DEBUG) { System.out.println("EnterCompilationUnit ::="); }  //$NON-NLS-1$
		    consumeEnterCompilationUnit();  
			break;
 
    case 88 : if (DEBUG) { System.out.println("CatchHeader ::= catch LPAREN CatchFormalParameter RPAREN"); }  //$NON-NLS-1$
		    consumeCatchHeader();  
			break;
 
    case 90 : if (DEBUG) { System.out.println("ImportDeclarations ::= ImportDeclarations..."); }  //$NON-NLS-1$
		    consumeImportDeclarations();  
			break;
 
    case 92 : if (DEBUG) { System.out.println("TypeDeclarations ::= TypeDeclarations TypeDeclaration"); }  //$NON-NLS-1$
		    consumeTypeDeclarations();  
			break;
 
    case 93 : if (DEBUG) { System.out.println("PackageDeclaration ::= PackageDeclarationName SEMICOLON"); }  //$NON-NLS-1$
		    consumePackageDeclaration();  
			break;
 
    case 94 : if (DEBUG) { System.out.println("PackageDeclarationName ::= Modifiers package..."); }  //$NON-NLS-1$
		    consumePackageDeclarationNameWithModifiers();  
			break;
 
    case 95 : if (DEBUG) { System.out.println("PackageDeclarationName ::= PackageComment package Name"); }  //$NON-NLS-1$
		    consumePackageDeclarationName();  
			break;
 
    case 96 : if (DEBUG) { System.out.println("PackageComment ::="); }  //$NON-NLS-1$
		    consumePackageComment();  
			break;
 
    case 101 : if (DEBUG) { System.out.println("SingleTypeImportDeclaration ::=..."); }  //$NON-NLS-1$
		    consumeImportDeclaration();  
			break;
 
    case 102 : if (DEBUG) { System.out.println("SingleTypeImportDeclarationName ::= import Name"); }  //$NON-NLS-1$
		    consumeSingleTypeImportDeclarationName();  
			break;
 
    case 103 : if (DEBUG) { System.out.println("TypeImportOnDemandDeclaration ::=..."); }  //$NON-NLS-1$
		    consumeImportDeclaration();  
			break;
 
    case 104 : if (DEBUG) { System.out.println("TypeImportOnDemandDeclarationName ::= import Name DOT..."); }  //$NON-NLS-1$
		    consumeTypeImportOnDemandDeclarationName();  
			break;
 
     case 107 : if (DEBUG) { System.out.println("TypeDeclaration ::= SEMICOLON"); }  //$NON-NLS-1$
		    consumeEmptyTypeDeclaration();  
			break;
 
    case 111 : if (DEBUG) { System.out.println("Modifiers ::= Modifiers Modifier"); }  //$NON-NLS-1$
		    consumeModifiers2();  
			break;
 
    case 123 : if (DEBUG) { System.out.println("Modifier ::= Annotation"); }  //$NON-NLS-1$
		    consumeAnnotationAsModifier();  
			break;
 
    case 124 : if (DEBUG) { System.out.println("ClassDeclaration ::= ClassHeader ClassBody"); }  //$NON-NLS-1$
		    consumeClassDeclaration();  
			break;
 
    case 125 : if (DEBUG) { System.out.println("ClassHeader ::= ClassHeaderName ClassHeaderExtendsopt..."); }  //$NON-NLS-1$
		    consumeClassHeader();  
			break;
 
    case 126 : if (DEBUG) { System.out.println("ClassHeaderName ::= ClassHeaderName1 TypeParameters"); }  //$NON-NLS-1$
		    consumeTypeHeaderNameWithTypeParameters();  
			break;
 
    case 128 : if (DEBUG) { System.out.println("ClassHeaderName1 ::= Modifiersopt class Identifier"); }  //$NON-NLS-1$
		    consumeClassHeaderName1();  
			break;
 
    case 129 : if (DEBUG) { System.out.println("ClassHeaderExtends ::= extends ClassType"); }  //$NON-NLS-1$
		    consumeClassHeaderExtends();  
			break;
 
    case 130 : if (DEBUG) { System.out.println("ClassHeaderImplements ::= implements InterfaceTypeList"); }  //$NON-NLS-1$
		    consumeClassHeaderImplements();  
			break;
 
    case 132 : if (DEBUG) { System.out.println("InterfaceTypeList ::= InterfaceTypeList COMMA..."); }  //$NON-NLS-1$
		    consumeInterfaceTypeList();  
			break;
 
    case 133 : if (DEBUG) { System.out.println("InterfaceType ::= ClassOrInterfaceType"); }  //$NON-NLS-1$
		    consumeInterfaceType();  
			break;
 
    case 136 : if (DEBUG) { System.out.println("ClassBodyDeclarations ::= ClassBodyDeclarations..."); }  //$NON-NLS-1$
		    consumeClassBodyDeclarations();  
			break;
 
    case 140 : if (DEBUG) { System.out.println("ClassBodyDeclaration ::= Diet NestedMethod..."); }  //$NON-NLS-1$
		    consumeClassBodyDeclaration();  
			break;
 
    case 141 : if (DEBUG) { System.out.println("Diet ::="); }  //$NON-NLS-1$
		    consumeDiet();  
			break;

    case 142 : if (DEBUG) { System.out.println("Initializer ::= Diet NestedMethod CreateInitializer..."); }  //$NON-NLS-1$
		    consumeClassBodyDeclaration();  
			break;
 
    case 143 : if (DEBUG) { System.out.println("CreateInitializer ::="); }  //$NON-NLS-1$
		    consumeCreateInitializer();  
			break;

    case 150 : if (DEBUG) { System.out.println("ClassMemberDeclaration ::= SEMICOLON"); }  //$NON-NLS-1$
		    consumeEmptyTypeDeclaration();  
			break;

    case 153 : if (DEBUG) { System.out.println("FieldDeclaration ::= Modifiersopt Type..."); }  //$NON-NLS-1$
		    consumeFieldDeclaration();  
			break;
 
    case 155 : if (DEBUG) { System.out.println("VariableDeclarators ::= VariableDeclarators COMMA..."); }  //$NON-NLS-1$
		    consumeVariableDeclarators();  
			break;
 
    case 158 : if (DEBUG) { System.out.println("EnterVariable ::="); }  //$NON-NLS-1$
		    consumeEnterVariable();  
			break;
 
    case 159 : if (DEBUG) { System.out.println("ExitVariableWithInitialization ::="); }  //$NON-NLS-1$
		    consumeExitVariableWithInitialization();  
			break;
 
    case 160 : if (DEBUG) { System.out.println("ExitVariableWithoutInitialization ::="); }  //$NON-NLS-1$
		    consumeExitVariableWithoutInitialization();  
			break;
 
    case 161 : if (DEBUG) { System.out.println("ForceNoDiet ::="); }  //$NON-NLS-1$
		    consumeForceNoDiet();  
			break;
 
    case 162 : if (DEBUG) { System.out.println("RestoreDiet ::="); }  //$NON-NLS-1$
		    consumeRestoreDiet();  
			break;
 
    case 167 : if (DEBUG) { System.out.println("MethodDeclaration ::= MethodHeader MethodBody"); }  //$NON-NLS-1$
		    // set to true to consume a method with a body
 consumeMethodDeclaration(true);  
			break;
 
    case 168 : if (DEBUG) { System.out.println("AbstractMethodDeclaration ::= MethodHeader SEMICOLON"); }  //$NON-NLS-1$
		    // set to false to consume a method without body
 consumeMethodDeclaration(false);  
			break;
 
    case 169 : if (DEBUG) { System.out.println("MethodHeader ::= MethodHeaderName FormalParameterListopt"); }  //$NON-NLS-1$
		    consumeMethodHeader();  
			break;
 
    case 170 : if (DEBUG) { System.out.println("MethodHeaderName ::= Modifiersopt TypeParameters Type..."); }  //$NON-NLS-1$
		    consumeMethodHeaderNameWithTypeParameters(false);  
			break;
 
    case 171 : if (DEBUG) { System.out.println("MethodHeaderName ::= Modifiersopt Type Identifier LPAREN"); }  //$NON-NLS-1$
		    consumeMethodHeaderName(false);  
			break;
 
    case 172 : if (DEBUG) { System.out.println("MethodHeaderRightParen ::= RPAREN"); }  //$NON-NLS-1$
		    consumeMethodHeaderRightParen();  
			break;
 
    case 173 : if (DEBUG) { System.out.println("MethodHeaderExtendedDims ::= Dimsopt"); }  //$NON-NLS-1$
		    consumeMethodHeaderExtendedDims();  
			break;
 
    case 174 : if (DEBUG) { System.out.println("MethodHeaderThrowsClause ::= throws ClassTypeList"); }  //$NON-NLS-1$
		    consumeMethodHeaderThrowsClause();  
			break;
 
    case 175 : if (DEBUG) { System.out.println("ConstructorHeader ::= ConstructorHeaderName..."); }  //$NON-NLS-1$
		    consumeConstructorHeader();  
			break;
 
    case 176 : if (DEBUG) { System.out.println("ConstructorHeaderName ::= Modifiersopt TypeParameters..."); }  //$NON-NLS-1$
		    consumeConstructorHeaderNameWithTypeParameters();  
			break;
 
    case 177 : if (DEBUG) { System.out.println("ConstructorHeaderName ::= Modifiersopt Identifier LPAREN"); }  //$NON-NLS-1$
		    consumeConstructorHeaderName();  
			break;
 
    case 179 : if (DEBUG) { System.out.println("FormalParameterList ::= FormalParameterList COMMA..."); }  //$NON-NLS-1$
		    consumeFormalParameterList();  
			break;
 
    case 180 : if (DEBUG) { System.out.println("FormalParameter ::= Modifiersopt Type..."); }  //$NON-NLS-1$
		    consumeFormalParameter(false);  
			break;
 
    case 181 : if (DEBUG) { System.out.println("FormalParameter ::= Modifiersopt Type ELLIPSIS..."); }  //$NON-NLS-1$
		    consumeFormalParameter(true);  
			break;
 
    case 182 : if (DEBUG) { System.out.println("CatchFormalParameter ::= Modifiersopt CatchType..."); }  //$NON-NLS-1$
		    consumeCatchFormalParameter();  
			break;
 
    case 183 : if (DEBUG) { System.out.println("CatchType ::= UnionType"); }  //$NON-NLS-1$
		    consumeCatchType();  
			break;
 
    case 184 : if (DEBUG) { System.out.println("UnionType ::= Type"); }  //$NON-NLS-1$
		    consumeUnionTypeAsClassType();  
			break;
 
    case 185 : if (DEBUG) { System.out.println("UnionType ::= UnionType OR Type"); }  //$NON-NLS-1$
		    consumeUnionType();  
			break;
 
    case 187 : if (DEBUG) { System.out.println("ClassTypeList ::= ClassTypeList COMMA ClassTypeElt"); }  //$NON-NLS-1$
		    consumeClassTypeList();  
			break;
 
    case 188 : if (DEBUG) { System.out.println("ClassTypeElt ::= ClassType"); }  //$NON-NLS-1$
		    consumeClassTypeElt();  
			break;
 
    case 189 : if (DEBUG) { System.out.println("MethodBody ::= NestedMethod LBRACE BlockStatementsopt..."); }  //$NON-NLS-1$
		    consumeMethodBody();  
			break;
 
    case 190 : if (DEBUG) { System.out.println("NestedMethod ::="); }  //$NON-NLS-1$
		    consumeNestedMethod();  
			break;
 
    case 191 : if (DEBUG) { System.out.println("StaticInitializer ::= StaticOnly Block"); }  //$NON-NLS-1$
		    consumeStaticInitializer();  
			break;

    case 192 : if (DEBUG) { System.out.println("StaticOnly ::= static"); }  //$NON-NLS-1$
		    consumeStaticOnly();  
			break;
 
    case 193 : if (DEBUG) { System.out.println("ConstructorDeclaration ::= ConstructorHeader MethodBody"); }  //$NON-NLS-1$
		    consumeConstructorDeclaration() ;  
			break;
 
    case 194 : if (DEBUG) { System.out.println("ConstructorDeclaration ::= ConstructorHeader SEMICOLON"); }  //$NON-NLS-1$
		    consumeInvalidConstructorDeclaration() ;  
			break;
 
    case 195 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= this LPAREN..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocation(0, THIS_CALL);  
			break;
 
    case 196 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= OnlyTypeArguments this"); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocationWithTypeArguments(0,THIS_CALL);  
			break;
 
    case 197 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= super LPAREN..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocation(0,SUPER_CALL);  
			break;
 
    case 198 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= OnlyTypeArguments..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocationWithTypeArguments(0,SUPER_CALL);  
			break;
 
    case 199 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Primary DOT super..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocation(1, SUPER_CALL);  
			break;
 
    case 200 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Primary DOT..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocationWithTypeArguments(1, SUPER_CALL);  
			break;
 
    case 201 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Name DOT super LPAREN"); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocation(2, SUPER_CALL);  
			break;
 
    case 202 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Name DOT..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocationWithTypeArguments(2, SUPER_CALL);  
			break;
 
    case 203 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Primary DOT this..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocation(1, THIS_CALL);  
			break;
 
    case 204 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Primary DOT..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocationWithTypeArguments(1, THIS_CALL);  
			break;
 
    case 205 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Name DOT this LPAREN"); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocation(2, THIS_CALL);  
			break;
 
    case 206 : if (DEBUG) { System.out.println("ExplicitConstructorInvocation ::= Name DOT..."); }  //$NON-NLS-1$
		    consumeExplicitConstructorInvocationWithTypeArguments(2, THIS_CALL);  
			break;
 
    case 207 : if (DEBUG) { System.out.println("InterfaceDeclaration ::= InterfaceHeader InterfaceBody"); }  //$NON-NLS-1$
		    consumeInterfaceDeclaration();  
			break;
 
    case 208 : if (DEBUG) { System.out.println("InterfaceHeader ::= InterfaceHeaderName..."); }  //$NON-NLS-1$
		    consumeInterfaceHeader();  
			break;
 
    case 209 : if (DEBUG) { System.out.println("InterfaceHeaderName ::= InterfaceHeaderName1..."); }  //$NON-NLS-1$
		    consumeTypeHeaderNameWithTypeParameters();  
			break;
 
    case 211 : if (DEBUG) { System.out.println("InterfaceHeaderName1 ::= Modifiersopt interface..."); }  //$NON-NLS-1$
		    consumeInterfaceHeaderName1();  
			break;
 
    case 212 : if (DEBUG) { System.out.println("InterfaceHeaderExtends ::= extends InterfaceTypeList"); }  //$NON-NLS-1$
		    consumeInterfaceHeaderExtends();  
			break;
 
    case 215 : if (DEBUG) { System.out.println("InterfaceMemberDeclarations ::=..."); }  //$NON-NLS-1$
		    consumeInterfaceMemberDeclarations();  
			break;
 
    case 216 : if (DEBUG) { System.out.println("InterfaceMemberDeclaration ::= SEMICOLON"); }  //$NON-NLS-1$
		    consumeEmptyTypeDeclaration();  
			break;
 
    case 218 : if (DEBUG) { System.out.println("InterfaceMemberDeclaration ::= MethodHeader MethodBody"); }  //$NON-NLS-1$
		    consumeInvalidMethodDeclaration();  
			break;
 
    case 219 : if (DEBUG) { System.out.println("InvalidConstructorDeclaration ::= ConstructorHeader..."); }  //$NON-NLS-1$
		    consumeInvalidConstructorDeclaration(true);  
			break;
 
    case 220 : if (DEBUG) { System.out.println("InvalidConstructorDeclaration ::= ConstructorHeader..."); }  //$NON-NLS-1$
		    consumeInvalidConstructorDeclaration(false);  
			break;
 
    case 231 : if (DEBUG) { System.out.println("PushLeftBrace ::="); }  //$NON-NLS-1$
		    consumePushLeftBrace();  
			break;
 
    case 232 : if (DEBUG) { System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace ,opt RBRACE"); }  //$NON-NLS-1$
		    consumeEmptyArrayInitializer();  
			break;
 
    case 233 : if (DEBUG) { System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace..."); }  //$NON-NLS-1$
		    consumeArrayInitializer();  
			break;
 
    case 234 : if (DEBUG) { System.out.println("ArrayInitializer ::= LBRACE PushLeftBrace..."); }  //$NON-NLS-1$
		    consumeArrayInitializer();  
			break;
 
    case 236 : if (DEBUG) { System.out.println("VariableInitializers ::= VariableInitializers COMMA..."); }  //$NON-NLS-1$
		    consumeVariableInitializers();  
			break;
 
    case 237 : if (DEBUG) { System.out.println("Block ::= OpenBlock LBRACE BlockStatementsopt RBRACE"); }  //$NON-NLS-1$
		    consumeBlock();  
			break;
 
    case 238 : if (DEBUG) { System.out.println("OpenBlock ::="); }  //$NON-NLS-1$
		    consumeOpenBlock() ;  
			break;
 
    case 240 : if (DEBUG) { System.out.println("BlockStatements ::= BlockStatements BlockStatement"); }  //$NON-NLS-1$
		    consumeBlockStatements() ;  
			break;
 
    case 244 : if (DEBUG) { System.out.println("BlockStatement ::= InterfaceDeclaration"); }  //$NON-NLS-1$
		    consumeInvalidInterfaceDeclaration();  
			break;
 
    case 245 : if (DEBUG) { System.out.println("BlockStatement ::= AnnotationTypeDeclaration"); }  //$NON-NLS-1$
		    consumeInvalidAnnotationTypeDeclaration();  
			break;
 
    case 246 : if (DEBUG) { System.out.println("BlockStatement ::= EnumDeclaration"); }  //$NON-NLS-1$
		    consumeInvalidEnumDeclaration();  
			break;
 
    case 247 : if (DEBUG) { System.out.println("LocalVariableDeclarationStatement ::=..."); }  //$NON-NLS-1$
		    consumeLocalVariableDeclarationStatement();  
			break;
 
    case 248 : if (DEBUG) { System.out.println("LocalVariableDeclaration ::= Type PushModifiers..."); }  //$NON-NLS-1$
		    consumeLocalVariableDeclaration();  
			break;
 
    case 249 : if (DEBUG) { System.out.println("LocalVariableDeclaration ::= Modifiers Type..."); }  //$NON-NLS-1$
		    consumeLocalVariableDeclaration();  
			break;
 
    case 250 : if (DEBUG) { System.out.println("PushModifiers ::="); }  //$NON-NLS-1$
		    consumePushModifiers();  
			break;
 
    case 251 : if (DEBUG) { System.out.println("PushModifiersForHeader ::="); }  //$NON-NLS-1$
		    consumePushModifiersForHeader();  
			break;
 
    case 252 : if (DEBUG) { System.out.println("PushRealModifiers ::="); }  //$NON-NLS-1$
		    consumePushRealModifiers();  
			break;
 
    case 279 : if (DEBUG) { System.out.println("EmptyStatement ::= SEMICOLON"); }  //$NON-NLS-1$
		    consumeEmptyStatement();  
			break;
 
    case 280 : if (DEBUG) { System.out.println("LabeledStatement ::= Label COLON Statement"); }  //$NON-NLS-1$
		    consumeStatementLabel() ;  
			break;
 
    case 281 : if (DEBUG) { System.out.println("LabeledStatementNoShortIf ::= Label COLON..."); }  //$NON-NLS-1$
		    consumeStatementLabel() ;  
			break;
 
    case 282 : if (DEBUG) { System.out.println("Label ::= Identifier"); }  //$NON-NLS-1$
		    consumeLabel() ;  
			break;
 
     case 283 : if (DEBUG) { System.out.println("ExpressionStatement ::= StatementExpression SEMICOLON"); }  //$NON-NLS-1$
		    consumeExpressionStatement();  
			break;
 
    case 292 : if (DEBUG) { System.out.println("IfThenStatement ::= if LPAREN Expression RPAREN..."); }  //$NON-NLS-1$
		    consumeStatementIfNoElse();  
			break;
 
    case 293 : if (DEBUG) { System.out.println("IfThenElseStatement ::= if LPAREN Expression RPAREN..."); }  //$NON-NLS-1$
		    consumeStatementIfWithElse();  
			break;
 
    case 294 : if (DEBUG) { System.out.println("IfThenElseStatementNoShortIf ::= if LPAREN Expression..."); }  //$NON-NLS-1$
		    consumeStatementIfWithElse();  
			break;
 
    case 295 : if (DEBUG) { System.out.println("SwitchStatement ::= switch LPAREN Expression RPAREN..."); }  //$NON-NLS-1$
		    consumeStatementSwitch() ;  
			break;
 
    case 296 : if (DEBUG) { System.out.println("SwitchBlock ::= LBRACE RBRACE"); }  //$NON-NLS-1$
		    consumeEmptySwitchBlock() ;  
			break;
 
    case 299 : if (DEBUG) { System.out.println("SwitchBlock ::= LBRACE SwitchBlockStatements..."); }  //$NON-NLS-1$
		    consumeSwitchBlock() ;  
			break;
 
    case 301 : if (DEBUG) { System.out.println("SwitchBlockStatements ::= SwitchBlockStatements..."); }  //$NON-NLS-1$
		    consumeSwitchBlockStatements() ;  
			break;
 
    case 302 : if (DEBUG) { System.out.println("SwitchBlockStatement ::= SwitchLabels BlockStatements"); }  //$NON-NLS-1$
		    consumeSwitchBlockStatement() ;  
			break;
 
    case 304 : if (DEBUG) { System.out.println("SwitchLabels ::= SwitchLabels SwitchLabel"); }  //$NON-NLS-1$
		    consumeSwitchLabels() ;  
			break;
 
     case 305 : if (DEBUG) { System.out.println("SwitchLabel ::= case ConstantExpression COLON"); }  //$NON-NLS-1$
		    consumeCaseLabel();  
			break;
 
     case 306 : if (DEBUG) { System.out.println("SwitchLabel ::= default COLON"); }  //$NON-NLS-1$
		    consumeDefaultLabel();  
			break;
 
    case 307 : if (DEBUG) { System.out.println("WhileStatement ::= while LPAREN Expression RPAREN..."); }  //$NON-NLS-1$
		    consumeStatementWhile() ;  
			break;
 
    case 308 : if (DEBUG) { System.out.println("WhileStatementNoShortIf ::= while LPAREN Expression..."); }  //$NON-NLS-1$
		    consumeStatementWhile() ;  
			break;
 
    case 309 : if (DEBUG) { System.out.println("DoStatement ::= do Statement while LPAREN Expression..."); }  //$NON-NLS-1$
		    consumeStatementDo() ;  
			break;
 
    case 310 : if (DEBUG) { System.out.println("ForStatement ::= for LPAREN ForInitopt SEMICOLON..."); }  //$NON-NLS-1$
		    consumeStatementFor() ;  
			break;
 
    case 311 : if (DEBUG) { System.out.println("ForStatementNoShortIf ::= for LPAREN ForInitopt..."); }  //$NON-NLS-1$
		    consumeStatementFor() ;  
			break;
 
    case 312 : if (DEBUG) { System.out.println("ForInit ::= StatementExpressionList"); }  //$NON-NLS-1$
		    consumeForInit() ;  
			break;
 
    case 316 : if (DEBUG) { System.out.println("StatementExpressionList ::= StatementExpressionList..."); }  //$NON-NLS-1$
		    consumeStatementExpressionList() ;  
			break;
 
    case 317 : if (DEBUG) { System.out.println("AssertStatement ::= assert Expression SEMICOLON"); }  //$NON-NLS-1$
		    consumeSimpleAssertStatement() ;  
			break;
 
    case 318 : if (DEBUG) { System.out.println("AssertStatement ::= assert Expression COLON Expression"); }  //$NON-NLS-1$
		    consumeAssertStatement() ;  
			break;
 
    case 319 : if (DEBUG) { System.out.println("BreakStatement ::= break SEMICOLON"); }  //$NON-NLS-1$
		    consumeStatementBreak() ;  
			break;
 
    case 320 : if (DEBUG) { System.out.println("BreakStatement ::= break Identifier SEMICOLON"); }  //$NON-NLS-1$
		    consumeStatementBreakWithLabel() ;  
			break;
 
    case 321 : if (DEBUG) { System.out.println("ContinueStatement ::= continue SEMICOLON"); }  //$NON-NLS-1$
		    consumeStatementContinue() ;  
			break;
 
    case 322 : if (DEBUG) { System.out.println("ContinueStatement ::= continue Identifier SEMICOLON"); }  //$NON-NLS-1$
		    consumeStatementContinueWithLabel() ;  
			break;
 
    case 323 : if (DEBUG) { System.out.println("ReturnStatement ::= return Expressionopt SEMICOLON"); }  //$NON-NLS-1$
		    consumeStatementReturn() ;  
			break;
 
    case 324 : if (DEBUG) { System.out.println("ThrowStatement ::= throw Expression SEMICOLON"); }  //$NON-NLS-1$
		    consumeStatementThrow();  
			break;
 
    case 325 : if (DEBUG) { System.out.println("SynchronizedStatement ::= OnlySynchronized LPAREN..."); }  //$NON-NLS-1$
		    consumeStatementSynchronized();  
			break;
 
    case 326 : if (DEBUG) { System.out.println("OnlySynchronized ::= synchronized"); }  //$NON-NLS-1$
		    consumeOnlySynchronized();  
			break;
 
    case 327 : if (DEBUG) { System.out.println("TryStatement ::= try TryBlock Catches"); }  //$NON-NLS-1$
		    consumeStatementTry(false, false);  
			break;
 
    case 328 : if (DEBUG) { System.out.println("TryStatement ::= try TryBlock Catchesopt Finally"); }  //$NON-NLS-1$
		    consumeStatementTry(true, false);  
			break;
 
    case 329 : if (DEBUG) { System.out.println("TryStatementWithResources ::= try ResourceSpecification"); }  //$NON-NLS-1$
		    consumeStatementTry(false, true);  
			break;
 
    case 330 : if (DEBUG) { System.out.println("TryStatementWithResources ::= try ResourceSpecification"); }  //$NON-NLS-1$
		    consumeStatementTry(true, true);  
			break;
 
    case 331 : if (DEBUG) { System.out.println("ResourceSpecification ::= LPAREN Resources ;opt RPAREN"); }  //$NON-NLS-1$
		    consumeResourceSpecification();  
			break;
 
    case 332 : if (DEBUG) { System.out.println(";opt ::="); }  //$NON-NLS-1$
		    consumeResourceOptionalTrailingSemiColon(false);  
			break;
 
    case 333 : if (DEBUG) { System.out.println(";opt ::= SEMICOLON"); }  //$NON-NLS-1$
		    consumeResourceOptionalTrailingSemiColon(true);  
			break;
 
    case 334 : if (DEBUG) { System.out.println("Resources ::= Resource"); }  //$NON-NLS-1$
		    consumeSingleResource();  
			break;
 
    case 335 : if (DEBUG) { System.out.println("Resources ::= Resources TrailingSemiColon Resource"); }  //$NON-NLS-1$
		    consumeMultipleResources();  
			break;
 
    case 336 : if (DEBUG) { System.out.println("TrailingSemiColon ::= SEMICOLON"); }  //$NON-NLS-1$
		    consumeResourceOptionalTrailingSemiColon(true);  
			break;
 
    case 337 : if (DEBUG) { System.out.println("Resource ::= Type PushModifiers VariableDeclaratorId..."); }  //$NON-NLS-1$
		    consumeResourceAsLocalVariableDeclaration();  
			break;
 
    case 338 : if (DEBUG) { System.out.println("Resource ::= Modifiers Type PushRealModifiers..."); }  //$NON-NLS-1$
		    consumeResourceAsLocalVariableDeclaration();  
			break;
 
    case 340 : if (DEBUG) { System.out.println("ExitTryBlock ::="); }  //$NON-NLS-1$
		    consumeExitTryBlock();  
			break;
 
    case 342 : if (DEBUG) { System.out.println("Catches ::= Catches CatchClause"); }  //$NON-NLS-1$
		    consumeCatches();  
			break;
 
    case 343 : if (DEBUG) { System.out.println("CatchClause ::= catch LPAREN CatchFormalParameter RPAREN"); }  //$NON-NLS-1$
		    consumeStatementCatch() ;  
			break;
 
    case 345 : if (DEBUG) { System.out.println("PushLPAREN ::= LPAREN"); }  //$NON-NLS-1$
		    consumeLeftParen();  
			break;
 
    case 346 : if (DEBUG) { System.out.println("PushRPAREN ::= RPAREN"); }  //$NON-NLS-1$
		    consumeRightParen();  
			break;
 
    case 351 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= this"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayThis();  
			break;
 
    case 352 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= PushLPAREN Expression_NotName..."); }  //$NON-NLS-1$
		    consumePrimaryNoNewArray();  
			break;
 
    case 353 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= PushLPAREN Name PushRPAREN"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayWithName();  
			break;
 
    case 356 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= Name DOT this"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayNameThis();  
			break;
 
    case 357 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= Name DOT super"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayNameSuper();  
			break;
 
    case 358 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= Name DOT class"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayName();  
			break;
 
    case 359 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= Name Dims DOT class"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayArrayType();  
			break;
 
    case 360 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= PrimitiveType Dims DOT class"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayPrimitiveArrayType();  
			break;
 
    case 361 : if (DEBUG) { System.out.println("PrimaryNoNewArray ::= PrimitiveType DOT class"); }  //$NON-NLS-1$
		    consumePrimaryNoNewArrayPrimitiveType();  
			break;
 
    case 364 : if (DEBUG) { System.out.println("AllocationHeader ::= new ClassType LPAREN..."); }  //$NON-NLS-1$
		    consumeAllocationHeader();  
			break;
 
    case 365 : if (DEBUG) { System.out.println("ClassInstanceCreationExpression ::= new..."); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpressionWithTypeArguments();  
			break;
 
    case 366 : if (DEBUG) { System.out.println("ClassInstanceCreationExpression ::= new ClassType LPAREN"); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpression();  
			break;
 
    case 367 : if (DEBUG) { System.out.println("ClassInstanceCreationExpression ::= Primary DOT new..."); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() ;  
			break;
 
    case 368 : if (DEBUG) { System.out.println("ClassInstanceCreationExpression ::= Primary DOT new..."); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpressionQualified() ;  
			break;
 
    case 369 : if (DEBUG) { System.out.println("ClassInstanceCreationExpression ::=..."); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpressionQualified() ;  
			break;
 
    case 370 : if (DEBUG) { System.out.println("ClassInstanceCreationExpression ::=..."); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() ;  
			break;
 
    case 371 : if (DEBUG) { System.out.println("ClassInstanceCreationExpressionName ::= Name DOT"); }  //$NON-NLS-1$
		    consumeClassInstanceCreationExpressionName() ;  
			break;
 
    case 372 : if (DEBUG) { System.out.println("UnqualifiedClassBodyopt ::="); }  //$NON-NLS-1$
		    consumeClassBodyopt();  
			break;
 
    case 374 : if (DEBUG) { System.out.println("UnqualifiedEnterAnonymousClassBody ::="); }  //$NON-NLS-1$
		    consumeEnterAnonymousClassBody(false);  
			break;
 
    case 375 : if (DEBUG) { System.out.println("QualifiedClassBodyopt ::="); }  //$NON-NLS-1$
		    consumeClassBodyopt();  
			break;
 
    case 377 : if (DEBUG) { System.out.println("QualifiedEnterAnonymousClassBody ::="); }  //$NON-NLS-1$
		    consumeEnterAnonymousClassBody(true);  
			break;
 
    case 379 : if (DEBUG) { System.out.println("ArgumentList ::= ArgumentList COMMA Expression"); }  //$NON-NLS-1$
		    consumeArgumentList();  
			break;
 
    case 380 : if (DEBUG) { System.out.println("ArrayCreationHeader ::= new PrimitiveType..."); }  //$NON-NLS-1$
		    consumeArrayCreationHeader();  
			break;
 
    case 381 : if (DEBUG) { System.out.println("ArrayCreationHeader ::= new ClassOrInterfaceType..."); }  //$NON-NLS-1$
		    consumeArrayCreationHeader();  
			break;
 
    case 382 : if (DEBUG) { System.out.println("ArrayCreationWithoutArrayInitializer ::= new..."); }  //$NON-NLS-1$
		    consumeArrayCreationExpressionWithoutInitializer();  
			break;
 
    case 383 : if (DEBUG) { System.out.println("ArrayCreationWithArrayInitializer ::= new PrimitiveType"); }  //$NON-NLS-1$
		    consumeArrayCreationExpressionWithInitializer();  
			break;
 
    case 384 : if (DEBUG) { System.out.println("ArrayCreationWithoutArrayInitializer ::= new..."); }  //$NON-NLS-1$
		    consumeArrayCreationExpressionWithoutInitializer();  
			break;
 
    case 385 : if (DEBUG) { System.out.println("ArrayCreationWithArrayInitializer ::= new..."); }  //$NON-NLS-1$
		    consumeArrayCreationExpressionWithInitializer();  
			break;
 
    case 387 : if (DEBUG) { System.out.println("DimWithOrWithOutExprs ::= DimWithOrWithOutExprs..."); }  //$NON-NLS-1$
		    consumeDimWithOrWithOutExprs();  
			break;
 
     case 389 : if (DEBUG) { System.out.println("DimWithOrWithOutExpr ::= LBRACKET RBRACKET"); }  //$NON-NLS-1$
		    consumeDimWithOrWithOutExpr();  
			break;
 
     case 390 : if (DEBUG) { System.out.println("Dims ::= DimsLoop"); }  //$NON-NLS-1$
		    consumeDims();  
			break;
 
     case 393 : if (DEBUG) { System.out.println("OneDimLoop ::= LBRACKET RBRACKET"); }  //$NON-NLS-1$
		    consumeOneDimLoop();  
			break;
 
    case 394 : if (DEBUG) { System.out.println("FieldAccess ::= Primary DOT Identifier"); }  //$NON-NLS-1$
		    consumeFieldAccess(false);  
			break;
 
    case 395 : if (DEBUG) { System.out.println("FieldAccess ::= super DOT Identifier"); }  //$NON-NLS-1$
		    consumeFieldAccess(true);  
			break;
 
    case 396 : if (DEBUG) { System.out.println("MethodInvocation ::= Name LPAREN ArgumentListopt RPAREN"); }  //$NON-NLS-1$
		    consumeMethodInvocationName();  
			break;
 
    case 397 : if (DEBUG) { System.out.println("MethodInvocation ::= Name DOT OnlyTypeArguments..."); }  //$NON-NLS-1$
		    consumeMethodInvocationNameWithTypeArguments();  
			break;
 
    case 398 : if (DEBUG) { System.out.println("MethodInvocation ::= Primary DOT OnlyTypeArguments..."); }  //$NON-NLS-1$
		    consumeMethodInvocationPrimaryWithTypeArguments();  
			break;
 
    case 399 : if (DEBUG) { System.out.println("MethodInvocation ::= Primary DOT Identifier LPAREN..."); }  //$NON-NLS-1$
		    consumeMethodInvocationPrimary();  
			break;
 
    case 400 : if (DEBUG) { System.out.println("MethodInvocation ::= super DOT OnlyTypeArguments..."); }  //$NON-NLS-1$
		    consumeMethodInvocationSuperWithTypeArguments();  
			break;
 
    case 401 : if (DEBUG) { System.out.println("MethodInvocation ::= super DOT Identifier LPAREN..."); }  //$NON-NLS-1$
		    consumeMethodInvocationSuper();  
			break;
 
    case 402 : if (DEBUG) { System.out.println("ArrayAccess ::= Name LBRACKET Expression RBRACKET"); }  //$NON-NLS-1$
		    consumeArrayAccess(true);  
			break;
 
    case 403 : if (DEBUG) { System.out.println("ArrayAccess ::= PrimaryNoNewArray LBRACKET Expression..."); }  //$NON-NLS-1$
		    consumeArrayAccess(false);  
			break;
 
    case 404 : if (DEBUG) { System.out.println("ArrayAccess ::= ArrayCreationWithArrayInitializer..."); }  //$NON-NLS-1$
		    consumeArrayAccess(false);  
			break;
 
    case 406 : if (DEBUG) { System.out.println("PostfixExpression ::= Name"); }  //$NON-NLS-1$
		    consumePostfixExpression();  
			break;
 
    case 409 : if (DEBUG) { System.out.println("PostIncrementExpression ::= PostfixExpression PLUS_PLUS"); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.PLUS,true);  
			break;
 
    case 410 : if (DEBUG) { System.out.println("PostDecrementExpression ::= PostfixExpression..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.MINUS,true);  
			break;
 
    case 411 : if (DEBUG) { System.out.println("PushPosition ::="); }  //$NON-NLS-1$
		    consumePushPosition();  
			break;
 
    case 414 : if (DEBUG) { System.out.println("UnaryExpression ::= PLUS PushPosition UnaryExpression"); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.PLUS);  
			break;
 
    case 415 : if (DEBUG) { System.out.println("UnaryExpression ::= MINUS PushPosition UnaryExpression"); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.MINUS);  
			break;
 
    case 417 : if (DEBUG) { System.out.println("PreIncrementExpression ::= PLUS_PLUS PushPosition..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.PLUS,false);  
			break;
 
    case 418 : if (DEBUG) { System.out.println("PreDecrementExpression ::= MINUS_MINUS PushPosition..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.MINUS,false);  
			break;
 
    case 420 : if (DEBUG) { System.out.println("UnaryExpressionNotPlusMinus ::= TWIDDLE PushPosition..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.TWIDDLE);  
			break;
 
    case 421 : if (DEBUG) { System.out.println("UnaryExpressionNotPlusMinus ::= NOT PushPosition..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.NOT);  
			break;
 
    case 423 : if (DEBUG) { System.out.println("CastExpression ::= PushLPAREN PrimitiveType Dimsopt..."); }  //$NON-NLS-1$
		    consumeCastExpressionWithPrimitiveType();  
			break;
 
    case 424 : if (DEBUG) { System.out.println("CastExpression ::= PushLPAREN Name..."); }  //$NON-NLS-1$
		    consumeCastExpressionWithGenericsArray();  
			break;
 
    case 425 : if (DEBUG) { System.out.println("CastExpression ::= PushLPAREN Name..."); }  //$NON-NLS-1$
		    consumeCastExpressionWithQualifiedGenericsArray();  
			break;
 
    case 426 : if (DEBUG) { System.out.println("CastExpression ::= PushLPAREN Name PushRPAREN..."); }  //$NON-NLS-1$
		    consumeCastExpressionLL1();  
			break;
 
    case 427 : if (DEBUG) { System.out.println("CastExpression ::= PushLPAREN Name Dims PushRPAREN..."); }  //$NON-NLS-1$
		    consumeCastExpressionWithNameArray();  
			break;
 
    case 428 : if (DEBUG) { System.out.println("OnlyTypeArgumentsForCastExpression ::= OnlyTypeArguments"); }  //$NON-NLS-1$
		    consumeOnlyTypeArgumentsForCastExpression();  
			break;
 
    case 429 : if (DEBUG) { System.out.println("InsideCastExpression ::="); }  //$NON-NLS-1$
		    consumeInsideCastExpression();  
			break;
 
    case 430 : if (DEBUG) { System.out.println("InsideCastExpressionLL1 ::="); }  //$NON-NLS-1$
		    consumeInsideCastExpressionLL1();  
			break;
 
    case 431 : if (DEBUG) { System.out.println("InsideCastExpressionWithQualifiedGenerics ::="); }  //$NON-NLS-1$
		    consumeInsideCastExpressionWithQualifiedGenerics();  
			break;
 
    case 433 : if (DEBUG) { System.out.println("MultiplicativeExpression ::= MultiplicativeExpression..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.MULTIPLY);  
			break;
 
    case 434 : if (DEBUG) { System.out.println("MultiplicativeExpression ::= MultiplicativeExpression..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.DIVIDE);  
			break;
 
    case 435 : if (DEBUG) { System.out.println("MultiplicativeExpression ::= MultiplicativeExpression..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.REMAINDER);  
			break;
 
    case 437 : if (DEBUG) { System.out.println("AdditiveExpression ::= AdditiveExpression PLUS..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.PLUS);  
			break;
 
    case 438 : if (DEBUG) { System.out.println("AdditiveExpression ::= AdditiveExpression MINUS..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.MINUS);  
			break;
 
    case 440 : if (DEBUG) { System.out.println("ShiftExpression ::= ShiftExpression LEFT_SHIFT..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.LEFT_SHIFT);  
			break;
 
    case 441 : if (DEBUG) { System.out.println("ShiftExpression ::= ShiftExpression RIGHT_SHIFT..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.RIGHT_SHIFT);  
			break;
 
    case 442 : if (DEBUG) { System.out.println("ShiftExpression ::= ShiftExpression UNSIGNED_RIGHT_SHIFT"); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.UNSIGNED_RIGHT_SHIFT);  
			break;
 
    case 444 : if (DEBUG) { System.out.println("RelationalExpression ::= RelationalExpression LESS..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.LESS);  
			break;
 
    case 445 : if (DEBUG) { System.out.println("RelationalExpression ::= RelationalExpression GREATER..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.GREATER);  
			break;
 
    case 446 : if (DEBUG) { System.out.println("RelationalExpression ::= RelationalExpression LESS_EQUAL"); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.LESS_EQUAL);  
			break;
 
    case 447 : if (DEBUG) { System.out.println("RelationalExpression ::= RelationalExpression..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.GREATER_EQUAL);  
			break;
 
    case 449 : if (DEBUG) { System.out.println("InstanceofExpression ::= InstanceofExpression instanceof"); }  //$NON-NLS-1$
		    consumeInstanceOfExpression();  
			break;
 
    case 451 : if (DEBUG) { System.out.println("EqualityExpression ::= EqualityExpression EQUAL_EQUAL..."); }  //$NON-NLS-1$
		    consumeEqualityExpression(OperatorIds.EQUAL_EQUAL);  
			break;
 
    case 452 : if (DEBUG) { System.out.println("EqualityExpression ::= EqualityExpression NOT_EQUAL..."); }  //$NON-NLS-1$
		    consumeEqualityExpression(OperatorIds.NOT_EQUAL);  
			break;
 
    case 454 : if (DEBUG) { System.out.println("AndExpression ::= AndExpression AND EqualityExpression"); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.AND);  
			break;
 
    case 456 : if (DEBUG) { System.out.println("ExclusiveOrExpression ::= ExclusiveOrExpression XOR..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.XOR);  
			break;
 
    case 458 : if (DEBUG) { System.out.println("InclusiveOrExpression ::= InclusiveOrExpression OR..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.OR);  
			break;
 
    case 460 : if (DEBUG) { System.out.println("ConditionalAndExpression ::= ConditionalAndExpression..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.AND_AND);  
			break;
 
    case 462 : if (DEBUG) { System.out.println("ConditionalOrExpression ::= ConditionalOrExpression..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.OR_OR);  
			break;
 
    case 464 : if (DEBUG) { System.out.println("ConditionalExpression ::= ConditionalOrExpression..."); }  //$NON-NLS-1$
		    consumeConditionalExpression(OperatorIds.QUESTIONCOLON) ;  
			break;
 
    case 467 : if (DEBUG) { System.out.println("Assignment ::= PostfixExpression AssignmentOperator..."); }  //$NON-NLS-1$
		    consumeAssignment();  
			break;
 
    case 469 : if (DEBUG) { System.out.println("Assignment ::= InvalidArrayInitializerAssignement"); }  //$NON-NLS-1$
		    ignoreExpressionAssignment(); 
			break;
 
    case 470 : if (DEBUG) { System.out.println("AssignmentOperator ::= EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(EQUAL);  
			break;
 
    case 471 : if (DEBUG) { System.out.println("AssignmentOperator ::= MULTIPLY_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(MULTIPLY);  
			break;
 
    case 472 : if (DEBUG) { System.out.println("AssignmentOperator ::= DIVIDE_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(DIVIDE);  
			break;
 
    case 473 : if (DEBUG) { System.out.println("AssignmentOperator ::= REMAINDER_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(REMAINDER);  
			break;
 
    case 474 : if (DEBUG) { System.out.println("AssignmentOperator ::= PLUS_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(PLUS);  
			break;
 
    case 475 : if (DEBUG) { System.out.println("AssignmentOperator ::= MINUS_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(MINUS);  
			break;
 
    case 476 : if (DEBUG) { System.out.println("AssignmentOperator ::= LEFT_SHIFT_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(LEFT_SHIFT);  
			break;
 
    case 477 : if (DEBUG) { System.out.println("AssignmentOperator ::= RIGHT_SHIFT_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(RIGHT_SHIFT);  
			break;
 
    case 478 : if (DEBUG) { System.out.println("AssignmentOperator ::= UNSIGNED_RIGHT_SHIFT_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(UNSIGNED_RIGHT_SHIFT);  
			break;
 
    case 479 : if (DEBUG) { System.out.println("AssignmentOperator ::= AND_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(AND);  
			break;
 
    case 480 : if (DEBUG) { System.out.println("AssignmentOperator ::= XOR_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(XOR);  
			break;
 
    case 481 : if (DEBUG) { System.out.println("AssignmentOperator ::= OR_EQUAL"); }  //$NON-NLS-1$
		    consumeAssignmentOperator(OR);  
			break;
 
    case 485 : if (DEBUG) { System.out.println("Expressionopt ::="); }  //$NON-NLS-1$
		    consumeEmptyExpression();  
			break;
 
    case 490 : if (DEBUG) { System.out.println("ClassBodyDeclarationsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyClassBodyDeclarationsopt();  
			break;
 
    case 491 : if (DEBUG) { System.out.println("ClassBodyDeclarationsopt ::= NestedType..."); }  //$NON-NLS-1$
		    consumeClassBodyDeclarationsopt();  
			break;
 
     case 492 : if (DEBUG) { System.out.println("Modifiersopt ::="); }  //$NON-NLS-1$
		    consumeDefaultModifiers();  
			break;
 
    case 493 : if (DEBUG) { System.out.println("Modifiersopt ::= Modifiers"); }  //$NON-NLS-1$
		    consumeModifiers();  
			break;
 
    case 494 : if (DEBUG) { System.out.println("BlockStatementsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyBlockStatementsopt();  
			break;
 
     case 496 : if (DEBUG) { System.out.println("Dimsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyDimsopt();  
			break;
 
     case 498 : if (DEBUG) { System.out.println("ArgumentListopt ::="); }  //$NON-NLS-1$
		    consumeEmptyArgumentListopt();  
			break;
 
    case 502 : if (DEBUG) { System.out.println("FormalParameterListopt ::="); }  //$NON-NLS-1$
		    consumeFormalParameterListopt();  
			break;
 
     case 506 : if (DEBUG) { System.out.println("InterfaceMemberDeclarationsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyInterfaceMemberDeclarationsopt();  
			break;
 
     case 507 : if (DEBUG) { System.out.println("InterfaceMemberDeclarationsopt ::= NestedType..."); }  //$NON-NLS-1$
		    consumeInterfaceMemberDeclarationsopt();  
			break;
 
    case 508 : if (DEBUG) { System.out.println("NestedType ::="); }  //$NON-NLS-1$
		    consumeNestedType();  
			break;

     case 509 : if (DEBUG) { System.out.println("ForInitopt ::="); }  //$NON-NLS-1$
		    consumeEmptyForInitopt();  
			break;
 
     case 511 : if (DEBUG) { System.out.println("ForUpdateopt ::="); }  //$NON-NLS-1$
		    consumeEmptyForUpdateopt();  
			break;
 
     case 515 : if (DEBUG) { System.out.println("Catchesopt ::="); }  //$NON-NLS-1$
		    consumeEmptyCatchesopt();  
			break;
 
     case 517 : if (DEBUG) { System.out.println("EnumDeclaration ::= EnumHeader EnumBody"); }  //$NON-NLS-1$
		    consumeEnumDeclaration();  
			break;
 
     case 518 : if (DEBUG) { System.out.println("EnumHeader ::= EnumHeaderName ClassHeaderImplementsopt"); }  //$NON-NLS-1$
		    consumeEnumHeader();  
			break;
 
     case 519 : if (DEBUG) { System.out.println("EnumHeaderName ::= Modifiersopt enum Identifier"); }  //$NON-NLS-1$
		    consumeEnumHeaderName();  
			break;
 
     case 520 : if (DEBUG) { System.out.println("EnumHeaderName ::= Modifiersopt enum Identifier..."); }  //$NON-NLS-1$
		    consumeEnumHeaderNameWithTypeParameters();  
			break;
 
     case 521 : if (DEBUG) { System.out.println("EnumBody ::= LBRACE EnumBodyDeclarationsopt RBRACE"); }  //$NON-NLS-1$
		    consumeEnumBodyNoConstants();  
			break;
 
     case 522 : if (DEBUG) { System.out.println("EnumBody ::= LBRACE COMMA EnumBodyDeclarationsopt..."); }  //$NON-NLS-1$
		    consumeEnumBodyNoConstants();  
			break;
 
     case 523 : if (DEBUG) { System.out.println("EnumBody ::= LBRACE EnumConstants COMMA..."); }  //$NON-NLS-1$
		    consumeEnumBodyWithConstants();  
			break;
 
     case 524 : if (DEBUG) { System.out.println("EnumBody ::= LBRACE EnumConstants..."); }  //$NON-NLS-1$
		    consumeEnumBodyWithConstants();  
			break;
 
    case 526 : if (DEBUG) { System.out.println("EnumConstants ::= EnumConstants COMMA EnumConstant"); }  //$NON-NLS-1$
		    consumeEnumConstants();  
			break;
 
    case 527 : if (DEBUG) { System.out.println("EnumConstantHeaderName ::= Modifiersopt Identifier"); }  //$NON-NLS-1$
		    consumeEnumConstantHeaderName();  
			break;
 
    case 528 : if (DEBUG) { System.out.println("EnumConstantHeader ::= EnumConstantHeaderName..."); }  //$NON-NLS-1$
		    consumeEnumConstantHeader();  
			break;
 
    case 529 : if (DEBUG) { System.out.println("EnumConstant ::= EnumConstantHeader ForceNoDiet..."); }  //$NON-NLS-1$
		    consumeEnumConstantWithClassBody();  
			break;
 
    case 530 : if (DEBUG) { System.out.println("EnumConstant ::= EnumConstantHeader"); }  //$NON-NLS-1$
		    consumeEnumConstantNoClassBody();  
			break;
 
    case 531 : if (DEBUG) { System.out.println("Arguments ::= LPAREN ArgumentListopt RPAREN"); }  //$NON-NLS-1$
		    consumeArguments();  
			break;
 
    case 532 : if (DEBUG) { System.out.println("Argumentsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyArguments();  
			break;
 
    case 534 : if (DEBUG) { System.out.println("EnumDeclarations ::= SEMICOLON ClassBodyDeclarationsopt"); }  //$NON-NLS-1$
		    consumeEnumDeclarations();  
			break;
 
    case 535 : if (DEBUG) { System.out.println("EnumBodyDeclarationsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyEnumDeclarations();  
			break;
 
    case 537 : if (DEBUG) { System.out.println("EnhancedForStatement ::= EnhancedForStatementHeader..."); }  //$NON-NLS-1$
		    consumeEnhancedForStatement();  
			break;
 
    case 538 : if (DEBUG) { System.out.println("EnhancedForStatementNoShortIf ::=..."); }  //$NON-NLS-1$
		    consumeEnhancedForStatement();  
			break;
 
    case 539 : if (DEBUG) { System.out.println("EnhancedForStatementHeaderInit ::= for LPAREN Type..."); }  //$NON-NLS-1$
		    consumeEnhancedForStatementHeaderInit(false);  
			break;
 
    case 540 : if (DEBUG) { System.out.println("EnhancedForStatementHeaderInit ::= for LPAREN Modifiers"); }  //$NON-NLS-1$
		    consumeEnhancedForStatementHeaderInit(true);  
			break;
 
    case 541 : if (DEBUG) { System.out.println("EnhancedForStatementHeader ::=..."); }  //$NON-NLS-1$
		    consumeEnhancedForStatementHeader();  
			break;
 
    case 542 : if (DEBUG) { System.out.println("SingleStaticImportDeclaration ::=..."); }  //$NON-NLS-1$
		    consumeImportDeclaration();  
			break;
 
    case 543 : if (DEBUG) { System.out.println("SingleStaticImportDeclarationName ::= import static Name"); }  //$NON-NLS-1$
		    consumeSingleStaticImportDeclarationName();  
			break;
 
    case 544 : if (DEBUG) { System.out.println("StaticImportOnDemandDeclaration ::=..."); }  //$NON-NLS-1$
		    consumeImportDeclaration();  
			break;
 
    case 545 : if (DEBUG) { System.out.println("StaticImportOnDemandDeclarationName ::= import static..."); }  //$NON-NLS-1$
		    consumeStaticImportOnDemandDeclarationName();  
			break;
 
    case 546 : if (DEBUG) { System.out.println("TypeArguments ::= LESS TypeArgumentList1"); }  //$NON-NLS-1$
		    consumeTypeArguments();  
			break;
 
    case 547 : if (DEBUG) { System.out.println("OnlyTypeArguments ::= LESS TypeArgumentList1"); }  //$NON-NLS-1$
		    consumeOnlyTypeArguments();  
			break;
 
    case 549 : if (DEBUG) { System.out.println("TypeArgumentList1 ::= TypeArgumentList COMMA..."); }  //$NON-NLS-1$
		    consumeTypeArgumentList1();  
			break;
 
    case 551 : if (DEBUG) { System.out.println("TypeArgumentList ::= TypeArgumentList COMMA TypeArgument"); }  //$NON-NLS-1$
		    consumeTypeArgumentList();  
			break;
 
    case 552 : if (DEBUG) { System.out.println("TypeArgument ::= ReferenceType"); }  //$NON-NLS-1$
		    consumeTypeArgument();  
			break;
 
    case 556 : if (DEBUG) { System.out.println("ReferenceType1 ::= ReferenceType GREATER"); }  //$NON-NLS-1$
		    consumeReferenceType1();  
			break;
 
    case 557 : if (DEBUG) { System.out.println("ReferenceType1 ::= ClassOrInterface LESS..."); }  //$NON-NLS-1$
		    consumeTypeArgumentReferenceType1();  
			break;
 
    case 559 : if (DEBUG) { System.out.println("TypeArgumentList2 ::= TypeArgumentList COMMA..."); }  //$NON-NLS-1$
		    consumeTypeArgumentList2();  
			break;
 
    case 562 : if (DEBUG) { System.out.println("ReferenceType2 ::= ReferenceType RIGHT_SHIFT"); }  //$NON-NLS-1$
		    consumeReferenceType2();  
			break;
 
    case 563 : if (DEBUG) { System.out.println("ReferenceType2 ::= ClassOrInterface LESS..."); }  //$NON-NLS-1$
		    consumeTypeArgumentReferenceType2();  
			break;
 
    case 565 : if (DEBUG) { System.out.println("TypeArgumentList3 ::= TypeArgumentList COMMA..."); }  //$NON-NLS-1$
		    consumeTypeArgumentList3();  
			break;
 
    case 568 : if (DEBUG) { System.out.println("ReferenceType3 ::= ReferenceType UNSIGNED_RIGHT_SHIFT"); }  //$NON-NLS-1$
		    consumeReferenceType3();  
			break;
 
    case 569 : if (DEBUG) { System.out.println("Wildcard ::= QUESTION"); }  //$NON-NLS-1$
		    consumeWildcard();  
			break;
 
    case 570 : if (DEBUG) { System.out.println("Wildcard ::= QUESTION WildcardBounds"); }  //$NON-NLS-1$
		    consumeWildcardWithBounds();  
			break;
 
    case 571 : if (DEBUG) { System.out.println("WildcardBounds ::= extends ReferenceType"); }  //$NON-NLS-1$
		    consumeWildcardBoundsExtends();  
			break;
 
    case 572 : if (DEBUG) { System.out.println("WildcardBounds ::= super ReferenceType"); }  //$NON-NLS-1$
		    consumeWildcardBoundsSuper();  
			break;
 
    case 573 : if (DEBUG) { System.out.println("Wildcard1 ::= QUESTION GREATER"); }  //$NON-NLS-1$
		    consumeWildcard1();  
			break;
 
    case 574 : if (DEBUG) { System.out.println("Wildcard1 ::= QUESTION WildcardBounds1"); }  //$NON-NLS-1$
		    consumeWildcard1WithBounds();  
			break;
 
    case 575 : if (DEBUG) { System.out.println("WildcardBounds1 ::= extends ReferenceType1"); }  //$NON-NLS-1$
		    consumeWildcardBounds1Extends();  
			break;
 
    case 576 : if (DEBUG) { System.out.println("WildcardBounds1 ::= super ReferenceType1"); }  //$NON-NLS-1$
		    consumeWildcardBounds1Super();  
			break;
 
    case 577 : if (DEBUG) { System.out.println("Wildcard2 ::= QUESTION RIGHT_SHIFT"); }  //$NON-NLS-1$
		    consumeWildcard2();  
			break;
 
    case 578 : if (DEBUG) { System.out.println("Wildcard2 ::= QUESTION WildcardBounds2"); }  //$NON-NLS-1$
		    consumeWildcard2WithBounds();  
			break;
 
    case 579 : if (DEBUG) { System.out.println("WildcardBounds2 ::= extends ReferenceType2"); }  //$NON-NLS-1$
		    consumeWildcardBounds2Extends();  
			break;
 
    case 580 : if (DEBUG) { System.out.println("WildcardBounds2 ::= super ReferenceType2"); }  //$NON-NLS-1$
		    consumeWildcardBounds2Super();  
			break;
 
    case 581 : if (DEBUG) { System.out.println("Wildcard3 ::= QUESTION UNSIGNED_RIGHT_SHIFT"); }  //$NON-NLS-1$
		    consumeWildcard3();  
			break;
 
    case 582 : if (DEBUG) { System.out.println("Wildcard3 ::= QUESTION WildcardBounds3"); }  //$NON-NLS-1$
		    consumeWildcard3WithBounds();  
			break;
 
    case 583 : if (DEBUG) { System.out.println("WildcardBounds3 ::= extends ReferenceType3"); }  //$NON-NLS-1$
		    consumeWildcardBounds3Extends();  
			break;
 
    case 584 : if (DEBUG) { System.out.println("WildcardBounds3 ::= super ReferenceType3"); }  //$NON-NLS-1$
		    consumeWildcardBounds3Super();  
			break;
 
    case 585 : if (DEBUG) { System.out.println("TypeParameterHeader ::= Identifier"); }  //$NON-NLS-1$
		    consumeTypeParameterHeader();  
			break;
 
    case 586 : if (DEBUG) { System.out.println("TypeParameters ::= LESS TypeParameterList1"); }  //$NON-NLS-1$
		    consumeTypeParameters();  
			break;
 
    case 588 : if (DEBUG) { System.out.println("TypeParameterList ::= TypeParameterList COMMA..."); }  //$NON-NLS-1$
		    consumeTypeParameterList();  
			break;
 
    case 590 : if (DEBUG) { System.out.println("TypeParameter ::= TypeParameterHeader extends..."); }  //$NON-NLS-1$
		    consumeTypeParameterWithExtends();  
			break;
 
    case 591 : if (DEBUG) { System.out.println("TypeParameter ::= TypeParameterHeader extends..."); }  //$NON-NLS-1$
		    consumeTypeParameterWithExtendsAndBounds();  
			break;
 
    case 593 : if (DEBUG) { System.out.println("AdditionalBoundList ::= AdditionalBoundList..."); }  //$NON-NLS-1$
		    consumeAdditionalBoundList();  
			break;
 
    case 594 : if (DEBUG) { System.out.println("AdditionalBound ::= AND ReferenceType"); }  //$NON-NLS-1$
		    consumeAdditionalBound();  
			break;
 
    case 596 : if (DEBUG) { System.out.println("TypeParameterList1 ::= TypeParameterList COMMA..."); }  //$NON-NLS-1$
		    consumeTypeParameterList1();  
			break;
 
    case 597 : if (DEBUG) { System.out.println("TypeParameter1 ::= TypeParameterHeader GREATER"); }  //$NON-NLS-1$
		    consumeTypeParameter1();  
			break;
 
    case 598 : if (DEBUG) { System.out.println("TypeParameter1 ::= TypeParameterHeader extends..."); }  //$NON-NLS-1$
		    consumeTypeParameter1WithExtends();  
			break;
 
    case 599 : if (DEBUG) { System.out.println("TypeParameter1 ::= TypeParameterHeader extends..."); }  //$NON-NLS-1$
		    consumeTypeParameter1WithExtendsAndBounds();  
			break;
 
    case 601 : if (DEBUG) { System.out.println("AdditionalBoundList1 ::= AdditionalBoundList..."); }  //$NON-NLS-1$
		    consumeAdditionalBoundList1();  
			break;
 
    case 602 : if (DEBUG) { System.out.println("AdditionalBound1 ::= AND ReferenceType1"); }  //$NON-NLS-1$
		    consumeAdditionalBound1();  
			break;
 
    case 608 : if (DEBUG) { System.out.println("UnaryExpression_NotName ::= PLUS PushPosition..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.PLUS);  
			break;
 
    case 609 : if (DEBUG) { System.out.println("UnaryExpression_NotName ::= MINUS PushPosition..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.MINUS);  
			break;
 
    case 612 : if (DEBUG) { System.out.println("UnaryExpressionNotPlusMinus_NotName ::= TWIDDLE..."); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.TWIDDLE);  
			break;
 
    case 613 : if (DEBUG) { System.out.println("UnaryExpressionNotPlusMinus_NotName ::= NOT PushPosition"); }  //$NON-NLS-1$
		    consumeUnaryExpression(OperatorIds.NOT);  
			break;
 
    case 616 : if (DEBUG) { System.out.println("MultiplicativeExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.MULTIPLY);  
			break;
 
    case 617 : if (DEBUG) { System.out.println("MultiplicativeExpression_NotName ::= Name MULTIPLY..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.MULTIPLY);  
			break;
 
    case 618 : if (DEBUG) { System.out.println("MultiplicativeExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.DIVIDE);  
			break;
 
    case 619 : if (DEBUG) { System.out.println("MultiplicativeExpression_NotName ::= Name DIVIDE..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.DIVIDE);  
			break;
 
    case 620 : if (DEBUG) { System.out.println("MultiplicativeExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.REMAINDER);  
			break;
 
    case 621 : if (DEBUG) { System.out.println("MultiplicativeExpression_NotName ::= Name REMAINDER..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.REMAINDER);  
			break;
 
    case 623 : if (DEBUG) { System.out.println("AdditiveExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.PLUS);  
			break;
 
    case 624 : if (DEBUG) { System.out.println("AdditiveExpression_NotName ::= Name PLUS..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.PLUS);  
			break;
 
    case 625 : if (DEBUG) { System.out.println("AdditiveExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.MINUS);  
			break;
 
    case 626 : if (DEBUG) { System.out.println("AdditiveExpression_NotName ::= Name MINUS..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.MINUS);  
			break;
 
    case 628 : if (DEBUG) { System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.LEFT_SHIFT);  
			break;
 
    case 629 : if (DEBUG) { System.out.println("ShiftExpression_NotName ::= Name LEFT_SHIFT..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.LEFT_SHIFT);  
			break;
 
    case 630 : if (DEBUG) { System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.RIGHT_SHIFT);  
			break;
 
    case 631 : if (DEBUG) { System.out.println("ShiftExpression_NotName ::= Name RIGHT_SHIFT..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.RIGHT_SHIFT);  
			break;
 
    case 632 : if (DEBUG) { System.out.println("ShiftExpression_NotName ::= ShiftExpression_NotName..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.UNSIGNED_RIGHT_SHIFT);  
			break;
 
    case 633 : if (DEBUG) { System.out.println("ShiftExpression_NotName ::= Name UNSIGNED_RIGHT_SHIFT..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.UNSIGNED_RIGHT_SHIFT);  
			break;
 
    case 635 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::= ShiftExpression_NotName"); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.LESS);  
			break;
 
    case 636 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::= Name LESS..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.LESS);  
			break;
 
    case 637 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::= ShiftExpression_NotName"); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.GREATER);  
			break;
 
    case 638 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::= Name GREATER..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.GREATER);  
			break;
 
    case 639 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.LESS_EQUAL);  
			break;
 
    case 640 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::= Name LESS_EQUAL..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.LESS_EQUAL);  
			break;
 
    case 641 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.GREATER_EQUAL);  
			break;
 
    case 642 : if (DEBUG) { System.out.println("RelationalExpression_NotName ::= Name GREATER_EQUAL..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.GREATER_EQUAL);  
			break;
 
    case 644 : if (DEBUG) { System.out.println("InstanceofExpression_NotName ::= Name instanceof..."); }  //$NON-NLS-1$
		    consumeInstanceOfExpressionWithName();  
			break;
 
    case 645 : if (DEBUG) { System.out.println("InstanceofExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeInstanceOfExpression();  
			break;
 
    case 647 : if (DEBUG) { System.out.println("EqualityExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeEqualityExpression(OperatorIds.EQUAL_EQUAL);  
			break;
 
    case 648 : if (DEBUG) { System.out.println("EqualityExpression_NotName ::= Name EQUAL_EQUAL..."); }  //$NON-NLS-1$
		    consumeEqualityExpressionWithName(OperatorIds.EQUAL_EQUAL);  
			break;
 
    case 649 : if (DEBUG) { System.out.println("EqualityExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeEqualityExpression(OperatorIds.NOT_EQUAL);  
			break;
 
    case 650 : if (DEBUG) { System.out.println("EqualityExpression_NotName ::= Name NOT_EQUAL..."); }  //$NON-NLS-1$
		    consumeEqualityExpressionWithName(OperatorIds.NOT_EQUAL);  
			break;
 
    case 652 : if (DEBUG) { System.out.println("AndExpression_NotName ::= AndExpression_NotName AND..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.AND);  
			break;
 
    case 653 : if (DEBUG) { System.out.println("AndExpression_NotName ::= Name AND EqualityExpression"); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.AND);  
			break;
 
    case 655 : if (DEBUG) { System.out.println("ExclusiveOrExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.XOR);  
			break;
 
    case 656 : if (DEBUG) { System.out.println("ExclusiveOrExpression_NotName ::= Name XOR AndExpression"); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.XOR);  
			break;
 
    case 658 : if (DEBUG) { System.out.println("InclusiveOrExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.OR);  
			break;
 
    case 659 : if (DEBUG) { System.out.println("InclusiveOrExpression_NotName ::= Name OR..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.OR);  
			break;
 
    case 661 : if (DEBUG) { System.out.println("ConditionalAndExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.AND_AND);  
			break;
 
    case 662 : if (DEBUG) { System.out.println("ConditionalAndExpression_NotName ::= Name AND_AND..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.AND_AND);  
			break;
 
    case 664 : if (DEBUG) { System.out.println("ConditionalOrExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeBinaryExpression(OperatorIds.OR_OR);  
			break;
 
    case 665 : if (DEBUG) { System.out.println("ConditionalOrExpression_NotName ::= Name OR_OR..."); }  //$NON-NLS-1$
		    consumeBinaryExpressionWithName(OperatorIds.OR_OR);  
			break;
 
    case 667 : if (DEBUG) { System.out.println("ConditionalExpression_NotName ::=..."); }  //$NON-NLS-1$
		    consumeConditionalExpression(OperatorIds.QUESTIONCOLON) ;  
			break;
 
    case 668 : if (DEBUG) { System.out.println("ConditionalExpression_NotName ::= Name QUESTION..."); }  //$NON-NLS-1$
		    consumeConditionalExpressionWithName(OperatorIds.QUESTIONCOLON) ;  
			break;
 
    case 672 : if (DEBUG) { System.out.println("AnnotationTypeDeclarationHeaderName ::= Modifiers AT..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeDeclarationHeaderName() ;  
			break;
 
    case 673 : if (DEBUG) { System.out.println("AnnotationTypeDeclarationHeaderName ::= Modifiers AT..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters() ;  
			break;
 
    case 674 : if (DEBUG) { System.out.println("AnnotationTypeDeclarationHeaderName ::= AT..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters() ;  
			break;
 
    case 675 : if (DEBUG) { System.out.println("AnnotationTypeDeclarationHeaderName ::= AT..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeDeclarationHeaderName() ;  
			break;
 
    case 676 : if (DEBUG) { System.out.println("AnnotationTypeDeclarationHeader ::=..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeDeclarationHeader() ;  
			break;
 
    case 677 : if (DEBUG) { System.out.println("AnnotationTypeDeclaration ::=..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeDeclaration() ;  
			break;
 
    case 679 : if (DEBUG) { System.out.println("AnnotationTypeMemberDeclarationsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyAnnotationTypeMemberDeclarationsopt() ;  
			break;
 
    case 680 : if (DEBUG) { System.out.println("AnnotationTypeMemberDeclarationsopt ::= NestedType..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeMemberDeclarationsopt() ;  
			break;
 
    case 682 : if (DEBUG) { System.out.println("AnnotationTypeMemberDeclarations ::=..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeMemberDeclarations() ;  
			break;
 
    case 683 : if (DEBUG) { System.out.println("AnnotationMethodHeaderName ::= Modifiersopt..."); }  //$NON-NLS-1$
		    consumeMethodHeaderNameWithTypeParameters(true);  
			break;
 
    case 684 : if (DEBUG) { System.out.println("AnnotationMethodHeaderName ::= Modifiersopt Type..."); }  //$NON-NLS-1$
		    consumeMethodHeaderName(true);  
			break;
 
    case 685 : if (DEBUG) { System.out.println("AnnotationMethodHeaderDefaultValueopt ::="); }  //$NON-NLS-1$
		    consumeEmptyMethodHeaderDefaultValue() ;  
			break;
 
    case 686 : if (DEBUG) { System.out.println("AnnotationMethodHeaderDefaultValueopt ::= DefaultValue"); }  //$NON-NLS-1$
		    consumeMethodHeaderDefaultValue();  
			break;
 
    case 687 : if (DEBUG) { System.out.println("AnnotationMethodHeader ::= AnnotationMethodHeaderName..."); }  //$NON-NLS-1$
		    consumeMethodHeader();  
			break;
 
    case 688 : if (DEBUG) { System.out.println("AnnotationTypeMemberDeclaration ::=..."); }  //$NON-NLS-1$
		    consumeAnnotationTypeMemberDeclaration() ;  
			break;
 
    case 696 : if (DEBUG) { System.out.println("AnnotationName ::= AT Name"); }  //$NON-NLS-1$
		    consumeAnnotationName() ;  
			break;
 
    case 697 : if (DEBUG) { System.out.println("NormalAnnotation ::= AnnotationName LPAREN..."); }  //$NON-NLS-1$
		    consumeNormalAnnotation() ;  
			break;
 
    case 698 : if (DEBUG) { System.out.println("MemberValuePairsopt ::="); }  //$NON-NLS-1$
		    consumeEmptyMemberValuePairsopt() ;  
			break;
 
    case 701 : if (DEBUG) { System.out.println("MemberValuePairs ::= MemberValuePairs COMMA..."); }  //$NON-NLS-1$
		    consumeMemberValuePairs() ;  
			break;
 
    case 702 : if (DEBUG) { System.out.println("MemberValuePair ::= SimpleName EQUAL EnterMemberValue..."); }  //$NON-NLS-1$
		    consumeMemberValuePair() ;  
			break;
 
    case 703 : if (DEBUG) { System.out.println("EnterMemberValue ::="); }  //$NON-NLS-1$
		    consumeEnterMemberValue() ;  
			break;
 
    case 704 : if (DEBUG) { System.out.println("ExitMemberValue ::="); }  //$NON-NLS-1$
		    consumeExitMemberValue() ;  
			break;
 
    case 706 : if (DEBUG) { System.out.println("MemberValue ::= Name"); }  //$NON-NLS-1$
		    consumeMemberValueAsName() ;  
			break;
 
    case 709 : if (DEBUG) { System.out.println("MemberValueArrayInitializer ::=..."); }  //$NON-NLS-1$
		    consumeMemberValueArrayInitializer() ;  
			break;
 
    case 710 : if (DEBUG) { System.out.println("MemberValueArrayInitializer ::=..."); }  //$NON-NLS-1$
		    consumeMemberValueArrayInitializer() ;  
			break;
 
    case 711 : if (DEBUG) { System.out.println("MemberValueArrayInitializer ::=..."); }  //$NON-NLS-1$
		    consumeEmptyMemberValueArrayInitializer() ;  
			break;
 
    case 712 : if (DEBUG) { System.out.println("MemberValueArrayInitializer ::=..."); }  //$NON-NLS-1$
		    consumeEmptyMemberValueArrayInitializer() ;  
			break;
 
    case 713 : if (DEBUG) { System.out.println("EnterMemberValueArrayInitializer ::="); }  //$NON-NLS-1$
		    consumeEnterMemberValueArrayInitializer() ;  
			break;
 
    case 715 : if (DEBUG) { System.out.println("MemberValues ::= MemberValues COMMA MemberValue"); }  //$NON-NLS-1$
		    consumeMemberValues() ;  
			break;
 
    case 716 : if (DEBUG) { System.out.println("MarkerAnnotation ::= AnnotationName"); }  //$NON-NLS-1$
		    consumeMarkerAnnotation() ;  
			break;
 
    case 717 : if (DEBUG) { System.out.println("SingleMemberAnnotationMemberValue ::= MemberValue"); }  //$NON-NLS-1$
		    consumeSingleMemberAnnotationMemberValue() ;  
			break;
 
    case 718 : if (DEBUG) { System.out.println("SingleMemberAnnotation ::= AnnotationName LPAREN..."); }  //$NON-NLS-1$
		    consumeSingleMemberAnnotation() ;  
			break;
 
    case 719 : if (DEBUG) { System.out.println("RecoveryMethodHeaderName ::= Modifiersopt TypeParameters"); }  //$NON-NLS-1$
		    consumeRecoveryMethodHeaderNameWithTypeParameters();  
			break;
 
    case 720 : if (DEBUG) { System.out.println("RecoveryMethodHeaderName ::= Modifiersopt Type..."); }  //$NON-NLS-1$
		    consumeRecoveryMethodHeaderName();  
			break;
 
    case 721 : if (DEBUG) { System.out.println("RecoveryMethodHeader ::= RecoveryMethodHeaderName..."); }  //$NON-NLS-1$
		    consumeMethodHeader();  
			break;
 
    case 722 : if (DEBUG) { System.out.println("RecoveryMethodHeader ::= RecoveryMethodHeaderName..."); }  //$NON-NLS-1$
		    consumeMethodHeader();  
			break;
 
	}
}
protected void consumeSimpleAssertStatement() {
	// AssertStatement ::= 'assert' Expression ';'
	this.expressionLengthPtr--;
	pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
}
protected void consumeSingleMemberAnnotation() {
	// SingleMemberAnnotation ::= '@' Name '(' MemberValue ')'
	SingleMemberAnnotation singleMemberAnnotation = null;

	int oldIndex = this.identifierPtr;

	TypeReference typeReference = getAnnotationType();
	singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[this.intPtr--]);
	singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
	this.expressionLengthPtr--;
	singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
	pushOnExpressionStack(singleMemberAnnotation);


	if(this.currentElement != null) {
		annotationRecoveryCheckPoint(singleMemberAnnotation.sourceStart, singleMemberAnnotation.declarationSourceEnd);

		if (this.currentElement instanceof RecoveredAnnotation) {
			this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(singleMemberAnnotation, oldIndex);
		}
	}

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
	}
	this.recordStringLiterals = true;
}
protected void consumeSingleMemberAnnotationMemberValue() {
	// this rule is used for syntax recovery only
	if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
		RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;

		recoveredAnnotation.setKind(RecoveredAnnotation.SINGLE_MEMBER);
	}

}
protected void consumeSingleResource() {
	// Resources ::= Resource
}
protected void consumeSingleStaticImportDeclarationName() {
	// SingleTypeImportDeclarationName ::= 'import' 'static' Name
	/* push an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt;
	int length;
	char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
	System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
	pushOnAstStack(impt = new ImportReference(tokens, positions, false, ClassFileConstants.AccStatic));

	this.modifiers = ClassFileConstants.AccDefault;
	this.modifiersSourceStart = -1; // <-- see comment into modifiersFlag(int)

	if (this.currentToken == TokenNameSEMICOLON){
		impt.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		impt.declarationSourceEnd = impt.sourceEnd;
	}
	impt.declarationEnd = impt.declarationSourceEnd;
	//this.endPosition is just before the ;
	impt.declarationSourceStart = this.intStack[this.intPtr--];

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		impt.modifiers = ClassFileConstants.AccDefault; // convert the static import reference to a non-static importe reference
		problemReporter().invalidUsageOfStaticImports(impt);
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = impt.declarationSourceEnd+1;
		this.currentElement = this.currentElement.add(impt, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeSingleTypeImportDeclarationName() {
	// SingleTypeImportDeclarationName ::= 'import' Name
	/* push an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt;
	int length;
	char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
	System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
	pushOnAstStack(impt = new ImportReference(tokens, positions, false, ClassFileConstants.AccDefault));

	if (this.currentToken == TokenNameSEMICOLON){
		impt.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		impt.declarationSourceEnd = impt.sourceEnd;
	}
	impt.declarationEnd = impt.declarationSourceEnd;
	//this.endPosition is just before the ;
	impt.declarationSourceStart = this.intStack[this.intPtr--];

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = impt.declarationSourceEnd+1;
		this.currentElement = this.currentElement.add(impt, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeStatementBreak() {
	// BreakStatement ::= 'break' ';'
	// break pushs a position on this.intStack in case there is no label

	pushOnAstStack(new BreakStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));

	if (this.pendingRecoveredType != null) {
		// Used only in statements recovery.
		// This is not a real break statement but a placeholder for an existing local type.
		// The break statement must be replace by the local type.
		if (this.pendingRecoveredType.allocation == null &&
				this.endPosition <= this.pendingRecoveredType.declarationSourceEnd) {
			this.astStack[this.astPtr] = this.pendingRecoveredType;
			this.pendingRecoveredType = null;
			return;
		}
		this.pendingRecoveredType = null;
	}
}
protected void consumeStatementBreakWithLabel() {
	// BreakStatement ::= 'break' Identifier ';'
	// break pushs a position on this.intStack in case there is no label

	pushOnAstStack(
		new BreakStatement(
			this.identifierStack[this.identifierPtr--],
			this.intStack[this.intPtr--],
			this.endStatementPosition));
	this.identifierLengthPtr--;
}
protected void consumeStatementCatch() {
	// CatchClause ::= 'catch' '(' FormalParameter ')'    Block

	//catch are stored directly into the Try
	//has they always comes two by two....
	//we remove one entry from the astlengthPtr.
	//The construction of the try statement must
	//then fetch the catches using  2*i and 2*i + 1

	this.astLengthPtr--;
	this.listLength = 0; // reset formalParameter counter (incremented for catch variable)
}
protected void consumeStatementContinue() {
	// ContinueStatement ::= 'continue' ';'
	// continue pushs a position on this.intStack in case there is no label

	pushOnAstStack(
		new ContinueStatement(
			null,
			this.intStack[this.intPtr--],
			this.endStatementPosition));
}
protected void consumeStatementContinueWithLabel() {
	// ContinueStatement ::= 'continue' Identifier ';'
	// continue pushs a position on this.intStack in case there is no label

	pushOnAstStack(
		new ContinueStatement(
			this.identifierStack[this.identifierPtr--],
			this.intStack[this.intPtr--],
			this.endStatementPosition));
	this.identifierLengthPtr--;
}
protected void consumeStatementDo() {
	// DoStatement ::= 'do' Statement 'while' '(' Expression ')' ';'

	//the 'while' pushes a value on this.intStack that we need to remove
	this.intPtr--;

	Statement statement = (Statement) this.astStack[this.astPtr];
	this.expressionLengthPtr--;
	this.astStack[this.astPtr] =
		new DoStatement(
			this.expressionStack[this.expressionPtr--],
			statement,
			this.intStack[this.intPtr--],
			this.endStatementPosition);
}
protected void consumeStatementExpressionList() {
	// StatementExpressionList ::= StatementExpressionList ',' StatementExpression
	concatExpressionLists();
}
protected void consumeStatementFor() {
	// ForStatement ::= 'for' '(' ForInitopt ';' Expressionopt ';' ForUpdateopt ')' Statement
	// ForStatementNoShortIf ::= 'for' '(' ForInitopt ';' Expressionopt ';' ForUpdateopt ')' StatementNoShortIf

	int length;
	Expression cond = null;
	Statement[] inits, updates;
	boolean scope = true;

	//statements
	this.astLengthPtr--;
	Statement statement = (Statement) this.astStack[this.astPtr--];

	//updates are on the expresion stack
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) == 0) {
		updates = null;
	} else {
		this.expressionPtr -= length;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			updates = new Statement[length],
			0,
			length);
	}

	if (this.expressionLengthStack[this.expressionLengthPtr--] != 0)
		cond = this.expressionStack[this.expressionPtr--];

	//inits may be on two different stacks
	if ((length = this.astLengthStack[this.astLengthPtr--]) == 0) {
		inits = null;
		scope = false;
	} else {
		if (length == -1) { //on this.expressionStack
			scope = false;
			length = this.expressionLengthStack[this.expressionLengthPtr--];
			this.expressionPtr -= length;
			System.arraycopy(
				this.expressionStack,
				this.expressionPtr + 1,
				inits = new Statement[length],
				0,
				length);
		} else { //on this.astStack
			this.astPtr -= length;
			System.arraycopy(
				this.astStack,
				this.astPtr + 1,
				inits = new Statement[length],
				0,
				length);
		}
	}
	pushOnAstStack(
		new ForStatement(
			inits,
			cond,
			updates,
			statement,
			scope,
			this.intStack[this.intPtr--],
			this.endStatementPosition));
}
protected void consumeStatementIfNoElse() {
	// IfThenStatement ::=  'if' '(' Expression ')' Statement

	//optimize the push/pop
	this.expressionLengthPtr--;
	Statement thenStatement = (Statement) this.astStack[this.astPtr];
	this.astStack[this.astPtr] =
		new IfStatement(
			this.expressionStack[this.expressionPtr--],
			thenStatement,
			this.intStack[this.intPtr--],
			this.endStatementPosition);
}
protected void consumeStatementIfWithElse() {
	// IfThenElseStatement ::=  'if' '(' Expression ')' StatementNoShortIf 'else' Statement
	// IfThenElseStatementNoShortIf ::=  'if' '(' Expression ')' StatementNoShortIf 'else' StatementNoShortIf

	this.expressionLengthPtr--;

	// optimized {..., Then, Else } ==> {..., If }
	this.astLengthPtr--;

	//optimize the push/pop
	this.astStack[--this.astPtr] =
		new IfStatement(
			this.expressionStack[this.expressionPtr--],
			(Statement) this.astStack[this.astPtr],
			(Statement) this.astStack[this.astPtr + 1],
			this.intStack[this.intPtr--],
			this.endStatementPosition);
}
protected void consumeStatementLabel() {
	// LabeledStatement ::= 'Identifier' ':' Statement
	// LabeledStatementNoShortIf ::= 'Identifier' ':' StatementNoShortIf

	//optimize push/pop
	Statement statement = (Statement) this.astStack[this.astPtr];
	this.astStack[this.astPtr] =
		new LabeledStatement(
			this.identifierStack[this.identifierPtr],
			statement,
			this.identifierPositionStack[this.identifierPtr--],
			this.endStatementPosition);
	this.identifierLengthPtr--;
}
protected void consumeStatementReturn() {
	// ReturnStatement ::= 'return' Expressionopt ';'
	// return pushs a position on this.intStack in case there is no expression

	if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
		pushOnAstStack(
			new ReturnStatement(
				this.expressionStack[this.expressionPtr--],
				this.intStack[this.intPtr--],
				this.endStatementPosition)
		);
	} else {
		pushOnAstStack(new ReturnStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
	}
}
protected void consumeStatementSwitch() {
	// SwitchStatement ::= 'switch' OpenBlock '(' Expression ')' SwitchBlock

	//OpenBlock just makes the semantic action blockStart()
	//the block is inlined but a scope need to be created
	//if some declaration occurs.

	int length;
	SwitchStatement switchStatement = new SwitchStatement();
	this.expressionLengthPtr--;
	switchStatement.expression = this.expressionStack[this.expressionPtr--];
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		this.astPtr -= length;
		System.arraycopy(
			this.astStack,
			this.astPtr + 1,
			switchStatement.statements = new Statement[length],
			0,
			length);
	}
	switchStatement.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
	pushOnAstStack(switchStatement);
	switchStatement.blockStart = this.intStack[this.intPtr--];
	switchStatement.sourceStart = this.intStack[this.intPtr--];
	switchStatement.sourceEnd = this.endStatementPosition;
	if (length == 0 && !containsComment(switchStatement.blockStart, switchStatement.sourceEnd)) {
		switchStatement.bits |= ASTNode.UndocumentedEmptyBlock;
	}
}
protected void consumeStatementSynchronized() {
	// SynchronizedStatement ::= OnlySynchronized '(' Expression ')' Block
	//optimize the push/pop

	if (this.astLengthStack[this.astLengthPtr] == 0) {
		this.astLengthStack[this.astLengthPtr] = 1;
		this.expressionLengthPtr--;
		this.astStack[++this.astPtr] =
			new SynchronizedStatement(
				this.expressionStack[this.expressionPtr--],
				null,
				this.intStack[this.intPtr--],
				this.endStatementPosition);
	} else {
		this.expressionLengthPtr--;
		this.astStack[this.astPtr] =
			new SynchronizedStatement(
				this.expressionStack[this.expressionPtr--],
				(Block) this.astStack[this.astPtr],
				this.intStack[this.intPtr--],
				this.endStatementPosition);
	}
	resetModifiers();
}
protected void consumeStatementThrow() {
	// ThrowStatement ::= 'throw' Expression ';'
	this.expressionLengthPtr--;
	pushOnAstStack(new ThrowStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
}
protected void consumeStatementTry(boolean withFinally, boolean hasResources) {
	// TryStatement ::= 'try'  Block Catches
	// TryStatement ::= 'try'  Block Catchesopt Finally
	// TryStatementWithResources ::= 'try' ResourceSpecification TryBlock Catchesopt
	// TryStatementWithResources ::= 'try' ResourceSpecification TryBlock Catchesopt Finally
	
	int length;
	TryStatement tryStmt = new TryStatement();
	//finally
	if (withFinally) {
		this.astLengthPtr--;
		tryStmt.finallyBlock = (Block) this.astStack[this.astPtr--];
	}
	//catches are handle by two <argument-block> [see statementCatch]
	if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		if (length == 1) {
			tryStmt.catchBlocks = new Block[] {(Block) this.astStack[this.astPtr--]};
			tryStmt.catchArguments = new Argument[] {(Argument) this.astStack[this.astPtr--]};
		} else {
			Block[] bks = (tryStmt.catchBlocks = new Block[length]);
			Argument[] args = (tryStmt.catchArguments = new Argument[length]);
			while (length-- > 0) {
				bks[length] = (Block) this.astStack[this.astPtr--];
				args[length] = (Argument) this.astStack[this.astPtr--];
			}
		}
	}
	//try
	this.astLengthPtr--;
	tryStmt.tryBlock = (Block) this.astStack[this.astPtr--];

	if (hasResources) {
		// get the resources
		length = this.astLengthStack[this.astLengthPtr--];
		LocalDeclaration[] resources = new LocalDeclaration[length];
		System.arraycopy(
				this.astStack,
				(this.astPtr -= length) + 1,
				resources,
				0,
				length);
		tryStmt.resources = resources;
		if (this.options.sourceLevel < ClassFileConstants.JDK1_7) {
			problemReporter().autoManagedResourcesNotBelow17(resources);
		}
	}
	//positions
	tryStmt.sourceEnd = this.endStatementPosition;
	tryStmt.sourceStart = this.intStack[this.intPtr--];
	pushOnAstStack(tryStmt);
}
protected void consumeStatementWhile() {
	// WhileStatement ::= 'while' '(' Expression ')' Statement
	// WhileStatementNoShortIf ::= 'while' '(' Expression ')' StatementNoShortIf

	this.expressionLengthPtr--;
	Statement statement = (Statement) this.astStack[this.astPtr];
	this.astStack[this.astPtr] =
		new WhileStatement(
			this.expressionStack[this.expressionPtr--],
			statement,
			this.intStack[this.intPtr--],
			this.endStatementPosition);
}
protected void consumeStaticImportOnDemandDeclarationName() {
	// TypeImportOnDemandDeclarationName ::= 'import' 'static' Name '.' '*'
	/* push an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt;
	int length;
	char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
	System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
	pushOnAstStack(impt = new ImportReference(tokens, positions, true, ClassFileConstants.AccStatic));

	// star end position
	impt.trailingStarPosition = this.intStack[this.intPtr--];
	this.modifiers = ClassFileConstants.AccDefault;
	this.modifiersSourceStart = -1; // <-- see comment into modifiersFlag(int)

	if (this.currentToken == TokenNameSEMICOLON){
		impt.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		impt.declarationSourceEnd = impt.sourceEnd;
	}
	impt.declarationEnd = impt.declarationSourceEnd;
	//this.endPosition is just before the ;
	impt.declarationSourceStart = this.intStack[this.intPtr--];

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		impt.modifiers = ClassFileConstants.AccDefault; // convert the static import reference to a non-static importe reference
		problemReporter().invalidUsageOfStaticImports(impt);
	}

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = impt.declarationSourceEnd+1;
		this.currentElement = this.currentElement.add(impt, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeStaticInitializer() {
	// StaticInitializer ::=  StaticOnly Block
	//push an Initializer
	//optimize the push/pop
	Block block = (Block) this.astStack[this.astPtr];
	if (this.diet) block.bits &= ~ASTNode.UndocumentedEmptyBlock; // clear bit set since was diet
	Initializer initializer = new Initializer(block, ClassFileConstants.AccStatic);
	this.astStack[this.astPtr] = initializer;
	initializer.sourceEnd = this.endStatementPosition;
	initializer.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
	this.nestedMethod[this.nestedType] --;
	initializer.declarationSourceStart = this.intStack[this.intPtr--];
	initializer.bodyStart = this.intStack[this.intPtr--];
	initializer.bodyEnd = this.endPosition;
	// doc comment
	initializer.javadoc = this.javadoc;
	this.javadoc = null;

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = initializer.declarationSourceEnd;
		this.currentElement = this.currentElement.add(initializer, 0);
		this.lastIgnoredToken = -1;
	}
}
protected void consumeStaticOnly() {
	// StaticOnly ::= 'static'
	int savedModifiersSourceStart = this.modifiersSourceStart;
	checkComment(); // might update declaration source start
	if (this.modifiersSourceStart >= savedModifiersSourceStart) {
		this.modifiersSourceStart = savedModifiersSourceStart;
	}
	pushOnIntStack(this.scanner.currentPosition);
	pushOnIntStack(
		this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
	jumpOverMethodBody();
	this.nestedMethod[this.nestedType]++;
	resetModifiers();
	this.expressionLengthPtr--; // remove the 0 pushed in consumeToken() for the static modifier

	// recovery
	if (this.currentElement != null){
		this.recoveredStaticInitializerStart = this.intStack[this.intPtr]; // remember start position only for static initializers
	}
}
protected void consumeSwitchBlock() {
	// SwitchBlock ::= '{' SwitchBlockStatements SwitchLabels '}'
	concatNodeLists();
}
protected void consumeSwitchBlockStatement() {
	// SwitchBlockStatement ::= SwitchLabels BlockStatements
	concatNodeLists();
}
protected void consumeSwitchBlockStatements() {
	// SwitchBlockStatements ::= SwitchBlockStatements SwitchBlockStatement
	concatNodeLists();
}
protected void consumeSwitchLabels() {
	// SwitchLabels ::= SwitchLabels SwitchLabel
	optimizedConcatNodeLists();
}
protected void consumeToken(int type) {
	/* remember the last consumed value */
	/* try to minimize the number of build values */
//	// clear the commentPtr of the scanner in case we read something different from a modifier
//	switch(type) {
//		case TokenNameabstract :
//		case TokenNamestrictfp :
//		case TokenNamefinal :
//		case TokenNamenative :
//		case TokenNameprivate :
//		case TokenNameprotected :
//		case TokenNamepublic :
//		case TokenNametransient :
//		case TokenNamevolatile :
//		case TokenNamestatic :
//		case TokenNamesynchronized :
//			break;
//		default:
//			this.scanner.commentPtr = -1;
//	}
	//System.out.println(this.scanner.toStringAction(type));
	switch (type) {
		case TokenNameIdentifier :
			pushIdentifier();
			if (this.scanner.useAssertAsAnIndentifier  &&
					this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
				long positions = this.identifierPositionStack[this.identifierPtr];
				if(!this.statementRecoveryActivated) problemReporter().useAssertAsAnIdentifier((int) (positions >>> 32), (int) positions);
			}
			if (this.scanner.useEnumAsAnIndentifier  &&
					this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
				long positions = this.identifierPositionStack[this.identifierPtr];
				if(!this.statementRecoveryActivated) problemReporter().useEnumAsAnIdentifier((int) (positions >>> 32), (int) positions);
			}
			break;
		case TokenNameinterface :
			//'class' is pushing two int (positions) on the stack ==> 'interface' needs to do it too....
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameabstract :
			checkAndSetModifiers(ClassFileConstants.AccAbstract);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamestrictfp :
			checkAndSetModifiers(ClassFileConstants.AccStrictfp);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamefinal :
			checkAndSetModifiers(ClassFileConstants.AccFinal);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamenative :
			checkAndSetModifiers(ClassFileConstants.AccNative);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNameprivate :
			checkAndSetModifiers(ClassFileConstants.AccPrivate);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNameprotected :
			checkAndSetModifiers(ClassFileConstants.AccProtected);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamepublic :
			checkAndSetModifiers(ClassFileConstants.AccPublic);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNametransient :
			checkAndSetModifiers(ClassFileConstants.AccTransient);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamevolatile :
			checkAndSetModifiers(ClassFileConstants.AccVolatile);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamestatic :
			checkAndSetModifiers(ClassFileConstants.AccStatic);
			pushOnExpressionStackLengthStack(0);
			break;
		case TokenNamesynchronized :
			this.synchronizedBlockSourceStart = this.scanner.startPosition;
			checkAndSetModifiers(ClassFileConstants.AccSynchronized);
			pushOnExpressionStackLengthStack(0);
			break;
			//==============================
		case TokenNamevoid :
			pushIdentifier(-T_void);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
			//push a default dimension while void is not part of the primitive
			//declaration baseType and so takes the place of a type without getting into
			//regular type parsing that generates a dimension on this.intStack
		case TokenNameboolean :
			pushIdentifier(-T_boolean);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamebyte :
			pushIdentifier(-T_byte);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamechar :
			pushIdentifier(-T_char);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamedouble :
			pushIdentifier(-T_double);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamefloat :
			pushIdentifier(-T_float);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameint :
			pushIdentifier(-T_int);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamelong :
			pushIdentifier(-T_long);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameshort :
			pushIdentifier(-T_short);
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
			//==============================
		case TokenNameIntegerLiteral :
			pushOnExpressionStack(
				IntLiteral.buildIntLiteral(
					this.scanner.getCurrentTokenSource(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1));
			break;
		case TokenNameLongLiteral :
			pushOnExpressionStack(
				LongLiteral.buildLongLiteral(
					this.scanner.getCurrentTokenSource(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1));
			break;
		case TokenNameFloatingPointLiteral :
			pushOnExpressionStack(
				new FloatLiteral(
					this.scanner.getCurrentTokenSource(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1));
			break;
		case TokenNameDoubleLiteral :
			pushOnExpressionStack(
				new DoubleLiteral(
					this.scanner.getCurrentTokenSource(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1));
			break;
		case TokenNameCharacterLiteral :
			pushOnExpressionStack(
				new CharLiteral(
					this.scanner.getCurrentTokenSource(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1));
			break;
		case TokenNameStringLiteral :
			StringLiteral stringLiteral;
			if (this.recordStringLiterals &&
					this.checkExternalizeStrings &&
					this.lastPosistion < this.scanner.currentPosition &&
					!this.statementRecoveryActivated) {
				stringLiteral = createStringLiteral(
					this.scanner.getCurrentTokenSourceString(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1,
					Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
				this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
			} else {
				stringLiteral = createStringLiteral(
					this.scanner.getCurrentTokenSourceString(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1,
					0);
			}
			pushOnExpressionStack(stringLiteral);
			break;
		case TokenNamefalse :
			pushOnExpressionStack(
				new FalseLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
			break;
		case TokenNametrue :
			pushOnExpressionStack(
				new TrueLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
			break;
		case TokenNamenull :
			pushOnExpressionStack(
				new NullLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
			break;
			//============================
		case TokenNamesuper :
		case TokenNamethis :
			this.endPosition = this.scanner.currentPosition - 1;
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameassert :
		case TokenNameimport :
		case TokenNamepackage :
		case TokenNamethrow :
		case TokenNamedo :
		case TokenNameif :
		case TokenNamefor :
		case TokenNameswitch :
		case TokenNametry :
		case TokenNamewhile :
		case TokenNamebreak :
		case TokenNamecontinue :
		case TokenNamereturn :
		case TokenNamecase :
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamenew :
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=40954
			resetModifiers();
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameclass :
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameenum :
			pushOnIntStack(this.scanner.currentPosition - 1);
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNamedefault :
			pushOnIntStack(this.scanner.startPosition);
			pushOnIntStack(this.scanner.currentPosition - 1);
			break;
			//let extra semantic action decide when to push
		case TokenNameRBRACKET :
			this.endPosition = this.scanner.startPosition;
			this.endStatementPosition = this.scanner.currentPosition - 1;
			break;
		case TokenNameLBRACE :
			this.endStatementPosition = this.scanner.currentPosition - 1;
			//$FALL-THROUGH$
		case TokenNamePLUS :
		case TokenNameMINUS :
		case TokenNameNOT :
		case TokenNameTWIDDLE :
			this.endPosition = this.scanner.startPosition;
			break;
		case TokenNamePLUS_PLUS :
		case TokenNameMINUS_MINUS :
			this.endPosition = this.scanner.startPosition;
			this.endStatementPosition = this.scanner.currentPosition - 1;
			break;
		case TokenNameRBRACE:
		case TokenNameSEMICOLON :
			this.endStatementPosition = this.scanner.currentPosition - 1;
			this.endPosition = this.scanner.startPosition - 1;
			//the item is not part of the potential futur expression/statement
			break;
		case TokenNameRPAREN :
			// in order to handle ( expression) ////// (cast)expression///// foo(x)
			this.rParenPos = this.scanner.currentPosition - 1; // position of the end of right parenthesis (in case of unicode \u0029) lex00101
			break;
		case TokenNameLPAREN :
			this.lParenPos = this.scanner.startPosition;
			break;
		case TokenNameAT :
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameQUESTION  :
			pushOnIntStack(this.scanner.startPosition);
			pushOnIntStack(this.scanner.currentPosition - 1);
			break;
		case TokenNameLESS :
			pushOnIntStack(this.scanner.startPosition);
			break;
		case TokenNameELLIPSIS :
			pushOnIntStack(this.scanner.currentPosition - 1);
			break;
		case TokenNameEQUAL  :
			if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
				RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation) this.currentElement;
				if (recoveredAnnotation.memberValuPairEqualEnd == -1) {
					recoveredAnnotation.memberValuPairEqualEnd = this.scanner.currentPosition - 1;
				}
			}
			break;
		case TokenNameMULTIPLY :
			// star end position
			pushOnIntStack(this.scanner.currentPosition - 1);
			break;
			//  case TokenNameCOMMA :
			//  case TokenNameCOLON  :
			//  case TokenNameLBRACKET  :
			//  case TokenNameDOT :
			//  case TokenNameERROR :
			//  case TokenNameEOF  :
			//  case TokenNamecase  :
			//  case TokenNamecatch  :
			//  case TokenNameelse  :
			//  case TokenNameextends  :
			//  case TokenNamefinally  :
			//  case TokenNameimplements  :
			//  case TokenNamethrows  :
			//  case TokenNameinstanceof  :
			//  case TokenNameEQUAL_EQUAL  :
			//  case TokenNameLESS_EQUAL  :
			//  case TokenNameGREATER_EQUAL  :
			//  case TokenNameNOT_EQUAL  :
			//  case TokenNameLEFT_SHIFT  :
			//  case TokenNameRIGHT_SHIFT  :
			//  case TokenNameUNSIGNED_RIGHT_SHIFT :
			//  case TokenNamePLUS_EQUAL  :
			//  case TokenNameMINUS_EQUAL  :
			//  case TokenNameMULTIPLY_EQUAL  :
			//  case TokenNameDIVIDE_EQUAL  :
			//  case TokenNameAND_EQUAL  :
			//  case TokenNameOR_EQUAL  :
			//  case TokenNameXOR_EQUAL  :
			//  case TokenNameREMAINDER_EQUAL  :
			//  case TokenNameLEFT_SHIFT_EQUAL  :
			//  case TokenNameRIGHT_SHIFT_EQUAL  :
			//  case TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL  :
			//  case TokenNameOR_OR  :
			//  case TokenNameAND_AND  :
			//  case TokenNameREMAINDER :
			//  case TokenNameXOR  :
			//  case TokenNameAND  :
			//  case TokenNameMULTIPLY :
			//  case TokenNameOR  :
			//  case TokenNameDIVIDE :
			//  case TokenNameGREATER  :
	}
}
protected void consumeTypeArgument() {
	pushOnGenericsStack(getTypeReference(this.intStack[this.intPtr--]));
}
protected void consumeTypeArgumentList() {
	concatGenericsLists();
}
protected void consumeTypeArgumentList1() {
	concatGenericsLists();
}
protected void consumeTypeArgumentList2() {
	concatGenericsLists();
}
protected void consumeTypeArgumentList3() {
	concatGenericsLists();
}
protected void consumeTypeArgumentReferenceType1() {
	concatGenericsLists();
	pushOnGenericsStack(getTypeReference(0));
	this.intPtr--;
}
protected void consumeTypeArgumentReferenceType2() {
	concatGenericsLists();
	pushOnGenericsStack(getTypeReference(0));
	this.intPtr--;
}
protected void consumeTypeArguments() {
	concatGenericsLists();
	this.intPtr--;

	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5 &&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		int length = this.genericsLengthStack[this.genericsLengthPtr];
		problemReporter().invalidUsageOfTypeArguments(
			(TypeReference)this.genericsStack[this.genericsPtr - length + 1],
			(TypeReference)this.genericsStack[this.genericsPtr]);
	}
}
protected void consumeTypeDeclarations() {
	// TypeDeclarations ::= TypeDeclarations TypeDeclaration
	concatNodeLists();
}
protected void consumeTypeHeaderNameWithTypeParameters() {
	// ClassHeaderName ::= ClassHeaderName1 TypeParameters
	// InterfaceHeaderName ::= InterfaceHeaderName1 TypeParameters
	TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];

	// consume type parameters
	int length = this.genericsLengthStack[this.genericsLengthPtr--];
	this.genericsPtr -= length;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);

	typeDecl.bodyStart = typeDecl.typeParameters[length-1].declarationSourceEnd + 1;

	this.listTypeParameterLength = 0;

	if (this.currentElement != null) {
		// is recovering
		if (this.currentElement instanceof RecoveredType) {
			RecoveredType recoveredType = (RecoveredType) this.currentElement;
			recoveredType.pendingTypeParameters = null;
			this.lastCheckPoint = typeDecl.bodyStart;
		} else {
			this.lastCheckPoint = typeDecl.bodyStart;
			this.currentElement = this.currentElement.add(typeDecl, 0);
			this.lastIgnoredToken = -1;
		}
	}
}
protected void consumeTypeImportOnDemandDeclarationName() {
	// TypeImportOnDemandDeclarationName ::= 'import' Name '.' '*'
	/* push an ImportRef build from the last name
	stored in the identifier stack. */

	ImportReference impt;
	int length;
	char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
	this.identifierPtr -= length;
	long[] positions = new long[length];
	System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
	System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
	pushOnAstStack(impt = new ImportReference(tokens, positions, true, ClassFileConstants.AccDefault));

	// star end position
	impt.trailingStarPosition = this.intStack[this.intPtr--];
	if (this.currentToken == TokenNameSEMICOLON){
		impt.declarationSourceEnd = this.scanner.currentPosition - 1;
	} else {
		impt.declarationSourceEnd = impt.sourceEnd;
	}
	impt.declarationEnd = impt.declarationSourceEnd;
	//this.endPosition is just before the ;
	impt.declarationSourceStart = this.intStack[this.intPtr--];

	// recovery
	if (this.currentElement != null){
		this.lastCheckPoint = impt.declarationSourceEnd+1;
		this.currentElement = this.currentElement.add(impt, 0);
		this.lastIgnoredToken = -1;
		this.restartRecovery = true; // used to avoid branching back into the regular automaton
	}
}
protected void consumeTypeParameter1() {
	// nothing to do
}
protected void consumeTypeParameter1WithExtends() {
	//TypeParameter1 ::= TypeParameterHeader 'extends' ReferenceType1
	TypeReference superType = (TypeReference) this.genericsStack[this.genericsPtr--];
	this.genericsLengthPtr--;
	TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
	typeParameter.declarationSourceEnd = superType.sourceEnd;
	typeParameter.type = superType;
	superType.bits |= ASTNode.IsSuperType;
	this.genericsStack[this.genericsPtr] = typeParameter;
}
protected void consumeTypeParameter1WithExtendsAndBounds() {
	//TypeParameter1 ::= TypeParameterHeader 'extends' ReferenceType AdditionalBoundList1
	int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
	TypeReference[] bounds = new TypeReference[additionalBoundsLength];
	this.genericsPtr -= additionalBoundsLength;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
	TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
	TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
	typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
	typeParameter.type = superType;
	superType.bits |= ASTNode.IsSuperType;
	typeParameter.bounds = bounds;
	for (int i = 0, max = bounds.length; i < max; i++) {
		bounds[i].bits |= ASTNode.IsSuperType;
	}
}
protected void consumeTypeParameterHeader() {
	//TypeParameterHeader ::= Identifier
	TypeParameter typeParameter = new TypeParameter();
	long pos = this.identifierPositionStack[this.identifierPtr];
	final int end = (int) pos;
	typeParameter.declarationSourceEnd = end;
	typeParameter.sourceEnd = end;
	final int start = (int) (pos >>> 32);
	typeParameter.declarationSourceStart = start;
	typeParameter.sourceStart = start;
	typeParameter.name = this.identifierStack[this.identifierPtr--];
	this.identifierLengthPtr--;
	pushOnGenericsStack(typeParameter);

	this.listTypeParameterLength++;
}
protected void consumeTypeParameterList() {
	//TypeParameterList ::= TypeParameterList ',' TypeParameter
	concatGenericsLists();
}
protected void consumeTypeParameterList1() {
	//TypeParameterList1 ::= TypeParameterList ',' TypeParameter1
	concatGenericsLists();
}
protected void consumeTypeParameters() {
	int startPos = this.intStack[this.intPtr--];

	if(this.currentElement != null) {
		if(this.currentElement instanceof RecoveredType) {
			RecoveredType recoveredType =(RecoveredType) this.currentElement;
			int length = this.genericsLengthStack[this.genericsLengthPtr];
			TypeParameter[] typeParameters = new TypeParameter[length];
			System.arraycopy(this.genericsStack, this.genericsPtr - length + 1, typeParameters, 0, length);

			recoveredType.add(typeParameters, startPos);
		}
	}


	if(!this.statementRecoveryActivated &&
			this.options.sourceLevel < ClassFileConstants.JDK1_5&&
			this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
		int length = this.genericsLengthStack[this.genericsLengthPtr];
		problemReporter().invalidUsageOfTypeParameters(
			(TypeParameter) this.genericsStack[this.genericsPtr - length + 1],
			(TypeParameter) this.genericsStack[this.genericsPtr]);
	}
}
protected void consumeTypeParameterWithExtends() {
	//TypeParameter ::= TypeParameterHeader 'extends' ReferenceType
	TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
	TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
	typeParameter.declarationSourceEnd = superType.sourceEnd;
	typeParameter.type = superType;
	superType.bits |= ASTNode.IsSuperType;
}
protected void consumeTypeParameterWithExtendsAndBounds() {
	//TypeParameter ::= TypeParameterHeader 'extends' ReferenceType AdditionalBoundList
	int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
	TypeReference[] bounds = new TypeReference[additionalBoundsLength];
	this.genericsPtr -= additionalBoundsLength;
	System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
	TypeReference superType = getTypeReference(this.intStack[this.intPtr--]);
	TypeParameter typeParameter = (TypeParameter) this.genericsStack[this.genericsPtr];
	typeParameter.type = superType;
	superType.bits |= ASTNode.IsSuperType;
	typeParameter.bounds = bounds;
	typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
	for (int i = 0, max = bounds.length; i < max; i++) {
		bounds[i].bits |= ASTNode.IsSuperType;
	}
}
protected void consumeUnaryExpression(int op) {
	// UnaryExpression ::= '+' PushPosition UnaryExpression
	// UnaryExpression ::= '-' PushPosition UnaryExpression
	// UnaryExpressionNotPlusMinus ::= '~' PushPosition UnaryExpression
	// UnaryExpressionNotPlusMinus ::= '!' PushPosition UnaryExpression

	//optimize the push/pop

	//handle manually the -2147483648 while it is not a real
	//computation of an - and 2147483648 (notice that 2147483648
	//is Integer.MAX_VALUE+1.....)
	//Same for -9223372036854775808L ............

	//this.intStack have the position of the operator

	Expression r, exp = this.expressionStack[this.expressionPtr];
	if (op == MINUS) {
		if (exp instanceof IntLiteral) {
			IntLiteral intLiteral = (IntLiteral) exp;
			IntLiteral convertToMinValue = intLiteral.convertToMinValue();
			if (convertToMinValue ==  intLiteral) {
				// not a min value literal so we convert it to an unary expression
				r = new UnaryExpression(exp, op);
			} else {
				r = convertToMinValue;
			}
		} else if (exp instanceof LongLiteral) {
			LongLiteral longLiteral = (LongLiteral) exp;
			LongLiteral convertToMinValue = longLiteral.convertToMinValue();
			if (convertToMinValue ==  longLiteral) {
				// not a min value literal so we convert it to an unary expression
				r = new UnaryExpression(exp, op);
			} else {
				r = convertToMinValue;
			}
		} else {
			r = new UnaryExpression(exp, op);
		}
	} else {
		r = new UnaryExpression(exp, op);
	}
	r.sourceStart = this.intStack[this.intPtr--];
	r.sourceEnd = exp.sourceEnd;
	this.expressionStack[this.expressionPtr] = r;
}
protected void consumeUnaryExpression(int op, boolean post) {
	// PreIncrementExpression ::= '++' PushPosition UnaryExpression
	// PreDecrementExpression ::= '--' PushPosition UnaryExpression

	// ++ and -- operators
	//optimize the push/pop

	//this.intStack has the position of the operator when prefix

	Expression leftHandSide = this.expressionStack[this.expressionPtr];
	if (leftHandSide instanceof Reference) {
		// ++foo()++ is unvalid
		if (post) {
			this.expressionStack[this.expressionPtr] =
				new PostfixExpression(
					leftHandSide,
					IntLiteral.One,
					op,
					this.endStatementPosition);
		} else {
			this.expressionStack[this.expressionPtr] =
				new PrefixExpression(
					leftHandSide,
					IntLiteral.One,
					op,
					this.intStack[this.intPtr--]);
		}
	} else {
		//the ++ or the -- is NOT taken into account if code gen proceeds
		if (!post) {
			this.intPtr--;
		}
		if(!this.statementRecoveryActivated) problemReporter().invalidUnaryExpression(leftHandSide);
	}
}
protected void consumeVariableDeclarators() {
	// VariableDeclarators ::= VariableDeclarators ',' VariableDeclarator
	optimizedConcatNodeLists();
}
protected void consumeVariableInitializers() {
	// VariableInitializers ::= VariableInitializers ',' VariableInitializer
	concatExpressionLists();
}
protected void consumeWildcard() {
	final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
	wildcard.sourceEnd = this.intStack[this.intPtr--];
	wildcard.sourceStart = this.intStack[this.intPtr--];
	pushOnGenericsStack(wildcard);
}
protected void consumeWildcard1() {
	final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
	wildcard.sourceEnd = this.intStack[this.intPtr--];
	wildcard.sourceStart = this.intStack[this.intPtr--];
	pushOnGenericsStack(wildcard);
}
protected void consumeWildcard1WithBounds() {
	// Nothing to do
	// The wildcard is created by the consumeWildcardBounds1Extends or by consumeWildcardBounds1Super
}
protected void consumeWildcard2() {
	final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
	wildcard.sourceEnd = this.intStack[this.intPtr--];
	wildcard.sourceStart = this.intStack[this.intPtr--];
	pushOnGenericsStack(wildcard);
}
protected void consumeWildcard2WithBounds() {
	// Nothing to do
	// The wildcard is created by the consumeWildcardBounds2Extends or by consumeWildcardBounds2Super
}
protected void consumeWildcard3() {
	final Wildcard wildcard = new Wildcard(Wildcard.UNBOUND);
	wildcard.sourceEnd = this.intStack[this.intPtr--];
	wildcard.sourceStart = this.intStack[this.intPtr--];
	pushOnGenericsStack(wildcard);
}
protected void consumeWildcard3WithBounds() {
	// Nothing to do
	// The wildcard is created by the consumeWildcardBounds3Extends or by consumeWildcardBounds3Super
}
protected void consumeWildcardBounds1Extends() {
	Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
	wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	this.genericsStack[this.genericsPtr] = wildcard;
}
protected void consumeWildcardBounds1Super() {
	Wildcard wildcard = new Wildcard(Wildcard.SUPER);
	wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
	this.intPtr--; // remove the starting position of the super keyword
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	this.genericsStack[this.genericsPtr] = wildcard;
}
protected void consumeWildcardBounds2Extends() {
	Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
	wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	this.genericsStack[this.genericsPtr] = wildcard;
}
protected void consumeWildcardBounds2Super() {
	Wildcard wildcard = new Wildcard(Wildcard.SUPER);
	wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
	this.intPtr--; // remove the starting position of the super keyword
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	this.genericsStack[this.genericsPtr] = wildcard;
}
protected void consumeWildcardBounds3Extends() {
	Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
	wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	this.genericsStack[this.genericsPtr] = wildcard;
}
protected void consumeWildcardBounds3Super() {
	Wildcard wildcard = new Wildcard(Wildcard.SUPER);
	wildcard.bound = (TypeReference) this.genericsStack[this.genericsPtr];
	this.intPtr--; // remove the starting position of the super keyword
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	this.genericsStack[this.genericsPtr] = wildcard;
}
protected void consumeWildcardBoundsExtends() {
	Wildcard wildcard = new Wildcard(Wildcard.EXTENDS);
	wildcard.bound = getTypeReference(this.intStack[this.intPtr--]);
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	pushOnGenericsStack(wildcard);
}
protected void consumeWildcardBoundsSuper() {
	Wildcard wildcard = new Wildcard(Wildcard.SUPER);
	wildcard.bound = getTypeReference(this.intStack[this.intPtr--]);
	this.intPtr--; // remove the starting position of the super keyword
	wildcard.sourceEnd = wildcard.bound.sourceEnd;
	this.intPtr--; // remove end position of the '?'
	wildcard.sourceStart = this.intStack[this.intPtr--];
	pushOnGenericsStack(wildcard);
}
protected void consumeWildcardWithBounds() {
	// Nothing to do
	// The wildcard is created by the consumeWildcardBoundsExtends or by consumeWildcardBoundsSuper
}
/**
 * Given the current comment stack, answer whether some comment is available in a certain exclusive range
 *
 * @param sourceStart int
 * @param sourceEnd int
 * @return boolean
 */
public boolean containsComment(int sourceStart, int sourceEnd) {
	int iComment = this.scanner.commentPtr;
	for (; iComment >= 0; iComment--) {
		int commentStart = this.scanner.commentStarts[iComment];
		if (commentStart < 0) commentStart = -commentStart;
		// ignore comments before start
		if (commentStart < sourceStart) continue;
		// ignore comments after end
		if (commentStart > sourceEnd) continue;
		return true;
	}
	return false;
}

public MethodDeclaration convertToMethodDeclaration(ConstructorDeclaration c, CompilationResult compilationResult) {
	MethodDeclaration m = new MethodDeclaration(compilationResult);
	m.typeParameters = c.typeParameters;
	m.sourceStart = c.sourceStart;
	m.sourceEnd = c.sourceEnd;
	m.bodyStart = c.bodyStart;
	m.bodyEnd = c.bodyEnd;
	m.declarationSourceEnd = c.declarationSourceEnd;
	m.declarationSourceStart = c.declarationSourceStart;
	m.selector = c.selector;
	m.statements = c.statements;
	m.modifiers = c.modifiers;
	m.annotations = c.annotations;
	m.arguments = c.arguments;
	m.thrownExceptions = c.thrownExceptions;
	m.explicitDeclarations = c.explicitDeclarations;
	m.returnType = null;
	m.javadoc = c.javadoc;
	return m;
}

protected TypeReference copyDims(TypeReference typeRef, int dim) {
	return typeRef.copyDims(dim);
}
protected FieldDeclaration createFieldDeclaration(char[] fieldDeclarationName, int sourceStart, int sourceEnd) {
	return new FieldDeclaration(fieldDeclarationName, sourceStart, sourceEnd);
}
protected JavadocParser createJavadocParser() {
	return new JavadocParser(this);
}
protected LocalDeclaration createLocalDeclaration(char[] localDeclarationName, int sourceStart, int sourceEnd) {
	return new LocalDeclaration(localDeclarationName, sourceStart, sourceEnd);
}
protected StringLiteral createStringLiteral(char[] token, int start, int end, int lineNumber) {
	return new StringLiteral(token, start, end, lineNumber);
}
protected RecoveredType currentRecoveryType() {
	if(this.currentElement != null) {
		if(this.currentElement instanceof RecoveredType) {
			return (RecoveredType) this.currentElement;
		} else {
			return this.currentElement.enclosingType();
		}
	}
	return null;
}
public CompilationUnitDeclaration dietParse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {

	CompilationUnitDeclaration parsedUnit;
	boolean old = this.diet;
	try {
		this.diet = true;
		parsedUnit = parse(sourceUnit, compilationResult);
	} finally {
		this.diet = old;
	}
	return parsedUnit;
}
protected void dispatchDeclarationInto(int length) {
	/* they are length on this.astStack that should go into
	   methods fields constructors lists of the typeDecl

	   Return if there is a constructor declaration in the methods declaration */


	// Looks for the size of each array .

	if (length == 0)
		return;
	int[] flag = new int[length + 1]; //plus one -- see <HERE>
	int size1 = 0, size2 = 0, size3 = 0;
	boolean hasAbstractMethods = false;
	for (int i = length - 1; i >= 0; i--) {
		ASTNode astNode = this.astStack[this.astPtr--];
		if (astNode instanceof AbstractMethodDeclaration) {
			//methods and constructors have been regrouped into one single list
			flag[i] = 2;
			size2++;
			if (((AbstractMethodDeclaration) astNode).isAbstract()) {
				hasAbstractMethods = true;
			}
		} else if (astNode instanceof TypeDeclaration) {
			flag[i] = 3;
			size3++;
		} else {
			//field
			flag[i] = 1;
			size1++;
		}
	}

	//arrays creation
	TypeDeclaration typeDecl = (TypeDeclaration) this.astStack[this.astPtr];
	if (size1 != 0) {
		typeDecl.fields = new FieldDeclaration[size1];
	}
	if (size2 != 0) {
		typeDecl.methods = new AbstractMethodDeclaration[size2];
		if (hasAbstractMethods) typeDecl.bits |= ASTNode.HasAbstractMethods;
	}
	if (size3 != 0) {
		typeDecl.memberTypes = new TypeDeclaration[size3];
	}

	//arrays fill up
	size1 = size2 = size3 = 0;
	int flagI = flag[0], start = 0;
	int length2;
	for (int end = 0; end <= length; end++) //<HERE> the plus one allows to
		{
		if (flagI != flag[end]) //treat the last element as a ended flag.....
			{ //array copy
			switch (flagI) {
				case 1 :
					size1 += (length2 = end - start);
					System.arraycopy(
						this.astStack,
						this.astPtr + start + 1,
						typeDecl.fields,
						size1 - length2,
						length2);
					break;
				case 2 :
					size2 += (length2 = end - start);
					System.arraycopy(
						this.astStack,
						this.astPtr + start + 1,
						typeDecl.methods,
						size2 - length2,
						length2);
					break;
				case 3 :
					size3 += (length2 = end - start);
					System.arraycopy(
						this.astStack,
						this.astPtr + start + 1,
						typeDecl.memberTypes,
						size3 - length2,
						length2);
					break;
			}
			flagI = flag[start = end];
		}
	}

	if (typeDecl.memberTypes != null) {
		for (int i = typeDecl.memberTypes.length - 1; i >= 0; i--) {
			typeDecl.memberTypes[i].enclosingType = typeDecl;
		}
	}
}
protected void dispatchDeclarationIntoEnumDeclaration(int length) {

	if (length == 0)
		return;
	int[] flag = new int[length + 1]; //plus one -- see <HERE>
	int size1 = 0, size2 = 0, size3 = 0;
	TypeDeclaration enumDeclaration = (TypeDeclaration) this.astStack[this.astPtr - length];
	boolean hasAbstractMethods = false;
	int enumConstantsCounter = 0;
	for (int i = length - 1; i >= 0; i--) {
		ASTNode astNode = this.astStack[this.astPtr--];
		if (astNode instanceof AbstractMethodDeclaration) {
			//methods and constructors have been regrouped into one single list
			flag[i] = 2;
			size2++;
			if (((AbstractMethodDeclaration) astNode).isAbstract()) {
				hasAbstractMethods = true;
			}
		} else if (astNode instanceof TypeDeclaration) {
			flag[i] = 3;
			size3++;
		} else if (astNode instanceof FieldDeclaration) {
			flag[i] = 1;
			size1++;
			if (((FieldDeclaration) astNode).getKind() == AbstractVariableDeclaration.ENUM_CONSTANT) {
				enumConstantsCounter++;
			}
		}
	}

	//arrays creation
	if (size1 != 0) {
		enumDeclaration.fields = new FieldDeclaration[size1];
	}
	if (size2 != 0) {
		enumDeclaration.methods = new AbstractMethodDeclaration[size2];
		if (hasAbstractMethods) enumDeclaration.bits |= ASTNode.HasAbstractMethods;
	}
	if (size3 != 0) {
		enumDeclaration.memberTypes = new TypeDeclaration[size3];
	}

	//arrays fill up
	size1 = size2 = size3 = 0;
	int flagI = flag[0], start = 0;
	int length2;
	for (int end = 0; end <= length; end++) //<HERE> the plus one allows to
	{
		if (flagI != flag[end]) //treat the last element as a ended flag.....
		{ //array copy
			switch (flagI) {
				case 1 :
					size1 += (length2 = end - start);
					System.arraycopy(
							this.astStack,
							this.astPtr + start + 1,
							enumDeclaration.fields,
							size1 - length2,
							length2);
					break;
				case 2 :
					size2 += (length2 = end - start);
					System.arraycopy(
							this.astStack,
							this.astPtr + start + 1,
							enumDeclaration.methods,
							size2 - length2,
							length2);
					break;
				case 3 :
					size3 += (length2 = end - start);
					System.arraycopy(
							this.astStack,
							this.astPtr + start + 1,
							enumDeclaration.memberTypes,
							size3 - length2,
							length2);
					break;
			}
			flagI = flag[start = end];
		}
	}

	if (enumDeclaration.memberTypes != null) {
		for (int i = enumDeclaration.memberTypes.length - 1; i >= 0; i--) {
			enumDeclaration.memberTypes[i].enclosingType = enumDeclaration;
		}
	}
	enumDeclaration.enumConstantsCounter = enumConstantsCounter;
}
protected CompilationUnitDeclaration endParse(int act) {

	this.lastAct = act;

	if(this.statementRecoveryActivated) {
		RecoveredElement recoveredElement = buildInitialRecoveryState();

		if (recoveredElement != null) {
			recoveredElement.topElement().updateParseTree();
		}

		if(this.hasError) resetStacks();
	} else if (this.currentElement != null){
		if (VERBOSE_RECOVERY){
			System.out.print(Messages.parser_syntaxRecovery);
			System.out.println("--------------------------");		 //$NON-NLS-1$
			System.out.println(this.compilationUnit);
			System.out.println("----------------------------------"); //$NON-NLS-1$
		}
		this.currentElement.topElement().updateParseTree();
	} else {
		if (this.diet & VERBOSE_RECOVERY){
			System.out.print(Messages.parser_regularParse);
			System.out.println("--------------------------");	 //$NON-NLS-1$
			System.out.println(this.compilationUnit);
			System.out.println("----------------------------------"); //$NON-NLS-1$
		}
	}
	persistLineSeparatorPositions();
	for (int i = 0; i < this.scanner.foundTaskCount; i++){
		if(!this.statementRecoveryActivated) problemReporter().task(
			new String(this.scanner.foundTaskTags[i]),
			new String(this.scanner.foundTaskMessages[i]),
			this.scanner.foundTaskPriorities[i] == null ? null : new String(this.scanner.foundTaskPriorities[i]),
			this.scanner.foundTaskPositions[i][0],
			this.scanner.foundTaskPositions[i][1]);
	}
	return this.compilationUnit;
}
/*
 * Flush comments defined prior to a given positions.
 *
 * Note: comments are stacked in syntactical order
 *
 * Either answer given <position>, or the end position of a comment line
 * immediately following the <position> (same line)
 *
 * e.g.
 * void foo(){
 * } // end of method foo
 */
public int flushCommentsDefinedPriorTo(int position) {

	int lastCommentIndex = this.scanner.commentPtr;
	if (lastCommentIndex < 0) return position; // no comment

	// compute the index of the first obsolete comment
	int index = lastCommentIndex;
	int validCount = 0;
	while (index >= 0){
		int commentEnd = this.scanner.commentStops[index];
		if (commentEnd < 0) commentEnd = -commentEnd; // negative end position for non-javadoc comments
		if (commentEnd <= position){
			break;
		}
		index--;
		validCount++;
	}
	// if the source at <position> is immediately followed by a line comment, then
	// flush this comment and shift <position> to the comment end.
	if (validCount > 0){
		int immediateCommentEnd = -this.scanner.commentStops[index+1]; //non-javadoc comment end positions are negative
		if (immediateCommentEnd > 0){ // only tolerating non-javadoc comments
			// is there any line break until the end of the immediate comment ? (thus only tolerating line comment)
			immediateCommentEnd--; // comment end in one char too far
			if (Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr)
					== Util.getLineNumber(immediateCommentEnd, this.scanner.lineEnds, 0, this.scanner.linePtr)){
				position = immediateCommentEnd;
				validCount--; // flush this comment
				index++;
			}
		}
	}

	if (index < 0) return position; // no obsolete comment

	switch (validCount) {
		case 0:
			// do nothing
			break;
		// move valid comment infos, overriding obsolete comment infos
		case 2:
			this.scanner.commentStarts[0] = this.scanner.commentStarts[index+1];
			this.scanner.commentStops[0] = this.scanner.commentStops[index+1];
			this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index+1];
			this.scanner.commentStarts[1] = this.scanner.commentStarts[index+2];
			this.scanner.commentStops[1] = this.scanner.commentStops[index+2];
			this.scanner.commentTagStarts[1] = this.scanner.commentTagStarts[index+2];
			break;
		case 1:
			this.scanner.commentStarts[0] = this.scanner.commentStarts[index+1];
			this.scanner.commentStops[0] = this.scanner.commentStops[index+1];
			this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index+1];
			break;
		default:
			System.arraycopy(this.scanner.commentStarts, index + 1, this.scanner.commentStarts, 0, validCount);
			System.arraycopy(this.scanner.commentStops, index + 1, this.scanner.commentStops, 0, validCount);
			System.arraycopy(this.scanner.commentTagStarts, index + 1, this.scanner.commentTagStarts, 0, validCount);
	}
	this.scanner.commentPtr = validCount - 1;
	return position;
}

protected TypeReference getAnnotationType() {
	int length = this.identifierLengthStack[this.identifierLengthPtr--];
	if (length == 1) {
		return new SingleTypeReference(
				this.identifierStack[this.identifierPtr],
				this.identifierPositionStack[this.identifierPtr--]);
	} else {
		char[][] tokens = new char[length][];
		this.identifierPtr -= length;
		long[] positions = new long[length];
		System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
		System.arraycopy(
			this.identifierPositionStack,
			this.identifierPtr + 1,
			positions,
			0,
			length);
		return new QualifiedTypeReference(tokens, positions);
	}
}
public int getFirstToken() {
	// the first token is a virtual token that
	// allows the parser to parse several goals
	// even if they aren't LALR(1)....
	// Goal ::= '++' CompilationUnit
	// Goal ::= '--' MethodBody
	// Goal ::= '==' ConstructorBody
	// -- Initializer
	// Goal ::= '>>' StaticInitializer
	// Goal ::= '>>' Block
	// -- error recovery
	// Goal ::= '>>>' Headers
	// Goal ::= '*' BlockStatements
	// Goal ::= '*' MethodPushModifiersHeader
	// -- JDOM
	// Goal ::= '&&' FieldDeclaration
	// Goal ::= '||' ImportDeclaration
	// Goal ::= '?' PackageDeclaration
	// Goal ::= '+' TypeDeclaration
	// Goal ::= '/' GenericMethodDeclaration
	// Goal ::= '&' ClassBodyDeclaration
	// -- code snippet
	// Goal ::= '%' Expression
	// -- completion parser
	// Goal ::= '!' ConstructorBlockStatementsopt
	// Goal ::= '~' BlockStatementsopt

	return this.firstToken;
}
/*
 * Answer back an array of sourceStart/sourceEnd positions of the available JavaDoc comments.
 * The array is a flattened structure: 2*n entries with consecutives start and end positions.
 *
 * If no JavaDoc is available, then null is answered instead of an empty array.
 *
 * e.g. { 10, 20, 25, 45 }  --> javadoc1 from 10 to 20, javadoc2 from 25 to 45
 */
public int[] getJavaDocPositions() {

	int javadocCount = 0;
	int max = this.scanner.commentPtr;
	for (int i = 0; i <= max; i++){
		// javadoc only (non javadoc comment have negative start and/or end positions.)
		if (this.scanner.commentStarts[i] >= 0 && this.scanner.commentStops[i] > 0) {
			javadocCount++;
		}
	}
	if (javadocCount == 0) return null;

	int[] positions = new int[2*javadocCount];
	int index = 0;
	for (int i = 0; i <= max; i++){
		// javadoc only (non javadoc comment have negative start and/or end positions.)
		int commentStart = this.scanner.commentStarts[i];
		if (commentStart >= 0) {
			int commentStop = this.scanner.commentStops[i];
			if (commentStop > 0){
				positions[index++] = commentStart;
				positions[index++] = commentStop-1; //stop is one over
			}
		}
	}
	return positions;
}
public void getMethodBodies(CompilationUnitDeclaration unit) {
	//fill the methods bodies in order for the code to be generated

	if (unit == null) return;

	if (unit.ignoreMethodBodies) {
		unit.ignoreFurtherInvestigation = true;
		return;
		// if initial diet parse did not work, no need to dig into method bodies.
	}

	if ((unit.bits & ASTNode.HasAllMethodBodies) != 0)
		return; //work already done ...

	// save existing values to restore them at the end of the parsing process
	// see bug 47079 for more details
	int[] oldLineEnds = this.scanner.lineEnds;
	int oldLinePtr = this.scanner.linePtr;

	//real parse of the method....
	CompilationResult compilationResult = unit.compilationResult;
	char[] contents = this.readManager != null
		? this.readManager.getContents(compilationResult.compilationUnit)
		: compilationResult.compilationUnit.getContents();
	this.scanner.setSource(contents, compilationResult);

	if (this.javadocParser != null && this.javadocParser.checkDocComment) {
		this.javadocParser.scanner.setSource(contents);
	}
	if (unit.types != null) {
		for (int i = 0, length = unit.types.length; i < length; i++)
			unit.types[i].parseMethods(this, unit);
	}

	// tag unit has having read bodies
	unit.bits |= ASTNode.HasAllMethodBodies;

	// this is done to prevent any side effects on the compilation unit result
	// line separator positions array.
	this.scanner.lineEnds = oldLineEnds;
	this.scanner.linePtr = oldLinePtr;
}
	protected char getNextCharacter(char[] comment, int[] index) {
		char nextCharacter = comment[index[0]++];
		switch(nextCharacter) {
			case '\\' :
				int c1, c2, c3, c4;
				index[0]++;
				while (comment[index[0]] == 'u') index[0]++;
				if (!(((c1 = ScannerHelper.getNumericValue(comment[index[0]++])) > 15
					|| c1 < 0)
					|| ((c2 = ScannerHelper.getNumericValue(comment[index[0]++])) > 15 || c2 < 0)
					|| ((c3 = ScannerHelper.getNumericValue(comment[index[0]++])) > 15 || c3 < 0)
					|| ((c4 = ScannerHelper.getNumericValue(comment[index[0]++])) > 15 || c4 < 0))) {
						nextCharacter = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
				}
				break;
		}
		return nextCharacter;
	}
protected Expression getTypeReference(Expression exp) {

	exp.bits &= ~ASTNode.RestrictiveFlagMASK;
	exp.bits |= Binding.TYPE;
	return exp;
}
protected TypeReference getTypeReference(int dim) {
	/* build a Reference on a variable that may be qualified or not
	 This variable is a type reference and dim will be its dimensions*/

	TypeReference ref;
	int length = this.identifierLengthStack[this.identifierLengthPtr--];
	if (length < 0) { //flag for precompiled type reference on base types
		ref = TypeReference.baseTypeReference(-length, dim);
		ref.sourceStart = this.intStack[this.intPtr--];
		if (dim == 0) {
			ref.sourceEnd = this.intStack[this.intPtr--];
		} else {
			this.intPtr--;
			ref.sourceEnd = this.endPosition;
		}
	} else {
		int numberOfIdentifiers = this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr--];
		if (length != numberOfIdentifiers || this.genericsLengthStack[this.genericsLengthPtr] != 0) {
			// generic type
			ref = getTypeReferenceForGenericType(dim, length, numberOfIdentifiers);
		} else if (length == 1) {
			// single variable reference
			this.genericsLengthPtr--; // pop the 0
			if (dim == 0) {
				ref =
					new SingleTypeReference(
						this.identifierStack[this.identifierPtr],
						this.identifierPositionStack[this.identifierPtr--]);
			} else {
				ref =
					new ArrayTypeReference(
						this.identifierStack[this.identifierPtr],
						dim,
						this.identifierPositionStack[this.identifierPtr--]);
				ref.sourceEnd = this.endPosition;
			}
		} else {
			this.genericsLengthPtr--;
			//Qualified variable reference
			char[][] tokens = new char[length][];
			this.identifierPtr -= length;
			long[] positions = new long[length];
			System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
			System.arraycopy(
				this.identifierPositionStack,
				this.identifierPtr + 1,
				positions,
				0,
				length);
			if (dim == 0) {
				ref = new QualifiedTypeReference(tokens, positions);
			} else {
				ref = new ArrayQualifiedTypeReference(tokens, dim, positions);
				ref.sourceEnd = this.endPosition;
			}
		}
	}
	return ref;
}
protected TypeReference getTypeReferenceForGenericType(int dim, int identifierLength, int numberOfIdentifiers) {
	if (identifierLength == 1 && numberOfIdentifiers == 1) {
		int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
		TypeReference[] typeArguments = null;
		if (currentTypeArgumentsLength < 0) {
			typeArguments = TypeReference.NO_TYPE_ARGUMENTS;
		} else {
			typeArguments = new TypeReference[currentTypeArgumentsLength];
			this.genericsPtr -= currentTypeArgumentsLength;
			System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, currentTypeArgumentsLength);
		}
		ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.identifierStack[this.identifierPtr], typeArguments, dim, this.identifierPositionStack[this.identifierPtr--]);
		if (dim != 0) {
			parameterizedSingleTypeReference.sourceEnd = this.endStatementPosition;
		}
		/* We used to eagerly mark the PSTR as constituting diamond usage if we encountered <>, but that is too eager and
		   complicates error handling by making it hard to distinguish legitimate use cases from ill formed ones. We are
		   more discriminating now and tag a type as being diamond only where <> can legally occur. 
		   See https://bugs.eclipse.org/bugs/show_bug.cgi?id=339478#c11
		*/
		return parameterizedSingleTypeReference;
	} else {
		TypeReference[][] typeArguments = new TypeReference[numberOfIdentifiers][];
		char[][] tokens = new char[numberOfIdentifiers][];
		long[] positions = new long[numberOfIdentifiers];
		int index = numberOfIdentifiers;
		int currentIdentifiersLength = identifierLength;
		while (index > 0) {
			int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
			if (currentTypeArgumentsLength > 0) {
				this.genericsPtr -= currentTypeArgumentsLength;
				System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments[index - 1] = new TypeReference[currentTypeArgumentsLength], 0, currentTypeArgumentsLength);
			} else if (currentTypeArgumentsLength < 0) {
				// diamond case for qualified type reference (java.util.ArrayList<>)
				typeArguments[index - 1] = TypeReference.NO_TYPE_ARGUMENTS;
			}
			switch(currentIdentifiersLength) {
				case 1 :
					// we are in a case A<B>.C<D> or A<B>.C<D>
					tokens[index - 1] = this.identifierStack[this.identifierPtr];
					positions[index - 1] = this.identifierPositionStack[this.identifierPtr--];
					break;
				default:
					// we are in a case A.B.C<B>.C<D> or A.B.C<B>...
					this.identifierPtr -= currentIdentifiersLength;
					System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, index - currentIdentifiersLength, currentIdentifiersLength);
					System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, index - currentIdentifiersLength, currentIdentifiersLength);
			}
			index -= currentIdentifiersLength;
			if (index > 0) {
				currentIdentifiersLength = this.identifierLengthStack[this.identifierLengthPtr--];
			}
		}
		ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, positions);
		if (dim != 0) {
			parameterizedQualifiedTypeReference.sourceEnd = this.endStatementPosition;
		}
		/* We used to eagerly mark the PQTR as constituting diamond usage if we encountered <>, but that is too eager and
		   complicates error handling by making it hard to distinguish legitimate use cases from ill formed ones. We are
		   more discriminating now and tag a type as being diamond only where <> can legally occur. 
		   See https://bugs.eclipse.org/bugs/show_bug.cgi?id=339478#c11
		*/
		return parameterizedQualifiedTypeReference;
	}
}
protected NameReference getUnspecifiedReference() {
	/* build a (unspecified) NameReference which may be qualified*/

	int length;
	NameReference ref;
	if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1)
		// single variable reference
		ref =
			new SingleNameReference(
				this.identifierStack[this.identifierPtr],
				this.identifierPositionStack[this.identifierPtr--]);
	else
		//Qualified variable reference
		{
		char[][] tokens = new char[length][];
		this.identifierPtr -= length;
		System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
		long[] positions = new long[length];
		System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
		ref =
			new QualifiedNameReference(tokens,
				positions,
				(int) (this.identifierPositionStack[this.identifierPtr + 1] >> 32), // sourceStart
				(int) this.identifierPositionStack[this.identifierPtr + length]); // sourceEnd
	}
	return ref;
}
protected NameReference getUnspecifiedReferenceOptimized() {
	/* build a (unspecified) NameReference which may be qualified
	The optimization occurs for qualified reference while we are
	certain in this case the last item of the qualified name is
	a field access. This optimization is IMPORTANT while it results
	that when a NameReference is build, the type checker should always
	look for that it is not a type reference */

	int length;
	NameReference ref;
	if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
		// single variable reference
		ref =
			new SingleNameReference(
				this.identifierStack[this.identifierPtr],
				this.identifierPositionStack[this.identifierPtr--]);
		ref.bits &= ~ASTNode.RestrictiveFlagMASK;
		ref.bits |= Binding.LOCAL | Binding.FIELD;
		return ref;
	}

	//Qualified-variable-reference
	//In fact it is variable-reference DOT field-ref , but it would result in a type
	//conflict tha can be only reduce by making a superclass (or inetrface ) between
	//nameReference and FiledReference or putting FieldReference under NameReference
	//or else..........This optimisation is not really relevant so just leave as it is

	char[][] tokens = new char[length][];
	this.identifierPtr -= length;
	System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
	long[] positions = new long[length];
	System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
	ref = new QualifiedNameReference(
			tokens,
			positions,
			(int) (this.identifierPositionStack[this.identifierPtr + 1] >> 32), // sourceStart
			(int) this.identifierPositionStack[this.identifierPtr + length]); // sourceEnd
	ref.bits &= ~ASTNode.RestrictiveFlagMASK;
	ref.bits |= Binding.LOCAL | Binding.FIELD;
	return ref;
}
public void goForBlockStatementsopt() {
	//tells the scanner to go for block statements opt parsing

	this.firstToken = TokenNameTWIDDLE;
	this.scanner.recordLineSeparator = false;
}
public void goForBlockStatementsOrCatchHeader() {
	//tells the scanner to go for block statements or method headers parsing

	this.firstToken = TokenNameMULTIPLY;
	this.scanner.recordLineSeparator = false;
}
public void goForClassBodyDeclarations() {
	//tells the scanner to go for any body declarations parsing

	this.firstToken = TokenNameAND;
	this.scanner.recordLineSeparator = true;
}
public void goForCompilationUnit(){
	//tells the scanner to go for compilation unit parsing

	this.firstToken = TokenNamePLUS_PLUS ;
	this.scanner.foundTaskCount = 0;
	this.scanner.recordLineSeparator = true;
}
public void goForExpression() {
	//tells the scanner to go for an expression parsing

	this.firstToken = TokenNameREMAINDER;
	this.scanner.recordLineSeparator = true; // recovery goals must record line separators
}
public void goForFieldDeclaration(){
	//tells the scanner to go for field declaration parsing

	this.firstToken = TokenNameAND_AND ;
	this.scanner.recordLineSeparator = true;
}
public void goForGenericMethodDeclaration(){
	//tells the scanner to go for generic method declarations parsing

	this.firstToken = TokenNameDIVIDE;
	this.scanner.recordLineSeparator = true;
}
public void goForHeaders(){
	//tells the scanner to go for headers only parsing
	RecoveredType currentType = currentRecoveryType();
	if(currentType != null && currentType.insideEnumConstantPart) {
		this.firstToken = TokenNameNOT;
	} else {
		this.firstToken = TokenNameUNSIGNED_RIGHT_SHIFT;
	}
	this.scanner.recordLineSeparator = true; // recovery goals must record line separators
}
public void goForImportDeclaration(){
	//tells the scanner to go for import declaration parsing

	this.firstToken = TokenNameOR_OR ;
	this.scanner.recordLineSeparator = true;
}
public void goForInitializer(){
	//tells the scanner to go for initializer parsing

	this.firstToken = TokenNameRIGHT_SHIFT ;
	this.scanner.recordLineSeparator = false;
}
public void goForMemberValue() {
	//tells the scanner to go for a member value parsing

	this.firstToken = TokenNameOR_OR;
	this.scanner.recordLineSeparator = true; // recovery goals must record line separators
}
public void goForMethodBody(){
	//tells the scanner to go for method body parsing

	this.firstToken = TokenNameMINUS_MINUS ;
	this.scanner.recordLineSeparator = false;
}
public void goForPackageDeclaration() {
	//tells the scanner to go for package declaration parsing

	this.firstToken = TokenNameQUESTION;
	this.scanner.recordLineSeparator = true;
}
public void goForTypeDeclaration() {
	//tells the scanner to go for type (interface or class) declaration parsing

	this.firstToken = TokenNamePLUS;
	this.scanner.recordLineSeparator = true;
}
/**
 * Look for a specific tag comment leading a given source range (comment located after any statement in astStack)
 * @param rangeEnd int
 * @return boolean
 */
public boolean hasLeadingTagComment(char[] commentPrefixTag, int rangeEnd) {
	int iComment = this.scanner.commentPtr;
	if (iComment < 0) return false; // no comment available
	int iStatement = this.astLengthPtr;
	if (iStatement < 0 || this.astLengthStack[iStatement] <= 1) return false; // no statement available
	// Fallthrough comment must be located after the previous statement
	ASTNode lastNode = this.astStack[this.astPtr];
	int rangeStart = lastNode.sourceEnd;
	previousComment: for (; iComment >= 0; iComment--) {
		int commentStart = this.scanner.commentStarts[iComment];
		if (commentStart < 0) commentStart = -commentStart; // line comments have negative start positions
		// ignore comments before start
		if (commentStart < rangeStart) return false; // no more comments in range
		// ignore comments after end
		if (commentStart > rangeEnd) continue previousComment;
		// found last comment in range - only check the last comment in range
		char[] source = this.scanner.source;
		int charPos = commentStart+2; // skip // or /*
		// tag can be leaded by optional spaces
		for (; charPos < rangeEnd; charPos++) {
			char c = source[charPos];
			if (c >= ScannerHelper.MAX_OBVIOUS || (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_JLS_SPACE) == 0) {
				break;
			}
		}
		for (int iTag = 0, length = commentPrefixTag.length; iTag < length; iTag++, charPos++) {
			if (charPos >= rangeEnd) return false; // comment is too small to host tag
			if (source[charPos] != commentPrefixTag[iTag]) return false;
		}
		return true;
	}
	return false;
}
protected void ignoreExpressionAssignment() {
	// Assignment ::= InvalidArrayInitializerAssignement
	// encoded operator would be: this.intStack[this.intPtr]
	this.intPtr--;
	ArrayInitializer arrayInitializer = (ArrayInitializer) this.expressionStack[this.expressionPtr--];
	this.expressionLengthPtr -- ;
	// report a syntax error and abort parsing
	if(!this.statementRecoveryActivated) problemReporter().arrayConstantsOnlyInArrayInitializers(arrayInitializer.sourceStart, arrayInitializer.sourceEnd);
}
public void initialize() {
	this.initialize(false);
}
public void initialize(boolean initializeNLS) {
	//positionning the parser for a new compilation unit
	//avoiding stack reallocation and all that....
	this.astPtr = -1;
	this.astLengthPtr = -1;
	this.expressionPtr = -1;
	this.expressionLengthPtr = -1;
	this.identifierPtr = -1;
	this.identifierLengthPtr	= -1;
	this.intPtr = -1;
	this.nestedMethod[this.nestedType = 0] = 0; // need to reset for further reuse
	this.variablesCounter[this.nestedType] = 0;
	this.dimensions = 0 ;
	this.realBlockPtr = -1;
	this.compilationUnit = null;
	this.referenceContext = null;
	this.endStatementPosition = 0;

	//remove objects from stack too, while the same parser/compiler couple is
	//re-used between two compilations ....

	int astLength = this.astStack.length;
	if (this.noAstNodes.length < astLength){
		this.noAstNodes = new ASTNode[astLength];
		//System.out.println("Resized AST stacks : "+ astLength);

	}
	System.arraycopy(this.noAstNodes, 0, this.astStack, 0, astLength);

	int expressionLength = this.expressionStack.length;
	if (this.noExpressions.length < expressionLength){
		this.noExpressions = new Expression[expressionLength];
		//System.out.println("Resized EXPR stacks : "+ expressionLength);
	}
	System.arraycopy(this.noExpressions, 0, this.expressionStack, 0, expressionLength);

	// reset this.scanner state
	this.scanner.commentPtr = -1;
	this.scanner.foundTaskCount = 0;
	this.scanner.eofPosition = Integer.MAX_VALUE;
	this.recordStringLiterals = true;
	final boolean checkNLS = this.options.getSeverity(CompilerOptions.NonExternalizedString) != ProblemSeverities.Ignore;
	this.checkExternalizeStrings = checkNLS;
	this.scanner.checkNonExternalizedStringLiterals = initializeNLS && checkNLS;
	this.scanner.lastPosition = -1;

	resetModifiers();

	// recovery
	this.lastCheckPoint = -1;
	this.currentElement = null;
	this.restartRecovery = false;
	this.hasReportedError = false;
	this.recoveredStaticInitializerStart = 0;
	this.lastIgnoredToken = -1;
	this.lastErrorEndPosition = -1;
	this.lastErrorEndPositionBeforeRecovery = -1;
	this.lastJavadocEnd = -1;
	this.listLength = 0;
	this.listTypeParameterLength = 0;
	this.lastPosistion = -1;

	this.rBraceStart = 0;
	this.rBraceEnd = 0;
	this.rBraceSuccessorStart = 0;

	this.genericsIdentifiersLengthPtr = -1;
	this.genericsLengthPtr = -1;
	this.genericsPtr = -1;
}
public void initializeScanner(){
	this.scanner = new Scanner(
		false /*comment*/,
		false /*whitespace*/,
		false, /* will be set in initialize(boolean) */
		this.options.sourceLevel /*sourceLevel*/,
		this.options.complianceLevel /*complianceLevel*/,
		this.options.taskTags/*taskTags*/,
		this.options.taskPriorities/*taskPriorities*/,
		this.options.isTaskCaseSensitive/*taskCaseSensitive*/);
}
public void jumpOverMethodBody() {
	//on diet parsing.....do not buffer method statements

	//the scanner.diet is reinitialized to false
	//automatically by the scanner once it has jumped over
	//the statements

	if (this.diet && (this.dietInt == 0))
		this.scanner.diet = true;
}
private void jumpOverType(){
	if (this.recoveredTypes != null && this.nextTypeStart > -1 && this.nextTypeStart < this.scanner.currentPosition) {

		if (DEBUG_AUTOMATON) {
			System.out.println("Jump         -"); //$NON-NLS-1$
		}

		TypeDeclaration typeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
		boolean isAnonymous = typeDeclaration.allocation != null;

		this.scanner.startPosition = typeDeclaration.declarationSourceEnd + 1;
		this.scanner.currentPosition = typeDeclaration.declarationSourceEnd + 1;
		this.scanner.diet = false; // quit jumping over method bodies

		if(!isAnonymous) {
			((RecoveryScanner)this.scanner).setPendingTokens(new int[]{TokenNameSEMICOLON, TokenNamebreak});
		} else {
			((RecoveryScanner)this.scanner).setPendingTokens(new int[]{TokenNameIdentifier, TokenNameEQUAL, TokenNameIdentifier});
		}

		this.pendingRecoveredType = typeDeclaration;

		try {
			this.currentToken = this.scanner.getNextToken();
		} catch(InvalidInputException e){
			// it's impossible because we added pending tokens before
		}

		if(++this.recoveredTypePtr < this.recoveredTypes.length) {
			TypeDeclaration nextTypeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
			this.nextTypeStart =
				nextTypeDeclaration.allocation == null
					? nextTypeDeclaration.declarationSourceStart
							: nextTypeDeclaration.allocation.sourceStart;
		} else {
			this.nextTypeStart = Integer.MAX_VALUE;
		}
	}
}
protected void markEnclosingMemberWithLocalType() {
	if (this.currentElement != null) return; // this is already done in the recovery code
	for (int i = this.astPtr; i >= 0; i--) {
		ASTNode node = this.astStack[i];
		if (node instanceof AbstractMethodDeclaration
				|| node instanceof FieldDeclaration
				|| (node instanceof TypeDeclaration // mark type for now: all initializers will be marked when added to this type
						// and enclosing type must not be closed (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=147485)
						&& ((TypeDeclaration) node).declarationSourceEnd == 0)) {
			node.bits |= ASTNode.HasLocalType;
			return;
		}
	}
	// default to reference context (case of parse method body)
	if (this.referenceContext instanceof AbstractMethodDeclaration
			|| this.referenceContext instanceof TypeDeclaration) {
		((ASTNode)this.referenceContext).bits |= ASTNode.HasLocalType;
	}
}

/*
 * Move checkpoint location (current implementation is moving it by one token)
 *
 * Answers true if successfully moved checkpoint (in other words, it did not attempt to move it
 * beyond end of file).
 */
protected boolean moveRecoveryCheckpoint() {

	int pos = this.lastCheckPoint;
	/* reset this.scanner, and move checkpoint by one token */
	this.scanner.startPosition = pos;
	this.scanner.currentPosition = pos;
	this.scanner.diet = false; // quit jumping over method bodies

	/* if about to restart, then no need to shift token */
	if (this.restartRecovery){
		this.lastIgnoredToken = -1;
		this.scanner.insideRecovery = true;
		return true;
	}

	/* protect against shifting on an invalid token */
	this.lastIgnoredToken = this.nextIgnoredToken;
	this.nextIgnoredToken = -1;
	do {
		try {
			this.nextIgnoredToken = this.scanner.getNextToken();
			if(this.scanner.currentPosition == this.scanner.startPosition){
				this.scanner.currentPosition++; // on fake completion identifier
				this.nextIgnoredToken = -1;
			}

		} catch(InvalidInputException e){
			pos = this.scanner.currentPosition;
		}
	} while (this.nextIgnoredToken < 0);

	if (this.nextIgnoredToken == TokenNameEOF) { // no more recovery after this point
		if (this.currentToken == TokenNameEOF) { // already tried one iteration on EOF
			return false;
		}
	}
	this.lastCheckPoint = this.scanner.currentPosition;

	/* reset this.scanner again to previous checkpoint location*/
	this.scanner.startPosition = pos;
	this.scanner.currentPosition = pos;
	this.scanner.commentPtr = -1;
	this.scanner.foundTaskCount = 0;
	return true;

/*
 	The following implementation moves the checkpoint location by one line:

	int pos = this.lastCheckPoint;
	// reset this.scanner, and move checkpoint by one token
	this.scanner.startPosition = pos;
	this.scanner.currentPosition = pos;
	this.scanner.diet = false; // quit jumping over method bodies

	// if about to restart, then no need to shift token
	if (this.restartRecovery){
		this.lastIgnoredToken = -1;
		return true;
	}

	// protect against shifting on an invalid token
	this.lastIgnoredToken = this.nextIgnoredToken;
	this.nextIgnoredToken = -1;

	boolean wasTokenizingWhiteSpace = this.scanner.tokenizeWhiteSpace;
	this.scanner.tokenizeWhiteSpace = true;
	checkpointMove:
		do {
			try {
				this.nextIgnoredToken = this.scanner.getNextToken();
				switch(this.nextIgnoredToken){
					case Scanner.TokenNameWHITESPACE :
						if(this.scanner.getLineNumber(this.scanner.startPosition)
							== this.scanner.getLineNumber(this.scanner.currentPosition)){
							this.nextIgnoredToken = -1;
							}
						break;
					case TokenNameSEMICOLON :
					case TokenNameLBRACE :
					case TokenNameRBRACE :
						break;
					case TokenNameIdentifier :
						if(this.scanner.currentPosition == this.scanner.startPosition){
							this.scanner.currentPosition++; // on fake completion identifier
						}
					default:
						this.nextIgnoredToken = -1;
						break;
					case TokenNameEOF :
						break checkpointMove;
				}
			} catch(InvalidInputException e){
				pos = this.scanner.currentPosition;
			}
		} while (this.nextIgnoredToken < 0);
	this.scanner.tokenizeWhiteSpace = wasTokenizingWhiteSpace;

	if (this.nextIgnoredToken == TokenNameEOF) { // no more recovery after this point
		if (this.currentToken == TokenNameEOF) { // already tried one iteration on EOF
			return false;
		}
	}
	this.lastCheckPoint = this.scanner.currentPosition;

	// reset this.scanner again to previous checkpoint location
	this.scanner.startPosition = pos;
	this.scanner.currentPosition = pos;
	this.scanner.commentPtr = -1;

	return true;
*/
}
protected MessageSend newMessageSend() {
	// '(' ArgumentListopt ')'
	// the arguments are on the expression stack

	MessageSend m = new MessageSend();
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			m.arguments = new Expression[length],
			0,
			length);
	}
	return m;
}
protected MessageSend newMessageSendWithTypeArguments() {
	MessageSend m = new MessageSend();
	int length;
	if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
		this.expressionPtr -= length;
		System.arraycopy(
			this.expressionStack,
			this.expressionPtr + 1,
			m.arguments = new Expression[length],
			0,
			length);
	}
	return m;
}
protected void optimizedConcatNodeLists() {
	/*back from a recursive loop. Virtualy group the
	astNode into an array using this.astLengthStack*/

	/*
	 * This is a case where you have two sublists into the this.astStack that you want
	 * to merge in one list. There is no action required on the this.astStack. The only
	 * thing you need to do is merge the two lengths specified on the astStackLength.
	 * The top two length are for example:
	 * ... p   n
	 * and you want to result in a list like:
	 * ... n+p
	 * This means that the p could be equals to 0 in case there is no astNode pushed
	 * on the this.astStack.
	 * Look at the InterfaceMemberDeclarations for an example.
	 * This case optimizes the fact that p == 1.
	 */

	this.astLengthStack[--this.astLengthPtr]++;
}
/*main loop of the automat
When a rule is reduced, the method consumeRule(int) is called with the number
of the consumed rule. When a terminal is consumed, the method consumeToken(int) is
called in order to remember (when needed) the consumed token */
// (int)asr[asi(act)]
// name[symbol_index[currentKind]]
protected void parse() {
	if (DEBUG) System.out.println("-- ENTER INSIDE PARSE METHOD --");  //$NON-NLS-1$

	if (DEBUG_AUTOMATON) {
		System.out.println("- Start --------------------------------");  //$NON-NLS-1$
	}

	boolean isDietParse = this.diet;
	int oldFirstToken = getFirstToken();
	this.hasError = false;

	this.hasReportedError = false;
	int act = START_STATE;
	this.stateStackTop = -1;
	this.currentToken = getFirstToken();
	ProcessTerminals : for (;;) {
		int stackLength = this.stack.length;
		if (++this.stateStackTop >= stackLength) {
			System.arraycopy(
				this.stack, 0,
				this.stack = new int[stackLength + StackIncrement], 0,
				stackLength);
		}
		this.stack[this.stateStackTop] = act;

		act = tAction(act, this.currentToken);
		if (act == ERROR_ACTION || this.restartRecovery) {

			if (DEBUG_AUTOMATON) {
				if (this.restartRecovery) {
					System.out.println("Restart      - "); //$NON-NLS-1$
				} else {
					System.out.println("Error        - "); //$NON-NLS-1$
				}
			}

			int errorPos = this.scanner.currentPosition - 1;
			if (!this.hasReportedError) {
				this.hasError = true;
			}
			int previousToken = this.currentToken;
			if (resumeOnSyntaxError()) {
				if (act == ERROR_ACTION && previousToken != 0) this.lastErrorEndPosition = errorPos;
				act = START_STATE;
				this.stateStackTop = -1;
				this.currentToken = getFirstToken();
				continue ProcessTerminals;
			}
			act = ERROR_ACTION;
			break ProcessTerminals;
		}
		if (act <= NUM_RULES) {
			this.stateStackTop--;

			if (DEBUG_AUTOMATON) {
				System.out.print("Reduce       - "); //$NON-NLS-1$
			}

		} else if (act > ERROR_ACTION) { /* shift-reduce */
			consumeToken(this.currentToken);
			if (this.currentElement != null) {
				boolean oldValue = this.recordStringLiterals;
				this.recordStringLiterals = false;
				recoveryTokenCheck();
				this.recordStringLiterals = oldValue;
			}
			try {
				this.currentToken = this.scanner.getNextToken();
			} catch(InvalidInputException e){
				if (!this.hasReportedError){
					problemReporter().scannerError(this, e.getMessage());
					this.hasReportedError = true;
				}
				this.lastCheckPoint = this.scanner.currentPosition;
				this.currentToken = 0;
				this.restartRecovery = true;
			}
			if(this.statementRecoveryActivated) {
				jumpOverType();
			}
			act -= ERROR_ACTION;

			if (DEBUG_AUTOMATON) {
				System.out.print("Shift/Reduce - (" + name[terminal_index[this.currentToken]]+") ");  //$NON-NLS-1$  //$NON-NLS-2$
			}

		} else {
		    if (act < ACCEPT_ACTION) { /* shift */
				consumeToken(this.currentToken);
				if (this.currentElement != null) {
					boolean oldValue = this.recordStringLiterals;
					this.recordStringLiterals = false;
					recoveryTokenCheck();
					this.recordStringLiterals = oldValue;
				}
				try{
					this.currentToken = this.scanner.getNextToken();
				} catch(InvalidInputException e){
					if (!this.hasReportedError){
						problemReporter().scannerError(this, e.getMessage());
						this.hasReportedError = true;
					}
					this.lastCheckPoint = this.scanner.currentPosition;
					this.currentToken = 0;
					this.restartRecovery = true;
				}
				if(this.statementRecoveryActivated) {
					jumpOverType();
				}
				if (DEBUG_AUTOMATON) {
					System.out.println("Shift        - (" + name[terminal_index[this.currentToken]]+")");  //$NON-NLS-1$  //$NON-NLS-2$
				}
				continue ProcessTerminals;
			}
			break ProcessTerminals;
		}

		// ProcessNonTerminals :
		do { /* reduce */

			if (DEBUG_AUTOMATON) {
				System.out.println(name[non_terminal_index[lhs[act]]]);
			}

			consumeRule(act);
			this.stateStackTop -= (rhs[act] - 1);
			act = ntAction(this.stack[this.stateStackTop], lhs[act]);

			if (DEBUG_AUTOMATON) {
				if (act <= NUM_RULES) {
					System.out.print("             - ");  //$NON-NLS-1$
				}
			}

		} while (act <= NUM_RULES);

		if (DEBUG_AUTOMATON) {
			System.out.println("----------------------------------------");  //$NON-NLS-1$
		}
	}

	if (DEBUG_AUTOMATON) {
		System.out.println("- End ----------------------------------");  //$NON-NLS-1$
	}

	endParse(act);
	// record all nls tags in the corresponding compilation unit
	final NLSTag[] tags = this.scanner.getNLSTags();
	if (tags != null) {
		this.compilationUnit.nlsTags = tags;
	}

	this.scanner.checkNonExternalizedStringLiterals = false;
	if (this.reportSyntaxErrorIsRequired && this.hasError && !this.statementRecoveryActivated) {
		if(!this.options.performStatementsRecovery) {
			reportSyntaxErrors(isDietParse, oldFirstToken);
		} else {
			RecoveryScannerData data = this.referenceContext.compilationResult().recoveryScannerData;

			if(this.recoveryScanner == null) {
				this.recoveryScanner = new RecoveryScanner(this.scanner, data);
			} else {
				this.recoveryScanner.setData(data);
			}

			this.recoveryScanner.setSource(this.scanner.source);
			this.recoveryScanner.lineEnds = this.scanner.lineEnds;
			this.recoveryScanner.linePtr = this.scanner.linePtr;

			reportSyntaxErrors(isDietParse, oldFirstToken);

			if(data == null) {
				this.referenceContext.compilationResult().recoveryScannerData =
					this.recoveryScanner.getData();
			}

			if (this.methodRecoveryActivated && this.options.performStatementsRecovery) {
				this.methodRecoveryActivated = false;
				recoverStatements();
				this.methodRecoveryActivated = true;

				this.lastAct = ERROR_ACTION;
			}
		}
	}

	if (DEBUG) System.out.println("-- EXIT FROM PARSE METHOD --");  //$NON-NLS-1$
}
public void parse(ConstructorDeclaration cd, CompilationUnitDeclaration unit, boolean recordLineSeparator) {
	//only parse the method body of cd
	//fill out its statements

	//convert bugs into parse error

	boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
	if(this.options.performMethodsFullRecovery) {
		this.methodRecoveryActivated = true;
	}

	initialize();
	goForBlockStatementsopt();
	if (recordLineSeparator) {
		this.scanner.recordLineSeparator = true;
	}
	this.nestedMethod[this.nestedType]++;
	pushOnRealBlockStack(0);

	this.referenceContext = cd;
	this.compilationUnit = unit;

	this.scanner.resetTo(cd.bodyStart, cd.bodyEnd);
	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
		if(this.options.performStatementsRecovery) {
			this.methodRecoveryActivated = oldMethodRecoveryActivated;
		}
	}

	checkNonNLSAfterBodyEnd(cd.declarationSourceEnd);

	if (this.lastAct == ERROR_ACTION) {
		cd.bits |= ASTNode.HasSyntaxErrors;
		initialize();
		return;
	}

	//statements
	cd.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
	int length;
	if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		this.astPtr -= length;
		if (!this.options.ignoreMethodBodies) {
			if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall)
				//avoid a isSomeThing that would only be used here BUT what is faster between two alternatives ?
				{
				System.arraycopy(
					this.astStack,
					this.astPtr + 2,
					cd.statements = new Statement[length - 1],
					0,
					length - 1);
				cd.constructorCall = (ExplicitConstructorCall) this.astStack[this.astPtr + 1];
			} else { //need to add explicitly the super();
				System.arraycopy(
					this.astStack,
					this.astPtr + 1,
					cd.statements = new Statement[length],
					0,
					length);
				cd.constructorCall = SuperReference.implicitSuperConstructorCall();
			}
		}
	} else {
		if (!this.options.ignoreMethodBodies) {
			cd.constructorCall = SuperReference.implicitSuperConstructorCall();
		}
		if (!containsComment(cd.bodyStart, cd.bodyEnd)) {
			cd.bits |= ASTNode.UndocumentedEmptyBlock;
		}
	}

	ExplicitConstructorCall explicitConstructorCall = cd.constructorCall;
	if (explicitConstructorCall != null && explicitConstructorCall.sourceEnd == 0) {
		explicitConstructorCall.sourceEnd = cd.sourceEnd;
		explicitConstructorCall.sourceStart = cd.sourceStart;
	}
}
// A P I

public void parse(
	FieldDeclaration field,
	TypeDeclaration type,
	CompilationUnitDeclaration unit,
	char[] initializationSource) {
	//only parse the initializationSource of the given field

	//convert bugs into parse error

	initialize();
	goForExpression();
	this.nestedMethod[this.nestedType]++;

	this.referenceContext = type;
	this.compilationUnit = unit;

	this.scanner.setSource(initializationSource);
	this.scanner.resetTo(0, initializationSource.length-1);
	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
	}

	if (this.lastAct == ERROR_ACTION) {
		field.bits |= ASTNode.HasSyntaxErrors;
		return;
	}

	field.initialization = this.expressionStack[this.expressionPtr];

	// mark field with local type if one was found during parsing
	if ((type.bits & ASTNode.HasLocalType) != 0) {
		field.bits |= ASTNode.HasLocalType;
	}
}
// A P I

public CompilationUnitDeclaration parse(
	ICompilationUnit sourceUnit,
	CompilationResult compilationResult) {
	// parses a compilation unit and manages error handling (even bugs....)

	return parse(sourceUnit, compilationResult, -1, -1/*parse without reseting the scanner*/);
}
// A P I

public CompilationUnitDeclaration parse(
	ICompilationUnit sourceUnit,
	CompilationResult compilationResult,
	int start,
	int end) {
	// parses a compilation unit and manages error handling (even bugs....)

	CompilationUnitDeclaration unit;
	try {
		/* automaton initialization */
		initialize(true);
		goForCompilationUnit();

		/* unit creation */
		this.referenceContext =
			this.compilationUnit =
				new CompilationUnitDeclaration(
					this.problemReporter,
					compilationResult,
					0);

		/* scanners initialization */
		char[] contents;
		try {
			contents = this.readManager != null ? this.readManager.getContents(sourceUnit) : sourceUnit.getContents();
		} catch(AbortCompilationUnit abortException) {
			problemReporter().cannotReadSource(this.compilationUnit, abortException, this.options.verbose);
			contents = CharOperation.NO_CHAR; // pretend empty from thereon
		}
		this.scanner.setSource(contents);
		this.compilationUnit.sourceEnd = this.scanner.source.length - 1;
		if (end != -1) this.scanner.resetTo(start, end);
		if (this.javadocParser != null && this.javadocParser.checkDocComment) {
			this.javadocParser.scanner.setSource(contents);
			if (end != -1) {
				this.javadocParser.scanner.resetTo(start, end);
			}
		}
		/* run automaton */
		parse();
	} finally {
		unit = this.compilationUnit;
		this.compilationUnit = null; // reset parser
		// tag unit has having read bodies
		if (!this.diet) unit.bits |= ASTNode.HasAllMethodBodies;
	}
	return unit;
}
// A P I

public void parse(
	Initializer initializer,
	TypeDeclaration type,
	CompilationUnitDeclaration unit) {
	//only parse the method body of md
	//fill out method statements

	//convert bugs into parse error

	boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
	if(this.options.performMethodsFullRecovery) {
		this.methodRecoveryActivated = true;
	}

	initialize();
	goForBlockStatementsopt();
	this.nestedMethod[this.nestedType]++;
	pushOnRealBlockStack(0);

	this.referenceContext = type;
	this.compilationUnit = unit;

	this.scanner.resetTo(initializer.bodyStart, initializer.bodyEnd); // just on the beginning {
	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
		if(this.options.performStatementsRecovery) {
			this.methodRecoveryActivated = oldMethodRecoveryActivated;
		}
	}

	checkNonNLSAfterBodyEnd(initializer.declarationSourceEnd);

	if (this.lastAct == ERROR_ACTION) {
		initializer.bits |= ASTNode.HasSyntaxErrors;
		return;
	}

	//refill statements
	initializer.block.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
	int length;
	if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) > 0) {
		System.arraycopy(this.astStack, (this.astPtr -= length) + 1, initializer.block.statements = new Statement[length], 0, length);
	} else {
		// check whether this block at least contains some comment in it
		if (!containsComment(initializer.block.sourceStart, initializer.block.sourceEnd)) {
			initializer.block.bits |= ASTNode.UndocumentedEmptyBlock;
		}
	}

	// mark initializer with local type if one was found during parsing
	if ((type.bits & ASTNode.HasLocalType) != 0) {
		initializer.bits |= ASTNode.HasLocalType;
	}
}
// A P I
public void parse(MethodDeclaration md, CompilationUnitDeclaration unit) {
	//only parse the method body of md
	//fill out method statements

	//convert bugs into parse error

	if (md.isAbstract())
		return;
	if (md.isNative())
		return;
	if ((md.modifiers & ExtraCompilerModifiers.AccSemicolonBody) != 0)
		return;

	boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
	if(this.options.performMethodsFullRecovery) {
		this.methodRecoveryActivated = true;
		this.rParenPos = md.sourceEnd;
	}
	initialize();
	goForBlockStatementsopt();
	this.nestedMethod[this.nestedType]++;
	pushOnRealBlockStack(0);

	this.referenceContext = md;
	this.compilationUnit = unit;

	this.scanner.resetTo(md.bodyStart, md.bodyEnd);
	// reset the scanner to parser from { down to }
	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
		if(this.options.performStatementsRecovery) {
			this.methodRecoveryActivated = oldMethodRecoveryActivated;
		}
	}

	checkNonNLSAfterBodyEnd(md.declarationSourceEnd);

	if (this.lastAct == ERROR_ACTION) {
		md.bits |= ASTNode.HasSyntaxErrors;
		return;
	}

	//refill statements
	md.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
	int length;
	if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
		if (this.options.ignoreMethodBodies) {
			// ignore statements
			this.astPtr -= length;
		} else {
			System.arraycopy(
				this.astStack,
				(this.astPtr -= length) + 1,
				md.statements = new Statement[length],
				0,
				length);
		}
	} else {
		if (!containsComment(md.bodyStart, md.bodyEnd)) {
			md.bits |= ASTNode.UndocumentedEmptyBlock;
		}
	}
}
public ASTNode[] parseClassBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
	boolean oldDiet = this.diet;
	/* automaton initialization */
	initialize();
	goForClassBodyDeclarations();
	/* scanner initialization */
	this.scanner.setSource(source);
	this.scanner.resetTo(offset, offset + length - 1);
	if (this.javadocParser != null && this.javadocParser.checkDocComment) {
		this.javadocParser.scanner.setSource(source);
		this.javadocParser.scanner.resetTo(offset, offset + length - 1);
	}

	/* type declaration should be parsed as member type declaration */
	this.nestedType = 1;

	/* unit creation */
	TypeDeclaration referenceContextTypeDeclaration = new TypeDeclaration(unit.compilationResult);
	referenceContextTypeDeclaration.name = Util.EMPTY_STRING.toCharArray();
	referenceContextTypeDeclaration.fields = new FieldDeclaration[0];
	this.compilationUnit = unit;
	unit.types = new TypeDeclaration[1];
	unit.types[0] = referenceContextTypeDeclaration;
	this.referenceContext = unit;

	/* run automaton */
	try {
		this.diet = true;
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.diet = oldDiet;
	}

	ASTNode[] result = null;
	if (this.lastAct == ERROR_ACTION) {
		if (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
			return null;
		}
		// collect all body declaration inside the compilation unit except the default constructor
		final List bodyDeclarations = new ArrayList();
		ASTVisitor visitor = new ASTVisitor() {
			public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
				if (!methodDeclaration.isDefaultConstructor()) {
					bodyDeclarations.add(methodDeclaration);
				}
				return false;
			}
			public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
				bodyDeclarations.add(fieldDeclaration);
				return false;
			}
			public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
				bodyDeclarations.add(memberTypeDeclaration);
				return false;
			}
		};
		unit.ignoreFurtherInvestigation = false;
		unit.traverse(visitor, unit.scope);
		unit.ignoreFurtherInvestigation = true;
		result = (ASTNode[]) bodyDeclarations.toArray(new ASTNode[bodyDeclarations.size()]);
	} else {
		int astLength;
		if (this.astLengthPtr > -1 && (astLength = this.astLengthStack[this.astLengthPtr--]) != 0) {
			result = new ASTNode[astLength];
			this.astPtr -= astLength;
			System.arraycopy(this.astStack, this.astPtr + 1, result, 0, astLength);
		} else {
			// empty class body declaration (like ';' see https://bugs.eclipse.org/bugs/show_bug.cgi?id=280079).
			result = new ASTNode[0];
		}
	}
	boolean containsInitializers = false;
	TypeDeclaration typeDeclaration = null;
	for (int i = 0, max = result.length; i < max; i++) {
		// parse each class body declaration
		ASTNode node = result[i];
		if (node instanceof TypeDeclaration) {
			((TypeDeclaration) node).parseMethods(this, unit);
		} else if (node instanceof AbstractMethodDeclaration) {
			((AbstractMethodDeclaration) node).parseStatements(this, unit);
		} else if (node instanceof FieldDeclaration) {
			FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
			switch(fieldDeclaration.getKind()) {
				case AbstractVariableDeclaration.INITIALIZER:
					containsInitializers = true;
					if (typeDeclaration == null) {
						typeDeclaration = referenceContextTypeDeclaration;
					}
					if (typeDeclaration.fields == null) {
						typeDeclaration.fields = new FieldDeclaration[1];
						typeDeclaration.fields[0] = fieldDeclaration;
					} else {
						int length2 = typeDeclaration.fields.length;
						FieldDeclaration[] temp = new FieldDeclaration[length2 + 1];
						System.arraycopy(typeDeclaration.fields, 0, temp, 0, length2);
						temp[length2] = fieldDeclaration;
						typeDeclaration.fields = temp;
					}
					break;
			}
		}
		if (((node.bits & ASTNode.HasSyntaxErrors) != 0) && (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery)) {
			return null;
		}
	}
	if (containsInitializers) {
		FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
		for (int i = 0, max = fieldDeclarations.length; i < max; i++) {
			Initializer initializer = (Initializer) fieldDeclarations[i];
			initializer.parseStatements(this, typeDeclaration , unit);
			if (((initializer.bits & ASTNode.HasSyntaxErrors) != 0) && (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery)) {
				return null;
			}
		}
	}
	return result;
}
public Expression parseExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit) {

	initialize();
	goForExpression();
	this.nestedMethod[this.nestedType]++;

	this.referenceContext = unit;
	this.compilationUnit = unit;

	this.scanner.setSource(source);
	this.scanner.resetTo(offset, offset + length - 1);
	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
	}

	if (this.lastAct == ERROR_ACTION) {
		return null;
	}

	return this.expressionStack[this.expressionPtr];
}
public Expression parseMemberValue(char[] source, int offset, int length, CompilationUnitDeclaration unit) {

	initialize();
	goForMemberValue();
	this.nestedMethod[this.nestedType]++;

	this.referenceContext = unit;
	this.compilationUnit = unit;

	this.scanner.setSource(source);
	this.scanner.resetTo(offset, offset + length - 1);
	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
	}

	if (this.lastAct == ERROR_ACTION) {
		return null;
	}

	return this.expressionStack[this.expressionPtr];
}
public void parseStatements(ReferenceContext rc, int start, int end, TypeDeclaration[] types, CompilationUnitDeclaration unit) {
	boolean oldStatementRecoveryEnabled = this.statementRecoveryActivated;
	this.statementRecoveryActivated = true;

	initialize();

	goForBlockStatementsopt();
	this.nestedMethod[this.nestedType]++;
	pushOnRealBlockStack(0);

	pushOnAstLengthStack(0);

	this.referenceContext = rc;
	this.compilationUnit = unit;

	this.pendingRecoveredType = null;

	if(types != null && types.length > 0) {
		this.recoveredTypes = types;
		this.recoveredTypePtr = 0;
		this.nextTypeStart =
			this.recoveredTypes[0].allocation == null
				? this.recoveredTypes[0].declarationSourceStart
						: this.recoveredTypes[0].allocation.sourceStart;
	} else {
		this.recoveredTypes = null;
		this.recoveredTypePtr = -1;
		this.nextTypeStart = -1;
	}

	this.scanner.resetTo(start, end);
	// reset the scanner to parser from { down to }

	this.lastCheckPoint = this.scanner.initialPosition;


	this.stateStackTop = -1;

	try {
		parse();
	} catch (AbortCompilation ex) {
		this.lastAct = ERROR_ACTION;
	} finally {
		this.nestedMethod[this.nestedType]--;
		this.recoveredTypes = null;
		this.statementRecoveryActivated = oldStatementRecoveryEnabled;
	}

	checkNonNLSAfterBodyEnd(end);
}
public void persistLineSeparatorPositions() {
	if (this.scanner.recordLineSeparator) {
		this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
	}
}
/*
 * Prepares the state of the parser to go for BlockStatements.
 */
protected void prepareForBlockStatements() {
	this.nestedMethod[this.nestedType = 0] = 1;
	this.variablesCounter[this.nestedType] = 0;
	this.realBlockStack[this.realBlockPtr = 1] = 0;
}
/**
 * Returns this parser's problem reporter initialized with its reference context.
 * Also it is assumed that a problem is going to be reported, so initializes
 * the compilation result's line positions.
 *
 * @return ProblemReporter
 */
public ProblemReporter problemReporter(){
	if (this.scanner.recordLineSeparator) {
		this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
	}
	this.problemReporter.referenceContext = this.referenceContext;
	return this.problemReporter;
}
protected void pushIdentifier() {
	/*push the consumeToken on the identifier stack.
	Increase the total number of identifier in the stack.
	identifierPtr points on the next top */

	int stackLength = this.identifierStack.length;
	if (++this.identifierPtr >= stackLength) {
		System.arraycopy(
			this.identifierStack, 0,
			this.identifierStack = new char[stackLength + 20][], 0,
			stackLength);
		System.arraycopy(
			this.identifierPositionStack, 0,
			this.identifierPositionStack = new long[stackLength + 20], 0,
			stackLength);
	}
	this.identifierStack[this.identifierPtr] = this.scanner.getCurrentIdentifierSource();
	this.identifierPositionStack[this.identifierPtr] =
		(((long) this.scanner.startPosition) << 32) + (this.scanner.currentPosition - 1);

	stackLength = this.identifierLengthStack.length;
	if (++this.identifierLengthPtr >= stackLength) {
		System.arraycopy(
			this.identifierLengthStack, 0,
			this.identifierLengthStack = new int[stackLength + 10], 0,
			stackLength);
	}
	this.identifierLengthStack[this.identifierLengthPtr] = 1;
}
protected void pushIdentifier(int flag) {
	/*push a special flag on the stack :
	-zero stands for optional Name
	-negative number for direct ref to base types.
	identifierLengthPtr points on the top */

	int stackLength = this.identifierLengthStack.length;
	if (++this.identifierLengthPtr >= stackLength) {
		System.arraycopy(
			this.identifierLengthStack, 0,
			this.identifierLengthStack = new int[stackLength + 10], 0,
			stackLength);
	}
	this.identifierLengthStack[this.identifierLengthPtr] = flag;
}
protected void pushOnAstLengthStack(int pos) {

	int stackLength = this.astLengthStack.length;
	if (++this.astLengthPtr >= stackLength) {
		System.arraycopy(
			this.astLengthStack, 0,
			this.astLengthStack = new int[stackLength + StackIncrement], 0,
			stackLength);
	}
	this.astLengthStack[this.astLengthPtr] = pos;
}
protected void pushOnAstStack(ASTNode node) {
	/*add a new obj on top of the ast stack
	astPtr points on the top*/

	int stackLength = this.astStack.length;
	if (++this.astPtr >= stackLength) {
		System.arraycopy(
			this.astStack, 0,
			this.astStack = new ASTNode[stackLength + AstStackIncrement], 0,
			stackLength);
		this.astPtr = stackLength;
	}
	this.astStack[this.astPtr] = node;

	stackLength = this.astLengthStack.length;
	if (++this.astLengthPtr >= stackLength) {
		System.arraycopy(
			this.astLengthStack, 0,
			this.astLengthStack = new int[stackLength + AstStackIncrement], 0,
			stackLength);
	}
	this.astLengthStack[this.astLengthPtr] = 1;
}
protected void pushOnExpressionStack(Expression expr) {

	int stackLength = this.expressionStack.length;
	if (++this.expressionPtr >= stackLength) {
		System.arraycopy(
			this.expressionStack, 0,
			this.expressionStack = new Expression[stackLength + ExpressionStackIncrement], 0,
			stackLength);
	}
	this.expressionStack[this.expressionPtr] = expr;

	stackLength = this.expressionLengthStack.length;
	if (++this.expressionLengthPtr >= stackLength) {
		System.arraycopy(
			this.expressionLengthStack, 0,
			this.expressionLengthStack = new int[stackLength + ExpressionStackIncrement], 0,
			stackLength);
	}
	this.expressionLengthStack[this.expressionLengthPtr] = 1;
}
protected void pushOnExpressionStackLengthStack(int pos) {

	int stackLength = this.expressionLengthStack.length;
	if (++this.expressionLengthPtr >= stackLength) {
		System.arraycopy(
			this.expressionLengthStack, 0,
			this.expressionLengthStack = new int[stackLength + StackIncrement], 0,
			stackLength);
	}
	this.expressionLengthStack[this.expressionLengthPtr] = pos;
}
protected void pushOnGenericsIdentifiersLengthStack(int pos) {
	int stackLength = this.genericsIdentifiersLengthStack.length;
	if (++this.genericsIdentifiersLengthPtr >= stackLength) {
		System.arraycopy(
			this.genericsIdentifiersLengthStack, 0,
			this.genericsIdentifiersLengthStack = new int[stackLength + GenericsStackIncrement], 0,
			stackLength);
	}
	this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] = pos;
}
protected void pushOnGenericsLengthStack(int pos) {
	int stackLength = this.genericsLengthStack.length;
	if (++this.genericsLengthPtr >= stackLength) {
		System.arraycopy(
			this.genericsLengthStack, 0,
			this.genericsLengthStack = new int[stackLength + GenericsStackIncrement], 0,
			stackLength);
	}
	this.genericsLengthStack[this.genericsLengthPtr] = pos;
}
protected void pushOnGenericsStack(ASTNode node) {
	/*add a new obj on top of the generics stack
	genericsPtr points on the top*/

	int stackLength = this.genericsStack.length;
	if (++this.genericsPtr >= stackLength) {
		System.arraycopy(
			this.genericsStack, 0,
			this.genericsStack = new ASTNode[stackLength + GenericsStackIncrement], 0,
			stackLength);
	}
	this.genericsStack[this.genericsPtr] = node;

	stackLength = this.genericsLengthStack.length;
	if (++this.genericsLengthPtr >= stackLength) {
		System.arraycopy(
			this.genericsLengthStack, 0,
			this.genericsLengthStack = new int[stackLength + GenericsStackIncrement], 0,
			stackLength);
	}
	this.genericsLengthStack[this.genericsLengthPtr] = 1;
}
protected void pushOnIntStack(int pos) {

	int stackLength = this.intStack.length;
	if (++this.intPtr >= stackLength) {
		System.arraycopy(
			this.intStack, 0,
			this.intStack = new int[stackLength + StackIncrement], 0,
			stackLength);
	}
	this.intStack[this.intPtr] = pos;
}
protected void pushOnRealBlockStack(int i){

	int stackLength = this.realBlockStack.length;
	if (++this.realBlockPtr >= stackLength) {
		System.arraycopy(
			this.realBlockStack, 0,
			this.realBlockStack = new int[stackLength + StackIncrement], 0,
			stackLength);
	}
	this.realBlockStack[this.realBlockPtr] = i;
}
protected void recoverStatements() {
	class MethodVisitor extends ASTVisitor {
		public ASTVisitor typeVisitor;

		TypeDeclaration enclosingType; // used only for initializer

		TypeDeclaration[] types = new TypeDeclaration[0];
		int typePtr = -1;
		public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
			endVisitMethod(constructorDeclaration, scope);
		}
		public void endVisit(Initializer initializer, MethodScope scope) {
			if (initializer.block == null) return;
			TypeDeclaration[] foundTypes = null;
			int length = 0;
			if(this.typePtr > -1) {
				length = this.typePtr + 1;
				foundTypes = new TypeDeclaration[length];
				System.arraycopy(this.types, 0, foundTypes, 0, length);
			}
			ReferenceContext oldContext = Parser.this.referenceContext;
			Parser.this.recoveryScanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
			Scanner oldScanner = Parser.this.scanner;
			Parser.this.scanner = Parser.this.recoveryScanner;
			parseStatements(
					this.enclosingType,
					initializer.bodyStart,
					initializer.bodyEnd,
					foundTypes,
					Parser.this.compilationUnit);
			Parser.this.scanner = oldScanner;
			Parser.this.referenceContext = oldContext;

			for (int i = 0; i < length; i++) {
				foundTypes[i].traverse(this.typeVisitor, scope);
			}
		}
		public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
			endVisitMethod(methodDeclaration, scope);
		}
		private void endVisitMethod(AbstractMethodDeclaration methodDeclaration, ClassScope scope) {
			TypeDeclaration[] foundTypes = null;
			int length = 0;
			if(this.typePtr > -1) {
				length = this.typePtr + 1;
				foundTypes = new TypeDeclaration[length];
				System.arraycopy(this.types, 0, foundTypes, 0, length);
			}
			ReferenceContext oldContext = Parser.this.referenceContext;
			Parser.this.recoveryScanner.resetTo(methodDeclaration.bodyStart, methodDeclaration.bodyEnd);
			Scanner oldScanner = Parser.this.scanner;
			Parser.this.scanner = Parser.this.recoveryScanner;
			parseStatements(
					methodDeclaration,
					methodDeclaration.bodyStart,
					methodDeclaration.bodyEnd,
					foundTypes,
					Parser.this.compilationUnit);
			Parser.this.scanner = oldScanner;
			Parser.this.referenceContext = oldContext;

			for (int i = 0; i < length; i++) {
				foundTypes[i].traverse(this.typeVisitor, scope);
			}
		}
		public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
			this.typePtr = -1;
			return true;
		}
		public boolean visit(Initializer initializer, MethodScope scope) {
			this.typePtr = -1;
			if (initializer.block == null) return false;
			return true;
		}
		public boolean visit(MethodDeclaration methodDeclaration,ClassScope scope) {
			this.typePtr = -1;
			return true;
		}
		private boolean visit(TypeDeclaration typeDeclaration) {
			if(this.types.length <= ++this.typePtr) {
				int length = this.typePtr;
				System.arraycopy(this.types, 0, this.types = new TypeDeclaration[length * 2 + 1], 0, length);
			}
			this.types[this.typePtr] = typeDeclaration;
			return false;
		}
		public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
			return this.visit(typeDeclaration);
		}
		public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
			return this.visit(typeDeclaration);
		}
	}
	class TypeVisitor extends ASTVisitor {
		public MethodVisitor methodVisitor;

		TypeDeclaration[] types = new TypeDeclaration[0];
		int typePtr = -1;

		public void endVisit(TypeDeclaration typeDeclaration, BlockScope scope) {
			endVisitType();
		}
		public void endVisit(TypeDeclaration typeDeclaration, ClassScope scope) {
			endVisitType();
		}
		private void endVisitType() {
			this.typePtr--;
		}
		public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
			if(constructorDeclaration.isDefaultConstructor()) return false;

			constructorDeclaration.traverse(this.methodVisitor, scope);
			return false;
		}
		public boolean visit(Initializer initializer, MethodScope scope) {
			if (initializer.block == null) return false;
			this.methodVisitor.enclosingType = this.types[this.typePtr];
			initializer.traverse(this.methodVisitor, scope);
			return false;
		}
		public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
			methodDeclaration.traverse(this.methodVisitor, scope);
			return false;
		}
		private boolean visit(TypeDeclaration typeDeclaration) {
			if(this.types.length <= ++this.typePtr) {
				int length = this.typePtr;
				System.arraycopy(this.types, 0, this.types = new TypeDeclaration[length * 2 + 1], 0, length);
			}
			this.types[this.typePtr] = typeDeclaration;
			return true;
		}
		public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
			return this.visit(typeDeclaration);
		}
		public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
			return this.visit(typeDeclaration);
		}
	}

	MethodVisitor methodVisitor = new MethodVisitor();
	TypeVisitor typeVisitor = new TypeVisitor();
	methodVisitor.typeVisitor = typeVisitor;
	typeVisitor.methodVisitor = methodVisitor;

	if(this.referenceContext instanceof AbstractMethodDeclaration) {
		((AbstractMethodDeclaration)this.referenceContext).traverse(methodVisitor, (ClassScope)null);
	} else if(this.referenceContext instanceof TypeDeclaration) {
		TypeDeclaration typeContext = (TypeDeclaration)this.referenceContext;

		int length = typeContext.fields.length;
		for (int i = 0; i < length; i++) {
			final FieldDeclaration fieldDeclaration = typeContext.fields[i];
			switch(fieldDeclaration.getKind()) {
				case AbstractVariableDeclaration.INITIALIZER:
					Initializer initializer = (Initializer) fieldDeclaration;
					if (initializer.block == null) break;
					methodVisitor.enclosingType = typeContext;
					initializer.traverse(methodVisitor, (MethodScope)null);
					break;
			}
		}
	}
}

public void recoveryExitFromVariable() {
	if(this.currentElement != null && this.currentElement.parent != null) {
		if(this.currentElement instanceof RecoveredLocalVariable) {

			int end = ((RecoveredLocalVariable)this.currentElement).localDeclaration.sourceEnd;
			this.currentElement.updateSourceEndIfNecessary(end);
			this.currentElement = this.currentElement.parent;
		} else if(this.currentElement instanceof RecoveredField
			&& !(this.currentElement instanceof RecoveredInitializer)) {
			// Do not move focus to parent if we are still inside an array initializer
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=292087 
			if (this.currentElement.bracketBalance <= 0) {
				int end = ((RecoveredField)this.currentElement).fieldDeclaration.sourceEnd;
				this.currentElement.updateSourceEndIfNecessary(end);
				this.currentElement = this.currentElement.parent;
			}
		}
	}
}
/* Token check performed on every token shift once having entered
 * recovery mode.
 */
public void recoveryTokenCheck() {
	switch (this.currentToken) {
		case TokenNameStringLiteral :
			if (this.recordStringLiterals &&
					this.checkExternalizeStrings &&
					this.lastPosistion < this.scanner.currentPosition &&
					!this.statementRecoveryActivated) {
				StringLiteral stringLiteral = createStringLiteral(
					this.scanner.getCurrentTokenSourceString(),
					this.scanner.startPosition,
					this.scanner.currentPosition - 1,
					Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
				this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
			}
			break;
		case TokenNameLBRACE :
			RecoveredElement newElement = null;
			if(!this.ignoreNextOpeningBrace) {
				newElement = this.currentElement.updateOnOpeningBrace(this.scanner.startPosition - 1, this.scanner.currentPosition - 1);
			}
			this.lastCheckPoint = this.scanner.currentPosition;
			if (newElement != null){ // null means nothing happened
				this.restartRecovery = true; // opening brace detected
				this.currentElement = newElement;
			}
			break;

		case TokenNameRBRACE :
			this.rBraceStart = this.scanner.startPosition - 1;
			this.rBraceEnd = this.scanner.currentPosition - 1;
			this.endPosition = flushCommentsDefinedPriorTo(this.rBraceEnd);
			newElement =
				this.currentElement.updateOnClosingBrace(this.scanner.startPosition, this.rBraceEnd);
				this.lastCheckPoint = this.scanner.currentPosition;
			if (newElement != this.currentElement){
				this.currentElement = newElement;
//				if (newElement instanceof RecoveredField && this.dietInt <= 0) {
//					if (((RecoveredField)newElement).fieldDeclaration.type == null) { // enum constant
//						this.isInsideEnumConstantPart = true; // restore status
//					}
//				}
			}
			break;
		case TokenNameSEMICOLON :
			this.endStatementPosition = this.scanner.currentPosition - 1;
			this.endPosition = this.scanner.startPosition - 1;
			RecoveredType currentType = currentRecoveryType();
			if(currentType != null) {
				currentType.insideEnumConstantPart = false;
			}
			//$FALL-THROUGH$
		default : {
			if (this.rBraceEnd > this.rBraceSuccessorStart && this.scanner.currentPosition != this.scanner.startPosition){
				this.rBraceSuccessorStart = this.scanner.startPosition;
			}
			break;
		}
	}
	this.ignoreNextOpeningBrace = false;
}
// A P I
protected void reportSyntaxErrors(boolean isDietParse, int oldFirstToken) {
	if(this.referenceContext instanceof MethodDeclaration) {
		MethodDeclaration methodDeclaration = (MethodDeclaration) this.referenceContext;
		if((methodDeclaration.bits & ASTNode.ErrorInSignature) != 0){
			return;
		}
	}
	this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
	this.scanner.recordLineSeparator = false;

	int start = this.scanner.initialPosition;
	int end = this.scanner.eofPosition == Integer.MAX_VALUE ? this.scanner.eofPosition : this.scanner.eofPosition - 1;
	if(isDietParse) {
		TypeDeclaration[] types = this.compilationUnit.types;
		int[][] intervalToSkip = org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil.computeDietRange(types);
		DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, intervalToSkip[0], intervalToSkip[1], intervalToSkip[2], this.options);
		diagnoseParser.diagnoseParse(false);

		reportSyntaxErrorsForSkippedMethod(types);
		this.scanner.resetTo(start, end);
	} else {
		DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, this.options);
		diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
	}
}
private void reportSyntaxErrorsForSkippedMethod(TypeDeclaration[] types){
	if(types != null) {
		for (int i = 0; i < types.length; i++) {
			TypeDeclaration[] memberTypes = types[i].memberTypes;
			if(memberTypes != null) {
				reportSyntaxErrorsForSkippedMethod(memberTypes);
			}

			AbstractMethodDeclaration[] methods = types[i].methods;
			if(methods != null) {
				for (int j = 0; j < methods.length; j++) {
					AbstractMethodDeclaration method = methods[j];
					if((method.bits & ASTNode.ErrorInSignature) != 0) {
						if(method.isAnnotationMethod()) {
							DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameQUESTION, method.declarationSourceStart, method.declarationSourceEnd, this.options);
							diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
						} else {
							DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameDIVIDE, method.declarationSourceStart, method.declarationSourceEnd, this.options);
							diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
						}

					}
				}
			}

			FieldDeclaration[] fields = types[i].fields;
			if (fields != null) {
				int length = fields.length;
				for (int j = 0; j < length; j++) {
					if (fields[j] instanceof Initializer) {
						Initializer initializer = (Initializer)fields[j];
						if((initializer.bits & ASTNode.ErrorInSignature) != 0){
							DiagnoseParser diagnoseParser = new DiagnoseParser(this, TokenNameRIGHT_SHIFT, initializer.declarationSourceStart, initializer.declarationSourceEnd, this.options);
							diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
						}
					}
				}
			}
		}
	}
}
protected void resetModifiers() {
	this.modifiers = ClassFileConstants.AccDefault;
	this.modifiersSourceStart = -1; // <-- see comment into modifiersFlag(int)
	this.scanner.commentPtr = -1;
}
/*
 * Reset context so as to resume to regular parse loop
 */
protected void resetStacks() {

	this.astPtr = -1;
	this.astLengthPtr = -1;
	this.expressionPtr = -1;
	this.expressionLengthPtr = -1;
	this.identifierPtr = -1;
	this.identifierLengthPtr	= -1;
	this.intPtr = -1;
	this.nestedMethod[this.nestedType = 0] = 0; // need to reset for further reuse
	this.variablesCounter[this.nestedType] = 0;
	this.dimensions = 0 ;
	this.realBlockStack[this.realBlockPtr = 0] = 0;
	this.recoveredStaticInitializerStart = 0;
	this.listLength = 0;
	this.listTypeParameterLength = 0;

	this.genericsIdentifiersLengthPtr = -1;
	this.genericsLengthPtr = -1;
	this.genericsPtr = -1;
}
/*
 * Reset context so as to resume to regular parse loop
 * If unable to reset for resuming, answers false.
 *
 * Move checkpoint location, reset internal stacks and
 * decide which grammar goal is activated.
 */
protected boolean resumeAfterRecovery() {
	if(!this.methodRecoveryActivated && !this.statementRecoveryActivated) {

		// reset internal stacks
		resetStacks();
		resetModifiers();

		/* attempt to move checkpoint location */
		if (!moveRecoveryCheckpoint()) {
			return false;
		}

		// only look for headers
		if (this.referenceContext instanceof CompilationUnitDeclaration){
			goForHeaders();
			this.diet = true; // passed this point, will not consider method bodies
			return true;
		}

		// does not know how to restart
		return false;
	} else if(!this.statementRecoveryActivated) {

		// reset internal stacks
		resetStacks();
		resetModifiers();

		/* attempt to move checkpoint location */
		if (!moveRecoveryCheckpoint()) {
			return false;
		}

		// only look for headers
		goForHeaders();
		return true;
	} else {
		return false;
	}
}
protected boolean resumeOnSyntaxError() {
	/* request recovery initialization */
	if (this.currentElement == null){
		// Reset javadoc before restart parsing after recovery
		this.javadoc = null;

		// do not investigate deeper in statement recovery
		if (this.statementRecoveryActivated) return false;

		// build some recovered elements
		this.currentElement = buildInitialRecoveryState();
	}
	/* do not investigate deeper in recovery when no recovered element */
	if (this.currentElement == null) return false;

	/* manual forced recovery restart - after headers */
	if (this.restartRecovery){
		this.restartRecovery = false;
	}
	/* update recovery state with current error state of the parser */
	updateRecoveryState();
	if (getFirstToken() == TokenNameAND) {
		if (this.referenceContext instanceof CompilationUnitDeclaration) {
			TypeDeclaration typeDeclaration = new TypeDeclaration(this.referenceContext.compilationResult());
			typeDeclaration.name = Util.EMPTY_STRING.toCharArray();
			this.currentElement = this.currentElement.add(typeDeclaration, 0);
		}
	}

	if (this.lastPosistion < this.scanner.currentPosition) {
		this.lastPosistion = this.scanner.currentPosition;
		this.scanner.lastPosition = this.scanner.currentPosition;
	}

	/* attempt to reset state in order to resume to parse loop */
	return resumeAfterRecovery();
}
public void setMethodsFullRecovery(boolean enabled) {
	this.options.performMethodsFullRecovery = enabled;
}
public void setStatementsRecovery(boolean enabled) {
	if(enabled) this.options.performMethodsFullRecovery = true;
	this.options.performStatementsRecovery = enabled;
}
public String toString() {


	String s = "lastCheckpoint : int = " + String.valueOf(this.lastCheckPoint) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
	s = s + "identifierStack : char["+(this.identifierPtr + 1)+"][] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.identifierPtr; i++) {
		s = s + "\"" + String.valueOf(this.identifierStack[i]) + "\","; //$NON-NLS-1$ //$NON-NLS-2$
	}
	s = s + "}\n"; //$NON-NLS-1$

	s = s + "identifierLengthStack : int["+(this.identifierLengthPtr + 1)+"] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.identifierLengthPtr; i++) {
		s = s + this.identifierLengthStack[i] + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$

	s = s + "astLengthStack : int["+(this.astLengthPtr + 1)+"] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.astLengthPtr; i++) {
		s = s + this.astLengthStack[i] + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$
	s = s + "astPtr : int = " + String.valueOf(this.astPtr) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$

	s = s + "intStack : int["+(this.intPtr + 1)+"] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.intPtr; i++) {
		s = s + this.intStack[i] + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$

	s = s + "expressionLengthStack : int["+(this.expressionLengthPtr + 1)+"] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.expressionLengthPtr; i++) {
		s = s + this.expressionLengthStack[i] + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$

	s = s + "expressionPtr : int = " + String.valueOf(this.expressionPtr) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$

	s = s + "genericsIdentifiersLengthStack : int["+(this.genericsIdentifiersLengthPtr + 1)+"] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.genericsIdentifiersLengthPtr; i++) {
		s = s + this.genericsIdentifiersLengthStack[i] + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$

	s = s + "genericsLengthStack : int["+(this.genericsLengthPtr + 1)+"] = {"; //$NON-NLS-1$ //$NON-NLS-2$
	for (int i = 0; i <= this.genericsLengthPtr; i++) {
		s = s + this.genericsLengthStack[i] + ","; //$NON-NLS-1$
	}
	s = s + "}\n"; //$NON-NLS-1$

	s = s + "genericsPtr : int = " + String.valueOf(this.genericsPtr) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$

	s = s + "\n\n\n----------------Scanner--------------\n" + this.scanner.toString(); //$NON-NLS-1$
	return s;

}
/*
 * Update recovery state based on current parser/scanner state
 */
protected void updateRecoveryState() {

	/* expose parser state to recovery state */
	this.currentElement.updateFromParserState();

	/* check and update recovered state based on current token,
		this action is also performed when shifting token after recovery
		got activated once.
	*/
	recoveryTokenCheck();
}
protected void updateSourceDeclarationParts(int variableDeclaratorsCounter) {
	//fields is a definition of fields that are grouped together like in
	//public int[] a, b[], c
	//which results into 3 fields.

	FieldDeclaration field;
	int endTypeDeclarationPosition =
		-1 + this.astStack[this.astPtr - variableDeclaratorsCounter + 1].sourceStart;
	for (int i = 0; i < variableDeclaratorsCounter - 1; i++) {
		//last one is special(see below)
		field = (FieldDeclaration) this.astStack[this.astPtr - i - 1];
		field.endPart1Position = endTypeDeclarationPosition;
		field.endPart2Position = -1 + this.astStack[this.astPtr - i].sourceStart;
	}
	//last one
	(field = (FieldDeclaration) this.astStack[this.astPtr]).endPart1Position =
		endTypeDeclarationPosition;
	field.endPart2Position = field.declarationSourceEnd;

}
protected void updateSourcePosition(Expression exp) {
	//update the source Position of the expression

	//this.intStack : int int
	//-->
	//this.intStack :

	exp.sourceEnd = this.intStack[this.intPtr--];
	exp.sourceStart = this.intStack[this.intPtr--];
}
}
