package ru.sbt.jschool.session3.problem1.parking;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParkingTest {
    @Test
    public void testDriveIn() throws Exception {
        Parking parking = new Parking(2,1);
        Boolean res = parking.driveIn(1,1);
        assertEquals( true, res);
        res = parking.driveIn(1,1);
        assertEquals(false, res);
        res = parking.driveIn(2,1);
        assertEquals( true, res);
        res = parking.driveIn(3,1);
        assertEquals(false, res);
    }

    @Test
    public void testDriveOut() throws Exception {
        Parking parking = new Parking(1,1);
        Boolean res = parking.driveIn(1,1);
        assertEquals(true, res);
        res = parking.driveIn(2,1);
        assertEquals(false, res);
        parking.driveOut(1 ,2);
        res = parking.driveIn(1,1);
        assertEquals(true, res);
    }

    @Test
    public void testDriveOutSum() throws Exception {
        Parking parking = new Parking(1,1);
        float sum = 0;
        parking.driveIn(1,24 + 21);
        sum = parking.driveOut(1,24 + 23);
        assertEquals(2, sum, 0);
        parking.driveIn(1,24 + 24 + 6);
        sum = parking.driveOut(1,24 + 24 + 7);
        assertEquals(1, sum, 0);
        parking.driveIn(1,24 + 21);
        sum = parking.driveOut(1,24 + 24 + 3);
        assertEquals(10, sum, 0);
        parking.driveIn(1,24 + 24 + 2);
        sum = parking.driveOut(1,24 + 24 + 8);
        assertEquals(10, sum, 0);
        parking.driveIn(1,24 + 24 + 2);
        sum = parking.driveOut(1,24 + 24 + 6);
        assertEquals(8, sum, 0);
        parking.driveIn(1, 21);
        sum = parking.driveOut(1, 23);
        assertEquals(2, sum, 0);
        parking.driveIn(1, 6);
        sum = parking.driveOut(1, 7);
        assertEquals(1, sum, 0);
        parking.driveIn(1,21);
        sum = parking.driveOut(1,24 + 3);
        assertEquals(10, sum, 0);
        parking.driveIn(1,2);
        sum = parking.driveOut(1, 8);
        assertEquals(10, sum, 0);
        parking.driveIn(1, 2);
        sum = parking.driveOut(1, 6);
        assertEquals( 8, sum, 0);

    }

}
