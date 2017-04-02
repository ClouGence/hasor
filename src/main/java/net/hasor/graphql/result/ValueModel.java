package net.hasor.graphql.result;
import net.hasor.graphql.ValueResult;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public class ValueModel implements ValueResult {
    private Object value = null;
    public ValueModel(Object value) {
        this.value = value;
    }
    @Override
    public Object getOriValue() {
        return null;
    }
    @Override
    public boolean getBoolean() {
        return false;
    }
    @Override
    public String getString() {
        return null;
    }
    @Override
    public byte getByte() {
        return 0;
    }
    @Override
    public short getShort() {
        return 0;
    }
    @Override
    public int getInt() {
        return 0;
    }
    @Override
    public long getLong() {
        return 0;
    }
    @Override
    public float getFloat() {
        return 0;
    }
    @Override
    public double getDouble() {
        return 0;
    }
}