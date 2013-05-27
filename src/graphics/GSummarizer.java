/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GSummarizer.java
 *
 * Created on Apr 30, 2013, 2:24:09 PM
 */
package graphics;

/**
 *
 * @author Administrator
 */

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.net.MalformedURLException;
import java.net.URL;
import de.l3s.boilerpipe.extractors.*;


import de.vogella.rss.model.Feed;
import de.vogella.rss.model.FeedMessage;
import de.vogella.rss.read.RSSFeedParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.summarizer.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GSummarizer extends javax.swing.JFrame {

    /** Creates new form GSummarizer */
    public GSummarizer() throws Exception {
        
        Class.forName("com.mysql.jdbc.Driver");
        
        connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/nlsummarize?"
              + "user=root&password=admin");
        
        if ( connect.isClosed() ) System.out.println("CONNECTION CLOSED");
            
        obj = new Summarize();
        
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabel1.setText("LINK");
        jLabel1.setBounds(10, 30, 50, 30);
        jLayeredPane1.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jTextField2.setBounds(70, 30, 740, 30);
        jLayeredPane1.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setText("Info per data");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.setBounds(830, 30, 130, 30);
        jLayeredPane1.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 1, 20)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NEWS SUMMARIZER");
        jLabel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 255, 255)));
        jLabel2.setBounds(0, 0, 1370, 25);
        jLayeredPane1.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton1.setText("Generate Summary");
        jButton1.setActionCommand("Generate");
        jButton1.setAlignmentX(50);
        jButton1.setAlignmentY(50);
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setVerifyInputWhenFocusTarget(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GSummarizer.this.actionPerformed(evt);
            }
        });
        jButton1.setBounds(980, 30, 130, 30);
        jLayeredPane1.add(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        jScrollPane1.setBounds(10, 70, 540, 550);
        jLayeredPane1.add(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextArea2.setColumns(20);
        jTextArea2.setEditable(false);
        jTextArea2.setLineWrap(true);
        jTextArea2.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea2);

        jScrollPane2.setBounds(570, 70, 540, 550);
        jLayeredPane1.add(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField3.setText("Enter RSS Link");
        jTextField3.setBounds(1130, 70, 110, 40);
        jLayeredPane1.add(jTextField3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Vani", 1, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("RSS FEEDS");
        jLabel3.setBounds(1130, 30, 190, 30);
        jLayeredPane1.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setText("Load");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GSummarizer.this.actionPerformed(evt);
            }
        });
        jButton2.setBounds(1260, 70, 70, 30);
        jLayeredPane1.add(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jScrollPane3.setBounds(1130, 120, 200, 500);
        jLayeredPane1.add(jScrollPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton3.setText("Search Topic");
        jButton3.setActionCommand("search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GSummarizer.this.actionPerformed(evt);
            }
        });
        jButton3.setBounds(1130, 630, 190, 23);
        jLayeredPane1.add(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField1ActionPerformed

    private void actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionPerformed
        // TODO add your handling code here:
        String command = evt.getActionCommand();
        if ( command.equals("Generate")) {
            
            String link = jTextField2.getText();
            URL url;
            String text, inFile, outFile = "";
            double limit = Double.parseDouble(jTextField1.getText());
            
            if ( link.equals(" ") ) { } else {
                try {
                     //////////////// SQL PROCESSING ///////////////////
                    try {
                        
                        PreparedStatement ps = connect.prepareStatement("select details from nlsummarize.links_summary where link like ?");
                        ps.setString(1,link);
                        ResultSet rs = ps.executeQuery();
                        
                        int i,size;
                        size = rs.getFetchSize();
                        size = (int)(limit / 100 ) * size;
                        i = 1;
                        while ( rs.next() ) {
                            if ( i > size ) break;
                            outFile += rs.getString("details");
                            outFile += '\n';
                            i++;
                        }
                        
                        jTextArea2.setText(outFile);
                        rs.close();
                        ps.close();
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(GSummarizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                    
                    //////////////////////////////////////////////////
                    
                    url = new URL(link);
                    try {
                        text = ArticleExtractor.INSTANCE.getText(url);
                        jTextArea1.setText(text);
                    } catch (BoilerpipeProcessingException ex) {
                        //Logger.getLogger(GSummarizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (MalformedURLException ex) {
                    //Logger.getLogger(GSummarizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            
            inFile = jTextArea1.getText();
            if ( inFile.equals("") || !outFile.isEmpty() ) { System.out.println("OutFile Is not Empty"); }
            else {
                try {
                    
                    outFile = obj.doSummarization(inFile, limit, link);
                    jTextArea2.setText("");
                    jTextArea2.setText(outFile);
                    
                } catch (Exception ex) {
                    Logger.getLogger(GSummarizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        if ( command.equals("Load") ) {
            
            jTextArea3.setText(" ");
            String url = jTextField3.getText();
            RSSFeedParser parser = new RSSFeedParser(url);
            Feed feed = parser.readFeed();
            String rss = "";
            for (FeedMessage message : feed.getMessages()) {
                //    System.out.println(message);
                rss += '\n';
                rss += message;
            }
            jTextArea3.setText(rss);
        }
        
        if ( command.equals("search") ) {
            GTopic topic;
            try {
                topic = new GTopic();
                topic.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(GSummarizer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
}//GEN-LAST:event_actionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    new GSummarizer().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(GSummarizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private Summarize obj;
    private Connection connect = null;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
