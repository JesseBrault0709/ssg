package com.jessebrault.ssg.text

import com.jessebrault.ssg.provider.Provider

interface TextsProvider extends Provider<Collection<Text>> {
    Collection<TextType> getTextTypes()
}
