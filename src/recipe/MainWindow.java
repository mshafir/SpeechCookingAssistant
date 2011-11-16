/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on Sep 29, 2011, 11:37:17 AM
 */
package recipe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import recipe.speech.RATextToSpeech;
import recipe.speech.RASpeechRecognizer;
import recipe.core.Category;
import recipe.core.Ingredient;
import recipe.core.Recipe;
import recipe.db.RecipeDB;
import recipe.speech.SpeechEventListener;
import recipe.speech.handlers.RecipeSRH;

/**
 *
 * @author Michael
 */
public class MainWindow extends javax.swing.JFrame {
   
    private RecipeSRH handler;
    private boolean recipeMode;
    private ImagePanel speaker0;
    private ImagePanel speaker1;
    private ImagePanel speaker2;
    
    /** Creates new form MainWindow */
    public MainWindow() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.png"));
        load();
        initComponents();
        setupLayout();
        recipeMode = false;
        loadCategories();
    }
    
    public final void load() {
        //Image background = new ImageIcon("images/splash.png").getImage();
        //g.drawImage(background, 0,0,size.width,size.height,null);
        //SplashScreen splash = SplashScreen.getSplashScreen();
        //Graphics2D g = splash.createGraphics();
        //Dimension size = splash.getSize();
        
        //Font f = Font.getFont("Comic Sans MS");      
        //g.setFont(f.deriveFont(24));
        //g.drawString("Recipe Assistant", 100,size.width-100);
        //g.setFont(f.deriveFont(12));
        //g.drawString("Loading the recipe database...",140,size.width-100);
        //splash.update();
        RecipeDB.initialize();
        //g.drawString("Loading the speech recognizer...",160,size.width-100);
        //splash.update();
        RASpeechRecognizer.initialize();
        //g.drawString("Loading text to speech...",180,size.width-100);
        //splash.update();
        RATextToSpeech.initialize();
        //splash.close();
    }
    
    public final void setupLayout() {
        speaker0 = new ImagePanel("images/speaker0.png",
                new Dimension(150,150));
        this.add(speaker0);
        speaker0.setBounds(0, this.getHeight()-170, speaker0.getWidth(), speaker0.getHeight());
        speaker0.setVisible(false);
        
        speaker1 = new ImagePanel("images/speaker1.png",
                new Dimension(150,150));
        this.add(speaker1);
        speaker1.setBounds(0, this.getHeight()-170, speaker1.getWidth(), speaker1.getHeight());
        speaker1.setVisible(false);
        
        speaker2 = new ImagePanel("images/speaker2.png",
                new Dimension(150,150));
        this.add(speaker2);
        speaker2.setBounds(0, this.getHeight()-170, speaker2.getWidth(), speaker2.getHeight());
        speaker2.setVisible(false);
        
        this.setLayout(null);
        ImagePanel back = new ImagePanel("images/background.png",
                new Dimension(this.getWidth()-5,this.getHeight()-100));
        this.add(back);
        
        ImagePanel bar = new ImagePanel("images/bar.png",
                new Dimension(this.getWidth()-5,100));
        this.add(bar);
        bar.setBounds(0, this.getHeight()-100, bar.getWidth(), bar.getHeight());
        
        jList1.setCellRenderer(new MyCellRenderer());
        jList2.setCellRenderer(new MyCellRenderer());
        jList1.setBounds(50,100,340,380);
        jList2.setBounds(430,100,340,380);
        lblTitle.setBounds(50,40,340,40);
        lblYield.setBounds(50,80,340,20);
        btnBack.setBounds(620,60,150,20);
        btnBack.setVisible(false);
        lblQ.setBounds(200,540,500,20);
        lblQ.setForeground(Color.WHITE);
        lblR.setBounds(200,560,500,20);
        lblR.setForeground(Color.WHITE);
    }
    
    public final void loadCategories() {
        try {
            ArrayList<Category> categories = RecipeDB.getInstance().getCategories();
            DefaultListModel model = new DefaultListModel();
            for (Category c:categories) {
                //System.out.println(c.Name);
                model.addElement(c);
            }
            jList1.setModel(model);
            jList2.setModel(new DefaultListModel());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void loadRecipe(Recipe recipe) {
        try {
            recipe.Ingredients = RecipeDB.getInstance().getIngredients(recipe.ID);
            recipe.Steps = RecipeDB.getInstance().getSteps(recipe.ID);
            loadRecipeUI(recipe);
            loadRecipeSpeech(recipe);
            recipeMode = true;
            //RecipeWindow rWnd = new RecipeWindow(recipe);
            //rWnd.setVisible(true);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void loadRecipeUI(Recipe r) {
        this.setTitle(r.Title);
        lblTitle.setText(r.Title);
        lblYield.setText("Yields "+Float.toString(r.Yield)+" servings.");
        DefaultListModel modelIng = new DefaultListModel();
        for (Ingredient i : r.Ingredients) {
            modelIng.addElement(i);
        }
        jList1.setModel(modelIng);
        DefaultListModel modelSteps = new DefaultListModel();
        int curNum = 1;
        for (String s : r.Steps) {
            modelSteps.addElement(Integer.toString(curNum)+". "+s);
            curNum++;
        }
        jList2.setModel(modelSteps);
        btnBack.setVisible(true);
    }
    
    public final void loadRecipeSpeech(Recipe r) {
        try {
            handler = new RecipeSRH(r);
            handler.addListener(new RecipeSpeechListener(this));
            handler.go();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Started");
    }
    
    class RecipeSpeechListener implements SpeechEventListener {
        MainWindow window;
        public RecipeSpeechListener(MainWindow window) {
            this.window = window;
        }
        
        @Override
        public void handleEvent(int state, String arg) {
            setSpeaker(state);
            if (state == 1) {
                window.lblQ.setText(arg.replaceAll("sue chef", "Sous-chef,") +"?");
                window.lblR.setText("...");
            } else if (state == 2) {
                window.lblR.setText(arg);
            }
        }
        
        private void setSpeaker(int state) {
            if (state == 0)
                speaker0.setVisible(true);
            else
                speaker0.setVisible(false);
            if (state == 1)
                speaker1.setVisible(true);
            else
                speaker1.setVisible(false);
            if (state == 2)
                speaker2.setVisible(true);
            else
                speaker2.setVisible(false);
        }
        
    }
    
    class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
         public MyCellRenderer() {
             setOpaque(false);
         }

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            this.setFont(list.getFont());
            if (isSelected) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
                this.setOpaque(true);
            } else {
                setForeground(list.getForeground());
                this.setOpaque(false);
            }
            return this;
        }
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jList1 = new javax.swing.JList();
        jList2 = new javax.swing.JList();
        lblTitle = new javax.swing.JLabel();
        lblYield = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        lblQ = new javax.swing.JLabel();
        lblR = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sous-Chef");
        setBackground(new java.awt.Color(102, 102, 102));
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
        setFocusableWindowState(false);
        setForeground(java.awt.Color.gray);
        setMinimumSize(new java.awt.Dimension(640, 480));
        setResizable(false);

        jList1.setFont(new java.awt.Font("Comic Sans MS", 0, 11));
        jList1.setOpaque(false);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });

        jList2.setFont(new java.awt.Font("Comic Sans MS", 0, 11));
        jList2.setOpaque(false);
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Comic Sans MS", 0, 18));
        lblTitle.setText("Recipe Categories");

        lblYield.setFont(new java.awt.Font("Comic Sans MS", 0, 11));
        lblYield.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        btnBack.setText("Go Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblR.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jList1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jList2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBack))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblQ)
                        .addGap(80, 80, 80)
                        .addComponent(lblTitle)
                        .addGap(120, 120, 120)
                        .addComponent(lblYield)))
                .addContainerGap(250, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(lblYield)
                    .addComponent(lblQ))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jList2, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jList1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblR))
                    .addComponent(btnBack))
                .addContainerGap(389, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        if (!recipeMode) {
            DefaultListModel model = new DefaultListModel();
            Category c = (Category)jList1.getSelectedValue();
            if (c != null) {
                try {
                    ArrayList<Recipe> recipes = RecipeDB.getInstance().getRecipes(c.ID);
                    for (Recipe r:recipes) {
                        model.addElement(r);
                    }
                    jList2.setModel(model);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        if (evt.getClickCount()>1 && !recipeMode) {
            Recipe r = (Recipe)jList2.getSelectedValue();
            if (r != null) {
                loadRecipe(r);
            } 
        }
    }//GEN-LAST:event_jList2MouseClicked

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        recipeMode = false;
        RASpeechRecognizer.getInstance().stop();
        loadCategories();
        lblTitle.setText("Recipe Categories");
        lblYield.setText("");
        btnBack.setVisible(false);
    }//GEN-LAST:event_btnBackActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } 
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JLabel lblQ;
    private javax.swing.JLabel lblR;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblYield;
    // End of variables declaration//GEN-END:variables
}
