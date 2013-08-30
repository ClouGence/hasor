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

package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;

/**
 * Represent a set of irritant flags. Irritants are organized in up to 8 group
 * of 29, allowing for a maximum of 232 distinct irritants.
 */
public class IrritantSet {

	// Reserve two high bits for selecting the right bit pattern
	public final static int GROUP_MASK = ASTNode.Bit32 | ASTNode.Bit31 | ASTNode.Bit30;
	public final static int GROUP_SHIFT = 29;
	public final static int GROUP_MAX = 3; // can be increased up to 8

	// Group prefix for irritants
	public final static int GROUP0 = 0 << GROUP_SHIFT;
	public final static int GROUP1 = 1 << GROUP_SHIFT;
	public final static int GROUP2 = 2 << GROUP_SHIFT;
	// reveal subsequent groups as needed
	// public final static int GROUP3 = 3 << GROUP_SHIFT;
	// public final static int GROUP4 = 4 << GROUP_SHIFT;
	// public final static int GROUP5 = 5 << GROUP_SHIFT;
	// public final static int GROUP6 = 6 << GROUP_SHIFT;
	// public final static int GROUP7 = 7 << GROUP_SHIFT;

	// Predefine sets of irritants matching warning tokens
	public static final IrritantSet ALL = new IrritantSet(0xFFFFFFFF & ~GROUP_MASK);
	public static final IrritantSet BOXING = new IrritantSet(CompilerOptions.AutoBoxing);
	public static final IrritantSet CAST = new IrritantSet(CompilerOptions.UnnecessaryTypeCheck);
	public static final IrritantSet DEPRECATION = new IrritantSet(CompilerOptions.UsingDeprecatedAPI);
	public static final IrritantSet DEP_ANN = new IrritantSet(CompilerOptions.MissingDeprecatedAnnotation);
	public static final IrritantSet FALLTHROUGH = new IrritantSet(CompilerOptions.FallthroughCase);
	public static final IrritantSet FINALLY = new IrritantSet(CompilerOptions.FinallyBlockNotCompleting);
	public static final IrritantSet HIDING = new IrritantSet(CompilerOptions.MaskedCatchBlock);
	public static final IrritantSet INCOMPLETE_SWITCH = new IrritantSet(CompilerOptions.IncompleteEnumSwitch);
	public static final IrritantSet NLS = new IrritantSet(CompilerOptions.NonExternalizedString);
	public static final IrritantSet NULL = new IrritantSet(CompilerOptions.NullReference);
	public static final IrritantSet RAW = new IrritantSet(CompilerOptions.RawTypeReference);
	public static final IrritantSet RESTRICTION = new IrritantSet(CompilerOptions.ForbiddenReference);
	public static final IrritantSet SERIAL = new IrritantSet(CompilerOptions.MissingSerialVersion);
	public static final IrritantSet STATIC_ACCESS = new IrritantSet(CompilerOptions.IndirectStaticAccess);
	public static final IrritantSet STATIC_METHOD = new IrritantSet(CompilerOptions.MethodCanBeStatic);
	public static final IrritantSet SYNTHETIC_ACCESS = new IrritantSet(CompilerOptions.AccessEmulation);
	public static final IrritantSet SUPER = new IrritantSet(CompilerOptions.OverridingMethodWithoutSuperInvocation);
	public static final IrritantSet UNUSED = new IrritantSet(CompilerOptions.UnusedLocalVariable);
	public static final IrritantSet UNCHECKED = new IrritantSet(CompilerOptions.UncheckedTypeOperation);
	public static final IrritantSet UNQUALIFIED_FIELD_ACCESS = new IrritantSet(CompilerOptions.UnqualifiedFieldAccess);

