/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hasor.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hasor.context.HasorModule;
/**
 * 标志该类注册到系统初始化过程，该类在标记注解时必须实现{@link HasorModule}接口。
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Module {
    /**默认名称，该名称在系统控制台用于管理显示用途。*/
    public String displayName() default "";
    /**对该类的描述信息。*/
    public String description() default "";
    /**启动顺序默认值0，该值越大表示启动顺序越延后。提示：负值等同于“0”。*/
    public int startIndex() default 0;
    /*级别定义：所有级别都是相对于Integet的最小值。*/
    /**级别0：1~99，表示开始点：1*/
    public static int Lv_0    = Integer.MIN_VALUE + 1;  //
    /**级别0：1~99，表示结束点：99*/
    public static int Lv_0Max = Integer.MIN_VALUE + 99; //
    /**级别1：101~199，表示开始点：101*/
    public static int Lv_1    = Integer.MIN_VALUE + 101; //
    /**级别1：101~199，表示结束点：199*/
    public static int Lv_1Max = Integer.MIN_VALUE + 199; //
    /**级别2：201~299，表示开始点：201*/
    public static int Lv_2    = Integer.MIN_VALUE + 201; //
    /**级别2：201~299，表示结束点：299*/
    public static int Lv_2Max = Integer.MIN_VALUE + 299; //
    /**级别3：301~399，表示开始点：301*/
    public static int Lv_3    = Integer.MIN_VALUE + 301; //
    /**级别3：301~399，表示结束点：399*/
    public static int Lv_3Max = Integer.MIN_VALUE + 399; //
    /**级别4：401~499，表示开始点：401*/
    public static int Lv_4    = Integer.MIN_VALUE + 401; //
    /**级别4：401~499，表示结束点：499*/
    public static int Lv_4Max = Integer.MIN_VALUE + 499; //
    /**级别5：501~599，表示开始点：501*/
    public static int Lv_5    = Integer.MIN_VALUE + 501; //
    /**级别5：501~599，表示结束点：599*/
    public static int Lv_5Max = Integer.MIN_VALUE + 599; //
    /**级别6：601~699，表示开始点：601*/
    public static int Lv_6    = Integer.MIN_VALUE + 601; //
    /**级别6：601~699，表示结束点：699*/
    public static int Lv_6Max = Integer.MIN_VALUE + 699; //
    /**级别7：701~799，表示开始点：701*/
    public static int Lv_7    = Integer.MIN_VALUE + 701; //
    /**级别7：701~799，表示结束点：799*/
    public static int Lv_7Max = Integer.MIN_VALUE + 799; //
    /**级别8：801~899，表示开始点：801*/
    public static int Lv_8    = Integer.MIN_VALUE + 801; //
    /**级别8：801~899，表示结束点：899*/
    public static int Lv_8Max = Integer.MIN_VALUE + 899; //
    /**级别9：901~999，表示开始点：901*/
    public static int Lv_9    = Integer.MIN_VALUE + 901; //
    /**级别9：901~999，表示结束点：999*/
    public static int Lv_9Max = Integer.MIN_VALUE + 999; //
}