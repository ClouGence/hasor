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
package net.hasor.db.transaction.interceptor.simple;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
/**
 * 
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    /**传播属性*/
    public Propagation propagation() default Propagation.REQUIRED;
    /**隔离级别*/
    public Isolation isolation() default Isolation.DEFAULT;
    /**是否为只读事务。*/
    public boolean readOnly() default false;
    /**遇到下列异常抛出回滚事务。*/
    public Class<? extends Throwable>[] rollbackFor() default {};
    /**遇到下列异常抛出回滚事务。*/
    public String[] rollbackForClassName() default {};
    /**遇到下列异常继续事务递交。*/
    public Class<? extends Throwable>[] noRollbackFor() default {};
    /**遇到下列异常继续事务递交。*/
    public String[] noRollbackForClassName() default {};
}