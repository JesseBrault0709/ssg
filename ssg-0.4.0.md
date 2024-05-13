# Targeting 0.4.0: Ideas and Plans

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
