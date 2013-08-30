/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - contribution for bug 337868 - [compiler][model] incomplete support for package-info.java when using SearchableEnvironment
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFilePool;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class LookupEnvironment implements ProblemReasons, TypeConstants {

	/**
	 * Map from typeBinding -> accessRestriction rule
	 */
	private Map accessRestrictions;
	ImportBinding[] defaultImports;
	public PackageBinding defaultPackage;
	HashtableOfPackage knownPackages;
	private int lastCompletedUnitIndex = -1;
	private int lastUnitIndex = -1;

	public INameEnvironment nameEnvironment;
	public CompilerOptions globalOptions;

	public ProblemReporter problemReporter;
	public ClassFilePool classFilePool;
	// indicate in which step on the compilation we are.
	// step 1 : build the reference binding
	// step 2 : conect the hierarchy (connect bindings)
	// step 3 : build fields and method bindings.
	private int stepCompleted;
	public ITypeRequestor typeRequestor;

	private ArrayBinding[][] uniqueArrayBindings;
	private SimpleLookupTable uniqueParameterizedTypeBindings;
	private SimpleLookupTable uniqueRawTypeBindings;
	private SimpleLookupTable uniqueWildcardBindings;
	private SimpleLookupTable uniqueParameterizedGenericMethodBindings;
	
	// key is a string with the method selector value is an array of method bindings
	private SimpleLookupTable uniquePolymorphicMethodBindings;
	private SimpleLookupTable uniqueGetClassMethodBinding; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=300734

	public CompilationUnitDeclaration unitBeingCompleted = null; // only set while completing units
	public Object missingClassFileLocation = null; // only set when resolving certain references, to help locating problems
	private CompilationUnitDeclaration[] units = new CompilationUnitDeclaration[4];
	private MethodVerifier verifier;

	public MethodBinding arrayClone;

	private ArrayList missingTypes;
	Set typesBeingConnected;
	public boolean isProcessingAnnotations = false;

	final static int BUILD_FIELDS_AND_METHODS = 4;
	final static int BUILD_TYPE_HIERARCHY = 1;
	final static int CHECK_AND_SET_IMPORTS = 2;
	final static int CONNECT_TYPE_HIERARCHY = 3;

	static final ProblemPackageBinding TheNotFoundPackage = new ProblemPackageBinding(CharOperation.NO_CHAR, NotFound);
	static final ProblemReferenceBinding TheNotFoundType = new ProblemReferenceBinding(CharOperation.NO_CHAR_CHAR, null, NotFound);

public LookupEnvironment(ITypeRequestor typeRequestor, CompilerOptions globalOptions, ProblemReporter problemReporter, INameEnvironment nameEnvironment) {
	this.typeRequestor = typeRequestor;
	this.globalOptions = globalOptions;
	this.problemReporter = problemReporter;
	this.defaultPackage = new PackageBinding(this); // assume the default package always exists
	this.defaultImports = null;
	this.nameEnvironment = nameEnvironment;
	this.knownPackages = new HashtableOfPackage();
	this.uniqueArrayBindings = new ArrayBinding[5][];
	this.uniqueArrayBindings[0] = new ArrayBinding[50]; // start off the most common 1 dimension array @ 50
	this.uniqueParameterizedTypeBindings = new SimpleLookupTable(3);
	this.uniqueRawTypeBindings = new SimpleLookupTable(3);
	this.uniqueWildcardBindings = new SimpleLookupTable(3);
	this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
	this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
	this.missingTypes = null;
	this.accessRestrictions = new HashMap(3);
	this.classFilePool = ClassFilePool.newInstance();
	this.typesBeingConnected = new HashSet();
}

/**
 * Ask the name environment for a type which corresponds to the compoundName.
 * Answer null if the name cannot be found.
 */

public ReferenceBinding askForType(char[][] compoundName) {
	NameEnvironmentAnswer answer = this.nameEnvironment.findType(compoundName);
	if (answer == null) return null;

	if (answer.isBinaryType()) {
		// the type was found as a .class file
		this.typeRequestor.accept(answer.getBinaryType(), computePackageFrom(compoundName, false /* valid pkg */), answer.getAccessRestriction());
	} else if (answer.isCompilationUnit()) {
		// the type was found as a .java file, try to build it then search the cache
		this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
	} else if (answer.isSourceType()) {
		// the type was found as a source model
		this.typeRequestor.accept(answer.getSourceTypes(), computePackageFrom(compoundName, false /* valid pkg */), answer.getAccessRestriction());
	}
	return getCachedType(compoundName);
}
/* Ask the oracle for a type named name in the packageBinding.
* Answer null if the name cannot be found.
*/

ReferenceBinding askForType(PackageBinding packageBinding, char[] name) {
	if (packageBinding == null) {
		packageBinding = this.defaultPackage;
	}
	NameEnvironmentAnswer answer = this.nameEnvironment.findType(name, packageBinding.compoundName);
	if (answer == null)
		return null;

	if (answer.isBinaryType()) {
		// the type was found as a .class file
		this.typeRequestor.accept(answer.getBinaryType(), packageBinding, answer.getAccessRestriction());
	} else if (answer.isCompilationUnit()) {
		// the type was found as a .java file, try to build it then search the cache
		try {
			this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
		} catch (AbortCompilation abort) {
			if (CharOperation.equals(name, TypeConstants.PACKAGE_INFO_NAME))
				return null; // silently, requestor may not be able to handle compilation units (HierarchyResolver)
			throw abort;
		}
	} else if (answer.isSourceType()) {
		// the type was found as a source model
		this.typeRequestor.accept(answer.getSourceTypes(), packageBinding, answer.getAccessRestriction());
	}
	return packageBinding.getType0(name);
}

/* Create the initial type bindings for the compilation unit.
*
* See completeTypeBindings() for a description of the remaining steps
*
* NOTE: This method can be called multiple times as additional source files are needed
*/
public void buildTypeBindings(CompilationUnitDeclaration unit, AccessRestriction accessRestriction) {
	CompilationUnitScope scope = new CompilationUnitScope(unit, this);
	scope.buildTypeBindings(accessRestriction);
	int unitsLength = this.units.length;
	if (++this.lastUnitIndex >= unitsLength)
		System.arraycopy(this.units, 0, this.units = new CompilationUnitDeclaration[2 * unitsLength], 0, unitsLength);
	this.units[this.lastUnitIndex] = unit;
}

/* Cache the binary type since we know it is needed during this compile.
*
* Answer the created BinaryTypeBinding or null if the type is already in the cache.
*/
public BinaryTypeBinding cacheBinaryType(IBinaryType binaryType, AccessRestriction accessRestriction) {
	return cacheBinaryType(binaryType, true, accessRestriction);
}

/* Cache the binary type since we know it is needed during this compile.
*
* Answer the created BinaryTypeBinding or null if the type is already in the cache.
*/
public BinaryTypeBinding cacheBinaryType(IBinaryType binaryType, boolean needFieldsAndMethods, AccessRestriction accessRestriction) {
	char[][] compoundName = CharOperation.splitOn('/', binaryType.getName());
	ReferenceBinding existingType = getCachedType(compoundName);

	if (existingType == null || existingType instanceof UnresolvedReferenceBinding)
		// only add the binary type if its not already in the cache
		return createBinaryTypeFrom(binaryType, computePackageFrom(compoundName, false /* valid pkg */), needFieldsAndMethods, accessRestriction);
	return null; // the type already exists & can be retrieved from the cache
}

public void completeTypeBindings() {
	this.stepCompleted = BUILD_TYPE_HIERARCHY;

	for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; i++) {
	    (this.unitBeingCompleted = this.units[i]).scope.checkAndSetImports();
	}
	this.stepCompleted = CHECK_AND_SET_IMPORTS;

	for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; i++) {
	    (this.unitBeingCompleted = this.units[i]).scope.connectTypeHierarchy();
	}
	this.stepCompleted = CONNECT_TYPE_HIERARCHY;

	for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; i++) {
		CompilationUnitScope unitScope = (this.unitBeingCompleted = this.units[i]).scope;
		unitScope.checkParameterizedTypes();
		unitScope.buildFieldsAndMethods();
		this.units[i] = null; // release unnecessary reference to the parsed unit
	}
	this.stepCompleted = BUILD_FIELDS_AND_METHODS;
	this.lastCompletedUnitIndex = this.lastUnitIndex;
	this.unitBeingCompleted = null;
}

