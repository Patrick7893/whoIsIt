package com.unteleported.truecaller.screens.calls;

import android.content.Context;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.model.Call;
import com.unteleported.truecaller.utils.DateConverter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stasenkopavel on 4/7/16.
 */
public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> {
    private ArrayList<Call> calls;
    private Context ctx;
    private int position;
    private final onCallsClickListener listener;

    public interface onCallsClickListener {
        void callCLick(Call item);
        void infoClick(Call item);
    }

    public CallsAdapter(Context ctx, ArrayList<Call> calls, onCallsClickListener onItemClickListener) {
        this.calls = calls;
        this.ctx = ctx;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_call_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Call call = calls.get(position);
        holder.bind(call, ctx, listener);
    }

    public void notify(List<Call> calls) {
        this.calls = new ArrayList<>();
        this.calls.addAll(calls);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return calls.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.dateTextView) TextView dateTextView;
        @Bind(R.id.nameTextView) TextView nameTextView;
        @Bind(R.id.numberTypeTextView) TextView numberTypeTextView;
        @Bind(R.id.nameContainer) RelativeLayout nameContainer;
        @Bind(R.id.informationImageView) ImageView infoImageView;

        public ViewHolder(View v) {
            super(v);
           // this.listener= listener;
            ButterKnife.bind(this, v);
        }

        public void bind(final Call call, Context ctx, final onCallsClickListener listener) {
            String numberOfCalls;
            if (call.getCallsNumber() == 0) {
                numberOfCalls = "";
            }
            else {
                numberOfCalls = "(" + String.valueOf(call.getCallsNumber()+1) + ")";
            }
            if (call.getName() == null) {
                nameTextView.setText(call.getNumber() + "  " + numberOfCalls);
            }
            else {
                nameTextView.setText(call.getName() + "  " + numberOfCalls);
            }
            dateTextView.setText(DateConverter.dateToString(ctx, call.getDate()));
            if (call.getType() == CallLog.Calls.MISSED_TYPE) {
                nameTextView.setTextColor(ctx.getResources().getColor(R.color.missedCall));
                numberTypeTextView.setText(call.getTypeOfNumber());
            }
            else if (call.getType() == CallLog.Calls.OUTGOING_TYPE){
                nameTextView.setTextColor(ctx.getResources().getColor(android.R.color.black));
                numberTypeTextView.setText(ctx.getString(R.string.arrow) + call.getTypeOfNumber());
            }
            else {
                nameTextView.setTextColor(ctx.getResources().getColor(android.R.color.black));
                numberTypeTextView.setText(call.getTypeOfNumber());
            }
            nameContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.callCLick(call);
                }
            });
            infoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.infoClick(call);
                }
            });
        }


    }
}
