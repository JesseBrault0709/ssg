# TODO

Here will be kept all of the various todos for this project, organized by release.

## 0.4.1

- [ ] `Text` component for simply rendering Text objects. Can be used as such:
```
<Text path='/SomeText.md' />
<Text name='SomeText.md' />
<Text text={text} />
```
- [ ] Update groowt to 0.1.1.

## Future

### Add
- [ ] Plan out plugin system such that we can create custom providers of texts, data, etc.
- [ ] Add `Watchable` interface/trait back; an abstraction over FS watching and other sources (such as a database, etc.).
- [ ] Explore `apply(Plugin)` in buildScripts.

### Fix

## v0.2.0

### Add
- [ ] Write manual.
- [x] Remove `lib` module.
- [x] Add a way for CLI to choose a build to do, or multiple builds, defaulting to 'default' if it exists.
  - [ ] Still must work on 'default'-ing.
- [x] Write lots of tests for buildscript dsl, etc.
- [x] Explore `base` in buildScript dsl.
  - Get rid of `allBuilds` concept, and replace it with composable/concat-able builds. In the dsl we could have a notion of `abstractBuild` which can be 'extended' (i.e., on the left side of a concat operation) but not actually run (since it doesn't have a name).
  - `OutputDir` should be concat-able, such that the left is the *base* for the right.
  - `OutputDirFunctions.concat` should be concat-able as well, such that both are `BiFunction<OutputDir, Build, OutputDir>`, and the output of the left is the input of the right. 
  - Make the delegates as dumb as possible; no more `getResult` methods; make different classes/object handle concat'ing and getting results.
- [x] Provide a way to override `ssgBuilds` variables from the cli.

### Fix
- [ ] Update CHANGELOG to reflect the gsp-dsl changes.
  - [ ] `taskTypes` gone, use class name instead
  - [ ] introduction of `models`
- [x] Change most instances of `Closure<Void>` to `Closure<?>` to help with IDE expectations.
- [ ] Fix auto-imports in build script so we don't need to import things.
- [x] Re-introduce input/output concept to tasks, if possible

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
