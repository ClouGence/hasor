package net.hasor.core.container.inject;
import net.hasor.core.Inject;
import net.hasor.core.container.beans.TestBean;
//
public class ByNameTestBeanRef {
    @Inject("testBean")
    private TestBean testBean;
    public TestBean getTestBean() {
        return testBean;
    }
    public void setTestBean(TestBean testBean) {
        this.testBean = testBean;
    }
}
