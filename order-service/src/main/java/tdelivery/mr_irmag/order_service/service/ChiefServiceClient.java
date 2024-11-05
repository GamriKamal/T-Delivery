package tdelivery.mr_irmag.order_service.service;

import org.springframework.stereotype.Service;

@Service
public class ChiefServiceClient {

    public static int getTimeOfCooking(){
        int minTimeOfCooking = 20;
        int maxTimeOfCooking = 40;

        return (int) (Math.random() * ((maxTimeOfCooking - minTimeOfCooking) + 1)) + minTimeOfCooking;
    }
}
