package com.example.mycovidtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CovidListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCovidRecyclerView;
    private CovidAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSubtitleVisible =
                    savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        View view =
                inflater.inflate(R.layout.fragment_covid_list,
                        container, false);
        mCovidRecyclerView = (RecyclerView) view
                .findViewById(R.id.covid_recycler_view);
        mCovidRecyclerView.setLayoutManager(new
                LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,
                                    MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_covid_list,
                menu);

        MenuItem subtitleItem =
                menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_covid:
                Covid covid = new Covid();
                CovidLab.get(getActivity()).addCovid(covid);
                Intent intent = CovidPagerActivity
                        .newIntent(getActivity(),
                                covid.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CovidLab covidLab = CovidLab.get(getActivity());
        int covidCount = covidLab.getCovids().size();
        String subtitle =
                getString(R.string.subtitle_format, covidCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity)
                getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CovidLab covidLab =
                CovidLab.get(getActivity());
        List<Covid> covids = covidLab.getCovids();
        if (mAdapter == null) {
            mAdapter = new CovidAdapter(covids);
            mCovidRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCovids(covids);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    private class CovidHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {
        private Covid mCovid;
        private TextView mTitleTextView;
        private TextView mDateTextView;

        public CovidHolder(LayoutInflater inflater,
                           ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_covid,
                    parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.covid_title);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.covid_date);
        }

        @Override
        public void onClick(View view) {
            Intent intent =
                    CovidPagerActivity.newIntent(getActivity(),
                            mCovid.getId());
            startActivity(intent);
        }

        public void bind(Covid covid) {
            mCovid = covid;
            mTitleTextView.setText(mCovid.getTitle());
            mDateTextView.setText(mCovid.getDate().toString());
        }
    }

    private class CovidAdapter extends
            RecyclerView.Adapter<CovidHolder> {
        private List<Covid> mCrimes;

        public CovidAdapter(List<Covid> covids) {
            mCrimes = covids;
        }

        @Override
        public CovidHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater =
                    LayoutInflater.from(getActivity());
            return new CovidHolder(layoutInflater,
                    parent);
        }

        @Override
        public void onBindViewHolder(CovidHolder
                                             holder, int position) {
            Covid covid = mCrimes.get(position);
            holder.bind(covid);

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
        public void setCovids(List<Covid> covids) {
            mCrimes = covids;
        }
    }
}