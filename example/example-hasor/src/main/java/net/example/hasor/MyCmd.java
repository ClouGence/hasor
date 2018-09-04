package net.example.hasor;
import java.lang.annotation.*;
//
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface MyCmd {
    String value();
}