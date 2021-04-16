package com.github.itroadlabs.oas.apicross;

import com.github.itroadlabs.oas.apicross.core.data.DataModelResolver;
import com.github.itroadlabs.oas.apicross.core.data.PropertyNameResolver;
import com.github.itroadlabs.oas.apicross.core.data.model.*;
import com.github.itroadlabs.oas.apicross.core.handler.impl.DefaultRequestsHandlerMethodsResolver;
import com.github.itroadlabs.oas.apicross.core.handler.impl.OperationFirstTagHttpOperationsGroupsResolver;
import com.github.itroadlabs.oas.apicross.core.handler.model.RequestsHandler;
import com.github.itroadlabs.oas.apicross.core.handler.*;
import com.github.itroadlabs.oas.apicross.utils.OpenApiComponentsIndex;
import com.github.itroadlabs.oas.apicross.utils.OpenApiSpecificationParser;
import com.github.itroadlabs.oas.apicross.utils.SchemaHelper;
import com.github.itroadlabs.oas.apicross.utils.UnusedSchemasCleaner;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public abstract class CodeGenerator<T extends CodeGeneratorOptions> {
    private String specUrl;
    private T options;
    private File writeSourcesTo;

    public void setSpecUrl(String specUrl) {
        this.specUrl = specUrl;
    }

    public void setOptions(T options) throws Exception {
        this.options = options;
        this.writeSourcesTo = new File(options.getWriteSourcesTo());
    }

    protected T getOptions() {
        return options;
    }

    protected File getWriteSourcesTo() {
        return writeSourcesTo;
    }

    public void generate() throws IOException {
        Preconditions.checkState(this.specUrl != null, "specification url was not defined");
        log.info("Reading API specification from {}...", specUrl);
        OpenAPI openAPI = OpenApiSpecificationParser.parse(specUrl);
        OpenApiComponentsIndex openAPIComponentsIndex = new OpenApiComponentsIndex(openAPI);

        log.info("Configuring resolvers...");
        DataModelResolver dataModelResolver = setupDataModelSchemaResolver(openAPIComponentsIndex);
        RequestsHandlersResolver requestsHandlersResolver = setupRequestsHandlersResolver(openAPIComponentsIndex, dataModelResolver);

        log.info("Resolving data models...");
        Collection<ObjectDataModel> models = resolveDataModels(dataModelResolver, openAPIComponentsIndex);

        log.info("Resolving handlers...");
        List<RequestsHandler> handlers = resolveRequestsHandlers(requestsHandlersResolver, openAPI.getPaths());

        if (!getOptions().isGenerateOnlyModels()) {
            log.info("Cleaning unused schemas...");
            UnusedSchemasCleaner cleaner = new UnusedSchemasCleaner();
            models = cleaner.clean(models, handlers);
        }

        preProcess(models, handlers);

        generate(models, handlers);

        log.info("Source generation completed!");
    }

    protected void preProcess(Iterable<ObjectDataModel> models, Iterable<RequestsHandler> handlers) {
        for (ObjectDataModel model : models) {
            Set<ObjectDataModelProperty> properties = model.getProperties();
            for (ObjectDataModelProperty property : properties) {
                DataModel propertyDataModel = property.getType();
                if (propertyDataModel instanceof PrimitiveDataModel) {
                    PrimitiveDataModel primitiveDataModel = (PrimitiveDataModel) propertyDataModel;
                    boolean maxLengthDefined = primitiveDataModel.getMaxLength() != null;
                    boolean minLengthDefined = primitiveDataModel.getMinLength() != null;
                    boolean constrainedLength = maxLengthDefined || minLengthDefined;
                    boolean minimumDefined = primitiveDataModel.getMinimum() != null;
                    boolean maximumDefined = primitiveDataModel.getMaximum() != null;
                    primitiveDataModel.addCustomAttribute("constrainedLength", constrainedLength);
                    primitiveDataModel.addCustomAttribute("maxLengthDefined", maxLengthDefined);
                    primitiveDataModel.addCustomAttribute("minLengthDefined", minLengthDefined);
                    primitiveDataModel.addCustomAttribute("minimumDefined", minimumDefined);
                    primitiveDataModel.addCustomAttribute("maximumDefined", maximumDefined);
                } else if (propertyDataModel instanceof ArrayDataModel) {
                    ArrayDataModel arrayDataModel = (ArrayDataModel) propertyDataModel;
                    boolean maxItemsDefined = arrayDataModel.getMaxItems() != null;
                    boolean minItemsDefined = arrayDataModel.getMinItems() != null;
                    boolean arrayLengthConstrained = maxItemsDefined || minItemsDefined;
                    arrayDataModel.addCustomAttribute("arrayLengthConstrained", arrayLengthConstrained);
                    arrayDataModel.addCustomAttribute("maxItemsDefined", maxItemsDefined);
                    arrayDataModel.addCustomAttribute("minItemsDefined", minItemsDefined);
                }
            }
        }
    }

    protected abstract void generate(Collection<ObjectDataModel> models, List<RequestsHandler> handlers) throws IOException;

    protected List<ObjectDataModel> resolveDataModels(DataModelResolver dataModelResolver, OpenApiComponentsIndex openAPIComponentsIndex) {
        List<ObjectDataModel> schemas = new ArrayList<>();

        log.info("Start data models resolving...");
        for (String schemaName : openAPIComponentsIndex.schemasNames()) {
            Schema<?> schema = openAPIComponentsIndex.schemaByName(schemaName);
            if (SchemaHelper.isObjectSchema(schema)) {
                ObjectDataModel resolvedSchema = (ObjectDataModel) dataModelResolver.resolve(schema);
                log.debug("Resolved data model for schema: {}", schemaName);
                schemas.add(resolvedSchema);
            }
        }

        log.info("Data models resolving completed");

        return schemas;
    }

    protected List<RequestsHandler> resolveRequestsHandlers(RequestsHandlersResolver requestsHandlersResolver, Paths paths) {
        return requestsHandlersResolver.resolve(paths);
    }

    protected DataModelResolver setupDataModelSchemaResolver(OpenApiComponentsIndex openAPIComponentsIndex) {
        return new DataModelResolver(openAPIComponentsIndex, setupPropertyNameResolver());
    }

    protected RequestsHandlersResolver setupRequestsHandlersResolver(OpenApiComponentsIndex openApiComponentsIndex, DataModelResolver dataModelResolver) {
        ParameterNameResolver parameterNameResolver = setupParameterNameResolver();
        return new RequestsHandlersResolver(
                setupHttpOperationsGroupingStrategy(),
                setupRequestsHandlerTypeNameResolver(),
                setupRequestsHandlerMethodNameResolver(),
                setupRequestsHandlerMethodsResolver(openApiComponentsIndex, dataModelResolver), parameterNameResolver);
    }

    protected HttpOperationsGroupsResolver setupHttpOperationsGroupingStrategy() {
        return new OperationFirstTagHttpOperationsGroupsResolver(this.getOptions().getGenerateOnlyTags(), this.getOptions().getSkipTags());
    }

    protected RequestsHandlerMethodsResolver setupRequestsHandlerMethodsResolver(OpenApiComponentsIndex openApiComponentsIndex,
                                                                                 DataModelResolver dataModelResolver) {
        return new DefaultRequestsHandlerMethodsResolver(dataModelResolver, openApiComponentsIndex);
    }

    protected abstract PropertyNameResolver setupPropertyNameResolver();

    protected abstract ParameterNameResolver setupParameterNameResolver();

    protected abstract RequestsHandlerMethodNameResolver setupRequestsHandlerMethodNameResolver();

    protected abstract RequestsHandlerTypeNameResolver setupRequestsHandlerTypeNameResolver();

}
