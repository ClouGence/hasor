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
package net.hasor.plugins.jfinal;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import org.more.util.BeanUtils;
import org.more.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.List;
/**
 *
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractModel<M extends AbstractModel<M>> extends Model<M> implements IBean {
    public static interface PersistentFilter {
        boolean doPersistent(Field field, Object oldValue, Object newValue);
    }
    //
    protected Model<M> setup(PersistentFilter filter) {
        Class<?> oriType = ClassUtils.getSuperClassGenricType(this.getClass(), 0);
        oriType = (oriType == null) ? this.getClass() : oriType;
        List<Field> allFields = BeanUtils.findALLFields(oriType);
        for (Field field : allFields) {
            if (field.getDeclaringClass().isAssignableFrom(Model.class))
                continue;
            if (field.getDeclaringClass().isAssignableFrom(AbstractModel.class))
                continue;
            //
            String name = field.getName();
            Object newValue = BeanUtils.readPropertyOrField(this, name);
            if (filter.doPersistent(field, this.get(name), newValue)) {
                this.set(name, newValue);
            }
        }
        return this;
    }
    /** 持久化所有值 */
    public Model<M> setupAll() {
        return this.setup(new PersistentFilter() {
            public boolean doPersistent(Field field, Object oldValue, Object newValue) {
                return true;
            }
        });
    }
    /** 持久化非Blank的值 */
    public Model<M> setupIgnoreEmpty() {
        return this.setup(new PersistentFilter() {
            public boolean doPersistent(Field field, Object oldValue, Object newValue) {
                return !(newValue == null || "".equals(newValue));
            }
        });
    }
    /** 持久化非空的值，包括持久化空字符串。 */
    public Model<M> setupIgnoreNull() {
        return this.setup(new PersistentFilter() {
            public boolean doPersistent(Field field, Object oldValue, Object newValue) {
                return !(newValue == null);
            }
        });
    }
    /** 持久化填充oldValue为空的值。 */
    public Model<M> setupFillEmpty() {
        return this.setup(new PersistentFilter() {
            public boolean doPersistent(Field field, Object oldValue, Object newValue) {
                return !(oldValue == null || "".equals(oldValue));
            }
        });
    }
}