package com.unteleported.truecaller.screens.user_profile;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
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

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.unteleported.truecaller.R;
import com.unteleported.truecaller.activity.MainActivityMethods;
import com.unteleported.truecaller.api.ApiInterface;
import com.unteleported.truecaller.api.FindPhoneResponse;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.model.Phone_Table;
import com.unteleported.truecaller.screens.call_story.CallStoryFragment;
import com.unteleported.truecaller.screens.conatctslist.ContactslistFragment;
import com.unteleported.truecaller.utils.CountryManager;
import com.unteleported.truecaller.utils.FontManager;
import com.unteleported.truecaller.utils.UserContactsManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 4/18/16.
 */
public class UserProfileFragment extends Fragment {

    @Bind(R.id.userInfoContainer) View userInfoContainer;
    @Bind(R.id.userActionsContainer) View userActionsContainer;
    @Bind(R.id.titleTextView) TextView titleTextView;
    @Bind(R.id.phoneNumbersRecyclerView) RecyclerView phoneNumbersRecyclerView;
    @Bind(R.id.likeButton) ImageView likeButton;
    @Bind(R.id.avatarImageView) CircleImageView avatarImageView;
    @Bind(R.id.saveToContatcsButton) TextView saveToContactsButton;
    @Bind(R.id.divider) View divider;
    @Bind(R.id.blockButton) TextView blockButton;
    @Bind(R.id.addressTextView) TextView addressTextView;
    @Bind(R.id.progressBar) CircularProgressView progressBar;

    private UserProfilePresenter presenter;

    private Contact contact;
    private boolean isContactPresent, isBlocked  = false;
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
        presenter = new UserProfilePresenter(this, contact);
        presenter.find(contact.getPhones().get(0).getNumber().replaceAll("[^0-9+]", ""));
        if (!TextUtils.isEmpty(contact.getName()))
            titleTextView.setText(contact.getName());
        addressTextView.setText(CountryManager.getCountryNameFromIso(contact.getPhones().get(0).getCountryIso()));
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
        if (!TextUtils.isEmpty(contact.getAvatar()))
            Picasso.with(getContext()).load(contact.getAvatar()).into(avatarImageView);


        checkUserInContacts();
        checkUserIsBlocked();
    }

    public void displayUserInfo(FindPhoneResponse findPhoneResponse) {
        titleTextView.setText(findPhoneResponse.getData().get(0).getName());
        if (!TextUtils.isEmpty(findPhoneResponse.getData().get(0).getAvatar().getUrl())) {
            Picasso.with(getContext()).load(ApiInterface.SERVICE_ENDPOINT + findPhoneResponse.getData().get(0).getAvatar().getUrl()).into(avatarImageView);
        }
        addressTextView.setText(CountryManager.getCountryNameFromIso(findPhoneResponse.getData().get(0).getCountryIso()));

    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void checkUserInContacts() {
        isContactPresent = UserContactsManager.checkNumberInContacts(getActivity(), contact.getPhones().get(0).getNumber());
        if (isContactPresent)
            saveToContactsButton.setText(getString(R.string.changeContact));
    }

    private void checkUserIsBlocked () {
        Phone phoneFromDb = new Select().from(Phone.class).where(Phone_Table.number.is(contact.getPhones().get(0).getNumber())).querySingle();
        if (phoneFromDb != null) {
            if (!phoneFromDb.isBlocked()) {
                isBlocked = false;
            } else {
                isBlocked = true;
                blockButton.setText(getString(R.string.removeFromBlackList));
            }
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
        if (isContactPresent) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getPhones().get(0).getNumber()));
            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID};
            Cursor cur = getActivity().getContentResolver().query(uri, mPhoneNumberProjection, null, null, null);
            long idContact = 0;
            if(cur!=null) {
                while (cur.moveToNext()) {
                    idContact = cur.getLong(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                }
            }
            cur.close();
            Intent i = new Intent(Intent.ACTION_EDIT);
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idContact);
            i.setData(contactUri);
            startActivity(i);

        }
        else {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhones().get(0).getNumber());
            startActivity(intent);
        }
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

    @OnClick(R.id.blockButton)
    public void setBlockButton() {
        if (!isBlocked) {
            isBlocked = true;
            presenter.blockUser();
            blockButton.setText(getString(R.string.removeFromBlackList));
        }
        else {
            isBlocked = false;
            presenter.unblockUser();
            blockButton.setText(getString(R.string.block));
        }
    }
}
