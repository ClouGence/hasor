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
package net.hasor.jdbc.transaction;
/**
 * 事务传播属性
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public enum TransactionBehavior {
    /**
     * 加入已有事务
     * <p><i><b>释意</b></i>：尝试加入已经存在的事务中，如果没有则开启一个新的事务。*/
    PROPAGATION_REQUIRED,
    /**
     * 独立事务
     * <p><i><b>释意</b></i>：将挂起当前存在的事务挂起（如果存在的话）。
     * 并且开启一个全新的事务，新事务与已存在的事务之间彼此没有关系。*/
    RROPAGATION_REQUIRES_NEW,
    /**
     * 嵌套事务
     * <p><i><b>释意</b></i>：在当前事务中开启一个子事务。如果事务递交将连同上一级事务一同递交。
     * <p><i><b>注意</b></i>：需要驱动支持保存点。*/
    PROPAGATION_NESTED,
    /**
     * 跟随环境
     * <p><i><b>释意</b></i>：如果当前没有事务存在，就以非事务方式执行；如果有，就使用当前事务。*/
    PROPAGATION_SUPPORTS,
    /**
     * 非事务方式
     * <p><i><b>释意</b></i>：如果当前没有事务存在，就以非事务方式执行；如果有，就将当前事务挂起。
     * */
    PROPAGATION_NOT_SUPPORTED,
    /**
     * 排除事务
     * <p><i><b>释意</b></i>：如果当前没有事务存在，就以非事务方式执行；如果有，就抛出异常。*/
    PROPAGATION_NEVER,
    /**
     * 要求环境中存在事务
     * <p><i><b>释意</b></i>：如果当前没有事务存在，就抛出异常；如果有，就使用当前事务。*/
    PROPAGATION_MANDATORY,
}