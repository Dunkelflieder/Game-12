package strategy;

import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSNodeRoot;
import de.nerogar.noise.serialization.NDSWriter;
import strategy.core.map.CoreMap;

import java.io.FileNotFoundException;
import java.util.Random;

public class MapPatcher {

	private static final String mapID = "map1";

	private static final int dimX = 128;
	private static final int dimY = 32;
	private static final int dimZ = 128;

	/*
	public void loadMapGenerate(T map, String mapID) {

		dimX = 256;
		dimY = 128;
		dimZ = 256;

		blocks = new short[dimX * dimY * dimZ];
		shapes = new byte[dimX * dimY * dimZ];

		// test

		Random rand = new Random(0);

		for (int x = 0; x < dimX; x++) {
			for (int z = 0; z < dimZ; z++) {

				float heightx = (float) (Math.sin((float) x / 10f) * 5);
				float heightz = (float) (Math.sin((float) z / 10f) * 5);

				float distMult = (float) Math.sqrt((x * x) + (z * z)) / 200f;
				distMult = Math.min(1, Math.max(0, distMult - 0.3f));

				int height = (int) (heightx * heightz * distMult) + 35;

				for (int y = 0; y < height; y++) {
					short id = (short) (rand.nextInt(4) + 1);
					map.updateBlock(x, y, z, id, CORNER_ALL);
				}

				if (rand.nextFloat() < distMult) {
					byte randShape = (byte) (rand.nextInt(0xF) + 1);
					short id = (short) (rand.nextInt(4) + 1);
					map.updateBlock(x, height, z, id, randShape);
				}

			}
		}

		done = true;

	}*/

	private static void generateBlocks(short[] blocks, byte[] shapes) {
		Random rand = new Random(0);

		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				for (int z = 0; z < dimZ; z++) {
					int index = ((z * dimX + x) * dimY) + y;

					if (y < dimY / 2) {
						blocks[index] = (short) ((rand.nextInt(4)) + 1);
						shapes[index] = CoreMap.CORNER_ALL;
					} else if (y == dimY / 2) {
						if (rand.nextFloat() < 0.01) {
							blocks[index] = (short) (rand.nextInt(4) + 1);
							shapes[index] = (byte) (rand.nextInt(0xF) + 1);
						}
					}
				}
			}
		}
	}

	private static void generateSystems(NDSNodeObject systemsObject) {
		// blocks
		NDSNodeObject blocksObject = new NDSNodeObject("blocks");
		short[] blocks = new short[dimX * dimY * dimZ];
		byte[] shapes = new byte[dimX * dimY * dimZ];
		generateBlocks(blocks, shapes);
		blocksObject.addShortArray("blocks", blocks);
		blocksObject.addByteArray("shapes", shapes);
		systemsObject.addObject(blocksObject);
	}

	private static void createMap(NDSFile file) {
		file.setType("mapV1");

		NDSNodeRoot data = file.getData();

		// meta
		NDSNodeObject metaObject = new NDSNodeObject("meta");
		metaObject.addInt("dimX", dimX);
		metaObject.addInt("dimY", dimY);
		metaObject.addInt("dimZ", dimZ);
		data.addObject(metaObject);

		// systems
		NDSNodeObject systemsObject = new NDSNodeObject("systems");
		generateSystems(systemsObject);
		data.addObject(systemsObject);

		// entities
		NDSNodeObject[] entityIdMap = new NDSNodeObject[0];
		data.addObjectArray("entityIdMap", entityIdMap);

		NDSNodeObject[] entities = new NDSNodeObject[0];
		data.addObjectArray("entities", entities);
	}

	public static void main(String[] args) throws FileNotFoundException {
		NDSFile file = new NDSFile();
		createMap(file);

		NDSWriter.writeFile(file, "mapsCopy/" + mapID + ".map");
	}

}
