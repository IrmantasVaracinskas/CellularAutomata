package neighbourCounters;

import Helpers.Helpers;
import neighbourCounters.NeighbourCounter;
import processing.Sketch;

/**
 * Created by irmis on 15/01/2018.
 */
public class MooreCounterSum implements NeighbourCounter {
    private int radius;
    private int neighbours;
    public MooreCounterSum(int _radius)
    {
        radius = _radius;
    }
    public float countNeighbours(int x, int y)
    {
        neighbours = 0;
        float sum = 0;
        for(int i = Helpers.max(0, x - radius); i <= Helpers.min(x + radius, Sketch.board.boardWidth-1); i++)
        {
            for(int j = Helpers.max(0, y - radius); j <= Helpers.min(y + radius, Sketch.board.boardHeight-1); j++)
            {
                if(i != x || j != y)
                {
                    Sketch.Cell c = Sketch.board.getCell(i, j);
                    sum += c.state;
                    ++neighbours;
                }
            }
        }
        return sum;
    }

    public float countNeighbours(Sketch.Cell cell)
    {
        return countNeighbours(cell.x, cell.y);
    }

    public int getTotalNeighbours()
    {
        return neighbours;
        //return Helpers.pow(2*radius + 1, 2);
    }
}
