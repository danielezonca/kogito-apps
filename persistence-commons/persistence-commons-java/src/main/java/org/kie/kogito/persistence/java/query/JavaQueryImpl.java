/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.java.query;

import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.AttributeSort;
import org.kie.kogito.persistence.api.query.FilterCondition;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.java.cache.JavaStorageImpl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.persistence.api.query.FilterCondition.*;

public class JavaQueryImpl<T> implements Query<T> {

    private Integer limit;
    private Integer offset;
    private List<AttributeFilter<?>> filters;
    private List<AttributeSort> sortBy;
    private Map<?, T> delegate;

    public JavaQueryImpl(JavaStorageImpl<?, T> storage) {
        this.delegate = storage.get;
    }

    @Override
    public Query<T> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query<T> offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query<T> filter(List<AttributeFilter<?>> filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public Query<T> sort(List<AttributeSort> sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    @Override
    public List<T> execute() {
        return Function.<List<T>>identity()
                .andThen(this::applyWhereConditions)
                .andThen(this::applySortConditions)
                .andThen(this::applyOffset)
                .andThen(this::applyLimit)
                .apply(new ArrayList<>(delegate.values()));
    }

    private List<T> applyWhereConditions(List<T> input) {
        if (filters == null || filters.isEmpty()) {
            return input;
        }

        return input.stream()
                .filter(i ->
                        filters.stream()
                                .allMatch(f -> applyFilter(f, i)))
                .collect(toList());

    }

    private List<T> applySortConditions(List<T> input) {
        if (sortBy == null || sortBy.isEmpty()) {
            return input;
        }
        throw new UnsupportedOperationException("sort");
    }

    private List<T> applyOffset(List<T> input) {
        if (offset == null) {
            return input;
        }
        return input.subList(offset, input.size());
    }

    private List<T> applyLimit(List<T> input) {
        if (limit == null) {
            return input;
        }
        return input.subList(0, limit);
    }

    @SuppressWarnings("unchecked")
    private boolean applyFilter(AttributeFilter<?> filter, T i) {
        switch (filter.getCondition()) {
            case CONTAINS:
//                    return format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                throw new UnsupportedOperationException(CONTAINS.getLabel());
            case CONTAINS_ALL:
//                    return (String) ((List) filter.getValue()).stream().map(o -> format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(o))).collect(joining(AND));
                throw new UnsupportedOperationException(CONTAINS_ALL.getLabel());
            case CONTAINS_ANY:
//                    return (String) ((List) filter.getValue()).stream().map(o -> format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(o))).collect(joining(OR));
                throw new UnsupportedOperationException(CONTAINS_ANY.getLabel());
            case LIKE:
//                    return format("o.%s like %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue())).replaceAll("\\*", "%");
                throw new UnsupportedOperationException(LIKE.getLabel());
            case EQUAL:
//                    return format(ATTRIBUTE_VALUE, filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                throw new UnsupportedOperationException(EQUAL.getLabel());
            case IN:
//                    return format("o.%s in (%s)", filter.getAttribute(), ((List) filter.getValue()).stream().map(getValueForQueryString()).collect(joining(", ")));
                throw new UnsupportedOperationException(IN.getLabel());
            case IS_NULL:
//                    return format("o.%s is null", filter.getAttribute());
                throw new UnsupportedOperationException(IS_NULL.getLabel());
            case NOT_NULL:
//                    return format("o.%s is not null", filter.getAttribute());
                throw new UnsupportedOperationException(NOT_NULL.getLabel());
            case BETWEEN:
//                    List<Object> value = (List<Object>) filter.getValue();
//                    return format("o.%s between %s and %s", filter.getAttribute(), getValueForQueryString().apply(value.get(0)), getValueForQueryString().apply(value.get(1)));
                throw new UnsupportedOperationException(BETWEEN.getLabel());
            case GT:

//                    return format("o.%s > %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                throw new UnsupportedOperationException(GT.getLabel());
            case GTE:
//                    return format("o.%s >= %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                throw new UnsupportedOperationException(GTE.getLabel());
            case LT:
//                    return format("o.%s < %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                throw new UnsupportedOperationException(LT.getLabel());
            case LTE:
//                    return format("o.%s <= %s", filter.getAttribute(), getValueForQueryString().apply(filter.getValue()));
                throw new UnsupportedOperationException(LTE.getLabel());
            case OR:
                List<AttributeFilter<?>> filters = (List<AttributeFilter<?>>) filter.getValue();
//                    return getRecursiveString(filter, OR);
                throw new UnsupportedOperationException(FilterCondition.OR.getLabel());
            case AND:
//                    return getRecursiveString(filter, AND);
                throw new UnsupportedOperationException(FilterCondition.AND.getLabel());
            default:
                return true;
        }
    }
}
