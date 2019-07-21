package net.hasor.test.beans.scope;
import javax.inject.Scope;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Scope
public @interface My {
}