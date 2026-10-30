package net.ecom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.jspecify.annotations.Nullable;

public class Model {
    @Nullable private String selectedCategory = null;
    private String title = "Terminal Store";
    private HashSet<String> collapsedCategories = new HashSet<String>();
    private HashMap<String, HashSet<String>> categories = new HashMap<>();
    private HashMap<String, ArrayList<String>> products = new HashMap<>();

    public Model() {
        collapsedCategories.add("Groceries");
    }

    public Model(
        HashMap<String, HashSet<String>> categories, HashMap<String, ArrayList<String>> products) {
        this.categories = categories;
        this.products = products;
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

    public ArrayList<String> getCategoryProducts(String category) {
        return this.products.get(category);
    }
}
