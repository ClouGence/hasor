package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelContext;

@FunctionalInterface
public interface TelHostPreFinishListener extends java.util.EventListener {
    public void onFinish(TelContext telContext);
}