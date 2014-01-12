package test;

import java.util.regex.Matcher;

public class ParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
		//String line = "a.c:9:9: 警告: 変数 ‘xs’ が設定されましたが使用されていません [-Wunused-but-set-variable]";
		String line = "/home/mikami/runtime-EclipseApplication/test/test.cpp:12:5: エラー: ‘hhhhogehoge’ was not declared in this scope";
        Matcher matcher = flymake.GppErrorParser.getPattern().matcher(line);
        if(matcher.matches()){
        	System.out.println("FileName " + matcher.group(1));
        	System.out.println("Line " + matcher.group(2));
        	System.out.println("Type " + matcher.group(4));
        	System.out.println("Message " + matcher.group(5));
        }else{
        	System.out.println("not matches");
        }
	}

}
