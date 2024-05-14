import com.jessebrault.ssg.buildscript.BuildScriptBase
import groovy.transform.BaseScript

@BaseScript
BuildScriptBase base

build {
    siteName 'My Site'
    baseUrl 'https://mysite.com'
}
