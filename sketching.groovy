import EventSpecProvider

build('production') {
    outDir = new File('build')

    siteSpec {
        baseUrl = 'https://reddogensemble.com'
    }
}

build('preview') {
    outDir = new File('previewBuild')

    siteSpec {
        baseUrl = 'https://reddogensemble.com/preview'
    }
}

allBuilds {
    siteSpec {
        name = 'Red Dog Ensemble'
    }

    globals {
        siteAuthor = 'Red Dog Ensemble'
    }

    taskFactories {
        textToHtml {
            providers << new FileBasedTextProvider(new File('otherTexts'))
        }

        modelToHtml {
            specProviders << new EventSpecProvider()
        }
    }
}