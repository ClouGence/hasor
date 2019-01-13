package net.hasor.core.aop;
//
//
public class ConstructorAopBean {
    private byte    byteValue;
    private short   shortValue;
    private int     intValue;
    private long    longValue;
    private float   floatValue;
    private double  doubleValue;
    private boolean booleanValue;
    private char    charValue;
    //
    public ConstructorAopBean(      //
            byte byteValue          //
            , short shortValue      //
            , int intValue          //
            , long longValue        //
            , float floatValue      //
            , double doubleValue    //
            , boolean booleanValue  //
            , char charValue) {
        //
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.intValue = intValue;
        this.longValue = longValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.booleanValue = booleanValue;
        this.charValue = charValue;
    }
    //
    //
    public byte getByteValue() {
        return byteValue;
    }
    public short getShortValue() {
        return shortValue;
    }
    public int getIntValue() {
        return intValue;
    }
    public long getLongValue() {
        return longValue;
    }
    public float getFloatValue() {
        return floatValue;
    }
    public double getDoubleValue() {
        return doubleValue;
    }
    public boolean isBooleanValue() {
        return booleanValue;
    }
    public char getCharValue() {
        return charValue;
    }
}
