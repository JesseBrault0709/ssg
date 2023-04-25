# TODO

Here will be kept all of the various todos for this project, organized by release.

## Next

### Add
- [ ] Add some kind of `outputs` map to dsl that can be used to retrieve various info about another output of the current build. For example:
    ```groovy
    // while in a special page 'special.gsp' we could get the 'output' info for a text 'blog/post.md'
    def post = outputs['blog/post.md']
    assert post instanceof Output // or something
    assert post.path == 'blog/post.md'
    assert post.targetPath = 'blog/post.html'
    // as well as some other information, perhaps such as the Type, extension, *etc.*
    ```
- [ ] Add `extensionUtil` object to dsl.
- [ ] Investigate imports, including static, in scripts
- [ ] Get rid of `taskTypes` DSL, replace with static import of task types to scripts
- [ ] Plan out plugin system such that we can create custom providers of texts, data, etc.
- [ ] Plan out `data` models DSL
- [ ] Provide a way to override `ssgBuilds` variables from the cli.

### Fix
