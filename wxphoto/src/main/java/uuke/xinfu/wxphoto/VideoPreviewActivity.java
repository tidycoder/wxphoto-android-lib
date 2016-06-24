package uuke.xinfu.wxphoto;

/**
 * Created by liupeng on 16/6/21.
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPreviewActivity extends AppCompatActivity{

    VideoView mVideoView;
    public static final int REQUEST_PREVIEW = 99;
    public Video mVideo;
    private Button btnPreviewConfirm;
    private ImageButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_preview);

        initViews();

        mVideo = getIntent().getParcelableExtra("video");

        btnPreviewConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });

        mVideoView = (VideoView)findViewById(R.id.android_videoView);

        MediaController mController = new MediaController(this);
        mVideoView.setMediaController(mController);//设置一个控制条

        mVideoView.setVideoURI(Uri.parse(mVideo.path));

        mVideoView.requestFocus();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                }
                btnStart.setVisibility(View.INVISIBLE);
            }
        });

//        mVideoView.start();
    }

    private void initViews(){
        // ActionBar Setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.pickerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("视频预览");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnPreviewConfirm = (Button) findViewById(R.id.btnVideoPreviewConfirm);
        btnStart = (ImageButton) findViewById(R.id.videostart);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("action", 2);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    // 返回已选择的图片数据
    private void complete() {
        Intent data = new Intent();
        data.putExtra("action", 1);
        data.putExtra("result", mVideo);
        setResult(RESULT_OK, data);
        finish();
    }
}
