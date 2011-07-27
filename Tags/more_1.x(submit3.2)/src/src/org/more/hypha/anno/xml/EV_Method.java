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
package org.more.hypha.anno.xml;
import org.more.core.asm.AnnotationVisitor;
import org.more.core.asm.Attribute;
import org.more.core.asm.MethodAdapter;
import org.more.core.asm.MethodVisitor;
import org.more.core.classcode.EngineToos;
import org.more.hypha.anno.AnnoService;
/**
 * 该类负责确定方法级别中是否有必要惊动解析解析类。
 * @version 2010-10-19
 * @author 赵永春 (zyc@byshell.org)
 */
class EV_Method extends MethodAdapter {
    private AnnoService plugin = null;
    private EV_Mark      mark   = null;
    //----------
    public EV_Method(AnnoService plugin, EV_Mark mark, MethodVisitor mv) {
        super(mv);
        this.plugin = plugin;
        this.mark = mark;
    }
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        //检查是否存在这个注解的解析器注册
        String annoType = EngineToos.asmTypeToType(desc).replace("/", ".");
        if (this.plugin.containsKeepWatchParser(annoType) == true)
            this.mark.mark(annoType);
        return new EV_Anno(this.plugin, this.mark, super.visitAnnotation(desc, visible));
    }
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        //检查是否存在这个注解的解析器注册
        String annoType = EngineToos.asmTypeToType(desc).replace("/", ".");
        if (this.plugin.containsKeepWatchParser(annoType) == true)
            this.mark.mark(annoType);
        return new EV_Anno(this.plugin, this.mark, super.visitParameterAnnotation(parameter, desc, visible));
    }
    public void visitAttribute(Attribute attr) {
        super.visitAttribute(attr);
    }
    public void visitEnd() {
        super.visitEnd();
    }
}