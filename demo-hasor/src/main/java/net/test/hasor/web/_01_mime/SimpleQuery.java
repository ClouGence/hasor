/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.hasor.web._01_mime;
import net.hasor.core.Hasor;
import net.hasor.plugins.mimetype.MimeType;
import net.hasor.plugins.mimetype.MimeTypeModule;
import org.junit.Test;
/**
 * 
 * @version : 2015年1月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class SimpleQuery {
    @Test
    public void queryMime() throws Throwable {
        MimeType mimeType = Hasor.createAppContext(new MimeTypeModule()).getInstance(MimeType.class);
        String suffix = "";
        //
        suffix = "rar";
        System.out.println(suffix + " :" + mimeType.getMimeType(suffix));
        suffix = "exe";
        System.out.println(suffix + " :" + mimeType.getMimeType(suffix));
        suffix = "zip";
        System.out.println(suffix + " :" + mimeType.getMimeType(suffix));
        suffix = "jpeg";
        System.out.println(suffix + " :" + mimeType.getMimeType(suffix));
    }
}