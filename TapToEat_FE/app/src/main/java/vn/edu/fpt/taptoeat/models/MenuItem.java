package vn.edu.fpt.taptoeat.models;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MenuItem {
    private String _id;
    private String name;
    private String description;
    private double price;
    
    @SerializedName("imageUrl")
    private String image;
    
    @JsonAdapter(CategoryIdDeserializer.class)
    private CategoryIdWrapper categoryId;
    private int preparationTime;
    private boolean isAvailable;
    private List<String> tags;

    public MenuItem() {
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryId() {
        // Return the string ID from the wrapper object if it exists
        return categoryId != null ? categoryId.get_id() : null;
    }

    public void setCategoryId(String categoryId) {
        // Create a wrapper with just the ID
        this.categoryId = new CategoryIdWrapper(categoryId, null, 0);
    }
    
    public void setCategoryIdWrapper(CategoryIdWrapper categoryId) {
        this.categoryId = categoryId;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Utility methods
    public String getFormattedPrice() {
        return String.format("%,.0f đ", price);
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }
    
    // Inner class to handle categoryId which can be String or Object
    public static class CategoryIdWrapper {
        private String _id;
        private String name;
        private int displayOrder;
        
        public CategoryIdWrapper(String _id, String name, int displayOrder) {
            this._id = _id;
            this.name = name;
            this.displayOrder = displayOrder;
        }
        
        public String get_id() {
            return _id;
        }
        
        public String getName() {
            return name;
        }
        
        public int getDisplayOrder() {
            return displayOrder;
        }
    }
}
