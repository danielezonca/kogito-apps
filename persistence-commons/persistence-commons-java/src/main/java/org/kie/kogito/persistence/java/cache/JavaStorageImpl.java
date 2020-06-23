/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.java.query.JavaQueryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class JavaStorageImpl<K, V> implements Storage<K, V> {

    private final Map<K, V> delegate;
    private final String rootType;
    private final List<Consumer<V>> objectCreatedListeners = new ArrayList<>();
    private final List<Consumer<V>> objectUpdatedListeners = new ArrayList<>();
    private final List<Consumer<K>> objectRemovedListener = new ArrayList<>();

    public JavaStorageImpl(Map<K, V> delegate, String rootType) {
        this.delegate = delegate;
        this.rootType = rootType;
    }

    public V get(K key) {
        return delegate.get(key);
    }

    public void clear() {
        delegate.clear();
    }

    public V remove(K key) {
        if (containsKey(key)) {
            objectRemovedListener.forEach(consumer -> consumer.accept(key));
        }
        return delegate.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return delegate.containsKey(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public V put(K key, V value) {
        V previousValue = get(key);
        if (previousValue != null) {
            objectUpdatedListeners.forEach(consumer -> consumer.accept(previousValue));
        }
        objectCreatedListeners.forEach(consumer -> consumer.accept(value));
        return delegate.put(key, value);
    }

    @Override
    public void addObjectCreatedListener(Consumer<V> consumer) {
        objectCreatedListeners.add(consumer);
    }

    @Override
    public void addObjectUpdatedListener(Consumer<V> consumer) {
        objectUpdatedListeners.add(consumer);
    }

    @Override
    public void addObjectRemovedListener(Consumer<K> consumer) {
        objectRemovedListener.add(consumer);
    }

    @Override
    public String getRootType() {
        return rootType;
    }

    @Override
    public Query<V> query() {
        return new JavaQueryImpl<>(this);
    }

    protected Map<K, V> getDelegate() {
        return delegate;
    }
}
