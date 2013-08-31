/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import javax.tools.FileObject;

/**
 * Util class that defines helper methods to read class contents with handling of wrong encoding
 *
 */
public final class Util {
	public static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

	public static class EncodingError {
		int position;
		int length;
		public EncodingError(int position, int length) {
			this.position = position;
			this.length = length;
		}
		
		public String getSource(char[] unitSource) {
			//extra from the source the innacurate     token
			//and "highlight" it using some underneath ^^^^^
			//put some context around too.

			//this code assumes that the font used in the console is fixed size

			//sanity .....
			int startPosition = this.position;
			int endPosition = this.position + this.length - 1;
			
			if ((startPosition > endPosition)
				|| ((startPosition < 0) && (endPosition < 0))
				|| unitSource.length == 0)
				return "No source available"; //$NON-NLS-1$

			StringBuffer errorBuffer = new StringBuffer();
			errorBuffer.append('\t');
			
			char c;
			final char SPACE = ' ';
			final char MARK = '^';
			final char TAB = '\t';
			//the next code tries to underline the token.....
			//it assumes (for a good display) that token source does not
			//contain any \r \n. This is false on statements ! 
			//(the code still works but the display is not optimal !)

			// expand to line limits
			int length = unitSource.length, begin, end;
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
	}
	public static class EncodingErrorCollector {
		ArrayList<EncodingError> encodingErrors = new ArrayList<EncodingError>();
		FileObject fileObject;
		String encoding;
		
		public EncodingErrorCollector(FileObject fileObject, String encoding) {
			this.fileObject = fileObject;
			this.encoding = encoding;
		}
		public void collect(int position, int length) {
			this.encodingErrors.add(new EncodingError(position, length));
		}
		public void reportAllEncodingErrors(String string) {
			// this is where the encoding errors should be reported
			char[] unitSource = string.toCharArray();
			for (EncodingError error : this.encodingErrors) {
				System.err.println(this.fileObject.getName() + " Unmappable character for encoding " + this.encoding);//$NON-NLS-1$
				System.err.println(error.getSource(unitSource));
			}
		}
	}

	public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding) throws IOException {
		Charset charset = null;
		try {
			charset = Charset.forName(encoding);
		} catch (IllegalCharsetNameException e) {
			System.err.println("Illegal charset name : " + encoding); //$NON-NLS-1$
			return null;
		} catch(UnsupportedCharsetException e) {
			System.err.println("Unsupported charset : " + encoding); //$NON-NLS-1$
			return null;
		}
		CharsetDecoder charsetDecoder = charset.newDecoder();
		charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
		byte[] contents = org.eclipse.jdt.internal.compiler.util.Util.getInputStreamAsByteArray(stream, length);
		ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
		byteBuffer.put(contents);
		byteBuffer.flip();
		return charsetDecoder.decode(byteBuffer).array();
	}
	
	public static CharSequence getCharContents(FileObject fileObject, boolean ignoreEncodingErrors, byte[] contents, String encoding) throws IOException {
		if (contents == null) return null;
		Charset charset = null;
		try {
			charset = Charset.forName(encoding);
		} catch (IllegalCharsetNameException e) {
			System.err.println("Illegal charset name : " + encoding); //$NON-NLS-1$
			return null;
		} catch(UnsupportedCharsetException e) {
			System.err.println("Unsupported charset : " + encoding); //$NON-NLS-1$
			return null;
		}
		CharsetDecoder charsetDecoder = charset.newDecoder();
		ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
		byteBuffer.put(contents);
		byteBuffer.flip();
		if (ignoreEncodingErrors) {
			charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
			return charsetDecoder.decode(byteBuffer);
		} else {
			charsetDecoder.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
			CharBuffer out = CharBuffer.allocate(contents.length);
			CoderResult result = null;
			String replacement = charsetDecoder.replacement();
			int replacementLength = replacement.length();
			EncodingErrorCollector collector = null;
			while (true) {
				result = charsetDecoder.decode(byteBuffer, out, true);
				if (result.isMalformed() || result.isUnmappable()) {
					/* treat the error
					 * The wrong input character is at out.position
					 */
					if (collector == null) {
						collector = new EncodingErrorCollector(fileObject, encoding);
					}
					reportEncodingError(collector, out.position(), result.length());
					if ((out.position() + replacementLength) >= out.capacity()) {
						// resize
						CharBuffer temp = CharBuffer.allocate(out.capacity() * 2);
						out.flip();
						temp.put(out);
						out = temp;
					}
					out.append(replacement);
					byteBuffer.position(byteBuffer.position() + result.length());
					continue;
				}
				if (result.isOverflow()) {
					CharBuffer temp = CharBuffer.allocate(out.capacity() * 2);
					out.flip();
					temp.put(out);
					out = temp;
				} else {
					break;
				}
			}
			out.flip();
			if (collector != null) {
				collector.reportAllEncodingErrors(out.toString());
			}
			return out;
		}
	}
	
	private static void reportEncodingError(EncodingErrorCollector collector, int position, int length) {
		collector.collect(position, -length);
	}
}

