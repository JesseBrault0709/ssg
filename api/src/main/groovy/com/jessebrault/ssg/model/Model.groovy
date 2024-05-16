package com.jessebrault.ssg.model

interface Model<T> {
    String getName()
    Class<T> getType()
    T get()
}
