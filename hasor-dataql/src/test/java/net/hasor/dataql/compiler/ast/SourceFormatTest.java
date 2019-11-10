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
package net.hasor.dataql.compiler.ast;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.compiler.QueryHelper;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * 测试用例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public class SourceFormatTest extends AbstractTestResource {
    private void astTest(String testCase) throws IOException {
        String query1 = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.ql");
        QueryModel queryModel1 = QueryHelper.queryParser(query1);
        String q1 = queryModel1.buildToString();
        List<String> list1 = acceptVisitor(queryModel1);
        String visitor1 = StringUtils.join(list1.toArray(), "\n");
        //
        String query2 = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.format");
        QueryModel queryModel2 = QueryHelper.queryParser(query1);
        String q2 = queryModel2.buildToString();
        List<String> list2 = acceptVisitor(queryModel1);
        String visitor2 = StringUtils.join(list2.toArray(), "\n");
        //
        assert !query1.equals(query2);
        assert q1.equals(q2);
        //
        String basicVisitor = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.visitor");
        assert visitor1.trim().equals(basicVisitor.trim());
        assert visitor2.trim().equals(basicVisitor.trim());
    }

    @Test
    public void option1_ast_format_test() throws IOException {
        astTest("option_1");
    }

    @Test
    public void option2_ast_format_test() throws IOException {
        astTest("option_2");
    }

    @Test
    public void import_ast_format_test() throws IOException {
        astTest("import_1");
    }

    @Test
    public void basictype1_ast_format_test() throws IOException {
        astTest("basictype_1");
    }

    @Test
    public void return1_ast_format_test() throws IOException {
        astTest("return_1");
    }

    @Test
    public void exit1_ast_format_test() throws IOException {
        astTest("exit_1");
    }

    @Test
    public void throw1_ast_format_test() throws IOException {
        astTest("throw_1");
    }

    @Test
    public void switch1_ast_format_test() throws IOException {
        astTest("switch_1");
    }

    @Test
    public void switch2_ast_format_test() throws IOException {
        astTest("switch_2");
    }

    @Test
    public void switch3_ast_format_test() throws IOException {
        astTest("switch_3");
    }

    @Test
    public void expr1_ast_format_test() throws IOException {
        astTest("expr_1");
    }
    //    InstSet
    //            SwitchInst
    //    @Test
    //    public void ql_basic_test() throws Throwable {
    //        for (String script : basicQl) {
    //            QueryHelper.queryParser(getScript(script));
    //        }
    //    }
    //
    //    @Test
    //    public void ql_eval_test() throws Throwable {
    //        for (String script : evalQl) {
    //            QueryHelper.queryParser(getScript(script));
    //        }
    //    }
    //
    //    @Test
    //    public void ql_import_test() throws Throwable {
    //        for (String script : importQl) {
    //            QueryHelper.queryParser(getScript(script));
    //        }
    //    }
    //
    //    @Test
    //    public void ql_lambda_test() throws Throwable {
    //        for (String script : lambdaQl) {
    //            QueryHelper.queryParser(getScript(script));
    //        }
    //    }
}