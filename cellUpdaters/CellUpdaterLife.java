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
    public CellUpdaterLife(NeighbourCounter _nCounter)
    {
        nCounter = _nCounter;
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
    }
}
