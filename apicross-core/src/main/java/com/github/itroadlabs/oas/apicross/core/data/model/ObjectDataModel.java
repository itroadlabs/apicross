package com.github.itroadlabs.oas.apicross.core.data.model;

import com.github.itroadlabs.oas.apicross.core.NamedDatum;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nullable;
import java.util.*;

public class ObjectDataModel extends DataModel {
    private String typeName;
    private final String originalTypeName;
    private Map<String, ObjectDataModelProperty> propertiesMap = new HashMap<>();

    private Map<String, ObjectDataModel> inheritanceChildModels;
    private String inheritanceDiscriminatorPropertyName;
    private ObjectDataModel inheritanceParent;
    private String inheritanceDiscriminatorValue;

    private Set<String> propertiesOriginSchemasNames;
    private DataModel additionalPropertiesDataModel;

    ObjectDataModel(String typeName, Schema<?> source) {
        super(source);
        this.typeName = typeName;
        this.originalTypeName = typeName;
    }

    ObjectDataModel(String typeName, Schema<?> source, Set<ObjectDataModelProperty> properties, DataModel additionalPropertiesDataModel) {
        this(typeName, source);
        this.propertiesMap = new LinkedHashMap<>();
        this.additionalPropertiesDataModel = additionalPropertiesDataModel;
        this.initPropertiesFrom(properties);
    }

    ObjectDataModel(String typeName, Schema<?> source, Map<String, ObjectDataModel> childModels, String discriminatorPropertyName, Map<String, String> mapping) {
        this(typeName, source);
        this.inheritanceDiscriminatorPropertyName = discriminatorPropertyName;
        this.inheritanceChildModels = new LinkedHashMap<>(childModels);
        for (ObjectDataModel childModel : inheritanceChildModels.values()) {
            childModel.inheritanceParent = this;
            childModel.inheritanceDiscriminatorValue = mapping != null ?
                    mapping.getOrDefault(childModel.typeName, childModel.typeName) : childModel.typeName;
        }

        // Some properties from child schemas might be declared in the same schemas.
        // For example SchemaA is a AllOf(SchemaX and something else), SchemaB is AllOf(SchemaX and something else),
        // so when SchemaA and SchemaB is child data models for this model, so their common properties can be moved to this data model

        Set<String> originSchemasNamesForCommonProperties = collectCommonPropertiesOriginSchemasNames();

        List<ObjectDataModelProperty> collectedCommonProperties = new ArrayList<>();
        for (ObjectDataModel childModel : this.inheritanceChildModels.values()) {
            List<ObjectDataModelProperty> propsToBeMovedToParent = childModel.removePropertiesForOriginSources(originSchemasNamesForCommonProperties);
            propsToBeMovedToParent.removeIf(property -> property.getName().equals(this.inheritanceDiscriminatorPropertyName));
            childModel.removeRequiredProperty(this.inheritanceDiscriminatorPropertyName);
            collectedCommonProperties.addAll(propsToBeMovedToParent);
        }
        this.initPropertiesFrom(collectedCommonProperties); // TODO: here in the collectedCommonProperties might be duplicates, but they are avoid in the initPropertiesFrom(), and it's not clear :(
    }

    private Set<String> collectCommonPropertiesOriginSchemasNames() {
        Set<String> commonSchemasNames = new HashSet<>();

        for (ObjectDataModel childSchema : this.inheritanceChildModels.values()) {
            commonSchemasNames.addAll(childSchema.propertiesOriginSchemasNames);
        }
        for (ObjectDataModel childSchema : this.inheritanceChildModels.values()) {
            commonSchemasNames.retainAll(childSchema.propertiesOriginSchemasNames);
        }
        commonSchemasNames.removeIf(Objects::isNull);
        return commonSchemasNames;
    }

