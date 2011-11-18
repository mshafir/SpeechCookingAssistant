/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Michael
 */
class ImagePanel extends JPanel {

  protected Image img;
  protected Dimension size;

  public ImagePanel(String img,Dimension size) {
    this(new ImageIcon(img).getImage(),size);
  }

  public ImagePanel(Image img, Dimension size) {
    this.img = img;
    this.size = size;
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

    @Override
  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, size.width,size.height, null);
  }
}
