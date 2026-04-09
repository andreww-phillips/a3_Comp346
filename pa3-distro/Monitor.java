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
	public final String WAITING = "WAITING";
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
	public synchronized void pickUp(final int piTID)
	{	
		if(philosopherStates[piTID] != EATING && philosopherStates[piTID] != TALKING){
			int leftChopstick = piTID;
			int rightChopstick = (piTID + 1) % NUMBER_OF_CHOPSTICKS;

			while(chopsticksInUse[leftChopstick] || chopsticksInUse[rightChopstick]){
				try{
					wait();
				} catch (InterruptedException e){
					System.err.println("Monitor.pickUp():");
					DiningPhilosophers.reportException(e);
					System.exit(1);
				}
			}
			chopsticksInUse[leftChopstick] = true;
			chopsticksInUse[rightChopstick] = true;
			philosopherStates[piTID] = EATING;
			System.out.println("Philosopher " + piTID + " has picked up chopsticks " + leftChopstick + " and " + rightChopstick + " and is now eating.");
		}	
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		if(philosopherStates[piTID] == EATING){
			int leftChopstick = piTID;
			int rightChopstick = (piTID + 1) % NUMBER_OF_CHOPSTICKS;

			chopsticksInUse[leftChopstick] = false;
			chopsticksInUse[rightChopstick] = false;
			philosopherStates[piTID] = THINKING;
			System.out.println("Philosopher " + piTID + " has put down chopsticks " + leftChopstick + " and " + rightChopstick + " and is now thinking.");
			notifyAll();
		}
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID)
	{
		if(philosopherStates[piTID] != EATING && philosopherStates[piTID] != TALKING){
			for(int i = 0; i < philosopherStates.length; i++){
				if(philosopherStates[i] == TALKING){
					try{
						wait();
					}catch(InterruptedException e){
						System.err.println("Monitor.requestTalk():");
						DiningPhilosophers.reportException(e);
						System.exit(1);
					}	
				}else{
					philosopherStates[piTID] = TALKING;
					System.out.println("Philosopher " + piTID + " has started talking.");
				}
			}
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		if(philosopherStates[piTID] == TALKING){
			philosopherStates[piTID] = THINKING;
			System.out.println("Philosopher " + piTID + " has stopped talking and is now thinking.");
			notifyAll();
		}
	}
}

// EOF
