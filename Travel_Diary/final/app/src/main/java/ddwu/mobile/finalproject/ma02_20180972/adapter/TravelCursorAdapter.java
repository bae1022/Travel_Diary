package ddwu.mobile.finalproject.ma02_20180972.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.database.TravelDBHelper;

public class TravelCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    int layout;

    public TravelCursorAdapter(Context context, int layout, Cursor c){
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);

        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder.tvTravelDate == null){
            holder.tvTravelDate = view.findViewById(R.id.mainact_date);
            holder.tvTravelPlace = view.findViewById(R.id.mainact_address);
            holder.tvTravelStars = view.findViewById(R.id.mainact_rating);

        }

        holder.tvTravelDate.setText(cursor.getString(cursor.getColumnIndex(TravelDBHelper.COL_DATE)));
        holder.tvTravelPlace.setText(cursor.getString(cursor.getColumnIndex(TravelDBHelper.COL_PLACE)));
        holder.tvTravelStars.setRating(cursor.getFloat(cursor.getColumnIndex(TravelDBHelper.COL_STAR)));


        holder.tvTravelStars.setFocusable(false);
    }

    static class ViewHolder {
        public ViewHolder(){
            tvTravelDate = null;
            tvTravelPlace = null;
            tvTravelStars = null;
        }

        TextView tvTravelDate;
        TextView tvTravelPlace;
        RatingBar tvTravelStars;
    }
}
