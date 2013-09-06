/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

/**
 * Ant 1.5 compiler adapter for the Eclipse Java compiler. This adapter permits the
 * Eclipse Java compiler to be used with the <code>javac</code> task in Ant scripts. In order
 * to use it, just set the property <code>build.compiler</code> as follows:
 * <p>
 * <code>&lt;property name="build.compiler" value="org.eclipse.jdt.core.JDTCompilerAdapter"/&gt;</code>
 * </p>
 * <p>
 * For more information on Ant check out the website at http://jakarta.apache.org/ant/ .
 * </p>
 *
 * @since 2.0
 */
public class JDTCompilerAdapter extends DefaultCompilerAdapter {
	private static final char[] SEPARATOR_CHARS = new char[] { '/', '\\' };
	private static final char[] ADAPTER_PREFIX = "#ADAPTER#".toCharArray(); //$NON-NLS-1$
	private static final char[] ADAPTER_ENCODING = "ENCODING#".toCharArray(); //$NON-NLS-1$
	private static final char[] ADAPTER_ACCESS = "ACCESS#".toCharArray(); //$NON-NLS-1$
	private static String compilerClass = "org.eclipse.jdt.internal.compiler.batch.Main"; //$NON-NLS-1$
	String logFileName;
	Map customDefaultOptions;
	private Map fileEncodings = null;
	private Map dirEncodings = null;
	private List accessRules = null;

	/**
	 * Performs a compile using the JDT batch compiler
	 * @throws BuildException if anything wrong happen during the compilation
	 * @return boolean true if the compilation is ok, false otherwise
	 */
	public boolean execute() throws BuildException {
		this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.usingJDTCompiler"), Project.MSG_VERBOSE); //$NON-NLS-1$
		Commandline cmd = setupJavacCommand();

		try {
			Class c = Class.forName(compilerClass);
			Constructor batchCompilerConstructor = c.getConstructor(new Class[] { PrintWriter.class, PrintWriter.class, Boolean.TYPE, Map.class});
			Object batchCompilerInstance = batchCompilerConstructor.newInstance(new Object[] {new PrintWriter(System.out), new PrintWriter(System.err), Boolean.TRUE, this.customDefaultOptions});
			Method compile = c.getMethod("compile", new Class[] {String[].class}); //$NON-NLS-1$
			Object result = compile.invoke(batchCompilerInstance, new Object[] { cmd.getArguments()});
			final boolean resultValue = ((Boolean) result).booleanValue();
			if (!resultValue && this.logFileName != null) {
				this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.error.compilationFailed", this.logFileName)); //$NON-NLS-1$
			}
			return resultValue;
		} catch (ClassNotFoundException cnfe) {
			throw new BuildException(AntAdapterMessages.getString("ant.jdtadapter.error.cannotFindJDTCompiler")); //$NON-NLS-1$
		} catch (Exception ex) {
			throw new BuildException(ex);
		}
	}


