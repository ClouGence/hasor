package net.hasor.dataql.runtime.operator;
public interface OperatorMatch extends OperatorProcess {
    public boolean testMatch(Class<?>... fstType);
}