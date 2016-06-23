package com.unteleported.truecaller.screens.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.Toaster;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 5/16/16.
 */
public class NewUserFragment extends Fragment {

    public static final int AVATARDIALOG = 1;
    public static final String ID = "id";
    public static final String PHONE = "phone";
    public static final String COUNTRY = "country";

    private NewUserPresener presenter;

    @Bind(R.id.firstnameEditText) EditText firstNameEditText;
    @Bind(R.id.surnameEditText) EditText surnameEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.avatarImageView) CircleImageView avatarImageView;

    private Bitmap avatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_new_user, container, false);
        ButterKnife.bind(this, view);
        FontManager.overrideFonts(view);
        presenter = new NewUserPresener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AVATARDIALOG:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    avatar = (Bitmap) bundle.get("Image");
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    avatar.compress(Bitmap.CompressFormat.PNG, 80, out);
                    avatar = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    avatarImageView.setImageBitmap(avatar);
                    break;
                }
        }
    }

    @OnClick(R.id.avatarImageView)
    public void pickImage() {
        DialogAvatar dialogAvatar = new DialogAvatar();
        dialogAvatar.setTargetFragment(this, 1);
        dialogAvatar.show(getActivity().getSupportFragmentManager(), "AVATARDIALOG");
    }

    @OnClick(R.id.okButton)
    public void okButton() {
        if (!TextUtils.isEmpty(firstNameEditText.getText())&&!TextUtils.isEmpty(surnameEditText.getText())) {
            try {
                presenter.createUser(this.getArguments().getString(PHONE), this.getArguments().getString(COUNTRY), firstNameEditText.getText().toString(), surnameEditText.getText().toString(), this.getArguments().getString(PHONE), emailEditText.getText().toString(), presenter.convertBitmapToFile(avatar));
            } catch (IOException e) {
                e.getMessage();
            }
        }
        else
            Toaster.toast(getActivity(), R.string.pleaseInputName);
    }
}
