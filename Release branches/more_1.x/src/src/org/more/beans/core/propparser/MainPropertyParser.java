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
package org.more.beans.core.propparser;
import org.more.DoesSupportException;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.Prop;
import org.more.beans.info.PropArray;
import org.more.beans.info.PropList;
import org.more.beans.info.PropMap;
import org.more.beans.info.PropRefValue;
import org.more.beans.info.PropSet;
import org.more.beans.info.PropVarValue;
/**
 * 该类是整个属性解析器的入口，它也实现了{@link PropertyParser}接口但是使用MainPropertyParser对象时使用它的简化parser方法的重载方法。
 * 重载方法减少了一些不必要的参数提供，这些参数已经被封装在MainPropertyParser对象中。
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class MainPropertyParser implements PropertyParser {
    //========================================================================================Field
    private ValueTypeParser     valueTypeParser = new ValueTypeParser(); //
    private RefTypeParser       refTypeParser   = new RefTypeParser();  //
    private ListParser          listParser      = new ListParser();     //
    private MapParser           mapParser       = new MapParser();      //
    private SetParser           setParser       = new SetParser();      //
    private ArrayParser         arrayParser     = new ArrayParser();    //
    private ResourceBeanFactory factory         = null;                 //
    //==================================================================================Constructor
    /**创建MainPropertyParser对象，必须指定ResourceBeanFactory对象。*/
    public MainPropertyParser(ResourceBeanFactory factory) {
        //        if (factory == null)
        //            throw new NullPointerException("必须指定factory参数。");
        this.factory = factory;
    }
    //==========================================================================================Job
    /**
     * 该方法是{@link PropertyParser}接口方法的简化重载方法，解析属性并且返回对属性片段的解析结果。
     * @param context 当前要解析的属性是哪个对象上的属性，如果解析的属性是构造方法属性则由于目标对象还没有创建所以在构造方法属性中context是null。除此之外工厂方法也一样。
     * @param contextParams 创建bean时候附带的环境参数，这个参数是ResourceBeanFactory.getBean()时传递的参数。
     * @param prop 要解析的属性片段。
     * @param propContext 该属性属性片段所属的那个{@link BeanDefinition}中定义的属性对象。如果属性片段有多个层次结构但是解析这些片段之后的需要注入的属性却是不变的，这个属性的定义就是该参数。
     * @param definition 参数propContext所属的那个{@link BeanDefinition}对象。
     * @return 返回解析的结果。
     * @throws Exception 解析过程发生异常。
     * @throws DoesSupportException 如果解析的prop对象类型不是已经受支持的类型将会引发该异常。
     */
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition) throws Exception {
        return this.parser(context, contextParams, prop, propContext, definition, this.factory, null);
    }
    /**
     * 该方法是{@link PropertyParser}接口方法的简化重载方法，解析属性并且返回对属性片段的解析结果解析结果是属性类型。
     * @param context 当前要解析的属性是哪个对象上的属性，如果解析的属性是构造方法属性则由于目标对象还没有创建所以在构造方法属性中context是null。除此之外工厂方法也一样。
     * @param contextParams 创建bean时候附带的环境参数，这个参数是ResourceBeanFactory.getBean()时传递的参数。
     * @param prop 要解析的属性片段。
     * @param propContext 该属性属性片段所属的那个{@link BeanDefinition}中定义的属性对象。如果属性片段有多个层次结构但是解析这些片段之后的需要注入的属性却是不变的，这个属性的定义就是该参数。
     * @param definition 参数propContext所属的那个{@link BeanDefinition}对象。
     * @return 返回解析的结果。
     * @throws Exception 解析过程发生异常。
     * @throws DoesSupportException 如果解析的prop对象类型不是已经受支持的类型将会引发该异常。
     */
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition) throws Exception {
        return this.parserType(context, contextParams, prop, propContext, definition, this.factory, null);
    }
    @Override
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        if (prop == null)
            return null;
        else if (prop instanceof PropVarValue)
            return valueTypeParser.parser(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropRefValue)
            return refTypeParser.parser(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropList)
            return listParser.parser(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropMap)
            return mapParser.parser(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropSet)
            return setParser.parser(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropArray)
            return arrayParser.parser(context, contextParams, prop, propContext, definition, factory, this);
        else
            throw new DoesSupportException("目前暂不支持[" + prop.getClass() + "]属性类型解析器的支持。");
    }
    @Override
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        String propType = propContext.getPropType();
        if (propType != null)
            return Prop.getType(propType, factory.getBeanClassLoader());
        //
        if (prop == null)
            return factory.getBeanClassLoader().loadClass(propContext.getPropType());
        else if (prop instanceof PropVarValue)
            return valueTypeParser.parserType(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropRefValue)
            return refTypeParser.parserType(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropList)
            return listParser.parserType(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropMap)
            return mapParser.parserType(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropSet)
            return setParser.parserType(context, contextParams, prop, propContext, definition, factory, this);
        else if (prop instanceof PropArray)
            return arrayParser.parserType(context, contextParams, prop, propContext, definition, factory, this);
        else
            throw new DoesSupportException("目前暂不支持自定义属性类型解析器的支持。");
    }
}