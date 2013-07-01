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
package org.moreframework.context.core;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.util.StringUtils;
import org.moreframework.context.AppContext;
import org.moreframework.context.Environment;
import org.moreframework.context.WorkSpace;
/**
 * 
 * @version : 2013-5-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public abstract class AbstractEnvironment implements Environment {
    @Override
    public String evalString(String evalString) {
        Pattern keyPattern = Pattern.compile("(?:\\{(\\w+)\\}){1,1}");//  (?:\{(\w+)\})
        Matcher keyM = keyPattern.matcher(evalString);
        ArrayList<String> data = new ArrayList<String>();
        while (keyM.find()) {
            String varKey = keyM.group(1);
            String var = System.getProperty(varKey);
            var = StringUtils.isBlank(var) ? System.getenv(varKey) : var;
            var = var == null ? "" : var;
            data.add(new String(var));
        }
        String[] splitArr = keyPattern.split(evalString);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < splitArr.length; i++) {
            sb.append(splitArr[i]);
            if (data.size() > i)
                sb.append(data.get(i));
        }
        return sb.toString().replace("/", File.separator);
    }
    //
    protected void loadEnvironment(AppContext appContext) {
        WorkSpace ws = appContext.getWorkSpace();
        System.setProperty(MORE_WORK_HOME, ws.getWorkDir());
        System.setProperty(MORE_DATA_HOME, ws.getDataDir());
        System.setProperty(MORE_TEMP_HOME, ws.getTempDir());
        System.setProperty(MORE_CACHE_HOME, ws.getCacheDir());
    }
}