package recipe.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class TransparentCellRenderer extends JLabel implements ListCellRenderer<Object> {

	public TransparentCellRenderer() {
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
