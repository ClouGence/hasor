package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Hints;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.Location;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

public class BasicSymbolTest extends AbstractTestResource {
    private OperatorManager om         = OperatorManager.defaultManager();
    private Hints           optionSet  = new HintsSet();
    private Hints           decimalSet = new HintsSet() {{
        setHint(Hints.MIN_DECIMAL_WIDTH, "big");
    }};

    @Test
    public void uo_test() throws Exception {
        OperatorProcess process = null;
        process = om.findUnaryProcess("!", Boolean.class);
        assert !((Boolean) process.doProcess(Location.unknownLocation(), "!", new Object[] { true }, optionSet));
        assert ((Boolean) process.doProcess(Location.unknownLocation(), "!", new Object[] { false }, optionSet));
        process = om.findUnaryProcess("!", Boolean.TYPE);
        assert !((Boolean) process.doProcess(Location.unknownLocation(), "!", new Object[] { true }, optionSet));
        assert ((Boolean) process.doProcess(Location.unknownLocation(), "!", new Object[] { false }, optionSet));
        //
        process = om.findUnaryProcess("-", Number.class);
        assert ((Byte) process.doProcess(Location.unknownLocation(), "-", new Object[] { (byte) 1 }, optionSet)) == -1;
        assert ((Short) process.doProcess(Location.unknownLocation(), "-", new Object[] { (short) -1 }, optionSet)) == 1;
        assert ((Integer) process.doProcess(Location.unknownLocation(), "-", new Object[] { (int) 10 }, optionSet)) == -10;
        assert ((Long) process.doProcess(Location.unknownLocation(), "-", new Object[] { (long) -11 }, optionSet)) == 11;
        assert ((Float) process.doProcess(Location.unknownLocation(), "-", new Object[] { (float) 10.3 }, optionSet)) == -10.3f;
        assert ((Double) process.doProcess(Location.unknownLocation(), "-", new Object[] { (double) -11.44 }, optionSet)) == 11.44;
        assert ((BigInteger) process.doProcess(Location.unknownLocation(), "-", new Object[] { BigInteger.valueOf(1234) }, optionSet)).intValue() == -1234;
        assert ((BigDecimal) process.doProcess(Location.unknownLocation(), "-", new Object[] { BigDecimal.valueOf(-123.4d) }, optionSet)).doubleValue() == 123.4;
    }

    @Test
    public void plus_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("+", Integer.class, Integer.class);
        assert (Integer) process.doProcess(Location.unknownLocation(), "+", new Object[] { 1, 2 }, optionSet) == 3;
        //
        process = om.findDyadicProcess("+", Integer.class, Long.class);
        assert (Long) process.doProcess(Location.unknownLocation(), "+", new Object[] { 1, 2l }, optionSet) == 3;
        //
        process = om.findDyadicProcess("+", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).add(bigInts[1]);
        assert process.doProcess(Location.unknownLocation(), "+", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("+", Double.class, Double.class);
        assert (Double) process.doProcess(Location.unknownLocation(), "+", new Object[] { 1.2, 3.8 }, optionSet) == 5.0;
        //
        process = om.findDyadicProcess("+", Float.class, Float.class);
        assert (Float) process.doProcess(Location.unknownLocation(), "+", new Object[] { 1.2f, 3.8f }, optionSet) == 5.0;
        //
        process = om.findDyadicProcess("+", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "+", new Object[] { 1.2f, 3.8f }, decimalSet).equals(BigDecimal.valueOf(5));
    }

