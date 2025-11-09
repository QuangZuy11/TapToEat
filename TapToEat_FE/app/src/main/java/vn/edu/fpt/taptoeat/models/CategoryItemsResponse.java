package vn.edu.fpt.taptoeat.models;

import java.util.List;

/**
 * Response wrapper for GET /api/categories/:id/items
 * Backend returns: {"success": true, "data": {"category": {...}, "items": [...]}}
 */
public class CategoryItemsResponse {
    private Category category;
    private List<MenuItem> items;
    private int totalItems;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
