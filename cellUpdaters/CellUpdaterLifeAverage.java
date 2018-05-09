package cellUpdaters;

import cellUpdaters.CellUpdater;
import neighbourCounters.NeighbourCounter;
import processing.Sketch;

/**
 * Created by irmis on 15/01/2018.
 */
public class CellUpdaterLifeAverage implements CellUpdater {
    NeighbourCounter nCounter;
    int totalNeighbors;

    int[] cellsCount;
    public CellUpdaterLifeAverage(NeighbourCounter _nCounter)
    {
        nCounter = _nCounter;
        totalNeighbors = nCounter.getTotalNeighbours() - 1;

        cellsCount = new int[Sketch.ruleMap.size() - 1];
    }

    public void updateCell(Sketch.Cell cell) {
        float neighboursSum = nCounter.countNeighbours(cell);

        totalNeighbors = nCounter.getTotalNeighbours();
        int average = (int)neighboursSum / totalNeighbors;
        if(average < 0)
            average = 0;
        if(average > Sketch.ruleMap.size() - 1)
            average = Sketch.ruleMap.size() - 1;
        cell.futureState = Sketch.ruleMap.get(average).left;
        cell.cellColor = Sketch.ruleMap.get(average).right;

        cellsCount[(int)cell.futureState]++;
    }

    @Override
    public int[] getCellsCount() {
        return cellsCount;
    }

    @Override
    public void resetCellsCount() {
        for(int i = 0; i < cellsCount.length; ++i)
        {
            cellsCount[i] = 0;
        }
    }
}
