package com.github.itroadlabs.oas.apicross.mavenplugin;

import com.github.itroadlabs.oas.apicross.CodeGeneratorOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import com.github.itroadlabs.oas.apicross.CodeGenerator;
import com.github.itroadlabs.oas.apicross.utils.OpenApiSpecificationParseException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;


@Mojo(name = "generate-code")
public class SourceCodeGeneratorMojo extends AbstractMojo {
    @Parameter
    private String specUrl;
    @Parameter
    private String generatorClassName;
    @Parameter
    private CodeGeneratorOptions generatorOptions;

    @Override
    public void execute() throws MojoExecutionException {
        CodeGenerator codeGenerator;

        try {
            codeGenerator = (CodeGenerator) Class.forName(generatorClassName).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            getLog().error(e.getMessage(), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }

        try {
            codeGenerator.setSpecUrl(specUrl);
            codeGenerator.setOptions(generatorOptions);
            codeGenerator.generate();
        } catch (OpenApiSpecificationParseException spe) {
            SwaggerParseResult swaggerParseResult = spe.getSwaggerParseResult();
            List<String> messages = swaggerParseResult.getMessages();
            StringBuilder stringBuilder = new StringBuilder();
            for (String message : messages) {
                stringBuilder.append(message).append("\n");
            }
            getLog().error(stringBuilder.toString());
            throw new MojoExecutionException(spe.getMessage(), spe);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
