# -*- coding:utf8 -*-
from bs4 import BeautifulSoup
import os, sys, urllib2,time,random
 
path = os.getcwd()                     
new_path = os.path.join(path,u'loft')
if not os.path.isdir(new_path):
    os.mkdir(new_path)
 
 
def page_loop(page=1):
    url = 'http://me2-sex.lofter.com/tag/美女摄影?page=%s' % page
    print url
    content = urllib2.urlopen(url)
    soup = BeautifulSoup(content)
    my_girl = soup.find_all('img')
    for girl in my_girl:
        link = girl.get('src')
        flink = link
        print flink
        content2 = urllib2.urlopen(flink).read()
 
        #with open(u'loft'+'/'+time.strftime('%H-%M-%S')+random.choice('qwertyuiopasdfghjklzxcvbnm')+flink[-5:],'wb') as code:   
        with open(u'loft'+'/'+flink[-11:],'wb') as code:
            code.write(content2)
 
    page = int(page) + 1
    print u'开始抓取下一页'
    print 'the %s page' % page
    page_loop(page)
     
page_loop()
print "~~~~~~~~~~~~~~~~~~~~~~~~~~END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
#为了避免双击的时候直接一闪退出，在最后面加了这么一句
raw_input("Press <Enter> To Quit!")
