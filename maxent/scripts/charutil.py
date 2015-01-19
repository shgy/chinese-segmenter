# -*- coding: utf-8 -*-
"""
字符 工具类，用来判断和处理字符
"""

#判断是否是数字
digits_str=u'1234567890〇一二三四五六七八九十'
digits= set(list(digits_str))
def isdigit(char):
	return char in digits

#全角字符转换成半角字符
def strq2b(ustring):
	rstring = ""
	for uchar in ustring:
		inside_code=ord(uchar)
		if inside_code == 12288:#全角空格直接转换
			inside_code = 32
		elif inside_code>=65281 and inside_code<=65374:
			inside_code-=65248
		rstring += unichr(inside_code)
	return rstring

#判断是否是标点符号
#。？！，、；：“”‘’（）{}【】—…《》
asc_punc_str=u"!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~"
chi_punc_str=u"。？！，、；：“”‘’（）{}【】—…《》"
punc=set(list(asc_punc_str+chi_punc_str))
def ispunc(char):
	return char in punc

#是否是日期单位: 年/月/日
dateunits=set(list(u'年月日'))
def isdateunit(char):
	return char in dateunits

#是否是英文字符:大写、小写 共52个
def isletter(char):
	if (char>='a' and char<='z') or (char>='A' and char<='Z'):
		return True
	return False
	
#是否是汉字
#\u4e00-\u9fa5
#\uF900-\uFA2D
def ishanzi(char):
	if (char >= u'\u4e00' and char <= u'\u9fa5') or (char >= u'\uF900' and char <= u'\uFA2D'):
		return True
	return False 