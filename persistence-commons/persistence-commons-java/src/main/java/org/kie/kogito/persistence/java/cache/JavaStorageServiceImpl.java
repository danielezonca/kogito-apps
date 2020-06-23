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

package org.kie.kogito.persistence.java.cache;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.persistence.api.factory.StorageQualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

import static org.kie.kogito.persistence.java.Constants.JAVA_STORAGE;

@ApplicationScoped
@StorageQualifier(JAVA_STORAGE)
public class JavaStorageServiceImpl implements StorageService {

    private final Map<String, Storage<?, ?>> caches = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected <K, V> Storage<K, V> getOrCreateCache(final String name, final String rootType) {
        return (Storage<K, V>) caches
                .computeIfAbsent(name,
                        key -> new JavaStorageImpl<>(new HashMap<>(), rootType));
    }

    @Override
    public Storage<String, String> getCache(String name){
        return getOrCreateCache(name, String.class.getName());
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type) {
        return getOrCreateCache(name, type.getName());
    }

    @Override
    public <T> Storage<String, T> getCacheWithDataFormat(String name, Class<T> type, String rootType){
        return getOrCreateCache(name, rootType);
    }
}