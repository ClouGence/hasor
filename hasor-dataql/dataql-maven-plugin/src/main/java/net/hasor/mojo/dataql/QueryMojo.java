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
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.AutoCloseInputStream;
import net.hasor.utils.io.FileUtils;
import net.hasor.utils.io.IOUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parses DataQL query files {@code *.ql} and transforms them into Java source files.
 * @version : 2020-02-09
 * @author 赵永春 (zyc@hasor.net)
 */
@Mojo(name = "dataql", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true,//
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class QueryMojo extends AbstractMojo {
    /** specify query file encoding; e.g., euc-jp */
    @Parameter(property = "project.build.sourceEncoding")
    protected String       inputEncoding;
    /** specify output file encoding; defaults to source encoding */
    @Parameter(property = "project.build.sourceEncoding")
    protected String       outputEncoding;
    /* --------------------------------------------------------------------
     * The following are Maven specific parameters, rather than specific options that the DataQL tool can use.
     */
    /**
     * Provides an explicit list of all the query that should be included in the generate phase of the plugin.
     * <p>
     * A set of Ant-like inclusion patterns used to select files from the source
     * directory for processing. By default, the pattern <code>**&#47;*.ql</code> is used to select query files.
     * </p>
     */
    @Parameter
    protected Set<String>  includes = new HashSet<>();
    /**
     * A set of Ant-like exclusion patterns used to prevent certain files from
     * being processed. By default, this set is empty such that no files are excluded.
     */
    @Parameter
    protected Set<String>  excludes = new HashSet<>();
    /** The current Maven project. */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;
    /** The directory where the DataQL query files ({@code *.ql}) are located. */
    @Parameter(defaultValue = "${basedir}/src/main/java")
    private   File         sourceDirectory;
    /** Specify output directory where the Java files are generated. */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/dataql")
    private   File         outputSourceDirectory;
    /** Specify output directory where the Resource files are generated. */
    @Parameter(defaultValue = "${project.build.directory}/generated-resources/dataql")
    private   File         outputResourceDirectory;
    @Component
    private   BuildContext buildContext;

    /**
     * The main entry point for this Mojo, it is responsible for converting
     * dataql query into the target language specified.
     * @exception MojoExecutionException if a configuration or query error causes the code generation process to fail
     */
    @Override
    public void execute() throws MojoExecutionException {
        Log log = getLog();
        outputEncoding = validateEncoding(outputEncoding);
        if (log.isDebugEnabled()) {
            for (String e : excludes) {
                log.debug("DataQL: Exclude: " + e);
            }
            for (String e : includes) {
                log.debug("DataQL: Include: " + e);
            }
            log.debug("DataQL: Output: " + outputSourceDirectory);
        }
        if (!sourceDirectory.isDirectory()) {
            log.info("No DataQL files to compile in " + sourceDirectory.getAbsolutePath());
            return;
        }
        try {
            log.info("delete generate Query Jave Class in Directory " + sourceDirectory.getAbsolutePath());
            FileUtils.deleteDirectory(outputSourceDirectory);
            if (!outputSourceDirectory.exists()) {
                outputSourceDirectory.mkdirs();
            }
            log.info("delete generate Query Resource in Directory " + outputResourceDirectory.getAbsolutePath());
            FileUtils.deleteDirectory(outputResourceDirectory);
            if (!outputResourceDirectory.exists()) {
                outputResourceDirectory.mkdirs();
            }
        } catch (Exception e) {
            log.error(e);
        }
        //
        //
        if (!project.getCompileSourceRoots().contains(outputSourceDirectory.getAbsolutePath())) {
            log.info("DataQL directory " + outputSourceDirectory.getAbsolutePath() + " add to CompileSource Directory.");
            project.addCompileSourceRoot(outputSourceDirectory.getAbsolutePath());
        }
        if (!project.getResources().contains(outputResourceDirectory.getAbsolutePath())) {
            log.info("DataQL directory " + outputResourceDirectory.getAbsolutePath() + " add to Resource Directory.");
            Resource resource = new Resource();
            resource.setDirectory(outputResourceDirectory.getAbsolutePath());
            project.addResource(resource);
        }
        //
        log.info("DataQL : Processing source directory " + outputSourceDirectory.getAbsolutePath());
        try {
            List<String> stringList = IOUtils.readLines(ResourcesUtils.getResourceAsStream("/META-INF/dataql-codegen-template.tpl"), "UTF-8");
            final String javaTemp = StringUtils.join(stringList.toArray(new String[0]), "\n");
            //
            SourceInclusionScanner scan = new SimpleSourceInclusionScanner(getIncludesPatterns(), excludes);
            scan.addSourceMapping(new SuffixMapping("ql", Collections.emptySet()));
            Set<File> qlFiles = scan.getIncludedSources(sourceDirectory, null);
            for (File qlFile : qlFiles) {
                buildContext.refresh(qlFile);
                buildContext.removeMessages(qlFile);
                log.info("Query file '" + qlFile.getPath() + "' detected.");
                String relPathBase = MojoUtils.findSourceSubdir(sourceDirectory, qlFile);
                String relPath = relPathBase + qlFile.getName();
                getLog().debug("  ... relative path is: " + relPath);
                //
                // 进行一次解析操作，过滤掉语法有问题的查询文件
                QueryHelper.queryParser(new AutoCloseInputStream(new FileInputStream(qlFile)));
                //
                // Copy 原始的查询文件
                try (InputStream sourceQueryFile = FileUtils.openInputStream(qlFile)) {
                    File outFile = new File(outputResourceDirectory, relPath);
                    try (OutputStream targetQueryFile = FileUtils.openOutputStream(outFile)) {
                        IOUtils.copy(sourceQueryFile, targetQueryFile);
                        targetQueryFile.flush();
                    }
                }
                // .build
                String className = qlFile.getName().split("\\.")[0] + "Query";
                String targetPackageName = "";
                if (StringUtils.isNotBlank(relPathBase)) {
                    targetPackageName = "package " + relPathBase.replace(File.separator, ".");
                    targetPackageName = targetPackageName.substring(0, targetPackageName.length() - 1) + ";";
                }
                String targetJavaName = relPath.replace(File.separator, ".");
                String tempClass = new String(javaTemp.toCharArray().clone());
                tempClass = tempClass.replace("%target_pacakge%", targetPackageName);
                tempClass = tempClass.replace("%source_resource%", "/" + relPath.replace(File.separator, "/"));
                tempClass = tempClass.replace("%target_name%", className);
                File outFile = new File(new File(outputSourceDirectory, relPath).getParentFile(), className + ".java");
                try (OutputStream targetQueryFile = FileUtils.openOutputStream(outFile)) {
                    targetQueryFile.write(tempClass.getBytes());
                    targetQueryFile.flush();
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new MojoExecutionException("Fatal error occured while evaluating the names of the query files to analyze", e);
        }
    }

    private Set<String> getIncludesPatterns() {
        if (includes == null || includes.isEmpty()) {
            return Collections.singleton("**/*.ql");
        }
        return includes;
    }

    /**
     * Validates the given encoding.
     * @return the validated encoding. If {@code null} was provided, returns the platform default encoding.
     */
    private String validateEncoding(String encoding) {
        return (encoding == null) ? Charset.defaultCharset().name() : Charset.forName(encoding.trim()).name();
    }
}
