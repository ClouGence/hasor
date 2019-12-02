package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.Option;

class UnaryProxyOperatorProcess implements OperatorMatch {
    private Class<?>        unaryType;
    private OperatorProcess process;

    public UnaryProxyOperatorProcess(Class<?> unaryType, OperatorProcess process) {
        this.unaryType = unaryType;
        this.process = process;
    }

    @Override
    public Object doProcess(String operator, Object[] args, Option option) throws InvokerProcessException {
        return this.process.doProcess(operator, args, option);
    }

    @Override
    public boolean testMatch(Class<?>... fstType) {
        return this.unaryType.isAssignableFrom(fstType[0]);
    }
}