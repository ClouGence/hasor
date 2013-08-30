/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Tromey - Contribution for bug 125961
 *     Tom Tromey - Contribution for bug 159641
 *     Benjamin Muskalla - Contribution for bug 239066
 *     Stephan Herrmann  - Contribution for bug 236385
 *     Stephan Herrmann  - Contribution for bug 295551
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.batch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRule;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.GenericXMLWriter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Main implements ProblemSeverities, SuffixConstants {
	public static class Logger {
		private PrintWriter err;
		private PrintWriter log;
		private Main main;
		private PrintWriter out;
		private HashMap parameters;
		int tagBits;
		private static final String CLASS = "class"; //$NON-NLS-1$
		private static final String CLASS_FILE = "classfile"; //$NON-NLS-1$
		private static final String CLASSPATH = "classpath"; //$NON-NLS-1$
		private static final String CLASSPATH_FILE = "FILE"; //$NON-NLS-1$
		private static final String CLASSPATH_FOLDER = "FOLDER"; //$NON-NLS-1$
		private static final String CLASSPATH_ID = "id"; //$NON-NLS-1$
		private static final String CLASSPATH_JAR = "JAR"; //$NON-NLS-1$
		private static final String CLASSPATHS = "classpaths"; //$NON-NLS-1$
		private static final String COMMAND_LINE_ARGUMENT = "argument"; //$NON-NLS-1$
		private static final String COMMAND_LINE_ARGUMENTS = "command_line"; //$NON-NLS-1$
		private static final String COMPILER = "compiler"; //$NON-NLS-1$
		private static final String COMPILER_COPYRIGHT = "copyright"; //$NON-NLS-1$
		private static final String COMPILER_NAME = "name"; //$NON-NLS-1$
		private static final String COMPILER_VERSION = "version"; //$NON-NLS-1$
		public static final int EMACS = 2;
		private static final String ERROR = "ERROR"; //$NON-NLS-1$
		private static final String ERROR_TAG = "error"; //$NON-NLS-1$
		private static final String WARNING_TAG = "warning"; //$NON-NLS-1$
		private static final String EXCEPTION = "exception"; //$NON-NLS-1$
		private static final String EXTRA_PROBLEM_TAG = "extra_problem"; //$NON-NLS-1$
		private static final String EXTRA_PROBLEMS = "extra_problems"; //$NON-NLS-1$
		private static final HashtableOfInt FIELD_TABLE = new HashtableOfInt();
		private static final String KEY = "key"; //$NON-NLS-1$
		private static final String MESSAGE = "message"; //$NON-NLS-1$
		private static final String NUMBER_OF_CLASSFILES = "number_of_classfiles"; //$NON-NLS-1$
		private static final String NUMBER_OF_ERRORS = "errors"; //$NON-NLS-1$
		private static final String NUMBER_OF_LINES = "number_of_lines"; //$NON-NLS-1$
		private static final String NUMBER_OF_PROBLEMS = "problems"; //$NON-NLS-1$
		private static final String NUMBER_OF_TASKS = "tasks"; //$NON-NLS-1$
		private static final String NUMBER_OF_WARNINGS = "warnings"; //$NON-NLS-1$
		private static final String OPTION = "option"; //$NON-NLS-1$
		private static final String OPTIONS = "options"; //$NON-NLS-1$
		private static final String OUTPUT = "output"; //$NON-NLS-1$
		private static final String PACKAGE = "package"; //$NON-NLS-1$
		private static final String PATH = "path"; //$NON-NLS-1$
		private static final String PROBLEM_ARGUMENT = "argument"; //$NON-NLS-1$
		private static final String PROBLEM_ARGUMENT_VALUE = "value"; //$NON-NLS-1$
		private static final String PROBLEM_ARGUMENTS = "arguments"; //$NON-NLS-1$
		private static final String PROBLEM_CATEGORY_ID = "categoryID"; //$NON-NLS-1$
		private static final String ID = "id"; //$NON-NLS-1$
		private static final String PROBLEM_ID = "problemID"; //$NON-NLS-1$
		private static final String PROBLEM_LINE = "line"; //$NON-NLS-1$
		private static final String PROBLEM_OPTION_KEY = "optionKey"; //$NON-NLS-1$
		private static final String PROBLEM_MESSAGE = "message"; //$NON-NLS-1$
		private static final String PROBLEM_SEVERITY = "severity"; //$NON-NLS-1$
		private static final String PROBLEM_SOURCE_END = "charEnd"; //$NON-NLS-1$
		private static final String PROBLEM_SOURCE_START = "charStart"; //$NON-NLS-1$
		private static final String PROBLEM_SUMMARY = "problem_summary"; //$NON-NLS-1$
		private static final String PROBLEM_TAG = "problem"; //$NON-NLS-1$
		private static final String PROBLEMS = "problems"; //$NON-NLS-1$
		private static final String SOURCE = "source"; //$NON-NLS-1$
		private static final String SOURCE_CONTEXT = "source_context"; //$NON-NLS-1$
		private static final String SOURCE_END = "sourceEnd"; //$NON-NLS-1$
		private static final String SOURCE_START = "sourceStart"; //$NON-NLS-1$
		private static final String SOURCES = "sources"; //$NON-NLS-1$

		private static final String STATS = "stats"; //$NON-NLS-1$

		private static final String TASK = "task"; //$NON-NLS-1$
		private static final String TASKS = "tasks"; //$NON-NLS-1$
		private static final String TIME = "time"; //$NON-NLS-1$
		private static final String VALUE = "value"; //$NON-NLS-1$
		private static final String WARNING = "WARNING"; //$NON-NLS-1$
		public static final int XML = 1;
		private static final String XML_DTD_DECLARATION = "<!DOCTYPE compiler PUBLIC \"-//Eclipse.org//DTD Eclipse JDT 3.2.004 Compiler//EN\" \"http://www.eclipse.org/jdt/core/compiler_32_004.dtd\">"; //$NON-NLS-1$
		static {
			try {
				Class c = IProblem.class;
				Field[] fields = c.getFields();
				for (int i = 0, max = fields.length; i < max; i++) {
					Field field = fields[i];
					if (field.getType().equals(Integer.TYPE)) {
						Integer value = (Integer) field.get(null);
						int key2 = value.intValue() & IProblem.IgnoreCategoriesMask;
						if (key2 == 0) {
							key2 = Integer.MAX_VALUE;
						}
						Logger.FIELD_TABLE.put(key2, field.getName());
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		public Logger(Main main, PrintWriter out, PrintWriter err) {
			this.out = out;
			this.err = err;
			this.parameters = new HashMap();
			this.main = main;
		}

		public String buildFileName(
			String outputPath,
			String relativeFileName) {
			char fileSeparatorChar = File.separatorChar;
			String fileSeparator = File.separator;

			outputPath = outputPath.replace('/', fileSeparatorChar);
			// To be able to pass the mkdirs() method we need to remove the extra file separator at the end of the outDir name
			StringBuffer outDir = new StringBuffer(outputPath);
			if (!outputPath.endsWith(fileSeparator)) {
				outDir.append(fileSeparator);
			}
			StringTokenizer tokenizer =
				new StringTokenizer(relativeFileName, fileSeparator);
			String token = tokenizer.nextToken();
			while (tokenizer.hasMoreTokens()) {
				outDir.append(token).append(fileSeparator);
				token = tokenizer.nextToken();
			}
			// token contains the last one
			return outDir.append(token).toString();
		}

		public void close() {
			if (this.log != null) {
				if ((this.tagBits & Logger.XML) != 0) {
					endTag(Logger.COMPILER);
					flush();
				}
				this.log.close();
			}
		}

		/**
		 *
		 */
		public void compiling() {
			printlnOut(this.main.bind("progress.compiling")); //$NON-NLS-1$
		}
		private void endLoggingExtraProblems() {
			endTag(Logger.EXTRA_PROBLEMS);
		}
		/**
		 * Used to stop logging problems.
		 * Only use in xml mode.
		 */
		private void endLoggingProblems() {
			endTag(Logger.PROBLEMS);
		}
		public void endLoggingSource() {
			if ((this.tagBits & Logger.XML) != 0) {
				endTag(Logger.SOURCE);
			}
		}

		public void endLoggingSources() {
			if ((this.tagBits & Logger.XML) != 0) {
				endTag(Logger.SOURCES);
			}
		}

		public void endLoggingTasks() {
			if ((this.tagBits & Logger.XML) != 0) {
				endTag(Logger.TASKS);
			}
		}
		private void endTag(String name) {
			if (this.log != null) {
				((GenericXMLWriter) this.log).endTag(name, true, true);
			}
		}
		private String errorReportSource(CategorizedProblem problem, char[] unitSource, int bits) {
			//extra from the source the innacurate     token
			//and "highlight" it using some underneath ^^^^^
			//put some context around too.

			//this code assumes that the font used in the console is fixed size

			//sanity .....
			int startPosition = problem.getSourceStart();
			int endPosition = problem.getSourceEnd();
			if (unitSource == null) {
				if (problem.getOriginatingFileName() != null) {
					try {
						unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
					} catch (IOException e) {
						// ignore;
					}
				}
			}
			int length;
			if ((startPosition > endPosition)
				|| ((startPosition < 0) && (endPosition < 0))
				|| (unitSource == null)
				|| (length = unitSource.length) == 0)
				return Messages.problem_noSourceInformation;

			StringBuffer errorBuffer = new StringBuffer();
			if ((bits & Main.Logger.EMACS) == 0) {
				errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(problem.getSourceLineNumber())));
				errorBuffer.append(Util.LINE_SEPARATOR);
			}
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
			int begin;
			int end;
			for (begin = startPosition >= length ? length - 1 : startPosition; begin > 0; begin--) {
				if ((c = unitSource[begin - 1]) == '\n' || c == '\r') break;
			}
			for (end = endPosition >= length ? length - 1 : endPosition ; end+1 < length; end++) {
				if ((c = unitSource[end + 1]) == '\r' || c == '\n') break;
			}

			// trim left and right spaces/tabs
			while ((c = unitSource[begin]) == ' ' || c == '\t') begin++;
			//while ((c = unitSource[end]) == ' ' || c == '\t') end--; TODO (philippe) should also trim right, but all tests are to be updated

			// copy source
			errorBuffer.append(unitSource, begin, end-begin+1);
			errorBuffer.append(Util.LINE_SEPARATOR).append("\t"); //$NON-NLS-1$

			// compute underline
			for (int i = begin; i <startPosition; i++) {
				errorBuffer.append((unitSource[i] == TAB) ? TAB : SPACE);
			}
			for (int i = startPosition; i <= (endPosition >= length ? length - 1 : endPosition); i++) {
				errorBuffer.append(MARK);
			}
			return errorBuffer.toString();
		}

		private void extractContext(CategorizedProblem problem, char[] unitSource) {
			//sanity .....
			int startPosition = problem.getSourceStart();
			int endPosition = problem.getSourceEnd();
			if (unitSource == null) {
				if (problem.getOriginatingFileName() != null) {
					try {
						unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
					} catch(IOException e) {
						// ignore
					}
				}
			}
			int length;
			if ((startPosition > endPosition)
					|| ((startPosition < 0) && (endPosition < 0))
					|| (unitSource == null)
					|| ((length = unitSource.length) <= 0)
					|| (endPosition > length)) {
				this.parameters.put(Logger.VALUE, Messages.problem_noSourceInformation);
				this.parameters.put(Logger.SOURCE_START, "-1"); //$NON-NLS-1$
				this.parameters.put(Logger.SOURCE_END, "-1"); //$NON-NLS-1$
				printTag(Logger.SOURCE_CONTEXT, this.parameters, true, true);
				return;
			}

			char c;
			//the next code tries to underline the token.....
			//it assumes (for a good display) that token source does not
			//contain any \r \n. This is false on statements !
			//(the code still works but the display is not optimal !)

			// expand to line limits
			int begin, end;
			for (begin = startPosition >= length ? length - 1 : startPosition; begin > 0; begin--) {
				if ((c = unitSource[begin - 1]) == '\n' || c == '\r') break;
			}
			for (end = endPosition >= length ? length - 1 : endPosition ; end+1 < length; end++) {
				if ((c = unitSource[end + 1]) == '\r' || c == '\n') break;
			}

			// trim left and right spaces/tabs
			while ((c = unitSource[begin]) == ' ' || c == '\t') begin++;
			while ((c = unitSource[end]) == ' ' || c == '\t') end--;

			// copy source
			StringBuffer buffer = new StringBuffer();
			buffer.append(unitSource, begin, end - begin + 1);

			this.parameters.put(Logger.VALUE, String.valueOf(buffer));
			this.parameters.put(Logger.SOURCE_START, Integer.toString(startPosition - begin));
			this.parameters.put(Logger.SOURCE_END, Integer.toString(endPosition - begin));
			printTag(Logger.SOURCE_CONTEXT, this.parameters, true, true);
		}
		public void flush() {
			this.out.flush();
			this.err.flush();
			if (this.log != null) {
				this.log.flush();
			}
		}

		private String getFieldName(int id) {
			int key2 = id & IProblem.IgnoreCategoriesMask;
			if (key2 == 0) {
				key2 = Integer.MAX_VALUE;
			}
			return (String) Logger.FIELD_TABLE.get(key2);
		}

		// find out an option name controlling a given problemID
		private String getProblemOptionKey(int problemID) {
			int irritant = ProblemReporter.getIrritant(problemID);
			return CompilerOptions.optionKeyFromIrritant(irritant);
		}

		public void logAverage() {
			Arrays.sort(this.main.compilerStats);
			long lineCount = this.main.compilerStats[0].lineCount;
			final int length = this.main.maxRepetition;
			long sum = 0;
			long parseSum = 0, resolveSum = 0, analyzeSum = 0, generateSum = 0;
			for (int i = 1, max = length - 1; i < max; i++) {
				CompilerStats stats = this.main.compilerStats[i];
				sum += stats.elapsedTime();
				parseSum += stats.parseTime;
				resolveSum += stats.resolveTime;
				analyzeSum += stats.analyzeTime;
				generateSum += stats.generateTime;
			}
			long time = sum / (length - 2);
			long parseTime = parseSum/(length - 2);
			long resolveTime = resolveSum/(length - 2);
			long analyzeTime = analyzeSum/(length - 2);
			long generateTime = generateSum/(length - 2);
			printlnOut(this.main.bind(
				"compile.averageTime", //$NON-NLS-1$
				new String[] {
					String.valueOf(lineCount),
					String.valueOf(time),
					String.valueOf(((int) (lineCount * 10000.0 / time)) / 10.0),
				}));
			if ((this.main.timing & Main.TIMING_DETAILED) != 0) {
				printlnOut(
						this.main.bind("compile.detailedTime", //$NON-NLS-1$
							new String[] {
								String.valueOf(parseTime),
								String.valueOf(((int) (parseTime * 1000.0 / time)) / 10.0),
								String.valueOf(resolveTime),
								String.valueOf(((int) (resolveTime * 1000.0 / time)) / 10.0),
								String.valueOf(analyzeTime),
								String.valueOf(((int) (analyzeTime * 1000.0 / time)) / 10.0),
								String.valueOf(generateTime),
								String.valueOf(((int) (generateTime * 1000.0 / time)) / 10.0),
							}));
			}
		}
		public void logClassFile(boolean generatePackagesStructure, String outputPath, String relativeFileName) {
			if ((this.tagBits & Logger.XML) != 0) {
				String fileName = null;
				if (generatePackagesStructure) {
					fileName = buildFileName(outputPath, relativeFileName);
				} else {
					char fileSeparatorChar = File.separatorChar;
					String fileSeparator = File.separator;
					// First we ensure that the outputPath exists
					outputPath = outputPath.replace('/', fileSeparatorChar);
					// To be able to pass the mkdirs() method we need to remove the extra file separator at the end of the outDir name
					int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
					if (indexOfPackageSeparator == -1) {
						if (outputPath.endsWith(fileSeparator)) {
							fileName = outputPath + relativeFileName;
						} else {
							fileName = outputPath + fileSeparator + relativeFileName;
						}
					} else {
						int length = relativeFileName.length();
						if (outputPath.endsWith(fileSeparator)) {
							fileName = outputPath + relativeFileName.substring(indexOfPackageSeparator + 1, length);
						} else {
							fileName = outputPath + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
						}
					}
				}
				File f = new File(fileName);
				try {
					this.parameters.put(Logger.PATH, f.getCanonicalPath());
					printTag(Logger.CLASS_FILE, this.parameters, true, true);
				} catch (IOException e) {
					logNoClassFileCreated(outputPath, relativeFileName, e);
				}
			}
		}
		public void logClasspath(FileSystem.Classpath[] classpaths) {
			if (classpaths == null) return;
			if ((this.tagBits & Logger.XML) != 0) {
				final int length = classpaths.length;
				if (length != 0) {
					// generate xml output
					printTag(Logger.CLASSPATHS, null, true, false);
					for (int i = 0; i < length; i++) {
						String classpath = classpaths[i].getPath();
						this.parameters.put(Logger.PATH, classpath);
						File f = new File(classpath);
						String id = null;
						if (f.isFile()) {
							if (Util.isPotentialZipArchive(classpath)) {
								id = Logger.CLASSPATH_JAR;
							} else {
								id = Logger.CLASSPATH_FILE;
							}
						} else if (f.isDirectory()) {
							id = Logger.CLASSPATH_FOLDER;
						}
						if (id != null) {
							this.parameters.put(Logger.CLASSPATH_ID, id);
							printTag(Logger.CLASSPATH, this.parameters, true, true);
						}
					}
					endTag(Logger.CLASSPATHS);
				}
			}

		}

		public void logCommandLineArguments(String[] commandLineArguments) {
			if (commandLineArguments == null) return;
			if ((this.tagBits & Logger.XML) != 0) {
				final int length = commandLineArguments.length;
				if (length != 0) {
					// generate xml output
					printTag(Logger.COMMAND_LINE_ARGUMENTS, null, true, false);
					for (int i = 0; i < length; i++) {
						this.parameters.put(Logger.VALUE, commandLineArguments[i]);
						printTag(Logger.COMMAND_LINE_ARGUMENT, this.parameters, true, true);
					}
					endTag(Logger.COMMAND_LINE_ARGUMENTS);
				}
			}
		}

		/**
		 * @param e the given exception to log
		 */
		public void logException(Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			printWriter.flush();
			printWriter.close();
			final String stackTrace = writer.toString();
			if ((this.tagBits & Logger.XML) != 0) {
				LineNumberReader reader = new LineNumberReader(new StringReader(stackTrace));
				String line;
				int i = 0;
				StringBuffer buffer = new StringBuffer();
				String message = e.getMessage();
				if (message != null) {
					buffer.append(message).append(Util.LINE_SEPARATOR);
				}
				try {
					while ((line = reader.readLine()) != null && i < 4) {
						buffer.append(line).append(Util.LINE_SEPARATOR);
						i++;
					}
					reader.close();
				} catch (IOException e1) {
					// ignore
				}
				message = buffer.toString();
				this.parameters.put(Logger.MESSAGE, message);
				this.parameters.put(Logger.CLASS, e.getClass());
				printTag(Logger.EXCEPTION, this.parameters, true, true);
			}
			String message = e.getMessage();
			if (message == null) {
				this.printlnErr(stackTrace);
			} else {
				this.printlnErr(message);
			}
		}

		private void logExtraProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount) {
			char[] originatingFileName = problem.getOriginatingFileName();
			if (originatingFileName == null) {
				// simplified message output
				if (problem.isError()) {
					printErr(this.main.bind(
								"requestor.extraerror", //$NON-NLS-1$
								Integer.toString(globalErrorCount)));
				} else {
					// warning / mandatory warning / other
					printErr(this.main.bind(
							"requestor.extrawarning", //$NON-NLS-1$
							Integer.toString(globalErrorCount)));
				}
				printErr(" "); //$NON-NLS-1$
				this.printlnErr(problem.getMessage());
			} else {
				String fileName = new String(originatingFileName);
				if ((this.tagBits & Logger.EMACS) != 0) {
					String result = fileName
							+ ":" //$NON-NLS-1$
							+ problem.getSourceLineNumber()
							+ ": " //$NON-NLS-1$
							+ (problem.isError() ? this.main.bind("output.emacs.error") : this.main.bind("output.emacs.warning")) //$NON-NLS-1$ //$NON-NLS-2$
							+ ": " //$NON-NLS-1$
							+ problem.getMessage();
					this.printlnErr(result);
					final String errorReportSource = errorReportSource(problem, null, this.tagBits);
					this.printlnErr(errorReportSource);
				} else {
					if (localErrorCount == 0) {
						this.printlnErr("----------"); //$NON-NLS-1$
					}
					printErr(problem.isError() ?
							this.main.bind(
									"requestor.error", //$NON-NLS-1$
									Integer.toString(globalErrorCount),
									new String(fileName))
									: this.main.bind(
											"requestor.warning", //$NON-NLS-1$
											Integer.toString(globalErrorCount),
											new String(fileName)));
					final String errorReportSource = errorReportSource(problem, null, 0);
					this.printlnErr(errorReportSource);
					this.printlnErr(problem.getMessage());
					this.printlnErr("----------"); //$NON-NLS-1$
				}
			}
		}

		public void loggingExtraProblems(Main currentMain) {
			ArrayList problems = currentMain.extraProblems;
			final int count = problems.size();
			int localProblemCount = 0;
			if (count != 0) {
				int errors = 0;
				int warnings = 0;
				for (int i = 0; i < count; i++) {
					CategorizedProblem problem = (CategorizedProblem) problems.get(i);
					if (problem != null) {
						currentMain.globalProblemsCount++;
						logExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
						localProblemCount++;
						if (problem.isError()) {
							errors++;
							currentMain.globalErrorsCount++;
						} else if (problem.isWarning()) {
							currentMain.globalWarningsCount++;
							warnings++;
						}
					}
				}
				if ((this.tagBits & Logger.XML) != 0) {
					if ((errors + warnings) != 0) {
						startLoggingExtraProblems(count);
						for (int i = 0; i < count; i++) {
							CategorizedProblem problem = (CategorizedProblem) problems.get(i);
							if (problem!= null) {
								if (problem.getID() != IProblem.Task) {
									logXmlExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
								}
							}
						}
						endLoggingExtraProblems();
					}
				}
			}
		}

		public void logIncorrectVMVersionForAnnotationProcessing() {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.MESSAGE, this.main.bind("configure.incorrectVMVersionforAPT")); //$NON-NLS-1$
				printTag(Logger.ERROR_TAG, this.parameters, true, true);
			}
			this.printlnErr(this.main.bind("configure.incorrectVMVersionforAPT")); //$NON-NLS-1$
		}

		/**
		 *
		 */
		public void logNoClassFileCreated(String outputDir, String relativeFileName, IOException e) {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.MESSAGE, this.main.bind("output.noClassFileCreated", //$NON-NLS-1$
					new String[] {
						outputDir,
						relativeFileName,
						e.getMessage()
					}));
				printTag(Logger.ERROR_TAG, this.parameters, true, true);
			}
			this.printlnErr(this.main.bind("output.noClassFileCreated", //$NON-NLS-1$
				new String[] {
					outputDir,
					relativeFileName,
					e.getMessage()
				}));
		}

		/**
		 * @param exportedClassFilesCounter
		 */
		public void logNumberOfClassFilesGenerated(int exportedClassFilesCounter) {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.VALUE, new Integer(exportedClassFilesCounter));
				printTag(Logger.NUMBER_OF_CLASSFILES, this.parameters, true, true);
			}
			if (exportedClassFilesCounter == 1) {
				printlnOut(this.main.bind("compile.oneClassFileGenerated")); //$NON-NLS-1$
			} else {
				printlnOut(this.main.bind("compile.severalClassFilesGenerated", //$NON-NLS-1$
					String.valueOf(exportedClassFilesCounter)));
			}
		}

		/**
		 * @param options the given compiler options
		 */
		public void logOptions(Map options) {
			if ((this.tagBits & Logger.XML) != 0) {
				printTag(Logger.OPTIONS, null, true, false);
				final Set entriesSet = options.entrySet();
				Object[] entries = entriesSet.toArray();
				Arrays.sort(entries, new Comparator() {
					public int compare(Object o1, Object o2) {
						Map.Entry entry1 = (Map.Entry) o1;
						Map.Entry entry2 = (Map.Entry) o2;
						return ((String) entry1.getKey()).compareTo((String) entry2.getKey());
					}
				});
				for (int i = 0, max = entries.length; i < max; i++) {
					Map.Entry entry = (Map.Entry) entries[i];
					String key = (String) entry.getKey();
					this.parameters.put(Logger.KEY, key);
					this.parameters.put(Logger.VALUE, entry.getValue());
					printTag(Logger.OPTION, this.parameters, true, true);
				}
				endTag(Logger.OPTIONS);
			}
		}

		/**
		 * @param error the given error
		 */
		public void logPendingError(String error) {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.MESSAGE, error);
				printTag(Logger.ERROR_TAG, this.parameters, true, true);
			}
			this.printlnErr(error);
		}

		/**
		 * @param message the given message
		 */
		public void logWarning(String message) {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.MESSAGE, message);
				printTag(Logger.WARNING_TAG, this.parameters, true, true);
			}
			this.printlnOut(message);
		}

		private void logProblem(CategorizedProblem problem, int localErrorCount,
			int globalErrorCount, char[] unitSource) {
			if ((this.tagBits & Logger.EMACS) != 0) {
				String result = (new String(problem.getOriginatingFileName())
						+ ":" //$NON-NLS-1$
						+ problem.getSourceLineNumber()
						+ ": " //$NON-NLS-1$
						+ (problem.isError() ? this.main.bind("output.emacs.error") : this.main.bind("output.emacs.warning")) //$NON-NLS-1$ //$NON-NLS-2$
						+ ": " //$NON-NLS-1$
						+ problem.getMessage());
				this.printlnErr(result);
				final String errorReportSource = errorReportSource(problem, unitSource, this.tagBits);
				if (errorReportSource.length() != 0) this.printlnErr(errorReportSource);
			} else {
				if (localErrorCount == 0) {
					this.printlnErr("----------"); //$NON-NLS-1$
				}
				printErr(problem.isError() ?
						this.main.bind(
								"requestor.error", //$NON-NLS-1$
								Integer.toString(globalErrorCount),
								new String(problem.getOriginatingFileName()))
								: this.main.bind(
										"requestor.warning", //$NON-NLS-1$
										Integer.toString(globalErrorCount),
										new String(problem.getOriginatingFileName())));
				try {
					final String errorReportSource = errorReportSource(problem, unitSource, 0);
					this.printlnErr(errorReportSource);
					this.printlnErr(problem.getMessage());
				} catch (Exception e) {
					this.printlnErr(this.main.bind(
						"requestor.notRetrieveErrorMessage", problem.toString())); //$NON-NLS-1$
				}
				this.printlnErr("----------"); //$NON-NLS-1$
			}
		}

		public int logProblems(CategorizedProblem[] problems, char[] unitSource, Main currentMain) {
			final int count = problems.length;
			int localErrorCount = 0;
			int localProblemCount = 0;
			if (count != 0) {
				int errors = 0;
				int warnings = 0;
				int tasks = 0;
				for (int i = 0; i < count; i++) {
					CategorizedProblem problem = problems[i];
					if (problem != null) {
						currentMain.globalProblemsCount++;
						logProblem(problem, localProblemCount, currentMain.globalProblemsCount, unitSource);
						localProblemCount++;
						if (problem.isError()) {
							localErrorCount++;
							errors++;
							currentMain.globalErrorsCount++;
						} else if (problem.getID() == IProblem.Task) {
							currentMain.globalTasksCount++;
							tasks++;
						} else {
							currentMain.globalWarningsCount++;
							warnings++;
						}
					}
				}
				if ((this.tagBits & Logger.XML) != 0) {
					if ((errors + warnings) != 0) {
						startLoggingProblems(errors, warnings);
						for (int i = 0; i < count; i++) {
							CategorizedProblem problem = problems[i];
							if (problem!= null) {
								if (problem.getID() != IProblem.Task) {
									logXmlProblem(problem, unitSource);
								}
							}
						}
						endLoggingProblems();
					}
					if (tasks != 0) {
						startLoggingTasks(tasks);
						for (int i = 0; i < count; i++) {
							CategorizedProblem problem = problems[i];
							if (problem!= null) {
								if (problem.getID() == IProblem.Task) {
									logXmlTask(problem, unitSource);
								}
							}
						}
						endLoggingTasks();
					}
				}
			}
			return localErrorCount;
		}

		/**
		 * @param globalProblemsCount
		 * @param globalErrorsCount
		 * @param globalWarningsCount
		 */
		public void logProblemsSummary(int globalProblemsCount,
			int globalErrorsCount, int globalWarningsCount, int globalTasksCount) {
			if ((this.tagBits & Logger.XML) != 0) {
				// generate xml
				this.parameters.put(Logger.NUMBER_OF_PROBLEMS, new Integer(globalProblemsCount));
				this.parameters.put(Logger.NUMBER_OF_ERRORS, new Integer(globalErrorsCount));
				this.parameters.put(Logger.NUMBER_OF_WARNINGS, new Integer(globalWarningsCount));
				this.parameters.put(Logger.NUMBER_OF_TASKS, new Integer(globalTasksCount));
				printTag(Logger.PROBLEM_SUMMARY, this.parameters, true, true);
			}
			if (globalProblemsCount == 1) {
				String message = null;
				if (globalErrorsCount == 1) {
					message = this.main.bind("compile.oneError"); //$NON-NLS-1$
				} else {
					message = this.main.bind("compile.oneWarning"); //$NON-NLS-1$
				}
				printErr(this.main.bind("compile.oneProblem", message)); //$NON-NLS-1$
			} else {
				String errorMessage = null;
				String warningMessage = null;
				if (globalErrorsCount > 0) {
					if (globalErrorsCount == 1) {
						errorMessage = this.main.bind("compile.oneError"); //$NON-NLS-1$
					} else {
						errorMessage = this.main.bind("compile.severalErrors", String.valueOf(globalErrorsCount)); //$NON-NLS-1$
					}
				}
				int warningsNumber = globalWarningsCount + globalTasksCount;
				if (warningsNumber > 0) {
					if (warningsNumber == 1) {
						warningMessage = this.main.bind("compile.oneWarning"); //$NON-NLS-1$
					} else {
						warningMessage = this.main.bind("compile.severalWarnings", String.valueOf(warningsNumber)); //$NON-NLS-1$
					}
				}
				if (errorMessage == null || warningMessage == null) {
					if (errorMessage == null) {
						printErr(this.main.bind(
							"compile.severalProblemsErrorsOrWarnings", //$NON-NLS-1$
							String.valueOf(globalProblemsCount),
							warningMessage));
					} else {
						printErr(this.main.bind(
							"compile.severalProblemsErrorsOrWarnings", //$NON-NLS-1$
							String.valueOf(globalProblemsCount),
							errorMessage));
					}
				} else {
					printErr(this.main.bind(
						"compile.severalProblemsErrorsAndWarnings", //$NON-NLS-1$
						new String[] {
							String.valueOf(globalProblemsCount),
							errorMessage,
							warningMessage
						}));
				}
			}
			if ((this.tagBits & Logger.EMACS) != 0) {
				this.printlnErr();
			}
		}

		/**
		 *
		 */
		public void logProgress() {
			printOut('.');
		}

		/**
		 * @param i
		 *            the current repetition number
		 * @param repetitions
		 *            the given number of repetitions
		 */
		public void logRepetition(int i, int repetitions) {
			printlnOut(this.main.bind("compile.repetition", //$NON-NLS-1$
				String.valueOf(i + 1), String.valueOf(repetitions)));
		}
		/**
		 * @param compilerStats
		 */
		public void logTiming(CompilerStats compilerStats) {
			long time = compilerStats.elapsedTime();
			long lineCount = compilerStats.lineCount;
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.VALUE, new Long(time));
				printTag(Logger.TIME, this.parameters, true, true);
				this.parameters.put(Logger.VALUE, new Long(lineCount));
				printTag(Logger.NUMBER_OF_LINES, this.parameters, true, true);
			}
			if (lineCount != 0) {
				printlnOut(
					this.main.bind("compile.instantTime", //$NON-NLS-1$
						new String[] {
							String.valueOf(lineCount),
							String.valueOf(time),
							String.valueOf(((int) (lineCount * 10000.0 / time)) / 10.0),
						}));
			} else {
				printlnOut(
					this.main.bind("compile.totalTime", //$NON-NLS-1$
						new String[] {
							String.valueOf(time),
						}));
			}
			if ((this.main.timing & Main.TIMING_DETAILED) != 0) {
				printlnOut(
						this.main.bind("compile.detailedTime", //$NON-NLS-1$
							new String[] {
								String.valueOf(compilerStats.parseTime),
								String.valueOf(((int) (compilerStats.parseTime * 1000.0 / time)) / 10.0),
								String.valueOf(compilerStats.resolveTime),
								String.valueOf(((int) (compilerStats.resolveTime * 1000.0 / time)) / 10.0),
								String.valueOf(compilerStats.analyzeTime),
								String.valueOf(((int) (compilerStats.analyzeTime * 1000.0 / time)) / 10.0),
								String.valueOf(compilerStats.generateTime),
								String.valueOf(((int) (compilerStats.generateTime * 1000.0 / time)) / 10.0),
							}));
			}
		}

		/**
		 * Print the usage of the compiler
		 * @param usage
		 */
		public void logUsage(String usage) {
			printlnOut(usage);
		}

		/**
		 * Print the version of the compiler in the log and/or the out field
		 */
		public void logVersion(final boolean printToOut) {
			if (this.log != null && (this.tagBits & Logger.XML) == 0) {
				final String version = this.main.bind("misc.version", //$NON-NLS-1$
					new String[] {
						this.main.bind("compiler.name"), //$NON-NLS-1$
						this.main.bind("compiler.version"), //$NON-NLS-1$
						this.main.bind("compiler.copyright") //$NON-NLS-1$
					}
				);
				this.log.println("# " + version); //$NON-NLS-1$
				if (printToOut) {
					this.out.println(version);
					this.out.flush();
				}
			} else if (printToOut) {
				final String version = this.main.bind("misc.version", //$NON-NLS-1$
					new String[] {
						this.main.bind("compiler.name"), //$NON-NLS-1$
						this.main.bind("compiler.version"), //$NON-NLS-1$
						this.main.bind("compiler.copyright") //$NON-NLS-1$
					}
				);
				this.out.println(version);
				this.out.flush();
			}
		}

		/**
		 * Print the usage of wrong JDK
		 */
		public void logWrongJDK() {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.MESSAGE, this.main.bind("configure.requiresJDK1.2orAbove")); //$NON-NLS-1$
				printTag(Logger.ERROR, this.parameters, true, true);
			}
			this.printlnErr(this.main.bind("configure.requiresJDK1.2orAbove")); //$NON-NLS-1$
		}

		private void logXmlExtraProblem(CategorizedProblem problem, int globalErrorCount, int localErrorCount) {
			final int sourceStart = problem.getSourceStart();
			final int sourceEnd = problem.getSourceEnd();
			boolean isError = problem.isError();
			this.parameters.put(Logger.PROBLEM_SEVERITY, isError ? Logger.ERROR : Logger.WARNING);
			this.parameters.put(Logger.PROBLEM_LINE, new Integer(problem.getSourceLineNumber()));
			this.parameters.put(Logger.PROBLEM_SOURCE_START, new Integer(sourceStart));
			this.parameters.put(Logger.PROBLEM_SOURCE_END, new Integer(sourceEnd));
			printTag(Logger.EXTRA_PROBLEM_TAG, this.parameters, true, false);
			this.parameters.put(Logger.VALUE, problem.getMessage());
			printTag(Logger.PROBLEM_MESSAGE, this.parameters, true, true);
			extractContext(problem, null);
			endTag(Logger.EXTRA_PROBLEM_TAG);
		}
		/**
		 * @param problem
		 *            the given problem to log
		 * @param unitSource
		 *            the given unit source
		 */
		private void logXmlProblem(CategorizedProblem problem, char[] unitSource) {
			final int sourceStart = problem.getSourceStart();
			final int sourceEnd = problem.getSourceEnd();
			final int id = problem.getID();
			this.parameters.put(Logger.ID, getFieldName(id)); // ID as field name
			this.parameters.put(Logger.PROBLEM_ID, new Integer(id)); // ID as numeric value
			boolean isError = problem.isError();
			int severity = isError ? ProblemSeverities.Error : ProblemSeverities.Warning;
			this.parameters.put(Logger.PROBLEM_SEVERITY, isError ? Logger.ERROR : Logger.WARNING);
			this.parameters.put(Logger.PROBLEM_LINE, new Integer(problem.getSourceLineNumber()));
			this.parameters.put(Logger.PROBLEM_SOURCE_START, new Integer(sourceStart));
			this.parameters.put(Logger.PROBLEM_SOURCE_END, new Integer(sourceEnd));
			String problemOptionKey = getProblemOptionKey(id);
			if (problemOptionKey != null) {
				this.parameters.put(Logger.PROBLEM_OPTION_KEY, problemOptionKey);
			}
			int categoryID = ProblemReporter.getProblemCategory(severity, id);
			this.parameters.put(Logger.PROBLEM_CATEGORY_ID, new Integer(categoryID));
			printTag(Logger.PROBLEM_TAG, this.parameters, true, false);
			this.parameters.put(Logger.VALUE, problem.getMessage());
			printTag(Logger.PROBLEM_MESSAGE, this.parameters, true, true);
			extractContext(problem, unitSource);
			String[] arguments = problem.getArguments();
			final int length = arguments.length;
			if (length != 0) {
				printTag(Logger.PROBLEM_ARGUMENTS, null, true, false);
				for (int i = 0; i < length; i++) {
					this.parameters.put(Logger.PROBLEM_ARGUMENT_VALUE, arguments[i]);
					printTag(Logger.PROBLEM_ARGUMENT, this.parameters, true, true);
				}
				endTag(Logger.PROBLEM_ARGUMENTS);
			}
			endTag(Logger.PROBLEM_TAG);
		}
		/**
		 * @param problem
		 *            the given problem to log
		 * @param unitSource
		 *            the given unit source
		 */
		private void logXmlTask(CategorizedProblem problem, char[] unitSource) {
			this.parameters.put(Logger.PROBLEM_LINE, new Integer(problem.getSourceLineNumber()));
			this.parameters.put(Logger.PROBLEM_SOURCE_START, new Integer(problem.getSourceStart()));
			this.parameters.put(Logger.PROBLEM_SOURCE_END, new Integer(problem.getSourceEnd()));
			String problemOptionKey = getProblemOptionKey(problem.getID());
			if (problemOptionKey != null) {
				this.parameters.put(Logger.PROBLEM_OPTION_KEY, problemOptionKey);
			}
			printTag(Logger.TASK, this.parameters, true, false);
			this.parameters.put(Logger.VALUE, problem.getMessage());
			printTag(Logger.PROBLEM_MESSAGE, this.parameters, true, true);
			extractContext(problem, unitSource);
			endTag(Logger.TASK);
		}

		private void printErr(String s) {
			this.err.print(s);
			if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
				this.log.print(s);
			}
		}

		private void printlnErr() {
			this.err.println();
			if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
				this.log.println();
			}
		}

		private void printlnErr(String s) {
			this.err.println(s);
			if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
				this.log.println(s);
			}
		}

		private void printlnOut(String s) {
			this.out.println(s);
			if ((this.tagBits & Logger.XML) == 0 && this.log != null) {
				this.log.println(s);
			}
		}

		/**
		 *
		 */
		public void printNewLine() {
			this.out.println();
		}

		private void printOut(char c) {
			this.out.print(c);
		}

		public void printStats() {
			final boolean isTimed = (this.main.timing & TIMING_ENABLED) != 0;
			if ((this.tagBits & Logger.XML) != 0) {
				printTag(Logger.STATS, null, true, false);
			}
			if (isTimed) {
				CompilerStats compilerStats = this.main.batchCompiler.stats;
				compilerStats.startTime = this.main.startTime; // also include batch initialization times
				compilerStats.endTime = System.currentTimeMillis(); // also include batch output times
				logTiming(compilerStats);
			}
			if (this.main.globalProblemsCount > 0) {
				logProblemsSummary(this.main.globalProblemsCount, this.main.globalErrorsCount, this.main.globalWarningsCount, this.main.globalTasksCount);
			}
			if (this.main.exportedClassFilesCounter != 0
					&& (this.main.showProgress || isTimed || this.main.verbose)) {
				logNumberOfClassFilesGenerated(this.main.exportedClassFilesCounter);
			}
			if ((this.tagBits & Logger.XML) != 0) {
				endTag(Logger.STATS);
			}
		}

		private void printTag(String name, HashMap params, boolean insertNewLine, boolean closeTag) {
			if (this.log != null) {
				((GenericXMLWriter) this.log).printTag(name, this.parameters, true, insertNewLine, closeTag);
			}
			this.parameters.clear();
		}

		public void setEmacs() {
			this.tagBits |= Logger.EMACS;
		}
		public void setLog(String logFileName) {
			final Date date = new Date();
			final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.getDefault());
			try {
				int index = logFileName.lastIndexOf('.');
				if (index != -1) {
					if (logFileName.substring(index).toLowerCase().equals(".xml")) { //$NON-NLS-1$
						this.log = new GenericXMLWriter(new OutputStreamWriter(new FileOutputStream(logFileName, false), Util.UTF_8), Util.LINE_SEPARATOR, true);
						this.tagBits |= Logger.XML;
						// insert time stamp as comment
						this.log.println("<!-- " + dateFormat.format(date) + " -->");//$NON-NLS-1$//$NON-NLS-2$
						this.log.println(Logger.XML_DTD_DECLARATION);
						this.parameters.put(Logger.COMPILER_NAME, this.main.bind("compiler.name")); //$NON-NLS-1$
						this.parameters.put(Logger.COMPILER_VERSION, this.main.bind("compiler.version")); //$NON-NLS-1$
						this.parameters.put(Logger.COMPILER_COPYRIGHT, this.main.bind("compiler.copyright")); //$NON-NLS-1$
						printTag(Logger.COMPILER, this.parameters, true, false);
					} else {
						this.log = new PrintWriter(new FileOutputStream(logFileName, false));
						this.log.println("# " + dateFormat.format(date));//$NON-NLS-1$
					}
				} else {
					this.log = new PrintWriter(new FileOutputStream(logFileName, false));
					this.log.println("# " + dateFormat.format(date));//$NON-NLS-1$
				}
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLog", logFileName)); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLogInvalidEncoding", logFileName)); //$NON-NLS-1$
			}
		}
		private void startLoggingExtraProblems(int count) {
			this.parameters.put(Logger.NUMBER_OF_PROBLEMS, new Integer(count));
			printTag(Logger.EXTRA_PROBLEMS, this.parameters, true, false);
		}

		/**
		 * Used to start logging problems.
		 * Only use in xml mode.
		 */
		private void startLoggingProblems(int errors, int warnings) {
			this.parameters.put(Logger.NUMBER_OF_PROBLEMS, new Integer(errors + warnings));
			this.parameters.put(Logger.NUMBER_OF_ERRORS, new Integer(errors));
			this.parameters.put(Logger.NUMBER_OF_WARNINGS, new Integer(warnings));
			printTag(Logger.PROBLEMS, this.parameters, true, false);
		}

		public void startLoggingSource(CompilationResult compilationResult) {
			if ((this.tagBits & Logger.XML) != 0) {
				ICompilationUnit compilationUnit = compilationResult.compilationUnit;
				if (compilationUnit != null) {
    				char[] fileName = compilationUnit.getFileName();
    				File f = new File(new String(fileName));
    				if (fileName != null) {
    					this.parameters.put(Logger.PATH, f.getAbsolutePath());
    				}
    				char[][] packageName = compilationResult.packageName;
    				if (packageName != null) {
    					this.parameters.put(
    							Logger.PACKAGE,
    							new String(CharOperation.concatWith(packageName, File.separatorChar)));
    				}
    				CompilationUnit unit = (CompilationUnit) compilationUnit;
    				String destinationPath = unit.destinationPath;
					if (destinationPath == null) {
						destinationPath = this.main.destinationPath;
					}
					if (destinationPath != null && destinationPath != NONE) {
						if (File.separatorChar == '/') {
							this.parameters.put(Logger.OUTPUT, destinationPath);
						} else {
							this.parameters.put(Logger.OUTPUT, destinationPath.replace('/', File.separatorChar));
						}
					}
				}
				printTag(Logger.SOURCE, this.parameters, true, false);
			}
		}

		public void startLoggingSources() {
			if ((this.tagBits & Logger.XML) != 0) {
				printTag(Logger.SOURCES, null, true, false);
			}
		}

		public void startLoggingTasks(int tasks) {
			if ((this.tagBits & Logger.XML) != 0) {
				this.parameters.put(Logger.NUMBER_OF_TASKS, new Integer(tasks));
				printTag(Logger.TASKS, this.parameters, true, false);
			}
		}
	}

	/**
	 * Resource bundle factory to share bundles for the same locale
	 */
	public static class ResourceBundleFactory {
		private static HashMap Cache = new HashMap();
		public static synchronized ResourceBundle getBundle(Locale locale) {
			ResourceBundle bundle = (ResourceBundle) Cache.get(locale);
			if (bundle == null) {
				bundle = ResourceBundle.getBundle(Main.bundleName, locale);
				Cache.put(locale, bundle);
			}
			return bundle;
		}
	}
	// javadoc analysis tuning
	boolean enableJavadocOn;

	boolean warnJavadocOn;
	boolean warnAllJavadocOn;

	public Compiler batchCompiler;
	/* Bundle containing messages */
	public ResourceBundle bundle;
	protected FileSystem.Classpath[] checkedClasspaths;

	public Locale compilerLocale;
	public CompilerOptions compilerOptions; // read-only
	public CompilationProgress progress;
	public String destinationPath;
	public String[] destinationPaths;
	// destination path for compilation units that get no more specific
	// one (through directory arguments or various classpath options);
	// coding is:
	// == null: unspecified, write class files close to their respective
	//          source files;
	// == Main.NONE: absorbent element, do not output class files;
	// else: use as the path of the directory into which class files must
	//       be written.
	private boolean didSpecifySource;
	private boolean didSpecifyTarget;
	public String[] encodings;
	public int exportedClassFilesCounter;
	public String[] filenames;
	public String[] classNames;
	// overrides of destinationPath on a directory argument basis
	public int globalErrorsCount;
	public int globalProblemsCount;
	public int globalTasksCount;
	public int globalWarningsCount;

	private File javaHomeCache;

	private boolean javaHomeChecked = false;
	public long lineCount0;

	public String log;

	public Logger logger;
	public int maxProblems;
	public Map options;
	protected PrintWriter out;
	public boolean proceed = true;
	public boolean proceedOnError = false;
	public boolean produceRefInfo = false;
	public int currentRepetition, maxRepetition;
	public boolean showProgress = false;
	public long startTime;
	public ArrayList pendingErrors;
	public boolean systemExitWhenFinished = true;

	public static final int TIMING_DISABLED = 0;
	public static final int TIMING_ENABLED = 1;
	public static final int TIMING_DETAILED = 2;

	public int timing = TIMING_DISABLED;
	public CompilerStats[] compilerStats;
	public boolean verbose = false;
	private String[] expandedCommandLine;

	private PrintWriter err;

	protected ArrayList extraProblems;
	public final static String bundleName = "org.eclipse.jdt.internal.compiler.batch.messages"; //$NON-NLS-1$
	// two uses: recognize 'none' in options; code the singleton none
	// for the '-d none' option (wherever it may be found)
	public static final int DEFAULT_SIZE_CLASSPATH = 4;

	public static final String NONE = "none"; //$NON-NLS-1$

