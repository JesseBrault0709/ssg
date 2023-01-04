package com.jessebrault.ssg.template;

import java.io.File;
import java.util.Collection;

public interface TemplatesFactory {
    Collection<Template> getTemplates(File templatesDir);
}
