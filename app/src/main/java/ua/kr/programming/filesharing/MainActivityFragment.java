package ua.kr.programming.filesharing;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.kr.programming.filesharing.models.User;
import ua.kr.programming.filesharing.models.UserAdapter;


public class MainActivityFragment extends Fragment {

	@InjectView(R.id.lvUser)
	ListView lvUser;

	private UserAdapter mUserAdapter;

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, view);

		mUserAdapter = new UserAdapter(MainService.users);
		lvUser.setAdapter(mUserAdapter);

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		adapterUpdateStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		adapterUpdateStop();
	}


	private TimerTask mTask;
	private Timer mTimer;

	private void adapterUpdateStart() {
		updateAdapter();
		mTask = new TimerTask() {
			@Override
			public void run() {
				updateAdapter();
			}
		};
		mTimer = new Timer();
		mTimer.schedule(mTask, 0, 1000);
	}

	public void adapterUpdateStop() {
		if (mTimer != null) {
			mTimer.purge();
			mTimer.cancel();
		}
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
	}

	private void updateAdapter() {
		// Update counters
		int scrollApp = lvUser.getScrollY();
		mUserAdapter.setUsers(MainService.users);
		lvUser.setScrollY(scrollApp);
	}

}
