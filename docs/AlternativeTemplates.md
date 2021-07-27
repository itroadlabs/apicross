# Alternative templates
You can create alternative [Handlebars](https://github.com/jknack/handlebars.java) templates for source code generation.
Configuration options below is an example how to configure alternative templates.
```xml
    <generatorOptions implementation="io.github.itroadlabs.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <alternativeTemplatesPath>
            <item>${project.basedir}/src/main/resources/com/myapp/alternative_templates</item>
        </alternativeTemplatesPath>
    </generatorOptions>
```
Alternative templates overrides templates from original code generator.
For example for SpringMvcCodeGenerator following templates exist: `dataModelPropertyConstraints.hbs`, `requestsHandler.hbs` (and many others).
You can place Handlebars templates with same names in the alternative path, so these custom templates will override original templates.

It is possible to load alternative templates from maven plugin classpath, in this case use `classpath:` prefix like in example below:
```xml
    <generatorOptions implementation="io.github.itroadlabs.apicross.springmvc.SpringMvcCodeGeneratorOptions">
        ...
        <alternativeTemplatesPath>
            <item>classpath:/com/myapp/lib/alternative_templates</item>
        </alternativeTemplatesPath>
    </generatorOptions>
```
Note: template file name or classpath resource name must end with `.hbs`.