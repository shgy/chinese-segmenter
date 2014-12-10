package org.xh.xs.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResLoader {
	//加载文本预处理用到的feature
		public static Map<String,Integer> loadStrIntMap(String name,boolean absolutely){
			try {
				Map<String,Integer> wordEncMap=new HashMap<String,Integer>();
				BufferedReader br=getReader(name,absolutely);
				String line;
				String[] datas;
				while((line=br.readLine())!=null){
					datas=line.split("\\s+|:");
					wordEncMap.put(datas[0],Integer.parseInt(datas[1]));
				}
				br.close();
				return wordEncMap;
			} catch (Exception e) {
				return null;
			}
		}
	
	public static Map<Integer,Integer> loadIntIntMap(String name,boolean absolutely){
		try {
			Map<Integer,Integer> map=new HashMap<Integer,Integer>();
			BufferedReader br=getReader(name,absolutely);
			String line;
			String[] data;
			while((line=br.readLine())!=null){
				data=line.split("\\s+|:");
				map.put(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
			}
			br.close();
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
		
	public static Map<Integer,String> loadIntStrMap(String name,boolean absolutely) {
		try {
			Map<Integer,String> map=new HashMap<Integer,String>();
			BufferedReader br=getReader(name,absolutely);
			String line;
			String[] data;
			while((line=br.readLine())!=null){
				data=line.split("\\s+|:");
				map.put(Integer.parseInt(data[0]), data[1]);
			}
			br.close();
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Set<Character> loadCharSet(String name,boolean absolutely){
		try {
			Set<Character> set=new HashSet<Character>();
			BufferedReader br=null;
			br=getReader(name,absolutely);
			String line;
			while((line=br.readLine())!=null){
				if(line.trim().length()==0)continue;
				set.add(line.charAt(0));
			}
			br.close();
			return set;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Set<String> loadStrSet(String file,boolean absolutely){
		try {
			Set<String> set=new HashSet<String>();
			BufferedReader br=getReader(file,absolutely);
			String line;
			while((line=br.readLine())!=null){
				if(line.length()>=1)
					set.add(line.trim());
			}
			return set;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> loadStrList(String name,boolean absolutely){
		try {
			List<String> list=new ArrayList<String>();
			BufferedReader br=getReader(name,absolutely);//getURL(name)
			String line;
			while((line=br.readLine())!=null){
				line=line.trim();
				if(line.length()>0)
					list.add(line);
			}
			br.close();
			return list;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Map<String, Integer[]> loadStrIntArr(String fullName) {
		try{
			Map<String,Integer[]> rs=new HashMap<String,Integer[]>();
			BufferedReader br=open_reader(fullName);
			String line;
			String[] data;
			Integer[] d;
			while((line=br.readLine())!=null){
				data=line.split("\\s+");
				if(data.length!=3)continue;
				d=new Integer[2];
				d[0]=Integer.parseInt(data[1]);
				d[1]=Integer.parseInt(data[2]);
				rs.put(data[0], d);
				
			}
			br.close();
			return rs;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Map<String,Integer> loadLabeledMap(String fullName,int label) {
		try {
			Map<String,Integer> map=new HashMap<String,Integer>();
			String line;
			BufferedReader br=open_reader(fullName);
			while((line=br.readLine())!=null){
				map.put(line.trim(), label);
			}
			br.close();
			return map;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static BufferedReader getReader(String fullName,boolean absolutely) throws Exception{
		if(!absolutely){
			return new BufferedReader(new InputStreamReader(ResLoader.class.getResourceAsStream(fullName),"utf-8"));
		}
		return open_reader(fullName);
	}
	
	public static BufferedReader open_reader(String fileName) throws Exception{
		return open_reader(new File(fileName));
	}
	
	public static BufferedReader open_reader(File file) throws Exception{
		return new BufferedReader(new InputStreamReader(
				new FileInputStream(file),"utf-8"));
	}
	
	public static BufferedWriter open_writer(String fileName) throws Exception{
		return open_writer(new File(fileName));
	}
	
	public static BufferedWriter open_writer(File file)throws Exception{
		return new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
	}
	
	public static BufferedWriter open_writer(String fileName,boolean append) throws Exception{
		return open_writer(new File(fileName),append);
	}
	
	public static BufferedWriter open_writer(File file,boolean append)throws Exception{
		return new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file,append),"utf-8"));
	}
	
	public static DataOutputStream open_outStream(String file)throws FileNotFoundException{
			DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			return dos;
	}
	
	public static DataInputStream open_inStream(String file)throws FileNotFoundException{
		DataInputStream dos=new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		return dos;
}
}
