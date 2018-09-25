package net.hasor.core.container.inject;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.core.container.beans.CallInitBean;
public class InjectMembersBean extends CallInitBean implements InjectMembers {
    @Override
    public void doInject(AppContext appContext) throws Throwable {
        super.init();
    }
}
