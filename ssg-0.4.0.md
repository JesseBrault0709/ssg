# Targeting 0.4.0: Ideas and Plans
**This file is old as of 06/14/2024.**

Have the following layout of dirs and files. It is a combined gradle/ssg project.
- bin: a folder for shell scripts
  - ssg: a shell script to invoke the ssg-cli
- pages: a source set for pages
  - groovy
  - wvc
  - gst
- parts: a source set for parts/components
  - groovy
  - wvc
  - gst
- src: a source set for the main/root project which can be depended upon by everything else. Useful for models, etc.
  - groovy
  - java
  - resources
- ssg: the folder for the build logic
  - buildSrc: a gradle subproject where we can put custom build logic that is accessible from scripts.
    - src
      - groovy
        - default.ssg.groovy: the 'default' build, which production/preview/etc. can extend
    - build.gradle
  - production.ssg.groovy: a 'production' build
  - preview.ssg.groovy: a 'preview' build
- texts: a general folder for texts and other textual data, can be .md, .txt, .html, etc.
- build.gradle: the root project build.gradle
- settings.gradle: the usual gradle settings

## Api TODO
- [X] Move from `Provider`/`Property` in `api` to `groowt.util.provider`.
- [X] Move from all the fp-util stuff to `groowt.util.fp`.
- [X] Get rid of graph-dependency.
- [ ] Create a `SsgComponentContext` which extends `DefaultWebViewComponentContext` and automatically resolves based
  on package names given in the build script.
- [ ] Add a section for plugins in the build script. Plugins can transform the build spec, generator, etc.

## New Build Script DSL

**This section is old as of 5/17/24.** See the newer example in `test-ssg-project/ssg/default.groovy`. 

Simple example:
```groovy
extendsFrom('default')

// isAbstract = true

siteName = 'Hello, World!'
baseUrl = 'https://helloworld.com/'
buildDir = new File('hello-world-build')

globals {
    someProp = 'Some property.'
}

includedBuilds << 'anotherBuild'
```

Example with models:
```groovy
Set<NamedModel> myModels = [
        new NamedModel(name: 'thing0'),
        new NamedModel(name: 'thing1')
]

models {
    myModels.each {
        add(it.name, it)
    }
}
```
