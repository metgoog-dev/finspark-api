package com.xyz.microfinance.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStatsResponse {
    private long totalCustomers;
    private long totalLoans;
    private long activeLoans;
    private long pendingLoans;
    private BigDecimal totalDisbursed;
    private List<LoanResponse> recentLoans;
    private List<TopCustomerResponse> topCustomers;
    private List<ChartDataResponse> chartData;

    public DashboardStatsResponse() {}

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalLoans() { return totalLoans; }
    public void setTotalLoans(long totalLoans) { this.totalLoans = totalLoans; }

    public long getActiveLoans() { return activeLoans; }
    public void setActiveLoans(long activeLoans) { this.activeLoans = activeLoans; }

    public long getPendingLoans() { return pendingLoans; }
    public void setPendingLoans(long pendingLoans) { this.pendingLoans = pendingLoans; }

    public BigDecimal getTotalDisbursed() { return totalDisbursed; }
    public void setTotalDisbursed(BigDecimal totalDisbursed) { this.totalDisbursed = totalDisbursed; }

    public List<LoanResponse> getRecentLoans() { return recentLoans; }
    public void setRecentLoans(List<LoanResponse> recentLoans) { this.recentLoans = recentLoans; }

    public List<TopCustomerResponse> getTopCustomers() { return topCustomers; }
    public void setTopCustomers(List<TopCustomerResponse> topCustomers) { this.topCustomers = topCustomers; }

    public List<ChartDataResponse> getChartData() { return chartData; }
    public void setChartData(List<ChartDataResponse> chartData) { this.chartData = chartData; }
}
