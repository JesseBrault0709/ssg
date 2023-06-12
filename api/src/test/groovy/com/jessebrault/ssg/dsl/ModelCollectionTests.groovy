package com.jessebrault.ssg.dsl

import com.jessebrault.ssg.model.Models
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

final class ModelCollectionTests {

    interface Message {}

    static final class TextMessage implements Message {}

    static final class MailMessage implements Message {}

    @Test
    void findAllByType() {
        def textModel = Models.of('text', new TextMessage())
        def mailModel = Models.of('mail', new MailMessage())
        def models = new ModelCollection([textModel, mailModel])
        assertEquals(2, models.findAllByType(Message).size())
        assertEquals(1, models.findAllByType(TextMessage).size())
        assertEquals(1, models.findAllByType(MailMessage).size())
    }

}
