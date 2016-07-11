package uuke.xinfu.wxphoto;

import java.util.List;

/**
 * Created by liupeng on 16/6/24.
 */
public class VideoFolder {
    public String name;
    public String path;
    public Video video_cover;
    public List<Video> videos;

    @Override
    public boolean equals(Object o) {
        try {
            VideoFolder other = (VideoFolder) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
