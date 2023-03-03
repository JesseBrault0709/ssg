# Changelog

All notable changes to SSG will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 0.1.0

### Added

- Templates, SpecialPages, and Parts all have access to two related objects: `tasks` and `taskTypes`. The first is an instance of [`TaskContainer`](lib/src/main/groovy/com/jessebrault/ssg/task/TaskContainer.groovy) and can be used to access all of the [`Task`](lib/src/main/groovy/com/jessebrault/ssg/task/Task.groovy) instances for a given build. The second is in an instance of [`TaskTypeContainer`](lib/src/main/groovy/com/jessebrault/ssg/task/TaskTypeContainer.groovy) and can be used to access the various [`TaskType`](lib/src/main/groovy/com/jessebrault/ssg/task/TaskType.groovy) instances for a given build. For example, one could use these together to obtain the output path of an html file from another task (assume one is in a `.gsp` file):
    ```groovy
    def otherTask = tasks.findAllByType(taskTypes.textToHtmlFile).find { it.input.path == 'someText.md' }
    assert otherTask.output.htmlPath == 'someText.html'
    ```
  This is a complicated and experimental feature and may be changed frequently depending on future developments. [92c8108](https://github.com/JesseBrault0709/ssg/commit/92c8108).
- Templates, SpecialPages, and Parts all have access to a `logger` of type `org.slf4j.Logger`. [64f342a](https://github.com/JesseBrault0709/ssg/commit/64f342a).
- There is now the notion of a `siteSpec`, an object of type of [`SiteSpec`](lib/src/main/groovy/com/jessebrault/ssg/SiteSpec.groovy). It is simmilar to the `globals` object in that it contains properties that are available in all Templates, SpecialPages, and Parts. Unlike `globals`, which contains user-defined keys, `siteSpec` contains (for now) the pre-defined keys `baseUrl` and `title`. To configure the `siteSpec`, add the following block to a `build` block in `ssgBuilds.groovy`:
    ```groovy
    siteSpec {
        baseUrl = 'https://test.com' // or whatever, defaults to an empty string
        title = 'My Great Website' // or whatever, defaults to an empty string
    }
    ```
  Then use it in any Template, SpecialPage, or part like so:
  ```gsp
  <%
      assert siteSpec.baseUrl == 'https://test.com' && siteSpec.title == 'My Great Website'
  %>
  ```
  [111bdea](https://github.com/JesseBrault0709/ssg/commit/111bdea), [ef9e566](https://github.com/JesseBrault0709/ssg/commit/ef9e566).
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
- Templates, SpecialPages, and Parts all have access to `sourcePath` of type `String` representing the path of the 'source' file: either a text file or a special page. In Templates, the 'source' comes from the path of the Text being rendered; in SpecialPages, this comes from the path of the SpecialPage being rendered (i.e., itself); in Parts, this comes from either the Template or SpecialPage which called (i.e., embedded) the Part. 
    ```gsp
    <%
        // in a template or part when rendering a text at 'home.md'
        assert sourcePath == 'home.md'
    
        // in a template or part when rendering a text at 'posts/helloWorld.md'
        assert sourcePath == 'posts/helloWorld.md'
    
        // in a special page or part when rendering a special page at 'foo/bar/specialPage.gsp'
        assert sourcePath == 'foo/bar/specialPage.gsp'
    %>
    ```
  [0371d41](https://github.com/JesseBrault0709/ssg/commit/0371d41), [9983685](https://github.com/JesseBrault0709/ssg/commit/9983685), [076bc9b](https://github.com/JesseBrault0709/ssg/commit/076bc9b), [c5ac810](https://github.com/JesseBrault0709/ssg/commit/c5ac810).
- Templates, SpecialPages, and Parts all have access to a `urlBuilder` of type [`PathBasedUrlBuilder`](lib/src/main/groovy/com/jessebrault/ssg/url/PathBasedUrlBuilder.groovy) (implementing [`UrlBuilder`](lib/src/main/groovy/com/jessebrault/ssg/url/UrlBuilder.groovy)). It can be used to obtain both absolute (using `siteSpec.baseUrl`) and relative urls (to the current `targetPath`). Use it like so:
    ```gsp
    <%
        // when targetPath == 'simple.html'
        assert urlBuilder.relative('images/test.jpg') == 'images/test.jpg'
        
        // when targetPath == 'nested/post.html'
        assert urlBuilder.relative('images/test.jpg') == '../images/test.jpg'
        
        // when baseUrl is 'https://test.com' and targetPath is 'simple.html'
        assert urlBuilder.absolute == 'https://test.com/simple.html
        
        // when baseUrl is 'https://test.com' and we want an absolute to another file
        assert urlBuilder.absolute('images/test.jpg') == 'https://test.com/images/test.jpg'
    %>
    ```
  *Nota bene:* likely will break on Windows since `PathBasedUrlBuilder` currently uses Java's `Path` api and paths would be thusly rendered using Windows-style backslashes. This will be explored in the future. [0371d41](https://github.com/JesseBrault0709/ssg/commit/0371d41), [0762dc6](https://github.com/JesseBrault0709/ssg/commit/0762dc6), [60f4c14](https://github.com/JesseBrault0709/ssg/commit/60f4c14).
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
- The path of a file was stripped of its extension when previously referring to Texts or SpecialPages; now, the extension is present. For example:
    ```gsp
    <%
        // suppose we have a text called 'test.md' and we are in a template, special page, or part
        assert texts['test'] == null
        assert texts['test.md'] != null
    %>
    ```
  [0371d41](https://github.com/JesseBrault0709/ssg/commit/0371d41).
- The `text` object in Templates is now an instance of [`EmbeddableText`](lib/src/main/groovy/com/jessebrault/ssg/text/EmbeddableText.groovy) instead of `String`. Thus, one must use `text.render()` to obtain the rendered text. [34d9cd5](https://github.com/JesseBrault0709/ssg/commit/34d9cd5).
- The `frontMatter` object is no longer available. Use `text.frontMatter` instead. [eafc8cd](https://github.com/JesseBrault0709/ssg/commit/eafc8cd), [c5ac810](https://github.com/JesseBrault0709/ssg/commit/c5ac810).
