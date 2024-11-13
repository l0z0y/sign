package com.yy.sign.model;

public class SignRequest {

    public String capture_img;
    public String capture_time;
    public String dev_sno;
    public String workNo;

    @Override
    public String toString() {
        return "SignRequest{" +
                "capture_time='" + capture_time + '\'' +
                ", dev_sno='" + dev_sno + '\'' +
                ", workNo='" + workNo + '\'' +
                '}';
    }
}



