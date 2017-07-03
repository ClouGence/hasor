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
 * if指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SwitchInst extends Inst {
    public static class SwitchExpression {
        private Expression testExpression;
        private BlockSet   instBlockSet;
    }
    private List<SwitchExpression> testBlockSet;
    private BlockSet               elseBlockSet;
    public SwitchInst() {
        this.testBlockSet = new ArrayList<SwitchExpression>();
    }
    //
    //
    /** 添加条件分支 */
    public void addBlockSet(Expression testExp, BlockSet instBlockSet) {
        SwitchExpression se = new SwitchExpression();
        se.testExpression = testExp;
        se.instBlockSet = instBlockSet;
        this.testBlockSet.add(se);
    }
    /** 设置默认条件分支 */
    public void setElseBlockSet(BlockSet instBlockSet) {
        this.elseBlockSet = instBlockSet;
    }
}