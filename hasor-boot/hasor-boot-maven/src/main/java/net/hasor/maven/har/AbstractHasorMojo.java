/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
package net.hasor.maven.har;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
/**
 * Goal which touches a timestamp file.
 */
public abstract class AbstractHasorMojo extends AbstractMojo {
    //
    protected static final String[]     DEFAULT_EXCLUDES = new String[] { "**/package.html" };
    protected static final String[]     DEFAULT_INCLUDES = new String[] { "**/**" };
    /**
     * List of files to include. Specified as fileset patterns which are relative to the
     * input directory whose contents is being packaged into the JAR.
     */
    @Parameter
    private                String[]     includes;
    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the
     * input directory whose contents is being packaged into the JAR.
     */
    @Parameter
    private                String[]     excludes;
    /** The Maven project. */
    @Component
    private                MavenProject project;
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private                File         outputDirectory;
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private                File         classesDirectory;
    /** Name of the generated JAR. */
    @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
    private                String       finalName;
    @Parameter(defaultValue = "${javaHome}", readonly = true)
    private                String       javaHome;
    //
    //
    //
    //
    //
    //
    public MavenProject getProject() {
        return project;
    }
    public File getOutputDirectory() {
        return outputDirectory;
    }
    public File getClassesDirectory() {
        return classesDirectory;
    }
    public String[] getIncludes() {
        if (includes != null && includes.length > 0) {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }
    public String[] getExcludes() {
        if (excludes != null && excludes.length > 0) {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }
    public String getFinalName() {
        return finalName;
    }
}
