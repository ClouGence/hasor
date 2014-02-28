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
package net.hasor.plugins.transaction;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.hasor.jdbc.transaction.TransactionBehavior;
import net.hasor.jdbc.transaction.TransactionLevel;
/**
 * 声明该方法或者该类的所有方法在被调用时开启事务控制。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Transaction {
    /**默认级别*/
    public TransactionLevel level() default TransactionLevel.ISOLATION_DEFAULT;
    /**默认传播属性*/
    public TransactionBehavior value() default TransactionBehavior.PROPAGATION_REQUIRED;
}