package android.bignerdranch.mycheckins;

import android.Manifest;
import android.app.Activity;
import android.bignerdranch.criminalintent.R;
import android.bignerdranch.mycheckins.database.CheckInBaseHelper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CheckInFragment extends Fragment {
    private static final String ARG_CRIME_ID = "checkIn_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private CheckIn mCheckIn;
    private File mPhotoFile;

    private EditText mTitleField;
    private EditText mPlaceField;
    private EditText mDetailsField;
    private TextView mLocationLabel;
    private Button mLocationButton;
    private Button mDateButton;
    private Button mShareButton;
    private Button mDeleteButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private GoogleApiClient mClient;

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();

        CheckInLab.get(getActivity()).updateCheckIn(mCheckIn);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_checkin, container, false);

        mTitleField = (EditText) v.findViewById(R.id.checkin_title);
        mTitleField.setText(mCheckIn.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckIn.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // also blank
            }
        });

        mPlaceField = (EditText) v.findViewById(R.id.checkin_place);
        mPlaceField.setText(mCheckIn.getPlace());
        mPlaceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckIn.setPlace(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // also blank
            }
        });

        mDetailsField = (EditText) v.findViewById(R.id.checkin_details);
        mDetailsField.setText(mCheckIn.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckIn.setDetails(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // also blank
            }
        });

        mLocationLabel = (TextView) v.findViewById(R.id.location_label);
        mLocationButton = (Button) v.findViewById(R.id.location_button);
        mLocationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.newIntent(getContext(), mCheckIn.getLatitude(), mCheckIn.getLongitude());
                startActivity(intent);
            }
        });

        mShareButton = (Button) v.findViewById(R.id.share_button);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCheckInReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.checkin_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        if (mCheckIn.getSuspect() != null) {
            mShareButton.setText(mCheckIn.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();

        mDateButton = (Button) v.findViewById(R.id.checkin_date);

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCheckIn.getDate());

                dialog.setTargetFragment(CheckInFragment.this, REQUEST_DATE);

                dialog.show(manager, DIALOG_DATE);
            }
        });

        mDeleteButton = (Button) v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckInLab.get(getActivity()).deleteCheckIn(mCheckIn.getId());
                getActivity().finish();
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.checkin_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.checkin_photo);
        updatePhotoView();

        return v;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCheckIn.setDate(date);

            updateDate();

        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCheckIn.setSuspect(suspect);
                mShareButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    public static CheckInFragment newInstance(UUID checkInId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, checkInId);

        CheckInFragment fragment = new CheckInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID checkInId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCheckIn = CheckInLab.get(getActivity()).getCheckIn(checkInId);
        mPhotoFile = CheckInLab.get(getActivity()).getPhotoFile(mCheckIn);

        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                LocationRequest request = LocationRequest.create();
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setNumUpdates(1);
                request.setInterval(0);

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                LocationServices.getFusedLocationProviderClient(getActivity())
                        .requestLocationUpdates(request, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult result) {
                                super.onLocationResult(result);
                                Location location = result.getLastLocation();
                                updateLocation(location);
                            }
                        }, null);
                }

            @Override
            public void onConnectionSuspended(int i) {
            }
        })
        .build();
    }

    private void updateDate() {
        mDateButton.setText(mCheckIn.getDate().toString());
    }

    private void updateLocation(Location location) {
        mCheckIn.setLatitude(location.getLatitude());
        mCheckIn.setLongitude(location.getLongitude());

        String locationUpdate = getString(R.string.location_label_text, mCheckIn.getLatitude(), mCheckIn.getLongitude());
        mLocationLabel.setText(locationUpdate);
    }

    private String getCheckInReport() {
        String introduction = "Hello";

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCheckIn.getDate()).toString();

        String report = getString(R.string.checkin_report, introduction, mCheckIn.getPlace(), dateString, mCheckIn.getDetails());

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}


