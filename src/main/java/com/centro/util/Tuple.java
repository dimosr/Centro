package com.centro.util;

public class Tuple<X, Y> {
    
    public final X first; 
    public final Y second;
    
    public Tuple(X x, Y y) { 
        this.first = x; 
        this.second = y; 
    } 
} 