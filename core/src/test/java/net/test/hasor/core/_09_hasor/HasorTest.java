package net.test.hasor.core._09_hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import org.junit.Test;
/**
 * Created by yongchun.zyc on 2017/2/25.
 */
public class HasorTest {
    @Test
    public void hasorTest() {
        //
        AppContext appContext = Hasor.create().build();
    }
}