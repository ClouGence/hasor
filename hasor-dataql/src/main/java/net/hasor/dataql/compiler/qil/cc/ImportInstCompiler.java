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
package net.hasor.dataql.compiler.qil.cc;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.dataql.compiler.ast.inst.ImportInst;
import net.hasor.dataql.compiler.ast.inst.ImportInst.ImportType;
import net.hasor.dataql.compiler.ast.inst.RootBlockSet;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.utils.ExceptionUtils;

import java.io.InputStream;
import java.util.Objects;

/**
 * import 语法
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ImportInstCompiler implements InstCompiler<ImportInst> {
    @Override
    public void doCompiler(ImportInst astInst, InstQueue queue, CompilerContext compilerContext) {
        String asName = astInst.getAsName();
        String importResource = astInst.getImportName();
        ImportType importType = astInst.getImportType();
        //
        // .导入操作
        if (importType == ImportType.Resource) {
            int importAddress = compilerContext.findImport(importResource);
            if (importAddress < 0) {
                InstQueue newMethodInst = queue.newMethodInst();
                newMethodInst.inst(M_STAR, 0);
                compilerContext.putImport(importResource, newMethodInst.getName());
                this.loadResource(importResource, newMethodInst, compilerContext.createSegregate());
                importAddress = newMethodInst.getName();
            }
            queue.inst(M_REF, importAddress);
            //
        } else if (importType == ImportType.ClassType) {
            queue.inst(M_TYP, importResource);
        } else {
            throw new RuntimeException("import compiler failed -> importType undefined");
        }
        //
        // .导入对象保存到堆
        int index = compilerContext.containsWithCurrent(asName);
        if (index >= 0) {
            throw new RuntimeException("import '" + asName + "' is defined.");
        }
        index = compilerContext.push(asName);
        queue.inst(STORE, index);
    }

    private void loadResource(String importName, InstQueue queue, CompilerContext compilerContext) {
        // .parser资源
        RootBlockSet queryModel = null;
        try {
            InputStream inputStream = Objects.requireNonNull(compilerContext.findResource(importName), "import resource '" + importName + "' not found.");
            queryModel = (RootBlockSet) QueryHelper.queryParser(inputStream);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("import compiler failed -> parser failed.", throwable));
        }
        // 编译资源
        compilerContext.findInstCompilerByInst(queryModel).doCompiler(queue);
    }
}