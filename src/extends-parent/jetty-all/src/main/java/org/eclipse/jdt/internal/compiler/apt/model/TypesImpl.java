/*******************************************************************************
 * Copyright (c) 2007, 2011 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *    IBM Corporation - fix for 342598
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

/**
 * Utilities for working with types (as opposed to elements).
 * There is one of these for every ProcessingEnvironment.
 */
public class TypesImpl implements Types {

    private final BaseProcessingEnvImpl _env;

    /*
     * The processing env creates and caches a TypesImpl.  Other clients should
     * not create their own; they should ask the env for it.
     */
    public TypesImpl(BaseProcessingEnvImpl env) {
        _env = env;
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#asElement(javax.lang.model.type.TypeMirror)
     */
    @Override
    public Element asElement(TypeMirror t) {
        switch(t.getKind()) {
            case DECLARED :
            case TYPEVAR :
                return _env.getFactory().newElement(((TypeMirrorImpl)t).binding());
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#asMemberOf(javax.lang.model.type.DeclaredType, javax.lang.model.element.Element)
     */
    @Override
    public TypeMirror asMemberOf(DeclaredType containing, Element element) {
        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("NYI: TypesImpl.asMemberOf(" + containing + ", " + element + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	ElementImpl elementImpl = (ElementImpl) element;
    	DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl) containing;
    	ReferenceBinding referenceBinding = (ReferenceBinding) declaredTypeImpl._binding;
    	switch(element.getKind()) {
    		case CONSTRUCTOR :
    		case METHOD :
    			MethodBinding methodBinding = (MethodBinding) elementImpl._binding;
    			if (methodBinding.declaringClass != referenceBinding) {
    				throw new IllegalArgumentException("element is not valid for the containing declared type"); //$NON-NLS-1$
    			}
    			for (MethodBinding method : referenceBinding.methods()) {
    				if (CharOperation.equals(method.selector, methodBinding.selector)
    						&& method.areParameterErasuresEqual(methodBinding)) {
    					return this._env.getFactory().newTypeMirror(method);
    				}
    			}
    			break;
    		case FIELD :
    		case ENUM_CONSTANT:
    			FieldBinding fieldBinding = (FieldBinding) elementImpl._binding;
    			if (fieldBinding.declaringClass != referenceBinding) {
    				throw new IllegalArgumentException("element is not valid for the containing declared type"); //$NON-NLS-1$
    			}
    			for (FieldBinding field : referenceBinding.fields()) {
    				if (CharOperation.equals(field.name, fieldBinding.name)) {
    					return this._env.getFactory().newTypeMirror(field);
    				}
    			}
    			break;
    		case ENUM :
    		case ANNOTATION_TYPE :
    		case INTERFACE :
    		case CLASS :
    			ReferenceBinding referenceBinding2 = (ReferenceBinding) elementImpl._binding;
    			if (referenceBinding2.enclosingType() != referenceBinding) {
    				throw new IllegalArgumentException("element is not valid for the containing declared type"); //$NON-NLS-1$
    			}
    			for (ReferenceBinding referenceBinding3 : referenceBinding.memberTypes()) {
    				if (CharOperation.equals(referenceBinding3.compoundName, referenceBinding3.compoundName)) {
    					return this._env.getFactory().newTypeMirror(referenceBinding3);
    				}
    			}
    			break;
    	}
		throw new IllegalArgumentException("element is not valid for the containing declared type: element kind " + element.getKind()); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#boxedClass(javax.lang.model.type.PrimitiveType)
     */
    @Override
    public TypeElement boxedClass(PrimitiveType p) {
        PrimitiveTypeImpl primitiveTypeImpl = (PrimitiveTypeImpl) p;
        BaseTypeBinding baseTypeBinding = (BaseTypeBinding)primitiveTypeImpl._binding;
        TypeBinding boxed = _env.getLookupEnvironment().computeBoxingType(baseTypeBinding);
        return (TypeElement) _env.getFactory().newElement(boxed);
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#capture(javax.lang.model.type.TypeMirror)
     */
    @Override
    public TypeMirror capture(TypeMirror t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("NYI: TypesImpl.capture(...)"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#contains(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)
     */
    @Override
    public boolean contains(TypeMirror t1, TypeMirror t2) {
    	switch(t1.getKind()) {
    		case EXECUTABLE :
    		case PACKAGE :
    			throw new IllegalArgumentException("Executable and package are illegal argument for Types.contains(..)"); //$NON-NLS-1$
    	}
    	switch(t2.getKind()) {
    		case EXECUTABLE :
    		case PACKAGE :
    			throw new IllegalArgumentException("Executable and package are illegal argument for Types.contains(..)"); //$NON-NLS-1$
    	}
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("NYI: TypesImpl.contains(" + t1 + ", " + t2 + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#directSupertypes(javax.lang.model.type.TypeMirror)
     */
    @Override
    public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
        switch(t.getKind()) {
            case PACKAGE :
            case EXECUTABLE :
                throw new IllegalArgumentException("Invalid type mirror for directSypertypes"); //$NON-NLS-1$
        }
        TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl) t;
        Binding binding = typeMirrorImpl._binding;
        if (binding instanceof ReferenceBinding) {
        	ReferenceBinding referenceBinding = (ReferenceBinding) binding;
        	ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();
        	ReferenceBinding superclass = referenceBinding.superclass();
			if (superclass != null) {
        		list.add(this._env.getFactory().newTypeMirror(superclass));
        	}
			for (ReferenceBinding interfaceBinding : referenceBinding.superInterfaces()) {
        		list.add(this._env.getFactory().newTypeMirror(interfaceBinding));
			}
			return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#erasure(javax.lang.model.type.TypeMirror)
     */
    @Override
    public TypeMirror erasure(TypeMirror t) {
    	TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl) t;
    	Binding binding = typeMirrorImpl._binding;
    	if (binding instanceof ReferenceBinding) {
    		return _env.getFactory().newTypeMirror(((ReferenceBinding) binding).erasure());
    	}
    	if (binding instanceof ArrayBinding) {
    		TypeBinding typeBinding = (TypeBinding) binding;
    		return _env.getFactory().newTypeMirror(
    				this._env.getLookupEnvironment().createArrayType(
    						typeBinding.leafComponentType().erasure(),
    						typeBinding.dimensions()));
    	}
    	return t;
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#getArrayType(javax.lang.model.type.TypeMirror)
     */
    @Override
    public ArrayType getArrayType(TypeMirror componentType) {
        TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl) componentType;
        TypeBinding typeBinding = (TypeBinding) typeMirrorImpl._binding;
        return (ArrayType) _env.getFactory().newTypeMirror(
        		this._env.getLookupEnvironment().createArrayType(
        				typeBinding.leafComponentType(),
        				typeBinding.dimensions() + 1));
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#getDeclaredType(javax.lang.model.element.TypeElement, javax.lang.model.type.TypeMirror[])
     */
    @Override
    public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
        int typeArgsLength = typeArgs.length;
        TypeElementImpl typeElementImpl = (TypeElementImpl) typeElem;
        ReferenceBinding referenceBinding = (ReferenceBinding) typeElementImpl._binding;
        TypeVariableBinding[] typeVariables = referenceBinding.typeVariables();
        int typeVariablesLength = typeVariables.length;
        if (typeArgsLength == 0) {
            if (referenceBinding.isGenericType()) {
                // must return a raw type
                return (DeclaredType) _env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createRawType(referenceBinding, null));
            }
            return (DeclaredType)typeElem.asType();
        } else if (typeArgsLength != typeVariablesLength) {
            throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem"); //$NON-NLS-1$
        }
        TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
        for (int i = 0; i < typeArgsLength; i++) {
            TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl) typeArgs[i];
            Binding binding = typeMirrorImpl._binding;
            if (!(binding instanceof TypeBinding)) {
                throw new IllegalArgumentException("Invalid type argument: " + typeMirrorImpl); //$NON-NLS-1$
            }
            typeArguments[i] = (TypeBinding) binding;
        }
        return (DeclaredType) _env.getFactory().newTypeMirror(
                this._env.getLookupEnvironment().createParameterizedType(referenceBinding, typeArguments, null));
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#getDeclaredType(javax.lang.model.type.DeclaredType, javax.lang.model.element.TypeElement, javax.lang.model.type.TypeMirror[])
     */
    @Override
    public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem,
            TypeMirror... typeArgs) {
        int typeArgsLength = typeArgs.length;
        TypeElementImpl typeElementImpl = (TypeElementImpl) typeElem;
        ReferenceBinding referenceBinding = (ReferenceBinding) typeElementImpl._binding;
        TypeVariableBinding[] typeVariables = referenceBinding.typeVariables();
        int typeVariablesLength = typeVariables.length;
        DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl) containing;
        ReferenceBinding enclosingType = (ReferenceBinding) declaredTypeImpl._binding;
        if (typeArgsLength == 0) {
            if (referenceBinding.isGenericType()) {
                // must return a raw type
                return (DeclaredType) _env.getFactory().newTypeMirror(this._env.getLookupEnvironment().createRawType(referenceBinding, enclosingType));
            }
            // TODO (see how to create a member type binding
            throw new UnsupportedOperationException("NYI: TypesImpl.getDeclaredType(...) for member types"); //$NON-NLS-1$
        } else if (typeArgsLength != typeVariablesLength) {
            throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem"); //$NON-NLS-1$
        }
        TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
        for (int i = 0; i < typeArgsLength; i++) {
            TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl) typeArgs[i];
            Binding binding = typeMirrorImpl._binding;
            if (!(binding instanceof TypeBinding)) {
                throw new IllegalArgumentException("Invalid type for a type arguments : " + typeMirrorImpl); //$NON-NLS-1$
            }
            typeArguments[i] = (TypeBinding) binding;
        }
        return (DeclaredType) _env.getFactory().newTypeMirror(
                this._env.getLookupEnvironment().createParameterizedType(referenceBinding, typeArguments, enclosingType));
    }

    @Override
    public NoType getNoType(TypeKind kind) {
        return _env.getFactory().getNoType(kind);
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#getNullType()
     */
    @Override
    public NullType getNullType() {
        return _env.getFactory().getNullType();
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#getPrimitiveType(javax.lang.model.type.TypeKind)
     */
    @Override
    public PrimitiveType getPrimitiveType(TypeKind kind) {
        return _env.getFactory().getPrimitiveType(kind);
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#getWildcardType(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)
     */
    @Override
    public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        if (extendsBound != null && superBound != null) {
            throw new IllegalArgumentException("Extends and super bounds cannot be set at the same time"); //$NON-NLS-1$
        }
        if (extendsBound != null) {
            TypeMirrorImpl extendsBoundMirrorType = (TypeMirrorImpl) extendsBound;
            TypeBinding typeBinding = (TypeBinding) extendsBoundMirrorType._binding;
            return (WildcardType) _env.getFactory().newTypeMirror(
            		this._env.getLookupEnvironment().createWildcard(
	                    null,
	                    0,
	                    typeBinding,
	                    null,
	                    Wildcard.EXTENDS));
        }
        if (superBound != null) {
            TypeMirrorImpl superBoundMirrorType = (TypeMirrorImpl) superBound;
            TypeBinding typeBinding = (TypeBinding) superBoundMirrorType._binding;
            return new WildcardTypeImpl(_env, this._env.getLookupEnvironment().createWildcard(
                    null,
                    0,
                    typeBinding,
                    null,
                    Wildcard.SUPER));
        }
        return new WildcardTypeImpl(_env, this._env.getLookupEnvironment().createWildcard(
                null,
                0,
                null,
                null,
                Wildcard.UNBOUND));
    }

    /**
     * @return true if a value of type t1 can be assigned to a variable of type t2, i.e., t2 = t1.
     */
    @Override
    public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        Binding b1 = ((TypeMirrorImpl)t1).binding();
        Binding b2 = ((TypeMirrorImpl)t2).binding();
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            // package, method, import, etc.
            throw new IllegalArgumentException();
        }
        if (((TypeBinding)b1).isCompatibleWith((TypeBinding)b2)) {
            return true;
        }

        TypeBinding convertedType = _env.getLookupEnvironment().computeBoxingType((TypeBinding)b1);
        return null != convertedType && convertedType.isCompatibleWith((TypeBinding)b2);
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#isSameType(javax.lang.model.type.TypeMirror, javax.lang.model.type.TypeMirror)
     */
    @Override
    public boolean isSameType(TypeMirror t1, TypeMirror t2) {
    	if (t1.getKind() == TypeKind.WILDCARD || t2.getKind() == TypeKind.WILDCARD) {
            // Wildcard types are never equal, according to the spec of this method
    		return false;
    	}
        if (t1 == t2) {
            return true;
        }
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        Binding b1 = ((TypeMirrorImpl)t1).binding();
        Binding b2 = ((TypeMirrorImpl)t2).binding();
        return b1 == b2;
    }

    /* (non-Javadoc)
     * @see javax.lang.model.util.Types#isSubsignature(javax.lang.model.type.ExecutableType, javax.lang.model.type.ExecutableType)
     */
    @Override
    public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
		MethodBinding methodBinding1 = (MethodBinding) ((ExecutableTypeImpl) m1)._binding;
		MethodBinding methodBinding2 = (MethodBinding) ((ExecutableTypeImpl) m2)._binding;
		if (!CharOperation.equals(methodBinding1.selector, methodBinding2.selector))
			return false;
		return methodBinding1.areParameterErasuresEqual(methodBinding2) && methodBinding1.areTypeVariableErasuresEqual(methodBinding2);
	}

    /**
     * @return true if t1 is a subtype of t2, or if t1 == t2.
     */
    @Override
    public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
    	if (t1 instanceof NoTypeImpl) {
    		if (t2 instanceof NoTypeImpl) {
    			return ((NoTypeImpl) t1).getKind() == ((NoTypeImpl) t2).getKind();
    		}
    		return false;
    	} else if (t2 instanceof NoTypeImpl) {
    		return false;
    	}
        if (!(t1 instanceof TypeMirrorImpl) || !(t2 instanceof TypeMirrorImpl)) {
            return false;
        }
        if (t1 == t2) {
            return true;
        }
        Binding b1 = ((TypeMirrorImpl)t1).binding();
        Binding b2 = ((TypeMirrorImpl)t2).binding();
        if (b1 == b2) {
            return true;
        }
        if (!(b1 instanceof TypeBinding) || !(b2 instanceof TypeBinding)) {
            // package, method, import, etc.
            return false;
        }
        if (b1.kind() == Binding.BASE_TYPE || b2.kind() == Binding.BASE_TYPE) {
            if (b1.kind() != b2.kind()) {
                return false;
            }
            else {
                // for primitives, compatibility implies subtype
                return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
            }
        }
        return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
    }

    @Override
    public PrimitiveType unboxedType(TypeMirror t) {
        if (!(((TypeMirrorImpl)t)._binding instanceof ReferenceBinding)) {
            // Not an unboxable type - could be primitive, array, not a type at all, etc.
            throw new IllegalArgumentException("Given type mirror cannot be unboxed"); //$NON-NLS-1$
        }
        ReferenceBinding boxed = (ReferenceBinding)((TypeMirrorImpl)t)._binding;
        TypeBinding unboxed = _env.getLookupEnvironment().computeBoxingType(boxed);
        if (unboxed.kind() != Binding.BASE_TYPE) {
            // No boxing conversion was found
            throw new IllegalArgumentException();
        }
        return (PrimitiveType) _env.getFactory().newTypeMirror((BaseTypeBinding)unboxed);
    }

}