/*
* 1. Connect the type hierarchy for the type bindings created for parsedUnits.
* 2. Create the field bindings
* 3. Create the method bindings
*/

/* We know each known compilationUnit is free of errors at this point...
*
* Each step will create additional bindings unless a problem is detected, in which
* case either the faulty import/superinterface/field/method will be skipped or a
* suitable replacement will be substituted (such as Object for a missing superclass)
*/
public void completeTypeBindings(CompilationUnitDeclaration parsedUnit) {
	if (this.stepCompleted == BUILD_FIELDS_AND_METHODS) {
		// This can only happen because the original set of units are completely built and
		// are now being processed, so we want to treat all the additional units as a group
		// until they too are completely processed.
		completeTypeBindings();
	} else {
		if (parsedUnit.scope == null) return; // parsing errors were too severe

		if (this.stepCompleted >= CHECK_AND_SET_IMPORTS)
			(this.unitBeingCompleted = parsedUnit).scope.checkAndSetImports();

		if (this.stepCompleted >= CONNECT_TYPE_HIERARCHY)
			(this.unitBeingCompleted = parsedUnit).scope.connectTypeHierarchy();

		this.unitBeingCompleted = null;
	}
}

/*
* Used by other compiler tools which do not start by calling completeTypeBindings().
*
* 1. Connect the type hierarchy for the type bindings created for parsedUnits.
* 2. Create the field bindings
* 3. Create the method bindings
*/

/*
* Each step will create additional bindings unless a problem is detected, in which
* case either the faulty import/superinterface/field/method will be skipped or a
* suitable replacement will be substituted (such as Object for a missing superclass)
*/
public void completeTypeBindings(CompilationUnitDeclaration parsedUnit, boolean buildFieldsAndMethods) {
	if (parsedUnit.scope == null) return; // parsing errors were too severe

	(this.unitBeingCompleted = parsedUnit).scope.checkAndSetImports();
	parsedUnit.scope.connectTypeHierarchy();
	parsedUnit.scope.checkParameterizedTypes();
	if (buildFieldsAndMethods)
		parsedUnit.scope.buildFieldsAndMethods();
	this.unitBeingCompleted = null;
}

/*
* Used by other compiler tools which do not start by calling completeTypeBindings()
* and have more than 1 unit to complete.
*
* 1. Connect the type hierarchy for the type bindings created for parsedUnits.
* 2. Create the field bindings
* 3. Create the method bindings
*/
public void completeTypeBindings(CompilationUnitDeclaration[] parsedUnits, boolean[] buildFieldsAndMethods, int unitCount) {
	for (int i = 0; i < unitCount; i++) {
		CompilationUnitDeclaration parsedUnit = parsedUnits[i];
		if (parsedUnit.scope != null)
			(this.unitBeingCompleted = parsedUnit).scope.checkAndSetImports();
	}

	for (int i = 0; i < unitCount; i++) {
		CompilationUnitDeclaration parsedUnit = parsedUnits[i];
		if (parsedUnit.scope != null)
			(this.unitBeingCompleted = parsedUnit).scope.connectTypeHierarchy();
	}

	for (int i = 0; i < unitCount; i++) {
		CompilationUnitDeclaration parsedUnit = parsedUnits[i];
		if (parsedUnit.scope != null) {
			(this.unitBeingCompleted = parsedUnit).scope.checkParameterizedTypes();
			if (buildFieldsAndMethods[i])
				parsedUnit.scope.buildFieldsAndMethods();
		}
	}

	this.unitBeingCompleted = null;
}
public MethodBinding computeArrayClone(MethodBinding objectClone) {
	if (this.arrayClone == null) {
		this.arrayClone = new MethodBinding(
				(objectClone.modifiers & ~ClassFileConstants.AccProtected) | ClassFileConstants.AccPublic,
				TypeConstants.CLONE,
				objectClone.returnType,
				Binding.NO_PARAMETERS,
				Binding.NO_EXCEPTIONS, // no exception for array specific method
				(ReferenceBinding)objectClone.returnType);
	}
	return this.arrayClone;
	
}
public TypeBinding computeBoxingType(TypeBinding type) {
	TypeBinding boxedType;
	switch (type.id) {
		case TypeIds.T_JavaLangBoolean :
			return TypeBinding.BOOLEAN;
		case TypeIds.T_JavaLangByte :
			return TypeBinding.BYTE;
		case TypeIds.T_JavaLangCharacter :
			return TypeBinding.CHAR;
		case TypeIds.T_JavaLangShort :
			return TypeBinding.SHORT;
		case TypeIds.T_JavaLangDouble :
			return TypeBinding.DOUBLE;
		case TypeIds.T_JavaLangFloat :
			return TypeBinding.FLOAT;
		case TypeIds.T_JavaLangInteger :
			return TypeBinding.INT;
		case TypeIds.T_JavaLangLong :
			return TypeBinding.LONG;

		case TypeIds.T_int :
			boxedType = getType(JAVA_LANG_INTEGER);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_INTEGER, null, NotFound);
		case TypeIds.T_byte :
			boxedType = getType(JAVA_LANG_BYTE);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_BYTE, null, NotFound);
		case TypeIds.T_short :
			boxedType = getType(JAVA_LANG_SHORT);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_SHORT, null, NotFound);
		case TypeIds.T_char :
			boxedType = getType(JAVA_LANG_CHARACTER);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_CHARACTER, null, NotFound);
		case TypeIds.T_long :
			boxedType = getType(JAVA_LANG_LONG);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_LONG, null, NotFound);
		case TypeIds.T_float :
			boxedType = getType(JAVA_LANG_FLOAT);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_FLOAT, null, NotFound);
		case TypeIds.T_double :
			boxedType = getType(JAVA_LANG_DOUBLE);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_DOUBLE, null, NotFound);
		case TypeIds.T_boolean :
			boxedType = getType(JAVA_LANG_BOOLEAN);
			if (boxedType != null) return boxedType;
			return new ProblemReferenceBinding(JAVA_LANG_BOOLEAN, null, NotFound);
//		case TypeIds.T_int :
//			return getResolvedType(JAVA_LANG_INTEGER, null);
//		case TypeIds.T_byte :
//			return getResolvedType(JAVA_LANG_BYTE, null);
//		case TypeIds.T_short :
//			return getResolvedType(JAVA_LANG_SHORT, null);
//		case TypeIds.T_char :
//			return getResolvedType(JAVA_LANG_CHARACTER, null);
//		case TypeIds.T_long :
//			return getResolvedType(JAVA_LANG_LONG, null);
//		case TypeIds.T_float :
//			return getResolvedType(JAVA_LANG_FLOAT, null);
//		case TypeIds.T_double :
//			return getResolvedType(JAVA_LANG_DOUBLE, null);
//		case TypeIds.T_boolean :
//			return getResolvedType(JAVA_LANG_BOOLEAN, null);
	}
	// allow indirect unboxing conversion for wildcards and type parameters
	switch (type.kind()) {
		case Binding.WILDCARD_TYPE :
		case Binding.INTERSECTION_TYPE :
		case Binding.TYPE_PARAMETER :
			switch (type.erasure().id) {
				case TypeIds.T_JavaLangBoolean :
					return TypeBinding.BOOLEAN;
				case TypeIds.T_JavaLangByte :
					return TypeBinding.BYTE;
				case TypeIds.T_JavaLangCharacter :
					return TypeBinding.CHAR;
				case TypeIds.T_JavaLangShort :
					return TypeBinding.SHORT;
				case TypeIds.T_JavaLangDouble :
					return TypeBinding.DOUBLE;
				case TypeIds.T_JavaLangFloat :
					return TypeBinding.FLOAT;
				case TypeIds.T_JavaLangInteger :
					return TypeBinding.INT;
				case TypeIds.T_JavaLangLong :
					return TypeBinding.LONG;
			}
	}
	return type;
}

