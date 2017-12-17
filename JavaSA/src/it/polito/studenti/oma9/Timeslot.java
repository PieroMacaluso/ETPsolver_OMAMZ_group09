package it.polito.studenti.oma9;

/**
 * @deprecated
 */
class Timeslot {
	private int sloID;

	/**
	 * Default constructor
	 *
	 * @param sloID slot ID
	 * @deprecated
	 */
	Timeslot(int sloID) {
		this.sloID = sloID;
	}


	/**
	 * Get timeslot identifier
	 *
	 * @return timeslot ID
	 * @deprecated
	 */
	int getSloID() {
		return sloID;
	}
}
