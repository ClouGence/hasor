/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.search.domain;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * A concrete representation of a document within a Solr index.  Unlike a lucene
 * Document, a SolrDocument may have an Object value matching the type defined in
 * schema.xml
 * For indexing documents, use the SolrInputDocument that contains extra information
 * for document and field boosting.
 * @since solr 1.3
 */
public class SearchDocument implements Map<String, Object>, Iterable<Map.Entry<String, Object>>, Serializable {
    private static final long         serialVersionUID = 4862986551952096199L;
    private final Map<String, Object> fields;
    private List<SearchDocument>      childDocuments;
    //
    public SearchDocument() {
        fields = new LinkedHashMap<String, Object>();
    }
    /**
     * @return a list of field names defined in this document - this Collection is directly backed by this SolrDocument.
     * @see #keySet
     */
    public Collection<String> getFieldNames() {
        return this.keySet();
    }
    ///////////////////////////////////////////////////////////////////
    // Add / Set / Remove Fields
    ///////////////////////////////////////////////////////////////////
    /**
     * Remove all fields from the document
     */
    @Override
    public void clear() {
        fields.clear();
        if (childDocuments != null) {
            childDocuments.clear();
        }
    }
    /**
     * Remove all fields with the name
     */
    public boolean removeFields(String name) {
        return this.remove(name) != null;
    }
    /**
     * Set a field with the given object.  If the object is an Array, it will 
     * set multiple fields with the included contents.  This will replace any existing 
     * field with the given name
     */
    @SuppressWarnings("unchecked")
    public void setField(String name, Object value) {
        if (value instanceof Object[]) {
            value = new ArrayList(Arrays.asList((Object[]) value));
        } else if (value instanceof Collection) {
            // nothing
        } else if (value instanceof Iterable) {
            ArrayList<Object> lst = new ArrayList<Object>();
            for (Object o : (Iterable) value) {
                lst.add(o);
            }
            value = lst;
        }
        fields.put(name, value);
    }
    /**
     * This will add a field to the document.  If fields already exist with this
     * name it will append value to the collection. If the value is Collection,
     * each value will be added independently. 
     * 
     * The class type of value and the name parameter should match schema.xml. 
     * schema.xml can be found in conf directory under the solr home by default.
     * 
     * @param name Name of the field, should match one of the field names defined under "fields" tag in schema.xml.
     * @param value Value of the field, should be of same class type as defined by "type" attribute of the corresponding field in schema.xml. 
     */
    @SuppressWarnings("unchecked")
    public void addField(String name, Object value) {
        Object existing = fields.get(name);
        if (existing == null) {
            if (value instanceof Collection) {
                Collection<Object> c = new ArrayList<Object>(3);
                for (Object o : (Collection<Object>) value) {
                    c.add(o);
                }
                this.setField(name, c);
            } else {
                this.setField(name, value);
            }
            return;
        }
        Collection<Object> vals = null;
        if (existing instanceof Collection) {
            vals = (Collection<Object>) existing;
        } else {
            vals = new ArrayList<Object>(3);
            vals.add(existing);
        }
        // Add the values to the collection
        if (value instanceof Iterable) {
            for (Object o : (Iterable<Object>) value) {
                vals.add(o);
            }
        } else if (value instanceof Object[]) {
            for (Object o : (Object[]) value) {
                vals.add(o);
            }
        } else {
            vals.add(value);
        }
        fields.put(name, vals);
    }
    ///////////////////////////////////////////////////////////////////
    // Get the field values
    ///////////////////////////////////////////////////////////////////
    /**
     * returns the first value for a field
     */
    public Object getFirstValue(String name) {
        Object v = fields.get(name);
        if (v == null || !(v instanceof Collection))
            return v;
        Collection c = (Collection) v;
        if (c.size() > 0) {
            return c.iterator().next();
        }
        return null;
    }
    /**
     * Get the value or collection of values for a given field.  
     */
    public Object getFieldValue(String name) {
        return fields.get(name);
    }
    /**
     * Get a collection of values for a given field name
     */
    @SuppressWarnings("unchecked")
    public Collection<Object> getFieldValues(String name) {
        Object v = fields.get(name);
        if (v instanceof Collection) {
            return (Collection<Object>) v;
        }
        if (v != null) {
            ArrayList<Object> arr = new ArrayList<Object>(1);
            arr.add(v);
            return arr;
        }
        return null;
    }
    @Override
    public String toString() {
        return "SolrDocument" + fields;
    }
    /**
     * Iterate of String->Object keys
     */
    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return fields.entrySet().iterator();
    }
    //-----------------------------------------------------------------------------------------
    // JSTL Helpers
    //-----------------------------------------------------------------------------------------
    /**
     * Expose a Map interface to the solr field value collection.
     */
    public Map<String, Collection<Object>> getFieldValuesMap() {
        return new Map<String, Collection<Object>>() {
            /** Get the field Value */
            @Override
            public Collection<Object> get(Object key) {
                return getFieldValues((String) key);
            }
            // Easily Supported methods
            @Override
            public boolean containsKey(Object key) {
                return fields.containsKey(key);
            }
            @Override
            public Set<String> keySet() {
                return fields.keySet();
            }
            @Override
            public int size() {
                return fields.size();
            }
            @Override
            public boolean isEmpty() {
                return fields.isEmpty();
            }
            // Unsupported operations.  These are not necessary for JSTL
            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
            @Override
            public boolean containsValue(Object value) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Set<java.util.Map.Entry<String, Collection<Object>>> entrySet() {
                throw new UnsupportedOperationException();
            }
            @Override
            public void putAll(Map<? extends String, ? extends Collection<Object>> t) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Collection<Collection<Object>> values() {
                throw new UnsupportedOperationException();
            }
            @Override
            public Collection<Object> put(String key, Collection<Object> value) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Collection<Object> remove(Object key) {
                throw new UnsupportedOperationException();
            }
            @Override
            public String toString() {
                return fields.toString();
            }
        };
    }
    /**
     * Expose a Map interface to the solr fields.  This function is useful for JSTL
     */
    public Map<String, Object> getFieldValueMap() {
        return new Map<String, Object>() {
            /** Get the field Value */
            @Override
            public Object get(Object key) {
                return getFirstValue((String) key);
            }
            // Easily Supported methods
            @Override
            public boolean containsKey(Object key) {
                return fields.containsKey(key);
            }
            @Override
            public Set<String> keySet() {
                return fields.keySet();
            }
            @Override
            public int size() {
                return fields.size();
            }
            @Override
            public boolean isEmpty() {
                return fields.isEmpty();
            }
            // Unsupported operations.  These are not necessary for JSTL
            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
            @Override
            public boolean containsValue(Object value) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Set<java.util.Map.Entry<String, Object>> entrySet() {
                throw new UnsupportedOperationException();
            }
            @Override
            public void putAll(Map<? extends String, ? extends Object> t) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Collection<Object> values() {
                throw new UnsupportedOperationException();
            }
            @Override
            public Collection<Object> put(String key, Object value) {
                throw new UnsupportedOperationException();
            }
            @Override
            public Collection<Object> remove(Object key) {
                throw new UnsupportedOperationException();
            }
            @Override
            public String toString() {
                return fields.toString();
            }
        };
    }
    //---------------------------------------------------
    // MAP interface
    //---------------------------------------------------
    @Override
    public boolean containsKey(Object key) {
        return fields.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return fields.containsValue(value);
    }
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return fields.entrySet();
    }
    //TODO: Shouldn't the input parameter here be a String?  The fields map requires a String.
    @Override
    public Object get(Object key) {
        return fields.get(key);
    }
    @Override
    public boolean isEmpty() {
        return fields.isEmpty();
    }
    @Override
    public Set<String> keySet() {
        return fields.keySet();
    }
    @Override
    public Object put(String key, Object value) {
        return fields.put(key, value);
    }
    @Override
    public void putAll(Map<? extends String, ? extends Object> t) {
        fields.putAll(t);
    }
    @Override
    public Object remove(Object key) {
        return fields.remove(key);
    }
    @Override
    public int size() {
        return fields.size();
    }
    @Override
    public Collection<Object> values() {
        return fields.values();
    }
    public void addChildDocument(SearchDocument child) {
        if (childDocuments == null) {
            childDocuments = new ArrayList<SearchDocument>();
        }
        childDocuments.add(child);
    }
    public void addChildDocuments(Collection<SearchDocument> childs) {
        for (SearchDocument child : childs) {
            addChildDocument(child);
        }
    }
    /** Returns the list of child documents, or null if none. */
    public List<SearchDocument> getChildDocuments() {
        return childDocuments;
    }
    public boolean hasChildDocuments() {
        boolean isEmpty = (childDocuments == null || childDocuments.isEmpty());
        return !isEmpty;
    }
    public int getChildDocumentCount() {
        return childDocuments.size();
    }
}
