package net.hasor.solon.boot.test;

import net.hasor.solon.boot.EnableHasor;
import org.noear.solon.annotation.Import;

@Import(scanPackages = { "net.hasor.test.spring.mod1" })
@EnableHasor(useProperties = false)
public class BootShareHasor_2 {
}
