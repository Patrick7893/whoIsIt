package com.whois.whoiswho.screens.user_profile;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;
import com.whois.whoiswho.R;
import com.whois.whoiswho.activity.MainActivityMethods;
import com.whois.whoiswho.api.ApiInterface;
import com.whois.whoiswho.api.GetRecordByNumberResponse;
import com.whois.whoiswho.app.App;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.ContactNumber;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.model.Phone_Table;
import com.whois.whoiswho.screens.call_story.CallStoryFragment;
import com.whois.whoiswho.screens.conatctslist.ContactslistFragment;
import com.whois.whoiswho.utils.CountryManager;
import com.whois.whoiswho.utils.FontManager;
import com.whois.whoiswho.utils.PermissionManager;
import com.whois.whoiswho.utils.PhoneFormatter;
import com.whois.whoiswho.utils.ContactsManager;

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
    @Bind(R.id.actionBar) RelativeLayout actionBar;

    private UserProfilePresenter presenter;

    private Contact contact;
    private Phone phoneFromDb;
    private boolean isContactPresent, isBlocked, isLiked  = false;
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
        presenter.find(contact.getNumbers().get(0).getNumber().replaceAll("[^0-9+]", ""));
        if (!TextUtils.isEmpty(contact.getName()))
            titleTextView.setText(contact.getName());
        addressTextView.setText(CountryManager.getCountryNameFromIso(contact.getNumbers().get(0).getCountryIso()));
        UserPhonesAdapter userPhonesAdapter = new UserPhonesAdapter(getActivity(), contact.getNumbers(), new UserPhonesAdapter.OnPhoneClickListener() {
            @Override
            public void callCLick(ContactNumber item) {
                if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getNumber()));
                    startActivity(intent);
                }
                else {
                    PermissionManager.requestPermissions(getActivity(), Manifest.permission.CALL_PHONE);
                }

            }

            @Override
            public void messageClick(ContactNumber item) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", item.getNumber(), null)));
            }
        });
        phoneNumbersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        phoneNumbersRecyclerView.setAdapter(userPhonesAdapter);
        if (!TextUtils.isEmpty(contact.getAvatar()))
            Picasso.with(getContext()).load(contact.getAvatar()).into(avatarImageView);


        checkUserInContacts();
        checkUserIsBlocked();
        checkUserIsLiked();
    }

    public void displayUserInfo(GetRecordByNumberResponse findPhoneResponse) {
        titleTextView.setText(findPhoneResponse.getData().getName());
        if (!TextUtils.isEmpty(findPhoneResponse.getData().getAvatar().getUrl())) {
            Picasso.with(getContext()).load(ApiInterface.SERVICE_ENDPOINT + findPhoneResponse.getData().getAvatar().getUrl()).into(avatarImageView);
        }
        String addressText;
        if (TextUtils.isEmpty(findPhoneResponse.getData().getOperator()))
            addressText = CountryManager.getCountryNameFromIso(findPhoneResponse.getData().getCountryIso());
        else
            addressText = CountryManager.getCountryNameFromIso(findPhoneResponse.getData().getCountryIso()) + ", " + findPhoneResponse.getData().getOperator();
        addressTextView.setText(addressText);

    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void checkUserInContacts() {
        isContactPresent = ContactsManager.checkNumberInContacts(getActivity(), contact.getNumbers().get(0).getNumber());
        if (isContactPresent)
            saveToContactsButton.setText(getString(R.string.changeContact));
    }

    private void checkUserIsBlocked () {
        phoneFromDb = new Select().from(Phone.class).where(Phone_Table.number.is(PhoneFormatter.removeAllNonNumeric(contact.getNumbers().get(0).getNumber()))).querySingle();
        if (phoneFromDb != null) {
            if (!phoneFromDb.isBlocked()) {
                isBlocked = false;
            } else {
                isBlocked = true;
                blockButton.setText(getString(R.string.removeFromBlackList));
                actionBar.setBackgroundColor(getResources().getColor(R.color.missedCall));
            }
        }
    }

    private void checkUserIsLiked() {
        phoneFromDb = new Select().from(Phone.class).where(Phone_Table.number.is(PhoneFormatter.removeAllNonNumeric(contact.getNumbers().get(0).getNumber()))).querySingle();
        if (phoneFromDb != null) {
            if (!phoneFromDb.getIsLiked()) {
                isLiked = false;
            } else {
                isLiked = true;
                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorites_filled));
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
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right).add(R.id.flContent, callStoryFragment).addToBackStack(null).commit();

    }

    @OnClick(R.id.saveToContatcsButton)
    public void saveToContatcs() {
        if (isContactPresent) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact.getNumbers().get(0).getNumber()));
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
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, contact.getNumbers().get(0).getNumber());
            startActivity(intent);
        }
    }

    @OnClick(R.id.likeButton)
    public void setLike() {
        if (!isLiked) {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorites_filled));
            contact.setIsLiked(true);
            isLiked = true;
        }
        else {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
            contact.setIsLiked(false);
            isLiked = false;
        }
        setUserLike(isLiked);
    }

    @OnClick(R.id.blockButton)
    public void setBlockButton() {
        if (!isBlocked) {
            isBlocked = true;
            presenter.blockUser();
            blockButton.setText(getString(R.string.removeFromBlackList));
            actionBar.setBackgroundColor(getResources().getColor(R.color.missedCall));
        }
        else {
            isBlocked = false;
            presenter.unblockUser();
            blockButton.setText(getString(R.string.block));
            actionBar.setBackground(getResources().getDrawable(R.drawable.primary_color_gradient));
        }

    }

    private void setUserLike(boolean like) {
        if (phoneFromDb == null) {
            phoneFromDb = new Phone(contact.getNumbers().get(0).getNumber(), contact.getNumbers().get(0).getTypeOfNumber());
            phoneFromDb.setName(titleTextView.getText().toString());
        }
        else {
            phoneFromDb.setIsLiked(like);
        }
        phoneFromDb.save();
    }
}
