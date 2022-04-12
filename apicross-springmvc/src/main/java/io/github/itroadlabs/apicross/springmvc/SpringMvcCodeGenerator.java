package io.github.itroadlabs.apicross.springmvc;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.github.itroadlabs.apicross.core.handler.model.MediaTypeContentModel;
import io.github.itroadlabs.apicross.core.handler.model.RequestQueryParameter;
import io.github.itroadlabs.apicross.core.handler.model.RequestsHandler;
import io.github.itroadlabs.apicross.core.handler.model.RequestsHandlerMethod;
import io.github.itroadlabs.apicross.java.JavaCodeGenerator;
import io.github.itroadlabs.apicross.utils.HandlebarsFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SpringMvcCodeGenerator extends JavaCodeGenerator<SpringMvcCodeGeneratorOptions> {
    private Template requestsHandlerQueryObjectTemplate;
    private Template dataModelReadInterfaceTemplate;
    private boolean enableApicrossJavaBeanValidationSupport = false;
    private boolean enableDataModelReadInterfaces = false;
    private boolean enableSpringSecurityAuthPrincipal = false;
    private boolean useQueryStringParametersObject = true;
    private String apiModelReadInterfacesPackage;

    @Override
    public void setOptions(SpringMvcCodeGeneratorOptions options) throws Exception {
        super.setOptions(options);
        this.enableApicrossJavaBeanValidationSupport = options.isEnableApicrossJavaBeanValidationSupport();
        this.enableDataModelReadInterfaces = options.isEnableDataModelReadInterfaces();
        this.apiModelReadInterfacesPackage = options.getApiModelReadInterfacesPackage();
        this.enableSpringSecurityAuthPrincipal = options.isEnableSpringSecurityAuthPrincipal();
        if (this.apiModelReadInterfacesPackage == null) {
            this.apiModelReadInterfacesPackage = super.apiModelPackage;
        }
        this.useQueryStringParametersObject = options.isUseQueryStringParametersObject();
    }

    @Override
    protected void initSourceCodeTemplates(Handlebars templatesEngine) throws IOException {
        super.initSourceCodeTemplates(templatesEngine);
        this.requestsHandlerQueryObjectTemplate = templatesEngine.compile("requestsHandlerQueryStringParametersObject");
        this.dataModelReadInterfaceTemplate = templatesEngine.compile("dataModelReadInterface");
        templatesEngine.setInfiniteLoops(true); // for recursion with type.hbs
    }

    @Override
    protected void preProcess(Iterable<ObjectDataModel> models, Iterable<RequestsHandler> handlers) {
        super.preProcess(models, handlers);
        if (!getOptions().isGenerateOnlyModels()) {
            handleNeedToUseRequestBodyAnnotation(handlers);
        }
        if (enableSpringSecurityAuthPrincipal) {
            processSpringSecurityAuthPrincipalFeature(handlers);
        }
    }

    protected void processSpringSecurityAuthPrincipalFeature(Iterable<RequestsHandler> handlers) {
        for (RequestsHandler handler : handlers) {
            List<RequestsHandlerMethod> methods = handler.getMethods();
            boolean anySecurityOptionsForHandler = false;
            for (RequestsHandlerMethod method : methods) {
                if (method.hasSecurityOptions()) {
                    method.addCustomAttribute("addAuthPrincipalArg", Boolean.TRUE);
                    anySecurityOptionsForHandler = true;
                }
            }
            if (anySecurityOptionsForHandler) {
                handler.addCustomAttribute("enableSpringSecurityAuthPrincipal", Boolean.TRUE);
            }
        }
    }

    @Override
    protected List<ObjectDataModel> prepareJavaClassDataModels(Collection<ObjectDataModel> schemas, Collection<RequestsHandler> handlers) {
        final List<ObjectDataModel> objectDataModels = super.prepareJavaClassDataModels(schemas, handlers);

        if (enableDataModelReadInterfaces) {
            for (ObjectDataModel model : objectDataModels) {
                Map<String, Object> customAttributes = model.getCustomAttributes();
                List<String> ifaces = (List<String>) customAttributes.get("implementsInterfaces");

                if (ifaces == null) {
                    ifaces = new ArrayList<>();
                    model.addCustomAttribute("implementsInterfaces", ifaces);
                }

                String iface = "IRead" + model.getOriginalTypeName();
                ifaces.add(iface);

                if (!super.apiModelPackage.equals(apiModelReadInterfacesPackage)) {
                    model.addCustomAttribute("apiModelReadInterfacesPackage", apiModelReadInterfacesPackage);
                }
            }
        }
        return objectDataModels;
    }

    protected void handleNeedToUseRequestBodyAnnotation(Iterable<RequestsHandler> handlers) {
        handlers.forEach(requestsHandler -> requestsHandler.getMethods().forEach(requestsHandlerMethod -> {
            MediaTypeContentModel requestBody = requestsHandlerMethod.getRequestBody();
            if (requestBody != null) {
                if ("multipart/form-data".equals(requestBody.getMediaType()) ||
                        "application/x-www-form-urlencoded".equals(requestBody.getMediaType())) {
                    requestBody.addCustomAttribute("avoidRequestBodyAnnotation", Boolean.TRUE);
                }
            }
        }));
    }

    @Override
    protected ClassPathTemplateLoader setupDefaultTemplatesLoader() {
        return new ClassPathTemplateLoader("/io/github/itroadlabs/apicross/springmvc/templates", ".hbs");
    }

    @Override
    protected void writeHandlersSources(File handlersPackageDir, File modelsPackageDir, List<RequestsHandler> handlers) throws IOException {
        super.writeHandlersSources(handlersPackageDir, modelsPackageDir, handlers);
        writeApiHandlerQueryObjectModels(handlers, modelsPackageDir);
    }

    @Override
    protected void writeModelsSources(File modelsPackageDir, List<ObjectDataModel> models) throws IOException {
        super.writeModelsSources(modelsPackageDir, models);
        if (enableDataModelReadInterfaces) {
            File apiModelReadInterfacesDirectory = new File(getWriteSourcesTo(), toFilePath(apiModelReadInterfacesPackage));
            apiModelReadInterfacesDirectory.mkdirs();
            writeDataModelsReadInterfaceSourceFiles(models, apiModelReadInterfacesDirectory, model -> "IRead" + model.getOriginalTypeName() + ".java");
        }
    }

    protected void writeApiHandlerQueryObjectModels(List<RequestsHandler> handlers, File modelsPackageDir) throws IOException {
        log.info("Writing QueryObject data models...");

        Set<String> handledOperations = new LinkedHashSet<>(); // 1 query object for operationId
        for (RequestsHandler handler : handlers) {
            List<RequestsHandlerMethod> methods = handler.getMethods();
            for (RequestsHandlerMethod method : methods) {
                String operationId = method.getOperationId();
                if (method.hasQueryParameters() && !handledOperations.contains(operationId)) {
                    File sourceFile = new File(modelsPackageDir, StringUtils.capitalize(operationId) + "Query.java");
                    try (FileOutputStream out = new FileOutputStream(sourceFile)) {
                        PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                        String iface = queryObjectsInterfacesMap != null ? queryObjectsInterfacesMap.get(operationId) : null;
                        Set<String> ifaces = new LinkedHashSet<>();
                        if (iface != null) {
                            ifaces.add(iface);
                        }
                        if (globalQueryObjectsInterfaces != null && !globalQueryObjectsInterfaces.isEmpty()) {
                            ifaces.addAll(globalQueryObjectsInterfaces);
                        }
                        Context context = buildTemplateContext(method, apiModelPackage);
                        if (!ifaces.isEmpty()) {
                            context.combine("queryObjectTypeInterfaces", ifaces);
                        }
                        context.combine("queryObjectRequiredProperties", method.getQueryParameters().stream()
                                .filter(RequestQueryParameter::isRequired)
                                .map(RequestQueryParameter::getName)
                                .collect(Collectors.toSet()));
                        writeSource(sourcePrintWriter, requestsHandlerQueryObjectTemplate.apply(context));
                    }
                    handledOperations.add(operationId);
                }
            }
        }
    }

    protected void writeDataModelsReadInterfaceSourceFiles(List<ObjectDataModel> models, File modelsPackageDir, Function<ObjectDataModel, String> fileNameFactory) throws IOException {
        log.info("Writing API data models read interfaces...");

        for (ObjectDataModel model : models) {
            File sourceFile = new File(modelsPackageDir, fileNameFactory.apply(model));
            try (FileOutputStream out = new FileOutputStream(sourceFile)) {
                PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                Context context = buildTemplateContext(model, apiModelReadInterfacesPackage);
                writeSource(sourcePrintWriter, dataModelReadInterfaceTemplate.apply(context));
            }
        }
    }

    @Override
    protected Map<String, Object> buildGeneratorOpts() {
        Map<String, Object> generatorOpts = super.buildGeneratorOpts();
        generatorOpts.put("enableApicrossJavaBeanValidationSupport", this.enableApicrossJavaBeanValidationSupport);
        generatorOpts.put("useQueryStringParametersObject", this.useQueryStringParametersObject);
        return generatorOpts;
    }
}
