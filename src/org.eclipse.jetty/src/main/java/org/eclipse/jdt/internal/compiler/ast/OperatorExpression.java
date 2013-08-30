/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Perry James - nullStatus method improvement (165346)
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.util.Util;

public abstract class OperatorExpression extends Expression implements OperatorIds {

	public static int[][] OperatorSignatures = new int[NumberOfTables][];

	static {classInitialize();}

	/**
	 * OperatorExpression constructor comment.
	 */
	public OperatorExpression() {
		super();
	}
	public static final void classInitialize() {
		OperatorSignatures[AND] = get_AND();
		OperatorSignatures[AND_AND] = get_AND_AND();
		OperatorSignatures[DIVIDE] = get_DIVIDE();
		OperatorSignatures[EQUAL_EQUAL] = get_EQUAL_EQUAL();
		OperatorSignatures[GREATER] = get_GREATER();
		OperatorSignatures[GREATER_EQUAL] = get_GREATER_EQUAL();
		OperatorSignatures[LEFT_SHIFT] = get_LEFT_SHIFT();
		OperatorSignatures[LESS] = get_LESS();
		OperatorSignatures[LESS_EQUAL] = get_LESS_EQUAL();
		OperatorSignatures[MINUS] = get_MINUS();
		OperatorSignatures[MULTIPLY] = get_MULTIPLY();
		OperatorSignatures[OR] = get_OR();
		OperatorSignatures[OR_OR] = get_OR_OR();
		OperatorSignatures[PLUS] = get_PLUS();
		OperatorSignatures[REMAINDER] = get_REMAINDER();
		OperatorSignatures[RIGHT_SHIFT] = get_RIGHT_SHIFT();
		OperatorSignatures[UNSIGNED_RIGHT_SHIFT] = get_UNSIGNED_RIGHT_SHIFT();
		OperatorSignatures[XOR] = get_XOR();
	}

	public static final String generateTableTestCase(){
		//return a String which is a java method allowing to test
		//the non zero entries of all tables

		/*
		org.eclipse.jdt.internal.compiler.ast.
		OperatorExpression.generateTableTestCase();
		*/

		int[] operators = new int[]{AND,AND_AND,DIVIDE,GREATER,GREATER_EQUAL,
				LEFT_SHIFT,LESS,LESS_EQUAL,MINUS,MULTIPLY,OR,OR_OR,PLUS,REMAINDER,
				RIGHT_SHIFT,UNSIGNED_RIGHT_SHIFT,XOR};

		class Decode {
			public  final String constant(int code){
				switch(code){
					case T_boolean 	: return "true"; //$NON-NLS-1$
					case T_byte		: return "((byte) 3)"; //$NON-NLS-1$
					case T_char		: return "'A'"; //$NON-NLS-1$
					case T_double	: return "300.0d"; //$NON-NLS-1$
					case T_float	: return "100.0f"; //$NON-NLS-1$
					case T_int		: return "1"; //$NON-NLS-1$
					case T_long		: return "7L"; //$NON-NLS-1$
					case T_JavaLangString	: return "\"hello-world\""; //$NON-NLS-1$
					case T_null		: return "null"; //$NON-NLS-1$
					case T_short	: return "((short) 5)"; //$NON-NLS-1$
					case T_JavaLangObject	: return "null";} //$NON-NLS-1$
				return Util.EMPTY_STRING;}

			public  final String type(int code){
				switch(code){
					case T_boolean 	: return "z"; //$NON-NLS-1$
					case T_byte		: return "b"; //$NON-NLS-1$
					case T_char		: return "c"; //$NON-NLS-1$
					case T_double	: return "d"; //$NON-NLS-1$
					case T_float	: return "f"; //$NON-NLS-1$
					case T_int		: return "i"; //$NON-NLS-1$
					case T_long		: return "l"; //$NON-NLS-1$
					case T_JavaLangString	: return "str"; //$NON-NLS-1$
					case T_null		: return "null"; //$NON-NLS-1$
					case T_short	: return "s"; //$NON-NLS-1$
					case T_JavaLangObject	: return "obj";} //$NON-NLS-1$
				return "xxx";} //$NON-NLS-1$

			public  final String operator(int operator){
					switch (operator) {
					case EQUAL_EQUAL :	return "=="; //$NON-NLS-1$
					case LESS_EQUAL :	return "<="; //$NON-NLS-1$
					case GREATER_EQUAL :return ">="; //$NON-NLS-1$
					case LEFT_SHIFT :	return "<<"; //$NON-NLS-1$
					case RIGHT_SHIFT :	return ">>"; //$NON-NLS-1$
					case UNSIGNED_RIGHT_SHIFT :	return ">>>"; //$NON-NLS-1$
					case OR_OR :return "||"; //$NON-NLS-1$
					case AND_AND :		return "&&"; //$NON-NLS-1$
					case PLUS :			return "+"; //$NON-NLS-1$
					case MINUS :		return "-"; //$NON-NLS-1$
					case NOT :			return "!"; //$NON-NLS-1$
					case REMAINDER :	return "%"; //$NON-NLS-1$
					case XOR :			return "^"; //$NON-NLS-1$
					case AND :			return "&"; //$NON-NLS-1$
					case MULTIPLY :		return "*"; //$NON-NLS-1$
					case OR :			return "|"; //$NON-NLS-1$
					case TWIDDLE :		return "~"; //$NON-NLS-1$
					case DIVIDE :		return "/"; //$NON-NLS-1$
					case GREATER :		return ">"; //$NON-NLS-1$
					case LESS :			return "<";	} //$NON-NLS-1$
				return "????";} //$NON-NLS-1$
		}


		Decode decode = new Decode();
		String s;

		s = "\tpublic static void binaryOperationTablesTestCase(){\n" + //$NON-NLS-1$

			"\t\t//TC test : all binary operation (described in tables)\n"+ //$NON-NLS-1$
			"\t\t//method automatically generated by\n"+ //$NON-NLS-1$
			"\t\t//org.eclipse.jdt.internal.compiler.ast.OperatorExpression.generateTableTestCase();\n"+ //$NON-NLS-1$

			"\t\tString str0;\t String str\t= "+decode.constant(T_JavaLangString)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tint i0;\t int i\t= "+decode.constant(T_int)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tboolean z0;\t boolean z\t= "+decode.constant(T_boolean)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tchar c0; \t char  c\t= "+decode.constant(T_char)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tfloat f0; \t float f\t= "+decode.constant(T_float)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tdouble d0;\t double d\t= "+decode.constant(T_double)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tbyte b0; \t byte b\t= "+decode.constant(T_byte)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tshort s0; \t short s\t= "+decode.constant(T_short)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tlong l0; \t long l\t= "+decode.constant(T_long)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\t\tObject obj0; \t Object obj\t= "+decode.constant(T_JavaLangObject)+";\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			"\n"; //$NON-NLS-1$

		int error = 0;
		for (int i=0; i < operators.length; i++)
		{	int operator = operators[i];
			for (int left=0; left<16;left++)
			for (int right=0; right<16;right++)
			{	int result = (OperatorSignatures[operator][(left<<4)+right]) & 0x0000F;
				if (result != T_undefined)

					//1/ First regular computation then 2/ comparaison
					//with a compile time constant (generated by the compiler)
					//	z0 = s >= s;
					//	if ( z0 != (((short) 5) >= ((short) 5)))
					//		System.out.println(155);

				{	s += "\t\t"+decode.type(result)+"0"+" = "+decode.type(left); //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-2$
					s += " "+decode.operator(operator)+" "+decode.type(right)+";\n"; //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-3$
					String begin = result == T_JavaLangString ? "\t\tif (! " : "\t\tif ( "; //$NON-NLS-2$ //$NON-NLS-1$
					String test = result == T_JavaLangString ? ".equals(" : " != ("; //$NON-NLS-2$ //$NON-NLS-1$
					s += begin	+decode.type(result)+"0"+test //$NON-NLS-1$
								+decode.constant(left)+" " //$NON-NLS-1$
								+decode.operator(operator)+" " //$NON-NLS-1$
								+decode.constant(right)+"))\n"; //$NON-NLS-1$
					s += "\t\t\tSystem.out.println("+ (++error) +");\n"; //$NON-NLS-1$ //$NON-NLS-2$

					}
				}
			}

		return s += "\n\t\tSystem.out.println(\"binary tables test : done\");}"; //$NON-NLS-1$
	}

	public static final int[] get_AND(){

		//the code is an int, only 20 bits are used, see below.
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table  = new int[16*16];

		//	table[(T_undefined<<4)+T_undefined] 	= T_undefined;
		//	table[(T_undefined<<4)+T_byte] 			= T_undefined;
		//	table[(T_undefined<<4)+T_long] 			= T_undefined;
		//	table[(T_undefined<<4)+T_short] 		= T_undefined;
		//	table[(T_undefined<<4)+T_void] 			= T_undefined;
		//	table[(T_undefined<<4)+T_String] 		= T_undefined;
		//	table[(T_undefined<<4)+T_Object] 		= T_undefined;
		//	table[(T_undefined<<4)+T_double] 		= T_undefined;
		//	table[(T_undefined<<4)+T_float] 		= T_undefined;
		//	table[(T_undefined<<4)+T_boolean] 		= T_undefined;
		//	table[(T_undefined<<4)+T_char] 			= T_undefined;
		//	table[(T_undefined<<4)+T_int] 			= T_undefined;
		//	table[(T_undefined<<4)+T_null] 			= T_undefined;

		//	table[(T_byte<<4)+T_undefined] 	= T_undefined;
		table[(T_byte<<4)+T_byte] 		= (Byte2Int<<12) +(Byte2Int<<4) +T_int;
		table[(T_byte<<4)+T_long]		= (Byte2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_byte<<4)+T_short] 		= (Byte2Int<<12) +(Short2Int<<4)+T_int;
		//	table[(T_byte<<4)+T_void] 		= T_undefined;
		//	table[(T_byte<<4)+T_String] 	= T_undefined;
		//	table[(T_byte<<4)+T_Object] 	= T_undefined;
		//	table[(T_byte<<4)+T_double] 	= T_undefined;
		//	table[(T_byte<<4)+T_float] 		= T_undefined;
		//	table[(T_byte<<4)+T_boolean] 	= T_undefined;
		table[(T_byte<<4)+T_char] 		= (Byte2Int<<12) +(Char2Int<<4) +T_int;
		table[(T_byte<<4)+T_int] 		= (Byte2Int<<12) +(Int2Int<<4)  +T_int;
		//	table[(T_byte<<4)+T_null] 		= T_undefined;

