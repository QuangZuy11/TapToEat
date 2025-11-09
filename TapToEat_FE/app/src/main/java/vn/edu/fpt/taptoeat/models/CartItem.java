package vn.edu.fpt.taptoeat.models;

/**
 * Model for cart item
 * Represents a menu item added to cart with quantity and notes
 */
public class CartItem {
    private MenuItem menuItem;
    private int quantity;
    private String notes;
    private long addedAt; // Timestamp when added to cart

    public CartItem(MenuItem menuItem, int quantity, String notes) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.notes = notes;
        this.addedAt = System.currentTimeMillis();
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }

    // Calculate total price for this cart item
    public double getTotalPrice() {
        return menuItem.getPrice() * quantity;
    }

    public String getFormattedTotalPrice() {
        return String.format("%,.0f đ", getTotalPrice());
    }

    // Check if this cart item is for the same menu item
    public boolean isSameMenuItem(String menuItemId) {
        return menuItem != null && menuItem.get_id().equals(menuItemId);
    }
}
