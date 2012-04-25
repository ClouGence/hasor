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
package org.more.hypha.aop.define;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/**
 * 切入点组定义，如果组中没有任何切入点定义则匹配时将返回true，切入点组在计算匹配值时候是根据注册顺序依次俩俩匹配。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopPointcutGroupDefine extends AbstractPointcutDefine {
    /**比较{@link AopPointcutGroupDefine}中其他切入点时使用的关系条件。*/
    public enum RelEnum {
        /**与*/
        And,
        /**或*/
        Or,
        /**非*/
        Not
    }
    private ArrayList<AbstractPointcutDefine> defineList = new ArrayList<AbstractPointcutDefine>();
    private RelEnum                           relation   = RelEnum.And;
    /**获取当匹配时的匹配策略。*/
    public RelEnum getRelation() {
        return relation;
    }
    /**设置当匹配时的匹配策略。*/
    public void setRelation(RelEnum relation) {
        this.relation = relation;
    }
    /**向组中添加一个切点，如果已经存在该切点则添加操作失效。*/
    public void addPointcutDefine(AbstractPointcutDefine define) {
        if (this.defineList.contains(define) == false)
            this.defineList.add(define);
    }
    /**创建{@link AopPointcutGroupDefine}对象*/
    public List<AbstractPointcutDefine> getPointcutList() {
        return this.defineList;
    };
    /**按照关系要求计算组中所有表达式，并返回计算之后的匹配结果。*/
    public boolean isMatch(Method method) {
        if (this.defineList.size() == 0)
            return true;
        //
        boolean res_A = defineList.get(0).isMatch(method);
        for (int i = 1; i < this.defineList.size(); i++) {
            boolean res_B = this.defineList.get(i).isMatch(method);
            if (this.processRel(res_A, res_B) == false)
                return false;
            res_A = true;
        }
        return this.processRel(res_A);
    }
    /**关系计算，负责计算and,or,not三种关系。*/
    private boolean processRel(boolean res) {
        if (this.relation == RelEnum.Not && res == false)
            return true;
        return res;
    }
    /**关系计算，负责计算and,or,not三种关系。*/
    private boolean processRel(boolean res_1, boolean res_2) {
        if (this.relation == RelEnum.And)
            return res_1 == true && res_2 == true;
        else if (this.relation == RelEnum.Or)
            return res_1 == true || res_2 == true;
        else if (this.relation == RelEnum.Not)
            return res_1 == false && res_2 == false;
        return true;
    }
}