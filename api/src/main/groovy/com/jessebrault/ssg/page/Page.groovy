package com.jessebrault.ssg.page

import com.jessebrault.ssg.view.PageView

interface Page {
    String getName()
    String getPath()
    String getFileExtension()
    Class<? extends PageView> getViewType()
    String getTemplateResource()
}
