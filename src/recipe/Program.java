package recipe;

import recipe.db.RecipeDB;
import recipe.gui.MainWindow;
import recipe.gui.SplashScreen;
import recipe.speech.RASpeechRecognizer;
import recipe.speech.RATextToSpeech;

/**
 * Application starting point
 * @author Michael
 *
 */
public class Program {
	/**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the look and feel */
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
        
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);
        splash.setStatus("Loading recipe databse...");
        RecipeDB.initialize();
        splash.setStatus("Initializing speech recognition...");
        RASpeechRecognizer.initialize();
        splash.setStatus("Initializing text to speech...");
        RATextToSpeech.initialize();
        splash.setVisible(false);
        splash.dispose();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
}
