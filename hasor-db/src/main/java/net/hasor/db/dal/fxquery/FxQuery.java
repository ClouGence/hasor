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
package net.hasor.db.dal.fxquery;
import java.util.List;

/**
 * Query 文本处理器，兼容 #{...}、${...} 两种写法。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public interface FxQuery {
    /** 是否包含替换占位符，如果包含替换占位符那么不能使用批量模式 */
    public boolean isHavePlaceholder();

    public String buildQueryString(Object context);

    public List<Object> buildParameterSource(Object context);

    public <T> T attach(Class<? extends T> attach, T attachValue);

    public <T> T attach(Class<? extends T> attach);
}
