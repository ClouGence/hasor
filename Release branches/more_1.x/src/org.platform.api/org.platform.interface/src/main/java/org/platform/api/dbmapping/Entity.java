package org.platform.api.dbmapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 定义一个实体对象
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Entity {
    /**实体名称，默认使用简短类名作为该值。*/
    public String name() default "";
    /**实体映射的表名，默认使用简短类名作为该值。*/
    public String table() default "";
    /***/
    public Mode mode() default Mode.Upate;
    public boolean lazy() default false;
    //
    public static enum Mode {
        /***/
        CreateDrop,
        /***/
        Upate
    }
}