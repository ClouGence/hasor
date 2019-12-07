package net.hasor.dataql.runtime;
import java.util.function.Supplier;

/**
 * 为 compilerVar 提供一个延迟装载的机制，当 LOAD 指令加载到 compilerVar 时。会自动对该接口对象进行展开。
 */
public interface VarSupplier extends Supplier {
}