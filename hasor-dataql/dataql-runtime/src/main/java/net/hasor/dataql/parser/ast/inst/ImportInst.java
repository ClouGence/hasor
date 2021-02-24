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
package net.hasor.dataql.parser.ast.inst;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.ast.AstVisitor;
import net.hasor.dataql.parser.ast.FormatWriter;
import net.hasor.dataql.parser.ast.Inst;
import net.hasor.dataql.parser.ast.InstVisitorContext;
import net.hasor.dataql.parser.ast.token.StringToken;
import net.hasor.dataql.parser.location.BlockLocation;

import java.io.IOException;

/**
 * import 语法
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ImportInst extends BlockLocation implements Inst {
    public enum ImportType {
        Resource,
        ClassType
    }

    private final ImportType  importType;
    private final StringToken importName;
    private final StringToken asName;

    public ImportInst(ImportType importType, StringToken importName, StringToken asName) {
        this.importType = importType;
        this.importName = importName;
        this.asName = asName;
    }

    public ImportType getImportType() {
        return importType;
    }

    public StringToken getImportName() {
        return importName;
    }

    public StringToken getAsName() {
        return asName;
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
        writer.write("import ");
        if (this.importType == ImportType.Resource) {
            writer.write("@");
        } else if (this.importType == ImportType.ClassType) {
            //
        }
        writer.write('"' + this.importName.getValue() + '"');
        writer.write(" as " + this.asName.getValue() + ";\n");
    }
}
