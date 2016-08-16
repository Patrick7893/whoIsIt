package com.whois.whoiswho.screens.findcontact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whois.whoiswho.R;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.utils.CountryManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/6/16.
 */
public class FindContactsAutocompliteAdapter extends RecyclerView.Adapter<FindContactsAutocompliteAdapter.ViewHolder> {

    private ArrayList<Contact> items;
    private int localContcatsSize;
    private Context context;
    private OnContactsClickListener onContactsClickListener;

    public interface OnContactsClickListener {
        void infoClick(Contact item);
    }

    public FindContactsAutocompliteAdapter(Context context, ArrayList<Contact> items, OnContactsClickListener onContactsClickListener) {
        this.items = items;
        this.context = context;
        this.onContactsClickListener = onContactsClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_search_list, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = items.get(position);
        holder.bind(context, contact, onContactsClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilter(List<Contact> contacts) {
        this.items = new ArrayList<>();
        this.items.addAll(contacts);
        localContcatsSize = items.size();
        notifyDataSetChanged();
    }

    public void setEmptyAdapter() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addContactsFromServer(List<Contact> contacts) {
        if (localContcatsSize > 0)
            items = new ArrayList<>(items.subList(0, localContcatsSize));
        else {
            items = new ArrayList<>();
        }
        items.addAll(contacts);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
        @BindView(R.id.nameTextView) TextView nameTextView;
        @BindView(R.id.countryTextView) TextView countryTextView;
        @BindView(R.id.photoImageView) ImageView photoImageView;
        @BindView(R.id.findContainer) RelativeLayout findContainer;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(Context ctx, final Contact contact, final OnContactsClickListener onContactsClickListener) {
            nameTextView.setText(contact.getName());
            Picasso.with(ctx).load(contact.getAvatar()).into(photoImageView);

            findContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactsClickListener.infoClick(contact);
                }
            });
            countryTextView.setText(CountryManager.getCountryNameFromIso(contact.getNumbers().get(0).getCountryIso()));

        }

    }
}
