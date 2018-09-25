package net.hasor.core.container.inject;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.core.container.beans.CallInitBean;
public class InjectMembersThrowBean extends CallInitBean implements InjectMembers {
    @Override
    public void doInject(AppContext appContext) throws Throwable {
        super.init();
        throw new RuntimeException("testError");
    }
}
