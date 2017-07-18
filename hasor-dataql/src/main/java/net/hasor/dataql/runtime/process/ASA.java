package net.hasor.dataql.runtime.process;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.*;
import net.hasor.dataql.runtime.struts.ListResultStruts;
import net.hasor.dataql.runtime.struts.MemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class ASA implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASA;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, ProcessContet context) throws ProcessException {
        //
        // .读取返回值并包装成 ResultStruts
        String typeString = sequence.currentInst().getString(0);
        Class<?> objectType = null;
        if (StringUtils.isNotBlank(typeString)) {
            objectType = context.loadType(typeString);
        } else {
            objectType = ObjectModel.class;
        }
        Object toType = null;
        try {
            toType = objectType.newInstance();
        } catch (Exception e) {
            throw new ProcessException("ASA -> " + e.getMessage(), e);
        }
        Object result = memStack.pop();
        memStack.push(new ListResultStruts(toType));
        //
        // .圈定处理结果集的指令集
        final AtomicInteger dogs = new AtomicInteger(0);
        InstSequence subSequence = sequence.findSubSequence(new InstFilter() {
            public boolean isExit(Instruction inst) {
                //
                if (ASM == inst.getInstCode() || ASA == inst.getInstCode()) {
                    dogs.incrementAndGet();
                    return false;
                }
                //
                if (ASE == inst.getInstCode()) {
                    dogs.decrementAndGet();
                    if (dogs.get() == 0) {
                        return true;
                    }
                }
                return false;
            }
        });
        //
        // .对结果集进行迭代处理
        Collection<Object> dataSet = toCollection(result);
        for (Object obj : dataSet) {
            subSequence.reset();                        // 重置执行序列
            memStack.pushData(obj);                     // 设置DS
            context.processInset(subSequence, memStack);// 执行序列
            memStack.popData();                         // 销毁DS
        }
        //
        // .处理完毕跳到出口
        sequence.jumpTo(subSequence.exitPosition() - 1);
    }
    private Collection<Object> toCollection(Object curData) {
        Collection<Object> listData = null;
        if (curData == null) {
            listData = new ArrayList<Object>();
        } else {
            if (!(curData instanceof Collection)) {
                if (curData.getClass().isArray()) {
                    listData = new ArrayList<Object>();
                    for (Object obj : (Object[]) curData) {
                        listData.add(obj);
                    }
                } else {
                    listData = Arrays.asList(curData);
                }
            } else {
                listData = (Collection<Object>) curData;
            }
        }
        //
        return listData;
    }
}