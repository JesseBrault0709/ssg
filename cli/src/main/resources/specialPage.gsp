<html>
    <head>
        <title>${ globals.siteTitle }: Special Page</title>
    </head>
    <body>
        <%= texts.find { it.path == 'hello' }.render() %>
    </body>
</html>