package uuke.xinfu.wxphoto;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liupeng on 16/6/21.
 */
public class VideoGridAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private List<Video> mVideos = new ArrayList<>();
    private Video mSelectdedVideo;

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public VideoGridAdapter(Context context, int itemSize){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItemSize = itemSize;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
    }

    public Video getSelected() {
        return mSelectdedVideo;
    }


    public void select(Video video) {
        mSelectdedVideo = video;
        notifyDataSetChanged();
    }

//    private Image getImageByPath(String path){
//        if(mImages != null && mImages.size() > 0){
//            for(Image image : mImages){
//                if(image.path.equalsIgnoreCase(path)){
//                    return image;
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 设置数据集
     * @param videos
     */
    public void setData(List<Video> videos) {
        mSelectdedVideo = null;

        if(videos != null && videos.size() > 0){
            mVideos = videos;
        }else{
            mVideos.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 重置每个Column的Size
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if(mItemSize == columnWidth){
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Video getItem(int i) {
        return mVideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolde holde;
        if(view == null){
            view = mInflater.inflate(R.layout.item_video, viewGroup, false);
            holde = new ViewHolde(view);
        }else{
            holde = (ViewHolde) view.getTag();
            if(holde == null){
                view = mInflater.inflate(R.layout.item_video, viewGroup, false);
                holde = new ViewHolde(view);
            }
        }
        if(holde != null) {
            holde.bindData(i, getItem(i));
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if(lp.height != mItemSize){
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }


    class ViewHolde {
        ImageView image;
//        View mask;
        ImageView indicator;
        TextView textDuration;
        int index;

        ViewHolde(View view){
            image = (ImageView) view.findViewById(R.id.videocover);
            indicator = (ImageView) view.findViewById(R.id.checkmark_video);
            textDuration = (TextView) view.findViewById(R.id.videoDuration);
            indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItem(index) == mSelectdedVideo) {
                        mSelectdedVideo = null;
                    } else {
                        mSelectdedVideo = getItem(index);
                    }
                    notifyDataSetChanged();
                }
            });
//            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        String getDuration(long duration) {
            long seconds = (duration/1000)%60;
            long minutes = (duration/1000)/60;
            return String.format("%02d:%02d", minutes, seconds);
        }

        void bindData(int i, final Video data){
            index = i;
            if(data == null) return;
            indicator.setVisibility(View.VISIBLE);
            if (mSelectdedVideo == data) {
                indicator.setImageResource(R.mipmap.btn_selected);
            } else {
                indicator.setImageResource(R.mipmap.btn_unselected);
            }

            if(mItemSize > 0) {
                File imageFile = new File(data.path);
                Glide.with(mContext)
                        .load(imageFile)
                        .error(R.mipmap.default_error)
                        .override(mItemSize, mItemSize)
                        .centerCrop()
                        .into(image);
                textDuration.setText(getDuration(data.duration));
            }
        }
    }

}
