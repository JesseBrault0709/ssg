package com.jessebrault.ssg.buildscript.delegates

import com.jessebrault.ssg.model.Model
import com.jessebrault.ssg.model.Models
import groowt.util.fp.provider.DefaultNamedProvider
import groowt.util.fp.provider.NamedProvider
import groowt.util.fp.provider.Provider
import org.jetbrains.annotations.ApiStatus

import java.util.function.Supplier

final class ModelsDelegate {

    private final Map<String, Provider<Model>> modelsMap = [:]

    void add(String name, Object modelObject) {
        this.modelsMap.put(name) { Models.of(name, modelObject ) }
    }

    void add(Model model) {
        this.modelsMap.put(model.name) { model }
    }

    void add(NamedProvider<Model> namedModelProvider) {
        this.modelsMap.put(namedModelProvider.name, namedModelProvider)
    }

    void add(String name, Provider<Model> modelProvider) {
        this.modelsMap.put(name, modelProvider)
    }

    void add(String name, Supplier objectSupplier) {
        this.modelsMap.put(name) { Models.ofSupplier(name, objectSupplier) }
    }
    
    void addAll(Map<String, Object> namesAndModels) {
        namesAndModels.each { name, modelObject ->
            this.add(name, modelObject)
        }
    }

    @ApiStatus.Internal
    Set<NamedProvider<Model>> getResult() {
        this.modelsMap.inject([] as Set<NamedProvider<Model>>) { acc, name, modelProvider ->
            acc << new DefaultNamedProvider(name, modelProvider)
        }
    }

}
