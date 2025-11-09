package vn.edu.fpt.taptoeat.models;

import java.util.Date;
import java.util.List;

public class Session {
    private String id;
    private String tableId;
    private int tableNumber;
    private Date startTime;
    private Date endTime;
    private String status; // "active", "completed", "cancelled"
    private List<String> orderIds;
    private double totalAmount;

    public Session() {
    }

    public Session(String id, String tableId, int tableNumber, Date startTime) {
        this.id = id;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.startTime = startTime;
        this.status = "active";
        this.totalAmount = 0.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<String> orderIds) {
        this.orderIds = orderIds;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isActive() {
        return "active".equals(status);
    }
}