/**
 * @deprecated - use {@link BatchCompiler#compile(String, PrintWriter, PrintWriter, CompilationProgress)} instead
 * 						  e.g. BatchCompiler.compile(commandLine, new PrintWriter(System.out), new PrintWriter(System.err), null);
 */
public static boolean compile(String commandLine) {
	return new Main(new PrintWriter(System.out), new PrintWriter(System.err), false /* systemExit */, null /* options */, null /* progress */).compile(tokenize(commandLine));
}

/**
 * @deprecated - use {@link BatchCompiler#compile(String, PrintWriter, PrintWriter, CompilationProgress)} instead
 *                       e.g. BatchCompiler.compile(commandLine, outWriter, errWriter, null);
 */
public static boolean compile(String commandLine, PrintWriter outWriter, PrintWriter errWriter) {
	return new Main(outWriter, errWriter, false /* systemExit */, null /* options */, null /* progress */).compile(tokenize(commandLine));
}

/*
 * Internal API for public API BatchCompiler#compile(String[], PrintWriter, PrintWriter, CompilationProgress)
 */
public static boolean compile(String[] commandLineArguments, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress) {
	return new Main(outWriter, errWriter, false /* systemExit */, null /* options */, progress).compile(commandLineArguments);
}
public static File[][] getLibrariesFiles(File[] files) {
	FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return Util.isPotentialZipArchive(name);
		}
	};
	final int filesLength = files.length;
	File[][] result = new File[filesLength][];
	for (int i = 0; i < filesLength; i++) {
		File currentFile = files[i];
		if (currentFile.exists() && currentFile.isDirectory()) {
			result[i] = currentFile.listFiles(filter);
		}
	}
	return result;
}

