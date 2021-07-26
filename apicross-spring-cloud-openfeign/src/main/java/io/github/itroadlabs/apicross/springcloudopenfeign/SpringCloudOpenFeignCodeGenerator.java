package io.github.itroadlabs.apicross.springcloudopenfeign;

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
import java.util.stream.Collectors;

@Slf4j
public class SpringCloudOpenFeignCodeGenerator extends JavaCodeGenerator<SpringCloudOpenFeignCodeGeneratorOptions> {
    private Template requestsHandlerQueryObjectTemplate;
    private boolean enableApicrossJavaBeanValidationSupport = false;
    private List<String> alternativeTemplatesPath;

    @Override
    public void setOptions(SpringCloudOpenFeignCodeGeneratorOptions options) throws Exception {
        super.setOptions(options);
        this.enableApicrossJavaBeanValidationSupport = options.isEnableApicrossJavaBeanValidationSupport();
        this.alternativeTemplatesPath = options.getAlternativeTemplatesPath();
    }

    @Override
    protected void initSourceCodeTemplates(Handlebars templatesEngine) throws IOException {
        super.initSourceCodeTemplates(templatesEngine);
        this.requestsHandlerQueryObjectTemplate = templatesEngine.compile("requestsHandlerQueryStringParametersObject");
        templatesEngine.setInfiniteLoops(true); // for recursion with type.hbs
    }

    @Override
    protected void preProcess(Iterable<ObjectDataModel> models, Iterable<RequestsHandler> handlers) {
        super.preProcess(models, handlers);
        if (!getOptions().isGenerateOnlyModels()) {
            handleNeedToUseRequestBodyAnnotation(handlers);
        }
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
    protected Handlebars setupHandlebars() {
        ClassPathTemplateLoader defaultTemplatesLoader = new ClassPathTemplateLoader("/io/github/itroadlabs/apicross/springcloudopenfeign/templates", ".hbs");

        if (alternativeTemplatesPath != null && !alternativeTemplatesPath.isEmpty()) {
            List<TemplateLoader> effectiveTemplatesClassPath = new ArrayList<>();
            for (String path : alternativeTemplatesPath) {
                effectiveTemplatesClassPath.add(new FileTemplateLoader(path, ".hbs"));
            }
            effectiveTemplatesClassPath.add(defaultTemplatesLoader);
            return HandlebarsFactory.setupHandlebars(effectiveTemplatesClassPath);
        } else {
            return HandlebarsFactory.setupHandlebars(Collections.singletonList(defaultTemplatesLoader));
        }
    }

    @Override
    protected void writeHandlersSources(File handlersPackageDir, File modelsPackageDir, List<RequestsHandler> handlers) throws IOException {
        log.info("Writing API handlers...");

        for (RequestsHandler handler : handlers) {
            File handlerInterfaceSourceFile = new File(handlersPackageDir, handler.getTypeName() + "Client.java");
            try (FileOutputStream out = new FileOutputStream(handlerInterfaceSourceFile)) {
                PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                writeRequestsHandler(handler, sourcePrintWriter);
            }
        }
        writeApiHandlerQueryObjectModels(handlers, modelsPackageDir);
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

    @Override
    protected Map<String, Object> buildGeneratorOpts() {
        Map<String, Object> generatorOpts = super.buildGeneratorOpts();
        generatorOpts.put("enableApicrossJavaBeanValidationSupport", this.enableApicrossJavaBeanValidationSupport);
        return generatorOpts;
    }
}
