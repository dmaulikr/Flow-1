package com.pressurelabs.flow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;


import java.util.LinkedList;
import java.util.List;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 */
public class ImageAdapter extends BaseAdapter {
    // references to our images
    private List<FlowElement> elementList;
    private Context mContext;

    public ImageAdapter(Context c, LinkedList<FlowElement> res) {
        mContext = c;
        elementList = res;
    }

    public int getCount() {
        return elementList.size();
    }

    public FlowElement getItem(int position) {
        return null;
    }

    //return the row id of the item
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckableLayout l;
        ImageView imageView;
//        TextView textName;
//        TextView time;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
//            textName = new TextView(mContext);
//            time = new TextView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                /* sets the height and width for the View—this ensures that,
                no matter the size of the drawable, each image is resized and
                cropped to fit in these dimensions, as appropriate.
                 */
            imageView.setScaleType(ImageView.ScaleType.CENTER);
                //Crops to Center if possible
            imageView.setPadding(2, 2, 2, 2);
//            textName.setText(elementList.get(position).getElementName());
//            time.setText(elementList.get(position).getFormattedTime());

            l = new CheckableLayout(mContext);
            l.setLayoutParams(new GridView.LayoutParams(
                    GridView.LayoutParams.WRAP_CONTENT,
                    GridView.LayoutParams.WRAP_CONTENT));
            l.addView(imageView);
//            l.addView(textName);
//            l.addView(time);

        } else {
            l = (CheckableLayout) convertView;
            imageView = (ImageView) l.getChildAt(0);
                /* If the View passed !=null, than the ImageView is
                initialized with the recycled View object
                */
        }
        imageView.setImageResource(R.drawable.flag_black_48dp);
        return l;
    }

    public void update(List<FlowElement> updatedList) {
        this.elementList = updatedList;
        this.notifyDataSetChanged();
    }


    class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        @SuppressWarnings("deprecation")
        public void setChecked(boolean checked) {
            mChecked = checked;
            setBackgroundDrawable(checked ? getResources().getDrawable(android.R.color.holo_blue_bright) : null);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }

    @Override
    public String toString() {
        return this.elementList.toString();
    }
}
