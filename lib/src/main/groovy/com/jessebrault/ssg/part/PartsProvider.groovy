package com.jessebrault.ssg.part

import com.jessebrault.ssg.provider.Provider

interface PartsProvider extends Provider<Collection<Part>> {
    Collection<PartType> getPartTypes()
}