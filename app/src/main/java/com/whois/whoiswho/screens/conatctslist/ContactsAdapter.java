package com.whois.whoiswho.screens.conatctslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whois.whoiswho.R;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.view.FastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by stasenkopavel on 4/1/16.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements FastScroller.BubbleTextGetter {
    private ArrayList<Contact> contacts;
    private Context ctx;
    private OnContactsClickListener onContactsClickListener;

    public interface OnContactsClickListener {
        void callClick(Contact item);
        void infoClick(Contact item);
    }


    public ContactsAdapter(Context ctx, ArrayList<Contact> contacts, OnContactsClickListener onContactsClickListener) {
        this.contacts = contacts;
        this.ctx = ctx;
        this.onContactsClickListener = onContactsClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_contact_list, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(ctx, contact, onContactsClickListener);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }



    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(contacts.get(pos).getName().charAt(0)).toLowerCase();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.categoryTextView) TextView categoryTextView;
        @Bind(R.id.nameTextView) TextView nameTextView;
        @Bind(R.id.photoImageView) CircleImageView photoImageView;
        @Bind(R.id.nameContainer) RelativeLayout nameContainer;
        @Bind(R.id.informationImageView) ImageView informationImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(Context ctx, final Contact contact, final OnContactsClickListener onContactsClickListener) {
            nameTextView.setText(contact.getName());
            if (!TextUtils.isEmpty(contact.getTitle())) {
                categoryTextView.setVisibility(View.VISIBLE);
                categoryTextView.setText(contact.getTitle());
            }
            else {
                categoryTextView.setVisibility(View.GONE);
            }
            Picasso.with(ctx).load(contact.getAvatar()).into(photoImageView);

            nameContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactsClickListener.callClick(contact);
                }
            });
            informationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactsClickListener.infoClick(contact);
                }
            });
        }


    }

    public void setFilter(List<Contact> contacts) {
        this.contacts = new ArrayList<>();
        this.contacts.addAll(contacts);
        notifyDataSetChanged();
    }
}
