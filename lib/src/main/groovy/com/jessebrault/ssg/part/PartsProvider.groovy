package com.jessebrault.ssg.part

interface PartsProvider {
    Collection<Part> getParts()
    Collection<PartType> getPartTypes()
}