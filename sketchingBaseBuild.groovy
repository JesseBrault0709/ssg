abstractBuild(name: 'redDogAll', extends: 'default') {
    siteSpec {
        name = 'Red Dog Ensemble'
        baseUrl = 'https://reddogensemble.com'
    }

    globals {
        greeting = 'Say hello to good music!'
    }
}

build(name: 'preview', extends: 'redDogAll') {
    siteSpec { base ->
        baseUrl = base.baseUrl + '/preview'
    }

    globals {
        greeting = 'Hello from preview!' // overwrite
    }
}