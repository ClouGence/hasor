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
package net.hasor.data.ql.dsl.domain;
import java.util.ArrayList;
import java.util.List;
/**
 * lambda 函数定义
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class LambdaBlockSet extends BlockSet implements Variable {
    private List<String> paramList = new ArrayList<String>();
    //
    //
    /** 添加入参 */
    public void addParam(String name) {
        if (this.paramList.contains(name)) {
            throw new java.lang.IllegalStateException(name + " param existing.");
        }
        this.paramList.add(name);
    }
    /** 添加函数指令集 */
    public void addToBody(BlockSet instList) {
        if (instList == null || instList.instList.isEmpty()) {
            return;
        }
        for (Inst inst : instList.instList) {
            this.addInst(inst);
        }
    }
    /**获取参数列表*/
    public List<String> getParamList() {
        return this.paramList;
    }
}