package net.hasor.dataql.domain;
import net.hasor.dataql.AbstractTestResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class BasicDomainTest extends AbstractTestResource {
    @Test
    public void basic_test() {
        assert DomainHelper.convertTo(new HashMap()) instanceof ObjectModel;
        assert DomainHelper.convertTo(new LinkedHashMap<>()) instanceof ObjectModel;
        //
        assert DomainHelper.convertTo(new ArrayList<>()) instanceof ListModel;
        assert DomainHelper.convertTo(new LinkedList<>()) instanceof ListModel;
        //
        assert DomainHelper.convertTo(true) == ValueModel.TRUE;
        assert DomainHelper.convertTo(false) == ValueModel.FALSE;
        assert DomainHelper.convertTo(null) == ValueModel.NULL;
        //
        assert DomainHelper.convertTo(123) instanceof ValueModel;
    }

    @Test
    public void null_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(null);
        assert dataModel.isNull();
        assert !dataModel.isNumber();
        assert dataModel.asString() == null;
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert !dataModel.asBoolean();
        assert dataModel.asByte() == 0;
        assert dataModel.asShort() == 0;
        assert dataModel.asInt() == 0;
        assert dataModel.asLong() == 0;
        assert dataModel.asFloat() == 0;
        assert dataModel.asDouble() == 0;
        assert dataModel.asBigInteger() == BigInteger.ZERO;
        assert dataModel.asBigDecimal() == BigDecimal.ZERO;
    }

    @Test
    public void boolean1_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(true);
        assert !dataModel.isNull();
        assert !dataModel.isNumber();
        assert dataModel.asString().equals("true");
        //
        assert dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == 1;
        assert dataModel.asShort() == 1;
        assert dataModel.asInt() == 1;
        assert dataModel.asLong() == 1;
        assert dataModel.asFloat() == 1;
        assert dataModel.asDouble() == 1;
        assert dataModel.asBigInteger() == BigInteger.ONE;
        assert dataModel.asBigDecimal() == BigDecimal.ONE;
    }

    @Test
    public void boolean2_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(false);
        assert !dataModel.isNull();
        assert !dataModel.isNumber();
        assert dataModel.asString().equals("false");
        //
        assert dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert !dataModel.asBoolean();
        assert dataModel.asByte() == 0;
        assert dataModel.asShort() == 0;
        assert dataModel.asInt() == 0;
        assert dataModel.asLong() == 0;
        assert dataModel.asFloat() == 0;
        assert dataModel.asDouble() == 0;
        assert dataModel.asBigInteger() == BigInteger.ZERO;
        assert dataModel.asBigDecimal() == BigDecimal.ZERO;
    }

    @Test
    public void byte_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo((byte) 123);
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert !dataModel.isDecimal();
        assert dataModel.asString().equals("123");
        //
        assert !dataModel.isBoolean();
        assert dataModel.isByte();
        assert dataModel.isShort();
        assert dataModel.isInt();
        assert dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == 123;
        assert dataModel.asShort() == 123;
        assert dataModel.asInt() == 123;
        assert dataModel.asLong() == 123;
        assert dataModel.asFloat() == 123;
        assert dataModel.asDouble() == 123;
        assert dataModel.asBigInteger().equals(BigInteger.valueOf(123));
        assert dataModel.asBigDecimal().equals(BigDecimal.valueOf(123));
    }

    @Test
    public void short_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo((short) 12345);
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert !dataModel.isDecimal();
        assert dataModel.asString().equals("12345");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert dataModel.isShort();
        assert dataModel.isInt();
        assert dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == 57; // 被截断了
        assert dataModel.asShort() == 12345;
        assert dataModel.asInt() == 12345;
        assert dataModel.asLong() == 12345;
        assert dataModel.asFloat() == 12345;
        assert dataModel.asDouble() == 12345;
        assert dataModel.asBigInteger().equals(BigInteger.valueOf(12345));
        assert dataModel.asBigDecimal().equals(BigDecimal.valueOf(12345));
    }

    @Test
    public void int_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(1234567);
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert !dataModel.isDecimal();
        assert dataModel.asString().equals("1234567");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert dataModel.isInt();
        assert dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == -121;      // 被截断了
        assert dataModel.asShort() == -10617;   // 被截断了
        assert dataModel.asInt() == 1234567;
        assert dataModel.asLong() == 1234567;
        assert dataModel.asFloat() == 1234567;
        assert dataModel.asDouble() == 1234567;
        assert dataModel.asBigInteger().equals(BigInteger.valueOf(1234567));
        assert dataModel.asBigDecimal().equals(BigDecimal.valueOf(1234567));
    }

    @Test
    public void long_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(1234567890123456L);
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert !dataModel.isDecimal();
        assert dataModel.asString().equals("1234567890123456");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == -64;               // 被截断了
        assert dataModel.asShort() == -17728;           // 被截断了
        assert dataModel.asInt() == 1015724736;         // 被截断了
        assert dataModel.asLong() == 1234567890123456L;
        assert dataModel.asFloat() == 1.23456795E15f;   // 被截断了
        assert dataModel.asDouble() == 1.234567890123456E15d;
        assert dataModel.asBigInteger().equals(BigInteger.valueOf(1234567890123456L));
        assert dataModel.asBigDecimal().equals(BigDecimal.valueOf(1234567890123456L));
    }

    @Test
    public void bigint_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(new BigInteger("12345678901234567890123456"));
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert !dataModel.isDecimal();
        assert dataModel.asString().equals("12345678901234567890123456");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == -64;                       // 被截断了
        assert dataModel.asShort() == -17728;                   // 被截断了
        assert dataModel.asInt() == 1792850624;                 // 被截断了
        assert dataModel.asLong() == -7484280360333952320L;     // 被截断了
        assert dataModel.asFloat() == 1.234568E25f;             // 被截断了
        assert dataModel.asDouble() == 1.2345678901234568E25d;  // 被截断了
        assert dataModel.asBigInteger().equals(new BigInteger("12345678901234567890123456"));
        assert dataModel.asBigDecimal().equals(new BigDecimal("12345678901234567890123456"));
    }

    @Test
    public void float_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(12345.123f);
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert dataModel.isDecimal();
        assert dataModel.asString().equals("12345.123");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert dataModel.isFloat();
        assert dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == 57;            // 被截断,取整了
        assert dataModel.asShort() == 12345;        // 被截断,取整了
        assert dataModel.asInt() == 12345;          // 被截断,取整了
        assert dataModel.asLong() == 12345L;        // 被截断,取整了
        assert dataModel.asFloat() == 12345.123f;
        assert dataModel.asDouble() == 12345.123046875d; // 浮点数值转换的时存在精度误差。
        assert dataModel.asBigInteger().equals(new BigInteger("12345"));        // 被截断,取整了
        assert dataModel.asBigDecimal().equals(new BigDecimal("12345.123"));
    }

    @Test
    public void double_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo(12345.123d);
        assert !dataModel.isNull();
        assert dataModel.isNumber();
        assert dataModel.isDecimal();
        assert dataModel.asString().equals("12345.123");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert dataModel.isBigDecimal();
        //
        assert dataModel.asBoolean();
        assert dataModel.asByte() == 57;            // 被截断,取整了
        assert dataModel.asShort() == 12345;        // 被截断,取整了
        assert dataModel.asInt() == 12345;          // 被截断,取整了
        assert dataModel.asLong() == 12345L;        // 被截断,取整了
        assert dataModel.asFloat() == 12345.123f;
        assert dataModel.asDouble() == 12345.123d;
        assert dataModel.asBigInteger().equals(new BigInteger("12345"));        // 被截断,取整了
        assert dataModel.asBigDecimal().equals(new BigDecimal("12345.123"));
    }

    @Test
    public void string1_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo("123w");
        assert !dataModel.isNull();
        assert !dataModel.isNumber();
        assert dataModel.asString().equals("123w");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        try {
            dataModel.asBoolean();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to boolean, value : 123w");
        }
        try {
            dataModel.asByte();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to byte, value : 123w");
        }
        try {
            dataModel.asShort();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to short, value : 123w");
        }
        try {
            dataModel.asInt();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to int, value : 123w");
        }
        try {
            dataModel.asLong();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to long, value : 123w");
        }
        try {
            dataModel.asFloat();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to float, value : 123w");
        }
        try {
            dataModel.asDouble();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to double, value : 123w");
        }
        try {
            dataModel.asBigInteger();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to BigInteger, value : 123w");
        }
        try {
            dataModel.asBigDecimal();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to BigDecimal, value : 123w");
        }
    }

    @Test
    public void string2_test() {
        ValueModel dataModel = (ValueModel) DomainHelper.convertTo("123");
        assert !dataModel.isNull();
        assert !dataModel.isNumber();
        assert dataModel.asString().equals("123");
        //
        assert !dataModel.isBoolean();
        assert !dataModel.isByte();
        assert !dataModel.isShort();
        assert !dataModel.isInt();
        assert !dataModel.isLong();
        assert !dataModel.isFloat();
        assert !dataModel.isDouble();
        assert !dataModel.isBigInteger();
        assert !dataModel.isBigDecimal();
        //
        try {
            dataModel.asBoolean();
            assert false;
        } catch (ClassCastException e) {
            assert e.getMessage().equals("can not cast to boolean, value : 123");
        }
        assert dataModel.asByte() == 123;
        assert dataModel.asShort() == 123;
        assert dataModel.asInt() == 123;
        assert dataModel.asLong() == 123;
        assert dataModel.asFloat() == 123;
        assert dataModel.asDouble() == 123;
        assert dataModel.asBigInteger().equals(new BigInteger("123"));
        assert dataModel.asBigDecimal().equals(new BigDecimal("123"));
    }
}