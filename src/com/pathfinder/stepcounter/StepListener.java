package com.pathfinder.stepcounter;

public class StepListener {

	private static int steps;

	public StepListener() {
		steps = 0;
	}

	public int getSteps() {
		return steps;
	}

	public void onStep() {
		steps++;
	}

	public void reset() {
		steps = 0;
	}

}
