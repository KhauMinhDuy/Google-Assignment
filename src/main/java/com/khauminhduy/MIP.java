package com.khauminhduy;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class MIP {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();

		MPSolver solver = MPSolver.createSolver("SCIP");
		if (solver == null) {
			return;
		}

		int[][] costs = { 
				{ 18, 52, 64, 39 },
				{ 75, 55, 19, 48 },
				{ 35, 57, 8, 65 },
				{ 27, 25, 14, 16 }
			};
		
		int numWorkers = costs.length;
		int numTasks = costs[0].length;
		
		MPVariable[][] variables = new MPVariable[numWorkers][numTasks];
		
		for(int i = 0; i < numWorkers; i++) {
			for (int j = 0; j < numTasks; j++) {
				variables[i][j] = solver.makeIntVar(0, 1, "");
			}
		}
		
		System.out.println("Number of Variables: " + solver.numVariables());
		
		for(int i = 0; i < numWorkers; i++) {
			MPConstraint constraint = solver.makeConstraint(0, 1, "");
			for(int j = 0; j < numTasks; j++) {
				constraint.setCoefficient(variables[i][j], 1);
			}
		}
		
		for(int j = 0; j < numTasks; j++) {
			MPConstraint constraint = solver.makeConstraint(1, 1, "");
			for(int i = 0; i < numWorkers; i++) {
				constraint.setCoefficient(variables[i][j], 1);
			}
		}
		
		System.out.println("Number of Constraints : " + solver.numConstraints());
		
		MPObjective objective = solver.objective();
		for(int i = 0; i < numWorkers; i++) {
			for(int j = 0; j < numTasks; j++) {
				objective.setCoefficient(variables[i][j], costs[i][j]);
			}
		}
		
		objective.setMinimization();
		
		ResultStatus resultStatus = solver.solve();
		if(resultStatus == ResultStatus.OPTIMAL ) {
			System.out.println("Cost: " + objective.value());
			for(int i = 0; i < numWorkers; i++) {
				for(int j = 0; j < numTasks; j++) {
					if(variables[i][j].solutionValue() == 1) {
						System.out.println("Worker " + i + " assigned to task " + j + ".  Cost = " + costs[i][j]);
					}
				}
			}
		}
	}

}
