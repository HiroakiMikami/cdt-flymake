package flymake;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.core.resources.IFile;


public class GppErrorParser implements IErrorParser {
	public GppErrorParser(){}
	/*
	 * sample line to parse
	 *  a.c:9:9: 警告: 変数 ‘xs’ が設定されましたが使用されていません [-Wunused-but-set-variable]
	 *  -1-:2:3: -4- : --------5------------------------------------
	 * 1:filename
	 * 2:line
	 * 3:列
	 * 4:重要性
	 * 5:エラーメッセージ
	 */
	private static Pattern pattern = Pattern.compile("(.*):(\\d+):(\\d+):\\s*(.*):\\s*(.*)");
	// TODO: コンパイルは通るけど、エラー報告がなされない。
	@Override
	public boolean processLine(String line, ErrorParserManager parserManager) {
		Matcher matcher = getPattern().matcher(line);
		if (!matcher.matches()) {
			return false;
		}
		IFile fileName = parserManager.findFileName(matcher.group(1));
		if (fileName != null) {
			int lineNumber = Integer.parseInt(matcher.group(2));
			String description = matcher.group(5);
			int severity = findSeverityCode(matcher.group(4));
			ProblemMarkerInfo info = 
					new ProblemMarkerInfo(fileName, lineNumber, description, severity, null);
			parserManager.addProblemMarker(info);
			return true;
		}
		return false;
	}
	private static Map<String, Integer> SEVERITY_MAPPING = new HashMap<String, Integer>();

	static {
		SEVERITY_MAPPING.put("error", IMarkerGenerator.SEVERITY_ERROR_RESOURCE);
		SEVERITY_MAPPING.put("エラー", IMarkerGenerator.SEVERITY_ERROR_RESOURCE);
		SEVERITY_MAPPING.put("warning", IMarkerGenerator.SEVERITY_WARNING);
		SEVERITY_MAPPING.put("警告", IMarkerGenerator.SEVERITY_WARNING);
	}

	private int findSeverityCode(String text) {
		if(text.startsWith("fatal error") || text.startsWith("致命的エラー")){
			return IMarkerGenerator.SEVERITY_ERROR_RESOURCE;
		}
		
   	    Integer code = SEVERITY_MAPPING.get(text);
		if (code != null) {
			return code;
		}
		return IMarkerGenerator.SEVERITY_INFO;
	}

	/**
	 * @return the pattern
	 */
	public static Pattern getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public static void setPattern(Pattern pattern) {
		GppErrorParser.pattern = pattern;
	}
}
