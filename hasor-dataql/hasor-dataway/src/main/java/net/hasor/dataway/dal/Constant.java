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
package net.hasor.dataway.dal;
import java.util.function.Function;

/**
 * 内部常量
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-08
 */
public interface Constant {
    public final Function<String, String> ReqBodySchemaPrefix = s -> "ReqBodyType_" + s + "_";
    public final Function<String, String> ReqHeadSchemaPrefix = s -> "ReqHeadType_" + s + "_";
    public final Function<String, String> ResBodySchemaPrefix = s -> "ResBodyType_" + s + "_";
    public final Function<String, String> ResHeadSchemaPrefix = s -> "ResHeadType_" + s + "_";
}