/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.parser;

import java.util.List;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

/**
 * Parser specialized for decoding javadoc comments
 */
public class JavadocParser extends AbstractCommentParser {

	// Public fields
	public Javadoc docComment;

	// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=51600
	// Store param references for tag with invalid syntax
	private int invalidParamReferencesPtr = -1;
	private ASTNode[] invalidParamReferencesStack;

	// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=153399
	// Store value tag positions
	private long validValuePositions, invalidValuePositions;

	// returns whether this JavadocParser should report errors or not (overrides reportProblems)
	// see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=192449"
	public boolean shouldReportProblems = true;
	
	// flag to let the parser know that the current tag is waiting for a description
	// see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=222900"
	private int tagWaitingForDescription;

	public JavadocParser(Parser sourceParser) {
		super(sourceParser);
		this.kind = COMPIL_PARSER | TEXT_VERIF;
		if (sourceParser != null && sourceParser.options != null) {
			this.setJavadocPositions = sourceParser.options.processAnnotations;
		}
	}

	/* (non-Javadoc)
	 * Returns true if tag @deprecated is present in javadoc comment.
	 *
	 * If javadoc checking is enabled, will also construct an Javadoc node, which will be stored into Parser.javadoc
	 * slot for being consumed later on.
	 */
	public boolean checkDeprecation(int commentPtr) {

		// Store javadoc positions
		this.javadocStart = this.sourceParser.scanner.commentStarts[commentPtr];
		this.javadocEnd = this.sourceParser.scanner.commentStops[commentPtr]-1;
		this.firstTagPosition = this.sourceParser.scanner.commentTagStarts[commentPtr];
		this.validValuePositions = -1;
		this.invalidValuePositions = -1;
		this.tagWaitingForDescription = NO_TAG_VALUE;

		// Init javadoc if necessary
		if (this.checkDocComment) {
			this.docComment = new Javadoc(this.javadocStart, this.javadocEnd);
		} else if (this.setJavadocPositions) {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=189459
			// if annotation processors are there, javadoc object is required but
			// they need not be resolved
			this.docComment = new Javadoc(this.javadocStart, this.javadocEnd);
			this.docComment.bits &= ~ASTNode.ResolveJavadoc;
		} else {
			this.docComment = null;
		}

		// If there's no tag in javadoc, return without parsing it
		if (this.firstTagPosition == 0) {
			switch (this.kind & PARSER_KIND) {
				case COMPIL_PARSER:
				case SOURCE_PARSER:
					return false;
			}
		}

		// Parse
		try {
			this.source = this.sourceParser.scanner.source;
			if (this.checkDocComment) {
				// Initialization
				this.scanner.lineEnds = this.sourceParser.scanner.lineEnds;
				this.scanner.linePtr = this.sourceParser.scanner.linePtr;
				this.lineEnds = this.scanner.lineEnds;
				commentParse();
			} else {

				// Parse comment
				Scanner sourceScanner = this.sourceParser.scanner;
				int firstLineNumber = Util.getLineNumber(this.javadocStart, sourceScanner.lineEnds, 0, sourceScanner.linePtr);
				int lastLineNumber = Util.getLineNumber(this.javadocEnd, sourceScanner.lineEnds, 0, sourceScanner.linePtr);
				this.index = this.javadocStart +3;

				// scan line per line, since tags must be at beginning of lines only
				this.deprecated = false;
				nextLine : for (int line = firstLineNumber; line <= lastLineNumber; line++) {
					int lineStart = line == firstLineNumber
							? this.javadocStart + 3 // skip leading /**
							: this.sourceParser.scanner.getLineStart(line);
					this.index = lineStart;
					this.lineEnd = line == lastLineNumber
							? this.javadocEnd - 2 // remove trailing * /
							: this.sourceParser.scanner.getLineEnd(line);
					nextCharacter : while (this.index < this.lineEnd) {
						char c = readChar(); // consider unicodes
						switch (c) {
							case '*' :
							case '\u000c' :	/* FORM FEED               */
							case ' ' :			/* SPACE                   */
							case '\t' :			/* HORIZONTAL TABULATION   */
							case '\n' :			/* LINE FEED   */
							case '\r' :			/* CR */
								// do nothing for space or '*' characters
						        continue nextCharacter;
						    case '@' :
						    	parseSimpleTag();
						    	if (this.tagValue == TAG_DEPRECATED_VALUE) {
						    		if (this.abort) break nextCharacter;
						    	}
						}
			        	continue nextLine;
					}
				}
				return this.deprecated;
			}
		} finally {
			this.source = null; // release source as soon as finished
		}
		return this.deprecated;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#createArgumentReference(char[], java.lang.Object, int)
	 */
	protected Object createArgumentReference(char[] name, int dim, boolean isVarargs, Object typeRef, long[] dimPositions, long argNamePos) throws InvalidInputException {
		try {
			TypeReference argTypeRef = (TypeReference) typeRef;
			if (dim > 0) {
				long pos = (((long) argTypeRef.sourceStart) << 32) + argTypeRef.sourceEnd;
				if (typeRef instanceof JavadocSingleTypeReference) {
					JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference) typeRef;
					argTypeRef = new JavadocArraySingleTypeReference(singleRef.token, dim, pos);
				} else {
					JavadocQualifiedTypeReference qualifRef = (JavadocQualifiedTypeReference) typeRef;
					argTypeRef = new JavadocArrayQualifiedTypeReference(qualifRef, dim);
				}
			}
			int argEnd = argTypeRef.sourceEnd;
			if (dim > 0) {
				argEnd = (int) dimPositions[dim-1];
				if (isVarargs) {
					argTypeRef.bits |= ASTNode.IsVarArgs; // set isVarArgs
				}
			}
			if (argNamePos >= 0) argEnd = (int) argNamePos;
			return new JavadocArgumentExpression(name, argTypeRef.sourceStart, argEnd, argTypeRef);
		}
		catch (ClassCastException ex) {
			throw new InvalidInputException();
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#createFieldReference()
	 */
	protected Object createFieldReference(Object receiver) throws InvalidInputException {
		try {
			// Get receiver type
			TypeReference typeRef = (TypeReference) receiver;
			if (typeRef == null) {
				char[] name = this.sourceParser.compilationUnit.getMainTypeName();
				typeRef = new JavadocImplicitTypeReference(name, this.memberStart);
			}
			// Create field
			JavadocFieldReference field = new JavadocFieldReference(this.identifierStack[0], this.identifierPositionStack[0]);
			field.receiver = typeRef;
			field.tagSourceStart = this.tagSourceStart;
			field.tagSourceEnd = this.tagSourceEnd;
			field.tagValue = this.tagValue;
			return field;
		}
		catch (ClassCastException ex) {
			throw new InvalidInputException();
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#createMethodReference(java.lang.Object[])
	 */
	protected Object createMethodReference(Object receiver, List arguments) throws InvalidInputException {
		try {
			// Get receiver type
			TypeReference typeRef = (TypeReference) receiver;
			// Decide whether we have a constructor or not
			boolean isConstructor = false;
			int length = this.identifierLengthStack[0];	// may be > 1 for member class constructor reference
			if (typeRef == null) {
				char[] name = this.sourceParser.compilationUnit.getMainTypeName();
				TypeDeclaration typeDecl = getParsedTypeDeclaration();
				if (typeDecl != null) {
					name = typeDecl.name;
				}
				isConstructor = CharOperation.equals(this.identifierStack[length-1], name);
				typeRef = new JavadocImplicitTypeReference(name, this.memberStart);
			} else {
				if (typeRef instanceof JavadocSingleTypeReference) {
					char[] name = ((JavadocSingleTypeReference)typeRef).token;
					isConstructor = CharOperation.equals(this.identifierStack[length-1], name);
				} else if (typeRef instanceof JavadocQualifiedTypeReference) {
					char[][] tokens = ((JavadocQualifiedTypeReference)typeRef).tokens;
					int last = tokens.length-1;
					isConstructor = CharOperation.equals(this.identifierStack[length-1], tokens[last]);
					if (isConstructor) {
						boolean valid = true;
						if (valid) {
							for (int i=0; i<length-1 && valid; i++) {
								valid = CharOperation.equals(this.identifierStack[i], tokens[i]);
							}
						}
						if (!valid) {
							if (this.reportProblems) {
								this.sourceParser.problemReporter().javadocInvalidMemberTypeQualification((int)(this.identifierPositionStack[0]>>>32), (int)this.identifierPositionStack[length-1], -1);
							}
							return null;
						}
					}
				} else {
					throw new InvalidInputException();
				}
			}
			// Create node
			if (arguments == null) {
				if (isConstructor) {
					JavadocAllocationExpression allocation = new JavadocAllocationExpression(this.identifierPositionStack[length-1]);
					allocation.type = typeRef;
					allocation.tagValue = this.tagValue;
					allocation.sourceEnd = this.scanner.getCurrentTokenEndPosition();
					if (length == 1) {
						allocation.qualification = new char[][] { this.identifierStack[0] };
					} else {
						System.arraycopy(this.identifierStack, 0, allocation.qualification = new char[length][], 0, length);
						allocation.sourceStart = (int) (this.identifierPositionStack[0] >>> 32);
					}
					allocation.memberStart = this.memberStart;
					return allocation;
				} else {
					JavadocMessageSend msg = new JavadocMessageSend(this.identifierStack[length-1], this.identifierPositionStack[length-1]);
					msg.receiver = typeRef;
					msg.tagValue = this.tagValue;
					msg.sourceEnd = this.scanner.getCurrentTokenEndPosition();
					return msg;
				}
			} else {
				JavadocArgumentExpression[] expressions = new JavadocArgumentExpression[arguments.size()];
				arguments.toArray(expressions);
				if (isConstructor) {
					JavadocAllocationExpression allocation = new JavadocAllocationExpression(this.identifierPositionStack[length-1]);
					allocation.arguments = expressions;
					allocation.type = typeRef;
					allocation.tagValue = this.tagValue;
					allocation.sourceEnd = this.scanner.getCurrentTokenEndPosition();
					if (length == 1) {
						allocation.qualification = new char[][] { this.identifierStack[0] };
					} else {
						System.arraycopy(this.identifierStack, 0, allocation.qualification = new char[length][], 0, length);
						allocation.sourceStart = (int) (this.identifierPositionStack[0] >>> 32);
					}
					allocation.memberStart = this.memberStart;
					return allocation;
				} else {
					JavadocMessageSend msg = new JavadocMessageSend(this.identifierStack[length-1], this.identifierPositionStack[length-1], expressions);
					msg.receiver = typeRef;
					msg.tagValue = this.tagValue;
					msg.sourceEnd = this.scanner.getCurrentTokenEndPosition();
					return msg;
				}
			}
		}
		catch (ClassCastException ex) {
			throw new InvalidInputException();
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#createReturnStatement()
	 */
	protected Object createReturnStatement() {
		return new JavadocReturnStatement(this.scanner.getCurrentTokenStartPosition(),
					this.scanner.getCurrentTokenEndPosition());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#parseTagName()
	 */
	protected void createTag() {
		this.tagValue = TAG_OTHERS_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#createTypeReference()
	 */
	protected Object createTypeReference(int primitiveToken) {
		TypeReference typeRef = null;
		int size = this.identifierLengthStack[this.identifierLengthPtr];
		if (size == 1) { // Single Type ref
			typeRef = new JavadocSingleTypeReference(
						this.identifierStack[this.identifierPtr],
						this.identifierPositionStack[this.identifierPtr],
						this.tagSourceStart,
						this.tagSourceEnd);
		} else if (size > 1) { // Qualified Type ref
			char[][] tokens = new char[size][];
			System.arraycopy(this.identifierStack, this.identifierPtr - size + 1, tokens, 0, size);
			long[] positions = new long[size];
			System.arraycopy(this.identifierPositionStack, this.identifierPtr - size + 1, positions, 0, size);
			typeRef = new JavadocQualifiedTypeReference(tokens, positions, this.tagSourceStart, this.tagSourceEnd);
		}
		return typeRef;
	}

	/*
	 * Get current parsed type declaration.
	 */
	protected TypeDeclaration getParsedTypeDeclaration() {
		int ptr = this.sourceParser.astPtr;
		while (ptr >= 0) {
			Object node = this.sourceParser.astStack[ptr];
			if (node instanceof TypeDeclaration) {
				TypeDeclaration typeDecl = (TypeDeclaration) node;
				if (typeDecl.bodyEnd == 0) { // type declaration currenly parsed
					return typeDecl;
				}
			}
			ptr--;
		}
		return null;
	}

	/*
	 * Parse @throws tag declaration and flag missing description if corresponding option is enabled
	 */
	protected boolean parseThrows() {
		boolean valid = super.parseThrows();
		this.tagWaitingForDescription = valid && this.reportProblems ? TAG_THROWS_VALUE : NO_TAG_VALUE;
		return valid;
	}

	/*
	 * Parse @return tag declaration
	 */
	protected boolean parseReturn() {
		if (this.returnStatement == null) {
			this.returnStatement = createReturnStatement();
			return true;
		}
		if (this.reportProblems) {
			this.sourceParser.problemReporter().javadocDuplicatedReturnTag(
				this.scanner.getCurrentTokenStartPosition(),
				this.scanner.getCurrentTokenEndPosition());
		}
		return false;
	}


	protected void parseSimpleTag() {

		// Read first char
		// readChar() code is inlined to balance additional method call in checkDeprectation(int)
		char first = this.source[this.index++];
		if (first == '\\' && this.source[this.index] == 'u') {
			int c1, c2, c3, c4;
			int pos = this.index;
			this.index++;
			while (this.source[this.index] == 'u')
				this.index++;
			if (!(((c1 = ScannerHelper.getNumericValue(this.source[this.index++])) > 15 || c1 < 0)
					|| ((c2 = ScannerHelper.getNumericValue(this.source[this.index++])) > 15 || c2 < 0)
					|| ((c3 = ScannerHelper.getNumericValue(this.source[this.index++])) > 15 || c3 < 0) || ((c4 = ScannerHelper.getNumericValue(this.source[this.index++])) > 15 || c4 < 0))) {
				first = (char) (((c1 * 16 + c2) * 16 + c3) * 16 + c4);
			} else {
				this.index = pos;
			}
		}

		// switch on first tag char
		switch (first) {
			case 'd':
		        if ((readChar() == 'e') &&
						(readChar() == 'p') && (readChar() == 'r') &&
						(readChar() == 'e') && (readChar() == 'c') &&
						(readChar() == 'a') && (readChar() == 't') &&
						(readChar() == 'e') && (readChar() == 'd')) {
					// ensure the tag is properly ended: either followed by a space, a tab, line end or asterisk.
					char c = readChar();
					if (ScannerHelper.isWhitespace(c) || c == '*') {
						this.abort = true;
			    		this.deprecated = true;
						this.tagValue = TAG_DEPRECATED_VALUE;
					}
		        }
				break;
		}
	}

	protected boolean parseTag(int previousPosition) throws InvalidInputException {

		// Complain when tag is missing a description
		// Note that if the parse of an inline tag has already started, consider it
		// as the expected description, hence do not report any warning
		switch (this.tagWaitingForDescription) {
			case TAG_PARAM_VALUE:
			case TAG_THROWS_VALUE:
				if (!this.inlineTagStarted) {
					int start = (int) (this.identifierPositionStack[0] >>> 32);
					int end = (int) this.identifierPositionStack[this.identifierPtr];
					this.sourceParser.problemReporter().javadocMissingTagDescriptionAfterReference(start, end, this.sourceParser.modifiers);
				}
				break;
			case NO_TAG_VALUE:
				break;
			default:
				if (!this.inlineTagStarted) {
					this.sourceParser.problemReporter().javadocMissingTagDescription(TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
				}
				break;
		}
		this.tagWaitingForDescription = NO_TAG_VALUE;

		// Verify first character
		this.tagSourceStart = this.index;
		this.tagSourceEnd = previousPosition;
		this.scanner.startPosition = this.index;
		int currentPosition = this.index;
		char firstChar = readChar();
		switch (firstChar) {
			case ' ':
			case '*':
			case '}':
			case '#':
				// the first character is not valid, hence report invalid empty tag
				if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(previousPosition, currentPosition);
				if (this.textStart == -1) this.textStart = currentPosition;
				this.scanner.currentCharacter = firstChar;
				return false;
			default:
				if (ScannerHelper.isWhitespace(firstChar)) {
					// the first character is not valid, hence report invalid empty tag
					if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(previousPosition, currentPosition);
					if (this.textStart == -1) this.textStart = currentPosition;
					this.scanner.currentCharacter = firstChar;
					return false;
				}
				break;
		}
		
		// Read tag name
		char[] tagName = new char[32];
		int length = 0;
		char currentChar = firstChar;
		int tagNameLength = tagName.length;
		boolean validTag = true;
		tagLoop: while (true) {
			if (length == tagNameLength) {
				System.arraycopy(tagName, 0, tagName = new char[tagNameLength+32], 0, tagNameLength);
				tagNameLength = tagName.length;
			}
			tagName[length++] = currentChar;
			currentPosition = this.index;
			currentChar = readChar();
			switch (currentChar) {
				case ' ':
				case '*':
				case '}':
					// these characters mark the end of the tag reading
					break tagLoop;
				case '#':
					// invalid tag character, mark the tag as invalid but continue until the end of the tag
					validTag = false;
					break;
				default:
					if (ScannerHelper.isWhitespace(currentChar)) {
						// whitespace characters mark the end of the tag reading
						break tagLoop;
					}
					break;
			}
		}

		// Init positions
		this.tagSourceEnd = currentPosition - 1;
		this.scanner.currentCharacter = currentChar;
		this.scanner.currentPosition = currentPosition;
		this.index = this.tagSourceEnd+1;

		// Return if the tag is not valid
		if (!validTag) {
			if (this.reportProblems) this.sourceParser.problemReporter().javadocInvalidTag(this.tagSourceStart, this.tagSourceEnd);
			if (this.textStart == -1) this.textStart = this.index;
			this.scanner.currentCharacter = currentChar;
			return false;
		}

		// Decide which parse to perform depending on tag name
		this.tagValue = TAG_OTHERS_VALUE;
		boolean valid = false;
		switch (firstChar) {
			case 'a':
				if (length == TAG_AUTHOR_LENGTH && CharOperation.equals(TAG_AUTHOR, tagName, 0, length)) {
					this.tagValue = TAG_AUTHOR_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				}
				break;
			case 'c':
				if (length == TAG_CATEGORY_LENGTH && CharOperation.equals(TAG_CATEGORY, tagName, 0, length)) {
					this.tagValue = TAG_CATEGORY_VALUE;
					if (!this.inlineTagStarted) {
						valid = parseIdentifierTag(false); // TODO (frederic) reconsider parameter value when @category will be significant in spec
					}
				} else if (length == TAG_CODE_LENGTH && this.inlineTagStarted && CharOperation.equals(TAG_CODE, tagName, 0, length)) {
					this.tagValue = TAG_CODE_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				}
				break;
			case 'd':
				if (length == TAG_DEPRECATED_LENGTH && CharOperation.equals(TAG_DEPRECATED, tagName, 0, length)) {
					this.deprecated = true;
					valid = true;
					this.tagValue = TAG_DEPRECATED_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				} else if (length == TAG_DOC_ROOT_LENGTH && CharOperation.equals(TAG_DOC_ROOT, tagName, 0, length)) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=227730
					// identify @docRoot tag as a base tag that does not expect any argument
					valid = true;
					this.tagValue = TAG_DOC_ROOT_VALUE;
				}
				break;
			case 'e':
				if (length == TAG_EXCEPTION_LENGTH && CharOperation.equals(TAG_EXCEPTION, tagName, 0, length)) {
					this.tagValue = TAG_EXCEPTION_VALUE;
					if (!this.inlineTagStarted) {
						valid = parseThrows();
					}
				}
				break;
			case 'i':
				if (length == TAG_INHERITDOC_LENGTH && CharOperation.equals(TAG_INHERITDOC, tagName, 0, length)) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=247037, @inheritDoc usage is illegal
					// outside of few block tags and the main description.
					switch (this.lastBlockTagValue) {
						case TAG_RETURN_VALUE:
						case TAG_THROWS_VALUE:
						case TAG_EXCEPTION_VALUE:
						case TAG_PARAM_VALUE:
						case NO_TAG_VALUE:     // Still in main description
							valid = true;
							if (this.reportProblems) {
								recordInheritedPosition((((long) this.tagSourceStart) << 32) + this.tagSourceEnd);
							}
							if (this.inlineTagStarted) {
								// parse a 'valid' inheritDoc tag
								parseInheritDocTag();
							}
							break;
						default:
							valid = false;
							if (this.reportProblems) {
								this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart,
										this.tagSourceEnd);
							}
					}
					this.tagValue = TAG_INHERITDOC_VALUE;
				}
				break;
			case 'l':
				if (length == TAG_LINK_LENGTH && CharOperation.equals(TAG_LINK, tagName, 0, length)) {
					this.tagValue = TAG_LINK_VALUE;
					if (this.inlineTagStarted || (this.kind & COMPLETION_PARSER) != 0) {
						valid= parseReference();
					}
				} else if (length == TAG_LINKPLAIN_LENGTH && CharOperation.equals(TAG_LINKPLAIN, tagName, 0, length)) {
					this.tagValue = TAG_LINKPLAIN_VALUE;
					if (this.inlineTagStarted) {
						valid = parseReference();
					} 
				} else if (length == TAG_LITERAL_LENGTH && this.inlineTagStarted && CharOperation.equals(TAG_LITERAL, tagName, 0, length)) {
					this.tagValue = TAG_LITERAL_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				}
				break;
			case 'p':
				if (length == TAG_PARAM_LENGTH && CharOperation.equals(TAG_PARAM, tagName, 0, length)) {
					this.tagValue = TAG_PARAM_VALUE;
					if (!this.inlineTagStarted) {
						valid = parseParam();
					}
				}
				break;
			case 'r':
				if (length == TAG_RETURN_LENGTH && CharOperation.equals(TAG_RETURN, tagName, 0, length)) {
					this.tagValue = TAG_RETURN_VALUE;
					if (!this.inlineTagStarted) {
						valid = parseReturn();
					}
				}
				break;
			case 's':
				if (length == TAG_SEE_LENGTH && CharOperation.equals(TAG_SEE, tagName, 0, length)) {
					this.tagValue = TAG_SEE_VALUE;
					if (!this.inlineTagStarted) {
						valid = parseReference();
					}
				} else if (length == TAG_SERIAL_LENGTH && CharOperation.equals(TAG_SERIAL, tagName, 0, length)) {
					this.tagValue = TAG_SERIAL_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				} else if (length == TAG_SERIAL_DATA_LENGTH && CharOperation.equals(TAG_SERIAL_DATA, tagName, 0, length)) {
					this.tagValue = TAG_SERIAL_DATA_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				} else if (length == TAG_SERIAL_FIELD_LENGTH && CharOperation.equals(TAG_SERIAL_FIELD, tagName, 0, length)) {
					this.tagValue = TAG_SERIAL_FIELD_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				} else if (length == TAG_SINCE_LENGTH && CharOperation.equals(TAG_SINCE, tagName, 0, length)) {
					this.tagValue = TAG_SINCE_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				}					
				break;
			case 't':
				if (length == TAG_THROWS_LENGTH && CharOperation.equals(TAG_THROWS, tagName, 0, length)) {
					this.tagValue = TAG_THROWS_VALUE;
					if (!this.inlineTagStarted) {
						valid = parseThrows();
					}
				}
				break;
			case 'v':
				if (length == TAG_VALUE_LENGTH && CharOperation.equals(TAG_VALUE, tagName, 0, length)) {
					this.tagValue = TAG_VALUE_VALUE;
					if (this.sourceLevel >= ClassFileConstants.JDK1_5) {
						if (this.inlineTagStarted) {
							valid = parseReference();
						}
					} else {
						if (this.validValuePositions == -1) {
							if (this.invalidValuePositions != -1) {
								if (this.reportProblems) this.sourceParser.problemReporter().javadocUnexpectedTag((int) (this.invalidValuePositions>>>32), (int) this.invalidValuePositions);
							}
							if (valid) {
								this.validValuePositions = (((long) this.tagSourceStart) << 32) + this.tagSourceEnd;
								this.invalidValuePositions = -1;
							} else {
								this.invalidValuePositions = (((long) this.tagSourceStart) << 32) + this.tagSourceEnd;
							}
						} else {
							if (this.reportProblems) this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
						}
					}
				} else if (length == TAG_VERSION_LENGTH && CharOperation.equals(TAG_VERSION, tagName, 0, length)) {
					this.tagValue = TAG_VERSION_VALUE;
					this.tagWaitingForDescription = this.tagValue;
				} else {
					createTag();
				}
				break;
			default:
				createTag();
				break;
		}
		this.textStart = this.index;
		if (this.tagValue != TAG_OTHERS_VALUE) {
			if (!this.inlineTagStarted) {
				this.lastBlockTagValue = this.tagValue;
			}
			// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=267833
			// Report a problem if a block tag is being used in the context of an inline tag and vice versa.
			if ((this.inlineTagStarted && JAVADOC_TAG_TYPE[this.tagValue] == TAG_TYPE_BLOCK)
					|| (!this.inlineTagStarted && JAVADOC_TAG_TYPE[this.tagValue] == TAG_TYPE_INLINE)) {
				valid = false;
				this.tagValue = TAG_OTHERS_VALUE;
				this.tagWaitingForDescription = NO_TAG_VALUE;
				if (this.reportProblems) {
					this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
				}
			}
		}
		return valid;
	}

	protected void parseInheritDocTag() {
		// do nothing
	}

	/*
	 * Parse @param tag declaration and flag missing description if corresponding option is enabled
	 */
	protected boolean parseParam() throws InvalidInputException {
		boolean valid = super.parseParam();
		this.tagWaitingForDescription = valid && this.reportProblems ? TAG_PARAM_VALUE : NO_TAG_VALUE;
		return valid;
	}

	/*
	 * Push a param name in ast node stack.
	 */
	protected boolean pushParamName(boolean isTypeParam) {
		// Create param reference
		ASTNode nameRef = null;
		if (isTypeParam) {
			JavadocSingleTypeReference ref = new JavadocSingleTypeReference(this.identifierStack[1],
				this.identifierPositionStack[1],
				this.tagSourceStart,
				this.tagSourceEnd);
			nameRef = ref;
		} else {
			JavadocSingleNameReference ref = new JavadocSingleNameReference(this.identifierStack[0],
				this.identifierPositionStack[0],
				this.tagSourceStart,
				this.tagSourceEnd);
			nameRef = ref;
		}
		// Push ref on stack
		if (this.astLengthPtr == -1) { // First push
			pushOnAstStack(nameRef, true);
		} else {
			// Verify that no @throws has been declared before
			if (!isTypeParam) { // do not verify for type parameters as @throws may be invalid tag (when declared in class)
				for (int i=THROWS_TAG_EXPECTED_ORDER; i<=this.astLengthPtr; i+=ORDERED_TAGS_NUMBER) {
					if (this.astLengthStack[i] != 0) {
						if (this.reportProblems) this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
						// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=51600
						// store invalid param references in specific array
						if (this.invalidParamReferencesPtr == -1l) {
							this.invalidParamReferencesStack = new JavadocSingleNameReference[10];
						}
						int stackLength = this.invalidParamReferencesStack.length;
						if (++this.invalidParamReferencesPtr >= stackLength) {
							System.arraycopy(
								this.invalidParamReferencesStack, 0,
								this.invalidParamReferencesStack = new JavadocSingleNameReference[stackLength + AST_STACK_INCREMENT], 0,
								stackLength);
						}
						this.invalidParamReferencesStack[this.invalidParamReferencesPtr] = nameRef;
						return false;
					}
				}
			}
			switch (this.astLengthPtr % ORDERED_TAGS_NUMBER) {
				case PARAM_TAG_EXPECTED_ORDER :
					// previous push was a @param tag => push another param name
					pushOnAstStack(nameRef, false);
					break;
				case SEE_TAG_EXPECTED_ORDER :
					// previous push was a @see tag => push new param name
					pushOnAstStack(nameRef, true);
					break;
				default:
					return false;
			}
		}
		return true;
	}

	/*
	 * Push a reference statement in ast node stack.
	 */
	protected boolean pushSeeRef(Object statement) {
		if (this.astLengthPtr == -1) { // First push
			pushOnAstStack(null, true);
			pushOnAstStack(null, true);
			pushOnAstStack(statement, true);
		} else {
			switch (this.astLengthPtr % ORDERED_TAGS_NUMBER) {
				case PARAM_TAG_EXPECTED_ORDER :
					// previous push was a @param tag => push empty @throws tag and new @see tag
					pushOnAstStack(null, true);
					pushOnAstStack(statement, true);
					break;
				case THROWS_TAG_EXPECTED_ORDER :
					// previous push was a @throws tag => push new @see tag
					pushOnAstStack(statement, true);
					break;
				case SEE_TAG_EXPECTED_ORDER :
					// previous push was a @see tag => push another @see tag
					pushOnAstStack(statement, false);
					break;
				default:
					return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#pushText(int, int)
	 */
	protected void pushText(int start, int end) {
		// The tag gets its description => clear the flag
		this.tagWaitingForDescription = NO_TAG_VALUE;
	}

	/*
	 * Push a throws type ref in ast node stack.
	 */
	protected boolean pushThrowName(Object typeRef) {
		if (this.astLengthPtr == -1) { // First push
			pushOnAstStack(null, true);
			pushOnAstStack(typeRef, true);
		} else {
			switch (this.astLengthPtr % ORDERED_TAGS_NUMBER) {
				case PARAM_TAG_EXPECTED_ORDER :
					// previous push was a @param tag => push new @throws tag
					pushOnAstStack(typeRef, true);
					break;
				case THROWS_TAG_EXPECTED_ORDER :
					// previous push was a @throws tag => push another @throws tag
					pushOnAstStack(typeRef, false);
					break;
				case SEE_TAG_EXPECTED_ORDER :
					// previous push was a @see tag => push empty @param and new @throws tags
					pushOnAstStack(null, true);
					pushOnAstStack(typeRef, true);
					break;
				default:
					return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.parser.AbstractCommentParser#refreshInlineTagPosition(int)
	 */
	protected void refreshInlineTagPosition(int previousPosition) {

		// Signal tag missing description if necessary
		if (this.tagWaitingForDescription!= NO_TAG_VALUE) {
			this.sourceParser.problemReporter().javadocMissingTagDescription(TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
			this.tagWaitingForDescription = NO_TAG_VALUE;
		}
	}

	/*
	 * Refresh return statement
	 */
	protected void refreshReturnStatement() {
		((JavadocReturnStatement) this.returnStatement).bits &= ~ASTNode.Empty;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("check javadoc: ").append(this.checkDocComment).append("\n");	//$NON-NLS-1$ //$NON-NLS-2$
		buffer.append("javadoc: ").append(this.docComment).append("\n");	//$NON-NLS-1$ //$NON-NLS-2$
		buffer.append(super.toString());
		return buffer.toString();
	}

	/*
	 * Fill associated comment fields with ast nodes information stored in stack.
	 */
	protected void updateDocComment() {

		// Complain when tag is missing a description
		// Note that if the parse of an inline tag has already started, consider it
		// as the expected description, hence do not report any warning
		switch (this.tagWaitingForDescription) {
			case TAG_PARAM_VALUE:
			case TAG_THROWS_VALUE:
				if (!this.inlineTagStarted) {
					int start = (int) (this.identifierPositionStack[0] >>> 32);
					int end = (int) this.identifierPositionStack[this.identifierPtr];
					this.sourceParser.problemReporter().javadocMissingTagDescriptionAfterReference(start, end, this.sourceParser.modifiers);
				}
				break;
			case NO_TAG_VALUE:
				break;
			default:
				if (!this.inlineTagStarted) {
					this.sourceParser.problemReporter().javadocMissingTagDescription(TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
				}
				break;
		}
		this.tagWaitingForDescription = NO_TAG_VALUE;

		// Set positions
		if (this.inheritedPositions != null && this.inheritedPositionsPtr != this.inheritedPositions.length) {
			// Compact array by shrinking.
			System.arraycopy(this.inheritedPositions, 0, 
					this.inheritedPositions = new long[this.inheritedPositionsPtr], 0, this.inheritedPositionsPtr);
		}
		this.docComment.inheritedPositions = this.inheritedPositions;
		this.docComment.valuePositions = this.validValuePositions != -1 ? this.validValuePositions : this.invalidValuePositions;

		// Set return node if present
		if (this.returnStatement != null) {
			this.docComment.returnStatement = (JavadocReturnStatement) this.returnStatement;
		}

		// Copy array of invalid syntax param tags
		if (this.invalidParamReferencesPtr >= 0) {
			this.docComment.invalidParameters = new JavadocSingleNameReference[this.invalidParamReferencesPtr+1];
			System.arraycopy(this.invalidParamReferencesStack, 0, this.docComment.invalidParameters, 0, this.invalidParamReferencesPtr+1);
		}

		// If no nodes stored return
		if (this.astLengthPtr == -1) {
			return;
		}

		// Initialize arrays
		int[] sizes = new int[ORDERED_TAGS_NUMBER];
		for (int i=0; i<=this.astLengthPtr; i++) {
			sizes[i%ORDERED_TAGS_NUMBER] += this.astLengthStack[i];
		}
		this.docComment.seeReferences = new Expression[sizes[SEE_TAG_EXPECTED_ORDER]];
		this.docComment.exceptionReferences = new TypeReference[sizes[THROWS_TAG_EXPECTED_ORDER]];
		int paramRefPtr = sizes[PARAM_TAG_EXPECTED_ORDER];
		this.docComment.paramReferences = new JavadocSingleNameReference[paramRefPtr];
		int paramTypeParamPtr = sizes[PARAM_TAG_EXPECTED_ORDER];
		this.docComment.paramTypeParameters = new JavadocSingleTypeReference[paramTypeParamPtr];

		// Store nodes in arrays
		while (this.astLengthPtr >= 0) {
			int ptr = this.astLengthPtr % ORDERED_TAGS_NUMBER;
			// Starting with the stack top, so get references (Expression) coming from @see declarations
			switch(ptr) {
				case SEE_TAG_EXPECTED_ORDER:
					int size = this.astLengthStack[this.astLengthPtr--];
					for (int i=0; i<size; i++) {
						this.docComment.seeReferences[--sizes[ptr]] = (Expression) this.astStack[this.astPtr--];
					}
					break;

				// Then continuing with class names (TypeReference) coming from @throw/@exception declarations
				case THROWS_TAG_EXPECTED_ORDER:
					size = this.astLengthStack[this.astLengthPtr--];
					for (int i=0; i<size; i++) {
						this.docComment.exceptionReferences[--sizes[ptr]] = (TypeReference) this.astStack[this.astPtr--];
					}
					break;

				// Finally, finishing with parameters names (Argument) coming from @param declaration
				case PARAM_TAG_EXPECTED_ORDER:
					size = this.astLengthStack[this.astLengthPtr--];
					for (int i=0; i<size; i++) {
						Expression reference = (Expression) this.astStack[this.astPtr--];
						if (reference instanceof JavadocSingleNameReference)
							this.docComment.paramReferences[--paramRefPtr] = (JavadocSingleNameReference) reference;
						else if (reference instanceof JavadocSingleTypeReference)
							this.docComment.paramTypeParameters[--paramTypeParamPtr] = (JavadocSingleTypeReference) reference;
					}
					break;
			}
		}

		// Resize param tag references arrays
		if (paramRefPtr == 0) { // there's no type parameters references
			this.docComment.paramTypeParameters = null;
		} else if (paramTypeParamPtr == 0) { // there's no names references
			this.docComment.paramReferences = null;
		} else { // there both of references => resize arrays
			int size = sizes[PARAM_TAG_EXPECTED_ORDER];
			System.arraycopy(this.docComment.paramReferences, paramRefPtr, this.docComment.paramReferences = new JavadocSingleNameReference[size - paramRefPtr], 0, size - paramRefPtr);
			System.arraycopy(this.docComment.paramTypeParameters, paramTypeParamPtr, this.docComment.paramTypeParameters = new JavadocSingleTypeReference[size - paramTypeParamPtr], 0, size - paramTypeParamPtr);
		}
	}
}
