package org.chengpx.test180428.domain;

/**
 * 公交车信息
 * <p>
 * create at 2018/4/30 21:48 by chengpx
 */
public class BusBean {

    private Integer BusId;
    private Integer BusCapacity;

    public Integer getBusId() {
        return BusId;
    }

    public void setBusId(Integer busId) {
        BusId = busId;
    }

    public Integer getBusCapacity() {
        return BusCapacity;
    }

    public void setBusCapacity(Integer busCapacity) {
        BusCapacity = busCapacity;
    }

    @Override
    public String toString() {
        return "BusBean{" +
                "BusId=" + BusId +
                ", BusCapacity=" + BusCapacity +
                '}';
    }

}
