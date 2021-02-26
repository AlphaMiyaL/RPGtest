package world;

public class Tile {
	private byte id;
	private String texture;
	private boolean solid;
	public static Tile tiles[] = new Tile[255]; //number of tiles in the game
	public static byte numOfTiles = 0;
	public static final Tile grassTile = new Tile("grass_1");
	public static final Tile waterTile = new Tile("water_1").setSolid();
	public static final Tile treeTile =  new Tile("tree_1").setSolid();
	public static final Tile sTreeTile =  new Tile("tree_2").setSolid();
	public static final Tile woodTile = new Tile("wood_1");
	
	public Tile(String texture) {
		this.id = numOfTiles;
		numOfTiles++;
		this.texture = texture;
		if(tiles[id] != null) {
			throw new IllegalStateException("Tiles at [" +id+ "] is already being used!");
		}
		tiles[id]=this;
		this.solid=false;
	}

	public byte getId() {
		return id;
	}

	public String getTexture() {
		return texture;
	}
	
	public Tile setSolid() {
		this.solid=true;
		return this;
	}
	
	public boolean isSolid() {
		return solid;
	}
}
