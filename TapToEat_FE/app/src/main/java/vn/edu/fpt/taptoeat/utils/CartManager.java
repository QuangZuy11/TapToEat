package vn.edu.fpt.taptoeat.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.taptoeat.models.CartItem;
import vn.edu.fpt.taptoeat.models.MenuItem;

/**
 * Manages shopping cart in memory
 * Stores cart items locally before submitting order
 */
public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private List<CartChangeListener> listeners;

    private CartManager() {
        cartItems = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Add item to cart
    public void addItem(MenuItem menuItem, int quantity, String notes) {
        if (menuItem == null || quantity <= 0) {
            return;
        }

        // Check if item already exists in cart
        CartItem existingItem = findCartItem(menuItem.get_id());
        if (existingItem != null) {
            // Update quantity and notes
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            if (notes != null && !notes.trim().isEmpty()) {
                existingItem.setNotes(notes);
            }
        } else {
            // Add new cart item
            CartItem cartItem = new CartItem(menuItem, quantity, notes);
            cartItems.add(cartItem);
        }

        notifyCartChanged();
    }

    // Update item quantity
    public void updateQuantity(String menuItemId, int newQuantity) {
        CartItem item = findCartItem(menuItemId);
        if (item != null) {
            if (newQuantity <= 0) {
                removeItem(menuItemId);
            } else {
                item.setQuantity(newQuantity);
                notifyCartChanged();
            }
        }
    }

    // Update item notes
    public void updateNotes(String menuItemId, String notes) {
        CartItem item = findCartItem(menuItemId);
        if (item != null) {
            item.setNotes(notes);
            notifyCartChanged();
        }
    }

    // Remove item from cart
    public void removeItem(String menuItemId) {
        CartItem item = findCartItem(menuItemId);
        if (item != null) {
            cartItems.remove(item);
            notifyCartChanged();
        }
    }

    // Find cart item by menu item ID
    private CartItem findCartItem(String menuItemId) {
        for (CartItem item : cartItems) {
            if (item.isSameMenuItem(menuItemId)) {
                return item;
            }
        }
        return null;
    }

    // Get all cart items
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // Get total items count
    public int getTotalItemsCount() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    // Get total price
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public String getFormattedTotalPrice() {
        return String.format("%,.0f đ", getTotalPrice());
    }

    // Check if cart is empty
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    // Clear cart
    public void clearCart() {
        cartItems.clear();
        notifyCartChanged();
    }

    // Listener interface for cart changes
    public interface CartChangeListener {
        void onCartChanged();
    }

    // Register listener
    public void addCartChangeListener(CartChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    // Unregister listener
    public void removeCartChangeListener(CartChangeListener listener) {
        listeners.remove(listener);
    }

    // Notify all listeners
    private void notifyCartChanged() {
        for (CartChangeListener listener : listeners) {
            listener.onCartChanged();
        }
    }
}
