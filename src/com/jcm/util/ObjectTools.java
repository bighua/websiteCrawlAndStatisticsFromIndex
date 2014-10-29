package com.jcm.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

public class ObjectTools {

	public static final Object byteStreamToObject(byte[] bytes) {
		if (null != bytes && 0 < bytes.length) {
			ByteArrayInputStream bis = null;
			ObjectInputStream ois = null;
			try {
				bis = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bis);
				return ois.readObject();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (null != bis) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (null != ois) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public static final byte[] objectToByteStream(Object obj) {
		if (null != obj) {
			ObjectOutputStream oos = null;
			ByteArrayOutputStream bos = null;
			try {
				bos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(bos);
				oos.writeObject(obj);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != bos) {
					try {
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != oos)
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

			if (null != bos) {
				try {
					// return (new String(bos.toByteArray(), "ISO-8859-1"));
					// return URLEncoder.encode(new String(bos.toByteArray(),
					// "ISO-8859-1"), "ISO-8859-1");
					return (bos.toByteArray());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
