package vn.edu.fpt.taptoeat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.models.CartItem;

/**
 * Adapter for displaying cart items
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemDeleted(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemActionListener listener) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateData(List<CartItem> newItems) {
        this.cartItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCartItemImage;
        TextView tvCartItemName;
        TextView tvCartItemPrice;
        TextView tvCartItemNotes;
        TextView tvCartItemQuantity;
        TextView tvCartItemTotal;
        ImageView btnDecreaseQuantity;
        ImageView btnIncreaseQuantity;
        ImageView btnDeleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartItemImage = itemView.findViewById(R.id.ivCartItemImage);
            tvCartItemName = itemView.findViewById(R.id.tvCartItemName);
            tvCartItemPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvCartItemNotes = itemView.findViewById(R.id.tvCartItemNotes);
            tvCartItemQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            tvCartItemTotal = itemView.findViewById(R.id.tvCartItemTotal);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        }

        public void bind(CartItem cartItem, OnCartItemActionListener listener) {
            if (cartItem == null || cartItem.getMenuItem() == null) return;

            // Set item data
            tvCartItemName.setText(cartItem.getMenuItem().getName());
            tvCartItemPrice.setText(cartItem.getMenuItem().getFormattedPrice());
            tvCartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
            tvCartItemTotal.setText(cartItem.getFormattedTotalPrice());

            // Show/hide notes
            if (cartItem.getNotes() != null && !cartItem.getNotes().isEmpty()) {
                tvCartItemNotes.setVisibility(View.VISIBLE);
                tvCartItemNotes.setText("Ghi chú: " + cartItem.getNotes());
            } else {
                tvCartItemNotes.setVisibility(View.GONE);
            }

            // Load image
            if (cartItem.getMenuItem().getImage() != null && !cartItem.getMenuItem().getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(cartItem.getMenuItem().getImage())
                        .placeholder(R.drawable.ic_restaurant)
                        .error(R.drawable.ic_restaurant)
                        .centerCrop()
                        .into(ivCartItemImage);
            } else {
                ivCartItemImage.setImageResource(R.drawable.ic_restaurant);
            }

            // Decrease quantity
            btnDecreaseQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    int newQuantity = cartItem.getQuantity() - 1;
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            });

            // Increase quantity
            btnIncreaseQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    int newQuantity = cartItem.getQuantity() + 1;
                    if (newQuantity <= 99) { // Max 99
                        listener.onQuantityChanged(cartItem, newQuantity);
                    }
                }
            });

            // Delete item
            btnDeleteItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemDeleted(cartItem);
                }
            });
        }
    }
}
