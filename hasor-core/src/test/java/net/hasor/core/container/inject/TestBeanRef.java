package net.hasor.core.container.inject;
import net.hasor.core.Inject;
import net.hasor.core.container.beans.TestBean;
//
public class TestBeanRef {
    @Inject
    private TestBean testBean;
    private String   paramName;
    public TestBean getTestBean() {
        return testBean;
    }
    public void setTestBean(TestBean testBean) {
        this.testBean = testBean;
    }
    public String getParamName() {
        return paramName;
    }
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}