    @Test
    public void sub_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("-", Integer.class, Integer.class);
        assert (Integer) process.doProcess(Location.unknownLocation(), "-", new Object[] { 1, 2 }, optionSet) == -1;
        //
        process = om.findDyadicProcess("-", Integer.class, Long.class);
        assert (Long) process.doProcess(Location.unknownLocation(), "-", new Object[] { 1, 2l }, optionSet) == -1;
        //
        process = om.findDyadicProcess("-", String.class, Long.class);
        assert process == null;
        //
        process = om.findDyadicProcess("-", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).subtract(bigInts[1]);
        assert process.doProcess(Location.unknownLocation(), "-", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("-", Double.class, Double.class);
        assert (Double) process.doProcess(Location.unknownLocation(), "-", new Object[] { 1.2, 3.8 }, optionSet) != -2.6; // 浮点数计算精度导致
        //
        process = om.findDyadicProcess("-", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "-", new Object[] { 1.2f, 3.8f }, optionSet).toString().equals("-2.6");
        //
        process = om.findDyadicProcess("-", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "-", new Object[] { 1.2, 3.8 }, decimalSet).equals(BigDecimal.valueOf(-2.6));
    }

    @Test
    public void mul_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("*", Integer.class, Integer.class);
        assert (Integer) process.doProcess(Location.unknownLocation(), "*", new Object[] { 2, 3 }, optionSet) == 6;
        //
        process = om.findDyadicProcess("*", Integer.class, Long.class);
        assert (Long) process.doProcess(Location.unknownLocation(), "*", new Object[] { 2, 3l }, optionSet) == 6;
        //
        process = om.findDyadicProcess("*", String.class, Long.class);
        assert process == null;
        //
        process = om.findDyadicProcess("*", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).multiply(bigInts[1]);
        assert process.doProcess(Location.unknownLocation(), "*", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("*", Float.class, Float.class);
        assert (Float) process.doProcess(Location.unknownLocation(), "*", new Object[] { 1.2f, 2f }, optionSet) == 2.4f;
        //
        process = om.findDyadicProcess("*", Double.class, Double.class);
        assert (Double) process.doProcess(Location.unknownLocation(), "*", new Object[] { 0.14, 100 }, optionSet) != 14; // 浮点数计算精度导致
        //
        process = om.findDyadicProcess("*", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "*", new Object[] { 0.14, 100 }, decimalSet).equals(BigDecimal.valueOf(14));
    }

    @Test
    public void div_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("/", Integer.class, Integer.class);
        assert (Integer) process.doProcess(Location.unknownLocation(), "/", new Object[] { 2, 3 }, optionSet) == 0;
        assert process.doProcess(Location.unknownLocation(), "/", new Object[] { 2.0, 3 }, optionSet).toString().equals("0.6666666666666666");
        //
        process = om.findDyadicProcess("/", Integer.class, Long.class);
        assert (Long) process.doProcess(Location.unknownLocation(), "/", new Object[] { 2, 3l }, optionSet) == 0;
        assert process.doProcess(Location.unknownLocation(), "/", new Object[] { 2.0, 3l }, optionSet).toString().equals("0.6666666666666666");
        //
        process = om.findDyadicProcess("/", String.class, Long.class);
        assert process == null;
        //
        process = om.findDyadicProcess("/", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).divide(bigInts[1]);
        assert process.doProcess(Location.unknownLocation(), "/", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("/", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "/", new Object[] { 2.2f, 100f }, optionSet).equals(Float.parseFloat("0.022"));
        //
        process = om.findDyadicProcess("/", Double.class, Double.class);
        assert (Double) process.doProcess(Location.unknownLocation(), "/", new Object[] { 2.2, 100 }, optionSet) != 0.022; // 浮点数计算精度导致
        //
        process = om.findDyadicProcess("/", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "/", new Object[] { 2.2, 100 }, decimalSet).equals(BigDecimal.valueOf(0.022));
    }

