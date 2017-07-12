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
package net.test.hasor.web;
import net.hasor.web.mime.MimeTypeSupplier;
import org.junit.Test;

import java.io.IOException;
/**
 * S
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class MimeTypeTest {
    @Test
    public void mimeTypeTest() throws IOException {
        MimeTypeSupplier mimeTypeContext = new MimeTypeSupplier(null);
        mimeTypeContext.loadStream("/META-INF/mime.types.xml");
        mimeTypeContext.loadStream("mime.types.xml");
        //
        String htmlType = mimeTypeContext.get("html");
        assert "text/html".equalsIgnoreCase(htmlType);
    }
}
