package vn.edu.fpt.taptoeat.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.models.MenuItem;
import vn.edu.fpt.taptoeat.utils.CartManager;

/**
 * Dialog for adding menu item to cart
 * Allows user to select quantity and add notes
 */
public class AddToCartDialog extends Dialog {

    private MenuItem menuItem;
    private int quantity = 1;
    private OnItemAddedListener listener;

    private ImageView ivMenuItemImage;
    private TextView tvMenuItemName;
    private TextView tvMenuItemDescription;
    private TextView tvMenuItemPrice;
    private TextView tvQuantity;
    private TextView tvTotalPrice;
    private TextInputEditText etNotes;
    private MaterialButton btnDecrease;
    private MaterialButton btnIncrease;
    private MaterialButton btnCancel;
    private MaterialButton btnAddToCart;

    public interface OnItemAddedListener {
        void onItemAdded();
    }

    public AddToCartDialog(@NonNull Context context, MenuItem menuItem, OnItemAddedListener listener) {
        super(context);
        this.menuItem = menuItem;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_to_cart);

        initViews();
        setupData();
        setupListeners();
        updateTotalPrice();
    }

    private void initViews() {
        ivMenuItemImage = findViewById(R.id.ivMenuItemImage);
        tvMenuItemName = findViewById(R.id.tvMenuItemName);
        tvMenuItemDescription = findViewById(R.id.tvMenuItemDescription);
        tvMenuItemPrice = findViewById(R.id.tvMenuItemPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        etNotes = findViewById(R.id.etNotes);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnCancel = findViewById(R.id.btnCancel);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void setupData() {
        if (menuItem == null) return;

        // Load image
        if (menuItem.getImage() != null && !menuItem.getImage().isEmpty()) {
            Glide.with(getContext())
                    .load(menuItem.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivMenuItemImage);
        }

        // Set text data
        tvMenuItemName.setText(menuItem.getName());
        tvMenuItemDescription.setText(menuItem.getDescription());
        tvMenuItemPrice.setText(menuItem.getFormattedPrice());
        tvQuantity.setText(String.valueOf(quantity));
    }

    private void setupListeners() {
        // Decrease quantity
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        // Increase quantity
        btnIncrease.setOnClickListener(v -> {
            if (quantity < 99) { // Max 99 items
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        // Cancel
        btnCancel.setOnClickListener(v -> dismiss());

        // Add to cart
        btnAddToCart.setOnClickListener(v -> {
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";
            
            // Add to cart
            CartManager.getInstance().addItem(menuItem, quantity, notes);
            
            // Notify listener
            if (listener != null) {
                listener.onItemAdded();
            }
            
            dismiss();
        });
    }

    private void updateTotalPrice() {
        if (menuItem == null) return;
        
        double total = menuItem.getPrice() * quantity;
        String formattedPrice = String.format("%,.0f đ", total);
        tvTotalPrice.setText(formattedPrice);
    }
}
