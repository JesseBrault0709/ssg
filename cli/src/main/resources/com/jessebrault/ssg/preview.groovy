import com.jessebrault.ssg.buildscript.BuildScriptBase
import groovy.transform.BaseScript

@BaseScript
BuildScriptBase base

build {
    extending 'default'
    baseUrl baseUrl.map { defaultBaseUrl -> defaultBaseUrl + '/preview' }
}
