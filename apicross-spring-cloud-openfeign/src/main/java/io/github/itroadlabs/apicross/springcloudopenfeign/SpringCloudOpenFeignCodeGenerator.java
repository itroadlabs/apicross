package io.github.itroadlabs.apicross.springcloudopenfeign;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import io.github.itroadlabs.apicross.core.data.model.ObjectDataModel;
import io.github.itroadlabs.apicross.core.handler.model.MediaTypeContentModel;
import io.github.itroadlabs.apicross.core.handler.model.RequestsHandler;
import io.github.itroadlabs.apicross.java.JavaCodeGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
public class SpringCloudOpenFeignCodeGenerator extends JavaCodeGenerator<SpringCloudOpenFeignCodeGeneratorOptions> {
    private boolean enableApicrossJavaBeanValidationSupport = false;

    @Override
    public void setOptions(SpringCloudOpenFeignCodeGeneratorOptions options) throws Exception {
        super.setOptions(options);
        this.enableApicrossJavaBeanValidationSupport = options.isEnableApicrossJavaBeanValidationSupport();
    }

    @Override
    protected ClassPathTemplateLoader setupDefaultTemplatesLoader() {
        return new ClassPathTemplateLoader("/io/github/itroadlabs/apicross/springcloudopenfeign/templates", ".hbs");
    }

    @Override
    protected void initSourceCodeTemplates(Handlebars templatesEngine) throws IOException {
        super.initSourceCodeTemplates(templatesEngine);
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
    protected void writeHandlersSources(File handlersPackageDir, File modelsPackageDir, List<RequestsHandler> handlers) throws IOException {
        log.info("Writing API handlers...");

        for (RequestsHandler handler : handlers) {
            File handlerInterfaceSourceFile = new File(handlersPackageDir, handler.getTypeName() + "Client.java");
            try (FileOutputStream out = new FileOutputStream(handlerInterfaceSourceFile)) {
                PrintWriter sourcePrintWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                writeRequestsHandler(handler, sourcePrintWriter);
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
