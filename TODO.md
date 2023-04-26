# TODO

Here will be kept all of the various todos for this project, organized by release.

## v0.3.0

### Add
- [ ] Plan out plugin system such that we can create custom providers of texts, data, etc.
- [ ] Provide a way to override `ssgBuilds` variables from the cli.

### Fix

## v0.2.0

### Add
- [ ] Write manual.
- [x] Remove `lib` module.
- [ ] Add a way for CLI to choose a build to do, or multiple builds, defaulting to 'default' if it exists.
- [ ] Write lots of tests for buildscript dsl, etc.

### Fix
- [ ] Update CHANGELOG to reflect the gsp-dsl changes.
- [x] Change most instances of `Closure<Void>` to `Closure<?>` to help with IDE expectations.

## Finished

### v0.2.0
- [x] Investigate imports, including static, in scripts
  - Does not work; must use binding
- [x] Get rid of `taskTypes` DSL, replace with static import of task types to scripts
  - Done via the binding directly
- [x] Plan out `data` models DSL
  - Done via `models` dsl

### v0.1.0
- [x] Add some kind of `outputs` map to dsl that can be used to retrieve various info about another output of the current build. For example:
    ```groovy
    // while in a special page 'special.gsp' we could get the 'output' info for a text 'blog/post.md'
    def post = outputs['blog/post.md']
    assert post instanceof Output // or something
    assert post.path == 'blog/post.md'
    assert post.targetPath = 'blog/post.html'
    // as well as some other information, perhaps such as the Type, extension, *etc.*
    ```