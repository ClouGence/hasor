package net.hasor.graphql;
import java.lang.annotation.*;
/**
 * Created by yongchun.zyc on 2017/4/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
@Documented
public @interface GraphParam {
    public String value();
}
