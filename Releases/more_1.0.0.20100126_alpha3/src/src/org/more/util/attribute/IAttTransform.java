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
package org.more.util.attribute;
import java.util.Properties;
/**
 * 属性类型转换接口，如果属性接口实现类实现了该接口就具备了与其他属性类型进行转换的功能。
 * @version 2009-5-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IAttTransform {
    /**
     * 获取属性接口的Properties形式。当IAttribute接口转化成Properties对象时如果IAttribute
     * 接口对象中存在值为空的属性则在转换过程中忽略。
     * @return 返回当前属性接口的Properties形式
     */
    public Properties toProperties();
    /**
     * 将Properties形式的属性数据转换成IAttribute接口形式。注意：由于Properties属性中提供的是
     * Object类型而IAttribute接口提供的是类型化的属性类型，因此在使用该方法时需要注意类型转换问题。
     * 否则将会引发类型转换异常。解决类型转换异常的常规做法是将类型化参数更改为Object。
     * @param prop Properties形式的属性数据
     */
    public void fromProperties(Properties prop);
}