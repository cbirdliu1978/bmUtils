//package com.bmtech.utils;
//
//import java.io.IOException;
//import java.io.Reader;
//import java.io.StringReader;
//import java.util.HashSet;
//import java.util.Iterator;
//
//import trdpt.org.apache.lucene.analysis.Analyzer;
//import trdpt.org.apache.lucene.analysis.TokenStream;
//import trdpt.org.apache.lucene.analysis.WhitespaceTokenizer;
//
//import com.bmtech.utils.log.BmtLogger;
//import com.bmtech.utils.segment.Segment;
//import com.bmtech.utils.segment.TokenHandler;
//
//public class CnAnalysis extends Analyzer{
//	public static final String error="tokenString error";
//	public static final String[] STOP_WORDS = {/*W
//		"the","a","an","or","but","not","is","are","was","were" */};
//	private final Segment segment ;
//	public static final HashSet<String>forbidden;	
//	static{
//		forbidden=new HashSet<String>();
//		for(int i=0;i<STOP_WORDS.length;i++){
//			forbidden.add(STOP_WORDS[i]);
//		}
//	}
//	public CnAnalysis(Segment segment) {
//		this.segment = segment;
//	}
//	public  TokenStream tokenStream(String fieldName, Reader reader){
//		int ch ;
//		StringBuilder buf = new StringBuilder();
//
//		try{
//			while (true){
//				ch = reader.read();
//				if (ch == -1)
//					break;
//				buf.append((char)ch);
//			}
//		}catch (IOException e){
//			BmtLogger.instance().log(e, "when analysis");
//		}
//
//
//		String input = buf.toString();
//		String res = null;
//		if (input != null){
//			if(segment != null) {
//				res = segment.segment(input).toString();
//			}
//		}
//
//		TokenStream ts = new WhitespaceTokenizer(new StringReader(res));
//
//		return ts;
//	}
//
//
//	public static Iterator<String> splitAndMakeSet(String str,
//			Segment segment){
//		TokenHandler handler = segment.segment(str);
//		HashSet<String> set = new HashSet<String>();
//		String token;
//		while(handler.hasNext()){
//			token = handler.next();
//			if(acceptWord(token))
//				set.add(token);
//
//		}
//		return set.iterator();
//	}
//	public static boolean acceptWord(String word) {
//		return !forbidden.contains(word);
//	}
//
//}
