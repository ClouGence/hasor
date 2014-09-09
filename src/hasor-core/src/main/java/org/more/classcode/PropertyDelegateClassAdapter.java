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
import org.more.asm.ClassVisitor;
import org.more.asm.Opcodes;
/**
 * 该类负责输出代理属性。
 * @version : 2014年9月8日
 * @author 赵永春(zyc@hasor.net)
 */
class PropertyDelegateClassAdapter extends ClassVisitor implements Opcodes {
    public PropertyDelegateClassAdapter(ClassVisitor cv, ClassConfig cc) {
        super(ASM4, cv);
    }
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        // TODO Auto-generated method stub
        super.visit(version, access, name, signature, superName, interfaces);
    }
    public void visitEnd() {
        // TODO Auto-generated method stub
        super.visitEnd();
    }
}