public static void main(String[] argv) {
	new Main(new PrintWriter(System.out), new PrintWriter(System.err), true/*systemExit*/, null/*options*/, null/*progress*/).compile(argv);
}

public static String[] tokenize(String commandLine) {

	int count = 0;
	String[] arguments = new String[10];
	StringTokenizer tokenizer = new StringTokenizer(commandLine, " \"", true); //$NON-NLS-1$
	String token = Util.EMPTY_STRING;
	boolean insideQuotes = false;
	boolean startNewToken = true;

	// take care to quotes on the command line
	// 'xxx "aaa bbb";ccc yyy' --->  {"xxx", "aaa bbb;ccc", "yyy" }
	// 'xxx "aaa bbb;ccc" yyy' --->  {"xxx", "aaa bbb;ccc", "yyy" }
	// 'xxx "aaa bbb";"ccc" yyy' --->  {"xxx", "aaa bbb;ccc", "yyy" }
	// 'xxx/"aaa bbb";"ccc" yyy' --->  {"xxx/aaa bbb;ccc", "yyy" }
	while (tokenizer.hasMoreTokens()) {
		token = tokenizer.nextToken();

		if (token.equals(" ")) { //$NON-NLS-1$
			if (insideQuotes) {
				arguments[count - 1] += token;
				startNewToken = false;
			} else {
				startNewToken = true;
			}
		} else if (token.equals("\"")) { //$NON-NLS-1$
			if (!insideQuotes && startNewToken) {
				if (count == arguments.length)
					System.arraycopy(arguments, 0, (arguments = new String[count * 2]), 0, count);
				arguments[count++] = Util.EMPTY_STRING;
			}
			insideQuotes = !insideQuotes;
			startNewToken = false;
		} else {
			if (insideQuotes) {
				arguments[count - 1] += token;
			} else {
				if (token.length() > 0 && !startNewToken) {
					arguments[count - 1] += token;
				} else {
					if (count == arguments.length)
						System.arraycopy(arguments, 0, (arguments = new String[count * 2]), 0, count);
					String trimmedToken = token.trim();
					if (trimmedToken.length() != 0) {
						arguments[count++] = trimmedToken;
					}
				}
			}
			startNewToken = false;
		}
	}
	System.arraycopy(arguments, 0, arguments = new String[count], 0, count);
	return arguments;
}

/**
 * @deprecated - use {@link #Main(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. Main(outWriter, errWriter, systemExitWhenFinished, null, null)
 */
public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished) {
	this(outWriter, errWriter, systemExitWhenFinished, null /* options */, null /* progress */);
}

/**
 * @deprecated - use {@link #Main(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. Main(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, null)
 */
public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map customDefaultOptions) {
	this(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, null /* progress */);
}

public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map customDefaultOptions, CompilationProgress compilationProgress) {
	this.initialize(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress);
	this.relocalize();
}

public void addExtraProblems(CategorizedProblem problem) {
	if (this.extraProblems == null) {
		this.extraProblems = new ArrayList();
	}
	this.extraProblems.add(problem);
}
protected void addNewEntry(ArrayList paths, String currentClasspathName,
		ArrayList currentRuleSpecs, String customEncoding,
		String destPath, boolean isSourceOnly,
		boolean rejectDestinationPathOnJars) {

	int rulesSpecsSize = currentRuleSpecs.size();
	AccessRuleSet accessRuleSet = null;
	if (rulesSpecsSize != 0) {
		AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
		boolean rulesOK = true;
		Iterator i = currentRuleSpecs.iterator();
		int j = 0;
		while (i.hasNext()) {
			String ruleSpec = (String) i.next();
			char key = ruleSpec.charAt(0);
			String pattern = ruleSpec.substring(1);
			if (pattern.length() > 0) {
				switch (key) {
					case '+':
						accessRules[j++] = new AccessRule(pattern
								.toCharArray(), 0);
						break;
					case '~':
						accessRules[j++] = new AccessRule(pattern
								.toCharArray(),
								IProblem.DiscouragedReference);
						break;
					case '-':
						accessRules[j++] = new AccessRule(pattern
								.toCharArray(),
								IProblem.ForbiddenReference);
						break;
					case '?':
						accessRules[j++] = new AccessRule(pattern
								.toCharArray(),
								IProblem.ForbiddenReference, true/*keep looking for accessible type*/);
						break;
					default:
						rulesOK = false;
				}
			} else {
				rulesOK = false;
			}
		}
		if (rulesOK) {
    		accessRuleSet = new AccessRuleSet(accessRules, AccessRestriction.COMMAND_LINE, currentClasspathName);
		} else {
			if (currentClasspathName.length() != 0) {
				// we go on anyway
				addPendingErrors(this.bind("configure.incorrectClasspath", currentClasspathName));//$NON-NLS-1$
			}
			return;
		}
	}
	if (NONE.equals(destPath)) {
		destPath = NONE; // keep == comparison valid
	}
	if (rejectDestinationPathOnJars && destPath != null &&
			Util.isPotentialZipArchive(currentClasspathName)) {
		throw new IllegalArgumentException(
			this.bind("configure.unexpectedDestinationPathEntryFile", //$NON-NLS-1$
						currentClasspathName));
	}
	FileSystem.Classpath currentClasspath = FileSystem.getClasspath(
			currentClasspathName,
			customEncoding,
			isSourceOnly,
			accessRuleSet,
			destPath);
	if (currentClasspath != null) {
		paths.add(currentClasspath);
	} else if (currentClasspathName.length() != 0) {
		// we go on anyway
		addPendingErrors(this.bind("configure.incorrectClasspath", currentClasspathName));//$NON-NLS-1$
	}
}
void addPendingErrors(String message) {
	if (this.pendingErrors == null) {
		this.pendingErrors = new ArrayList();
	}
	this.pendingErrors.add(message);
}
/*
 * Lookup the message with the given ID in this catalog
 */
public String bind(String id) {
	return bind(id, (String[]) null);
}
/*
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given string.
 */
public String bind(String id, String binding) {
	return bind(id, new String[] { binding });
}

/*
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given strings.
 */
public String bind(String id, String binding1, String binding2) {
	return bind(id, new String[] { binding1, binding2 });
}

/*
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given string values.
 */
public String bind(String id, String[] arguments) {
	if (id == null)
		return "No message available"; //$NON-NLS-1$
	String message = null;
	try {
		message = this.bundle.getString(id);
	} catch (MissingResourceException e) {
		// If we got an exception looking for the message, fail gracefully by just returning
		// the id we were looking for.  In most cases this is semi-informative so is not too bad.
		return "Missing message: " + id + " in: " + Main.bundleName; //$NON-NLS-2$ //$NON-NLS-1$
	}
	return MessageFormat.format(message, arguments);
}
/**
 * Return true if and only if the running VM supports the given minimal version.
 *
 * <p>This only checks the major version, since the minor version is always 0 (at least for the useful cases).</p>
 * <p>The given minimalSupportedVersion is one of the constants:</p>
 * <ul>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_1</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_2</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_3</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_4</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_5</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_6</code></li>
 * <li><code>org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants.JDK1_7</code></li>
 * </ul>
 * @param minimalSupportedVersion the given minimal version
 * @return true if and only if the running VM supports the given minimal version, false otherwise
 */
