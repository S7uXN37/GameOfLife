package main;

import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.Log;

public class Game extends BasicGame
{
	public static int GRID_SIZE_X = 50;
	public static int GRID_SIZE_Y = 50;
	public static int PX_PER_GRID = 10;
	public static int BORDER_SIZE = 10;
	public static float ticksPerSec = 5F;
	public static final Color GRID_COLOR = new Color(1F, 1F, 1F, 0.5F);
	public static final Color BCKG_COLOR = Color.black;
	public static final Color OFF_COLOR = Color.gray;
	public static final Color ON_COLOR = Color.cyan;
	public static final Color ALIVE_COLOR = Color.blue;
	public static final Color DEAD_COLOR = Color.yellow;
	
	public GameContainer gameContainer;
	
	private boolean closeRequested = false;
	protected org.newdawn.slick.Font smallFont;
	protected static int WIDTH;
	protected static int HEIGHT;
	
	int updatesPerSecond = 0;
	float tickTimer = 0F;
	boolean[] map;
	boolean isSimulating = false;
	
	public Game(String gamename)
	{
		super(gamename);
		map = new boolean[GRID_SIZE_X * GRID_SIZE_Y];
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		gameContainer = gc;
		gameContainer.setAlwaysRender(true);
		smallFont = new TrueTypeFont(new Font("Arial", Font.PLAIN, 10), true);
		reset();
	}
	
	public void reset() {
		map = new boolean[GRID_SIZE_X * GRID_SIZE_Y];
		
		if (isSimulating)
			toggleSimulation();
		
		closeRequested = false;
		
		gameContainer.getInput().removeAllKeyListeners();
		gameContainer.getInput().addKeyListener(new InputInterface(this));
		gameContainer.getInput().removeAllMouseListeners();
		gameContainer.getInput().addMouseListener(new InputInterface(this));
	}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		updatesPerSecond = (updatesPerSecond + 1000/delta)/2;
		
		if (isSimulating) {
			if (tickTimer <= 0F) {
				tickTimer = 1/ticksPerSec;
				
				tick();
			} else {
				tickTimer -= delta/1000F;
			}
		}
		
		if(closeRequested) {
			Log.info("Close has been requested, exiting...");
			container.exit();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		// draw borders
		g.setColor(isSimulating ? ON_COLOR : OFF_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(BCKG_COLOR);
		g.fillRect(BORDER_SIZE,
			BORDER_SIZE,
			WIDTH-2*BORDER_SIZE,
			HEIGHT-2*BORDER_SIZE
		);
		
		// draw grid
		g.setColor(GRID_COLOR);
		for(int i=1 ; i<GRID_SIZE_X ; i++) { // vertical lines
			g.drawLine(i*PX_PER_GRID + BORDER_SIZE,
				BORDER_SIZE,
				i*PX_PER_GRID + BORDER_SIZE,
				HEIGHT - BORDER_SIZE
			);
		}
		for(int i=1 ; i<GRID_SIZE_Y ; i++) { // horizontal lines
			g.drawLine(BORDER_SIZE,
				i*PX_PER_GRID + BORDER_SIZE,
				WIDTH - BORDER_SIZE,
				i*PX_PER_GRID + BORDER_SIZE
			);
		}
		
		// draw cells
		for(int i=0; i < map.length; i++) {
			int[] absCoords = Util.fieldToAbsCoords(i);
			g.setColor(map[i] ? ALIVE_COLOR : DEAD_COLOR);
			g.fillRect(
					absCoords[0]+1, absCoords[2]+1,
					Math.abs(absCoords[1]-absCoords[0])-2,
					Math.abs(absCoords[3]-absCoords[2])-2
			);
		}
		
		// draw update counter
		g.setColor(Color.black);
		g.setFont(smallFont);
		g.drawString(""+updatesPerSecond, 2, -2);;
	}
	
	private void tick() {
		HashMap<Integer, Boolean> changes = new HashMap<Integer, Boolean>();
		
		for (int x = 0; x < GRID_SIZE_X; x++) {
			for (int y = 0; y < GRID_SIZE_Y; y++) {
				int newVal = -1;
				boolean isLive = map[Util.coordsToField(x, y)];
				int adjLive = 0;
				
				for (int _x = x-1; _x <= x+1; _x++) {
					for (int _y = y-1; _y <= y+1; _y++) {
						if (_x < 0 || _x >= GRID_SIZE_X || _y < 0 || _y >= GRID_SIZE_Y)
							continue;
						if (_x == x && _y == y)
							continue;
						if (map[Util.coordsToField(_x, _y)])
							adjLive++;
					}
				}
				
				if (isLive) {
					if (adjLive < 2 || adjLive > 3)
						newVal = 0;
				} else if (adjLive == 3) {
					newVal = 1;
				}
				
				if (newVal != -1)
					changes.put(Util.coordsToField(x, y), newVal == 1);
			}
		}
		
		Iterator<Integer> it = changes.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			map[key] = changes.get(key);
		}
	}
	
	public static void main(String[] args) {
		try
		{
			WIDTH = PX_PER_GRID * GRID_SIZE_X + BORDER_SIZE*2;
			HEIGHT = PX_PER_GRID * GRID_SIZE_Y + BORDER_SIZE*2;
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Game("Game Of Life"));
			appgc.setDisplayMode(WIDTH, HEIGHT, false);
			appgc.setShowFPS(false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void close() {
		closeRequested = true;
	}
	
	public void toggleSimulation() {
		isSimulating = !isSimulating;
	}
}