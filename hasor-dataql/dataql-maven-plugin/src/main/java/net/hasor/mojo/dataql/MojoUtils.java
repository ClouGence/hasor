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
package net.hasor.mojo.dataql;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Parses DataQL query files {@code *.ql} and transforms them into Java source files.
 * @version : 2020-02-09
 * @author 赵永春 (zyc@hasor.net)
 */
class MojoUtils {
    /**
     * Creates the MD5 checksum for the given file.
     * @param   file  the file.
     * @return the checksum.
     */
    public static byte[] checksum(File file) throws IOException {
        try {
            InputStream in = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            try {
                int n;
                do {
                    n = in.read(buffer);
                    if (n > 0) {
                        complete.update(buffer, 0, n);
                    }
                } while (n != -1);
            } finally {
                in.close();
            }
            return complete.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("Could not create checksum " + file, ex);
        }
    }

    /**
     * Given the source directory File object and the full PATH to a query, produce the
     * path to the named query file in relative terms to the {@code sourceDirectory}.
     * This will then allow DataQL to produce output relative to the base of the output
     * directory and reflect the input organization of the query files.
     *
     * @param   sourceDirectory  The source directory {@link File} object
     * @param   queryFile  The full path to the input query file
     * @return The path to the query file relative to the source directory
     */
    public static String findSourceSubdir(File sourceDirectory, File queryFile) {
        String srcPath = sourceDirectory.getPath() + File.separator;
        String path = queryFile.getPath();
        if (!path.startsWith(srcPath)) {
            throw new IllegalArgumentException("expected " + path + " to be prefixed with " + sourceDirectory);
        }
        File unprefixedGrammarFileName = new File(path.substring(srcPath.length()));
        if (unprefixedGrammarFileName.getParent() == null) {
            return "";
        }
        return unprefixedGrammarFileName.getParent() + File.separator;
    }
}