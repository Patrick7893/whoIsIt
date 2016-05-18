package com.unteleported.truecaller.screens.spam;

import android.content.Context;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.model.Call;
import com.unteleported.truecaller.model.Phone;
import com.unteleported.truecaller.utils.DateConverter;
import com.unteleported.truecaller.utils.FontManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 5/12/16.
 */
public class SpamAdapter extends RecyclerView.Adapter<SpamAdapter.ViewHolder> {

    private ArrayList<Phone> phones;
    private Context context;
    private ViewHolder holder;
    public static int numberOfUserSpam;

    public SpamAdapter(ArrayList<Phone> phones) {
        this.phones = phones;
        this.numberOfUserSpam = phones.size();
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
        holder.bind(phone, context, position);
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public void addSpammersFromServer(ArrayList<Phone> spammers) {
        this.phones.addAll(spammers);
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.nameTextView) TextView nameTextView;
        @Bind(R.id.phoneTextView) TextView phoneTextView;
        @Bind(R.id.titleTextView) TextView titleTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            FontManager.overrideFonts(v);
        }

        public void bind(final Phone phone, Context ctx, int position) {
            phoneTextView.setText(phone.getNumber());
            if (!TextUtils.isEmpty(phone.getName())) {
                nameTextView.setText(phone.getName());
            }
            else {
                nameTextView.setText(ctx.getString(R.string.unknown_user));
            }
            if (position == 0) {
                titleTextView.setVisibility(View.VISIBLE);
            }
            if (position == numberOfUserSpam) {
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(ctx.getString(R.string.spammers));
            }

        }
    }
}
