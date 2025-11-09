package vn.edu.fpt.taptoeat.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import vn.edu.fpt.taptoeat.fragments.MenuItemsFragment;
import vn.edu.fpt.taptoeat.models.Category;

public class MenuPagerAdapter extends FragmentStateAdapter {

    private final List<Category> categories;

    public MenuPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Category category = categories.get(position);
        return MenuItemsFragment.newInstance(category.get_id());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
