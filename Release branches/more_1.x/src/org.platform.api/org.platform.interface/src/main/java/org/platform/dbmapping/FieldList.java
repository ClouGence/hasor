package org.platform.dbmapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 表示多对一的映射关系。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface FieldList {
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
    public DBType dbType() default DBType.Nvarchar;
    //
    //
    /**设置目标实体的一个属性字段，该字段可以和这个字段组成外键关联。*/
    public String forProperty();
    /**目标实体集合的顺序依照的属性名。*/
    public String sortBy() default "";
    /**目标实体集合的顺序方式。*/
    public String sortMode() default "asc";
    /**附加过滤条件。*/
    public String filter() default "";//"this.userName like 'abc%' and this.attGroup.abc='A'";
}