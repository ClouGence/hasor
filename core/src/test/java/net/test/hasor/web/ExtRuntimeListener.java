package net.test.hasor.web;
import net.hasor.core.Hasor;
import net.hasor.web.startup.RuntimeListener;

import javax.servlet.ServletContext;
/**
 * Created by yongchun.zyc on 2017/2/25.
 */
public class ExtRuntimeListener extends RuntimeListener {
    protected Hasor createAppContext(ServletContext sc) throws Throwable {
        Hasor hasor = super.createAppContext(sc);
        hasor.setMainSettings(".....");
        return hasor;
    }
}