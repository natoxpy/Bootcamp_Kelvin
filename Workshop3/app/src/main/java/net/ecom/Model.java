package net.ecom;

import java.util.HashMap;
import java.util.HashSet;

public class Model {
    private String title = "Terminal Store";
    private HashMap<String, String[]> categories = new HashMap<String, String[]>();
    private HashSet<String> collapsedCategories = new HashSet<String>();
    private HashMap<String, String[]> products = new HashMap<String, String[]>();
    private String selectedCategory = "Shoes";

    public Model() {
        // collapsedCategories.add("Groceries");

        // String[] groceries = {"Produce", "Dairy", "Bakery", "Snacks", "Beverages"};
        // this.categories.put("Groceries", groceries);

        // String[] homeGoods = {"Furniture", "Kitchenware", "Bedding", "Decor", "Storage"};
        // this.categories.put("Home Goods", homeGoods);

        // String[] clothing = {
        //     "Men's Wear", "Women's Wear", "Kids' Wear", "Shoes", "Accessories", "Outerwear"};
        // this.categories.put("Clothing", clothing);

        // String[] shoes = {"Nike Air Max 90", "Adidas Ultraboost", "Converse Chuck Taylor All Star",
        //     "Vans Old Skool", "New Balance 990", "Dr. Martens 1460 Boot", "Timberland 6-Inch Boot",
        //     "Puma Suede Classic", "Clarks Desert Boot", "Birkenstock Arizona Sandal",
        //     "Crocs Classic Clog", "Reebok Classic Leather", "ASICS Gel-Kayano", "Skechers Go Walk",
        //     "Hoka Clifton"};

        // this.products.put("Shoes", shoes);
    }

    public String getTitle() {
        return this.title;
    }

    public HashMap<String, String[]> getCategories() {
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

    public String[] getCategoryProducts(String category) {
        return this.products.get(category);
    }
}
