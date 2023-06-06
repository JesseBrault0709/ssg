package com.jessebrault.ssg.provider;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.BiFunction;

public interface DirectoryCollectionProvider<T> extends CollectionProvider<T> {
    File getBaseDirectory();
    BiFunction<File, String, @Nullable T> getMapper();
}
