package com.unteleported.truecaller.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.dbflow.data.Blob;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.LoadContactsRequest;
import com.unteleported.truecaller.api.RegistrationResponse;
import com.unteleported.truecaller.app.App;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.calls.CallFragment;
import com.unteleported.truecaller.screens.findcontact.FindContactsFragment;
import com.unteleported.truecaller.screens.login.LoginFragment;
import com.unteleported.truecaller.screens.mainscreen.TabFragment;
import com.unteleported.truecaller.screens.spam.SpamFragment;
import com.unteleported.truecaller.screens.splash.SplashFragment;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.Toaster;
import com.unteleported.truecaller.utils.UserContactsManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MainActivityMethods{

    private List<Fragment> fragmentsList = new ArrayList<Fragment>();
    public final static int MY_PERMISSIONS_REQUEST = 100;

    private static MainActivityPresenter presenter;

    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.nav_view) NavigationView navigationView;


    TextView nameTextView, phoneTextView;
    CircleImageView avatarImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainActivityPresenter(this);
        presenter.requestPermissions();
        View headerView = navigationView.getHeaderView(0);
        nameTextView = (TextView)headerView.findViewById(R.id.nameTextView);
        phoneTextView = (TextView)headerView.findViewById(R.id.phoneTextView);
        avatarImageView = (CircleImageView)headerView.findViewById(R.id.avatarImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayUserInfo(User user) throws IOException {
        nameTextView.setText(user.getFirstname() + " " + user.getSurname());
        phoneTextView.setText(PhoneNumberUtils.formatNumber(user.getNumber(), user.getCountyIso()));
        if (!TextUtils.isEmpty(user.getAvatarPath())) {
            Picasso.with(getApplicationContext()).load(user.getAvatarPath()).into(avatarImageView);
        }

    }


    @Override
    public void switchFragment(Fragment fragment) {
        switchFragment(fragment, true);
    }

    @Override
    public void switchFragment(Fragment fragment, boolean addToFragmentList) {
        if (addToFragmentList) {
            getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
        }else {
            getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).commit();
        }
    }

    @Override
    public void removeFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void swithConatcsFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).addToBackStack(null).commit();
    }


    @Override
    public void back() {
        onBackPressed();
    }

    @Override
    public void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            Toaster.toast(this, R.string.checkConnection);
        }
        else {
            Toaster.toast(this, R.string.serverError);
        }
    }

    @Override
    public void enableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void setUserInfo() {
        try {
            presenter.setUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

    }

    @Override
    public void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }



}
