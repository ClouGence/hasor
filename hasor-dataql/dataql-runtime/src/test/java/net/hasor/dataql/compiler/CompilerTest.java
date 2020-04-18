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
package net.hasor.dataql.compiler;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Finder;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * 测试用例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public class CompilerTest extends AbstractTestResource {
    private void qilTest(String testCase) throws IOException {
        String query1 = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.ql");
        QueryModel queryModel = QueryHelper.queryParser(query1);
        QIL qil = QueryHelper.queryCompiler(queryModel, null, Finder.DEFAULT);
        //
        String qilString1 = qil.toString();
        String qilString2 = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.qil");
        assert qilString1.trim().equals(qilString2.trim());
    }

    private void astTest(String testCase) throws IOException {
        String query1 = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.ql");
        QueryModel queryModel1 = QueryHelper.queryParser(query1);
        String q11 = queryModel1.toQueryString();
        String q12 = QueryHelper.queryParser(q11).toQueryString();
        List<String> list1 = acceptVisitor(queryModel1);
        String visitor1 = StringUtils.join(list1.toArray(), "\n");
        //
        String query2 = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.format");
        //
        assert q11.equals(q12);                     // 原始QL 格式化之后再次 parser 应该和第一次 parser 相同。
        assert q11.trim().equals(query2.trim());    // 格式化之后和 预先格式化的 .format 应该相等
        assert !query1.trim().equals(query2.trim());// 格式化前后不相等
        //
        String basicVisitor = getScript("/net_hasor_dataql_ast/" + testCase + "/ast.visitor");
        assert visitor1.trim().equals(basicVisitor.trim());
        //
        qilTest(testCase);
    }

    @Test
    public void hint1_ast_format_test() throws IOException {
        astTest("hint_1");
    }

    @Test
    public void hint2_ast_format_test() throws IOException {
        astTest("hint_2");
    }

    @Test
    public void hint3_ast_format_test() throws IOException {
        astTest("hint_3");
    }

    @Test
    public void import1_ast_format_test() throws IOException {
        astTest("import_1");
    }

    @Test
    public void import2_ast_format_test() throws IOException {
        astTest("import_2");
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
    public void return2_ast_format_test() throws IOException {
        astTest("return_2");
    }

    @Test
    public void exit1_ast_format_test() throws IOException {
        astTest("exit_1");
    }

    @Test
    public void exit2_ast_format_test() throws IOException {
        astTest("exit_2");
    }

    @Test
    public void throw1_ast_format_test() throws IOException {
        astTest("throw_1");
    }

    @Test
    public void throw2_ast_format_test() throws IOException {
        astTest("throw_2");
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
    public void switch4_ast_format_test() throws IOException {
        astTest("switch_4");
    }

    @Test
    public void switch5_ast_format_test() throws IOException {
        astTest("switch_5");
    }

    @Test
    public void expr1_ast_format_test() throws IOException {
        astTest("expr_1");
    }

    @Test
    public void expr2_ast_format_test() throws IOException {
        astTest("expr_2");
    }

    @Test
    public void object1_ast_format_test() throws IOException {
        astTest("val_object_1");
    }

    @Test
    public void object2_ast_format_test() throws IOException {
        astTest("val_object_2");
    }

    @Test
    public void object3_ast_format_test() throws IOException {
        astTest("val_object_3");
    }

    @Test
    public void object4_ast_format_test() throws IOException {
        astTest("val_object_4");
    }

    @Test
    public void fmt1_ast_format_test() throws IOException {
        astTest("fmt_1");
    }

    @Test
    public void fmt2_ast_format_test() throws IOException {
        astTest("fmt_2");
    }

    @Test
    public void fmt3_ast_format_test() throws IOException {
        astTest("fmt_3");
    }

    @Test
    public void fmt4_ast_format_test() throws IOException {
        astTest("fmt_4");
    }

    @Test
    public void fmt5_ast_format_test() throws IOException {
        astTest("fmt_5");
    }

    @Test
    public void fmt6_ast_format_test() throws IOException {
        astTest("fmt_6");
    }

    @Test
    public void fmt7_ast_format_test() throws IOException {
        astTest("fmt_7");
    }

    @Test
    public void fmt8_ast_format_test() throws IOException {
        astTest("fmt_8");
    }

    @Test
    public void route1_ast_format_test() throws IOException {
        astTest("route_1");
    }

    @Test
    public void route2_ast_format_test() throws IOException {
        astTest("route_2");
    }

    @Test
    public void route3_ast_format_test() throws IOException {
        astTest("route_3");
    }

    @Test
    public void route4_ast_format_test() throws IOException {
        astTest("route_4");
    }

    @Test
    public void route5_ast_format_test() throws IOException {
        astTest("route_5");
    }

    @Test
    public void run1_ast_format_test() throws IOException {
        astTest("run_1");
    }

    @Test
    public void lambda1_ast_format_test() throws IOException {
        astTest("lambda_1");
    }

    @Test
    public void lambda2_ast_format_test() throws IOException {
        astTest("lambda_2");
    }

    @Test
    public void fragment1_ast_format_test() throws IOException {
        astTest("fragment_1");
    }

    @Test
    public void fragment2_ast_format_test() throws IOException {
        astTest("fragment_2");
    }

    @Test
    public void error_ast_format_test() throws IOException {
        try {
            QueryHelper.queryParser("return [1,2,3,4,5,6] => [ # ]"); // 不支持的语法
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
}