private PackageBinding computePackageFrom(char[][] constantPoolName, boolean isMissing) {
	if (constantPoolName.length == 1)
		return this.defaultPackage;

	PackageBinding packageBinding = getPackage0(constantPoolName[0]);
	if (packageBinding == null || packageBinding == TheNotFoundPackage) {
		packageBinding = new PackageBinding(constantPoolName[0], this);
		if (isMissing) packageBinding.tagBits |= TagBits.HasMissingType;
		this.knownPackages.put(constantPoolName[0], packageBinding);
	}

	for (int i = 1, length = constantPoolName.length - 1; i < length; i++) {
		PackageBinding parent = packageBinding;
		if ((packageBinding = parent.getPackage0(constantPoolName[i])) == null || packageBinding == TheNotFoundPackage) {
			packageBinding = new PackageBinding(CharOperation.subarray(constantPoolName, 0, i + 1), parent, this);
			if (isMissing) {
				packageBinding.tagBits |= TagBits.HasMissingType;
			}
			parent.addPackage(packageBinding);
		}
	}
	return packageBinding;
}

/**
 * Convert a given source type into a parameterized form if generic.
 * generic X<E> --> param X<E>
 */
public ReferenceBinding convertToParameterizedType(ReferenceBinding originalType) {
	if (originalType != null) {
		boolean isGeneric = originalType.isGenericType();
		ReferenceBinding originalEnclosingType = originalType.enclosingType();
		ReferenceBinding convertedEnclosingType = originalEnclosingType;
		boolean needToConvert = isGeneric;
		if (originalEnclosingType != null) {
			convertedEnclosingType = originalType.isStatic()
				? (ReferenceBinding) convertToRawType(originalEnclosingType, false /*do not force conversion of enclosing types*/)
				: convertToParameterizedType(originalEnclosingType);
			needToConvert |= originalEnclosingType != convertedEnclosingType;
		}
		if (needToConvert) {
			return createParameterizedType(originalType, isGeneric ? originalType.typeVariables() : null, convertedEnclosingType);
		}
	}
	return originalType;
}

/**
 * Returns the given binding's raw type binding.
 * @param type the TypeBinding to raw convert
 * @param forceRawEnclosingType forces recursive raw conversion of enclosing types (used in Javadoc references only)
 * @return TypeBinding the raw converted TypeBinding
 */
public TypeBinding convertToRawType(TypeBinding type, boolean forceRawEnclosingType) {
	int dimension;
	TypeBinding originalType;
	switch(type.kind()) {
		case Binding.BASE_TYPE :
		case Binding.TYPE_PARAMETER:
		case Binding.WILDCARD_TYPE:
		case Binding.INTERSECTION_TYPE:
		case Binding.RAW_TYPE:
			return type;
		case Binding.ARRAY_TYPE:
			dimension = type.dimensions();
			originalType = type.leafComponentType();
			break;
		default:
			if (type.id == TypeIds.T_JavaLangObject)
				return type; // Object is not generic
			dimension = 0;
			originalType = type;
	}
	boolean needToConvert;
	switch (originalType.kind()) {
		case Binding.BASE_TYPE :
			return type;
		case Binding.GENERIC_TYPE :
			needToConvert = true;
			break;
		case Binding.PARAMETERIZED_TYPE :
			ParameterizedTypeBinding paramType = (ParameterizedTypeBinding) originalType;
			needToConvert = paramType.genericType().isGenericType(); // only recursive call to enclosing type can find parameterizedType with arguments
			break;
		default :
			needToConvert = false;
			break;
	}
	ReferenceBinding originalEnclosing = originalType.enclosingType();
	TypeBinding convertedType;
	if (originalEnclosing == null) {
		convertedType = needToConvert ? createRawType((ReferenceBinding)originalType.erasure(), null) : originalType;
	} else {
		ReferenceBinding convertedEnclosing;
		if (originalEnclosing.kind() == Binding.RAW_TYPE) {
			needToConvert |= !((ReferenceBinding)originalType).isStatic();
			convertedEnclosing = originalEnclosing;
		} else if (forceRawEnclosingType && !needToConvert/*stop recursion when conversion occurs*/) {
			convertedEnclosing = (ReferenceBinding) convertToRawType(originalEnclosing, forceRawEnclosingType);
			needToConvert = originalEnclosing != convertedEnclosing; // only convert generic or parameterized types
		} else if (needToConvert || ((ReferenceBinding)originalType).isStatic()) {
			convertedEnclosing = (ReferenceBinding) convertToRawType(originalEnclosing, false);
		} else {
			convertedEnclosing = convertToParameterizedType(originalEnclosing);
		}
		if (needToConvert) {
			convertedType = createRawType((ReferenceBinding) originalType.erasure(), convertedEnclosing);
		} else if (originalEnclosing != convertedEnclosing) {
			convertedType = createParameterizedType((ReferenceBinding) originalType.erasure(), null, convertedEnclosing);
		} else {
			convertedType = originalType;
		}
	}
	if (originalType != convertedType) {
		return dimension > 0 ? (TypeBinding)createArrayType(convertedType, dimension) : convertedType;
	}
	return type;
}

/**
 * Convert an array of types in raw forms.
 * Only allocate an array if anything is different.
 */
public ReferenceBinding[] convertToRawTypes(ReferenceBinding[] originalTypes, boolean forceErasure, boolean forceRawEnclosingType) {
	if (originalTypes == null) return null;
    ReferenceBinding[] convertedTypes = originalTypes;
    for (int i = 0, length = originalTypes.length; i < length; i++) {
        ReferenceBinding originalType = originalTypes[i];
        ReferenceBinding convertedType = (ReferenceBinding) convertToRawType(forceErasure ? originalType.erasure() : originalType, forceRawEnclosingType);
        if (convertedType != originalType) {        
            if (convertedTypes == originalTypes) {
                System.arraycopy(originalTypes, 0, convertedTypes = new ReferenceBinding[length], 0, i);
            }
            convertedTypes[i] = convertedType;
        } else if (convertedTypes != originalTypes) {
            convertedTypes[i] = originalType;
        }
    }
    return convertedTypes;
}

