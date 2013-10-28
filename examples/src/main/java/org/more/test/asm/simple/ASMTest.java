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
package org.more.test.asm.simple;
import java.io.InputStream;
import org.more.asm.ClassReader;
import org.more.asm.ClassVisitor;
import org.more.asm.ClassWriter;
import org.more.asm.MethodVisitor;
import org.more.asm.Opcodes;
/**
 * 
 * @version : 2013-9-20
 * @author 赵永春 (zyc@byshell.org)
 */
public class ASMTest implements Opcodes {
    public static void main(String[] args) throws Exception {
        AopClassLoader aopLoader = new AopClassLoader(Thread.currentThread().getContextClassLoader());
        Class<?> testBean = aopLoader.loadClass("org.more.test.asm.simple.TestBean_Tmp");
        TestBean bean = (TestBean) testBean.newInstance();
        bean.halloAop();
    }
}
class AopClassLoader extends ClassLoader implements Opcodes {
    public AopClassLoader(ClassLoader parent) {
        super(parent);
    }
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (!name.contains("TestBean_Tmp"))
            return super.loadClass(name);
        try {
            ClassWriter cw = new ClassWriter(0);
            //
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/more/test/asm/simple/TestBean.class");
            ClassReader reader = new ClassReader(is);
            reader.accept(new AopClassAdapter(ASM4, cw), ClassReader.SKIP_DEBUG);
            //
            byte[] code = cw.toByteArray();
            //            FileOutputStream fos = new FileOutputStream("c:\\TestBean_Tmp.class");
            //            fos.write(code);
            //            fos.flush();
            //            fos.close();
            return this.defineClass(name, code, 0, code.length);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }
}
class AopClassAdapter extends ClassVisitor implements Opcodes {
    public AopClassAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        //更改类名，并使新类继承原有的类。
        // 
        super.visit(version, access, name + "_Tmp", signature, name, interfaces);
        {
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, name, "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name))
            return null;
        if (!name.equals("halloAop"))
            return null;
        //
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new AopMethod(this.api, mv);
    }
}
class AopMethod extends MethodVisitor implements Opcodes {
    public AopMethod(int api, MethodVisitor mv) {
        super(api, mv);
    }
    public void visitCode() {
        super.visitCode();
        this.visitMethodInsn(INVOKESTATIC, "org/more/test/asm/simple/AopInterceptor", "beforeInvoke", "()V");
    }
    public void visitInsn(int opcode) {
        if (opcode == RETURN) {
            mv.visitMethodInsn(INVOKESTATIC, "org/more/test/asm/simple/AopInterceptor", "afterInvoke", "()V");
        }
        super.visitInsn(opcode);
    }
}