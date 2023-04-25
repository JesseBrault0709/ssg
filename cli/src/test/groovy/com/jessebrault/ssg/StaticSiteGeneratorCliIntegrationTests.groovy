package com.jessebrault.ssg

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

final class StaticSiteGeneratorCliIntegrationTests {

    @Test
    @Disabled('until we figure out how to do the base dir arg')
    void defaultConfiguration() {
        def partsDir = new File('parts').tap {
            mkdir()
            deleteOnExit()
        }
        def specialPagesDir = new File('specialPages').tap {
            mkdir()
            deleteOnExit()
        }
        def templatesDir = new File('templatesDir').tap {
            mkdir()
            deleteOnExit()
        }
        def textsDir = new File('textsDir').tap {
            mkdir()
            deleteOnExit()
        }

        new File(partsDir, 'part.gsp').write('<%= binding.test %>')
        new File(specialPagesDir, 'page.gsp').write('<%= parts.part.render([test: "Greetings!"]) %>')
        new File(templatesDir, 'template.gsp').write('<%= text %>')
        new File(textsDir, 'text.md').write('---\ntemplate: template.gsp\n---\n**Hello, World!**')

        StaticSiteGeneratorCli.main('--trace')

        def buildDir = new File('build').tap {
            deleteOnExit()
        }
        assertTrue(buildDir.exists())

        def textHtml = new File(buildDir, 'text.html')
        assertTrue(textHtml.exists())
        assertEquals('<p><strong>Hello, World!</strong></p>\n', textHtml.text)

        def specialPage = new File(buildDir, 'specialPage.html')
        assertTrue(specialPage.exists())
        assertEquals('Greetings!', specialPage.text)
    }

}
