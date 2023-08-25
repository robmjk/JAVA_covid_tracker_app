package com.example.mycovidtracker;

import androidx.fragment.app.Fragment;

public class CovidListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CovidListFragment();
    }
}