package org.platform.api.dbmapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 将一个属性映射到数据库列上。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Field {
    /**映射的表列名。*/
    public String column() default "";
    /**该字段是否允许空值。*/
    public boolean isNull() default true;
    /**最大设置长度。*/
    public int length() default 1000;
    /**默认值。*/
    public String defaultValue() default "";
    /**字段是否参与更新。*/
    public boolean update() default true;
    /**字段是否参与插入。*/
    public boolean insert() default true;
    /**字段是否为懒加载。*/
    public boolean lazy() default false;
    /**数据库使用的数据类型。*/
    public DBType dbType() default DBType.Nvarchar;
    //    public Type type() default Type.String;//  "string(default)|float|int|double|long|boolean|datetime|uuid|btye|json|bytes"
    //    /**支持的Java数据类型。*/
    //    public enum Type {
    //        /**字符串*/
    //        String,
    //        /**浮点数*/
    //        Float,
    //        /**整数*/
    //        Integer,
    //        /**双精度数字*/
    //        Double,
    //        /**长整数*/
    //        Long,
    //        /**布尔类型*/
    //        Boolean,
    //        /**时间日期*/
    //        Date,
    //        /**UUID*/
    //        UUID,
    //        /**字节类型。*/
    //        Btye,
    //        /**字节数组*/
    //        Btyes
    //    }
}