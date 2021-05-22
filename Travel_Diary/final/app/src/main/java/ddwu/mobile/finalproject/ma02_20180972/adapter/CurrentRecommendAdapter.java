package ddwu.mobile.finalproject.ma02_20180972.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.activities.AddActivity;
import ddwu.mobile.finalproject.ma02_20180972.activities.CurrentLocationMapActivity;
import ddwu.mobile.finalproject.ma02_20180972.activities.CurrentLocationRecommendActivity;
import ddwu.mobile.finalproject.ma02_20180972.activities.LocationActivity;
import ddwu.mobile.finalproject.ma02_20180972.activities.RecommendActivity;
import ddwu.mobile.finalproject.ma02_20180972.dto.RecommendDto;
import ddwu.mobile.finalproject.ma02_20180972.manager.ImageFileManager;
import ddwu.mobile.finalproject.ma02_20180972.manager.TravelNetworkManager;

public class CurrentRecommendAdapter extends BaseAdapter {

    public static final String TAG = "CurrentRecommendAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<RecommendDto> list;
    private TravelNetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;

    public CurrentRecommendAdapter(Context context, int resource, ArrayList<RecommendDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        imageFileManager = new ImageFileManager(context);
        networkManager = new TravelNetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).get_id();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.recommend_title);
            viewHolder.tvAddress = view.findViewById(R.id.recommend_address);
            viewHolder.tvImg = view.findViewById(R.id.recommend_img);
            viewHolder.add = view.findViewById(R.id.add_recommend);
            viewHolder.map = view.findViewById(R.id.confirm_map);
            view.setTag(viewHolder);

            //리사이클러 뷰는 viewHolder 사전에 이미 가지고 있음.
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        RecommendDto dto = list.get(i);

        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvAddress.setText(dto.getAddress());

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x = dto.getX();
                String y = dto.getY();
                String t = dto.getTitle();

                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("title", t);

                ((CurrentLocationRecommendActivity)context).startActivity(intent);
            }
        });

        viewHolder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x1 = dto.getX();
                String y1 = dto.getY();
                String t = dto.getTitle();

                double x = Double.valueOf(x1);
                double y = Double.valueOf(y1);

                Intent intent = new Intent(context, LocationActivity.class);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("title", t);

                ((CurrentLocationRecommendActivity)context).startActivity(intent);
            }
        });

        if (dto.getImg() == null){
            viewHolder.tvImg.setImageResource(R.mipmap.ic_launcher);
            return view;
        }

        Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImg()); //파일 이름

        if (savedBitmap != null){
            viewHolder.tvImg.setImageBitmap(savedBitmap);
            Log.d(TAG, "Image loading from file");
        } else{ //1방식에서는 필요 X
            viewHolder.tvImg.setImageResource(R.mipmap.ic_launcher);
            new GetImageAsyncTask(viewHolder).execute(dto.getImg());
            Log.d(TAG, "Image loading form network");
        }


        return view;
    }

    public void setList(ArrayList<RecommendDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvAddress = null;
        public ImageView tvImg = null;

        public Button add = null;
        public Button map = null;
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ViewHolder holder) { // 현재 작업 중인 viewholder
            viewHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;

            result = networkManager.downloadImage(imageAddress);

            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null){
                viewHolder.tvImg.setImageBitmap(bitmap);

                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }

    }

}