	public static final IrritantSet JAVADOC = new IrritantSet(CompilerOptions.InvalidJavadoc);
	public static final IrritantSet COMPILER_DEFAULT_ERRORS = new IrritantSet(0); // no optional error by default	
	public static final IrritantSet COMPILER_DEFAULT_WARNINGS = new IrritantSet(0); // see static initializer below
	static {
		COMPILER_DEFAULT_WARNINGS
			// group-0 warnings enabled by default
			.set(
				CompilerOptions.MethodWithConstructorName
				| CompilerOptions.OverriddenPackageDefaultMethod
				| CompilerOptions.UsingDeprecatedAPI
				| CompilerOptions.MaskedCatchBlock
				| CompilerOptions.UnusedLocalVariable
				| CompilerOptions.NoImplicitStringConversion
				| CompilerOptions.AssertUsedAsAnIdentifier
				| CompilerOptions.UnusedImport
				| CompilerOptions.NonStaticAccessToStatic
				| CompilerOptions.NoEffectAssignment
				| CompilerOptions.IncompatibleNonInheritedInterfaceMethod
				| CompilerOptions.UnusedPrivateMember
				| CompilerOptions.FinallyBlockNotCompleting)
			// group-1 warnings enabled by default
			.set(
				CompilerOptions.UncheckedTypeOperation
				| CompilerOptions.FinalParameterBound
				| CompilerOptions.MissingSerialVersion
				| CompilerOptions.EnumUsedAsAnIdentifier
				| CompilerOptions.ForbiddenReference
				| CompilerOptions.VarargsArgumentNeedCast
				| CompilerOptions.NullReference
				| CompilerOptions.AnnotationSuperInterface
				| CompilerOptions.TypeHiding
				| CompilerOptions.DiscouragedReference
				| CompilerOptions.UnhandledWarningToken
				| CompilerOptions.RawTypeReference
				| CompilerOptions.UnusedLabel
				| CompilerOptions.UnusedTypeArguments
				| CompilerOptions.UnusedWarningToken
				| CompilerOptions.ComparingIdentical)
			// group-2 warnings enabled by default
			.set(
				CompilerOptions.DeadCode
				|CompilerOptions.Tasks);
			
		ALL.setAll();
		HIDING
			.set(CompilerOptions.FieldHiding)
			.set(CompilerOptions.LocalVariableHiding)
			.set(CompilerOptions.TypeHiding);
		NULL
			.set(CompilerOptions.PotentialNullReference)
			.set(CompilerOptions.RedundantNullCheck);
		RESTRICTION.set(CompilerOptions.DiscouragedReference);
		STATIC_ACCESS.set(CompilerOptions.NonStaticAccessToStatic);
		UNUSED
			.set(CompilerOptions.UnusedArgument)
			.set(CompilerOptions.UnusedPrivateMember)
			.set(CompilerOptions.UnusedDeclaredThrownException)
			.set(CompilerOptions.UnusedLabel)
			.set(CompilerOptions.UnusedImport)
			.set(CompilerOptions.UnusedTypeArguments)
			.set(CompilerOptions.RedundantSuperinterface)
			.set(CompilerOptions.DeadCode)
			.set(CompilerOptions.UnusedObjectAllocation)
			.set(CompilerOptions.RedundantSpecificationOfTypeArguments);
		STATIC_METHOD
		    .set(CompilerOptions.MethodCanBePotentiallyStatic);
		String suppressRawWhenUnchecked = System.getProperty("suppressRawWhenUnchecked"); //$NON-NLS-1$
		if (suppressRawWhenUnchecked != null && "true".equalsIgnoreCase(suppressRawWhenUnchecked)) { //$NON-NLS-1$
			UNCHECKED.set(CompilerOptions.RawTypeReference);
		}
		
		JAVADOC
			.set(CompilerOptions.MissingJavadocComments)
			.set(CompilerOptions.MissingJavadocTags);
	}

	// Internal state
	private int[] bits = new int[GROUP_MAX];

	/**
	 * Constructor with initial irritant set
	 */
	public IrritantSet(int singleGroupIrritants) {
		initialize(singleGroupIrritants);
	}

	/**
	 * Constructor with initial irritant set
	 */
	public IrritantSet(IrritantSet other) {
		initialize(other);
	}

	public boolean areAllSet() {
		for (int i = 0; i < GROUP_MAX; i++) {
			if (this.bits[i] != (0xFFFFFFFF & ~GROUP_MASK))
				return false;
		}
		return true;
	}

	public IrritantSet clear(int singleGroupIrritants) {
		int group = (singleGroupIrritants & GROUP_MASK) >> GROUP_SHIFT;
		this.bits[group] &= ~singleGroupIrritants;
		return this;
	}

	public IrritantSet clearAll() {
		for (int i = 0; i < GROUP_MAX; i++) {
			this.bits[i] = 0;
		}
		return this;
	}

	/**
	 * Initialize a set of irritants in one group
	 * 
	 * @param singleGroupIrritants
	 */
	public void initialize(int singleGroupIrritants) {
		if (singleGroupIrritants == 0)
			return;
		int group = (singleGroupIrritants & GROUP_MASK) >> GROUP_SHIFT;
		this.bits[group] = singleGroupIrritants & ~GROUP_MASK; // erase group information
	}

	public void initialize(IrritantSet other) {
		if (other == null)
			return;
		System.arraycopy(other.bits, 0, this.bits = new int[GROUP_MAX], 0, GROUP_MAX);
	}

	/**
	 * Returns true if any of the irritants in given other set is positionned in receiver
	 * @param other
	 */
	public boolean isAnySet(IrritantSet other) {
		if (other == null)
			return false;
		for (int i = 0; i < GROUP_MAX; i++) {
			if ((this.bits[i] & other.bits[i]) != 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns true if all of the irritants in the given irritant set are set in receiver
	 * @param irritantSet the given irritant set
	 */
	public boolean hasSameIrritants(IrritantSet irritantSet) {
		if (irritantSet == null)
			return false;
		for (int i = 0; i < GROUP_MAX; i++) {
			if (this.bits[i] != irritantSet.bits[i])
				return false;
		}
		return true;
	}

	public boolean isSet(int singleGroupIrritants) {
		int group = (singleGroupIrritants & GROUP_MASK) >> GROUP_SHIFT;
		return (this.bits[group] & singleGroupIrritants) != 0;
	}

	public IrritantSet set(int singleGroupIrritants) {
		int group = (singleGroupIrritants & GROUP_MASK) >> GROUP_SHIFT;
		this.bits[group] |= (singleGroupIrritants & ~GROUP_MASK); // erase the group bits
		return this;
	}

	/**
	 * Return updated irritantSet or null if it was a no-op
	 * 
	 * @param other
	 */
	public IrritantSet set(IrritantSet other) {
		if (other == null)
			return this;
		boolean wasNoOp = true;
		for (int i = 0; i < GROUP_MAX; i++) {
			int otherIrritant = other.bits[i] & ~GROUP_MASK; // erase the
																	// group
																	// bits
			if ((this.bits[i] & otherIrritant) != otherIrritant) {
				wasNoOp = false;
				this.bits[i] |= otherIrritant;
			}
		}
		return wasNoOp ? null : this;
	}
	
	public IrritantSet setAll() {
		for (int i = 0; i < GROUP_MAX; i++) {
			this.bits[i] |= 0xFFFFFFFF & ~GROUP_MASK; // erase the group
															// bits;
		}
		return this;
	}
}
