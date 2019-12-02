package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Option;
import net.hasor.dataql.runtime.OptionSet;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

public class BasicSymbolTest extends AbstractTestResource {
    private OperatorManager om         = OperatorManager.defaultManager();
    private Option          optionSet  = new OptionSet();
    private Option          decimalSet = new OptionSet() {{
        setOption(Option.USE_DECIMAL, true);
    }};

    @Test
    public void uo_test() throws Exception {
        OperatorProcess process = null;
        process = om.findUnaryProcess("!", Boolean.class);
        assert !((Boolean) process.doProcess("!", new Object[] { true }, optionSet));
        assert ((Boolean) process.doProcess("!", new Object[] { false }, optionSet));
        process = om.findUnaryProcess("!", Boolean.TYPE);
        assert !((Boolean) process.doProcess("!", new Object[] { true }, optionSet));
        assert ((Boolean) process.doProcess("!", new Object[] { false }, optionSet));
        //
        process = om.findUnaryProcess("-", Number.class);
        assert ((Byte) process.doProcess("-", new Object[] { (byte) 1 }, optionSet)) == -1;
        assert ((Short) process.doProcess("-", new Object[] { (short) -1 }, optionSet)) == 1;
        assert ((Integer) process.doProcess("-", new Object[] { (int) 10 }, optionSet)) == -10;
        assert ((Long) process.doProcess("-", new Object[] { (long) -11 }, optionSet)) == 11;
        assert ((Float) process.doProcess("-", new Object[] { (float) 10.3 }, optionSet)) == -10.3f;
        assert ((Double) process.doProcess("-", new Object[] { (double) -11.44 }, optionSet)) == 11.44;
        assert ((BigInteger) process.doProcess("-", new Object[] { BigInteger.valueOf(1234) }, optionSet)).intValue() == -1234;
        assert ((BigDecimal) process.doProcess("-", new Object[] { BigDecimal.valueOf(-123.4d) }, optionSet)).doubleValue() == 123.4;
    }

