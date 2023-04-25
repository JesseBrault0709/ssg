<html>
    <%
        out << parts['head.gsp'].render([
            title: "${ globals.siteTitle }: ${ text.frontMatter.title }"
        ])
    %>
    <body>
        <%= text.render() %>
    </body>
</html>