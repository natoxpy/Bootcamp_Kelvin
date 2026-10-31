package net.ecom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.ecom.data.Product;
import org.jspecify.annotations.Nullable;

public class Model {
    @Nullable private String selectedCategory = null;
    private String title = "Terminal Store";
    private HashSet<String> collapsedCategories = new HashSet<String>();
    private HashMap<String, HashSet<String>> categories = new HashMap<>();
    private HashMap<String, ArrayList<Product>> products = new HashMap<>();

    // Controller State
    private String focusArea = "";
    private HashMap<String, String> focusBehaviors;
    private HashMap<String, Object> focusState = new HashMap<>();
    //

    public Model(
        HashMap<String, HashSet<String>> categories, HashMap<String, ArrayList<Product>> products) {
        this.categories = categories;
        this.products = products;

        this.selectedCategory = "Snacks";
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, HashSet<String>> getCategories() {
        return this.categories;
    }

    public boolean isCollapsed(String category) {
        return this.collapsedCategories.contains(category);
    }

    public void addCollapse(String category) {
        this.collapsedCategories.add(category);
    }

    public void removeCollapse(String category) {
        this.collapsedCategories.remove(category);
    }

    public String getCategorySelection() {
        return this.selectedCategory;
    }

    public void setCategorySelection(String category) {
        this.selectedCategory = category;
    }

    public ArrayList<Product> getCategoryProducts(String category) {
        return this.products.get(category);
    }

    // Controller
    public void setFocusArea(String fa) {
        this.focusArea = fa;
    }

    public String getFocusArea() {
        return this.focusArea;
    }

    public HashMap<String, String> getFocusBehaviors() {
        return this.focusBehaviors;
    }

    public void setFocusBehaviors(HashMap<String, String> fb) {
        this.focusBehaviors = fb;
    }

    public Object getFocusState(String key) {
        return this.focusState.get(key);
    }

    public void setFocusState(String key, Object state) {
        this.focusState.put(key, state);
    }
}
