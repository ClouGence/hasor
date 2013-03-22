package org.platform.api.upfile;
/**
 * 上传文件的处理类，需要实现{@link IUpFile}接口。
 * @see org.platform.api.upfile.IUpFile
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public @interface UpLoad {
    /**上传文件的注册名。*/
    public String value() default "";
    /**上传文件允许的最大长度。默认：-1(无穷大)*/
    public long maxSize() default -1;
    /**上传文件允许的最小长度。默认：0*/
    public long minSize() default 0;
    /**是否允许多文件上传。*/
    public boolean allowMulti() default false;
    /**允许的文件类型。*/
    public String[] allowFiles();
    /**上传处理要求设定。默认:Require.Simple*/
    public Require require() default Require.Simple;
    /**
     * 上传处理要求设定枚举
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Require {
        /**简单上传，无特殊要求。Level 0*/
        Simple,
        /**
         * 需要通过策略检查。Level 1
         * @see org.platform.api.upfile.IUpFilePolicy
         */
        PassPolicy
    }
}