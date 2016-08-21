package com.pressurelabs.flow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-08-14
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Flow> flowList;
    private onCardClickListener cardClickCallback;

    public RecyclerViewAdapter(Context mContext, ArrayList<Flow> flowList) {
        this.mContext = mContext;
        this.flowList = flowList;
        this.cardClickCallback = (onCardClickListener) mContext;
    }

    /**
     * Represents each CardView present in the RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View card;
        public Flow flow;
            // The Flow this card represents
        public int position;
            // The position of this card in the recycler view
        public TextView name, elements, timeEstimate;
        public EditText rename;
        public ViewSwitcher switcher;

        public ViewHolder(View view) {
            super(view);
            card = view;
            name = (TextView) view.findViewById(R.id.item_flow_name);
            elements = (TextView) view.findViewById(R.id.item_element_count);
            timeEstimate = (TextView) view.findViewById(R.id.item_total_time);
            rename = (EditText) view.findViewById(R.id.item_flow_rename);
            switcher = (ViewSwitcher) view.findViewById(R.id.rename_switcher);
            /* Set up views for set operations */
        }

        public void prepare(Flow inFlow, int inPosition) {
            /* since data is not avaliable until onBindViewHolder assign Flow and position here */
            this.flow = inFlow;
            this.position = inPosition;
            setOnClicks();
        }

        private void setOnClicks() {
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /* Passes Flow but passes the memory address of the childFlowElements
                     instead of the actual object containing the
                      */
                    cardClickCallback.onCardClick(flow);
                }
            });

            card.setLongClickable(true);

            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // View is the child view provided from AdapterView parent
                    cardClickCallback.onCardLongClick(flow, position, card);
                    return true;
                }
            });


        }



    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flow_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
            Flow flow = flowList.get(position);
            holder.name.setText(String.valueOf(flow.getName()));
            // String.valueOf() otherwise Resources$NotFoundException thrown
            holder.elements.setText(String.valueOf(flow.getElementCount()));

            holder.timeEstimate.setText(String.valueOf(flow.getFormattedTime()));
            holder.prepare(flow, position);


    }

    @Override
    public int getItemCount() {
        return flowList.size();
    }


    public interface onCardClickListener {
        void onCardClick(Flow clickedFlow);
        void onCardLongClick(Flow longClickedFlow, int cardPosition, View cardViewClicked);
    }
}