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
package org.more.util;
import java.io.File;
import java.io.InputStream;
/**
 * 资源路径工具类。
 * @version 2011-6-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class ResourcesUtil {
    public static InputStream getResourceStream(String resourcePath) {
        // TODO Auto-generated method stub
        return null;
    }
    public static File getResourceFile(String resourcePath) {
        if (resourcePath == null)
            return null;
        String sw = "classpath:";
        File fileObject = null;
        if (resourcePath.startsWith(sw) == true) {
            String file = resourcePath.substring(sw.length());
            //表示选择classpath目录下的。 
            fileObject = new File(ClassPathUtil.CLASS_PATH_Strings.get(0), file);
        } else
            fileObject = new File(resourcePath);
        return fileObject;
    }
}