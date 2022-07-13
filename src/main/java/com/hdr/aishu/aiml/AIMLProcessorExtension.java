// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import org.w3c.dom.Node;
import java.util.Set;

public interface AIMLProcessorExtension {
    Set<String> extensionTagSet();
    
    String recursEval(final Node p0, final ParseState p1);
}
