package uuke.xinfu.wxphoto;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VideoPickerActivity extends AppCompatActivity{

    public static final String TAG = VideoPickerActivity.class.getName();

    private Context mCxt;

    // 文件夹数据
    private ArrayList<VideoFolder> mResultFolder = new ArrayList<>();


    // 不同loader定义
    private static final int LOADER_ALL_VIDEO = 0;

    private GridView mGridView;

    private VideoGridAdapter mVideoAdapter;
    private VideoFolderAdapter mFolderAdapter;

    private boolean hasFolderGened = false;
    private View mPopupAnchorView;
    private ListPopupWindow mFolderPopupWindow;
    private Button btnAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopicker);

        initViews();

        getSupportLoaderManager().initLoader(LOADER_ALL_VIDEO, null, mLoaderCallback);

        // 是否显示照相机
        mVideoAdapter = new VideoGridAdapter(mCxt, getItemImageWidth());
        // 是否显示选择指示器
        mGridView.setAdapter(mVideoAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 正常操作
                Video video = (Video) adapterView.getAdapter().getItem(i);
                selectVideoFromGrid(video);
            }
        });

        mFolderAdapter = new VideoFolderAdapter(mCxt);

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

    }

    private void initViews(){
        mCxt = this;
        // ActionBar Setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.pickerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("视频");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGridView = (GridView) findViewById(R.id.android_gridView);
        mGridView.setNumColumns(getNumColnums());

        mPopupAnchorView = findViewById(R.id.video_picker_footer);
        btnAlbum = (Button)findViewById(R.id.btnVideoAlbum);
    }

    private void createPopupFolderList(){

        mFolderPopupWindow = new ListPopupWindow(mCxt);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(ListPopupWindow.MATCH_PARENT);
        mFolderPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);

        // 计算ListPopupWindow内容的高度(忽略mPopupAnchorView.height)，R.layout.item_foloer
        int folderItemViewHeight =
                // 图片高度
                getResources().getDimensionPixelOffset(R.dimen.folder_cover_size) +
                        // Padding Top
                        getResources().getDimensionPixelOffset(R.dimen.folder_padding) +
                        // Padding Bottom
                        getResources().getDimensionPixelOffset(R.dimen.folder_padding);
        int folderViewHeight = mFolderAdapter.getCount() * folderItemViewHeight;

        int screenHeigh = getResources().getDisplayMetrics().heightPixels;
        if(folderViewHeight >= screenHeigh){
            mFolderPopupWindow.setHeight(Math.round(screenHeigh * 0.6f));
        }else{
            mFolderPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        }

        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mFolderAdapter.setSelectIndex(position);

                final int index = position;
                final AdapterView v = parent;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();

                        if (index == 0) {
                            getSupportLoaderManager().restartLoader(LOADER_ALL_VIDEO, null, mLoaderCallback);
                            btnAlbum.setText("所有视频");
                        } else {
                            VideoFolder folder = (VideoFolder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mVideoAdapter.setData(folder.videos);
                                btnAlbum.setText(folder.name);
                            }
                        }

                        // 滑动到最初始位置
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case VideoPreviewActivity.REQUEST_PREVIEW:
//                    ArrayList<String> pathArr = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
//                    int action = data.getIntExtra("action", 1);
//
//                    if(pathArr != null && pathArr.size() != resultList.size()){
//                        resultList = pathArr;
//                        mImageAdapter.setDefaultSelected(resultList);
//                    }
                    int action = data.getIntExtra("action", 1);
                    if (action == 1) {
                        Video video = data.getParcelableExtra("result");
                        complete(video);
                    }

                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "on change");

        // 重置列数
        mGridView.setNumColumns(getNumColnums());
        // 重置Item宽度
        mVideoAdapter.setItemSize(getItemImageWidth());

        if(mFolderPopupWindow != null){
            if(mFolderPopupWindow.isShowing()){
                mFolderPopupWindow.dismiss();
            }

            // 重置PopupWindow高度
            int screenHeigh = getResources().getDisplayMetrics().heightPixels;
            mFolderPopupWindow.setHeight(Math.round(screenHeigh * 0.6f));
        }

        super.onConfigurationChanged(newConfig);
    }


    private void selectVideoFromGrid(Video video) {
        if(video != null) {
            mVideoAdapter.select(video);
            //@todo go to preview video
            // 预览
            Intent intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra("video", video);
            startActivityForResult(intent, VideoPreviewActivity.REQUEST_PREVIEW);
        }
    }

    private String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) +"G";
        }
        return fileSizeString;
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] VIDEO_PROJECTION = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media._ID };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            // 根据图片设置参数新增验证条件
            StringBuilder selectionArgs = new StringBuilder();

            if(id == LOADER_ALL_VIDEO) {
                CursorLoader cursorLoader = new CursorLoader(mCxt,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                        selectionArgs.toString(), null, VIDEO_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Video> videos = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do{
                        String path = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                        long duration = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));

                        Video video = new Video(path, name, dateTime, duration);
                        videos.add(video);
                        if( !hasFolderGened ) {
                            // 获取文件夹名称
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            VideoFolder folder = new VideoFolder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.video_cover = video;
                            if (!mResultFolder.contains(folder)) {
                                List<Video> videoList = new ArrayList<>();
                                videoList.add(video);
                                folder.videos = videoList;
                                mResultFolder.add(folder);
                            } else {
                                // 更新
                                VideoFolder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.videos.add(video);
                            }
                        }

                    }while(data.moveToNext());

                    mVideoAdapter.setData(videos);

//                    // 设定默认选择
//                    if(resultList != null && resultList.size()>0){
//                        mVideoAdapter.setDefaultSelected(resultList);
//                    }

                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * 获取GridView Item宽度
     * @return
     */
    private int getItemImageWidth(){
        int cols = getNumColnums();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
        return (screenWidth - columnSpace * (cols-1)) / cols;
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列
     * @return
     */
    private int getNumColnums(){
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        return cols < 3 ? 3 : cols;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_picker, menu);
//        menuDoneItem = menu.findItem(R.id.action_picker_done);
//        menuDoneItem.setVisible(false);
//        refreshActionStatus();
//        return true;
//    }

//    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

//        if(item.getItemId() == R.id.action_picker_done){
//            complete();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    // 返回已选择的图片数据
    private void complete(Video video){
        Intent data = new Intent();
        data.putExtra("video", video);
        setResult(RESULT_OK, data);
        finish();
    }

}
