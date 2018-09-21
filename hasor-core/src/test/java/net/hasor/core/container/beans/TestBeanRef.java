package net.hasor.core.container.beans;
import net.hasor.core.Inject;
//
public class TestBeanRef {
    @Inject
    private TestBean testBean;
    public TestBean getTestBean() {
        return testBean;
    }
    public void setTestBean(TestBean testBean) {
        this.testBean = testBean;
    }
}
