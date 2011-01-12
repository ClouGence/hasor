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
import org.more.core.asm.AnnotationVisitor;
import org.more.core.classcode.EngineToos;
import org.more.hypha.annotation.AnnoResourceExpand;
/**
 * 该类负责确定注解级别中是否有必要惊动解析解析类。
 * @version 2010-10-19
 * @author 赵永春 (zyc@byshell.org)
 */
class EV_Anno implements AnnotationVisitor {
    private AnnoResourceExpand plugin  = null;
    private EV_Mark                        mark    = null;
    private AnnotationVisitor              visitor = null;
    //----------
    public EV_Anno(AnnoResourceExpand plugin, EV_Mark mark, AnnotationVisitor visitor) {
        this.plugin = plugin;
        this.mark = mark;
        this.visitor = visitor;
    }
    public void visitEnd() {
        this.visitor.visitEnd();
    }
    public void visit(String name, Object value) {
        this.visitor.visit(name, value);
    }
    public void visitEnum(String name, String desc, String value) {
        this.visitor.visitEnum(name, desc, value);
    }
    /**嵌套注解类型值*/
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        String annoType = EngineToos.asmTypeToType(desc).replace("/", ".");
        if (this.plugin.containsKeepWatchParser(annoType) == true)
            this.mark.mark(annoType);
        AnnotationVisitor av = this.visitor.visitAnnotation(name, desc);
        return new EV_Anno(this.plugin, this.mark, av);
    }
    /**数组类型值*/
    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor av = this.visitor.visitArray(name);
        return new EV_Anno(this.plugin, this.mark, av);
    }
}