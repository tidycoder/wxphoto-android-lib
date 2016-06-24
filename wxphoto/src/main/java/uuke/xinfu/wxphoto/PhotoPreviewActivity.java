package uuke.xinfu.wxphoto;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import uuke.xinfu.wxphoto.widget.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Created by foamtrace on 2015/8/25.
 */
public class PhotoPreviewActivity extends AppCompatActivity implements PhotoPagerAdapter.PhotoViewClickListener{

    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String EXTRA_CURRENT_ITEM = "extra_current_item";
    public static final String EXTRA_ORIGIN = "is_origin";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "preview_result";

    /** 预览请求状态码 */
    public static final int REQUEST_PREVIEW = 99;

    private ArrayList<String> paths;
    private ViewPagerFixed mViewPager;
    private uuke.xinfu.wxphoto.PhotoPagerAdapter mPagerAdapter;
    private int currentItem = 0;
    private CheckBox btnOrigin;
    private Button btnPreviewConfirm;
    private Boolean isOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_preview);

        initViews();

        paths = new ArrayList<>();
        ArrayList<String> pathArr = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        if(pathArr != null){
            paths.addAll(pathArr);
        }
        isOrigin = getIntent().getBooleanExtra(EXTRA_ORIGIN, false);
        btnOrigin.setChecked(isOrigin);

        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);

        mPagerAdapter = new PhotoPagerAdapter(this, paths);
        mPagerAdapter.setPhotoViewClickListener(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateActionBarTitle();

        btnOrigin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                isOrigin = btnOrigin.isChecked();
            }
        });

        btnPreviewConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });
    }

    private void initViews(){
        mViewPager = (ViewPagerFixed) findViewById(R.id.vp_photos);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.pickerToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnPreviewConfirm = (Button) findViewById(R.id.btnPreviewConfirm);
        btnOrigin = (CheckBox) findViewById(R.id.btnPreviewOrigin);
    }

    @Override
    public void OnPhotoTapListener(View view, float v, float v1) {
        onBackPressed();
    }

    public void updateActionBarTitle() {
        getSupportActionBar().setTitle(
                getString(R.string.image_index, mViewPager.getCurrentItem() + 1, paths.size()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("action", 2);
        intent.putExtra(EXTRA_ORIGIN, isOrigin);
        intent.putExtra(EXTRA_RESULT, paths);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        // 删除当前照片
//        if(item.getItemId() == R.id.action_discard){
//            final int index = mViewPager.getCurrentItem();
//            final String deletedPath =  paths.get(index);
//            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.deleted_a_photo,
//                    Snackbar.LENGTH_LONG);
//            if(paths.size() <= 1){
//                // 最后一张照片弹出删除提示
//                // show confirm dialog
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.confirm_to_delete)
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                paths.remove(index);
//                                onBackPressed();
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .show();
//            }else{
//                snackbar.show();
//                paths.remove(index);
//                mPagerAdapter.notifyDataSetChanged();
//            }
//
//            snackbar.setAction(R.string.undo, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (paths.size() > 0) {
//                        paths.add(index, deletedPath);
//                    } else {
//                        paths.add(deletedPath);
//                    }
//                    mPagerAdapter.notifyDataSetChanged();
//                    mViewPager.setCurrentItem(index, true);
//                }
//            });
//        }
        return super.onOptionsItemSelected(item);
    }

    // 返回已选择的图片数据
    private void complete() {
        Intent data = new Intent();
        data.putStringArrayListExtra(EXTRA_RESULT, paths);
        data.putExtra("action", 1);
        data.putExtra(EXTRA_ORIGIN, isOrigin);
        data.putExtra(EXTRA_RESULT, paths);
        setResult(RESULT_OK, data);
        finish();
    }
}
