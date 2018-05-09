package cellUpdaters;

import cellUpdaters.CellUpdater;
import neighbourCounters.NeighbourCounter;
import processing.Pair;
import processing.Sketch;

/**
 * Created by irmis on 15/01/2018.
 */
public class CellUpdaterLife implements CellUpdater {
    NeighbourCounter nCounter;
    int aliveCells;
    int deadCells;
    public CellUpdaterLife(NeighbourCounter _nCounter)
    {
        nCounter = _nCounter;
        aliveCells = 0;
        deadCells = 0;
    }

    public void updateCell(Sketch.Cell cell)
    {
        int neighbours = (int)nCounter.countNeighbours(cell);
        Pair<Integer, Integer> value = Sketch.ruleMap.get(neighbours);
        if(cell.state > 0)
        {
            cell.cellColor = Sketch.colorMap.get(neighbours).left;
            cell.futureState = value.left;
        } else{
            cell.cellColor = Sketch.colorMap.get(neighbours).right;
            cell.futureState = value.right;
        }
        if(cell.futureState > 0)
            ++aliveCells;
        else
            ++deadCells;
    }

    public int[] getCellsCount()
    {
        return new int[]{deadCells, aliveCells};
    }

    public void resetCellsCount()
    {
        deadCells = 0;
        aliveCells = 0;
    }
}
