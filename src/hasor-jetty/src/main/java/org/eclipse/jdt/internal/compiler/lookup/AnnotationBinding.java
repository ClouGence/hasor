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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Annotation;

/**
 * Represents JSR 175 Annotation instances in the type-system.
 */
public class AnnotationBinding {
	// do not access directly - use getters instead (UnresolvedAnnotationBinding
	// resolves types for type and pair contents just in time)
	ReferenceBinding type;
	ElementValuePair[] pairs;

/**
 * Add the standard annotations encoded in the tag bits to the recorded annotations.
 *
 * @param recordedAnnotations existing annotations already created
 * @param annotationTagBits
 * @param env
 * @return the combined list of annotations
 */
public static AnnotationBinding[] addStandardAnnotations(AnnotationBinding[] recordedAnnotations, long annotationTagBits, LookupEnvironment env) {
	// NOTE: expect annotations to be requested just once so there is no need to store the standard annotations
	// and all of the standard annotations created by this method are fully resolved since the sender is expected to use them immediately
	int count = 0;
	if ((annotationTagBits & TagBits.AnnotationTargetMASK) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationRetentionMASK) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationDeprecated) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationDocumented) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationInherited) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationOverride) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationSuppressWarnings) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationPolymorphicSignature) != 0)
		count++;
	if ((annotationTagBits & TagBits.AnnotationSafeVarargs) != 0)
		count++;
	if (count == 0)
		return recordedAnnotations;

	int index = recordedAnnotations.length;
	AnnotationBinding[] result = new AnnotationBinding[index + count];
	System.arraycopy(recordedAnnotations, 0, result, 0, index);
	if ((annotationTagBits & TagBits.AnnotationTargetMASK) != 0)
		result[index++] = buildTargetAnnotation(annotationTagBits, env);
	if ((annotationTagBits & TagBits.AnnotationRetentionMASK) != 0)
		result[index++] = buildRetentionAnnotation(annotationTagBits, env);
	if ((annotationTagBits & TagBits.AnnotationDeprecated) != 0)
		result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_DEPRECATED, env);
	if ((annotationTagBits & TagBits.AnnotationDocumented) != 0)
		result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED, env);
	if ((annotationTagBits & TagBits.AnnotationInherited) != 0)
		result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_ANNOTATION_INHERITED, env);
	if ((annotationTagBits & TagBits.AnnotationOverride) != 0)
		result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, env);
	if ((annotationTagBits & TagBits.AnnotationSuppressWarnings) != 0)
		result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_SUPPRESSWARNINGS, env);
	if ((annotationTagBits & TagBits.AnnotationPolymorphicSignature) != 0)
		result[index++] = buildMarkerAnnotationForMemberType(TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE, env);
	if ((annotationTagBits & TagBits.AnnotationSafeVarargs) != 0)
		result[index++] = buildMarkerAnnotation(TypeConstants.JAVA_LANG_SAFEVARARGS, env);
	return result;
}

private static AnnotationBinding buildMarkerAnnotationForMemberType(char[][] compoundName, LookupEnvironment env) {
	ReferenceBinding type = env.getResolvedType(compoundName, null);
	// since this is a member type name using '$' the return binding is a
	// problem reference binding with reason ProblemReasons.InternalNameProvided
	if (!type.isValidBinding()) {
		type = ((ProblemReferenceBinding) type).closestMatch;
	}
	return env.createAnnotation(type, Binding.NO_ELEMENT_VALUE_PAIRS);
}

private static AnnotationBinding buildMarkerAnnotation(char[][] compoundName, LookupEnvironment env) {
	ReferenceBinding type = env.getResolvedType(compoundName, null);
	return env.createAnnotation(type, Binding.NO_ELEMENT_VALUE_PAIRS);
}

private static AnnotationBinding buildRetentionAnnotation(long bits, LookupEnvironment env) {
	ReferenceBinding retentionPolicy =
		env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY,
			null);
	Object value = null;
	if ((bits & TagBits.AnnotationRuntimeRetention) == TagBits.AnnotationRuntimeRetention) {
		value = retentionPolicy.getField(TypeConstants.UPPER_RUNTIME, true);
	} else if ((bits & TagBits.AnnotationClassRetention) != 0) {
		value = retentionPolicy.getField(TypeConstants.UPPER_CLASS, true);
	} else if ((bits & TagBits.AnnotationSourceRetention) != 0) {
		value = retentionPolicy.getField(TypeConstants.UPPER_SOURCE, true);
	}
	return env.createAnnotation(
		env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_RETENTION, null),
		new ElementValuePair[] {
			new ElementValuePair(TypeConstants.VALUE, value, null)
		});
}

