abstractBuild(name: 'redDogAll', extends: 'default') {
    // siteSpec(Closure) is short for siteSpec.merge(Closure)
    siteSpec {
        name = 'Red Dog Ensemble'
        baseUrl = 'https://reddogensemble.com'
    }

    globals {
        greeting = 'Say hello to good music!'
    }

    sources { types ->
        models.map { acc ->
            acc + someMethodThatGetsEventModels()
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
    siteSpec { base ->
        baseUrl = base.baseUrl + '/preview' // if possible
    }

    globals {
        greeting = 'Hello from preview!'
    }
}