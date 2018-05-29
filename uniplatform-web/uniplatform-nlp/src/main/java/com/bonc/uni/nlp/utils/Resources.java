package com.bonc.uni.nlp.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import com.bonc.usdp.odk.logmanager.LogManager;


@SuppressWarnings("serial")
public class Resources extends Properties {

	private static Resources instance_ = null;

	/**
	 * Simple constructor.
	 */
	public Resources() {
		super();
	}

	public Resources(String resFileName) {
		super();
		LogManager.Debug("Path is:===========" + resFileName);
		initResources(resFileName);
	}

	public Resources(String resFileName, boolean withCommonParam) { 
		this(resFileName);
	}

	public static Resources getInstance() {
		if (instance_ == null) {
			instance_ = new Resources();
		}

		return instance_;
	}

	public void initResources(String resFileName) {

		if (resFileName == null)
			return;

		// Necessary when getInstance() is invoked
		if (instance_ == null)
			instance_ = this;
		try(InputStream input = new FileInputStream(resFileName);) {
			
			load(input);
			LogManager.Debug("file is loaded" + this.getProperty("ip"));
		} catch (Exception e1) {
			LogManager.Exception(e1);
		}
	}

	@SuppressWarnings("rawtypes")
	public Vector getVectorProperty(String key, char separator) {
		return getVectorProperty(key, separator, 1, true);
	}

	@SuppressWarnings("rawtypes")
	public Vector getVectorProperty(String key, char separator, int width) {
		return getVectorProperty(key, separator, width, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getVectorProperty(String key, char separator, int width, boolean vertical) {

		key = key.trim();
		if (width < 1) {
			LogManager.Debug("Invalid vector width");
			return new Vector();
		}

		Vector res = new Vector(width);
		Vector lineRes = new Vector();
		String rawProperty = getProperty(key);
		if (rawProperty == null)
			return res;
		if (rawProperty.length() < 1)
			return res;

		Vector[] columns = new Vector[width];
		for (int i = 0; i < width; i++)
			columns[i] = new Vector();

		Vector line = new Vector(width);
		int beginIndex = 0;
		int endIndex = 1;
		int col = 0;

		while (endIndex != -1) {
			endIndex = rawProperty.indexOf(separator, beginIndex);
			if (endIndex == -1) // No ending index found.
			{
				if (rawProperty.length() - beginIndex >= 1) { // Extracting the
																// last value
					String tempRes = rawProperty.substring(beginIndex, rawProperty.length());
					tempRes = tempRes.replace((char) 9, (char) 32); // clean
																	// tabs
					tempRes = tempRes.trim(); // clean spaces
					columns[col].add(tempRes);
					line.add(tempRes);
					if (col == width - 1) {
						lineRes.add(line);
					}
				}

				break;
			} else if (endIndex - beginIndex >= 1) {

				String tempRes = rawProperty.substring(beginIndex, endIndex);
				tempRes = tempRes.replace((char) 9, (char) 32);
				tempRes = tempRes.trim();
				columns[col].add(tempRes);
				line.add(tempRes);
				col++;
				if (col == width) {
					col = 0;
					lineRes.add(line);
					line = new Vector(width);
				}

			}
			beginIndex = endIndex + 1;
		}

		if (width == 1) {
			return columns[0]; // if width = 1, there is no need to make a
								// Vector of Vectors, so we return only one
								// Vector.
		}

		for (int i = 0; i < width - 1; i++) {
			if (columns[i].size() != columns[i + 1].size()) {

				LogManager.Debug("Cardinality Error in resource " + key + " on line : " + i);
				LogManager.Debug("===> " + columns[i]);
				return res;

			}
		}
		for (int i = 0; i < width; i++)
			res.add(columns[i]);

		if (vertical) {
			return res; // return a vector of columns
		} else {
			return lineRes; // return a vector of rows
		}
	}

	/**
	 * key is the name of the menuBar object, an integer is appended to it, we
	 * form a vector of (menu_name1, nemu_name2, menu_name3...) each menu_name
	 * identifies a Menu object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getHandleProperty(String key, char separator) {
		Vector _Vmenu = new Vector();
		boolean b = true;
		for (int i = 0; b; i++) {
			String s = key + i;
			Vector v = getVectorProperty(s, ',');
			if (!v.isEmpty())
				_Vmenu.add(v);
			else if (i > 0)
				b = false;
		}
		return _Vmenu;
	}

	public String getStringProperty(String key, String defaultVal) {
		String rawProperty = defaultVal;

		if (key != null) {
			key = key.trim();
			rawProperty = getProperty(key); // get value
			if (rawProperty != null) {
				rawProperty = rawProperty.trim(); // clean spaces
				rawProperty = rawProperty.replace((char) 9, (char) 32); // clean
																		// tabs
			} else {
				rawProperty = defaultVal;
			}
		} else {
			LogManager.Error("Resources.getStringProperty : unable to retrieve the ressource with key : null");
		}
		return rawProperty;
	}

	public String getStringProperty(String key) {
		return getStringProperty(key, null);
	}

	public int getIntProperty(String key, int defaut) //
	{
		Integer i = new Integer(defaut);
		String rawProperty = getStringProperty(key, i.toString());
		i = new Integer(rawProperty);
		return i.intValue();
	}

	public int getIntProperty(String key) {
		String rawProperty = getStringProperty(key);
		Integer i = new Integer(rawProperty);
		return i.intValue();
	}

	public float getFloatProperty(String key, float defaut) {
		Float f = new Float(defaut);
		String rawProperty = getStringProperty(key, f.toString());
		f = new Float(rawProperty);
		return f.floatValue();
	}

	public float getFloatProperty(String key) {
		String rawProperty = getStringProperty(key);
		Float f = new Float(rawProperty);
		return f.floatValue();
	}

	public boolean getBooleanProperty(String key, boolean defaut) {
		Boolean B = new Boolean(defaut);
		String rawProperty = getStringProperty(key, B.toString());
		rawProperty = rawProperty.trim();
		rawProperty = rawProperty.toLowerCase();

		return Boolean.parseBoolean(rawProperty);
	}

	public boolean getBooleanProperty(String key) {
		return getBooleanProperty(key, false);
	}

	public String computeProperty(String propertyName, String type, String release) {
		// Look first at type and release level
		String property = propertyName + "." + type + "." + release;
		if (containsKey(property)) {
			return property;
		}
		// else, look only at type level
		property = propertyName + "." + type;
		if (containsKey(property)) {
			return property;
		}
		// Finally, nothing is specified at type or release level
		// return the property itself
		return propertyName;
	}
}
