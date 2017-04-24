package com.cfido.commons.utils.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * 各类编码工具，包括base64 desc md5 rsa等等
 * 
 * @author liangwj
 * 
 */
public class EncryptUtil {

	public static class GenKeyResult {
		private final RSAPrivateKey privateKey;
		private final RSAPublicKey publicKey;

		private final String privateKeyStr;
		private final String publicKeyStr;

		public GenKeyResult(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
			super();
			this.privateKey = privateKey;
			this.publicKey = publicKey;

			Encoder b = Base64.getEncoder();
			this.privateKeyStr = new String(b.encode(privateKey.getEncoded()));
			this.publicKeyStr = new String(b.encode(publicKey.getEncoded()));
		}

		public RSAPrivateKey getPrivateKey() {
			return privateKey;
		}

		public String getPrivateKeyStr() {
			return privateKeyStr;
		}

		public RSAPublicKey getPublicKey() {
			return publicKey;
		}

		public String getPublicKeyStr() {
			return publicKeyStr;
		}

	}

	private final static char[] hexDigits = "0123456789abcdef".toCharArray();

	/**
	 * 字节数组 -- 十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String byteArray2String(byte[] byteArray) {
		StringBuffer resultSb = new StringBuffer(33);
		for (int i = 0; i < byteArray.length; i++) {

			int value = byteArray[i];
			if (value < 0) {
				value += 256;
			}
			int d1 = value / 16;
			int d2 = value % 16;
			resultSb.append(hexDigits[d1]).append(hexDigits[d2]);
		}
		return resultSb.toString();
	}

	/**
	 * 十六进制字符串 --- 字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] byteString2Array(String str) {
		if (str == null)
			return null;

		byte key[] = new BigInteger(str, 16).toByteArray();
		if (key[0] != 0) {
			return key;
		} else {
			byte[] res = new byte[key.length - 1];
			for (int i = 1; i < key.length; i++) {
				res[i - 1] = key[i];
			}
			return res;
		}
	}

	/**
	 * des解密，字节数组
	 * 
	 * @param keyString
	 *            秘钥
	 * @param input
	 *            要解密的内容
	 * @return 解密后的内容
	 * @throws EncryptException
	 *             错误
	 */
	public static byte[] desDecrypt(String keyString, byte[] input) throws EncryptException {
		try {

			// DES算法要求有一个可信任的随机数源
			SecureRandom random = new SecureRandom();
			Key key = getDesSecretKey(keyString);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, key, random);
			// 真正开始解密操作
			return cipher.doFinal(input);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	public static String desDecryptBase64String(String keyString, String base64String) throws EncryptException {
		byte rawByte[] = desDecrypt(keyString, org.apache.commons.codec.binary.Base64.decodeBase64(base64String.getBytes()));
		return new String(rawByte);
	}

	public static String desDecryptAscll(String keyString, String ascllString) throws EncryptException {
		byte rawByte[] = desDecrypt(keyString, byteString2Array(ascllString));
		return new String(rawByte);
	}

	/**
	 * 加密，字节数组
	 * 
	 * @param keyString
	 * @param input
	 * @return
	 * @throws EncryptException
	 */
	public static byte[] desEncrypt(String keyString, byte[] input) throws EncryptException {
		try {
			SecureRandom random = new SecureRandom();

			Key key = getDesSecretKey(keyString);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, key, random);
			// 正式执行加密操作
			return cipher.doFinal(input);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 加密，base64方式编码输出
	 * 
	 * @param keyString
	 * @param data
	 * @return
	 * @throws EncryptException
	 */
	public static String desEncryptBase64String(String keyString, String data) throws EncryptException {
		byte dataByte[] = desEncrypt(keyString, data.getBytes());
		return new String(org.apache.commons.codec.binary.Base64.encodeBase64(dataByte));
	}

	/**
	 * 加密，base64方式编码输出
	 * 
	 * @param keyString
	 * @param data
	 * @return
	 * @throws EncryptException
	 */
	public static String desEncryptAscllString(String keyString, String data) throws EncryptException {
		byte dataByte[] = desEncrypt(keyString, data.getBytes());
		return byteArray2String(dataByte);
	}

	/**
	 * 随机生成RSA密钥对
	 * 
	 * @throws EncryptException
	 */
	public static GenKeyResult genRsaKeyPair() throws EncryptException {
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptException(e);
		}
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		GenKeyResult res = new GenKeyResult((RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
		return res;
	}

	public static String getSignForAddCoin(String accountName, String orderId, int coin, int reward) {
		StringBuffer sb = new StringBuffer(100);
		sb.append(accountName);
		sb.append('\t');
		sb.append("senatry4758");
		sb.append('\t');
		sb.append(orderId);
		sb.append('\t');
		sb.append(coin);
		sb.append('\t');
		sb.append(reward);
		String sign = EncryptUtil.md5(sb.toString());
		return sign;
	}

	public static RSAPrivateKey loadRsaPrivateKey(String privateKeyStr) throws EncryptException {
		try {
			byte[] buffer = Base64.getDecoder().decode(privateKeyStr.getBytes());
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws EncryptException
	 *             加载公钥时产生的异常
	 */
	public static RSAPublicKey loadRsaPublicKey(String publicKeyStr) throws EncryptException {
		try {
			byte[] buffer = Base64.getDecoder().decode(publicKeyStr.getBytes());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	public static String md5(String origin) {
		String resultString = null;
		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] byteArray = md.digest(resultString.getBytes());
			return byteArray2String(byteArray);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 私钥解密过程
	 * 
	 * @param privateKey
	 *            私钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws EncryptException
	 *             解密过程中的异常信息
	 */
	public static byte[] rsaDecrypt(RSAPrivateKey privateKey, byte[] cipherData) throws EncryptException {
		if (privateKey == null || cipherData == null || cipherData.length == 0) {
			throw new IllegalArgumentException("参数不能为空");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			// cipher = Cipher.getInstance("RSA");
			// 使用指令初始化,否则与android端加密的无法匹配
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 公钥解密过程
	 * 
	 * @param publicKey
	 *            公钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws EncryptException
	 *             解密过程中的异常信息
	 */
	public static byte[] rsaDecrypt(RSAPublicKey publicKey, byte[] cipherData) throws EncryptException {
		if (publicKey == null || cipherData == null || cipherData.length == 0) {
			throw new IllegalArgumentException("参数不能为空");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			// cipher = Cipher.getInstance("RSA");
			// 使用指令初始化,否则与android端加密的无法匹配
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 私钥加密过程
	 * 
	 * @param privateKey
	 *            私钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws EncryptException
	 *             加密过程中的异常信息
	 */
	public static byte[] rsaEncrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws EncryptException {
		if (privateKey == null || plainTextData == null || plainTextData.length == 0) {
			throw new IllegalArgumentException("参数不能为空");
		}
		try {
			// 使用默认RSA
			// Cipher cipher = Cipher.getInstance("RSA");
			// 使用指令初始化,否则与android端加密的无法匹配
			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 公钥加密过程
	 * 
	 * @param publicKey
	 *            公钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws EncryptException
	 *             加密过程中的异常信息
	 */
	public static byte[] rsaEncrypt(RSAPublicKey publicKey, byte[] plainTextData) throws EncryptException {
		if (publicKey == null || plainTextData == null || plainTextData.length == 0) {
			throw new IllegalArgumentException("参数不能为空");
		}
		try {
			// 使用默认RSA
			// Cipher cipher = Cipher.getInstance("RSA");
			// 使用指令初始化,否则与android端加密的无法匹配
			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 在原加密工具类的基础上实现分段加密,自动满足所有长度的byte数组加密
	 * 
	 * @param key
	 *            加密的公钥
	 * @param data
	 *            待加密的数据
	 * @return 加密后的结果
	 * @throws Exception
	 */
	public static byte[] rsaEncryptBySplite(RSAPublicKey key, byte[] data) throws Exception {
		byte[] result = null;
		try {
			int bufferSize = key.getModulus().bitLength() / 8;
			byte[][] spliteData = spliteData(data, bufferSize - 11);
			result = new byte[spliteData.length * bufferSize];
			int i = 0;
			// 分段加密
			for (byte[] dataToEnc : spliteData) {
				byte[] dataEnc = rsaEncrypt(key, dataToEnc);
				System.arraycopy(dataEnc, 0, result, i++ * bufferSize, bufferSize);
			}
		} catch (Exception e) {
			throw new EncryptException(e);
		}
		return result;
	}

	/**
	 * 在原加密工具类的基础上实现分段解密,自动满足所有长度的byte数组解密
	 * 
	 * @param key
	 *            解密的私钥
	 * @param data
	 *            待解密的数据
	 * @return 解密后的结果
	 * @throws Exception
	 */
	public static byte[] rsaDecryptBySplite(RSAPrivateKey key, byte[] data) throws Exception {
		byte[] result = null;
		try {
			int bufferSize = key.getModulus().bitLength() / 8;
			byte[][] spliteData = spliteData(data, bufferSize);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			// 分段解密
			for (byte[] dataToDec : spliteData) {
				byte[] dataDec = rsaDecrypt(key, dataToDec);
				// 不同sdk版本会有不同的补0方式,采取去掉的方式处理
				int cutPadding = 0;
				for (byte b : dataDec) {
					if (b == 0) {
						++cutPadding;
					} else {
						break;
					}
				}
				output.write(dataDec, cutPadding, dataDec.length - cutPadding);
			}
			result = output.toByteArray();
			output.flush();
			output.close();
		} catch (Exception e) {
			throw new EncryptException(e);
		}
		return result;
	}

	/**
	 * 位移加密
	 * 
	 * @param offset
	 *            位移数
	 * @param data
	 *            加密内容
	 * @return
	 * @throws Exception
	 */
	public static byte[] offsetEncrypt(int offset, byte[] data) throws Exception {
		byte[] result = new byte[data.length];
		// 位移数不超过byte长度
		if (offset >= 8 || offset <= -8) {
			offset = offset % 8;
		}
		if (offset != 0) {
			try {
				int i = 0;
				// 位移方向判断
				if (offset > 0) {
					for (byte b : data) {
						// byte进行移位操作时,自动转型为32为的int类型,并且若最高位为1,则int类型补进的位数全部为1,需要通过b
						// & 0xFF来清除
						result[i] = (byte) (((b & 0xFF) >>> offset) | (b << 8 - offset));
						++i;
					}
				} else {
					offset = -offset;
					for (byte b : data) {
						// byte进行移位操作时,自动转型为32为的int类型,并且若最高位为1,则int类型补进的位数全部为1,需要通过b
						// & 0xFF来清除
						result[i] = (byte) ((b << offset) | ((b & 0xFF) >>> 8 - offset));
						++i;
					}
				}

			} catch (Exception e) {
				throw new Exception("CryptUtil->offsetEncrypt:" + e.getMessage());
			}
		}
		return result;
	}

	/**
	 * 位移解密
	 * 
	 * @param offset
	 *            位移数
	 * @param data
	 *            加密内容
	 * @return
	 * @throws Exception
	 */
	public static byte[] offsetDecrypt(int offset, byte[] data) throws Exception {
		byte[] result = new byte[data.length];
		// 位移数不超过byte长度
		if (offset >= 8) {
			offset = offset % 8;
		}
		if (offset != 0) {
			try {
				int i = 0;
				// 位移方向判断
				if (offset > 0) {
					for (byte b : data) {
						// byte进行移位操作时,自动转型为32为的int类型,并且若最高位为1,则int类型补进的位数全部为1,需要通过b
						// & 0xFF来清除
						result[i] = (byte) ((b << offset) | ((b & 0xFF) >>> 8 - offset));
						++i;
					}
				} else {
					offset = -offset;
					for (byte b : data) {
						// byte进行移位操作时,自动转型为32为的int类型,并且若最高位为1,则int类型补进的位数全部为1,需要通过b
						// & 0xFF来清除
						result[i] = (byte) (((b & 0xFF) >>> offset) | (b << 8 - offset));
						++i;
					}
				}

			} catch (Exception e) {
				throw new Exception("CryptUtil->offsetEncrypt:" + e.getMessage());
			}
		}
		return result;
	}

	private static Key getDesSecretKey(String password)
			throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException {
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		return securekey;
	}

	/**
	 * 获取目标数组根据分组长度分配的二维数组
	 * 
	 * @param data
	 *            目标数组
	 * @param buffsize
	 *            分组长度
	 * @return 结果二维数组
	 */
	private static byte[][] spliteData(byte[] data, int buffsize) {
		int x = data.length / buffsize;
		int y = data.length % buffsize;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[buffsize];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * buffsize, arr, 0, y);
			} else {
				System.arraycopy(data, i * buffsize, arr, 0, buffsize);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

}
