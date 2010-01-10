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
package org.more.beans.resource.annotation;
/**
 * classpath扫描工具，在扫描过程中需要通过该接口确定排除哪些不需要的资源。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PackageUtilExclude {
    /**
     * 确定是否排除这个资源，如果排除返回true否则返回false。
     * @param name 查找到的资源名称，如果是目录则是目录名。
     * @param isFile 用于确定找到的资源是否是一个文件。
     */
    public boolean exclude(String name, boolean isFile);
}