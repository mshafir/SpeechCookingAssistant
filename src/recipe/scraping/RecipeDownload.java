/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.scraping;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import recipe.Recipe;

/**
 *
 * @author Michael
 */
public class RecipeDownload {
    public static ArrayList<String> getCategories() {
        try {
            Connection c = Jsoup.connect("http://www.cooks.com");           
            c.get().html();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return null;
    }
}
