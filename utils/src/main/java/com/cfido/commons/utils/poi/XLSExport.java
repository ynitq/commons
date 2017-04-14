package com.cfido.commons.utils.poi;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * excel 导出辅助类
 * 说明：HHSF方式的（对应2007以前版本的，一般用这个就好，版本可以向下兼容的，如果要用2007以后带x后缀的版本，请用XSSF方式读写，此处不提供辅助类）
 * </pre>
 * 
 * @author 黄云 2015-10-8
 */
public class XLSExport {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(XLSExport.class);

	// 设置日期格式
	private static String DATE_FORMAT = "yyyy-mm-dd";

	private final String xlsFileName;

	private final HSSFWorkbook workbook;

	private final HSSFSheet sheet;

	private HSSFRow row;

	/**
	 * 初始化Excel
	 * 
	 * @param fileName
	 *            导出文件名
	 */
	public XLSExport(String fileName) {
		this.xlsFileName = fileName;
		this.workbook = new HSSFWorkbook();
		this.sheet = workbook.createSheet();
	}

	/**
	 * 设置自动列宽--按中文宽度来
	 * 
	 */
	private void setAutoSizeColumn() {
		if (sheet.getRow(0) != null) {
			int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();
			for (int i = 0; i <= coloumNum; i++) {
				this.sheet.autoSizeColumn(i);
			}
			// 由于对中文自动宽度支持不好，这个地方把所有宽度*1.5
			int curColWidth = 0;
			for (int i = 0; i <= coloumNum; i++) {
				curColWidth = sheet.getColumnWidth(i);
				sheet.setColumnWidth(i, (int) (curColWidth * 1.5));
			}
		}
	}

	/**
	 * 导出Excel文件
	 * 
	 */
	public void exportXLS(HttpServletResponse response) {
		try {
			setAutoSizeColumn();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			workbook.write(os);
			byte[] bytes = os.toByteArray();
			String fileName = xlsFileName;

			response.reset();
			response.setContentType("application/msexcel;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'"+URLEncoder.encode(fileName, "UTF-8")+".xls ");
			response.getOutputStream().write(bytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (FileNotFoundException e) {
			LogUtil.traceError(log, e);
		} catch (IOException e) {
			LogUtil.traceError(log, e);
		}
	}
	
	/**
	 * 写Excel文件到服务器地址
	 * 
	 */
	public void writeXLS() {
		try {
			setAutoSizeColumn();
			FileOutputStream fOut = new FileOutputStream(xlsFileName);
			workbook.write(fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			LogUtil.traceError(log, e);
		} catch (IOException e) {
			LogUtil.traceError(log, e);
		}
	}

	/**
	 * 增加一行
	 * 
	 * @param index
	 *            行号
	 */
	public void createRow(int index) {
		this.row = this.sheet.createRow(index);
	}

	/**
	 * 设置行样式--这个根据自己喜好搞一搞去，我就不管了(●'◡'●)
	 * 
	 * @param cellStyle
	 *            行样式
	 */
	public void setRowCss(HSSFCellStyle cellStyle) {
		this.row.setRowStyle(cellStyle);
	}

	/**
	 * 设置单元格--字符型
	 * 
	 * @param index
	 *            列号
	 * @param value
	 *            单元格填充值
	 */
	public void setCell(int index, String value) {
		HSSFCell cell = this.row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(value);
	}

	/**
	 * 设置单元格--日期型：java.util.Date
	 * 
	 * @param index
	 *            列号
	 * @param value
	 *            单元格填充值
	 */
	public void setCell(int index, Date value) {
		HSSFCell cell = this.row.createCell(index);
		cell.setCellValue(value);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		HSSFDataFormat format = workbook.createDataFormat();
		cellStyle.setDataFormat(format.getFormat(DATE_FORMAT));
		cell.setCellStyle(cellStyle);
	}

	/**
	 * 设置单元格--Int数字型
	 * 
	 * @param index
	 *            列号
	 * @param value
	 *            单元格填充值
	 */
	public void setCell(int index, int value) {
		HSSFCell cell = this.row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
	}

	/**
	 * 设置单元格--浮点型
	 * 
	 * @param index
	 *            列号
	 * @param value
	 *            单元格填充值
	 */
	public void setCell(int index, double value) {
		HSSFCell cell = this.row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		cell.setCellStyle(cellStyle);
	}

	/**
	 * 设置单元格--金额型：java.math.BigDecimal
	 * 
	 * @param index
	 *            列号
	 * @param value
	 *            单元格填充值
	 */
	public void setCell(int index, BigDecimal value) {
		HSSFCell cell = this.row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value.doubleValue());
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		HSSFDataFormat format = workbook.createDataFormat();
		cellStyle.setDataFormat(format.getFormat("0.00"));
		cell.setCellStyle(cellStyle);
	}

	/**
	 * 测试用(●'◡'●)
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(" 开始导出Excel文件 ");
		XLSExport e = new XLSExport("d:/test.xls");
		e.createRow(0);
		e.setCell(0, " 编号 ");
		e.setCell(1, " 名称 ");
		e.setCell(2, " 日期 ");
		e.setCell(3, " 金额 ");
		e.setCell(4, " 浮点 ");
		e.createRow(1);
		e.setCell(0, 1);
		e.setCell(1, " 工商银行 ");
		e.setCell(2, new Date());
		e.setCell(3, 111123.99);
		e.setCell(4, 111123.99);
		e.createRow(2);
		e.setCell(0, 2);
		e.setCell(1, " 招商银行 ");
		e.setCell(2, new Date());
		e.setCell(3, 222456.88);
		e.setCell(4, 222456.88);

		try {
			e.writeXLS();
			System.out.println(" 导出Excel文件[成功] ");
		} catch (Exception e1) {
			System.out.println(" 导出Excel文件[失败] ");
			e1.printStackTrace();
		}
	}

}
