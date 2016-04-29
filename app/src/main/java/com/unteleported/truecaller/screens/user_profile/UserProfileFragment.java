package com.unteleported.truecaller.screens.user_profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.screens.call_story.CallStoryFragment;
import com.unteleported.truecaller.screens.conatctslist.ContactslistFragment;
import com.unteleported.truecaller.utils.FontManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 4/18/16.
 */
public class UserProfileFragment extends Fragment {

    @Bind(R.id.titleTextView) TextView titleTextView;
    @Bind(R.id.phoneNumbersRecyclerView) RecyclerView phoneNumbersRecyclerView;
    @Bind(R.id.likeButton) ImageView likeButton;
    @Bind(R.id.avatarImageView) CircleImageView avatarImageView;
    @Bind(R.id.saveToContatcsButton) TextView saveToContactsButton;

    private Contact contact;
    public static final String CONTACTINFO = "CONTACTINFOTOSTORY";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_user_profile, container, false);
        FontManager.overrideFonts(view);
        ButterKnife.bind(this, view);
        initiallizeScreen();
        return view;

    }

    public void initiallizeScreen() {
        Bundle bundle = getArguments();
        String contatcString = bundle.getString(ContactslistFragment.CONTACTINFO);
        Gson gson = new Gson();
        contact = gson.fromJson(contatcString, Contact.class);
        titleTextView.setText(contact.getName());
        UserPhonesAdapter userPhonesAdapter = new UserPhonesAdapter(getActivity(), contact.getPhones(), new UserPhonesAdapter.OnPhoneClickListener() {
            @Override
            public void callCLick(Phone item) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumber()));
                startActivity(intent);
            }

            @Override
            public void messageClick(Phone item) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", item.getNumber(), null)));
            }
        });
        phoneNumbersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        phoneNumbersRecyclerView.setAdapter(userPhonesAdapter);
        if (!TextUtils.isEmpty(contact.getPhoto()))
            Picasso.with(getContext()).load(contact.getPhoto()).into(avatarImageView);
        if (!TextUtils.isEmpty(contact.getName())) {
            saveToContactsButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.backButton)
    public void back() {
        ((MainActivityMethods)getActivity()).back();
    }



    @OnClick(R.id.callStoryButton)
    public void getCallStory() {
        Gson gson = new Gson();
        String contactString = gson.toJson(contact);
        Bundle bundle = new Bundle();
        bundle.putString(CONTACTINFO, contactString);
        CallStoryFragment callStoryFragment = new CallStoryFragment();
        callStoryFragment.setArguments(bundle);
        ((MainActivityMethods)getActivity()).switchFragment(callStoryFragment);

    }

    @OnClick(R.id.saveToContatcsButton)
    public void saveToContatcs() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhones().get(0).getNumber());
        startActivity(intent);
    }

    @OnClick(R.id.likeButton)
    public void setLike() {
        if (!contact.isLiked()) {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorites_filled));
            contact.setIsLiked(true);
        }
        else {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
            contact.setIsLiked(false);
        }
    }
}
