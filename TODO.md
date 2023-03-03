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

### Fix
