package com.jessebrault.ssg.renderer;

import com.jessebrault.ssg.template.Template;

public interface Renderer {
    String render(Template template, String text);
}
