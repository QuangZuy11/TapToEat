package vn.edu.fpt.taptoeat.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.ApiService.OrderResponse;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderResponse> orders;

    public OrderAdapter(List<OrderResponse> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<OrderResponse> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvOrderTime;
        private TextView tvOrderStatus;
        private RecyclerView recyclerViewOrderItems;
        private TextView tvOrderTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            recyclerViewOrderItems = itemView.findViewById(R.id.recyclerViewOrderItems);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);

            // Setup nested RecyclerView
            recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewOrderItems.setNestedScrollingEnabled(false);
        }

        public void bind(OrderResponse order) {
            // Set order ID (last 6 characters)
            String orderId = order.getId();
            if (orderId != null && orderId.length() > 6) {
                orderId = orderId.substring(orderId.length() - 6);
            }
            tvOrderId.setText("#" + orderId);

            // Format time
            String timeStr = formatTime(order.getCreatedAt());
            tvOrderTime.setText(timeStr);

            // Set status with color
            String status = order.getStatus();
            if (status != null && !status.isEmpty()) {
                setStatusBadge(status);
            } else {
                setStatusBadge("unknown");
            }

            // Setup items RecyclerView - handle null items
            List<ApiService.OrderItemResponse> items = order.getItems();
            if (items == null || items.isEmpty()) {
                items = new java.util.ArrayList<>();
            }
            OrderItemAdapter itemAdapter = new OrderItemAdapter(items);
            recyclerViewOrderItems.setAdapter(itemAdapter);

            // Format total price
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvOrderTotal.setText(formatter.format(order.getTotalAmount()) + " đ");
        }

        private void setStatusBadge(String status) {
            String statusText;
            int textColor;
            int backgroundColor;

            switch (status.toLowerCase()) {
                case "pending":
                    statusText = "⏳ Đang chờ";
                    textColor = Color.parseColor("#F57C00"); // Orange 700
                    backgroundColor = Color.parseColor("#FFF3E0"); // Orange 50
                    break;
                case "preparing":
                    statusText = "👨‍🍳 Đang làm";
                    textColor = Color.parseColor("#1976D2"); // Blue 700
                    backgroundColor = Color.parseColor("#E3F2FD"); // Blue 50
                    break;
                case "ready":
                    statusText = "✅ Đã xong";
                    textColor = Color.parseColor("#388E3C"); // Green 700
                    backgroundColor = Color.parseColor("#E8F5E9"); // Green 50
                    break;
                case "served":
                    statusText = "🍽️ Đã phục vụ";
                    textColor = Color.parseColor("#616161"); // Gray 700
                    backgroundColor = Color.parseColor("#F5F5F5"); // Gray 100
                    break;
                default:
                    statusText = status;
                    textColor = Color.parseColor("#757575");
                    backgroundColor = Color.parseColor("#EEEEEE");
                    break;
            }

            tvOrderStatus.setText(statusText);
            tvOrderStatus.setTextColor(textColor);
            tvOrderStatus.setBackgroundColor(backgroundColor);
        }

        private String formatTime(String createdAt) {
            try {
                // Parse ISO 8601 format: 2024-01-15T10:30:00.000Z
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date date = isoFormat.parse(createdAt);
                
                if (date != null) {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                    return displayFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return createdAt;
        }
    }
}
