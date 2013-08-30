/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Walter Harley   - Patch for ensuring the parent folders are created
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.SimpleJavaFileObject;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;

/**
 * Implementation of a Java file object that corresponds to a file on the file system
 */
public class EclipseFileObject extends SimpleJavaFileObject {
	private File f;
	private Charset charset;
	private boolean parentsExist; // parent directories exist
	
	public EclipseFileObject(String className, URI uri, Kind kind, Charset charset) {
		super(uri, kind);
		this.f = new File(this.uri);
		this.charset = charset;
		this.parentsExist = false;
	}

	/* (non-Javadoc)
	 * @see javax.tools.JavaFileObject#getAccessLevel()
	 */
	@Override
	public Modifier getAccessLevel() {
		// cannot express multiple modifier
		if (getKind() != Kind.CLASS) {
			return null;
		}
		ClassFileReader reader = null;
   		try {
			reader = ClassFileReader.read(this.f);
		} catch (ClassFormatException e) {
			// ignore
		} catch (IOException e) {
			// ignore
		}
		if (reader == null) {
			return null;
		}
		final int accessFlags = reader.accessFlags();
		if ((accessFlags & ClassFileConstants.AccPublic) != 0) {
			return Modifier.PUBLIC;
		}
		if ((accessFlags & ClassFileConstants.AccAbstract) != 0) {
			return Modifier.ABSTRACT;
		}
		if ((accessFlags & ClassFileConstants.AccFinal) != 0) {
			return Modifier.FINAL;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.tools.JavaFileObject#getNestingKind()
	 */
	@Override
	public NestingKind getNestingKind() {
		switch(this.kind) {
			case SOURCE :
				return NestingKind.TOP_LEVEL;
			case CLASS :
        		ClassFileReader reader = null;
        		try {
        			reader = ClassFileReader.read(this.f);
        		} catch (ClassFormatException e) {
        			// ignore
        		} catch (IOException e) {
        			// ignore
        		}
        		if (reader == null) {
        			return null;
        		}
        		if (reader.isAnonymous()) {
        			return NestingKind.ANONYMOUS;
        		}
        		if (reader.isLocal()) {
        			return NestingKind.LOCAL;
        		}
        		if (reader.isMember()) {
        			return NestingKind.MEMBER;
        		}
        		return NestingKind.TOP_LEVEL;
        	default:
        		return null;
		}
	}

	/**
	 * @see javax.tools.FileObject#delete()
	 */
	@Override
	public boolean delete() {
		return this.f.delete();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EclipseFileObject)) {
			return false;
		}
		EclipseFileObject eclipseFileObject = (EclipseFileObject) o;
		return eclipseFileObject.toUri().equals(this.uri);
	}

	/**
	 * @see javax.tools.FileObject#getCharContent(boolean)
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return Util.getCharContents(this, ignoreEncodingErrors, org.eclipse.jdt.internal.compiler.util.Util.getFileByteContent(this.f), this.charset.name());
	}

	/**
	 * @see javax.tools.FileObject#getLastModified()
	 */
	@Override
	public long getLastModified() {
		return this.f.lastModified();
	}

	@Override
	public String getName() {
        return this.f.getPath();
    }
    
	@Override
	public int hashCode() {
		return this.f.hashCode();
	}

	/**
	 * @see javax.tools.FileObject#openInputStream()
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		// TODO (olivier) should use buffered input stream
		return new FileInputStream(this.f);
	}

	/**
	 * @see javax.tools.FileObject#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		ensureParentDirectoriesExist();
		return new FileOutputStream(this.f);
	}

	/**
	 * @see javax.tools.FileObject#openReader(boolean)
	 */
	@Override
	public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
		return new FileReader(this.f);
	}

	/**
	 * @see javax.tools.FileObject#openWriter()
	 */
	@Override
	public Writer openWriter() throws IOException {
		ensureParentDirectoriesExist();
		return new FileWriter(this.f);
	}
	
	@Override
	public String toString() {
		return this.f.getAbsolutePath();
	}
	
    private void ensureParentDirectoriesExist() throws IOException {
        if (!this.parentsExist) {
            File parent = this.f.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    // could have been concurrently created
                    if (!parent.exists() || !parent.isDirectory())
                        throw new IOException("Unable to create parent directories for " + this.f); //$NON-NLS-1$
                }
            }
            this.parentsExist = true;
        }
    }


}
