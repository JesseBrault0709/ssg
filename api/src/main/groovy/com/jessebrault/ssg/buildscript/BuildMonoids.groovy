package com.jessebrault.ssg.buildscript

import com.jessebrault.ssg.SiteSpec
import com.jessebrault.ssg.task.TaskFactory
import com.jessebrault.ssg.task.TaskFactorySpec
import com.jessebrault.ssg.util.Monoid
import com.jessebrault.ssg.util.Monoids

import java.util.function.Function

final class BuildMonoids {

    static final Monoid<Function<Build, OutputDir>> outputDirFunctionMonoid = OutputDirFunctions.DEFAULT_MONOID
    static final Monoid<SiteSpec> siteSpecMonoid = SiteSpec.DEFAULT_MONOID
    static final Monoid<Map<String, Object>> globalsMonoid = Monoids.getMapMonoid()
    static final Monoid<TypesContainer> typesMonoid = TypesContainer.DEFAULT_MONOID
    static final Monoid<SourceProviders> sourcesMonoid = SourceProviders.DEFAULT_MONOID

    static final Monoid<Collection<TaskFactorySpec<TaskFactory>>> taskFactoriesMonoid =
            Monoids.getMergeCollectionMonoid(
                    TaskFactorySpec.SAME_NAME_AND_SUPPLIER_EQ,
                    TaskFactorySpec.DEFAULT_SEMIGROUP
            )

    static final Monoid<Collection<String>> includedBuildsMonoid = Monoids.getCollectionMonoid()

    static Monoid<BuildIntermediate> buildIntermediateMonoid = Monoids.of(SyntheticBuildIntermediate.getEmpty())
            { bi0, bi1 ->
                new SyntheticBuildIntermediate(
                        BuildSpec.get(
                                name: "SyntheticBuildSpec(${ bi0.buildSpec.name }, ${ bi1.buildSpec.name })",
                                isAbstract: true,
                                extending: [],
                                buildClosure: { }
                        ),
                        outputDirFunctionMonoid.concat.apply(bi0.outputDirFunction, bi1.outputDirFunction),
                        siteSpecMonoid.concat.apply(bi0.siteSpec, bi1.siteSpec),
                        globalsMonoid.concat.apply(bi0.globals, bi1.globals),
                        typesMonoid.concat.apply(bi0.types, bi1.types),
                        sourcesMonoid.concat.apply(bi0.sources, bi1.sources),
                        taskFactoriesMonoid.concat.apply(bi0.taskFactorySpecs, bi1.taskFactorySpecs),
                        includedBuildsMonoid.concat.apply(bi0.includedBuilds, bi1.includedBuilds)
                )
            }

    private BuildMonoids() {}

}
