package com.jessebrault.ssg.textfile;

import java.io.File;
import java.util.Collection;

public interface TextFilesFactory {
    Collection<TextFile> getTextFiles(File textsDir);
}
