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
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

/**
 * Internal type structure for parsing recovery
 */

public class RecoveredType extends RecoveredStatement implements TerminalTokens {
	public static final int MAX_TYPE_DEPTH = 256;
	
	public TypeDeclaration typeDeclaration;

	public RecoveredAnnotation[] annotations;
	public int annotationCount;

	public int modifiers;
	public int modifiersStart;

	public RecoveredType[] memberTypes;
	public int memberTypeCount;
	public RecoveredField[] fields;
	public int fieldCount;
	public RecoveredMethod[] methods;
	public int methodCount;

	public boolean preserveContent = false;	// only used for anonymous types
	public int bodyEnd;

	public boolean insideEnumConstantPart = false;

	public TypeParameter[] pendingTypeParameters;
	public int pendingTypeParametersStart;

	int pendingModifiers;
	int pendingModifersSourceStart = -1;
	RecoveredAnnotation[] pendingAnnotations;
	int pendingAnnotationCount;

public RecoveredType(TypeDeclaration typeDeclaration, RecoveredElement parent, int bracketBalance){
	super(typeDeclaration, parent, bracketBalance);
	this.typeDeclaration = typeDeclaration;
	if(typeDeclaration.allocation != null && typeDeclaration.allocation.type == null) {
		// an enum constant body can not exist if there is no opening brace
		this.foundOpeningBrace = true;
	} else {
		this.foundOpeningBrace = !bodyStartsAtHeaderEnd();
	}
	this.insideEnumConstantPart = TypeDeclaration.kind(typeDeclaration.modifiers) == TypeDeclaration.ENUM_DECL;
	if(this.foundOpeningBrace) {
		this.bracketBalance++;
	}

	this.preserveContent = parser().methodRecoveryActivated || parser().statementRecoveryActivated;
}
public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue) {

	/* do not consider a method starting passed the type end (if set)
		it must be belonging to an enclosing type */
	if (this.typeDeclaration.declarationSourceEnd != 0
		&& methodDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd){
		this.pendingTypeParameters = null;
		resetPendingModifiers();

		return this.parent.add(methodDeclaration, bracketBalanceValue);
	}

	if (this.methods == null) {
		this.methods = new RecoveredMethod[5];
		this.methodCount = 0;
	} else {
		if (this.methodCount == this.methods.length) {
			System.arraycopy(
				this.methods,
				0,
				(this.methods = new RecoveredMethod[2 * this.methodCount]),
				0,
				this.methodCount);
		}
	}
	RecoveredMethod element = new RecoveredMethod(methodDeclaration, this, bracketBalanceValue, this.recoveringParser);
	this.methods[this.methodCount++] = element;

	if(this.pendingTypeParameters != null) {
		element.attach(this.pendingTypeParameters, this.pendingTypeParametersStart);
		this.pendingTypeParameters = null;
	}

	if(this.pendingAnnotationCount > 0) {
		element.attach(
				this.pendingAnnotations,
				this.pendingAnnotationCount,
				this.pendingModifiers,
				this.pendingModifersSourceStart);
	}
	resetPendingModifiers();

	this.insideEnumConstantPart = false;

	/* consider that if the opening brace was not found, it is there */
	if (!this.foundOpeningBrace){
		this.foundOpeningBrace = true;
		this.bracketBalance++;
	}
	/* if method not finished, then method becomes current */
	if (methodDeclaration.declarationSourceEnd == 0) return element;
	return this;
}
public RecoveredElement add(Block nestedBlockDeclaration,int bracketBalanceValue) {
	this.pendingTypeParameters = null;
	resetPendingModifiers();

	int mods = ClassFileConstants.AccDefault;
	if(parser().recoveredStaticInitializerStart != 0) {
		mods = ClassFileConstants.AccStatic;
	}
	return this.add(new Initializer(nestedBlockDeclaration, mods), bracketBalanceValue);
}
public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue) {
	this.pendingTypeParameters = null;

	/* do not consider a field starting passed the type end (if set)
	it must be belonging to an enclosing type */
	if (this.typeDeclaration.declarationSourceEnd != 0
		&& fieldDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {

		resetPendingModifiers();

		return this.parent.add(fieldDeclaration, bracketBalanceValue);
	}
	if (this.fields == null) {
		this.fields = new RecoveredField[5];
		this.fieldCount = 0;
	} else {
		if (this.fieldCount == this.fields.length) {
			System.arraycopy(
				this.fields,
				0,
				(this.fields = new RecoveredField[2 * this.fieldCount]),
				0,
				this.fieldCount);
		}
	}
	RecoveredField element;
	switch (fieldDeclaration.getKind()) {
		case AbstractVariableDeclaration.FIELD:
		case AbstractVariableDeclaration.ENUM_CONSTANT:
			element = new RecoveredField(fieldDeclaration, this, bracketBalanceValue);
			break;
		case AbstractVariableDeclaration.INITIALIZER:
			element = new RecoveredInitializer(fieldDeclaration, this, bracketBalanceValue);
			break;
		default:
			// never happens, as field is always identified
			return this;
	}
	this.fields[this.fieldCount++] = element;

	if(this.pendingAnnotationCount > 0) {
		element.attach(
				this.pendingAnnotations,
				this.pendingAnnotationCount,
				this.pendingModifiers,
				this.pendingModifersSourceStart);
	}
	resetPendingModifiers();

	/* consider that if the opening brace was not found, it is there */
	if (!this.foundOpeningBrace){
		this.foundOpeningBrace = true;
		this.bracketBalance++;
	}
	/* if field not finished, then field becomes current */
	if (fieldDeclaration.declarationSourceEnd == 0) return element;
	return this;
}
public RecoveredElement add(TypeDeclaration memberTypeDeclaration, int bracketBalanceValue) {
	this.pendingTypeParameters = null;

	/* do not consider a type starting passed the type end (if set)
		it must be belonging to an enclosing type */
	if (this.typeDeclaration.declarationSourceEnd != 0
		&& memberTypeDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd){

		resetPendingModifiers();

		return this.parent.add(memberTypeDeclaration, bracketBalanceValue);
	}

	this.insideEnumConstantPart = false;

	if ((memberTypeDeclaration.bits & ASTNode.IsAnonymousType) != 0){
		if (this.methodCount > 0) {
			// add it to the last method body
			RecoveredMethod lastMethod = this.methods[this.methodCount-1];
			lastMethod.methodDeclaration.bodyEnd = 0; // reopen method
			lastMethod.methodDeclaration.declarationSourceEnd = 0; // reopen method
			lastMethod.bracketBalance++; // expect one closing brace

			resetPendingModifiers();

			return lastMethod.add(memberTypeDeclaration, bracketBalanceValue);
		} else {
			// ignore
			return this;
		}
	}

	if (this.memberTypes == null) {
		this.memberTypes = new RecoveredType[5];
		this.memberTypeCount = 0;
	} else {
		if (this.memberTypeCount == this.memberTypes.length) {
			System.arraycopy(
				this.memberTypes,
				0,
				(this.memberTypes = new RecoveredType[2 * this.memberTypeCount]),
				0,
				this.memberTypeCount);
		}
	}
	RecoveredType element = new RecoveredType(memberTypeDeclaration, this, bracketBalanceValue);
	this.memberTypes[this.memberTypeCount++] = element;

	if(this.pendingAnnotationCount > 0) {
		element.attach(
				this.pendingAnnotations,
				this.pendingAnnotationCount,
				this.pendingModifiers,
				this.pendingModifersSourceStart);
	}
	resetPendingModifiers();

	/* consider that if the opening brace was not found, it is there */
	if (!this.foundOpeningBrace){
		this.foundOpeningBrace = true;
		this.bracketBalance++;
	}
	/* if member type not finished, then member type becomes current */
	if (memberTypeDeclaration.declarationSourceEnd == 0) return element;
	return this;
}
public void add(TypeParameter[] parameters, int startPos) {
	this.pendingTypeParameters = parameters;
	this.pendingTypeParametersStart = startPos;
}
public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
	if (this.pendingAnnotations == null) {
		this.pendingAnnotations = new RecoveredAnnotation[5];
		this.pendingAnnotationCount = 0;
	} else {
		if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
			System.arraycopy(
				this.pendingAnnotations,
				0,
				(this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount]),
				0,
				this.pendingAnnotationCount);
		}
	}

	RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);

	this.pendingAnnotations[this.pendingAnnotationCount++] = element;

	return element;
}
public void addModifier(int flag, int modifiersSourceStart) {
	this.pendingModifiers |= flag;

	if (this.pendingModifersSourceStart < 0) {
		this.pendingModifersSourceStart = modifiersSourceStart;
	}
}
public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
	if (annotCount > 0) {
		Annotation[] existingAnnotations = this.typeDeclaration.annotations;
		if (existingAnnotations != null) {
			this.annotations = new RecoveredAnnotation[annotCount];
			this.annotationCount = 0;
			next : for (int i = 0; i < annotCount; i++) {
				for (int j = 0; j < existingAnnotations.length; j++) {
					if (annots[i].annotation == existingAnnotations[j]) continue next;
				}
				this.annotations[this.annotationCount++] = annots[i];
			}
		} else {
			this.annotations = annots;
			this.annotationCount = annotCount;
		}
	}

	if (mods != 0) {
		this.modifiers = mods;
		this.modifiersStart = modsSourceStart;
	}
}
/*
 * Answer the body end of the corresponding parse node
 */
