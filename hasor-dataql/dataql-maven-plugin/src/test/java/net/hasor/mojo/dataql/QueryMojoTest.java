/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package net.hasor.mojo.dataql;
import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({ "3.6.2" })
public class QueryMojoTest {
    @Rule
    public final TestResources resources = new TestResources();
    public final MavenRuntime  mavenRuntime;

    public QueryMojoTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
        this.mavenRuntime = builder.build();
    }

    @Test
    public void buildExample() throws Exception {
        File basedir = resources.getBasedir("example");
        MavenExecutionResult result = mavenRuntime.forProject(basedir).execute("clean", "clean", "package");
        result.assertErrorFreeLog();
    }
}