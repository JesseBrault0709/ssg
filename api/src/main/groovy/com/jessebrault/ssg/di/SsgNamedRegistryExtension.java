package com.jessebrault.ssg.di;

import groowt.util.di.*;
import jakarta.inject.Named;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SsgNamedRegistryExtension implements NamedRegistryExtension {

    protected static void checkName(String name) {
        if (name.startsWith(":") || name.endsWith(":")) {
            throw new IllegalArgumentException(
                    "Illegal ssg @Named format: cannot start or end with colon (':'); given: " + name
            );
        }
    }

    protected static @Nullable String getPrefix(String fullName) {
        final var firstColon = fullName.indexOf(":");
        if (firstColon == -1) {
            return null;
        } else {
            return fullName.substring(0, firstColon);
        }
    }

    protected static String getAfterPrefix(String fullName) {
        final int firstColon = fullName.indexOf(":");
        if (firstColon == -1) {
            return fullName;
        } else {
            return fullName.substring(firstColon + 1);
        }
    }

    protected static boolean hasPrefix(String fullName) {
        return fullName.contains(":");
    }

    protected static String getSimplePrefix(Class<?> dependencyClass) {
        final String simpleTypeName = dependencyClass.getSimpleName();
        final String simpleTypeNameStart = simpleTypeName.substring(0, 1).toLowerCase();
        final String uncapitalizedSimpleTypeName;
        if (simpleTypeName.length() > 1) {
            uncapitalizedSimpleTypeName = simpleTypeNameStart + simpleTypeName.substring(1);
        } else {
            uncapitalizedSimpleTypeName = simpleTypeNameStart;
        }
        return uncapitalizedSimpleTypeName;
    }

    protected static String getCanonicalPrefix(Class<?> dependencyClass) {
        return dependencyClass.getName();
    }

    public static class SsgNamedQualifierHandler implements QualifierHandler<Named> {

        private final SsgNamedRegistryExtension extension;

        public SsgNamedQualifierHandler(SsgNamedRegistryExtension extension) {
            this.extension = extension;
        }

        @Override
        public @Nullable <T> Binding<T> handle(Named annotation, Class<T> dependencyClass) {
            return this.extension.getBinding(new SimpleKeyHolder<>(
                    SsgNamedRegistryExtension.class,
                    dependencyClass,
                    annotation.value()
            ));
        }

    }

    public static class SsgNamedWithPrefixKeyHolder<T> implements KeyHolder<NamedRegistryExtension, String, T> {

        private final Class<T> dependencyType;
        private final @Nullable String prefix;
        private final String afterPrefix;

        public SsgNamedWithPrefixKeyHolder(
                Class<T> dependencyType,
                @Nullable String prefix,
                String afterPrefix
        ) {
            this.dependencyType = dependencyType;
            this.afterPrefix = afterPrefix;
            this.prefix = prefix;
        }

        @Override
        public Class<NamedRegistryExtension> binderType() {
            return NamedRegistryExtension.class;
        }

        @Override
        public Class<T> type() {
            return this.dependencyType;
        }

        @Override
        public String key() {
            return this.afterPrefix;
        }

        public @Nullable String prefix() {
            return this.prefix;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof SsgNamedWithPrefixKeyHolder<?> other) {
                return this.dependencyType.equals(other.type())
                        && this.key().equals(other.key())
                        && Objects.equals(this.prefix(), other.prefix());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = dependencyType.hashCode();
            result = 31 * result + afterPrefix.hashCode();
            result = 31 * result + Objects.hashCode(prefix);
            return result;
        }

    }

    private final Map<KeyHolder<NamedRegistryExtension, String, ?>, Binding<?>> bindings = new HashMap<>();
    private final QualifierHandler<Named> qualifierHandler = this.getNamedQualifierHandler();

    protected QualifierHandler<Named> getNamedQualifierHandler() {
        return new SsgNamedQualifierHandler(this);
    }

    @Override
    public Class<String> getKeyClass() {
        return String.class;
    }

    @Override
    public <B extends KeyBinder<String>, T> void bind(
            KeyHolder<B, ? extends String, T> keyHolder,
            Consumer<? super BindingConfigurator<T>> configure
    ) {
        final var configurator = new SimpleBindingConfigurator<>(keyHolder.type());
        configure.accept(configurator);
        final String fullName = keyHolder.key();
        checkName(fullName);
        if (hasPrefix(fullName)) {
            this.bindings.put(new SsgNamedWithPrefixKeyHolder<>(
                    keyHolder.type(),
                    getPrefix(fullName),
                    getAfterPrefix(fullName)
            ), configurator.getBinding());
        } else {
            this.bindings.put(new SimpleKeyHolder<>(
                    NamedRegistryExtension.class,
                    keyHolder.type(),
                    keyHolder.key()
            ), configurator.getBinding());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <B extends KeyBinder<String>, T> Binding<T> getBinding(
            KeyHolder<B, ? extends String, T> keyHolder
    ) {
        final String fullName = keyHolder.key();
        checkName(fullName);
        if (hasPrefix(fullName)) {
            if (keyHolder instanceof SsgNamedWithPrefixKeyHolder<?> && this.bindings.containsKey(keyHolder)) {
                return (Binding<T>) this.bindings.get(keyHolder);
            } else {
                final String afterPrefix = getAfterPrefix(fullName);
                final Class<T> type = keyHolder.type();

                final @Nullable Binding<T> withSimple = (Binding<T>) this.bindings.get(
                        new SsgNamedWithPrefixKeyHolder<>(type, afterPrefix, getSimplePrefix(type))
                );

                if (withSimple != null) {
                    return withSimple;
                }

                return (Binding<T>) this.bindings.get(new SsgNamedWithPrefixKeyHolder<>(
                        type, afterPrefix, getCanonicalPrefix(type)
                ));
            }
        } else {
            return (Binding<T>) this.bindings.getOrDefault(keyHolder, null);
        }
    }

    @Override
    public <B extends KeyBinder<String>, T> void removeBinding(KeyHolder<B, ? extends String, T> keyHolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <B extends KeyBinder<String>, T> void removeBindingIf(
            KeyHolder<B, ? extends String, T> keyHolder,
            Predicate<? super Binding<T>> filter
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearAllBindings() {
        this.bindings.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> qualifierType) {
        return (QualifierHandler<A>) this.qualifierHandler;
    }

}
