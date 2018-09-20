package net.hasor.core.container;
import net.hasor.core.Inject;
//
public class RefBean {
    @Inject
    private TestBean testBean;
    public TestBean getTestBean() {
        return testBean;
    }
    public void setTestBean(TestBean testBean) {
        this.testBean = testBean;
    }
}
