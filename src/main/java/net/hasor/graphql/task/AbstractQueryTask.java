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
package net.hasor.graphql.task;
import net.hasor.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractQueryTask implements QueryTask {
    private List<AbstractQueryTask> subList    = new ArrayList<AbstractQueryTask>();
    private TaskStatus              taskStatus = TaskStatus.Plan;
    //
    public void addSubTask(AbstractQueryTask task) {
        this.subList.add(task);
    }
    //
    public void printDetailTaskTree(StringBuilder builder) {
        this.printDetailTaskTree(builder, 0);
    }
    //
    private static String genDepthStr(int depth) {
        return StringUtils.leftPad("", depth * 2, " ");
    }
    private void printDetailTaskTree(StringBuilder builder, int depth) {
        builder.append("Task [" + this.getClass().getSimpleName() + "]");
        for (int i = 0; i < this.subList.size(); i++) {
            AbstractQueryTask task = this.subList.get(i);
            builder.append("\n" + genDepthStr(depth) + " -> ");
            task.printDetailTaskTree(builder, depth + 1);
        }
    }
}