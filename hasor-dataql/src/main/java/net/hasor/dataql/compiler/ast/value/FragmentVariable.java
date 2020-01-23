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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * var指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class FragmentVariable implements Inst, Variable {
    private String       fragmentName;
    private List<String> paramList = new ArrayList<>();
    private String       fragmentString;

    public FragmentVariable(String fragmentName, String fragmentString) {
        this.fragmentName = fragmentName;
        this.fragmentString = fragmentString;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public String getFragmentString() {
        return fragmentString;
    }

    public List<String> getParamList() {
        return paramList;
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
        writer.write("@@" + this.fragmentName);
        writer.write("(");
        for (int i = 0; i < this.paramList.size(); i++) {
            if (i == 0) {
                writer.write(this.paramList.get(i));
            } else {
                writer.write("," + this.paramList.get(i));
            }
        }
        writer.write(") <%" + this.fragmentString + "%>");
    }
}