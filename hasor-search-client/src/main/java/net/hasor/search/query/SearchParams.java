/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.search.query;
import java.io.Serializable;
import java.util.Iterator;
import net.hasor.search.SearchException;
import net.hasor.search.utils.StrUtils;
/**
 * SolrParams hold request parameters.
 * @version : 2015年1月22日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class SearchParams implements Serializable {
    private static final long serialVersionUID = 158299592945759124L;
    /** returns the String value of a param, or null if not set */
    public abstract String get(String param);
    /** returns an array of the String values of a param, or null if none */
    public abstract String[] getParams(String param);
    /** returns an Iterator over the parameter names */
    public abstract Iterator<String> getParameterNamesIterator();
    /** returns the value of the param, or def if not set */
    public String get(String param, String def) {
        String val = get(param);
        return val == null ? def : val;
    }
    protected String fpname(String field, String param) {
        return "f." + field + '.' + param;
    }
    /** returns the String value of the field parameter, "f.field.param", or the value for "param" if that is not set. */
    public String getFieldParam(String field, String param) {
        String val = get(fpname(field, param));
        return val != null ? val : get(param);
    }
    /** returns the String value of the field parameter, "f.field.param", or the value for "param" if that is not set.  If that is not set, def */
    public String getFieldParam(String field, String param, String def) {
        String val = get(fpname(field, param));
        return val != null ? val : get(param, def);
    }
    /** returns the String values of the field parameter, "f.field.param", or the values for "param" if that is not set. */
    public String[] getFieldParams(String field, String param) {
        String[] val = getParams(fpname(field, param));
        return val != null ? val : getParams(param);
    }
    /** Returns the Boolean value of the param, or null if not set */
    public Boolean getBool(String param) {
        String val = get(param);
        return val == null ? null : StrUtils.parseBool(val);
    }
    /** Returns the boolean value of the param, or def if not set */
    public boolean getBool(String param, boolean def) {
        String val = get(param);
        return val == null ? def : StrUtils.parseBool(val);
    }
    /** Returns the Boolean value of the field param, or the value for param, or null if neither is set. */
    public Boolean getFieldBool(String field, String param) {
        String val = getFieldParam(field, param);
        return val == null ? null : StrUtils.parseBool(val);
    }
    /** Returns the boolean value of the field param, or the value for param, or def if neither is set. */
    public boolean getFieldBool(String field, String param, boolean def) {
        String val = getFieldParam(field, param);
        return val == null ? def : StrUtils.parseBool(val);
    }
    /** Returns the Integer value of the param, or null if not set */
    public Integer getInt(String param) {
        String val = get(param);
        try {
            return val == null ? null : Integer.valueOf(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the int value of the param, or def if not set */
    public int getInt(String param, int def) {
        String val = get(param);
        try {
            return val == null ? def : Integer.parseInt(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** @return The int value of the field param, or the value for param or <code>null</code> if neither is set.  **/
    public Integer getFieldInt(String field, String param) {
        String val = getFieldParam(field, param);
        try {
            return val == null ? null : Integer.valueOf(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the int value of the field param, or the value for param, or def if neither is set. */
    public int getFieldInt(String field, String param, int def) {
        String val = getFieldParam(field, param);
        try {
            return val == null ? def : Integer.parseInt(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the Float value of the param, or null if not set */
    public Float getFloat(String param) {
        String val = get(param);
        try {
            return val == null ? null : Float.valueOf(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the float value of the param, or def if not set */
    public float getFloat(String param, float def) {
        String val = get(param);
        try {
            return val == null ? def : Float.parseFloat(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the Float value of the param, or null if not set */
    public Double getDouble(String param) {
        String val = get(param);
        try {
            return val == null ? null : Double.valueOf(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the float value of the param, or def if not set */
    public double getDouble(String param, double def) {
        String val = get(param);
        try {
            return val == null ? def : Double.parseDouble(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the float value of the field param. */
    public Float getFieldFloat(String field, String param) {
        String val = getFieldParam(field, param);
        try {
            return val == null ? null : Float.valueOf(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the float value of the field param, or the value for param, or def if neither is set. */
    public float getFieldFloat(String field, String param, float def) {
        String val = getFieldParam(field, param);
        try {
            return val == null ? def : Float.parseFloat(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the float value of the field param. */
    public Double getFieldDouble(String field, String param) {
        String val = getFieldParam(field, param);
        try {
            return val == null ? null : Double.valueOf(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
    /** Returns the float value of the field param, or the value for param, or def if neither is set. */
    public double getFieldDouble(String field, String param, double def) {
        String val = getFieldParam(field, param);
        try {
            return val == null ? def : Double.parseDouble(val);
        } catch (Exception ex) {
            throw new SearchException(ex.getMessage(), ex);
        }
    }
}