public int bodyEnd(){
	if (this.bodyEnd == 0) return this.typeDeclaration.declarationSourceEnd;
	return this.bodyEnd;
}
public boolean bodyStartsAtHeaderEnd(){
	if (this.typeDeclaration.superInterfaces == null){
		if (this.typeDeclaration.superclass == null){
			if(this.typeDeclaration.typeParameters == null) {
				return this.typeDeclaration.bodyStart == this.typeDeclaration.sourceEnd+1;
			} else {
				return this.typeDeclaration.bodyStart == this.typeDeclaration.typeParameters[this.typeDeclaration.typeParameters.length-1].sourceEnd+1;
			}
		} else {
			return this.typeDeclaration.bodyStart == this.typeDeclaration.superclass.sourceEnd+1;
		}
	} else {
		return this.typeDeclaration.bodyStart
				== this.typeDeclaration.superInterfaces[this.typeDeclaration.superInterfaces.length-1].sourceEnd+1;
	}
}
/*
 * Answer the enclosing type node, or null if none
 */
public RecoveredType enclosingType(){
	RecoveredElement current = this.parent;
	while (current != null){
		if (current instanceof RecoveredType){
			return (RecoveredType) current;
		}
		current = current.parent;
	}
	return null;
}
public int lastMemberEnd() {
	int lastMemberEnd = this.typeDeclaration.bodyStart;

	if (this.fieldCount > 0) {
		FieldDeclaration lastField = this.fields[this.fieldCount - 1].fieldDeclaration;
		if (lastMemberEnd < lastField.declarationSourceEnd && lastField.declarationSourceEnd != 0) {
			lastMemberEnd = lastField.declarationSourceEnd;
		}
	}

	if (this.methodCount > 0) {
		AbstractMethodDeclaration lastMethod = this.methods[this.methodCount - 1].methodDeclaration;
		if (lastMemberEnd < lastMethod.declarationSourceEnd && lastMethod.declarationSourceEnd != 0) {
			lastMemberEnd = lastMethod.declarationSourceEnd;
		}
	}

	if (this.memberTypeCount > 0) {
		TypeDeclaration lastType = this.memberTypes[this.memberTypeCount - 1].typeDeclaration;
		if (lastMemberEnd < lastType.declarationSourceEnd && lastType.declarationSourceEnd != 0) {
			lastMemberEnd = lastType.declarationSourceEnd;
		}
	}

	return lastMemberEnd;
}
public char[] name(){
	return this.typeDeclaration.name;
}
/*
 * Answer the associated parsed structure
 */
