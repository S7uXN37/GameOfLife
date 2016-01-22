package main;

import java.util.HashSet;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;

public class InputInterface implements KeyListener, MouseListener {
	private Game game;
	private boolean shiftDown = false;
	
	public InputInterface(Game g) {
		game = g;
	}
	
	@Override
	public void setInput(Input input) {}
	
	@Override
	public boolean isAcceptingInput() {
		return true;
	}
	
	@Override
	public void inputEnded() {}
	
	@Override
	public void inputStarted() {}
	
	@Override
	public void keyPressed(int key, char c) {
		switch(key) {
			case Input.KEY_ESCAPE:
				game.close();
				break;
			case Input.KEY_R:
				if (shiftDown)
					game.resetSaves();
				else
					game.resetMap();
				break;
			case Input.KEY_SPACE:
				game.toggleSimulation();
				break;
			case Input.KEY_F:
				game.tick();
				break;
			case Input.KEY_LEFT:
				Game.ticksPerSec -= 1F;
				break;
			case Input.KEY_RIGHT:
				Game.ticksPerSec += 1F;
				break;
			case Input.KEY_T:
				game.toggleTutorial();
				break;
			case Input.KEY_LSHIFT:
			case Input.KEY_RSHIFT:
				shiftDown = true;
				break;
			case Input.KEY_1:
				if (shiftDown)
					game.save(0);
				else
					game.load(0);
				break;
			case Input.KEY_2:
				if (shiftDown)
					game.save(1);
				else
					game.load(1);
				break;
			case Input.KEY_3:
				if (shiftDown)
					game.save(2);
				else
					game.load(2);
				break;
			case Input.KEY_4:
				if (shiftDown)
					game.save(3);
				else
					game.load(3);
				break;
			case Input.KEY_5:
				if (shiftDown)
					game.save(4);
				else
					game.load(4);
				break;
			case Input.KEY_6:
				if (shiftDown)
					game.save(5);
				else
					game.load(5);
				break;
			case Input.KEY_7:
				if (shiftDown)
					game.save(6);
				else
					game.load(6);
				break;
			case Input.KEY_8:
				if (shiftDown)
					game.save(7);
				else
					game.load(7);
				break;
			case Input.KEY_9:
				if (shiftDown)
					game.save(8);
				else
					game.load(8);
				break;
			case Input.KEY_0:
				if (shiftDown)
					game.save(9);
				else
					game.load(9);
				break;
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {
		switch (key) {
			case Input.KEY_LSHIFT:
			case Input.KEY_RSHIFT:
				shiftDown = false;
				break;
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		if (!game.isInputEnabled())
			return;
		
		game.map[Util.absCoordsToField(x, y)] = dragMode;
	}
	
	boolean dragMode;
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if (!game.isInputEnabled())
			return;
		
		HashSet<Integer> fieldsLookedAt = new HashSet<Integer>();
		
		float percent = 0;
		while (percent <= 1) {
			int field = Util.absCoordsToField((int) ((oldx-newx) * percent + oldx), (int) ((oldy-newy) * percent + oldy));
			if (field != -1)
			if (!fieldsLookedAt.contains(field)) {
				game.map[field] = dragMode;
				fieldsLookedAt.add(field);
			}
			percent += 0.0001;
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {}

	@Override
	public void mousePressed(int button, int x, int y) {
		dragMode = button == 0;
	}

	@Override
	public void mouseReleased(int button, int x, int y) {}

	@Override
	public void mouseWheelMoved(int change) {}
}