    private List<ObjectDataModelProperty> removePropertiesForOriginSources(Set<String> originSchemasNames) {
        List<ObjectDataModelProperty> propsToBeRemoved = new ArrayList<>();
        List<ObjectDataModelProperty> propsToBeLeft = new ArrayList<>();
        for (ObjectDataModelProperty property : propertiesMap.values()) {
            String originSchemaName = property.getOriginSchemaName();
            if ((originSchemaName != null) && originSchemasNames.contains(originSchemaName)) {
                propsToBeRemoved.add(property);
            } else {
                propsToBeLeft.add(property);
            }
        }
        propertiesMap.clear();
        initPropertiesFrom(propsToBeLeft);
        return propsToBeRemoved;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOriginalTypeName() {
        return originalTypeName;
    }

    private void initPropertiesFrom(Collection<ObjectDataModelProperty> properties) {
        Set<String> schemasNames = new HashSet<>();
        for (ObjectDataModelProperty property : properties) {
            propertiesMap.put(property.getName(), property);
            String originSchemaName = property.getOriginSchemaName();
            schemasNames.add(originSchemaName);
        }
        schemasNames.removeIf(Objects::isNull);
        this.propertiesOriginSchemasNames = Collections.unmodifiableSet(schemasNames);
    }

    public Set<ObjectDataModelProperty> getProperties() {
        return new LinkedHashSet<>(propertiesMap.values());
    }

    @Nullable
    public ObjectDataModelProperty getProperty(String name) {
        return propertiesMap.get(name);
    }

    public Integer getMinProperties() {
        return getSource().getMinProperties();
    }

    public Integer getMaxProperties() {
        return getSource().getMaxProperties();
    }

    public List<String> getRequiredProperties() {
        return getSource().getRequired();
    }

    public void removeRequiredProperty(String propertyName) {
        List<String> required = getSource().getRequired();
        if (required != null) {
            required.remove(propertyName);
        }
    }

    public DataModel getAdditionalPropertiesDataModel() {
        return additionalPropertiesDataModel;
    }

    public Map<String, ObjectDataModel> getInheritanceChildModelsMap() {
        return inheritanceChildModels;
    }

    @Nullable
    public Collection<ObjectDataModel> getInheritanceChildModels() {
        return inheritanceChildModels != null ? inheritanceChildModels.values() : null;
    }

    public String getInheritanceDiscriminatorPropertyName() {
        return inheritanceDiscriminatorPropertyName;
    }

    public ObjectDataModel getInheritanceParent() {
        return inheritanceParent;
    }

    public String getInheritanceDiscriminatorValue() {
        return inheritanceDiscriminatorValue;
    }

    public void changeTypeName(String newTypeName, boolean clearPropertiesAndInheritance) {
        this.typeName = newTypeName;
        if (clearPropertiesAndInheritance) {
            this.propertiesMap.clear();
            this.inheritanceChildModels = null;
            this.inheritanceDiscriminatorPropertyName = null;
            this.inheritanceParent = null;
            this.additionalPropertiesDataModel = null;
        }
    }

    public void changeTypeToExternal(String newTypeName) {
        changeTypeName(newTypeName, true);
        addCustomAttribute("externalType", Boolean.TRUE);
    }

    public boolean isContainingOptionalProperties() {
        return getProperties()
                .stream()
                .anyMatch(NamedDatum::isOptional);
    }

    public boolean isContainingArrayProperties() {
        Set<ObjectDataModelProperty> properties = getProperties();
        for (ObjectDataModelProperty property : properties) {
            if (property.getType().isArray()) {
                return true;
            }
        }
        return false;
    }

    public void replacePropertyTypeByExternalTypesMap(Map<String, String> externalTypesMap) {
        // TODO: make it deeper - replace for array items, for example
        Set<ObjectDataModelProperty> properties = this.getProperties();
        for (ObjectDataModelProperty property : properties) {
            DataModel type = property.getType();
            if (type instanceof ObjectDataModel) {
                String typeName = ((ObjectDataModel) type).getTypeName();
                if (externalTypesMap.containsKey(typeName)) {
                    property.changeTypeToExternal(externalTypesMap.get(typeName));
                }
            }
        }
    }

    public boolean isInheritanceChild() {
        return inheritanceParent != null;
    }

    public String getInheritanceParentTypeName() {
        return inheritanceParent != null ? inheritanceParent.getTypeName() : null;
    }

    public String getInheritanceParentOriginalTypeName() {
        return inheritanceParent != null ? inheritanceParent.getOriginalTypeName() : null;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectDataModel)) {
            return false;
        }
        ObjectDataModel dataModel = (ObjectDataModel) object;
        if (!dataModel.getClass().equals(this.getClass())) {
            return false;
        }
        return this.typeName != null && this.typeName.equals(dataModel.typeName);
    }

    @Override
    public int hashCode() {
        return this.getSource().getType() != null ? this.getSource().getType().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ObjectDataModel{" +
                "typeName='" + typeName + '\'' +
                '}';
    }
}