public ASTNode parseTree(){
	return this.typeDeclaration;
}
public void resetPendingModifiers() {
	this.pendingAnnotations = null;
	this.pendingAnnotationCount = 0;
	this.pendingModifiers = 0;
	this.pendingModifersSourceStart = -1;
}
/*
 * Answer the very source end of the corresponding parse node
 */
public int sourceEnd(){
	return this.typeDeclaration.declarationSourceEnd;
}
public String toString(int tab) {
	StringBuffer result = new StringBuffer(tabString(tab));
	result.append("Recovered type:\n"); //$NON-NLS-1$
	if ((this.typeDeclaration.bits & ASTNode.IsAnonymousType) != 0) {
		result.append(tabString(tab));
		result.append(" "); //$NON-NLS-1$
	}
	this.typeDeclaration.print(tab + 1, result);
	if (this.annotations != null) {
		for (int i = 0; i < this.annotationCount; i++) {
			result.append("\n"); //$NON-NLS-1$
			result.append(this.annotations[i].toString(tab + 1));
		}
	}
	if (this.memberTypes != null) {
		for (int i = 0; i < this.memberTypeCount; i++) {
			result.append("\n"); //$NON-NLS-1$
			result.append(this.memberTypes[i].toString(tab + 1));
		}
	}
	if (this.fields != null) {
		for (int i = 0; i < this.fieldCount; i++) {
			result.append("\n"); //$NON-NLS-1$
			result.append(this.fields[i].toString(tab + 1));
		}
	}
	if (this.methods != null) {
		for (int i = 0; i < this.methodCount; i++) {
			result.append("\n"); //$NON-NLS-1$
			result.append(this.methods[i].toString(tab + 1));
		}
	}
	return result.toString();
}
/*
 * Update the bodyStart of the corresponding parse node
 */