	protected Commandline setupJavacCommand() throws BuildException {
		Commandline cmd = new Commandline();
		this.customDefaultOptions = new CompilerOptions().getMap();

		Class javacClass = Javac.class;

		/*
		 * Read in the compiler arguments first since we might need to modify
		 * the classpath if any access rules were specified
		 */
		String [] compilerArgs = processCompilerArguments(javacClass);

		/*
		 * This option is used to never exit at the end of the ant task.
		 */
		cmd.createArgument().setValue("-noExit"); //$NON-NLS-1$

		if (this.bootclasspath != null) {
			cmd.createArgument().setValue("-bootclasspath"); //$NON-NLS-1$
			if (this.bootclasspath.size() != 0) {
				/*
				 * Set the bootclasspath for the Eclipse compiler.
				 */
				cmd.createArgument().setPath(this.bootclasspath);
			} else {
				cmd.createArgument().setValue(Util.EMPTY_STRING);
			}
		}

		/*
		 * Eclipse compiler doesn't support -extdirs.
		 * It is emulated using the classpath. We add extdirs entries after the
		 * bootclasspath.
		 */
		if (this.extdirs != null) {
			cmd.createArgument().setValue("-extdirs"); //$NON-NLS-1$
			cmd.createArgument().setPath(this.extdirs);
		}

		Path classpath = new Path(this.project);
		/*
		 * The java runtime is already handled, so we simply want to retrieve the
		 * ant runtime and the compile classpath.
		 */
		classpath.append(getCompileClasspath());
		/*
		 * Set the classpath for the Eclipse compiler.
		 */
		cmd.createArgument().setValue("-classpath"); //$NON-NLS-1$
		createClasspathArgument(cmd, classpath);

		// For -sourcepath, use the "sourcepath" value if present.
		// Otherwise default to the "srcdir" value.
		Path sourcepath = null;

		// retrieve the method getSourcepath() using reflect
		// This is done to improve the compatibility to ant 1.5
		Method getSourcepathMethod = null;
		try {
			getSourcepathMethod = javacClass.getMethod("getSourcepath", null); //$NON-NLS-1$
		} catch(NoSuchMethodException e) {
			// if not found, then we cannot use this method (ant 1.5)
		}
		Path compileSourcePath = null;
		if (getSourcepathMethod != null) {
			try {
				compileSourcePath = (Path) getSourcepathMethod.invoke(this.attributes, null);
			} catch (IllegalAccessException e) {
				// should never happen
			} catch (InvocationTargetException e) {
				// should never happen
			}
		}
		if (compileSourcePath != null) {
			sourcepath = compileSourcePath;
		} else {
			sourcepath = this.src;
		}
		cmd.createArgument().setValue("-sourcepath"); //$NON-NLS-1$
		createClasspathArgument(cmd, sourcepath);

		final String javaVersion = JavaEnvUtils.getJavaVersion();
		String memoryParameterPrefix = javaVersion.equals(JavaEnvUtils.JAVA_1_1) ? "-J-" : "-J-X";//$NON-NLS-1$//$NON-NLS-2$
		if (this.memoryInitialSize != null) {
			if (!this.attributes.isForkedJavac()) {
				this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryInitialSize"), Project.MSG_WARN); //$NON-NLS-1$
			} else {
				cmd.createArgument().setValue(memoryParameterPrefix
						+ "ms" + this.memoryInitialSize); //$NON-NLS-1$
			}
		}

		if (this.memoryMaximumSize != null) {
			if (!this.attributes.isForkedJavac()) {
				this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryMaximumSize"), Project.MSG_WARN); //$NON-NLS-1$
			} else {
				cmd.createArgument().setValue(memoryParameterPrefix
						+ "mx" + this.memoryMaximumSize); //$NON-NLS-1$
			}
		}

		if (this.debug) {
			// retrieve the method getSourcepath() using reflect
			// This is done to improve the compatibility to ant 1.5
			Method getDebugLevelMethod = null;
			try {
				getDebugLevelMethod = javacClass.getMethod("getDebugLevel", null); //$NON-NLS-1$
			} catch(NoSuchMethodException e) {
				// if not found, then we cannot use this method (ant 1.5)
				// debug level is only available with ant 1.5.x
			}
			String debugLevel = null;
			if (getDebugLevelMethod != null) {
				try {
					debugLevel = (String) getDebugLevelMethod.invoke(this.attributes, null);
				} catch (IllegalAccessException e) {
					// should never happen
				} catch (InvocationTargetException e) {
					// should never happen
				}
			}
			if (debugLevel != null) {
				this.customDefaultOptions.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.DO_NOT_GENERATE);
				this.customDefaultOptions.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.DO_NOT_GENERATE);
				this.customDefaultOptions.put(CompilerOptions.OPTION_SourceFileAttribute , CompilerOptions.DO_NOT_GENERATE);
				if (debugLevel.length() != 0) {
					if (debugLevel.indexOf("vars") != -1) {//$NON-NLS-1$
						this.customDefaultOptions.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
					}
					if (debugLevel.indexOf("lines") != -1) {//$NON-NLS-1$
						this.customDefaultOptions.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
					}
					if (debugLevel.indexOf("source") != -1) {//$NON-NLS-1$
						this.customDefaultOptions.put(CompilerOptions.OPTION_SourceFileAttribute , CompilerOptions.GENERATE);
					}
				}
			} else {
				this.customDefaultOptions.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
				this.customDefaultOptions.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
				this.customDefaultOptions.put(CompilerOptions.OPTION_SourceFileAttribute , CompilerOptions.GENERATE);
			}
		} else {
			this.customDefaultOptions.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.DO_NOT_GENERATE);
			this.customDefaultOptions.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.DO_NOT_GENERATE);
			this.customDefaultOptions.put(CompilerOptions.OPTION_SourceFileAttribute , CompilerOptions.DO_NOT_GENERATE);
		}

		/*
		 * Handle the nowarn option. If none, then we generate all warnings.
		 */
		if (this.attributes.getNowarn()) {
			// disable all warnings
			Object[] entries = this.customDefaultOptions.entrySet().toArray();
			for (int i = 0, max = entries.length; i < max; i++) {
				Map.Entry entry = (Map.Entry) entries[i];
				if (!(entry.getKey() instanceof String))
					continue;
				if (!(entry.getValue() instanceof String))
					continue;
				if (((String) entry.getValue()).equals(CompilerOptions.WARNING)) {
					this.customDefaultOptions.put(entry.getKey(), CompilerOptions.IGNORE);
				}
			}
			this.customDefaultOptions.put(CompilerOptions.OPTION_TaskTags, Util.EMPTY_STRING);
			if (this.deprecation) {
				this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.WARNING);
				this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode, CompilerOptions.ENABLED);
				this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, CompilerOptions.ENABLED);
			}
		} else if (this.deprecation) {
			this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.WARNING);
			this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode, CompilerOptions.ENABLED);
			this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, CompilerOptions.ENABLED);
		} else {
			this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
			this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecationInDeprecatedCode, CompilerOptions.DISABLED);
			this.customDefaultOptions.put(CompilerOptions.OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, CompilerOptions.DISABLED);
		}

		/*
		 * destDir option.
		 */
		if (this.destDir != null) {
			cmd.createArgument().setValue("-d"); //$NON-NLS-1$
			cmd.createArgument().setFile(this.destDir.getAbsoluteFile());
		}

		/*
		 * verbose option
		 */
		if (this.verbose) {
			cmd.createArgument().setValue("-verbose"); //$NON-NLS-1$
		}

		/*
		 * failnoerror option
		 */
		if (!this.attributes.getFailonerror()) {
			cmd.createArgument().setValue("-proceedOnError"); //$NON-NLS-1$
		}

		/*
		 * target option.
		 */
		if (this.target != null) {
			this.customDefaultOptions.put(CompilerOptions.OPTION_TargetPlatform, this.target);
		}

		/*
		 * source option
		 */
		String source = this.attributes.getSource();
		if (source != null) {
			this.customDefaultOptions.put(CompilerOptions.OPTION_Source, source);
		}

		if (compilerArgs != null) {
			/*
			 * Add extra argument on the command line
			 */
			final int length = compilerArgs.length;
			if (length != 0) {
				for (int i = 0, max = length; i < max; i++) {
					String arg = compilerArgs[i];
					if (this.logFileName == null && "-log".equals(arg) && ((i + 1) < max)) { //$NON-NLS-1$
						this.logFileName = compilerArgs[i + 1];
					}
					cmd.createArgument().setValue(arg);
				}
			}
		}
		/*
		 * encoding option. javac task encoding property must be the last encoding on the command
		 * line as compiler arg might also specify an encoding.
		 */
		if (this.encoding != null) {
			cmd.createArgument().setValue("-encoding"); //$NON-NLS-1$
			cmd.createArgument().setValue(this.encoding);
		}

		/*
		 * Eclipse compiler doesn't have a -sourcepath option. This is
		 * handled through the javac task that collects all source files in
		 * srcdir option.
		 */
		logAndAddFilesToCompile(cmd);
		return cmd;
	}

	/**
	 * Get the compiler arguments
	 * @param javacClass
	 * @return String[] the array of arguments
	 */
	private String[] processCompilerArguments(Class javacClass) {
		// retrieve the method getCurrentCompilerArgs() using reflect
		// This is done to improve the compatibility to ant 1.5
		Method getCurrentCompilerArgsMethod = null;
		try {
			getCurrentCompilerArgsMethod = javacClass.getMethod("getCurrentCompilerArgs", null); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			// if not found, then we cannot use this method (ant 1.5)
			// debug level is only available with ant 1.5.x
		}
		String[] compilerArgs = null;
		if (getCurrentCompilerArgsMethod != null) {
			try {
				compilerArgs = (String[]) getCurrentCompilerArgsMethod.invoke(this.attributes, null);
			} catch (IllegalAccessException e) {
				// should never happen
			} catch (InvocationTargetException e) {
				// should never happen
			}
		}
		//check the compiler arguments for anything requiring extra processing
		if (compilerArgs != null) checkCompilerArgs(compilerArgs);
		return compilerArgs;
	}
	/**
	 * check the compiler arguments.
	 * Extract from files specified using @, lines marked with ADAPTER_PREFIX
	 * These lines specify information that needs to be interpreted by us.
	 * @param args compiler arguments to process
	 */
	private void checkCompilerArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '@') {
				try {
					char[] content = Util.getFileCharContent(new File(args[i].substring(1)), null);
					int offset = 0;
					int prefixLength = ADAPTER_PREFIX.length;
					while ((offset = CharOperation.indexOf(ADAPTER_PREFIX, content, true, offset)) > -1) {
						int start = offset + prefixLength;
						int end = CharOperation.indexOf('\n', content, start);
						if (end == -1)
							end = content.length;
						while (CharOperation.isWhitespace(content[end])) {
							end--;
						}

						// end is inclusive, but in the API end is exclusive
						if (CharOperation.equals(ADAPTER_ENCODING, content, start, start + ADAPTER_ENCODING.length)) {
							CharOperation.replace(content, SEPARATOR_CHARS, File.separatorChar, start, end + 1);
							// file or folder level custom encoding
							start += ADAPTER_ENCODING.length;
							int encodeStart = CharOperation.lastIndexOf('[', content, start, end);
							if (start < encodeStart && encodeStart < end) {
								boolean isFile = CharOperation.equals(SuffixConstants.SUFFIX_java, content, encodeStart - 5, encodeStart, false);

								String str = String.valueOf(content, start, encodeStart - start);
								String enc = String.valueOf(content, encodeStart, end - encodeStart + 1);
								if (isFile) {
									if (this.fileEncodings == null)
										this.fileEncodings = new HashMap();
									//use File to translate the string into a path with the correct File.seperator
									this.fileEncodings.put(str, enc);
								} else {
									if (this.dirEncodings == null)
										this.dirEncodings = new HashMap();
									this.dirEncodings.put(str, enc);
								}
							}
						} else if (CharOperation.equals(ADAPTER_ACCESS, content, start, start + ADAPTER_ACCESS.length)) {
							// access rules for the classpath
							start += ADAPTER_ACCESS.length;
							int accessStart = CharOperation.indexOf('[', content, start, end);
							CharOperation.replace(content, SEPARATOR_CHARS, File.separatorChar, start, accessStart);
							if (start < accessStart && accessStart < end) {
								String path = String.valueOf(content, start, accessStart - start);
								String access = String.valueOf(content, accessStart, end - accessStart + 1);
								if (this.accessRules == null)
									this.accessRules = new ArrayList();
								this.accessRules.add(path);
								this.accessRules.add(access);
							}
						}
						offset = end;
					}
				} catch (IOException e) {
					//ignore
				}
			}
		}

	}

	/**
	 * Copy the classpath to the command line with access rules included.
	 * @param cmd the given command line
	 * @param classpath the given classpath entry
	 */
	private void createClasspathArgument(Commandline cmd, Path classpath) {
		Argument arg = cmd.createArgument();
		final String[] pathElements = classpath.list();

		// empty path return empty string
		if (pathElements.length == 0) {
			arg.setValue(Util.EMPTY_STRING);
			return;
		}

		// no access rules, can set the path directly
		if (this.accessRules == null) {
			arg.setPath(classpath);
			return;
		}

		int rulesLength = this.accessRules.size();
		String[] rules = (String[]) this.accessRules.toArray(new String[rulesLength]);
		int nextRule = 0;
		final StringBuffer result = new StringBuffer();

		//access rules are expected in the same order as the classpath, but there could
		//be elements in the classpath not in the access rules or access rules not in the classpath
		for (int i = 0, max = pathElements.length; i < max; i++) {
			if (i > 0)
				result.append(File.pathSeparatorChar);
			String pathElement = pathElements[i];
			result.append(pathElement);
			//the rules list is [path, rule, path, rule, ...]
			for (int j = nextRule; j < rulesLength; j += 2) {
				String rule = rules[j];
				if (pathElement.endsWith(rule)) {
					result.append(rules[j + 1]);
					nextRule = j + 2;
					break;
				}
				// if the path doesn't match, it could be due to a trailing file separatorChar in the rule
				if (rule.endsWith(File.separator)) {
					// rule ends with the File.separator, but pathElement might not
					// otherwise it would match on the first endsWith
					int ruleLength = rule.length();
					if (pathElement.regionMatches(false, pathElement.length() - ruleLength + 1, rule, 0, ruleLength - 1)) {
						result.append(rules[j + 1]);
						nextRule = j + 2;
						break;
					}
				} else if (pathElement.endsWith(File.separator)) {
					// rule doesn't end with the File.separator, but pathElement might
					int ruleLength = rule.length();
					if (pathElement.regionMatches(false, pathElement.length() - ruleLength - 1, rule, 0, ruleLength)) {
						result.append(rules[j + 1]);
						nextRule = j + 2;
						break;
					}
				}
			}
		}

		arg.setValue(result.toString());
	}
	/**
	 * Modified from base class, Logs the compilation parameters, adds the files
	 * to compile and logs the &quot;niceSourceList&quot;
	 * Appends encoding information at the end of arguments
	 *
	 * @param cmd the given command line
	 */
	protected void logAndAddFilesToCompile(Commandline cmd) {
		this.attributes.log("Compilation " + cmd.describeArguments(), //$NON-NLS-1$
				Project.MSG_VERBOSE);

		StringBuffer niceSourceList = new StringBuffer("File"); //$NON-NLS-1$
		if (this.compileList.length != 1) {
			niceSourceList.append("s"); //$NON-NLS-1$
		}
		niceSourceList.append(" to be compiled:"); //$NON-NLS-1$
		niceSourceList.append(lSep);

		String[] encodedFiles = null, encodedDirs = null;
		int encodedFilesLength = 0, encodedDirsLength = 0;
		if (this.fileEncodings != null) {
			encodedFilesLength = this.fileEncodings.size();
			encodedFiles = new String[encodedFilesLength];
			this.fileEncodings.keySet().toArray(encodedFiles);
		}
		if (this.dirEncodings != null) {
			encodedDirsLength = this.dirEncodings.size();
			encodedDirs = new String[encodedDirsLength];
			this.dirEncodings.keySet().toArray(encodedDirs);
			//we need the directories sorted, longest first,since sub directories can
			//override encodings for their parent directories
			Comparator comparator = new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((String) o2).length() - ((String) o1).length();
				}
			};
			Arrays.sort(encodedDirs, comparator);
		}

		for (int i = 0; i < this.compileList.length; i++) {
			String arg = this.compileList[i].getAbsolutePath();
			boolean encoded = false;
			if (encodedFiles != null) {
				//check for file level custom encoding
				for (int j = 0; j < encodedFilesLength; j++) {
					if (arg.endsWith(encodedFiles[j])) {
						//found encoding, remove it from the list to speed things up next time around
						arg = arg + (String) this.fileEncodings.get(encodedFiles[j]);
						if (j < encodedFilesLength - 1) {
							System.arraycopy(encodedFiles, j + 1, encodedFiles, j, encodedFilesLength - j - 1);
						}
						encodedFiles[--encodedFilesLength] = null;
						encoded = true;
						break;
					}
				}
			}
			if (!encoded && encodedDirs != null) {
				//check folder level custom encoding
				for (int j = 0; j < encodedDirsLength; j++) {
					if (arg.lastIndexOf(encodedDirs[j]) != -1) {
						arg = arg + (String) this.dirEncodings.get(encodedDirs[j]);
						break;
					}
				}
			}
			cmd.createArgument().setValue(arg);
			niceSourceList.append("    " + arg + lSep); //$NON-NLS-1$
		}

		this.attributes.log(niceSourceList.toString(), Project.MSG_VERBOSE);
	}
}
