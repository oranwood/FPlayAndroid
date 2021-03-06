//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package br.com.carlosrafaelgn.fplay.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.util.SparseArray;

public final class SerializableMap {
	private static final int TYPE_INT = 0;
	private static final int TYPE_LONG = 1;
	private static final int TYPE_FLOAT = 2;
	private static final int TYPE_DOUBLE = 3;
	private static final int TYPE_STRING = 4;
	private static final int TYPE_BUFFER = 5;
	
	private final SparseArray<Object> dict;
	
	public SerializableMap() {
		dict = new SparseArray<Object>();
	}
	
	public SerializableMap(int initialCapacity) {
		dict = new SparseArray<Object>(initialCapacity);
	}
	
	public boolean serialize(Context context, String fileName) {
		FileOutputStream fs = null;
		try {
			fs = context.openFileOutput(fileName, 0);
			final byte[] buf = new byte[16];
			for (int i = dict.size() - 1; i >= 0; i--) {
				final int k = dict.keyAt(i);
				final Object v = dict.get(k);
				Serializer.serializeInt(buf, 0, k);
				if (v instanceof Integer) {
					buf[4] = TYPE_INT;
					Serializer.serializeInt(buf, 5, (Integer)v);
					fs.write(buf, 0, 9);
				} else if (v instanceof Long) {
					buf[4] = TYPE_LONG;
					Serializer.serializeLong(buf, 5, (Long)v);
					fs.write(buf, 0, 13);
				} else if (v instanceof Float) {
					buf[4] = TYPE_FLOAT;
					Serializer.serializeFloat(buf, 5, (Float)v);
					fs.write(buf, 0, 9);
				} else if (v instanceof Double) {
					buf[4] = TYPE_DOUBLE;
					Serializer.serializeDouble(buf, 5, (Double)v);
					fs.write(buf, 0, 13);
				} else if (v instanceof String) {
					final String tmpS = v.toString();
					byte[] tmpB = null;
					int tmp = 0;
					if (tmpS != null && tmpS.length() > 0) {
						tmpB = tmpS.getBytes();
						tmp = tmpB.length;
					}
					buf[4] = TYPE_STRING;
					Serializer.serializeInt(buf, 5, tmp);
					fs.write(buf, 0, 9);
					if (tmp != 0) fs.write(tmpB, 0, tmp);
				} else {
					final byte[] tmpB = (byte[])v;
					final int tmp = (tmpB != null ? tmpB.length : 0);
					buf[4] = TYPE_BUFFER;
					Serializer.serializeInt(buf, 5, tmp);
					fs.write(buf, 0, 9);
					if (tmp != 0) fs.write(tmpB, 0, tmp);
				}
			}
			return true;
		} catch (Throwable ex) {
		} finally {
			try {
				if (fs != null)
					fs.close();
			} catch (Throwable ex) {
			}
		}
		return false;
	}

	public static SerializableMap deserialize(Context context, String fileName) {
		FileInputStream fs = null;
		try {
			fs = context.openFileInput(fileName);
			byte[] buf = new byte[16];
			int tmp;
			SerializableMap dict = new SerializableMap(32);
			while (fs.read(buf, 0, 9) == 9) {
				switch ((int)buf[4]) {
				case TYPE_INT:
					dict.put(Serializer.deserializeInt(buf, 0), Serializer.deserializeInt(buf, 5));
					break;
				case TYPE_LONG:
					if (fs.read(buf, 9, 4) != 4) return dict;
					dict.put(Serializer.deserializeInt(buf, 0), Serializer.deserializeLong(buf, 5));
					break;
				case TYPE_FLOAT:
					dict.put(Serializer.deserializeInt(buf, 0), Serializer.deserializeFloat(buf, 5));
					break;
				case TYPE_DOUBLE:
					if (fs.read(buf, 9, 4) != 4) return dict;
					dict.put(Serializer.deserializeInt(buf, 0), Serializer.deserializeDouble(buf, 5));
					break;
				case TYPE_STRING:
					tmp = Serializer.deserializeInt(buf, 5);
					if (tmp <= 0) {
						dict.put(Serializer.deserializeInt(buf, 0), "");
					} else {
						byte[] tmpB = new byte[tmp];
						if (fs.read(tmpB, 0, tmp) != tmp) return dict;
						dict.put(Serializer.deserializeInt(buf, 0), new String(tmpB, 0, tmp));
					}
					break;
				case TYPE_BUFFER:
					tmp = Serializer.deserializeInt(buf, 5);
					if (tmp <= 0) {
						dict.put(Serializer.deserializeInt(buf, 0), new byte[0]);
					} else {
						byte[] tmpB = new byte[tmp];
						if (fs.read(tmpB, 0, tmp) != tmp) return dict;
						dict.put(Serializer.deserializeInt(buf, 0), tmpB);
					}
					break;
				default:
					return dict;
				}
			}
			return dict;
		} catch (Throwable ex) {
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (Throwable ex) {
				}
			}
		}
		return null;
	}

	public boolean containsKey(int key) {
		return (dict.indexOfKey(key) >= 0);
	}

	public void remove(int key) {
		dict.remove(key);
	}

	public void put(int key, boolean value) {
		dict.put(key, (value ? (int)1 : (int)0));
	}

	public void put(int key, int value) {
		dict.put(key, value);
	}

	public void put(int key, long value) {
		dict.put(key, value);
	}

	public void put(int key, float value) {
		dict.put(key, value);
	}

	public void put(int key, double value) {
		dict.put(key, value);
	}

	public void put(int key, String value) {
		dict.put(key, value);
	}

	public void put(int key, byte[] value) {
		dict.put(key, value);
	}

	public Object get(int key) {
		return dict.get(key);
	}

	public Object get(int key, Object defaultValue) {
		final Object o = dict.get(key);
		return ((o == null) ? defaultValue : o);
	}

	public boolean getBoolean(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Integer))
			return false;
		return ((Integer)o != 0);
	}

	public boolean getBoolean(int key, boolean defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Integer))
			return defaultValue;
		return ((Integer)o != 0);
	}

	public int getInt(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Integer))
			return 0;
		return (Integer)o;
	}

	public int getInt(int key, int defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Integer))
			return defaultValue;
		return (Integer)o;
	}

	public long getLong(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Long))
			return 0;
		return (Long)o;
	}

	public long getLong(int key, long defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Long))
			return defaultValue;
		return (Long)o;
	}

	public float getFloat(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Float))
			return 0;
		return (Float)o;
	}

	public float getFloat(int key, float defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Float))
			return defaultValue;
		return (Float)o;
	}

	public double getDouble(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Double))
			return 0;
		return (Double)o;
	}

	public double getDouble(int key, double defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof Double))
			return defaultValue;
		return (Double)o;
	}

	public String getString(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof String))
			return null;
		return o.toString();
	}

	public String getString(int key, String defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof String))
			return defaultValue;
		return o.toString();
	}

	public byte[] getBuffer(int key) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof byte[]))
			return null;
		return (byte[])o;
	}

	public byte[] getBuffer(int key, byte[] defaultValue) {
		final Object o = dict.get(key);
		if (o == null || !(o instanceof byte[]))
			return defaultValue;
		return (byte[])o;
	}
}
