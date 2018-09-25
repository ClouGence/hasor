package net.hasor.core.container.inject;
import net.hasor.core.ConstructorBy;
import net.hasor.core.Inject;
import net.hasor.core.container.beans.TestBean;
//
public class ConstructorTestBeanRef {
    private TestBean testBean;
    @ConstructorBy
    public ConstructorTestBeanRef(@Inject TestBean testBean) {
        this.testBean = testBean;
    }
    //
    public TestBean getTestBean() {
        return testBean;
    }
}
