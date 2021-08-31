package io.github.itroadlabs.apicross.core.data.model;

import io.github.itroadlabs.apicross.core.NamedDatum;
import io.swagger.v3.oas.models.media.Schema;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectDataModel extends DataModel {
    private String typeName;
    private String originalTypeName;
    private Map<String, ObjectDataModelProperty> propertiesMap = new HashMap<>();
    private Map<String, ObjectDataModel> inheritanceChildModels;
    private String inheritanceDiscriminatorPropertyName;
    private ObjectDataModel inheritanceParent;
    private String inheritanceDiscriminatorValue;
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

        Set<SchemaWithPropertyName> commonProperties = commonProperties(childModels.values());

        List<ObjectDataModelProperty> collectedCommonProperties = new ArrayList<>();
        for (ObjectDataModel childModel : this.inheritanceChildModels.values()) {
            List<ObjectDataModelProperty> propsToBeMovedToParent = childModel.removePropertiesFor(commonProperties);
            propsToBeMovedToParent.removeIf(property -> property.getName().equals(this.inheritanceDiscriminatorPropertyName));
            childModel.removeRequiredProperty(this.inheritanceDiscriminatorPropertyName);
            collectedCommonProperties.addAll(propsToBeMovedToParent);
        }
        this.initPropertiesFrom(collectedCommonProperties); // TODO: here in the collectedCommonProperties might be duplicates, but they are avoid in the initPropertiesFrom(), and it's not clear :(
    }

    static Set<SchemaWithPropertyName> commonProperties(Collection<ObjectDataModel> models) {
        List<Collection<SchemaWithPropertyName>> list = new ArrayList<>();
        for (ObjectDataModel model : models) {
            list.add(model.getProperties().stream()
                    .map(objectDataModelProperty ->
                            new SchemaWithPropertyName(objectDataModelProperty.getName(), objectDataModelProperty.getType().getSource()))
                    .collect(Collectors.toList()));
        }
        return findIntersection(new HashSet<>(), list);
    }

    static <T, C extends Collection<T>> C findIntersection(C newCollection, List<Collection<T>> collections) {
        boolean first = true;
        for (Collection<T> collection : collections) {
            if (first) {
                newCollection.addAll(collection);
                first = false;
            } else {
                newCollection.retainAll(collection);
            }
        }
        return newCollection;
    }

    static class SchemaWithPropertyName {
        private final String propertyName;
        private final Schema<?> schema;

        public SchemaWithPropertyName(@NonNull String propertyName, @NonNull Schema<?> schema) {
            this.propertyName = propertyName;
            this.schema = schema;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Schema<?> getSchema() {
            return schema;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SchemaWithPropertyName that = (SchemaWithPropertyName) o;
            return propertyName.equals(that.propertyName) && schema.equals(that.schema);
        }

        @Override
        public int hashCode() {
            return Objects.hash(propertyName, schema);
        }
    }

    private List<ObjectDataModelProperty> removePropertiesFor(Set<SchemaWithPropertyName> source) {
        List<ObjectDataModelProperty> propsToBeRemoved = new ArrayList<>();
        for (SchemaWithPropertyName schemaWithPropertyName : source) {
            ObjectDataModelProperty property = this.propertiesMap.get(schemaWithPropertyName.getPropertyName());
            if (property != null && property.getType().getSource().equals(schemaWithPropertyName.getSchema())) {
                propsToBeRemoved.add(property);
                this.propertiesMap.remove(property.getName());
            }
        }
        return propsToBeRemoved;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        if (this.originalTypeName == null) {
            this.originalTypeName = typeName;
        }
    }

    public String getOriginalTypeName() {
        return originalTypeName;
    }

    private void initPropertiesFrom(Collection<ObjectDataModelProperty> properties) {
        for (ObjectDataModelProperty property : properties) {
            propertiesMap.put(property.getName(), property);
        }
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

    public Set<String> getRequiredProperties() {
        return this.getProperties().stream()
                .filter(NamedDatum::isRequired)
                .map(NamedDatum::getName)
                .collect(Collectors.toSet());
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
