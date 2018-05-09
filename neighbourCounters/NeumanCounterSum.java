package neighbourCounters;

import Helpers.Helpers;
import neighbourCounters.NeighbourCounter;
import processing.Sketch;

/**
 * Created by irmis on 15/01/2018.
 */
public class NeumanCounterSum implements NeighbourCounter {
    private int radius;
    public NeumanCounterSum(int _radius)
    {
        radius = _radius;
    }
    public float countNeighbours(int x, int y)
    {
        float sum = 0;
        for(int i = Helpers.max(0, x - radius); i < Helpers.min(Sketch.board.boardWidth, x + radius + 1); i++)
        {
            if(i != x)
                sum += Sketch.board.getCell(i, y).state;

        }
        for(int i = Helpers.max(0, y - radius); i < Helpers.min(Sketch.board.boardHeight, y + radius + 1); i++)
        {
            if(i != y)
                sum += Sketch.board.getCell(x, i).state;
        }
        return sum;
    }

    public float countNeighbours(Sketch.Cell cell)
    {
        return countNeighbours(cell.x, cell.y);
    }

    public int getTotalNeighbours()
    {
        return 4 * radius + 1;
    }
}
