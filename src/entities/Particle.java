package entities;

import level.Level;

public abstract class Particle extends Entity{

	public Entity owner = null;
	
	public Particle(Level l){super(l);}
	public Particle(Level level, Entity owner, int x, int y, int color){
		super(level, x, y, color);
		this.owner = owner;
	}
	
}
