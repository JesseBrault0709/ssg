# Changelog

All notable changes to SSG will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to 
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Next/Unreleased

### Added

- A `tagBuilder` of type `DynamicTagBuilder` is available in Templates, SpecialPages, and Parts.
    ```groovy
    def simpleTag = tagBuilder.test()
    assert simpleTag == '<test />'
  
    def tagWithBody = tagBuilder.title 'Hello, World!'
    assert tagWithBody == '<title>Hello, World!</title>'
    
    def tagWithAttributes = tagBuilder.meta name: 'og:title', content: 'Hello, World!'
    assert tagWithAttributes == '<meta name="og:title" content="Hello, World!" />'
  
    def tagWithAttributesAndBody = tagBuilder.p([id: 'my-paragraph'], 'Hello, World!')
    assert tagWithAttributesAndBody == '<p id="my-paragraph">Hello, World!</p>'
    ```
  This is likely most useful for building simple, one-line html/xml tags. 
  [93687d](https://github.com/JesseBrault0709/ssg/commit/936587d).
- **Breaking**: the `text` object in Templates is now an instance of `EmbeddableText` instead of `String`. Thus, one must
  use `text.render()` to obtain the rendered text. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).
- Parts have a `text` object of type `EmbeddableText`. If we are rendering a part called from anything other than a Template
  (which has an associated text), this will be `null`. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).

### Fixed
