package earth.terrarium.adastra.common.util.radio;

public interface RadioHolder {

    int RANGE_SQ = 3072;
    int RANGE_DROPOFF_SQ = 1024;

    String getRadioUrl();

    void setRadioUrl(String url);
}
