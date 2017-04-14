//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2017.02.23 时间 09:50:49 AM CST 
//


package com.cfido.commons.spring.dict.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dictXmlRow" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="todo" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="usedCount" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="html" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="dictAttachmentRow" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="pathPrefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="extName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="imageFile" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="imageWidth" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="imageHeight" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dictXmlRow",
    "dictAttachmentRow"
})
@XmlRootElement(name = "dictXml")
public class DictXml {

    protected List<DictXml.DictXmlRow> dictXmlRow;
    protected List<DictXml.DictAttachmentRow> dictAttachmentRow;

    /**
     * Gets the value of the dictXmlRow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dictXmlRow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDictXmlRow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DictXml.DictXmlRow }
     * 
     * 
     */
    public List<DictXml.DictXmlRow> getDictXmlRow() {
        if (dictXmlRow == null) {
            dictXmlRow = new ArrayList<DictXml.DictXmlRow>();
        }
        return this.dictXmlRow;
    }

    /**
     * Gets the value of the dictAttachmentRow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dictAttachmentRow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDictAttachmentRow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DictXml.DictAttachmentRow }
     * 
     * 
     */
    public List<DictXml.DictAttachmentRow> getDictAttachmentRow() {
        if (dictAttachmentRow == null) {
            dictAttachmentRow = new ArrayList<DictXml.DictAttachmentRow>();
        }
        return this.dictAttachmentRow;
    }


    /**
     * <p>anonymous complex type的 Java 类。
     * 
     * <p>以下模式片段指定包含在此类中的预期内容。
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="pathPrefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="extName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="imageFile" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="imageWidth" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="imageHeight" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "pathPrefix",
        "extName",
        "memo"
    })
    public static class DictAttachmentRow {

        @XmlElement(required = true)
        protected String pathPrefix;
        @XmlElement(required = true)
        protected String extName;
        protected String memo;
        @XmlAttribute(name = "key", required = true)
        protected String key;
        @XmlAttribute(name = "imageFile", required = true)
        protected boolean imageFile;
        @XmlAttribute(name = "imageWidth", required = true)
        protected int imageWidth;
        @XmlAttribute(name = "imageHeight", required = true)
        protected int imageHeight;

        /**
         * 获取pathPrefix属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPathPrefix() {
            return pathPrefix;
        }

        /**
         * 设置pathPrefix属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPathPrefix(String value) {
            this.pathPrefix = value;
        }

        /**
         * 获取extName属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getExtName() {
            return extName;
        }

        /**
         * 设置extName属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setExtName(String value) {
            this.extName = value;
        }

        /**
         * 获取memo属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMemo() {
            return memo;
        }

        /**
         * 设置memo属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMemo(String value) {
            this.memo = value;
        }

        /**
         * 获取key属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKey() {
            return key;
        }

        /**
         * 设置key属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKey(String value) {
            this.key = value;
        }

        /**
         * 获取imageFile属性的值。
         * 
         */
        public boolean isImageFile() {
            return imageFile;
        }

        /**
         * 设置imageFile属性的值。
         * 
         */
        public void setImageFile(boolean value) {
            this.imageFile = value;
        }

        /**
         * 获取imageWidth属性的值。
         * 
         */
        public int getImageWidth() {
            return imageWidth;
        }

        /**
         * 设置imageWidth属性的值。
         * 
         */
        public void setImageWidth(int value) {
            this.imageWidth = value;
        }

        /**
         * 获取imageHeight属性的值。
         * 
         */
        public int getImageHeight() {
            return imageHeight;
        }

        /**
         * 设置imageHeight属性的值。
         * 
         */
        public void setImageHeight(int value) {
            this.imageHeight = value;
        }

    }


    /**
     * <p>anonymous complex type的 Java 类。
     * 
     * <p>以下模式片段指定包含在此类中的预期内容。
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="todo" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="usedCount" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="html" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value",
        "memo"
    })
    public static class DictXmlRow {

        @XmlElement(required = true)
        protected String value;
        protected String memo;
        @XmlAttribute(name = "todo", required = true)
        protected boolean todo;
        @XmlAttribute(name = "usedCount", required = true)
        protected int usedCount;
        @XmlAttribute(name = "html", required = true)
        protected boolean html;
        @XmlAttribute(name = "key", required = true)
        protected String key;

        /**
         * 获取value属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * 设置value属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * 获取memo属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMemo() {
            return memo;
        }

        /**
         * 设置memo属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMemo(String value) {
            this.memo = value;
        }

        /**
         * 获取todo属性的值。
         * 
         */
        public boolean isTodo() {
            return todo;
        }

        /**
         * 设置todo属性的值。
         * 
         */
        public void setTodo(boolean value) {
            this.todo = value;
        }

        /**
         * 获取usedCount属性的值。
         * 
         */
        public int getUsedCount() {
            return usedCount;
        }

        /**
         * 设置usedCount属性的值。
         * 
         */
        public void setUsedCount(int value) {
            this.usedCount = value;
        }

        /**
         * 获取html属性的值。
         * 
         */
        public boolean isHtml() {
            return html;
        }

        /**
         * 设置html属性的值。
         * 
         */
        public void setHtml(boolean value) {
            this.html = value;
        }

        /**
         * 获取key属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKey() {
            return key;
        }

        /**
         * 设置key属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKey(String value) {
            this.key = value;
        }

    }

}