// variation for unresolved types in binaries (consider generic type as raw)
public TypeBinding convertUnresolvedBinaryToRawType(TypeBinding type) {
	int dimension;
	TypeBinding originalType;
	switch(type.kind()) {
		case Binding.BASE_TYPE :
		case Binding.TYPE_PARAMETER:
		case Binding.WILDCARD_TYPE:
		case Binding.INTERSECTION_TYPE:
		case Binding.RAW_TYPE:
			return type;
		case Binding.ARRAY_TYPE:
			dimension = type.dimensions();
			originalType = type.leafComponentType();
			break;
		default:
			if (type.id == TypeIds.T_JavaLangObject)
				return type; // Object is not generic
			dimension = 0;
			originalType = type;
	}
	boolean needToConvert;
	switch (originalType.kind()) {
		case Binding.BASE_TYPE :
			return type;
		case Binding.GENERIC_TYPE :
			needToConvert = true;
			break;
		case Binding.PARAMETERIZED_TYPE :
			ParameterizedTypeBinding paramType = (ParameterizedTypeBinding) originalType;
			needToConvert = paramType.genericType().isGenericType(); // only recursive call to enclosing type can find parameterizedType with arguments
			break;
		default :
			needToConvert = false;
			break;
	}
	ReferenceBinding originalEnclosing = originalType.enclosingType();
	TypeBinding convertedType;
	if (originalEnclosing == null) {
		convertedType = needToConvert ? createRawType((ReferenceBinding)originalType.erasure(), null) : originalType;
	} else {
		ReferenceBinding convertedEnclosing = (ReferenceBinding) convertUnresolvedBinaryToRawType(originalEnclosing);
		if (convertedEnclosing != originalEnclosing) {
			needToConvert |= !((ReferenceBinding)originalType).isStatic();
		}
		if (needToConvert) {
			convertedType = createRawType((ReferenceBinding) originalType.erasure(), convertedEnclosing);
		} else if (originalEnclosing != convertedEnclosing) {
			convertedType = createParameterizedType((ReferenceBinding) originalType.erasure(), null, convertedEnclosing);
		} else {
			convertedType = originalType;
		}
	}
	if (originalType != convertedType) {
		return dimension > 0 ? (TypeBinding)createArrayType(convertedType, dimension) : convertedType;
	}
	return type;
}
/*
 *  Used to guarantee annotation identity.
 */
public AnnotationBinding createAnnotation(ReferenceBinding annotationType, ElementValuePair[] pairs) {
	if (pairs.length != 0) {
		AnnotationBinding.setMethodBindings(annotationType, pairs);
	}
	return new AnnotationBinding(annotationType, pairs);
}

/*
 *  Used to guarantee array type identity.
 */
public ArrayBinding createArrayType(TypeBinding leafComponentType, int dimensionCount) {
	if (leafComponentType instanceof LocalTypeBinding) // cache local type arrays with the local type itself
		return ((LocalTypeBinding) leafComponentType).createArrayType(dimensionCount, this);

	// find the array binding cache for this dimension
	int dimIndex = dimensionCount - 1;
	int length = this.uniqueArrayBindings.length;
	ArrayBinding[] arrayBindings;
	if (dimIndex < length) {
		if ((arrayBindings = this.uniqueArrayBindings[dimIndex]) == null)
			this.uniqueArrayBindings[dimIndex] = arrayBindings = new ArrayBinding[10];
	} else {
		System.arraycopy(
			this.uniqueArrayBindings, 0,
			this.uniqueArrayBindings = new ArrayBinding[dimensionCount][], 0,
			length);
		this.uniqueArrayBindings[dimIndex] = arrayBindings = new ArrayBinding[10];
	}

	// find the cached array binding for this leaf component type (if any)
	int index = -1;
	length = arrayBindings.length;
	while (++index < length) {
		ArrayBinding currentBinding = arrayBindings[index];
		if (currentBinding == null) // no matching array, but space left
			return arrayBindings[index] = new ArrayBinding(leafComponentType, dimensionCount, this);
		if (currentBinding.leafComponentType == leafComponentType)
			return currentBinding;
	}

	// no matching array, no space left
	System.arraycopy(
		arrayBindings, 0,
		(arrayBindings = new ArrayBinding[length * 2]), 0,
		length);
	this.uniqueArrayBindings[dimIndex] = arrayBindings;
	return arrayBindings[length] = new ArrayBinding(leafComponentType, dimensionCount, this);
}
public BinaryTypeBinding createBinaryTypeFrom(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
	return createBinaryTypeFrom(binaryType, packageBinding, true, accessRestriction);
}

public BinaryTypeBinding createBinaryTypeFrom(IBinaryType binaryType, PackageBinding packageBinding, boolean needFieldsAndMethods, AccessRestriction accessRestriction) {
	BinaryTypeBinding binaryBinding = new BinaryTypeBinding(packageBinding, binaryType, this);

	// resolve any array bindings which reference the unresolvedType
	ReferenceBinding cachedType = packageBinding.getType0(binaryBinding.compoundName[binaryBinding.compoundName.length - 1]);
	if (cachedType != null) { // update reference to unresolved binding after having read classfile (knows whether generic for raw conversion)
		if (cachedType instanceof UnresolvedReferenceBinding) {
			((UnresolvedReferenceBinding) cachedType).setResolvedType(binaryBinding, this);
		} else {
			if (cachedType.isBinaryBinding()) // sanity check... at this point the cache should ONLY contain unresolved types
				return (BinaryTypeBinding) cachedType;
			// it is possible with a large number of source files (exceeding AbstractImageBuilder.MAX_AT_ONCE) that a member type can be in the cache as an UnresolvedType,
			// but because its enclosingType is resolved while its created (call to BinaryTypeBinding constructor), its replaced with a source type
			return null;
		}
	}
	packageBinding.addType(binaryBinding);
	setAccessRestriction(binaryBinding, accessRestriction);
	binaryBinding.cachePartsFrom(binaryType, needFieldsAndMethods);
	return binaryBinding;
}

/*
 * Used to create types denoting missing types.
 * If package is given, then reuse the package; if not then infer a package from compound name.
 * If the package is existing, then install the missing type in type cache
*/
public MissingTypeBinding createMissingType(PackageBinding packageBinding, char[][] compoundName) {
	// create a proxy for the missing BinaryType
	if (packageBinding == null) {
		packageBinding = computePackageFrom(compoundName, true /* missing */);
		if (packageBinding == TheNotFoundPackage) packageBinding = this.defaultPackage;
	}
	MissingTypeBinding missingType = new MissingTypeBinding(packageBinding, compoundName, this);
	if (missingType.id != TypeIds.T_JavaLangObject) {
		// make Object be its superclass - it could in turn be missing as well
		ReferenceBinding objectType = getType(TypeConstants.JAVA_LANG_OBJECT);
		if (objectType == null) {
			objectType = createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);	// create a proxy for the missing Object type
		}
		missingType.setMissingSuperclass(objectType);
	}
	packageBinding.addType(missingType);
	if (this.missingTypes == null)
		this.missingTypes = new ArrayList(3);
	this.missingTypes.add(missingType);
	return missingType;
}

/*
* 1. Connect the type hierarchy for the type bindings created for parsedUnits.
* 2. Create the field bindings
* 3. Create the method bindings
*/
public PackageBinding createPackage(char[][] compoundName) {
	PackageBinding packageBinding = getPackage0(compoundName[0]);
	if (packageBinding == null || packageBinding == TheNotFoundPackage) {
		packageBinding = new PackageBinding(compoundName[0], this);
		this.knownPackages.put(compoundName[0], packageBinding);
	}

	for (int i = 1, length = compoundName.length; i < length; i++) {
		// check to see if it collides with a known type...
		// this case can only happen if the package does not exist as a directory in the file system
		// otherwise when the source type was defined, the correct error would have been reported
		// unless its an unresolved type which is referenced from an inconsistent class file
		// NOTE: empty packages are not packages according to changes in JLS v2, 7.4.3
		// so not all types cause collision errors when they're created even though the package did exist
		ReferenceBinding type = packageBinding.getType0(compoundName[i]);
		if (type != null && type != TheNotFoundType && !(type instanceof UnresolvedReferenceBinding))
			return null;

		PackageBinding parent = packageBinding;
		if ((packageBinding = parent.getPackage0(compoundName[i])) == null || packageBinding == TheNotFoundPackage) {
			// if the package is unknown, check to see if a type exists which would collide with the new package
			// catches the case of a package statement of: package java.lang.Object;
			// since the package can be added after a set of source files have already been compiled,
			// we need to check whenever a package is created
			if (this.nameEnvironment.findType(compoundName[i], parent.compoundName) != null)
				return null;

			packageBinding = new PackageBinding(CharOperation.subarray(compoundName, 0, i + 1), parent, this);
			parent.addPackage(packageBinding);
		}
	}
	return packageBinding;
}

