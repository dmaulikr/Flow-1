package com.pressurelabs.flow;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 */
public class ImageAdapter extends BaseAdapter {
    // references to our images
    private LinkedList<FlowElement> elementList;
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
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                /* sets the height and width for the View—this ensures that,
                no matter the size of the drawable, each image is resized and
                cropped to fit in these dimensions, as appropriate.
                 */
            imageView.setScaleType(ImageView.ScaleType.CENTER);
                //Crops to Center if possible
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
                /* If the View passed !=null, than the ImageView is
                initialized with the recycled View object
                */
        }
        imageView.setImageResource(R.drawable.flag_black_48dp);
        return imageView;
    }

    /**
     * Removes all selected tasks, while recording the positions removed and actual flow elements
     * Then displays a snackbar and reverses the process if desired
     *
     * @param selection the items selected in the view
     * @param content the content from the Activity
     * @param gridV the view containing the images
     * @param f the current flow of the Activity
     * @param c the context of the activity
     */
//    public void onItemsRemoved(final SparseBooleanArray selection,
//                               final LinkedList<FlowElement> content,
//                               View gridV,
//                              final Flow f,
//                               final Context c) {
//
//        final LinkedList<FlowElement> reference = new LinkedList<>(content);
//        final LinkedList<FlowElement> newChildElements = new LinkedList<>();
//            // Tracks flow elements removed from the content array
//        final ArrayList<Integer> positionRemoved = new ArrayList<>();
//
//        try {
//            for (int i=0; i<selection.size();i++) {
//                Log.d("VALUE ", "Value of I is " + String.valueOf(i));
//                if (selection.get(i) != true) {
//                        // The item was not selected
//
//                    newChildElements.add(content.get(i));
//                    positionRemoved.add(i);
//                    Log.d("TAG", "Added Object to feRemoved: " + content.get(i));
//                    Log.d("TAG", "Added Position " + String.valueOf(i) + " to positionRemoved. \n" + "index in positionRemoved is " + positionRemoved.indexOf(i));
//
//                }
//            }
//            Log.d("TAG", "FLOW BEFORE RESET " + f.getChildElements().toString());
//            f.setChildElements(newChildElements);
//                //Sets the new Elements retained
//            Log.d("TAG", "FLOW AFTER RESET " + f.getChildElements().toString());
//            elementList=newChildElements;
//            new AppDataManager(c).overwrite(f.getUuid(), f);
//
//        } catch (Exception e) {
//            Log.e("EXCEPTION THROWN: ", e.getMessage());
//        }
//
//
//            Snackbar bar = Snackbar.make(gridV, "Flow Tasks were deleted", Snackbar.LENGTH_SHORT)
//                    .setAction("UNDO", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            f.setChildElements(content);
//                            elementList=reference;
//                            new AppDataManager(c).overwrite(f.getUuid(),f);
//                        }
//                    });
//
//
//            bar.show();
//            //TODO On one item selected fails to delete (null selection)
//            //TODO Selecting side by side items seems to delete wrong elements (disconnect between position in flow and content?)
//            // TODO FIx this, SortedList Vs Other idk..
//
//        }
}
