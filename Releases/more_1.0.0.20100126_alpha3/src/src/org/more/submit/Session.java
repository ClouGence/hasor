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
package org.more.submit;
import java.io.Serializable;
import org.more.util.attribute.IAttribute;
/**
 * 使用Session接口保存数据可以保证当下一个Action请求到达时仍然可以访问到存放到Session中的数据。
 * @version 2009-11-27
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Session extends IAttribute, Serializable {
    /**
     * 获取Session在SessionManager中的唯一标识号。
     * @return 返回Session在SessionManager中的唯一标识号。
     */
    public String getSessionID();
    /**
     * 获得创建这个Session时的创建时间。
     * @return 返回创建这个Session时的时间。
     */
    public long getCreateTime();
}