public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, RawTypeBinding rawType) {
	// cached info is array of already created parameterized types for this type
	ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
	boolean needToGrow = false;
	int index = 0;
	if (cachedInfo != null){
		nextCachedMethod :
			// iterate existing parameterized for reusing one with same type arguments if any
			for (int max = cachedInfo.length; index < max; index++){
				ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
				if (cachedMethod == null) break nextCachedMethod;
				if (!cachedMethod.isRaw) continue nextCachedMethod;
				if (cachedMethod.declaringClass != (rawType == null ? genericMethod.declaringClass : rawType)) continue nextCachedMethod;
				return cachedMethod;
		}
		needToGrow = true;
	} else {
		cachedInfo = new ParameterizedGenericMethodBinding[5];
		this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length){
		System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedGenericMethodBinding[length*2], 0, length);
		this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
	}
	// add new binding
	ParameterizedGenericMethodBinding parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, rawType, this);
	cachedInfo[index] = parameterizedGenericMethod;
	return parameterizedGenericMethod;
}

public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, TypeBinding[] typeArguments) {
	// cached info is array of already created parameterized types for this type
	ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
	int argLength = typeArguments == null ? 0: typeArguments.length;
	boolean needToGrow = false;
	int index = 0;
	if (cachedInfo != null){
		nextCachedMethod :
			// iterate existing parameterized for reusing one with same type arguments if any
			for (int max = cachedInfo.length; index < max; index++){
				ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
				if (cachedMethod == null) break nextCachedMethod;
				if (cachedMethod.isRaw) continue nextCachedMethod;
				TypeBinding[] cachedArguments = cachedMethod.typeArguments;
				int cachedArgLength = cachedArguments == null ? 0 : cachedArguments.length;
				if (argLength != cachedArgLength) continue nextCachedMethod;
				for (int j = 0; j < cachedArgLength; j++){
					if (typeArguments[j] != cachedArguments[j]) continue nextCachedMethod;
				}
				// all arguments match, reuse current
				return cachedMethod;
		}
		needToGrow = true;
	} else {
		cachedInfo = new ParameterizedGenericMethodBinding[5];
		this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length){
		System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedGenericMethodBinding[length*2], 0, length);
		this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
	}
	// add new binding
	ParameterizedGenericMethodBinding parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, typeArguments, this);
	cachedInfo[index] = parameterizedGenericMethod;
	return parameterizedGenericMethod;
}
public PolymorphicMethodBinding createPolymorphicMethod(MethodBinding originalPolymorphicMethod, TypeBinding[] parameters) {
	// cached info is array of already created polymorphic methods for this type
	String key = new String(originalPolymorphicMethod.selector);
	PolymorphicMethodBinding[] cachedInfo = (PolymorphicMethodBinding[]) this.uniquePolymorphicMethodBindings.get(key);
	int parametersLength = parameters == null ? 0: parameters.length;
	TypeBinding[] parametersTypeBinding = new TypeBinding[parametersLength]; 
	for (int i = 0; i < parametersLength; i++) {
		TypeBinding parameterTypeBinding = parameters[i];
		if (parameterTypeBinding.id == TypeIds.T_null) {
			parametersTypeBinding[i] = getType(JAVA_LANG_VOID);
		} else {
			parametersTypeBinding[i] = parameterTypeBinding.erasure();
		}
	}
	boolean needToGrow = false;
	int index = 0;
	if (cachedInfo != null) {
		nextCachedMethod :
			// iterate existing polymorphic method for reusing one with same type arguments if any
			for (int max = cachedInfo.length; index < max; index++) {
				PolymorphicMethodBinding cachedMethod = cachedInfo[index];
				if (cachedMethod == null) {
					break nextCachedMethod;
				}
				if (cachedMethod.matches(parametersTypeBinding, originalPolymorphicMethod.returnType)) {
					return cachedMethod;
				}
		}
		needToGrow = true;
	} else {
		cachedInfo = new PolymorphicMethodBinding[5];
		this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length) {
		System.arraycopy(cachedInfo, 0, cachedInfo = new PolymorphicMethodBinding[length*2], 0, length);
		this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
	}
	// add new binding
	PolymorphicMethodBinding polymorphicMethod = new PolymorphicMethodBinding(
			originalPolymorphicMethod,
			parametersTypeBinding);
	cachedInfo[index] = polymorphicMethod;
	return polymorphicMethod;
}
public MethodBinding updatePolymorphicMethodReturnType(PolymorphicMethodBinding binding, TypeBinding typeBinding) {
	// update the return type to be the given return type, but reuse existing binding if one can match
	String key = new String(binding.selector);
	PolymorphicMethodBinding[] cachedInfo = (PolymorphicMethodBinding[]) this.uniquePolymorphicMethodBindings.get(key);
	boolean needToGrow = false;
	int index = 0;
	TypeBinding[] parameters = binding.parameters;
	if (cachedInfo != null) {
		nextCachedMethod :
			// iterate existing polymorphic method for reusing one with same type arguments if any
			for (int max = cachedInfo.length; index < max; index++) {
				PolymorphicMethodBinding cachedMethod = cachedInfo[index];
				if (cachedMethod == null) {
					break nextCachedMethod;
				}
				if (cachedMethod.matches(parameters, typeBinding)) {
					return cachedMethod;
				}
		}
		needToGrow = true;
	} else {
		cachedInfo = new PolymorphicMethodBinding[5];
		this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length) {
		System.arraycopy(cachedInfo, 0, cachedInfo = new PolymorphicMethodBinding[length*2], 0, length);
		this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
	}
	// add new binding
	PolymorphicMethodBinding polymorphicMethod = new PolymorphicMethodBinding(
			binding.original(),
			typeBinding,
			parameters);
	cachedInfo[index] = polymorphicMethod;
	return polymorphicMethod;
}
public ParameterizedMethodBinding createGetClassMethod(TypeBinding receiverType, MethodBinding originalMethod, Scope scope) {
	// see if we have already cached this method for the given receiver type.
	ParameterizedMethodBinding retVal = null;
	if (this.uniqueGetClassMethodBinding == null) {
		this.uniqueGetClassMethodBinding = new SimpleLookupTable(3);
	} else {
		retVal = (ParameterizedMethodBinding)this.uniqueGetClassMethodBinding.get(receiverType);
	}
	if (retVal == null) {
		retVal = ParameterizedMethodBinding.instantiateGetClass(receiverType, originalMethod, scope);
		this.uniqueGetClassMethodBinding.put(receiverType, retVal);
	}
	return retVal;
}

public ParameterizedTypeBinding createParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType) {
	// cached info is array of already created parameterized types for this type
	ParameterizedTypeBinding[] cachedInfo = (ParameterizedTypeBinding[])this.uniqueParameterizedTypeBindings.get(genericType);
	int argLength = typeArguments == null ? 0: typeArguments.length;
	boolean needToGrow = false;
	int index = 0;
	if (cachedInfo != null){
		nextCachedType :
			// iterate existing parameterized for reusing one with same type arguments if any
			for (int max = cachedInfo.length; index < max; index++){
			    ParameterizedTypeBinding cachedType = cachedInfo[index];
			    if (cachedType == null) break nextCachedType;
			    if (cachedType.actualType() != genericType) continue nextCachedType; // remain of unresolved type
			    if (cachedType.enclosingType() != enclosingType) continue nextCachedType;
				TypeBinding[] cachedArguments = cachedType.arguments;
				int cachedArgLength = cachedArguments == null ? 0 : cachedArguments.length;
				if (argLength != cachedArgLength) continue nextCachedType; // would be an error situation (from unresolved binaries)
				for (int j = 0; j < cachedArgLength; j++){
					if (typeArguments[j] != cachedArguments[j]) continue nextCachedType;
				}
				// all arguments match, reuse current
				return cachedType;
		}
		needToGrow = true;
	} else {
		cachedInfo = new ParameterizedTypeBinding[5];
		this.uniqueParameterizedTypeBindings.put(genericType, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length){
		System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedTypeBinding[length*2], 0, length);
		this.uniqueParameterizedTypeBindings.put(genericType, cachedInfo);
	}
	// add new binding
	ParameterizedTypeBinding parameterizedType = new ParameterizedTypeBinding(genericType,typeArguments, enclosingType, this);
	cachedInfo[index] = parameterizedType;
	return parameterizedType;
}

