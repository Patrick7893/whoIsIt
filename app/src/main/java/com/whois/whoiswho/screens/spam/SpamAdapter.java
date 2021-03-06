package com.whois.whoiswho.screens.spam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whois.whoiswho.R;
import com.whois.whoiswho.model.Phone;
import com.whois.whoiswho.utils.FontManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 5/12/16.
 */
public class SpamAdapter extends RecyclerView.Adapter<SpamAdapter.ViewHolder> {

    public static int numberOfUserSpam;

    private ArrayList<Phone> phones;
    private Context context;
    private ViewHolder holder;
    private OnSpamClickListener listener;

    public interface OnSpamClickListener {
        void spamClick(Phone item);
    }

    public SpamAdapter(ArrayList<Phone> phones, OnSpamClickListener onSpamClickLisner) {
        this.phones = phones;
        this.numberOfUserSpam = phones.size();
        this.listener = onSpamClickLisner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_spam_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        holder = viewHolder;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Phone phone = phones.get(position);
        holder.bind(phone, context, position, listener);
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public void addSpammersFromServer(ArrayList<Phone> spammers) {
        this.phones.addAll(spammers);
        numberOfUserSpam = this.phones.size();
        notifyDataSetChanged();
    }

    public void addGlobalSpammers(ArrayList<Phone> globalSpammers) {
        this.phones.addAll(globalSpammers);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameTextView) TextView nameTextView;
        @BindView(R.id.phoneTextView) TextView phoneTextView;
        @BindView(R.id.titleTextView) TextView titleTextView;
        @BindView(R.id.spamContainer) LinearLayout spamContainer;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            FontManager.overrideFonts(v);
        }

        public void bind(final Phone phone, Context ctx, int position, final OnSpamClickListener listener) {
            phoneTextView.setText(phone.getNumber());
            if (!TextUtils.isEmpty(phone.getName())) {
                nameTextView.setText(phone.getName());
            }
            else {
                nameTextView.setText(ctx.getString(R.string.unknown_user));
            }
            if (position == numberOfUserSpam || position == 0) {
                if (position != 0)
                    titleTextView.setText(ctx.getString(R.string.spammers));
                else {
                    if (numberOfUserSpam != 0)
                        titleTextView.setText(ctx.getString(R.string.myBlackList));
                    else
                        titleTextView.setText(R.string.spammers);
                }
                titleTextView.setVisibility(View.VISIBLE);
            }
            else {
                titleTextView.setVisibility(View.GONE);
            }

            spamContainer.setOnClickListener(v -> listener.spamClick(phone));

        }
    }
}
