package net.hasor.dataql.runtime.operator;
public interface DyadicOperatorRegistry {
    /** 添加 操作符 实现 */
    public default void registryOperator(String symbolName, Class[] classSetA, Class[] classSetB, OperatorProcess process) {
        if (classSetA == null || classSetA.length == 0 || classSetB == null || classSetB.length == 0) {
            throw new NullPointerException("classSetA or classSetB is empty.");
        }
        for (Class fstType : classSetA) {
            for (Class secType : classSetB) {
                this.registryOperator(symbolName, fstType, secType, process);
            }
        }
    }

    /** 添加 操作符 实现 */
    public void registryOperator(String symbolName, Class<?> fstType, Class<?> secType, OperatorProcess process);

    public OperatorProcess findDyadicProcess(String symbolName, Class<?> fstType, Class<?> secType);
}
