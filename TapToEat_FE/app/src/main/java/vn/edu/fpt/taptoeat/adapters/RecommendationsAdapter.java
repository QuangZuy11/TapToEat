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
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.models.MenuItem;
import vn.edu.fpt.taptoeat.utils.CartManager;
import android.widget.Button;
import android.widget.Toast;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private List<ApiService.RecommendationItem> items;

    public RecommendationsAdapter(List<ApiService.RecommendationItem> items) {
        this.items = items != null ? items : new ArrayList<>();
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
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_restaurant)
                            .error(R.drawable.ic_restaurant)
                            .centerCrop()
                            .into(holder.ivMenuItem);
                } else {
                    holder.ivMenuItem.setImageResource(R.drawable.ic_restaurant);
                }
            }

            holder.tvReason.setText(item.getReason() != null ? item.getReason() : "");
            holder.tvMatchBadge.setText("⭐ Phù hợp " + item.getMatchScore() + "%");

            // Wire Add to Cart button
            if (holder.btnAddToCart != null) {
                holder.btnAddToCart.setOnClickListener(v -> {
                    MenuItem menu = item.getMenuItem();
                    if (menu != null) {
                        CartManager.getInstance().addItem(menu, 1, null);
                        Toast.makeText(holder.itemView.getContext(), "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
    
    // Add btn reference to ViewHolder class
    
}
