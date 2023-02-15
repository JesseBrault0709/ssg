# Changelog

All notable changes to SSG will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Next

### Added

- Templates, SpecialPages, and Parts all have access to `targetPath` of type `String` representing the path of the 'output' file. For now, this is always a `.html` file.
    ```gsp
    <%
        // in a template where the source text is 'foo/bar.md'
        assert targetPath == 'foo/bar.html'
        
        // in a special page whose path is 'special.gsp'
        assert targetPath == 'special.html'
        
        // in a part with a source text of 'foo/bar/hello.md'
        assert targetPath == 'foo/bar/hello.html'
        
        // in a part with a source special page of 'foo/bar/baz/special.gsp'
        assert targetParth == 'foo/bar/baz/special.html'
    %>
    ```
    [6de83df](https://github.com/JesseBrault0709/ssg/commit/6de83df), [06499a9](https://github.com/JesseBrault0709/ssg/commit/06499a9).
- Templates, SpecialPages, and Parts all have access to `path` of type `String` representing the path of the 'source' file: either a text file or a special page. In Templates, the 'source' comes from the path of the Text being rendered; in SpecialPages, this comes from the path of the SpecialPage being rendered (i.e., itself); in Parts, this comes from either the Template or SpecialPage which called (i.e., embedded) the Part. 
    ```gsp
    <%
        // in a template or part when rendering a text at 'home.md'
        assert path == 'home.md'
    
        // in a template or part when rendering a text at 'posts/helloWorld.md'
        assert path == 'posts/helloWorld.md'
    
        // in a special page or part when rendering a special page at 'foo/bar/specialPage.gsp'
        assert path == 'foo/bar/specialPage.gsp'
    %>
    ```
  [0371d41](https://github.com/JesseBrault0709/ssg/commit/0371d41), [9983685](https://github.com/JesseBrault0709/ssg/commit/9983685), [076bc9b](https://github.com/JesseBrault0709/ssg/commit/076bc9b).
- Templates, SpecialPages, and Parts all have access to a `urlBuilder` of type [`PathBasedUrlBuilder`](lib/src/main/groovy/com/jessebrault/ssg/url/PathBasedUrlBuilder.groovy) (implementing [`UrlBuilder`](lib/src/main/groovy/com/jessebrault/ssg/url/UrlBuilder.groovy)). Use it like so:
    ```gsp
    <%
        // when targetPath == 'nested/post.html'
        assert urlBuilder.relative('images/test.jpg') == '../images/test.jpg'
    
        // when targetPath == 'simple.html'
        assert urlBuilder.relative('images/test.jpg') == 'images/test.jpg'
    %>
    ```
  *Nota bene:* likely will break on Windows since `PathBasedUrlBuilder` currently uses Java's `Path` api and paths would be thusly rendered using Windows-style backslashes. This will be explored in the future. [0371d41](https://github.com/JesseBrault0709/ssg/commit/0371d41).
- Parts have access to all other parts now via `parts`, an object of type [`EmbeddablePartsMap`](lib/src/main/groovy/com/jessebrault/ssg/part/EmbeddablePartsMap.groovy). For example:

    ```gsp
    <% 
        // myPart.gsp
        out << parts['otherPart.gsp'].render()
    %>
    ```
    
  [0e49414](https://github.com/JesseBrault0709/ssg/commit/0e49414).
- A `tagBuilder` object of type [`DynamicTagBuilder`](lib/src/main/groovy/com/jessebrault/ssg/tagbuilder/DynamicTagBuilder.groovy) (implementing [`TagBuilder`](lib/src/main/groovy/com/jessebrault/ssg/tagbuilder/TagBuilder.groovy)) is available in Templates, SpecialPages, and Parts.

    ```gsp
    <%
        def simpleTag = tagBuilder.test()
        assert simpleTag == '<test />'
  
        def tagWithBody = tagBuilder.title 'Hello, World!'
        assert tagWithBody == '<title>Hello, World!</title>'
    
        def tagWithAttributes = tagBuilder.meta name: 'og:title', content: 'Hello, World!'
        assert tagWithAttributes == '<meta name="og:title" content="Hello, World!" />'
  
        def tagWithAttributesAndBody = tagBuilder.p([id: 'my-paragraph'], 'Hello, World!')
        assert tagWithAttributesAndBody == '<p id="my-paragraph">Hello, World!</p>'
    %>
    ```
    
  This is likely most useful for building simple, one-line html/xml tags. [93687d](https://github.com/JesseBrault0709/ssg/commit/936587d).
- Parts have a `text` object of type [`EmbeddableText`](lib/src/main/groovy/com/jessebrault/ssg/text/EmbeddableText.groovy). If one is rendering a Part called from anything other than a Template (which has an associated text), this will be `null`. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).

### Breaking Changes
- The `path` object of type `String`, where it did previously exist, was stripped of its extension when previously referring to Texts or SpecialPages; now, the extension is present. For example:
    ```gsp
    <%
        // suppose we have a text called 'test.md' and we are in a template, special page, or part
        assert texts['test'] == null
        assert texts['test.md'] != null
    %>
    ```
  [0371d41](https://github.com/JesseBrault0709/ssg/commit/0371d41).
- The `text` object in Templates is now an instance of [`EmbeddableText`](lib/src/main/groovy/com/jessebrault/ssg/text/EmbeddableText.groovy) instead of `String`. Thus, one must use `text.render()` to obtain the rendered text. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).

### Deprecated
- The `frontMatter` object in Templates is now deprecated. Use `text.frontMatter` instead. [eafc8cd](https://github.com/JesseBrault0709/ssg/commit/eafc8cd).
