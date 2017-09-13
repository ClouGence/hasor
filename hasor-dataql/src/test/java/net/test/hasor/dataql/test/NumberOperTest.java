package net.test.hasor.dataql.test;
import net.hasor.dataql.utils.NumberUtils;
import net.test.hasor.dataql.AbstractTaskTest;
import org.junit.Test;

import java.math.BigInteger;
/**
 * Created by yongchun.zyc on 2017/7/17.
 */
public class NumberOperTest extends AbstractTaskTest {
    @Test
    public void mainALL() throws Exception {
        byte byteValue = 111;
        short shortValue = 11111;
        int intValue = 1111111111;
        long longValue = 1111111111111111111L;
        float floatValue = 111111111111111.0f;
        double doubleValue = 111111111111111.0d;
        BigInteger bigValue = BigInteger.valueOf(123L);
        //
        Number number = NumberUtils.add(floatValue, shortValue);
        //
        System.out.println(number);
    }
}