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
import freemarker.template.Configuration;
import freemarker.template.Template;
import net.hasor.plugins.freemarker.loader.ClasspathTemplateLoader;
import net.hasor.utils.StringUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.dir.DirectoryArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "export", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ExportAppMojo extends AbstractHasorMojo {
    @Component
    private MavenSession              session;
    @Component
    private ProjectBuilder            projectBuilder;
    /** The HAR archiver. */
    @Component(role = Archiver.class, hint = "dir")
    private DirectoryArchiver         dirArchiver;
    /** The Jar archiver. */
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver               jarArchiver;
    @Component
    private RepositorySystem          repositorySystem;
    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository        localRepository;
    /* 目标打包平台：mac、win、all */
    @Parameter(defaultValue = "all", readonly = true, required = true)
    private PlatformEnum              platform;
    //
    //
    /** The archive configuration to use.
     * http://maven.apache.org/shared/maven-archiver/index.html */
    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
    //
    //
    public void execute() throws MojoExecutionException {
        //
        getLog().info(" build to " + this.getOutputDirectory());
        String destDirectoryName = this.getProject().getArtifact().getArtifactId() + "-" + this.getProject().getVersion();
        dirArchiver.setDestFile(new File(this.getOutputDirectory(), destDirectoryName));
        //
        // .确定 boot 包版本
        Attributes bootLauncher = this.findManifestSection("bootLauncher");
        String classworldsGroup = bootLauncher.getValue("classworldsGroup");
        String classworldsArtifactID = bootLauncher.getValue("classworldsArtifactId");
        String classworldsVersion = bootLauncher.getValue("classworldsVersion");
        this.addArtifactToBoot("boot", classworldsGroup, classworldsArtifactID, classworldsVersion);
        //
        Set<Artifact> allDependency = fetchDependencyTo(true, this.getProject(), false);
        String bootVersion = getBootVersion(allDependency);
        if (StringUtils.isBlank(bootVersion)) {
            bootVersion = bootLauncher.getValue("bootVersion");
        }
        this.addArtifactToBoot("boot", "net.hasor", "hasor-boot", bootVersion);
        //
        //
        // .依赖jars to lib
        Set<Artifact> dependencyToLib = fetchDependencyTo(true, this.getProject(), true);
        for (Artifact artifact : dependencyToLib) {
            getLog().info(" copy " + artifact + " to lib.");
            dirArchiver.addFile(artifact.getFile(), "lib/" + getArtifactDestName(artifact));
        }
        //
        //
        // .项目本身
        File outputDirectory = getOutputDirectory();
        String jarName = getFinalName();
        if (outputDirectory == null) {
            throw new IllegalArgumentException("outputDirectory is not allowed to be null");
        }
        if (jarName == null) {
            throw new IllegalArgumentException("jarName is not allowed to be null");
        }
        File jarFile = new File(outputDirectory, jarName + ".jar");
        try {
            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver(this.jarArchiver);
            archiver.setOutputFile(jarFile);
            archive.setForced(true);
            //
            File contentDirectory = getClassesDirectory();
            if (!contentDirectory.exists()) {
                getLog().warn("JAR will be empty - no content was marked for inclusion!");
            } else {
                archiver.getArchiver().addDirectory(contentDirectory, getIncludes(), getExcludes());
            }
            archiver.createArchive(session, this.getProject(), archive);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dirArchiver.addFile(jarFile, "lib/" + jarFile.getName());
        }
        //
        //
        // .输出 launcher 骨架（bin 目录）
        boolean configWinLauncher = PlatformEnum.win == this.platform || PlatformEnum.all == this.platform;
        boolean configLinuxLauncher = PlatformEnum.linux == this.platform || PlatformEnum.all == this.platform;
        Configuration config = newConfiguration();
        renderFile(config, "/binary/app.conf", "bin/app.conf");
        if (configWinLauncher) {
            renderFile(config, "/binary/win/catalina.bat", "bin/catalina.bat");
            renderFile(config, "/binary/win/debug.bat", "bin/debug.bat");
            renderFile(config, "/binary/win/setenv.bat", "bin/setenv.bat");
            renderFile(config, "/binary/win/shutdown.bat", "bin/shutdown.bat");
            renderFile(config, "/binary/win/startup.bat", "bin/startup.bat");
            renderFile(config, "/binary/win/version.bat", "bin/version.bat");
        }
        if (configLinuxLauncher) {
            renderFile(config, "/binary/linux/catalina.sh", "bin/catalina.sh");
            renderFile(config, "/binary/linux/debug.sh", "bin/debug.sh");
            renderFile(config, "/binary/linux/run.sh", "bin/run.sh");
            renderFile(config, "/binary/linux/setenv.sh", "bin/setenv.sh");
            renderFile(config, "/binary/linux/shutdown.sh", "bin/shutdown.sh");
            renderFile(config, "/binary/linux/startup.sh", "bin/startup.sh");
            renderFile(config, "/binary/linux/version.sh", "bin/version.sh");
        }
        //
        //        this.getProject().getLicenses().get(0).
        //        PlexusIoZipFileResourceCollection collection = new PlexusIoZipFileResourceCollection();
        //        collection.getInputStream()
        //        collection.setIncludes(getIncludes());
        //        collection.setExcludes(getExcludes());
        //        collection.setBaseDir(contentDirectory);
        //        collection.setIncludingEmptyDirectories(false);
        //        collection.setPrefix("");
        //        collection.setUsingDefaultExcludes(true);
        //
        //
        try {
            dirArchiver.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        archiverManager.getArchiver()
        //        archiverManager.getArchiver();
        //        TarGZipUnArchiver archiver = null;
    }
    private void addArtifactToBoot(String logName, String groupID, String artifactID, String version) {
        Artifact artifact = repositorySystem.createArtifact(groupID, artifactID, version, "jar");
        artifact = localRepository.find(artifact);
        getLog().info(logName + " version -> " + artifact);
        dirArchiver.addFile(artifact.getFile(), "boot/" + getArtifactDestName(artifact));
    }
    //
    private void renderFile(Configuration config, String templateName, String toName) {
        try {
            File toFile = new File(dirArchiver.getDestFile().getAbsoluteFile(), toName);
            toFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(toFile);
            Template template = config.getTemplate(templateName);
            template.process(new HashMap<String, Object>(), writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* 查找依赖项 */
    protected Set<Artifact> fetchDependencyTo(boolean stopOnFailure, MavenProject project, boolean skipBoot) throws MojoExecutionException {
        Set<Artifact> all = new HashSet<Artifact>();
        for (Artifact artifact : project.getArtifacts()) {
            fetchDependencyTo(stopOnFailure, artifact, all, skipBoot);
        }
        return all;
    }
    private void fetchDependencyTo(boolean stopOnFailure, Artifact artifact, Set<Artifact> fetchTo, boolean skipBoot) throws MojoExecutionException {
        if (skipBoot) {
            if ("net.hasor".equalsIgnoreCase(artifact.getGroupId()) && "hasor-boot".equalsIgnoreCase(artifact.getArtifactId())) {
                return;
            }
            Attributes bootLauncher = this.findManifestSection("bootLauncher");
            String classworldsGroup = bootLauncher.getValue("classworldsGroup");
            String classworldsArtifactID = bootLauncher.getValue("classworldsArtifactId");
            if (classworldsGroup.equalsIgnoreCase(artifact.getGroupId()) && classworldsArtifactID.equalsIgnoreCase(artifact.getArtifactId())) {
                return;
            }
        }
        fetchTo.add(artifact);
        MavenProject project = null;
        try {
            project = projectBuilder.build(artifact, session.getProjectBuildingRequest()).getProject();
            for (Artifact art : project.getArtifacts()) {
                fetchDependencyTo(stopOnFailure, art, fetchTo, skipBoot);
            }
        } catch (ProjectBuildingException e) {
            if (stopOnFailure) {
                getLog().error(e);
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }
    //
    private String getArtifactDestName(Artifact artifact) {
        return artifact.getArtifactId() + "-" + artifact.getVersion() + "." + artifact.getType();
    }
    //
    /* 查找Hasor-Boot的版本 */
    protected String getBootVersion(Set<Artifact> fetchTo) {
        String bootVersion = null;
        for (Artifact artifact : fetchTo) {
            // .忽略 hasor-boot 和 plexus-classworlds
            if ("net.hasor".equalsIgnoreCase(artifact.getGroupId()) && "hasor-boot".equalsIgnoreCase(artifact.getArtifactId())) {
                bootVersion = artifact.getVersion();
                break;
            }
        }
        //
        if (StringUtils.isNotBlank(bootVersion)) {
            return bootVersion;
        }
        return null;
    }
    //
    protected Attributes findManifestSection(String name) {
        try {
            URL inputStreamURL = this.getClass().getProtectionDomain().getCodeSource().getLocation();
            JarFile jarFile = new JarFile(inputStreamURL.getFile());
            java.util.jar.Manifest manifest = jarFile.getManifest();
            return manifest.getAttributes(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //
    //
    protected Configuration newConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setTemplateLoader(new ClasspathTemplateLoader("/META-INF/hasor-mavenplugin"));
        //
        configuration.setDefaultEncoding("utf-8");
        configuration.setOutputEncoding("utf-8");
        configuration.setLocalizedLookup(false);//是否开启国际化false
        configuration.setClassicCompatible(true);//null值测处理配置
        //
        return configuration;
    }
}