private boolean checkVMVersion(long minimalSupportedVersion) {
	// the format of this property is supposed to be xx.x where x are digits.
	String classFileVersion = System.getProperty("java.class.version"); //$NON-NLS-1$
	if (classFileVersion == null) {
		// by default we don't support a class file version we cannot recognize
		return false;
	}
	int index = classFileVersion.indexOf('.');
	if (index == -1) {
		// by default we don't support a class file version we cannot recognize
		return false;
	}
	int majorVersion;
	try {
		majorVersion = Integer.parseInt(classFileVersion.substring(0, index));
	} catch (NumberFormatException e) {
		// by default we don't support a class file version we cannot recognize
		return false;
	}
	switch(majorVersion) {
		case 45 : // 1.0 and 1.1
			return ClassFileConstants.JDK1_1 >= minimalSupportedVersion;
		case 46 : // 1.2
			return ClassFileConstants.JDK1_2 >= minimalSupportedVersion;
		case 47 : // 1.3
			return ClassFileConstants.JDK1_3 >= minimalSupportedVersion;
		case 48 : // 1.4
			return ClassFileConstants.JDK1_4 >= minimalSupportedVersion;
		case 49 : // 1.5
			return ClassFileConstants.JDK1_5 >= minimalSupportedVersion;
		case 50 : // 1.6
			return ClassFileConstants.JDK1_6 >= minimalSupportedVersion;
		case 51 : // 1.7
			return ClassFileConstants.JDK1_7 >= minimalSupportedVersion;
	}
	// unknown version
	return false;
}
/*
 *  Low-level API performing the actual compilation
 */
public boolean compile(String[] argv) {

	// decode command line arguments
	try {
		configure(argv);
		if (this.progress != null)
			this.progress.begin(this.filenames == null ? 0 : this.filenames.length * this.maxRepetition);
		if (this.proceed) {
//				if (this.verbose) {
//					System.out.println(new CompilerOptions(this.options));
//				}
			if (this.showProgress) this.logger.compiling();
			for (this.currentRepetition = 0; this.currentRepetition < this.maxRepetition; this.currentRepetition++) {
				this.globalProblemsCount = 0;
				this.globalErrorsCount = 0;
				this.globalWarningsCount = 0;
				this.globalTasksCount = 0;
				this.exportedClassFilesCounter = 0;

				if (this.maxRepetition > 1) {
					this.logger.flush();
					this.logger.logRepetition(this.currentRepetition, this.maxRepetition);
				}
				// request compilation
				performCompilation();
			}
			if (this.compilerStats != null) {
				this.logger.logAverage();
			}
			if (this.showProgress) this.logger.printNewLine();
		}
		if (this.systemExitWhenFinished) {
			this.logger.flush();
			this.logger.close();
			System.exit(this.globalErrorsCount > 0 ? -1 : 0);
		}
	} catch (IllegalArgumentException e) {
		this.logger.logException(e);
		if (this.systemExitWhenFinished) {
			this.logger.flush();
			this.logger.close();
			System.exit(-1);
		}
		return false;
	} catch (RuntimeException e) { // internal compiler failure
		this.logger.logException(e);
		if (this.systemExitWhenFinished) {
			this.logger.flush();
			this.logger.close();
			System.exit(-1);
		}
		return false;
	} finally {
		this.logger.flush();
		this.logger.close();
		if (this.progress != null)
			this.progress.done();
	}
	if (this.globalErrorsCount == 0 && (this.progress == null || !this.progress.isCanceled()))
		return true;
	return false;
}

/*
Decode the command line arguments
 */
