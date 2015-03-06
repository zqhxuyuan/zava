package com.interview.design.questions.parkinglot;


import com.interview.utils.ctci.AssortedMethods;

public class ParkingSimulator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ParkingLot lot = new ParkingLot();
		
		Vehicle v = null;
		while (v == null || lot.parkVehicle(v)) {
			lot.print();
			int r = AssortedMethods.randomIntInRange(0, 10);
			if (r < 2) {
				v = new Bus();
			} else if (r < 4) {
				v = new Motorcycle();
			} else {
				v = new Car();
			}
			System.out.print("\nParkingSimulator a ");
			v.print();
			System.out.println("");
		}
		System.out.println("ParkingSimulator Failed. Final state: ");
		lot.print();
	}

}
