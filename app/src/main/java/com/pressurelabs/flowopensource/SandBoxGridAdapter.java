package com.pressurelabs.flowopensource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 */
public class SandBoxGridAdapter extends BaseDynamicGridAdapter {
    // references to our images
    private List<FlowElement> elementList;
    private Context mContext;


    public SandBoxGridAdapter(Context c, LinkedList<FlowElement> res, int colNum) {
        super(c,res,colNum);
        /* Must give resource List to the super class to handle animation and reorder */
        /* Resource list must be stable (ie. reset) */

        mContext = c;
        elementList = res;
    }

    public int getCount() {
        return elementList.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.sandbox_gridview_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.build();
        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;

        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.sandbox_gview_item_img);
        }

        void build() {
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.flag_black_48dp);
            imageView.setPadding(2,2,2,2);
        }

    }


    /**
     * More involved "notifyDataSetChanged()".
     * Updates this adapter's list and
     * Updates the super()'s List so that new items can be dragged around grid
     *
     * @param updatedList
     */
    public void notifyDataSetUpdated(List<FlowElement> updatedList) {
        this.elementList = updatedList;
        this.set(updatedList);
        this.notifyDataSetChanged();
    }


    @Override
    public String toString() {
        return this.elementList.toString();
    }


}
