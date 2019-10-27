package android.bignerdranch.mycheckins;

import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.UUID;

public class CheckInActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.checkIn_id";
    private static final int REQUEST_ERROR = 0;

    public static Intent newIntent(Context packageContext, UUID checkInId) {
        Intent intent = new Intent(packageContext, CheckInActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, checkInId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID checkInId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CheckInFragment.newInstance(checkInId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this,
                    errorCode, REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });

            errorDialog.show();
        }
    }
}
