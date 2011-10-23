/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.core;

import java.awt.Component;

/**
 *
 * @author Michael
 */
public class Category extends Component {
    public int ID;
    public String Name;
    
    public Category(int id, String name) {
        this.ID = id;
        this.Name = name;
    }
    
    @Override
    public String toString() {
        return Name;
    }
}
