package com.jessebrault.ssg

import com.jessebrault.ssg.part.GspPartRenderer
import com.jessebrault.ssg.part.PartFilePartsProvider
import com.jessebrault.ssg.part.PartType
import com.jessebrault.ssg.template.GspTemplateRenderer
import com.jessebrault.ssg.template.TemplateType
import com.jessebrault.ssg.template.TemplateFileTemplatesProvider
import com.jessebrault.ssg.text.MarkdownFrontMatterGetter
import com.jessebrault.ssg.text.MarkdownTextRenderer
import com.jessebrault.ssg.text.TextType
import com.jessebrault.ssg.text.TextFileTextsProvider

class StaticSiteGeneratorCli {

    static void main(String[] args) {
        def markdownText = new TextType(['.md'], new MarkdownTextRenderer(), new MarkdownFrontMatterGetter())
        def gspTemplate = new TemplateType(['.gsp'], new GspTemplateRenderer())
        def gspPart = new PartType(['.gsp'], new GspPartRenderer())

        def config = new Config(
                textTypes: [markdownText],
                templateTypes: [gspTemplate],
                partTypes: [gspPart],

                textsDir: new File('texts'),
                templatesDir: new File('templates'),
                partsDir: new File('parts'),

                textsProviderGetter: { Config config -> new TextFileTextsProvider(config.textTypes, config.textsDir) },
                templatesProviderGetter: { Config config -> new TemplateFileTemplatesProvider(config.templateTypes, config.templatesDir) },
                partsProviderGetter: { Config config -> new PartFilePartsProvider(config.partTypes, config.partsDir) }
        )

        def ssg = new SimpleStaticSiteGenerator(config)
        ssg.generate(new File('build'))
    }

}