public RawTypeBinding createRawType(ReferenceBinding genericType, ReferenceBinding enclosingType) {
	// cached info is array of already created raw types for this type
	RawTypeBinding[] cachedInfo = (RawTypeBinding[])this.uniqueRawTypeBindings.get(genericType);
	boolean needToGrow = false;
	int index = 0;
	if (cachedInfo != null){
		nextCachedType :
			// iterate existing parameterized for reusing one with same type arguments if any
			for (int max = cachedInfo.length; index < max; index++){
			    RawTypeBinding cachedType = cachedInfo[index];
			    if (cachedType == null) break nextCachedType;
			    if (cachedType.actualType() != genericType) continue nextCachedType; // remain of unresolved type
			    if (cachedType.enclosingType() != enclosingType) continue nextCachedType;
				// all enclosing type match, reuse current
				return cachedType;
		}
		needToGrow = true;
	} else {
		cachedInfo = new RawTypeBinding[1];
		this.uniqueRawTypeBindings.put(genericType, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length){
		System.arraycopy(cachedInfo, 0, cachedInfo = new RawTypeBinding[length*2], 0, length);
		this.uniqueRawTypeBindings.put(genericType, cachedInfo);
	}
	// add new binding
	RawTypeBinding rawType = new RawTypeBinding(genericType, enclosingType, this);
	cachedInfo[index] = rawType;
	return rawType;

}

public WildcardBinding createWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind) {
	// cached info is array of already created wildcard  types for this type
	if (genericType == null) // pseudo wildcard denoting composite bounds for lub computation
		genericType = ReferenceBinding.LUB_GENERIC;
	WildcardBinding[] cachedInfo = (WildcardBinding[])this.uniqueWildcardBindings.get(genericType);
	boolean needToGrow = false;
	int index = 0;
	if (cachedInfo != null){
		nextCachedType :
			// iterate existing wildcards for reusing one with same information if any
			for (int max = cachedInfo.length; index < max; index++){
			    WildcardBinding cachedType = cachedInfo[index];
			    if (cachedType == null) break nextCachedType;
			    if (cachedType.genericType != genericType) continue nextCachedType; // remain of unresolved type
			    if (cachedType.rank != rank) continue nextCachedType;
			    if (cachedType.boundKind != boundKind) continue nextCachedType;
			    if (cachedType.bound != bound) continue nextCachedType;
			    if (cachedType.otherBounds != otherBounds) {
			    	int cachedLength = cachedType.otherBounds == null ? 0 : cachedType.otherBounds.length;
			    	int length = otherBounds == null ? 0 : otherBounds.length;
			    	if (cachedLength != length) continue nextCachedType;
			    	for (int j = 0; j < length; j++) {
			    		if (cachedType.otherBounds[j] != otherBounds[j]) continue nextCachedType;
			    	}
			    }
				// all match, reuse current
				return cachedType;
		}
		needToGrow = true;
	} else {
		cachedInfo = new WildcardBinding[10];
		this.uniqueWildcardBindings.put(genericType, cachedInfo);
	}
	// grow cache ?
	int length = cachedInfo.length;
	if (needToGrow && index == length){
		System.arraycopy(cachedInfo, 0, cachedInfo = new WildcardBinding[length*2], 0, length);
		this.uniqueWildcardBindings.put(genericType, cachedInfo);
	}
	// add new binding
	WildcardBinding wildcard = new WildcardBinding(genericType, rank, bound, otherBounds, boundKind, this);
	cachedInfo[index] = wildcard;
	return wildcard;
}

/**
 * Returns the access restriction associated to a given type, or null if none
 */
public AccessRestriction getAccessRestriction(TypeBinding type) {
	return (AccessRestriction) this.accessRestrictions.get(type);
}

/**
 *  Answer the type for the compoundName if it exists in the cache.
 * Answer theNotFoundType if it could not be resolved the first time
 * it was looked up, otherwise answer null.
 *
 * NOTE: Do not use for nested types... the answer is NOT the same for a.b.C or a.b.C.D.E
 * assuming C is a type in both cases. In the a.b.C.D.E case, null is the answer.
 */
public ReferenceBinding getCachedType(char[][] compoundName) {
	if (compoundName.length == 1) {
		return this.defaultPackage.getType0(compoundName[0]);
	}
	PackageBinding packageBinding = getPackage0(compoundName[0]);
	if (packageBinding == null || packageBinding == TheNotFoundPackage)
		return null;

	for (int i = 1, packageLength = compoundName.length - 1; i < packageLength; i++)
		if ((packageBinding = packageBinding.getPackage0(compoundName[i])) == null || packageBinding == TheNotFoundPackage)
			return null;
	return packageBinding.getType0(compoundName[compoundName.length - 1]);
}

/* Answer the top level package named name if it exists in the cache.
* Answer theNotFoundPackage if it could not be resolved the first time
* it was looked up, otherwise answer null.
*
* NOTE: Senders must convert theNotFoundPackage into a real problem
* package if its to returned.
*/
PackageBinding getPackage0(char[] name) {
	return this.knownPackages.get(name);
}

/* Answer the type corresponding to the compoundName.
* Ask the name environment for the type if its not in the cache.
* Fail with a classpath error if the type cannot be found.
*/
public ReferenceBinding getResolvedType(char[][] compoundName, Scope scope) {
	ReferenceBinding type = getType(compoundName);
	if (type != null) return type;

	// create a proxy for the missing BinaryType
	// report the missing class file first
	this.problemReporter.isClassPathCorrect(
		compoundName,
		scope == null ? this.unitBeingCompleted : scope.referenceCompilationUnit(),
		this.missingClassFileLocation);
	return createMissingType(null, compoundName);
}

/* Answer the top level package named name.
* Ask the oracle for the package if its not in the cache.
* Answer null if the package cannot be found.
*/
PackageBinding getTopLevelPackage(char[] name) {
	PackageBinding packageBinding = getPackage0(name);
	if (packageBinding != null) {
		if (packageBinding == TheNotFoundPackage)
			return null;
		return packageBinding;
	}

	if (this.nameEnvironment.isPackage(null, name)) {
		this.knownPackages.put(name, packageBinding = new PackageBinding(name, this));
		return packageBinding;
	}

	this.knownPackages.put(name, TheNotFoundPackage); // saves asking the oracle next time
	return null;
}

