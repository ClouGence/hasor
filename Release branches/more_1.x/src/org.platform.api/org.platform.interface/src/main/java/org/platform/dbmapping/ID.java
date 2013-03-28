package org.platform.dbmapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 表示一个数据库表的主键
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ID {
    /**映射的表列名。*/
    public String column() default "";
    /**该字段是否允许空值。*/
    public boolean isNull() default true;
    /**最大设置长度。*/
    public int length() default 1000;
    /**默认值。*/
    public String defaultValue() default "";
    /**字段是否参与更新。*/
    public boolean updateMode() default true;
    /**字段是否参与插入。*/
    public boolean insertMode() default true;
    /**字段是否为懒加载。*/
    public boolean lazy() default false;
    /**数据库使用的数据类型。*/
    public DBType dbType() default DBType.UUID;
    //
    //
    /**使用的主键生成策略。*/
    public String keyGenerator() default "uuid.string";
}