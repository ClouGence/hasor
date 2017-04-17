package net.hasor.graphql;
import java.lang.annotation.*;
/**
 * Created by yongchun.zyc on 2017/4/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface GraphUDF {
    public String value() default "";
}