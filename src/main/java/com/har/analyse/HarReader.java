package com.har.analyse;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HarReader<T> {

	public static <T> T readHar(URI path, Class<T> clazz) throws IOException {

		Path p = Paths.get(path);

		byte[] b = Files.readAllBytes(p);

		ObjectMapper mapper = new ObjectMapper();

		T data = mapper.readValue(b, clazz);

		return data;
	}

	public static Map<String, BodySpeed> readLogFile(URI path) throws IOException {
		Path p = Paths.get(path);
		byte[] b = Files.readAllBytes(p);

		String s = new String(b);

		Map<String, BodySpeed> data = Arrays.stream(s.split("\r\n")).map(string -> string.split("=BodySpeed", 2))
				.collect(Collectors.toMap(key -> key[0], val -> {

					String[] value = val[1].replace("[", "").replace("]", "").replace(":::", "").trim().split(" ");
					BodySpeed bs = new BodySpeed();
					bs.setBodySize(new Double(value[0].split("=")[1]));
					bs.setTime(new Double(value[1].split("=")[1]));

					return bs;
				}));

		return data;
	}

	public static Map<String, BodySpeed[]> readCompareFile(URI path) throws IOException {
		Path p = Paths.get(path);
		byte[] b = Files.readAllBytes(p);
		String s = new String(b);

		Map<String, BodySpeed[]> data = Arrays.stream(s.split("\r\n")).map(string -> string.split("=\\["))
				.collect(Collectors.toMap(key -> key[0], val -> {
					String[] value = val[1].replace("[", "").replace("]", "").replace(":::", "")
							.replace("BodySpeed", "").split(",");

					BodySpeed[] bss = { new BodySpeed(), new BodySpeed() };
					String[] minVal = value[0].trim().split(" ");
					String minBody = minVal[0].split("-")[1];
					String minTime = minVal[1].split("-")[1];

					bss[0].setBodySize(Double.parseDouble(minBody));
					bss[0].setTime(Double.parseDouble(minTime));

					if (!value[1].trim().equalsIgnoreCase("null")) {
						String[] nonMinVal = value[1].trim().split(" ");
						String nonMinBody = nonMinVal[0].split("-")[1];
						String nonMinTime = nonMinVal[1].split("-")[1];

						bss[1].setBodySize(Double.parseDouble(nonMinBody));
						bss[1].setTime(Double.parseDouble(nonMinTime));
					}
					return bss;
				}));

		return data;
	}

	public static HSSFWorkbook createExcel(Map<String, BodySpeed[]> extract) {

		HSSFWorkbook workbook = new HSSFWorkbook();

		HSSFSheet jsSheet = workbook.createSheet("JavaScript");
		HSSFSheet cssSheet = workbook.createSheet("Cascading Style Sheets");
		HSSFSheet others = workbook.createSheet("Other Resources");

		HSSFRow jsHeadROw = jsSheet.createRow(0);
		jsHeadROw.createCell(0).setCellValue("Resource Name");
		jsHeadROw.createCell(1).setCellValue("GEC Size");
		jsHeadROw.createCell(2).setCellValue("GEC Time");
		jsHeadROw.createCell(3).setCellValue("OP Size");
		jsHeadROw.createCell(4).setCellValue("OP Time");

		HSSFRow cssHeadRow = cssSheet.createRow(0);
		cssHeadRow.createCell(0).setCellValue("Resource Name");
		cssHeadRow.createCell(1).setCellValue("GEC Size");
		cssHeadRow.createCell(2).setCellValue("GEC Time");
		cssHeadRow.createCell(3).setCellValue("OP Size");
		cssHeadRow.createCell(4).setCellValue("OP Time");

		HSSFRow otherHeadROw = others.createRow(0);
		otherHeadROw.createCell(0).setCellValue("Resource Name");
		otherHeadROw.createCell(1).setCellValue("GEC Size");
		otherHeadROw.createCell(2).setCellValue("GEC Time");
		otherHeadROw.createCell(3).setCellValue("OP Size");
		otherHeadROw.createCell(4).setCellValue("OP Time");

		int jsCounter = 1;
		int cssCounter = 1;
		int otherCoutner = 1;

		for (Map.Entry<String, BodySpeed[]> entry : extract.entrySet()) {

			String key = entry.getKey();
			BodySpeed[] bss = entry.getValue();


			if (key.indexOf(".js")>-1) {

				HSSFRow row = jsSheet.createRow(jsCounter);
				row.createCell(0).setCellValue(key);
				row.createCell(1).setCellValue(bss[0].getBodySize());
				row.createCell(2).setCellValue(bss[0].getTime());
				if (bss[1] != null) {
					row.createCell(3).setCellValue(bss[1].getBodySize());
					row.createCell(4).setCellValue(bss[1].getTime());
				}

				jsCounter++;

			} else if (key.indexOf(".css")>-1) {
				
				System.out.println("Inside CSS");
				
				HSSFRow row = cssSheet.createRow(cssCounter);
				row.createCell(0).setCellValue(key);
				row.createCell(1).setCellValue(bss[0].getBodySize());
				row.createCell(2).setCellValue(bss[0].getTime());
				if (bss[1] != null) {
					row.createCell(3).setCellValue(bss[1].getBodySize());
					row.createCell(4).setCellValue(bss[1].getTime());
				}

				cssCounter++;

			} else {
				
				
				System.out.println("Inside else");
				
				HSSFRow row = others.createRow(otherCoutner);
				row.createCell(0).setCellValue(key);
				row.createCell(1).setCellValue(bss[0].getBodySize());
				row.createCell(2).setCellValue(bss[0].getTime());
				if (bss[1] != null) {
					row.createCell(3).setCellValue(bss[1].getBodySize());
					row.createCell(4).setCellValue(bss[1].getTime());
				}

				otherCoutner++;
			}

		}

		return workbook;

	}

}
