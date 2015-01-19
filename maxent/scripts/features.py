# -*- coding: utf-8 -*-
import charutil as chtl

class Features:
	
	def __init__(self,text):
		self.text=text
		self.len=len(text)
		self.tc5=FeaTC5(text)
	
	#提取特征a): cn (n=-2,-1,0,1,2)
	def feCn(self,idx):
		ans =[]
		ans.append('C0='+self.text[idx])
		if idx-2>=0:ans.append('C_2='+self.text[idx-2])
		if idx-1>=0:ans.append('C_1='+self.text[idx-1])
		if idx+1 <self.len:ans.append('C1='+self.text[idx+1])
		if idx+2 <self.len:ans.append('C2='+self.text[idx+2])
		return ans
		
	#提取特征b): CnCn+1 (n=-2,-1,0,1)
	def feCnCn1(self,idx):
		ans =[]
		if idx+1<self.len:ans.append('C01='+self.text[idx:idx+2])
		if idx+2<self.len:ans.append('C12='+self.text[idx+1:idx+3])
		if idx-2>=0:ans.append('C_2_1='+self.text[idx-2:idx])
		if idx-1>=0:ans.append('C_10='+self.text[idx-1:idx+1])
		return ans
		
	#提取特征c): C-1C1  开头或者末尾为None
	def feCn_1Cn1(self,idx):
		if idx ==0 or self.len<3 or idx == self.len-1 :
			return []
		return ['C_11='+self.text[idx-1]+self.text[idx+1]]
		
	#提取特征Pu(C0)，如果是标点符号，则返回"1"，否则返回"0"
	def fePunc(self,idx):
		return ["Pu=1"] if chtl.ispunc(self.text[idx]) else ["Pu=0"]
	
	
	#提取文本中当前位置的上下文特征
	def getFeats(self,idx):
		feats=[]
		feats+=self.feCn(idx)
		feats+=self.feCnCn1(idx)
		feats+=self.feCn_1Cn1(idx)
		feats+=self.fePunc(idx)
		feats+=self.tc5.feature(self.len,idx)
		return feats
	
	
	
"""
提取特征e): T(C-2)T(C-1)T(C0)T(C1)T(C2)
数字是第1类
日期单位是第2类
英语字符是第3类
其它字符是第4类
比如“九〇年代R”=11243
"""
class FeaTC5:
	def __init__(self,text):
		tcs=range(len(text))
		for i in range(len(text)):
			if chtl.isdigit(text[i]):tcs[i]='1'
			elif chtl.isdateunit(text[i]):tcs[i]='2'
			elif chtl.isletter(text[i]):tcs[i]='3'
			else: tcs[i]='4'
		self.tcs=tcs
	
	def feature(self,len,idx):
		ans=''
		if idx-2>=0:ans+=self.tcs[idx-2]
		if idx-1>=0:ans+=self.tcs[idx-1]
		ans+=self.tcs[idx]
		if idx+1<len:ans+=self.tcs[idx+1]
		if idx+2<len:ans+=self.tcs[idx+2]
		return ['TC='+ans]