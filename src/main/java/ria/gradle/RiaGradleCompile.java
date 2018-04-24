package ria.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class RiaGradleCompile implements Plugin<Project> {
    RiaConfig riaConfig;

    @Override
    public void apply(Project project) {
        riaConfig = project.getExtensions().create("riaCompile", RiaConfig.class);
        RiaCompiler compileRia = project.getTasks().create("compileRia", RiaCompiler.class);
        project.getTasks().getByName("classes").dependsOn(compileRia);
    }
}
