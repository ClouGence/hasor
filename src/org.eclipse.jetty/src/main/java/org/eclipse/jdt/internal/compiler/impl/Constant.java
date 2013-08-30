/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;

public abstract class Constant implements TypeIds, OperatorIds {

	public static final Constant NotAConstant = DoubleConstant.fromValue(Double.NaN);
	
	public boolean booleanValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "boolean" })); //$NON-NLS-1$
	}

	public byte byteValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "byte" })); //$NON-NLS-1$
	}

	public final Constant castTo(int conversionToTargetType){
		//the cast is an int of the form
		// (castId<<4)+typeId (in order to follow the
		//user written style (cast)expression ....

		if (this == NotAConstant) return NotAConstant;
		switch(conversionToTargetType){
			case T_undefined : 						return this;
	//            TARGET TYPE  <- FROM TYPE
	//	    case (T_undefined<<4)+T_undefined  	 : return NotAConstant;
	//	    case (T_undefined<<4)+T_byte  		 : return NotAConstant;
	//	    case (T_undefined<<4)+T_long  		 : return NotAConstant;
	//	    case (T_undefined<<4)+T_short  		 : return NotAConstant;
	//	    case (T_undefined<<4)+T_void  		 : return NotAConstant;
	//	    case (T_undefined<<4)+T_String  	 : return NotAConstant;
	//	    case (T_undefined<<4)+T_Object  	 : return NotAConstant;
	//	    case (T_undefined<<4)+T_double  	 : return NotAConstant;
	//	    case (T_undefined<<4)+T_float  		 : return NotAConstant;
	//	    case (T_undefined<<4)+T_boolean 	 : return NotAConstant;
	//	    case (T_undefined<<4)+T_char  		 : return NotAConstant;
	//	    case (T_undefined<<4)+T_int  		 : return NotAConstant;

	//	    case (T_byte<<4)+T_undefined  	 : return NotAConstant;
		    case (T_byte<<4)+T_byte  		 : return this;
		    case (T_byte<<4)+T_long  		 : return ByteConstant.fromValue((byte)longValue());
		    case (T_byte<<4)+T_short  		 : return ByteConstant.fromValue((byte)shortValue());
	//	    case (T_byte<<4)+T_void  		 : return NotAConstant;
	//	    case (T_byte<<4)+T_String  	 	 : return NotAConstant;
	//	    case (T_byte<<4)+T_Object  	 	 : return NotAConstant;
		    case (T_byte<<4)+T_double  	 	 : return ByteConstant.fromValue((byte)doubleValue());
		    case (T_byte<<4)+T_float  		 : return ByteConstant.fromValue((byte)floatValue());
	//	    case (T_byte<<4)+T_boolean  	 : return NotAConstant;
		    case (T_byte<<4)+T_char  		 : return ByteConstant.fromValue((byte)charValue());
		    case (T_byte<<4)+T_int  		 : return ByteConstant.fromValue((byte)intValue());

	//	    case (T_long<<4)+T_undefined  	 : return NotAConstant;
		    case (T_long<<4)+T_byte  		 : return LongConstant.fromValue(byteValue());
		    case (T_long<<4)+T_long  		 : return this;
		    case (T_long<<4)+T_short  		 : return LongConstant.fromValue(shortValue());
	//	    case (T_long<<4)+T_void  		 : return NotAConstant;
	//	    case (T_long<<4)+T_String  		 : return NotAConstant;
	//	    case (T_long<<4)+T_Object  		 : return NotAConstant;
		    case (T_long<<4)+T_double  		 : return LongConstant.fromValue((long)doubleValue());
		    case (T_long<<4)+T_float  		 : return LongConstant.fromValue((long)floatValue());
	//	    case (T_long<<4)+T_boolean  	 : return NotAConstant;
		    case (T_long<<4)+T_char  		 : return LongConstant.fromValue(charValue());
		    case (T_long<<4)+T_int  		 : return LongConstant.fromValue(intValue());

	//	    case (T_short<<4)+T_undefined  	 : return NotAConstant;
		    case (T_short<<4)+T_byte  		 : return ShortConstant.fromValue(byteValue());
		    case (T_short<<4)+T_long  		 : return ShortConstant.fromValue((short)longValue());
		    case (T_short<<4)+T_short  		 : return this;
	//	    case (T_short<<4)+T_void  		 : return NotAConstant;
	//	    case (T_short<<4)+T_String  	 : return NotAConstant;
	//	    case (T_short<<4)+T_Object  	 : return NotAConstant;
		    case (T_short<<4)+T_double  	 : return ShortConstant.fromValue((short)doubleValue());
		    case (T_short<<4)+T_float  		 : return ShortConstant.fromValue((short)floatValue());
	//	    case (T_short<<4)+T_boolean 	 : return NotAConstant;
		    case (T_short<<4)+T_char  		 : return ShortConstant.fromValue((short)charValue());
		    case (T_short<<4)+T_int  		 : return ShortConstant.fromValue((short)intValue());

	//	    case (T_void<<4)+T_undefined  	 : return NotAConstant;
	//	    case (T_void<<4)+T_byte  		 : return NotAConstant;
	//	    case (T_void<<4)+T_long  		 : return NotAConstant;
	//	    case (T_void<<4)+T_short  		 : return NotAConstant;
	//	    case (T_void<<4)+T_void  		 : return NotAConstant;
	//	    case (T_void<<4)+T_String  	 	 : return NotAConstant;
	//	    case (T_void<<4)+T_Object  	 	 : return NotAConstant;
	//	    case (T_void<<4)+T_double  	 	 : return NotAConstant;
	//	    case (T_void<<4)+T_float  		 : return NotAConstant;
	//	    case (T_void<<4)+T_boolean  	 : return NotAConstant;
	//	    case (T_void<<4)+T_char  		 : return NotAConstant;
	//	    case (T_void<<4)+T_int  		 : return NotAConstant;

	//	    case (T_String<<4)+T_undefined   : return NotAConstant;
	//	    case (T_String<<4)+T_byte  		 : return NotAConstant;
	//	    case (T_String<<4)+T_long  		 : return NotAConstant;
	//	    case (T_String<<4)+T_short  	 : return NotAConstant;
	//	    case (T_String<<4)+T_void  		 : return NotAConstant;
		    case (T_JavaLangString<<4)+T_JavaLangString  	 : return this;
	//	    case (T_String<<4)+T_Object  	 : return NotAConstant;
	//	    case (T_String<<4)+T_double  	 : return NotAConstant;
	//	    case (T_String<<4)+T_float  	 : return NotAConstant;
	//	    case (T_String<<4)+T_boolean 	 : return NotAConstant;
	//	    case (T_String<<4)+T_char  		 : return NotAConstant;
	//	    case (T_String<<4)+T_int  		 : return NotAConstant;

	//	    case (T_Object<<4)+T_undefined   	: return NotAConstant;
	//	    case (T_Object<<4)+T_byte  		 	: return NotAConstant;
	//	    case (T_Object<<4)+T_long  		 	: return NotAConstant;
	//	    case (T_Object<<4)+T_short 		 	: return NotAConstant;
	//	    case (T_Object<<4)+T_void  		 	: return NotAConstant;
	//	    case (T_Object<<4)+T_String  		: return NotAConstant;
	//	    case (T_Object<<4)+T_Object  		: return NotAConstant;
	//	    case (T_Object<<4)+T_double  		: return NotAConstant;
	//	    case (T_Object<<4)+T_float  		: return NotAConstant;
	//	    case (T_Object<<4)+T_boolean 		: return NotAConstant;
	//	    case (T_Object<<4)+T_char  		 	: return NotAConstant;
	//	    case (T_Object<<4)+T_int  			: return NotAConstant;

	//	    case (T_double<<4)+T_undefined  	: return NotAConstant;
		    case (T_double<<4)+T_byte  		 	: return DoubleConstant.fromValue(byteValue());
		    case (T_double<<4)+T_long  		 	: return DoubleConstant.fromValue(longValue());
		    case (T_double<<4)+T_short  		: return DoubleConstant.fromValue(shortValue());
	//	    case (T_double<<4)+T_void  		 	: return NotAConstant;
	//	    case (T_double<<4)+T_String  		: return NotAConstant;
	//	    case (T_double<<4)+T_Object  		: return NotAConstant;
		    case (T_double<<4)+T_double  		: return this;
		    case (T_double<<4)+T_float  		: return DoubleConstant.fromValue(floatValue());
	//	    case (T_double<<4)+T_boolean  		: return NotAConstant;
		    case (T_double<<4)+T_char  		 	: return DoubleConstant.fromValue(charValue());
		    case (T_double<<4)+T_int  			: return DoubleConstant.fromValue(intValue());

	//	    case (T_float<<4)+T_undefined  	 : return NotAConstant;
		    case (T_float<<4)+T_byte  		 : return FloatConstant.fromValue(byteValue());
		    case (T_float<<4)+T_long  		 : return FloatConstant.fromValue(longValue());
		    case (T_float<<4)+T_short  		 : return FloatConstant.fromValue(shortValue());
	//	    case (T_float<<4)+T_void  		 : return NotAConstant;
	//	    case (T_float<<4)+T_String  	 : return NotAConstant;
	//	    case (T_float<<4)+T_Object  	 : return NotAConstant;
		    case (T_float<<4)+T_double  	 : return FloatConstant.fromValue((float)doubleValue());
		    case (T_float<<4)+T_float  		 : return this;
	//	    case (T_float<<4)+T_boolean 	 : return NotAConstant;
		    case (T_float<<4)+T_char  		 : return FloatConstant.fromValue(charValue());
		    case (T_float<<4)+T_int  		 : return FloatConstant.fromValue(intValue());

	//	    case (T_boolean<<4)+T_undefined  		 : return NotAConstant;
	//	    case (T_boolean<<4)+T_byte  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_long  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_short  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_void  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_String  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_Object  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_double  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_float  			 : return NotAConstant;
		    case (T_boolean<<4)+T_boolean  			 : return this;
	//	    case (T_boolean<<4)+T_char  			 : return NotAConstant;
	//	    case (T_boolean<<4)+T_int  				 : return NotAConstant;

	//	    case (T_char<<4)+T_undefined  	 : return NotAConstant;
		    case (T_char<<4)+T_byte  		 : return CharConstant.fromValue((char)byteValue());
		    case (T_char<<4)+T_long  		 : return CharConstant.fromValue((char)longValue());
		    case (T_char<<4)+T_short  		 : return CharConstant.fromValue((char)shortValue());
	//	    case (T_char<<4)+T_void  		 : return NotAConstant;
	//	    case (T_char<<4)+T_String  		 : return NotAConstant;
	//	    case (T_char<<4)+T_Object  		 : return NotAConstant;
		    case (T_char<<4)+T_double  		 : return CharConstant.fromValue((char)doubleValue());
		    case (T_char<<4)+T_float  		 : return CharConstant.fromValue((char)floatValue());
	//	    case (T_char<<4)+T_boolean  	 : return NotAConstant;
		    case (T_char<<4)+T_char  		 : return this;
		    case (T_char<<4)+T_int  		 : return CharConstant.fromValue((char)intValue());

	//	    case (T_int<<4)+T_undefined  	 : return NotAConstant;
		    case (T_int<<4)+T_byte  		 : return IntConstant.fromValue(byteValue());
		    case (T_int<<4)+T_long  		 : return IntConstant.fromValue((int) longValue());
		    case (T_int<<4)+T_short  		 : return IntConstant.fromValue(shortValue());
	//	    case (T_int<<4)+T_void  		 : return NotAConstant;
	//	    case (T_int<<4)+T_String  		 : return NotAConstant;
	//	    case (T_int<<4)+T_Object  		 : return NotAConstant;
		    case (T_int<<4)+T_double  		 : return IntConstant.fromValue((int) doubleValue());
		    case (T_int<<4)+T_float  		 : return IntConstant.fromValue((int) floatValue());
	//	    case (T_int<<4)+T_boolean  	 	 : return NotAConstant;
		    case (T_int<<4)+T_char  		 : return IntConstant.fromValue(charValue());
		    case (T_int<<4)+T_int  		 	 : return this;

		}
		return NotAConstant;
	}

	public char charValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "char" })); //$NON-NLS-1$
	}

	public static final Constant computeConstantOperation(Constant cst, int id, int operator) {
		switch (operator) {
			case NOT	:
							return BooleanConstant.fromValue(!cst.booleanValue());
			case PLUS	:
							return computeConstantOperationPLUS(IntConstant.fromValue(0),T_int,cst,id);
			case MINUS	:	//the two special -9223372036854775808L and -2147483648 are inlined at parseTime
							switch (id){
								case T_float  :	float f;
												if ( (f= cst.floatValue()) == 0.0f)
												{ //positive and negative 0....
													if (Float.floatToIntBits(f) == 0)
														return FloatConstant.fromValue(-0.0f);
													else
														return FloatConstant.fromValue(0.0f);}
												break; //default case
								case T_double : double d;
												if ( (d= cst.doubleValue()) == 0.0d)
												{ //positive and negative 0....
													if (Double.doubleToLongBits(d) == 0)
														return DoubleConstant.fromValue(-0.0d);
													else
														return DoubleConstant.fromValue(0.0d);}
												break; //default case
							}
							return computeConstantOperationMINUS(IntConstant.fromValue(0),T_int,cst,id);
			case TWIDDLE:
				switch (id){
					case T_char :	return IntConstant.fromValue(~ cst.charValue());
					case T_byte:	return IntConstant.fromValue(~ cst.byteValue());
					case T_short:	return IntConstant.fromValue(~ cst.shortValue());
					case T_int:		return IntConstant.fromValue(~ cst.intValue());
					case T_long:	return LongConstant.fromValue(~ cst.longValue());
					default : return NotAConstant;
				}
			default : return NotAConstant;
		}
	}

	public static final Constant computeConstantOperation(Constant left, int leftId, int operator, Constant right, int rightId) {
		switch (operator) {
			case AND		: return computeConstantOperationAND		(left,leftId,right,rightId);
			case AND_AND	: return computeConstantOperationAND_AND	(left,leftId,right,rightId);
			case DIVIDE 	: return computeConstantOperationDIVIDE		(left,leftId,right,rightId);
			case GREATER	: return computeConstantOperationGREATER	(left,leftId,right,rightId);
			case GREATER_EQUAL	: return computeConstantOperationGREATER_EQUAL(left,leftId,right,rightId);
			case LEFT_SHIFT	: return computeConstantOperationLEFT_SHIFT	(left,leftId,right,rightId);
			case LESS		: return computeConstantOperationLESS		(left,leftId,right,rightId);
			case LESS_EQUAL	: return computeConstantOperationLESS_EQUAL	(left,leftId,right,rightId);
			case MINUS		: return computeConstantOperationMINUS		(left,leftId,right,rightId);
			case MULTIPLY	: return computeConstantOperationMULTIPLY	(left,leftId,right,rightId);
			case OR			: return computeConstantOperationOR			(left,leftId,right,rightId);
			case OR_OR		: return computeConstantOperationOR_OR		(left,leftId,right,rightId);
			case PLUS		: return computeConstantOperationPLUS		(left,leftId,right,rightId);
			case REMAINDER	: return computeConstantOperationREMAINDER	(left,leftId,right,rightId);
			case RIGHT_SHIFT: return computeConstantOperationRIGHT_SHIFT(left,leftId,right,rightId);
			case UNSIGNED_RIGHT_SHIFT: return computeConstantOperationUNSIGNED_RIGHT_SHIFT(left,leftId,right,rightId);
			case XOR		: return computeConstantOperationXOR		(left,leftId,right,rightId);
			default : return NotAConstant;
		}
	}

	public static final Constant computeConstantOperationAND(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_boolean :		return BooleanConstant.fromValue(left.booleanValue() & right.booleanValue());
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() & right.charValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() & right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() & right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() & right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() & right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() & right.charValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() & right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() & right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() & right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() & right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() & right.charValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() & right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() & right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() & right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() & right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() & right.charValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() & right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() & right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() & right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() & right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() & right.charValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() & right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() & right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() & right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() & right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationAND_AND(Constant left, int leftId, Constant right, int rightId) {
		return BooleanConstant.fromValue(left.booleanValue() && right.booleanValue());
	}

	public static final Constant computeConstantOperationDIVIDE(Constant left, int leftId, Constant right, int rightId) {
		// division by zero must be handled outside this method (error reporting)
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() / right.charValue());
					case T_float:	return FloatConstant.fromValue(left.charValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.charValue() / right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() / right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() / right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() / right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() / right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return FloatConstant.fromValue(left.floatValue() / right.charValue());
					case T_float:	return FloatConstant.fromValue(left.floatValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.floatValue() / right.doubleValue());
					case T_byte:	return FloatConstant.fromValue(left.floatValue() / right.byteValue());
					case T_short:	return FloatConstant.fromValue(left.floatValue() / right.shortValue());
					case T_int:		return FloatConstant.fromValue(left.floatValue() / right.intValue());
					case T_long:	return FloatConstant.fromValue(left.floatValue() / right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return DoubleConstant.fromValue(left.doubleValue() / right.charValue());
					case T_float:	return DoubleConstant.fromValue(left.doubleValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.doubleValue() / right.doubleValue());
					case T_byte:	return DoubleConstant.fromValue(left.doubleValue() / right.byteValue());
					case T_short:	return DoubleConstant.fromValue(left.doubleValue() / right.shortValue());
					case T_int:		return DoubleConstant.fromValue(left.doubleValue() / right.intValue());
					case T_long:	return DoubleConstant.fromValue(left.doubleValue() / right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() / right.charValue());
					case T_float:	return FloatConstant.fromValue(left.byteValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.byteValue() / right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() / right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() / right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() / right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() / right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() / right.charValue());
					case T_float:	return FloatConstant.fromValue(left.shortValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.shortValue() / right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() / right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() / right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() / right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() / right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() / right.charValue());
					case T_float:	return FloatConstant.fromValue(left.intValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.intValue() / right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() / right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() / right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() / right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() / right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() / right.charValue());
					case T_float:	return FloatConstant.fromValue(left.longValue() / right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.longValue() / right.doubleValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() / right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() / right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() / right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() / right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationEQUAL_EQUAL(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_boolean :
				if (rightId == T_boolean) {
					return BooleanConstant.fromValue(left.booleanValue() == right.booleanValue());
				}
				break;
			case T_char :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.charValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.charValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.charValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.charValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.charValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.charValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.charValue() == right.longValue());}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.floatValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.floatValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.floatValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.floatValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.floatValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.floatValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.floatValue() == right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.doubleValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.doubleValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.doubleValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.doubleValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.doubleValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.doubleValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.doubleValue() == right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.byteValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.byteValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.byteValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.byteValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.byteValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.byteValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.byteValue() == right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.shortValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.shortValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.shortValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.shortValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.shortValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.shortValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.shortValue() == right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.intValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.intValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.intValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.intValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.intValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.intValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.intValue() == right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.longValue() == right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.longValue() == right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.longValue() == right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.longValue() == right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.longValue() == right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.longValue() == right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.longValue() == right.longValue());
				}
				break;
			case T_JavaLangString :
				if (rightId == T_JavaLangString) {
					//String are interned in th compiler==>thus if two string constant
					//get to be compared, it is an equal on the vale which is done
					return BooleanConstant.fromValue(((StringConstant)left).hasSameValue(right));
				}
				break;
			case T_null :
				if (rightId == T_JavaLangString) {
					return BooleanConstant.fromValue(false);
				} else {
					if (rightId == T_null) {
						return BooleanConstant.fromValue(true);
					}
				}
		}
		return BooleanConstant.fromValue(false);
	}

	public static final Constant computeConstantOperationGREATER(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.charValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.charValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.charValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.charValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.charValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.charValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.charValue() > right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.floatValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.floatValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.floatValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.floatValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.floatValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.floatValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.floatValue() > right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.doubleValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.doubleValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.doubleValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.doubleValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.doubleValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.doubleValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.doubleValue() > right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.byteValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.byteValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.byteValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.byteValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.byteValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.byteValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.byteValue() > right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.shortValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.shortValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.shortValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.shortValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.shortValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.shortValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.shortValue() > right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.intValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.intValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.intValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.intValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.intValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.intValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.intValue() > right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.longValue() > right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.longValue() > right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.longValue() > right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.longValue() > right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.longValue() > right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.longValue() > right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.longValue() > right.longValue());
				}

		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationGREATER_EQUAL(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.charValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.charValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.charValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.charValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.charValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.charValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.charValue() >= right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.floatValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.floatValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.floatValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.floatValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.floatValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.floatValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.floatValue() >= right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.doubleValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.doubleValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.doubleValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.doubleValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.doubleValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.doubleValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.doubleValue() >= right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.byteValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.byteValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.byteValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.byteValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.byteValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.byteValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.byteValue() >= right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.shortValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.shortValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.shortValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.shortValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.shortValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.shortValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.shortValue() >= right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.intValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.intValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.intValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.intValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.intValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.intValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.intValue() >= right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.longValue() >= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.longValue() >= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.longValue() >= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.longValue() >= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.longValue() >= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.longValue() >= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.longValue() >= right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationLEFT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() << right.charValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() << right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() << right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() << right.intValue());
					case T_long:	return IntConstant.fromValue(left.charValue() << right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() << right.charValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() << right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() << right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() << right.intValue());
					case T_long:	return IntConstant.fromValue(left.byteValue() << right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() << right.charValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() << right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() << right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() << right.intValue());
					case T_long:	return IntConstant.fromValue(left.shortValue() << right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() << right.charValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() << right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() << right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() << right.intValue());
					case T_long:	return IntConstant.fromValue(left.intValue() << right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() << right.charValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() << right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() << right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() << right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() << right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationLESS(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.charValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.charValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.charValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.charValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.charValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.charValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.charValue() < right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.floatValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.floatValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.floatValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.floatValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.floatValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.floatValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.floatValue() < right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.doubleValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.doubleValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.doubleValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.doubleValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.doubleValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.doubleValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.doubleValue() < right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.byteValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.byteValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.byteValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.byteValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.byteValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.byteValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.byteValue() < right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.shortValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.shortValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.shortValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.shortValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.shortValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.shortValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.shortValue() < right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.intValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.intValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.intValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.intValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.intValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.intValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.intValue() < right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.longValue() < right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.longValue() < right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.longValue() < right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.longValue() < right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.longValue() < right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.longValue() < right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.longValue() < right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationLESS_EQUAL(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.charValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.charValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.charValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.charValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.charValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.charValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.charValue() <= right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.floatValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.floatValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.floatValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.floatValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.floatValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.floatValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.floatValue() <= right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.doubleValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.doubleValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.doubleValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.doubleValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.doubleValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.doubleValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.doubleValue() <= right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.byteValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.byteValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.byteValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.byteValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.byteValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.byteValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.byteValue() <= right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.shortValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.shortValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.shortValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.shortValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.shortValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.shortValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.shortValue() <= right.longValue());
				}
			break;
			case T_int :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.intValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.intValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.intValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.intValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.intValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.intValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.intValue() <= right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return BooleanConstant.fromValue(left.longValue() <= right.charValue());
					case T_float:	return BooleanConstant.fromValue(left.longValue() <= right.floatValue());
					case T_double:	return BooleanConstant.fromValue(left.longValue() <= right.doubleValue());
					case T_byte:	return BooleanConstant.fromValue(left.longValue() <= right.byteValue());
					case T_short:	return BooleanConstant.fromValue(left.longValue() <= right.shortValue());
					case T_int:		return BooleanConstant.fromValue(left.longValue() <= right.intValue());
					case T_long:	return BooleanConstant.fromValue(left.longValue() <= right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationMINUS(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() - right.charValue());
					case T_float:	return FloatConstant.fromValue(left.charValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.charValue() - right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() - right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() - right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() - right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() - right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return FloatConstant.fromValue(left.floatValue() - right.charValue());
					case T_float:	return FloatConstant.fromValue(left.floatValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.floatValue() - right.doubleValue());
					case T_byte:	return FloatConstant.fromValue(left.floatValue() - right.byteValue());
					case T_short:	return FloatConstant.fromValue(left.floatValue() - right.shortValue());
					case T_int:		return FloatConstant.fromValue(left.floatValue() - right.intValue());
					case T_long:	return FloatConstant.fromValue(left.floatValue() - right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return DoubleConstant.fromValue(left.doubleValue() - right.charValue());
					case T_float:	return DoubleConstant.fromValue(left.doubleValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.doubleValue() - right.doubleValue());
					case T_byte:	return DoubleConstant.fromValue(left.doubleValue() - right.byteValue());
					case T_short:	return DoubleConstant.fromValue(left.doubleValue() - right.shortValue());
					case T_int:		return DoubleConstant.fromValue(left.doubleValue() - right.intValue());
					case T_long:	return DoubleConstant.fromValue(left.doubleValue() - right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() - right.charValue());
					case T_float:	return FloatConstant.fromValue(left.byteValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.byteValue() - right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() - right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() - right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() - right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() - right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() - right.charValue());
					case T_float:	return FloatConstant.fromValue(left.shortValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.shortValue() - right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() - right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() - right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() - right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() - right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() - right.charValue());
					case T_float:	return FloatConstant.fromValue(left.intValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.intValue() - right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() - right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() - right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() - right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() - right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() - right.charValue());
					case T_float:	return FloatConstant.fromValue(left.longValue() - right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.longValue() - right.doubleValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() - right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() - right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() - right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() - right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationMULTIPLY(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() * right.charValue());
					case T_float:	return FloatConstant.fromValue(left.charValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.charValue() * right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() * right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() * right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() * right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() * right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return FloatConstant.fromValue(left.floatValue() * right.charValue());
					case T_float:	return FloatConstant.fromValue(left.floatValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.floatValue() * right.doubleValue());
					case T_byte:	return FloatConstant.fromValue(left.floatValue() * right.byteValue());
					case T_short:	return FloatConstant.fromValue(left.floatValue() * right.shortValue());
					case T_int:		return FloatConstant.fromValue(left.floatValue() * right.intValue());
					case T_long:	return FloatConstant.fromValue(left.floatValue() * right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return DoubleConstant.fromValue(left.doubleValue() * right.charValue());
					case T_float:	return DoubleConstant.fromValue(left.doubleValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.doubleValue() * right.doubleValue());
					case T_byte:	return DoubleConstant.fromValue(left.doubleValue() * right.byteValue());
					case T_short:	return DoubleConstant.fromValue(left.doubleValue() * right.shortValue());
					case T_int:		return DoubleConstant.fromValue(left.doubleValue() * right.intValue());
					case T_long:	return DoubleConstant.fromValue(left.doubleValue() * right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() * right.charValue());
					case T_float:	return FloatConstant.fromValue(left.byteValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.byteValue() * right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() * right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() * right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() * right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() * right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() * right.charValue());
					case T_float:	return FloatConstant.fromValue(left.shortValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.shortValue() * right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() * right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() * right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() * right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() * right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() * right.charValue());
					case T_float:	return FloatConstant.fromValue(left.intValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.intValue() * right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() * right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() * right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() * right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() * right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() * right.charValue());
					case T_float:	return FloatConstant.fromValue(left.longValue() * right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.longValue() * right.doubleValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() * right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() * right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() * right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() * right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationOR(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_boolean :		return BooleanConstant.fromValue(left.booleanValue() | right.booleanValue());
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() | right.charValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() | right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() | right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() | right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() | right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() | right.charValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() | right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() | right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() | right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() | right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() | right.charValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() | right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() | right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() | right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() | right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() | right.charValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() | right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() | right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() | right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() | right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() | right.charValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() | right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() | right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() | right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() | right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationOR_OR(Constant left, int leftId, Constant right, int rightId) {
		return BooleanConstant.fromValue(left.booleanValue() || right.booleanValue());
	}

	public static final Constant computeConstantOperationPLUS(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_JavaLangObject :
				if (rightId == T_JavaLangString) {
					return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_boolean :
				if (rightId == T_JavaLangString) {
					return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() + right.charValue());
					case T_float:	return FloatConstant.fromValue(left.charValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.charValue() + right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() + right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() + right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() + right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return FloatConstant.fromValue(left.floatValue() + right.charValue());
					case T_float:	return FloatConstant.fromValue(left.floatValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.floatValue() + right.doubleValue());
					case T_byte:	return FloatConstant.fromValue(left.floatValue() + right.byteValue());
					case T_short:	return FloatConstant.fromValue(left.floatValue() + right.shortValue());
					case T_int:		return FloatConstant.fromValue(left.floatValue() + right.intValue());
					case T_long:	return FloatConstant.fromValue(left.floatValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return DoubleConstant.fromValue(left.doubleValue() + right.charValue());
					case T_float:	return DoubleConstant.fromValue(left.doubleValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.doubleValue() + right.doubleValue());
					case T_byte:	return DoubleConstant.fromValue(left.doubleValue() + right.byteValue());
					case T_short:	return DoubleConstant.fromValue(left.doubleValue() + right.shortValue());
					case T_int:		return DoubleConstant.fromValue(left.doubleValue() + right.intValue());
					case T_long:	return DoubleConstant.fromValue(left.doubleValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() + right.charValue());
					case T_float:	return FloatConstant.fromValue(left.byteValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.byteValue() + right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() + right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() + right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() + right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() + right.charValue());
					case T_float:	return FloatConstant.fromValue(left.shortValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.shortValue() + right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() + right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() + right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() + right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() + right.charValue());
					case T_float:	return FloatConstant.fromValue(left.intValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.intValue() + right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() + right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() + right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() + right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() + right.charValue());
					case T_float:	return FloatConstant.fromValue(left.longValue() + right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.longValue() + right.doubleValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() + right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() + right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() + right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() + right.longValue());
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
				}
				break;
			case T_JavaLangString :
				switch (rightId){
					case T_char :	return StringConstant.fromValue(left.stringValue() + String.valueOf(right.charValue()));
					case T_float:	return StringConstant.fromValue(left.stringValue() + String.valueOf(right.floatValue()));
					case T_double:	return StringConstant.fromValue(left.stringValue() + String.valueOf(right.doubleValue()));
					case T_byte:	return StringConstant.fromValue(left.stringValue() + String.valueOf(right.byteValue()));
					case T_short:	return StringConstant.fromValue(left.stringValue() + String.valueOf(right.shortValue()));
					case T_int:		return StringConstant.fromValue(left.stringValue() + String.valueOf(right.intValue()));
					case T_long:	return StringConstant.fromValue(left.stringValue() + String.valueOf(right.longValue()));
					case T_JavaLangString:	return StringConstant.fromValue(left.stringValue() + right.stringValue());
					case T_boolean:	return StringConstant.fromValue(left.stringValue() + right.booleanValue());
				}
				break;
//			case T_null :
//				switch (rightId){
//					case T_char :	return Constant.fromValue(left.stringValue() + String.valueOf(right.charValue()));
//					case T_float:	return Constant.fromValue(left.stringValue() + String.valueOf(right.floatValue()));
//					case T_double:	return Constant.fromValue(left.stringValue() + String.valueOf(right.doubleValue()));
//					case T_byte:	return Constant.fromValue(left.stringValue() + String.valueOf(right.byteValue()));
//					case T_short:	return Constant.fromValue(left.stringValue() + String.valueOf(right.shortValue()));
//					case T_int:		return Constant.fromValue(left.stringValue() + String.valueOf(right.intValue()));
//					case T_long:	return Constant.fromValue(left.stringValue() + String.valueOf(right.longValue()));
//					case T_JavaLangString:	return Constant.fromValue(left.stringValue() + right.stringValue());
//					case T_boolean:	return Constant.fromValue(left.stringValue() + right.booleanValue());
//				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationREMAINDER(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() % right.charValue());
					case T_float:	return FloatConstant.fromValue(left.charValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.charValue() % right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() % right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() % right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() % right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() % right.longValue());
				}
				break;
			case T_float :
				switch (rightId){
					case T_char :	return FloatConstant.fromValue(left.floatValue() % right.charValue());
					case T_float:	return FloatConstant.fromValue(left.floatValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.floatValue() % right.doubleValue());
					case T_byte:	return FloatConstant.fromValue(left.floatValue() % right.byteValue());
					case T_short:	return FloatConstant.fromValue(left.floatValue() % right.shortValue());
					case T_int:		return FloatConstant.fromValue(left.floatValue() % right.intValue());
					case T_long:	return FloatConstant.fromValue(left.floatValue() % right.longValue());
				}
				break;
			case T_double :
				switch (rightId){
					case T_char :	return DoubleConstant.fromValue(left.doubleValue() % right.charValue());
					case T_float:	return DoubleConstant.fromValue(left.doubleValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.doubleValue() % right.doubleValue());
					case T_byte:	return DoubleConstant.fromValue(left.doubleValue() % right.byteValue());
					case T_short:	return DoubleConstant.fromValue(left.doubleValue() % right.shortValue());
					case T_int:		return DoubleConstant.fromValue(left.doubleValue() % right.intValue());
					case T_long:	return DoubleConstant.fromValue(left.doubleValue() % right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() % right.charValue());
					case T_float:	return FloatConstant.fromValue(left.byteValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.byteValue() % right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() % right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() % right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() % right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() % right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() % right.charValue());
					case T_float:	return FloatConstant.fromValue(left.shortValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.shortValue() % right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() % right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() % right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() % right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() % right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() % right.charValue());
					case T_float:	return FloatConstant.fromValue(left.intValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.intValue() % right.doubleValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() % right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() % right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() % right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() % right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() % right.charValue());
					case T_float:	return FloatConstant.fromValue(left.longValue() % right.floatValue());
					case T_double:	return DoubleConstant.fromValue(left.longValue() % right.doubleValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() % right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() % right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() % right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() % right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationRIGHT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() >> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() >> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() >> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() >> right.intValue());
					case T_long:	return IntConstant.fromValue(left.charValue() >> right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() >> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() >> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() >> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() >> right.intValue());
					case T_long:	return IntConstant.fromValue(left.byteValue() >> right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() >> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() >> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() >> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() >> right.intValue());
					case T_long:	return IntConstant.fromValue(left.shortValue() >> right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() >> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() >> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() >> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() >> right.intValue());
					case T_long:	return IntConstant.fromValue(left.intValue() >> right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() >> right.charValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() >> right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() >> right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() >> right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() >> right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationUNSIGNED_RIGHT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() >>> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() >>> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() >>> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() >>> right.intValue());
					case T_long:	return IntConstant.fromValue(left.charValue() >>> right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() >>> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() >>> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() >>> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() >>> right.intValue());
					case T_long:	return IntConstant.fromValue(left.byteValue() >>> right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() >>> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() >>> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() >>> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() >>> right.intValue());
					case T_long:	return IntConstant.fromValue(left.shortValue() >>> right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() >>> right.charValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() >>> right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() >>> right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() >>> right.intValue());
					case T_long:	return IntConstant.fromValue(left.intValue() >>> right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() >>> right.charValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() >>> right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() >>> right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() >>> right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() >>> right.longValue());
				}
		}
		return NotAConstant;
	}

	public static final Constant computeConstantOperationXOR(Constant left, int leftId, Constant right, int rightId) {
		switch (leftId){
			case T_boolean :		return BooleanConstant.fromValue(left.booleanValue() ^ right.booleanValue());
			case T_char :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.charValue() ^ right.charValue());
					case T_byte:	return IntConstant.fromValue(left.charValue() ^ right.byteValue());
					case T_short:	return IntConstant.fromValue(left.charValue() ^ right.shortValue());
					case T_int:		return IntConstant.fromValue(left.charValue() ^ right.intValue());
					case T_long:	return LongConstant.fromValue(left.charValue() ^ right.longValue());
				}
				break;
			case T_byte :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.byteValue() ^ right.charValue());
					case T_byte:	return IntConstant.fromValue(left.byteValue() ^ right.byteValue());
					case T_short:	return IntConstant.fromValue(left.byteValue() ^ right.shortValue());
					case T_int:		return IntConstant.fromValue(left.byteValue() ^ right.intValue());
					case T_long:	return LongConstant.fromValue(left.byteValue() ^ right.longValue());
				}
				break;
			case T_short :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.shortValue() ^ right.charValue());
					case T_byte:	return IntConstant.fromValue(left.shortValue() ^ right.byteValue());
					case T_short:	return IntConstant.fromValue(left.shortValue() ^ right.shortValue());
					case T_int:		return IntConstant.fromValue(left.shortValue() ^ right.intValue());
					case T_long:	return LongConstant.fromValue(left.shortValue() ^ right.longValue());
				}
				break;
			case T_int :
				switch (rightId){
					case T_char :	return IntConstant.fromValue(left.intValue() ^ right.charValue());
					case T_byte:	return IntConstant.fromValue(left.intValue() ^ right.byteValue());
					case T_short:	return IntConstant.fromValue(left.intValue() ^ right.shortValue());
					case T_int:		return IntConstant.fromValue(left.intValue() ^ right.intValue());
					case T_long:	return LongConstant.fromValue(left.intValue() ^ right.longValue());
				}
				break;
			case T_long :
				switch (rightId){
					case T_char :	return LongConstant.fromValue(left.longValue() ^ right.charValue());
					case T_byte:	return LongConstant.fromValue(left.longValue() ^ right.byteValue());
					case T_short:	return LongConstant.fromValue(left.longValue() ^ right.shortValue());
					case T_int:		return LongConstant.fromValue(left.longValue() ^ right.intValue());
					case T_long:	return LongConstant.fromValue(left.longValue() ^ right.longValue());
				}
		}
		return NotAConstant;
	}

	public double doubleValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "double" })); //$NON-NLS-1$
	}

	public float floatValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "float" })); //$NON-NLS-1$
	}
	
	/**
	 * Returns true if both constants have the same type and the same actual value
	 * @param otherConstant
	 */
	public boolean hasSameValue(Constant otherConstant) {
		if (this == otherConstant)
			return true;
		int typeID;
		if ((typeID = typeID()) != otherConstant.typeID())
			return false;
		switch (typeID) {
			case TypeIds.T_boolean:
				return booleanValue() == otherConstant.booleanValue();
			case TypeIds.T_byte:
				return byteValue() == otherConstant.byteValue();
			case TypeIds.T_char:
				return charValue() == otherConstant.charValue();
			case TypeIds.T_double:
				return doubleValue() == otherConstant.doubleValue();
			case TypeIds.T_float:
				return floatValue() == otherConstant.floatValue();
			case TypeIds.T_int:
				return intValue() == otherConstant.intValue();
			case TypeIds.T_short:
				return shortValue() == otherConstant.shortValue();
			case TypeIds.T_long:
				return longValue() == otherConstant.longValue();
			case TypeIds.T_JavaLangString:
				String value = stringValue();
				return value == null
					? otherConstant.stringValue() == null
					: value.equals(otherConstant.stringValue());
		}
		return false;
	}

	public int intValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "int" })); //$NON-NLS-1$
	}

	public long longValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { typeName(), "long" })); //$NON-NLS-1$
	}

	public short shortValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[] { typeName(), "short" })); //$NON-NLS-1$
	}

	public String stringValue() {
		throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[] { typeName(), "String" })); //$NON-NLS-1$
	}

	public String toString(){
		if (this == NotAConstant) return "(Constant) NotAConstant"; //$NON-NLS-1$
		return super.toString(); }

	public abstract int typeID();

	public String typeName() {
		switch (typeID()) {
			case T_int : return "int"; //$NON-NLS-1$
			case T_byte : return "byte"; //$NON-NLS-1$
			case T_short : return "short"; //$NON-NLS-1$
			case T_char : return "char"; //$NON-NLS-1$
			case T_float : return "float"; //$NON-NLS-1$
			case T_double : return "double"; //$NON-NLS-1$
			case T_boolean : return "boolean"; //$NON-NLS-1$
			case T_long : return "long";//$NON-NLS-1$
			case T_JavaLangString : return "java.lang.String"; //$NON-NLS-1$
			default: return "unknown"; //$NON-NLS-1$
		}
	}
}