/* Answer the type corresponding to the compoundName.
* Ask the name environment for the type if its not in the cache.
* Answer null if the type cannot be found.
*/
public ReferenceBinding getType(char[][] compoundName) {
	ReferenceBinding referenceBinding;

	if (compoundName.length == 1) {
		if ((referenceBinding = this.defaultPackage.getType0(compoundName[0])) == null) {
			PackageBinding packageBinding = getPackage0(compoundName[0]);
			if (packageBinding != null && packageBinding != TheNotFoundPackage)
				return null; // collides with a known package... should not call this method in such a case
			referenceBinding = askForType(this.defaultPackage, compoundName[0]);
		}
	} else {
		PackageBinding packageBinding = getPackage0(compoundName[0]);
		if (packageBinding == TheNotFoundPackage)
			return null;

		if (packageBinding != null) {
			for (int i = 1, packageLength = compoundName.length - 1; i < packageLength; i++) {
				if ((packageBinding = packageBinding.getPackage0(compoundName[i])) == null)
					break;
				if (packageBinding == TheNotFoundPackage)
					return null;
			}
		}

		if (packageBinding == null)
			referenceBinding = askForType(compoundName);
		else if ((referenceBinding = packageBinding.getType0(compoundName[compoundName.length - 1])) == null)
			referenceBinding = askForType(packageBinding, compoundName[compoundName.length - 1]);
	}

	if (referenceBinding == null || referenceBinding == TheNotFoundType)
		return null;
	referenceBinding = (ReferenceBinding) BinaryTypeBinding.resolveType(referenceBinding, this, false /* no raw conversion for now */);

	// compoundName refers to a nested type incorrectly (for example, package1.A$B)
	if (referenceBinding.isNestedType())
		return new ProblemReferenceBinding(compoundName, referenceBinding, InternalNameProvided);
	return referenceBinding;
}

private TypeBinding[] getTypeArgumentsFromSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, ReferenceBinding genericType, char[][][] missingTypeNames) {
	java.util.ArrayList args = new java.util.ArrayList(2);
	int rank = 0;
	do {
		args.add(getTypeFromVariantTypeSignature(wrapper, staticVariables, enclosingType, genericType, rank++, missingTypeNames));
	} while (wrapper.signature[wrapper.start] != '>');
	wrapper.start++; // skip '>'
	TypeBinding[] typeArguments = new TypeBinding[args.size()];
	args.toArray(typeArguments);
	return typeArguments;
}

/* Answer the type corresponding to the compound name.
* Does not ask the oracle for the type if its not found in the cache... instead an
* unresolved type is returned which must be resolved before used.
*
* NOTE: Does NOT answer base types nor array types!
*/
private ReferenceBinding getTypeFromCompoundName(char[][] compoundName, boolean isParameterized, boolean wasMissingType) {
	ReferenceBinding binding = getCachedType(compoundName);
	if (binding == null) {
		PackageBinding packageBinding = computePackageFrom(compoundName, false /* valid pkg */);
		binding = new UnresolvedReferenceBinding(compoundName, packageBinding);
		if (wasMissingType) {
			binding.tagBits |= TagBits.HasMissingType; // record it was bound to a missing type
		}
		packageBinding.addType(binding);
	} else if (binding == TheNotFoundType) {
		// report the missing class file first
		this.problemReporter.isClassPathCorrect(compoundName, this.unitBeingCompleted, this.missingClassFileLocation);
		// create a proxy for the missing BinaryType
		binding = createMissingType(null, compoundName);
	} else if (!isParameterized) {
	    // check raw type, only for resolved types
        binding = (ReferenceBinding) convertUnresolvedBinaryToRawType(binding);
	}
	return binding;
}

/* Answer the type corresponding to the name from the binary file.
* Does not ask the oracle for the type if its not found in the cache... instead an
* unresolved type is returned which must be resolved before used.
*
* NOTE: Does NOT answer base types nor array types!
*/
ReferenceBinding getTypeFromConstantPoolName(char[] signature, int start, int end, boolean isParameterized, char[][][] missingTypeNames) {
	if (end == -1)
		end = signature.length;
	char[][] compoundName = CharOperation.splitOn('/', signature, start, end);
	boolean wasMissingType = false;
	if (missingTypeNames != null) {
		for (int i = 0, max = missingTypeNames.length; i < max; i++) {
			if (CharOperation.equals(compoundName, missingTypeNames[i])) {
				wasMissingType = true;
				break;
			}
		}
	}
	return getTypeFromCompoundName(compoundName, isParameterized, wasMissingType);
}

/* Answer the type corresponding to the signature from the binary file.
* Does not ask the oracle for the type if its not found in the cache... instead an
* unresolved type is returned which must be resolved before used.
*
* NOTE: Does answer base types & array types.
*/
TypeBinding getTypeFromSignature(char[] signature, int start, int end, boolean isParameterized, TypeBinding enclosingType, char[][][] missingTypeNames) {
	int dimension = 0;
	while (signature[start] == '[') {
		start++;
		dimension++;
	}
	if (end == -1)
		end = signature.length - 1;

	// Just switch on signature[start] - the L case is the else
	TypeBinding binding = null;
	if (start == end) {
		switch (signature[start]) {
			case 'I' :
				binding = TypeBinding.INT;
				break;
			case 'Z' :
				binding = TypeBinding.BOOLEAN;
				break;
			case 'V' :
				binding = TypeBinding.VOID;
				break;
			case 'C' :
				binding = TypeBinding.CHAR;
				break;
			case 'D' :
				binding = TypeBinding.DOUBLE;
				break;
			case 'B' :
				binding = TypeBinding.BYTE;
				break;
			case 'F' :
				binding = TypeBinding.FLOAT;
				break;
			case 'J' :
				binding = TypeBinding.LONG;
				break;
			case 'S' :
				binding = TypeBinding.SHORT;
				break;
			default :
				this.problemReporter.corruptedSignature(enclosingType, signature, start);
				// will never reach here, since error will cause abort
		}
	} else {
		binding = getTypeFromConstantPoolName(signature, start + 1, end, isParameterized, missingTypeNames); // skip leading 'L' or 'T'
	}

	if (dimension == 0)
		return binding;
	return createArrayType(binding, dimension);
}

public TypeBinding getTypeFromTypeSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, char[][][] missingTypeNames) {
	// TypeVariableSignature = 'T' Identifier ';'
	// ArrayTypeSignature = '[' TypeSignature
	// ClassTypeSignature = 'L' Identifier TypeArgs(optional) ';'
	//   or ClassTypeSignature '.' 'L' Identifier TypeArgs(optional) ';'
	// TypeArgs = '<' VariantTypeSignature VariantTypeSignatures '>'
	int dimension = 0;
	while (wrapper.signature[wrapper.start] == '[') {
		wrapper.start++;
		dimension++;
	}
	if (wrapper.signature[wrapper.start] == 'T') {
	    int varStart = wrapper.start + 1;
	    int varEnd = wrapper.computeEnd();
		for (int i = staticVariables.length; --i >= 0;)
			if (CharOperation.equals(staticVariables[i].sourceName, wrapper.signature, varStart, varEnd))
				return dimension == 0 ? (TypeBinding) staticVariables[i] : createArrayType(staticVariables[i], dimension);
	    ReferenceBinding initialType = enclosingType;
		do {
			TypeVariableBinding[] enclosingTypeVariables;
			if (enclosingType instanceof BinaryTypeBinding) { // compiler normal case, no eager resolution of binary variables
				enclosingTypeVariables = ((BinaryTypeBinding)enclosingType).typeVariables; // do not trigger resolution of variables
			} else { // codepath only use by codeassist for decoding signatures
				enclosingTypeVariables = enclosingType.typeVariables();
			}
			for (int i = enclosingTypeVariables.length; --i >= 0;)
				if (CharOperation.equals(enclosingTypeVariables[i].sourceName, wrapper.signature, varStart, varEnd))
					return dimension == 0 ? (TypeBinding) enclosingTypeVariables[i] : createArrayType(enclosingTypeVariables[i], dimension);
		} while ((enclosingType = enclosingType.enclosingType()) != null);
		this.problemReporter.undefinedTypeVariableSignature(CharOperation.subarray(wrapper.signature, varStart, varEnd), initialType);
		return null; // cannot reach this, since previous problem will abort compilation
	}
	boolean isParameterized;
	TypeBinding type = getTypeFromSignature(wrapper.signature, wrapper.start, wrapper.computeEnd(), isParameterized = (wrapper.end == wrapper.bracket), enclosingType, missingTypeNames);
	if (!isParameterized)
		return dimension == 0 ? type : createArrayType(type, dimension);

	// type must be a ReferenceBinding at this point, cannot be a BaseTypeBinding or ArrayTypeBinding
	ReferenceBinding actualType = (ReferenceBinding) type;
	if (actualType instanceof UnresolvedReferenceBinding)
		if (CharOperation.indexOf('$', actualType.compoundName[actualType.compoundName.length - 1]) > 0)
			actualType = (ReferenceBinding) BinaryTypeBinding.resolveType(actualType, this, false /* no raw conversion */); // must resolve member types before asking for enclosingType
	ReferenceBinding actualEnclosing = actualType.enclosingType();
	if (actualEnclosing != null) { // convert needed if read some static member type
		actualEnclosing = (ReferenceBinding) convertToRawType(actualEnclosing, false /*do not force conversion of enclosing types*/);
	}
	TypeBinding[] typeArguments = getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, actualType, missingTypeNames);
	ParameterizedTypeBinding parameterizedType = createParameterizedType(actualType, typeArguments, actualEnclosing);

	while (wrapper.signature[wrapper.start] == '.') {
		wrapper.start++; // skip '.'
		int memberStart = wrapper.start;
		char[] memberName = wrapper.nextWord();
		BinaryTypeBinding.resolveType(parameterizedType, this, false);
		ReferenceBinding memberType = parameterizedType.genericType().getMemberType(memberName);
		// need to protect against the member type being null when the signature is invalid
		if (memberType == null)
			this.problemReporter.corruptedSignature(parameterizedType, wrapper.signature, memberStart); // aborts
		if (wrapper.signature[wrapper.start] == '<') {
			wrapper.start++; // skip '<'
			typeArguments = getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, memberType, missingTypeNames);
		} else {
			typeArguments = null;
		}
		parameterizedType = createParameterizedType(memberType, typeArguments, parameterizedType);
	}
	wrapper.start++; // skip ';'
	return dimension == 0 ? (TypeBinding) parameterizedType : createArrayType(parameterizedType, dimension);
}

