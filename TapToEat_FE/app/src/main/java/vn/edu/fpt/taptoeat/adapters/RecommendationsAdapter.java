package vn.edu.fpt.taptoeat.adapters;

import android.util.Log;
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
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.models.MenuItem;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private List<ApiService.RecommendationItem> items;

    public RecommendationsAdapter(List<ApiService.RecommendationItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public interface OnAddToCartListener {
        void onAddToCart(vn.edu.fpt.taptoeat.models.MenuItem menuItem);
    }

    private OnAddToCartListener addToCartListener;

    public void setOnAddToCartListener(OnAddToCartListener listener) {
        this.addToCartListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ai_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiService.RecommendationItem item = items.get(position);
        if (item != null) {
            if (item.getMenuItem() != null) {
                MenuItem menu = item.getMenuItem();
                holder.tvItemName.setText(menu.getName() != null ? menu.getName() : "");
                holder.tvItemDesc.setText(menu.getDescription() != null ? menu.getDescription() : "");
                holder.tvItemPrice.setText(menu.getFormattedPrice());

                // Load image with Glide if available
                String imageUrl = menu.getImage();
                Log.d("RecommendationsAdapter", "Loading image for menu " + (menu.getName() != null ? menu.getName() : "<no-name>") + " -> " + imageUrl);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_restaurant)
                            .error(R.drawable.ic_restaurant)
                            .centerCrop()
                            .into(holder.ivMenuItem);
                } else {
                    holder.ivMenuItem.setImageResource(R.drawable.ic_restaurant);
                    // debug hint: show toast once that image missing (only in debug builds)
                    // Avoid spamming by logging instead of toast
                    Log.w("RecommendationsAdapter", "No imageUrl for menu: " + menu.getName());
                }
                // Wire add to cart button
                holder.btnAddToCart.setOnClickListener(v -> {
                    if (addToCartListener != null) {
                        addToCartListener.onAddToCart(menu);
                    }
                });
            }

            holder.tvReason.setText(item.getReason() != null ? item.getReason() : "");
            holder.tvMatchBadge.setText("⭐ Phù hợp " + item.getMatchScore() + "%");
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateData(List<ApiService.RecommendationItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivMenuItem;
        TextView tvItemName;
        TextView tvItemDesc;
        TextView tvItemPrice;
        TextView tvReason;
        TextView tvMatchBadge;
    com.google.android.material.button.MaterialButton btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuItem = itemView.findViewById(R.id.ivMenuItem);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDesc = itemView.findViewById(R.id.tvItemDescription);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvReason = itemView.findViewById(R.id.tvAIReason);
            tvMatchBadge = itemView.findViewById(R.id.tvMatchBadge);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
