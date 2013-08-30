/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public class AnnotationMirrorImpl implements AnnotationMirror, InvocationHandler {
	
	public final BaseProcessingEnvImpl _env;
	public final AnnotationBinding _binding;
	
	/* package */ AnnotationMirrorImpl(BaseProcessingEnvImpl env, AnnotationBinding binding) {
		_env = env;
		_binding = binding;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnnotationMirrorImpl) {
			if (this._binding == null) {
				return ((AnnotationMirrorImpl) obj)._binding == null;
			}
			return equals(this._binding, ((AnnotationMirrorImpl) obj)._binding);
		}
		return false;
	}

	private static boolean equals(AnnotationBinding annotationBinding, AnnotationBinding annotationBinding2) {
		if (annotationBinding.getAnnotationType() != annotationBinding2.getAnnotationType()) return false;
		final ElementValuePair[] elementValuePairs = annotationBinding.getElementValuePairs();
		final ElementValuePair[] elementValuePairs2 = annotationBinding2.getElementValuePairs();
		final int length = elementValuePairs.length;
		if (length != elementValuePairs2.length) return false;
		loop: for (int i = 0; i < length; i++) {
			ElementValuePair pair = elementValuePairs[i];
			// loop on the given pair to make sure one will match
			for (int j = 0; j < length; j++) {
				ElementValuePair pair2 = elementValuePairs2[j];
				if (pair.binding == pair2.binding) {
					if (pair.value == null) {
						if (pair2.value == null) {
							continue loop;
						}
						return false;
					} else {
						if (pair2.value == null
								|| !pair2.value.equals(pair.value)) {
							return false;
						}
					}
					continue loop;
				}
			}
			return false;
		}
		return true;
	}

	public DeclaredType getAnnotationType() {
		return (DeclaredType) _env.getFactory().newTypeMirror(_binding.getAnnotationType());
	}
	
	/**
	 * @return all the members of this annotation mirror that have explicit values.
	 * Default values are not included.
	 */
	public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
		if (this._binding == null) {
			return Collections.emptyMap();
		}
		ElementValuePair[] pairs = _binding.getElementValuePairs();
		Map<ExecutableElement, AnnotationValue> valueMap =
			new LinkedHashMap<ExecutableElement, AnnotationValue>(pairs.length);
		for (ElementValuePair pair : pairs) {
			MethodBinding method = pair.getMethodBinding();
			if (method == null) {
				// ideally we should be able to create a fake ExecutableElementImpl
				continue;
			}
			ExecutableElement e = new ExecutableElementImpl(_env, method);
			AnnotationValue v = new AnnotationMemberValue(_env, pair.getValue(), method);
			valueMap.put(e, v);
		}
		return Collections.unmodifiableMap(valueMap);
	}
	
	/**
	 * {@see Elements#getElementValuesWithDefaults()}
	 * @return all the members of this annotation mirror that have explicit or default
	 * values.
	 */
	public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults() {
		if (this._binding == null) {
			return Collections.emptyMap();
		}
		ElementValuePair[] pairs = _binding.getElementValuePairs();
		ReferenceBinding annoType = _binding.getAnnotationType();
		Map<ExecutableElement, AnnotationValue> valueMap =
			new LinkedHashMap<ExecutableElement, AnnotationValue>();
		for (MethodBinding method : annoType.methods()) {
			// if binding is in ElementValuePair list, then get value from there
			boolean foundExplicitValue = false;
			for (int i = 0; i < pairs.length; ++i) {
				MethodBinding explicitBinding = pairs[i].getMethodBinding();
				if (method == explicitBinding) {
					ExecutableElement e = new ExecutableElementImpl(_env, explicitBinding);
					AnnotationValue v = new AnnotationMemberValue(_env, pairs[i].getValue(), explicitBinding);
					valueMap.put(e, v);
					foundExplicitValue = true;
					break;
				}
			}
			// else get default value if one exists
			if (!foundExplicitValue) {
				Object defaultVal = method.getDefaultValue();
				if (null != defaultVal) {
					ExecutableElement e = new ExecutableElementImpl(_env, method);
					AnnotationValue v = new AnnotationMemberValue(_env, defaultVal, method);
					valueMap.put(e, v);
				}
			}
		}
		return Collections.unmodifiableMap(valueMap);
	}
	
	public int hashCode() {
		if (this._binding == null) return this._env.hashCode();
		return this._binding.hashCode();
	}

	/*
	 * Used by getAnnotation(), which returns a reflective proxy of the annotation class.  When processors then
	 * invoke methods such as value() on the annotation proxy, this method is called.
	 * <p>
	 * A challenge here is that the processor was not necessarily compiled against the same annotation
	 * definition that the compiler is looking at right now, not to mention that the annotation itself
	 * may be defective in source.  So the actual type of the value may be quite different than the
	 * type expected by the caller, which will result in a ClassCastException, which is ugly for the
	 * processor to try to catch.  So we try to catch and correct this type mismatch where possible.
	 * <p>
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if (this._binding == null) return null;
        final String methodName = method.getName();
        if ( args == null || args.length == 0 ) {
            if( methodName.equals("hashCode") ) { //$NON-NLS-1$
                return new Integer( hashCode() );
            }
            else if( methodName.equals("toString") ) { //$NON-NLS-1$
                return toString();
            }
            else if( methodName.equals("annotationType")) { //$NON-NLS-1$
            	return proxy.getClass().getInterfaces()[0];
            }
        }
        else if ( args.length == 1 && methodName.equals("equals") ) { //$NON-NLS-1$
            return new Boolean( equals( args[0] ) );
        }
        
        // If it's not one of the above methods, it must be an annotation member, so it cannot take any arguments
        if ( args != null && args.length != 0 ) {
            throw new NoSuchMethodException("method " + method.getName() + formatArgs(args) + " does not exist on annotation " + toString()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        final MethodBinding methodBinding = getMethodBinding(methodName);
        if ( methodBinding == null ) {
            throw new NoSuchMethodException("method " + method.getName() + "() does not exist on annotation" + toString()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Object actualValue = null;
        boolean foundMethod = false;
        ElementValuePair[] pairs = _binding.getElementValuePairs();
		for (ElementValuePair pair : pairs) {
			if (methodName.equals(new String(pair.getName()))) {
				actualValue = pair.getValue();
				foundMethod = true;
				break;
			}
		}
		if (!foundMethod) {
			// couldn't find explicit value; see if there's a default
			actualValue = methodBinding.getDefaultValue(); 
		}
		Class<?> expectedType = method.getReturnType();
		TypeBinding actualType = methodBinding.returnType;
        return getReflectionValue(actualValue, actualType, expectedType);
	}

	/*
	 * (non-Javadoc)
	 * Sun implementation shows the values.  We avoid that here,
	 * because getting the values is not idempotent.
	 */
	@Override
	public String toString() {
		if (this._binding == null) {
			return "@any()"; //$NON-NLS-1$
		}
		return "@" + _binding.getAnnotationType().debugName(); //$NON-NLS-1$
	}
	
	/**
	 * Used for constructing exception message text.
	 * @return a string like "(a, b, c)".
	 */
    private String formatArgs(final Object[] args)
    {
        // estimate that each class name (plus the separators) is 10 characters long plus 2 for "()".
        final StringBuilder builder = new StringBuilder(args.length * 8 + 2 );
        builder.append('(');
        for( int i=0; i<args.length; i++ )
        {
            if( i > 0 ) 
            	builder.append(", "); //$NON-NLS-1$
            builder.append(args[i].getClass().getName());
        }
        builder.append(')');
        return builder.toString();
    }
    
	/**
	 * Find a particular annotation member by name.
	 * @return a compiler method binding, or null if no member was found.
	 */
	private MethodBinding getMethodBinding(String name) {
		ReferenceBinding annoType = _binding.getAnnotationType();
		MethodBinding[] methods = annoType.getMethods(name.toCharArray());
		for (MethodBinding method : methods) {
			// annotation members have no parameters
			if (method.parameters.length == 0) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Convert an annotation member value from JDT into Reflection, and from whatever its actual type
	 * is into whatever type the reflective invoker of a method is expecting.
	 * <p>
	 * Only certain types are permitted as member values.  Specifically, a member must be a constant,
	 * and must be either a primitive type, String, Class, an enum constant, an annotation, or an
	 * array of any of those.  Multidimensional arrays are not permitted.
	 * 
	 * @param annoValue the value as represented by {@link ElementValuePair#getValue()}
	 * @param actualType the return type of the corresponding {@link MethodBinding}
	 * @param expectedType the type that the reflective method invoker is expecting
	 * @return an object of the expected type representing the annotation member value, 
	 * or an appropriate dummy value (such as null) if no value is available
	 */
	private Object getReflectionValue(Object actualValue, TypeBinding actualType, Class<?> expectedType)
	{
		if (null == expectedType) {
			// With no expected type, we can't even guess at a conversion
			return null;
		}
		if (null == actualValue) {
			// Return a type-appropriate equivalent of null
			return Factory.getMatchingDummyValue(expectedType);
		}
		if (expectedType.isArray()) {
			if (Class.class.equals(expectedType.getComponentType())) {
				// package Class[]-valued return as a MirroredTypesException
				if (actualType.isArrayType() && actualValue instanceof Object[] &&
						((ArrayBinding)actualType).leafComponentType.erasure().id == TypeIds.T_JavaLangClass) {
					Object[] bindings = (Object[])actualValue;
					List<TypeMirror> mirrors = new ArrayList<TypeMirror>(bindings.length);
					for (int i = 0; i < bindings.length; ++i) {
						if (bindings[i] instanceof TypeBinding) {
							mirrors.add(_env.getFactory().newTypeMirror((TypeBinding)bindings[i]));
						}
					}
					throw new MirroredTypesException(mirrors);
				}
				// TODO: actual value is not a TypeBinding[].  Should we return a TypeMirror[] around an ErrorType?
				return null;
			}
			// Handle arrays of types other than Class, e.g., int[], MyEnum[], ...
			return convertJDTArrayToReflectionArray(actualValue, actualType, expectedType);
		}
		else if (Class.class.equals(expectedType)) {
			// package the Class-valued return as a MirroredTypeException
			if (actualValue instanceof TypeBinding) {
				TypeMirror mirror = _env.getFactory().newTypeMirror((TypeBinding)actualValue);
				throw new MirroredTypeException(mirror);
			}
			else {
				// TODO: actual value is not a TypeBinding.  Should we return a TypeMirror around an ErrorType?
				return null;
			}
		}
		else {
			// Handle unitary values of type other than Class, e.g., int, MyEnum, ...
			return convertJDTValueToReflectionType(actualValue, actualType, expectedType);
		}
	}

	/**
	 * Convert an array of JDT types as obtained from ElementValuePair.getValue()
	 * (e.g., an Object[] containing IntConstant elements) to the type expected by
	 * a reflective method invocation (e.g., int[]).
	 * <p>
	 * This does not handle arrays of Class, but it does handle primitives, enum constants,
	 * and types such as String.
	 * @param jdtValue the actual value returned by ElementValuePair.getValue() or MethodBinding.getDefault()
	 * @param jdtType the return type of the annotation method binding
	 * @param expectedType the type that the invoker of the method is expecting; must be an array type
	 * @return an Object which is, e.g., an int[]; or null, if an array cannot be created.
	 */
	private Object convertJDTArrayToReflectionArray(Object jdtValue, TypeBinding jdtType, Class<?> expectedType)
	{
		assert null != expectedType && expectedType.isArray();
		if (!jdtType.isArrayType()) {
			// the compiler says that the type binding isn't an array type; this probably means
			// that there's some sort of syntax error.
			return null;
		}
		Object[] jdtArray;
		// See bug 261969: it's legal to pass a solo element for an array-typed value
		if (jdtValue != null && !(jdtValue instanceof Object[])) {
			// Create an array of the expected type
			jdtArray = (Object[]) Array.newInstance(jdtValue.getClass(), 1);
			jdtArray[0] = jdtValue;
		} else {
			jdtArray = (Object[])jdtValue;
		}
		TypeBinding jdtLeafType = jdtType.leafComponentType();
		Class<?> expectedLeafType = expectedType.getComponentType();
        final int length = jdtArray.length;
        final Object returnArray = Array.newInstance(expectedLeafType, length);
        for (int i = 0; i < length; ++i) {
        	Object jdtElementValue = jdtArray[i];
    		if (expectedLeafType.isPrimitive() || String.class.equals(expectedLeafType)) {
    			if (jdtElementValue instanceof Constant) {
    				if (boolean.class.equals(expectedLeafType)) {
    					Array.setBoolean(returnArray, i, ((Constant)jdtElementValue).booleanValue());
    				}
    				else if (byte.class.equals(expectedLeafType)) {
    					Array.setByte(returnArray, i, ((Constant)jdtElementValue).byteValue());
    				}
    				else if (char.class.equals(expectedLeafType)) {
    					Array.setChar(returnArray, i, ((Constant)jdtElementValue).charValue());
    				}
    				else if (double.class.equals(expectedLeafType)) {
    					Array.setDouble(returnArray, i, ((Constant)jdtElementValue).doubleValue());
    				}
    				else if (float.class.equals(expectedLeafType)) {
    					Array.setFloat(returnArray, i, ((Constant)jdtElementValue).floatValue());
    				}
    				else if (int.class.equals(expectedLeafType)) {
    					Array.setInt(returnArray, i, ((Constant)jdtElementValue).intValue());
    				}
    				else if (long.class.equals(expectedLeafType)) {
    					Array.setLong(returnArray, i, ((Constant)jdtElementValue).longValue());
    				}
    				else if (short.class.equals(expectedLeafType)) {
    					Array.setShort(returnArray, i, ((Constant)jdtElementValue).shortValue());
    				}
    				else if (String.class.equals(expectedLeafType)) {
    					Array.set(returnArray, i, ((Constant)jdtElementValue).stringValue());
    				}
    			}
    			else {
	    			// Primitive or string is expected, but our actual value cannot be coerced into one.
	    			// TODO: if the actual value is an array of primitives, should we unpack the first one?
	    			Factory.setArrayMatchingDummyValue(returnArray, i, expectedLeafType);
    			}
    		}
    		else if (expectedLeafType.isEnum()) {
    			Object returnVal = null;
    	        if (jdtLeafType != null && jdtLeafType.isEnum() && jdtElementValue instanceof FieldBinding) {
    	        	FieldBinding binding = (FieldBinding)jdtElementValue;
    	        	try {
    	        		Field returnedField = null;
    	        		returnedField = expectedLeafType.getField( new String(binding.name) );
    	        		if (null != returnedField) {
    	        			returnVal = returnedField.get(null);
    	        		}
    	        	}
    	        	catch (NoSuchFieldException nsfe) {
    	        		// return null
    	        	}
    	        	catch (IllegalAccessException iae) {
    	        		// return null
    	        	}
    	        }
    	        Array.set(returnArray, i, returnVal);
    		}
    		else if (expectedLeafType.isAnnotation()) {
    			// member value is expected to be an annotation type.  Wrap it in an Annotation proxy.
    			Object returnVal = null;
    			if (jdtLeafType.isAnnotationType() && jdtElementValue instanceof AnnotationBinding) {
    				AnnotationMirrorImpl annoMirror =
    					(AnnotationMirrorImpl)_env.getFactory().newAnnotationMirror((AnnotationBinding)jdtElementValue);
    				returnVal = Proxy.newProxyInstance(expectedLeafType.getClassLoader(),
    						new Class[]{ expectedLeafType }, annoMirror );
    			}
    	        Array.set(returnArray, i, returnVal);
    		}
    		else {
    			Array.set(returnArray, i, null);
    		}
        }
		return returnArray;
	}

	/**
	 * Convert a JDT annotation value as obtained from ElementValuePair.getValue()
	 * (e.g., IntConstant, FieldBinding, etc.) to the type expected by a reflective
	 * method invocation (e.g., int, an enum constant, etc.).
	 * @return a value of type {@code expectedType}, or a dummy value of that type if
	 * the actual value cannot be converted.
	 */
	private Object convertJDTValueToReflectionType(Object jdtValue, TypeBinding actualType, Class<?> expectedType) {
		if (expectedType.isPrimitive() || String.class.equals(expectedType)) {
			if (jdtValue instanceof Constant) {
				if (boolean.class.equals(expectedType)) {
					return ((Constant)jdtValue).booleanValue();
				}
				else if (byte.class.equals(expectedType)) {
					return ((Constant)jdtValue).byteValue();
				}
				else if (char.class.equals(expectedType)) {
					return ((Constant)jdtValue).charValue();
				}
				else if (double.class.equals(expectedType)) {
					return ((Constant)jdtValue).doubleValue();
				}
				else if (float.class.equals(expectedType)) {
					return ((Constant)jdtValue).floatValue();
				}
				else if (int.class.equals(expectedType)) {
					return ((Constant)jdtValue).intValue();
				}
				else if (long.class.equals(expectedType)) {
					return ((Constant)jdtValue).longValue();
				}
				else if (short.class.equals(expectedType)) {
					return ((Constant)jdtValue).shortValue();
				}
				else if (String.class.equals(expectedType)) {
					return ((Constant)jdtValue).stringValue();
				}
			}
			// Primitive or string is expected, but our actual value cannot be coerced into one.
			// TODO: if the actual value is an array of primitives, should we unpack the first one?
			return Factory.getMatchingDummyValue(expectedType);
		}
		else if (expectedType.isEnum()) {
			Object returnVal = null;
	        if (actualType != null && actualType.isEnum() && jdtValue instanceof FieldBinding) {
	        	
	        	FieldBinding binding = (FieldBinding)jdtValue;
	        	try {
	        		Field returnedField = null;
	        		returnedField = expectedType.getField( new String(binding.name) );
	        		if (null != returnedField) {
	        			returnVal = returnedField.get(null);
	        		}
	        	}
	        	catch (NoSuchFieldException nsfe) {
	        		// return null
	        	}
	        	catch (IllegalAccessException iae) {
	        		// return null
	        	}
	        }
	        return null == returnVal ? Factory.getMatchingDummyValue(expectedType) : returnVal;
		}
		else if (expectedType.isAnnotation()) {
			// member value is expected to be an annotation type.  Wrap it in an Annotation proxy.
			if (actualType.isAnnotationType() && jdtValue instanceof AnnotationBinding) {
				AnnotationMirrorImpl annoMirror =
					(AnnotationMirrorImpl)_env.getFactory().newAnnotationMirror((AnnotationBinding)jdtValue);
				return Proxy.newProxyInstance(expectedType.getClassLoader(),
						new Class[]{ expectedType }, annoMirror );
			}
			else {
				// No way to cast a non-annotation value to an annotation type; return null to caller
				return null;
			}
		}
		else {
			return Factory.getMatchingDummyValue(expectedType);
		}
	}

}
