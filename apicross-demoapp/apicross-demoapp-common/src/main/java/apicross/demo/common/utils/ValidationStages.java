package apicross.demo.common.utils;

import com.github.itroadlabs.oas.apicross.beanvalidation.BeanPropertiesValidationGroup;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({BeanPropertiesValidationGroup.class, Default.class})
public interface ValidationStages {
}
