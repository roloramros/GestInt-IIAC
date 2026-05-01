package com.rfscu.iaacbd;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rfscu.iaacbd.utils.DrawerHelper;
import com.rfscu.iaacbd.utils.ThemeBaseActivity;

public class CalibrationActivity extends ThemeBaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Button btnLogoutDrawer;
    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calibration);

        setupEdgeToEdge();
        initViews();
        setupToolbar();
        setupDrawer();
        setupTabs();
    }

    private void setupEdgeToEdge() {
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View mainContent = findViewById(R.id.main_content);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return androidx.core.view.WindowInsetsCompat.CONSUMED;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnLogoutDrawer = findViewById(R.id.btnLogoutDrawer);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupDrawer() {
        DrawerHelper.setupNavigationListener(this, navView, drawerLayout);
        DrawerHelper.setupDrawer(this, navView, btnLogoutDrawer);
    }

    private void setupTabs() {
        CalibrationPagerAdapter adapter = new CalibrationPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(getString(R.string.menu_calibration_monthly));
                            break;
                        case 1:
                            tab.setText(getString(R.string.menu_calibration_update));
                            break;
                        case 2:
                            tab.setText(getString(R.string.menu_calibration_history));
                            break;
                    }
                }
        ).attach();
    }

    private static class CalibrationPagerAdapter extends FragmentStateAdapter {
        public CalibrationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new MonthlyPlansFragment();
                case 1: return new UpdateCertsFragment();
                case 2: return new CertsHistoryFragment();
                default: return new MonthlyPlansFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
