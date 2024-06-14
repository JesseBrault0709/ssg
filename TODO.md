# TODO

Here will be kept all of the various todos for this project, organized by release.
N.b. that v0.3.0 was skipped because of such a fundamental change in the usage of the
program with the incorporation of Groowt and Web View Components.

## 0.6.0
- [ ] Plugin system for build scripts

## 0.5.0
- [ ] watch/dev mode and server
- [ ] Reorganize gradle project layout so there is less hunting around for files

## 0.4.3
- [ ] `Text` component for simply rendering Text objects. Can be used as such:
```
<Text path='/SomeText.md' />
<Text name='SomeText.md' />
<Text text={text} />
```
- [ ] `TextContainer` for accessing all found texts
- [ ] `ModelFactory` for creating models, and `TextModelFactory` for creating models from texts.
- [ ] `Model` component for rendering a model with either a supplied renderer, or a registered `ModelRenderer`
- [ ] `Global` component for rendering globals.

## 0.4.1
- [x] Update groowt to 0.1.2.

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
