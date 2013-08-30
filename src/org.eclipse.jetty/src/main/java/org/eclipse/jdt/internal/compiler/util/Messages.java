/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public final class Messages {
	private static class MessagesProperties extends Properties {

		private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		private static final long serialVersionUID = 1L;

		private final Map fields;

		public MessagesProperties(Field[] fieldArray, String bundleName) {
			super();
			final int len = fieldArray.length;
			this.fields = new HashMap(len * 2);
			for (int i = 0; i < len; i++) {
				this.fields.put(fieldArray[i].getName(), fieldArray[i]);
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
		 */
		public synchronized Object put(Object key, Object value) {
			try {
				Field field = (Field) this.fields.get(key);
				if (field == null) {
					return null;
				}
				//can only set value of public static non-final fields
				if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED)
					return null;
				// Set the value into the field. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// log it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				try {
					field.set(null, value);
				} catch (Exception e) {
					// ignore
				}
			} catch (SecurityException e) {
				// ignore
			}
			return null;
		}
	}


	private static String[] nlSuffixes;
	private static final String EXTENSION = ".properties"; //$NON-NLS-1$

	private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.compiler.messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String compilation_unresolvedProblem;
	public static String compilation_unresolvedProblems;
	public static String compilation_request;
	public static String compilation_loadBinary;
	public static String compilation_process;
	public static String compilation_write;
	public static String compilation_done;
	public static String compilation_units;
	public static String compilation_unit;
	public static String compilation_internalError;
	public static String compilation_beginningToCompile;
	public static String compilation_processing;
	public static String output_isFile;
	public static String output_notValidAll;
	public static String output_notValid;
	public static String problem_noSourceInformation;
	public static String problem_atLine;
	public static String abort_invalidAttribute;
	public static String abort_invalidExceptionAttribute;
	public static String abort_invalidOpcode;
	public static String abort_missingCode;
	public static String abort_againstSourceModel;
	public static String accept_cannot;
	public static String parser_incorrectPath;
	public static String parser_moveFiles;
	public static String parser_syntaxRecovery;
	public static String parser_regularParse;
	public static String parser_missingFile;
	public static String parser_corruptedFile;
	public static String parser_endOfFile;
	public static String parser_endOfConstructor;
	public static String parser_endOfMethod;
	public static String parser_endOfInitializer;
	public static String ast_missingCode;
	public static String constant_cannotCastedInto;
	public static String constant_cannotConvertedTo;

	static {
		initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 *
	 * @param message the message to be manipulated
	 * @return the manipulated String
	 */
	public static String bind(String message) {
		return bind(message, null);
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 *
	 * @param message the message to be manipulated
	 * @param binding the object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding) {
		return bind(message, new Object[] {binding});
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 *
	 * @param message the message to be manipulated
	 * @param binding1 An object to be inserted into the message
	 * @param binding2 A second object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding1, Object binding2) {
		return bind(message, new Object[] {binding1, binding2});
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 *
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object[] bindings) {
		return MessageFormat.format(message, bindings);
	}

	/*
	 * Build an array of directories to search
	 */
	private static String[] buildVariants(String root) {
		if (nlSuffixes == null) {
			//build list of suffixes for loading resource bundles
			String nl = Locale.getDefault().toString();
			ArrayList result = new ArrayList(4);
			int lastSeparator;
			while (true) {
				result.add('_' + nl + EXTENSION);
				lastSeparator = nl.lastIndexOf('_');
				if (lastSeparator == -1)
					break;
				nl = nl.substring(0, lastSeparator);
			}
			//add the empty suffix last (most general)
			result.add(EXTENSION);
			nlSuffixes = (String[]) result.toArray(new String[result.size()]);
		}
		root = root.replace('.', '/');
		String[] variants = new String[nlSuffixes.length];
		for (int i = 0; i < variants.length; i++)
			variants[i] = root + nlSuffixes[i];
		return variants;
	}
	public static void initializeMessages(String bundleName, Class clazz) {
		// load the resource bundle and set the fields
		final Field[] fields = clazz.getDeclaredFields();
		load(bundleName, clazz.getClassLoader(), fields);

		// iterate over the fields in the class to make sure that there aren't any empty ones
		final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		final int numFields = fields.length;
		for (int i = 0; i < numFields; i++) {
			Field field = fields[i];
			if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED)
				continue;
			try {
				// Set the value into the field if its empty. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// log it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				if (field.get(clazz) == null) {
					String value = "Missing message: " + field.getName() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
					field.set(null, value);
				}
			} catch (IllegalArgumentException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			}
		}
	}
	/**
	 * Load the given resource bundle using the specified class loader.
	 */
	public static void load(final String bundleName, final ClassLoader loader, final Field[] fields) {
		final String[] variants = buildVariants(bundleName);
		// search the dirs in reverse order so the cascading defaults is set correctly
		for (int i = variants.length; --i >= 0;) {
			InputStream input = (loader == null)
				? ClassLoader.getSystemResourceAsStream(variants[i])
				: loader.getResourceAsStream(variants[i]);
			if (input == null) continue;
			try {
				final MessagesProperties properties = new MessagesProperties(fields, bundleName);
				properties.load(input);
			} catch (IOException e) {
				// ignore
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
}
