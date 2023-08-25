package com.example.mycovidtracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CovidFragment extends Fragment {
    private static final String ARG_COVID_ID = "covid_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;
    private Covid mCovid;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static CovidFragment newInstance(UUID covidId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_COVID_ID, covidId);
        CovidFragment fragment = new CovidFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID covidId = (UUID)
                getArguments().getSerializable(ARG_COVID_ID);
        mCovid =
                CovidLab.get(getActivity()).getCovid(covidId);
        mPhotoFile =
                CovidLab.get(getActivity()).getPhotoFile(mCovid);
    }
    @Override
    public void onPause() {
        super.onPause();
        CovidLab.get(getActivity())
                .updateCovid(mCovid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v =
                inflater.inflate(R.layout.fragment_covid, container,
                        false);
        mTitleField = (EditText)
                v.findViewById(R.id.covid_title);
        mTitleField.setText(mCovid.getTitle());
        mTitleField.addTextChangedListener(new
                                                   TextWatcher() {
                                                       @Override
                                                       public void beforeTextChanged(
                                                               CharSequence s, int start, int count,
                                                               int after) {
// This space intentionally left blank
                                                       }
                                                       @Override
                                                       public void onTextChanged(
                                                               CharSequence s, int start, int
                                                               before, int count) {
                                                           mCovid.setTitle(s.toString());
                                                       }
                                                       @Override
                                                       public void afterTextChanged(Editable s)
                                                       {
// This one too
                                                       }
                                                   });

        mDateButton = (Button)
                v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new
                                               View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       FragmentManager manager =
                                                               getFragmentManager();
                                                       DatePickerFragment dialog =
                                                               DatePickerFragment
                                                                       .newInstance(mCovid.getDate());
                                                       dialog.setTargetFragment(CovidFragment.this,
                                                               REQUEST_DATE);
                                                       dialog.show(manager, DIALOG_DATE);
                                                   }
                                               });




        mSolvedCheckBox =
                (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCovid.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new
                                                           CompoundButton.OnCheckedChangeListener() {
                                                               @Override
                                                               public void
                                                               onCheckedChanged(CompoundButton buttonView,
                                                                                boolean isChecked) {
                                                                   mCovid.setSolved(isChecked);
                                                               }
                                                           });
        mReportButton = (Button)
                v.findViewById(R.id.covid_report);
        mReportButton.setOnClickListener(new
                                                 View.OnClickListener() {
                                                     public void onClick(View v) {
                                                         Intent i = new
                                                                 Intent(Intent.ACTION_SEND);
                                                         i.setType("text/plain");
                                                         i.putExtra(Intent.EXTRA_TEXT,
                                                                 getCovidReport());
                                                         i.putExtra(Intent.EXTRA_SUBJECT,
                                                                 getString(R.string.crime_report_subject));
                                                         i = Intent.createChooser(i,
                                                                 getString(R.string.send_report));
                                                         startActivity(i);
                                                     }
                                                 });
        final Intent pickContact = new
                Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)
                v.findViewById(R.id.covid_case);
        mSuspectButton.setOnClickListener(new
                                                  View.OnClickListener() {
                                                      public void onClick(View v) {
                                                          startActivityForResult(pickContact,
                                                                  REQUEST_CONTACT);
                                                      }
                                                  });
        if (mCovid.getSuspect() != null) {
            mSuspectButton.setText(mCovid.getSuspect());
        }

        PackageManager packageManager =
                getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) ==
                null) {
            mSuspectButton.setEnabled(false);
        }
        mPhotoButton = (ImageButton)
                v.findViewById(R.id.covid_camera);
        final Intent captureImage = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new
                                                View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Uri uri =
                                                                FileProvider.getUriForFile(getActivity(),
                                                                        "com.example.mycovidtracker.fileprovider",
                                                                        mPhotoFile);
                                                        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                                        List<ResolveInfo> cameraActivities =
                                                                getActivity()
                                                                        .getPackageManager().queryIntentActivities(captureImage,
                                                                        PackageManager.MATCH_DEFAULT_ONLY);
                                                        for (ResolveInfo activity :
                                                                cameraActivities) {
                                                            getActivity().grantUriPermission(activity.activityInfo.packageName,
                                                                    uri,
                                                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                                        }
                                                        startActivityForResult(captureImage,
                                                                REQUEST_PHOTO);
                                                    }
                                                });
        mPhotoView = (ImageView)
                v.findViewById(R.id.covid_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCovid.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data
                != null) {
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri =
                    FileProvider.getUriForFile(getActivity(),
                            "com.example.mycovidtracker.fileprovider",
                            mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
            Uri contactUri = data.getData();
// Specify which fields you want your query to return
// values for
                    String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
// Perform your query - the contactUri is like a "where"
// clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null,
                            null, null);
            try {
// Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
// Pull out the first column of the first row of data -
// that is your suspect's name
                        c.moveToFirst();
                String suspect = c.getString(0);
                mCovid.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mCovid.getDate().toString());
    }
    private String getCovidReport() {
        String solvedString = null;
        if (mCovid.isSolved()) {
            solvedString =
                    getString(R.string.crime_report_solved);
        } else {
            solvedString =
                    getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString =
                DateFormat.format(dateFormat,
                        mCovid.getDate()).toString();
        String suspect = mCovid.getSuspect();
        if (suspect == null) {
            suspect =
                    getString(R.string.crime_report_no_suspect);
        } else {
            suspect =
                    getString(R.string.crime_report_suspect, suspect);
        }
        String report =
                getString(R.string.crime_report,
                        mCovid.getTitle(), dateString,
                        solvedString, suspect);
        return report;
    }
    private void updatePhotoView() {
        if (mPhotoFile == null ||
                !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap =
                    PictureUtils.getScaledBitmap(
                            mPhotoFile.getPath(),
                            getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
