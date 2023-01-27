package com.jessebrault.gcp.tokenizer;

import java.util.function.Function;

interface FsmFunction extends Function<CharSequence, FsmOutput> {}
