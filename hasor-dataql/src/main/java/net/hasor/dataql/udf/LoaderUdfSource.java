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
package net.hasor.dataql.udf;
import net.hasor.dataql.*;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.runtime.QueryEngineImpl;
import net.hasor.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
/**
 * 用于加载另一个 DataQL 并将其包装为 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-10-18
 */
public class LoaderUdfSource extends SimpleUdfSource {
    private ClassLoader classLoader;
    public LoaderUdfSource() {
    }
    public LoaderUdfSource(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    @Override
    public UDF findUdf(String udfName, QueryEngine sourceEngine) throws Throwable {
        //
        UDF target = super.get(udfName);
        if (target != null) {
            return target;
        }
        //
        InputStream asStream = null;
        if (udfName.contains("://")) {
            asStream = ResourcesUtils.getResourceAsStream(new URL(udfName));
        } else {
            asStream = ResourcesUtils.getResourceAsStream(udfName);
        }
        if (asStream == null) {
            return null;
        }
        //
        InputStreamReader reader = new InputStreamReader(asStream, Charset.forName("UTF-8"));
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(reader, outWriter);
        QIL compilerQuery = QueryCompiler.compilerQuery(outWriter.toString());
        final QueryEngineImpl engine = new QueryEngineImpl(sourceEngine.getUdfManager(), compilerQuery);
        if (this.classLoader != null) {
            engine.setClassLoader(this.classLoader);
        }
        //
        target = new UDF() {
            public Object call(Object[] values, Option readOnly) throws Throwable {
                Query query = engine.newQuery();
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        query.addParameter("param" + i, values[i]);
                    }
                }
                QueryResult execute = query.execute();
                return execute.getData();
            }
        };
        //
        super.put(udfName, target);
        return target;
    }
}