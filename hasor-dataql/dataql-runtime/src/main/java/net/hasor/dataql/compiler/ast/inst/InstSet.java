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
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.ast.InstVisitorContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 指令序列
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class InstSet extends ArrayList<Inst> implements Inst {
    private List<HintInst> optionSet = new ArrayList<>();
    private boolean        multipleInst;

    public InstSet(boolean multipleInst) {
        this.multipleInst = multipleInst;
    }

    /** 添加选项 */
    public void addOptionInst(HintInst inst) {
        this.optionSet.add(Objects.requireNonNull(inst, "option inst npe."));
    }

    public List<HintInst> getOptionSet() {
        return optionSet;
    }

    public void setMultipleInst(boolean multipleInst) {
        this.multipleInst = multipleInst;
    }

    public boolean isMultipleInst() {
        return this.multipleInst;
    }

    /** 批量添加指令集 */
    public void addInstSet(InstSet inst) {
        this.addAll(inst);
    }

    /** 添加一条指令 */
    public void addInst(Inst inst) {
        if (inst != null) {
            this.add(inst);
        }
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                for (HintInst inst : getOptionSet()) {
                    inst.accept(astVisitor);
                }
                for (Inst inst : InstSet.this) {
                    inst.accept(astVisitor);
                }
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        int size = this.size();
        if (this.getOptionSet().isEmpty() && size <= 1 && !this.isMultipleInst()) {
            if (size == 0) {
                return;
            } else {
                Inst inst = this.get(0);
                inst.doFormat(depth, formatOption, writer);
                return;
            }
        }
        writer.write("{\n");
        for (HintInst opt : this.getOptionSet()) {
            opt.doFormat(depth + 1, formatOption, writer);
        }
        if (!this.getOptionSet().isEmpty()) {
            writer.write("\n");
        }
        for (int i = 0; i < this.size(); i++) {
            Inst inst = this.get(i);
            inst.doFormat(depth + 1, formatOption, writer);
            if (inst instanceof InstSet) {
                writer.write("\n");
            }
        }
        writer.write("}");
    }
}