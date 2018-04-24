package ria.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.Path;
import ria.lang.compiler.ria;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RiaCompiler extends DefaultTask {

    @TaskAction
    public void compileRia() {
        implement(getProject());
    }

    public void implement(Project project) {

        RiaConfig riaConfig = (RiaConfig) project.getExtensions().getByName("riaCompile");

        List<String> runtimeClasspathElements;
        String[] compileSourceRoots = riaConfig.sourceDirs;
        try {
            SourceSet main = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName("main");
            runtimeClasspathElements = Arrays.asList(main.getCompileClasspath().getAsPath().split(Path.SEPARATOR));
        } catch(Exception e) {
            throw new GradleException(e.getMessage(), e);
        }

        String outputDirectory = project
                .getConvention()
                .getPlugin(JavaPluginConvention.class)
                .getSourceSets()
                .getByName("main")
                .getOutput()
                .getClassesDirs()
                .getSingleFile()
                .getAbsolutePath();

        final String classPath = runtimeClasspathElements.stream().collect(Collectors.joining(":"));

        try {
            project.getLogger().info("Setting classpath to: " + classPath);
            if(compileSourceRoots != null) {
                for(String srcDir : compileSourceRoots) {
                    project.getLogger().info("Compiling directory: " + srcDir);
                    ria.main(new String[] {"-d", outputDirectory, "-preload", "ria/lang/std:ria/lang/io", "-cp", classPath, srcDir});
                }
            }
        } catch(Exception e) {
            project.getLogger().error(e.getMessage());
            throw new GradleException(e.getMessage(), e);
        }
    }
}
