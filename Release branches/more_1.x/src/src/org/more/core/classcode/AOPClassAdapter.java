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
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.MethodVisitor;
import org.more.core.asm.Opcodes;
/**
 * 完成AOP代理的字节码改写对象。
 * Date : 2009-10-30
 * @author 赵永春
 */
class AOPClassAdapter extends ClassAdapter implements Opcodes {
    public AOPClassAdapter(ClassVisitor cv) {
        super(cv);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}