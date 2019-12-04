package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.Option;
import net.hasor.dataql.runtime.InstructRuntimeException;

class UnaryProxyOperatorProcess implements OperatorMatch {
    private Class<?>        unaryType;
    private OperatorProcess process;

    public UnaryProxyOperatorProcess(Class<?> unaryType, OperatorProcess process) {
        this.unaryType = unaryType;
        this.process = process;
    }

    @Override
    public Object doProcess(String operator, Object[] args, Option option) throws InstructRuntimeException {
        return this.process.doProcess(operator, args, option);
    }

    @Override
    public boolean testMatch(Class<?>... fstType) {
        return this.unaryType.isAssignableFrom(fstType[0]);
    }
}