    @Test
    public void plus_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("+", Integer.class, Integer.class);
        assert (Integer) process.doProcess("+", new Object[] { 1, 2 }, optionSet) == 3;
        //
        process = om.findDyadicProcess("+", Integer.class, Long.class);
        assert (Long) process.doProcess("+", new Object[] { 1, 2l }, optionSet) == 3;
        //
        process = om.findDyadicProcess("+", BigInteger.class, BigInteger.class);
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("999999999999999999999999999999999999999999999999999999999999"),//
                new BigInteger("8888888888888888888888888888888888888")//
        };
        BigInteger finalInt = BigInteger.ZERO.add(bigInts[0]).add(bigInts[1]);
        assert process.doProcess("+", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("+", Double.class, Double.class);
        assert (Double) process.doProcess("+", new Object[] { 1.2, 3.8 }, optionSet) == 5.0;
        //
        process = om.findDyadicProcess("+", Float.class, Float.class);
        assert (Float) process.doProcess("+", new Object[] { 1.2f, 3.8f }, optionSet) == 5.0;
        //
        process = om.findDyadicProcess("+", Float.class, Float.class);
        assert process.doProcess("+", new Object[] { 1.2f, 3.8f }, decimalSet).equals(BigDecimal.valueOf(5));
    }

    @Test
    public void sub_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("-", Integer.class, Integer.class);
        assert (Integer) process.doProcess("-", new Object[] { 1, 2 }, optionSet) == -1;
        //
        process = om.findDyadicProcess("-", Integer.class, Long.class);
        assert (Long) process.doProcess("-", new Object[] { 1, 2l }, optionSet) == -1;
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
        assert process.doProcess("-", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("-", Double.class, Double.class);
        assert (Double) process.doProcess("-", new Object[] { 1.2, 3.8 }, optionSet) != -2.6; // 浮点数计算精度导致
        //
        process = om.findDyadicProcess("-", Float.class, Float.class);
        assert process.doProcess("-", new Object[] { 1.2f, 3.8f }, optionSet).toString().equals("-2.6");
        //
        process = om.findDyadicProcess("-", Double.class, Double.class);
        assert process.doProcess("-", new Object[] { 1.2, 3.8 }, decimalSet).equals(BigDecimal.valueOf(-2.6));
    }

    @Test
    public void mul_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("*", Integer.class, Integer.class);
        assert (Integer) process.doProcess("*", new Object[] { 2, 3 }, optionSet) == 6;
        //
        process = om.findDyadicProcess("*", Integer.class, Long.class);
        assert (Long) process.doProcess("*", new Object[] { 2, 3l }, optionSet) == 6;
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
        assert process.doProcess("*", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("*", Float.class, Float.class);
        assert (Float) process.doProcess("*", new Object[] { 1.2f, 2f }, optionSet) == 2.4f;
        //
        process = om.findDyadicProcess("*", Double.class, Double.class);
        assert (Double) process.doProcess("*", new Object[] { 0.14, 100 }, optionSet) != 14; // 浮点数计算精度导致
        //
        process = om.findDyadicProcess("*", Double.class, Double.class);
        assert process.doProcess("*", new Object[] { 0.14, 100 }, decimalSet).equals(BigDecimal.valueOf(14));
    }

    @Test
    public void div_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("/", Integer.class, Integer.class);
        assert (Integer) process.doProcess("/", new Object[] { 2, 3 }, optionSet) == 0;
        assert process.doProcess("/", new Object[] { 2.0, 3 }, optionSet).toString().equals("0.6666666666666666");
        //
        process = om.findDyadicProcess("/", Integer.class, Long.class);
        assert (Long) process.doProcess("/", new Object[] { 2, 3l }, optionSet) == 0;
        assert process.doProcess("/", new Object[] { 2.0, 3l }, optionSet).toString().equals("0.6666666666666666");
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
        assert process.doProcess("/", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("/", Float.class, Float.class);
        assert process.doProcess("/", new Object[] { 2.2f, 100f }, optionSet).equals(Float.parseFloat("0.022"));
        //
        process = om.findDyadicProcess("/", Double.class, Double.class);
        assert (Double) process.doProcess("/", new Object[] { 2.2, 100 }, optionSet) != 0.022; // 浮点数计算精度导致
        //
        process = om.findDyadicProcess("/", Double.class, Double.class);
        assert process.doProcess("/", new Object[] { 2.2, 100 }, decimalSet).equals(BigDecimal.valueOf(0.022));
    }

    @Test
    public void mod_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("%", Integer.class, Integer.class);
        assert (Integer) process.doProcess("%", new Object[] { 2, 3 }, optionSet) == 2;
        assert process.doProcess("%", new Object[] { 2.0, 3 }, optionSet).toString().equals("2.0");
        //
        process = om.findDyadicProcess("%", Integer.class, Long.class);
        assert (Long) process.doProcess("%", new Object[] { 2, 3l }, optionSet) == 2;
        assert process.doProcess("%", new Object[] { 2.0, 3l }, optionSet).toString().equals("2.0");
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
        assert process.doProcess("%", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("%", Float.class, Float.class);
        assert process.doProcess("%", new Object[] { 6f, 4f }, optionSet).toString().equals("2.0");
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6"),        //
                new BigDecimal("4")         //
        };
        process = om.findDyadicProcess("%", BigDecimal.class, BigDecimal.class);
        assert process.doProcess("%", bigDecs, optionSet).equals(new BigDecimal("2"));
    }

    @Test
    public void div2_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("\\", Integer.class, Integer.class);
        assert (Integer) process.doProcess("\\", new Object[] { 6, 3 }, optionSet) == 2;
        assert process.doProcess("\\", new Object[] { 6.0, 3 }, optionSet).toString().equals("2");
        //
        process = om.findDyadicProcess("\\", Integer.class, Long.class);
        assert (Long) process.doProcess("\\", new Object[] { 6, 3l }, optionSet) == 2;
        assert process.doProcess("\\", new Object[] { 6.0, 3l }, optionSet).toString().equals("2");
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
        assert process.doProcess("\\", bigInts, optionSet).equals(finalInt);
        //
        process = om.findDyadicProcess("\\", Float.class, Float.class);
        assert process.doProcess("\\", new Object[] { 6f, 3f }, optionSet).equals(2);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6"),        //
                new BigDecimal("3")         //
        };
        process = om.findDyadicProcess("\\", BigDecimal.class, BigDecimal.class);
        assert process.doProcess("\\", bigDecs, optionSet).equals(new BigDecimal("2"));
    }

    @Test
    public void plus2_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("+", String.class, Long.class);
        assert process.doProcess("+", new Object[] { "1", 2l }, optionSet).equals("12");
        //
        process = om.findDyadicProcess("+", String.class, HashMap.class);
        assert process.doProcess("+", new Object[] { "1", new HashMap<>() }, optionSet).equals("1{}");
    }

    @Test
    public void eq_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("==", String.class, Long.class);
        assert process.doProcess("==", new Object[] { "1", 2l }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("==", String.class, HashMap.class);
        assert process.doProcess("==", new Object[] { "1", new HashMap<>() }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("==", String.class, String.class);
        assert process.doProcess("==", new Object[] { "1", "1" }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("==", Long.class, Long.class);
        assert process.doProcess("==", new Object[] { 1L, 1L }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("==", Float.class, Float.class);
        assert process.doProcess("==", new Object[] { 1.2f, 1.2f }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("==", Double.class, Double.class);
        assert process.doProcess("==", new Object[] { 1.2d, 1.2d }, optionSet).equals(true);
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
        assert process.doProcess("==", bigInts, optionSet).equals(true);
        process = om.findDyadicProcess("==", BigDecimal.class, BigDecimal.class);
        assert process.doProcess("==", bigDecs, optionSet).equals(true);
    }

    @Test
    public void ne_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("!=", String.class, Long.class);
        assert process.doProcess("!=", new Object[] { "1", 2l }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, HashMap.class);
        assert process.doProcess("!=", new Object[] { "1", new HashMap<>() }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, String.class);
        assert process.doProcess("!=", new Object[] { "1", "1" }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("!=", Long.class, String.class);
        assert process.doProcess("!=", new Object[] { 1L, "1" }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", Float.class, Boolean.class);
        assert process.doProcess("!=", new Object[] { 1.2f, true }, optionSet).equals(false); // 任何非零值都可以认为 是true
        //
        process = om.findDyadicProcess("!=", Double.class, Double.class);
        assert process.doProcess("!=", new Object[] { 1.2d, 1.2d }, optionSet).equals(false);
    }

    @Test
    public void ne2_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("!=", String.class, Long.class);
        assert process.doProcess("!=", new Object[] { "1", 2l }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, HashMap.class);
        assert process.doProcess("!=", new Object[] { "1", new HashMap<>() }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("!=", String.class, String.class);
        assert process.doProcess("!=", new Object[] { "1", "1" }, optionSet).equals(false);
    }

    @Test
    public void logicAnd_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.class);
        assert process.doProcess("&&", new Object[] { true, true }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.class);
        assert process.doProcess("&&", new Object[] { true, false }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.class);
        assert process.doProcess("&&", new Object[] { false, false }, optionSet).equals(false);
        process = om.findDyadicProcess("&&", Boolean.class, Boolean.TYPE);
        assert process.doProcess("&&", new Object[] { false, false }, optionSet).equals(false);
    }

    @Test
    public void logicOr_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("||", Boolean.class, Boolean.class);
        assert process.doProcess("||", new Object[] { true, true }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("||", Boolean.class, Boolean.class);
        assert process.doProcess("||", new Object[] { true, false }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("||", Boolean.class, Boolean.class);
        assert process.doProcess("||", new Object[] { false, false }, optionSet).equals(false);
        process = om.findDyadicProcess("||", Boolean.class, Boolean.TYPE);
        assert process.doProcess("||", new Object[] { false, false }, optionSet).equals(false);
    }

    @Test
    public void and_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("&", Boolean.class, Boolean.class);
        assert process.doProcess("&", new Object[] { true, true }, optionSet).equals(1);
        //
        process = om.findDyadicProcess("&", Boolean.class, Boolean.class);
        assert process.doProcess("&", new Object[] { true, false }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("&", Boolean.class, Boolean.class);
        assert process.doProcess("&", new Object[] { false, false }, optionSet).equals(0);
        process = om.findDyadicProcess("&", Boolean.class, Boolean.TYPE);
        assert process.doProcess("&", new Object[] { false, false }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("&", Integer.class, Integer.class);
        assert process.doProcess("&", new Object[] { 8, 1 }, optionSet).equals(0);
        //
        process = om.findDyadicProcess("&", Integer.class, Integer.class);
        assert process.doProcess("&", new Object[] { 8, 9 }, optionSet).equals(8);
        //
        process = om.findDyadicProcess("&", Long.class, Integer.class);
        assert process.doProcess("&", new Object[] { 8L, 9 }, optionSet).equals(8L);
        //
        process = om.findDyadicProcess("&", Long.class, Integer.class);
        assert process.doProcess("&", new Object[] { BigInteger.valueOf(8), 9 }, optionSet).equals(BigInteger.valueOf(8));
    }

    @Test
    public void gt_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess(">", Integer.class, Integer.class);
        assert process.doProcess(">", new Object[] { 8, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">", Long.class, Integer.class);
        assert process.doProcess(">", new Object[] { 12L, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">", Float.class, Float.class);
        assert process.doProcess(">", new Object[] { 1.2f, 2.2f }, optionSet).equals(false);
        //
        process = om.findDyadicProcess(">", Double.class, Double.class);
        assert process.doProcess(">", new Object[] { 3.2d, 2.2d }, optionSet).equals(true);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("123")       //
        };
        process = om.findDyadicProcess(">", BigInteger.class, BigInteger.class);
        assert process.doProcess(">", bigInts, optionSet).equals(true);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess(">", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(">", bigDecs, optionSet).equals(true);
    }

    @Test
    public void gteq_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess(">=", Integer.class, Integer.class);
        assert process.doProcess(">=", new Object[] { 1, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">=", Long.class, Integer.class);
        assert process.doProcess(">=", new Object[] { 12L, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess(">=", Float.class, Float.class);
        assert process.doProcess(">=", new Object[] { 1.2f, 2.2f }, optionSet).equals(false);
        //
        process = om.findDyadicProcess(">=", Double.class, Double.class);
        assert process.doProcess(">=", new Object[] { 3.2d, 2.2d }, optionSet).equals(true);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess(">=", BigInteger.class, BigInteger.class);
        assert process.doProcess(">=", bigInts, optionSet).equals(true);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("6.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess(">=", BigDecimal.class, BigDecimal.class);
        assert process.doProcess(">=", bigDecs, optionSet).equals(true);
    }

    @Test
    public void lt_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("<", Integer.class, Integer.class);
        assert process.doProcess("<", new Object[] { 1, 2 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<", Long.class, Integer.class);
        assert process.doProcess("<", new Object[] { 12L, 1 }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("<", Float.class, Float.class);
        assert process.doProcess("<", new Object[] { 1.2f, 2.2f }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<", Double.class, Double.class);
        assert process.doProcess("<", new Object[] { 3.2d, 2.2d }, optionSet).equals(false);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess("<", BigInteger.class, BigInteger.class);
        assert process.doProcess("<", bigInts, optionSet).equals(false);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("5.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess("<", BigDecimal.class, BigDecimal.class);
        assert process.doProcess("<", bigDecs, optionSet).equals(true);
    }

    @Test
    public void lteq_test() throws Exception {
        OperatorProcess process = null;
        process = om.findDyadicProcess("<=", Integer.class, Integer.class);
        assert process.doProcess("<=", new Object[] { 1, 1 }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<=", Long.class, Integer.class);
        assert process.doProcess("<=", new Object[] { 12L, 1 }, optionSet).equals(false);
        //
        process = om.findDyadicProcess("<=", Float.class, Float.class);
        assert process.doProcess("<=", new Object[] { 2.2f, 2.2f }, optionSet).equals(true);
        //
        process = om.findDyadicProcess("<=", Double.class, Double.class);
        assert process.doProcess("<=", new Object[] { 3.2d, 2.2d }, optionSet).equals(false);
        //
        BigInteger[] bigInts = new BigInteger[] {//
                new BigInteger("124"),      //
                new BigInteger("124")       //
        };
        process = om.findDyadicProcess("<=", BigInteger.class, BigInteger.class);
        assert process.doProcess("<=", bigInts, optionSet).equals(true);
        //
        BigDecimal[] bigDecs = new BigDecimal[] {//
                new BigDecimal("5.2"),      //
                new BigDecimal("6.1")       //
        };
        process = om.findDyadicProcess("<=", BigDecimal.class, BigDecimal.class);
        assert process.doProcess("<=", bigDecs, optionSet).equals(true);
    }
}
//        // .二元，位运算
//        om.registryOperator("|", classSet, classSet, new BinaryDOP());
//        om.registryOperator("^", classSet, classSet, new BinaryDOP());
//        om.registryOperator("<<", classSet, classSet, new BinaryDOP());
//        om.registryOperator(">>", classSet, classSet, new BinaryDOP());
//        om.registryOperator(">>>", classSet, classSet, new BinaryDOP());
//        // .二元，数值比较运算
//        om.registryOperator("==", classSet, classSet, new CompareDOP());
//        om.registryOperator("!=", classSet, classSet, new CompareDOP());