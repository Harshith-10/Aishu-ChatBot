// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

public final class CodeStream
{
    private final CharSequence src;
    private final int end;
    private int pos;
    private int code;
    private int octetPos;
    private int octetLen;
    
    public CodeStream(final CharSequence src, final int pos) {
        this.src = src;
        this.end = this.src.length();
        this.pos = pos;
        this.code = ((pos == this.end) ? '\0' : src.charAt(pos));
        this.octetLen = this.octetLength(this.code);
        this.octetPos = this.octetLen;
    }
    
    public boolean isEos() {
        return this.pos == this.end;
    }
    
    public char read() {
        final char peek = this.peek();
        this.eat();
        return peek;
    }
    
    public int position() {
        return this.pos;
    }
    
    private int octetLength(final int n) {
        if (n < 128) {
            return 1;
        }
        if (n < 2048) {
            return 2;
        }
        if (n < 65536) {
            return 3;
        }
        return 4;
    }
    
    private char peek() {
        if (this.octetPos != this.octetLen) {
            return (char)(128 + (byte)(this.code >> (this.octetPos - 1) * 6 & 0x3F));
        }
        switch (this.octetLen) {
            case 1: {
                return (char)this.code;
            }
            case 2: {
                return (char)(192 + (byte)(this.code >> 6 & 0x1F));
            }
            case 3: {
                return (char)(224 + (byte)(this.code >> 12 & 0xF));
            }
            default: {
                return (char)(240 + (byte)(this.code >> 18 & 0x7));
            }
        }
    }
    
    private void eat() {
        --this.octetPos;
        if (this.octetPos == 0) {
            ++this.pos;
            if (!this.isEos()) {
                this.code = this.src.charAt(this.pos);
                this.octetLen = this.octetLength(this.code);
                this.octetPos = this.octetLen;
            }
        }
    }
}
