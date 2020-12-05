package org.sekaijin;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Goal which create a Constant class.
 *
 * @goal generate
 * 
 * @phase generate-sources
 * 
 * @execute goal="generate"
 * 
 * @executionStrategy always
 */
public class ConstantsMojo extends AbstractMojo {

    private static final String PUBLIC_FINAL_STRING = "    public static final String ";

    /**
     * @parameter default-value="${session.request.startTime}"
     * @required
     * @readonly
     */
    private Date timestamp;

    /**
     * @parameter default-value="${project.build.directory}/generated-sources/java"
     * @required
     * @readonly
     */
    private String targetFolder;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    MavenProject project;

    public void execute() throws MojoExecutionException {
        // getLog().error(project.getVersion());
        // getLog().error(project.getName());
        // getLog().error(project.getArtifactId());
        // getLog().error(project.getGroupId());
        // getLog().error(project.getProperties().toString());

        DateFormat df = new SimpleDateFormat(
                project.getProperties().getProperty("maven.build.timestamp.format", "yyyy-MM-dd HH:mm:ss z"));

        // getLog().error(df.format(timestamp));
        // getLog().error(targetFolder);

        project.addCompileSourceRoot(targetFolder);

        String packageName = project.getGroupId();
        String className = StringUtils.capitaliseAllWords(
            project.getArtifactId().replaceAll("\\.|\\-", " "))
            .replaceAll(" ", "");
        getLog().info("package " + packageName);
        getLog().info("class " + className + " {");

        Path rootPath = Paths.get(targetFolder);
        Path packagePath = rootPath.resolve(packageName.replaceAll("\\.", "/"));
        Path classPath = packagePath.resolve(className + ".java");
        try {
            Files.createDirectories(packagePath);
            Files.deleteIfExists(classPath);
            Files.createFile(classPath);
            try (final BufferedWriter out = Files.newBufferedWriter(classPath, StandardCharsets.UTF_8)) {
                out.append("package " + packageName + ";\n\n");
                out.append("public class " + className + "\n{\n");
                if (! project.getProperties().containsKey("constant.full.name")) {
                    getLog().info("    FULL_NAME");
                    out.append(PUBLIC_FINAL_STRING + "FULL_NAME =\"" + project.getName() + "\";\n");
                }
                if (! project.getProperties().containsKey("constant.name")) {
                    getLog().info("    NAME");
                    out.append(PUBLIC_FINAL_STRING + "NAME =\"" + project.getArtifactId() + "\";\n");
                }
                if (! project.getProperties().containsKey("constant.version")) {
                    getLog().info("    VERSION");
                    out.append(PUBLIC_FINAL_STRING + "VERSION =\"" + project.getVersion() + "\";\n");
                }
                getLog().info("    BUILD");
                out.append(PUBLIC_FINAL_STRING + "BUILD =\"" + df.format(timestamp) + "\";\n");
                for (String property : project.getProperties().stringPropertyNames()) {
                    if (property.startsWith("constant.")) {
                        String name = property.replaceFirst("constant.", "").toUpperCase().replace("\\.", "_");
                        getLog().info("    "+name);
                        out.append(PUBLIC_FINAL_STRING
                                + name + " =\""
                                + project.getProperties().getProperty(property) + "\";\n");
                    }
                }
                getLog().info("}");
                out.append("} //class\n");
                out.flush();
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
