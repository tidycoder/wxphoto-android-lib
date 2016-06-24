package uuke.xinfu.wxphoto.intent;

import android.content.Context;
import android.content.Intent;

import uuke.xinfu.wxphoto.VideoPickerActivity;

/**
 * Created by liupeng on 16/6/22.
 */
public class VideoPickerIntent extends Intent {

    public VideoPickerIntent(Context packageContext) {
        super(packageContext, VideoPickerActivity.class);
    }

    /**
     * 选择
     * @param model
     */
    public void setTest(String test){
        this.putExtra("Test", test);
    }
}
