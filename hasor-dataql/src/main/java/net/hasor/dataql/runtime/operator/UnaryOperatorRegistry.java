package net.hasor.dataql.runtime.operator;
public interface UnaryOperatorRegistry {
    /** 添加 操作符 实现 */
    public default void registryOperator(String symbolName, Class[] opeTypeSet, OperatorProcess process) {
        if (opeTypeSet == null || opeTypeSet.length == 0) {
            throw new NullPointerException("classSetA or classSetB is empty.");
        }
        for (Class opeType : opeTypeSet) {
            this.registryOperator(symbolName, opeType, process);
        }
    }

    /** 添加 操作符 实现 */
    public void registryOperator(String symbolName, Class<?> opeType, OperatorProcess process);

    public OperatorProcess findUnaryProcess(String symbolName, Class<?> fstType);
}