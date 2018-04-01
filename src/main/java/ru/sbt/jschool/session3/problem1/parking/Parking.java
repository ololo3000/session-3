package ru.sbt.jschool.session3.problem1.parking;

import java.text.ParseException;
import java.util.HashMap;

public class Parking {
    private final int CAPACITY;
    private final float TARIFF;
    private HashMap<Long, Long> cars = new HashMap();
    private final int DAY_HOURS = 24;
    private final int START_HOUR_OF_DT = 23;
    private final long DOUBLE_TARIFF_DURATION = 7;
    private final float SUM_PER_DAY;
    private final long END_HOUR_OF_DT;


    public Parking(int capacity, float tariff) {
        cars = new HashMap(capacity, 1);
        this.CAPACITY = capacity;
        this.TARIFF = tariff;
        END_HOUR_OF_DT = (START_HOUR_OF_DT + DOUBLE_TARIFF_DURATION) % DAY_HOURS;
        this.SUM_PER_DAY = (DOUBLE_TARIFF_DURATION + DAY_HOURS) * TARIFF;
    }

    public boolean driveIn(long carId, long driveInTime) {
        if (cars.size() == CAPACITY) {
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
            long dh = parkingTime % DAY_HOURS;
            long sh = cars.get(carId) % DAY_HOURS;
            long eh = (sh + dh) % DAY_HOURS;

            sum += (int) (parkingTime / DAY_HOURS) * SUM_PER_DAY;
            cars.remove(carId);

            if (eh <= START_HOUR_OF_DT
                    && eh >= END_HOUR_OF_DT
                    && sh >= END_HOUR_OF_DT
                    && sh <= START_HOUR_OF_DT) {
                sum += dh * TARIFF;
                return sum;
            }

            if (sh < START_HOUR_OF_DT && sh > END_HOUR_OF_DT) {
                sum += (START_HOUR_OF_DT - sh) * TARIFF;
                sh = START_HOUR_OF_DT;
            }

            if (eh > END_HOUR_OF_DT && eh < START_HOUR_OF_DT) {
                sum += (eh - END_HOUR_OF_DT) * TARIFF;
                eh = END_HOUR_OF_DT;
            }

            long duration = eh - sh;

            if (duration < 0) {
                duration = duration + DAY_HOURS;
            }
            sum += duration * TARIFF * 2;
        }
        return sum;
    }
}
