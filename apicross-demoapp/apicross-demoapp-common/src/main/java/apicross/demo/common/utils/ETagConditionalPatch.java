package apicross.demo.common.utils;

import apicross.demo.common.models.AbstractEntity;

public abstract class ETagConditionalPatch<T extends AbstractEntity> {
    private final ETagMatchPolicy eTagMatchPolicy;

    public ETagConditionalPatch(ETagMatchPolicy eTagMatchPolicy) {
        this.eTagMatchPolicy = eTagMatchPolicy;
    }

    public void apply(T entity) {
        if (eTagMatchPolicy.matches(entity.etag())) {
            doPatch(entity);
        } else {
            throw new ETagDoesntMatchException();
        }
    }

    protected abstract void doPatch(T entity);
}
