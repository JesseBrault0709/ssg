# Changelog

All notable changes to SSG will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Next

### Added

- Parts have access to all other parts now via `parts`, an object of type [`EmbeddablePartsMap`](lib/src/main/groovy/com/jessebrault/ssg/part/EmbeddablePartsMap.groovy). For example:

    ```gsp
    // myPart.gsp
    <% out << parts['otherPart.gsp'].render() %>
    ```
    
  [0e49414](https://github.com/JesseBrault0709/ssg/commit/0e49414).
- A `tagBuilder` object of type [`DynamicTagBuilder`](lib/src/main/groovy/com/jessebrault/ssg/tagbuilder/DynamicTagBuilder.groovy) is available in Templates, SpecialPages, and Parts.

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
    
  This is likely most useful for building simple, one-line html/xml tags. [93687d](https://github.com/JesseBrault0709/ssg/commit/936587d).
- Parts have a `text` object of type [`EmbeddableText`](lib/src/main/groovy/com/jessebrault/ssg/text/EmbeddableText.groovy). If one is rendering a Part called from anything other than a Template (which has an associated text), this will be `null`. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).

### Changed
- **Breaking**: the `text` object in Templates is now an instance of [`EmbeddableText`](lib/src/main/groovy/com/jessebrault/ssg/text/EmbeddableText.groovy) instead of `String`. Thus, one must use `text.render()` to obtain the rendered text. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).

### Deprecated
- The `frontMatter` object in Templates is now deprecated. Use `text.frontMatter` instead. [eafc8cd](https://github.com/JesseBrault0709/ssg/commit/eafc8cd).
