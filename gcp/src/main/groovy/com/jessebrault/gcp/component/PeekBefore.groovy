package com.jessebrault.gcp.component


import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.SOURCE)
@interface PeekBefore {
    ComponentToken.Type[] value()
}