public void configure(String[] argv) {

	if ((argv == null) || (argv.length == 0)) {
		printUsage();
		return;
	}

	final int INSIDE_CLASSPATH_start = 1;
	final int INSIDE_DESTINATION_PATH = 3;
	final int INSIDE_TARGET = 4;
	final int INSIDE_LOG = 5;
	final int INSIDE_REPETITION = 6;
	final int INSIDE_SOURCE = 7;
	final int INSIDE_DEFAULT_ENCODING = 8;
	final int INSIDE_BOOTCLASSPATH_start = 9;
	final int INSIDE_MAX_PROBLEMS = 11;
	final int INSIDE_EXT_DIRS = 12;
	final int INSIDE_SOURCE_PATH_start = 13;
	final int INSIDE_ENDORSED_DIRS = 15;
	final int INSIDE_SOURCE_DIRECTORY_DESTINATION_PATH = 16;
	final int INSIDE_PROCESSOR_PATH_start = 17;
	final int INSIDE_PROCESSOR_start = 18;
	final int INSIDE_S_start = 19;
	final int INSIDE_CLASS_NAMES = 20;
	final int INSIDE_WARNINGS_PROPERTIES = 21;

	final int DEFAULT = 0;
	ArrayList bootclasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
	String sourcepathClasspathArg = null;
	ArrayList sourcepathClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
	ArrayList classpaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
	ArrayList extdirsClasspaths = null;
	ArrayList endorsedDirClasspaths = null;

	int index = -1;
	int filesCount = 0;
	int classCount = 0;
	int argCount = argv.length;
	int mode = DEFAULT;
	this.maxRepetition = 0;
	boolean printUsageRequired = false;
	String usageSection = null;
	boolean printVersionRequired = false;

	boolean didSpecifyDeprecation = false;
	boolean didSpecifyCompliance = false;
	boolean didSpecifyDisabledAnnotationProcessing = false;

	String customEncoding = null;
	String customDestinationPath = null;
	String currentSourceDirectory = null;
	String currentArg = Util.EMPTY_STRING;
	
	Set specifiedEncodings = null;

	// expand the command line if necessary
	boolean needExpansion = false;
	loop: for (int i = 0; i < argCount; i++) {
			if (argv[i].startsWith("@")) { //$NON-NLS-1$
				needExpansion = true;
				break loop;
			}
	}

	String[] newCommandLineArgs = null;
	if (needExpansion) {
		newCommandLineArgs = new String[argCount];
		index = 0;
		for (int i = 0; i < argCount; i++) {
			String[] newArgs = null;
			String arg = argv[i].trim();
			if (arg.startsWith("@")) { //$NON-NLS-1$
				try {
					LineNumberReader reader = new LineNumberReader(new StringReader(new String(Util.getFileCharContent(new File(arg.substring(1)), null))));
					StringBuffer buffer = new StringBuffer();
					String line;
					while((line = reader.readLine()) != null) {
						line = line.trim();
						if (!line.startsWith("#")) { //$NON-NLS-1$
							buffer.append(line).append(" "); //$NON-NLS-1$
						}
					}
					newArgs = tokenize(buffer.toString());
				} catch(IOException e) {
					throw new IllegalArgumentException(
						this.bind("configure.invalidexpansionargumentname", arg)); //$NON-NLS-1$
				}
			}
			if (newArgs != null) {
				int newCommandLineArgsLength = newCommandLineArgs.length;
				int newArgsLength = newArgs.length;
				System.arraycopy(newCommandLineArgs, 0, (newCommandLineArgs = new String[newCommandLineArgsLength + newArgsLength - 1]), 0, index);
				System.arraycopy(newArgs, 0, newCommandLineArgs, index, newArgsLength);
				index += newArgsLength;
			} else {
				newCommandLineArgs[index++] = arg;
			}
		}
		index = -1;
	} else {
		newCommandLineArgs = argv;
		for (int i = 0; i < argCount; i++) {
			newCommandLineArgs[i] = newCommandLineArgs[i].trim();
		}
	}
	argCount = newCommandLineArgs.length;
	this.expandedCommandLine = newCommandLineArgs;
	while (++index < argCount) {

		if (customEncoding != null) {
			throw new IllegalArgumentException(
				this.bind("configure.unexpectedCustomEncoding", currentArg, customEncoding)); //$NON-NLS-1$
		}

		currentArg = newCommandLineArgs[index];

		switch(mode) {
			case DEFAULT :
				if (currentArg.startsWith("[")) { //$NON-NLS-1$
					throw new IllegalArgumentException(
						this.bind("configure.unexpectedBracket", //$NON-NLS-1$
									currentArg));
				}

				if (currentArg.endsWith("]")) { //$NON-NLS-1$
					// look for encoding specification
					int encodingStart = currentArg.indexOf('[') + 1;
					if (encodingStart <= 1) {
						throw new IllegalArgumentException(
								this.bind("configure.unexpectedBracket", currentArg)); //$NON-NLS-1$
					}
					int encodingEnd = currentArg.length() - 1;
					if (encodingStart >= 1) {
						if (encodingStart < encodingEnd) {
							customEncoding = currentArg.substring(encodingStart, encodingEnd);
							try { // ensure encoding is supported
								new InputStreamReader(new ByteArrayInputStream(new byte[0]), customEncoding);
							} catch (UnsupportedEncodingException e) {
								throw new IllegalArgumentException(
									this.bind("configure.unsupportedEncoding", customEncoding)); //$NON-NLS-1$
							}
						}
						currentArg = currentArg.substring(0, encodingStart - 1);
					}
				}

				if (currentArg.endsWith(SuffixConstants.SUFFIX_STRING_java)) {
					if (this.filenames == null) {
						this.filenames = new String[argCount - index];
						this.encodings = new String[argCount - index];
						this.destinationPaths = new String[argCount - index];
					} else if (filesCount == this.filenames.length) {
						int length = this.filenames.length;
						System.arraycopy(
							this.filenames,
							0,
							(this.filenames = new String[length + argCount - index]),
							0,
							length);
						System.arraycopy(
							this.encodings,
							0,
							(this.encodings = new String[length + argCount - index]),
							0,
							length);
						System.arraycopy(
							this.destinationPaths,
							0,
							(this.destinationPaths = new String[length + argCount - index]),
							0,
							length);
					}
					this.filenames[filesCount] = currentArg;
					this.encodings[filesCount++] = customEncoding;
					// destination path cannot be specified upon an individual file
					customEncoding = null;
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-log")) { //$NON-NLS-1$
					if (this.log != null)
						throw new IllegalArgumentException(
							this.bind("configure.duplicateLog", currentArg)); //$NON-NLS-1$
					mode = INSIDE_LOG;
					continue;
				}
				if (currentArg.equals("-repeat")) { //$NON-NLS-1$
					if (this.maxRepetition > 0)
						throw new IllegalArgumentException(
							this.bind("configure.duplicateRepeat", currentArg)); //$NON-NLS-1$
					mode = INSIDE_REPETITION;
					continue;
				}
				if (currentArg.equals("-maxProblems")) { //$NON-NLS-1$
					if (this.maxProblems > 0)
						throw new IllegalArgumentException(
							this.bind("configure.duplicateMaxProblems", currentArg)); //$NON-NLS-1$
					mode = INSIDE_MAX_PROBLEMS;
					continue;
				}
				if (currentArg.equals("-source")) { //$NON-NLS-1$
					mode = INSIDE_SOURCE;
					continue;
				}
				if (currentArg.equals("-encoding")) { //$NON-NLS-1$
					mode = INSIDE_DEFAULT_ENCODING;
					continue;
				}
				if (currentArg.equals("-1.3")) { //$NON-NLS-1$
					if (didSpecifyCompliance) {
						throw new IllegalArgumentException(
							this.bind("configure.duplicateCompliance", currentArg));//$NON-NLS-1$
					}
					didSpecifyCompliance = true;
					this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_3);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-1.4")) { //$NON-NLS-1$
					if (didSpecifyCompliance) {
						throw new IllegalArgumentException(
							this.bind("configure.duplicateCompliance", currentArg)); //$NON-NLS-1$
					}
					didSpecifyCompliance = true;
					this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_4);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-1.5") || currentArg.equals("-5") || currentArg.equals("-5.0")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if (didSpecifyCompliance) {
						throw new IllegalArgumentException(
							this.bind("configure.duplicateCompliance", currentArg)); //$NON-NLS-1$
					}
					didSpecifyCompliance = true;
					this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_5);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-1.6") || currentArg.equals("-6") || currentArg.equals("-6.0")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if (didSpecifyCompliance) {
						throw new IllegalArgumentException(
							this.bind("configure.duplicateCompliance", currentArg)); //$NON-NLS-1$
					}
					didSpecifyCompliance = true;
					this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_6);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-1.7") || currentArg.equals("-7") || currentArg.equals("-7.0")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if (didSpecifyCompliance) {
						throw new IllegalArgumentException(
							this.bind("configure.duplicateCompliance", currentArg)); //$NON-NLS-1$
					}
					didSpecifyCompliance = true;
					this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_7);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-d")) { //$NON-NLS-1$
					if (this.destinationPath != null) {
						StringBuffer errorMessage = new StringBuffer();
						errorMessage.append(currentArg);
						if ((index + 1) < argCount) {
							errorMessage.append(' ');
							errorMessage.append(newCommandLineArgs[index + 1]);
						}
						throw new IllegalArgumentException(
							this.bind("configure.duplicateOutputPath", errorMessage.toString())); //$NON-NLS-1$
					}
					mode = INSIDE_DESTINATION_PATH;
					continue;
				}
				if (currentArg.equals("-classpath") //$NON-NLS-1$
					|| currentArg.equals("-cp")) { //$NON-NLS-1$
					mode = INSIDE_CLASSPATH_start;
					continue;
				}
				if (currentArg.equals("-bootclasspath")) {//$NON-NLS-1$
					if (bootclasspaths.size() > 0) {
						StringBuffer errorMessage = new StringBuffer();
						errorMessage.append(currentArg);
						if ((index + 1) < argCount) {
							errorMessage.append(' ');
							errorMessage.append(newCommandLineArgs[index + 1]);
						}
						throw new IllegalArgumentException(
							this.bind("configure.duplicateBootClasspath", errorMessage.toString())); //$NON-NLS-1$
					}
					mode = INSIDE_BOOTCLASSPATH_start;
					continue;
				}
				if (currentArg.equals("-sourcepath")) {//$NON-NLS-1$
					if (sourcepathClasspathArg != null) {
						StringBuffer errorMessage = new StringBuffer();
						errorMessage.append(currentArg);
						if ((index + 1) < argCount) {
							errorMessage.append(' ');
							errorMessage.append(newCommandLineArgs[index + 1]);
						}
						throw new IllegalArgumentException(
							this.bind("configure.duplicateSourcepath", errorMessage.toString())); //$NON-NLS-1$
					}
					mode = INSIDE_SOURCE_PATH_start;
					continue;
				}
				if (currentArg.equals("-extdirs")) {//$NON-NLS-1$
					if (extdirsClasspaths != null) {
						StringBuffer errorMessage = new StringBuffer();
						errorMessage.append(currentArg);
						if ((index + 1) < argCount) {
							errorMessage.append(' ');
							errorMessage.append(newCommandLineArgs[index + 1]);
						}
						throw new IllegalArgumentException(
							this.bind("configure.duplicateExtDirs", errorMessage.toString())); //$NON-NLS-1$
					}
					mode = INSIDE_EXT_DIRS;
					continue;
				}
				if (currentArg.equals("-endorseddirs")) { //$NON-NLS-1$
					if (endorsedDirClasspaths != null) {
						StringBuffer errorMessage = new StringBuffer();
						errorMessage.append(currentArg);
						if ((index + 1) < argCount) {
							errorMessage.append(' ');
							errorMessage.append(newCommandLineArgs[index + 1]);
						}
						throw new IllegalArgumentException(
							this.bind("configure.duplicateEndorsedDirs", errorMessage.toString())); //$NON-NLS-1$
					}
					mode = INSIDE_ENDORSED_DIRS;
					continue;
				}
				if (currentArg.equals("-progress")) { //$NON-NLS-1$
					mode = DEFAULT;
					this.showProgress = true;
					continue;
				}
				if (currentArg.startsWith("-proceedOnError")) { //$NON-NLS-1$
					mode = DEFAULT;
					int length = currentArg.length();
					if (length > 15) {
						if (currentArg.equals("-proceedOnError:Fatal")) { //$NON-NLS-1$
							this.options.put(CompilerOptions.OPTION_FatalOptionalError, CompilerOptions.ENABLED);
						} else {
							throw new IllegalArgumentException(
									this.bind("configure.invalidWarningConfiguration", currentArg)); //$NON-NLS-1$
						}
					} else {
						this.options.put(CompilerOptions.OPTION_FatalOptionalError, CompilerOptions.DISABLED);
					}
					this.proceedOnError = true;
					continue;
				}
				if (currentArg.equals("-time")) { //$NON-NLS-1$
					mode = DEFAULT;
					this.timing = TIMING_ENABLED;
					continue;
				}
				if (currentArg.equals("-time:detail")) { //$NON-NLS-1$
					mode = DEFAULT;
					this.timing = TIMING_ENABLED|TIMING_DETAILED;
					continue;
				}
				if (currentArg.equals("-version") //$NON-NLS-1$
						|| currentArg.equals("-v")) { //$NON-NLS-1$
					this.logger.logVersion(true);
					this.proceed = false;
					return;
				}
				if (currentArg.equals("-showversion")) { //$NON-NLS-1$
					printVersionRequired = true;
					mode = DEFAULT;
					continue;
				}
				if ("-deprecation".equals(currentArg)) { //$NON-NLS-1$
					didSpecifyDeprecation = true;
					this.options.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.WARNING);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-help") || currentArg.equals("-?")) { //$NON-NLS-1$ //$NON-NLS-2$
					printUsageRequired = true;
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-help:warn") || //$NON-NLS-1$
						currentArg.equals("-?:warn")) { //$NON-NLS-1$
					printUsageRequired = true;
					usageSection = "misc.usage.warn"; //$NON-NLS-1$
					continue;
				}
				if (currentArg.equals("-noExit")) { //$NON-NLS-1$
					this.systemExitWhenFinished = false;
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-verbose")) { //$NON-NLS-1$
					this.verbose = true;
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-referenceInfo")) { //$NON-NLS-1$
					this.produceRefInfo = true;
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-inlineJSR")) { //$NON-NLS-1$
					mode = DEFAULT;
					this.options.put(
							CompilerOptions.OPTION_InlineJsr,
							CompilerOptions.ENABLED);
					continue;
				}
				if (currentArg.startsWith("-g")) { //$NON-NLS-1$
					mode = DEFAULT;
					String debugOption = currentArg;
					int length = currentArg.length();
					if (length == 2) {
						this.options.put(
							CompilerOptions.OPTION_LocalVariableAttribute,
							CompilerOptions.GENERATE);
						this.options.put(
							CompilerOptions.OPTION_LineNumberAttribute,
							CompilerOptions.GENERATE);
						this.options.put(
							CompilerOptions.OPTION_SourceFileAttribute,
							CompilerOptions.GENERATE);
						continue;
					}
					if (length > 3) {
						this.options.put(
							CompilerOptions.OPTION_LocalVariableAttribute,
							CompilerOptions.DO_NOT_GENERATE);
						this.options.put(
							CompilerOptions.OPTION_LineNumberAttribute,
							CompilerOptions.DO_NOT_GENERATE);
						this.options.put(
							CompilerOptions.OPTION_SourceFileAttribute,
							CompilerOptions.DO_NOT_GENERATE);
						if (length == 7 && debugOption.equals("-g:" + NONE)) //$NON-NLS-1$
							continue;
						StringTokenizer tokenizer =
							new StringTokenizer(debugOption.substring(3, debugOption.length()), ","); //$NON-NLS-1$
						while (tokenizer.hasMoreTokens()) {
							String token = tokenizer.nextToken();
							if (token.equals("vars")) { //$NON-NLS-1$
								this.options.put(
									CompilerOptions.OPTION_LocalVariableAttribute,
									CompilerOptions.GENERATE);
							} else if (token.equals("lines")) { //$NON-NLS-1$
								this.options.put(
									CompilerOptions.OPTION_LineNumberAttribute,
									CompilerOptions.GENERATE);
							} else if (token.equals("source")) { //$NON-NLS-1$
								this.options.put(
									CompilerOptions.OPTION_SourceFileAttribute,
									CompilerOptions.GENERATE);
							} else {
								throw new IllegalArgumentException(
									this.bind("configure.invalidDebugOption", debugOption)); //$NON-NLS-1$
							}
						}
						continue;
					}
					throw new IllegalArgumentException(
						this.bind("configure.invalidDebugOption", debugOption)); //$NON-NLS-1$
				}
				if (currentArg.startsWith("-nowarn")) { //$NON-NLS-1$
					disableWarnings();
					mode = DEFAULT;
					continue;
				}
				if (currentArg.startsWith("-warn")) { //$NON-NLS-1$
					mode = DEFAULT;
					String warningOption = currentArg;
					int length = currentArg.length();
					if (length == 10 && warningOption.equals("-warn:" + NONE)) { //$NON-NLS-1$
						disableWarnings();
						continue;
					}
					if (length <= 6) {
						throw new IllegalArgumentException(
							this.bind("configure.invalidWarningConfiguration", warningOption)); //$NON-NLS-1$
					}
					int warnTokenStart;
					boolean isEnabling, allowPlusOrMinus;
					switch (warningOption.charAt(6)) {
						case '+' :
							warnTokenStart = 7;
							isEnabling = true;
							allowPlusOrMinus = true;
							break;
						case '-' :
							warnTokenStart = 7;
							isEnabling = false; // specified warnings are disabled
							allowPlusOrMinus = true;
							break;
						default:
							disableWarnings();
							warnTokenStart = 6;
							isEnabling = true;
							allowPlusOrMinus = false;
					}

					StringTokenizer tokenizer =
						new StringTokenizer(warningOption.substring(warnTokenStart, warningOption.length()), ","); //$NON-NLS-1$
					int tokenCounter = 0;

					if (didSpecifyDeprecation) {  // deprecation could have also been set through -deprecation option
						this.options.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.WARNING);
					}

					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						tokenCounter++;
						switch(token.charAt(0)) {
							case '+' :
								if (allowPlusOrMinus) {
									isEnabling = true;
									token = token.substring(1);
								} else {
									throw new IllegalArgumentException(
											this.bind("configure.invalidUsageOfPlusOption", token)); //$NON-NLS-1$
								}
								break;
							case '-' :
								if (allowPlusOrMinus) {
									isEnabling = false;
									token = token.substring(1);
								} else {
									throw new IllegalArgumentException(
											this.bind("configure.invalidUsageOfMinusOption", token)); //$NON-NLS-1$
								}
						}
						handleWarningToken(token, isEnabling);
					}
					if (tokenCounter == 0) {
						throw new IllegalArgumentException(
							this.bind("configure.invalidWarningOption", currentArg)); //$NON-NLS-1$
					}
					continue;
				}
				if (currentArg.startsWith("-err")) { //$NON-NLS-1$
					mode = DEFAULT;
					String errorOption = currentArg;
					int length = currentArg.length();
					if (length <= 5) {
						throw new IllegalArgumentException(
							this.bind("configure.invalidErrorConfiguration", errorOption)); //$NON-NLS-1$
					}
					int errorTokenStart;
					boolean isEnabling, allowPlusOrMinus;
					switch (errorOption.charAt(5)) {
						case '+' :
							errorTokenStart = 6;
							isEnabling = true;
							allowPlusOrMinus = true;
							break;
						case '-' :
							errorTokenStart = 6;
							isEnabling = false; // specified errors are disabled
							allowPlusOrMinus = true;
							break;
						default:
							disableErrors();
							errorTokenStart = 5;
							isEnabling = true;
							allowPlusOrMinus = false;
					}

					StringTokenizer tokenizer =
						new StringTokenizer(errorOption.substring(errorTokenStart, errorOption.length()), ","); //$NON-NLS-1$
					int tokenCounter = 0;

					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						tokenCounter++;
						switch(token.charAt(0)) {
							case '+' :
								if (allowPlusOrMinus) {
									isEnabling = true;
									token = token.substring(1);
								} else {
									throw new IllegalArgumentException(
											this.bind("configure.invalidUsageOfPlusOption", token)); //$NON-NLS-1$
								}
								break;
							case '-' :
								if (allowPlusOrMinus) {
									isEnabling = false;
									token = token.substring(1);
								} else {
									throw new IllegalArgumentException(
											this.bind("configure.invalidUsageOfMinusOption", token)); //$NON-NLS-1$
								}
								break;
						}
						handleErrorToken(token, isEnabling);
					}
					if (tokenCounter == 0) {
						throw new IllegalArgumentException(
							this.bind("configure.invalidErrorOption", currentArg)); //$NON-NLS-1$
					}
					continue;
				}
				if (currentArg.equals("-target")) { //$NON-NLS-1$
					mode = INSIDE_TARGET;
					continue;
				}
				if (currentArg.equals("-preserveAllLocals")) { //$NON-NLS-1$
					this.options.put(
						CompilerOptions.OPTION_PreserveUnusedLocal,
						CompilerOptions.PRESERVE);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-enableJavadoc")) {//$NON-NLS-1$
					mode = DEFAULT;
					this.enableJavadocOn = true;
					continue;
				}
				if (currentArg.equals("-Xemacs")) { //$NON-NLS-1$
					mode = DEFAULT;
					this.logger.setEmacs();
					continue;
				}
				// annotation processing
				if (currentArg.startsWith("-A")) { //$NON-NLS-1$
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-processorpath")) { //$NON-NLS-1$
					mode = INSIDE_PROCESSOR_PATH_start;
					continue;
				}
				if (currentArg.equals("-processor")) { //$NON-NLS-1$
					mode = INSIDE_PROCESSOR_start;
					continue;
				}
				if (currentArg.equals("-proc:only")) { //$NON-NLS-1$
					this.options.put(
						CompilerOptions.OPTION_GenerateClassFiles,
						CompilerOptions.DISABLED);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-proc:none")) { //$NON-NLS-1$
					didSpecifyDisabledAnnotationProcessing = true;
					this.options.put(
						CompilerOptions.OPTION_Process_Annotations,
						CompilerOptions.DISABLED);
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-s")) { //$NON-NLS-1$
					mode = INSIDE_S_start;
					continue;
				}
				if (currentArg.equals("-XprintProcessorInfo") //$NON-NLS-1$
						|| currentArg.equals("-XprintRounds")) { //$NON-NLS-1$
					mode = DEFAULT;
					continue;
				}
				// tolerated javac options - quietly filtered out
				if (currentArg.startsWith("-X")) { //$NON-NLS-1$
					mode = DEFAULT;
					continue;
				}
				if (currentArg.startsWith("-J")) { //$NON-NLS-1$
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-O")) { //$NON-NLS-1$
					mode = DEFAULT;
					continue;
				}
				if (currentArg.equals("-classNames")) { //$NON-NLS-1$
					mode = INSIDE_CLASS_NAMES;
					continue;
				}
				if (currentArg.equals("-properties")) { //$NON-NLS-1$
					mode = INSIDE_WARNINGS_PROPERTIES;
					continue;
				}
				break;
			case INSIDE_TARGET :
				if (this.didSpecifyTarget) {
					throw new IllegalArgumentException(
						this.bind("configure.duplicateTarget", currentArg));//$NON-NLS-1$
				}
				this.didSpecifyTarget = true;
				if (currentArg.equals("1.1")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_1);
				} else if (currentArg.equals("1.2")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
				} else if (currentArg.equals("1.3")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_3);
				} else if (currentArg.equals("1.4")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
				} else if (currentArg.equals("1.5") || currentArg.equals("5") || currentArg.equals("5.0")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
				} else if (currentArg.equals("1.6") || currentArg.equals("6") || currentArg.equals("6.0")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
				} else if (currentArg.equals("1.7") || currentArg.equals("7") || currentArg.equals("7.0")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
				} else if (currentArg.equals("jsr14")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_JSR14);
				} else if (currentArg.equals("cldc1.1")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_CLDC1_1);
					this.options.put(CompilerOptions.OPTION_InlineJsr, CompilerOptions.ENABLED);
				}else {
					throw new IllegalArgumentException(this.bind("configure.targetJDK", currentArg)); //$NON-NLS-1$
				}
				mode = DEFAULT;
				continue;
			case INSIDE_LOG :
				this.log = currentArg;
				mode = DEFAULT;
				continue;
			case INSIDE_REPETITION :
				try {
					this.maxRepetition = Integer.parseInt(currentArg);
					if (this.maxRepetition <= 0) {
						throw new IllegalArgumentException(this.bind("configure.repetition", currentArg)); //$NON-NLS-1$
					}
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(this.bind("configure.repetition", currentArg)); //$NON-NLS-1$
				}
				mode = DEFAULT;
				continue;
			case INSIDE_MAX_PROBLEMS :
				try {
					this.maxProblems = Integer.parseInt(currentArg);
					if (this.maxProblems <= 0) {
						throw new IllegalArgumentException(this.bind("configure.maxProblems", currentArg)); //$NON-NLS-1$
					}
					this.options.put(CompilerOptions.OPTION_MaxProblemPerUnit, currentArg);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(this.bind("configure.maxProblems", currentArg)); //$NON-NLS-1$
				}
				mode = DEFAULT;
				continue;
			case INSIDE_SOURCE :
				if (this.didSpecifySource) {
					throw new IllegalArgumentException(
						this.bind("configure.duplicateSource", currentArg));//$NON-NLS-1$
				}
				this.didSpecifySource = true;
				if (currentArg.equals("1.3")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_3);
				} else if (currentArg.equals("1.4")) { //$NON-NLS-1$
					this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_4);
				} else if (currentArg.equals("1.5") || currentArg.equals("5") || currentArg.equals("5.0")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
				} else if (currentArg.equals("1.6") || currentArg.equals("6") || currentArg.equals("6.0")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_6);
				} else if (currentArg.equals("1.7") || currentArg.equals("7") || currentArg.equals("7.0")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_7);
				} else {
					throw new IllegalArgumentException(this.bind("configure.source", currentArg)); //$NON-NLS-1$
				}
				mode = DEFAULT;
				continue;
			case INSIDE_DEFAULT_ENCODING :
				if (specifiedEncodings != null) {
					// check already defined encoding
					if (!specifiedEncodings.contains(currentArg)) {
						if (specifiedEncodings.size() > 1) {
							this.logger.logWarning(
									this.bind("configure.differentencodings", //$NON-NLS-1$
									currentArg,
									getAllEncodings(specifiedEncodings)));
						} else {
							this.logger.logWarning(
									this.bind("configure.differentencoding", //$NON-NLS-1$
									currentArg,
									getAllEncodings(specifiedEncodings)));
						}
					}
				} else {
					specifiedEncodings = new HashSet();
				}
				try { // ensure encoding is supported
					new InputStreamReader(new ByteArrayInputStream(new byte[0]), currentArg);
				} catch (UnsupportedEncodingException e) {
					throw new IllegalArgumentException(
						this.bind("configure.unsupportedEncoding", currentArg)); //$NON-NLS-1$
				}
				specifiedEncodings.add(currentArg);
				this.options.put(CompilerOptions.OPTION_Encoding, currentArg);
				mode = DEFAULT;
				continue;
			case INSIDE_DESTINATION_PATH :
				setDestinationPath(currentArg.equals(NONE) ? NONE : currentArg);
				mode = DEFAULT;
				continue;
			case INSIDE_CLASSPATH_start:
				mode = DEFAULT;
				index += processPaths(newCommandLineArgs, index, currentArg, classpaths);
				continue;
			case INSIDE_BOOTCLASSPATH_start:
				mode = DEFAULT;
				index += processPaths(newCommandLineArgs, index, currentArg, bootclasspaths);
				continue;
			case INSIDE_SOURCE_PATH_start:
				mode = DEFAULT;
				String[] sourcePaths = new String[1];
				index += processPaths(newCommandLineArgs, index, currentArg, sourcePaths);
				sourcepathClasspathArg = sourcePaths[0];
				continue;
			case INSIDE_EXT_DIRS:
				if (currentArg.indexOf("[-d") != -1) { //$NON-NLS-1$
					throw new IllegalArgumentException(
						this.bind("configure.unexpectedDestinationPathEntry", //$NON-NLS-1$
							"-extdir")); //$NON-NLS-1$
				}
				StringTokenizer tokenizer = new StringTokenizer(currentArg,	File.pathSeparator, false);
				extdirsClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
				while (tokenizer.hasMoreTokens())
					extdirsClasspaths.add(tokenizer.nextToken());
				mode = DEFAULT;
				continue;
			case INSIDE_ENDORSED_DIRS:
				if (currentArg.indexOf("[-d") != -1) { //$NON-NLS-1$
					throw new IllegalArgumentException(
						this.bind("configure.unexpectedDestinationPathEntry", //$NON-NLS-1$
							"-endorseddirs")); //$NON-NLS-1$
				}				tokenizer = new StringTokenizer(currentArg,	File.pathSeparator, false);
				endorsedDirClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
				while (tokenizer.hasMoreTokens())
					endorsedDirClasspaths.add(tokenizer.nextToken());
				mode = DEFAULT;
				continue;
			case INSIDE_SOURCE_DIRECTORY_DESTINATION_PATH:
				if (currentArg.endsWith("]")) { //$NON-NLS-1$
					customDestinationPath = currentArg.substring(0,
						currentArg.length() - 1);
				} else {
					throw new IllegalArgumentException(
						this.bind("configure.incorrectDestinationPathEntry", //$NON-NLS-1$
							"[-d " + currentArg)); //$NON-NLS-1$
				}
				break;
			case INSIDE_PROCESSOR_PATH_start :
				// nothing to do here. This is consumed again by the AnnotationProcessorManager
				mode = DEFAULT;
				continue;
			case INSIDE_PROCESSOR_start :
				// nothing to do here. This is consumed again by the AnnotationProcessorManager
				mode = DEFAULT;
				continue;
			case INSIDE_S_start :
				// nothing to do here. This is consumed again by the AnnotationProcessorManager
				mode = DEFAULT;
				continue;
			case INSIDE_CLASS_NAMES :
				tokenizer = new StringTokenizer(currentArg, ","); //$NON-NLS-1$
				if (this.classNames == null) {
					this.classNames = new String[DEFAULT_SIZE_CLASSPATH];
				}
				while (tokenizer.hasMoreTokens()) {
					if (this.classNames.length == classCount) {
						// resize
						System.arraycopy(
							this.classNames,
							0,
							(this.classNames = new String[classCount * 2]),
							0,
							classCount);
					}
					this.classNames[classCount++] = tokenizer.nextToken();
				}
				mode = DEFAULT;
				continue;
			case INSIDE_WARNINGS_PROPERTIES :
				initializeWarnings(currentArg);
				mode = DEFAULT;
				continue;
		}

		// default is input directory, if no custom destination path exists
		if (customDestinationPath == null) {
			if (File.separatorChar != '/') {
				currentArg = currentArg.replace('/', File.separatorChar);
			}
			if (currentArg.endsWith("[-d")) { //$NON-NLS-1$
				currentSourceDirectory = currentArg.substring(0,
					currentArg.length() - 3);
				mode = INSIDE_SOURCE_DIRECTORY_DESTINATION_PATH;
				continue;
			}
			currentSourceDirectory = currentArg;
		}
		File dir = new File(currentSourceDirectory);
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(
				this.bind("configure.unrecognizedOption", currentSourceDirectory)); //$NON-NLS-1$
		}
		String[] result = FileFinder.find(dir, SuffixConstants.SUFFIX_STRING_JAVA);
		if (NONE.equals(customDestinationPath)) {
			customDestinationPath = NONE; // ensure == comparison
		}
		if (this.filenames != null) {
			// some source files were specified explicitly
			int length = result.length;
			System.arraycopy(
				this.filenames,
				0,
				(this.filenames = new String[length + filesCount]),
				0,
				filesCount);
			System.arraycopy(
				this.encodings,
				0,
				(this.encodings = new String[length + filesCount]),
				0,
				filesCount);
			System.arraycopy(
				this.destinationPaths,
				0,
				(this.destinationPaths = new String[length + filesCount]),
				0,
				filesCount);
			System.arraycopy(result, 0, this.filenames, filesCount, length);
			for (int i = 0; i < length; i++) {
				this.encodings[filesCount + i] = customEncoding;
				this.destinationPaths[filesCount + i] = customDestinationPath;
			}
			filesCount += length;
			customEncoding = null;
			customDestinationPath = null;
			currentSourceDirectory = null;
		} else {
			this.filenames = result;
			filesCount = this.filenames.length;
			this.encodings = new String[filesCount];
			this.destinationPaths = new String[filesCount];
			for (int i = 0; i < filesCount; i++) {
				this.encodings[i] = customEncoding;
				this.destinationPaths[i] = customDestinationPath;
			}
			customEncoding = null;
			customDestinationPath = null;
			currentSourceDirectory = null;
		}
		mode = DEFAULT;
		continue;
	}

	// set DocCommentSupport, with appropriate side effects on defaults if
	// javadoc is not enabled
	if (this.enableJavadocOn) {
		this.options.put(
			CompilerOptions.OPTION_DocCommentSupport,
			CompilerOptions.ENABLED);
	} else if (this.warnJavadocOn || this.warnAllJavadocOn) {
		this.options.put(
			CompilerOptions.OPTION_DocCommentSupport,
			CompilerOptions.ENABLED);
		// override defaults: references that are embedded in javadoc are ignored
		// from the perspective of parameters and thrown exceptions usage
		this.options.put(
			CompilerOptions.OPTION_ReportUnusedParameterIncludeDocCommentReference,
			CompilerOptions.DISABLED);
		this.options.put(
			CompilerOptions.OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference,
			CompilerOptions.DISABLED);
	}
	// configure warnings for javadoc contents
	if (this.warnJavadocOn) {
		this.options.put(
			CompilerOptions.OPTION_ReportInvalidJavadocTags,
			CompilerOptions.ENABLED);
		this.options.put(
			CompilerOptions.OPTION_ReportInvalidJavadocTagsDeprecatedRef,
			CompilerOptions.ENABLED);
		this.options.put(
			CompilerOptions.OPTION_ReportInvalidJavadocTagsNotVisibleRef,
			CompilerOptions.ENABLED);
		this.options.put(
			CompilerOptions.OPTION_ReportMissingJavadocTagsVisibility,
			CompilerOptions.PRIVATE);
	}

	if (printUsageRequired || (filesCount == 0 && classCount == 0)) {
		if (usageSection ==  null) {
			printUsage(); // default
		} else {
			printUsage(usageSection);
		}
		this.proceed = false;
		return;
	}

	if (this.log != null) {
		this.logger.setLog(this.log);
	} else {
		this.showProgress = false;
	}
	this.logger.logVersion(printVersionRequired);

	validateOptions(didSpecifyCompliance);

	// Enable annotation processing by default in batch mode when compliance is at least 1.6
	// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=185768
	if (!didSpecifyDisabledAnnotationProcessing
			&& CompilerOptions.versionToJdkLevel(this.options.get(CompilerOptions.OPTION_Compliance)) >= ClassFileConstants.JDK1_6) {
		this.options.put(CompilerOptions.OPTION_Process_Annotations, CompilerOptions.ENABLED);
	}

	this.logger.logCommandLineArguments(newCommandLineArgs);
	this.logger.logOptions(this.options);

	if (this.maxRepetition == 0) {
		this.maxRepetition = 1;
	}
	if (this.maxRepetition >= 3 && (this.timing & TIMING_ENABLED) != 0) {
		this.compilerStats = new CompilerStats[this.maxRepetition];
	}

	if (filesCount != 0) {
		System.arraycopy(
			this.filenames,
			0,
			(this.filenames = new String[filesCount]),
			0,
			filesCount);
	}

	if (classCount != 0) {
		System.arraycopy(
			this.classNames,
			0,
			(this.classNames = new String[classCount]),
			0,
			classCount);
	}

	setPaths(bootclasspaths,
			sourcepathClasspathArg,
			sourcepathClasspaths,
			classpaths,
			extdirsClasspaths,
			endorsedDirClasspaths,
			customEncoding);

	if (specifiedEncodings != null && specifiedEncodings.size() > 1) {
		this.logger.logWarning(this.bind("configure.multipleencodings", //$NON-NLS-1$
				(String) this.options.get(CompilerOptions.OPTION_Encoding),
				getAllEncodings(specifiedEncodings)));
	}
	if (this.pendingErrors != null) {
		for (Iterator iterator = this.pendingErrors.iterator(); iterator.hasNext(); ) {
			String message = (String) iterator.next();
			this.logger.logPendingError(message);
		}
		this.pendingErrors = null;
	}
}
private static String getAllEncodings(Set encodings) {
	int size = encodings.size();
	String[] allEncodings = new String[size];
	encodings.toArray(allEncodings);
	Arrays.sort(allEncodings);
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < size; i++) {
		if (i > 0) {
			buffer.append(", "); //$NON-NLS-1$
		}
		buffer.append(allEncodings[i]);
	}
	return String.valueOf(buffer);
}

