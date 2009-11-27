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
package org.more.core.copybean.ext;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.more.core.copybean.ConvertType;
import org.more.core.copybean.Copy;
import org.more.core.copybean.PropertyReaderWrite;
/**
 * 某一个属性的深拷贝。
 * Date : 2009-5-15
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class DeepCopy extends Copy {
    /**  */
    private static final long serialVersionUID = 3577120677124915374L;
    /**
     * 拷贝属性到目标PropertyReaderWrite属性读写器中。如果拷贝成功则返回true否则返回fale。
     * @param toObject 准备拷贝的目标对象。
     * @return 如果拷贝成功则返回true否则返回fale。
     * @throws Exception  如果拷贝中发生异常
     */
    @Override
    public boolean copyTo(PropertyReaderWrite toObject) {
        Object obj = this.get();
        if (obj == null) {
            toObject.set(null);
            return true;
        }
        Class<?> obj_type = obj.getClass();
        //
        if (obj_type == String.class || //String
                obj_type == Character.class || obj_type == char.class || //char
                obj_type == Byte.class || obj_type == byte.class || //byte
                obj_type == Short.class || obj_type == short.class || //short
                obj_type == Integer.class || obj_type == int.class || //integer
                obj_type == Long.class || obj_type == long.class || //long
                obj_type == Float.class || obj_type == float.class || //float
                obj_type == Double.class || obj_type == double.class || //double
                obj_type == Boolean.class || obj_type == boolean.class || //boolean
                obj instanceof Date || //boolean
                obj_type.isInterface() == true || //接口，抽象类不执行深拷贝
                //
                obj instanceof List || //Map不执行深拷贝
                obj instanceof Set || //List不执行深拷贝
                obj instanceof Map //Set不执行深拷贝
        ) {} else {
            //执行深拷贝 
            this.copyBeanUtil.copy(obj, toObject.get());
            return true;
        }
        //执行浅拷贝
        //===============================================================
        if (this.canReader() == true && toObject.canWrite() == true) {
            //from -> to
            ConvertType convert = this.getConvertType(this.getPropertyClass(), toObject.getPropertyClass());
            if (convert == null)
                //直接拷贝不转换 
                toObject.set(obj);
            else
                //转换并且拷贝  
                toObject.set(convert.convert(obj));
            return true;
        } else
            return false;
    }
}
