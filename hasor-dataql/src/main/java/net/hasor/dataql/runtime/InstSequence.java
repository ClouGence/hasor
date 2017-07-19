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
package net.hasor.dataql.runtime;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.domain.inst.InstQueue;
import net.hasor.dataql.domain.inst.Instruction;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 指令序列集
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-14
 */
public class InstSequence {
    private final int             name;         // 指令集中的序列名称
    private final Instruction[][] queueSet;     // 指令集
    private final int             startPosition;// 有效的起始位置
    private final int             endPosition;  // 有效的终止位置
    private final AtomicInteger   sequenceIndex;// 当前指令指针指向的序列位置
    private boolean jumpMark = false;
    //
    public InstSequence(int name, InstQueue queue) throws ProcessException {
        this(name, queue.buildArrays());
    }
    private InstSequence(int name, Instruction[][] queueSet) {
        this.name = name;
        this.queueSet = queueSet;
        this.startPosition = 0;
        this.endPosition = this.queueSet[name].length;
        this.sequenceIndex = new AtomicInteger(this.startPosition);
    }
    private InstSequence(int name, Instruction[][] queueSet, int startPosition, int endPosition) {
        this.name = name;
        this.queueSet = queueSet;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.sequenceIndex = new AtomicInteger(this.startPosition);
    }
    //
    /** 当前指令 */
    public Instruction currentInst() {
        if (this.queueSet == null) {
            return null;
        }
        if (this.queueSet.length < this.name || this.queueSet[this.name] == null) {
            return null;
        }
        //
        return this.queueSet[this.name][this.sequenceIndex.get()];
    }
    /** 另一个方法序列 */
    public InstSequence methodSet(int address) {
        if (address < 0 || address > this.queueSet.length) {
            return null;
        }
        return new InstSequence(address, this.queueSet);
    }
    /** 根据 filter，来决定圈定  form to 范围的指令集。 */
    public InstSequence findSubSequence(InstFilter instFilter) {
        Instruction[] curInstSet = this.queueSet[this.name];
        int startIndex = this.sequenceIndex.get();  // 从下一条指令作为开始
        int endIndex = curInstSet.length - 1;       // 结束位置，默认为最长
        for (int i = startIndex; i < endIndex; i++) {
            if (instFilter.isExit(curInstSet[i])) {
                endIndex = i;
                break;
            }
        }
        return new InstSequence(this.name, this.queueSet, startIndex + 1, endIndex);
    }
    //
    /** 是否还有更多指令等待执行。 */
    public boolean hasNext() {
        return this.sequenceIndex.get() < this.endPosition;
    }
    /** 移动指令序列指针，到下一个位置。 */
    public boolean doNext(int nextSkip) throws ProcessException {
        if (this.jumpMark) {
            this.jumpMark = false;
            return true;
        }
        if (nextSkip < 0) {
            throw new ProcessException("nextSkip must be > 0");
        }
        int newPosition = this.sequenceIndex.get() + nextSkip;
        if (newPosition > this.endPosition) {
            return false;
        }
        //
        if (nextSkip > 0) {
            this.sequenceIndex.addAndGet(nextSkip);
        } else {
            this.sequenceIndex.incrementAndGet();
        }
        return true;
    }
    /**指令集的出口地址*/
    public int exitPosition() {
        return this.endPosition;
    }
    /**重置执行指针到序列指定位置*/
    public void jumpTo(int position) {
        this.sequenceIndex.set(position);
        this.jumpMark = true;
    }
    /**重置执行指针到序列最开始*/
    public void reset() {
        this.jumpTo(this.startPosition);
    }
    //
    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("[");
        strBuffer.append(this.name);
        strBuffer.append("]\n");
        //
        Instruction[] instList = this.queueSet[this.name];
        int length = String.valueOf(instList.length).length();
        for (int i = this.startPosition; i < this.endPosition; i++) {
            if (i == this.sequenceIndex.get()) {
                strBuffer.append("> #");
            } else {
                strBuffer.append("  #");
            }
            strBuffer.append(StringUtils.leftPad(String.valueOf(i), length, '0'));
            strBuffer.append("  ");
            strBuffer.append(instList[i].toString());
            strBuffer.append("\n");
        }
        strBuffer.append("\n");
        return strBuffer.toString();
    }
}