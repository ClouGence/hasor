/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.jdbc.lambda;
import java.util.function.Consumer;

/**
 * 动态拼条件，专门处理 and 和 or 的嵌套查询条件。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Nested<I, R> {
    /** 括号方式嵌套一组查询条件，与现有条件为并且关系。类似：'and ( ...where... )' */
    public R and(Consumer<I> lambda);

    /** 括号方式嵌套一组查询条件，与现有条件为或关系。类似：'or ( ...where... )' */
    public R or(Consumer<I> lambda);
}