// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

public class History<T>
{
    private Object[] history;
    private String name;
    
    public History() {
        this("unknown");
    }
    
    public History(final String name) {
        this.name = name;
        this.history = new Object[MagicNumbers.max_history];
    }
    
    public void add(final T item) {
        for (int i = MagicNumbers.max_history - 1; i > 0; --i) {
            this.history[i] = this.history[i - 1];
        }
        this.history[0] = item;
    }
    
    public T get(final int index) {
        if (index >= MagicNumbers.max_history) {
            return null;
        }
        if (this.history[index] == null) {
            return null;
        }
        return (T)this.history[index];
    }
    
    public String getString(final int index) {
        if (index >= MagicNumbers.max_history) {
            return null;
        }
        if (this.history[index] == null) {
            return MagicStrings.unknown_history_item;
        }
        return (String)this.history[index];
    }
    
    public void printHistory() {
        for (int i = 0; this.get(i) != null; ++i) {
            System.out.println(this.name + "History " + (i + 1) + " = " + this.get(i));
            System.out.println(String.valueOf(this.get(i).getClass()).contains("History"));
            if (String.valueOf(this.get(i).getClass()).contains("History")) {
                ((History) this.get(i)).printHistory();
            }
        }
    }
}
