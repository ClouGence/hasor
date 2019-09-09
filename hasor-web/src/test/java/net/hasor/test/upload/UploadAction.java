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
package net.hasor.test.upload;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.web.FileItem;
import net.hasor.web.WebController;
import net.hasor.web.annotation.Any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadAction extends WebController {
    @Any
    public Map<String, String> execute() throws IOException, NoSuchAlgorithmException {
        Map<String, String> hashData = new HashMap<>();
        //
        List<FileItem> fileItems = getMultipartList();
        for (FileItem fileItem : fileItems) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fileItem.writeTo(byteArrayOutputStream);
            hashData.put(fileItem.getFieldName(), CommonCodeUtils.MD5.encodeMD5(byteArrayOutputStream.toByteArray()));
        }
        //
        return hashData;
    }
}
