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
package net.hasor.dataql.compiler.ast.value;
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.ast.*;
import net.hasor.dataql.compiler.ast.CodeLocation.CodeLocationInfo;
import net.hasor.dataql.compiler.ast.token.StringToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * var指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class FragmentVariable extends CodeLocationInfo implements Inst, Variable {
    private final StringToken       fragmentName;
    private final List<StringToken> paramList = new ArrayList<>();
    private final StringToken       fragmentString;
    private final boolean           batchMode;

    public FragmentVariable(StringToken fragmentName, StringToken fragmentString, boolean batchMode) {
        this.fragmentName = fragmentName;
        this.fragmentString = fragmentString;
        this.batchMode = batchMode;
    }

    public StringToken getFragmentName() {
        return fragmentName;
    }

    public StringToken getFragmentString() {
        return fragmentString;
    }

    public List<StringToken> getParamList() {
        return paramList;
    }

    public boolean isBatchMode() {
        return this.batchMode;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        writer.write("@@" + this.fragmentName.getValue());
        if (batchMode) {
            writer.write("[]");
        }
        writer.write("(");
        for (int i = 0; i < this.paramList.size(); i++) {
            if (i == 0) {
                writer.write(this.paramList.get(i).getValue());
            } else {
                writer.write("," + this.paramList.get(i).getValue());
            }
        }
        writer.write(") <%" + this.fragmentString.getValue() + "%>");
    }
}