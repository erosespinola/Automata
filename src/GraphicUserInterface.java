
import java.io.File;
import javax.swing.JFileChooser;
import java.io.FileReader;
import java.util.Scanner;

public class GraphicUserInterface extends javax.swing.JFrame {
    String path;
    private javax.swing.JTextField automataString;
    private javax.swing.JTextField fileLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton openButton;
    private javax.swing.JLabel stateLabel;
    private javax.swing.JLabel stringLabel;
    private javax.swing.JButton testButton;
    private javax.swing.JLabel title;
    
    public GraphicUserInterface() {
        initComponents();
    }

    private void initComponents() {
        title = new javax.swing.JLabel();
        openButton = new javax.swing.JButton();
        automataString = new javax.swing.JTextField();
        fileLabel = new javax.swing.JTextField();
        stringLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        testButton = new javax.swing.JButton();
        stateLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Postfix Evaluator");

        title.setFont(new java.awt.Font("Ebrima", 0, 18)); // NOI18N
        title.setText("Postfix Evaluator 1.2.5.b.0");

        openButton.setText("Open file");
        openButton.setBorderPainted(false);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        fileLabel.setEditable(false);
        fileLabel.setText("No file chosen");

        stringLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stringLabel.setText("Postfix to evaluate");

        testButton.setText("Test");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        stateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fileLabel)
                    .addComponent(automataString)
                    .addComponent(openButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(stringLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(testButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(99, 99, 99))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(title)
                .addGap(21, 21, 21)
                .addComponent(fileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openButton)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stringLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(automataString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(testButton)
                .addGap(18, 18, 18)
                .addComponent(stateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>                        

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        try {
            JFileChooser jfc = new JFileChooser();
            jfc.showOpenDialog(null);
            File open = jfc.getSelectedFile();
            path = open.getAbsolutePath();
            String fileName = open.getName();
            fileLabel.setText(fileName);
        }
        catch(NullPointerException e) {
            stateLabel.setText("Choose a file");
        }
    }                                          

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        try {
        	Postfix p = new Postfix("(en,En)((a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z)*(s,a))");
        	AFNE a = new AFNE(p);
        	a.convertToAFD();        	
            Scanner s = new Scanner(new FileReader(path));
            while (s.hasNextLine()) {
                a.accepted(s.nextLine().trim());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            stateLabel.setText("Error, try again.");
        } 
    }                                          

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphicUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphicUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphicUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphicUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphicUserInterface().setVisible(true);
            }
        });
    }
    
}
