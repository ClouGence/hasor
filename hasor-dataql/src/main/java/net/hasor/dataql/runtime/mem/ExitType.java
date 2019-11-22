package net.hasor.dataql.runtime.mem;
/**
 * 退出模式
 */
public enum ExitType {
    /** 正常退出，后续指令序列继续执行 */
    Return,
    /** 非正常退出，终止后续指令序列执行并抛出异常。 */
    Throw,
    /** 中断正常执行，并退出整个执行序列。 */
    Exit
}
