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
package org.more.hypha.annotation.assembler;
import java.util.ArrayList;
import java.util.List;
import org.more.core.asm.AnnotationVisitor;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.FieldVisitor;
import org.more.core.asm.MethodVisitor;
import org.more.core.classcode.EngineToos;
import org.more.hypha.annotation.AnnotationDefineResourcePlugin;
/**
 * 该类负责确定类级别中是否有必要惊动解析解析类。
 * @version 2010-10-19
 * @author 赵永春 (zyc@byshell.org)
 */
class EV_Class extends ClassAdapter implements EV_Mark {
    private String                         className = null;
    private AnnotationDefineResourcePlugin plugin    = null;
    private boolean                        mark      = false;                  //标记TagListener类是否忽略解析这个类。
    private ArrayList<String>              markList  = new ArrayList<String>();
    /**创建{@link EV_Class}对象。*/
    public EV_Class(AnnotationDefineResourcePlugin plugin, ClassVisitor cv) {
        super(cv);
        this.plugin = plugin;
    }
    public boolean isMark() {
        return this.mark;
    }
    public void mark(String annoType) {
        if (this.markList.contains(annoType) == false)
            this.markList.add(annoType);
        if (this.mark == false)
            this.mark = true;
    }
    public List<String> getAnnos() {
        return this.markList;
    }
    public String getClassName() {
        return this.className;
    }
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
        super.visit(version, access, name, signature, superName, interfaces);
    }
    /**类级别*/
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        String annoType = EngineToos.asmTypeToType(desc).replace("/", ".");
        if (this.plugin.containsKeepWatchParser(annoType) == true)
            this.mark(annoType);
        return new EV_Anno(this.plugin, this, super.visitAnnotation(desc, visible));
    }
    /**字段级别*/
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor visitor = super.visitField(access, name, desc, signature, value);
        return new EV_Field(this.plugin, this, visitor);
    }
    /**方法级别*/
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new EV_Method(this.plugin, this, mv);
    }
}