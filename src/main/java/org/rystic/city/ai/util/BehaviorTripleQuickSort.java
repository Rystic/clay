package org.rystic.city.ai.util;

public class BehaviorTripleQuickSort
{
	public static BehaviorTriple[] sort(BehaviorTriple[] values)
	{
		BehaviorTriple[] returnValues = values;
		_quickSorts = 0;
		quicksort(returnValues, 0, values.length - 1);
//		System.out.println("Quick sort iterations: " + _quickSorts);
		return returnValues;
	}

	private static void quicksort(BehaviorTriple[] values, int low, int high)
	{
		int i = low, j = high;
		int pivot = values[low + (high - low) / 2]._weight;

		while (i <= j)
		{
			while (values[i]._weight > pivot)
			{
				i++;
			}
			while (values[j]._weight < pivot)
			{
				j--;
			}
			if (i <= j)
			{
				exchange(values, i, j);
				i++;
				j--;
			}
		}
		if (low < j)
			quicksort(values, low, j);
		if (i < high)
			quicksort(values, i, high);
	}

	private static void exchange(BehaviorTriple[] values, int i, int j)
	{
		_quickSorts++;
		BehaviorTriple temp = values[i];
		values[i] = values[j];
		values[j] = temp;
	}
	
	private static int _quickSorts;
}
