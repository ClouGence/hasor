package org.dev.toos.constcode.ui.job;
import org.dev.toos.constcode.model.ConstModelSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
/**
 * 
 * @version : 2013-2-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class RefreshModelJob extends Job {
    private Runnable callBack = null;
    public RefreshModelJob(String name, Runnable callBack) {
        super(name);
        this.setRule(JobSchedulingRule.JobRule);
        this.callBack = callBack;
    }
    @Override
    /**重新载入工作空间中的xml。*/
    public IStatus run(IProgressMonitor monitor) {
        ConstModelSet.refresh(monitor);
        runCallBack();
        return Status.OK_STATUS;
    }
    private void runCallBack() {
        if (callBack != null)
            callBack.run();
    }
}