public void updateBodyStart(int bodyStart){
	this.foundOpeningBrace = true;
	this.typeDeclaration.bodyStart = bodyStart;
}
public Statement updatedStatement(int depth, Set knownTypes){

	// ignore closed anonymous type
	if ((this.typeDeclaration.bits & ASTNode.IsAnonymousType) != 0 && !this.preserveContent){
		return null;
	}

	TypeDeclaration updatedType = updatedTypeDeclaration(depth + 1, knownTypes);
	if (updatedType != null && (updatedType.bits & ASTNode.IsAnonymousType) != 0){
		/* in presence of an anonymous type, we want the full allocation expression */
		QualifiedAllocationExpression allocation = updatedType.allocation;

		if (allocation.statementEnd == -1) {
			allocation.statementEnd = updatedType.declarationSourceEnd;
		}
		return allocation;
	}
	return updatedType;
}
public TypeDeclaration updatedTypeDeclaration(int depth, Set knownTypes){
	if (depth >= MAX_TYPE_DEPTH) return null;

	if(knownTypes.contains(this.typeDeclaration)) return null;
	knownTypes.add(this.typeDeclaration);
	
	int lastEnd = this.typeDeclaration.bodyStart;
	/* update annotations */
	if (this.modifiers != 0) {
		this.typeDeclaration.modifiers |= this.modifiers;
		if (this.modifiersStart < this.typeDeclaration.declarationSourceStart) {
			this.typeDeclaration.declarationSourceStart = this.modifiersStart;
		}
	}
	/* update annotations */
	if (this.annotationCount > 0){
		int existingCount = this.typeDeclaration.annotations == null ? 0 : this.typeDeclaration.annotations.length;
		Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
		if (existingCount > 0){
			System.arraycopy(this.typeDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
		}
		for (int i = 0; i < this.annotationCount; i++){
			annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
		}
		this.typeDeclaration.annotations = annotationReferences;

		int start = this.annotations[0].annotation.sourceStart;
		if (start < this.typeDeclaration.declarationSourceStart) {
			this.typeDeclaration.declarationSourceStart = start;
		}
	}
	/* update member types */
	if (this.memberTypeCount > 0){
		int existingCount = this.typeDeclaration.memberTypes == null ? 0 : this.typeDeclaration.memberTypes.length;
		TypeDeclaration[] memberTypeDeclarations = new TypeDeclaration[existingCount + this.memberTypeCount];
		if (existingCount > 0){
			System.arraycopy(this.typeDeclaration.memberTypes, 0, memberTypeDeclarations, 0, existingCount);
		}
		// may need to update the declarationSourceEnd of the last type
		if (this.memberTypes[this.memberTypeCount - 1].typeDeclaration.declarationSourceEnd == 0){
			int bodyEndValue = bodyEnd();
			this.memberTypes[this.memberTypeCount - 1].typeDeclaration.declarationSourceEnd = bodyEndValue;
			this.memberTypes[this.memberTypeCount - 1].typeDeclaration.bodyEnd =  bodyEndValue;
		}
		
		int updatedCount = 0;
		for (int i = 0; i < this.memberTypeCount; i++){
			TypeDeclaration updatedTypeDeclaration = this.memberTypes[i].updatedTypeDeclaration(depth + 1, knownTypes);
			if (updatedTypeDeclaration != null) {
				memberTypeDeclarations[existingCount + (updatedCount++)] = updatedTypeDeclaration;
			}
		}
		if (updatedCount < this.memberTypeCount) {
			int length = existingCount + updatedCount;
			System.arraycopy(memberTypeDeclarations, 0, memberTypeDeclarations = new TypeDeclaration[length], 0, length);
		}
		
		if (memberTypeDeclarations.length > 0) { 
			this.typeDeclaration.memberTypes = memberTypeDeclarations;
			if(memberTypeDeclarations[memberTypeDeclarations.length - 1].declarationSourceEnd > lastEnd) {
				lastEnd = memberTypeDeclarations[memberTypeDeclarations.length - 1].declarationSourceEnd;
			}
		}
	}
	/* update fields */
	if (this.fieldCount > 0){
		int existingCount = this.typeDeclaration.fields == null ? 0 : this.typeDeclaration.fields.length;
		FieldDeclaration[] fieldDeclarations = new FieldDeclaration[existingCount + this.fieldCount];
		if (existingCount > 0){
			System.arraycopy(this.typeDeclaration.fields, 0, fieldDeclarations, 0, existingCount);
		}
		// may need to update the declarationSourceEnd of the last field
		if (this.fields[this.fieldCount - 1].fieldDeclaration.declarationSourceEnd == 0){
			int temp = bodyEnd();
			this.fields[this.fieldCount - 1].fieldDeclaration.declarationSourceEnd = temp;
			this.fields[this.fieldCount - 1].fieldDeclaration.declarationEnd = temp;
		}
		for (int i = 0; i < this.fieldCount; i++){
			fieldDeclarations[existingCount + i] = this.fields[i].updatedFieldDeclaration(depth, knownTypes);
		}
		
		for (int i = this.fieldCount - 1; 0 < i; i--) {
			if (fieldDeclarations[existingCount + i - 1].declarationSourceStart == fieldDeclarations[existingCount + i].declarationSourceStart) {
				fieldDeclarations[existingCount + i - 1].declarationSourceEnd = fieldDeclarations[existingCount + i].declarationSourceEnd;
				fieldDeclarations[existingCount + i - 1].declarationEnd = fieldDeclarations[existingCount + i].declarationEnd;
			}
		}
		
		this.typeDeclaration.fields = fieldDeclarations;
		if(fieldDeclarations[fieldDeclarations.length - 1].declarationSourceEnd > lastEnd) {
			lastEnd = fieldDeclarations[fieldDeclarations.length - 1].declarationSourceEnd;
		}
	}
	/* update methods */
	int existingCount = this.typeDeclaration.methods == null ? 0 : this.typeDeclaration.methods.length;
	boolean hasConstructor = false, hasRecoveredConstructor = false;
	boolean hasAbstractMethods = false;
	int defaultConstructorIndex = -1;
	if (this.methodCount > 0){
		AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[existingCount + this.methodCount];
		for (int i = 0; i < existingCount; i++){
			AbstractMethodDeclaration m = this.typeDeclaration.methods[i];
			if (m.isDefaultConstructor()) defaultConstructorIndex = i;
			if (m.isAbstract()) hasAbstractMethods = true;
			methodDeclarations[i] = m;
		}
		// may need to update the declarationSourceEnd of the last method
		if (this.methods[this.methodCount - 1].methodDeclaration.declarationSourceEnd == 0){
			int bodyEndValue = bodyEnd();
			this.methods[this.methodCount - 1].methodDeclaration.declarationSourceEnd = bodyEndValue;
			this.methods[this.methodCount - 1].methodDeclaration.bodyEnd = bodyEndValue;
		}
		for (int i = 0; i < this.methodCount; i++){
			AbstractMethodDeclaration updatedMethod = this.methods[i].updatedMethodDeclaration(depth, knownTypes);
			if (updatedMethod.isConstructor()) hasRecoveredConstructor = true;
			if (updatedMethod.isAbstract()) hasAbstractMethods = true;
			methodDeclarations[existingCount + i] = updatedMethod;
		}
		this.typeDeclaration.methods = methodDeclarations;
		if(methodDeclarations[methodDeclarations.length - 1].declarationSourceEnd > lastEnd) {
			lastEnd = methodDeclarations[methodDeclarations.length - 1].declarationSourceEnd;
		}
		if (hasAbstractMethods) this.typeDeclaration.bits |= ASTNode.HasAbstractMethods;
		hasConstructor = this.typeDeclaration.checkConstructors(parser());
	} else {
		for (int i = 0; i < existingCount; i++){
			if (this.typeDeclaration.methods[i].isConstructor()) hasConstructor = true;
		}
	}
	/* add clinit ? */
	if (this.typeDeclaration.needClassInitMethod()){
		boolean alreadyHasClinit = false;
		for (int i = 0; i < existingCount; i++){
			if (this.typeDeclaration.methods[i].isClinit()){
				alreadyHasClinit = true;
				break;
			}
		}
		if (!alreadyHasClinit) this.typeDeclaration.addClinit();
	}
	/* add default constructor ? */
	if (defaultConstructorIndex >= 0 && hasRecoveredConstructor){
		/* should discard previous default construtor */
		AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[this.typeDeclaration.methods.length - 1];
		if (defaultConstructorIndex != 0){
			System.arraycopy(this.typeDeclaration.methods, 0, methodDeclarations, 0, defaultConstructorIndex);
		}
		if (defaultConstructorIndex != this.typeDeclaration.methods.length-1){
			System.arraycopy(
				this.typeDeclaration.methods,
				defaultConstructorIndex+1,
				methodDeclarations,
				defaultConstructorIndex,
				this.typeDeclaration.methods.length - defaultConstructorIndex - 1);
		}
		this.typeDeclaration.methods = methodDeclarations;
	} else {
		int kind = TypeDeclaration.kind(this.typeDeclaration.modifiers);
		if (!hasConstructor &&
				kind != TypeDeclaration.INTERFACE_DECL &&
				kind != TypeDeclaration.ANNOTATION_TYPE_DECL &&
				this.typeDeclaration.allocation == null) {// if was already reduced, then constructor
			boolean insideFieldInitializer = false;
			RecoveredElement parentElement = this.parent;
			while (parentElement != null){
				if (parentElement instanceof RecoveredField){
						insideFieldInitializer = true;
						break;
				}
				parentElement = parentElement.parent;
			}
			this.typeDeclaration.createDefaultConstructor(!parser().diet || insideFieldInitializer, true);
		}
	}
	if (this.parent instanceof RecoveredType){
		this.typeDeclaration.bits |= ASTNode.IsMemberType;
	} else if (this.parent instanceof RecoveredMethod){
		this.typeDeclaration.bits |= ASTNode.IsLocalType;
	}
	if(this.typeDeclaration.declarationSourceEnd == 0) {
		this.typeDeclaration.declarationSourceEnd = lastEnd;
		this.typeDeclaration.bodyEnd = lastEnd;
	}
	return this.typeDeclaration;
}
/*
 * Update the corresponding parse node from parser state which
 * is about to disappear because of restarting recovery
 */
public void updateFromParserState(){

	// anymous type and enum constant doesn't need to be updated
	if(bodyStartsAtHeaderEnd() && this.typeDeclaration.allocation == null){
		Parser parser = parser();
		/* might want to recover implemented interfaces */
		// protection for bugs 15142
		if (parser.listLength > 0 && parser.astLengthPtr > 0){ // awaiting interface type references
			int length = parser.astLengthStack[parser.astLengthPtr];
			int astPtr = parser.astPtr - length;
			boolean canConsume = astPtr >= 0;
			if(canConsume) {
				if((!(parser.astStack[astPtr] instanceof TypeDeclaration))) {
					canConsume = false;
				}
				for (int i = 1, max = length + 1; i < max; i++) {
					if(!(parser.astStack[astPtr + i ] instanceof TypeReference)) {
						canConsume = false;
					}
				}
			}
			if(canConsume) {
				parser.consumeClassHeaderImplements();
				// will reset typeListLength to zero
				// thus this check will only be performed on first errorCheck after class X implements Y,Z,
			}
		} else if (parser.listTypeParameterLength > 0) {
			int length = parser.listTypeParameterLength;
			int genericsPtr = parser.genericsPtr;
			boolean canConsume = genericsPtr + 1 >= length && parser.astPtr > -1;
			if(canConsume) {
				if (!(parser.astStack[parser.astPtr] instanceof TypeDeclaration)) {
					canConsume = false;
				}
				while(genericsPtr + 1 > length && !(parser.genericsStack[genericsPtr] instanceof TypeParameter)) {
					genericsPtr--;
				}
				for (int i = 0; i < length; i++) {
					if(!(parser.genericsStack[genericsPtr - i] instanceof TypeParameter)) {
						canConsume = false;
					}
				}
			}
			if(canConsume) {
				TypeDeclaration typeDecl = (TypeDeclaration)parser.astStack[parser.astPtr];
				System.arraycopy(parser.genericsStack, genericsPtr - length + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);
				typeDecl.bodyStart = typeDecl.typeParameters[length-1].declarationSourceEnd + 1;
				parser.listTypeParameterLength = 0;
				parser.lastCheckPoint = typeDecl.bodyStart;
			}
		}
	}
}
/*
 * A closing brace got consumed, might have closed the current element,
 * in which case both the currentElement is exited
 */
public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd){
	if ((--this.bracketBalance <= 0) && (this.parent != null)){
		this.updateSourceEndIfNecessary(braceStart, braceEnd);
		this.bodyEnd = braceStart - 1;
		return this.parent;
	}
	return this;
}
/*
 * An opening brace got consumed, might be the expected opening one of the current element,
 * in which case the bodyStart is updated.
 */
