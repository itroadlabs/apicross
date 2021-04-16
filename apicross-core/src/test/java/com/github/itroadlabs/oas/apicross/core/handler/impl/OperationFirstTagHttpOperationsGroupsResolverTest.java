package com.github.itroadlabs.oas.apicross.core.handler.impl;

import com.github.itroadlabs.oas.apicross.core.handler.impl.OperationFirstTagHttpOperationsGroupsResolver;
import com.github.itroadlabs.oas.apicross.core.handler.model.HttpOperationsGroup;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class OperationFirstTagHttpOperationsGroupsResolverTest {
    @Test
    public void testGrouping() {
        OperationFirstTagHttpOperationsGroupsResolver grouper
                = new OperationFirstTagHttpOperationsGroupsResolver(Collections.emptySet(), Collections.emptySet());
        Paths paths = new Paths()
                .addPathItem("/test1",
                        new PathItem()
                                .get(new Operation().operationId("a").addTagsItem("tag1").addTagsItem("tag2"))
                                .post(new Operation().operationId("b").addTagsItem("tag1").addTagsItem("tag3")))
                .addPathItem("/test2",
                        new PathItem()
                                .patch(new Operation().operationId("c").addTagsItem("tag2").addTagsItem("tag3")))
                .addPathItem("/test3",
                        new PathItem()
                                .delete(new Operation().operationId("d").addTagsItem("tag2")));
        Collection<HttpOperationsGroup> groups = grouper.resolve(paths);
        assertEquals(2, groups.size());
        assertEquals(2, groupByName(groups, "tag1").size());
        assertEquals(2, groupByName(groups, "tag2").size());
    }

    @Test
    public void testSkipTags() {
        OperationFirstTagHttpOperationsGroupsResolver grouper
                = new OperationFirstTagHttpOperationsGroupsResolver(null, Collections.singleton("tag2"));
        Paths paths = new Paths()
                .addPathItem("/test1",
                        new PathItem()
                                .get(new Operation().operationId("a").addTagsItem("tag1").addTagsItem("tag2"))
                                .post(new Operation().operationId("b").addTagsItem("tag1").addTagsItem("tag3")))
                .addPathItem("/test2",
                        new PathItem()
                                .patch(new Operation().operationId("c").addTagsItem("tag2").addTagsItem("tag3")))
                .addPathItem("/test3",
                        new PathItem()
                                .delete(new Operation().operationId("d").addTagsItem("tag2")));
        Collection<HttpOperationsGroup> groups = grouper.resolve(paths);
        assertEquals(1, groups.size());
        assertEquals(1, groupByName(groups, "tag1").size());
        assertNull(groupByName(groups, "tag2"));
    }

    @Test
    public void testOnlyTags_whenIntersectionPresent() {
        OperationFirstTagHttpOperationsGroupsResolver grouper
                = new OperationFirstTagHttpOperationsGroupsResolver(Collections.singleton("tag1"), null);
        Paths paths = new Paths()
                .addPathItem("/test1",
                        new PathItem()
                                .get(new Operation().operationId("a").addTagsItem("tag1").addTagsItem("tag2"))
                                .post(new Operation().operationId("b").addTagsItem("tag1").addTagsItem("tag3")))
                .addPathItem("/test2",
                        new PathItem()
                                .patch(new Operation().operationId("c").addTagsItem("tag2").addTagsItem("tag3")))
                .addPathItem("/test3",
                        new PathItem()
                                .delete(new Operation().operationId("d").addTagsItem("tag2")));
        Collection<HttpOperationsGroup> groups = grouper.resolve(paths);
        assertEquals(1, groups.size());
        assertEquals(2, groupByName(groups, "tag1").size());
    }

    @Test
    public void testOnlyTags_whenNoIntersection() {
        OperationFirstTagHttpOperationsGroupsResolver grouper
                = new OperationFirstTagHttpOperationsGroupsResolver(Collections.singleton("tag4"), null);
        Paths paths = new Paths()
                .addPathItem("/test1",
                        new PathItem()
                                .get(new Operation().operationId("a").addTagsItem("tag1").addTagsItem("tag2"))
                                .post(new Operation().operationId("b").addTagsItem("tag1").addTagsItem("tag3")))
                .addPathItem("/test2",
                        new PathItem()
                                .patch(new Operation().operationId("c").addTagsItem("tag2").addTagsItem("tag3")))
                .addPathItem("/test3",
                        new PathItem()
                                .delete(new Operation().operationId("d").addTagsItem("tag2")));
        Collection<HttpOperationsGroup> groups = grouper.resolve(paths);
        assertEquals(0, groups.size());
    }

    private HttpOperationsGroup groupByName(Collection<HttpOperationsGroup> groups, @Nonnull String name) {
        return groups.stream()
                .filter(httpOperations -> name.equals(httpOperations.getName()))
                .findFirst().orElse(null);
    }
}
