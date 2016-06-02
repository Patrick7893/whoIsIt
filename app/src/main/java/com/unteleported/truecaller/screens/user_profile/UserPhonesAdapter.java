package com.unteleported.truecaller.screens.user_profile;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.ContactNumber;
import com.unteleported.truecaller.model.Phone;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/18/16.
 */
public class UserPhonesAdapter extends RecyclerView.Adapter<UserPhonesAdapter.ViewHolder> {

    private ArrayList<ContactNumber> phones;
    private Context ctx;
    private final OnPhoneClickListener listener;

    public interface OnPhoneClickListener {
        void callCLick(ContactNumber item);
        void messageClick(ContactNumber item);
    }

    public UserPhonesAdapter(Context ctx, ArrayList<ContactNumber> phones, OnPhoneClickListener onItemClickListener) {
        this.phones = phones;
        this.ctx = ctx;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_phone_numbers_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactNumber phone = phones.get(position);
        holder.bind(ctx, phone, listener);
    }


    @Override
    public int getItemCount() {
        return phones.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.numberTextView) TextView numberTextView;
        @Bind(R.id.numberTypeTextView) TextView numberTypeTextView;
        @Bind(R.id.callContainer) RelativeLayout callContainer;
        @Bind(R.id.messageImageView) ImageView messageImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(Context ctx, final ContactNumber phone, final OnPhoneClickListener onPhoneClickListener) {
            numberTextView.setText(phone.getNumber());
            phone.setTypeDescriptionFromType(ctx, phone.getTypeOfNumber());
            numberTypeTextView.setText(phone.getTypeDescription());

            callContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPhoneClickListener.callCLick(phone);
                }
            });
            messageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPhoneClickListener.messageClick(phone);
                }
            });
        }
    }
}

