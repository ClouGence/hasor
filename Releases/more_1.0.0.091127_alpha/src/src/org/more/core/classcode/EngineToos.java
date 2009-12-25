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
package org.more.core.classcode;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import org.more.FormatException;
import org.more.core.asm.Opcodes;
import org.more.core.asm.Type;
/**
 * 生成字节码时候使用的工具类，当重写{@link ClassEngine}的相关方法时候会用上此类。
 * Date : 2009-10-16
 * @author 赵永春
 */
public class EngineToos implements Opcodes {
    /** 在类型中获取某个方法，首先在类定义的方法中找(包括私有方法)，随后在起共有方法中以及继承的方法中找。 */
    public static java.lang.reflect.Method getMethod(Class<?> atClass, String name, Class<?>... types) {
        try {
            return atClass.getDeclaredMethod(name, types);
        } catch (Exception e) {
            try {
                return atClass.getMethod(name, types);
            } catch (Exception e1) {
                return null;
            }
        }
    }
    /**检测类名是否合法。*/
    public static boolean checkClassName(String className) {
        if (className == null || className.equals(""))
            return false;
        String item[] = { "..", "!", "@", "#", "%", "^", "&", "*", "(", ")", "-", "=", "+", "{", "}", ";", ";", "\"", "'", "<", ">", ",", "?", "/", "`", "~", " ", "\\", "|" };
        for (int i = 0; i <= item.length - 1; i++)
            if (className.indexOf(item[i]) >= 0)
                return false;
        if (className.indexOf(".") == 0)
            return false;
        if (className.indexOf(".", className.length()) == className.length())
            return false;
        //System.out.println("OK");
        return true;
    }
    /**根据类型获取其Return指令。*/
    public static int getReturn(String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return IRETURN;//Byte
        case 'C':
            return IRETURN;//Char
        case 'D':
            return DRETURN;//Double
        case 'F':
            return FRETURN;//Float
        case 'I':
            return IRETURN;//Integer
        case 'J':
            return LRETURN;//Long
        case 'L':
            return ARETURN;//Ref
        case 'S':
            return IRETURN;//Short
        case 'Z':
            return IRETURN;//Boolean
        case '[':
            return ARETURN;//Array
        case 'V':
            return RETURN;//Void
        default:
            throw new RuntimeException("不支持的类型装载请求");//
        }
    }
    /**根据类型获取其Load指令。*/
    public static int getLoad(String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return ILOAD;//Byte
        case 'C':
            return ILOAD;//Char
        case 'D':
            return DLOAD;//Double
        case 'F':
            return FLOAD;//Float
        case 'I':
            return ILOAD;//Integer
        case 'J':
            return LLOAD;//Long
        case 'L':
            return ALOAD;//Ref
        case 'S':
            return ILOAD;//Short
        case 'Z':
            return ILOAD;//Boolean
        case '[':
            return ALOAD;//Array
        default:
            throw new RuntimeException("不支持的类型装载请求");//
        }
    }
    //=======================================================================================================================
    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    public static String toAsmType(Class<?> classType) {
        if (classType == int.class)
            return "I";
        else if (classType == byte.class)
            return "B";
        else if (classType == char.class)
            return "C";
        else if (classType == double.class)
            return "D";
        else if (classType == float.class)
            return "F";
        else if (classType == long.class)
            return "J";
        else if (classType == short.class)
            return "S";
        else if (classType == boolean.class)
            return "Z";
        else if (classType == void.class)
            return "V";
        else if (classType.isArray() == true)
            return "[" + EngineToos.toAsmType(classType.getComponentType());
        else
            return "L" + Type.getInternalName(classType) + ";";
    }
    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    public static String toAsmType(Class<?>[] classType) {
        String returnString = "";
        for (Class<?> c : classType)
            returnString += EngineToos.toAsmType(c);;
        return returnString;
    }
    /** 将IIIILjava/lang/Integer;F形式的ASM类型表述分解为数组。测试字符串IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean; */
    public static String[] splitAsmType(String asmTypes) {
        class AsmTypeRead {
            StringReader sread = null;
            public AsmTypeRead(String sr) {
                this.sread = new StringReader(sr);
            }
            /** 读取到下一个分号为止或者结束为止。*/
            private String readToSemicolon() throws IOException {
                String res = "";
                while (true) {
                    int strInt = sread.read();
                    if (strInt == -1)
                        return res;
                    else if ((char) strInt == ';')
                        return res + ';';
                    else
                        res += (char) strInt;
                }
            }
            /** 读取一个类型 */
            private String readType() throws IOException {
                int strInt = sread.read();
                if (strInt == -1)
                    return "";
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
                    if (s.equals("") == true)
                        break;
                    else
                        ss.add(s);
                }
                String[] res = new String[ss.size()];
                ss.toArray(res);
                return res;
            }
        }
        try {
            return new AsmTypeRead(asmTypes).readTypes();//     IIIILjava/lang/Integer;F[[[Ljava/util/Date;
        } catch (Exception e) {
            throw new FormatException("不合法的ASM类型desc。");
        }
    }
    /***/
    public static String toClassType(String asmType) {
        if (asmType.charAt(0) == 'L')
            return asmType.substring(1, asmType.length() - 1);
        else
            return asmType;
    }
    //=======================================================================================================================
    /**获取一个类对象字节码的读取流。*/
    public static InputStream getClassInputStream(Class<?> type) {
        ClassLoader cl = type.getClassLoader();
        if (cl == null)
            throw new RuntimeException("当前版本无法装载 rt.jar中的类。");
        else
            return cl.getResourceAsStream(type.getName().replace(".", "/") + ".class");
    }
    /**获取类完整限定名的类名部分。*/
    public static String splitSimpleName(String fullName) {
        String[] ns = fullName.split("\\.");
        return ns[ns.length - 1];
    }
    /**获取类完整限定名的包名部分。*/
    public static String splitPackageName(String fullName) {
        if (fullName.lastIndexOf(".") > 0)
            return fullName.substring(0, fullName.lastIndexOf("."));
        else
            return fullName;
    }
    /**获取类完整限定名的包名部分，参数格式是asm格式。*/
    public static String splitPackageNameByASM(String fullName) {
        if (fullName.lastIndexOf("/") > 0)
            return fullName.substring(0, fullName.lastIndexOf("/"));
        else
            return fullName;
    }
    /**获取类完整限定名的类名部分，参数格式是asm格式。*/
    public static String splitSimpleNameByASM(String fullName) {
        String[] ns = fullName.split("/");
        return ns[ns.length - 1];
    }
    /**将类名转换为asm类名。*/
    public static String replaceClassName(String className) {
        return className.replace(".", "/");
    }
    /**根据asm类型获取其ASTORE指令。*/
    public static int getAstore(String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return IASTORE;//Byte
        case 'C':
            return IASTORE;//Char
        case 'D':
            return DASTORE;//Double
        case 'F':
            return FASTORE;//Float
        case 'I':
            return IASTORE;//Integer
        case 'J':
            return LASTORE;//Long
        case 'L':
            return AASTORE;//Ref
        case 'S':
            return IASTORE;//Short
        case 'Z':
            return IASTORE;//Boolean
        case '[':
            return AASTORE;//Array
        default:
            throw new RuntimeException("不支持的类型装载请求");//
        }
    }
    /**通过位运算决定check是否在data里。*/
    public static boolean checkIn(int data, int check) {
        int or = data | check;
        return or == data;
    }
}