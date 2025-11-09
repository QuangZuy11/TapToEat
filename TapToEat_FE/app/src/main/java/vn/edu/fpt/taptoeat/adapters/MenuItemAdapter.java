package vn.edu.fpt.taptoeat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.models.MenuItem;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    private List<MenuItem> menuItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem);
    }

    public MenuItemAdapter(List<MenuItem> menuItems, OnItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (menuItems == null || position >= menuItems.size()) {
            return;
        }
        MenuItem item = menuItems.get(position);
        if (item != null) {
            holder.bind(item, listener);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems != null ? menuItems.size() : 0;
    }

    public void updateData(List<MenuItem> newItems) {
        this.menuItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMenuItem;
        TextView tvMenuItemName;
        TextView tvMenuItemDescription;
        TextView tvMenuItemPrice;
        TextView tvPreparationTime;
        TextView tvPopularTag;
        TextView tvUnavailable;
        View overlayUnavailable;
        Button btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuItem = itemView.findViewById(R.id.ivMenuItem);
            tvMenuItemName = itemView.findViewById(R.id.tvMenuItemName);
            tvMenuItemDescription = itemView.findViewById(R.id.tvMenuItemDescription);
            tvMenuItemPrice = itemView.findViewById(R.id.tvMenuItemPrice);
            tvPreparationTime = itemView.findViewById(R.id.tvPreparationTime);
            tvPopularTag = itemView.findViewById(R.id.tvPopularTag);
            tvUnavailable = itemView.findViewById(R.id.tvUnavailable);
            overlayUnavailable = itemView.findViewById(R.id.overlayUnavailable);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        public void bind(MenuItem item, OnItemClickListener listener) {
            if (item == null) return;
            
            tvMenuItemName.setText(item.getName() != null ? item.getName() : "");
            tvMenuItemDescription.setText(item.getDescription() != null ? item.getDescription() : "");
            tvMenuItemPrice.setText(item.getFormattedPrice());
            tvPreparationTime.setText(item.getPreparationTime() + " phút");

            // Show popular tag
            if (item.hasTag("popular")) {
                tvPopularTag.setVisibility(View.VISIBLE);
            } else {
                tvPopularTag.setVisibility(View.GONE);
            }

            // Handle unavailable items
            if (!item.isAvailable()) {
                overlayUnavailable.setVisibility(View.VISIBLE);
                tvUnavailable.setVisibility(View.VISIBLE);
                btnAddToCart.setEnabled(false);
                btnAddToCart.setAlpha(0.5f);
                itemView.setAlpha(0.6f);
            } else {
                overlayUnavailable.setVisibility(View.GONE);
                tvUnavailable.setVisibility(View.GONE);
                btnAddToCart.setEnabled(true);
                btnAddToCart.setAlpha(1.0f);
                itemView.setAlpha(1.0f);
            }

            // Load image with Glide
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(item.getImage())
                    .placeholder(R.drawable.ic_restaurant)
                    .error(R.drawable.ic_restaurant)
                    .centerCrop()
                    .into(ivMenuItem);
            } else {
                ivMenuItem.setImageResource(R.drawable.ic_restaurant);
            }

            // Add to cart button click
            btnAddToCart.setOnClickListener(v -> {
                if (item.isAvailable() && listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
