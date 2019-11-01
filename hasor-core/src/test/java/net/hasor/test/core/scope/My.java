package net.hasor.test.core.scope;
import javax.inject.Scope;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Scope
public @interface My {
}