package ru.sbt.jschool.session3.problem1.parking;

import java.text.ParseException;
import java.util.HashMap;

public class Parking {
    private final int capacity;
    private final float tariff;
    private HashMap<Long, Long> cars = new HashMap();
    private final int dayHours = 24;
    private final int startHourOfDT = 23;
    private final long doubleTariffDuration = 7;
    private final float summPerDay;
    private long endHourOfDT;


    public Parking(int capacity, float tariff) {
        cars = new HashMap(capacity, 1);
        this.capacity = capacity;
        this.tariff = tariff;
        endHourOfDT = (startHourOfDT + doubleTariffDuration) % dayHours;
        this.summPerDay = (doubleTariffDuration + dayHours) * tariff;
    }

    public boolean driveIn(long carId, long driveInTime) {
        if (cars.size() == capacity) {
            return false;
        }
        if (!cars.containsKey(carId)) {
            cars.put(carId, driveInTime);
            return true;
        }
        return false;
    }

    public float driveOut(long carId, long driveOutTime) {
        float sum = 0;

        if (cars.containsKey(carId)) {
            long parkingTime = driveOutTime - cars.get(carId);

            sum += (int) (parkingTime / dayHours) * summPerDay;

            long dh = parkingTime % dayHours;
            long sh = cars.get(carId) % dayHours;
            long eh = (sh + dh) % dayHours;

            cars.remove(carId);

            if (eh <= startHourOfDT
                    && eh >= endHourOfDT
                    && sh >= endHourOfDT
                    && sh <= startHourOfDT) {
                sum += dh * tariff;
                return sum;
            }

            if (sh < startHourOfDT && sh > endHourOfDT) {
                sum += (startHourOfDT - sh) * tariff;
                sh = startHourOfDT;
            }

            if (eh > endHourOfDT && eh < startHourOfDT) {
                sum += (eh - endHourOfDT) * tariff;
                eh = endHourOfDT;
            }

            long d = eh - sh;

            if (d < 0) {
                d = d + dayHours;
            }
            sum += d * tariff * 2;
        }
        return sum;
    }
}
