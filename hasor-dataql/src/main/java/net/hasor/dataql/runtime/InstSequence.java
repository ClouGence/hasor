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
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.domain.compiler.Instruction;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.utils.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 指令序列集
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-14
 */
public class InstSequence {
    private final int           address;         // 指令集中的序列地址
    private final QIL           queueSet;     // 指令集
    private final int           startPosition;// 有效的起始位置
    private final int           endPosition;  // 有效的终止位置
    private final AtomicInteger sequenceIndex;// 当前指令指针指向的序列位置
    private boolean jumpMark = false;
    //
    InstSequence(int address, QIL queueSet) {
        this.address = address;
        this.queueSet = queueSet;
        this.startPosition = 0;
        this.endPosition = this.queueSet.iqlSize(address);
        this.sequenceIndex = new AtomicInteger(this.startPosition);
    }
    InstSequence(int address, QIL queueSet, int startPosition, int endPosition) {
        this.address = address;
        this.queueSet = queueSet;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.sequenceIndex = new AtomicInteger(this.startPosition);
    }
    //
    /** 当前指令序列的地址 */
    public int getAddress() {
        return this.address;
    }
    /** 克隆一个 */
    public InstSequence clone() {
        return new InstSequence(this.address, this.queueSet);
    }
    /** 当前指令 */
    public Instruction currentInst() {
        if (this.queueSet == null) {
            return null;
        }
        //
        return this.queueSet.instOf(this.address, this.sequenceIndex.get());
    }
    /** 另一个方法序列 */
    public InstSequence methodSet(int address) {
        if (address < 0 || address > this.queueSet.iqlPoolSize()) {
            return null;
        }
        return new InstSequence(address, this.queueSet);
    }
    /** 根据 filter，来决定圈定  form to 范围的指令集。 */
    public InstSequence findSubSequence(InstFilter instFilter) {
        Instruction[] curInstSet = this.queueSet.iqlArrays(this.address);
        int startIndex = this.sequenceIndex.get();              // 从下一条指令作为开始
        int endIndex = curInstSet.length - 1;       // 结束位置，默认为最长
        for (int i = startIndex; i < endIndex; i++) {
            if (instFilter.isExit(curInstSet[i])) {
                endIndex = i;
                break;
            }
        }
        return new InstSequence(this.address, this.queueSet, startIndex + 1, endIndex);
    }
    //
    /** 是否还有更多指令等待执行。 */
    public boolean hasNext() {
        return this.sequenceIndex.get() < this.endPosition;
    }
    /** 移动指令序列指针，到下一个位置。 */
    public boolean doNext(int nextSkip) throws InvokerProcessException {
        if (this.jumpMark) {
            this.jumpMark = false;
            return true;
        }
        if (nextSkip < 0) {
            throw new InvokerProcessException(0, "nextSkip must be > 0");
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
        this.sequenceIndex.set(this.startPosition);
    }
    //
    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("[");
        strBuffer.append(this.address);
        strBuffer.append("]\n");
        //
        Instruction[] instList = this.queueSet.iqlArrays(this.address);
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