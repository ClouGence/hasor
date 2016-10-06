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
package net.demo.hasor.web.actions;
import net.hasor.restful.WebController;
import net.hasor.restful.api.MappingTo;
import net.hasor.web.FileItem;

import java.io.File;
import java.io.IOException;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/fileupload.do")
public class FileupLoad extends WebController {
    public void execute() throws IOException {
        //
        // 方式1: - 使用默认缓存目录
        FileItem multipart = this.getOneMultipart("upfile");
        multipart.writeTo(new File(""));
        multipart.deleteOrSkip();
        //
        // 方式2: - 使用自定义缓存目录
        String cacheDirectory = "...";
        Integer maxPostSize = 1024 * 1024;
        FileItem multipart1 = this.getOneMultipart("upfile", cacheDirectory, maxPostSize);
    }
}