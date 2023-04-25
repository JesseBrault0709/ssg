<html>
    <head>
        <title>${ globals.siteTitle }: Page</title>
    </head>
    <body>
        <%= texts.find { it.path == 'hello.md' }.render() %>
    </body>
</html>