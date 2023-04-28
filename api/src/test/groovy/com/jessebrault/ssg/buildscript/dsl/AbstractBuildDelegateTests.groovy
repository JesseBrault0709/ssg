package com.jessebrault.ssg.buildscript.dsl

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.buildscript.SourceProviders
import com.jessebrault.ssg.buildscript.TypesContainer
import com.jessebrault.ssg.page.Page
import com.jessebrault.ssg.page.PageTypes
import com.jessebrault.ssg.provider.CollectionProvider
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.text.Text
import com.jessebrault.ssg.text.TextTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import java.util.function.Supplier

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension)
abstract class AbstractBuildDelegateTests<T> {

    protected abstract AbstractBuildDelegate<T> getDelegate()

    @Test
    void siteSpecsAdded() {
        def d = this.getDelegate()
        d.siteSpec {
            name = 'test'
        }
        d.siteSpec {
            baseUrl = 'test'
        }
        def sum = d.getSiteSpecResult()
        assertEquals(new SiteSpec('test', 'test'), sum)
    }

    @Test
    void globalsAdded() {
        def d = this.getDelegate()
        d.globals {
            a = 0
        }
        d.globals {
            b = 1
        }
        def sum = d.getGlobalsResult()
        assertEquals([a: 0, b: 1], sum)
    }

    @Test
    void typesAdded() {
        def d = this.getDelegate()
        d.types {
            textTypes << TextTypes.MARKDOWN
        }
        d.types {
            pageTypes << PageTypes.GSP
        }
        def sum = d.getTypesResult()
        assertTrue(TextTypes.MARKDOWN in sum.textTypes)
        assertTrue(PageTypes.GSP in sum.pageTypes)
    }

    @Test
    void sourcesAdded(@Mock CollectionProvider<Text> textsProvider, @Mock CollectionProvider<Page> pagesProvider) {
        def d = this.getDelegate()
        d.providers {
            texts(textsProvider)
        }
        d.providers {
            pages(pagesProvider)
        }
        def sum = d.getSourcesResult(TypesContainer.getEmpty())
        assertTrue(textsProvider in sum.textsProvider)
        assertTrue(pagesProvider in sum.pagesProvider)
    }

    @Test
    void taskFactoriesAdded(@Mock Supplier<TaskFactory> taskFactorySupplier) {
        def d = this.getDelegate()
        d.taskFactories {
            register('tf0', taskFactorySupplier)
        }
        d.taskFactories {
            register('tf1', taskFactorySupplier)
        }
        def sum = d.getTaskFactoriesResult(SourceProviders.getEmpty())
        assertEquals(2, sum.size())
        assertTrue(sum.inject(true) { acc, spec ->
            acc && spec.supplier == taskFactorySupplier
        })
    }

}
