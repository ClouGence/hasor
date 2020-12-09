/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core.aop;
import net.hasor.utils.asm.Label;
import net.hasor.utils.asm.MethodVisitor;
import net.hasor.utils.asm.Opcodes;
import net.hasor.utils.asm.Type;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.*;
import java.util.*;

/**
 * 生成字节码时候使用的工具类。
 * @version 2009-10-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AsmTools implements Opcodes {
    /**根据类型获取其Return指令。*/
    public static int getReturn(final String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return Opcodes.IRETURN;//Byte
        case 'C':
            return Opcodes.IRETURN;//Char
        case 'D':
            return Opcodes.DRETURN;//Double
        case 'F':
            return Opcodes.FRETURN;//Float
        case 'I':
            return Opcodes.IRETURN;//Integer
        case 'J':
            return Opcodes.LRETURN;//Long
        case 'L':
            return Opcodes.ARETURN;//Ref
        case 'S':
            return Opcodes.IRETURN;//Short
        case 'Z':
            return Opcodes.IRETURN;//Boolean
        case '[':
            return Opcodes.ARETURN;//Array
        case 'V':
            return Opcodes.RETURN;//Void
        default:
            throw new UnsupportedOperationException("Unsupported LOAD instruction.");//
        }
    }

    /**根据类型获取其Load指令。*/
    public static int getLoad(final String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return Opcodes.ILOAD;//Byte
        case 'C':
            return Opcodes.ILOAD;//Char
        case 'D':
            return Opcodes.DLOAD;//Double
        case 'F':
            return Opcodes.FLOAD;//Float
        case 'I':
            return Opcodes.ILOAD;//Integer
        case 'J':
            return Opcodes.LLOAD;//Long
        case 'L':
            return Opcodes.ALOAD;//Ref
        case 'S':
            return Opcodes.ILOAD;//Short
        case 'Z':
            return Opcodes.ILOAD;//Boolean
        case '[':
            return Opcodes.ALOAD;//Array
        default:
            throw new UnsupportedOperationException("Unsupported LOAD instruction.");//
        }
    }
    /**根据asm类型获取其ASTORE指令。*/
    /*public static int getAstore(final String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return Opcodes.IASTORE;//Byte
        case 'C':
            return Opcodes.IASTORE;//Char
        case 'D':
            return Opcodes.DASTORE;//Double
        case 'F':
            return Opcodes.FASTORE;//Float
        case 'I':
            return Opcodes.IASTORE;//Integer
        case 'J':
            return Opcodes.LASTORE;//Long
        case 'L':
            return Opcodes.AASTORE;//Ref
        case 'S':
            return Opcodes.IASTORE;//Short
        case 'Z':
            return Opcodes.IASTORE;//Boolean
        case '[':
            return Opcodes.AASTORE;//Array
        default:
            throw new UnsupportedOperationException("不支持的类型装载请求");//
        }
    }*/

    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    public static String toAsmType(final Class<?> classType) {
        if (classType == int.class) {
            return "I";
        } else if (classType == byte.class) {
            return "B";
        } else if (classType == char.class) {
            return "C";
        } else if (classType == double.class) {
            return "D";
        } else if (classType == float.class) {
            return "F";
        } else if (classType == long.class) {
            return "J";
        } else if (classType == short.class) {
            return "S";
        } else if (classType == boolean.class) {
            return "Z";
        } else if (classType == void.class) {
            return "V";
        } else if (classType.isArray()) {
            return "[" + AsmTools.toAsmType(classType.getComponentType());
        } else {
            return "L" + Type.getInternalName(classType) + ";";
        }
    }

    /**将某一个类型转为asm形式的表述， int,int 转为 II，String,int转为 Ljava/lang/String;I。*/
    public static String toAsmType(final Class<?>[] classType) {
        String returnString = "";
        for (Class<?> c : classType) {
            returnString += AsmTools.toAsmType(c);
        }
        ;
        return returnString;
    }

    /**获取方法的Signature描述信息。*/
    public static String toAsmSignature(Method targetMethod) {
        class MoreType {
            String          name       = null;
            Class<?>        paramClass = null;
            TypeVariable<?> paramType  = null;
        }
        //
        StringBuffer signature = new StringBuffer();
        {
            //Step:1
            Class<?>[] pTypeArray = targetMethod.getParameterTypes();
            MoreType[] mTypeList = new MoreType[pTypeArray.length];
            for (int i = 0; i < pTypeArray.length; i++) {
                Class<?> pType = pTypeArray[i];
                MoreType mtype = new MoreType();
                mtype.paramClass = pType;
                mTypeList[i] = mtype;
            }
            //Step:2
            java.lang.reflect.Type[] gTypeArray = targetMethod.getGenericParameterTypes();
            for (int i = 0; i < gTypeArray.length; i++) {
                java.lang.reflect.Type gType = gTypeArray[i];
                if (gType instanceof TypeVariable) {
                    mTypeList[i].name = ((TypeVariable<?>) gType).getName();
                    mTypeList[i].paramType = (TypeVariable<?>) gType;
                }
            }
            //Step:3
            java.lang.reflect.Type[] tTypeArray = targetMethod.getTypeParameters();
            for (int i = 0; i < tTypeArray.length; i++) {
                if (i == 0) {
                    signature.append("<");
                }
                //
                TypeVariable<?> tType = (TypeVariable<?>) tTypeArray[i];
                String tName = tType.getName();
                for (int j = 0; j < mTypeList.length; j++) {
                    if (tName.equals(mTypeList[j].name)) {
                        signature.append(tName);
                        if (mTypeList[j].paramClass.isInterface()) {
                            signature.append("::");
                        } else {
                            signature.append(":");
                        }
                        //
                        for (java.lang.reflect.Type atType : mTypeList[j].paramType.getBounds()) {
                            getTypeVarStr(signature, atType);
                        }
                    }
                }
                //
                if (i == tTypeArray.length - 1) {
                    signature.append(">");
                }
            }
            //Step:4
            signature.append("(");
            for (int i = 0; i < mTypeList.length; i++) {
                MoreType mType = mTypeList[i];
                if (mType.name != null) {
                    signature.append(String.format("T%s;", mType.name));
                } else {
                    signature.append(toAsmType(mType.paramClass));
                }
            }
            signature.append(")");
            //Step:5
            signature.append(toAsmType(targetMethod.getReturnType()));
        }
        //
        if (signature.length() == 0) {
            return null;
        }
        return signature.toString();
    }

    private static StringBuffer getTypeVarStr(StringBuffer atString, java.lang.reflect.Type type) {
        //
        if (type instanceof TypeVariable) {
            TypeVariable<?> paramType = (TypeVariable<?>) type;
            atString.append("T" + paramType.getName() + ";");
        } else if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            atString.append("L" + replaceClassName((Class<?>) paramType.getRawType()));
            atString.append("<");
            for (java.lang.reflect.Type atType : paramType.getActualTypeArguments()) {
                getTypeVarStr(atString, atType);
            }
            atString.append(">");
            atString.append(";");
        } else if (type instanceof Class<?>) {
            Class<?> paramType = (Class<?>) type;
            atString.append(toAsmType(paramType));
        } else if (type instanceof WildcardType) {
            WildcardType paramType = (WildcardType) type;
            java.lang.reflect.Type[] upperType = paramType.getUpperBounds();//上边界
            java.lang.reflect.Type[] lowerType = paramType.getLowerBounds();//下边界
            if (lowerType.length == 0 && upperType.length != 0) {
                atString.append("+");
                for (java.lang.reflect.Type atType : upperType) {
                    getTypeVarStr(atString, atType);
                }
            } else if (lowerType.length != 0 && upperType.length != 0) {
                atString.append("-");
                for (java.lang.reflect.Type atType : lowerType) {
                    getTypeVarStr(atString, atType);
                }
            } else {
                throw new RuntimeException("Generic format error.");
            }
        } else if (type instanceof GenericArrayType) {
            //System.out.println();
        }
        //
        return atString;
    }

    /**获取方法的ASM格式描述信息。*/
    public static String toAsmFullDesc(Method method) {
        StringBuffer str = new StringBuffer();
        str.append(method.getName());
        str.append("(");
        str.append(toAsmType(method.getParameterTypes()));
        str.append(")");
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            str.append("V");
        } else {
            str.append(toAsmType(returnType));
        }
        return str.toString();
    }

    /**获取方法的ASM格式描述信息。*/
    public static String toAsmDesc(Method method) {
        StringBuffer str = new StringBuffer();
        str.append("(");
        str.append(toAsmType(method.getParameterTypes()));
        str.append(")");
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            str.append("V");
        } else {
            str.append(toAsmType(returnType));
        }
        return str.toString();
    }

    /**
     * 将IIIILjava/lang/Integer;F形式的ASM类型表述分解为数组。
     * 测试字符串IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean; ->
     *      ["I", "I", "I", "I", "Ljava/lang/Integer;", "F", "[[[I", "Ljava/lang.Boolean"]
     *  */
    public static String[] splitAsmType(final String asmTypes) {
        class AsmTypeRead {
            StringReader sread = null;

            public AsmTypeRead(final String sr) {
                this.sread = new StringReader(sr);
            }

            /** 读取到下一个分号为止或者结束为止。*/
            private String readToSemicolon() throws IOException {
                String res = "";
                while (true) {
                    int strInt = this.sread.read();
                    if (strInt == -1) {
                        return res;
                    } else if ((char) strInt == ';') {
                        return res + ';';
                    } else {
                        res += (char) strInt;
                    }
                }
            }

            /** 读取一个类型 */
            private String readType() throws IOException {
                int strInt = this.sread.read();
                if (strInt == -1) {
                    return "";
                }
                switch ((char) strInt) {
                case '['://array
                    return '[' + this.readType();
                case 'L'://Object
                    return 'L' + this.readToSemicolon();
                default:
                    return String.valueOf((char) strInt);
                }
            }

            /** 读取所有类型 */
            public String[] readTypes() throws IOException {
                ArrayList<String> ss = new ArrayList<String>(0);
                while (true) {
                    String s = this.readType();
                    if ("".equals(s)) {
                        break;
                    } else {
                        ss.add(s);
                    }
                }
                String[] res = new String[ss.size()];
                ss.toArray(res);
                return res;
            }
        }
        try {
            return new AsmTypeRead(asmTypes).readTypes();//     IIIILjava/lang/Integer;F[[[Ljava/util/Date;
        } catch (IOException e) {
            throw new RuntimeException("Invalid ASM type desc.");
        }
    }

    /**将类名转换为asm类名。*/
    public static String replaceClassName(final Class<?> targetClass) {
        return targetClass.getName().replace(".", "/");
    }

    public static String replaceClassName(final String targetClass) {
        return targetClass.replace(".", "/");
    }

    public static String[] replaceClassName(Class<?>[] exceptionTypes) {
        String[] typeStr = new String[exceptionTypes.length];
        for (int i = 0; i < exceptionTypes.length; i++) {
            typeStr[i] = replaceClassName(exceptionTypes[i]);
        }
        return typeStr;
    }

    /**通过位运算决定check是否在data里。*/
    public static boolean checkAnd(final int data, int... check) {
        for (int checkItem : check) {
            int or = data | checkItem;
            if (or != data) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOr(final int data, int... check) {
        for (int checkItem : check) {
            int or = data | checkItem;
            if (or == data) {
                return true;
            }
        }
        return false;
    }

    /**将一个Ljava/lang/Object;形式的字符串转化为java/lang/Object形式。*/
    public static String asmTypeToType(final String asmType) {
        if (asmType.charAt(0) == 'L') {
            return asmType.substring(1, asmType.length() - 1);
        } else {
            return asmType;
        }
    }

    /**父类是否支持*/
    public static boolean isSupport(Class<?> superClass) {
        String resName = superClass.getName().replace(".", "/") + ".class";
        if (resName.startsWith("java/") || resName.startsWith("javax/")) {
            return false;
        } else {
            return AsmTools.checkAnd(superClass.getModifiers(), Modifier.PUBLIC);
        }
    }

    //Code Builder “new Object[] { abc, abcc, abcc };”
    public static void codeBuilder_1(MethodVisitor mv, String[] asmParams, Map<String, Integer> paramIndexMap) {
        Set<String> typeEnum = new HashSet<>(Arrays.asList("B", "S", "I", "J", "F", "D", "C", "Z"));
        int paramCount = asmParams.length;
        mv.visitIntInsn(Opcodes.BIPUSH, paramCount);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            if (typeEnum.contains(asmParams[i])) {
                mv.visitVarInsn(AsmTools.getLoad(asmType), paramIndexMap.get("args" + i));
                codeBuilder_valueOf(mv, asmParams[i]);
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, paramIndexMap.get("args" + i));
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }

    //Code Builder “Double.valueOf(xxx);”
    public static void codeBuilder_valueOf(MethodVisitor mv, String asmType) {
        if (asmType.equals("B")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (asmType.equals("S")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (asmType.equals("I")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (asmType.equals("J")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (asmType.equals("F")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (asmType.equals("D")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (asmType.equals("C")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (asmType.equals("Z")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
    }

    //Code Builder “new Class[] { int.class, Object.class, boolean.class, short.class };”
    public static void codeBuilder_2(MethodVisitor mv, String[] asmParams) {
        int paramCount = asmParams.length;
        mv.visitIntInsn(BIPUSH, paramCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            if (asmParams[i].equals("B")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("S")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("I")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("J")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("F")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("D")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("C")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("Z")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else {
                mv.visitLdcInsn(Type.getType(asmType));//  Ljava/lang/Object;
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }

    //Code Builder “return ...”
    public static void codeBuilder_3(MethodVisitor mv, String asmReturns) {
        codeBuilder_3(mv, asmReturns, null);
    }

    //Code Builder “return ...”
    public static void codeBuilder_3(MethodVisitor mv, String asmReturns, Label tryEnd) {
        codeBuilder_Cast(mv, asmReturns, tryEnd);
        mv.visitInsn(AsmTools.getReturn(asmReturns));
    }

    //Code Builder “(Object) ...”
    public static void codeBuilder_Cast(MethodVisitor mv, String asmReturns, Label tryEnd) {
        if (asmReturns.equals("B")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("S")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("I")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("J")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("F")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("D")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("C")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("Z")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("V")) {
            mv.visitInsn(Opcodes.POP);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, AsmTools.asmTypeToType(asmReturns));
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        }
    }
}