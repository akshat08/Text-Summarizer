/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GTopic.java
 *
 * Created on Apr 30, 2013, 2:19:15 PM
 */
package graphics;

/**
 *
 * @author Administrator
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GTopic extends javax.swing.JFrame {

    /** Creates new form GTopic */
    public GTopic() throws Exception {
        
        System.out.println("System Started");
        
        Class.forName("com.mysql.jdbc.Driver");
        
        connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/nlsummarize?"
              + "user=root&password=admin");
        
        if ( connect.isClosed() ) System.out.println("CONNECTION CLOSED");
        
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
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Topic");
        jLabel1.setBounds(10, 10, 80, 30);
        jLayeredPane1.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jTextField1.setBounds(70, 10, 520, 30);
        jLayeredPane1.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton1.setText("Find");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GTopic.this.actionPerformed(evt);
            }
        });
        jButton1.setBounds(600, 10, 90, 30);
        jLayeredPane1.add(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setText("Related Stories");
        jLabel2.setBounds(20, 44, 120, 30);
        jLayeredPane1.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jScrollPane1.setBounds(20, 70, 240, 380);
        jLayeredPane1.add(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setText("Details");
        jLabel3.setBounds(300, 50, 60, 20);
        jLayeredPane1.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jScrollPane2.setBounds(300, 70, 390, 380);
        jLayeredPane1.add(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionPerformed
        // TODO add your handling code here:
        String comm = evt.getActionCommand();
        if ( comm.equals("Find") ) {
            jTextArea1.setText("");
            jTextArea2.setText("");
            String out = "";
            try {
                String topic  = jTextField1.getText();
                PreparedStatement ps = connect.prepareStatement("select link from nlsummarize.label_link where name like ?");
                ps.setString(1,"%"+topic+"%");
                ResultSet rs = ps.executeQuery();
                
                while ( rs.next() ) {
                    String link = rs.getString("LINK");
                    out += link;
                    out += '\n';
                }
                
                jTextArea1.setText(out);
                
                out = "";
                
                ps = connect.prepareStatement("select details from nlsummarize.label_summary where name like ?");
                ps.setString(1,"%"+topic+"%");
                ResultSet rs1 = ps.executeQuery();
                
                while ( rs1.next() ) {
                    String point = rs1.getString("details");
                    out += point;
                    out += '\n';
                }
                
                jTextArea2.setText(out);
                
                ps.close();
                
            } catch (SQLException ex) {
                Logger.getLogger(GTopic.class.getName()).log(Level.SEVERE, null, ex);
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
                    new GTopic().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(GTopic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    private Connection connect = null;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
