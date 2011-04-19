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
package org.more.hypha.xml.support.anno;
import org.more.core.asm.AnnotationVisitor;
import org.more.core.asm.Attribute;
import org.more.core.asm.FieldVisitor;
import org.more.core.classcode.EngineToos;
import org.more.hypha.assembler.anno.AnnoResourcePlugin;
/**
 * 该类负责确定字段级别中是否有必要惊动解析解析类。
 * @version 2010-10-19
 * @author 赵永春 (zyc@byshell.org)
 */
class EV_Field implements FieldVisitor {
    private EV_Mark                        mark    = null;
    private FieldVisitor                   visitor = null;
    private AnnoResourcePlugin plugin  = null;
    //----------
    public EV_Field(AnnoResourcePlugin plugin, EV_Mark mark, FieldVisitor visitor) {
        this.plugin = plugin;
        this.mark = mark;
        this.visitor = visitor;
    }
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        //检查是否存在这个注解的解析器注册
        String annoType = EngineToos.asmTypeToType(desc).replace("/", ".");
        if (this.plugin.containsKeepWatchParser(annoType) == true)
            this.mark.mark(annoType);
        return new EV_Anno(this.plugin, this.mark, this.visitor.visitAnnotation(desc, visible));
    }
    public void visitAttribute(Attribute attr) {
        this.visitor.visitAttribute(attr);
    }
    public void visitEnd() {
        this.visitor.visitEnd();
    }
}