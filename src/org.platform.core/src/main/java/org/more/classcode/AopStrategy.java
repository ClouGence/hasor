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
package org.more.classcode;
import java.lang.reflect.Method;
/**
 * Aop生成策略，可以通过该接口来确定aop方面的生成策略。只有{@link ClassEngine}对象被设置了
 * {@link AopBeforeListener}、{@link AopReturningListener}、{@link AopThrowingListener}
 * 三个切面接口或者{@link AopInvokeFilter}过滤器接口时才会在生成类的期间启用aop的支持。<br/>
 * 通过该接口中的方法可以确定某个方法是否参与aop的生成，同时还可以控制生成的aop方法上切面对象和aop过滤器对象集合。
 * @version 2010-9-3
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AopStrategy extends BaseStrategy {
    /**
     * 在{@link ClassEngine}扫描类期间，如果配置了aop相关设置可以通过该方法来确定遇到的这个方法是否忽略aop包装。
     * 如果想要忽略返回true否则返回false。
     * @param superClass 该参数用于表示，被发现的该方法所属的类型。
     * @param ignoreMethod 该参数用于表示被发现的方法。
     * @return 返回是否忽略这个方法的aop包装。如果想要忽略返回true否则返回false。
     */
    public boolean isIgnore(Class<?> superClass, Method ignoreMethod);
    /**
     * 该方法在配置bean的时候会被调用。通过该方法可以决定方法的最终生效{@link AopBeforeListener}集合。
     * @param target 被装备对象。
     * @param method 需要aop切面的方法。
     * @param beforeListener 即将生效的{@link AopBeforeListener}集合，通过该方法可以将改变这个即将生效的对象集。
     * @return 返回最终生效的{@link AopBeforeListener}对象集合。
     */
    public AopBeforeListener[] filterAopBeforeListener(Object target, Method method, AopBeforeListener[] beforeListener);
    /**
     * 该方法在配置bean的时候会被调用。通过该方法可以决定方法的最终生效{@link AopReturningListener}集合。
     * @param target 被装备对象。
     * @param method 需要aop切面的方法。
     * @param returningListener 即将生效的{@link AopReturningListener}集合，通过该方法可以将改变这个即将生效的对象集。
     * @return 返回最终生效的{@link AopReturningListener}对象集合。
     */
    public AopReturningListener[] filterAopReturningListener(Object target, Method method, AopReturningListener[] returningListener);
    /**
     * 该方法在配置bean的时候会被调用。通过该方法可以决定方法的最终生效{@link AopThrowingListener}集合。
     * @param target 被装备对象。
     * @param method 需要aop切面的方法。
     * @param throwingListener 即将生效的{@link AopThrowingListener}集合，通过该方法可以将改变这个即将生效的对象集。
     * @return 返回最终生效的{@link AopThrowingListener}对象集合。
     */
    public AopThrowingListener[] filterAopThrowingListener(Object target, Method method, AopThrowingListener[] throwingListener);
    /**
     * 该方法在配置bean的时候会被调用。通过该方法可以决定方法的最终生效{@link AopInvokeFilter}集合。
     * @param target 被装备对象。
     * @param method 需要aop切面的方法。
     * @param invokeFilter 即将生效的{@link AopInvokeFilter}集合，通过该方法可以将改变这个即将生效的对象集。
     * @return 返回最终生效的{@link AopInvokeFilter}对象集合。
     */
    public AopInvokeFilter[] filterAopInvokeFilter(Object target, Method method, AopInvokeFilter[] invokeFilter);
}