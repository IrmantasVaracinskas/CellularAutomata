package neighbourCounters;
import Helpers.*;
import neighbourCounters.NeighbourCounter;
import processing.Sketch;

/**
 * Created by Irmis on 2018-01-07.
 */
public class NeumanCounter implements NeighbourCounter
{
    private int radius;
    public NeumanCounter(int _radius)
    {
        radius = _radius;
    }
    public float countNeighbours(int x, int y)
    {
        int neighbours = 0;
        for(int i = Helpers.max(0, x - radius); i < Helpers.min(Sketch.board.boardWidth, x + radius + 1); i++)
        {
            if(Sketch.board.getCell(i, y).state != 0 && i != x)
                neighbours++;
        }
        for(int i = Helpers.max(0, y - radius); i < Helpers.min(Sketch.board.boardHeight, y + radius + 1); i++)
        {
            if(Sketch.board.getCell(x, i).state != 0 && i != y)
                neighbours++;
        }
        return neighbours;
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