package com.whois.whoiswho.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.android.device.DeviceName;
import com.squareup.picasso.Picasso;
import com.whois.whoiswho.R;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.edit_profile.EditProfileFragment;
import com.whois.whoiswho.screens.login.LoginFragment;
import com.whois.whoiswho.screens.mainscreen.TabFragment;
import com.whois.whoiswho.utils.KeyboardManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends Activity implements MainActivityMethods, NavigationView.OnNavigationItemSelectedListener{

    public final static int MY_PERMISSIONS_REQUEST = 100;

    private static MainActivityPresenter presenter;

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;


    private TextView nameTextView, phoneTextView;
    private CircleImageView avatarImageView;
    private ImageView editProfileButton;


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
        editProfileButton = (ImageView)headerView.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            getFragmentManager().beginTransaction().setCustomAnimations(0,0,0, R.animator.slide_out_to_bottom).add(R.id.flContent, new EditProfileFragment()).addToBackStack(null).commit();
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.setNavigationItemSelectedListener(this);
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

    public void initActivity() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (!TextUtils.isEmpty(SharedPreferencesSaver.get().getToken())) {
            switchFragment(new TabFragment(), false);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            switchFragment(new LoginFragment());
            //view.switchFragment(new TabFragment(), false);
        }
    }

    public void displayUserInfo(User user) {
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
        if (addToFragmentList)
            getFragmentManager().beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
        else
            getFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void removeFragment(Fragment fragment) {
        getFragmentManager().popBackStack();
    }

    @Override
    public void swithConatcsFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_bottom, R.animator.slide_out_to_top, R.animator.slide_in_from_top, R.animator.slide_out_to_bottom).add(R.id.flContent, fragment).addToBackStack(null).commit();
    }


    @Override
    public void back() {
        onBackPressed();
    }


    @Override
    public void enableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void disableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    public void getUserInfo() {
        presenter.getUserInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if  (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initActivity();
            }
        }
    }

    @Override
    public void openDrawer() {
        KeyboardManager.hideKeyboard(this);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.invite_sidebar) {
            // Handle the camera action
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareText) + Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName()));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else if (id == R.id.help_sidebar) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setData(Uri.parse("whoiswhoapp@gmail.com"));
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "whoiswhoapp@gmail.com" });
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "WhoIsWhoApp support");
            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                Toaster.toast(this, R.string.youhaveNoGmail);
            }
        }
        else if (id == R.id.rateapp_sidebar) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


}
