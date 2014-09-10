/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import org.more.asm.Opcodes;
import org.more.asm.Type;
/**
 * 生成字节码时候使用的工具类，当重写{@link ClassConfig}的相关方法时候会用上此类。
 * @version 2009-10-16
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerEngineToos implements Opcodes {
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
            throw new UnsupportedOperationException("不支持的类型装载请求");//
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
            throw new UnsupportedOperationException("不支持的类型装载请求");//
        }
    }
    /**根据asm类型获取其ASTORE指令。*/
    public static int getAstore(final String asmType) {
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
    }
    //=======================================================================================================================
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
        } else if (classType.isArray() == true) {
            return "[" + InnerEngineToos.toAsmType(classType.getComponentType());
        } else {
            return "L" + Type.getInternalName(classType) + ";";
        }
    }
    /**将某一个类型转为asm形式的表述， int,int 转为 II，String,int转为 Ljava/lang/String;I。*/
    public static String toAsmType(final Class<?>[] classType) {
        String returnString = "";
        for (Class<?> c : classType) {
            returnString += InnerEngineToos.toAsmType(c);
        };
        return returnString;
    }
    /**获取方法的Signature描述信息。*/
    public static String toAsmSignature(Method targetMethod) {
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
                    if (tName.equals(mTypeList[j].name) == true) {
                        signature.append(tName);
                        if (mTypeList[j].paramClass.isInterface() == true) {
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
    /**判断某个类是否为一个lang包的类。*/
    public static boolean isLangClass(final Class<?> type) {
        return type.getName().startsWith("java.lang.");
    };
    /** 将IIIILjava/lang/Integer;F形式的ASM类型表述分解为数组。测试字符串IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean; */
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
                    if (s.equals("") == true) {
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
        } catch (Exception e) {
            throw new RuntimeException("不合法的ASM类型desc。");
        }
    }
    /**将类名转换为asm类名。*/
    public static String replaceClassName(final Class<?> targetClass) {
        return targetClass.getName().replace(".", "/");
    }
    /**通过位运算决定check是否在data里。*/
    public static boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    };
    /**检查类型是否为一个基本类型或其包装类型，基本类型包括了boolean, byte, char, short, int, long, float, 和 double*/
    public static boolean isBaseType(final Class<?> type) {
        /*判断是否为基本类型*/
        if (type.isPrimitive() == true) {
            return true;
        }
        /*判断各种包装类型*/
        if (type == Boolean.class) {
            return true;
        }
        if (type == Byte.class) {
            return true;
        }
        if (type == Character.class) {
            return true;
        }
        if (type == Short.class) {
            return true;
        }
        if (type == Integer.class) {
            return true;
        }
        if (type == Long.class) {
            return true;
        }
        if (type == Float.class) {
            return true;
        }
        if (type == Double.class) {
            return true;
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
    //
    private static class MoreType {
        public String          name       = null;
        public Class<?>        paramClass = null;
        public TypeVariable<?> paramType  = null;
    }
    //    /**检测类名是否合法。*/
    //    public static boolean checkClassName(final String className) {
    //        if (className == null || className.equals("")) {
    //            return false;
    //        }
    //        String item[] = { "..", "!", "@", "#", "%", "^", "&", "*", "(", ")", "-", "=", "+", "{", "}", ";", ";", "\"", "'", "<", ">", ",", "?", "/", "`", "~", " ", "\\", "|" };
    //        for (int i = 0; i <= item.length - 1; i++) {
    //            if (className.indexOf(item[i]) >= 0) {
    //                return false;
    //            }
    //        }
    //        if (className.indexOf(".") == 0) {
    //            return false;
    //        }
    //        if (className.indexOf(".", className.length()) == className.length()) {
    //            return false;
    //        }
    //        return true;
    //    }
    //    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    //    public static Class<?> toJavaType(final String asmClassType, final ClassLoader loader) {
    //        if (asmClassType.equals("I") == true) {
    //            return int.class;
    //        } else if (asmClassType.equals("B") == true) {
    //            return byte.class;
    //        } else if (asmClassType.equals("C") == true) {
    //            return char.class;
    //        } else if (asmClassType.equals("D") == true) {
    //            return double.class;
    //        } else if (asmClassType.equals("F") == true) {
    //            return float.class;
    //        } else if (asmClassType.equals("J") == true) {
    //            return long.class;
    //        } else if (asmClassType.equals("S") == true) {
    //            return short.class;
    //        } else if (asmClassType.equals("Z") == true) {
    //            return boolean.class;
    //        } else if (asmClassType.equals("V") == true) {
    //            return void.class;
    //        } else if (asmClassType.charAt(0) == '[') {
    //            int length = 0;
    //            while (true) {
    //                if (asmClassType.charAt(length) != '[') {
    //                    break;
    //                }
    //                length++;
    //            }
    //            String arrayType = asmClassType.substring(length, asmClassType.length());
    //            arrayType = arrayType.replace("/", ".");
    //            Class<?> returnType = EngineToos.toJavaType(arrayType, loader);
    //            for (int i = 0; i < length; i++) {
    //                Object obj = Array.newInstance(returnType, length);
    //                returnType = obj.getClass();
    //            }
    //            return returnType;
    //        } else {
    //            String cs = EngineToos.asmTypeToType(asmClassType).replace("/", ".");
    //            try {
    //                return loader.loadClass(cs);
    //            } catch (ClassNotFoundException e) {
    //                throw new RuntimeException(cs + "类不能被装载," + e.getMessage());
    //            }
    //        }
    //    }
    //    /**使用指定的ClassLoader将一组asm类型转化为一组Class对象。*/
    //    public static Class<?>[] toJavaType(final String[] asmClassType, final ClassLoader loader) {
    //        Class<?>[] types = new Class<?>[asmClassType.length];
    //        for (int i = 0; i < asmClassType.length; i++) {
    //            types[i] = EngineToos.toJavaType(asmClassType[i], loader);
    //        }
    //        return types;
    //    }
    //    /**在一个类中查找某个方法。*/
    //    public static Method findMethod(final Class<?> atClass, final String name, final Class<?>[] paramType) {
    //        try {
    //            return atClass.getMethod(name, paramType);
    //        } catch (Exception e) {
    //            try {
    //                return atClass.getDeclaredMethod(name, paramType);
    //            } catch (Exception e1) {
    //                return null;
    //            }
    //        }
    //    }
    //    /**返回一个类的多个方法，其中包含了类定义的私有方法和父类中可见的方法。*/
    //    public static ArrayList<Method> findAllMethod(final Class<?> atClass) {
    //        ArrayList<Method> al = new ArrayList<Method>();
    //        Method[] m1 = atClass.getDeclaredMethods();
    //        Collections.addAll(al, m1);
    //        for (Method m : atClass.getMethods()) {
    //            if (al.contains(m) == false) {
    //                al.add(m);
    //            }
    //        }
    //        return al;
    //    }
    //    /**返回一个类的多个字段，其中包含了类定义的私有字段和父类中可见的字段。*/
    //    public static ArrayList<Field> findAllField(final Class<?> atClass) {
    //        ArrayList<Field> al = new ArrayList<Field>();
    //        Field[] m1 = atClass.getDeclaredFields();
    //        Collections.addAll(al, m1);
    //        for (Field f : atClass.getFields()) {
    //            if (al.contains(f) == false) {
    //                al.add(f);
    //            }
    //        }
    //        return al;
    //    }
    //=======================================================================================================================
    //    /**获取类完整限定名的类名部分。*/
    //    public static String splitSimpleName(final String fullName) {
    //        String[] ns = fullName.split("\\.");
    //        return ns[ns.length - 1];
    //    }
    //    /**获取类完整限定名的包名部分。*/
    //    public static String splitPackageName(final String fullName) {
    //        if (fullName.lastIndexOf(".") > 0) {
    //            return fullName.substring(0, fullName.lastIndexOf("."));
    //        } else {
    //            return fullName;
    //        }
    //    }
    //
    //
}