package apicross.demo.common.utils;

import io.github.itroadlabs.apicross.beanvalidation.BeanPropertiesValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({BeanPropertiesValidationGroup.class, Default.class})
public interface ValidationStages {
}