    @Test
    public void mod_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("%", Integer.class, Integer.class);
        assert (Integer) process.doProcess(Location.unknownLocation(), "%", new Object[] { 2, 3 }, optionSet) == 2;
        assert process.doProcess(Location.unknownLocation(), "%", new Object[] { 2.0, 3 }, optionSet).toString().equals("2.0");
        //
        process = om.findDyadicProcess("%", Integer.class, Long.class);
        assert (Long) process.doProcess(Location.unknownLocation(), "%", new Object[] { 2, 3l }, optionSet) == 2;
        assert process.doProcess(Location.unknownLocation(), "%", new Object[] { 2.0, 3l }, optionSet).toString().equals("2.0");
        //
        process = om.findDyadicProcess("%", String.class, Long.class);
        assert process == null;
        //
        process = om.findDyadicProcess("%", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).mod(bigInts[1]);
        assert process.doProcess(Location.unknownLocation(), "%", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("%", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "%", new Object[] { 6f, 4f }, optionSet).toString().equals("2.0");
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6"),        //
                new BigDecimal("4")         //
        };
        process = om.findDyadicProcess("%", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), "%", bigDecs, optionSet).equals(new BigDecimal("2"));
    }

    @Test
    public void div2_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("\\", Integer.class, Integer.class);
        assert (Integer) process.doProcess(Location.unknownLocation(), "\\", new Object[] { 6, 3 }, optionSet) == 2;
        assert process.doProcess(Location.unknownLocation(), "\\", new Object[] { 6.0, 3 }, optionSet).toString().equals("2");
        //
        process = om.findDyadicProcess("\\", Integer.class, Long.class);
        assert (Long) process.doProcess(Location.unknownLocation(), "\\", new Object[] { 6, 3l }, optionSet) == 2;
        assert process.doProcess(Location.unknownLocation(), "\\", new Object[] { 6.0, 3l }, optionSet).toString().equals("2");
        //
        process = om.findDyadicProcess("\\", String.class, Long.class);
        assert process == null;
        //
        process = om.findDyadicProcess("\\", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).divide(bigInts[1]);
        assert process.doProcess(Location.unknownLocation(), "\\", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("\\", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "\\", new Object[] { 6f, 3f }, optionSet).equals(2L);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6"),        //
                new BigDecimal("3")         //
        };
        process = om.findDyadicProcess("\\", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), "\\", bigDecs, optionSet).equals(new BigDecimal("2"));
    }

    @Test
    public void plus2_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("+", String.class, Long.class);
        assert process.doProcess(Location.unknownLocation(), "+", new Object[] { "1", 2l }, optionSet).equals("12");
        //
        process = om.findDyadicProcess("+", String.class, HashMap.class);
        assert process.doProcess(Location.unknownLocation(), "+", new Object[] { "1", new HashMap<>() }, optionSet).equals("1{}");
    }

    @Test
    public void eq_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("==", String.class, Long.class);
        assert process.doProcess(Location.unknownLocation(), "==", new Object[] { "1", 2l }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("==", String.class, HashMap.class);
        assert process.doProcess(Location.unknownLocation(), "==", new Object[] { "1", new HashMap<>() }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("==", String.class, String.class);
        assert process.doProcess(Location.unknownLocation(), "==", new Object[] { "1", "1" }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("==", Long.class, Long.class);
        assert process.doProcess(Location.unknownLocation(), "==", new Object[] { 1L, 1L }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("==", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "==", new Object[] { 1.2f, 1.2f }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("==", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "==", new Object[] { 1.2d, 1.2d }, optionSet).equals(true);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("123"),      //
                new BigInteger("123")       //
        };
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6"),        //
                new BigDecimal("6")         //
        };
        process = om.findDyadicProcess("==", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), "==", bigInts, optionSet).equals(true);
        process = om.findDyadicProcess("==", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), "==", bigDecs, optionSet).equals(true);
    }

    @Test
    public void ne_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("!=", String.class, Long.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { "1", 2l }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, HashMap.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { "1", new HashMap<>() }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, String.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { "1", "1" }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("!=", Long.class, String.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { 1L, "1" }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", Float.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { 1.2f, true }, optionSet).equals(false); // 任何非零值都可以认为 是true
        //
        process = om.findDyadicProcess("!=", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { 1.2d, 1.2d }, optionSet).equals(false);
    }

    @Test
    public void ne2_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("!=", String.class, Long.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { "1", 2l }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, HashMap.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { "1", new HashMap<>() }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, String.class);
        assert process.doProcess(Location.unknownLocation(), "!=", new Object[] { "1", "1" }, optionSet).equals(false);
    }

    @Test
    public void logicAnd_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "&&", new Object[] { true, true }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "&&", new Object[] { true, false }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "&&", new Object[] { false, false }, optionSet).equals(false);
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.TYPE);
        assert process.doProcess(Location.unknownLocation(), "&&", new Object[] { false, false }, optionSet).equals(false);
    }

    @Test
    public void logicOr_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("||", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "||", new Object[] { true, true }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("||", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "||", new Object[] { true, false }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("||", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "||", new Object[] { false, false }, optionSet).equals(false);
        process = om.findDyadicProcess("||", Boolean.class, Boolean.TYPE);
        assert process.doProcess(Location.unknownLocation(), "||", new Object[] { false, false }, optionSet).equals(false);
    }

    @Test
    public void and_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("&", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { true, true }, optionSet).equals(1);
        //
        process = om.findDyadicProcess("&", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { true, false }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("&", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { false, false }, optionSet).equals(0);
        process = om.findDyadicProcess("&", Boolean.class, Boolean.TYPE);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { false, false }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("&", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { 8, 1 }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("&", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { 8, 9 }, optionSet).equals(8);
        //
        process = om.findDyadicProcess("&", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { 8L, 9 }, optionSet).equals(8L);
        //
        process = om.findDyadicProcess("&", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "&", new Object[] { BigInteger.valueOf(8), 9 }, optionSet).equals(BigInteger.valueOf(8));
    }

    @Test
    public void or_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("|", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { true, true }, optionSet).equals(1);
        //
        process = om.findDyadicProcess("|", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { true, false }, optionSet).equals(1);
        //
        process = om.findDyadicProcess("|", Boolean.class, Boolean.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { false, false }, optionSet).equals(0);
        process = om.findDyadicProcess("|", Boolean.class, Boolean.TYPE);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { false, true }, optionSet).equals(1);
        //
        process = om.findDyadicProcess("|", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { 8, 1 }, optionSet).equals(9);
        //
        process = om.findDyadicProcess("|", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { 8, 9 }, optionSet).equals(9);
        //
        process = om.findDyadicProcess("|", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { 8L, 9 }, optionSet).equals(9L);
        //
        process = om.findDyadicProcess("|", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "|", new Object[] { BigInteger.valueOf(8), 9 }, optionSet).equals(BigInteger.valueOf(9));
    }

    @Test
    public void gt_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess(">", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">", new Object[] { 8, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">", new Object[] { 12L, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), ">", new Object[] { 1.2f, 2.2f }, optionSet).equals(false);
        //
        process = om.findDyadicProcess(">", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), ">", new Object[] { 3.2d, 2.2d }, optionSet).equals(true);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("123")       //
        };
        process = om.findDyadicProcess(">", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), ">", bigInts, optionSet).equals(true);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess(">", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), ">", bigDecs, optionSet).equals(true);
    }

    @Test
    public void gteq_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess(">=", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">=", new Object[] { 1, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">=", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">=", new Object[] { 12L, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">=", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), ">=", new Object[] { 1.2f, 2.2f }, optionSet).equals(false);
        //
        process = om.findDyadicProcess(">=", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), ">=", new Object[] { 3.2d, 2.2d }, optionSet).equals(true);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess(">=", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), ">=", bigInts, optionSet).equals(true);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess(">=", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), ">=", bigDecs, optionSet).equals(true);
    }

    @Test
    public void lt_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("<", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<", new Object[] { 1, 2 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<", new Object[] { 12L, 1 }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("<", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "<", new Object[] { 1.2f, 2.2f }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "<", new Object[] { 3.2d, 2.2d }, optionSet).equals(false);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess("<", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), "<", bigInts, optionSet).equals(false);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("5.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess("<", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), "<", bigDecs, optionSet).equals(true);
    }

    @Test
    public void lteq_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("<=", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<=", new Object[] { 1, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<=", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<=", new Object[] { 12L, 1 }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("<=", Float.class, Float.class);
        assert process.doProcess(Location.unknownLocation(), "<=", new Object[] { 2.2f, 2.2f }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<=", Double.class, Double.class);
        assert process.doProcess(Location.unknownLocation(), "<=", new Object[] { 3.2d, 2.2d }, optionSet).equals(false);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess("<=", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), "<=", bigInts, optionSet).equals(true);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("5.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess("<=", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(Location.unknownLocation(), "<=", bigDecs, optionSet).equals(true);
    }

    @Test
    public void xor_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("^", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "^", new Object[] { 1, 1 }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("^", Long.class, Integer.class);
        assert ((Long) process.doProcess(Location.unknownLocation(), "^", new Object[] { 12L, 1 }, optionSet)) == (12 ^ 1);
        assert ((Long) process.doProcess(Location.unknownLocation(), "^", new Object[] { 12L, 4 }, optionSet)) == (12 ^ 4);
        //
        try {
            process = om.findDyadicProcess("^", Float.class, Float.class);
            process.doProcess(Location.unknownLocation(), "^", new Object[] { 2.2f, 2.2f }, optionSet);
            assert false;
        } catch (NumberFormatException e) {
            assert e.getMessage().equals("value mast not be Decimal.");
        }
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess("^", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), "^", bigInts, optionSet).equals(BigInteger.valueOf(0));
    }

    @Test
    public void shiftLeft_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("<<", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<<", new Object[] { 1, 1 }, optionSet).equals(2);
        //
        process = om.findDyadicProcess("<<", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<<", new Object[] { 12L, 1 }, optionSet).equals(24L);
        //
        process = om.findDyadicProcess("<<", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), "<<", new Object[] { 12, 2 }, optionSet).equals(48);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("4")         //
        };
        process = om.findDyadicProcess("<<", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), "<<", bigInts, optionSet).equals(new BigInteger("1984"));
    }

    @Test
    public void shiftRight_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess(">>", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>", new Object[] { 1, 1 }, optionSet).equals(0);
        //
        process = om.findDyadicProcess(">>", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>", new Object[] { 12L, 1 }, optionSet).equals(6L);
        //
        process = om.findDyadicProcess(">>", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>", new Object[] { 12, 2 }, optionSet).equals(3);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("4")         //
        };
        process = om.findDyadicProcess(">>", BigInteger.class, BigInteger.class);
        assert process.doProcess(Location.unknownLocation(), ">>", bigInts, optionSet).equals(new BigInteger("7"));
        //
        process = om.findDyadicProcess(">>", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>", new Object[] { -1234, 2 }, optionSet).equals(-309);
    }

    @Test
    public void shiftRightWithUnsigned_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess(">>>", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>>", new Object[] { 1, 1 }, optionSet).equals(0);
        //
        process = om.findDyadicProcess(">>>", Long.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>>", new Object[] { 12L, 1 }, optionSet).equals(12L >>> 1);
        //
        process = om.findDyadicProcess(">>>", Integer.class, Integer.class);
        assert process.doProcess(Location.unknownLocation(), ">>>", new Object[] { 12, 2 }, optionSet).equals(12 >>> 2);
        assert process.doProcess(Location.unknownLocation(), ">>>", new Object[] { -1234, 2 }, optionSet).equals(-1234 >>> 2);
        //
        process = om.findDyadicProcess(">>>", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts1 = new BigInteger[] { new BigInteger("-124"), new BigInteger("4") };
        BigInteger[] bigInts2 = new BigInteger[] { new BigInteger("124"), new BigInteger("4") };
        assert process.doProcess(Location.unknownLocation(), ">>>", bigInts1, optionSet).equals(new BigInteger("-8"));
        assert process.doProcess(Location.unknownLocation(), ">>>", bigInts2, optionSet).equals(new BigInteger("7"));
    }
}