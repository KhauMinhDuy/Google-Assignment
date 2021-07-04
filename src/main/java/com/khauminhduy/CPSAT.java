package com.khauminhduy;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class CPSAT {

	public static void main(String[] args) {
		Loader.loadNativeLibraries();
		int[][] costs = { 
				{ 18, 52, 64, 39 },
				{ 75, 55, 19, 48 },
				{ 35, 57, 8, 65 },
				{ 27, 25, 14, 16 }
			};
		
		CpModel model = new CpModel();
		final int numWorkers = costs.length;
		final int numTasks = costs[0].length;
		
		IntVar[][] variables = new IntVar[numWorkers][numTasks];
		IntVar[] variablesFlat = new IntVar[numWorkers * numTasks];
		
		int[] costsFlat = new int[numWorkers * numTasks];
		
		for(int i = 0; i < numWorkers; i++) {
			for(int j = 0; j < numTasks; j++) {
				// 0 <= x <= 1
				variables[i][j] = model.newIntVar(0, 1, "");
				int k = i * numTasks + j;
				variablesFlat[k] = variables[i][j];
				costsFlat[k] = costs[i][j];
			}
		}
		
		for(int i = 0; i < numWorkers; i++) {
			IntVar[] vars = new IntVar[numTasks];
			for(int j = 0; j < numTasks; j++) {
				vars[j] = variables[i][j];
			}
			// tong cua dong <= 1
			model.addLessOrEqual(LinearExpr.sum(vars), 1);
		}
		
		for(int j = 0; j < numTasks; j++) {
			IntVar[] vars = new IntVar[numWorkers];
			for(int i = 0; i < numWorkers; i++) {
				vars[i] = variables[i][j];
			}
			// tong cua cot = 1
			model.addEquality(LinearExpr.sum(vars), 1);
		}
		
		model.minimize(LinearExpr.scalProd(variablesFlat, costsFlat));
		CpSolver solver = new CpSolver();
		CpSolverStatus status = solver.solve(model);
		if(status == CpSolverStatus.OPTIMAL) {
			System.out.println("Cost: " + solver.objectiveValue());
			for(int i = 0; i < numWorkers; i++) {
				for(int j = 0; j < numTasks; j++) {
					if(solver.value(variables[i][j]) == 1) {
						System.out.println("Worker " + i + " assigned to task " + j + ".  Cost = " + costs[i][j]);
					}
				}
			}
		}
		
	}

}
