// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku;

import com.hdr.sanmoku.dic.Matrix;
import com.hdr.sanmoku.dic.Unknown;
import com.hdr.sanmoku.dic.WordDic;
import com.hdr.sanmoku.dic.PartsOfSpeech;
import java.util.List;
import com.hdr.sanmoku.dic.ViterbiNode;
import java.util.ArrayList;

public final class Tagger
{
    private static final ArrayList<ViterbiNode> BOS_NODES;
    
    public static List<Morpheme> parse(final String s) {
        return parse(s, new ArrayList<Morpheme>(s.length() / 2));
    }
    
    public static List<Morpheme> parse(final String s, final List<Morpheme> list) {
        for (ViterbiNode viterbiNode = parseImpl(s); viterbiNode != null; viterbiNode = viterbiNode.prev) {
            list.add(new Morpheme(s.substring(viterbiNode.start, viterbiNode.start + viterbiNode.length()), PartsOfSpeech.get(viterbiNode.posId()), viterbiNode.start, viterbiNode.morphemeId));
        }
        return list;
    }
    
    public static List<String> wakati(final String s) {
        return wakati(s, new ArrayList<String>(s.length() / 1));
    }
    
    public static List<String> wakati(final String s, final List<String> list) {
        for (ViterbiNode viterbiNode = parseImpl(s); viterbiNode != null; viterbiNode = viterbiNode.prev) {
            list.add(s.substring(viterbiNode.start, viterbiNode.start + viterbiNode.length()));
        }
        return list;
    }
    
    public static ViterbiNode parseImpl(final String s) {
        final int length = s.length();
        final ArrayList list = new ArrayList<ArrayList<ViterbiNode>>(length + 1);
        list.add(Tagger.BOS_NODES);
        for (int i = 1; i <= length; ++i) {
            list.add(new ArrayList<ViterbiNode>());
        }
        final MakeLattice makeLattice = new MakeLattice(list);
        for (int j = 0; j < length; ++j) {
            if (!((ArrayList<ViterbiNode>)list.get(j)).isEmpty()) {
                makeLattice.set(j);
                WordDic.search(s, j, makeLattice);
                Unknown.search(s, j, makeLattice);
                if (j > 0) {
                    ((ArrayList<ViterbiNode>)list.get(j)).clear();
                }
            }
        }
        ViterbiNode prev = setMincostNode(ViterbiNode.makeBOSEOS(), (ArrayList<ViterbiNode>) list.get(length)).prev;
        ViterbiNode prev2 = null;
        while (prev.prev != null) {
            final ViterbiNode prev3 = prev.prev;
            prev.prev = prev2;
            prev2 = prev;
            prev = prev3;
        }
        return prev2;
    }
    
    private static ViterbiNode setMincostNode(final ViterbiNode viterbiNode, final ArrayList<ViterbiNode> list) {
        final ViterbiNode prev = list.get(0);
        viterbiNode.prev = prev;
        final ViterbiNode viterbiNode2 = prev;
        int n = viterbiNode2.cost + Matrix.linkCost(viterbiNode2.posId(), viterbiNode.posId());
        for (int i = 1; i < list.size(); ++i) {
            final ViterbiNode prev2 = list.get(i);
            final int n2 = prev2.cost + Matrix.linkCost(prev2.posId(), viterbiNode.posId());
            if (n2 < n) {
                n = n2;
                viterbiNode.prev = prev2;
            }
        }
        viterbiNode.cost += n;
        return viterbiNode;
    }
    
    static {
        (BOS_NODES = new ArrayList<ViterbiNode>(1)).add(ViterbiNode.makeBOSEOS());
    }
    
    private static final class MakeLattice implements WordDic.Callback
    {
        private final ArrayList<ArrayList<ViterbiNode>> nodesAry;
        private int i;
        private ArrayList<ViterbiNode> prevs;
        private boolean empty;
        
        public MakeLattice(final ArrayList<ArrayList<ViterbiNode>> nodesAry) {
            this.empty = true;
            this.nodesAry = nodesAry;
        }
        
        public void set(final int n) {
            this.i = n;
            this.prevs = this.nodesAry.get(n);
            this.empty = true;
        }
        
        @Override
        public void call(final ViterbiNode viterbiNode) {
            this.empty = false;
            if (viterbiNode.isSpace()) {
                this.nodesAry.get(this.i + viterbiNode.length()).addAll(this.prevs);
            }
            else {
                this.nodesAry.get(this.i + viterbiNode.length()).add(setMincostNode(viterbiNode, this.prevs));
            }
        }
        
        @Override
        public boolean isEmpty() {
            return this.empty;
        }
    }
}
