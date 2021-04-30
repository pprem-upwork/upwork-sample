package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.TripDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FareCalculationRules {


    enum FairTypes{BASEFARE,DAILYCAPFARE,WEEKLYCAPFARE};

    public TripDetails execute(List<TripDetails> pastTrips, TripDetails newTrip, Map<FairTypes,BigDecimal> allFares);
}
