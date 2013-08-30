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
package org.eclipse.jdt.internal.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrame;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream.ExceptionMarker;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream.StackDepthMarker;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream.StackMarker;
import org.eclipse.jdt.internal.compiler.codegen.VerificationTypeInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

/**
 * Represents a class file wrapper on bytes, it is aware of its actual
 * type name.
 *
 * Public APIs are listed below:
 *
 * byte[] getBytes();
 *		Answer the actual bytes of the class file
 *
 * char[][] getCompoundName();
 * 		Answer the compound name of the class file.
 * 		For example, {{java}, {util}, {Hashtable}}.
 *
 * byte[] getReducedBytes();
 * 		Answer a smaller byte format, which is only contains some structural
 *      information. Those bytes are decodable with a regular class file reader,
 *      such as DietClassFileReader
 */
public class ClassFile implements TypeConstants, TypeIds {

	private byte[] bytes;
	public CodeStream codeStream;
	public ConstantPool constantPool;

	public int constantPoolOffset;

	// the header contains all the bytes till the end of the constant pool
	public byte[] contents;

	public int contentsOffset;

	protected boolean creatingProblemType;

	public ClassFile enclosingClassFile;
	public byte[] header;
	// that collection contains all the remaining bytes of the .class file
	public int headerOffset;
	public Set innerClassesBindings;
	public int methodCount;
	public int methodCountOffset;
	// pool managment
	boolean isShared = false;
	// used to generate private access methods
	// debug and stack map attributes
	public int produceAttributes;
	public SourceTypeBinding referenceBinding;
	public boolean isNestedType;
	public long targetJDK;

	public List missingTypes = null;

	public Set visitedTypes;

	public static final int INITIAL_CONTENTS_SIZE = 400;
	public static final int INITIAL_HEADER_SIZE = 1500;
	public static final int INNER_CLASSES_SIZE = 5;

	/**
	 * INTERNAL USE-ONLY
	 * Request the creation of a ClassFile compatible representation of a problematic type
	 *
	 * @param typeDeclaration org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
	 * @param unitResult org.eclipse.jdt.internal.compiler.CompilationUnitResult
	 */
	public static void createProblemType(TypeDeclaration typeDeclaration, CompilationResult unitResult) {
		SourceTypeBinding typeBinding = typeDeclaration.binding;
		ClassFile classFile = ClassFile.getNewInstance(typeBinding);
		classFile.initialize(typeBinding, null, true);

		if (typeBinding.hasMemberTypes()) {
			// see bug 180109
			ReferenceBinding[] members = typeBinding.memberTypes;
			for (int i = 0, l = members.length; i < l; i++)
				classFile.recordInnerClasses(members[i]);
		}
		// TODO (olivier) handle cases where a field cannot be generated (name too long)
		// TODO (olivier) handle too many methods
		// inner attributes
		if (typeBinding.isNestedType()) {
			classFile.recordInnerClasses(typeBinding);
		}
		TypeVariableBinding[] typeVariables = typeBinding.typeVariables();
		for (int i = 0, max = typeVariables.length; i < max; i++) {
			TypeVariableBinding typeVariableBinding = typeVariables[i];
			if ((typeVariableBinding.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
				Util.recordNestedType(classFile, typeVariableBinding);
			}
		}
		// add its fields
		FieldBinding[] fields = typeBinding.fields();
		if ((fields != null) && (fields != Binding.NO_FIELDS)) {
			classFile.addFieldInfos();
		} else {
			// we have to set the number of fields to be equals to 0
			classFile.contents[classFile.contentsOffset++] = 0;
			classFile.contents[classFile.contentsOffset++] = 0;
		}
		// leave some space for the methodCount
		classFile.setForMethodInfos();
		// add its user defined methods
		int problemsLength;
		CategorizedProblem[] problems = unitResult.getErrors();
		if (problems == null) {
			problems = new CategorizedProblem[0];
		}
		CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
		System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);

		AbstractMethodDeclaration[] methodDecls = typeDeclaration.methods;
		if (methodDecls != null) {
			if (typeBinding.isInterface()) {
				// we cannot create problem methods for an interface. So we have to generate a clinit
				// which should contain all the problem
				classFile.addProblemClinit(problemsCopy);
				for (int i = 0, length = methodDecls.length; i < length; i++) {
					AbstractMethodDeclaration methodDecl = methodDecls[i];
					MethodBinding method = methodDecl.binding;
					if (method == null || method.isConstructor()) continue;
					method.modifiers = ClassFileConstants.AccPublic | ClassFileConstants.AccAbstract;
					classFile.addAbstractMethod(methodDecl, method);
				}
			} else {
				for (int i = 0, length = methodDecls.length; i < length; i++) {
					AbstractMethodDeclaration methodDecl = methodDecls[i];
					MethodBinding method = methodDecl.binding;
					if (method == null) continue;
					if (method.isConstructor()) {
						classFile.addProblemConstructor(methodDecl, method, problemsCopy);
					} else if (method.isAbstract()) {
						classFile.addAbstractMethod(methodDecl, method);
					} else {
						classFile.addProblemMethod(methodDecl, method, problemsCopy);
					}
				}
			}
			// add abstract methods
			classFile.addDefaultAbstractMethods();
		}

		// propagate generation of (problem) member types
		if (typeDeclaration.memberTypes != null) {
			for (int i = 0, max = typeDeclaration.memberTypes.length; i < max; i++) {
				TypeDeclaration memberType = typeDeclaration.memberTypes[i];
				if (memberType.binding != null) {
					ClassFile.createProblemType(memberType, unitResult);
				}
			}
		}
		classFile.addAttributes();
		unitResult.record(typeBinding.constantPoolName(), classFile);
	}
	public static ClassFile getNewInstance(SourceTypeBinding typeBinding) {
		LookupEnvironment env = typeBinding.scope.environment();
		return env.classFilePool.acquire(typeBinding);
	}
	/**
	 * INTERNAL USE-ONLY
	 * This methods creates a new instance of the receiver.
	 */
	protected ClassFile() {
		// default constructor for subclasses
	}

