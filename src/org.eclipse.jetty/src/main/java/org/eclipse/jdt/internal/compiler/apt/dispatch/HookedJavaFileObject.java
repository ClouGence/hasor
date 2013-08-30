/*******************************************************************************
 * Copyright (c) 2006, 2007 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *    
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaFileObject;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

/**
 * A delegating JavaFileObject that hooks the close() methods of the Writer
 * or OutputStream objects that it produces, and notifies the annotation
 * dispatch manager when a new compilation unit is produced.
 */
public class HookedJavaFileObject extends
		ForwardingJavaFileObject<JavaFileObject> 
{
	// A delegating Writer that passes all commands to its contained Writer,
	// but hooks close() to notify the annotation dispatch manager of the new unit.
	private class ForwardingWriter extends Writer {
		private final Writer _w;
		ForwardingWriter(Writer w) {
			_w = w;
		}
		@Override
		public Writer append(char c) throws IOException {
			return _w.append(c);
		}
		@Override
		public Writer append(CharSequence csq, int start, int end)
				throws IOException {
			return _w.append(csq, start, end);
		}
		@Override
		public Writer append(CharSequence csq) throws IOException {
			return _w.append(csq);
		}
		// This is the only interesting method - it has to notify the
		// dispatch manager of the new file.
		@Override
		public void close() throws IOException {
			_w.close();
			closed();
		}
		@Override
		public void flush() throws IOException {
			_w.flush();
		}
		@Override
		public void write(char[] cbuf) throws IOException {
			_w.write(cbuf);
		}
		@Override
		public void write(int c) throws IOException {
			_w.write(c);
		}
		@Override
		public void write(String str, int off, int len)
				throws IOException {
			_w.write(str, off, len);
		}
		@Override
		public void write(String str) throws IOException {
			_w.write(str);
		}
		@Override
		public void write(char[] cbuf, int off, int len)
		throws IOException {
			_w.write(cbuf, off, len);
		}
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return new ForwardingWriter(this._w);
		}
		@Override
		public int hashCode() {
			return _w.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ForwardingWriter other = (ForwardingWriter) obj;
			if (_w == null) {
				if (other._w != null)
					return false;
			} else if (!_w.equals(other._w))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "ForwardingWriter wrapping " + _w.toString(); //$NON-NLS-1$
		}
	}
	
	// A delegating Writer that passes all commands to its contained Writer,
	// but hooks close() to notify the annotation dispatch manager of the new unit.
	private class ForwardingOutputStream extends OutputStream {
		private final OutputStream _os;
		
		ForwardingOutputStream(OutputStream os) {
			_os = os;
		}
		
		@Override
		public void close() throws IOException {
			_os.close();
			closed();
		}
		@Override
		public void flush() throws IOException {
			_os.flush();
		}
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			_os.write(b, off, len);
		}
		@Override
		public void write(byte[] b) throws IOException {
			_os.write(b);
		}
		@Override
		public void write(int b) throws IOException {
			_os.write(b);
		}
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return new ForwardingOutputStream(this._os);
		}
		@Override
		public int hashCode() {
			return _os.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ForwardingOutputStream other = (ForwardingOutputStream) obj;
			if (_os == null) {
				if (other._os != null)
					return false;
			} else if (!_os.equals(other._os))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "ForwardingOutputStream wrapping " + _os.toString(); //$NON-NLS-1$
		}
	}
	
	/**
	 * The Filer implementation that we need to notify when a new file is created. 
	 */
	protected final BatchFilerImpl _filer;
	
	/**
	 * The name of the file that is created; this is passed to the CompilationUnit constructor,
	 * and ultimately to the java.io.File constructor, so it is a normal pathname, just like 
	 * what would be on the compiler command line.
	 */
	protected final String _fileName;
	
	/**
	 * A compilation unit is created when the writer or stream is closed.  Only do this once.
	 */
	private boolean _closed = false;
	
	public HookedJavaFileObject(JavaFileObject fileObject, String fileName, BatchFilerImpl filer) {
		super(fileObject);
		_filer = filer;
		_fileName = fileName;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return new ForwardingOutputStream(super.openOutputStream());
	}

	@Override
	public Writer openWriter() throws IOException {
		return new ForwardingWriter(super.openWriter());
	}
	
	protected void closed() {
		if (!_closed) {
			_closed = true;
			//TODO: support encoding
			switch(this.getKind()) {
				case SOURCE :
					CompilationUnit unit = new CompilationUnit(null, _fileName, null /* encoding */);
					_filer.addNewUnit(unit);
					break;
				case CLASS :
					IBinaryType binaryType = null;
					try {
						binaryType = ClassFileReader.read(_fileName);
					} catch (ClassFormatException e) {
						// ignore
					} catch (IOException e) {
						// ignore
					}
					if (binaryType != null) {
						char[] name = binaryType.getName();
						ReferenceBinding type = this._filer._env._compiler.lookupEnvironment.getType(CharOperation.splitOn('/', name));
						if (type != null && type.isValidBinding() && type.isBinaryBinding()) {
							_filer.addNewClassFile(type);
						}
					}
			}
		}
	}
}
