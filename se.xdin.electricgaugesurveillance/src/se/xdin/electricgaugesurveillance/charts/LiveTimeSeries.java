package se.xdin.electricgaugesurveillance.charts;

import java.io.Serializable;
import java.sql.Date;

import org.achartengine.model.TimeSeries;
import org.achartengine.util.IndexXYMap;
import org.achartengine.util.MathHelper;
import org.achartengine.util.XYEntry;

public class LiveTimeSeries extends TimeSeries implements Serializable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 2114989014991412602L;

public LiveTimeSeries(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

/** The series title. */
  private String mTitle;
  /** A map to contain values for X and Y axes and index for each bundle */
  private final IndexXYMap<Double, Double> mXY = new IndexXYMap<Double, Double>();
  /** The minimum value for the X axis. */
  private double mMinX = MathHelper.NULL_VALUE;
  /** The maximum value for the X axis. */
  private double mMaxX = -MathHelper.NULL_VALUE;
  /** The minimum value for the Y axis. */
  private double mMinY = MathHelper.NULL_VALUE;
  /** The maximum value for the Y axis. */
  private double mMaxY = -MathHelper.NULL_VALUE;
  /** The scale number for this series. */
  
  private int maxPoints = 200;

  /**
   * Initializes the range for both axes.
   */
  private void initRange() {
    mMinX = MathHelper.NULL_VALUE;
    mMaxX = -MathHelper.NULL_VALUE;
    mMinY = MathHelper.NULL_VALUE;
    mMaxY = -MathHelper.NULL_VALUE;
    int length = getItemCount();
    for (int k = 0; k < length; k++) {
      double x = getX(k);
      double y = getY(k);
      updateRange(x, y);
    }
  }
  
  public void setMaxPoints(int maxPoints) {
	  this.maxPoints = maxPoints;
  }
  
  public int getMaxPoints() {
	  return this.maxPoints;
  }

  /**
   * Updates the range on both axes.
   *
   * @param x the new x value
   * @param y the new y value
   */
  private void updateRange(double x, double y) {
    mMinX = Math.min(mMinX, x);
    mMaxX = Math.max(mMaxX, x);
    mMinY = Math.min(mMinY, y);
    mMaxY = Math.max(mMaxY, y);
  }

  /**
   * Returns the series title.
   *
   * @return the series title
   */
  public String getTitle() {
    return mTitle;
  }

  /**
   * Sets the series title.
   *
   * @param title the series title
   */
  public void setTitle(String title) {
    mTitle = title;
  }

  /**
   * Adds a new value to the series.
   *
   * @param x the value for the X axis
   * @param y the value for the Y axis
   */
  public synchronized void add(double x, double y) {
	  if (mXY.size() >= maxPoints) {
		  remove(0);
	  }
    mXY.put(x, y);
    updateRange(x, y);
  }
  
  public synchronized void add(Date x, double y) {
	   	add(x.getTime(), y);
  }

  /**
   * Removes an existing value from the series.
   *
   * @param index the index in the series of the value to remove
   */
  public synchronized void remove(int index) {
    XYEntry<Double, Double> removedEntry = mXY.removeByIndex(index);
    double removedX = removedEntry.getKey();
    double removedY = removedEntry.getValue();
    if (removedX == mMinX || removedX == mMaxX || removedY == mMinY || removedY == mMaxY) {
      initRange();
    }
  }
}