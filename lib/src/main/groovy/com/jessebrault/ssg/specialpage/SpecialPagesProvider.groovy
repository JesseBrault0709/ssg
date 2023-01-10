package com.jessebrault.ssg.specialpage

import com.jessebrault.ssg.provider.Provider

interface SpecialPagesProvider extends Provider<Collection<SpecialPage>> {
    Collection<SpecialPageType> getSpecialPageTypes()
}