package net.hasor.test.core.docs;
import net.hasor.core.ConstructorBy;
import net.hasor.core.Inject;

public class CustomBean {
    private FunBean funBean = null;

    @ConstructorBy
    public CustomBean(@Inject() FunBean funBean) {
        this.funBean = funBean;
    }

    public FunBean callFoo() {
        return this.funBean.foo();
    }
}