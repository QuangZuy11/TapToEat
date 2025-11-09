package vn.edu.fpt.taptoeat.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.api.ApiService.OrderResponse;

public class ChefOrderAdapter extends RecyclerView.Adapter<ChefOrderAdapter.ViewHolder> {

    public interface OnOrderStatusChangeListener {
        void onStatusChanged(OrderResponse order, String newStatus);
    }
    
    public interface OnItemStatusChangeListener {
        void onItemStatusChanged(String orderId, String itemId, String newStatus);
    }

    private List<OrderResponse> orders;
    private OnOrderStatusChangeListener listener;
    private OnItemStatusChangeListener itemListener;

    public ChefOrderAdapter(List<OrderResponse> orders, OnOrderStatusChangeListener listener) {
        this.orders = orders;
        this.listener = listener;
    }
    
    public ChefOrderAdapter(List<OrderResponse> orders, OnOrderStatusChangeListener listener, OnItemStatusChangeListener itemListener) {
        this.orders = orders;
        this.listener = listener;
        this.itemListener = itemListener;
    }
    
    public void updateOrders(List<OrderResponse> newOrders) {
        this.orders.clear();
        this.orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chef_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardOrder;
        private TextView tvOrderInfo;
        private TextView tvOrderTime;
        private TextView tvTableNumber;
        private TextView tvWaitingTime;
        private RecyclerView recyclerViewItems;
        private MaterialButton btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardOrder = (MaterialCardView) itemView;
            tvOrderInfo = itemView.findViewById(R.id.tvOrderInfo);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvWaitingTime = itemView.findViewById(R.id.tvWaitingTime);
            recyclerViewItems = itemView.findViewById(R.id.recyclerViewItems);
            btnAction = itemView.findViewById(R.id.btnAction);

            // Setup nested RecyclerView
            recyclerViewItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewItems.setNestedScrollingEnabled(false);
        }

        public void bind(OrderResponse order, OnOrderStatusChangeListener listener) {
            // Order info
            String orderId = order.getOrderNumber() != null ? 
                    order.getOrderNumber() : 
                    order.getId().substring(Math.max(0, order.getId().length() - 6));
            tvOrderInfo.setText(orderId);

            // Table number
            tvTableNumber.setText("Bàn " + order.getTableNumber());

            // Time
            String timeStr = formatTime(order.getCreatedAt());
            tvOrderTime.setText(timeStr);

            // Waiting time
            long waitingMinutes = calculateWaitingTime(order.getCreatedAt());
            tvWaitingTime.setText(waitingMinutes + " phút");
            
            // Highlight if waiting too long (> 15 minutes)
            if (waitingMinutes > 15) {
                cardOrder.setStrokeColor(Color.parseColor("#F44336")); // Red
                cardOrder.setStrokeWidth(4);
            } else {
                cardOrder.setStrokeWidth(0);
            }

            // Setup items RecyclerView
            ChefOrderItemAdapter itemAdapter = new ChefOrderItemAdapter(order.getItems());
            recyclerViewItems.setAdapter(itemAdapter);

            // Action button based on status
            setupActionButton(order, listener);
        }

        private void setupActionButton(OrderResponse order, OnOrderStatusChangeListener listener) {
            String status = order.getStatus();
            
            switch (status) {
                case "pending":
                    btnAction.setText("Bắt đầu làm");
                    btnAction.setBackgroundColor(Color.parseColor("#2196F3")); // Blue
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onStatusChanged(order, "preparing");
                        }
                    });
                    break;
                    
                case "preparing":
                    btnAction.setText("Món đã xong");
                    btnAction.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onStatusChanged(order, "ready");
                        }
                    });
                    break;
                    
                case "ready":
                    btnAction.setText("Đã phục vụ");
                    btnAction.setBackgroundColor(Color.parseColor("#9E9E9E")); // Gray
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onStatusChanged(order, "served");
                        }
                    });
                    break;
                    
                default:
                    btnAction.setVisibility(View.GONE);
                    break;
            }
        }

        private String formatTime(String createdAt) {
            if (createdAt == null) return "";
            
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date date = isoFormat.parse(createdAt);
                
                if (date != null) {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return displayFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return createdAt;
        }

        private long calculateWaitingTime(String createdAt) {
            if (createdAt == null) return 0;
            
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date orderTime = isoFormat.parse(createdAt);
                
                if (orderTime != null) {
                    long diffInMillis = System.currentTimeMillis() - orderTime.getTime();
                    return diffInMillis / (1000 * 60); // Convert to minutes
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}