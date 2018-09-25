package net.hasor.core.container.inject;
import net.hasor.core.ConstructorBy;
import net.hasor.core.Inject;
import net.hasor.core.Type;
import net.hasor.core.container.beans.TestBean;
//
public class ByIDConstructorTestBeanRef {
    private TestBean testBean;
    @ConstructorBy
    public ByIDConstructorTestBeanRef(@Inject(value = "testBean", byType = Type.ByID) TestBean testBean) {
        this.testBean = testBean;
    }
    //
    public TestBean getTestBean() {
        return testBean;
    }
}
