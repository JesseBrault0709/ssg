package com.jessebrault.ssg.util

import groovy.transform.TupleConstructor

import java.util.regex.Pattern

/**
 * A very basic class for handling globs. Can handle one ** at most.
 * In file/directory names, only '.' is escaped; any other special characters
 * may cause an invalid regex pattern.
 */
final class Glob {

    private sealed interface GlobPart permits Literal, AnyDirectoryHierarchy, GlobFileOrDirectory {}

    @TupleConstructor
    private static final class Literal implements GlobPart {
        final String literal
    }

    private static final class AnyDirectoryHierarchy implements GlobPart {}

    @TupleConstructor
    private static final class GlobFileOrDirectory implements GlobPart {
        final String original
        final Pattern regexPattern
    }

    private static List<GlobPart> toParts(String glob) {
        final List<String> originalParts
        if (glob.startsWith('/')) {
            originalParts = glob.substring(1).split('/') as List<String>
        } else {
            originalParts = glob.split('/') as List<String>
        }

        def result = originalParts.collect {
            if (it == '**') {
                new AnyDirectoryHierarchy()
            } else if (it.contains('*')) {
                def replaced = it.replace([
                        '*': '.',
                        '.': '\\.'
                ])
                new GlobFileOrDirectory(it, ~replaced)
            } else {
                new Literal(it)
            }
        }

        result
    }

    private final List<GlobPart> parts

    Glob(String glob) {
        this.parts = toParts(glob)
    }

    boolean matches(File file) {
        this.matches(file.toString().replace(File.separator, '/'))
    }

    /**
     * @param subject Must contain only '/' as a separator.
     * @return whether the subject String matches this glob.
     */
    boolean matches(String subject) {
        final List<String> subjectParts
        if (subject.startsWith('/')) {
            subjectParts = subject.substring(1).split('/') as List<String>
        } else {
            subjectParts = subject.split('/') as List<String>
        }

        def subjectPartIter = subjectParts.iterator()
        def subjectPartStack = new LinkedList<String>()
        while (subjectPartIter.hasNext()) {
            subjectPartStack.push(subjectPartIter.next())
        }

        boolean result = true
        parts:
        for (def part : this.parts) {
            switch (part) {
                case Literal -> {
                    if (subjectPartStack.isEmpty()) {
                        result = false
                        break
                    }
                    def subjectPart = subjectPartStack.pop()
                    if (part.literal != subjectPart) {
                        result = false
                        break
                    }
                }
                case AnyDirectoryHierarchy -> {
                    while (!subjectPartStack.isEmpty()) {
                        def current = subjectPartStack.pop()
                        if (subjectPartStack.isEmpty()) {
                            subjectPartStack.push(current)
                            continue parts
                        }
                    }
                }
                case GlobFileOrDirectory -> {
                    def subjectPart = subjectPartStack.pop()
                    def m = part.regexPattern.matcher(subjectPart)
                    if (!m.matches()) {
                        result = false
                        break
                    }
                }
            }
        }
        result
    }

}