private void initializeWarnings(String propertiesFile) {
	File file = new File(propertiesFile);
	if (!file.exists()) {
		throw new IllegalArgumentException(this.bind("configure.missingwarningspropertiesfile", propertiesFile)); //$NON-NLS-1$
	}
	BufferedInputStream stream = null;
	Properties properties = null;
	try {
		stream = new BufferedInputStream(new FileInputStream(propertiesFile));
		properties = new Properties();
		properties.load(stream);
	} catch(IOException e) {
		e.printStackTrace();
		throw new IllegalArgumentException(this.bind("configure.ioexceptionwarningspropertiesfile", propertiesFile)); //$NON-NLS-1$
	} finally {
		if (stream != null) {
			try {
				stream.close();
			} catch(IOException e) {
				// ignore
			}
		}
	}
	for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext(); ) {
		Map.Entry entry = (Map.Entry) iterator.next();
		final String key = (String) entry.getKey();
		if (key.startsWith("org.eclipse.jdt.core.compiler.problem")) { //$NON-NLS-1$
			this.options.put(key, entry.getValue());
		}
	}
}
protected void disableWarnings() {
	Object[] entries = this.options.entrySet().toArray();
	for (int i = 0, max = entries.length; i < max; i++) {
		Map.Entry entry = (Map.Entry) entries[i];
		if (!(entry.getKey() instanceof String))
			continue;
		if (!(entry.getValue() instanceof String))
			continue;
		if (((String) entry.getValue()).equals(CompilerOptions.WARNING)) {
			this.options.put(entry.getKey(), CompilerOptions.IGNORE);
		}
	}
	this.options.put(CompilerOptions.OPTION_TaskTags, Util.EMPTY_STRING);
}
protected void disableErrors() {
	Object[] entries = this.options.entrySet().toArray();
	for (int i = 0, max = entries.length; i < max; i++) {
		Map.Entry entry = (Map.Entry) entries[i];
		if (!(entry.getKey() instanceof String))
			continue;
		if (!(entry.getValue() instanceof String))
			continue;
		if (((String) entry.getValue()).equals(CompilerOptions.ERROR)) {
			this.options.put(entry.getKey(), CompilerOptions.IGNORE);
		}
	}
}
public String extractDestinationPathFromSourceFile(CompilationResult result) {
	ICompilationUnit compilationUnit = result.compilationUnit;
	if (compilationUnit != null) {
		char[] fileName = compilationUnit.getFileName();
		int lastIndex = CharOperation.lastIndexOf(java.io.File.separatorChar, fileName);
		if (lastIndex != -1) {
			final String outputPathName = new String(fileName, 0, lastIndex);
			final File output = new File(outputPathName);
			if (output.exists() && output.isDirectory()) {
				return outputPathName;
			}
		}
	}
	return System.getProperty("user.dir"); //$NON-NLS-1$
}
/*
 * Answer the component to which will be handed back compilation results from the compiler
 */
public ICompilerRequestor getBatchRequestor() {
	return new ICompilerRequestor() {
		int lineDelta = 0;
		public void acceptResult(CompilationResult compilationResult) {
			if (compilationResult.lineSeparatorPositions != null) {
				int unitLineCount = compilationResult.lineSeparatorPositions.length;
				this.lineDelta += unitLineCount;
				if (Main.this.showProgress && this.lineDelta > 2000) {
					// in -log mode, dump a dot every 2000 lines compiled
					Main.this.logger.logProgress();
					this.lineDelta = 0;
				}
			}
			Main.this.logger.startLoggingSource(compilationResult);
			if (compilationResult.hasProblems() || compilationResult.hasTasks()) {
				Main.this.logger.logProblems(compilationResult.getAllProblems(), compilationResult.compilationUnit.getContents(), Main.this);
			}
			outputClassFiles(compilationResult);
			Main.this.logger.endLoggingSource();
		}
	};
}
/*
 *  Build the set of compilation source units
 */
public CompilationUnit[] getCompilationUnits() {
	int fileCount = this.filenames.length;
	CompilationUnit[] units = new CompilationUnit[fileCount];
	HashtableOfObject knownFileNames = new HashtableOfObject(fileCount);

	String defaultEncoding = (String) this.options.get(CompilerOptions.OPTION_Encoding);
	if (Util.EMPTY_STRING.equals(defaultEncoding))
		defaultEncoding = null;

	for (int i = 0; i < fileCount; i++) {
		char[] charName = this.filenames[i].toCharArray();
		if (knownFileNames.get(charName) != null)
			throw new IllegalArgumentException(this.bind("unit.more", this.filenames[i])); //$NON-NLS-1$
		knownFileNames.put(charName, charName);
		File file = new File(this.filenames[i]);
		if (!file.exists())
			throw new IllegalArgumentException(this.bind("unit.missing", this.filenames[i])); //$NON-NLS-1$
		String encoding = this.encodings[i];
		if (encoding == null)
			encoding = defaultEncoding;
		units[i] = new CompilationUnit(null, this.filenames[i], encoding,
				this.destinationPaths[i]);
	}
	return units;
}

/*
 *  Low-level API performing the actual compilation
 */
public IErrorHandlingPolicy getHandlingPolicy() {

	// passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive match)
	return new IErrorHandlingPolicy() {
		public boolean proceedOnErrors() {
			return Main.this.proceedOnError; // stop if there are some errors
		}
		public boolean stopOnFirstError() {
			return false;
		}
	};
}

/*
 * External API
 */
public File getJavaHome() {
	if (!this.javaHomeChecked) {
		this.javaHomeChecked = true;
		this.javaHomeCache = Util.getJavaHome();
	}
	return this.javaHomeCache;
}

public FileSystem getLibraryAccess() {
	return new FileSystem(this.checkedClasspaths, this.filenames);
}

/*
 *  Low-level API performing the actual compilation
 */
public IProblemFactory getProblemFactory() {
	return new DefaultProblemFactory(this.compilerLocale);
}

/*
 * External API
 */
protected ArrayList handleBootclasspath(ArrayList bootclasspaths, String customEncoding) {
 	final int bootclasspathsSize;
	if ((bootclasspaths != null)
		&& ((bootclasspathsSize = bootclasspaths.size()) != 0))
	{
		String[] paths = new String[bootclasspathsSize];
		bootclasspaths.toArray(paths);
		bootclasspaths.clear();
		for (int i = 0; i < bootclasspathsSize; i++) {
			processPathEntries(DEFAULT_SIZE_CLASSPATH, bootclasspaths,
				paths[i], customEncoding, false, true);
		}
	} else {
		bootclasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
		try {
			Util.collectRunningVMBootclasspath(bootclasspaths);
		} catch(IllegalStateException e) {
			this.logger.logWrongJDK();
			this.proceed = false;
			return null;
		}
	}
	return bootclasspaths;
}

/*
 * External API
 */
protected ArrayList handleClasspath(ArrayList classpaths, String customEncoding) {
	final int classpathsSize;
	if ((classpaths != null)
		&& ((classpathsSize = classpaths.size()) != 0))
	{
		String[] paths = new String[classpathsSize];
		classpaths.toArray(paths);
		classpaths.clear();
		for (int i = 0; i < classpathsSize; i++) {
			processPathEntries(DEFAULT_SIZE_CLASSPATH, classpaths, paths[i],
					customEncoding, false, true);
		}
	} else {
		// no user classpath specified.
		classpaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
		String classProp = System.getProperty("java.class.path"); //$NON-NLS-1$
		if ((classProp == null) || (classProp.length() == 0)) {
			addPendingErrors(this.bind("configure.noClasspath")); //$NON-NLS-1$
			final Classpath classpath = FileSystem.getClasspath(System.getProperty("user.dir"), customEncoding, null);//$NON-NLS-1$
			if (classpath != null) {
				classpaths.add(classpath);
			}
		} else {
			StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
			String token;
			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken();
				FileSystem.Classpath currentClasspath = FileSystem
						.getClasspath(token, customEncoding, null);
				if (currentClasspath != null) {
					classpaths.add(currentClasspath);
				} else if (token.length() != 0) {
					addPendingErrors(this.bind("configure.incorrectClasspath", token));//$NON-NLS-1$
				}
			}
		}
	}
	ArrayList result = new ArrayList();
	HashMap knownNames = new HashMap();
	FileSystem.ClasspathSectionProblemReporter problemReporter =
		new FileSystem.ClasspathSectionProblemReporter() {
			public void invalidClasspathSection(String jarFilePath) {
				addPendingErrors(bind("configure.invalidClasspathSection", jarFilePath)); //$NON-NLS-1$
			}
			public void multipleClasspathSections(String jarFilePath) {
				addPendingErrors(bind("configure.multipleClasspathSections", jarFilePath)); //$NON-NLS-1$
			}
		};
	while (! classpaths.isEmpty()) {
		Classpath current = (Classpath) classpaths.remove(0);
		String currentPath = current.getPath();
		if (knownNames.get(currentPath) == null) {
			knownNames.put(currentPath, current);
			result.add(current);
			List linkedJars = current.fetchLinkedJars(problemReporter);
			if (linkedJars != null) {
				classpaths.addAll(0, linkedJars);
			}
		}
	}
	return result;
}
/*
 * External API
 */
protected ArrayList handleEndorseddirs(ArrayList endorsedDirClasspaths) {
 	final File javaHome = getJavaHome();
	/*
	 * Feed endorsedDirClasspath according to:
	 * - -endorseddirs first if present;
	 * - else java.endorsed.dirs if defined;
	 * - else default extensions directory for the platform. (/lib/endorsed)
	 */
	if (endorsedDirClasspaths == null) {
		endorsedDirClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
		String endorsedDirsStr = System.getProperty("java.endorsed.dirs"); //$NON-NLS-1$
		if (endorsedDirsStr == null) {
			if (javaHome != null) {
				endorsedDirClasspaths.add(javaHome.getAbsolutePath() + "/lib/endorsed"); //$NON-NLS-1$
			}
		} else {
			StringTokenizer tokenizer = new StringTokenizer(endorsedDirsStr, File.pathSeparator);
			while (tokenizer.hasMoreTokens()) {
				endorsedDirClasspaths.add(tokenizer.nextToken());
			}
		}
	}

	/*
	 * Feed extdirsClasspath with the entries found into the directories listed by
	 * extdirsNames.
	 */
	if (endorsedDirClasspaths.size() != 0) {
		File[] directoriesToCheck = new File[endorsedDirClasspaths.size()];
		for (int i = 0; i < directoriesToCheck.length; i++)
			directoriesToCheck[i] = new File((String) endorsedDirClasspaths.get(i));
		endorsedDirClasspaths.clear();
		File[][] endorsedDirsJars = getLibrariesFiles(directoriesToCheck);
		if (endorsedDirsJars != null) {
			for (int i = 0, max = endorsedDirsJars.length; i < max; i++) {
				File[] current = endorsedDirsJars[i];
				if (current != null) {
					for (int j = 0, max2 = current.length; j < max2; j++) {
						FileSystem.Classpath classpath =
							FileSystem.getClasspath(
									current[j].getAbsolutePath(),
									null, null);
						if (classpath != null) {
							endorsedDirClasspaths.add(classpath);
						}
					}
				} else if (directoriesToCheck[i].isFile()) {
					addPendingErrors(
						this.bind(
							"configure.incorrectEndorsedDirsEntry", //$NON-NLS-1$
							directoriesToCheck[i].getAbsolutePath()));
				}
			}
		}
	}
	return endorsedDirClasspaths;
}

/*
 * External API
 * Handle extdirs processing
 */
protected ArrayList handleExtdirs(ArrayList extdirsClasspaths) {
 	final File javaHome = getJavaHome();

	/*
	 * Feed extDirClasspath according to:
	 * - -extdirs first if present;
	 * - else java.ext.dirs if defined;
	 * - else default extensions directory for the platform.
	 */
	if (extdirsClasspaths == null) {
		extdirsClasspaths = new ArrayList(DEFAULT_SIZE_CLASSPATH);
		String extdirsStr = System.getProperty("java.ext.dirs"); //$NON-NLS-1$
		if (extdirsStr == null) {
			extdirsClasspaths.add(javaHome.getAbsolutePath() + "/lib/ext"); //$NON-NLS-1$
		} else {
			StringTokenizer tokenizer = new StringTokenizer(extdirsStr, File.pathSeparator);
			while (tokenizer.hasMoreTokens())
				extdirsClasspaths.add(tokenizer.nextToken());
		}
	}

	/*
	 * Feed extdirsClasspath with the entries found into the directories listed by
	 * extdirsNames.
	 */
	if (extdirsClasspaths.size() != 0) {
		File[] directoriesToCheck = new File[extdirsClasspaths.size()];
		for (int i = 0; i < directoriesToCheck.length; i++)
			directoriesToCheck[i] = new File((String) extdirsClasspaths.get(i));
		extdirsClasspaths.clear();
		File[][] extdirsJars = getLibrariesFiles(directoriesToCheck);
		if (extdirsJars != null) {
			for (int i = 0, max = extdirsJars.length; i < max; i++) {
				File[] current = extdirsJars[i];
				if (current != null) {
					for (int j = 0, max2 = current.length; j < max2; j++) {
						FileSystem.Classpath classpath =
							FileSystem.getClasspath(
									current[j].getAbsolutePath(),
									null, null);
						if (classpath != null) {
							extdirsClasspaths.add(classpath);
						}
					}
				} else if (directoriesToCheck[i].isFile()) {
					addPendingErrors(this.bind(
							"configure.incorrectExtDirsEntry", //$NON-NLS-1$
							directoriesToCheck[i].getAbsolutePath()));
				}
			}
		}
	}

	return extdirsClasspaths;
}

