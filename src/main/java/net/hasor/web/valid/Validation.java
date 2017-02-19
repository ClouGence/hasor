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
package net.hasor.web.valid;
/**
 * 对象验证，如果验证失败返回验证消息。
 * @version : 2017-01-10
 * @author 赵永春(zyc@hasor.net)
 */
public interface Validation<T> {
    /**
     * 验证逻辑
     * @param scene 场景,由 {@link Valid}注解指定的场景名。
     * @param dataForm 等待验证的数据。
     * @param errors 验证结果。
     */
    public void doValidation(String scene, T dataForm, ValidInvoker errors);
}