private static AnnotationBinding buildTargetAnnotation(long bits, LookupEnvironment env) {
	ReferenceBinding target = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_TARGET, null);
	if ((bits & TagBits.AnnotationTarget) != 0)
		return new AnnotationBinding(target, Binding.NO_ELEMENT_VALUE_PAIRS);

	int arraysize = 0;
	if ((bits & TagBits.AnnotationForAnnotationType) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForConstructor) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForField) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForLocalVariable) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForMethod) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForPackage) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForParameter) != 0)
		arraysize++;
	if ((bits & TagBits.AnnotationForType) != 0)
		arraysize++;
	Object[] value = new Object[arraysize];
	if (arraysize > 0) {
		ReferenceBinding elementType = env.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE, null);
		int index = 0;
		if ((bits & TagBits.AnnotationForAnnotationType) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_ANNOTATION_TYPE, true);
		if ((bits & TagBits.AnnotationForConstructor) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_CONSTRUCTOR, true);
		if ((bits & TagBits.AnnotationForField) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_FIELD, true);
		if ((bits & TagBits.AnnotationForLocalVariable) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_LOCAL_VARIABLE, true);
		if ((bits & TagBits.AnnotationForMethod) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_METHOD, true);
		if ((bits & TagBits.AnnotationForPackage) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_PACKAGE, true);
		if ((bits & TagBits.AnnotationForParameter) != 0)
			value[index++] = elementType.getField(TypeConstants.UPPER_PARAMETER, true);
		if ((bits & TagBits.AnnotationForType) != 0)
			value[index++] = elementType.getField(TypeConstants.TYPE, true);
	}
	return env.createAnnotation(
			target,
			new ElementValuePair[] {
				new ElementValuePair(TypeConstants.VALUE, value, null)
			});
}

AnnotationBinding(ReferenceBinding type, ElementValuePair[] pairs) {
	this.type = type;
	this.pairs = pairs;
}

AnnotationBinding(Annotation astAnnotation) {
	this((ReferenceBinding) astAnnotation.resolvedType, astAnnotation.computeElementValuePairs());
}

/*
 * Computes a key that uniquely identifies this binding, using the given recipient's unique key.
 * recipientKey @ typeKey
 * @MyAnnot void bar() --> Lp/X;.bar()V@Lp/MyAnnot;
 */
public char[] computeUniqueKey(char[] recipientKey) {
	char[] typeKey = this.type.computeUniqueKey(false);
	int recipientKeyLength = recipientKey.length;
	char[] uniqueKey = new char[recipientKeyLength+1+typeKey.length];
	System.arraycopy(recipientKey, 0, uniqueKey, 0, recipientKeyLength);
	uniqueKey[recipientKeyLength] = '@';
	System.arraycopy(typeKey, 0, uniqueKey, recipientKeyLength+1, typeKey.length);
	return uniqueKey;
}

public ReferenceBinding getAnnotationType() {
	return this.type;
}

public ElementValuePair[] getElementValuePairs() {
	return this.pairs;
}

public static void setMethodBindings(ReferenceBinding type, ElementValuePair[] pairs) {
	// set the method bindings of each element value pair
	for (int i = pairs.length; --i >= 0;) {
		ElementValuePair pair = pairs[i];
		MethodBinding[] methods = type.getMethods(pair.getName());
		// there should be exactly one since the type is an annotation type.
		if (methods != null && methods.length == 1)
			pair.setMethodBinding(methods[0]);
	}
}

public String toString() {
	StringBuffer buffer = new StringBuffer(5);
	buffer.append('@').append(this.type.sourceName);
	if (this.pairs != null && this.pairs.length > 0) {
		buffer.append("{ "); //$NON-NLS-1$
		for (int i = 0, max = this.pairs.length; i < max; i++) {
			if (i > 0) buffer.append(", "); //$NON-NLS-1$
			buffer.append(this.pairs[i]);
		}
		buffer.append('}');
	}
	return buffer.toString();
}
}
