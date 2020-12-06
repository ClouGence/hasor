// ========================================================================
// Copyright (c) 2004-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at 
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses. 
// ========================================================================
package net.hasor.utils.json;
import net.hasor.utils.json.JSON.Convertor;
import net.hasor.utils.json.JSON.Output;

import java.util.Map;
/**
 *
 */
public class JSONPojoConvertorFactory implements Convertor {
    private final JSON    _json;
    private final boolean _fromJson;
    public JSONPojoConvertorFactory(JSON json) {
        if (json == null) {
            throw new IllegalArgumentException();
        }
        _json = json;
        _fromJson = true;
    }
    /* ------------------------------------------------------------ */
    /**
     * @param json The JSON instance to use
     * @param fromJSON If true, the class name of the objects is included
     * in the generated JSON and is used to instantiate the object when
     * JSON is parsed (otherwise a Map is used).
     */
    public JSONPojoConvertorFactory(JSON json, boolean fromJSON) {
        if (json == null) {
            throw new IllegalArgumentException();
        }
        _json = json;
        _fromJson = fromJSON;
    }
    /* ------------------------------------------------------------ */
    public void toJSON(Object obj, Output out) {
        String clsName = obj.getClass().getName();
        Convertor convertor = _json.getConvertorFor(clsName);
        if (convertor == null) {
            try {
                Class cls = Loader.loadClass(JSON.class, clsName);
                convertor = new JSONPojoConvertor(cls, _fromJson);
                _json.addConvertorFor(clsName, convertor);
            } catch (ClassNotFoundException e) {
                JSON.logger.warning(e.getMessage());
            }
        }
        if (convertor != null) {
            convertor.toJSON(obj, out);
        }
    }
    public Object fromJSON(Map object) {
        Map map = object;
        String clsName = (String) map.get("class");
        if (clsName != null) {
            Convertor convertor = _json.getConvertorFor(clsName);
            if (convertor == null) {
                try {
                    Class cls = Loader.loadClass(JSON.class, clsName);
                    convertor = new JSONPojoConvertor(cls, _fromJson);
                    _json.addConvertorFor(clsName, convertor);
                } catch (ClassNotFoundException e) {
                    JSON.logger.warning(e.getMessage());
                }
            }
            if (convertor != null) {
                return convertor.fromJSON(object);
            }
        }
        return map;
    }
}
