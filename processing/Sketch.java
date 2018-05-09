package processing;
import cellUpdaters.CellUpdater;
import enumerators.CAType;
import enumerators.InitialStateType;
import enumerators.Neighborhood;
import enumerators.RuleType;
import neighbourCounters.NeighbourCounter;
import neighbourCounters.NeighbourhoodCounterBuilder;
import processing.core.*;
import processing.data.XML;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import Helpers.*;
import processing.event.MouseEvent;

import static enumerators.Neighborhood.MOORE;
import static enumerators.Neighborhood.NEUMAN;

/**
 * Created by Irmis on 2018-01-07.
 */
public class Sketch extends PApplet{
    static String filename = null;
    public static Board board;
    Cell clickedCell;
    float scale = 1;
    float translateX = 0, translateY = 0;

    float zoomSpeed = (float)0.5;
    int cellSize;

    public static String type;

    String rule;
    Neighborhood neighborhood;
    int radius;

    float strokeWeight;
    int strokeColor;

    public static Map<Integer, Pair<Integer, Integer>> ruleMap;
    public static Map<Integer, Pair<Integer, Integer>> colorMap;


    public static void main(String[] args) {
        PApplet.main("processing.Sketch");
    }

    public void settings()
    {
        size(500, 500);
    }
    public void setup()
    {
        surface.setResizable(true);
        getConfigFile();
        while(filename == null) {
            print("");
        }
        String fileName = filename;
        strokeWeight = 1;
        strokeColor = color(255);
        loadConfiguration(fileName);

        NeighbourCounter counter = new NeighbourhoodCounterBuilder().buildCounter(neighborhood.name().toLowerCase(), radius);
        board = new Board(width / cellSize, height / cellSize, cellSize,
                new CellUpdaterBuilder().buildUpdater(type, counter));
        try {
            loadInitialState(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            exit();
        }
    }
    public void draw()
    {
        setupStroke();
        background(125);
        pushMatrix();
        translate(translateX + offX, translateY + offY);
        scale(scale);

        board.display();
        popMatrix();
        board.update();

        drawStatistics();
    }

    void drawStatistics()
    {
        int[] cellsStates = board.cellUpdater.getCellsCount();
        fill(0);
        rect(0, 0, 150, 40 + cellsStates.length * 12);
        fill(255);
        textSize(12);
        text("Generations: " + frameCount, 5, 10);
        text("Total cells: " + board.cells.length, 5, 34);
        for(int i = 0; i < cellsStates.length; i++)
        {
            text("State " + i + ": " + cellsStates[i], 5, 46 + 12 * i);
        }

    }

    void setupStroke()
    {
        strokeWeight(strokeWeight);
        stroke(strokeColor);
    }

    public class Board implements Displayable
    {
        public int boardWidth;
        public int boardHeight;
        public int cellSize;
        public Cell[] cells;
        public CellUpdater cellUpdater;

        public Board(int w, int h, int _cellSize, CellUpdater _cellUpdater )
        {
            boardWidth = w;
            boardHeight = h;
            cellSize = _cellSize;
            cells = new Cell[boardWidth * boardHeight];
            cellUpdater = _cellUpdater;
            for(int i = 0; i < cells.length; i++)
            {
                //y*width+x = i
                // x = i % width
                cells[i] = new Cell(i % w, i / w, cellSize, 0);
            }
        }

        public void display()
        {
            for(Cell cell : cells)
            {
                cell.display();
            }
        }

        public void update()
        {
            cellUpdater.resetCellsCount();
            int i = 0;
            i++;
            for(Cell cell : cells)
            {
                cellUpdater.updateCell(cell);
            }


            for(Cell cell : cells)
            {
                cell.state = cell.futureState;
                cell.futureState = 0;
            }
        }

        public Cell getCell(int x, int y)
        {
            return cells[y * boardWidth + x];
        }
    }

    public class Cell implements Displayable
    {
        public int x;
        public int y;
        public float state;
        public float futureState;
        public int cellColor;
        int size;
        public Cell(int _x, int _y, int _size, int _state)
        {
            x = _x;
            y = _y;
            size = _size;
            state = _state;
            futureState = state;
        }

        public void display()
        {
            fill(cellColor);
            rect(x * size, y * size, size, size);
        }
    }

    void loadConfiguration(String fileName) {
        XML root = loadXML(fileName);
        try
        {
            XML frameSizeXml = root.getChild("frameSize");
            int x = frameSizeXml.getInt("width");
            int y = frameSizeXml.getInt("height");

            surface.setSize(x, y);
            println("Frame size set to " + x + " x " + y);
        }
        catch (Exception e)
        {
            println("Incorrect or no frame size given. Using default");
        }
        XML typeXML = root.getChild("type");
        radius = root.getChild("radius").getIntContent();
        type = typeXML.getContent().toLowerCase();
        println("type: " + type);

        String neighborhoodTemp = root.getChild("neighborhood").getContent().toLowerCase();
        if(neighborhoodTemp.equals(NEUMAN.name().toLowerCase()))
            neighborhood = NEUMAN;
        else if(neighborhoodTemp.equals(MOORE.name().toLowerCase()))
            neighborhood = MOORE;

        if (!neighborhood.equals(MOORE) && !neighborhood.equals(NEUMAN))
            throw new IllegalArgumentException(String.format("Invalid neighborhood for %s.\nWas %s but expected %s or %s", CAType.LIFE.name(), neighborhood, MOORE, NEUMAN));
        println("neighborhood: " + neighborhood);

        cellSize = root.getChild("cellSize").getIntContent();
        println("cellSize: " + cellSize);

        float framerate = 0;
        try {
            framerate = root.getChild("frameRate").getFloatContent();
            if(framerate < 0)
            {
                println("Given framerate is lower than 0. Using default framerate");
            }
            else{
                println("Framerate: ", framerate);
                frameRate(framerate);
            }
        } catch (Exception e)
        {
            println("No framerate given. Using default framerate");
        }

        try {
            XML displayXML = root.getChild("display");
            strokeWeight = displayXML.getChild("strokeWeight").getFloatContent();

            strokeColor = getColor(displayXML.getChild("strokeColor"));
            Sketch.println("strokeColor: " + strokeColor);
        } catch (Exception e) {
            Sketch.println("No display settings given or invalid display settings format. " + e.getMessage());

        }

        if(type.equals(CAType.LIFE.name().toLowerCase()))
        {
            loadLife(root);
        } else if(type.equals(CAType.LIFEAVERAGE.name().toLowerCase()))
        {
            loadLifeAverage(root);
        }

    }


    int getColor(XML colorElement) {
        int r = colorElement.getInt("r");
        int g = colorElement.getInt("g");
        int b = colorElement.getInt("b");
        return color(r, g, b);
    }

    void loadLife(XML root)
    {
        println("Loading life neighborhood " + neighborhood);

        switch (neighborhood) {
            case MOORE:
                loadMooreLife(root);
                break;
            case NEUMAN:
                loadNeumanLife(root);
                break;
        }
    }

    void loadLifeAverage(XML root)
    {
        println("Loading lifeAverage neighborhood " + neighborhood);

        switch (neighborhood) {
            case MOORE:
                loadMooreLifeAverage(root);
                break;
            case NEUMAN:
                loadNeumanLifeAverage(root);
                break;
        }
    }

    void loadNeumanLifeAverage(XML root)
    {
        loadMooreLifeAverage(root);
    }

    void loadMooreLifeAverage(XML root)
    {
        XML ruleXML = root.getChild("rule");
        ruleMap = new HashMap<Integer, Pair<Integer, Integer>>();

        //int colorCount = 256;

        XML[] ruleEntries = ruleXML.getChildren("rules/entry");
        //if(colorCount != ruleEntries.length)
        //    throw new IllegalArgumentException(String.format("Invalid number of color entries in %s in %s neighborhood.\nExpected %d but was %s.", CAType.LIFEAVERAGE.name(), MOORE.name(), colorCount, ruleEntries.length));

        for(XML ruleEntry : ruleEntries)
        {
            int _key = ruleEntry.getInt("key");
            int newState = ruleEntry.getInt("newState");
            int color = getColorFromString(ruleEntry.getString("color"));
            ruleMap.put(_key, new Pair<Integer, Integer>(newState, color));
        }
    }

    void loadMooreLife(XML root)
    {
        int defaultDeadColor;
        int defaultAliveColor;
        XML ruleXML = root.getChild("rule");
        String ruleType = ruleXML.getChild("ruleType").getContent().toLowerCase();
        if(!ruleType.equals(RuleType.MATCH.name().toLowerCase()) && !ruleType.equals(RuleType.COUNT.name().toLowerCase()))
            throw new IllegalArgumentException(String.format("Invalid rule type for %s.\nWas %s but expected %s or %s", CAType.LIFE.name(), ruleType, RuleType.COUNT.name().toLowerCase(), RuleType.MATCH.name().toLowerCase()));

        ruleMap = new HashMap<Integer, Pair<Integer, Integer>>();
        colorMap = new HashMap<Integer, Pair<Integer, Integer>>();

        defaultAliveColor = getDefaultAliveColor(ruleXML);
        defaultDeadColor = getDefaultDeadColor(ruleXML);

        println("radius: "+radius);

        if(ruleType.equals(RuleType.COUNT.name().toLowerCase())) {
            //int ruleEntriesCount = Helpers.pow(3, radius + 1);
            int ruleEntriesCount = Helpers.pow(2*radius + 1, 2);
            XML[] ruleEntries = ruleXML.getChildren("rules/entry");
            if (ruleEntries.length != ruleEntriesCount)
                throw new IllegalArgumentException(String.format("Invalid number of rule entries in %s in %s neighborhood.\nExpected %d but was %s.", CAType.LIFE.name(), MOORE.name(), ruleEntriesCount, ruleEntries.length));
            for (XML ruleEntry : ruleEntries) {
                int _key = ruleEntry.getInt("key");
                ruleMap.put(_key, getRule(ruleEntry));

                colorMap.put(_key, getColorPair(ruleEntry, defaultAliveColor, defaultDeadColor));
            }
        }
        else if(ruleType.equals(RuleType.MATCH.name().toLowerCase())) {
        }
    }

    void loadNeumanLife(XML root)
    {
        int defaultDeadColor;
        int defaultAliveColor;
        XML ruleXML = root.getChild("rule");
        String ruleType = ruleXML.getChild("ruleType").getContent().toLowerCase();
        if(!ruleType.equals(RuleType.MATCH.name().toLowerCase()) && !ruleType.equals(RuleType.COUNT.name().toLowerCase()))
            throw new IllegalArgumentException(String.format("Invalid rule type for %s.\nWas %s but expected %s or %s", CAType.LIFE.name(), ruleType, RuleType.COUNT.name().toLowerCase(), RuleType.MATCH.name().toLowerCase()));

        ruleMap = new HashMap<>();
        colorMap = new HashMap<>();

        println("radius: "+radius);

        defaultAliveColor = getDefaultAliveColor(ruleXML);
        defaultDeadColor = getDefaultDeadColor(ruleXML);

        if(ruleType.equals(RuleType.COUNT.name().toLowerCase())) {
            int ruleEntriesCount = 4 * radius + 1;
            XML[] ruleEntries = ruleXML.getChildren("rules/entry");
            if (ruleEntries.length != ruleEntriesCount)
                throw new IllegalArgumentException(String.format("Invalid number of rule entries in %s in %s neighborhood.\nExpected %d but was %s.", CAType.LIFE.name(), MOORE, ruleEntriesCount, ruleEntries.length));
            for (XML ruleEntry : ruleEntries) {
                int _key = ruleEntry.getInt("key");
                ruleMap.put(_key, getRule(ruleEntry));

                colorMap.put(_key, getColorPair(ruleEntry, defaultAliveColor, defaultDeadColor));
            }
        }
        else if(ruleType.equals(RuleType.MATCH.name().toLowerCase())) {
        }
    }

    private int getDefaultAliveColor(XML ruleXML)
    {
        int defaultAliveColor;
        try{
            defaultAliveColor = getColorFromString(ruleXML.getChild("defaultColors").getChild("alive").getString("colorIfAlive"));
        }
        catch (Exception e)
        {
            defaultAliveColor = color(255);
        }
        return defaultAliveColor;
    }
    private int getDefaultDeadColor(XML ruleXML)
    {
        int defaultDeadColor;
        try{
            defaultDeadColor = getColorFromString(ruleXML.getChild("defaultColors").getChild("dead").getString("colorIfDead"));
        }
        catch (Exception e)
        {
            defaultDeadColor = color(255);
        }
        return defaultDeadColor;
    }

    private Pair<Integer, Integer> getRule(XML ruleEntry)
    {
        int _key = ruleEntry.getInt("key");
        int valueIfAlive = ruleEntry.getInt("valueIfAlive");
        int valueIfDead = ruleEntry.getInt("valueIfDead");
        println("_key: "+_key);
        println("valueIfAlive: "+valueIfAlive);
        println("valueIfDead: "+valueIfDead);
        println("");
        Pair <Integer, Integer> p = new Pair<Integer, Integer>(valueIfAlive, valueIfDead);

        return p;
    }

    private Pair<Integer, Integer> getColorPair(XML ruleEntry, int defaultIfAlive, int defaultIfDead)
    {
        int colorIfAlive;
        int colorIfDead;
        try
        {
            colorIfAlive = getColorFromString(ruleEntry.getString("colorIfAlive"));
        } catch (Exception e)
        {
            colorIfAlive = defaultIfAlive;
        }
        try
        {
            colorIfDead = getColorFromString(ruleEntry.getString("colorIfDead"));
        } catch (Exception e)
        {
            colorIfDead = defaultIfDead;
        }

        Pair<Integer, Integer> colorPair = new Pair<Integer, Integer>(colorIfAlive, colorIfDead);
        return colorPair;
    }

    int getColorFromString(String colorString)
    {
        String[] colors = colorString.split(" ");
        int r = Integer.parseInt(colors[0]);
        int g = Integer.parseInt(colors[1]);
        int b = Integer.parseInt(colors[2]);
        return color(r, g, b);
    }

    void loadInitialState(String fileName) throws Exception
    {
        XML root = loadXML(fileName);
        XML initialStateRoot = root.getChild("initialState");
        String initialStateType = initialStateRoot.getChild("initialStateType").getContent().toLowerCase();

        if(!initialStateType.equals(InitialStateType.RANDOM.name().toLowerCase()) && !initialStateType.equals(InitialStateType.GIVEN.name().toLowerCase()))
            throw new IllegalArgumentException(String.format("Invalid initialStateType .\nWas %s but expected %s or %s", initialStateType, InitialStateType.RANDOM.name(), InitialStateType.GIVEN.name()));

        println("initialStateType: "+initialStateType);

        if(initialStateType.equals(InitialStateType.RANDOM.name().toLowerCase()))
        {
            loadRandom(initialStateRoot);
        }else if(initialStateType.equals(InitialStateType.GIVEN.name().toLowerCase()))
        {
            loadGiven(initialStateRoot);
        }

    }

    void loadRandom(XML initialStateRoot)
    {
        float probability = initialStateRoot.getChild("initialAliveProbability").getFloatContent();

        println("probability: "+probability);

        if(!type.equals("lifeaverage")) {
            for (Cell c : board.cells) {
                if (random((float) 1.0) < probability)
                    c.state = 1;
            }
        }
        else{
            for (Cell c : board.cells) {
                c.state = random(ruleMap.size() - 1);
            }
        }
    }

    void loadGiven(XML initialStateRoot)
    {
        XML[] entriesXML = initialStateRoot.getChild("aliveCells").getChildren("entry");
        for(XML entry : entriesXML)
        {
            int x = entry.getInt("x");
            int y = entry.getInt("y");
            int state = entry.getIntContent(1);
            board.getCell(x, y).state = state;
        }
    }


    float prevMouseX;
    float prevMouseY;
    float offX;
    float offY;


    public void mouseClicked()
    {
        int mX = (int)(mouseX / scale);
        int mY = (int)(mouseY / scale);
        int x = (int)((mX - translateX / scale) / board.cellSize);
        int y = (int)((mY - translateY / scale) / board.cellSize);
        clickedCell = board.getCell(x, y);
        clickedCell.futureState = 255;
    }

    public void mousePressed()
    {
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    public void mouseDragged()
    {
        translateX += mouseX - prevMouseX;
        translateY += mouseY - prevMouseY;
        reboundTranslation();
        prevMouseX = mouseX;
        prevMouseY = mouseY;

    }


    public void mouseWheel(MouseEvent e)
    {
        scale = scale - e.getAmount() * zoomSpeed;
        if(scale < 1.0)
        {
            scale = 1;
            translateX = 0;
            translateY = 0;
        }
        else
        {
            translateX += e.getAmount() * mouseX * zoomSpeed;

            translateY += e.getAmount() * mouseY * zoomSpeed;
            //mouseClicked();
            reboundTranslation();
        }
    }

    public void reboundTranslation()
    {
        if(translateX < width - width*scale)
            translateX = width - width*scale;
        if(translateX > 0)
            translateX = 0;
        if(translateY < height - height*scale)
            translateY = height - height*scale;
        if(translateY > 0)
            translateY = 0;
    }

    public void getConfigFile()
    {
        selectInput("Select configuration file", "getFileCallback");
    }
    public void getFileCallback(File selection)
    {
        if(selection == null)
            filename = "no file";
        else
            filename = selection.getAbsolutePath();
        println("DONE " + filename);
    }
}
