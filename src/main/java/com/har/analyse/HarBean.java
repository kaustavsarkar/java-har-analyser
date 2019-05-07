package com.har.analyse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HarBean {

	private List<Entry> entries;

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	/**
	 * This method is created to check when does a particular resource starts to
	 * load. so that if there is any critical resource being loaded later
	 * 
	 * @return
	 */
	public Map<Date, String> getPagesStartedAtSameTime() {

		Map<Date, String> map = entries.parallelStream().collect(Collectors.toMap(x -> {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.setTime(x.getStartedDateTime());
			return cal.getTime();
		}, x -> x.getRequest().getUrl(), (x, y) -> x + ";" + y));

		Map<Date, String> treeMap = new TreeMap<>((Comparator<Date>) (o1, o2) -> o1.compareTo(o2));
		treeMap.putAll(map);
		// System.out.println("============>Map " + map);

		// System.out.println("============>Tree Map " + treeMap);

		return treeMap;
	}

	/**
	 * This method provides information about byte/second information about every
	 * file. It shows if some files are hard to download
	 * 
	 * @return
	 */
	public Map<String, Double> getResSizePerTime() {

		Map<String, Double> resourceSpeed = entries.parallelStream().collect(Collectors.toMap(x -> {
			String name = x.getRequest().getUrl();
			return name;
		}, x -> {
			double bodySize = x.getResponse().getBodySize();
			double contentSize = x.getResponse().getContent().getSize();
			double time = x.getTime();
			if (bodySize == 0) {
				return contentSize / (time * 0.001);
			}
			return bodySize / time;
		}, (oldVal, newVal) -> (oldVal > newVal ? oldVal : newVal)));

		// System.out.println(resourceSpeed);

		Double maxSpeed = resourceSpeed.values().parallelStream().filter(m -> !m.isInfinite())
				.mapToDouble(m -> Double.valueOf(m)).max().getAsDouble();
		Double minSpeed = resourceSpeed.values().parallelStream().mapToDouble(m -> Double.valueOf(m)).min()
				.getAsDouble();

		resourceSpeed.put("MAX", maxSpeed);
		resourceSpeed.put("MIN", minSpeed);
		final Map<String, Double> sortedBySpeed = resourceSpeed.entrySet()

				.stream()

				.sorted(Map.Entry.comparingByValue())

				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		return sortedBySpeed;
	}

	public Map<String, String> getResSizeAndTime() {
		Map<String, BodySpeed> resourceSpeed = entries.parallelStream().collect(Collectors.toMap(x -> {
			String name = x.getRequest().getUrl();
			return name;
		}, x -> {
			BodySpeed bs = new BodySpeed();
			double bodySize = x.getResponse().getBodySize();
			double contentSize = x.getResponse().getContent().getSize();
			double time = x.getTime();
			bs.setTime(time);
			if (bodySize == 0) {
				bs.setBodySize(contentSize);
				return bs;
			}
			bs.setBodySize(bodySize);
			return bs;
		}, (oldVal, newVal) -> (oldVal.getTime() > newVal.getTime() ? oldVal : newVal)));

		// System.out.println(resourceSpeed);

		Double maxSpeed = resourceSpeed.values().parallelStream().filter(m -> !m.getTime().isInfinite())
				.mapToDouble(m -> Double.valueOf(m.getTime())).max().getAsDouble();
		BodySpeed maxTime = resourceSpeed
				.values()
				.parallelStream()
				.filter(m -> !m.getTime().isInfinite())
				.max((c1,c2) -> c1.getTime().compareTo(c2.getTime()))
				.get();
		BodySpeed maxBody = resourceSpeed
				.values()
				.parallelStream()
				.filter(m -> !m.getBodySize().isInfinite())
				.max((c1,c2) -> c1.getBodySize().compareTo(c2.getBodySize()))
				.get();
		BodySpeed minTime = resourceSpeed
				.values()
				.parallelStream()
				.filter(m -> !m.getTime().isInfinite())
				.min((c1,c2) -> c1.getTime().compareTo(c2.getTime()))
				.get();
		BodySpeed minBody = resourceSpeed
				.values()
				.parallelStream()
				.filter(m -> !m.getBodySize().isInfinite())
				.min((c1,c2) -> c1.getBodySize().compareTo(c2.getBodySize()))
				.get();
		Double minSpeed = resourceSpeed.values().parallelStream().mapToDouble(m -> Double.valueOf(m.getTime())).min()
				.getAsDouble();

		resourceSpeed.put("MAX-BODY", maxBody);
		resourceSpeed.put("MIN-BODY", minBody);
		resourceSpeed.put("MAX-TIME", maxTime);
		resourceSpeed.put("MIN-TIME", minTime);
		final Map<String, String> sortedBySpeed = resourceSpeed.entrySet()

				.stream()

				.sorted(Map.Entry.comparingByValue())

				.collect(Collectors.toMap(m -> m.getKey(),
						m -> m.getValue().toString(),
						(e1, e2) -> e1,
						LinkedHashMap::new));

		return sortedBySpeed;
	}

	/**
	 * This method provides information as per mime type of resources. It identifies
	 * if certain mime types are hard to download.
	 * 
	 * @return
	 */
	public Map<String, Double> getMeanTimeByMime() {

		Map<String, List<Integer>> mimeTimes = new HashMap<>();

		entries.parallelStream().forEach(entry -> {

			String mimeType = entry.getResponse().getContent().getMimeType();
			Integer time = entry.getTime();
			if (!mimeTimes.containsKey(mimeType)) {
				mimeTimes.put(mimeType, new ArrayList<>());
			}
			mimeTimes.get(mimeType).add(time);
		});

		mimeTimes.entrySet().parallelStream().collect(Collectors.toMap(mimeTime -> mimeTime.getKey(), mimeTime -> {

			return null;
		}));

		return null;
	}
}
