package vn.edu.fpt.taptoeat.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.api.ApiService.OrderItemResponse;

public class ChefOrderItemAdapter extends RecyclerView.Adapter<ChefOrderItemAdapter.ViewHolder> {
    private static final String TAG = "ChefOrderItemAdapter";

    public interface OnItemActionListener {
        void onItemStatusChange(int itemIndex, String newStatus);
    }

    private List<OrderItemResponse> items;
    private OnItemActionListener listener;

    public ChefOrderItemAdapter(List<OrderItemResponse> items) {
        this.items = items;
    }

    public ChefOrderItemAdapter(List<OrderItemResponse> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
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
        holder.bind(item, position, listener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvQuantity;
        private TextView tvNotes;
        private TextView tvItemStatus;
        private MaterialButton btnItemAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            btnItemAction = itemView.findViewById(R.id.btnItemAction);
        }

        public void bind(OrderItemResponse item, int position, OnItemActionListener listener) {
            Log.d(TAG, "Binding item at position " + position + ": " + item.getName() + ", status: " + item.getStatus());
            
            tvItemName.setText(item.getName());
            tvQuantity.setText("x" + item.getQuantity());
            
            if (item.getNotes() != null && !item.getNotes().isEmpty()) {
                tvNotes.setText("📝 " + item.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Display and handle item status
            String status = item.getStatus() != null ? item.getStatus() : "pending";
            Log.d(TAG, "Item status: " + status + ", listener: " + (listener != null ? "present" : "null"));
            updateStatusDisplay(status);
            setupActionButton(status, position, listener);
        }

        private void updateStatusDisplay(String status) {
            if (tvItemStatus == null) return;
            
            switch (status.toLowerCase()) {
                case "pending":
                    tvItemStatus.setText("⏳ Bắt đầu");
                    tvItemStatus.setTextColor(Color.parseColor("#F57C00")); // Orange
                    tvItemStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    break;
                case "preparing":
                    tvItemStatus.setText("👨‍🍳 Đang làm");
                    tvItemStatus.setTextColor(Color.parseColor("#1976D2")); // Blue
                    tvItemStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                    break;
                case "ready":
                    tvItemStatus.setText("✅ Ra món");
                    tvItemStatus.setTextColor(Color.parseColor("#388E3C")); // Green
                    tvItemStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    break;
                default:
                    tvItemStatus.setText(status);
                    tvItemStatus.setTextColor(Color.parseColor("#757575"));
                    tvItemStatus.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    break;
            }
        }

        private void setupActionButton(String status, int position, OnItemActionListener listener) {
            if (btnItemAction == null) {
                Log.w(TAG, "btnItemAction is null!");
                return;
            }
            
            Log.d(TAG, "Setting up action button for position " + position + ", status: " + status);
            
            switch (status.toLowerCase()) {
                case "pending":
                    btnItemAction.setText("Bắt đầu");
                    btnItemAction.setBackgroundColor(Color.parseColor("#2196F3")); // Blue
                    btnItemAction.setVisibility(View.VISIBLE);
                    btnItemAction.setEnabled(true); // Ensure button is enabled
                    btnItemAction.setOnClickListener(v -> {
                        Log.d(TAG, ">>> BUTTON CLICKED! Position: " + position + ", changing to preparing");
                        
                        // Disable button immediately to prevent double-clicks
                        btnItemAction.setEnabled(false);
                        
                        // Post to handler to avoid ANR
                        v.post(() -> {
                            try {
                                if (listener != null) {
                                    Log.d(TAG, "Calling listener.onItemStatusChange");
                                    listener.onItemStatusChange(position, "preparing");
                                } else {
                                    Log.e(TAG, "Listener is NULL! Cannot update status");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Exception in button click handler", e);
                            } finally {
                                // Re-enable after a delay
                                v.postDelayed(() -> btnItemAction.setEnabled(true), 1000);
                            }
                        });
                    });
                    Log.d(TAG, "Pending button setup complete, visible: " + (btnItemAction.getVisibility() == View.VISIBLE));
                    break;
                    
                case "preparing":
                    btnItemAction.setText("Hoàn thành");
                    btnItemAction.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                    btnItemAction.setVisibility(View.VISIBLE);
                    btnItemAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onItemStatusChange(position, "ready");
                        }
                    });
                    break;
                    
                case "ready":
                    btnItemAction.setVisibility(View.GONE);
                    break;
                    
                default:
                    btnItemAction.setVisibility(View.GONE);
                    break;
            }
        }
    }
}