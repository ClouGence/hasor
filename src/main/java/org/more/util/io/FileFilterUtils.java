/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.util.io;
import java.util.ArrayList;
import java.util.List;
import org.more.util.io.filefilter.AndFileFilter;
import org.more.util.io.filefilter.DirectoryFileFilter;
import org.more.util.io.filefilter.FalseFileFilter;
import org.more.util.io.filefilter.FileFileFilter;
import org.more.util.io.filefilter.NotFileFilter;
import org.more.util.io.filefilter.OrFileFilter;
import org.more.util.io.filefilter.TrueFileFilter;
/**
 * An interface which brings the FileFilter and FilenameFilter interfaces together.
 * @since Commons IO 1.0
 * @version $Revision: 1178224 $ $Date: 2011-10-02 15:09:57 -0400 (Sun, 02 Oct 2011) $
 * @author Stephen Colebourne
 */
public class FileFilterUtils {
    /**
     * Returns a filter that always returns true.
     * @return a true filter
     * @see TrueFileFilter#TRUE
     */
    public static IOFileFilter trueFileFilter() {
        return TrueFileFilter.TRUE;
    }
    /**
     * Returns a filter that always returns false.
     * @return a false filter
     * @see FalseFileFilter#FALSE
     */
    public static IOFileFilter falseFileFilter() {
        return FalseFileFilter.FALSE;
    }
    /**
     * Returns a filter that checks if the file is a directory.
     * @return file filter that accepts only directories and not files
     * @see DirectoryFileFilter#DIRECTORY
     */
    public static IOFileFilter directoryFileFilter() {
        return DirectoryFileFilter.DIRECTORY;
    }
    /**
     * Returns a filter that checks if the file is a file (and not a directory).
     * @return file filter that accepts only files and not directories
     * @see FileFileFilter#FILE
     */
    public static IOFileFilter fileFileFilter() {
        return FileFileFilter.FILE;
    }
    /**
     * Returns a filter that NOTs the specified filter.
     * @param filter  the filter to invert
     * @return a filter that NOTs the specified filter
     * @see NotFileFilter
     */
    public static IOFileFilter notFileFilter(IOFileFilter filter) {
        return new NotFileFilter(filter);
    }
    /**
     * Returns a filter that ANDs the specified filters.
     * @param filters the IOFileFilters that will be ANDed together.
     * @return a filter that ANDs the specified filters
     * @throws IllegalArgumentException if the filters are null or contain a null value.
     * @see AndFileFilter
     * @since Commons IO 2.0
     */
    public static IOFileFilter and(IOFileFilter... filters) {
        return new AndFileFilter(toList(filters));
    }
    /**
     * Returns a filter that ORs the specified filters.
     * @param filters the IOFileFilters that will be ORed together.
     * @return a filter that ORs the specified filters
     * @throws IllegalArgumentException if the filters are null or contain a null value.
     * @see OrFileFilter
     * @since Commons IO 2.0
     */
    public static IOFileFilter or(IOFileFilter... filters) {
        return new OrFileFilter(toList(filters));
    }
    /**
     * Create a List of file filters.
     * @param filters The file filters
     * @return The list of file filters
     * @throws IllegalArgumentException if the filters are null or contain a null value.
     * @since Commons IO 2.0
     */
    public static List<IOFileFilter> toList(IOFileFilter... filters) {
        if (filters == null) {
            throw new IllegalArgumentException("The filters must not be null");
        }
        List<IOFileFilter> list = new ArrayList<IOFileFilter>(filters.length);
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] == null) {
                throw new IllegalArgumentException("The filter[" + i + "] is null");
            }
            list.add(filters[i]);
        }
        return list;
    }
}