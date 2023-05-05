package com.jessebrault.ssg.buildscript.domain

import com.jessebrault.ssg.property.Properties
import com.jessebrault.ssg.property.Property
import com.jessebrault.ssg.util.Monoid

final class MutableSiteSpec {

    private static final Monoid<String> nameAndBaseUrlMonoid = new Monoid<>(
            { s0, s1 -> s1 },
            { '' }
    )

    static final Monoid<MutableSiteSpec> MONOID = new Monoid<>(
            MutableSiteSpec::concat,
            MutableSiteSpec::new
    )

    private static MutableSiteSpec concat(MutableSiteSpec ms0, MutableSiteSpec ms1) {
        new MutableSiteSpec().tap {
            name.set(ms1.name.get())
            baseUrl.set(ms1.baseUrl.get())
        }
    }

    final Property<String> name = Properties.get(nameAndBaseUrlMonoid)
    final Property<String> baseUrl = Properties.get(nameAndBaseUrlMonoid)

}
