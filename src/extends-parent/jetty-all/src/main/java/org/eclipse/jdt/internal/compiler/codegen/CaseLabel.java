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
package org.eclipse.jdt.internal.compiler.codegen;

public class CaseLabel extends BranchLabel {

	public int instructionPosition = POS_NOT_SET;

/**
 * CaseLabel constructor comment.
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 */
public CaseLabel(CodeStream codeStream) {
	super(codeStream);
}

/*
* Put down  a reference to the array at the location in the codestream.
* #placeInstruction() must be performed prior to any #branch()
*/
void branch() {
	if (this.position == POS_NOT_SET) {
		addForwardReference(this.codeStream.position);
		// Leave 4 bytes free to generate the jump offset afterwards
		this.codeStream.position += 4;
		this.codeStream.classFileOffset += 4;
	} else { //Position is set. Write it!
		/*
		 * Position is set. Write it if it is not a wide branch.
		 */
		this.codeStream.writeSignedWord(this.position - this.instructionPosition);
	}
}

/*
* No support for wide branches yet
*/
void branchWide() {
	branch(); // case label branch is already wide
}

public boolean isCaseLabel() {
	return true;
}
public boolean isStandardLabel(){
	return false;
}
/*
* Put down  a reference to the array at the location in the codestream.
*/
public void place() {
	if ((this.tagBits & USED) != 0) {
		this.position = this.codeStream.getPosition();
	} else {
		this.position = this.codeStream.position;
	}
	if (this.instructionPosition != POS_NOT_SET) {
		int offset = this.position - this.instructionPosition;
		int[] forwardRefs = forwardReferences();
		for (int i = 0, length = forwardReferenceCount(); i < length; i++) {
			this.codeStream.writeSignedWord(forwardRefs[i], offset);
		}
		// add the label in the codeStream labels collection
		this.codeStream.addLabel(this);
	}
}

/*
* Put down  a reference to the array at the location in the codestream.
*/
void placeInstruction() {
	if (this.instructionPosition == POS_NOT_SET) {
		this.instructionPosition = this.codeStream.position;
	}
}
}
