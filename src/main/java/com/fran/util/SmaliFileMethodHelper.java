package com.fran.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 程良明
 * @date 2024/3/11
 * * * 说明:计算smali文件的最大方法数
 **/
public class SmaliFileMethodHelper {
	/**
	 * 是否启用严苛模式
	 */
	private final boolean IS_HARSH = false;

	public static final SmaliFileMethodHelper getInstance() {
		return InnerHolder.sInstance;
	}

	public static void main(String[] args) {
		String classPath = "E:\\Downloaded\\文件\\mubao\\smali_classes3";
		Set<String> methodCount = SmaliFileMethodHelper.getInstance().getMethodCount(classPath);
		System.out.println("clm count : " + methodCount);
		System.out.println("clm count : " + methodCount.size());
	}

	public Set<String> getMethodCount(String path) {
		return getMethodCount(new File(path));
	}

	public Set<String> getMethodCount(File file) {
		getMethodCountByDir(file);
		return mMethodCount;
	}

	public void getMethodCountByDir(File file) {
		if (file.isDirectory()) {
			File[] listFile = file.listFiles();
			if (listFile != null) {
				for (File tempFile : listFile) {
					getMethodCountByDir(tempFile);
				}
			}
		} else {
			getMethodCountByFile(file);
		}
	}

	private void getMethodCountByFile(File file) {
		if (!file.isFile()) {
			return;
		}
		String content = readFileFromLine(file);
		processMethodCount(content);
	}

	private Set<String> mMethodCount = new HashSet<>();

	private void processMethodCount(String content) {
		String className = getClassByFile(content);
		String regex = "\\.method\\s.+|invoke-.*->.*";
		if (IS_HARSH) {
			regex = regex + "|value\\s=.*->.*|(iget|iput).*;->.*";
		}

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String line = matcher.group();
			String targetStr;
			if (line.startsWith(".method")) {
				targetStr = makeMethodInvoke(line, className);
			} else {
				targetStr = parseMethodInvoke(line);
				if (IS_HARSH) {
					if (line.contains("value =") && line.contains(".enum")) {
						continue;
					}
					if (line.startsWith("iget") | line.startsWith("iput")) {
//					严苛模式下可能需要计算这些值
//					计算这个，可能有误差，  特殊情况下需要这个,
						int index1 = targetStr.indexOf(":") + 1;
						String tempStr = targetStr.substring(index1);
						int index2 = tempStr.indexOf("$") + index1;
						String type;
						if (index2 > index1) {
							type = targetStr.substring(index1, index2);
						} else {
							type = targetStr.substring(index1);
						}
						targetStr = type.replace("[", "").replace(";", "");
					}
				}

			}
			mMethodCount.add(targetStr.trim());
		}

	}

	private String parseMethodInvoke(String line) {
		return line.substring(line.lastIndexOf(" "));
	}

	private String makeMethodInvoke(String line, String className) {
		return className + "->" + line.substring(line.lastIndexOf(" ")).trim();
	}

	private String makeMethodAnnotationInvoke(String line, String className) {
		return className + "->" + line.substring(line.lastIndexOf(" ")).trim();
	}

	private String readFileFromLine(File file) {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\r");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	private String getClassByFile(String content) {
		String className = "";
		String regex = "\\.class.+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			String line = matcher.group();
			className = line.substring(line.indexOf(" L"));
		}
		return className;
	}

	private SmaliFileMethodHelper() {

	}

	private static class InnerHolder {
		private static SmaliFileMethodHelper sInstance = new SmaliFileMethodHelper();
	}
}
