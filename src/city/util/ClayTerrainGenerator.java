package city.util;

import java.awt.Point;
import java.util.Random;

import main.ClayConstants;

import org.bushe.swing.event.EventBus;

import screens.AbstractScreen;
import city.generics.data.BuildingData;
import city.generics.entities.BuildingEntity;

public class ClayTerrainGenerator
{
	public ClayTerrainGenerator(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}

	public void buildTerrain(BuildingEntity[][] tiles_, int iterations_, int x_, Point prevPoints_)
	{
		createBumpyTerrain(tiles_, iterations_, x_, prevPoints_);
		placeClayChunks(tiles_, _random.nextInt(_xCells), _yCells - 1, 6, 0);
		placeClayChunks(tiles_, _random.nextInt(_xCells), _yCells - 1, 6, 0);
		placeClayChunks(tiles_, _random.nextInt(_xCells), _yCells - 1, 6, 0);
		placeClayChunks(tiles_, _random.nextInt(_xCells), _yCells - 1, 6, 0);
		placeClayChunks(tiles_, _random.nextInt(_xCells), _yCells - 1, 6, 0);
		EventBus.publish(new MapUpdateEvent(_homeScreen));
	}

	public void createBumpyTerrain(BuildingEntity[][] tiles_, int iterations_, int x_, Point prevPoints_)
	{
		if (iterations_ == 0)
		{
			return;
		}
		setHeight(tiles_, x_, _random.nextInt(3 * _yCells / 4));
		iterations_--;
		if (x_ == prevPoints_.x || x_ == prevPoints_.y)
			return;
		createBumpyTerrain(
				tiles_,
				iterations_,
				(x_ + prevPoints_.x) / 2,
				new Point(prevPoints_.x, x_));
		createBumpyTerrain(
				tiles_,
				iterations_,
				(x_ + prevPoints_.y) / 2,
				new Point(prevPoints_.y, x_));
		smooth(tiles_, x_, prevPoints_.x);
		smooth(tiles_, x_, prevPoints_.y);
	}

	private void setHeight(BuildingEntity[][] tiles_, int x_, int height_)
	{
		for (int y = 0; y < _yCells; y++)
		{
			tiles_[x_][y] = y < height_ ? new BuildingEntity(
					BuildingData.getBuildingByTag("dirt-block"),
					new Point(x_ * ClayConstants.TILE_X, y
							* ClayConstants.TILE_Y), _homeScreen, "base") : null;
		}
	}

	private void smooth(BuildingEntity[][] tiles_, int index_, int finalIndex_)
	{
		int indexHeight = getHeight(tiles_, index_);
		int finalHeight = getHeight(tiles_, finalIndex_);
		int indexDiff = Math.abs(finalIndex_ - index_);
		int addedHeight = (finalHeight + indexHeight) / indexDiff;
		addedHeight *= finalHeight < indexHeight ? -1 : 1;
		if (index_ < finalIndex_)
		{
			for (int columnIndex = index_; columnIndex < finalIndex_; columnIndex++)
			{
				indexHeight += addedHeight + addNoise(_random.nextInt(10));
				addHeight(tiles_, columnIndex, indexHeight);
			}
		}
		else if (index_ > finalIndex_)
		{
			for (int columnIndex = index_; columnIndex >= finalIndex_; columnIndex--)
			{
				indexHeight += addedHeight + addNoise(_random.nextInt(10));
				addHeight(tiles_, columnIndex, indexHeight);
			}
		}
	}

	private void addHeight(BuildingEntity[][] tiles_, int columnIndex_, int height_)
	{
		setHeight(
				tiles_,
				columnIndex_,
				(height_ + getHeight(tiles_, columnIndex_)) / 2);
	}

	private int getHeight(BuildingEntity[][] tiles_, int x_)
	{
		for (int y = 0; y < _yCells - 1; y++)
		{
			if (tiles_[x_][y] == null)
				return y;
		}
		return 0;
	}

	private int addNoise(int noise)
	{
		if (noise == 0)
			return 0;
		return _random.nextInt(noise) - _random.nextInt(noise);
	}

	public void placeClayChunks(BuildingEntity[][] tiles_, int x_, int y_, int iterations_, int max_)
	{
		int max = max_;
		if (x_ < 0 || x_ >= _xCells)
			return;
		for (; y_ > max; y_--)
		{
			if (tiles_[x_][y_] != null
					&& tiles_[x_][y_].getBuildingTag().equals("dirt-block"))
			{
				tiles_[x_][y_] = new BuildingEntity(
						BuildingData.getBuildingByTag("natural-clay"),
						new Point(x_ * ClayConstants.TILE_X, y_
								* ClayConstants.TILE_Y), _homeScreen,
						ClayConstants.DEFAULT_BUILDING_POSITION);
				if (iterations_ > 0)
				{
					max += 3;
					placeClayChunks(tiles_, x_ - 1, y_, iterations_ - 1, max);
					placeClayChunks(tiles_, x_ + 1, y_, iterations_ - 1, max);
				}
			}
		}

	}

	private AbstractScreen _homeScreen;
	private Random _random = new Random();

	private int _xCells = ClayConstants.DEFAULT_MAP_WIDTH
			/ ClayConstants.TILE_X;
	private int _yCells = ClayConstants.DEFAULT_MAP_HEIGHT
			/ ClayConstants.TILE_Y;

}