/*
 * External API
 * Handle a single warning token.
*/
protected void handleWarningToken(String token, boolean isEnabling) {
	handleErrorOrWarningToken(token, isEnabling, ProblemSeverities.Warning);
}
protected void handleErrorToken(String token, boolean isEnabling) {
	handleErrorOrWarningToken(token, isEnabling, ProblemSeverities.Error);
}
private void setSeverity(String compilerOptions, int severity, boolean isEnabling) {
	if (isEnabling) {
		switch(severity) {
			case ProblemSeverities.Error :
				this.options.put(compilerOptions, CompilerOptions.ERROR);
				break;
			case ProblemSeverities.Warning :
				this.options.put(compilerOptions, CompilerOptions.WARNING);
				break;
			default:
				this.options.put(compilerOptions, CompilerOptions.IGNORE);
		}
	} else {
		switch(severity) {
			case ProblemSeverities.Error :
				String currentValue = (String) this.options.get(compilerOptions);
				if (CompilerOptions.ERROR.equals(currentValue)) {
					this.options.put(compilerOptions, CompilerOptions.IGNORE);
				}
				break;
			case ProblemSeverities.Warning :
				currentValue = (String) this.options.get(compilerOptions);
				if (CompilerOptions.WARNING.equals(currentValue)) {
					this.options.put(compilerOptions, CompilerOptions.IGNORE);
				}
				break;
			default:
				this.options.put(compilerOptions, CompilerOptions.IGNORE);
		}
	}
}
private void handleErrorOrWarningToken(String token, boolean isEnabling, int severity) {
	if (token.length() == 0) return;
	switch(token.charAt(0)) {
		case 'a' :
			if (token.equals("allDeprecation")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportDeprecation, severity, isEnabling);
				this.options.put(
					CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode,
					isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				this.options.put(
					CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod,
					isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				return;
			} else if (token.equals("allJavadoc")) { //$NON-NLS-1$
				this.warnAllJavadocOn = this.warnJavadocOn = isEnabling;
				setSeverity(CompilerOptions.OPTION_ReportInvalidJavadoc, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportMissingJavadocTags, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportMissingJavadocComments, severity, isEnabling);
				return;
			} else if (token.equals("assertIdentifier")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportAssertIdentifier, severity, isEnabling);
				return;
			} else if (token.equals("allDeadCode")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportDeadCode, severity, isEnabling);
				this.options.put(
					CompilerOptions.OPTION_ReportDeadCodeInTrivialIfStatement,
					isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				return;
			} else if (token.equals("allOver-ann")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMissingOverrideAnnotation, severity, isEnabling);
				this.options.put(
					CompilerOptions.OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation,
					isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				return;
			} else if (token.equals("all-static-method")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMethodCanBeStatic, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportMethodCanBePotentiallyStatic, severity, isEnabling);
				return;
			}
			break;
		case 'b' :
			if (token.equals("boxing")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportAutoboxing, severity, isEnabling);
				return;
			}
			break;
		case 'c' :
			if (token.equals("constructorName")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMethodWithConstructorName, severity, isEnabling);
				return;
			} else if (token.equals("conditionAssign")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportPossibleAccidentalBooleanAssignment, severity, isEnabling);
				return;
			} else if (token.equals("compareIdentical")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportComparingIdentical, severity, isEnabling);
				return;
			} else if (token.equals("charConcat") /*|| token.equals("noImplicitStringConversion")/*backward compatible*/) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNoImplicitStringConversion, severity, isEnabling);
				return;
			}
			break;
		case 'd' :
			if (token.equals("deprecation")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportDeprecation, severity, isEnabling);
				this.options.put(
					CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode,
					CompilerOptions.DISABLED);
				this.options.put(
					CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod,
					CompilerOptions.DISABLED);
				return;
			} else if (token.equals("dep-ann")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMissingDeprecatedAnnotation, severity, isEnabling);
				return;
			} else if (token.equals("discouraged")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportDiscouragedReference, severity, isEnabling);
				return;
			} else if (token.equals("deadCode")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportDeadCode, severity, isEnabling);
				this.options.put(
					CompilerOptions.OPTION_ReportDeadCodeInTrivialIfStatement,
					CompilerOptions.DISABLED);
				return;
			}
			break;
		case 'e' :
			if (token.equals("enumSwitch") //$NON-NLS-1$
					|| token.equals("incomplete-switch")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportIncompleteEnumSwitch, severity, isEnabling);
				return;
			} else if (token.equals("emptyBlock")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUndocumentedEmptyBlock, severity, isEnabling);
				return;
			} else if (token.equals("enumIdentifier")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportEnumIdentifier, severity, isEnabling);
				return;
			}
			break;
		case 'f' :
			if (token.equals("fieldHiding")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportFieldHiding, severity, isEnabling);
				return;
			} else if (token.equals("finalBound")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportFinalParameterBound, severity, isEnabling);
				return;
			} else if (token.equals("finally")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportFinallyBlockNotCompletingNormally, severity, isEnabling);
				return;
			} else if (token.equals("forbidden")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportForbiddenReference, severity, isEnabling);
				return;
			} else if (token.equals("fallthrough")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportFallthroughCase, severity, isEnabling);
				return;
			}
			break;
		case 'h' :
			if (token.equals("hiding")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportHiddenCatchBlock, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportLocalVariableHiding, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportFieldHiding, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportTypeParameterHiding, severity, isEnabling);
				return;
			} else if (token.equals("hashCode")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMissingHashCodeMethod, severity, isEnabling);
				return;
			}
			break;
		case 'i' :
			if (token.equals("indirectStatic")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportIndirectStaticAccess, severity, isEnabling);
				return;
			} else if (token.equals("intfNonInherited") || token.equals("interfaceNonInherited")/*backward compatible*/) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportIncompatibleNonInheritedInterfaceMethod, severity, isEnabling);
				return;
			} else if (token.equals("intfAnnotation")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportAnnotationSuperInterface, severity, isEnabling);
				return;
			} else if (token.equals("intfRedundant") /*|| token.equals("redundantSuperinterface")*/) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportRedundantSuperinterface, severity, isEnabling);
				return;
			} else if (token.equals("includeAssertNull")) { //$NON-NLS-1$
				this.options.put(
						CompilerOptions.OPTION_IncludeNullInfoFromAsserts,
						isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				return;
			} 
			break;
		case 'j' :
			if (token.equals("javadoc")) {//$NON-NLS-1$
				this.warnJavadocOn = isEnabling;
				setSeverity(CompilerOptions.OPTION_ReportInvalidJavadoc, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportMissingJavadocTags, severity, isEnabling);
				return;
			}
			break;
		case 'l' :
			if (token.equals("localHiding")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportLocalVariableHiding, severity, isEnabling);
				return;
			}
			break;
		case 'm' :
			if (token.equals("maskedCatchBlock") || token.equals("maskedCatchBlocks")/*backward compatible*/) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportHiddenCatchBlock, severity, isEnabling);
				return;
			}
			break;
		case 'n' :
			if (token.equals("nls")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNonExternalizedStringLiteral, severity, isEnabling);
				return;
			} else if (token.equals("noEffectAssign")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNoEffectAssignment, severity, isEnabling);
				return;
			} else if (/*token.equals("charConcat") ||*/ token.equals("noImplicitStringConversion")/*backward compatible*/) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNoImplicitStringConversion, severity, isEnabling);
				return;
			} else if (token.equals("null")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNullReference, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportPotentialNullReference, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportRedundantNullCheck, severity, isEnabling);
				return;
			} else if (token.equals("nullDereference")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNullReference, severity, isEnabling);
				if (!isEnabling) {
					setSeverity(CompilerOptions.OPTION_ReportPotentialNullReference, ProblemSeverities.Ignore, isEnabling);
					setSeverity(CompilerOptions.OPTION_ReportRedundantNullCheck, ProblemSeverities.Ignore, isEnabling);
				}
				return;
			}
			break;
		case 'o' :
			if (token.equals("over-sync") /*|| token.equals("syncOverride")*/) { //$NON-NLS-1$ 
				setSeverity(CompilerOptions.OPTION_ReportMissingSynchronizedOnInheritedMethod, severity, isEnabling);
				return;
			} else if (token.equals("over-ann")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMissingOverrideAnnotation, severity, isEnabling);
				this.options.put(
					CompilerOptions.OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation,
					CompilerOptions.DISABLED);
				return;
			}
			break;
		case 'p' :
			if (token.equals("pkgDefaultMethod") || token.equals("packageDefaultMethod")/*backward compatible*/ ) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportOverridingPackageDefaultMethod, severity, isEnabling);
				return;
			} else if (token.equals("paramAssign")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportParameterAssignment, severity, isEnabling);
				return;
			}
			break;
		case 'r' :
			if (token.equals("raw")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportRawTypeReference, severity, isEnabling);
				return;
			} else if (/*token.equals("intfRedundant") ||*/ token.equals("redundantSuperinterface")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportRedundantSuperinterface, severity, isEnabling);
				return;
			}
			break;
		case 's' :
			if (token.equals("specialParamHiding")) { //$NON-NLS-1$
				this.options.put(
					CompilerOptions.OPTION_ReportSpecialParameterHidingField,
					isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				return;
			} else if (token.equals("syntheticAccess") || token.equals("synthetic-access")) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportSyntheticAccessEmulation, severity, isEnabling);
				return;
			} else if (token.equals("staticReceiver")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNonStaticAccessToStatic, severity, isEnabling);
				return;
			} else 	if (/*token.equals("over-sync") ||*/ token.equals("syncOverride")) { //$NON-NLS-1$ 
				setSeverity(CompilerOptions.OPTION_ReportMissingSynchronizedOnInheritedMethod, severity, isEnabling);
				return;
			} else if (token.equals("semicolon")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportEmptyStatement, severity, isEnabling);
				return;
			} else if (token.equals("serial")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMissingSerialVersion, severity, isEnabling);
				return;
			} else if (token.equals("suppress")) {//$NON-NLS-1$
				switch(severity) {
					case ProblemSeverities.Warning :
						this.options.put(
								CompilerOptions.OPTION_SuppressWarnings,
								isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
						this.options.put(
								CompilerOptions.OPTION_SuppressOptionalErrors,
								CompilerOptions.DISABLED);
						break;
					case ProblemSeverities.Error :
						this.options.put(
								CompilerOptions.OPTION_SuppressWarnings,
								isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
						this.options.put(
							CompilerOptions.OPTION_SuppressOptionalErrors,
							isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				}
				return;
			} else if (token.equals("static-access")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportNonStaticAccessToStatic, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportIndirectStaticAccess, severity, isEnabling);
				return;
			} else if (token.equals("super")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportOverridingMethodWithoutSuperInvocation, severity, isEnabling);
				return;
			} else if (token.equals("static-method")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportMethodCanBeStatic, severity, isEnabling);
				return;
			}
			break;
		case 't' :
			if (token.startsWith("tasks")) { //$NON-NLS-1$
				String taskTags = Util.EMPTY_STRING;
				int start = token.indexOf('(');
				int end = token.indexOf(')');
				if (start >= 0 && end >= 0 && start < end){
					taskTags = token.substring(start+1, end).trim();
					taskTags = taskTags.replace('|',',');
				}
				if (taskTags.length() == 0){
					throw new IllegalArgumentException(this.bind("configure.invalidTaskTag", token)); //$NON-NLS-1$
				}
				this.options.put(
					CompilerOptions.OPTION_TaskTags,
					isEnabling ? taskTags : Util.EMPTY_STRING);
				
				setSeverity(CompilerOptions.OPTION_ReportTasks, severity, isEnabling);
				return;
			} else if (token.equals("typeHiding")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportTypeParameterHiding, severity, isEnabling);
				return;
			}
			break;
		case 'u' :
			if (token.equals("unusedLocal") || token.equals("unusedLocals")/*backward compatible*/) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportUnusedLocal, severity, isEnabling);
				return;
			} else if (token.equals("unusedArgument") || token.equals("unusedArguments")/*backward compatible*/) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportUnusedParameter, severity, isEnabling);
				return;
			} else if (token.equals("unusedImport") || token.equals("unusedImports")/*backward compatible*/) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportUnusedImport, severity, isEnabling);
				return;
			} else if (token.equals("unusedAllocation")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnusedObjectAllocation, severity, isEnabling);
				return;
			} else if (token.equals("unusedPrivate")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnusedPrivateMember, severity, isEnabling);
				return;
			} else if (token.equals("unusedLabel")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnusedLabel, severity, isEnabling);
				return;
			} else if (token.equals("uselessTypeCheck")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnnecessaryTypeCheck, severity, isEnabling);
				return;
			} else if (token.equals("unchecked") || token.equals("unsafe")) {//$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportUncheckedTypeOperation, severity, isEnabling);
				return;
			} else if (token.equals("unnecessaryElse")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnnecessaryElse, severity, isEnabling);
				return;
			} else if (token.equals("unusedThrown")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnusedDeclaredThrownException, severity, isEnabling);
				return;
			} else if (token.equals("unqualifiedField") || token.equals("unqualified-field-access")) { //$NON-NLS-1$ //$NON-NLS-2$
				setSeverity(CompilerOptions.OPTION_ReportUnqualifiedFieldAccess, severity, isEnabling);
				return;
			} else if (token.equals("unused")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnusedLocal, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedParameter, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedImport, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedPrivateMember, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedDeclaredThrownException, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedLabel, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedTypeArgumentsForMethodInvocation, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportRedundantSpecificationOfTypeArguments, severity, isEnabling);
				return;
			} else if (token.equals("unusedTypeArgs")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnusedTypeArgumentsForMethodInvocation, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportRedundantSpecificationOfTypeArguments, severity, isEnabling);
				return;
			} else if (token.equals("unavoidableGenericProblems")) { //$NON-NLS-1$
				this.options.put(
					CompilerOptions.OPTION_ReportUnavoidableGenericTypeProblems,
					isEnabling ? CompilerOptions.ENABLED : CompilerOptions.DISABLED);
				return;
			}
			break;
		case 'v' :
			if (token.equals("varargsCast")) { //$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportVarargsArgumentNeedCast, severity, isEnabling);
				return;
			}
			break;
		case 'w' :
			if (token.equals("warningToken")) {//$NON-NLS-1$
				setSeverity(CompilerOptions.OPTION_ReportUnhandledWarningToken, severity, isEnabling);
				setSeverity(CompilerOptions.OPTION_ReportUnusedWarningToken, severity, isEnabling);
				return;
			}
			break;
	}
	String message = null;
	switch(severity) {
		case ProblemSeverities.Warning :
			message = this.bind("configure.invalidWarning", token); //$NON-NLS-1$
			break;
		case ProblemSeverities.Error :
			message = this.bind("configure.invalidError", token); //$NON-NLS-1$
	}
	addPendingErrors(message);
}
/**
 * @deprecated - use {@link #initialize(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. initialize(outWriter, errWriter, systemExit, null, null)
 */
protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit) {
	this.initialize(outWriter, errWriter, systemExit, null /* options */, null /* progress */);
}
/**
 * @deprecated - use {@link #initialize(PrintWriter, PrintWriter, boolean, Map, CompilationProgress)} instead
 *                       e.g. initialize(outWriter, errWriter, systemExit, customDefaultOptions, null)
 */
protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map customDefaultOptions) {
	this.initialize(outWriter, errWriter, systemExit, customDefaultOptions, null /* progress */);
}
protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map customDefaultOptions, CompilationProgress compilationProgress) {
	this.logger = new Logger(this, outWriter, errWriter);
	this.proceed = true;
	this.out = outWriter;
	this.err = errWriter;
	this.systemExitWhenFinished = systemExit;
	this.options = new CompilerOptions().getMap();

	this.progress = compilationProgress;
	if (customDefaultOptions != null) {
		this.didSpecifySource = customDefaultOptions.get(CompilerOptions.OPTION_Source) != null;
		this.didSpecifyTarget = customDefaultOptions.get(CompilerOptions.OPTION_TargetPlatform) != null;
		for (Iterator iter = customDefaultOptions.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			this.options.put(entry.getKey(), entry.getValue());
		}
	} else {
		this.didSpecifySource = false;
		this.didSpecifyTarget = false;
	}
	this.classNames = null;
}
protected void initializeAnnotationProcessorManager() {
	try {
		Class c = Class.forName("org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager"); //$NON-NLS-1$
		AbstractAnnotationProcessorManager annotationManager = (AbstractAnnotationProcessorManager) c.newInstance();
		annotationManager.configure(this, this.expandedCommandLine);
		annotationManager.setErr(this.err);
		annotationManager.setOut(this.out);
		this.batchCompiler.annotationProcessorManager = annotationManager;
	} catch (ClassNotFoundException e) {
		// ignore
	} catch (InstantiationException e) {
		// should not happen
		throw new org.eclipse.jdt.internal.compiler.problem.AbortCompilation();
	} catch (IllegalAccessException e) {
		// should not happen
		throw new org.eclipse.jdt.internal.compiler.problem.AbortCompilation();
	} catch(UnsupportedClassVersionError e) {
		// report a warning
		this.logger.logIncorrectVMVersionForAnnotationProcessing();
	}
}

// Dump classfiles onto disk for all compilation units that where successful
// and do not carry a -d none spec, either directly or inherited from Main.
public void outputClassFiles(CompilationResult unitResult) {
	if (!((unitResult == null) || (unitResult.hasErrors() && !this.proceedOnError))) {
		ClassFile[] classFiles = unitResult.getClassFiles();
		String currentDestinationPath = null;
		boolean generateClasspathStructure = false;
		CompilationUnit compilationUnit =
			(CompilationUnit) unitResult.compilationUnit;
		if (compilationUnit.destinationPath == null) {
			if (this.destinationPath == null) {
				currentDestinationPath =
					extractDestinationPathFromSourceFile(unitResult);
			} else if (this.destinationPath != NONE) {
				currentDestinationPath = this.destinationPath;
				generateClasspathStructure = true;
			} // else leave currentDestinationPath null
		} else if (compilationUnit.destinationPath != NONE) {
			currentDestinationPath = compilationUnit.destinationPath;
			generateClasspathStructure = true;
		} // else leave currentDestinationPath null
		if (currentDestinationPath != null) {
			for (int i = 0, fileCount = classFiles.length; i < fileCount; i++) {
				// retrieve the key and the corresponding classfile
				ClassFile classFile = classFiles[i];
				char[] filename = classFile.fileName();
				int length = filename.length;
				char[] relativeName = new char[length + 6];
				System.arraycopy(filename, 0, relativeName, 0, length);
				System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
				CharOperation.replace(relativeName, '/', File.separatorChar);
				String relativeStringName = new String(relativeName);
				try {
					if (this.compilerOptions.verbose)
						this.out.println(
							Messages.bind(
								Messages.compilation_write,
								new String[] {
									String.valueOf(this.exportedClassFilesCounter+1),
									relativeStringName
								}));
					Util.writeToDisk(
						generateClasspathStructure,
						currentDestinationPath,
						relativeStringName,
						classFile);
					this.logger.logClassFile(
						generateClasspathStructure,
						currentDestinationPath,
						relativeStringName);
					this.exportedClassFilesCounter++;
				} catch (IOException e) {
					this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
				}
			}
			this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
		}
	}
}
/*
 *  Low-level API performing the actual compilation
 */
public void performCompilation() {

	this.startTime = System.currentTimeMillis();

	FileSystem environment = getLibraryAccess();
	this.compilerOptions = new CompilerOptions(this.options);
	this.compilerOptions.performMethodsFullRecovery = false;
	this.compilerOptions.performStatementsRecovery = false;
	this.batchCompiler =
		new Compiler(
			environment,
			getHandlingPolicy(),
			this.compilerOptions,
			getBatchRequestor(),
			getProblemFactory(),
			this.out,
			this.progress);
	this.batchCompiler.remainingIterations = this.maxRepetition-this.currentRepetition/*remaining iterations including this one*/;
	// temporary code to allow the compiler to revert to a single thread
	String setting = System.getProperty("jdt.compiler.useSingleThread"); //$NON-NLS-1$
	this.batchCompiler.useSingleThread = setting != null && setting.equals("true"); //$NON-NLS-1$

	if (this.compilerOptions.complianceLevel >= ClassFileConstants.JDK1_6
			&& this.compilerOptions.processAnnotations) {
		if (checkVMVersion(ClassFileConstants.JDK1_6)) {
			initializeAnnotationProcessorManager();
			if (this.classNames != null) {
				this.batchCompiler.setBinaryTypes(processClassNames(this.batchCompiler.lookupEnvironment));
			}
		} else {
			// report a warning
			this.logger.logIncorrectVMVersionForAnnotationProcessing();
		}
	}

	// set the non-externally configurable options.
	this.compilerOptions.verbose = this.verbose;
	this.compilerOptions.produceReferenceInfo = this.produceRefInfo;
	try {
		this.logger.startLoggingSources();
		this.batchCompiler.compile(getCompilationUnits());
	} finally {
		this.logger.endLoggingSources();
	}

	if (this.extraProblems != null) {
		loggingExtraProblems();
		this.extraProblems = null;
	}
	if (this.compilerStats != null) {
		this.compilerStats[this.currentRepetition] = this.batchCompiler.stats;
	}
	this.logger.printStats();

	// cleanup
	environment.cleanup();
}
protected void loggingExtraProblems() {
	this.logger.loggingExtraProblems(this);
}
public void printUsage() {
	printUsage("misc.usage"); //$NON-NLS-1$
}
private void printUsage(String sectionID) {
	this.logger.logUsage(
		this.bind(
			sectionID,
			new String[] {
				System.getProperty("path.separator"), //$NON-NLS-1$
				this.bind("compiler.name"), //$NON-NLS-1$
				this.bind("compiler.version"), //$NON-NLS-1$
				this.bind("compiler.copyright") //$NON-NLS-1$
			}));
	this.logger.flush();
}
private ReferenceBinding[] processClassNames(LookupEnvironment environment) {
	// check for .class file presence in case of apt processing
	int length = this.classNames.length;
	ReferenceBinding[] referenceBindings = new ReferenceBinding[length];
	for (int i = 0; i < length; i++) {
		String currentName = this.classNames[i];
		char[][] compoundName = null;
		if (currentName.indexOf('.') != -1) {
			// consider names with '.' as fully qualified names
			char[] typeName = currentName.toCharArray();
			compoundName = CharOperation.splitOn('.', typeName);
		} else {
			compoundName = new char[][] { currentName.toCharArray() };
		}
		ReferenceBinding type = environment.getType(compoundName);
		if (type != null && type.isValidBinding()) {
			if (type.isBinaryBinding()) {
				referenceBindings[i] = type;
			}
		} else {
			throw new IllegalArgumentException(
					this.bind("configure.invalidClassName", currentName));//$NON-NLS-1$
		}
	}
	return referenceBindings;
}
/*
 * External API
 */
