package com.jessebrault.gcp.tokenizer;

interface FsmOutput {
    String entire();
    String part(int index);
}
