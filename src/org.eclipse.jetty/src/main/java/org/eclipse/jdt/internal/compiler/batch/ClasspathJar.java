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
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.util.ManifestAnalyzer;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathJar extends ClasspathLocation {

protected File file;
protected ZipFile zipFile;
protected boolean closeZipFileAtEnd;
protected Hashtable packageCache;

public ClasspathJar(File file, boolean closeZipFileAtEnd,
		AccessRuleSet accessRuleSet, String destinationPath) {
	super(accessRuleSet, destinationPath);
	this.file = file;
	this.closeZipFileAtEnd = closeZipFileAtEnd;
}

public List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
	// expected to be called once only - if multiple calls desired, consider
	// using a cache
	InputStream inputStream = null;
	try {
		initialize();
		ArrayList result = new ArrayList();
		ZipEntry manifest = this.zipFile.getEntry("META-INF/MANIFEST.MF"); //$NON-NLS-1$
		if (manifest != null) { // non-null implies regular file
			inputStream = this.zipFile.getInputStream(manifest);
			ManifestAnalyzer analyzer = new ManifestAnalyzer();
			boolean success = analyzer.analyzeManifestContents(inputStream);
			List calledFileNames = analyzer.getCalledFileNames();
			if (problemReporter != null) {
				if (!success || analyzer.getClasspathSectionsCount() == 1 &&  calledFileNames == null) {
					problemReporter.invalidClasspathSection(getPath());
				} else if (analyzer.getClasspathSectionsCount() > 1) {
					problemReporter.multipleClasspathSections(getPath());
				}
			}
			if (calledFileNames != null) {
				Iterator calledFilesIterator = calledFileNames.iterator();
				String directoryPath = getPath();
				int lastSeparator = directoryPath.lastIndexOf(File.separatorChar);
				directoryPath = directoryPath.substring(0, lastSeparator + 1); // potentially empty (see bug 214731)
				while (calledFilesIterator.hasNext()) {
					result.add(new ClasspathJar(new File(directoryPath + (String) calledFilesIterator.next()), this.closeZipFileAtEnd, this.accessRuleSet, this.destinationPath));
				}
			}
		}
		return result;
	} catch (IOException e) {
		return null;
	} finally {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// best effort
			}
		}
	}
}
public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName) {
	return findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
}
public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
	if (!isPackage(qualifiedPackageName))
		return null; // most common case

	try {
		ClassFileReader reader = ClassFileReader.read(this.zipFile, qualifiedBinaryFileName);
		if (reader != null)
			return new NameEnvironmentAnswer(reader, fetchAccessRestriction(qualifiedBinaryFileName));
	} catch(ClassFormatException e) {
		// treat as if class file is missing
	} catch (IOException e) {
		// treat as if class file is missing
	}
	return null;
}
public char[][][] findTypeNames(String qualifiedPackageName) {
	if (!isPackage(qualifiedPackageName))
		return null; // most common case

	ArrayList answers = new ArrayList();
	nextEntry : for (Enumeration e = this.zipFile.entries(); e.hasMoreElements(); ) {
		String fileName = ((ZipEntry) e.nextElement()).getName();

		// add the package name & all of its parent packages
		int last = fileName.lastIndexOf('/');
		while (last > 0) {
			// extract the package name
			String packageName = fileName.substring(0, last);
			if (!qualifiedPackageName.equals(packageName))
				continue nextEntry;
			int indexOfDot = fileName.lastIndexOf('.');
			if (indexOfDot != -1) {
				String typeName = fileName.substring(last + 1, indexOfDot);
				char[] packageArray = packageName.toCharArray();
				answers.add(
					CharOperation.arrayConcat(
						CharOperation.splitOn('/', packageArray),
						typeName.toCharArray()));
			}
		}
	}
	int size = answers.size();
	if (size != 0) {
		char[][][] result = new char[size][][];
		answers.toArray(result);
		return null;
	}
	return null;
}
public void initialize() throws IOException {
	if (this.zipFile == null) {
		this.zipFile = new ZipFile(this.file);
	}
}
public boolean isPackage(String qualifiedPackageName) {
	if (this.packageCache != null)
		return this.packageCache.containsKey(qualifiedPackageName);

	this.packageCache = new Hashtable(41);
	this.packageCache.put(Util.EMPTY_STRING, Util.EMPTY_STRING);

	nextEntry : for (Enumeration e = this.zipFile.entries(); e.hasMoreElements(); ) {
		String fileName = ((ZipEntry) e.nextElement()).getName();

		// add the package name & all of its parent packages
		int last = fileName.lastIndexOf('/');
		while (last > 0) {
			// extract the package name
			String packageName = fileName.substring(0, last);
			if (this.packageCache.containsKey(packageName))
				continue nextEntry;
			this.packageCache.put(packageName, packageName);
			last = packageName.lastIndexOf('/');
		}
	}
	return this.packageCache.containsKey(qualifiedPackageName);
}
public void reset() {
	if (this.zipFile != null && this.closeZipFileAtEnd) {
		try {
			this.zipFile.close();
		} catch(IOException e) {
			// ignore
		}
		this.zipFile = null;
	}
	this.packageCache = null;
}
public String toString() {
	return "Classpath for jar file " + this.file.getPath(); //$NON-NLS-1$
}
public char[] normalizedPath() {
	if (this.normalizedPath == null) {
		String path2 = this.getPath();
		char[] rawName = path2.toCharArray();
		if (File.separatorChar == '\\') {
			CharOperation.replace(rawName, '\\', '/');
		}
		this.normalizedPath = CharOperation.subarray(rawName, 0, CharOperation.lastIndexOf('.', rawName));
	}
	return this.normalizedPath;
}
public String getPath() {
	if (this.path == null) {
		try {
			this.path = this.file.getCanonicalPath();
		} catch (IOException e) {
			// in case of error, simply return the absolute path
			this.path = this.file.getAbsolutePath();
		}
	}
	return this.path;
}
public int getMode() {
	return BINARY;
}
}
