package com.unteleported.truecaller.screens.edit_profile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivity;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.model.User;
import com.unteleported.truecaller.screens.login.DialogAvatar;
import com.unteleported.truecaller.screens.login.LoginFragment;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.KeyboardManager;
import com.unteleported.truecaller.utils.SharedPreferencesSaver;
import com.unteleported.truecaller.utils.Toaster;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 6/13/16.
 */
public class EditProfileFragment extends android.support.v4.app.Fragment {

    public static final int AVATARDIALOG = 1;

    @Bind(R.id.changeAvatarButton) LinearLayout changeAvatarButton;
    @Bind(R.id.firstnameEditText) EditText firstNameEditText;
    @Bind(R.id.surnameEditText) EditText surnameEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.avatarImageView) CircleImageView avatarImageView;

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
            Picasso.with(getContext()).load(user.getAvatarPath()).into(avatarImageView);
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
        dialogAvatar.show(getActivity().getSupportFragmentManager(), "AVATARDIALOG");
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
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new LoginFragment()).commit();
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void updateSideBarUserInfo() {
        ((MainActivity)getActivity()).getUserInfo();
    }
}
