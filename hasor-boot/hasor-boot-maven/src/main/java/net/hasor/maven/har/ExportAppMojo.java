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
import net.hasor.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;
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
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import static net.hasor.utils.ResourcesUtils.scanJar;
/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "jar", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ExportAppMojo extends AbstractHasorMojo {
    @Component
    private MavenSession              session;
    @Component
    private ProjectBuilder            projectBuilder;
    @Component
    private RepositorySystem          repositorySystem;
    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository        localRepository;
    /** The Jar archiver. */
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver               jarArchiver;
    /** The archive configuration to use. http://maven.apache.org/shared/maven-archiver/index.html */
    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
    /** main class. */
    @Parameter(alias = "main-class")
    private String                    mainClass;
    //
    //
    public void execute() throws MojoExecutionException {
        // .build jar
        getLog().info("build to " + this.getOutputDirectory());
        String destDirectoryName = this.getProject().getArtifact().getArtifactId() + "-" + this.getProject().getVersion();
        jarArchiver.setDestFile(new File(this.getOutputDirectory(), destDirectoryName));
        //
        // .确定 boot 包版本
        Attributes bootLauncher = this.findManifestSection("bootLauncher");
        Set<Artifact> allDependency = fetchDependencyTo(true, this.getProject(), false);
        String bootVersion = getBootVersion(allDependency);
        if (StringUtils.isBlank(bootVersion)) {
            bootVersion = bootLauncher.getValue("bootVersion");
        }
        Artifact bootArtifact = repositorySystem.createArtifact("net.hasor", "hasor-boot", bootVersion, "jar");
        bootArtifact = localRepository.find(bootArtifact);
        getLog().info("use boot -> " + bootArtifact);
        jarArchiver.addFile(repackageArtifact(bootArtifact), "/BOOT-INF/lib/" + getArtifactDestName(bootArtifact));
        //
        // Booter-Classes
        getLog().info("copy booter Loader.");
        final File bootDirectory = new File(this.getClassesDirectory().getParentFile(), "booter-classes");
        try {
            JarFile jarFile = new JarFile(bootArtifact.getFile());
            scanJar(jarFile, "*", new ResourcesUtils.Scanner() {
                @Override
                public void found(ResourcesUtils.ScanEvent event, boolean isInJar) throws IOException {
                    if (!event.getName().startsWith("net/hasor/boot/launcher")) {
                        return;
                    }
                    getLog().debug("  copy class " + event.getName());
                    File targetFile = new File(bootDirectory, event.getName());
                    targetFile.getParentFile().mkdirs();
                    //
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    IOUtils.copy(event.getStream(), fos);
                    fos.flush();
                    fos.close();
                }
            });
        } catch (Exception e) {
            getLog().error(e);
        } finally {
            jarArchiver.addDirectory(bootDirectory);
            getLog().info("done.");
        }
        //
        // .依赖jars to lib
        Set<Artifact> dependencyToLib = fetchDependencyTo(true, this.getProject(), true);
        for (Artifact artifact : dependencyToLib) {
            getLog().info("  copy " + artifact + " to lib.");
            jarArchiver.addFile(repackageArtifact(artifact), "/BOOT-INF/lib/" + getArtifactDestName(artifact));
        }
        getLog().info("done.");
        //
        // .启动描述
        archive.getManifestEntries().put("Main-Class", "net.hasor.boot.launcher.JarLauncher");
        archive.getManifestEntries().put("Start-Class", mainClass);
        archive.setCompress(false);
        jarArchiver.setCompress(false);
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
                jarArchiver.addDirectory(contentDirectory, "/BOOT-INF/classes/", getIncludes(), getExcludes());
            }
            archiver.createArchive(session, this.getProject(), archive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private File repackageArtifact(Artifact artifact) {
        return artifact.getFile();
    }
    //
    //
    /* 查找依赖项 */
    protected Set<Artifact> fetchDependencyTo(boolean stopOnFailure, MavenProject project, boolean skipBoot) throws MojoExecutionException {
        Set<Artifact> all = new HashSet<Artifact>();
        for (Artifact artifact : project.getArtifacts()) {
            fetchDependencyTo(stopOnFailure, artifact, all, skipBoot);
        }
        return all;
    }
    //
    private void fetchDependencyTo(boolean stopOnFailure, Artifact artifact, Set<Artifact> fetchTo, boolean skipBoot) throws MojoExecutionException {
        if (skipBoot) {
            if ("net.hasor".equalsIgnoreCase(artifact.getGroupId()) && "hasor-boot".equalsIgnoreCase(artifact.getArtifactId())) {
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
        return (artifact.getArtifactId() + "-" + artifact.getVersion() + "." + artifact.getType()).replace(" ", "");
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
}