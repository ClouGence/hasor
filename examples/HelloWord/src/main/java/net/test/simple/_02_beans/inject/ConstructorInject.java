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
package net.test.simple._02_beans.inject;
import javax.inject.Inject;
import net.test.simple._02_beans.pojo.PojoBean;
/**
* 该例子演示了如何通过 JSR-330 标准进行构造方法依赖注入（构造函数注入）。
* @version : 2014-1-3
* @author 赵永春(zyc@hasor.net)
*/
public class ConstructorInject {
    private PojoBean userBean; //被注入的类型
    //依赖注入
    @Inject
    public ConstructorInject(PojoBean userBean) {
        this.userBean = userBean;
    }
    /**返回注入 Bean 的 userName 属性。*/
    public String getUserName() {
        return this.userBean.getName();
    }
}