TypeBinding getTypeFromVariantTypeSignature(
		SignatureWrapper wrapper,
		TypeVariableBinding[] staticVariables,
		ReferenceBinding enclosingType,
		ReferenceBinding genericType,
		int rank,
		char[][][] missingTypeNames) {
	// VariantTypeSignature = '-' TypeSignature
	//   or '+' TypeSignature
	//   or TypeSignature
	//   or '*'
	switch (wrapper.signature[wrapper.start]) {
		case '-' :
			// ? super aType
			wrapper.start++;
			TypeBinding bound = getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames);
			return createWildcard(genericType, rank, bound, null /*no extra bound*/, Wildcard.SUPER);
		case '+' :
			// ? extends aType
			wrapper.start++;
			bound = getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames);
			return createWildcard(genericType, rank, bound, null /*no extra bound*/, Wildcard.EXTENDS);
		case '*' :
			// ?
			wrapper.start++;
			return createWildcard(genericType, rank, null, null /*no extra bound*/, Wildcard.UNBOUND);
		default :
			return getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames);
	}
}

boolean isMissingType(char[] typeName) {
	for (int i = this.missingTypes == null ? 0 : this.missingTypes.size(); --i >= 0;) {
		MissingTypeBinding missingType = (MissingTypeBinding) this.missingTypes.get(i);
		if (CharOperation.equals(missingType.sourceName, typeName))
			return true;
	}
	return false;
}

/* Ask the oracle if a package exists named name in the package named compoundName.
*/
boolean isPackage(char[][] compoundName, char[] name) {
	if (compoundName == null || compoundName.length == 0)
		return this.nameEnvironment.isPackage(null, name);
	return this.nameEnvironment.isPackage(compoundName, name);
}
// The method verifier is lazily initialized to guarantee the receiver, the compiler & the oracle are ready.
public MethodVerifier methodVerifier() {
	if (this.verifier == null)
		this.verifier = newMethodVerifier();
	return this.verifier;
}

public MethodVerifier newMethodVerifier() {
	/* Always use MethodVerifier15. Even in a 1.4 project, we must internalize type variables and
	   observe any parameterization of super class and/or super interfaces in order to be able to
	   detect overriding in the presence of generics.
	   See https://bugs.eclipse.org/bugs/show_bug.cgi?id=324850
	 */
	return new MethodVerifier15(this);
}

public void releaseClassFiles(org.eclipse.jdt.internal.compiler.ClassFile[] classFiles) {
	for (int i = 0, fileCount = classFiles.length; i < fileCount; i++)
		this.classFilePool.release(classFiles[i]);
}

public void reset() {
	this.defaultPackage = new PackageBinding(this); // assume the default package always exists
	this.defaultImports = null;
	this.knownPackages = new HashtableOfPackage();
	this.accessRestrictions = new HashMap(3);

	this.verifier = null;
	for (int i = this.uniqueArrayBindings.length; --i >= 0;) {
		ArrayBinding[] arrayBindings = this.uniqueArrayBindings[i];
		if (arrayBindings != null)
			for (int j = arrayBindings.length; --j >= 0;)
				arrayBindings[j] = null;
	}
	// NOTE: remember to fix #updateCaches(...) when adding unique binding caches
	this.uniqueParameterizedTypeBindings = new SimpleLookupTable(3);
	this.uniqueRawTypeBindings = new SimpleLookupTable(3);
	this.uniqueWildcardBindings = new SimpleLookupTable(3);
	this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
	this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
	this.uniqueGetClassMethodBinding = null;
	this.missingTypes = null;
	this.typesBeingConnected = new HashSet();

	for (int i = this.units.length; --i >= 0;)
		this.units[i] = null;
	this.lastUnitIndex = -1;
	this.lastCompletedUnitIndex = -1;
	this.unitBeingCompleted = null; // in case AbortException occurred

	this.classFilePool.reset();
	// name environment has a longer life cycle, and must be reset in
	// the code which created it.
}

/**
 * Associate a given type with some access restriction
 * (did not store the restriction directly into binding, since sparse information)
 */
public void setAccessRestriction(ReferenceBinding type, AccessRestriction accessRestriction) {
	if (accessRestriction == null) return;
	type.modifiers |= ExtraCompilerModifiers.AccRestrictedAccess;
	this.accessRestrictions.put(type, accessRestriction);
}

void updateCaches(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType) {
	// walk all the unique collections & replace the unresolvedType with the resolvedType
	// must prevent 2 entries so == still works (1 containing the unresolvedType and the other containing the resolvedType)
	if (this.uniqueParameterizedTypeBindings.get(unresolvedType) != null) { // update the key
		Object[] keys = this.uniqueParameterizedTypeBindings.keyTable;
		for (int i = 0, l = keys.length; i < l; i++) {
			if (keys[i] == unresolvedType) {
				keys[i] = resolvedType; // hashCode is based on compoundName so this works - cannot be raw since type of parameterized type
				break;
			}
		}
	}
	if (this.uniqueRawTypeBindings.get(unresolvedType) != null) { // update the key
		Object[] keys = this.uniqueRawTypeBindings.keyTable;
		for (int i = 0, l = keys.length; i < l; i++) {
			if (keys[i] == unresolvedType) {
				keys[i] = resolvedType; // hashCode is based on compoundName so this works
				break;
			}
		}
	}
	if (this.uniqueWildcardBindings.get(unresolvedType) != null) { // update the key
		Object[] keys = this.uniqueWildcardBindings.keyTable;
		for (int i = 0, l = keys.length; i < l; i++) {
			if (keys[i] == unresolvedType) {
				keys[i] = resolvedType; // hashCode is based on compoundName so this works
				break;
			}
		}
	}
}
}
