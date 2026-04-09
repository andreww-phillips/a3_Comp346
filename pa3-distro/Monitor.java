/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	public final int NUMBER_OF_CHOPSTICKS;
	public boolean[] chopsticksInUse;

	public final String THINKING = "THINKING";
	public final String EATING = "EATING";
	public final String TALKING = "TALKING";

	String[] philosopherStates;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		this.NUMBER_OF_CHOPSTICKS = piNumberOfPhilosophers;
		this.chopsticksInUse = new boolean[piNumberOfPhilosophers];
		this.philosopherStates = new String[piNumberOfPhilosophers];
		for(int i = 0; i < philosopherStates.length; i++){
			philosopherStates[i] = THINKING;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */
	
	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	@SuppressWarnings("UnnecessaryLocalVariable")
	public synchronized void pickUp(final int piTID)
	{
		int id = piTID - 1;  // ← convert FIRST
		int leftChopstick = id;
		int rightChopstick = (id + 1) % NUMBER_OF_CHOPSTICKS;

		while(philosopherStates[id].equals(EATING) || philosopherStates[id].equals(TALKING)){
			try{
				wait();
			} catch(InterruptedException e){
				System.err.println("Monitor.pickUp():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
		while(chopsticksInUse[leftChopstick] || chopsticksInUse[rightChopstick]){
			try{
				wait();
			} catch(InterruptedException e){
				System.err.println("Monitor.pickUp():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
		chopsticksInUse[leftChopstick] = true;
		chopsticksInUse[rightChopstick] = true;
		philosopherStates[id] = EATING;
		System.out.println("Philosopher " + piTID + " has picked up chopsticks " + leftChopstick + " and " + rightChopstick + " and is now eating.");
	}

	public synchronized void putDown(final int piTID)
	{
		int id = piTID - 1;  // ← convert FIRST
		int leftChopstick = id;
		int rightChopstick = (id + 1) % NUMBER_OF_CHOPSTICKS;

		if(philosopherStates[id].equals(EATING)){
			chopsticksInUse[leftChopstick] = false;
			chopsticksInUse[rightChopstick] = false;
			philosopherStates[id] = THINKING;
			System.out.println("Philosopher " + piTID + " has put down chopsticks " + leftChopstick + " and " + rightChopstick + " and is now thinking.");
			notifyAll();
		}
	}

	public synchronized void requestTalk(final int piTID)
	{
		int id = piTID - 1;  // ← convert FIRST

		while(philosopherStates[id].equals(EATING) || isSomeoneTalking()){
			try{
				wait();
			} catch(InterruptedException e){
				System.err.println("Monitor.requestTalk():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
		philosopherStates[id] = TALKING;
		System.out.println("Philosopher " + piTID + " has started talking.");
	}

	public synchronized void endTalk(final int piTID)
	{
		int id = piTID - 1;  // ← convert FIRST

		if(philosopherStates[id].equals(TALKING)){
			philosopherStates[id] = THINKING;
			System.out.println("Philosopher " + piTID + " has stopped talking and is now thinking.");
			notifyAll();
		}
	}

	private boolean isSomeoneTalking(){
		for(String state : philosopherStates){
			if(state.equals(TALKING)) return true;
		}
		return false;
	}
}

// EOF
