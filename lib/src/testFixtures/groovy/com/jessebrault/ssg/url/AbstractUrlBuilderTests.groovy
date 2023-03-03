package com.jessebrault.ssg.url

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

abstract class AbstractUrlBuilderTests {

    protected abstract UrlBuilder getUrlBuilder(String targetPath, String baseUrl);

    @Test
    void upDownDown() {
        def builder = this.getUrlBuilder('posts/post.html', '')
        assertEquals('../images/test.jpg', builder.relative('images/test.jpg'))
    }

    @Test
    void downDown() {
        assertEquals(
                'images/test.jpg',
                this.getUrlBuilder('test.html', '').relative('images/test.jpg')
        )
    }

    @Test
    void upUpDownDown() {
        assertEquals(
                '../../images/test.jpg',
                this.getUrlBuilder('posts/old/test.html', '').relative('images/test.jpg')
        )
    }

    @Test
    void absoluteMatchesTargetPath() {
        assertEquals(
                'https://test.com/test/test.html',
                this.getUrlBuilder('test/test.html', 'https://test.com').absolute
        )
    }

    @Test
    void absoluteToCorrect() {
        assertEquals(
                'https://test.com/images/test.jpg',
                this.getUrlBuilder('', 'https://test.com').absolute('images/test.jpg')
        )
    }

}
