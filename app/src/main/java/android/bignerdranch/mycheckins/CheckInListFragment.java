package android.bignerdranch.mycheckins;

import android.bignerdranch.criminalintent.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CheckInListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCheckInRecyclerView;
    private CheckInAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_list, container, false);

        mCheckInRecyclerView = (RecyclerView) view.findViewById(R.id.checkin_recycler_view);
        mCheckInRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_checkin_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_checkin:
                CheckIn checkIn = new CheckIn();

                CheckInLab.get(getActivity()).addCheckIn(checkIn);
                Intent intent = CheckInActivity.newIntent(getActivity(), checkIn.getId());
                startActivity(intent);
                return true;

            case R.id.help:
                Intent helpActivity = new Intent(getActivity(), WebViewActivity.class);
                startActivity(helpActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CheckInLab checkInLab = CheckInLab.get(getActivity());
        int checkInCount = checkInLab.getCheckIns().size();
        String subtitle = getString(R.string.subtitle_format, checkInCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CheckInLab checkInLab = CheckInLab.get(getActivity());
        List<CheckIn> checkIns = checkInLab.getCheckIns();

        if (mAdapter == null) {
            mAdapter = new CheckInAdapter(checkIns);
            mCheckInRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
            mAdapter.setCheckIns(checkIns);
        }

        updateSubtitle();
    }

    private class CheckInHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckIn mCheckIn;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mPlaceTextView;

        public CheckInHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_checkin, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.checkin_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.checkin_date);
            mPlaceTextView = (TextView) itemView.findViewById(R.id.checkin_place);
        }

        public void bind(CheckIn checkIn) {
            mCheckIn = checkIn;
            mTitleTextView.setText(mCheckIn.getTitle());
            mDateTextView.setText(mCheckIn.getDate().toString());
            mPlaceTextView.setText(mCheckIn.getPlace());
        }

        @Override
        public void onClick(View view) {
            Intent intent = CheckInActivity.newIntent(getActivity(), mCheckIn.getId());
            startActivity(intent);
        }
    }

    private class CheckInAdapter extends RecyclerView.Adapter<CheckInHolder> {
        private List<CheckIn> mCheckIns;
        public CheckInAdapter(List<CheckIn> checkIns) {
            mCheckIns = checkIns;
        }

        @NonNull
        @Override
        public CheckInHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CheckInHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CheckInHolder holder, int position) {
            CheckIn checkIn = mCheckIns.get(position);
            holder.bind(checkIn);
        }

        @Override
        public int getItemCount() {
            return mCheckIns.size();
        }

        public void setCheckIns(List<CheckIn> checkIns) {
            mCheckIns = checkIns;
    }
    }
}
