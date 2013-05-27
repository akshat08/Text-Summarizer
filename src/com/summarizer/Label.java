/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.summarizer;

/**
 *
 * @author aki
 */

import com.sharethis.textrank.*;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Set;

public class Label 
{
    public double label_rank;
    public String label_key;
    public String label_name;
    public HashSet<Sentence> points = new HashSet<Sentence>();

    public Label(  String text_key, String text_name, double r  ) {
        this.label_key = text_key;
        this.label_name = text_name;
        this.label_rank = r;
    }
    
    public void addSentence( Sentence s ) throws Exception {
        this.points.add(s);
    }
}
