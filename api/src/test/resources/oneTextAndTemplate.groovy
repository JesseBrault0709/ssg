import com.jessebrault.ssg.buildscript.Build
import com.jessebrault.ssg.buildscript.BuildScriptBase
import com.jessebrault.ssg.buildscript.OutputDir
import com.jessebrault.ssg.html.TextToHtmlSpecProviders
import com.jessebrault.ssg.html.TextToHtmlTaskFactory
import com.jessebrault.ssg.template.TemplateTypes
import com.jessebrault.ssg.template.TemplatesProviders
import com.jessebrault.ssg.text.TextTypes
import com.jessebrault.ssg.text.TextsProviders
import groovy.transform.BaseScript

@BaseScript
BuildScriptBase b

final class Args {
    File sourceDir
    File tmpDir
    GroovyScriptEngine engine
}

def args = args as Args

build(name: 'test') {
    outputDirFunction = { Build build -> new OutputDir(new File(args.sourceDir, 'build')) }

    types {
        textTypes << TextTypes.MARKDOWN
        templateTypes << TemplateTypes.getGsp(['.gsp'], args.tmpDir, args.engine)
    }

    sources { base, types ->
        texts TextsProviders.from(new File(args.sourceDir, 'texts'), types.textTypes)
        templates TemplatesProviders.from(new File(args.sourceDir, 'templates'), types.templateTypes)
    }

    taskFactories { base, sources ->
        register('textToHtml', TextToHtmlTaskFactory::new) {
            it.specProvider += TextToHtmlSpecProviders.from(sources)
        }
    }
}
