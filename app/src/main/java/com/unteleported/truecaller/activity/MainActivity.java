package com.unteleported.truecaller.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).commit();
        }
    }

    @Override
    public void removeFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void swithConatcsFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top, R.anim.slide_in_from_bottom, R.anim.slide_out_to_top).add(R.id.flContent, fragment).addToBackStack(null).commit();
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
