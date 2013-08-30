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
package org.eclipse.jdt.internal.antadapter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class AntAdapterMessages {

	private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.antadapter.messages"; //$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE;

	static {
		try {
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
		} catch(MissingResourceException e) {
			System.out.println("Missing resource : " + BUNDLE_NAME.replace('.', '/') + ".properties for locale " + Locale.getDefault()); //$NON-NLS-1$//$NON-NLS-2$
			throw e;
		}
	}

	private AntAdapterMessages() {
		// cannot be instantiated
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getString(String key, String argument) {
		try {
			String message = RESOURCE_BUNDLE.getString(key);
			MessageFormat messageFormat = new MessageFormat(message);
			return messageFormat.format(new String[] { argument } );
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
