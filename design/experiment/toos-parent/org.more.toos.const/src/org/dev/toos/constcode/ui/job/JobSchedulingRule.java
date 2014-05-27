package org.dev.toos.constcode.ui.job;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
/**
 * 
 * @version : 2013-2-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class JobSchedulingRule implements ISchedulingRule {
    public static JobSchedulingRule JobRule = new JobSchedulingRule();
    @Override
    public boolean isConflicting(ISchedulingRule rule) {
        return this.equals(rule);
    }
    @Override
    public boolean contains(ISchedulingRule rule) {
        return this.equals(rule);
    }
}