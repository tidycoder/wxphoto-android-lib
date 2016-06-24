package uuke.xinfu.wxphoto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liupeng on 16/6/21.
 */
public class Video implements Parcelable {
    public String path;
    public String name;
    public long time;
    public long duration;

    public Video(String path, String name, long time, long duration){
        this.path = path;
        this.name = name;
        this.time = time;
        this.duration = duration;
    }

    protected Video(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.time = in.readLong();
        this.duration = in.readLong();
    }

    @Override
    public boolean equals(Object o) {
        try {
            Video other = (Video) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeLong(this.time);
        dest.writeLong(this.duration);
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