	public ClassFile(SourceTypeBinding typeBinding) {
		// default constructor for subclasses
		this.constantPool = new ConstantPool(this);
		final CompilerOptions options = typeBinding.scope.compilerOptions();
		this.targetJDK = options.targetJDK;
		this.produceAttributes = options.produceDebugAttributes;
		this.referenceBinding = typeBinding;
		this.isNestedType = typeBinding.isNestedType();
		if (this.targetJDK >= ClassFileConstants.JDK1_6) {
			this.produceAttributes |= ClassFileConstants.ATTR_STACK_MAP_TABLE;
			this.codeStream = new StackMapFrameCodeStream(this);
		} else if (this.targetJDK == ClassFileConstants.CLDC_1_1) {
			this.targetJDK = ClassFileConstants.JDK1_1; // put back 45.3
			this.produceAttributes |= ClassFileConstants.ATTR_STACK_MAP;
			this.codeStream = new StackMapFrameCodeStream(this);
		} else {
			this.codeStream = new CodeStream(this);
		}
		initByteArrays();
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a bogus method.
	 *
	 * @param method org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.MethodBinding
	 */
	public void addAbstractMethod(
			AbstractMethodDeclaration method,
			MethodBinding methodBinding) {

		this.generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
		completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
	}

	/**
	 * INTERNAL USE-ONLY
	 * This methods generate all the attributes for the receiver.
	 * For a class they could be:
	 * - source file attribute
	 * - inner classes attribute
	 * - deprecated attribute
	 */
	public void addAttributes() {
		// update the method count
		this.contents[this.methodCountOffset++] = (byte) (this.methodCount >> 8);
		this.contents[this.methodCountOffset] = (byte) this.methodCount;

		int attributesNumber = 0;
		// leave two bytes for the number of attributes and store the current offset
		int attributeOffset = this.contentsOffset;
		this.contentsOffset += 2;

		// source attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_SOURCE) != 0) {
			String fullFileName =
				new String(this.referenceBinding.scope.referenceCompilationUnit().getFileName());
			fullFileName = fullFileName.replace('\\', '/');
			int lastIndex = fullFileName.lastIndexOf('/');
			if (lastIndex != -1) {
				fullFileName = fullFileName.substring(lastIndex + 1, fullFileName.length());
			}
			attributesNumber += generateSourceAttribute(fullFileName);
		}
		// Deprecated attribute
		if (this.referenceBinding.isDeprecated()) {
			// check that there is enough space to write all the bytes for the field info corresponding
			// to the @fieldBinding
			attributesNumber += generateDeprecatedAttribute();
		}
		// add signature attribute
		char[] genericSignature = this.referenceBinding.genericSignature();
		if (genericSignature != null) {
			attributesNumber += generateSignatureAttribute(genericSignature);
		}
		if (this.targetJDK >= ClassFileConstants.JDK1_5
				&& this.referenceBinding.isNestedType()
				&& !this.referenceBinding.isMemberType()) {
			// add enclosing method attribute (1.5 mode only)
			attributesNumber += generateEnclosingMethodAttribute();
		}
		if (this.targetJDK >= ClassFileConstants.JDK1_4) {
			TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
			if (typeDeclaration != null) {
				final Annotation[] annotations = typeDeclaration.annotations;
				if (annotations != null) {
					attributesNumber += generateRuntimeAnnotations(annotations);
				}
			}
		}

		if (this.referenceBinding.isHierarchyInconsistent()) {
			ReferenceBinding superclass = this.referenceBinding.superclass;
			if (superclass != null) {
				this.missingTypes = superclass.collectMissingTypes(this.missingTypes);
			}
			ReferenceBinding[] superInterfaces = this.referenceBinding.superInterfaces();
			for (int i = 0, max = superInterfaces.length; i < max; i++) {
				this.missingTypes = superInterfaces[i].collectMissingTypes(this.missingTypes);
			}
			attributesNumber += generateHierarchyInconsistentAttribute();
		}
		// Inner class attribute
		int numberOfInnerClasses = this.innerClassesBindings == null ? 0 : this.innerClassesBindings.size();
		if (numberOfInnerClasses != 0) {
			ReferenceBinding[] innerClasses = new ReferenceBinding[numberOfInnerClasses];
			this.innerClassesBindings.toArray(innerClasses);
			Arrays.sort(innerClasses, new Comparator() {
				public int compare(Object o1, Object o2) {
					TypeBinding binding1 = (TypeBinding) o1;
					TypeBinding binding2 = (TypeBinding) o2;
					return CharOperation.compareTo(binding1.constantPoolName(), binding2.constantPoolName());
				}
			});
			attributesNumber += generateInnerClassAttribute(numberOfInnerClasses, innerClasses);
		}
		if (this.missingTypes != null) {
			generateMissingTypesAttribute();
			attributesNumber++;
		}
		// update the number of attributes
		if (attributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[attributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[attributeOffset] = (byte) attributesNumber;

		// resynchronize all offsets of the classfile
		this.header = this.constantPool.poolContent;
		this.headerOffset = this.constantPool.currentOffset;
		int constantPoolCount = this.constantPool.currentIndex;
		this.header[this.constantPoolOffset++] = (byte) (constantPoolCount >> 8);
		this.header[this.constantPoolOffset] = (byte) constantPoolCount;
	}
	/**
	 * INTERNAL USE-ONLY
	 * This methods generate all the default abstract method infos that correpond to
	 * the abstract methods inherited from superinterfaces.
	 */
	public void addDefaultAbstractMethods() { // default abstract methods
		MethodBinding[] defaultAbstractMethods =
			this.referenceBinding.getDefaultAbstractMethods();
		for (int i = 0, max = defaultAbstractMethods.length; i < max; i++) {
			MethodBinding methodBinding = defaultAbstractMethods[i];
			generateMethodInfoHeader(methodBinding);
			int methodAttributeOffset = this.contentsOffset;
			int attributeNumber = generateMethodInfoAttributes(methodBinding);
			completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
		}
	}

	private int addFieldAttributes(FieldBinding fieldBinding, int fieldAttributeOffset) {
		int attributesNumber = 0;
		// 4.7.2 only static constant fields get a ConstantAttribute
		// Generate the constantValueAttribute
		Constant fieldConstant = fieldBinding.constant();
		if (fieldConstant != Constant.NotAConstant){
			attributesNumber += generateConstantValueAttribute(fieldConstant, fieldBinding, fieldAttributeOffset);
		}
		if (this.targetJDK < ClassFileConstants.JDK1_5 && fieldBinding.isSynthetic()) {
			attributesNumber += generateSyntheticAttribute();
		}
		if (fieldBinding.isDeprecated()) {
			attributesNumber += generateDeprecatedAttribute();
		}
		// add signature attribute
		char[] genericSignature = fieldBinding.genericSignature();
		if (genericSignature != null) {
			attributesNumber += generateSignatureAttribute(genericSignature);
		}
		if (this.targetJDK >= ClassFileConstants.JDK1_4) {
			FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
			if (fieldDeclaration != null) {
				Annotation[] annotations = fieldDeclaration.annotations;
				if (annotations != null) {
					attributesNumber += generateRuntimeAnnotations(annotations);
				}
			}
		}
		if ((fieldBinding.tagBits & TagBits.HasMissingType) != 0) {
			this.missingTypes = fieldBinding.type.collectMissingTypes(this.missingTypes);
		}
		return attributesNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 * This methods generates the bytes for the given field binding
	 * @param fieldBinding the given field binding
	 */
	private void addFieldInfo(FieldBinding fieldBinding) {
		// check that there is enough space to write all the bytes for the field info corresponding
		// to the @fieldBinding
		if (this.contentsOffset + 8 >= this.contents.length) {
			resizeContents(8);
		}
		// Now we can generate all entries into the byte array
		// First the accessFlags
		int accessFlags = fieldBinding.getAccessFlags();
		if (this.targetJDK < ClassFileConstants.JDK1_5) {
			// pre 1.5, synthetic was an attribute, not a modifier
			accessFlags &= ~ClassFileConstants.AccSynthetic;
		}
		this.contents[this.contentsOffset++] = (byte) (accessFlags >> 8);
		this.contents[this.contentsOffset++] = (byte) accessFlags;
		// Then the nameIndex
		int nameIndex = this.constantPool.literalIndex(fieldBinding.name);
		this.contents[this.contentsOffset++] = (byte) (nameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) nameIndex;
		// Then the descriptorIndex
		int descriptorIndex = this.constantPool.literalIndex(fieldBinding.type);
		this.contents[this.contentsOffset++] = (byte) (descriptorIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) descriptorIndex;
		int fieldAttributeOffset = this.contentsOffset;
		int attributeNumber = 0;
		// leave some space for the number of attributes
		this.contentsOffset += 2;
		attributeNumber += addFieldAttributes(fieldBinding, fieldAttributeOffset);
		if (this.contentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[fieldAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[fieldAttributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 * This methods generate all the fields infos for the receiver.
	 * This includes:
	 * - a field info for each defined field of that class
	 * - a field info for each synthetic field (e.g. this$0)
	 */
	/**
	 * INTERNAL USE-ONLY
	 * This methods generate all the fields infos for the receiver.
	 * This includes:
	 * - a field info for each defined field of that class
	 * - a field info for each synthetic field (e.g. this$0)
	 */
	public void addFieldInfos() {
		SourceTypeBinding currentBinding = this.referenceBinding;
		FieldBinding[] syntheticFields = currentBinding.syntheticFields();
		int fieldCount = 	currentBinding.fieldCount() + (syntheticFields == null ? 0 : syntheticFields.length);

		// write the number of fields
		if (fieldCount > 0xFFFF) {
			this.referenceBinding.scope.problemReporter().tooManyFields(this.referenceBinding.scope.referenceType());
		}
		this.contents[this.contentsOffset++] = (byte) (fieldCount >> 8);
		this.contents[this.contentsOffset++] = (byte) fieldCount;

		FieldDeclaration[] fieldDecls = currentBinding.scope.referenceContext.fields;
		for (int i = 0, max = fieldDecls == null ? 0 : fieldDecls.length; i < max; i++) {
			FieldDeclaration fieldDecl = fieldDecls[i];
			if (fieldDecl.binding != null) {
				addFieldInfo(fieldDecl.binding);
			}
		}

		if (syntheticFields != null) {
			for (int i = 0, max = syntheticFields.length; i < max; i++) {
				addFieldInfo(syntheticFields[i]);
			}
		}
	}

	private void addMissingAbstractProblemMethod(MethodDeclaration methodDeclaration, MethodBinding methodBinding, CategorizedProblem problem, CompilationResult compilationResult) {
		// always clear the strictfp/native/abstract bit for a problem method
		generateMethodInfoHeader(methodBinding, methodBinding.modifiers & ~(ClassFileConstants.AccStrictfp | ClassFileConstants.AccNative | ClassFileConstants.AccAbstract));
		int methodAttributeOffset = this.contentsOffset;
		int attributeNumber = generateMethodInfoAttributes(methodBinding);

		// Code attribute
		attributeNumber++;

		int codeAttributeOffset = this.contentsOffset;
		generateCodeAttributeHeader();
		StringBuffer buffer = new StringBuffer(25);
		buffer.append("\t"  + problem.getMessage() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.insert(0, Messages.compilation_unresolvedProblem);
		String problemString = buffer.toString();

		this.codeStream.init(this);
		this.codeStream.preserveUnusedLocals = true;
		this.codeStream.initializeMaxLocals(methodBinding);

		// return codeStream.generateCodeAttributeForProblemMethod(comp.options.runtimeExceptionNameForCompileError, "")
		this.codeStream.generateCodeAttributeForProblemMethod(problemString);

		completeCodeAttributeForMissingAbstractProblemMethod(
			methodBinding,
			codeAttributeOffset,
			compilationResult.getLineSeparatorPositions(),
			problem.getSourceLineNumber());

		completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem clinit method info that correspond to a boggus method.
	 *
	 * @param problems org.eclipse.jdt.internal.compiler.problem.Problem[]
	 */
	public void addProblemClinit(CategorizedProblem[] problems) {
		generateMethodInfoHeaderForClinit();
		// leave two spaces for the number of attributes
		this.contentsOffset -= 2;
		int attributeOffset = this.contentsOffset;
		this.contentsOffset += 2;
		int attributeNumber = 0;

		int codeAttributeOffset = this.contentsOffset;
		generateCodeAttributeHeader();
		this.codeStream.resetForProblemClinit(this);
		String problemString = "" ; //$NON-NLS-1$
		int problemLine = 0;
		if (problems != null) {
			int max = problems.length;
			StringBuffer buffer = new StringBuffer(25);
			int count = 0;
			for (int i = 0; i < max; i++) {
				CategorizedProblem problem = problems[i];
				if ((problem != null) && (problem.isError())) {
					buffer.append("\t"  +problem.getMessage() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
					count++;
					if (problemLine == 0) {
						problemLine = problem.getSourceLineNumber();
					}
					problems[i] = null;
				}
			} // insert the top line afterwards, once knowing how many problems we have to consider
			if (count > 1) {
				buffer.insert(0, Messages.compilation_unresolvedProblems);
			} else {
				buffer.insert(0, Messages.compilation_unresolvedProblem);
			}
			problemString = buffer.toString();
		}

		// return codeStream.generateCodeAttributeForProblemMethod(comp.options.runtimeExceptionNameForCompileError, "")
		this.codeStream.generateCodeAttributeForProblemMethod(problemString);
		attributeNumber++; // code attribute
		completeCodeAttributeForClinit(
			codeAttributeOffset,
			problemLine);
		if (this.contentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[attributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[attributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a boggus constructor.
	 *
	 * @param method org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.MethodBinding
	 * @param problems org.eclipse.jdt.internal.compiler.problem.Problem[]
	 */
	public void addProblemConstructor(
		AbstractMethodDeclaration method,
		MethodBinding methodBinding,
		CategorizedProblem[] problems) {

		// always clear the strictfp/native/abstract bit for a problem method
		generateMethodInfoHeader(methodBinding, methodBinding.modifiers & ~(ClassFileConstants.AccStrictfp | ClassFileConstants.AccNative | ClassFileConstants.AccAbstract));
		int methodAttributeOffset = this.contentsOffset;
		int attributesNumber = generateMethodInfoAttributes(methodBinding);

		// Code attribute
		attributesNumber++;
		int codeAttributeOffset = this.contentsOffset;
		generateCodeAttributeHeader();
		this.codeStream.reset(method, this);
		String problemString = "" ; //$NON-NLS-1$
		int problemLine = 0;
		if (problems != null) {
			int max = problems.length;
			StringBuffer buffer = new StringBuffer(25);
			int count = 0;
			for (int i = 0; i < max; i++) {
				CategorizedProblem problem = problems[i];
				if ((problem != null) && (problem.isError())) {
					buffer.append("\t"  +problem.getMessage() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
					count++;
					if (problemLine == 0) {
						problemLine = problem.getSourceLineNumber();
					}
				}
			} // insert the top line afterwards, once knowing how many problems we have to consider
			if (count > 1) {
				buffer.insert(0, Messages.compilation_unresolvedProblems);
			} else {
				buffer.insert(0, Messages.compilation_unresolvedProblem);
			}
			problemString = buffer.toString();
		}

		// return codeStream.generateCodeAttributeForProblemMethod(comp.options.runtimeExceptionNameForCompileError, "")
		this.codeStream.generateCodeAttributeForProblemMethod(problemString);
		completeCodeAttributeForProblemMethod(
			method,
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions(),
			problemLine);
		completeMethodInfo(methodBinding, methodAttributeOffset, attributesNumber);
	}
	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a boggus constructor.
	 * Reset the position inside the contents byte array to the savedOffset.
	 *
	 * @param method org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.MethodBinding
	 * @param problems org.eclipse.jdt.internal.compiler.problem.Problem[]
	 * @param savedOffset <CODE>int</CODE>
	 */
	public void addProblemConstructor(
		AbstractMethodDeclaration method,
		MethodBinding methodBinding,
		CategorizedProblem[] problems,
		int savedOffset) {
		// we need to move back the contentsOffset to the value at the beginning of the method
		this.contentsOffset = savedOffset;
		this.methodCount--; // we need to remove the method that causes the problem
		addProblemConstructor(method, methodBinding, problems);
	}
	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a boggus method.
	 *
	 * @param method org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.MethodBinding
	 * @param problems org.eclipse.jdt.internal.compiler.problem.Problem[]
	 */
	public void addProblemMethod(
		AbstractMethodDeclaration method,
		MethodBinding methodBinding,
		CategorizedProblem[] problems) {
		if (methodBinding.isAbstract() && methodBinding.declaringClass.isInterface()) {
			method.abort(ProblemSeverities.AbortType, null);
		}
		// always clear the strictfp/native/abstract bit for a problem method
		generateMethodInfoHeader(methodBinding, methodBinding.modifiers & ~(ClassFileConstants.AccStrictfp | ClassFileConstants.AccNative | ClassFileConstants.AccAbstract));
		int methodAttributeOffset = this.contentsOffset;
		int attributesNumber = generateMethodInfoAttributes(methodBinding);

		// Code attribute
		attributesNumber++;

		int codeAttributeOffset = this.contentsOffset;
		generateCodeAttributeHeader();
		this.codeStream.reset(method, this);
		String problemString = "" ; //$NON-NLS-1$
		int problemLine = 0;
		if (problems != null) {
			int max = problems.length;
			StringBuffer buffer = new StringBuffer(25);
			int count = 0;
			for (int i = 0; i < max; i++) {
				CategorizedProblem problem = problems[i];
				if ((problem != null)
					&& (problem.isError())
					&& (problem.getSourceStart() >= method.declarationSourceStart)
					&& (problem.getSourceEnd() <= method.declarationSourceEnd)) {
					buffer.append("\t"  +problem.getMessage() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
					count++;
					if (problemLine == 0) {
						problemLine = problem.getSourceLineNumber();
					}
					problems[i] = null;
				}
			} // insert the top line afterwards, once knowing how many problems we have to consider
			if (count > 1) {
				buffer.insert(0, Messages.compilation_unresolvedProblems);
			} else {
				buffer.insert(0, Messages.compilation_unresolvedProblem);
			}
			problemString = buffer.toString();
		}

		// return codeStream.generateCodeAttributeForProblemMethod(comp.options.runtimeExceptionNameForCompileError, "")
		this.codeStream.generateCodeAttributeForProblemMethod(problemString);
		completeCodeAttributeForProblemMethod(
			method,
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions(),
			problemLine);
		completeMethodInfo(methodBinding, methodAttributeOffset, attributesNumber);
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a boggus method.
	 * Reset the position inside the contents byte array to the savedOffset.
	 *
	 * @param method org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.MethodBinding
	 * @param problems org.eclipse.jdt.internal.compiler.problem.Problem[]
	 * @param savedOffset <CODE>int</CODE>
	 */
	public void addProblemMethod(
		AbstractMethodDeclaration method,
		MethodBinding methodBinding,
		CategorizedProblem[] problems,
		int savedOffset) {
		// we need to move back the contentsOffset to the value at the beginning of the method
		this.contentsOffset = savedOffset;
		this.methodCount--; // we need to remove the method that causes the problem
		addProblemMethod(method, methodBinding, problems);
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for all the special method infos.
	 * They are:
	 * - synthetic access methods
	 * - default abstract methods
	 */
	public void addSpecialMethods() {

		// add all methods (default abstract methods and synthetic)

		// default abstract methods
		generateMissingAbstractMethods(this.referenceBinding.scope.referenceType().missingAbstractMethods, this.referenceBinding.scope.referenceCompilationUnit().compilationResult);

		MethodBinding[] defaultAbstractMethods = this.referenceBinding.getDefaultAbstractMethods();
		for (int i = 0, max = defaultAbstractMethods.length; i < max; i++) {
			MethodBinding methodBinding = defaultAbstractMethods[i];
			generateMethodInfoHeader(methodBinding);
			int methodAttributeOffset = this.contentsOffset;
			int attributeNumber = generateMethodInfoAttributes(methodBinding);
			completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
		}
		// add synthetic methods infos
		SyntheticMethodBinding[] syntheticMethods = this.referenceBinding.syntheticMethods();
		if (syntheticMethods != null) {
			for (int i = 0, max = syntheticMethods.length; i < max; i++) {
				SyntheticMethodBinding syntheticMethod = syntheticMethods[i];
				switch (syntheticMethod.purpose) {
					case SyntheticMethodBinding.FieldReadAccess :
					case SyntheticMethodBinding.SuperFieldReadAccess :
						// generate a method info to emulate an reading access to
						// a non-accessible field
						addSyntheticFieldReadAccessMethod(syntheticMethod);
						break;
					case SyntheticMethodBinding.FieldWriteAccess :
					case SyntheticMethodBinding.SuperFieldWriteAccess :
						// generate a method info to emulate an writing access to
						// a non-accessible field
						addSyntheticFieldWriteAccessMethod(syntheticMethod);
						break;
					case SyntheticMethodBinding.MethodAccess :
					case SyntheticMethodBinding.SuperMethodAccess :
					case SyntheticMethodBinding.BridgeMethod :
						// generate a method info to emulate an access to a non-accessible method / super-method or bridge method
						addSyntheticMethodAccessMethod(syntheticMethod);
						break;
					case SyntheticMethodBinding.ConstructorAccess :
						// generate a method info to emulate an access to a non-accessible constructor
						addSyntheticConstructorAccessMethod(syntheticMethod);
						break;
					case SyntheticMethodBinding.EnumValues :
						// generate a method info to define <enum>#values()
						addSyntheticEnumValuesMethod(syntheticMethod);
						break;
					case SyntheticMethodBinding.EnumValueOf :
						// generate a method info to define <enum>#valueOf(String)
						addSyntheticEnumValueOfMethod(syntheticMethod);
						break;
					case SyntheticMethodBinding.SwitchTable :
						// generate a method info to define the switch table synthetic method
						addSyntheticSwitchTable(syntheticMethod);
						break;
					case SyntheticMethodBinding.TooManyEnumsConstants :
						addSyntheticEnumInitializationMethod(syntheticMethod);
				}
			}
		}
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the bytes for a synthetic method that provides an access to a private constructor.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.SyntheticAccessMethodBinding
	 */
	public void addSyntheticConstructorAccessMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForConstructorAccess(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 *  Generate the bytes for a synthetic method that implements Enum#valueOf(String) for a given enum type
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.SyntheticAccessMethodBinding
	 */
	public void addSyntheticEnumValueOfMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForEnumValueOf(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 *  Generate the bytes for a synthetic method that implements Enum#values() for a given enum type
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.SyntheticAccessMethodBinding
	 */
	public void addSyntheticEnumValuesMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForEnumValues(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	public void addSyntheticEnumInitializationMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForEnumInitializationMethod(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}
	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a synthetic method that
	 * generate an read access to a private field.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.SyntheticAccessMethodBinding
	 */
	public void addSyntheticFieldReadAccessMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForFieldReadAccess(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for a problem method info that correspond to a synthetic method that
	 * generate an write access to a private field.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.SyntheticAccessMethodBinding
	 */
	public void addSyntheticFieldWriteAccessMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForFieldWriteAccess(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the bytes for a synthetic method that provides access to a private method.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.nameloopkup.SyntheticAccessMethodBinding
	 */
	public void addSyntheticMethodAccessMethod(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForMethodAccess(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	public void addSyntheticSwitchTable(SyntheticMethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding);
		int methodAttributeOffset = this.contentsOffset;
		// this will add exception attribute, synthetic attribute, deprecated attribute,...
		int attributeNumber = generateMethodInfoAttributes(methodBinding);
		// Code attribute
		int codeAttributeOffset = this.contentsOffset;
		attributeNumber++; // add code attribute
		generateCodeAttributeHeader();
		this.codeStream.init(this);
		this.codeStream.generateSyntheticBodyForSwitchTable(methodBinding);
		completeCodeAttributeForSyntheticMethod(
			true,
			methodBinding,
			codeAttributeOffset,
			((SourceTypeBinding) methodBinding.declaringClass)
				.scope
				.referenceCompilationUnit()
				.compilationResult
				.getLineSeparatorPositions());
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributeNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributeNumber;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method completes the creation of the code attribute by setting
	 * - the attribute_length
	 * - max_stack
	 * - max_locals
	 * - code_length
	 * - exception table
	 * - and debug attributes if necessary.
	 *
	 * @param codeAttributeOffset <CODE>int</CODE>
	 */
	public void completeCodeAttribute(int codeAttributeOffset) {
		// reinitialize the localContents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside localContents byte array before we started to write
		// any information about the codeAttribute
		// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset
		// to get the right position, 6 for the max_stack etc...
		int code_length = this.codeStream.position;
		if (code_length > 65535) {
			this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
				this.codeStream.methodDeclaration);
		}
		if (localContentsOffset + 20 >= this.contents.length) {
			resizeContents(20);
		}
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;

		boolean addStackMaps = (this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0;
		// write the exception table
		ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
		int exceptionHandlersCount = 0; // each label holds one handler per range (start/end contiguous)
		for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
			exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
		}
		int exSize = exceptionHandlersCount * 8 + 2;
		if (exSize + localContentsOffset >= this.contents.length) {
			resizeContents(exSize);
		}
		// there is no exception table, so we need to offset by 2 the current offset and move
		// on the attribute generation
		this.contents[localContentsOffset++] = (byte) (exceptionHandlersCount >> 8);
		this.contents[localContentsOffset++] = (byte) exceptionHandlersCount;
		for (int i = 0, max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
			ExceptionLabel exceptionLabel = exceptionLabels[i];
			if (exceptionLabel != null) {
				int iRange = 0, maxRange = exceptionLabel.getCount();
				if ((maxRange & 1) != 0) {
					this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
							Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)),
							this.codeStream.methodDeclaration);
				}
				while (iRange < maxRange) {
					int start = exceptionLabel.ranges[iRange++]; // even ranges are start positions
					this.contents[localContentsOffset++] = (byte) (start >> 8);
					this.contents[localContentsOffset++] = (byte) start;
					int end = exceptionLabel.ranges[iRange++]; // odd ranges are end positions
					this.contents[localContentsOffset++] = (byte) (end >> 8);
					this.contents[localContentsOffset++] = (byte) end;
					int handlerPC = exceptionLabel.position;
					if (addStackMaps) {
						StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
						stackMapFrameCodeStream.addFramePosition(handlerPC);
//						stackMapFrameCodeStream.addExceptionMarker(handlerPC, exceptionLabel.exceptionType);
					}
					this.contents[localContentsOffset++] = (byte) (handlerPC >> 8);
					this.contents[localContentsOffset++] = (byte) handlerPC;
					if (exceptionLabel.exceptionType == null) {
						// any exception handler
						this.contents[localContentsOffset++] = 0;
						this.contents[localContentsOffset++] = 0;
					} else {
						int nameIndex;
						if (exceptionLabel.exceptionType == TypeBinding.NULL) {
							/* represents ClassNotFoundException, see class literal access*/
							nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
						} else {
							nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
						}
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
					}
				}
			}
		}
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributesNumber = 0;
		// leave two bytes for the attribute_length
		localContentsOffset += 2;
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		this.contentsOffset = localContentsOffset;

		// first we handle the linenumber attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			attributesNumber += generateLineNumberAttribute();
		}
		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			final boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
			attributesNumber += generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, false);
		}

		if (addStackMaps) {
			attributesNumber += generateStackMapTableAttribute(
					this.codeStream.methodDeclaration.binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			attributesNumber += generateStackMapAttribute(
					this.codeStream.methodDeclaration.binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		this.contents[codeAttributeAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributesNumber;

		// update the attribute length
		int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method completes the creation of the code attribute by setting
	 * - the attribute_length
	 * - max_stack
	 * - max_locals
	 * - code_length
	 * - exception table
	 * - and debug attributes if necessary.
	 *
	 * @param codeAttributeOffset <CODE>int</CODE>
	 */
	public void completeCodeAttributeForClinit(int codeAttributeOffset) {
		// reinitialize the contents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside contents byte array before we started to write
		// any information about the codeAttribute
		// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset
		// to get the right position, 6 for the max_stack etc...
		int code_length = this.codeStream.position;
		if (code_length > 65535) {
			this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
				this.codeStream.methodDeclaration.scope.referenceType());
		}
		if (localContentsOffset + 20 >= this.contents.length) {
			resizeContents(20);
		}
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;

		boolean addStackMaps = (this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0;
		// write the exception table
		ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
		int exceptionHandlersCount = 0; // each label holds one handler per range (start/end contiguous)
		for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
			exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
		}
		int exSize = exceptionHandlersCount * 8 + 2;
		if (exSize + localContentsOffset >= this.contents.length) {
			resizeContents(exSize);
		}
		// there is no exception table, so we need to offset by 2 the current offset and move
		// on the attribute generation
		this.contents[localContentsOffset++] = (byte) (exceptionHandlersCount >> 8);
		this.contents[localContentsOffset++] = (byte) exceptionHandlersCount;
		for (int i = 0, max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
			ExceptionLabel exceptionLabel = exceptionLabels[i];
			if (exceptionLabel != null) {
				int iRange = 0, maxRange = exceptionLabel.getCount();
				if ((maxRange & 1) != 0) {
					this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
							Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)),
							this.codeStream.methodDeclaration);
				}
				while  (iRange < maxRange) {
					int start = exceptionLabel.ranges[iRange++]; // even ranges are start positions
					this.contents[localContentsOffset++] = (byte) (start >> 8);
					this.contents[localContentsOffset++] = (byte) start;
					int end = exceptionLabel.ranges[iRange++]; // odd ranges are end positions
					this.contents[localContentsOffset++] = (byte) (end >> 8);
					this.contents[localContentsOffset++] = (byte) end;
					int handlerPC = exceptionLabel.position;
					this.contents[localContentsOffset++] = (byte) (handlerPC >> 8);
					this.contents[localContentsOffset++] = (byte) handlerPC;
					if (addStackMaps) {
						StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
						stackMapFrameCodeStream.addFramePosition(handlerPC);
//						stackMapFrameCodeStream.addExceptionMarker(handlerPC, exceptionLabel.exceptionType);
					}
					if (exceptionLabel.exceptionType == null) {
						// any exception handler
						this.contents[localContentsOffset++] = 0;
						this.contents[localContentsOffset++] = 0;
					} else {
						int nameIndex;
						if (exceptionLabel.exceptionType == TypeBinding.NULL) {
							/* represents denote ClassNotFoundException, see class literal access*/
							nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
						} else {
							nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
						}
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
					}
				}
			}
		}
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributesNumber = 0;
		// leave two bytes for the attribute_length
		localContentsOffset += 2;
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		this.contentsOffset = localContentsOffset;

		// first we handle the linenumber attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			attributesNumber += generateLineNumberAttribute();
		}
		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			attributesNumber += generateLocalVariableTableAttribute(code_length, true, false);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0) {
			attributesNumber += generateStackMapTableAttribute(
					null,
					code_length,
					codeAttributeOffset,
					max_locals,
					true);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			attributesNumber += generateStackMapAttribute(
					null,
					code_length,
					codeAttributeOffset,
					max_locals,
					true);
		}

		// update the number of attributes
		// ensure first that there is enough space available inside the contents array
		if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[codeAttributeAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributesNumber;
		// update the attribute length
		int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method completes the creation of the code attribute by setting
	 * - the attribute_length
	 * - max_stack
	 * - max_locals
	 * - code_length
	 * - exception table
	 * - and debug attributes if necessary.
	 */
	public void completeCodeAttributeForClinit(
			int codeAttributeOffset,
			int problemLine) {
		// reinitialize the contents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside contents byte array before we started to write
		// any information about the codeAttribute
		// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset
		// to get the right position, 6 for the max_stack etc...
		int code_length = this.codeStream.position;
		if (code_length > 65535) {
			this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
				this.codeStream.methodDeclaration.scope.referenceType());
		}
		if (localContentsOffset + 20 >= this.contents.length) {
			resizeContents(20);
		}
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;

		// write the exception table
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;

		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributesNumber = 0; // leave two bytes for the attribute_length
		localContentsOffset += 2; // first we handle the linenumber attribute
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		this.contentsOffset = localContentsOffset;
		// first we handle the linenumber attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			attributesNumber += generateLineNumberAttribute(problemLine);
		}
		localContentsOffset = this.contentsOffset;
		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			int localVariableNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
			if (localContentsOffset + 8 >= this.contents.length) {
				resizeContents(8);
			}
			this.contents[localContentsOffset++] = (byte) (localVariableNameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) localVariableNameIndex;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 2;
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
			attributesNumber++;
		}

		this.contentsOffset = localContentsOffset;

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0) {
			attributesNumber += generateStackMapTableAttribute(
					null,
					code_length,
					codeAttributeOffset,
					max_locals,
					true);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			attributesNumber += generateStackMapAttribute(
					null,
					code_length,
					codeAttributeOffset,
					max_locals,
					true);
		}

		// update the number of attributes
		// ensure first that there is enough space available inside the contents array
		if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[codeAttributeAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributesNumber;
		// update the attribute length
		int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
	}


	/**
	 *
	 */
	public void completeCodeAttributeForMissingAbstractProblemMethod(
			MethodBinding binding,
			int codeAttributeOffset,
			int[] startLineIndexes,
			int problemLine) {
		// reinitialize the localContents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside localContents byte array before we started to write// any information about the codeAttribute// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset// to get the right position, 6 for the max_stack etc...
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		int code_length = this.codeStream.position;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;
		// write the exception table
		if (localContentsOffset + 50 >= this.contents.length) {
			resizeContents(50);
		}
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributesNumber = 0; // leave two bytes for the attribute_length
		localContentsOffset += 2; // first we handle the linenumber attribute
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		this.contentsOffset = localContentsOffset;
		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			if (problemLine == 0) {
				problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length-1);
			}
			attributesNumber += generateLineNumberAttribute(problemLine);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0) {
			attributesNumber += generateStackMapTableAttribute(
					binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			attributesNumber += generateStackMapAttribute(
					binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		// then we do the local variable attribute
		// update the number of attributes// ensure first that there is enough space available inside the localContents array
		if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[codeAttributeAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributesNumber;
		// update the attribute length
		int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method completes the creation of the code attribute by setting
	 * - the attribute_length
	 * - max_stack
	 * - max_locals
	 * - code_length
	 * - exception table
	 * - and debug attributes if necessary.
	 *
	 * @param codeAttributeOffset <CODE>int</CODE>
	 */
	public void completeCodeAttributeForProblemMethod(
			AbstractMethodDeclaration method,
			MethodBinding binding,
			int codeAttributeOffset,
			int[] startLineIndexes,
			int problemLine) {
		// reinitialize the localContents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside localContents byte array before we started to write// any information about the codeAttribute// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset// to get the right position, 6 for the max_stack etc...
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		int code_length = this.codeStream.position;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;
		// write the exception table
		if (localContentsOffset + 50 >= this.contents.length) {
			resizeContents(50);
		}

		// write the exception table
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributesNumber = 0; // leave two bytes for the attribute_length
		localContentsOffset += 2; // first we handle the linenumber attribute
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		this.contentsOffset = localContentsOffset;
		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			if (problemLine == 0) {
				problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length-1);
			}
			attributesNumber += generateLineNumberAttribute(problemLine);
		}

		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			final boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
			attributesNumber += generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, false);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0) {
			attributesNumber += generateStackMapTableAttribute(
					binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			attributesNumber += generateStackMapAttribute(
					binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		// update the number of attributes// ensure first that there is enough space available inside the localContents array
		if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[codeAttributeAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributesNumber;
		// update the attribute length
		int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method completes the creation of the code attribute by setting
	 * - the attribute_length
	 * - max_stack
	 * - max_locals
	 * - code_length
	 * - exception table
	 * - and debug attributes if necessary.
	 *
	 * @param binding org.eclipse.jdt.internal.compiler.lookup.SyntheticAccessMethodBinding
	 * @param codeAttributeOffset <CODE>int</CODE>
	 */
	public void completeCodeAttributeForSyntheticMethod(
			boolean hasExceptionHandlers,
			SyntheticMethodBinding binding,
			int codeAttributeOffset,
			int[] startLineIndexes) {
		// reinitialize the contents with the byte modified by the code stream
		this.contents = this.codeStream.bCodeStream;
		int localContentsOffset = this.codeStream.classFileOffset;
		// codeAttributeOffset is the position inside contents byte array before we started to write
		// any information about the codeAttribute
		// That means that to write the attribute_length you need to offset by 2 the value of codeAttributeOffset
		// to get the right position, 6 for the max_stack etc...
		int max_stack = this.codeStream.stackMax;
		this.contents[codeAttributeOffset + 6] = (byte) (max_stack >> 8);
		this.contents[codeAttributeOffset + 7] = (byte) max_stack;
		int max_locals = this.codeStream.maxLocals;
		this.contents[codeAttributeOffset + 8] = (byte) (max_locals >> 8);
		this.contents[codeAttributeOffset + 9] = (byte) max_locals;
		int code_length = this.codeStream.position;
		this.contents[codeAttributeOffset + 10] = (byte) (code_length >> 24);
		this.contents[codeAttributeOffset + 11] = (byte) (code_length >> 16);
		this.contents[codeAttributeOffset + 12] = (byte) (code_length >> 8);
		this.contents[codeAttributeOffset + 13] = (byte) code_length;
		if ((localContentsOffset + 40) >= this.contents.length) {
			resizeContents(40);
		}

		boolean addStackMaps = (this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP_TABLE) != 0;
		if (hasExceptionHandlers) {
			// write the exception table
			ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
			int exceptionHandlersCount = 0; // each label holds one handler per range (start/end contiguous)
			for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
				exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
			}
			int exSize = exceptionHandlersCount * 8 + 2;
			if (exSize + localContentsOffset >= this.contents.length) {
				resizeContents(exSize);
			}
			// there is no exception table, so we need to offset by 2 the current offset and move
			// on the attribute generation
			this.contents[localContentsOffset++] = (byte) (exceptionHandlersCount >> 8);
			this.contents[localContentsOffset++] = (byte) exceptionHandlersCount;
			for (int i = 0, max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
				ExceptionLabel exceptionLabel = exceptionLabels[i];
				if (exceptionLabel != null) {
					int iRange = 0, maxRange = exceptionLabel.getCount();
					if ((maxRange & 1) != 0) {
						this.referenceBinding.scope.problemReporter().abortDueToInternalError(
								Messages.bind(Messages.abort_invalidExceptionAttribute, new String(binding.selector),
										this.referenceBinding.scope.problemReporter().referenceContext));
					}
					while  (iRange < maxRange) {
						int start = exceptionLabel.ranges[iRange++]; // even ranges are start positions
						this.contents[localContentsOffset++] = (byte) (start >> 8);
						this.contents[localContentsOffset++] = (byte) start;
						int end = exceptionLabel.ranges[iRange++]; // odd ranges are end positions
						this.contents[localContentsOffset++] = (byte) (end >> 8);
						this.contents[localContentsOffset++] = (byte) end;
						int handlerPC = exceptionLabel.position;
						if (addStackMaps) {
							StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
							stackMapFrameCodeStream.addFramePosition(handlerPC);
						}
						this.contents[localContentsOffset++] = (byte) (handlerPC >> 8);
						this.contents[localContentsOffset++] = (byte) handlerPC;
						if (exceptionLabel.exceptionType == null) {
							// any exception handler
							this.contents[localContentsOffset++] = 0;
							this.contents[localContentsOffset++] = 0;
						} else {
							int nameIndex;
							switch(exceptionLabel.exceptionType.id) {
								case T_null :
									/* represents ClassNotFoundException, see class literal access*/
									nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
									break;
								case T_long :
									/* represents NoSuchFieldError, see switch table generation*/
									nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangNoSuchFieldErrorConstantPoolName);
									break;
								default:
									nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
							}
							this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
							this.contents[localContentsOffset++] = (byte) nameIndex;
						}
					}
				}
			}
		} else {
			// there is no exception table, so we need to offset by 2 the current offset and move
			// on the attribute generation
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = 0;
		}
		// debug attributes
		int codeAttributeAttributeOffset = localContentsOffset;
		int attributesNumber = 0;
		// leave two bytes for the attribute_length
		localContentsOffset += 2;
		if (localContentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}

		this.contentsOffset = localContentsOffset;
		// first we handle the linenumber attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_LINES) != 0) {
			int lineNumber = Util.getLineNumber(binding.sourceStart, startLineIndexes, 0, startLineIndexes.length-1);
			attributesNumber += generateLineNumberAttribute(lineNumber);
		}
		// then we do the local variable attribute
		if ((this.produceAttributes & ClassFileConstants.ATTR_VARS) != 0) {
			final boolean methodDeclarationIsStatic = binding.isStatic();
			attributesNumber += generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, true);
		}
		if (addStackMaps) {
			attributesNumber += generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
		}

		if ((this.produceAttributes & ClassFileConstants.ATTR_STACK_MAP) != 0) {
			attributesNumber += generateStackMapAttribute(
					binding,
					code_length,
					codeAttributeOffset,
					max_locals,
					false);
		}

		// update the number of attributes
		// ensure first that there is enough space available inside the contents array
		if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		this.contents[codeAttributeAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[codeAttributeAttributeOffset] = (byte) attributesNumber;

		// update the attribute length
		int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
		this.contents[codeAttributeOffset + 2] = (byte) (codeAttributeLength >> 24);
		this.contents[codeAttributeOffset + 3] = (byte) (codeAttributeLength >> 16);
		this.contents[codeAttributeOffset + 4] = (byte) (codeAttributeLength >> 8);
		this.contents[codeAttributeOffset + 5] = (byte) codeAttributeLength;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method completes the creation of the code attribute by setting
	 * - the attribute_length
	 * - max_stack
	 * - max_locals
	 * - code_length
	 * - exception table
	 * - and debug attributes if necessary.
	 *
	 * @param binding org.eclipse.jdt.internal.compiler.lookup.SyntheticAccessMethodBinding
	 * @param codeAttributeOffset <CODE>int</CODE>
	 */
	public void completeCodeAttributeForSyntheticMethod(
			SyntheticMethodBinding binding,
			int codeAttributeOffset,
			int[] startLineIndexes) {

		this.completeCodeAttributeForSyntheticMethod(
				false,
				binding,
				codeAttributeOffset,
				startLineIndexes);
	}

	/**
	 * INTERNAL USE-ONLY
	 * Complete the creation of a method info by setting up the number of attributes at the right offset.
	 *
	 * @param methodAttributeOffset <CODE>int</CODE>
	 * @param attributesNumber <CODE>int</CODE>
	 */
	public void completeMethodInfo(
			MethodBinding binding,
			int methodAttributeOffset,
			int attributesNumber) {
		// update the number of attributes
		this.contents[methodAttributeOffset++] = (byte) (attributesNumber >> 8);
		this.contents[methodAttributeOffset] = (byte) attributesNumber;
	}
	/**
	 * INTERNAL USE-ONLY
	 * This methods returns a char[] representing the file name of the receiver
	 *
	 * @return char[]
	 */
	public char[] fileName() {
		return this.constantPool.UTF8Cache.returnKeyFor(2);
	}

	private void generateAnnotation(Annotation annotation, int currentOffset) {
		int startingContentsOffset = currentOffset;
		if (this.contentsOffset + 4 >= this.contents.length) {
			resizeContents(4);
		}
		TypeBinding annotationTypeBinding = annotation.resolvedType;
		if (annotationTypeBinding == null) {
			this.contentsOffset = startingContentsOffset;
			return;
		}
		final int typeIndex = this.constantPool.literalIndex(annotationTypeBinding.signature());
		this.contents[this.contentsOffset++] = (byte) (typeIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) typeIndex;
		if (annotation instanceof NormalAnnotation) {
			NormalAnnotation normalAnnotation = (NormalAnnotation) annotation;
			MemberValuePair[] memberValuePairs = normalAnnotation.memberValuePairs;
			if (memberValuePairs != null) {
				final int memberValuePairsLength = memberValuePairs.length;
				this.contents[this.contentsOffset++] = (byte) (memberValuePairsLength >> 8);
				this.contents[this.contentsOffset++] = (byte) memberValuePairsLength;
				for (int i = 0; i < memberValuePairsLength; i++) {
					MemberValuePair memberValuePair = memberValuePairs[i];
					if (this.contentsOffset + 2 >= this.contents.length) {
						resizeContents(2);
					}
					final int elementNameIndex = this.constantPool.literalIndex(memberValuePair.name);
					this.contents[this.contentsOffset++] = (byte) (elementNameIndex >> 8);
					this.contents[this.contentsOffset++] = (byte) elementNameIndex;
					MethodBinding methodBinding = memberValuePair.binding;
					if (methodBinding == null) {
						this.contentsOffset = startingContentsOffset;
					} else {
						try {
							generateElementValue(memberValuePair.value, methodBinding.returnType, startingContentsOffset);
						} catch(ClassCastException e) {
							this.contentsOffset = startingContentsOffset;
						} catch(ShouldNotImplement e) {
							this.contentsOffset = startingContentsOffset;
						}
					}
				}
			} else {
				this.contents[this.contentsOffset++] = 0;
				this.contents[this.contentsOffset++] = 0;
			}
		} else if (annotation instanceof SingleMemberAnnotation) {
			SingleMemberAnnotation singleMemberAnnotation = (SingleMemberAnnotation) annotation;
			// this is a single member annotation (one member value)
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 1;
			if (this.contentsOffset + 2 >= this.contents.length) {
				resizeContents(2);
			}
			final int elementNameIndex = this.constantPool.literalIndex(VALUE);
			this.contents[this.contentsOffset++] = (byte) (elementNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) elementNameIndex;
			MethodBinding methodBinding = singleMemberAnnotation.memberValuePairs()[0].binding;
			if (methodBinding == null) {
				this.contentsOffset = startingContentsOffset;
			} else {
				try {
					generateElementValue(singleMemberAnnotation.memberValue, methodBinding.returnType, startingContentsOffset);
				} catch(ClassCastException e) {
					this.contentsOffset = startingContentsOffset;
				} catch(ShouldNotImplement e) {
					this.contentsOffset = startingContentsOffset;
				}
			}
		} else {
			// this is a marker annotation (no member value pairs)
			this.contents[this.contentsOffset++] = 0;
			this.contents[this.contentsOffset++] = 0;
		}
	}

	private int generateAnnotationDefaultAttribute(AnnotationMethodDeclaration declaration, int attributeOffset) {
		int attributesNumber = 0;
		// add an annotation default attribute
		int annotationDefaultNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.AnnotationDefaultName);
		if (this.contentsOffset + 6 >= this.contents.length) {
			resizeContents(6);
		}
		this.contents[this.contentsOffset++] = (byte) (annotationDefaultNameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) annotationDefaultNameIndex;
		int attributeLengthOffset = this.contentsOffset;
		this.contentsOffset += 4;
		generateElementValue(declaration.defaultValue, declaration.binding.returnType, attributeOffset);
		if (this.contentsOffset != attributeOffset) {
			int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
			this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 24);
			this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 16);
			this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 8);
			this.contents[attributeLengthOffset++] = (byte) attributeLength;
			attributesNumber++;
		}
		return attributesNumber;
	}
	/**
	 * INTERNAL USE-ONLY
	 * That method generates the header of a code attribute.
	 * - the index inside the constant pool for the attribute name ("Code")
	 * - leave some space for attribute_length(4), max_stack(2), max_locals(2), code_length(4).
	 */
	public void generateCodeAttributeHeader() {
		if (this.contentsOffset + 20 >= this.contents.length) {
			resizeContents(20);
		}
		int constantValueNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.CodeName);
		this.contents[this.contentsOffset++] = (byte) (constantValueNameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) constantValueNameIndex;
		// leave space for attribute_length(4), max_stack(2), max_locals(2), code_length(4)
		this.contentsOffset += 12;
	}
	
	private int generateConstantValueAttribute(Constant fieldConstant, FieldBinding fieldBinding, int fieldAttributeOffset) {
		int localContentsOffset = this.contentsOffset;
		int attributesNumber = 1;
		if (localContentsOffset + 8 >= this.contents.length) {
			resizeContents(8);
		}
		// Now we generate the constant attribute corresponding to the fieldBinding
		int constantValueNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.ConstantValueName);
		this.contents[localContentsOffset++] = (byte) (constantValueNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) constantValueNameIndex;
		// The attribute length = 2 in case of a constantValue attribute
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 2;
		// Need to add the constant_value_index
		switch (fieldConstant.typeID()) {
			case T_boolean :
				int booleanValueIndex =
					this.constantPool.literalIndex(fieldConstant.booleanValue() ? 1 : 0);
				this.contents[localContentsOffset++] = (byte) (booleanValueIndex >> 8);
				this.contents[localContentsOffset++] = (byte) booleanValueIndex;
				break;
			case T_byte :
			case T_char :
			case T_int :
			case T_short :
				int integerValueIndex =
					this.constantPool.literalIndex(fieldConstant.intValue());
				this.contents[localContentsOffset++] = (byte) (integerValueIndex >> 8);
				this.contents[localContentsOffset++] = (byte) integerValueIndex;
				break;
			case T_float :
				int floatValueIndex =
					this.constantPool.literalIndex(fieldConstant.floatValue());
				this.contents[localContentsOffset++] = (byte) (floatValueIndex >> 8);
				this.contents[localContentsOffset++] = (byte) floatValueIndex;
				break;
			case T_double :
				int doubleValueIndex =
					this.constantPool.literalIndex(fieldConstant.doubleValue());
				this.contents[localContentsOffset++] = (byte) (doubleValueIndex >> 8);
				this.contents[localContentsOffset++] = (byte) doubleValueIndex;
				break;
			case T_long :
				int longValueIndex =
					this.constantPool.literalIndex(fieldConstant.longValue());
				this.contents[localContentsOffset++] = (byte) (longValueIndex >> 8);
				this.contents[localContentsOffset++] = (byte) longValueIndex;
				break;
			case T_JavaLangString :
				int stringValueIndex =
					this.constantPool.literalIndex(
						((StringConstant) fieldConstant).stringValue());
				if (stringValueIndex == -1) {
					if (!this.creatingProblemType) {
						// report an error and abort: will lead to a problem type classfile creation
						TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
						FieldDeclaration[] fieldDecls = typeDeclaration.fields;
						int max = fieldDecls == null ? 0 : fieldDecls.length;
						for (int i = 0; i < max; i++) {
							if (fieldDecls[i].binding == fieldBinding) {
								// problem should abort
								typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(
									fieldDecls[i]);
							}
						}
					} else {
						// already inside a problem type creation : no constant for this field
						this.contentsOffset = fieldAttributeOffset;
						attributesNumber = 0;
					}
				} else {
					this.contents[localContentsOffset++] = (byte) (stringValueIndex >> 8);
					this.contents[localContentsOffset++] = (byte) stringValueIndex;
				}
		}
		this.contentsOffset = localContentsOffset;
		return attributesNumber;
	}
	private int generateDeprecatedAttribute() {
		int localContentsOffset = this.contentsOffset;
		if (localContentsOffset + 6 >= this.contents.length) {
			resizeContents(6);
		}
		int deprecatedAttributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
		this.contents[localContentsOffset++] = (byte) (deprecatedAttributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) deprecatedAttributeNameIndex;
		// the length of a deprecated attribute is equals to 0
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	private void generateElementValue(
			Expression defaultValue,
			TypeBinding memberValuePairReturnType,
			int attributeOffset) {
		Constant constant = defaultValue.constant;
		TypeBinding defaultValueBinding = defaultValue.resolvedType;
		if (defaultValueBinding == null) {
			this.contentsOffset = attributeOffset;
		} else {
			if (memberValuePairReturnType.isArrayType() && !defaultValueBinding.isArrayType()) {
				// automatic wrapping
				if (this.contentsOffset + 3 >= this.contents.length) {
					resizeContents(3);
				}
				this.contents[this.contentsOffset++] = (byte) '[';
				this.contents[this.contentsOffset++] = (byte) 0;
				this.contents[this.contentsOffset++] = (byte) 1;
			}
			if (constant != null && constant != Constant.NotAConstant) {
				generateElementValue(attributeOffset, defaultValue, constant, memberValuePairReturnType.leafComponentType());
			} else {
				generateElementValueForNonConstantExpression(defaultValue, attributeOffset, defaultValueBinding);
			}
		}
	}
	/**
	 * @param attributeOffset
	 */
	private void generateElementValue(int attributeOffset, Expression defaultValue, Constant constant, TypeBinding binding) {
		if (this.contentsOffset + 3 >= this.contents.length) {
			resizeContents(3);
		}
		switch (binding.id) {
			case T_boolean :
				this.contents[this.contentsOffset++] = (byte) 'Z';
				int booleanValueIndex =
					this.constantPool.literalIndex(constant.booleanValue() ? 1 : 0);
				this.contents[this.contentsOffset++] = (byte) (booleanValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) booleanValueIndex;
				break;
			case T_byte :
				this.contents[this.contentsOffset++] = (byte) 'B';
				int integerValueIndex =
					this.constantPool.literalIndex(constant.intValue());
				this.contents[this.contentsOffset++] = (byte) (integerValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) integerValueIndex;
				break;
			case T_char :
				this.contents[this.contentsOffset++] = (byte) 'C';
				integerValueIndex =
					this.constantPool.literalIndex(constant.intValue());
				this.contents[this.contentsOffset++] = (byte) (integerValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) integerValueIndex;
				break;
			case T_int :
				this.contents[this.contentsOffset++] = (byte) 'I';
				integerValueIndex =
					this.constantPool.literalIndex(constant.intValue());
				this.contents[this.contentsOffset++] = (byte) (integerValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) integerValueIndex;
				break;
			case T_short :
				this.contents[this.contentsOffset++] = (byte) 'S';
				integerValueIndex =
					this.constantPool.literalIndex(constant.intValue());
				this.contents[this.contentsOffset++] = (byte) (integerValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) integerValueIndex;
				break;
			case T_float :
				this.contents[this.contentsOffset++] = (byte) 'F';
				int floatValueIndex =
					this.constantPool.literalIndex(constant.floatValue());
				this.contents[this.contentsOffset++] = (byte) (floatValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) floatValueIndex;
				break;
			case T_double :
				this.contents[this.contentsOffset++] = (byte) 'D';
				int doubleValueIndex =
					this.constantPool.literalIndex(constant.doubleValue());
				this.contents[this.contentsOffset++] = (byte) (doubleValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) doubleValueIndex;
				break;
			case T_long :
				this.contents[this.contentsOffset++] = (byte) 'J';
				int longValueIndex =
					this.constantPool.literalIndex(constant.longValue());
				this.contents[this.contentsOffset++] = (byte) (longValueIndex >> 8);
				this.contents[this.contentsOffset++] = (byte) longValueIndex;
				break;
			case T_JavaLangString :
				this.contents[this.contentsOffset++] = (byte) 's';
				int stringValueIndex =
					this.constantPool.literalIndex(((StringConstant) constant).stringValue().toCharArray());
				if (stringValueIndex == -1) {
					if (!this.creatingProblemType) {
						// report an error and abort: will lead to a problem type classfile creation
						TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
						typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(defaultValue);
					} else {
						// already inside a problem type creation : no attribute
						this.contentsOffset = attributeOffset;
					}
				} else {
					this.contents[this.contentsOffset++] = (byte) (stringValueIndex >> 8);
					this.contents[this.contentsOffset++] = (byte) stringValueIndex;
				}
		}
	}
	
	private void generateElementValueForNonConstantExpression(Expression defaultValue, int attributeOffset, TypeBinding defaultValueBinding) {
		if (defaultValueBinding != null) {
			if (defaultValueBinding.isEnum()) {
				if (this.contentsOffset + 5 >= this.contents.length) {
					resizeContents(5);
				}
				this.contents[this.contentsOffset++] = (byte) 'e';
				FieldBinding fieldBinding = null;
				if (defaultValue instanceof QualifiedNameReference) {
					QualifiedNameReference nameReference = (QualifiedNameReference) defaultValue;
					fieldBinding = (FieldBinding) nameReference.binding;
				} else if (defaultValue instanceof SingleNameReference) {
					SingleNameReference nameReference = (SingleNameReference) defaultValue;
					fieldBinding = (FieldBinding) nameReference.binding;
				} else {
					this.contentsOffset = attributeOffset;
				}
				if (fieldBinding != null) {
					final int enumConstantTypeNameIndex = this.constantPool.literalIndex(fieldBinding.type.signature());
					final int enumConstantNameIndex = this.constantPool.literalIndex(fieldBinding.name);
					this.contents[this.contentsOffset++] = (byte) (enumConstantTypeNameIndex >> 8);
					this.contents[this.contentsOffset++] = (byte) enumConstantTypeNameIndex;
					this.contents[this.contentsOffset++] = (byte) (enumConstantNameIndex >> 8);
					this.contents[this.contentsOffset++] = (byte) enumConstantNameIndex;
				}
			} else if (defaultValueBinding.isAnnotationType()) {
				if (this.contentsOffset + 1 >= this.contents.length) {
					resizeContents(1);
				}
				this.contents[this.contentsOffset++] = (byte) '@';
				generateAnnotation((Annotation) defaultValue, attributeOffset);
			} else if (defaultValueBinding.isArrayType()) {
				// array type
				if (this.contentsOffset + 3 >= this.contents.length) {
					resizeContents(3);
				}
				this.contents[this.contentsOffset++] = (byte) '[';
				if (defaultValue instanceof ArrayInitializer) {
					ArrayInitializer arrayInitializer = (ArrayInitializer) defaultValue;
					int arrayLength = arrayInitializer.expressions != null ? arrayInitializer.expressions.length : 0;
					this.contents[this.contentsOffset++] = (byte) (arrayLength >> 8);
					this.contents[this.contentsOffset++] = (byte) arrayLength;
					for (int i = 0; i < arrayLength; i++) {
						generateElementValue(arrayInitializer.expressions[i], defaultValueBinding.leafComponentType(), attributeOffset);
					}
				} else {
					this.contentsOffset = attributeOffset;
				}
			} else {
				// class type
				if (this.contentsOffset + 3 >= this.contents.length) {
					resizeContents(3);
				}
				this.contents[this.contentsOffset++] = (byte) 'c';
				if (defaultValue instanceof ClassLiteralAccess) {
					ClassLiteralAccess classLiteralAccess = (ClassLiteralAccess) defaultValue;
					final int classInfoIndex = this.constantPool.literalIndex(classLiteralAccess.targetType.signature());
					this.contents[this.contentsOffset++] = (byte) (classInfoIndex >> 8);
					this.contents[this.contentsOffset++] = (byte) classInfoIndex;
				} else {
					this.contentsOffset = attributeOffset;
				}
			}
		} else {
			this.contentsOffset = attributeOffset;
		}
	}

	private int generateEnclosingMethodAttribute() {
		int localContentsOffset = this.contentsOffset;
		// add enclosing method attribute (1.5 mode only)
		if (localContentsOffset + 10 >= this.contents.length) {
			resizeContents(10);
		}
		int enclosingMethodAttributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.EnclosingMethodName);
		this.contents[localContentsOffset++] = (byte) (enclosingMethodAttributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) enclosingMethodAttributeNameIndex;
		// the length of a signature attribute is equals to 2
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 4;

		int enclosingTypeIndex = this.constantPool.literalIndexForType(this.referenceBinding.enclosingType().constantPoolName());
		this.contents[localContentsOffset++] = (byte) (enclosingTypeIndex >> 8);
		this.contents[localContentsOffset++] = (byte) enclosingTypeIndex;
		byte methodIndexByte1 = 0;
		byte methodIndexByte2 = 0;
		if (this.referenceBinding instanceof LocalTypeBinding) {
			MethodBinding methodBinding = ((LocalTypeBinding) this.referenceBinding).enclosingMethod;
			if (methodBinding != null) {
				int enclosingMethodIndex = this.constantPool.literalIndexForNameAndType(methodBinding.selector, methodBinding.signature(this));
				methodIndexByte1 = (byte) (enclosingMethodIndex >> 8);
				methodIndexByte2 = (byte) enclosingMethodIndex;
			}
		}
		this.contents[localContentsOffset++] = methodIndexByte1;
		this.contents[localContentsOffset++] = methodIndexByte2;
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	private int generateExceptionsAttribute(ReferenceBinding[] thrownsExceptions) {
		int localContentsOffset = this.contentsOffset;
		int length = thrownsExceptions.length;
		int exSize = 8 + length * 2;
		if (exSize + this.contentsOffset >= this.contents.length) {
			resizeContents(exSize);
		}
		int exceptionNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.ExceptionsName);
		this.contents[localContentsOffset++] = (byte) (exceptionNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) exceptionNameIndex;
		// The attribute length = length * 2 + 2 in case of a exception attribute
		int attributeLength = length * 2 + 2;
		this.contents[localContentsOffset++] = (byte) (attributeLength >> 24);
		this.contents[localContentsOffset++] = (byte) (attributeLength >> 16);
		this.contents[localContentsOffset++] = (byte) (attributeLength >> 8);
		this.contents[localContentsOffset++] = (byte) attributeLength;
		this.contents[localContentsOffset++] = (byte) (length >> 8);
		this.contents[localContentsOffset++] = (byte) length;
		for (int i = 0; i < length; i++) {
			int exceptionIndex = this.constantPool.literalIndexForType(thrownsExceptions[i]);
			this.contents[localContentsOffset++] = (byte) (exceptionIndex >> 8);
			this.contents[localContentsOffset++] = (byte) exceptionIndex;
		}
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	private int generateHierarchyInconsistentAttribute() {
		int localContentsOffset = this.contentsOffset;
		// add an attribute for inconsistent hierarchy
		if (localContentsOffset + 6 >= this.contents.length) {
			resizeContents(6);
		}
		int inconsistentHierarchyNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.InconsistentHierarchy);
		this.contents[localContentsOffset++] = (byte) (inconsistentHierarchyNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) inconsistentHierarchyNameIndex;
		// the length of an inconsistent hierarchy attribute is equals to 0
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	private int generateInnerClassAttribute(int numberOfInnerClasses, ReferenceBinding[] innerClasses) {
		int localContentsOffset = this.contentsOffset;
		// Generate the inner class attribute
		int exSize = 8 * numberOfInnerClasses + 8;
		if (exSize + localContentsOffset >= this.contents.length) {
			resizeContents(exSize);
		}
		// Now we now the size of the attribute and the number of entries
		// attribute name
		int attributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.InnerClassName);
		this.contents[localContentsOffset++] = (byte) (attributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) attributeNameIndex;
		int value = (numberOfInnerClasses << 3) + 2;
		this.contents[localContentsOffset++] = (byte) (value >> 24);
		this.contents[localContentsOffset++] = (byte) (value >> 16);
		this.contents[localContentsOffset++] = (byte) (value >> 8);
		this.contents[localContentsOffset++] = (byte) value;
		this.contents[localContentsOffset++] = (byte) (numberOfInnerClasses >> 8);
		this.contents[localContentsOffset++] = (byte) numberOfInnerClasses;
		for (int i = 0; i < numberOfInnerClasses; i++) {
			ReferenceBinding innerClass = innerClasses[i];
			int accessFlags = innerClass.getAccessFlags();
			int innerClassIndex = this.constantPool.literalIndexForType(innerClass.constantPoolName());
			// inner class index
			this.contents[localContentsOffset++] = (byte) (innerClassIndex >> 8);
			this.contents[localContentsOffset++] = (byte) innerClassIndex;
			// outer class index: anonymous and local have no outer class index
			if (innerClass.isMemberType()) {
				// member or member of local
				int outerClassIndex = this.constantPool.literalIndexForType(innerClass.enclosingType().constantPoolName());
				this.contents[localContentsOffset++] = (byte) (outerClassIndex >> 8);
				this.contents[localContentsOffset++] = (byte) outerClassIndex;
			} else {
				// equals to 0 if the innerClass is not a member type
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = 0;
			}
			// name index
			if (!innerClass.isAnonymousType()) {
				int nameIndex = this.constantPool.literalIndex(innerClass.sourceName());
				this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) nameIndex;
			} else {
				// equals to 0 if the innerClass is an anonymous type
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = 0;
			}
			// access flag
			if (innerClass.isAnonymousType()) {
				accessFlags &= ~ClassFileConstants.AccFinal;
			} else if (innerClass.isMemberType() && innerClass.isInterface()) {
				accessFlags |= ClassFileConstants.AccStatic; // implicitely static
			}
			this.contents[localContentsOffset++] = (byte) (accessFlags >> 8);
			this.contents[localContentsOffset++] = (byte) accessFlags;
		}
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	private int generateLineNumberAttribute() {
		int localContentsOffset = this.contentsOffset;
		int attributesNumber = 0;
		/* Create and add the line number attribute (used for debugging)
		 * Build the pairs of:
		 * 	(bytecodePC lineNumber)
		 * according to the table of start line indexes and the pcToSourceMap table
		 * contained into the codestream
		 */
		int[] pcToSourceMapTable;
		if (((pcToSourceMapTable = this.codeStream.pcToSourceMap) != null)
			&& (this.codeStream.pcToSourceMapSize != 0)) {
			int lineNumberNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
			if (localContentsOffset + 8 >= this.contents.length) {
				resizeContents(8);
			}
			this.contents[localContentsOffset++] = (byte) (lineNumberNameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) lineNumberNameIndex;
			int lineNumberTableOffset = localContentsOffset;
			localContentsOffset += 6;
			// leave space for attribute_length and line_number_table_length
			int numberOfEntries = 0;
			int length = this.codeStream.pcToSourceMapSize;
			for (int i = 0; i < length;) {
				// write the entry
				if (localContentsOffset + 4 >= this.contents.length) {
					resizeContents(4);
				}
				int pc = pcToSourceMapTable[i++];
				this.contents[localContentsOffset++] = (byte) (pc >> 8);
				this.contents[localContentsOffset++] = (byte) pc;
				int lineNumber = pcToSourceMapTable[i++];
				this.contents[localContentsOffset++] = (byte) (lineNumber >> 8);
				this.contents[localContentsOffset++] = (byte) lineNumber;
				numberOfEntries++;
			}
			// now we change the size of the line number attribute
			int lineNumberAttr_length = numberOfEntries * 4 + 2;
			this.contents[lineNumberTableOffset++] = (byte) (lineNumberAttr_length >> 24);
			this.contents[lineNumberTableOffset++] = (byte) (lineNumberAttr_length >> 16);
			this.contents[lineNumberTableOffset++] = (byte) (lineNumberAttr_length >> 8);
			this.contents[lineNumberTableOffset++] = (byte) lineNumberAttr_length;
			this.contents[lineNumberTableOffset++] = (byte) (numberOfEntries >> 8);
			this.contents[lineNumberTableOffset++] = (byte) numberOfEntries;
			attributesNumber = 1;
		}
		this.contentsOffset = localContentsOffset;
		return attributesNumber;
	}
	// this is used for problem and synthetic methods
	private int generateLineNumberAttribute(int problemLine) {
		int localContentsOffset = this.contentsOffset;
		if (localContentsOffset + 12 >= this.contents.length) {
			resizeContents(12);
		}
		/* Create and add the line number attribute (used for debugging)
		 * Build the pairs of:
		 * (bytecodePC lineNumber)
		 * according to the table of start line indexes and the pcToSourceMap table
		 * contained into the codestream
		 */
		int lineNumberNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
		this.contents[localContentsOffset++] = (byte) (lineNumberNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) lineNumberNameIndex;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 6;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 1;
		// first entry at pc = 0
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = (byte) (problemLine >> 8);
		this.contents[localContentsOffset++] = (byte) problemLine;
		// now we change the size of the line number attribute
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	
	private int generateLocalVariableTableAttribute(int code_length, boolean methodDeclarationIsStatic, boolean isSynthetic) {
		int attributesNumber = 0;
		int localContentsOffset = this.contentsOffset;
		int numberOfEntries = 0;
		int localVariableNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
		int maxOfEntries = 8 + 10 * (methodDeclarationIsStatic ? 0 : 1);
		for (int i = 0; i < this.codeStream.allLocalsCounter; i++) {
			LocalVariableBinding localVariableBinding = this.codeStream.locals[i];
			maxOfEntries += 10 * localVariableBinding.initializationCount;
		}
		// reserve enough space
		if (localContentsOffset + maxOfEntries >= this.contents.length) {
			resizeContents(maxOfEntries);
		}
		this.contents[localContentsOffset++] = (byte) (localVariableNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) localVariableNameIndex;
		int localVariableTableOffset = localContentsOffset;
		// leave space for attribute_length and local_variable_table_length
		localContentsOffset += 6;
		int nameIndex;
		int descriptorIndex;
		SourceTypeBinding declaringClassBinding = null;
		if (!methodDeclarationIsStatic && !isSynthetic) {
			numberOfEntries++;
			this.contents[localContentsOffset++] = 0; // the startPC for this is always 0
			this.contents[localContentsOffset++] = 0;
			this.contents[localContentsOffset++] = (byte) (code_length >> 8);
			this.contents[localContentsOffset++] = (byte) code_length;
			nameIndex = this.constantPool.literalIndex(ConstantPool.This);
			this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) nameIndex;
			declaringClassBinding = (SourceTypeBinding) this.codeStream.methodDeclaration.binding.declaringClass;
			descriptorIndex =
				this.constantPool.literalIndex(
					declaringClassBinding.signature());
			this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
			this.contents[localContentsOffset++] = (byte) descriptorIndex;
			this.contents[localContentsOffset++] = 0;// the resolved position for this is always 0
			this.contents[localContentsOffset++] = 0;
		}
		// used to remember the local variable with a generic type
		int genericLocalVariablesCounter = 0;
		LocalVariableBinding[] genericLocalVariables = null;
		int numberOfGenericEntries = 0;

		for (int i = 0, max = this.codeStream.allLocalsCounter; i < max; i++) {
			LocalVariableBinding localVariable = this.codeStream.locals[i];
			int initializationCount = localVariable.initializationCount;
			if (initializationCount == 0) continue;
			if (localVariable.declaration == null) continue;
			final TypeBinding localVariableTypeBinding = localVariable.type;
			boolean isParameterizedType = localVariableTypeBinding.isParameterizedType() || localVariableTypeBinding.isTypeVariable();
			if (isParameterizedType) {
				if (genericLocalVariables == null) {
					// we cannot have more than max locals
					genericLocalVariables = new LocalVariableBinding[max];
				}
				genericLocalVariables[genericLocalVariablesCounter++] = localVariable;
			}
			for (int j = 0; j < initializationCount; j++) {
				int startPC = localVariable.initializationPCs[j << 1];
				int endPC = localVariable.initializationPCs[(j << 1) + 1];
				if (startPC != endPC) { // only entries for non zero length
					if (endPC == -1) {
						localVariable.declaringScope.problemReporter().abortDueToInternalError(
								Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)),
								(ASTNode) localVariable.declaringScope.methodScope().referenceContext);
					}
					if (isParameterizedType) {
						numberOfGenericEntries++;
					}
					// now we can safely add the local entry
					numberOfEntries++;
					this.contents[localContentsOffset++] = (byte) (startPC >> 8);
					this.contents[localContentsOffset++] = (byte) startPC;
					int length = endPC - startPC;
					this.contents[localContentsOffset++] = (byte) (length >> 8);
					this.contents[localContentsOffset++] = (byte) length;
					nameIndex = this.constantPool.literalIndex(localVariable.name);
					this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
					this.contents[localContentsOffset++] = (byte) nameIndex;
					descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
					this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
					this.contents[localContentsOffset++] = (byte) descriptorIndex;
					int resolvedPosition = localVariable.resolvedPosition;
					this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
					this.contents[localContentsOffset++] = (byte) resolvedPosition;
				}
			}
		}
		int value = numberOfEntries * 10 + 2;
		this.contents[localVariableTableOffset++] = (byte) (value >> 24);
		this.contents[localVariableTableOffset++] = (byte) (value >> 16);
		this.contents[localVariableTableOffset++] = (byte) (value >> 8);
		this.contents[localVariableTableOffset++] = (byte) value;
		this.contents[localVariableTableOffset++] = (byte) (numberOfEntries >> 8);
		this.contents[localVariableTableOffset] = (byte) numberOfEntries;
		attributesNumber++;

		final boolean currentInstanceIsGeneric =
			!methodDeclarationIsStatic
			&& declaringClassBinding != null
			&& declaringClassBinding.typeVariables != Binding.NO_TYPE_VARIABLES;
		if (genericLocalVariablesCounter != 0 || currentInstanceIsGeneric) {
			// add the local variable type table attribute
			numberOfGenericEntries += (currentInstanceIsGeneric ? 1 : 0);
			maxOfEntries = 8 + numberOfGenericEntries * 10;
			// reserve enough space
			if (localContentsOffset + maxOfEntries >= this.contents.length) {
				resizeContents(maxOfEntries);
			}
			int localVariableTypeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
			this.contents[localContentsOffset++] = (byte) (localVariableTypeNameIndex >> 8);
			this.contents[localContentsOffset++] = (byte) localVariableTypeNameIndex;
			value = numberOfGenericEntries * 10 + 2;
			this.contents[localContentsOffset++] = (byte) (value >> 24);
			this.contents[localContentsOffset++] = (byte) (value >> 16);
			this.contents[localContentsOffset++] = (byte) (value >> 8);
			this.contents[localContentsOffset++] = (byte) value;
			this.contents[localContentsOffset++] = (byte) (numberOfGenericEntries >> 8);
			this.contents[localContentsOffset++] = (byte) numberOfGenericEntries;
			if (currentInstanceIsGeneric) {
				this.contents[localContentsOffset++] = 0; // the startPC for this is always 0
				this.contents[localContentsOffset++] = 0;
				this.contents[localContentsOffset++] = (byte) (code_length >> 8);
				this.contents[localContentsOffset++] = (byte) code_length;
				nameIndex = this.constantPool.literalIndex(ConstantPool.This);
				this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) nameIndex;
				descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
				this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
				this.contents[localContentsOffset++] = (byte) descriptorIndex;
				this.contents[localContentsOffset++] = 0;// the resolved position for this is always 0
				this.contents[localContentsOffset++] = 0;
			}

			for (int i = 0; i < genericLocalVariablesCounter; i++) {
				LocalVariableBinding localVariable = genericLocalVariables[i];
				for (int j = 0; j < localVariable.initializationCount; j++) {
					int startPC = localVariable.initializationPCs[j << 1];
					int endPC = localVariable.initializationPCs[(j << 1) + 1];
					if (startPC != endPC) {
						// only entries for non zero length
						// now we can safely add the local entry
						this.contents[localContentsOffset++] = (byte) (startPC >> 8);
						this.contents[localContentsOffset++] = (byte) startPC;
						int length = endPC - startPC;
						this.contents[localContentsOffset++] = (byte) (length >> 8);
						this.contents[localContentsOffset++] = (byte) length;
						nameIndex = this.constantPool.literalIndex(localVariable.name);
						this.contents[localContentsOffset++] = (byte) (nameIndex >> 8);
						this.contents[localContentsOffset++] = (byte) nameIndex;
						descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
						this.contents[localContentsOffset++] = (byte) (descriptorIndex >> 8);
						this.contents[localContentsOffset++] = (byte) descriptorIndex;
						int resolvedPosition = localVariable.resolvedPosition;
						this.contents[localContentsOffset++] = (byte) (resolvedPosition >> 8);
						this.contents[localContentsOffset++] = (byte) resolvedPosition;
					}
				}
			}
			attributesNumber++;
		}
		this.contentsOffset = localContentsOffset;
		return attributesNumber;
	}
	/**
	 * INTERNAL USE-ONLY
	 * That method generates the attributes of a code attribute.
	 * They could be:
	 * - an exception attribute for each try/catch found inside the method
	 * - a deprecated attribute
	 * - a synthetic attribute for synthetic access methods
	 *
	 * It returns the number of attributes created for the code attribute.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.lookup.MethodBinding
	 * @return <CODE>int</CODE>
	 */
	public int generateMethodInfoAttributes(MethodBinding methodBinding) {
		// leave two bytes for the attribute_number
		this.contentsOffset += 2;
		if (this.contentsOffset + 2 >= this.contents.length) {
			resizeContents(2);
		}
		// now we can handle all the attribute for that method info:
		// it could be:
		// - a CodeAttribute
		// - a ExceptionAttribute
		// - a DeprecatedAttribute
		// - a SyntheticAttribute

		// Exception attribute
		ReferenceBinding[] thrownsExceptions;
		int attributesNumber = 0;
		if ((thrownsExceptions = methodBinding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
			// The method has a throw clause. So we need to add an exception attribute
			// check that there is enough space to write all the bytes for the exception attribute
			attributesNumber += generateExceptionsAttribute(thrownsExceptions);
		}
		if (methodBinding.isDeprecated()) {
			// Deprecated attribute
			attributesNumber += generateDeprecatedAttribute();
		}
		if (this.targetJDK < ClassFileConstants.JDK1_5) {
			if (methodBinding.isSynthetic()) {
				attributesNumber += generateSyntheticAttribute();
			}
			if (methodBinding.isVarargs()) {
				attributesNumber += generateVarargsAttribute();
			}
		}
		// add signature attribute
		char[] genericSignature = methodBinding.genericSignature();
		if (genericSignature != null) {
			attributesNumber += generateSignatureAttribute(genericSignature);
		}
		if (this.targetJDK >= ClassFileConstants.JDK1_4) {
			AbstractMethodDeclaration methodDeclaration = methodBinding.sourceMethod();
			if (methodDeclaration != null) {
				Annotation[] annotations = methodDeclaration.annotations;
				if (annotations != null) {
					attributesNumber += generateRuntimeAnnotations(annotations);
				}
				if ((methodBinding.tagBits & TagBits.HasParameterAnnotations) != 0) {
					Argument[] arguments = methodDeclaration.arguments;
					if (arguments != null) {
						attributesNumber += generateRuntimeAnnotationsForParameters(arguments);
					}
				}
			}
		}
		if ((methodBinding.tagBits & TagBits.HasMissingType) != 0) {
			this.missingTypes = methodBinding.collectMissingTypes(this.missingTypes);
		}
		return attributesNumber;
	}
	public int generateMethodInfoAttributes(MethodBinding methodBinding, AnnotationMethodDeclaration declaration) {
		int attributesNumber = generateMethodInfoAttributes(methodBinding);
		int attributeOffset = this.contentsOffset;
		if ((declaration.modifiers & ClassFileConstants.AccAnnotationDefault) != 0) {
			// add an annotation default attribute
			attributesNumber += generateAnnotationDefaultAttribute(declaration, attributeOffset);
		}
		return attributesNumber;
	}
	/**
	 * INTERNAL USE-ONLY
	 * That method generates the header of a method info:
	 * The header consists in:
	 * - the access flags
	 * - the name index of the method name inside the constant pool
	 * - the descriptor index of the signature of the method inside the constant pool.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.lookup.MethodBinding
	 */
	public void generateMethodInfoHeader(MethodBinding methodBinding) {
		generateMethodInfoHeader(methodBinding, methodBinding.modifiers);
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method generates the header of a method info:
	 * The header consists in:
	 * - the access flags
	 * - the name index of the method name inside the constant pool
	 * - the descriptor index of the signature of the method inside the constant pool.
	 *
	 * @param methodBinding org.eclipse.jdt.internal.compiler.lookup.MethodBinding
	 * @param accessFlags the access flags
	 */
	public void generateMethodInfoHeader(MethodBinding methodBinding, int accessFlags) {
		// check that there is enough space to write all the bytes for the method info corresponding
		// to the @methodBinding
		this.methodCount++; // add one more method
		if (this.contentsOffset + 10 >= this.contents.length) {
			resizeContents(10);
		}
		if (this.targetJDK < ClassFileConstants.JDK1_5) {
			// pre 1.5, synthetic is an attribute, not a modifier
			// pre 1.5, varargs is an attribute, not a modifier (-target jsr14 mode)
			accessFlags &= ~(ClassFileConstants.AccSynthetic | ClassFileConstants.AccVarargs);
		}
		if ((methodBinding.tagBits & TagBits.ClearPrivateModifier) != 0) {
			accessFlags &= ~ClassFileConstants.AccPrivate;
		}
		this.contents[this.contentsOffset++] = (byte) (accessFlags >> 8);
		this.contents[this.contentsOffset++] = (byte) accessFlags;
		int nameIndex = this.constantPool.literalIndex(methodBinding.selector);
		this.contents[this.contentsOffset++] = (byte) (nameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) nameIndex;
		int descriptorIndex = this.constantPool.literalIndex(methodBinding.signature(this));
		this.contents[this.contentsOffset++] = (byte) (descriptorIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) descriptorIndex;
	}

	/**
	 * INTERNAL USE-ONLY
	 * That method generates the method info header of a clinit:
	 * The header consists in:
	 * - the access flags (always default access + static)
	 * - the name index of the method name (always <clinit>) inside the constant pool
	 * - the descriptor index of the signature (always ()V) of the method inside the constant pool.
	 */
	public void generateMethodInfoHeaderForClinit() {
		// check that there is enough space to write all the bytes for the method info corresponding
		// to the @methodBinding
		this.methodCount++; // add one more method
		if (this.contentsOffset + 10 >= this.contents.length) {
			resizeContents(10);
		}
		this.contents[this.contentsOffset++] = (byte) ((ClassFileConstants.AccDefault | ClassFileConstants.AccStatic) >> 8);
		this.contents[this.contentsOffset++] = (byte) (ClassFileConstants.AccDefault | ClassFileConstants.AccStatic);
		int nameIndex = this.constantPool.literalIndex(ConstantPool.Clinit);
		this.contents[this.contentsOffset++] = (byte) (nameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) nameIndex;
		int descriptorIndex =
			this.constantPool.literalIndex(ConstantPool.ClinitSignature);
		this.contents[this.contentsOffset++] = (byte) (descriptorIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) descriptorIndex;
		// We know that we won't get more than 1 attribute: the code attribute
		this.contents[this.contentsOffset++] = 0;
		this.contents[this.contentsOffset++] = 1;
	}

	/**
	 * INTERNAL USE-ONLY
	 * Generate the byte for problem method infos that correspond to missing abstract methods.
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=3179
	 *
	 * @param methodDeclarations Array of all missing abstract methods
	 */
	public void generateMissingAbstractMethods(MethodDeclaration[] methodDeclarations, CompilationResult compilationResult) {
		if (methodDeclarations != null) {
			TypeDeclaration currentDeclaration = this.referenceBinding.scope.referenceContext;
			int typeDeclarationSourceStart = currentDeclaration.sourceStart();
			int typeDeclarationSourceEnd = currentDeclaration.sourceEnd();
			for (int i = 0, max = methodDeclarations.length; i < max; i++) {
				MethodDeclaration methodDeclaration = methodDeclarations[i];
				MethodBinding methodBinding = methodDeclaration.binding;
				 String readableName = new String(methodBinding.readableName());
				 CategorizedProblem[] problems = compilationResult.problems;
				 int problemsCount = compilationResult.problemCount;
				for (int j = 0; j < problemsCount; j++) {
					CategorizedProblem problem = problems[j];
					if (problem != null
							&& problem.getID() == IProblem.AbstractMethodMustBeImplemented
							&& problem.getMessage().indexOf(readableName) != -1
							&& problem.getSourceStart() >= typeDeclarationSourceStart
							&& problem.getSourceEnd() <= typeDeclarationSourceEnd) {
						// we found a match
						addMissingAbstractProblemMethod(methodDeclaration, methodBinding, problem, compilationResult);
					}
				}
			}
		}
	}

	private void generateMissingTypesAttribute() {
		int initialSize = this.missingTypes.size();
		int[] missingTypesIndexes = new int[initialSize];
		int numberOfMissingTypes = 0;
		if (initialSize > 1) {
			Collections.sort(this.missingTypes, new Comparator() {
				public int compare(Object o1, Object o2) {
					TypeBinding typeBinding1 = (TypeBinding) o1;
					TypeBinding typeBinding2 = (TypeBinding) o2;
					return CharOperation.compareTo(typeBinding1.constantPoolName(), typeBinding2.constantPoolName());
				}
			});
		}
		int previousIndex = 0;
		next: for (int i = 0; i < initialSize; i++) {
			int missingTypeIndex = this.constantPool.literalIndexForType((TypeBinding) this.missingTypes.get(i));
			if (previousIndex == missingTypeIndex) {
				continue next;
			}
			previousIndex = missingTypeIndex;
			missingTypesIndexes[numberOfMissingTypes++] = missingTypeIndex;
		}
		// we don't need to resize as we interate from 0 to numberOfMissingTypes when recording the indexes in the .class file
		int attributeLength = numberOfMissingTypes * 2 + 2;
		if (this.contentsOffset + attributeLength + 6 >= this.contents.length) {
			resizeContents(attributeLength + 6);
		}
		int missingTypesNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.MissingTypesName);
		this.contents[this.contentsOffset++] = (byte) (missingTypesNameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) missingTypesNameIndex;

		// generate attribute length
		this.contents[this.contentsOffset++] = (byte) (attributeLength >> 24);
		this.contents[this.contentsOffset++] = (byte) (attributeLength >> 16);
		this.contents[this.contentsOffset++] = (byte) (attributeLength >> 8);
		this.contents[this.contentsOffset++] = (byte) attributeLength;

		// generate number of missing types
		this.contents[this.contentsOffset++] = (byte) (numberOfMissingTypes >> 8);
		this.contents[this.contentsOffset++] = (byte) numberOfMissingTypes;
		// generate entry for each missing type
		for (int i = 0; i < numberOfMissingTypes; i++) {
			int missingTypeIndex = missingTypesIndexes[i];
			this.contents[this.contentsOffset++] = (byte) (missingTypeIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) missingTypeIndex;
		}
	}

	/**
	 * @param annotations
	 * @return the number of attributes created while dumping the annotations in the .class file
	 */
	private int generateRuntimeAnnotations(final Annotation[] annotations) {
		int attributesNumber = 0;
		final int length = annotations.length;
		int visibleAnnotationsCounter = 0;
		int invisibleAnnotationsCounter = 0;

		for (int i = 0; i < length; i++) {
			Annotation annotation = annotations[i];
			if (annotation.isRuntimeInvisible()) {
				invisibleAnnotationsCounter++;
			} else if (annotation.isRuntimeVisible()) {
				visibleAnnotationsCounter++;
			}
		}

		int annotationAttributeOffset = this.contentsOffset;
		int constantPOffset = this.constantPool.currentOffset;
		int constantPoolIndex = this.constantPool.currentIndex;
		if (invisibleAnnotationsCounter != 0) {
			if (this.contentsOffset + 10 >= this.contents.length) {
				resizeContents(10);
			}
			int runtimeInvisibleAnnotationsAttributeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleAnnotationsName);
			this.contents[this.contentsOffset++] = (byte) (runtimeInvisibleAnnotationsAttributeNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) runtimeInvisibleAnnotationsAttributeNameIndex;
			int attributeLengthOffset = this.contentsOffset;
			this.contentsOffset += 4; // leave space for the attribute length

			int annotationsLengthOffset = this.contentsOffset;
			this.contentsOffset += 2; // leave space for the annotations length

			int counter = 0;
			loop: for (int i = 0; i < length; i++) {
				if (invisibleAnnotationsCounter == 0) break loop;
				Annotation annotation = annotations[i];
				if (annotation.isRuntimeInvisible()) {
					int currentAnnotationOffset = this.contentsOffset;
					generateAnnotation(annotation, currentAnnotationOffset);
					invisibleAnnotationsCounter--;
					if (this.contentsOffset != currentAnnotationOffset) {
						counter++;
					}
				}
			}
			if (counter != 0) {
				this.contents[annotationsLengthOffset++] = (byte) (counter >> 8);
				this.contents[annotationsLengthOffset++] = (byte) counter;

				int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 24);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 16);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 8);
				this.contents[attributeLengthOffset++] = (byte) attributeLength;
				attributesNumber++;
			} else {
				this.contentsOffset = annotationAttributeOffset;
				// reset the constant pool to its state before the clinit
				this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeInvisibleAnnotationsName, constantPoolIndex, constantPOffset);
			}
		}

		annotationAttributeOffset = this.contentsOffset;
		constantPOffset = this.constantPool.currentOffset;
		constantPoolIndex = this.constantPool.currentIndex;
		if (visibleAnnotationsCounter != 0) {
			if (this.contentsOffset + 10 >= this.contents.length) {
				resizeContents(10);
			}
			int runtimeVisibleAnnotationsAttributeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleAnnotationsName);
			this.contents[this.contentsOffset++] = (byte) (runtimeVisibleAnnotationsAttributeNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) runtimeVisibleAnnotationsAttributeNameIndex;
			int attributeLengthOffset = this.contentsOffset;
			this.contentsOffset += 4; // leave space for the attribute length

			int annotationsLengthOffset = this.contentsOffset;
			this.contentsOffset += 2; // leave space for the annotations length

			int counter = 0;
			loop: for (int i = 0; i < length; i++) {
				if (visibleAnnotationsCounter == 0) break loop;
				Annotation annotation = annotations[i];
				if (annotation.isRuntimeVisible()) {
					visibleAnnotationsCounter--;
					int currentAnnotationOffset = this.contentsOffset;
					generateAnnotation(annotation, currentAnnotationOffset);
					if (this.contentsOffset != currentAnnotationOffset) {
						counter++;
					}
				}
			}
			if (counter != 0) {
				this.contents[annotationsLengthOffset++] = (byte) (counter >> 8);
				this.contents[annotationsLengthOffset++] = (byte) counter;

				int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 24);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 16);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 8);
				this.contents[attributeLengthOffset++] = (byte) attributeLength;
				attributesNumber++;
			} else {
				this.contentsOffset = annotationAttributeOffset;
				this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeVisibleAnnotationsName, constantPoolIndex, constantPOffset);
			}
		}
		return attributesNumber;
	}
	private int generateRuntimeAnnotationsForParameters(Argument[] arguments) {
		final int argumentsLength = arguments.length;
		final int VISIBLE_INDEX = 0;
		final int INVISIBLE_INDEX = 1;
		int invisibleParametersAnnotationsCounter = 0;
		int visibleParametersAnnotationsCounter = 0;
		int[][] annotationsCounters = new int[argumentsLength][2];
		for (int i = 0; i < argumentsLength; i++) {
			Argument argument = arguments[i];
			Annotation[] annotations = argument.annotations;
			if (annotations != null) {
				for (int j = 0, max2 = annotations.length; j < max2; j++) {
					Annotation annotation = annotations[j];
					if (annotation.isRuntimeInvisible()) {
						annotationsCounters[i][INVISIBLE_INDEX]++;
						invisibleParametersAnnotationsCounter++;
					} else if (annotation.isRuntimeVisible()) {
						annotationsCounters[i][VISIBLE_INDEX]++;
						visibleParametersAnnotationsCounter++;
					}
				}
			}
		}
		int attributesNumber = 0;
		int annotationAttributeOffset = this.contentsOffset;
		if (invisibleParametersAnnotationsCounter != 0) {
			int globalCounter = 0;
			if (this.contentsOffset + 7 >= this.contents.length) {
				resizeContents(7);
			}
			int attributeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName);
			this.contents[this.contentsOffset++] = (byte) (attributeNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) attributeNameIndex;
			int attributeLengthOffset = this.contentsOffset;
			this.contentsOffset += 4; // leave space for the attribute length

			this.contents[this.contentsOffset++] = (byte) argumentsLength;
			for (int i = 0; i < argumentsLength; i++) {
				if (this.contentsOffset + 2 >= this.contents.length) {
					resizeContents(2);
				}
				if (invisibleParametersAnnotationsCounter == 0) {
					this.contents[this.contentsOffset++] = (byte) 0;
					this.contents[this.contentsOffset++] = (byte) 0;
				} else {
					final int numberOfInvisibleAnnotations = annotationsCounters[i][INVISIBLE_INDEX];
					int invisibleAnnotationsOffset = this.contentsOffset;
					// leave space for number of annotations
					this.contentsOffset += 2;
					int counter = 0;
					if (numberOfInvisibleAnnotations != 0) {
						Argument argument = arguments[i];
						Annotation[] annotations = argument.annotations;
						for (int j = 0, max = annotations.length; j < max; j++) {
							Annotation annotation = annotations[j];
							if (annotation.isRuntimeInvisible()) {
								int currentAnnotationOffset = this.contentsOffset;
								generateAnnotation(annotation, currentAnnotationOffset);
								if (this.contentsOffset != currentAnnotationOffset) {
									counter++;
									globalCounter++;
								}
								invisibleParametersAnnotationsCounter--;
							}
						}
					}
					this.contents[invisibleAnnotationsOffset++] = (byte) (counter >> 8);
					this.contents[invisibleAnnotationsOffset] = (byte) counter;
				}
			}
			if (globalCounter != 0) {
				int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 24);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 16);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 8);
				this.contents[attributeLengthOffset++] = (byte) attributeLength;
				attributesNumber++;
			} else {
				// if globalCounter is 0, this means that the code generation for all visible annotations failed
				this.contentsOffset = annotationAttributeOffset;
			}
		}
		if (visibleParametersAnnotationsCounter != 0) {
			int globalCounter = 0;
			if (this.contentsOffset + 7 >= this.contents.length) {
				resizeContents(7);
			}
			int attributeNameIndex =
				this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName);
			this.contents[this.contentsOffset++] = (byte) (attributeNameIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) attributeNameIndex;
			int attributeLengthOffset = this.contentsOffset;
			this.contentsOffset += 4; // leave space for the attribute length

			this.contents[this.contentsOffset++] = (byte) argumentsLength;
			for (int i = 0; i < argumentsLength; i++) {
				if (this.contentsOffset + 2 >= this.contents.length) {
					resizeContents(2);
				}
				if (visibleParametersAnnotationsCounter == 0) {
					this.contents[this.contentsOffset++] = (byte) 0;
					this.contents[this.contentsOffset++] = (byte) 0;
				} else {
					final int numberOfVisibleAnnotations = annotationsCounters[i][VISIBLE_INDEX];
					int visibleAnnotationsOffset = this.contentsOffset;
					// leave space for number of annotations
					this.contentsOffset += 2;
					int counter = 0;
					if (numberOfVisibleAnnotations != 0) {
						Argument argument = arguments[i];
						Annotation[] annotations = argument.annotations;
						for (int j = 0, max = annotations.length; j < max; j++) {
							Annotation annotation = annotations[j];
							if (annotation.isRuntimeVisible()) {
								int currentAnnotationOffset = this.contentsOffset;
								generateAnnotation(annotation, currentAnnotationOffset);
								if (this.contentsOffset != currentAnnotationOffset) {
									counter++;
									globalCounter++;
								}
								visibleParametersAnnotationsCounter--;
							}
						}
					}
					this.contents[visibleAnnotationsOffset++] = (byte) (counter >> 8);
					this.contents[visibleAnnotationsOffset] = (byte) counter;
				}
			}
			if (globalCounter != 0) {
				int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 24);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 16);
				this.contents[attributeLengthOffset++] = (byte) (attributeLength >> 8);
				this.contents[attributeLengthOffset++] = (byte) attributeLength;
				attributesNumber++;
			} else {
				// if globalCounter is 0, this means that the code generation for all visible annotations failed
				this.contentsOffset = annotationAttributeOffset;
			}
		}
		return attributesNumber;
	}

	private int generateSignatureAttribute(char[] genericSignature) {
		int localContentsOffset = this.contentsOffset;
		if (localContentsOffset + 8 >= this.contents.length) {
			resizeContents(8);
		}
		int signatureAttributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
		this.contents[localContentsOffset++] = (byte) (signatureAttributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) signatureAttributeNameIndex;
		// the length of a signature attribute is equals to 2
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 2;
		int signatureIndex =
			this.constantPool.literalIndex(genericSignature);
		this.contents[localContentsOffset++] = (byte) (signatureIndex >> 8);
		this.contents[localContentsOffset++] = (byte) signatureIndex;
		this.contentsOffset = localContentsOffset;
		return 1;
	}

	private int generateSourceAttribute(String fullFileName) {
		int localContentsOffset = this.contentsOffset;
		// check that there is enough space to write all the bytes for the field info corresponding
		// to the @fieldBinding
		if (localContentsOffset + 8 >= this.contents.length) {
			resizeContents(8);
		}
		int sourceAttributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.SourceName);
		this.contents[localContentsOffset++] = (byte) (sourceAttributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) sourceAttributeNameIndex;
		// The length of a source file attribute is 2. This is a fixed-length
		// attribute
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 2;
		// write the source file name
		int fileNameIndex = this.constantPool.literalIndex(fullFileName.toCharArray());
		this.contents[localContentsOffset++] = (byte) (fileNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) fileNameIndex;
		this.contentsOffset = localContentsOffset;
		return 1;
	}
	private int generateStackMapAttribute(
			MethodBinding methodBinding,
			int code_length,
			int codeAttributeOffset,
			int max_locals,
			boolean isClinit) {
		int attributesNumber = 0;
		int localContentsOffset = this.contentsOffset;
		StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
		stackMapFrameCodeStream.removeFramePosition(code_length);
		if (stackMapFrameCodeStream.hasFramePositions()) {
			ArrayList frames = new ArrayList();
			traverse(isClinit ? null : methodBinding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, isClinit);
			int numberOfFrames = frames.size();
			if (numberOfFrames > 1) {
				int stackMapTableAttributeOffset = localContentsOffset;
				// add the stack map table attribute
				if (localContentsOffset + 8 >= this.contents.length) {
					resizeContents(8);
				}
				int stackMapAttributeNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
				this.contents[localContentsOffset++] = (byte) (stackMapAttributeNameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) stackMapAttributeNameIndex;

				int stackMapAttributeLengthOffset = localContentsOffset;
				// generate the attribute
				localContentsOffset += 4;
				if (localContentsOffset + 4 >= this.contents.length) {
					resizeContents(4);
				}
				int numberOfFramesOffset = localContentsOffset;
				localContentsOffset += 2;
				if (localContentsOffset + 2 >= this.contents.length) {
					resizeContents(2);
				}
				StackMapFrame currentFrame = (StackMapFrame) frames.get(0);
				for (int j = 1; j < numberOfFrames; j++) {
					// select next frame
					currentFrame = (StackMapFrame) frames.get(j);
					// generate current frame
					// need to find differences between the current frame and the previous frame
					int frameOffset = currentFrame.pc;
					// FULL_FRAME
					if (localContentsOffset + 5 >= this.contents.length) {
						resizeContents(5);
					}
					this.contents[localContentsOffset++] = (byte) (frameOffset >> 8);
					this.contents[localContentsOffset++] = (byte) frameOffset;
					int numberOfLocalOffset = localContentsOffset;
					localContentsOffset += 2; // leave two spots for number of locals
					int numberOfLocalEntries = 0;
					int numberOfLocals = currentFrame.getNumberOfLocals();
					int numberOfEntries = 0;
					int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
					for (int i = 0; i < localsLength && numberOfLocalEntries < numberOfLocals; i++) {
						if (localContentsOffset + 3 >= this.contents.length) {
							resizeContents(3);
						}
						VerificationTypeInfo info = currentFrame.locals[i];
						if (info == null) {
							this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
						} else {
							switch(info.id()) {
								case T_boolean :
								case T_byte :
								case T_char :
								case T_int :
								case T_short :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
									break;
								case T_float :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
									break;
								case T_long :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
									i++;
									break;
								case T_double :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
									i++;
									break;
								case T_null :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
									break;
								default:
									this.contents[localContentsOffset++] = (byte) info.tag;
								switch (info.tag) {
									case VerificationTypeInfo.ITEM_UNINITIALIZED :
										int offset = info.offset;
										this.contents[localContentsOffset++] = (byte) (offset >> 8);
										this.contents[localContentsOffset++] = (byte) offset;
										break;
									case VerificationTypeInfo.ITEM_OBJECT :
										int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
										this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
										this.contents[localContentsOffset++] = (byte) indexForType;
								}
							}
							numberOfLocalEntries++;
						}
						numberOfEntries++;
					}
					if (localContentsOffset + 4 >= this.contents.length) {
						resizeContents(4);
					}
					this.contents[numberOfLocalOffset++] = (byte) (numberOfEntries >> 8);
					this.contents[numberOfLocalOffset] = (byte) numberOfEntries;
					int numberOfStackItems = currentFrame.numberOfStackItems;
					this.contents[localContentsOffset++] = (byte) (numberOfStackItems >> 8);
					this.contents[localContentsOffset++] = (byte) numberOfStackItems;
					for (int i = 0; i < numberOfStackItems; i++) {
						if (localContentsOffset + 3 >= this.contents.length) {
							resizeContents(3);
						}
						VerificationTypeInfo info = currentFrame.stackItems[i];
						if (info == null) {
							this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
						} else {
							switch(info.id()) {
								case T_boolean :
								case T_byte :
								case T_char :
								case T_int :
								case T_short :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
									break;
								case T_float :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
									break;
								case T_long :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
									break;
								case T_double :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
									break;
								case T_null :
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
									break;
								default:
									this.contents[localContentsOffset++] = (byte) info.tag;
								switch (info.tag) {
									case VerificationTypeInfo.ITEM_UNINITIALIZED :
										int offset = info.offset;
										this.contents[localContentsOffset++] = (byte) (offset >> 8);
										this.contents[localContentsOffset++] = (byte) offset;
										break;
									case VerificationTypeInfo.ITEM_OBJECT :
										int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
										this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
										this.contents[localContentsOffset++] = (byte) indexForType;
								}
							}
						}
					}
				}

				numberOfFrames--;
				if (numberOfFrames != 0) {
					this.contents[numberOfFramesOffset++] = (byte) (numberOfFrames >> 8);
					this.contents[numberOfFramesOffset] = (byte) numberOfFrames;

					int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
					this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 24);
					this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 16);
					this.contents[stackMapAttributeLengthOffset++] = (byte) (attributeLength >> 8);
					this.contents[stackMapAttributeLengthOffset] = (byte) attributeLength;
					attributesNumber++;
				} else {
					localContentsOffset = stackMapTableAttributeOffset;
				}
			}
		}
		this.contentsOffset = localContentsOffset;
		return attributesNumber;
	}

	private int generateStackMapTableAttribute(
			MethodBinding methodBinding,
			int code_length,
			int codeAttributeOffset,
			int max_locals,
			boolean isClinit) {
		int attributesNumber = 0;
		int localContentsOffset = this.contentsOffset;
		StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
		stackMapFrameCodeStream.removeFramePosition(code_length);
		if (stackMapFrameCodeStream.hasFramePositions()) {
			ArrayList frames = new ArrayList();
			traverse(isClinit ? null: methodBinding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, isClinit);
			int numberOfFrames = frames.size();
			if (numberOfFrames > 1) {
				int stackMapTableAttributeOffset = localContentsOffset;
				// add the stack map table attribute
				if (localContentsOffset + 8 >= this.contents.length) {
					resizeContents(8);
				}
				int stackMapTableAttributeNameIndex =
					this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
				this.contents[localContentsOffset++] = (byte) (stackMapTableAttributeNameIndex >> 8);
				this.contents[localContentsOffset++] = (byte) stackMapTableAttributeNameIndex;

				int stackMapTableAttributeLengthOffset = localContentsOffset;
				// generate the attribute
				localContentsOffset += 4;
				if (localContentsOffset + 4 >= this.contents.length) {
					resizeContents(4);
				}
				int numberOfFramesOffset = localContentsOffset;
				localContentsOffset += 2;
				if (localContentsOffset + 2 >= this.contents.length) {
					resizeContents(2);
				}
				StackMapFrame currentFrame = (StackMapFrame) frames.get(0);
				StackMapFrame prevFrame = null;
				for (int j = 1; j < numberOfFrames; j++) {
					// select next frame
					prevFrame = currentFrame;
					currentFrame = (StackMapFrame) frames.get(j);
					// generate current frame
					// need to find differences between the current frame and the previous frame
					int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
					switch (currentFrame.getFrameType(prevFrame)) {
						case StackMapFrame.APPEND_FRAME :
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							int numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
							this.contents[localContentsOffset++] = (byte) (251 + numberOfDifferentLocals);
							this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
							this.contents[localContentsOffset++] = (byte) offsetDelta;
							int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
							int numberOfLocals = currentFrame.getNumberOfLocals();
							for (int i = index; i < currentFrame.locals.length && numberOfDifferentLocals > 0; i++) {
								if (localContentsOffset + 6 >= this.contents.length) {
									resizeContents(6);
								}
								VerificationTypeInfo info = currentFrame.locals[i];
								if (info == null) {
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
								} else {
									switch(info.id()) {
										case T_boolean :
										case T_byte :
										case T_char :
										case T_int :
										case T_short :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
											break;
										case T_float :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
											break;
										case T_long :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
											i++;
											break;
										case T_double :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
											i++;
											break;
										case T_null :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
											break;
										default:
											this.contents[localContentsOffset++] = (byte) info.tag;
											switch (info.tag) {
												case VerificationTypeInfo.ITEM_UNINITIALIZED :
													int offset = info.offset;
													this.contents[localContentsOffset++] = (byte) (offset >> 8);
													this.contents[localContentsOffset++] = (byte) offset;
													break;
												case VerificationTypeInfo.ITEM_OBJECT :
													int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
													this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
													this.contents[localContentsOffset++] = (byte) indexForType;
											}
									}
									numberOfDifferentLocals--;
								}
							}
							break;
						case StackMapFrame.SAME_FRAME :
							if (localContentsOffset + 1 >= this.contents.length) {
								resizeContents(1);
							}
							this.contents[localContentsOffset++] = (byte) offsetDelta;
							break;
						case StackMapFrame.SAME_FRAME_EXTENDED :
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							this.contents[localContentsOffset++] = (byte) 251;
							this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
							this.contents[localContentsOffset++] = (byte) offsetDelta;
							break;
						case StackMapFrame.CHOP_FRAME :
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
							this.contents[localContentsOffset++] = (byte) (251 - numberOfDifferentLocals);
							this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
							this.contents[localContentsOffset++] = (byte) offsetDelta;
							break;
						case StackMapFrame.SAME_LOCALS_1_STACK_ITEMS :
							if (localContentsOffset + 4 >= this.contents.length) {
								resizeContents(4);
							}
							this.contents[localContentsOffset++] = (byte) (offsetDelta + 64);
							if (currentFrame.stackItems[0] == null) {
								this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
							} else {
								switch(currentFrame.stackItems[0].id()) {
									case T_boolean :
									case T_byte :
									case T_char :
									case T_int :
									case T_short :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
										break;
									case T_float :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
										break;
									case T_long :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
										break;
									case T_double :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
										break;
									case T_null :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
										break;
									default:
										VerificationTypeInfo info = currentFrame.stackItems[0];
										byte tag = (byte) info.tag;
										this.contents[localContentsOffset++] = tag;
										switch (tag) {
											case VerificationTypeInfo.ITEM_UNINITIALIZED :
												int offset = info.offset;
												this.contents[localContentsOffset++] = (byte) (offset >> 8);
												this.contents[localContentsOffset++] = (byte) offset;
												break;
											case VerificationTypeInfo.ITEM_OBJECT :
												int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
												this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
												this.contents[localContentsOffset++] = (byte) indexForType;
										}
								}
							}
							break;
						case StackMapFrame.SAME_LOCALS_1_STACK_ITEMS_EXTENDED :
							if (localContentsOffset + 6 >= this.contents.length) {
								resizeContents(6);
							}
							this.contents[localContentsOffset++] = (byte) 247;
							this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
							this.contents[localContentsOffset++] = (byte) offsetDelta;
							if (currentFrame.stackItems[0] == null) {
								this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
							} else {
								switch(currentFrame.stackItems[0].id()) {
									case T_boolean :
									case T_byte :
									case T_char :
									case T_int :
									case T_short :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
										break;
									case T_float :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
										break;
									case T_long :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
										break;
									case T_double :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
										break;
									case T_null :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
										break;
									default:
										VerificationTypeInfo info = currentFrame.stackItems[0];
										byte tag = (byte) info.tag;
										this.contents[localContentsOffset++] = tag;
										switch (tag) {
											case VerificationTypeInfo.ITEM_UNINITIALIZED :
												int offset = info.offset;
												this.contents[localContentsOffset++] = (byte) (offset >> 8);
												this.contents[localContentsOffset++] = (byte) offset;
												break;
											case VerificationTypeInfo.ITEM_OBJECT :
												int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
												this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
												this.contents[localContentsOffset++] = (byte) indexForType;
										}
								}
							}
							break;
						default :
							// FULL_FRAME
							if (localContentsOffset + 5 >= this.contents.length) {
								resizeContents(5);
							}
							this.contents[localContentsOffset++] = (byte) 255;
							this.contents[localContentsOffset++] = (byte) (offsetDelta >> 8);
							this.contents[localContentsOffset++] = (byte) offsetDelta;
							int numberOfLocalOffset = localContentsOffset;
							localContentsOffset += 2; // leave two spots for number of locals
							int numberOfLocalEntries = 0;
							numberOfLocals = currentFrame.getNumberOfLocals();
							int numberOfEntries = 0;
							int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
							for (int i = 0; i < localsLength && numberOfLocalEntries < numberOfLocals; i++) {
								if (localContentsOffset + 3 >= this.contents.length) {
									resizeContents(3);
								}
								VerificationTypeInfo info = currentFrame.locals[i];
								if (info == null) {
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
								} else {
									switch(info.id()) {
										case T_boolean :
										case T_byte :
										case T_char :
										case T_int :
										case T_short :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
											break;
										case T_float :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
											break;
										case T_long :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
											i++;
											break;
										case T_double :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
											i++;
											break;
										case T_null :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
											break;
										default:
											this.contents[localContentsOffset++] = (byte) info.tag;
											switch (info.tag) {
												case VerificationTypeInfo.ITEM_UNINITIALIZED :
													int offset = info.offset;
													this.contents[localContentsOffset++] = (byte) (offset >> 8);
													this.contents[localContentsOffset++] = (byte) offset;
													break;
												case VerificationTypeInfo.ITEM_OBJECT :
													int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
													this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
													this.contents[localContentsOffset++] = (byte) indexForType;
											}
									}
									numberOfLocalEntries++;
								}
								numberOfEntries++;
							}
							if (localContentsOffset + 4 >= this.contents.length) {
								resizeContents(4);
							}
							this.contents[numberOfLocalOffset++] = (byte) (numberOfEntries >> 8);
							this.contents[numberOfLocalOffset] = (byte) numberOfEntries;
							int numberOfStackItems = currentFrame.numberOfStackItems;
							this.contents[localContentsOffset++] = (byte) (numberOfStackItems >> 8);
							this.contents[localContentsOffset++] = (byte) numberOfStackItems;
							for (int i = 0; i < numberOfStackItems; i++) {
								if (localContentsOffset + 3 >= this.contents.length) {
									resizeContents(3);
								}
								VerificationTypeInfo info = currentFrame.stackItems[i];
								if (info == null) {
									this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
								} else {
									switch(info.id()) {
										case T_boolean :
										case T_byte :
										case T_char :
										case T_int :
										case T_short :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
											break;
										case T_float :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
											break;
										case T_long :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
											break;
										case T_double :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
											break;
										case T_null :
											this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
											break;
										default:
											this.contents[localContentsOffset++] = (byte) info.tag;
											switch (info.tag) {
												case VerificationTypeInfo.ITEM_UNINITIALIZED :
													int offset = info.offset;
													this.contents[localContentsOffset++] = (byte) (offset >> 8);
													this.contents[localContentsOffset++] = (byte) offset;
													break;
												case VerificationTypeInfo.ITEM_OBJECT :
													int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
													this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
													this.contents[localContentsOffset++] = (byte) indexForType;
											}
									}
								}
							}
					}
				}

				numberOfFrames--;
				if (numberOfFrames != 0) {
					this.contents[numberOfFramesOffset++] = (byte) (numberOfFrames >> 8);
					this.contents[numberOfFramesOffset] = (byte) numberOfFrames;

					int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
					this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 24);
					this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 16);
					this.contents[stackMapTableAttributeLengthOffset++] = (byte) (attributeLength >> 8);
					this.contents[stackMapTableAttributeLengthOffset] = (byte) attributeLength;
					attributesNumber++;
				} else {
					localContentsOffset = stackMapTableAttributeOffset;
				}
			}
		}
		this.contentsOffset = localContentsOffset;
		return attributesNumber;
	}

	private int generateSyntheticAttribute() {
		int localContentsOffset = this.contentsOffset;
		if (localContentsOffset + 6 >= this.contents.length) {
			resizeContents(6);
		}
		int syntheticAttributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
		this.contents[localContentsOffset++] = (byte) (syntheticAttributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) syntheticAttributeNameIndex;
		// the length of a synthetic attribute is equals to 0
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contentsOffset = localContentsOffset;
		return 1;
	}

	private int generateVarargsAttribute() {
		int localContentsOffset = this.contentsOffset;
		/*
		 * handle of the target jsr14 for varargs in the source
		 * Varargs attribute
		 * Check that there is enough space to write the attribute
		 */
		if (localContentsOffset + 6 >= this.contents.length) {
			resizeContents(6);
		}
		int varargsAttributeNameIndex =
			this.constantPool.literalIndex(AttributeNamesConstants.VarargsName);
		this.contents[localContentsOffset++] = (byte) (varargsAttributeNameIndex >> 8);
		this.contents[localContentsOffset++] = (byte) varargsAttributeNameIndex;
		// the length of a varargs attribute is equals to 0
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;
		this.contents[localContentsOffset++] = 0;

		this.contentsOffset = localContentsOffset;
		return 1;
	}

	/**
	 * EXTERNAL API
	 * Answer the actual bytes of the class file
	 *
	 * This method encodes the receiver structure into a byte array which is the content of the classfile.
	 * Returns the byte array that represents the encoded structure of the receiver.
	 *
	 * @return byte[]
	 */
	public byte[] getBytes() {
		if (this.bytes == null) {
			this.bytes = new byte[this.headerOffset + this.contentsOffset];
			System.arraycopy(this.header, 0, this.bytes, 0, this.headerOffset);
			System.arraycopy(this.contents, 0, this.bytes, this.headerOffset, this.contentsOffset);
		}
		return this.bytes;
	}
	/**
	 * EXTERNAL API
	 * Answer the compound name of the class file.
	 * @return char[][]
	 * e.g. {{java}, {util}, {Hashtable}}.
	 */
	public char[][] getCompoundName() {
		return CharOperation.splitOn('/', fileName());
	}

	private int getParametersCount(char[] methodSignature) {
		int i = CharOperation.indexOf('(', methodSignature);
		i++;
		char currentCharacter = methodSignature[i];
		if (currentCharacter == ')') {
			return 0;
		}
		int result = 0;
		while (true) {
			currentCharacter = methodSignature[i];
			if (currentCharacter == ')') {
				return result;
			}
			switch (currentCharacter) {
				case '[':
					// array type
					int scanType = scanType(methodSignature, i + 1);
					result++;
					i = scanType + 1;
					break;
				case 'L':
					scanType = CharOperation.indexOf(';', methodSignature,
							i + 1);
					result++;
					i = scanType + 1;
					break;
				case 'Z':
				case 'B':
				case 'C':
				case 'D':
				case 'F':
				case 'I':
				case 'J':
				case 'S':
					result++;
					i++;
					break;
				default:
					throw new IllegalArgumentException("Invalid starting type character : " + currentCharacter); //$NON-NLS-1$
			}
		}
	}

	private char[] getReturnType(char[] methodSignature) {
		// skip type parameters
		int paren = CharOperation.lastIndexOf(')', methodSignature);
		// there could be thrown exceptions behind, thus scan one type exactly
		return CharOperation.subarray(methodSignature, paren + 1,
				methodSignature.length);
	}


	private final int i4At(byte[] reference, int relativeOffset,
			int structOffset) {
		int position = relativeOffset + structOffset;
		return ((reference[position++] & 0xFF) << 24)
				+ ((reference[position++] & 0xFF) << 16)
				+ ((reference[position++] & 0xFF) << 8)
				+ (reference[position] & 0xFF);
	}

	protected void initByteArrays() {
		int members = this.referenceBinding.methods().length + this.referenceBinding.fields().length;
		this.header = new byte[INITIAL_HEADER_SIZE];
		this.contents = new byte[members < 15 ? INITIAL_CONTENTS_SIZE : INITIAL_HEADER_SIZE];
	}

	public void initialize(SourceTypeBinding aType, ClassFile parentClassFile, boolean createProblemType) {
		// generate the magic numbers inside the header
		this.header[this.headerOffset++] = (byte) (0xCAFEBABEL >> 24);
		this.header[this.headerOffset++] = (byte) (0xCAFEBABEL >> 16);
		this.header[this.headerOffset++] = (byte) (0xCAFEBABEL >> 8);
		this.header[this.headerOffset++] = (byte) (0xCAFEBABEL >> 0);

		long targetVersion = this.targetJDK;
		this.header[this.headerOffset++] = (byte) (targetVersion >> 8); // minor high
		this.header[this.headerOffset++] = (byte) (targetVersion>> 0); // minor low
		this.header[this.headerOffset++] = (byte) (targetVersion >> 24); // major high
		this.header[this.headerOffset++] = (byte) (targetVersion >> 16); // major low

		this.constantPoolOffset = this.headerOffset;
		this.headerOffset += 2;
		this.constantPool.initialize(this);

		// Modifier manipulations for classfile
		int accessFlags = aType.getAccessFlags();
		if (aType.isPrivate()) { // rewrite private to non-public
			accessFlags &= ~ClassFileConstants.AccPublic;
		}
		if (aType.isProtected()) { // rewrite protected into public
			accessFlags |= ClassFileConstants.AccPublic;
		}
		// clear all bits that are illegal for a class or an interface
		accessFlags
			&= ~(
				ClassFileConstants.AccStrictfp
					| ClassFileConstants.AccProtected
					| ClassFileConstants.AccPrivate
					| ClassFileConstants.AccStatic
					| ClassFileConstants.AccSynchronized
					| ClassFileConstants.AccNative);

		// set the AccSuper flag (has to be done after clearing AccSynchronized - since same value)
		if (!aType.isInterface()) { // class or enum
			accessFlags |= ClassFileConstants.AccSuper;
		}
		if (aType.isAnonymousType()) {
			accessFlags &= ~ClassFileConstants.AccFinal;
		}
		int finalAbstract = ClassFileConstants.AccFinal | ClassFileConstants.AccAbstract;
		if ((accessFlags & finalAbstract) == finalAbstract) {
			accessFlags &= ~finalAbstract;
		}
		this.enclosingClassFile = parentClassFile;
		// innerclasses get their names computed at code gen time

		// now we continue to generate the bytes inside the contents array
		this.contents[this.contentsOffset++] = (byte) (accessFlags >> 8);
		this.contents[this.contentsOffset++] = (byte) accessFlags;
		int classNameIndex = this.constantPool.literalIndexForType(aType);
		this.contents[this.contentsOffset++] = (byte) (classNameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) classNameIndex;
		int superclassNameIndex;
		if (aType.isInterface()) {
			superclassNameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName);
		} else {
			if (aType.superclass != null) {
				 if ((aType.superclass.tagBits & TagBits.HasMissingType) != 0) {
						superclassNameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName);
				 } else {
						superclassNameIndex = this.constantPool.literalIndexForType(aType.superclass);
				 }
			} else {
				superclassNameIndex = 0;
			}
		}
		this.contents[this.contentsOffset++] = (byte) (superclassNameIndex >> 8);
		this.contents[this.contentsOffset++] = (byte) superclassNameIndex;
		ReferenceBinding[] superInterfacesBinding = aType.superInterfaces();
		int interfacesCount = superInterfacesBinding.length;
		int interfacesCountPosition = this.contentsOffset;
		this.contentsOffset += 2;
		int interfaceCounter = 0;
		for (int i = 0; i < interfacesCount; i++) {
			ReferenceBinding binding = superInterfacesBinding[i];
			if ((binding.tagBits & TagBits.HasMissingType) != 0) {
				continue;
			}
			interfaceCounter++;
			int interfaceIndex = this.constantPool.literalIndexForType(binding);
			this.contents[this.contentsOffset++] = (byte) (interfaceIndex >> 8);
			this.contents[this.contentsOffset++] = (byte) interfaceIndex;
		}
		this.contents[interfacesCountPosition++] = (byte) (interfaceCounter >> 8);
		this.contents[interfacesCountPosition] = (byte) interfaceCounter;
		this.creatingProblemType = createProblemType;

		// retrieve the enclosing one guaranteed to be the one matching the propagated flow info
		// 1FF9ZBU: LFCOM:ALL - Local variable attributes busted (Sanity check)
		this.codeStream.maxFieldCount = aType.scope.outerMostClassScope().referenceType().maxFieldCount;
	}

	private void initializeDefaultLocals(StackMapFrame frame,
			MethodBinding methodBinding,
			int maxLocals,
			int codeLength) {
		if (maxLocals != 0) {
			int resolvedPosition = 0;
			// take into account enum constructor synthetic name+ordinal
			final boolean isConstructor = methodBinding.isConstructor();
			if (isConstructor || !methodBinding.isStatic()) {
				LocalVariableBinding localVariableBinding = new LocalVariableBinding(ConstantPool.This, methodBinding.declaringClass, 0, false);
				localVariableBinding.resolvedPosition = 0;
				this.codeStream.record(localVariableBinding);
				localVariableBinding.recordInitializationStartPC(0);
				localVariableBinding.recordInitializationEndPC(codeLength);
				frame.putLocal(resolvedPosition, new VerificationTypeInfo(
						isConstructor ? VerificationTypeInfo.ITEM_UNINITIALIZED_THIS : VerificationTypeInfo.ITEM_OBJECT,
						methodBinding.declaringClass));
				resolvedPosition++;
			}

			if (isConstructor) {
				if (methodBinding.declaringClass.isEnum()) {
					LocalVariableBinding localVariableBinding = new LocalVariableBinding(" name".toCharArray(), this.referenceBinding.scope.getJavaLangString(), 0, false); //$NON-NLS-1$
					localVariableBinding.resolvedPosition = resolvedPosition;
					this.codeStream.record(localVariableBinding);
					localVariableBinding.recordInitializationStartPC(0);
					localVariableBinding.recordInitializationEndPC(codeLength);

					frame.putLocal(resolvedPosition, new VerificationTypeInfo(
							TypeIds.T_JavaLangString,
							ConstantPool.JavaLangStringConstantPoolName));
					resolvedPosition++;

					localVariableBinding = new LocalVariableBinding(" ordinal".toCharArray(), TypeBinding.INT, 0, false); //$NON-NLS-1$
					localVariableBinding.resolvedPosition = resolvedPosition;
					this.codeStream.record(localVariableBinding);
					localVariableBinding.recordInitializationStartPC(0);
					localVariableBinding.recordInitializationEndPC(codeLength);
					frame.putLocal(resolvedPosition, new VerificationTypeInfo(
							TypeBinding.INT));
					resolvedPosition++;
				}

				// take into account the synthetic parameters
				if (methodBinding.declaringClass.isNestedType()) {
					ReferenceBinding enclosingInstanceTypes[];
					if ((enclosingInstanceTypes = methodBinding.declaringClass.syntheticEnclosingInstanceTypes()) != null) {
						for (int i = 0, max = enclosingInstanceTypes.length; i < max; i++) {
							// an enclosingInstanceType can only be a reference
							// binding. It cannot be
							// LongBinding or DoubleBinding
							LocalVariableBinding localVariableBinding = new LocalVariableBinding((" enclosingType" + i).toCharArray(), enclosingInstanceTypes[i], 0, false); //$NON-NLS-1$
							localVariableBinding.resolvedPosition = resolvedPosition;
							this.codeStream.record(localVariableBinding);
							localVariableBinding.recordInitializationStartPC(0);
							localVariableBinding.recordInitializationEndPC(codeLength);

							frame.putLocal(resolvedPosition,
									new VerificationTypeInfo(enclosingInstanceTypes[i]));
							resolvedPosition++;
						}
					}

					TypeBinding[] arguments;
					if ((arguments = methodBinding.parameters) != null) {
						for (int i = 0, max = arguments.length; i < max; i++) {
							final TypeBinding typeBinding = arguments[i];
							frame.putLocal(resolvedPosition,
									new VerificationTypeInfo(typeBinding));
							switch (typeBinding.id) {
								case TypeIds.T_double:
								case TypeIds.T_long:
									resolvedPosition += 2;
									break;
								default:
									resolvedPosition++;
							}
						}
					}

					SyntheticArgumentBinding syntheticArguments[];
					if ((syntheticArguments = methodBinding.declaringClass.syntheticOuterLocalVariables()) != null) {
						for (int i = 0, max = syntheticArguments.length; i < max; i++) {
							final TypeBinding typeBinding = syntheticArguments[i].type;
							LocalVariableBinding localVariableBinding = new LocalVariableBinding((" synthetic" + i).toCharArray(), typeBinding, 0, false); //$NON-NLS-1$
							localVariableBinding.resolvedPosition = resolvedPosition;
							this.codeStream.record(localVariableBinding);
							localVariableBinding.recordInitializationStartPC(0);
							localVariableBinding.recordInitializationEndPC(codeLength);

							frame.putLocal(resolvedPosition,
									new VerificationTypeInfo(typeBinding));
							switch (typeBinding.id) {
								case TypeIds.T_double:
								case TypeIds.T_long:
									resolvedPosition += 2;
									break;
								default:
									resolvedPosition++;
							}
						}
					}
				} else {
					TypeBinding[] arguments;
					if ((arguments = methodBinding.parameters) != null) {
						for (int i = 0, max = arguments.length; i < max; i++) {
							final TypeBinding typeBinding = arguments[i];
							frame.putLocal(resolvedPosition,
									new VerificationTypeInfo(typeBinding));
							switch (typeBinding.id) {
								case TypeIds.T_double:
								case TypeIds.T_long:
									resolvedPosition += 2;
									break;
								default:
									resolvedPosition++;
							}
						}
					}
				}
			} else {
				TypeBinding[] arguments;
				if ((arguments = methodBinding.parameters) != null) {
					for (int i = 0, max = arguments.length; i < max; i++) {
						final TypeBinding typeBinding = arguments[i];
						frame.putLocal(resolvedPosition,
								new VerificationTypeInfo(typeBinding));
						switch (typeBinding.id) {
							case TypeIds.T_double:
							case TypeIds.T_long:
								resolvedPosition += 2;
								break;
							default:
								resolvedPosition++;
						}
					}
				}
			}
		}
	}

	private void initializeLocals(boolean isStatic, int currentPC, StackMapFrame currentFrame) {
		VerificationTypeInfo[] locals = currentFrame.locals;
		int localsLength = locals.length;
		int i = 0;
		if (!isStatic) {
			// we don't want to reset the first local if the method is not static
			i = 1;
		}
		for (; i < localsLength; i++) {
			locals[i] = null;
		}
		i = 0;
		locals: for (int max = this.codeStream.allLocalsCounter; i < max; i++) {
			LocalVariableBinding localVariable = this.codeStream.locals[i];
			if (localVariable == null) continue;
			int resolvedPosition = localVariable.resolvedPosition;
			final TypeBinding localVariableTypeBinding = localVariable.type;
			inits: for (int j = 0; j < localVariable.initializationCount; j++) {
				int startPC = localVariable.initializationPCs[j << 1];
				int endPC = localVariable.initializationPCs[(j << 1) + 1];
				if (currentPC < startPC) {
					continue inits;
				} else if (currentPC < endPC) {
					// the current local is an active local
					if (currentFrame.locals[resolvedPosition] == null) {
						currentFrame.locals[resolvedPosition] =
								new VerificationTypeInfo(
										localVariableTypeBinding);
					}
					continue locals;
				}
			}
		}
	}
	/**
	 * INTERNAL USE-ONLY
	 * Returns the most enclosing classfile of the receiver. This is used know to store the constant pool name
	 * for all inner types of the receiver.
	 * @return org.eclipse.jdt.internal.compiler.codegen.ClassFile
	 */
	public ClassFile outerMostEnclosingClassFile() {
		ClassFile current = this;
		while (current.enclosingClassFile != null)
			current = current.enclosingClassFile;
		return current;
	}

	public void recordInnerClasses(TypeBinding binding) {
		if (this.innerClassesBindings == null) {
			this.innerClassesBindings = new HashSet(INNER_CLASSES_SIZE);
		}
		ReferenceBinding innerClass = (ReferenceBinding) binding;
		this.innerClassesBindings.add(innerClass.erasure());
		ReferenceBinding enclosingType = innerClass.enclosingType();
		while (enclosingType != null
				&& enclosingType.isNestedType()) {
			this.innerClassesBindings.add(enclosingType.erasure());
			enclosingType = enclosingType.enclosingType();
		}
	}

	public void reset(SourceTypeBinding typeBinding) {
		// the code stream is reinitialized for each method
		final CompilerOptions options = typeBinding.scope.compilerOptions();
		this.referenceBinding = typeBinding;
		this.isNestedType = typeBinding.isNestedType();
		this.targetJDK = options.targetJDK;
		this.produceAttributes = options.produceDebugAttributes;
		if (this.targetJDK >= ClassFileConstants.JDK1_6) {
			this.produceAttributes |= ClassFileConstants.ATTR_STACK_MAP_TABLE;
		} else if (this.targetJDK == ClassFileConstants.CLDC_1_1) {
			this.targetJDK = ClassFileConstants.JDK1_1; // put back 45.3
			this.produceAttributes |= ClassFileConstants.ATTR_STACK_MAP;
		}
		this.bytes = null;
		this.constantPool.reset();
		this.codeStream.reset(this);
		this.constantPoolOffset = 0;
		this.contentsOffset = 0;
		this.creatingProblemType = false;
		this.enclosingClassFile = null;
		this.headerOffset = 0;
		this.methodCount = 0;
		this.methodCountOffset = 0;
		if (this.innerClassesBindings != null) {
			this.innerClassesBindings.clear();
		}
		this.missingTypes = null;
		this.visitedTypes = null;
	}

	/**
	 * Resize the pool contents
	 */
	private final void resizeContents(int minimalSize) {
		int length = this.contents.length;
		int toAdd = length;
		if (toAdd < minimalSize)
			toAdd = minimalSize;
		System.arraycopy(this.contents, 0, this.contents = new byte[length + toAdd], 0, length);
	}

	private VerificationTypeInfo retrieveLocal(int currentPC, int resolvedPosition) {
		for (int i = 0, max = this.codeStream.allLocalsCounter; i < max; i++) {
			LocalVariableBinding localVariable = this.codeStream.locals[i];
			if (localVariable == null) continue;
			if (resolvedPosition == localVariable.resolvedPosition) {
				inits: for (int j = 0; j < localVariable.initializationCount; j++) {
					int startPC = localVariable.initializationPCs[j << 1];
					int endPC = localVariable.initializationPCs[(j << 1) + 1];
					if (currentPC < startPC) {
						continue inits;
					} else if (currentPC < endPC) {
						// the current local is an active local
						return new VerificationTypeInfo(localVariable.type);
					}
				}
			}
		}
		return null;
	}

	private int scanType(char[] methodSignature, int index) {
		switch (methodSignature[index]) {
			case '[':
				// array type
				return scanType(methodSignature, index + 1);
			case 'L':
				return CharOperation.indexOf(';', methodSignature, index + 1);
			case 'Z':
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
				return index;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * INTERNAL USE-ONLY
	 * This methods leaves the space for method counts recording.
	 */
	public void setForMethodInfos() {
		// leave some space for the methodCount
		this.methodCountOffset = this.contentsOffset;
		this.contentsOffset += 2;
	}

	public void traverse(MethodBinding methodBinding, int maxLocals, byte[] bytecodes, int codeOffset, int codeLength, ArrayList frames, boolean isClinit) {
		StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream) this.codeStream;
		int[] framePositions = stackMapFrameCodeStream.getFramePositions();
		int pc = codeOffset;
		int index;
		int[] constantPoolOffsets = this.constantPool.offsets;
		byte[] poolContents = this.constantPool.poolContent;

		// set initial values for frame positions
		int indexInFramePositions = 0;
		int framePositionsLength = framePositions.length;
		int currentFramePosition = framePositions[0];

		// set initial values for stack depth markers
		int indexInStackDepthMarkers = 0;
		StackDepthMarker[] stackDepthMarkers = stackMapFrameCodeStream.getStackDepthMarkers();
		int stackDepthMarkersLength = stackDepthMarkers == null ? 0 : stackDepthMarkers.length;
		boolean hasStackDepthMarkers = stackDepthMarkersLength != 0;
		StackDepthMarker stackDepthMarker = null;
		if (hasStackDepthMarkers) {
			stackDepthMarker = stackDepthMarkers[0];
		}

		// set initial values for stack markers (used only in cldc mode)
		int indexInStackMarkers = 0;
		StackMarker[] stackMarkers = stackMapFrameCodeStream.getStackMarkers();
		int stackMarkersLength = stackMarkers == null ? 0 : stackMarkers.length;
		boolean hasStackMarkers = stackMarkersLength != 0;
		StackMarker stackMarker = null;
		if (hasStackMarkers) {
			stackMarker = stackMarkers[0];
		}

		// set initial values for exception markers
		int indexInExceptionMarkers = 0;
		ExceptionMarker[] exceptionMarkers= stackMapFrameCodeStream.getExceptionMarkers();
		int exceptionsMarkersLength = exceptionMarkers == null ? 0 : exceptionMarkers.length;
		boolean hasExceptionMarkers = exceptionsMarkersLength != 0;
		ExceptionMarker exceptionMarker = null;
		if (hasExceptionMarkers) {
			exceptionMarker = exceptionMarkers[0];
		}

		StackMapFrame frame = new StackMapFrame(maxLocals);
		if (!isClinit) {
			initializeDefaultLocals(frame, methodBinding, maxLocals, codeLength);
		}
		frame.pc = -1;
		frames.add(frame.duplicate());
		while (true) {
			int currentPC = pc - codeOffset;
			if (hasStackMarkers && stackMarker.pc == currentPC) {
				VerificationTypeInfo[] infos = frame.stackItems;
				VerificationTypeInfo[] tempInfos = new VerificationTypeInfo[frame.numberOfStackItems];
				System.arraycopy(infos, 0, tempInfos, 0, frame.numberOfStackItems);
				stackMarker.setInfos(tempInfos);
			} else if (hasStackMarkers && stackMarker.destinationPC == currentPC) {
				VerificationTypeInfo[] infos = stackMarker.infos;
				frame.stackItems = infos;
				frame.numberOfStackItems = infos.length;
				indexInStackMarkers++;
				if (indexInStackMarkers < stackMarkersLength) {
					stackMarker = stackMarkers[indexInStackMarkers];
				} else {
					hasStackMarkers = false;
				}
			}
			if (hasStackDepthMarkers && stackDepthMarker.pc == currentPC) {
				TypeBinding typeBinding = stackDepthMarker.typeBinding;
				if (typeBinding != null) {
					if (stackDepthMarker.delta > 0) {
						frame.addStackItem(new VerificationTypeInfo(typeBinding));
					} else {
						frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(typeBinding);
					}
				} else {
					frame.numberOfStackItems--;
				}
				indexInStackDepthMarkers++;
				if (indexInStackDepthMarkers < stackDepthMarkersLength) {
					stackDepthMarker = stackDepthMarkers[indexInStackDepthMarkers];
				} else {
					hasStackDepthMarkers = false;
				}
			}
			if (hasExceptionMarkers && exceptionMarker.pc == currentPC) {
				frame.numberOfStackItems = 0;
				frame.addStackItem(new VerificationTypeInfo(0, VerificationTypeInfo.ITEM_OBJECT, exceptionMarker.constantPoolName));
				indexInExceptionMarkers++;
				if (indexInExceptionMarkers < exceptionsMarkersLength) {
					exceptionMarker = exceptionMarkers[indexInExceptionMarkers];
				} else {
					hasExceptionMarkers = false;
				}
			}
			if (currentFramePosition < currentPC) {
				do {
					indexInFramePositions++;
					if (indexInFramePositions < framePositionsLength) {
						currentFramePosition = framePositions[indexInFramePositions];
					} else {
						// no more frame to generate
						return;
					}
				} while (currentFramePosition < currentPC);
			}
			if (currentFramePosition == currentPC) {
				// need to build a new frame and create a stack map attribute entry
				StackMapFrame currentFrame = frame.duplicate();
				currentFrame.pc = currentPC;
				// initialize locals
				initializeLocals(isClinit ? true : methodBinding.isStatic(), currentPC, currentFrame);
				// insert a new frame
				frames.add(currentFrame);
				indexInFramePositions++;
				if (indexInFramePositions < framePositionsLength) {
					currentFramePosition = framePositions[indexInFramePositions];
				} else {
					// no more frame to generate
					return;
				}
			}
			byte opcode = (byte) u1At(bytecodes, 0, pc);
			switch (opcode) {
				case Opcodes.OPC_nop:
					pc++;
					break;
				case Opcodes.OPC_aconst_null:
					frame.addStackItem(TypeBinding.NULL);
					pc++;
					break;
				case Opcodes.OPC_iconst_m1:
				case Opcodes.OPC_iconst_0:
				case Opcodes.OPC_iconst_1:
				case Opcodes.OPC_iconst_2:
				case Opcodes.OPC_iconst_3:
				case Opcodes.OPC_iconst_4:
				case Opcodes.OPC_iconst_5:
					frame.addStackItem(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_lconst_0:
				case Opcodes.OPC_lconst_1:
					frame.addStackItem(TypeBinding.LONG);
					pc++;
					break;
				case Opcodes.OPC_fconst_0:
				case Opcodes.OPC_fconst_1:
				case Opcodes.OPC_fconst_2:
					frame.addStackItem(TypeBinding.FLOAT);
					pc++;
					break;
				case Opcodes.OPC_dconst_0:
				case Opcodes.OPC_dconst_1:
					frame.addStackItem(TypeBinding.DOUBLE);
					pc++;
					break;
				case Opcodes.OPC_bipush:
					frame.addStackItem(TypeBinding.BYTE);
					pc += 2;
					break;
				case Opcodes.OPC_sipush:
					frame.addStackItem(TypeBinding.SHORT);
					pc += 3;
					break;
				case Opcodes.OPC_ldc:
					index = u1At(bytecodes, 1, pc);
					switch (u1At(poolContents, 0, constantPoolOffsets[index])) {
						case ClassFileConstants.StringTag:
							frame
									.addStackItem(new VerificationTypeInfo(
											TypeIds.T_JavaLangString,
											ConstantPool.JavaLangStringConstantPoolName));
							break;
						case ClassFileConstants.IntegerTag:
							frame.addStackItem(TypeBinding.INT);
							break;
						case ClassFileConstants.FloatTag:
							frame.addStackItem(TypeBinding.FLOAT);
							break;
						case ClassFileConstants.ClassTag:
							frame.addStackItem(new VerificationTypeInfo(
									TypeIds.T_JavaLangClass,
									ConstantPool.JavaLangClassConstantPoolName));
					}
					pc += 2;
					break;
				case Opcodes.OPC_ldc_w:
					index = u2At(bytecodes, 1, pc);
					switch (u1At(poolContents, 0, constantPoolOffsets[index])) {
						case ClassFileConstants.StringTag:
							frame
									.addStackItem(new VerificationTypeInfo(
											TypeIds.T_JavaLangString,
											ConstantPool.JavaLangStringConstantPoolName));
							break;
						case ClassFileConstants.IntegerTag:
							frame.addStackItem(TypeBinding.INT);
							break;
						case ClassFileConstants.FloatTag:
							frame.addStackItem(TypeBinding.FLOAT);
							break;
						case ClassFileConstants.ClassTag:
							frame.addStackItem(new VerificationTypeInfo(
									TypeIds.T_JavaLangClass,
									ConstantPool.JavaLangClassConstantPoolName));
					}
					pc += 3;
					break;
				case Opcodes.OPC_ldc2_w:
					index = u2At(bytecodes, 1, pc);
					switch (u1At(poolContents, 0, constantPoolOffsets[index])) {
						case ClassFileConstants.DoubleTag:
							frame.addStackItem(TypeBinding.DOUBLE);
							break;
						case ClassFileConstants.LongTag:
							frame.addStackItem(TypeBinding.LONG);
							break;
					}
					pc += 3;
					break;
				case Opcodes.OPC_iload:
					frame.addStackItem(TypeBinding.INT);
					pc += 2;
					break;
				case Opcodes.OPC_lload:
					frame.addStackItem(TypeBinding.LONG);
					pc += 2;
					break;
				case Opcodes.OPC_fload:
					frame.addStackItem(TypeBinding.FLOAT);
					pc += 2;
					break;
				case Opcodes.OPC_dload:
					frame.addStackItem(TypeBinding.DOUBLE);
					pc += 2;
					break;
				case Opcodes.OPC_aload:
					index = u1At(bytecodes, 1, pc);
					VerificationTypeInfo localsN = retrieveLocal(currentPC, index);
					frame.addStackItem(localsN);
					pc += 2;
					break;
				case Opcodes.OPC_iload_0:
				case Opcodes.OPC_iload_1:
				case Opcodes.OPC_iload_2:
				case Opcodes.OPC_iload_3:
					frame.addStackItem(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_lload_0:
				case Opcodes.OPC_lload_1:
				case Opcodes.OPC_lload_2:
				case Opcodes.OPC_lload_3:
					frame.addStackItem(TypeBinding.LONG);
					pc++;
					break;
				case Opcodes.OPC_fload_0:
				case Opcodes.OPC_fload_1:
				case Opcodes.OPC_fload_2:
				case Opcodes.OPC_fload_3:
					frame.addStackItem(TypeBinding.FLOAT);
					pc++;
					break;
				case Opcodes.OPC_dload_0:
				case Opcodes.OPC_dload_1:
				case Opcodes.OPC_dload_2:
				case Opcodes.OPC_dload_3:
					frame.addStackItem(TypeBinding.DOUBLE);
					pc++;
					break;
				case Opcodes.OPC_aload_0:
					VerificationTypeInfo locals0 = frame.locals[0];
					if (locals0 == null || locals0.tag != VerificationTypeInfo.ITEM_UNINITIALIZED_THIS) {
						// special case to handle uninitialized object
						locals0 = retrieveLocal(currentPC, 0);
					}
					frame.addStackItem(locals0);
					pc++;
					break;
				case Opcodes.OPC_aload_1:
					VerificationTypeInfo locals1 = retrieveLocal(currentPC, 1);
					frame.addStackItem(locals1);
					pc++;
					break;
				case Opcodes.OPC_aload_2:
					VerificationTypeInfo locals2 = retrieveLocal(currentPC, 2);
					frame.addStackItem(locals2);
					pc++;
					break;
				case Opcodes.OPC_aload_3:
					VerificationTypeInfo locals3 = retrieveLocal(currentPC, 3);
					frame.addStackItem(locals3);
					pc++;
					break;
				case Opcodes.OPC_iaload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_laload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.LONG);
					pc++;
					break;
				case Opcodes.OPC_faload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.FLOAT);
					pc++;
					break;
				case Opcodes.OPC_daload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.DOUBLE);
					pc++;
					break;
				case Opcodes.OPC_aaload:
					frame.numberOfStackItems--;
					frame.replaceWithElementType();
					pc++;
					break;
				case Opcodes.OPC_baload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.BYTE);
					pc++;
					break;
				case Opcodes.OPC_caload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.CHAR);
					pc++;
					break;
				case Opcodes.OPC_saload:
					frame.numberOfStackItems -=2;
					frame.addStackItem(TypeBinding.SHORT);
					pc++;
					break;
				case Opcodes.OPC_istore:
				case Opcodes.OPC_lstore:
				case Opcodes.OPC_fstore:
				case Opcodes.OPC_dstore:
					frame.numberOfStackItems--;
					pc += 2;
					break;
				case Opcodes.OPC_astore:
					index = u1At(bytecodes, 1, pc);
					frame.numberOfStackItems--;
					pc += 2;
					break;
				case Opcodes.OPC_astore_0:
					frame.locals[0] = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_astore_1:
				case Opcodes.OPC_astore_2:
				case Opcodes.OPC_astore_3:
				case Opcodes.OPC_istore_0:
				case Opcodes.OPC_istore_1:
				case Opcodes.OPC_istore_2:
				case Opcodes.OPC_istore_3:
				case Opcodes.OPC_lstore_0:
				case Opcodes.OPC_lstore_1:
				case Opcodes.OPC_lstore_2:
				case Opcodes.OPC_lstore_3:
				case Opcodes.OPC_fstore_0:
				case Opcodes.OPC_fstore_1:
				case Opcodes.OPC_fstore_2:
				case Opcodes.OPC_fstore_3:
				case Opcodes.OPC_dstore_0:
				case Opcodes.OPC_dstore_1:
				case Opcodes.OPC_dstore_2:
				case Opcodes.OPC_dstore_3:
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_iastore:
				case Opcodes.OPC_lastore:
				case Opcodes.OPC_fastore:
				case Opcodes.OPC_dastore:
				case Opcodes.OPC_aastore:
				case Opcodes.OPC_bastore:
				case Opcodes.OPC_castore:
				case Opcodes.OPC_sastore:
					frame.numberOfStackItems-=3;
					pc++;
					break;
				case Opcodes.OPC_pop:
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_pop2:
					int numberOfStackItems = frame.numberOfStackItems;
					switch(frame.stackItems[numberOfStackItems - 1].id()) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							frame.numberOfStackItems--;
							break;
						default:
							frame.numberOfStackItems -= 2;
					}
					pc++;
					break;
				case Opcodes.OPC_dup:
					frame.addStackItem(frame.stackItems[frame.numberOfStackItems - 1]);
					pc++;
					break;
				case Opcodes.OPC_dup_x1:
					VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					frame.addStackItem(info);
					frame.addStackItem(info2);
					frame.addStackItem(info);
					pc++;
					break;
				case Opcodes.OPC_dup_x2:
					info = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					info2 = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					switch(info2.id()) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							frame.addStackItem(info);
							frame.addStackItem(info2);
							frame.addStackItem(info);
							break;
						default:
							numberOfStackItems = frame.numberOfStackItems;
							VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
							frame.numberOfStackItems--;
							frame.addStackItem(info);
							frame.addStackItem(info3);
							frame.addStackItem(info2);
							frame.addStackItem(info);
					}
					pc++;
					break;
				case Opcodes.OPC_dup2:
					info = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					switch(info.id()) {
						case TypeIds.T_double :
						case TypeIds.T_long :
							frame.addStackItem(info);
							frame.addStackItem(info);
							break;
						default:
							info2 = frame.stackItems[frame.numberOfStackItems - 1];
							frame.numberOfStackItems--;
							frame.addStackItem(info2);
							frame.addStackItem(info);
							frame.addStackItem(info2);
							frame.addStackItem(info);
					}
					pc++;
					break;
				case Opcodes.OPC_dup2_x1:
					info = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					info2 = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					switch(info.id()) {
						case TypeIds.T_double :
						case TypeIds.T_long :
							frame.addStackItem(info);
							frame.addStackItem(info2);
							frame.addStackItem(info);
							break;
						default:
							VerificationTypeInfo info3 = frame.stackItems[frame.numberOfStackItems - 1];
							frame.numberOfStackItems--;
							frame.addStackItem(info2);
							frame.addStackItem(info);
							frame.addStackItem(info3);
							frame.addStackItem(info2);
							frame.addStackItem(info);
					}
					pc++;
					break;
				case Opcodes.OPC_dup2_x2:
					numberOfStackItems = frame.numberOfStackItems;
					info = frame.stackItems[numberOfStackItems - 1];
					frame.numberOfStackItems--;
					info2 = frame.stackItems[frame.numberOfStackItems - 1];
					frame.numberOfStackItems--;
					switch(info.id()) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							switch(info2.id()) {
								case TypeIds.T_long :
								case TypeIds.T_double :
									// form 4
									frame.addStackItem(info);
									frame.addStackItem(info2);
									frame.addStackItem(info);
									break;
								default:
									// form 2
									numberOfStackItems = frame.numberOfStackItems;
									VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
									frame.numberOfStackItems--;
									frame.addStackItem(info);
									frame.addStackItem(info3);
									frame.addStackItem(info2);
									frame.addStackItem(info);
							}
							break;
						default:
							numberOfStackItems = frame.numberOfStackItems;
							VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
							frame.numberOfStackItems--;
							switch(info3.id()) {
								case TypeIds.T_long :
								case TypeIds.T_double :
									// form 3
									frame.addStackItem(info2);
									frame.addStackItem(info);
									frame.addStackItem(info3);
									frame.addStackItem(info2);
									frame.addStackItem(info);
									break;
								default:
									// form 1
									numberOfStackItems = frame.numberOfStackItems;
									VerificationTypeInfo info4 = frame.stackItems[numberOfStackItems - 1];
									frame.numberOfStackItems--;
									frame.addStackItem(info2);
									frame.addStackItem(info);
									frame.addStackItem(info4);
									frame.addStackItem(info3);
									frame.addStackItem(info2);
									frame.addStackItem(info);
							}
					}
					pc++;
					break;
				case Opcodes.OPC_swap:
					numberOfStackItems = frame.numberOfStackItems;
					info = frame.stackItems[numberOfStackItems - 1];
					info2 = frame.stackItems[numberOfStackItems - 2];
					frame.stackItems[numberOfStackItems - 1] = info2;
					frame.stackItems[numberOfStackItems - 2] = info;
					pc++;
					break;
				case Opcodes.OPC_iadd:
				case Opcodes.OPC_ladd:
				case Opcodes.OPC_fadd:
				case Opcodes.OPC_dadd:
				case Opcodes.OPC_isub:
				case Opcodes.OPC_lsub:
				case Opcodes.OPC_fsub:
				case Opcodes.OPC_dsub:
				case Opcodes.OPC_imul:
				case Opcodes.OPC_lmul:
				case Opcodes.OPC_fmul:
				case Opcodes.OPC_dmul:
				case Opcodes.OPC_idiv:
				case Opcodes.OPC_ldiv:
				case Opcodes.OPC_fdiv:
				case Opcodes.OPC_ddiv:
				case Opcodes.OPC_irem:
				case Opcodes.OPC_lrem:
				case Opcodes.OPC_frem:
				case Opcodes.OPC_drem:
				case Opcodes.OPC_ishl:
				case Opcodes.OPC_lshl:
				case Opcodes.OPC_ishr:
				case Opcodes.OPC_lshr:
				case Opcodes.OPC_iushr:
				case Opcodes.OPC_lushr:
				case Opcodes.OPC_iand:
				case Opcodes.OPC_land:
				case Opcodes.OPC_ior:
				case Opcodes.OPC_lor:
				case Opcodes.OPC_ixor:
				case Opcodes.OPC_lxor:
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_ineg:
				case Opcodes.OPC_lneg:
				case Opcodes.OPC_fneg:
				case Opcodes.OPC_dneg:
					pc++;
					break;
				case Opcodes.OPC_iinc:
					pc += 3;
					break;
				case Opcodes.OPC_i2l:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
					pc++;
					break;
				case Opcodes.OPC_i2f:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
					pc++;
					break;
				case Opcodes.OPC_i2d:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
					pc++;
					break;
				case Opcodes.OPC_l2i:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_l2f:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
					pc++;
					break;
				case Opcodes.OPC_l2d:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
					pc++;
					break;
				case Opcodes.OPC_f2i:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_f2l:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
					pc++;
					break;
				case Opcodes.OPC_f2d:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
					pc++;
					break;
				case Opcodes.OPC_d2i:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_d2l:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
					pc++;
					break;
				case Opcodes.OPC_d2f:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
					pc++;
					break;
				case Opcodes.OPC_i2b:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.BYTE);
					pc++;
					break;
				case Opcodes.OPC_i2c:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.CHAR);
					pc++;
					break;
				case Opcodes.OPC_i2s:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.SHORT);
					pc++;
					break;
				case Opcodes.OPC_lcmp:
				case Opcodes.OPC_fcmpl:
				case Opcodes.OPC_fcmpg:
				case Opcodes.OPC_dcmpl:
				case Opcodes.OPC_dcmpg:
					frame.numberOfStackItems-=2;
					frame.addStackItem(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_ifeq:
				case Opcodes.OPC_ifne:
				case Opcodes.OPC_iflt:
				case Opcodes.OPC_ifge:
				case Opcodes.OPC_ifgt:
				case Opcodes.OPC_ifle:
					frame.numberOfStackItems--;
					pc += 3;
					break;
				case Opcodes.OPC_if_icmpeq:
				case Opcodes.OPC_if_icmpne:
				case Opcodes.OPC_if_icmplt:
				case Opcodes.OPC_if_icmpge:
				case Opcodes.OPC_if_icmpgt:
				case Opcodes.OPC_if_icmple:
				case Opcodes.OPC_if_acmpeq:
				case Opcodes.OPC_if_acmpne:
					frame.numberOfStackItems -= 2;
					pc += 3;
					break;
				case Opcodes.OPC_goto:
					pc += 3;
					break;
				case Opcodes.OPC_tableswitch:
					pc++;
					while (((pc - codeOffset) & 0x03) != 0) {
						pc++;
					}
					pc += 4; // default
					int low = i4At(bytecodes, 0, pc);
					pc += 4;
					int high = i4At(bytecodes, 0, pc);
					pc += 4;
					int length = high - low + 1;
					pc += (length * 4);
					frame.numberOfStackItems--;
					break;
				case Opcodes.OPC_lookupswitch:
					pc++;
					while (((pc - codeOffset) & 0x03) != 0) {
						pc++;
					}
					pc += 4; // default
					int npairs = (int) u4At(bytecodes, 0, pc);
					pc += (4 + npairs * 8);
					frame.numberOfStackItems--;
					break;
				case Opcodes.OPC_ireturn:
				case Opcodes.OPC_lreturn:
				case Opcodes.OPC_freturn:
				case Opcodes.OPC_dreturn:
				case Opcodes.OPC_areturn:
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_return:
					pc++;
					break;
				case Opcodes.OPC_getstatic:
					index = u2At(bytecodes, 1, pc);
					int nameAndTypeIndex = u2At(poolContents, 3,
							constantPoolOffsets[index]);
					int utf8index = u2At(poolContents, 3,
							constantPoolOffsets[nameAndTypeIndex]);
					char[] descriptor = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					if (descriptor.length == 1) {
						// base type
						switch(descriptor[0]) {
							case 'Z':
								frame.addStackItem(TypeBinding.BOOLEAN);
								break;
							case 'B':
								frame.addStackItem(TypeBinding.BYTE);
								break;
							case 'C':
								frame.addStackItem(TypeBinding.CHAR);
								break;
							case 'D':
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case 'F':
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case 'I':
								frame.addStackItem(TypeBinding.INT);
								break;
							case 'J':
								frame.addStackItem(TypeBinding.LONG);
								break;
							case 'S':
								frame.addStackItem(TypeBinding.SHORT);
								break;
						}
					} else if (descriptor[0] == '[') {
						frame.addStackItem(new VerificationTypeInfo(0, descriptor));
					} else {
						frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(descriptor, 1, descriptor.length - 1)));
					}
					pc += 3;
					break;
				case Opcodes.OPC_putstatic:
					frame.numberOfStackItems--;
					pc += 3;
					break;
				case Opcodes.OPC_getfield:
					index = u2At(bytecodes, 1, pc);
					nameAndTypeIndex = u2At(poolContents, 3,
							constantPoolOffsets[index]);
					utf8index = u2At(poolContents, 3,
							constantPoolOffsets[nameAndTypeIndex]);
					descriptor = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					frame.numberOfStackItems--;
					if (descriptor.length == 1) {
						// base type
						switch(descriptor[0]) {
							case 'Z':
								frame.addStackItem(TypeBinding.BOOLEAN);
								break;
							case 'B':
								frame.addStackItem(TypeBinding.BYTE);
								break;
							case 'C':
								frame.addStackItem(TypeBinding.CHAR);
								break;
							case 'D':
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case 'F':
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case 'I':
								frame.addStackItem(TypeBinding.INT);
								break;
							case 'J':
								frame.addStackItem(TypeBinding.LONG);
								break;
							case 'S':
								frame.addStackItem(TypeBinding.SHORT);
								break;
						}
					} else if (descriptor[0] == '[') {
						frame.addStackItem(new VerificationTypeInfo(0, descriptor));
					} else {
						frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(descriptor, 1, descriptor.length - 1)));
					}
					pc += 3;
					break;
				case Opcodes.OPC_putfield:
					frame.numberOfStackItems -= 2;
					pc += 3;
					break;
				case Opcodes.OPC_invokevirtual:
					index = u2At(bytecodes, 1, pc);
					nameAndTypeIndex = u2At(poolContents, 3,
							constantPoolOffsets[index]);
					utf8index = u2At(poolContents, 3,
							constantPoolOffsets[nameAndTypeIndex]);
					descriptor = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[nameAndTypeIndex]);
					char[] name = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					frame.numberOfStackItems -= (getParametersCount(descriptor) + 1);
					char[] returnType = getReturnType(descriptor);
					if (returnType.length == 1) {
						// base type
						switch(returnType[0]) {
							case 'Z':
								frame.addStackItem(TypeBinding.BOOLEAN);
								break;
							case 'B':
								frame.addStackItem(TypeBinding.BYTE);
								break;
							case 'C':
								frame.addStackItem(TypeBinding.CHAR);
								break;
							case 'D':
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case 'F':
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case 'I':
								frame.addStackItem(TypeBinding.INT);
								break;
							case 'J':
								frame.addStackItem(TypeBinding.LONG);
								break;
							case 'S':
								frame.addStackItem(TypeBinding.SHORT);
								break;
						}
					} else {
						if (returnType[0] == '[') {
							frame.addStackItem(new VerificationTypeInfo(0, returnType));
						} else {
							frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
						}
					}
					pc += 3;
					break;
				case Opcodes.OPC_invokespecial:
					index = u2At(bytecodes, 1, pc);
					nameAndTypeIndex = u2At(poolContents, 3,
							constantPoolOffsets[index]);
					utf8index = u2At(poolContents, 3,
							constantPoolOffsets[nameAndTypeIndex]);
					descriptor = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[nameAndTypeIndex]);
					name = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					frame.numberOfStackItems -= getParametersCount(descriptor);
					if (CharOperation.equals(ConstantPool.Init, name)) {
						// constructor
						frame.stackItems[frame.numberOfStackItems - 1].tag = VerificationTypeInfo.ITEM_OBJECT;
					}
					frame.numberOfStackItems--;
					returnType = getReturnType(descriptor);
					if (returnType.length == 1) {
						// base type
						switch(returnType[0]) {
							case 'Z':
								frame.addStackItem(TypeBinding.BOOLEAN);
								break;
							case 'B':
								frame.addStackItem(TypeBinding.BYTE);
								break;
							case 'C':
								frame.addStackItem(TypeBinding.CHAR);
								break;
							case 'D':
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case 'F':
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case 'I':
								frame.addStackItem(TypeBinding.INT);
								break;
							case 'J':
								frame.addStackItem(TypeBinding.LONG);
								break;
							case 'S':
								frame.addStackItem(TypeBinding.SHORT);
								break;
						}
					} else {
						if (returnType[0] == '[') {
							frame.addStackItem(new VerificationTypeInfo(0, returnType));
						} else {
							frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
						}
					}
					pc += 3;
					break;
				case Opcodes.OPC_invokestatic:
					index = u2At(bytecodes, 1, pc);
					nameAndTypeIndex = u2At(poolContents, 3,
							constantPoolOffsets[index]);
					utf8index = u2At(poolContents, 3,
							constantPoolOffsets[nameAndTypeIndex]);
					descriptor = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[nameAndTypeIndex]);
					name = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					frame.numberOfStackItems -= getParametersCount(descriptor);
					returnType = getReturnType(descriptor);
					if (returnType.length == 1) {
						// base type
						switch(returnType[0]) {
							case 'Z':
								frame.addStackItem(TypeBinding.BOOLEAN);
								break;
							case 'B':
								frame.addStackItem(TypeBinding.BYTE);
								break;
							case 'C':
								frame.addStackItem(TypeBinding.CHAR);
								break;
							case 'D':
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case 'F':
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case 'I':
								frame.addStackItem(TypeBinding.INT);
								break;
							case 'J':
								frame.addStackItem(TypeBinding.LONG);
								break;
							case 'S':
								frame.addStackItem(TypeBinding.SHORT);
								break;
						}
					} else {
						if (returnType[0] == '[') {
							frame.addStackItem(new VerificationTypeInfo(0, returnType));
						} else {
							frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
						}
					}
					pc += 3;
					break;
				case Opcodes.OPC_invokeinterface:
					index = u2At(bytecodes, 1, pc);
					nameAndTypeIndex = u2At(poolContents, 3,
							constantPoolOffsets[index]);
					utf8index = u2At(poolContents, 3,
							constantPoolOffsets[nameAndTypeIndex]);
					descriptor = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[nameAndTypeIndex]);
					name = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					// we don't need count and args
					// u1At(bytecodes, 3, pc); // count
					// u1At(bytecodes, 4, pc); // extra args
					frame.numberOfStackItems -= (getParametersCount(descriptor) + 1);
					returnType = getReturnType(descriptor);
					if (returnType.length == 1) {
						// base type
						switch(returnType[0]) {
							case 'Z':
								frame.addStackItem(TypeBinding.BOOLEAN);
								break;
							case 'B':
								frame.addStackItem(TypeBinding.BYTE);
								break;
							case 'C':
								frame.addStackItem(TypeBinding.CHAR);
								break;
							case 'D':
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case 'F':
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case 'I':
								frame.addStackItem(TypeBinding.INT);
								break;
							case 'J':
								frame.addStackItem(TypeBinding.LONG);
								break;
							case 'S':
								frame.addStackItem(TypeBinding.SHORT);
								break;
						}
					} else {
						if (returnType[0] == '[') {
							frame.addStackItem(new VerificationTypeInfo(0, returnType));
						} else {
							frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
						}
					}
					pc += 5;
					break;
				case Opcodes.OPC_new:
					index = u2At(bytecodes, 1, pc);
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[index]);
					char[] className = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo(0, VerificationTypeInfo.ITEM_UNINITIALIZED, className);
					verificationTypeInfo.offset = currentPC;
					frame.addStackItem(verificationTypeInfo);
					pc += 3;
					break;
				case Opcodes.OPC_newarray:
					char[] constantPoolName = null;
					switch (u1At(bytecodes, 1, pc)) {
						case ClassFileConstants.INT_ARRAY :
							constantPoolName = new char[] { '[', 'I' };
							break;
						case ClassFileConstants.BYTE_ARRAY :
							constantPoolName = new char[] { '[', 'B' };
							break;
						case ClassFileConstants.BOOLEAN_ARRAY :
							constantPoolName = new char[] { '[', 'Z' };
							break;
						case ClassFileConstants.SHORT_ARRAY :
							constantPoolName = new char[] { '[', 'S' };
							break;
						case ClassFileConstants.CHAR_ARRAY :
							constantPoolName = new char[] { '[', 'C' };
							break;
						case ClassFileConstants.LONG_ARRAY :
							constantPoolName = new char[] { '[', 'J' };
							break;
						case ClassFileConstants.FLOAT_ARRAY :
							constantPoolName = new char[] { '[', 'F' };
							break;
						case ClassFileConstants.DOUBLE_ARRAY :
							constantPoolName = new char[] { '[', 'D' };
							break;
					}
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeIds.T_JavaLangObject, constantPoolName);
					pc += 2;
					break;
				case Opcodes.OPC_anewarray:
					index = u2At(bytecodes, 1, pc);
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[index]);
					className = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					int classNameLength = className.length;
					if (className[0] != '[') {
						// this is a type name (class or interface). So we add appropriate '[', 'L' and ';'.
						System.arraycopy(className, 0, (constantPoolName = new char[classNameLength + 3]), 2, classNameLength);
						constantPoolName[0] = '[';
						constantPoolName[1] = 'L';
						constantPoolName[classNameLength + 2] = ';';
					} else {
						// if class name is already an array, we just need to add one dimension
						System.arraycopy(className, 0, (constantPoolName = new char[classNameLength + 1]), 1, classNameLength);
						constantPoolName[0] = '[';
					}
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(0, constantPoolName);
					pc += 3;
					break;
				case Opcodes.OPC_arraylength:
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
					pc++;
					break;
				case Opcodes.OPC_athrow:
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_checkcast:
					index = u2At(bytecodes, 1, pc);
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[index]);
					className = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(0, className);
					pc += 3;
					break;
				case Opcodes.OPC_instanceof:
					// no need to know the class index = u2At(bytecodes, 1, pc);
					frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
					pc += 3;
					break;
				case Opcodes.OPC_monitorenter:
				case Opcodes.OPC_monitorexit:
					frame.numberOfStackItems--;
					pc++;
					break;
				case Opcodes.OPC_wide:
					opcode = (byte) u1At(bytecodes, 1, pc);
					if (opcode == Opcodes.OPC_iinc) {
						// index = u2At(bytecodes, 2, pc);
						// i2At(bytecodes, 4, pc); // const
						// we don't need the index and the const value
						pc += 6;
					} else {
						index = u2At(bytecodes, 2, pc);
						// need to handle iload, fload, aload, lload, dload, istore, fstore, astore, lstore or dstore
						switch(opcode) {
							case Opcodes.OPC_iload :
								frame.addStackItem(TypeBinding.INT);
								break;
							case Opcodes.OPC_fload :
								frame.addStackItem(TypeBinding.FLOAT);
								break;
							case Opcodes.OPC_aload :
								localsN = frame.locals[index];
								if (localsN == null) {
									localsN = retrieveLocal(currentPC, index);
								}
								frame.addStackItem(localsN);
								break;
							case Opcodes.OPC_lload :
								frame.addStackItem(TypeBinding.LONG);
								break;
							case Opcodes.OPC_dload :
								frame.addStackItem(TypeBinding.DOUBLE);
								break;
							case Opcodes.OPC_istore :
								frame.numberOfStackItems--;
								break;
							case Opcodes.OPC_fstore :
								frame.numberOfStackItems--;
								break;
							case Opcodes.OPC_astore :
								frame.locals[index] = frame.stackItems[frame.numberOfStackItems - 1];
								frame.numberOfStackItems--;
								break;
							case Opcodes.OPC_lstore :
								frame.numberOfStackItems--;
								break;
							case Opcodes.OPC_dstore :
								frame.numberOfStackItems--;
								break;
						}
						pc += 4;
					}
					break;
				case Opcodes.OPC_multianewarray:
					index = u2At(bytecodes, 1, pc);
					utf8index = u2At(poolContents, 1,
							constantPoolOffsets[index]);
					className = utf8At(poolContents,
							constantPoolOffsets[utf8index] + 3, u2At(
									poolContents, 1,
									constantPoolOffsets[utf8index]));
					int dimensions = u1At(bytecodes, 3, pc); // dimensions
					frame.numberOfStackItems -= dimensions;
					classNameLength = className.length;
					constantPoolName = new char[classNameLength + dimensions];
					for (int i = 0; i < dimensions; i++) {
						constantPoolName[i] = '[';
					}
					System.arraycopy(className, 0, constantPoolName, dimensions, classNameLength);
					frame.addStackItem(new VerificationTypeInfo(0, constantPoolName));
					pc += 4;
					break;
				case Opcodes.OPC_ifnull:
				case Opcodes.OPC_ifnonnull:
					frame.numberOfStackItems--;
					pc += 3;
					break;
				case Opcodes.OPC_goto_w:
					pc += 5;
					break;
				default: // should not occur
					this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
							Messages.bind(
									Messages.abort_invalidOpcode,
									new Object[] {
										new Byte(opcode),
										new Integer(pc),
										new String(methodBinding.shortReadableName()),
									}),
							this.codeStream.methodDeclaration);
				break;
			}
			if (pc >= (codeLength + codeOffset)) {
				break;
			}
		}
	}

	private final int u1At(byte[] reference, int relativeOffset,
			int structOffset) {
		return (reference[relativeOffset + structOffset] & 0xFF);
	}

	private final int u2At(byte[] reference, int relativeOffset,
			int structOffset) {
		int position = relativeOffset + structOffset;
		return ((reference[position++] & 0xFF) << 8)
				+ (reference[position] & 0xFF);
	}

	private final long u4At(byte[] reference, int relativeOffset,
			int structOffset) {
		int position = relativeOffset + structOffset;
		return (((reference[position++] & 0xFFL) << 24)
				+ ((reference[position++] & 0xFF) << 16)
				+ ((reference[position++] & 0xFF) << 8) + (reference[position] & 0xFF));
	}

	public char[] utf8At(byte[] reference, int absoluteOffset,
			int bytesAvailable) {
		int length = bytesAvailable;
		char outputBuf[] = new char[bytesAvailable];
		int outputPos = 0;
		int readOffset = absoluteOffset;

		while (length != 0) {
			int x = reference[readOffset++] & 0xFF;
			length--;
			if ((0x80 & x) != 0) {
				if ((x & 0x20) != 0) {
					length -= 2;
					x = ((x & 0xF) << 12)
							| ((reference[readOffset++] & 0x3F) << 6)
							| (reference[readOffset++] & 0x3F);
				} else {
					length--;
					x = ((x & 0x1F) << 6) | (reference[readOffset++] & 0x3F);
				}
			}
			outputBuf[outputPos++] = (char) x;
		}

		if (outputPos != bytesAvailable) {
			System.arraycopy(outputBuf, 0, (outputBuf = new char[outputPos]),
					0, outputPos);
		}
		return outputBuf;
	}
}
