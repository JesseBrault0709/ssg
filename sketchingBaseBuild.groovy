abstractBuild(name: 'redDogAll', extends: 'default') {
    siteSpec.merge {
        name = 'Red Dog Ensemble'
        baseUrl = 'https://reddogensemble.com'
    }

    globals.merge {
        greeting = 'Say hello to good music!'
    }

    sources { types ->
        models.map { acc ->
            old + someMethodThatGetsEventModels()
        }
    }

    taskFactories { sources ->
        register(name: 'eventToHtml', supplier: ModelToHtmlFactory::new) {
            modelsProvider.map { acc ->
                acc + CollectionProviders.fromSupplier {
                    sources.models.provide().findAll { it.name.startsWith('event/') }
                }
            }
        }
    }
}

build(name: 'preview', extends: 'redDogAll') {
    siteSpec.merge {
        baseUrl += '/preview' // if possible
    }

    globals.merge {
        greeting = 'Hello from preview!'
    }
}