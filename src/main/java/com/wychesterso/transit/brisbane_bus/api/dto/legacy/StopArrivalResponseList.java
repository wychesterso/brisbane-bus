package com.wychesterso.transit.brisbane_bus.api.dto.legacy;

import com.wychesterso.transit.brisbane_bus.api.dto.ArrivalResponse;

import java.util.List;

public class StopArrivalResponseList {
    private List<ArrivalResponse> arrivals;

    public StopArrivalResponseList() {}
    public StopArrivalResponseList(List<ArrivalResponse> arrivals) {
        this.arrivals = arrivals;
    }

    public List<ArrivalResponse> getArrivals() { return arrivals; }
    public void setArrivals(List<ArrivalResponse> arrivals) { this.arrivals = arrivals; }
}
