package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelCommand;

@FunctionalInterface
public interface TelCloseListener extends java.util.EventListener {
    /**
     * 触发SessionClose，若想取消本次 session close，可以设置  trigger.getSession().setAttribute(CLOSE_SESSION, "false") 来取消
     * @param trigger 触发 close 的命令
     * @param afterSeconds 倒计时时间
     */
    public void onClose(TelCommand trigger, int afterSeconds);
}