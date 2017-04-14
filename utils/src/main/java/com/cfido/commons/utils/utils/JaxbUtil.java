package com.cfido.commons.utils.utils;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <pre>
 * JaxbUtil 工具集合
 * </pre>
 * 
 * @author 梁韦江 2015年9月1日
 */
public class JaxbUtil {

	@SuppressWarnings("unchecked")
	public static <T> T parserXml(Class<T> clazz, String xmlStr) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		T xmlObj = (T) jaxbUnmarshaller.unmarshal(new StringReader(xmlStr));
		return xmlObj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T parserXml(Class<T> clazz, File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		T xmlObj = (T) jaxbUnmarshaller.unmarshal(file);
		return xmlObj;
	}

	/**
	 * 将xml对象保存到文件
	 * 
	 * @param xmlObj
	 * @param file
	 * @throws JAXBException
	 */
	public static void save(Object xmlObj, File file) throws JAXBException {
		Class<?> clazz = xmlObj.getClass();

		if (clazz.getAnnotation(XmlRootElement.class) == null) {
			throw new IllegalArgumentException("传入的对象必须是jaxb生成的类");
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(xmlObj, file);
	}

	/**
	 * 将xml对象变成xml
	 * 
	 * @param xmlObj
	 * @throws JAXBException
	 * 
	 * @return
	 */
	public static String toXmlString(Object xmlObj) throws JAXBException {
		Class<?> clazz = xmlObj.getClass();

		if (clazz.getAnnotation(XmlRootElement.class) == null) {
			throw new IllegalArgumentException("传入的对象必须是jaxb生成的类");
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		StringWriter w = new StringWriter();
		jaxbMarshaller.marshal(xmlObj, w);
		return w.toString();
	}
}
