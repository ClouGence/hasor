package net.hasor.test.core;
import net.hasor.core.BindInfo;

public class MockBindInfo implements BindInfo<Object> {
    @Override
    public String getBindID() {
        return null;
    }

    @Override
    public String getBindName() {
        return null;
    }

    @Override
    public Class<Object> getBindType() {
        return null;
    }

    @Override
    public Object getMetaData(String key) {
        return null;
    }

    @Override
    public void setMetaData(String key, Object value) {
    }

    @Override
    public void removeMetaData(String key) {
    }
}
