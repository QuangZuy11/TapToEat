package vn.edu.fpt.taptoeat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.api.ApiService.OrderItemResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private List<OrderItemResponse> items;

    public OrderItemAdapter(List<OrderItemResponse> items) {
        this.items = items != null ? items : new java.util.ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItemResponse item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<OrderItemResponse> newItems) {
        this.items = newItems != null ? newItems : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderItemName;
        private TextView tvOrderItemQuantity;
        private TextView tvOrderItemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderItemName = itemView.findViewById(R.id.tvOrderItemName);
            tvOrderItemQuantity = itemView.findViewById(R.id.tvOrderItemQuantity);
            tvOrderItemPrice = itemView.findViewById(R.id.tvOrderItemPrice);
        }

        public void bind(OrderItemResponse item) {
            tvOrderItemName.setText(item.getName());
            tvOrderItemQuantity.setText("x" + item.getQuantity());
            
            double totalPrice = item.getPrice() * item.getQuantity();
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvOrderItemPrice.setText(formatter.format(totalPrice) + " đ");
        }
    }
}
