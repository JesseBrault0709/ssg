<html>
    <head>
        <title>${ siteSpec.name }: Page</title>
    </head>
    <body>
        <%= texts.find { it.path == 'hello.md' }.render() %>
    </body>
</html>