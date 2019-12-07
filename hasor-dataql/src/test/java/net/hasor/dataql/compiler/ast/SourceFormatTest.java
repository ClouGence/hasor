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
import net.hasor.dataql.runtime.QueryHelper;
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
    public void lambda1_ast_format_test() throws IOException {
        astTest("lambda_1");
    }

    @Test
    public void lambda2_ast_format_test() throws IOException {
        astTest("lambda_2");
    }
}