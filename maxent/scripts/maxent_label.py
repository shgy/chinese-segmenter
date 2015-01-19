# -*- coding: utf-8 -*-
"""
use 4-tags label text
用4-tags法进行处理。
如果是单字，则标S
如果是词的首字母，则标B
如果是词的中间，则标M
如果是词的末尾，则标E

脚本执行方法如下：python maxent_label.py input output
比如，训练集为pku_training.utf8 ，将标注后的文本存储到pku_training_maxent.utf8中
则在命令行中执行脚本：python maxent_label.py pku_training.utf8 pku_training_maxent.utf8

脚本默认支持的文本格式是utf-8
"""
import codecs
import sys

def label_print(line):
	text,tags=label(line)
	ans =[]
	for i in range(len(text)):
		ans.append(text[i]+'/'+tags[i])
	return '  '.join(ans)

def text_tag(input_file,output_file):
	input=codecs.open(input_file,"r","utf-8")
	output=codecs.open(output_file,"w","utf-8")
	for line in input.readlines():
		output.write(label_print(line.strip())+'\n')
	input.close()
	output.close()
	
def label(line):
	text=""
	tags=""
	words=line.strip().split()
	for w in words:
		text+=w
		if len(w)==1:
			tags+="S"
		else:
			tags+="B"
			for ch in w[1:len(w)-1]:
				tags+="M"
			tags+="E"
	return (text,tags)

	
#
import features as feats
import charutil as chtl
def maxent_label(input_file,output_file,encode='utf-8'):
	input=codecs.open(input_file,"r",encode)
	output=codecs.open(output_file,"w",encode)
	count=0
	for line in input.readlines():
		count+=1
		if count%3!=0:continue
		line = chtl.strq2b(line)
		text,tags = label(line)
		textfea=feats.Features(text)
		for i in range(len(text)):
			curfea=textfea.getFeats(i)
			output.write(' '.join(curfea))
			output.write(' '+tags[i])
			output.write('\n')
	input.close()
	output.close()
		


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "Please use: python maxent_label.py input output"
        sys.exit()
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    maxent_label(input_file, output_file)


