package com.jessebrault.ssg.text

interface TextRenderer {

    /**
     * Renders the text from its raw form to html.
     *
     * @param text in raw form
     * @return the rendered text in html
     */
    String render(String text, Map globals)

}
