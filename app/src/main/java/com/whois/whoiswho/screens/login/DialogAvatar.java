package com.whois.whoiswho.screens.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whois.whoiswho.R;
import com.whois.whoiswho.utils.PermissionManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by stasenkopavel on 5/10/16.
 */
public class DialogAvatar extends DialogFragment {

    public static int PICKFROMCAMERA = 0;
    public static int PICKFROMGALLERY = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_avatar, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.cameraBuuton)
    public void pickCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, PICKFROMCAMERA);
    }

    @OnClick(R.id.galleryButton)
    public void pickGallery() {
        PermissionManager.requestPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , PICKFROMGALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    Bitmap selectedImage = (Bitmap)data.getExtras().get("data");
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtra("Image", selectedImage));
                    dismiss();
                }

                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
                        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtra("Image", bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
                break;
        }
    }
}
