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
package net.hasor.web.upload;
import net.hasor.test.web.upload.FileItemStreamUploadAction;
import net.hasor.test.web.upload.FileItemUploadAction;
import net.hasor.web.WebApiBinder;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class FileUploadTest extends AbstractFileUploadTest {
    @Test
    public void basic_test_1() throws Exception {
        this.doUploadTest("/fileupload.do", apiBinder -> {
            //
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/fileupload.do").with(FileItemUploadAction.class);
        }, (oriData, result) -> {
            //
            ((Map<String, String>) result).forEach((key, hash) -> {
                System.out.println("fileupload key -> " + key);
                Object val = oriData.get(key);
                if (val instanceof byte[]) {
                    assert md5((byte[]) val).equals(hash);
                } else if (val instanceof File) {
                    //
                } else {
                    assert md5(val.toString()).equals(hash);
                }
            });
        });
    }

    @Test
    public void basic_test_2() throws Exception {
        this.doUploadTest("/fileupload.do", apiBinder -> {
            //
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/fileupload.do").with(FileItemStreamUploadAction.class);
        }, (oriData, result) -> {
            //
            ((Map<String, String>) result).forEach((key, hash) -> {
                System.out.println("fileupload key -> " + key);
                Object val = oriData.get(key);
                if (val instanceof byte[]) {
                    assert md5((byte[]) val).equals(hash);
                } else if (val instanceof File) {
                    //
                } else {
                    assert md5(val.toString()).equals(hash);
                }
            });
        });
    }
}
