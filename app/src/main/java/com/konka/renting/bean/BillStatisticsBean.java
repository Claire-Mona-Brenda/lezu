package com.konka.renting.bean;

public class BillStatisticsBean {
    /**
     * recharge : 112499.00
     * service_charge : 10200.00
     * install_recharge : 8887.00
     * withdraw : 0.00
     * rental : 1392.00
     * total : 94804.00
     */

    private String recharge;//充值
    private String service_charge;//服务费
    private String install_recharge;//安装费
    private String withdraw;//提现
    private String rental;//房租
    private String total;//总计

    public String getRecharge() {
        return recharge;
    }

    public void setRecharge(String recharge) {
        this.recharge = recharge;
    }

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public String getInstall_recharge() {
        return install_recharge;
    }

    public void setInstall_recharge(String install_recharge) {
        this.install_recharge = install_recharge;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }

    public String getRental() {
        return rental;
    }

    public void setRental(String rental) {
        this.rental = rental;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
