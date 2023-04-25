package com.jessebrault.ssg.model

import groovy.io.FileType
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

import java.nio.file.Path

final class Models {

    static <T> Model<T> of(String name, T t) {
        new SimpleModel<>(name, t)
    }

    static <T> Model<T> from(String name, Closure<T> tClosure) {
        new ClosureBasedModel<>(name, tClosure)
    }

    /**
     * Takes a directory and iterates recursively through all files present in the directory and sub-directories,
     * supplying each File along with a String representing that File's path relative to the base Directory to the
     * given Closure, which then returns a Model containing T, all of which are collected and then returned together.
     *
     * @param directory The base directory in which to search for Files to process.
     * @param fileToModelClosure A Closure which receives two params: the File being processed,
     * and a String representing the path of that File relative to the base directory. Must return
     * a Model containing T.
     * @return A Collection of Models containing Ts.
     */
    static <T> Collection<Model<T>> fromDirectory(
            File directory,
            @ClosureParams(value = FromString, options = ['java.io.File, java.lang.String'])
            Closure<Model<T>> fileToModelClosure
    ) {
        final Collection<Model<T>> models = []
        def directoryPath = Path.of(directory.path)
        directory.eachFileRecurse(FileType.FILES) {
            def relativePath = directoryPath.relativize(Path.of(it.path)).toString()
            models << fileToModelClosure(it, relativePath)
        }
        models
    }

    private Models() {}

}