		//	table[(T_long<<4)+T_undefined] 	= T_undefined;
		table[(T_long<<4)+T_byte] 		= (Long2Long<<12)+(Byte2Long<<4)+T_long;
		table[(T_long<<4)+T_long] 		= (Long2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_long<<4)+T_short] 		= (Long2Long<<12)+(Short2Long<<4)+T_long;
		//	table[(T_long<<4)+T_void] 		= T_undefined;
		//	table[(T_long<<4)+T_String] 	= T_undefined;
		//	table[(T_long<<4)+T_Object] 	= T_undefined;
		//	table[(T_long<<4)+T_double] 	= T_undefined;
		//	table[(T_long<<4)+T_float] 		= T_undefined;
		//	table[(T_long<<4)+T_boolean] 	= T_undefined;
		table[(T_long<<4)+T_char] 		= (Long2Long<<12)+(Char2Long<<4)+T_long;
		table[(T_long<<4)+T_int] 		= (Long2Long<<12)+(Int2Long<<4)+T_long;
		//	table[(T_long<<4)+T_null] 		= T_undefined;

		//	table[(T_short<<4)+T_undefined] 	= T_undefined;
		table[(T_short<<4)+T_byte] 			= (Short2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_short<<4)+T_long] 			= (Short2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_short<<4)+T_short] 		= (Short2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_short<<4)+T_void] 			= T_undefined;
		//	table[(T_short<<4)+T_String] 		= T_undefined;
		//	table[(T_short<<4)+T_Object] 		= T_undefined;
		//	table[(T_short<<4)+T_double] 		= T_undefined;
		//	table[(T_short<<4)+T_float] 		= T_undefined;
		//	table[(T_short<<4)+T_boolean] 		= T_undefined;
		table[(T_short<<4)+T_char] 			= (Short2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_short<<4)+T_int] 			= (Short2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_short<<4)+T_null] 			= T_undefined;

		//	table[(T_void<<4)+T_undefined] 	= T_undefined;
		//	table[(T_void<<4)+T_byte] 		= T_undefined;
		//	table[(T_void<<4)+T_long] 		= T_undefined;
		//	table[(T_void<<4)+T_short] 		= T_undefined;
		//	table[(T_void<<4)+T_void] 		= T_undefined;
		//	table[(T_void<<4)+T_String] 	= T_undefined;
		//	table[(T_void<<4)+T_Object] 	= T_undefined;
		//	table[(T_void<<4)+T_double] 	= T_undefined;
		//	table[(T_void<<4)+T_float] 		= T_undefined;
		//	table[(T_void<<4)+T_boolean] 	= T_undefined;
		//	table[(T_void<<4)+T_char] 		= T_undefined;
		//	table[(T_void<<4)+T_int] 		= T_undefined;
		//	table[(T_void<<4)+T_null] 		= T_undefined;

		//	table[(T_String<<4)+T_undefined] 	= T_undefined;
		//	table[(T_String<<4)+T_byte] 		= T_undefined;
		//	table[(T_String<<4)+T_long] 		= T_undefined;
		//	table[(T_String<<4)+T_short] 		= T_undefined;
		//	table[(T_String<<4)+T_void] 		= T_undefined;
		//	table[(T_String<<4)+T_String] 		= T_undefined;
		//	table[(T_String<<4)+T_Object] 		= T_undefined;
		//	table[(T_String<<4)+T_double] 		= T_undefined;
		//	table[(T_String<<4)+T_float] 		= T_undefined;
		//	table[(T_String<<4)+T_boolean] 		= T_undefined;
		//	table[(T_String<<4)+T_char] 		= T_undefined;
		//	table[(T_String<<4)+T_int] 			= T_undefined;
		//	table[(T_String<<4)+T_null] 		= T_undefined;

		//	table[(T_Object<<4)+T_undefined] 	= T_undefined;
		//	table[(T_Object<<4)+T_byte] 		= T_undefined;
		//	table[(T_Object<<4)+T_long] 		= T_undefined;
		//	table[(T_Object<<4)+T_short]		= T_undefined;
		//	table[(T_Object<<4)+T_void] 		= T_undefined;
		//	table[(T_Object<<4)+T_String] 		= T_undefined;
		//	table[(T_Object<<4)+T_Object] 		= T_undefined;
		//	table[(T_Object<<4)+T_double] 		= T_undefined;
		//	table[(T_Object<<4)+T_float] 		= T_undefined;
		//	table[(T_Object<<4)+T_boolean]		= T_undefined;
		//	table[(T_Object<<4)+T_char] 		= T_undefined;
		//	table[(T_Object<<4)+T_int] 			= T_undefined;
		//	table[(T_Object<<4)+T_null] 		= T_undefined;

		//	table[(T_double<<4)+T_undefined] 	= T_undefined;
		//	table[(T_double<<4)+T_byte] 		= T_undefined;
		//	table[(T_double<<4)+T_long] 		= T_undefined;
		//	table[(T_double<<4)+T_short] 		= T_undefined;
		//	table[(T_double<<4)+T_void] 		= T_undefined;
		//	table[(T_double<<4)+T_String] 		= T_undefined;
		//	table[(T_double<<4)+T_Object] 		= T_undefined;
		//	table[(T_double<<4)+T_double] 		= T_undefined;
		//	table[(T_double<<4)+T_float] 		= T_undefined;
		//	table[(T_double<<4)+T_boolean] 		= T_undefined;
		//	table[(T_double<<4)+T_char] 		= T_undefined;
		//	table[(T_double<<4)+T_int] 			= T_undefined;
		//	table[(T_double<<4)+T_null] 		= T_undefined;

		//	table[(T_float<<4)+T_undefined] 	= T_undefined;
		//	table[(T_float<<4)+T_byte] 			= T_undefined;
		//	table[(T_float<<4)+T_long] 			= T_undefined;
		//	table[(T_float<<4)+T_short] 		= T_undefined;
		//	table[(T_float<<4)+T_void] 			= T_undefined;
		//	table[(T_float<<4)+T_String] 		= T_undefined;
		//	table[(T_float<<4)+T_Object] 		= T_undefined;
		//	table[(T_float<<4)+T_double] 		= T_undefined;
		//	table[(T_float<<4)+T_float] 		= T_undefined;
		//	table[(T_float<<4)+T_boolean] 		= T_undefined;
		//	table[(T_float<<4)+T_char] 			= T_undefined;
		//	table[(T_float<<4)+T_int] 			= T_undefined;
		//	table[(T_float<<4)+T_null] 			= T_undefined;

		//	table[(T_boolean<<4)+T_undefined] 		= T_undefined;
		//	table[(T_boolean<<4)+T_byte] 			= T_undefined;
		//	table[(T_boolean<<4)+T_long] 			= T_undefined;
		//	table[(T_boolean<<4)+T_short] 			= T_undefined;
		//	table[(T_boolean<<4)+T_void] 			= T_undefined;
		//	table[(T_boolean<<4)+T_String] 			= T_undefined;
		//	table[(T_boolean<<4)+T_Object] 			= T_undefined;
		//	table[(T_boolean<<4)+T_double] 			= T_undefined;
		//	table[(T_boolean<<4)+T_float] 			= T_undefined;
		table[(T_boolean<<4)+T_boolean] 		= (Boolean2Boolean << 12)+(Boolean2Boolean << 4)+T_boolean;
		//	table[(T_boolean<<4)+T_char] 			= T_undefined;
		//	table[(T_boolean<<4)+T_int] 			= T_undefined;
		//	table[(T_boolean<<4)+T_null] 			= T_undefined;

		//	table[(T_char<<4)+T_undefined] 		= T_undefined;
		table[(T_char<<4)+T_byte] 			= (Char2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_char<<4)+T_long] 			= (Char2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_char<<4)+T_short] 			= (Char2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_char<<4)+T_void] 			= T_undefined;
		//	table[(T_char<<4)+T_String] 		= T_undefined;
		//	table[(T_char<<4)+T_Object] 		= T_undefined;
		//	table[(T_char<<4)+T_double] 		= T_undefined;
		//	table[(T_char<<4)+T_float] 			= T_undefined;
		//	table[(T_char<<4)+T_boolean] 		= T_undefined;
		table[(T_char<<4)+T_char] 			= (Char2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_char<<4)+T_int] 			= (Char2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_char<<4)+T_null] 			= T_undefined;

		//	table[(T_int<<4)+T_undefined] 	= T_undefined;
		table[(T_int<<4)+T_byte] 		= (Int2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_int<<4)+T_long] 		= (Int2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_int<<4)+T_short] 		= (Int2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_int<<4)+T_void] 		= T_undefined;
		//	table[(T_int<<4)+T_String] 		= T_undefined;
		//	table[(T_int<<4)+T_Object] 		= T_undefined;
		//	table[(T_int<<4)+T_double] 		= T_undefined;
		//	table[(T_int<<4)+T_float] 		= T_undefined;
		//	table[(T_int<<4)+T_boolean] 	= T_undefined;
		table[(T_int<<4)+T_char] 		= (Int2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_int<<4)+T_int] 		= (Int2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_int<<4)+T_null] 		= T_undefined;

		//	table[(T_null<<4)+T_undefined] 		= T_undefined;
		//	table[(T_null<<4)+T_byte] 			= T_undefined;
		//	table[(T_null<<4)+T_long] 			= T_undefined;
		//	table[(T_null<<4)+T_short] 			= T_undefined;
		//	table[(T_null<<4)+T_void] 			= T_undefined;
		//	table[(T_null<<4)+T_String] 		= T_undefined;
		//	table[(T_null<<4)+T_Object] 		= T_undefined;
		//	table[(T_null<<4)+T_double] 		= T_undefined;
		//	table[(T_null<<4)+T_float] 			= T_undefined;
		//	table[(T_null<<4)+T_boolean] 		= T_undefined;
		//	table[(T_null<<4)+T_char] 			= T_undefined;
		//	table[(T_null<<4)+T_int] 			= T_undefined;
		//	table[(T_null<<4)+T_null] 			= T_undefined;

		return table;
	}

	public static final int[] get_AND_AND(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table  = new int[16*16];

		//     table[(T_undefined<<4)+T_undefined] 		= T_undefined;
		//     table[(T_undefined<<4)+T_byte] 			= T_undefined;
		//     table[(T_undefined<<4)+T_long] 			= T_undefined;
		//     table[(T_undefined<<4)+T_short] 			= T_undefined;
		//     table[(T_undefined<<4)+T_void] 			= T_undefined;
		//     table[(T_undefined<<4)+T_String] 		= T_undefined;
		//     table[(T_undefined<<4)+T_Object] 		= T_undefined;
		//     table[(T_undefined<<4)+T_double] 		= T_undefined;
		//     table[(T_undefined<<4)+T_float] 			= T_undefined;
		//     table[(T_undefined<<4)+T_boolean] 		= T_undefined;
		//     table[(T_undefined<<4)+T_char] 			= T_undefined;
		//     table[(T_undefined<<4)+T_int] 			= T_undefined;
		//     table[(T_undefined<<4)+T_null] 			= T_undefined;

		//     table[(T_byte<<4)+T_undefined] 	= T_undefined;
		//     table[(T_byte<<4)+T_byte] 		= T_undefined;
		//     table[(T_byte<<4)+T_long] 		= T_undefined;
		//     table[(T_byte<<4)+T_short] 		= T_undefined;
		//     table[(T_byte<<4)+T_void] 		= T_undefined;
		//     table[(T_byte<<4)+T_String] 		= T_undefined;
		//     table[(T_byte<<4)+T_Object] 		= T_undefined;
		//     table[(T_byte<<4)+T_double] 		= T_undefined;
		//     table[(T_byte<<4)+T_float] 		= T_undefined;
		//     table[(T_byte<<4)+T_boolean] 	= T_undefined;
		//     table[(T_byte<<4)+T_char] 		= T_undefined;
		//     table[(T_byte<<4)+T_int] 		= T_undefined;
		//     table[(T_byte<<4)+T_null] 		= T_undefined;

		//     table[(T_long<<4)+T_undefined] 	= T_undefined;
		//     table[(T_long<<4)+T_byte] 		= T_undefined;
		//     table[(T_long<<4)+T_long] 		= T_undefined;
		//     table[(T_long<<4)+T_short] 		= T_undefined;
		//     table[(T_long<<4)+T_void] 		= T_undefined;
		//     table[(T_long<<4)+T_String] 		= T_undefined;
		//     table[(T_long<<4)+T_Object] 		= T_undefined;
		//     table[(T_long<<4)+T_double] 		= T_undefined;
		//     table[(T_long<<4)+T_float] 		= T_undefined;
		//     table[(T_long<<4)+T_boolean] 	= T_undefined;
		//     table[(T_long<<4)+T_char] 		= T_undefined;
		//     table[(T_long<<4)+T_int] 		= T_undefined;
		//     table[(T_long<<4)+T_null] 		= T_undefined;

		//     table[(T_short<<4)+T_undefined] 	= T_undefined;
		//     table[(T_short<<4)+T_byte] 		= T_undefined;
		//     table[(T_short<<4)+T_long] 		= T_undefined;
		//     table[(T_short<<4)+T_short] 		= T_undefined;
		//     table[(T_short<<4)+T_void] 		= T_undefined;
		//     table[(T_short<<4)+T_String] 	= T_undefined;
		//     table[(T_short<<4)+T_Object] 	= T_undefined;
		//     table[(T_short<<4)+T_double] 	= T_undefined;
		//     table[(T_short<<4)+T_float] 		= T_undefined;
		//     table[(T_short<<4)+T_boolean]	= T_undefined;
		//     table[(T_short<<4)+T_char] 		= T_undefined;
		//     table[(T_short<<4)+T_int] 		= T_undefined;
		//     table[(T_short<<4)+T_null] 		= T_undefined;

		//     table[(T_void<<4)+T_undefined] 	= T_undefined;
		//     table[(T_void<<4)+T_byte] 		= T_undefined;
		//     table[(T_void<<4)+T_long] 		= T_undefined;
		//     table[(T_void<<4)+T_short] 		= T_undefined;
		//     table[(T_void<<4)+T_void] 		= T_undefined;
		//     table[(T_void<<4)+T_String] 	= T_undefined;
		//     table[(T_void<<4)+T_Object] 	= T_undefined;
		//     table[(T_void<<4)+T_double] 	= T_undefined;
		//     table[(T_void<<4)+T_float] 		= T_undefined;
		//     table[(T_void<<4)+T_boolean] 	= T_undefined;
		//     table[(T_void<<4)+T_char] 		= T_undefined;
		//     table[(T_void<<4)+T_int] 		= T_undefined;
		//     table[(T_void<<4)+T_null] 		= T_undefined;

		//     table[(T_String<<4)+T_undefined] 	= T_undefined;
		//     table[(T_String<<4)+T_byte] 		= T_undefined;
		//     table[(T_String<<4)+T_long] 		= T_undefined;
		//     table[(T_String<<4)+T_short] 		= T_undefined;
		//     table[(T_String<<4)+T_void] 		= T_undefined;
		//     table[(T_String<<4)+T_String] 		= T_undefined;
		//     table[(T_String<<4)+T_Object] 		= T_undefined;
		//     table[(T_String<<4)+T_double] 		= T_undefined;
		//     table[(T_String<<4)+T_float] 		= T_undefined;
		//     table[(T_String<<4)+T_boolean] 		= T_undefined;
		//     table[(T_String<<4)+T_char] 		= T_undefined;
		//     table[(T_String<<4)+T_int] 			= T_undefined;
		//     table[(T_String<<4)+T_null] 		= T_undefined;

		//     table[(T_Object<<4)+T_undefined] 	= T_undefined;
		//     table[(T_Object<<4)+T_byte] 		= T_undefined;
		//     table[(T_Object<<4)+T_long] 		= T_undefined;
		//     table[(T_Object<<4)+T_short]		= T_undefined;
		//     table[(T_Object<<4)+T_void] 		= T_undefined;
		//     table[(T_Object<<4)+T_String] 		= T_undefined;
		//     table[(T_Object<<4)+T_Object] 		= T_undefined;
		//     table[(T_Object<<4)+T_double] 		= T_undefined;
		//     table[(T_Object<<4)+T_float] 		= T_undefined;
		//     table[(T_Object<<4)+T_boolean]		= T_undefined;
		//     table[(T_Object<<4)+T_char] 		= T_undefined;
		//     table[(T_Object<<4)+T_int] 			= T_undefined;
		//     table[(T_Object<<4)+T_null] 		= T_undefined;

		//     table[(T_double<<4)+T_undefined] 	= T_undefined;
		//     table[(T_double<<4)+T_byte] 		= T_undefined;
		//     table[(T_double<<4)+T_long] 		= T_undefined;
		//     table[(T_double<<4)+T_short] 		= T_undefined;
		//     table[(T_double<<4)+T_void] 		= T_undefined;
		//     table[(T_double<<4)+T_String] 		= T_undefined;
		//     table[(T_double<<4)+T_Object] 		= T_undefined;
		//     table[(T_double<<4)+T_double] 		= T_undefined;
		//     table[(T_double<<4)+T_float] 		= T_undefined;
		//     table[(T_double<<4)+T_boolean] 		= T_undefined;
		//     table[(T_double<<4)+T_char] 		= T_undefined;
		//     table[(T_double<<4)+T_int] 			= T_undefined;
		//     table[(T_double<<4)+T_null] 		= T_undefined;

		//     table[(T_float<<4)+T_undefined] 	= T_undefined;
		//     table[(T_float<<4)+T_byte] 			= T_undefined;
		//     table[(T_float<<4)+T_long] 			= T_undefined;
		//     table[(T_float<<4)+T_short] 		= T_undefined;
		//     table[(T_float<<4)+T_void] 			= T_undefined;
		//     table[(T_float<<4)+T_String] 		= T_undefined;
		//     table[(T_float<<4)+T_Object] 		= T_undefined;
		//     table[(T_float<<4)+T_double] 		= T_undefined;
		//     table[(T_float<<4)+T_float] 		= T_undefined;
		//     table[(T_float<<4)+T_boolean] 		= T_undefined;
		//     table[(T_float<<4)+T_char] 			= T_undefined;
		//     table[(T_float<<4)+T_int] 			= T_undefined;
		//     table[(T_float<<4)+T_null] 			= T_undefined;

		//     table[(T_boolean<<4)+T_undefined] 		= T_undefined;
		//     table[(T_boolean<<4)+T_byte] 			= T_undefined;
		//     table[(T_boolean<<4)+T_long] 			= T_undefined;
		//     table[(T_boolean<<4)+T_short] 			= T_undefined;
		//     table[(T_boolean<<4)+T_void] 			= T_undefined;
		//     table[(T_boolean<<4)+T_String] 			= T_undefined;
		//     table[(T_boolean<<4)+T_Object] 			= T_undefined;
		//     table[(T_boolean<<4)+T_double] 			= T_undefined;
		//     table[(T_boolean<<4)+T_float] 			= T_undefined;
	   table[(T_boolean<<4)+T_boolean] 		= (Boolean2Boolean<<12)+(Boolean2Boolean<<4)+T_boolean;
		//     table[(T_boolean<<4)+T_char] 			= T_undefined;
		//     table[(T_boolean<<4)+T_int] 			= T_undefined;
		//     table[(T_boolean<<4)+T_null] 			= T_undefined;

		//     table[(T_char<<4)+T_undefined] 		= T_undefined;
		//     table[(T_char<<4)+T_byte] 			= T_undefined;
		//     table[(T_char<<4)+T_long] 			= T_undefined;
		//     table[(T_char<<4)+T_short] 			= T_undefined;
		//     table[(T_char<<4)+T_void] 			= T_undefined;
		//     table[(T_char<<4)+T_String] 		= T_undefined;
		//     table[(T_char<<4)+T_Object] 		= T_undefined;
		//     table[(T_char<<4)+T_double] 		= T_undefined;
		//     table[(T_char<<4)+T_float] 			= T_undefined;
		//     table[(T_char<<4)+T_boolean] 		= T_undefined;
		//     table[(T_char<<4)+T_char] 			= T_undefined;
		//     table[(T_char<<4)+T_int] 			= T_undefined;
		//     table[(T_char<<4)+T_null] 			= T_undefined;

		//     table[(T_int<<4)+T_undefined] 	= T_undefined;
		//     table[(T_int<<4)+T_byte] 		= T_undefined;
		//     table[(T_int<<4)+T_long] 		= T_undefined;
		//     table[(T_int<<4)+T_short] 		= T_undefined;
		//     table[(T_int<<4)+T_void] 		= T_undefined;
		//     table[(T_int<<4)+T_String] 		= T_undefined;
		//     table[(T_int<<4)+T_Object] 		= T_undefined;
		//     table[(T_int<<4)+T_double] 		= T_undefined;
		//     table[(T_int<<4)+T_float] 		= T_undefined;
		//     table[(T_int<<4)+T_boolean] 	= T_undefined;
		//     table[(T_int<<4)+T_char] 		= T_undefined;
		//     table[(T_int<<4)+T_int] 		= T_undefined;
		//     table[(T_int<<4)+T_null] 		= T_undefined;

		//     table[(T_null<<4)+T_undefined] 		= T_undefined;
		//     table[(T_null<<4)+T_byte] 			= T_undefined;
		//     table[(T_null<<4)+T_long] 			= T_undefined;
		//     table[(T_null<<4)+T_short] 			= T_undefined;
		//     table[(T_null<<4)+T_void] 			= T_undefined;
		//     table[(T_null<<4)+T_String] 		= T_undefined;
		//     table[(T_null<<4)+T_Object] 		= T_undefined;
		//     table[(T_null<<4)+T_double] 		= T_undefined;
		//     table[(T_null<<4)+T_float] 			= T_undefined;
		//     table[(T_null<<4)+T_boolean] 		= T_undefined;
		//     table[(T_null<<4)+T_char] 			= T_undefined;
		//     table[(T_null<<4)+T_int] 			= T_undefined;
		//     table[(T_null<<4)+T_null] 			= T_undefined;
		return table;
	}

	public static final int[] get_DIVIDE(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4


	//	int[] table  = new int[16*16];

		return get_MINUS();
	}

	public static final int[] get_EQUAL_EQUAL(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table  = new int[16*16];

		//	table[(T_undefined<<4)+T_undefined] 	= T_undefined;
		//	table[(T_undefined<<4)+T_byte] 			= T_undefined;
		//	table[(T_undefined<<4)+T_long] 			= T_undefined;
		//	table[(T_undefined<<4)+T_short] 		= T_undefined;
		//	table[(T_undefined<<4)+T_void] 			= T_undefined;
		//	table[(T_undefined<<4)+T_String] 		= T_undefined;
		//	table[(T_undefined<<4)+T_Object] 		= T_undefined;
		//	table[(T_undefined<<4)+T_double] 		= T_undefined;
		//	table[(T_undefined<<4)+T_float] 		= T_undefined;
		//	table[(T_undefined<<4)+T_boolean] 		= T_undefined;
		//	table[(T_undefined<<4)+T_char] 			= T_undefined;
		//	table[(T_undefined<<4)+T_int] 			= T_undefined;
		//	table[(T_undefined<<4)+T_null] 			= T_undefined;

		//	table[(T_byte<<4)+T_undefined] 	= T_undefined;
		table[(T_byte<<4)+T_byte] 		= (Byte2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_byte<<4)+T_long] 		= (Byte2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_byte<<4)+T_short] 		= (Byte2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_byte<<4)+T_void] 		= T_undefined;
		//	table[(T_byte<<4)+T_String] 	= T_undefined;
		//	table[(T_byte<<4)+T_Object] 	= T_undefined;
		table[(T_byte<<4)+T_double] 	= (Byte2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_byte<<4)+T_float] 		= (Byte2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_byte<<4)+T_boolean] 	= T_undefined;
		table[(T_byte<<4)+T_char] 		= (Byte2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_byte<<4)+T_int] 		= (Byte2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_byte<<4)+T_null] 		= T_undefined;

		//	table[(T_long<<4)+T_undefined] 	= T_undefined;
		table[(T_long<<4)+T_byte] 		= (Long2Long<<12)+(Byte2Long<<4)+T_boolean;
		table[(T_long<<4)+T_long] 		= (Long2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_long<<4)+T_short] 		= (Long2Long<<12)+(Short2Long<<4)+T_boolean;
		//	table[(T_long<<4)+T_void] 		= T_undefined;
		//	table[(T_long<<4)+T_String] 	= T_undefined;
		//	table[(T_long<<4)+T_Object] 	= T_undefined;
		table[(T_long<<4)+T_double] 	= (Long2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_long<<4)+T_float] 		= (Long2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_long<<4)+T_boolean] 	= T_undefined;
		table[(T_long<<4)+T_char] 		= (Long2Long<<12)+(Char2Long<<4)+T_boolean;
		table[(T_long<<4)+T_int] 		= (Long2Long<<12)+(Int2Long<<4)+T_boolean;
		//	table[(T_long<<4)+T_null] 		= T_undefined;

		//	table[(T_short<<4)+T_undefined] 	= T_undefined;
		table[(T_short<<4)+T_byte] 			= (Short2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_short<<4)+T_long] 			= (Short2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_short<<4)+T_short] 		= (Short2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_short<<4)+T_void] 			= T_undefined;
		//	table[(T_short<<4)+T_String] 		= T_undefined;
		//	table[(T_short<<4)+T_Object] 		= T_undefined;
		table[(T_short<<4)+T_double] 		= (Short2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_short<<4)+T_float] 		= (Short2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_short<<4)+T_boolean] 		= T_undefined;
		table[(T_short<<4)+T_char] 			= (Short2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_short<<4)+T_int] 			= (Short2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_short<<4)+T_null] 			= T_undefined;

		//	table[(T_void<<4)+T_undefined] 	= T_undefined;
		//	table[(T_void<<4)+T_byte] 		= T_undefined;
		//	table[(T_void<<4)+T_long] 		= T_undefined;
		//	table[(T_void<<4)+T_short] 		= T_undefined;
		//	table[(T_void<<4)+T_void] 		= T_undefined;
		//	table[(T_void<<4)+T_String] 	= T_undefined;
		//	table[(T_void<<4)+T_Object] 	= T_undefined;
		//	table[(T_void<<4)+T_double] 	= T_undefined;
		//	table[(T_void<<4)+T_float] 		= T_undefined;
		//	table[(T_void<<4)+T_boolean] 	= T_undefined;
		//	table[(T_void<<4)+T_char] 		= T_undefined;
		//	table[(T_void<<4)+T_int] 		= T_undefined;
		//	table[(T_void<<4)+T_null] 		= T_undefined;

		//	table[(T_String<<4)+T_undefined] 	= T_undefined;
		//	table[(T_String<<4)+T_byte] 		= T_undefined;
		//	table[(T_String<<4)+T_long] 		= T_undefined;
		//	table[(T_String<<4)+T_short] 		= T_undefined;
		//	table[(T_String<<4)+T_void] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_JavaLangString] 		= /*String2Object                 String2Object*/
											  (T_JavaLangObject<<16)+(T_JavaLangString<<12)+(T_JavaLangObject<<8)+(T_JavaLangString<<4)+T_boolean;
		table[(T_JavaLangString<<4)+T_JavaLangObject] 		= /*String2Object                 Object2Object*/
											  (T_JavaLangObject<<16)+(T_JavaLangString<<12)+(T_JavaLangObject<<8)+(T_JavaLangObject<<4)+T_boolean;
		//	table[(T_String<<4)+T_double] 		= T_undefined;
		//	table[(T_String<<4)+T_float] 		= T_undefined;
		//	table[(T_String<<4)+T_boolean] 		= T_undefined;
		//	table[(T_String<<4)+T_char] 		= T_undefined;
		//	table[(T_String<<4)+T_int] 			= T_undefined;
		table[(T_JavaLangString<<4)+T_null] 		= /*Object2String                null2Object */
											  (T_JavaLangObject<<16)+(T_JavaLangString<<12)+(T_JavaLangObject<<8)+(T_null<<4)+T_boolean;

		//	table[(T_Object<<4)+T_undefined] 	= T_undefined;
		//	table[(T_Object<<4)+T_byte] 		= T_undefined;
		//	table[(T_Object<<4)+T_long] 		= T_undefined;
		//	table[(T_Object<<4)+T_short]		= T_undefined;
		//	table[(T_Object<<4)+T_void] 		= T_undefined;
		table[(T_JavaLangObject<<4)+T_JavaLangString] 		= /*Object2Object                 String2Object*/
											  (T_JavaLangObject<<16)+(T_JavaLangObject<<12)+(T_JavaLangObject<<8)+(T_JavaLangString<<4)+T_boolean;
		table[(T_JavaLangObject<<4)+T_JavaLangObject] 		= /*Object2Object                 Object2Object*/
											  (T_JavaLangObject<<16)+(T_JavaLangObject<<12)+(T_JavaLangObject<<8)+(T_JavaLangObject<<4)+T_boolean;
		//	table[(T_Object<<4)+T_double] 		= T_undefined;
		//	table[(T_Object<<4)+T_float] 		= T_undefined;
		//	table[(T_Object<<4)+T_boolean]		= T_undefined;
		//	table[(T_Object<<4)+T_char] 		= T_undefined;
		//	table[(T_Object<<4)+T_int] 			= T_undefined;
		table[(T_JavaLangObject<<4)+T_null] 		= /*Object2Object                 null2Object*/
											  (T_JavaLangObject<<16)+(T_JavaLangObject<<12)+(T_JavaLangObject<<8)+(T_null<<4)+T_boolean;

		//	table[(T_double<<4)+T_undefined] 	= T_undefined;
		table[(T_double<<4)+T_byte] 		= (Double2Double<<12)+(Byte2Double<<4)+T_boolean;
		table[(T_double<<4)+T_long] 		= (Double2Double<<12)+(Long2Double<<4)+T_boolean;
		table[(T_double<<4)+T_short] 		= (Double2Double<<12)+(Short2Double<<4)+T_boolean;
		//	table[(T_double<<4)+T_void] 		= T_undefined;
		//	table[(T_double<<4)+T_String] 		= T_undefined;
		//	table[(T_double<<4)+T_Object] 		= T_undefined;
		table[(T_double<<4)+T_double] 		= (Double2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_double<<4)+T_float] 		= (Double2Double<<12)+(Float2Double<<4)+T_boolean;
		//	table[(T_double<<4)+T_boolean] 		= T_undefined;
		table[(T_double<<4)+T_char] 		= (Double2Double<<12)+(Char2Double<<4)+T_boolean;
		table[(T_double<<4)+T_int] 			= (Double2Double<<12)+(Int2Double<<4)+T_boolean;
		//	table[(T_double<<4)+T_null] 		= T_undefined;

		//	table[(T_float<<4)+T_undefined] 	= T_undefined;
		table[(T_float<<4)+T_byte] 			= (Float2Float<<12)+(Byte2Float<<4)+T_boolean;
		table[(T_float<<4)+T_long] 			= (Float2Float<<12)+(Long2Float<<4)+T_boolean;
		table[(T_float<<4)+T_short] 		= (Float2Float<<12)+(Short2Float<<4)+T_boolean;
		//	table[(T_float<<4)+T_void] 			= T_undefined;
		//	table[(T_float<<4)+T_String] 		= T_undefined;
		//	table[(T_float<<4)+T_Object] 		= T_undefined;
		table[(T_float<<4)+T_double] 		= (Float2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_float<<4)+T_float] 		= (Float2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_float<<4)+T_boolean] 		= T_undefined;
		table[(T_float<<4)+T_char] 			= (Float2Float<<12)+(Char2Float<<4)+T_boolean;
		table[(T_float<<4)+T_int] 			= (Float2Float<<12)+(Int2Float<<4)+T_boolean;
		//	table[(T_float<<4)+T_null] 			= T_undefined;

		//	table[(T_boolean<<4)+T_undefined] 		= T_undefined;
		//	table[(T_boolean<<4)+T_byte] 			= T_undefined;
		//	table[(T_boolean<<4)+T_long] 			= T_undefined;
		//	table[(T_boolean<<4)+T_short] 			= T_undefined;
		//	table[(T_boolean<<4)+T_void] 			= T_undefined;
		//	table[(T_boolean<<4)+T_String] 			= T_undefined;
		//	table[(T_boolean<<4)+T_Object] 			= T_undefined;
		//	table[(T_boolean<<4)+T_double] 			= T_undefined;
		//	table[(T_boolean<<4)+T_float] 			= T_undefined;
		table[(T_boolean<<4)+T_boolean] 		= (Boolean2Boolean<<12)+(Boolean2Boolean<<4)+T_boolean;
		//	table[(T_boolean<<4)+T_char] 			= T_undefined;
		//	table[(T_boolean<<4)+T_int] 			= T_undefined;
		//	table[(T_boolean<<4)+T_null] 			= T_undefined;

		//	table[(T_char<<4)+T_undefined] 		= T_undefined;
		table[(T_char<<4)+T_byte] 			= (Char2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_char<<4)+T_long] 			= (Char2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_char<<4)+T_short] 			= (Char2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_char<<4)+T_void] 			= T_undefined;
		//	table[(T_char<<4)+T_String] 		= T_undefined;
		//	table[(T_char<<4)+T_Object] 		= T_undefined;
		table[(T_char<<4)+T_double] 		= (Char2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_char<<4)+T_float] 			= (Char2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_char<<4)+T_boolean] 		= T_undefined;
		table[(T_char<<4)+T_char] 			= (Char2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_char<<4)+T_int] 			= (Char2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_char<<4)+T_null] 			= T_undefined;

		//	table[(T_int<<4)+T_undefined] 	= T_undefined;
		table[(T_int<<4)+T_byte] 		= (Int2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_int<<4)+T_long] 		= (Int2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_int<<4)+T_short] 		= (Int2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_int<<4)+T_void] 		= T_undefined;
		//	table[(T_int<<4)+T_String] 		= T_undefined;
		//	table[(T_int<<4)+T_Object] 		= T_undefined;
		table[(T_int<<4)+T_double] 		= (Int2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_int<<4)+T_float] 		= (Int2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_int<<4)+T_boolean] 	= T_undefined;
		table[(T_int<<4)+T_char] 		= (Int2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_int<<4)+T_int] 		= (Int2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_int<<4)+T_null] 		= T_undefined;

		//	table[(T_null<<4)+T_undefined] 		= T_undefined;
		//	table[(T_null<<4)+T_byte] 			= T_undefined;
		//	table[(T_null<<4)+T_long] 			= T_undefined;
		//	table[(T_null<<4)+T_short] 			= T_undefined;
		//	table[(T_null<<4)+T_void] 			= T_undefined;
		table[(T_null<<4)+T_JavaLangString] 		= /*null2Object                 String2Object*/
											  (T_JavaLangObject<<16)+(T_null<<12)+(T_JavaLangObject<<8)+(T_JavaLangString<<4)+T_boolean;
		table[(T_null<<4)+T_JavaLangObject] 		= /*null2Object                 Object2Object*/
											  (T_JavaLangObject<<16)+(T_null<<12)+(T_JavaLangObject<<8)+(T_JavaLangObject<<4)+T_boolean;
		//	table[(T_null<<4)+T_double] 		= T_undefined;
		//	table[(T_null<<4)+T_float] 			= T_undefined;
		//	table[(T_null<<4)+T_boolean] 		= T_undefined;
		//	table[(T_null<<4)+T_char] 			= T_undefined;
		//	table[(T_null<<4)+T_int] 			= T_undefined;
		table[(T_null<<4)+T_null] 			= /*null2Object                 null2Object*/
											  (T_JavaLangObject<<16)+(T_null<<12)+(T_JavaLangObject<<8)+(T_null<<4)+T_boolean;
		return table;
	}

	public static final int[] get_GREATER(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_LESS();
	}

	public static final int[] get_GREATER_EQUAL(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_LESS();
	}

	public static final int[] get_LEFT_SHIFT(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table  = new int[16*16];

		//	table[(T_undefined<<4)+T_undefined] 	= T_undefined;
		//	table[(T_undefined<<4)+T_byte] 			= T_undefined;
		//	table[(T_undefined<<4)+T_long] 			= T_undefined;
		//	table[(T_undefined<<4)+T_short] 		= T_undefined;
		//	table[(T_undefined<<4)+T_void] 			= T_undefined;
		//	table[(T_undefined<<4)+T_String] 		= T_undefined;
		//	table[(T_undefined<<4)+T_Object] 		= T_undefined;
		//	table[(T_undefined<<4)+T_double] 		= T_undefined;
		//	table[(T_undefined<<4)+T_float] 		= T_undefined;
		//	table[(T_undefined<<4)+T_boolean] 		= T_undefined;
		//	table[(T_undefined<<4)+T_char] 			= T_undefined;
		//	table[(T_undefined<<4)+T_int] 			= T_undefined;
		//	table[(T_undefined<<4)+T_null] 			= T_undefined;

		//	table[(T_byte<<4)+T_undefined] 	= T_undefined;
		table[(T_byte<<4)+T_byte] 		= (Byte2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_byte<<4)+T_long] 		= (Byte2Int<<12)+(Long2Int<<4)+T_int;
		table[(T_byte<<4)+T_short] 		= (Byte2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_byte<<4)+T_void] 		= T_undefined;
		//	table[(T_byte<<4)+T_String] 	= T_undefined;
		//	table[(T_byte<<4)+T_Object] 	= T_undefined;
		//	table[(T_byte<<4)+T_double] 	= T_undefined;
		//	table[(T_byte<<4)+T_float] 		= T_undefined;
		//	table[(T_byte<<4)+T_boolean] 	= T_undefined;
		table[(T_byte<<4)+T_char] 		= (Byte2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_byte<<4)+T_int] 		= (Byte2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_byte<<4)+T_null] 		= T_undefined;

		//	table[(T_long<<4)+T_undefined] 	= T_undefined;
		table[(T_long<<4)+T_byte] 		= (Long2Long<<12)+(Byte2Int<<4)+T_long;
		table[(T_long<<4)+T_long] 		= (Long2Long<<12)+(Long2Int<<4)+T_long;
		table[(T_long<<4)+T_short] 		= (Long2Long<<12)+(Short2Int<<4)+T_long;
		//	table[(T_long<<4)+T_void] 		= T_undefined;
		//	table[(T_long<<4)+T_String] 	= T_undefined;
		//	table[(T_long<<4)+T_Object] 	= T_undefined;
		//	table[(T_long<<4)+T_double] 	= T_undefined;
		//	table[(T_long<<4)+T_float] 		= T_undefined;
		//	table[(T_long<<4)+T_boolean] 	= T_undefined;
		table[(T_long<<4)+T_char] 		= (Long2Long<<12)+(Char2Int<<4)+T_long;
		table[(T_long<<4)+T_int] 		= (Long2Long<<12)+(Int2Int<<4)+T_long;
		//	table[(T_long<<4)+T_null] 		= T_undefined;

		//	table[(T_short<<4)+T_undefined] 	= T_undefined;
		table[(T_short<<4)+T_byte] 			= (Short2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_short<<4)+T_long] 			= (Short2Int<<12)+(Long2Int<<4)+T_int;
		table[(T_short<<4)+T_short] 		= (Short2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_short<<4)+T_void] 			= T_undefined;
		//	table[(T_short<<4)+T_String] 		= T_undefined;
		//	table[(T_short<<4)+T_Object] 		= T_undefined;
		//	table[(T_short<<4)+T_double] 		= T_undefined;
		//	table[(T_short<<4)+T_float] 		= T_undefined;
		//	table[(T_short<<4)+T_boolean] 		= T_undefined;
		table[(T_short<<4)+T_char] 			= (Short2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_short<<4)+T_int] 			= (Short2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_short<<4)+T_null] 			= T_undefined;

		//	table[(T_void<<4)+T_undefined] 	= T_undefined;
		//	table[(T_void<<4)+T_byte] 		= T_undefined;
		//	table[(T_void<<4)+T_long] 		= T_undefined;
		//	table[(T_void<<4)+T_short] 		= T_undefined;
		//	table[(T_void<<4)+T_void] 		= T_undefined;
		//	table[(T_void<<4)+T_String] 	= T_undefined;
		//	table[(T_void<<4)+T_Object] 	= T_undefined;
		//	table[(T_void<<4)+T_double] 	= T_undefined;
		//	table[(T_void<<4)+T_float] 		= T_undefined;
		//	table[(T_void<<4)+T_boolean] 	= T_undefined;
		//	table[(T_void<<4)+T_char] 		= T_undefined;
		//	table[(T_void<<4)+T_int] 		= T_undefined;
		//	table[(T_void<<4)+T_null] 		= T_undefined;

		//	table[(T_String<<4)+T_undefined] 	= T_undefined;
		//	table[(T_String<<4)+T_byte] 		= T_undefined;
		//	table[(T_String<<4)+T_long] 		= T_undefined;
		//	table[(T_String<<4)+T_short] 		= T_undefined;
		//	table[(T_String<<4)+T_void] 		= T_undefined;
		//	table[(T_String<<4)+T_String] 		= T_undefined;
		//	table[(T_String<<4)+T_Object] 		= T_undefined;
		//	table[(T_String<<4)+T_double] 		= T_undefined;
		//	table[(T_String<<4)+T_float] 		= T_undefined;
		//	table[(T_String<<4)+T_boolean] 		= T_undefined;
		//	table[(T_String<<4)+T_char] 		= T_undefined;
		//	table[(T_String<<4)+T_int] 			= T_undefined;
		//	table[(T_String<<4)+T_null] 		= T_undefined;

		//	table[(T_Object<<4)+T_undefined] 	= T_undefined;
		//	table[(T_Object<<4)+T_byte] 		= T_undefined;
		//	table[(T_Object<<4)+T_long] 		= T_undefined;
		//	table[(T_Object<<4)+T_short]		= T_undefined;
		//	table[(T_Object<<4)+T_void] 		= T_undefined;
		//	table[(T_Object<<4)+T_String] 		= T_undefined;
		//	table[(T_Object<<4)+T_Object] 		= T_undefined;
		//	table[(T_Object<<4)+T_double] 		= T_undefined;
		//	table[(T_Object<<4)+T_float] 		= T_undefined;
		//	table[(T_Object<<4)+T_boolean]		= T_undefined;
		//	table[(T_Object<<4)+T_char] 		= T_undefined;
		//	table[(T_Object<<4)+T_int] 			= T_undefined;
		//	table[(T_Object<<4)+T_null] 		= T_undefined;

		//	table[(T_double<<4)+T_undefined] 	= T_undefined;
		//	table[(T_double<<4)+T_byte] 		= T_undefined;
		//	table[(T_double<<4)+T_long] 		= T_undefined;
		//	table[(T_double<<4)+T_short] 		= T_undefined;
		//	table[(T_double<<4)+T_void] 		= T_undefined;
		//	table[(T_double<<4)+T_String] 		= T_undefined;
		//	table[(T_double<<4)+T_Object] 		= T_undefined;
		//	table[(T_double<<4)+T_double] 		= T_undefined;
		//	table[(T_double<<4)+T_float] 		= T_undefined;
		//	table[(T_double<<4)+T_boolean] 		= T_undefined;
		//	table[(T_double<<4)+T_char] 		= T_undefined;
		//	table[(T_double<<4)+T_int] 			= T_undefined;
		//	table[(T_double<<4)+T_null] 		= T_undefined;

		//	table[(T_float<<4)+T_undefined] 	= T_undefined;
		//	table[(T_float<<4)+T_byte] 			= T_undefined;
		//	table[(T_float<<4)+T_long] 			= T_undefined;
		//	table[(T_float<<4)+T_short] 		= T_undefined;
		//	table[(T_float<<4)+T_void] 			= T_undefined;
		//	table[(T_float<<4)+T_String] 		= T_undefined;
		//	table[(T_float<<4)+T_Object] 		= T_undefined;
		//	table[(T_float<<4)+T_double] 		= T_undefined;
		//	table[(T_float<<4)+T_float] 		= T_undefined;
		//	table[(T_float<<4)+T_boolean] 		= T_undefined;
		//	table[(T_float<<4)+T_char] 			= T_undefined;
		//	table[(T_float<<4)+T_int] 			= T_undefined;
		//	table[(T_float<<4)+T_null] 			= T_undefined;

		//	table[(T_boolean<<4)+T_undefined] 		= T_undefined;
		//	table[(T_boolean<<4)+T_byte] 			= T_undefined;
		//	table[(T_boolean<<4)+T_long] 			= T_undefined;
		//	table[(T_boolean<<4)+T_short] 			= T_undefined;
		//	table[(T_boolean<<4)+T_void] 			= T_undefined;
		//	table[(T_boolean<<4)+T_String] 			= T_undefined;
		//	table[(T_boolean<<4)+T_Object] 			= T_undefined;
		//	table[(T_boolean<<4)+T_double] 			= T_undefined;
		//	table[(T_boolean<<4)+T_float] 			= T_undefined;
		//	table[(T_boolean<<4)+T_boolean] 		= T_undefined;
		//	table[(T_boolean<<4)+T_char] 			= T_undefined;
		//	table[(T_boolean<<4)+T_int] 			= T_undefined;
		//	table[(T_boolean<<4)+T_null] 			= T_undefined;

		//	table[(T_char<<4)+T_undefined] 		= T_undefined;
		table[(T_char<<4)+T_byte] 			= (Char2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_char<<4)+T_long] 			= (Char2Int<<12)+(Long2Int<<4)+T_int;
		table[(T_char<<4)+T_short] 			= (Char2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_char<<4)+T_void] 			= T_undefined;
		//	table[(T_char<<4)+T_String] 		= T_undefined;
		//	table[(T_char<<4)+T_Object] 		= T_undefined;
		//	table[(T_char<<4)+T_double] 		= T_undefined;
		//	table[(T_char<<4)+T_float] 			= T_undefined;
		//	table[(T_char<<4)+T_boolean] 		= T_undefined;
		table[(T_char<<4)+T_char] 			= (Char2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_char<<4)+T_int] 			= (Char2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_char<<4)+T_null] 			= T_undefined;

		//	table[(T_int<<4)+T_undefined] 	= T_undefined;
		table[(T_int<<4)+T_byte] 		= (Int2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_int<<4)+T_long] 		= (Int2Int<<12)+(Long2Int<<4)+T_int;
		table[(T_int<<4)+T_short] 		= (Int2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_int<<4)+T_void] 		= T_undefined;
		//	table[(T_int<<4)+T_String] 		= T_undefined;
		//	table[(T_int<<4)+T_Object] 		= T_undefined;
		//	table[(T_int<<4)+T_double] 		= T_undefined;
		//	table[(T_int<<4)+T_float] 		= T_undefined;
		//	table[(T_int<<4)+T_boolean] 	= T_undefined;
		table[(T_int<<4)+T_char] 		= (Int2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_int<<4)+T_int] 		= (Int2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_int<<4)+T_null] 		= T_undefined;

		//	table[(T_null<<4)+T_undefined] 		= T_undefined;
		//	table[(T_null<<4)+T_byte] 			= T_undefined;
		//	table[(T_null<<4)+T_long] 			= T_undefined;
		//	table[(T_null<<4)+T_short] 			= T_undefined;
		//	table[(T_null<<4)+T_void] 			= T_undefined;
		//	table[(T_null<<4)+T_String] 		= T_undefined;
		//	table[(T_null<<4)+T_Object] 		= T_undefined;
		//	table[(T_null<<4)+T_double] 		= T_undefined;
		//	table[(T_null<<4)+T_float] 			= T_undefined;
		//	table[(T_null<<4)+T_boolean] 		= T_undefined;
		//	table[(T_null<<4)+T_char] 			= T_undefined;
		//	table[(T_null<<4)+T_int] 			= T_undefined;
		//	table[(T_null<<4)+T_null] 			= T_undefined;

		return table;
	}

	public static final int[] get_LESS(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table  = new int[16*16];

		//	table[(T_undefined<<4)+T_undefined] 	= T_undefined;
		//	table[(T_undefined<<4)+T_byte] 			= T_undefined;
		//	table[(T_undefined<<4)+T_long] 			= T_undefined;
		//	table[(T_undefined<<4)+T_short] 		= T_undefined;
		//	table[(T_undefined<<4)+T_void] 			= T_undefined;
		//	table[(T_undefined<<4)+T_String] 		= T_undefined;
		//	table[(T_undefined<<4)+T_Object] 		= T_undefined;
		//	table[(T_undefined<<4)+T_double] 		= T_undefined;
		//	table[(T_undefined<<4)+T_float] 		= T_undefined;
		//	table[(T_undefined<<4)+T_boolean] 		= T_undefined;
		//	table[(T_undefined<<4)+T_char] 			= T_undefined;
		//	table[(T_undefined<<4)+T_int] 			= T_undefined;
		//	table[(T_undefined<<4)+T_null] 			= T_undefined;

		//	table[(T_byte<<4)+T_undefined] 	= T_undefined;
		table[(T_byte<<4)+T_byte] 		= (Byte2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_byte<<4)+T_long] 		= (Byte2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_byte<<4)+T_short] 		= (Byte2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_byte<<4)+T_void] 		= T_undefined;
		//	table[(T_byte<<4)+T_String] 	= T_undefined;
		//	table[(T_byte<<4)+T_Object] 	= T_undefined;
		table[(T_byte<<4)+T_double] 	= (Byte2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_byte<<4)+T_float] 		= (Byte2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_byte<<4)+T_boolean] 	= T_undefined;
		table[(T_byte<<4)+T_char] 		= (Byte2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_byte<<4)+T_int] 		= (Byte2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_byte<<4)+T_null] 		= T_undefined;

		//	table[(T_long<<4)+T_undefined] 	= T_undefined;
		table[(T_long<<4)+T_byte] 		= (Long2Long<<12)+(Byte2Long<<4)+T_boolean;
		table[(T_long<<4)+T_long] 		= (Long2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_long<<4)+T_short] 		= (Long2Long<<12)+(Short2Long<<4)+T_boolean;
		//	table[(T_long<<4)+T_void] 		= T_undefined;
		//	table[(T_long<<4)+T_String] 	= T_undefined;
		//	table[(T_long<<4)+T_Object] 	= T_undefined;
		table[(T_long<<4)+T_double] 	= (Long2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_long<<4)+T_float] 		= (Long2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_long<<4)+T_boolean] 	= T_undefined;
		table[(T_long<<4)+T_char] 		= (Long2Long<<12)+(Char2Long<<4)+T_boolean;
		table[(T_long<<4)+T_int] 		= (Long2Long<<12)+(Int2Long<<4)+T_boolean;
		//	table[(T_long<<4)+T_null] 		= T_undefined;

		//	table[(T_short<<4)+T_undefined] 	= T_undefined;
		table[(T_short<<4)+T_byte] 			= (Short2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_short<<4)+T_long] 			= (Short2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_short<<4)+T_short] 		= (Short2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_short<<4)+T_void] 			= T_undefined;
		//	table[(T_short<<4)+T_String] 		= T_undefined;
		//	table[(T_short<<4)+T_Object] 		= T_undefined;
		table[(T_short<<4)+T_double] 		= (Short2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_short<<4)+T_float] 		= (Short2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_short<<4)+T_boolean] 		= T_undefined;
		table[(T_short<<4)+T_char] 			= (Short2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_short<<4)+T_int] 			= (Short2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_short<<4)+T_null] 			= T_undefined;

		//	table[(T_void<<4)+T_undefined] 	= T_undefined;
		//	table[(T_void<<4)+T_byte] 		= T_undefined;
		//	table[(T_void<<4)+T_long] 		= T_undefined;
		//	table[(T_void<<4)+T_short] 		= T_undefined;
		//	table[(T_void<<4)+T_void] 		= T_undefined;
		//	table[(T_void<<4)+T_String] 	= T_undefined;
		//	table[(T_void<<4)+T_Object] 	= T_undefined;
		//	table[(T_void<<4)+T_double] 	= T_undefined;
		//	table[(T_void<<4)+T_float] 		= T_undefined;
		//	table[(T_void<<4)+T_boolean] 	= T_undefined;
		//	table[(T_void<<4)+T_char] 		= T_undefined;
		//	table[(T_void<<4)+T_int] 		= T_undefined;
		//	table[(T_void<<4)+T_null] 		= T_undefined;

		//	table[(T_String<<4)+T_undefined] 	= T_undefined;
		//	table[(T_String<<4)+T_byte] 		= T_undefined;
		//	table[(T_String<<4)+T_long] 		= T_undefined;
		//	table[(T_String<<4)+T_short] 		= T_undefined;
		//	table[(T_String<<4)+T_void] 		= T_undefined;
		//	table[(T_String<<4)+T_String] 		= T_undefined;
		//	table[(T_String<<4)+T_Object] 		= T_undefined;
		//	table[(T_String<<4)+T_double] 		= T_undefined;
		//	table[(T_String<<4)+T_float] 		= T_undefined;
		//	table[(T_String<<4)+T_boolean] 		= T_undefined;
		//	table[(T_String<<4)+T_char] 		= T_undefined;
		//	table[(T_String<<4)+T_int] 			= T_undefined;
		//	table[(T_String<<4)+T_null] 		= T_undefined;

		//	table[(T_Object<<4)+T_undefined] 	= T_undefined;
		//	table[(T_Object<<4)+T_byte] 		= T_undefined;
		//	table[(T_Object<<4)+T_long] 		= T_undefined;
		//	table[(T_Object<<4)+T_short]		= T_undefined;
		//	table[(T_Object<<4)+T_void] 		= T_undefined;
		//	table[(T_Object<<4)+T_String] 		= T_undefined;
		//	table[(T_Object<<4)+T_Object] 		= T_undefined;
		//	table[(T_Object<<4)+T_double] 		= T_undefined;
		//	table[(T_Object<<4)+T_float] 		= T_undefined;
		//	table[(T_Object<<4)+T_boolean]		= T_undefined;
		//	table[(T_Object<<4)+T_char] 		= T_undefined;
		//	table[(T_Object<<4)+T_int] 			= T_undefined;
		//	table[(T_Object<<4)+T_null] 		= T_undefined;

		//	table[(T_double<<4)+T_undefined] 	= T_undefined;
		table[(T_double<<4)+T_byte] 		= (Double2Double<<12)+(Byte2Double<<4)+T_boolean;
		table[(T_double<<4)+T_long] 		= (Double2Double<<12)+(Long2Double<<4)+T_boolean;
		table[(T_double<<4)+T_short] 		= (Double2Double<<12)+(Short2Double<<4)+T_boolean;
		//	table[(T_double<<4)+T_void] 		= T_undefined;
		//	table[(T_double<<4)+T_String] 		= T_undefined;
		//	table[(T_double<<4)+T_Object] 		= T_undefined;
		table[(T_double<<4)+T_double] 		= (Double2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_double<<4)+T_float] 		= (Double2Double<<12)+(Float2Double<<4)+T_boolean;
		//	table[(T_double<<4)+T_boolean] 		= T_undefined;
		table[(T_double<<4)+T_char] 		= (Double2Double<<12)+(Char2Double<<4)+T_boolean;
		table[(T_double<<4)+T_int] 			= (Double2Double<<12)+(Int2Double<<4)+T_boolean;
		//	table[(T_double<<4)+T_null] 		= T_undefined;

		//	table[(T_float<<4)+T_undefined] 	= T_undefined;
		table[(T_float<<4)+T_byte] 			= (Float2Float<<12)+(Byte2Float<<4)+T_boolean;
		table[(T_float<<4)+T_long] 			= (Float2Float<<12)+(Long2Float<<4)+T_boolean;
		table[(T_float<<4)+T_short] 		= (Float2Float<<12)+(Short2Float<<4)+T_boolean;
		//	table[(T_float<<4)+T_void] 			= T_undefined;
		//	table[(T_float<<4)+T_String] 		= T_undefined;
		//	table[(T_float<<4)+T_Object] 		= T_undefined;
		table[(T_float<<4)+T_double] 		= (Float2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_float<<4)+T_float] 		= (Float2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_float<<4)+T_boolean] 		= T_undefined;
		table[(T_float<<4)+T_char] 			= (Float2Float<<12)+(Char2Float<<4)+T_boolean;
		table[(T_float<<4)+T_int] 			= (Float2Float<<12)+(Int2Float<<4)+T_boolean;
		//	table[(T_float<<4)+T_null] 			= T_undefined;

		//	table[(T_boolean<<4)+T_undefined] 		= T_undefined;
		//	table[(T_boolean<<4)+T_byte] 			= T_undefined;
		//	table[(T_boolean<<4)+T_long] 			= T_undefined;
		//	table[(T_boolean<<4)+T_short] 			= T_undefined;
		//	table[(T_boolean<<4)+T_void] 			= T_undefined;
		//	table[(T_boolean<<4)+T_String] 			= T_undefined;
		//	table[(T_boolean<<4)+T_Object] 			= T_undefined;
		//	table[(T_boolean<<4)+T_double] 			= T_undefined;
		//	table[(T_boolean<<4)+T_float] 			= T_undefined;
		//	table[(T_boolean<<4)+T_boolean] 		= T_undefined;
		//	table[(T_boolean<<4)+T_char] 			= T_undefined;
		//	table[(T_boolean<<4)+T_int] 			= T_undefined;
		//	table[(T_boolean<<4)+T_null] 			= T_undefined;

		//	table[(T_char<<4)+T_undefined] 		= T_undefined;
		table[(T_char<<4)+T_byte] 			= (Char2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_char<<4)+T_long] 			= (Char2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_char<<4)+T_short] 			= (Char2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_char<<4)+T_void] 			= T_undefined;
		//	table[(T_char<<4)+T_String] 		= T_undefined;
		//	table[(T_char<<4)+T_Object] 		= T_undefined;
		table[(T_char<<4)+T_double] 		= (Char2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_char<<4)+T_float] 			= (Char2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_char<<4)+T_boolean] 		= T_undefined;
		table[(T_char<<4)+T_char] 			= (Char2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_char<<4)+T_int] 			= (Char2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_char<<4)+T_null] 			= T_undefined;

		//	table[(T_int<<4)+T_undefined] 	= T_undefined;
		table[(T_int<<4)+T_byte] 		= (Int2Int<<12)+(Byte2Int<<4)+T_boolean;
		table[(T_int<<4)+T_long] 		= (Int2Long<<12)+(Long2Long<<4)+T_boolean;
		table[(T_int<<4)+T_short] 		= (Int2Int<<12)+(Short2Int<<4)+T_boolean;
		//	table[(T_int<<4)+T_void] 		= T_undefined;
		//	table[(T_int<<4)+T_String] 		= T_undefined;
		//	table[(T_int<<4)+T_Object] 		= T_undefined;
		table[(T_int<<4)+T_double] 		= (Int2Double<<12)+(Double2Double<<4)+T_boolean;
		table[(T_int<<4)+T_float] 		= (Int2Float<<12)+(Float2Float<<4)+T_boolean;
		//	table[(T_int<<4)+T_boolean] 	= T_undefined;
		table[(T_int<<4)+T_char] 		= (Int2Int<<12)+(Char2Int<<4)+T_boolean;
		table[(T_int<<4)+T_int] 		= (Int2Int<<12)+(Int2Int<<4)+T_boolean;
		//	table[(T_int<<4)+T_null] 		= T_undefined;

		//	table[(T_null<<4)+T_undefined] 		= T_undefined;
		//	table[(T_null<<4)+T_byte] 			= T_undefined;
		//	table[(T_null<<4)+T_long] 			= T_undefined;
		//	table[(T_null<<4)+T_short] 			= T_undefined;
		//	table[(T_null<<4)+T_void] 			= T_undefined;
		//	table[(T_null<<4)+T_String] 		= T_undefined;
		//	table[(T_null<<4)+T_Object] 		= T_undefined;
		//	table[(T_null<<4)+T_double] 		= T_undefined;
		//	table[(T_null<<4)+T_float] 			= T_undefined;
		//	table[(T_null<<4)+T_boolean] 		= T_undefined;
		//	table[(T_null<<4)+T_char] 			= T_undefined;
		//	table[(T_null<<4)+T_int] 			= T_undefined;
		//	table[(T_null<<4)+T_null] 			= T_undefined;

		return table;
	}

	public static final int[] get_LESS_EQUAL(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_LESS();
	}

	public static final int[] get_MINUS(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table = (int[]) get_PLUS().clone();

		// customization
		table[(T_JavaLangString<<4)+T_byte] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_long] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_short] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_void] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_JavaLangString] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_JavaLangObject] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_double] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_float] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_boolean] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_char] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_int] 			= T_undefined;
		table[(T_JavaLangString<<4)+T_null] 		= T_undefined;

		table[(T_byte<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_long<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_short<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_void<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_JavaLangObject<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_double<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_float<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_boolean<<4)+T_JavaLangString] 		= T_undefined;
		table[(T_char<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_int<<4)	+T_JavaLangString] 		= T_undefined;
		table[(T_null<<4)	+T_JavaLangString] 		= T_undefined;

		table[(T_null<<4)	+T_null] 		= T_undefined;

		return table;
	}

	public static final int[] get_MULTIPLY(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_MINUS();
	}

	public static final int[] get_OR(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4


		//	int[] table  = new int[16*16];
		return get_AND();
	}

	public static final int[] get_OR_OR(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_AND_AND();
	}

	public static final int[] get_PLUS(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		int[] table  = new int[16*16];

		//	table[(T_undefined<<4)+T_undefined] 	= T_undefined;
		//	table[(T_undefined<<4)+T_byte] 			= T_undefined;
		//	table[(T_undefined<<4)+T_long] 			= T_undefined;
		//	table[(T_undefined<<4)+T_short] 		= T_undefined;
		//	table[(T_undefined<<4)+T_void] 			= T_undefined;
		//	table[(T_undefined<<4)+T_String] 		= T_undefined;
		//	table[(T_undefined<<4)+T_Object] 		= T_undefined;
		//	table[(T_undefined<<4)+T_double] 		= T_undefined;
		//	table[(T_undefined<<4)+T_float] 		= T_undefined;
		//	table[(T_undefined<<4)+T_boolean] 		= T_undefined;
		//	table[(T_undefined<<4)+T_char] 			= T_undefined;
		//	table[(T_undefined<<4)+T_int] 			= T_undefined;
		//	table[(T_undefined<<4)+T_null] 			= T_undefined;

		//	table[(T_byte<<4)+T_undefined] 	= T_undefined;
		table[(T_byte<<4)+T_byte] 		= (Byte2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_byte<<4)+T_long] 		= (Byte2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_byte<<4)+T_short] 		= (Byte2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_byte<<4)+T_void] 		= T_undefined;
		table[(T_byte<<4)+T_JavaLangString] 	= (Byte2Byte<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_byte<<4)+T_Object] 	= T_undefined;
		table[(T_byte<<4)+T_double] 	= (Byte2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_byte<<4)+T_float] 		= (Byte2Float<<12)+(Float2Float<<4)+T_float;
		//	table[(T_byte<<4)+T_boolean] 	= T_undefined;
		table[(T_byte<<4)+T_char] 		= (Byte2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_byte<<4)+T_int] 		= (Byte2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_byte<<4)+T_null] 		= T_undefined;

		//	table[(T_long<<4)+T_undefined] 	= T_undefined;
		table[(T_long<<4)+T_byte] 		= (Long2Long<<12)+(Byte2Long<<4)+T_long;
		table[(T_long<<4)+T_long] 		= (Long2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_long<<4)+T_short] 		= (Long2Long<<12)+(Short2Long<<4)+T_long;
		//	table[(T_long<<4)+T_void] 		= T_undefined;
		table[(T_long<<4)+T_JavaLangString] 	= (Long2Long<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_long<<4)+T_Object] 	= T_undefined;
		table[(T_long<<4)+T_double] 	= (Long2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_long<<4)+T_float] 		= (Long2Float<<12)+(Float2Float<<4)+T_float;
		//	table[(T_long<<4)+T_boolean] 	= T_undefined;
		table[(T_long<<4)+T_char] 		= (Long2Long<<12)+(Char2Long<<4)+T_long;
		table[(T_long<<4)+T_int] 		= (Long2Long<<12)+(Int2Long<<4)+T_long;
		//	table[(T_long<<4)+T_null] 		= T_undefined;

		//	table[(T_short<<4)+T_undefined] 	= T_undefined;
		table[(T_short<<4)+T_byte] 			= (Short2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_short<<4)+T_long] 			= (Short2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_short<<4)+T_short] 		= (Short2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_short<<4)+T_void] 			= T_undefined;
		table[(T_short<<4)+T_JavaLangString] 		= (Short2Short<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_short<<4)+T_Object] 		= T_undefined;
		table[(T_short<<4)+T_double] 		= (Short2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_short<<4)+T_float] 		= (Short2Float<<12)+(Float2Float<<4)+T_float;
		//	table[(T_short<<4)+T_boolean] 		= T_undefined;
		table[(T_short<<4)+T_char] 			= (Short2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_short<<4)+T_int] 			= (Short2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_short<<4)+T_null] 			= T_undefined;

		//	table[(T_void<<4)+T_undefined] 	= T_undefined;
		//	table[(T_void<<4)+T_byte] 		= T_undefined;
		//	table[(T_void<<4)+T_long] 		= T_undefined;
		//	table[(T_void<<4)+T_short] 		= T_undefined;
		//	table[(T_void<<4)+T_void] 		= T_undefined;
		//	table[(T_void<<4)+T_String] 	= T_undefined;
		//	table[(T_void<<4)+T_Object] 	= T_undefined;
		//	table[(T_void<<4)+T_double] 	= T_undefined;
		//	table[(T_void<<4)+T_float] 		= T_undefined;
		//	table[(T_void<<4)+T_boolean] 	= T_undefined;
		//	table[(T_void<<4)+T_char] 		= T_undefined;
		//	table[(T_void<<4)+T_int] 		= T_undefined;
		//	table[(T_void<<4)+T_null] 		= T_undefined;

		//	table[(T_String<<4)+T_undefined] 	= T_undefined;
		table[(T_JavaLangString<<4)+T_byte] 		= (String2String<<12)+(Byte2Byte<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_long] 		= (String2String<<12)+(Long2Long<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_short] 		= (String2String<<12)+(Short2Short<<4)+T_JavaLangString;
		//	table[(T_String<<4)+T_void] 		= T_undefined;
		table[(T_JavaLangString<<4)+T_JavaLangString] 		= (String2String<<12)+(String2String<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_JavaLangObject] 		= (String2String<<12)+(Object2Object<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_double] 		= (String2String<<12)+(Double2Double<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_float] 		= (String2String<<12)+(Float2Float<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_boolean] 		= (String2String<<12)+(Boolean2Boolean<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_char] 		= (String2String<<12)+(Char2Char<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_int] 			= (String2String<<12)+(Int2Int<<4)+T_JavaLangString;
		table[(T_JavaLangString<<4)+T_null] 		= (String2String<<12)+(T_null<<8)+(T_null<<4)+T_JavaLangString;

		//	table[(T_Object<<4)+T_undefined] 	= T_undefined;
		//	table[(T_Object<<4)+T_byte] 		= T_undefined;
		//	table[(T_Object<<4)+T_long] 		= T_undefined;
		//	table[(T_Object<<4)+T_short]		= T_undefined;
		//	table[(T_Object<<4)+T_void] 		= T_undefined;
		table[(T_JavaLangObject<<4)+T_JavaLangString] 		= (Object2Object<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_Object<<4)+T_Object] 		= T_undefined;
		//	table[(T_Object<<4)+T_double] 		= T_undefined;
		//	table[(T_Object<<4)+T_float] 		= T_undefined;
		//	table[(T_Object<<4)+T_boolean]		= T_undefined;
		//	table[(T_Object<<4)+T_char] 		= T_undefined;
		//	table[(T_Object<<4)+T_int] 			= T_undefined;
		//	table[(T_Object<<4)+T_null] 		= T_undefined;

		//	table[(T_double<<4)+T_undefined] 	= T_undefined;
		table[(T_double<<4)+T_byte] 		= (Double2Double<<12)+(Byte2Double<<4)+T_double;
		table[(T_double<<4)+T_long] 		= (Double2Double<<12)+(Long2Double<<4)+T_double;
		table[(T_double<<4)+T_short] 		= (Double2Double<<12)+(Short2Double<<4)+T_double;
		//	table[(T_double<<4)+T_void] 		= T_undefined;
		table[(T_double<<4)+T_JavaLangString] 		= (Double2Double<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_double<<4)+T_Object] 		= T_undefined;
		table[(T_double<<4)+T_double] 		= (Double2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_double<<4)+T_float] 		= (Double2Double<<12)+(Float2Double<<4)+T_double;
		//	table[(T_double<<4)+T_boolean] 		= T_undefined;
		table[(T_double<<4)+T_char] 		= (Double2Double<<12)+(Char2Double<<4)+T_double;
		table[(T_double<<4)+T_int] 			= (Double2Double<<12)+(Int2Double<<4)+T_double;
		//	table[(T_double<<4)+T_null] 		= T_undefined;

		//	table[(T_float<<4)+T_undefined] 	= T_undefined;
		table[(T_float<<4)+T_byte] 			= (Float2Float<<12)+(Byte2Float<<4)+T_float;
		table[(T_float<<4)+T_long] 			= (Float2Float<<12)+(Long2Float<<4)+T_float;
		table[(T_float<<4)+T_short] 		= (Float2Float<<12)+(Short2Float<<4)+T_float;
		//	table[(T_float<<4)+T_void] 			= T_undefined;
		table[(T_float<<4)+T_JavaLangString] 		= (Float2Float<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_float<<4)+T_Object] 		= T_undefined;
		table[(T_float<<4)+T_double] 		= (Float2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_float<<4)+T_float] 		= (Float2Float<<12)+(Float2Float<<4)+T_float;
		//	table[(T_float<<4)+T_boolean] 		= T_undefined;
		table[(T_float<<4)+T_char] 			= (Float2Float<<12)+(Char2Float<<4)+T_float;
		table[(T_float<<4)+T_int] 			= (Float2Float<<12)+(Int2Float<<4)+T_float;
		//	table[(T_float<<4)+T_null] 			= T_undefined;

		//	table[(T_boolean<<4)+T_undefined] 		= T_undefined;
		//	table[(T_boolean<<4)+T_byte] 			= T_undefined;
		//	table[(T_boolean<<4)+T_long] 			= T_undefined;
		//	table[(T_boolean<<4)+T_short] 			= T_undefined;
		//	table[(T_boolean<<4)+T_void] 			= T_undefined;
		table[(T_boolean<<4)+T_JavaLangString] 			= (Boolean2Boolean<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_boolean<<4)+T_Object] 			= T_undefined;
		//	table[(T_boolean<<4)+T_double] 			= T_undefined;
		//	table[(T_boolean<<4)+T_float] 			= T_undefined;
		//	table[(T_boolean<<4)+T_boolean] 		= T_undefined;
		//	table[(T_boolean<<4)+T_char] 			= T_undefined;
		//	table[(T_boolean<<4)+T_int] 			= T_undefined;
		//	table[(T_boolean<<4)+T_null] 			= T_undefined;

		//	table[(T_char<<4)+T_undefined] 		= T_undefined;
		table[(T_char<<4)+T_byte] 			= (Char2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_char<<4)+T_long] 			= (Char2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_char<<4)+T_short] 			= (Char2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_char<<4)+T_void] 			= T_undefined;
		table[(T_char<<4)+T_JavaLangString] 		= (Char2Char<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_char<<4)+T_Object] 		= T_undefined;
		table[(T_char<<4)+T_double] 		= (Char2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_char<<4)+T_float] 			= (Char2Float<<12)+(Float2Float<<4)+T_float;
		//	table[(T_char<<4)+T_boolean] 		= T_undefined;
		table[(T_char<<4)+T_char] 			= (Char2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_char<<4)+T_int] 			= (Char2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_char<<4)+T_null] 			= T_undefined;

		//	table[(T_int<<4)+T_undefined] 	= T_undefined;
		table[(T_int<<4)+T_byte] 		= (Int2Int<<12)+(Byte2Int<<4)+T_int;
		table[(T_int<<4)+T_long] 		= (Int2Long<<12)+(Long2Long<<4)+T_long;
		table[(T_int<<4)+T_short] 		= (Int2Int<<12)+(Short2Int<<4)+T_int;
		//	table[(T_int<<4)+T_void] 		= T_undefined;
		table[(T_int<<4)+T_JavaLangString] 		= (Int2Int<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_int<<4)+T_Object] 		= T_undefined;
		table[(T_int<<4)+T_double] 		= (Int2Double<<12)+(Double2Double<<4)+T_double;
		table[(T_int<<4)+T_float] 		= (Int2Float<<12)+(Float2Float<<4)+T_float;
		//	table[(T_int<<4)+T_boolean] 	= T_undefined;
		table[(T_int<<4)+T_char] 		= (Int2Int<<12)+(Char2Int<<4)+T_int;
		table[(T_int<<4)+T_int] 		= (Int2Int<<12)+(Int2Int<<4)+T_int;
		//	table[(T_int<<4)+T_null] 		= T_undefined;

		//	table[(T_null<<4)+T_undefined] 		= T_undefined;
		//	table[(T_null<<4)+T_byte] 			= T_undefined;
		//	table[(T_null<<4)+T_long] 			= T_undefined;
		//	table[(T_null<<4)+T_short] 			= T_undefined;
		//	table[(T_null<<4)+T_void] 			= T_undefined;
		table[(T_null<<4)+T_JavaLangString] 		= (T_null<<16)+(T_null<<12)+(String2String<<4)+T_JavaLangString;
		//	table[(T_null<<4)+T_Object] 		= T_undefined;
		//	table[(T_null<<4)+T_double] 		= T_undefined;
		//	table[(T_null<<4)+T_float] 			= T_undefined;
		//	table[(T_null<<4)+T_boolean] 		= T_undefined;
		//	table[(T_null<<4)+T_char] 			= T_undefined;
		//	table[(T_null<<4)+T_int] 			= T_undefined;
		//	table[(T_null<<4)+T_null] 			= (Null2String<<12)+(Null2String<<4)+T_String;;

		return table;
	}

	public static final int[] get_REMAINDER(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_MINUS();
	}

	public static final int[] get_RIGHT_SHIFT(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_LEFT_SHIFT();
	}

	public static final int[] get_UNSIGNED_RIGHT_SHIFT(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_LEFT_SHIFT();
	}

	public static final int[] get_XOR(){

		//the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8    <<4

		//	int[] table  = new int[16*16];
		return get_AND();
	}

	public String operatorToString() {
		switch ((this.bits & OperatorMASK) >> OperatorSHIFT) {
			case EQUAL_EQUAL :
				return "=="; //$NON-NLS-1$
			case LESS_EQUAL :
				return "<="; //$NON-NLS-1$
			case GREATER_EQUAL :
				return ">="; //$NON-NLS-1$
			case NOT_EQUAL :
				return "!="; //$NON-NLS-1$
			case LEFT_SHIFT :
				return "<<"; //$NON-NLS-1$
			case RIGHT_SHIFT :
				return ">>"; //$NON-NLS-1$
			case UNSIGNED_RIGHT_SHIFT :
				return ">>>"; //$NON-NLS-1$
			case OR_OR :
				return "||"; //$NON-NLS-1$
			case AND_AND :
				return "&&"; //$NON-NLS-1$
			case PLUS :
				return "+"; //$NON-NLS-1$
			case MINUS :
				return "-"; //$NON-NLS-1$
			case NOT :
				return "!"; //$NON-NLS-1$
			case REMAINDER :
				return "%"; //$NON-NLS-1$
			case XOR :
				return "^"; //$NON-NLS-1$
			case AND :
				return "&"; //$NON-NLS-1$
			case MULTIPLY :
				return "*"; //$NON-NLS-1$
			case OR :
				return "|"; //$NON-NLS-1$
			case TWIDDLE :
				return "~"; //$NON-NLS-1$
			case DIVIDE :
				return "/"; //$NON-NLS-1$
			case GREATER :
				return ">"; //$NON-NLS-1$
			case LESS :
				return "<"; //$NON-NLS-1$
			case QUESTIONCOLON :
				return "?:"; //$NON-NLS-1$
			case EQUAL :
				return "="; //$NON-NLS-1$
		}
		return "unknown operator"; //$NON-NLS-1$
	}

	public int nullStatus(FlowInfo flowInfo) {
		return FlowInfo.NON_NULL;
	}

	public StringBuffer printExpression(int indent, StringBuffer output){

		output.append('(');
		return printExpressionNoParenthesis(0, output).append(')');
	}

	public abstract StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output);
}
