package com.whois.whoiswho.screens.user_profile;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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
import com.whois.whoiswho.utils.Toaster;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 4/18/16.
 */
public class UserProfileFragment extends Fragment {

    @BindView(R.id.userInfoContainer) View userInfoContainer;
    @BindView(R.id.userActionsContainer) View userActionsContainer;
    @BindView(R.id.titleTextView) TextView titleTextView;
    @BindView(R.id.phoneNumbersRecyclerView) RecyclerView phoneNumbersRecyclerView;
    @BindView(R.id.likeButton) ImageView likeButton;
    @BindView(R.id.avatarImageView) CircleImageView avatarImageView;
    @BindView(R.id.saveToContatcsButton) TextView saveToContactsButton;
    @BindView(R.id.divider) View divider;
    @BindView(R.id.blockButton) TextView blockButton;
    @BindView(R.id.addressTextView) TextView addressTextView;
    @BindView(R.id.progressBar) CircularProgressView progressBar;
    @BindView(R.id.actionBar) RelativeLayout actionBar;

    private UserProfilePresenter presenter;

    private Contact contact;
    private Phone phoneFromDb;
    private int isContactPresent;
    private boolean isBlocked = false;
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
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        presenter.find(contact.getNumbers().get(0).getNumber().replaceAll("[^0-9+]", ""), tm.getSimCountryIso());
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
            Picasso.with(getActivity().getApplicationContext()).load(contact.getAvatar()).into(avatarImageView);


        if (!TextUtils.isEmpty(contact.getNumbers().get(0).getNumber())) {
            checkPhoneInContacts();
            checkPhoneIsBlocked();
        }
    }

    public void displayUserInfo(GetRecordByNumberResponse findPhoneResponse) {
        if (isContactPresent == ContactsManager.DONT_PRESENT_IN_CONTACTS)
            titleTextView.setText(findPhoneResponse.getData().getName());
        if (!TextUtils.isEmpty(findPhoneResponse.getData().getAvatar().getUrl()))
            Picasso.with(getActivity().getApplicationContext()).load(ApiInterface.SERVICE_ENDPOINT + findPhoneResponse.getData().getAvatar().getUrl()).into(avatarImageView);

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

    private void checkPhoneInContacts() {
        isContactPresent = ContactsManager.checkNumberInContacts(getActivity(), contact.getNumbers().get(0).getNumber());
        if (isContactPresent != ContactsManager.DONT_PRESENT_IN_CONTACTS)
            saveToContactsButton.setText(getString(R.string.changeContact));
        if (isContactPresent == ContactsManager.PRESENT_IN_CONTACTS_STARRED)
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorites_filled));
    }

    private void checkPhoneIsBlocked() {
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
        getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right).add(R.id.flContent, callStoryFragment).addToBackStack(null).commit();

    }

    @OnClick(R.id.saveToContatcsButton)
    public void saveToContatcs() {
        if (isContactPresent != ContactsManager.DONT_PRESENT_IN_CONTACTS) {
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
        if (isContactPresent == ContactsManager.PRESENT_IN_CONTACTS_NOT_STARRED) {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorites_filled));
            ContactsManager.changeContactStarred(App.getContext(), String.valueOf(contact.getNumbers().get(0).getNumber()), ContactsManager.PRESENT_IN_CONTACTS_STARRED);
            Toaster.toast(getActivity(), R.string.contactAddedToStarred);
        }
        else if (isContactPresent == ContactsManager.PRESENT_IN_CONTACTS_STARRED){
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
            ContactsManager.changeContactStarred(App.getContext(), String.valueOf(contact.getNumbers().get(0).getNumber()), ContactsManager.PRESENT_IN_CONTACTS_NOT_STARRED);
            Toaster.toast(getActivity(), R.string.contactRemovedFromStarred);
        }
        else {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE).putExtra(ContactsContract.Intents.Insert.PHONE, contact.getNumbers().get(0).getNumber());
            startActivity(intent);
        }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                actionBar.setBackground(getResources().getDrawable(R.drawable.primary_color_gradient));
            }
        }

    }

}
