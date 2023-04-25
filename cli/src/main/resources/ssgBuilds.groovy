// This file was auto-generated by the ssg init command.

import groovy.transform.BaseScript
import com.jessebrault.ssg.buildscript.BuildScriptBase

@BaseScript
final BuildScriptBase base = null

allBuilds {
    siteSpec {
        name = 'My Site'
    }

    globals {
        greeting = 'Hello from AllBuilds!'
    }
}

build('production') {
    siteSpec {
        baseUrl = 'https://example.com'
    }
}

build('preview') {
    siteSpec {
        baseUrl = 'https://example.com/preview'
    }
}