public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd){
	/* in case the opening brace is not close enough to the signature, ignore it */
	if (this.bracketBalance == 0){
		/*
			if (parser.scanner.searchLineNumber(typeDeclaration.sourceEnd)
				!= parser.scanner.searchLineNumber(braceEnd)){
		 */
		Parser parser = parser();
		switch(parser.lastIgnoredToken){
			case -1 :
			case TokenNameextends :
			case TokenNameimplements :
			case TokenNameGREATER :
			case TokenNameRIGHT_SHIFT :
			case TokenNameUNSIGNED_RIGHT_SHIFT :
				if (parser.recoveredStaticInitializerStart == 0) break;
			//$FALL-THROUGH$
			default:
				this.foundOpeningBrace = true;
				this.bracketBalance = 1; // pretend the brace was already there
		}
	}
	// might be an initializer
	if (this.bracketBalance == 1){
		Block block = new Block(0);
		Parser parser = parser();
		block.sourceStart = parser.scanner.startPosition;
		Initializer init;
		if (parser.recoveredStaticInitializerStart == 0){
			init = new Initializer(block, ClassFileConstants.AccDefault);
		} else {
			init = new Initializer(block, ClassFileConstants.AccStatic);
			init.declarationSourceStart = parser.recoveredStaticInitializerStart;
		}
		init.bodyStart = parser.scanner.currentPosition;
		return this.add(init, 1);
	}
	return super.updateOnOpeningBrace(braceStart, braceEnd);
}
public void updateParseTree(){
	updatedTypeDeclaration(0, new HashSet());
}
/*
 * Update the declarationSourceEnd of the corresponding parse node
 */
public void updateSourceEndIfNecessary(int start, int end){
	if (this.typeDeclaration.declarationSourceEnd == 0){
		this.bodyEnd = 0;
		this.typeDeclaration.declarationSourceEnd = end;
		this.typeDeclaration.bodyEnd = end;
	}
}
}