public void processPathEntries(final int defaultSize, final ArrayList paths,
			final String currentPath, String customEncoding, boolean isSourceOnly,
			boolean rejectDestinationPathOnJars) {
	String currentClasspathName = null;
	String currentDestinationPath = null;
	ArrayList currentRuleSpecs = new ArrayList(defaultSize);
	StringTokenizer tokenizer = new StringTokenizer(currentPath,
			File.pathSeparator + "[]", true); //$NON-NLS-1$
	ArrayList tokens = new ArrayList();
	while (tokenizer.hasMoreTokens()) {
		tokens.add(tokenizer.nextToken());
	}
	// state machine
	final int start = 0;
	final int readyToClose = 1;
	// 'path' 'path1[rule];path2'
	final int readyToCloseEndingWithRules = 2;
	// 'path[rule]' 'path1;path2[rule]'
	final int readyToCloseOrOtherEntry = 3;
	// 'path[rule];' 'path;' 'path1;path2;'
	final int rulesNeedAnotherRule = 4;
	// 'path[rule1;'
	final int rulesStart = 5;
	// 'path[' 'path1;path2['
	final int rulesReadyToClose = 6;
	// 'path[rule' 'path[rule1;rule2'
	final int destinationPathReadyToClose = 7;
	// 'path[-d bin'
	final int readyToCloseEndingWithDestinationPath = 8;
	// 'path[-d bin]' 'path[rule][-d bin]'
	final int destinationPathStart = 9;
	// 'path[rule]['
	final int bracketOpened = 10;
	// '.*[.*'
	final int bracketClosed = 11;
	// '.*([.*])+'

	final int error = 99;
	int state = start;
	String token = null;
	int cursor = 0, tokensNb = tokens.size(), bracket = -1;
	while (cursor < tokensNb && state != error) {
		token = (String) tokens.get(cursor++);
		if (token.equals(File.pathSeparator)) {
			switch (state) {
			case start:
			case readyToCloseOrOtherEntry:
			case bracketOpened:
				break;
			case readyToClose:
			case readyToCloseEndingWithRules:
			case readyToCloseEndingWithDestinationPath:
				state = readyToCloseOrOtherEntry;
				addNewEntry(paths, currentClasspathName, currentRuleSpecs,
						customEncoding, currentDestinationPath, isSourceOnly,
						rejectDestinationPathOnJars);
				currentRuleSpecs.clear();
				break;
			case rulesReadyToClose:
				state = rulesNeedAnotherRule;
				break;
			case destinationPathReadyToClose:
				throw new IllegalArgumentException(
					this.bind("configure.incorrectDestinationPathEntry", //$NON-NLS-1$
						currentPath));
			case bracketClosed:
				cursor = bracket + 1;
				state = rulesStart;
				break;
			default:
				state = error;
			}
		} else if (token.equals("[")) { //$NON-NLS-1$
			switch (state) {
			case start:
				currentClasspathName = ""; //$NON-NLS-1$
				//$FALL-THROUGH$
				case readyToClose:
				bracket = cursor - 1;
				//$FALL-THROUGH$
				case bracketClosed:
				state = bracketOpened;
				break;
			case readyToCloseEndingWithRules:
				state = destinationPathStart;
				break;
			case readyToCloseEndingWithDestinationPath:
				state = rulesStart;
				break;
			case bracketOpened:
			default:
				state = error;
			}
		} else if (token.equals("]")) { //$NON-NLS-1$
			switch (state) {
			case rulesReadyToClose:
				state = readyToCloseEndingWithRules;
				break;
			case destinationPathReadyToClose:
				state = readyToCloseEndingWithDestinationPath;
				break;
			case bracketOpened:
				state = bracketClosed;
				break;
			case bracketClosed:
			default:
				state = error;
			}
		} else {
			// regular word
			switch (state) {
			case start:
			case readyToCloseOrOtherEntry:
				state = readyToClose;
				currentClasspathName = token;
				break;
			case rulesStart:
				if (token.startsWith("-d ")) { //$NON-NLS-1$
					if (currentDestinationPath != null) {
						throw new IllegalArgumentException(
								this.bind("configure.duplicateDestinationPathEntry", //$NON-NLS-1$
										currentPath));
					}
					currentDestinationPath = token.substring(3).trim();
					state = destinationPathReadyToClose;
					break;
				} // else we proceed with a rule
				//$FALL-THROUGH$
			case rulesNeedAnotherRule:
				if (currentDestinationPath != null) {
					throw new IllegalArgumentException(
							this.bind("configure.accessRuleAfterDestinationPath", //$NON-NLS-1$
								currentPath));
				}
				state = rulesReadyToClose;
				currentRuleSpecs.add(token);
				break;
			case destinationPathStart:
				if (!token.startsWith("-d ")) { //$NON-NLS-1$
					state = error;
				} else {
					currentDestinationPath = token.substring(3).trim();
					state = destinationPathReadyToClose;
				}
				break;
			case bracketClosed:
				for (int i = bracket; i < cursor ; i++) {
					currentClasspathName += (String) tokens.get(i);
				}
				state = readyToClose;
				break;
			case bracketOpened:
				break;
			default:
				state = error;
			}
		}
		if (state == bracketClosed && cursor == tokensNb) {
			cursor = bracket + 1;
			state = rulesStart;
		}
	}
	switch(state) {
		case readyToCloseOrOtherEntry:
			break;
		case readyToClose:
		case readyToCloseEndingWithRules:
		case readyToCloseEndingWithDestinationPath:
			addNewEntry(paths, currentClasspathName, currentRuleSpecs,
				customEncoding, currentDestinationPath, isSourceOnly,
				rejectDestinationPathOnJars);
			break;
		case bracketOpened:
		case bracketClosed:
		default :
			// we go on anyway
			if (currentPath.length() != 0) {
				addPendingErrors(this.bind("configure.incorrectClasspath", currentPath));//$NON-NLS-1$
			}
	}
}

private int processPaths(String[] args, int index, String currentArg, ArrayList paths) {
	int localIndex = index;
	int count = 0;
	for (int i = 0, max = currentArg.length(); i < max; i++) {
		switch(currentArg.charAt(i)) {
			case '[' :
				count++;
				break;
			case ']' :
				count--;
				break;
		}
	}
	if (count == 0) {
		paths.add(currentArg);
	} else if (count > 1) {
		throw new IllegalArgumentException(
				this.bind("configure.unexpectedBracket", //$NON-NLS-1$
							currentArg));
	} else {
		StringBuffer currentPath = new StringBuffer(currentArg);
		while (true) {
			if (localIndex >= args.length) {
				throw new IllegalArgumentException(
						this.bind("configure.unexpectedBracket", //$NON-NLS-1$
								currentArg));
			}
			localIndex++;
			String nextArg = args[localIndex];
			for (int i = 0, max = nextArg.length(); i < max; i++) {
				switch(nextArg.charAt(i)) {
					case '[' :
						if (count > 1) {
							throw new IllegalArgumentException(
									this.bind("configure.unexpectedBracket", //$NON-NLS-1$
											nextArg));
						}
						count++;
						break;
					case ']' :
						count--;
						break;
				}
			}
			if (count == 0) {
				currentPath.append(' ');
				currentPath.append(nextArg);
				paths.add(currentPath.toString());
				return localIndex - index;
			} else if (count < 0) {
				throw new IllegalArgumentException(
						this.bind("configure.unexpectedBracket", //$NON-NLS-1$
									nextArg));
			} else {
				currentPath.append(' ');
				currentPath.append(nextArg);
			}
		}

	}
	return localIndex - index;
}
private int processPaths(String[] args, int index, String currentArg, String[] paths) {
	int localIndex = index;
	int count = 0;
	for (int i = 0, max = currentArg.length(); i < max; i++) {
		switch(currentArg.charAt(i)) {
			case '[' :
				count++;
				break;
			case ']' :
				count--;
				break;
		}
	}
	if (count == 0) {
		paths[0] = currentArg;
	} else {
		StringBuffer currentPath = new StringBuffer(currentArg);
		while (true) {
			localIndex++;
			if (localIndex >= args.length) {
				throw new IllegalArgumentException(
						this.bind("configure.unexpectedBracket", //$NON-NLS-1$
								currentArg));
			}
			String nextArg = args[localIndex];
			for (int i = 0, max = nextArg.length(); i < max; i++) {
				switch(nextArg.charAt(i)) {
					case '[' :
						if (count > 1) {
							throw new IllegalArgumentException(
									this.bind("configure.unexpectedBracket", //$NON-NLS-1$
											currentArg));
						}
						count++;
						break;
					case ']' :
						count--;
						break;
				}
			}
			if (count == 0) {
				currentPath.append(' ');
				currentPath.append(nextArg);
				paths[0] = currentPath.toString();
				return localIndex - index;
			} else if (count < 0) {
				throw new IllegalArgumentException(
						this.bind("configure.unexpectedBracket", //$NON-NLS-1$
									currentArg));
			} else {
				currentPath.append(' ');
				currentPath.append(nextArg);
			}
		}

	}
	return localIndex - index;
}
/**
 * Creates a NLS catalog for the given locale.
 */
public void relocalize() {
	relocalize(Locale.getDefault());
}

private void relocalize(Locale locale) {
	this.compilerLocale = locale;
	try {
		this.bundle = ResourceBundleFactory.getBundle(locale);
	} catch(MissingResourceException e) {
		System.out.println("Missing resource : " + Main.bundleName.replace('.', '/') + ".properties for locale " + locale); //$NON-NLS-1$//$NON-NLS-2$
		throw e;
	}
}
/*
 * External API
 */
public void setDestinationPath(String dest) {
	this.destinationPath = dest;
}
/*
 * External API
 */
public void setLocale(Locale locale) {
	relocalize(locale);
}
/*
 * External API
 */
protected void setPaths(ArrayList bootclasspaths,
		String sourcepathClasspathArg,
		ArrayList sourcepathClasspaths,
		ArrayList classpaths,
		ArrayList extdirsClasspaths,
		ArrayList endorsedDirClasspaths,
		String customEncoding) {

	// process bootclasspath, classpath and sourcepaths
 	bootclasspaths = handleBootclasspath(bootclasspaths, customEncoding);

	classpaths = handleClasspath(classpaths, customEncoding);

	if (sourcepathClasspathArg != null) {
		processPathEntries(DEFAULT_SIZE_CLASSPATH, sourcepathClasspaths,
			sourcepathClasspathArg, customEncoding, true, false);
	}

	/*
	 * Feed endorsedDirClasspath according to:
	 * - -extdirs first if present;
	 * - else java.ext.dirs if defined;
	 * - else default extensions directory for the platform.
	 */
	extdirsClasspaths = handleExtdirs(extdirsClasspaths);

	endorsedDirClasspaths = handleEndorseddirs(endorsedDirClasspaths);

	/*
	 * Concatenate classpath entries
	 * We put the bootclasspath at the beginning of the classpath
	 * entries, followed by the extension libraries, followed by
	 * the sourcepath followed by the classpath.  All classpath
	 * entries are searched for both sources and binaries except
	 * the sourcepath entries which are searched for sources only.
	 */
	bootclasspaths.addAll(endorsedDirClasspaths);
	bootclasspaths.addAll(extdirsClasspaths);
	bootclasspaths.addAll(sourcepathClasspaths);
	bootclasspaths.addAll(classpaths);
	classpaths = bootclasspaths;
	classpaths = FileSystem.ClasspathNormalizer.normalize(classpaths);
	this.checkedClasspaths = new FileSystem.Classpath[classpaths.size()];
	classpaths.toArray(this.checkedClasspaths);
	this.logger.logClasspath(this.checkedClasspaths);
}
protected void validateOptions(boolean didSpecifyCompliance) {
	if (didSpecifyCompliance) {
		Object version = this.options.get(CompilerOptions.OPTION_Compliance);
		if (CompilerOptions.VERSION_1_3.equals(version)) {
			if (!this.didSpecifySource) this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_3);
			if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_1);
		} else if (CompilerOptions.VERSION_1_4.equals(version)) {
			if (this.didSpecifySource) {
				Object source = this.options.get(CompilerOptions.OPTION_Source);
				if (CompilerOptions.VERSION_1_3.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
				} else if (CompilerOptions.VERSION_1_4.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
				}
			} else {
				this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_3);
				if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_2);
			}
		} else if (CompilerOptions.VERSION_1_5.equals(version)) {
			if (this.didSpecifySource) {
				Object source = this.options.get(CompilerOptions.OPTION_Source);
				if (CompilerOptions.VERSION_1_3.equals(source)
						|| CompilerOptions.VERSION_1_4.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
				} else if (CompilerOptions.VERSION_1_5.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
				}
			} else {
				this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
				if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
			}
		} else if (CompilerOptions.VERSION_1_6.equals(version)) {
			if (this.didSpecifySource) {
				Object source = this.options.get(CompilerOptions.OPTION_Source);
				if (CompilerOptions.VERSION_1_3.equals(source)
						|| CompilerOptions.VERSION_1_4.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
				} else if (CompilerOptions.VERSION_1_5.equals(source)
						|| CompilerOptions.VERSION_1_6.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
				}
			} else {
				this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_6);
				if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
			}
		} else if (CompilerOptions.VERSION_1_7.equals(version)) {
			if (this.didSpecifySource) {
				Object source = this.options.get(CompilerOptions.OPTION_Source);
				if (CompilerOptions.VERSION_1_3.equals(source)
						|| CompilerOptions.VERSION_1_4.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
				} else if (CompilerOptions.VERSION_1_5.equals(source)
						|| CompilerOptions.VERSION_1_6.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
				} else if (CompilerOptions.VERSION_1_7.equals(source)) {
					if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
				}
			} else {
				this.options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_7);
				if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
			}
		}
	} else if (this.didSpecifySource) {
		Object version = this.options.get(CompilerOptions.OPTION_Source);
		// default is source 1.3 target 1.2 and compliance 1.4
		if (CompilerOptions.VERSION_1_4.equals(version)) {
			if (!didSpecifyCompliance) this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_4);
			if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_4);
		} else if (CompilerOptions.VERSION_1_5.equals(version)) {
			if (!didSpecifyCompliance) this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_5);
			if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);
		} else if (CompilerOptions.VERSION_1_6.equals(version)) {
			if (!didSpecifyCompliance) this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_6);
			if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_6);
		} else if (CompilerOptions.VERSION_1_7.equals(version)) {
			if (!didSpecifyCompliance) this.options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_7);
			if (!this.didSpecifyTarget) this.options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_7);
		}
	}

	final Object sourceVersion = this.options.get(CompilerOptions.OPTION_Source);
	final Object compliance = this.options.get(CompilerOptions.OPTION_Compliance);
	if (sourceVersion.equals(CompilerOptions.VERSION_1_7)
			&& CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_7) {
		// compliance must be 1.7 if source is 1.7
		throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", (String)this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_7)); //$NON-NLS-1$
	} else if (sourceVersion.equals(CompilerOptions.VERSION_1_6)
			&& CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_6) {
		// compliance must be 1.6 if source is 1.6
		throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", (String)this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_6)); //$NON-NLS-1$
	} else if (sourceVersion.equals(CompilerOptions.VERSION_1_5)
			&& CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_5) {
		// compliance must be 1.5 if source is 1.5
		throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", (String)this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_5)); //$NON-NLS-1$
	} else if (sourceVersion.equals(CompilerOptions.VERSION_1_4)
			&& CompilerOptions.versionToJdkLevel(compliance) < ClassFileConstants.JDK1_4) {
		// compliance must be 1.4 if source is 1.4
		throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForSource", (String)this.options.get(CompilerOptions.OPTION_Compliance), CompilerOptions.VERSION_1_4)); //$NON-NLS-1$
	}

	// check and set compliance/source/target compatibilities
	if (this.didSpecifyTarget) {
		final Object targetVersion = this.options.get(CompilerOptions.OPTION_TargetPlatform);
		// tolerate jsr14 target
		if (CompilerOptions.VERSION_JSR14.equals(targetVersion)) {
			// expecting source >= 1.5
			if (CompilerOptions.versionToJdkLevel(sourceVersion) < ClassFileConstants.JDK1_5) {
				throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForGenericSource", (String) targetVersion, (String) sourceVersion)); //$NON-NLS-1$
			}
		} else if (CompilerOptions.VERSION_CLDC1_1.equals(targetVersion)) {
			if (this.didSpecifySource && CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_4) {
				throw new IllegalArgumentException(this.bind("configure.incompatibleSourceForCldcTarget", (String) targetVersion, (String) sourceVersion)); //$NON-NLS-1$
			}
			if (CompilerOptions.versionToJdkLevel(compliance) >= ClassFileConstants.JDK1_5) {
				throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForCldcTarget", (String) targetVersion, (String) sourceVersion)); //$NON-NLS-1$
			}
		} else {
			// target must be 1.7 if source is 1.7
			if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_7
					&& CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_7){
				throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", (String) targetVersion, CompilerOptions.VERSION_1_7)); //$NON-NLS-1$
			}
			// target must be 1.6 if source is 1.6
			if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_6
					&& CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_6){
				throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", (String) targetVersion, CompilerOptions.VERSION_1_6)); //$NON-NLS-1$
			}
			// target must be 1.5 if source is 1.5
			if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_5
					&& CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_5){
				throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", (String) targetVersion, CompilerOptions.VERSION_1_5)); //$NON-NLS-1$
			}
			// target must be 1.4 if source is 1.4
			if (CompilerOptions.versionToJdkLevel(sourceVersion) >= ClassFileConstants.JDK1_4
					&& CompilerOptions.versionToJdkLevel(targetVersion) < ClassFileConstants.JDK1_4){
				throw new IllegalArgumentException(this.bind("configure.incompatibleTargetForSource", (String) targetVersion, CompilerOptions.VERSION_1_4)); //$NON-NLS-1$
			}
			// target cannot be greater than compliance level
			if (CompilerOptions.versionToJdkLevel(compliance) < CompilerOptions.versionToJdkLevel(targetVersion)){
				throw new IllegalArgumentException(this.bind("configure.incompatibleComplianceForTarget", (String)this.options.get(CompilerOptions.OPTION_Compliance), (String) targetVersion)); //$NON-NLS-1$
			}
		}
	}
}
}
