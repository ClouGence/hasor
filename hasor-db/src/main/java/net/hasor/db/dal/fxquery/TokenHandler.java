/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.hasor.db.dal.fxquery;
/**
 * 原版在 mybatis 中同名类，本类接口增加了2个参数。
 * @author Clinton Begin
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-12
 */
interface TokenHandler {
    public String handleToken(StringBuilder hasFound, String openToken, String content);
}
