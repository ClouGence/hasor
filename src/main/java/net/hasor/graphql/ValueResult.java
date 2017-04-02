package net.hasor.graphql;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public interface ValueResult extends QueryResult {
    public Object getOriValue();

    public boolean getBoolean();

    public String getString();

    public byte getByte();

    public short getShort();

    public int getInt();

    public long getLong();

    public float getFloat();

    public double getDouble();
}