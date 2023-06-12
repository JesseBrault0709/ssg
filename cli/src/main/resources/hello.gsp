<html>
    <%
        println "delegate.text: $delegate.text"
        out << parts['head.gsp'].render([
            title: "${ siteSpec.name }: ${ text.frontMatter.title }"
        ])
    %>
    <body>
        <%= text.render() %>
    </body>
</html>