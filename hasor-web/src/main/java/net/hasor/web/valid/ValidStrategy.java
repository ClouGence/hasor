package net.hasor.web.valid;

/**
 * 验证器执行策略
 */
public enum ValidStrategy {

    /**
     * 停止执行后面的验证器
     */
    STOP_EXECUTION(0),

    /**
     * 接着执行后面的验证器
     */
    THEN_EXECUTION(1);

    private int value;

    ValidStrategy(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
