/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on Sep 29, 2011, 11:37:17 AM
 */
package recipe.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import recipe.speech.RASpeechRecognizer;
import recipe.core.Category;
import recipe.core.Ingredient;
import recipe.core.Recipe;
import recipe.db.RecipeDB;
import recipe.speech.result_handlers.RecipeResultHandler;

/**
 *
 * @author Michael
 */
public class MainWindow extends javax.swing.JFrame {
   
    private RecipeResultHandler handler;
    private boolean recipeMode;
   
    /** Creates new form MainWindow */
    public MainWindow() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.png"));
        initComponents();
        loadCategories();
        recipeMode = false;
    }
    
    public final void loadCategories() {
        try {
            ArrayList<Category> categories = RecipeDB.getInstance().getCategories();
            DefaultListModel<Object> model = new DefaultListModel<Object>();
            for (Category c:categories) {
                //System.out.println(c.Name);
                model.addElement(c);
            }
            jList1.setModel(model);
            jList2.setModel(new DefaultListModel<Object>());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void loadRecipe(Recipe recipe) {
        try {
            recipe.Ingredients = RecipeDB.getInstance().getIngredients(recipe.ID);
            recipe.Steps = RecipeDB.getInstance().getSteps(recipe.ID);
            loadRecipeUI(recipe);
            recipeMode = true;
            loadRecipeSpeech(recipe);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void loadRecipeUI(Recipe r) {
        this.setTitle(r.Title);
        lblTitle.setText(r.Title);
        lblYield.setText("Yields "+Float.toString(r.Yield)+" servings.");
        DefaultListModel<Object> modelIng = new DefaultListModel<Object>();
        for (Ingredient i : r.Ingredients) {
            modelIng.addElement(i);
        }
        jList1.setModel(modelIng);
        DefaultListModel<Object> modelSteps = new DefaultListModel<Object>();
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
            handler = new RecipeResultHandler(r);
            handler.addListener(stateControl);
            handler.go();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Started");
    }
    
    private void initComponents() {

        jList1 = new javax.swing.JList<Object>();
        jList2 = new javax.swing.JList<Object>();
        lblTitle = new javax.swing.JLabel();
        lblYield = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sous-Chef");
        setBackground(new java.awt.Color(102, 102, 102));
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
        //setFocusableWindowState(false);
        setForeground(java.awt.Color.gray);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        setLocationRelativeTo(null);

        jList1.setFont(new java.awt.Font("Comic Sans MS", 0, 11));
        jList1.setOpaque(false);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jList1.setCellRenderer(new TransparentCellRenderer());      

        jList2.setFont(new java.awt.Font("Comic Sans MS", 0, 11));
        jList2.setOpaque(false);
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jList2.setCellRenderer(new TransparentCellRenderer());

        javax.swing.JScrollPane sp1 = new javax.swing.JScrollPane(jList1);
        sp1.setBounds(50,100,340,360);
        sp1.setBorder(null);
        sp1.setOpaque(false);
        sp1.setHorizontalScrollBar(null);
        sp1.getViewport().setBackground(new Color(0,0,0,0));
        sp1.getViewport().setOpaque(false);    
        this.add(sp1);
        
        javax.swing.JScrollPane sp2 = new javax.swing.JScrollPane(jList2);
        sp2.setBounds(410,100,340,360);
        sp2.setBorder(null);
        sp2.setOpaque(false);
        //sp2.setHorizontalScrollBar(null);
        sp2.getViewport().setBackground(new Color(0,0,0,0));
        sp2.getViewport().setOpaque(false);    
        this.add(sp2);
        
        lblTitle.setFont(new java.awt.Font("Comic Sans MS", 0, 18));
        lblTitle.setText("Recipe Categories");
        lblTitle.setBounds(50,40,340,40);
        this.add(lblTitle);
        
        lblYield.setFont(new java.awt.Font("Comic Sans MS", 0, 11));
        lblYield.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblYield.setBounds(50,80,340,20);
        this.add(lblYield);
        
        btnBack.setText("Go Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        btnBack.setBounds(600,60,150,20);
        btnBack.setVisible(false);
        this.add(btnBack);
        
    	stateControl = new SpeechStateControl(new Dimension(this.getWidth(),170));
    	stateControl.setLocation(0,this.getHeight()-170);
    	this.add(stateControl);
        
        back = new ImagePanel("images/background.png",
                new Dimension(this.getWidth()-5,this.getHeight()-100));
        this.add(back);
    }

    //CONTROL Events
    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!recipeMode) {
            DefaultListModel<Object> model = new DefaultListModel<Object>();
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
    }

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount()>1 && !recipeMode) {
            Recipe r = (Recipe)jList2.getSelectedValue();
            if (r != null) {
                loadRecipe(r);
            } 
        }
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        recipeMode = false;
        RASpeechRecognizer.getInstance().stop();
        loadCategories();
        lblTitle.setText("Recipe Categories");
        lblYield.setText("");
        btnBack.setVisible(false);
    }

    // Control Variables
    private javax.swing.JButton btnBack;
    private javax.swing.JList<Object> jList1;
    private javax.swing.JList<Object> jList2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblYield;
    private SpeechStateControl stateControl;
    private ImagePanel back;
    // End of variables declaration
}
