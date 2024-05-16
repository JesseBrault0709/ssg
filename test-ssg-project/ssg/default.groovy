import com.jessebrault.ssg.buildscript.BuildScriptBase
import groovy.transform.BaseScript

@BaseScript
BuildScriptBase base

build {
    siteName 'My Test Site'
    baseUrl 'https://hello.com'
}
