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
package net.hasor.dataql.fx;
import net.hasor.core.Settings;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.parser.QueryModel;
import net.hasor.dataql.parser.ast.AstVisitor;
import net.hasor.dataql.parser.ast.InstVisitorContext;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试用例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public class AbstractTestResource {
    protected static Logger logger = LoggerFactory.getLogger(AbstractTestResource.class);

    protected String getScript(String queryResource) throws IOException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(queryResource);
        if (inStream == null) {
            return "";
        }
        // .获取 DataQL 查询字符串
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        logger.info("resource = " + queryResource);
        InputStreamReader reader = new InputStreamReader(inStream, Charset.forName(Settings.DefaultCharset));
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(reader, outWriter);
        String buildQuery = outWriter.toString();
        logger.info("\n" + buildQuery);
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return buildQuery.replace("\r\n", "\n");
    }

    protected Query compilerQL(String qlString) throws IOException {
        return QueryHelper.createQuery(qlString, Finder.DEFAULT);
    }

    protected Query compilerQL(String qlString, Finder finder) throws IOException {
        return QueryHelper.createQuery(qlString, Finder.DEFAULT);
    }

    protected List<String> acceptVisitor(QueryModel queryModel) {
        List<String> astVisitor = new ArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        queryModel.accept(new AstVisitor() {
            @Override
            public void visitInst(InstVisitorContext inst) {
                String fixedString = StringUtils.repeat(' ', atomicInteger.get() * 4);
                String dataIn = "IN - " + inst.getInst().getClass().getSimpleName();
                String dataOut = "OUT - " + inst.getInst().getClass().getSimpleName();
                //
                astVisitor.add(fixedString + dataIn);
                atomicInteger.incrementAndGet();
                inst.visitChildren(this);
                atomicInteger.decrementAndGet();
                astVisitor.add(fixedString + dataOut);
            }
        });
        return astVisitor;
    }
}
