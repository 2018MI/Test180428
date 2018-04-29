package org.chengpx.test180428.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * create at 2018/4/28 21:30 by chengpx
 */
@DatabaseTable(tableName = "car")
public class CarBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "CarId")
    private Integer CarId;
    @DatabaseField(persisted = false)// 该字段不持久化
    private Integer Balance;
    @DatabaseField(columnName = "Money")
    private Integer Money;
    @DatabaseField(columnName = "rechargeDate")
    private Date rechargeDate;
    @DatabaseField(columnName = "fk_uid", foreignColumnName = "id", foreign = true, foreignAutoRefresh = true)
    private UserBean user;
    @DatabaseField(persisted = false)// 该字段不持久化
    private String CarAction;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCarId() {
        return CarId;
    }

    public void setCarId(Integer carId) {
        CarId = carId;
    }

    public Integer getBalance() {
        return Balance;
    }

    public void setBalance(Integer balance) {
        Balance = balance;
    }

    public Integer getMoney() {
        return Money;
    }

    public void setMoney(Integer money) {
        Money = money;
    }

    public Date getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(Date rechargeDate) {
        this.rechargeDate = rechargeDate;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getCarAction() {
        return CarAction;
    }

    public void setCarAction(String carAction) {
        CarAction = carAction;
    }

    @Override
    public String toString() {
        return "CarBean{" +
                "id=" + id +
                ", CarId=" + CarId +
                ", Balance=" + Balance +
                ", Money=" + Money +
                ", rechargeDate=" + rechargeDate +
                ", user=" + user +
                ", CarAction='" + CarAction + '\'' +
                '}';
    }

}
