package com.jessebrault.ssg.part

final class PartTypes {

    static final PartType GSP = new PartType(['.gsp'], new GspPartRenderer())

    private PartTypes() {}

}
