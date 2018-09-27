package net.hasor.core.container.aware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoAware;
public class BindInfoAwareBean implements BindInfoAware {
    private BindInfo<?> bindInfo;
    public BindInfo<?> getBindInfo() {
        return bindInfo;
    }
    @Override
    public void setBindInfo(BindInfo<?> bindInfo) {
        this.bindInfo = bindInfo;
    }
}