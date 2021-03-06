package com.whois.whoiswho.screens.edit_profile;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivity;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.User;
import com.whois.whoiswho.screens.login.DialogAvatar;
import com.whois.whoiswho.screens.login.LoginFragment;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.KeyboardManager;
import com.whois.whoiswho.utils.SharedPreferencesSaver;
import com.whois.whoiswho.utils.Toaster;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 6/13/16.
 */
public class EditProfileFragment extends Fragment {

    public static final int AVATARDIALOG = 1;

    @BindView(R.id.changeAvatarButton) LinearLayout changeAvatarButton;
    @BindView(R.id.firstnameEditText) EditText firstNameEditText;
    @BindView(R.id.surnameEditText) EditText surnameEditText;
    @BindView(R.id.emailEditText) EditText emailEditText;
    @BindView(R.id.avatarImageView) CircleImageView avatarImageView;

    private Bitmap avatar;

    private EditProfilePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_edit_profile, container, false);
        presenter = new EditProfilePresenter(this);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        ((MainActivityMethods)getActivity()).disableDrawer();
        try {
            presenter.getUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }



    public void displayUserInfo(User user) throws IOException {
        firstNameEditText.setText(user.getFirstname());
        surnameEditText.setText(user.getSurname());
        emailEditText.setText(user.getEmail());
        if (!TextUtils.isEmpty(user.getAvatarPath())) {
            Picasso.with(App.getContext()).load(user.getAvatarPath()).into(avatarImageView);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AVATARDIALOG:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    avatar = (Bitmap) bundle.get("Image");
                    avatarImageView.setImageBitmap(avatar);
                    break;
                }
        }
    }

    @OnClick(R.id.changeAvatarButton)
    public void pickImage() {
        DialogAvatar dialogAvatar = new DialogAvatar();
        dialogAvatar.setTargetFragment(this, 1);
        dialogAvatar.show(getActivity().getFragmentManager(), "AVATARDIALOG");
    }

    @OnClick(R.id.backButton)
    public void back() {
        KeyboardManager.hideKeyboard(getActivity());
        ((MainActivity)getActivity()).enableDrawer();
        ((MainActivityMethods)getActivity()).back();
    }

    @OnClick(R.id.doneButton)
    public void okButton() {
        if (!TextUtils.isEmpty(firstNameEditText.getText())&&!TextUtils.isEmpty(surnameEditText.getText())) {
            try {
                presenter.updateUser(presenter.getUser().getServerId(), firstNameEditText.getText().toString(), surnameEditText.getText().toString(), presenter.getUser().getNumber(), emailEditText.getText().toString(), presenter.convertBitmapToFile(avatar));
            } catch (IOException e) {
                e.getMessage();
            }
           // back();
        }
        else
            Toaster.toast(getActivity(), R.string.pleaseInputName);
    }

    @OnClick(R.id.exitButton)
    public void exit() {
        SharedPreferencesSaver.get().saveToken("");
        presenter.getUser().delete();
        getActivity().getFragmentManager().beginTransaction().replace(R.id.flContent, new LoginFragment()).commit();
        getActivity().getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void updateSideBarUserInfo() {
        ((MainActivity)getActivity()).getUserInfo();
    }
}
