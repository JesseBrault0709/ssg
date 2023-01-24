package com.jessebrault.gcp.tokenizer;

enum TokenizerState {
    NORMAL,
    COMPONENT,
    G_STRING,
    SCRIPTLET
}
