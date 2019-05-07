package com.har.analyse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class MainMethod {

	private static final ExecutorService EXE = Executors.newFixedThreadPool(3);
	private static final String HAR_NAME = "localhostLatest.json";
	private static final String PAGE_TIME_PATH = "D:\\workspace\\SpringRest\\Har_Ana\\PageTime.log";
	private static final String PAGE_TIME_PATH_MIN = "D:\\workspace\\SpringRest\\Har_Ana\\PageTime_latest.log";
	private static final String FEQ_PATH = "D:\\workspace\\SpringRest\\Har_Ana\\Frequency.log";
	private static final String FEQ_PATH_MIN = "D:\\workspace\\SpringRest\\Har_Ana\\Frequency_latest.log";
	private static final String RES_SIZE_TIME_PATH = "D:\\workspace\\SpringRest\\Har_Ana\\Size_time.log";
	private static final String RES_SIZE_TIME_PATH_MIN = "D:\\workspace\\SpringRest\\Har_Ana\\Size_time_latest.log";
	private static final String COMPARE_LOG_PATH = "D:\\workspace\\SpringRest\\Har_Ana\\Compare.log";
	private static final String COMPARE_EXCEL = "D:\\workspace\\SpringRest\\Har_Ana\\Compare.xls";
	private static Logger LOGGER_PAGE = Logger.getLogger("PageTime");
	private static Logger LOGGER_FEQ = Logger.getLogger("Frequency");

	public static void main(String[] args) throws IOException, URISyntaxException {
		 createExtract();

		// compareMinFilesinSizeTime();
		//createExcel();
	}

	private static void createExcel() throws IOException {
		File compareFile = new File(COMPARE_LOG_PATH);
		URI path = compareFile.toURI();
		Map<String, BodySpeed[]> extract = HarReader.readCompareFile(path);

		HSSFWorkbook book = HarReader.createExcel(extract);
		
		
		book.write(new File(COMPARE_EXCEL));
		//System.out.println(extract);

	}

	private static void compareMinFilesinSizeTime() throws URISyntaxException, IOException {
		File _file = new File(RES_SIZE_TIME_PATH);
		File fileMin = new File(RES_SIZE_TIME_PATH_MIN);
		URI path = _file.toURI();
		URI pathMin = fileMin.toURI();

		Map<String, BodySpeed> extract = HarReader.readLogFile(path);
		Map<String, BodySpeed> extractMin = HarReader.readLogFile(pathMin);

		Map<String, BodySpeed> minFiles = extractMin.entrySet().parallelStream()
				// .filter(e->e.getKey().contains("min"))
				.sorted(Map.Entry.comparingByKey((s1, s2) -> {
					String[] fileSplit = s1.split("/");
					int len = fileSplit.length;
					String[] fileSplit2 = s2.split("/");
					int len2 = fileSplit2.length;
					return fileSplit[len - 1].compareTo(fileSplit2[len2 - 1]);
				})).collect(
						Collectors.toMap(
								key -> key.getKey().replace("min.", "").replace(".min", "").replace("min/", "")
										.replace("gec", "").replace("Gec", "").replace("v6.1.0/", ""),
								Map.Entry::getValue));

		Set<String> filesMin = minFiles.keySet().parallelStream().map(s -> {
			String[] fileSplit = s.split("/");
			int len = fileSplit.length;
			return fileSplit[len - 1];
		}).collect(Collectors.toSet());

		Map<String, BodySpeed> _minFiles = extract.entrySet().parallelStream()
				/*
				 * .filter(e -> { String file = e.getKey().replace("min.","").replace(".min",
				 * "").replace("debug/",""); return filesMin .parallelStream() .anyMatch(min ->
				 * file.contains(min)); })
				 */
				.sorted(Map.Entry.comparingByKey((s1, s2) -> {
					String[] fileSplit = s1.split("/");
					int len = fileSplit.length;
					String[] fileSplit2 = s2.split("/");
					int len2 = fileSplit2.length;

					return fileSplit[len - 1].compareTo(fileSplit2[len2 - 1]);
				})).collect(Collectors.toMap(e -> e.getKey().replace("min.", "").replace(".min", "")
						.replace("debug/", "").replace("v2.3.0/", ""), Map.Entry::getValue));

		System.out.println("Un Min" + _minFiles.size());
		System.out.println("Minned" + minFiles.size());
		System.out.println("File Name: " + filesMin.size());

		/*
		 * System.out.println("Unmin"+_minFiles.keySet());
		 * System.out.println("Minned"+minFiles.keySet());
		 */

		Map<String, String> map = minFiles.entrySet().parallelStream().map(entry -> {

			String fileName = entry.getKey();
			BodySpeed bs1 = entry.getValue();

			BodySpeed bs2 = _minFiles.get(fileName);
			System.out.println(fileName);
			System.out.println(bs2);

			return new Object[] { fileName, new BodySpeed[] { bs1, bs2 } };

		}).collect(Collectors.toMap(obj -> (String) obj[0], obj -> Arrays.toString((BodySpeed[]) obj[1]) + "\r\n"));

		FileAppender fa = new FileAppender();
		fa.setName("Pages At same time");
		fa.setFile(COMPARE_LOG_PATH);
		fa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
		fa.setAppend(true);
		fa.activateOptions();
		LOGGER_PAGE.addAppender(fa);

		LOGGER_PAGE.info(map);

	}

	private static void createExtract() throws URISyntaxException, IOException {

		URI path = MainMethod.class.getClassLoader().getResource(HAR_NAME).toURI();

		System.out.println("===>" + path);

		HarBean bean = HarReader.readHar(path, HarBean.class);

		// System.out.println("============>" +
		// bean.getEntries().get(0).getStartedDateTime());

		EXE.execute(() -> {
			FileAppender fa = new FileAppender();
			fa.setName("Pages At same time");
			fa.setFile(PAGE_TIME_PATH_MIN);
			fa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
			fa.setAppend(true);
			fa.activateOptions();
			Map<Date, String> startTimeOfRes = bean.getPagesStartedAtSameTime();
			LOGGER_PAGE.addAppender(fa);

			LOGGER_PAGE.info(startTimeOfRes);
		});
		EXE.execute(() -> {
			FileAppender fa = new FileAppender();
			fa.setName("Pages size and time");
			fa.setFile(RES_SIZE_TIME_PATH_MIN);
			fa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
			fa.setAppend(true);
			fa.activateOptions();
			Map<String, String> startTimeOfRes = bean.getResSizeAndTime();
			LOGGER_PAGE.addAppender(fa);

			LOGGER_PAGE.info(startTimeOfRes);
		});
		EXE.submit(() -> {
			FileAppender fa = new FileAppender();
			fa.setName("Pages res size/time");
			fa.setFile(FEQ_PATH_MIN);
			fa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
			fa.setAppend(true);
			fa.activateOptions();
			Map<String, Double> resourceSpeed = bean.getResSizePerTime();
			LOGGER_FEQ.addAppender(fa);

			LOGGER_FEQ.info(resourceSpeed);
		});

		EXE.shutdown();
	}

}
