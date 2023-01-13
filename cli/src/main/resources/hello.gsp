<html>
    <%
        out << parts['head.gsp'].render([
            title: "${ globals.siteTitle }: ${ frontMatter.title }"
        ])
    %>
    <body>
        <%= text %>
    </body>
</html>