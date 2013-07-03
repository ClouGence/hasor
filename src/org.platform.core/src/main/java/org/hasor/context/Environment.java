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
package org.hasor.context;
/**
 * 负责环境变量相关的处理。
 * @version : 2013-6-19
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Environment {
    public static final String MORE_WORK_HOME  = "MORE_WORK_HOME";
    public static final String MORE_DATA_HOME  = "MORE_DATA_HOME";
    public static final String MORE_TEMP_HOME  = "MORE_TEMP_HOME";
    public static final String MORE_CACHE_HOME = "MORE_CACHE_HOME";
    /**计算字符串，字符串中涵盖了环境变量。*/
    public String evalString(String eval);
}