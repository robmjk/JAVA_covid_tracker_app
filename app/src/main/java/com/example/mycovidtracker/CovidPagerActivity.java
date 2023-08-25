package com.example.mycovidtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;
import java.util.UUID;

public class CovidPagerActivity  extends AppCompatActivity {
    private static final String EXTRA_COVID_ID = "com.example.mycovidtracker.covid_id";
    private ViewPager mViewPager;
    private List<Covid> mCrimes;

    public static Intent newIntent(Context
                                           packageContext, UUID covidId) {
        Intent intent = new Intent(packageContext,
                CovidPagerActivity.class);
        intent.putExtra(EXTRA_COVID_ID, covidId);
        return intent;
    }

    /** @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR,
                    new DialogInterface.OnCancelListener() {
                @Override
                        public void onCancel(DialogInterface dialog) {
                    finish();
                }
                    });
            errorDialog.show();
        }
    } **/

    @Override
    protected void onCreate(Bundle
                                    savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_pager);

        UUID covidId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_COVID_ID);

        mViewPager = (ViewPager)
                findViewById(R.id.covid_view_pager);
        mCrimes = CovidLab.get(this).getCovids();
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        mViewPager.setAdapter(new
                                      FragmentStatePagerAdapter(fragmentManager) {
                                          @Override
                                          public Fragment getItem(int position) {
                                              Covid covid = mCrimes.get(position);
                                              return
                                                      CovidFragment.newInstance(covid.getId());
                                          }
                                          @Override
                                          public int getCount() {
                                              return mCrimes.size();
                                          }
                                      });
        for (int i = 0; i < mCrimes.size(); i++) {
            if
            (mCrimes.get(i).getId().equals(covidId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
