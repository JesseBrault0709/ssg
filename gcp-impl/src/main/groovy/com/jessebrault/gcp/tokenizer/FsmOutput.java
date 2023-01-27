package com.jessebrault.gcp.tokenizer;

interface FsmOutput {
    CharSequence entire();
    CharSequence part(int index);
}
