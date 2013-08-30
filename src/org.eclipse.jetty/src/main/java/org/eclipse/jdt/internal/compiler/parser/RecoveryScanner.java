/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class RecoveryScanner extends Scanner {
	public static final char[] FAKE_IDENTIFIER = "$missing$".toCharArray(); //$NON-NLS-1$

	private RecoveryScannerData data;

	private int[] pendingTokens;
	private int pendingTokensPtr = -1;
	private char[] fakeTokenSource = null;
	private boolean isInserted = true;
	private boolean precededByRemoved = false;
	private int skipNextInsertedTokens = -1;

	public boolean record = true;

	public RecoveryScanner(Scanner scanner, RecoveryScannerData data) {
		super(false,
				scanner.tokenizeWhiteSpace,
				scanner.checkNonExternalizedStringLiterals,
				scanner.sourceLevel,
				scanner.complianceLevel,
				scanner.taskTags,
				scanner.taskPriorities,
				scanner.isTaskCaseSensitive);
		setData(data);
	}
	
	public RecoveryScanner(
			boolean tokenizeWhiteSpace,
			boolean checkNonExternalizedStringLiterals,
			long sourceLevel,
			long complianceLevel,
			char[][] taskTags,
			char[][] taskPriorities,
			boolean isTaskCaseSensitive,
			RecoveryScannerData data) {
		super(false,
				tokenizeWhiteSpace,
				checkNonExternalizedStringLiterals,
				sourceLevel,
				complianceLevel,
				taskTags,
				taskPriorities,
				isTaskCaseSensitive);
		setData(data);
	}

	public void insertToken(int token, int completedToken, int position) {
		insertTokens(new int []{token}, completedToken, position);
	}

	private int[] reverse(int[] tokens) {
		int length = tokens.length;
		for(int i = 0, max = length / 2; i < max; i++) {
			int tmp = tokens[i];
			tokens[i] = tokens[length - i - 1];
			tokens[length - i - 1] = tmp;
		}
		return tokens;
	}
	public void insertTokens(int[] tokens, int completedToken, int position) {
		if(!this.record) return;

		if(completedToken > -1 && Parser.statements_recovery_filter[completedToken] != 0) return;

		this.data.insertedTokensPtr++;
		if(this.data.insertedTokens == null) {
			this.data.insertedTokens = new int[10][];
			this.data.insertedTokensPosition = new int[10];
			this.data.insertedTokenUsed = new boolean[10];
		} else if(this.data.insertedTokens.length == this.data.insertedTokensPtr) {
			int length = this.data.insertedTokens.length;
			System.arraycopy(this.data.insertedTokens, 0, this.data.insertedTokens = new int[length * 2][], 0, length);
			System.arraycopy(this.data.insertedTokensPosition, 0, this.data.insertedTokensPosition = new int[length * 2], 0, length);
			System.arraycopy(this.data.insertedTokenUsed, 0, this.data.insertedTokenUsed = new boolean[length * 2], 0, length);
		}
		this.data.insertedTokens[this.data.insertedTokensPtr] = reverse(tokens);
		this.data.insertedTokensPosition[this.data.insertedTokensPtr] = position;
		this.data.insertedTokenUsed[this.data.insertedTokensPtr] = false;
	}

	public void replaceTokens(int token, int start, int end) {
		replaceTokens(new int []{token}, start, end);
	}

	public void replaceTokens(int[] tokens, int start, int end) {
		if(!this.record) return;
		this.data.replacedTokensPtr++;
		if(this.data.replacedTokensStart == null) {
			this.data.replacedTokens = new int[10][];
			this.data.replacedTokensStart = new int[10];
			this.data.replacedTokensEnd = new int[10];
			this.data.replacedTokenUsed= new boolean[10];
		} else if(this.data.replacedTokensStart.length == this.data.replacedTokensPtr) {
			int length = this.data.replacedTokensStart.length;
			System.arraycopy(this.data.replacedTokens, 0, this.data.replacedTokens = new int[length * 2][], 0, length);
			System.arraycopy(this.data.replacedTokensStart, 0, this.data.replacedTokensStart = new int[length * 2], 0, length);
			System.arraycopy(this.data.replacedTokensEnd, 0, this.data.replacedTokensEnd = new int[length * 2], 0, length);
			System.arraycopy(this.data.replacedTokenUsed, 0, this.data.replacedTokenUsed = new boolean[length * 2], 0, length);
		}
		this.data.replacedTokens[this.data.replacedTokensPtr] = reverse(tokens);
		this.data.replacedTokensStart[this.data.replacedTokensPtr] = start;
		this.data.replacedTokensEnd[this.data.replacedTokensPtr] = end;
		this.data.replacedTokenUsed[this.data.replacedTokensPtr] = false;
	}

	public void removeTokens(int start, int end) {
		if(!this.record) return;
		this.data.removedTokensPtr++;
		if(this.data.removedTokensStart == null) {
			this.data.removedTokensStart = new int[10];
			this.data.removedTokensEnd = new int[10];
			this.data.removedTokenUsed = new boolean[10];
		} else if(this.data.removedTokensStart.length == this.data.removedTokensPtr) {
			int length = this.data.removedTokensStart.length;
			System.arraycopy(this.data.removedTokensStart, 0, this.data.removedTokensStart = new int[length * 2], 0, length);
			System.arraycopy(this.data.removedTokensEnd, 0, this.data.removedTokensEnd = new int[length * 2], 0, length);
			System.arraycopy(this.data.removedTokenUsed, 0, this.data.removedTokenUsed = new boolean[length * 2], 0, length);
		}
		this.data.removedTokensStart[this.data.removedTokensPtr] = start;
		this.data.removedTokensEnd[this.data.removedTokensPtr] = end;
		this.data.removedTokenUsed[this.data.removedTokensPtr] = false;
	}

	public int getNextToken() throws InvalidInputException {
		if(this.pendingTokensPtr > -1) {
			int nextToken = this.pendingTokens[this.pendingTokensPtr--];
			if(nextToken == TerminalTokens.TokenNameIdentifier){
				this.fakeTokenSource = FAKE_IDENTIFIER;
			} else {
				this.fakeTokenSource = CharOperation.NO_CHAR;
			}
			return nextToken;
		}

		this.fakeTokenSource = null;
		this.precededByRemoved = false;

		if(this.data.insertedTokens != null) {
			for (int i = 0; i <= this.data.insertedTokensPtr; i++) {
				if(this.data.insertedTokensPosition[i] == this.currentPosition - 1 && i > this.skipNextInsertedTokens) {
					this.data.insertedTokenUsed[i] = true;
					this.pendingTokens = this.data.insertedTokens[i];
					this.pendingTokensPtr = this.data.insertedTokens[i].length - 1;
					this.isInserted = true;
					this.startPosition = this.currentPosition;
					this.skipNextInsertedTokens = i;
					int nextToken = this.pendingTokens[this.pendingTokensPtr--];
					if(nextToken == TerminalTokens.TokenNameIdentifier){
						this.fakeTokenSource = FAKE_IDENTIFIER;
					} else {
						this.fakeTokenSource = CharOperation.NO_CHAR;
					}
					return nextToken;
				}
			}
			this.skipNextInsertedTokens = -1;
		}

		int previousLocation = this.currentPosition;
		int currentToken = super.getNextToken();

		if(this.data.replacedTokens != null) {
			for (int i = 0; i <= this.data.replacedTokensPtr; i++) {
				if(this.data.replacedTokensStart[i] >= previousLocation &&
						this.data.replacedTokensStart[i] <= this.startPosition &&
						this.data.replacedTokensEnd[i] >= this.currentPosition - 1) {
					this.data.replacedTokenUsed[i] = true;
					this.pendingTokens = this.data.replacedTokens[i];
					this.pendingTokensPtr = this.data.replacedTokens[i].length - 1;
					this.fakeTokenSource = FAKE_IDENTIFIER;
					this.isInserted = false;
					this.currentPosition = this.data.replacedTokensEnd[i] + 1;
					int nextToken = this.pendingTokens[this.pendingTokensPtr--];
					if(nextToken == TerminalTokens.TokenNameIdentifier){
						this.fakeTokenSource = FAKE_IDENTIFIER;
					} else {
						this.fakeTokenSource = CharOperation.NO_CHAR;
					}
					return nextToken;
				}
			}
		}
		if(this.data.removedTokensStart != null) {
			for (int i = 0; i <= this.data.removedTokensPtr; i++) {
				if(this.data.removedTokensStart[i] >= previousLocation &&
						this.data.removedTokensStart[i] <= this.startPosition &&
						this.data.removedTokensEnd[i] >= this.currentPosition - 1) {
					this.data.removedTokenUsed[i] = true;
					this.currentPosition = this.data.removedTokensEnd[i] + 1;
					this.precededByRemoved = false;
					return getNextToken();
				}
			}
		}
		return currentToken;
	}

	public char[] getCurrentIdentifierSource() {
		if(this.fakeTokenSource != null) return this.fakeTokenSource;
		return super.getCurrentIdentifierSource();
	}

	public char[] getCurrentTokenSourceString() {
		if(this.fakeTokenSource != null) return this.fakeTokenSource;
		return super.getCurrentTokenSourceString();
	}

	public char[] getCurrentTokenSource() {
		if(this.fakeTokenSource != null) return this.fakeTokenSource;
		return super.getCurrentTokenSource();
	}

	public RecoveryScannerData getData() {
		return this.data;
	}

	public boolean isFakeToken() {
		return this.fakeTokenSource != null;
	}

	public boolean isInsertedToken() {
		return this.fakeTokenSource != null && this.isInserted;
	}

	public boolean isReplacedToken() {
		return this.fakeTokenSource != null && !this.isInserted;
	}

	public boolean isPrecededByRemovedToken() {
		return this.precededByRemoved;
	}

	public void setData(RecoveryScannerData data) {
		if(data == null) {
			this.data = new RecoveryScannerData();
		} else {
			this.data = data;
		}
	}

	public void setPendingTokens(int[] pendingTokens) {
		this.pendingTokens = pendingTokens;
		this.pendingTokensPtr = pendingTokens.length - 1;
	}
}
