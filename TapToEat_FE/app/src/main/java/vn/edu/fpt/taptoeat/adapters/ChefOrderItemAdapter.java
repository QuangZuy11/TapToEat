package vn.edu.fpt.taptoeat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.api.ApiService.OrderItemResponse;

public class ChefOrderItemAdapter extends RecyclerView.Adapter<ChefOrderItemAdapter.ViewHolder> {

    private List<OrderItemResponse> items;

    public ChefOrderItemAdapter(List<OrderItemResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chef_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItemResponse item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvQuantity;
        private TextView tvNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }

        public void bind(OrderItemResponse item) {
            tvItemName.setText(item.getName());
            tvQuantity.setText("x" + item.getQuantity());
            
            if (item.getNotes() != null && !item.getNotes().isEmpty()) {
                tvNotes.setText("📝 " + item.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }
        }
    }
}