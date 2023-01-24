package com.jessebrault.gcp.util

import com.jessebrault.gcp.component.ComponentToken

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.SOURCE)
@interface PeekBefore {
    ComponentToken.Type[] value()
}