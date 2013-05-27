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


import com.sharethis.common.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.PropertyConfigurator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Summarize {
    
    private final static Log LOG =
        LogFactory.getLog(TextRank.class.getName());
    
    private static TextRank tr, gtr = null;
    
    private final String res_path, lang_code, log4j_conf;
    
    private String file;
    
    private boolean use_wordnet;

    private ArrayList<Label> labels = new ArrayList<Label>();
    
    /////// DATABASE VARIABLES /////////////////////
    
    private Connection connect = null;
    
    
    ////// CONSTRUCTOR FUNCTION ///////////////////
    
    public Summarize( ) throws Exception {

        use_wordnet = false;
        res_path = "C://Users//Administrator//Documents//NetBeansProjects//TextRank//src//res";
        
        //Lang_code wiil be english as summarization is in english
        lang_code = "en";
        
        log4j_conf = "C://Users//Administrator//Documents//NetBeansProjects//TextRank//src//res//log4j.properties";
        PropertyConfigurator.configure(log4j_conf);
        
        Class.forName("com.mysql.jdbc.Driver");
        
        connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/nlsummarize?"
              + "user=root&password=admin");
        
        if ( connect.isClosed() ) System.out.println("CONNECTION CLOSED");
        
    }

    
    ////////////////////////////////////////////////////////////////////
    ////// Call Text Rank Algorithm ////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    
    public TextRank callTextRank( TextRank t ) throws Exception {
        
        t = new TextRank( res_path, lang_code);
        t.prepCall(file, use_wordnet);         
        
	// wrap the call in a timed task
	final FutureTask<Collection<MetricVector>> task = new FutureTask<Collection<MetricVector>>(t);
	Collection<MetricVector> answer = null;

	final Thread thread = new Thread(task);
	thread.run();

	try {
	    //answer = task.get();  // run until complete
	    answer = task.get(15000L, TimeUnit.MILLISECONDS); // timeout in N ms
	}
	catch (ExecutionException e) {
	    LOG.error("exec exception", e);
	}
	catch (InterruptedException e) {
	    LOG.error("interrupt", e);
	}
	catch (TimeoutException e) {
	    LOG.error("timeout", e);

	    return t;
	}
        
        return t;
    }
    
    
    
    
    ////////////////////////////////////////////////////////////////////
    /////////// Combine Local and Global Graph ////////////////////////
    ///////////////////////////////////////////////////////////////////
    
    
    public TextRank combineTextRankGraphs( TextRank one, TextRank two ) throws Exception {
        
        if ( one == null ) return two;
        else {
           
            for ( Node n : two.graph.values() ) {
                Node tmp = one.graph.get(n.key);
                // IF NODE NOT FOUND THEN ADD NODE FROM OTHER GRAPH
                if ( tmp == null ) {
                    
                    one.graph.put( n.key, n );
                    one.graph.get(n.key).rank = 1.0D;
                    
                }
                else {
                    // IF FOUND THEN ADD ALL THE NEIGHBOURS AND UPDATE THE EXISTING RANK
                    for ( Node keyword_node : n.edges ) {
                        
                        if ( one.graph.get( keyword_node.key ) == null ) one.graph.get( n.key ).connect(keyword_node);
                        else { 
                            
                            Node toFind = one.graph.get(keyword_node.key);
                            if ( !tmp.edges.contains(toFind) ) one.graph.get( n.key ).connect(toFind);
                            
                        }
                        
                    }
                    
                    one.graph.get(n.key).rank = 1.0D;
                    
                }
            }
            
            int max =
	    (int) Math.round((double) one.graph.size() * Graph.KEYWORD_REDUCTION_FACTOR);
            
            one.graph.runTextRank();
            sortGraph(one);
        }
        
        return one;
    }
    
    ////////////////////////////////////////////////////////////
    ////////// Update the Local Graph Nodes according //////////
    ////////////////// To Global Graph /////////////////////////
    ///////////////////////////////////////////////////////////
    
    public void updateLocalRanks( TextRank t ) throws Exception {
        for ( Node n : t.graph.values() ) {
            Double new_rank = gtr.graph.get(n.key).rank;
            n.rank = new_rank;
        }
    }
    
    ////////////////////////////////////////////////////////////////
    ////////// sort the graph //////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    
    public void sortGraph ( TextRank t ) throws Exception {
        int max =
	    (int) Math.round((double) t.graph.size() * Graph.KEYWORD_REDUCTION_FACTOR);
        t.graph.sortResults(max);
    }
    
    ////////////////////////////////////////////////////////////////
    ////////// creating NGRAMS from graph //////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    
    public void createNGrams ( TextRank t ) throws Exception {
        t.ngram_subgraph = NGram.collectNGrams( t.lang, t.s_list, t.graph.getRankThreshold() );
    }
    
    ////////////////////////////////////////////////////////////////
    ////////// Augment Graph with NGrams////////////////////////////
    ////////////////////////////////////////////////////////////////    
    
    public void augmentWithNGrams( TextRank t ) throws Exception {
        for (Node n : t.ngram_subgraph.values()) {
	    final NGram gram = (NGram) n.value;

	    if (gram.length < /*MAX_NGRAM_LENGTH */ 5 ) {
		t.graph.put(n.key, n);

		for (Node keyword_node : gram.nodes) {
		    n.connect(keyword_node);
		}
	    }
	}
    }
    
    ////////////////////////////////////////////////////////////////
    ////////// Perform Matriculation on the graph //////////////////
    ////////////////////////////////////////////////////////////////
    
    public void processNGramsMetric( TextRank t ) throws Exception {
        t.constructMetricSpace();
    }
    
    
    ////////////////////////////////////////////////////////////////
    ////////// FIND LABELS AND CLUSTERING OF TEXT //////////////////
    ////////////////////////////////////////////////////////////////
    
    public Label findLabel( String key ) throws Exception {
        
        Label apt = null;
        
        for ( Label t : labels ) {
            if ( t.label_key.equals(key) ) apt = t;
        }
        
        return apt;
        
    }
 
    public void clusterText( TextRank t, String link ) throws Exception { 
        
        final TreeSet<MetricVector> key_phrase_list = new TreeSet<MetricVector>(t.metric_space.values());
        
        Label obj;
        
        PreparedStatement ps;
        PreparedStatement preparedStatement;
        ///Creating labels
        
        preparedStatement = connect.prepareStatement("insert into nlsummarize.label(name) values(?)");
        ps = connect.prepareStatement("insert into nlsummarize.label_link(name,link) values(?,?)");
        
        for (MetricVector mv : key_phrase_list) {
            
	    if (mv.metric >= /*MIN_NORMALIZED_RANK */ 0.05D ) {
                
                /// Adding Labels
                obj = new Label(mv.ngram_key, mv.value.text, mv.ngram_rank);
                labels.add(obj);
                ////////////// SQL PROCESS ///////////////////////
                preparedStatement.setString(1,mv.value.text);
                preparedStatement.addBatch();
                if ( !link.isEmpty() ) {
                    ps.setString(1,mv.value.text);
                    ps.setString(2,link);
                    ps.addBatch();
                }
                /////////////////////////////////////////////////////
	    }
            ////////////// SQL PROCESS ///////////////////////
            preparedStatement.executeBatch();
            if ( !link.isEmpty() ) ps.executeBatch();
            //////////////////////////////////////////////////
            obj = null;
            
	}
         
        /// Adding Sentences according to labels
        
        ////////////// SQL PROCESS ///////////////////////
        
        preparedStatement = connect.prepareStatement("insert into nlsummarize.label_summary(name,details) values(?,?)");
        ps = connect.prepareStatement("insert into nlsummarize.links_summary(link,details) values(?,?)");
        
        ///////////////////////////////////////////////////
        
        
        // taking each sentence
        
        for ( Sentence s : t.s_list ) {
            
            double max_rank = 0.0D;
            Label apt = null;
            
            // processing for every ngram in sentence
            
            for ( String label_key: s.ngram_key ) {
                
                Label l = findLabel( label_key );
                if ( l != null ) {
                    if ( l.label_rank > max_rank ) {
                        max_rank = l.label_rank;
                        apt = l;
                    }       
                }
                
            }
            
            if ( apt != null ) { 
                apt.points.add(s);
                s.s_rank = max_rank;
                
                ////////////// SQL PROCESS ///////////////////////
                preparedStatement.setString(1, apt.label_name);
                preparedStatement.setString(2, s.text);
                if ( !link.isEmpty() ) {
                    ps.setString(1,link);
                    ps.setString(2,s.text);
                }
                preparedStatement.addBatch();
                if ( !link.isEmpty() ) ps.addBatch();
                ///////////////////////////////////////////////////
            }
            
        }
        
        preparedStatement.executeBatch();
        if ( !link.isEmpty() ) ps.executeBatch();
        preparedStatement.close();
        ps.close();
    }
    
    public void clearLabels() {
        labels.clear();
    }
    
    public void sortClusters() throws Exception {
        
        // Sorting Labels according to rank
        
        Collections.sort(labels,new Comparator<Label>() {
			public int compare (Label n1, Label n2) {
			    if (n1.label_rank > n2.label_rank ) {
				return -1;
			    }
			    else if (n1.label_rank < n2.label_rank ) {
				return 1;
			    }
			    else {
				return 0;
			    }
			}
		    }
                );
    }
    
    
    
    public String returnSummary( TextRank t, double per ) throws Exception {
        
        ArrayList<Sentence> summ = new ArrayList<Sentence>();
        String summarized = "";
        
        int limit = (int)per * ( t.s_list.size() );
        limit = limit / 100;
        
        //// Adding Sentences to summary
        
        int i = 1;
        
        for ( Label l: labels ) {
            if ( i > limit ) break;
            if ( l.points.size() > 0 ) {
                for ( Sentence s : l.points ) {
                    if ( i > limit ) break;
                    summ.add(s);
                    i++;
                }
            }
        }
         
        /// Sorting the summary according to labels
        
        Collections.sort(summ,new Comparator<Sentence>() {
			public int compare (Sentence n1, Sentence n2) {
			    if (n1.index < n2.index ) {
				return -1;
			    }
			    else if (n1.index > n2.index ) {
				return 1;
			    }
			    else {
				return 0;
			    }
			}
		    }
                );
        
        /// Streaming the summary to string format
        
        for ( Sentence point: summ ) {
            summarized += point.text;
            summarized += '\n';
        }
        
        return summarized;
    }
    
    public void showRanks( TextRank t )  {
        
        for ( Node n: t.graph.values() ) {
            System.out.println( n.value.text + " " + n.rank );
        }
        
    }
    
    //***************************************************************//
    //***************************************************************//
    //***************************************************************//
    //***************** PIVOT FUNCTION ******************************//
    //***************************************************************//
    //***************************************************************//
    //***************************************************************//
    
    public String doSummarization ( String text, double percentage, String link ) throws Exception {
        
        
        String summarized;        
        
        file = text;
            
            /// CALL TEXT RANK LOCALLY TO FIND LABELS
        tr = callTextRank( tr );
        
            /// COMBINE LOCAL GRAPH AND GLOBAL TEXT RANK GRAPH
        gtr = combineTextRankGraphs( gtr, tr );

            /// UPDATE LOCAL GRAPH RANK ACCORDING TO GLOBAL TEXT RANK GRAPH
        if ( tr != gtr ) {
               updateLocalRanks(tr);
        }
        
            /// SORT NODES IN GRAPH 
        sortGraph( tr );
            
            /// CREATE NGRAMS 
        createNGrams(tr);
            
            ///CONSTRUCT METRIC SPACE FOR LOCAL GRAPH
        processNGramsMetric(tr);
            
            ///Create Labels and make clusters from text
        clusterText(tr, link);
            
            /// Sort Clusters
        sortClusters();
            
            ///Print Clusters
        summarized = returnSummary( tr, percentage );

        clearLabels();
        
        showRanks( tr );
        
        return summarized;
    }
    
}
//LOG.info("\n" + tr);
//System.out.println("Existing Rank is " + tr.graph.get("NNmodi").rank);
//obj.updateNGramsRank(tr);
//            if ( tr.ngram_subgraph.get("NGramNNresourc") != null ) System.out.println("Existing Rank is " + tr.ngram_subgraph.get("NGramNNresourc").rank);
//System.out.println("Existing Rank is " + tr.graph.get("NNmodi").rank);
  /*  System.out.println( t.ngram_subgraph.get(l.label_key).value.text );
                System.out.println("---------------------------");
                System.out.println();*/