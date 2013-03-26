package org.platform.api.web;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @version : 2013-3-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface WebInitParam {
    /** Name of the initialization parameter */
    public String name();
    /** Value of the initialization parameter */
    public String value();
    /** Description of the initialization parameter */
    public String description() default "";
}