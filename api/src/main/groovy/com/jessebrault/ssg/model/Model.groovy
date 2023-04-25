package com.jessebrault.ssg.model

interface Model<T> {
    String getName()
    T get()
}
