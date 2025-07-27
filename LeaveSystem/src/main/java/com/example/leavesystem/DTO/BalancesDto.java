package com.example.leavesystem.DTO;

public class BalancesDto {

    private int id;
    private int userId;
    private int leaveId;
    private String year;
    private int remainDay;
    private String typename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getRemainDay() {
        return remainDay;
    }

    public void setRemainDay(int remainDay) {
        this.remainDay = remainDay;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }
}
