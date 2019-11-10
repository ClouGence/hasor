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
package net.hasor.dataql.compiler.ast.inst;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.ast.InstVisitorContext;
import net.hasor.dataql.runtime.OptionSet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 指令序列
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public class RootBlockSet extends InstSet implements QueryModel {
    private List<OptionInst> optionSet = new ArrayList<>();
    private List<ImportInst> importSet = new ArrayList<>();

    /** 添加选项 */
    public void addOptionInst(OptionInst inst) {
        this.optionSet.add(Objects.requireNonNull(inst, "option inst npe."));
    }

    /** 添加导入 */
    public void addImportInst(ImportInst inst) {
        this.importSet.add(Objects.requireNonNull(inst, "import inst npe."));
    }

    public List<OptionInst> getOptionSet() {
        return optionSet;
    }

    public List<ImportInst> getImportSet() {
        return importSet;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                for (OptionInst inst : optionSet) {
                    inst.accept(astVisitor);
                }
                for (ImportInst inst : importSet) {
                    inst.accept(astVisitor);
                }
                for (Inst inst : RootBlockSet.this) {
                    inst.accept(astVisitor);
                }
            }
        });
    }

    @Override
    public void buildToString(OptionSet formatOptions, Writer writer) throws IOException {
        formatOptions = (formatOptions == null) ? new OptionSet() : formatOptions;
        //
        for (OptionInst opt : this.optionSet) {
            opt.doFormat(0, formatOptions, new FormatWriter(writer));
        }
        for (ImportInst opt : this.importSet) {
            opt.doFormat(0, formatOptions, new FormatWriter(writer));
        }
        writer.write("\n");
        super.doFormat(0, formatOptions, new FormatWriter(writer));
        writer.flush();
    }
}