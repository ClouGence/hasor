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
package net.test.aliyun.z7;
/**
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式并保存到 oss2里,同时将压缩包中的文件目录保存到数据库中。
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
/* */
public abstract class AbstractTask {
    //
    public abstract void markError(Throwable errorMsg);
    // 
    public abstract void doWork() throws Throwable;
}