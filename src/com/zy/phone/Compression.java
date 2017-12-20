package com.zy.phone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
/**
 * —πÀı¿‡
 * @author lws
 *
 */
public class Compression {
	
	public static byte[] getGZipCompressed(String data) {
		byte[] compressed = null;
		if (data.getBytes().length <= 100) {
			return data.getBytes();
		} else {
			try {
				byte[] byteData = data.getBytes();
				ByteArrayOutputStream bos = new ByteArrayOutputStream(
						byteData.length);
				Deflater compressor = new Deflater();
				compressor.setLevel(Deflater.BEST_COMPRESSION); 
				compressor.setInput(byteData, 0, byteData.length);
				compressor.finish(); 
				final byte[] buf = new byte[1024];
				while (!compressor.finished()) {
					int count = compressor.deflate(buf);
					bos.write(buf, 0, count);
				}
				compressor.end();
				compressed = bos.toByteArray();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return compressed;
		}
	}
	public static byte[] getGZipUncompress(byte[] data) {
		byte[] unCompressed = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
			Inflater decompressor = new Inflater();
			try {
				decompressor.setInput(data);
				final byte[] buf = new byte[1024];
				while (!decompressor.finished()) {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				}

				unCompressed = bos.toByteArray();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				decompressor.end();
			}
			return unCompressed;
		} catch (Exception e) {
		}
		return unCompressed;
	}

	
	public static byte[] compress(byte[] data) {
		byte[] output = new byte[0];

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		compresser.end();
		return output;
	}

	
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		decompresser.end();
		return output;
	}

}
