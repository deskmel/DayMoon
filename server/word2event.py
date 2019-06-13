# -*- coding: utf-8 -*-
import os
import re
from datetime import *
LTP_DATA_DIR = '/Users/markdana/Downloads/ltp_data_v3.4.0'  # ltp模型目录的路径


numdict={'一':'1','二':'2','两':'2','三':'3','四':'4','五':'5','六':'6','七':'7','八':'8','九':'9','十':'10','天':'7','十一':'11','十二':'12'}

cws_model_path = os.path.join(LTP_DATA_DIR, 'cws.model')
pos_model_path = os.path.join(LTP_DATA_DIR, 'pos.model')
par_model_path = os.path.join(LTP_DATA_DIR, 'parser.model')
srl_model_path = os.path.join(LTP_DATA_DIR, 'pisrl.model')

from pyltp import Segmentor
from pyltp import Postagger
from pyltp import Parser
from pyltp import SementicRoleLabeller

segmentor = Segmentor()
postagger = Postagger()
parser = Parser()
labeller = SementicRoleLabeller()

segmentor.load(cws_model_path)
postagger.load(pos_model_path)
parser.load(par_model_path)
labeller.load(srl_model_path)

def parseTime(time_tuple):
    '''
    取出time信息
    :param time_tuple: 0或2维，第一维为词，第二维为词性
    :return: start_day, start_time, end_time, freq
    '''
    wds,pgs=time_tuple
    start_month_i,start_day_i,start_time_i,end_time_i,freq_i=-1,-1,-1,-1,-1
    start_day=datetime.today()
    start_time=()
    end_time=()
    freq=0

    for i in range(len(wds)):
        if bool(re.search(r'\d', wds[i])):
            if start_time_i==-1:start_time_i=i
            else: end_time_i=i
        if '天' in wds[i] or '星期'in wds[i]:
            start_day_i=i
        if '月' in wds[i]:
            start_month_i = i
        if '每' in wds[i] or '隔' in wds[i]:
            freq_i=i

    # print(start_month_i,start_day_i,start_time_i,end_time_i,freq_i)

    if start_month_i!=-1:
        month=int(numdict[wds[start_month_i].replace('月','')])
        day=1
        if '日'in wds[start_month_i+1] or '号' in wds[start_month_i+1]:
            day=int(numdict[wds[start_month_i+1].replace('日','').replace('号','')])
        start_day=date(start_day.year,month,day)
    if start_day_i != -1:
        if wds[start_day_i]=='明天':
            start_day=start_day + timedelta(days=1)
        if wds[start_day_i]=='后天':
            start_day = start_day + timedelta(days=2)
        if '星期' in wds[start_day_i]:
            targetweek=int(numdict[wds[start_day_i][-1]])
            todayweek=start_day.isoweekday()
            if targetweek<todayweek:
                start_day=start_day + timedelta(days=7+targetweek-todayweek)
            else:
                start_day = start_day + timedelta(days=targetweek - todayweek)

    if start_time_i!=-1:
        hour, minute = 0, 0
        if '下'in wds[start_time_i-1] or '晚'in wds[start_time_i-1]:hour+=12
        if '半' in wds[start_time_i]:minute=30
        hour+=int(wds[start_time_i].replace('点','').replace('半',''))
        start_time=(hour,minute)

    if end_time_i!=-1:
        hour, minute = 0, 0
        if '下'in wds[end_time_i-1] or '晚'in wds[end_time_i-1]:hour+=12
        if '半' in wds[end_time_i]:minute=30
        hour+=int(wds[end_time_i].replace('点','').replace('半',''))
        end_time=(hour,minute)

    if freq_i!=-1:
        if '每天'in wds[freq_i]:freq=1
        if '每' in wds[freq_i] and '星期' in wds[freq_i+1]:freq=7
        if '隔' in wds[freq_i]:freq=2

    return start_day, start_time, end_time, freq


def getRelationship(sentence):
    returnDict = {'date': '', 'start_time': '', 'end_time': '', 'freq': '', 'people': '', 'place': '', 'things': ''}
    try:
        sentence=sentence.replace('去','在')
        sentence = sentence.replace('礼拜', '星期')
        sentence = sentence.replace('周', '星期')
        sentence = sentence.replace('十一点', '11点')
        sentence = sentence.replace('十二点', '12点')
        sentence = [x for x in sentence]
        for i in range(len(sentence)-1):
            if sentence[i] in numdict.keys() and sentence[i+1]in ['点']:
                sentence[i]=numdict[sentence[i]]
        sentence=''.join(sentence)


        words = segmentor.segment(sentence)
        postags = postagger.postag(words)

        wds = list(words)
        pgs = list(postags)

        time=()
        people=()

        time_inds=[i for i in range(len(pgs)) if pgs[i]=='nt']
        if time_inds:
            from_ind=min(time_inds)
            to_ind=max(time_inds)
            if from_ind-1>=0 and (pgs[from_ind-1]=='p' or (pgs[from_ind-1]=='r' and '每'in wds[from_ind-1])): from_ind=from_ind-1
            if to_ind+1<len(pgs) and pgs[to_ind+1]=='r' and '每'in wds[to_ind+1]:to_ind=to_ind+1
            time=(wds[from_ind:to_ind+1],pgs[from_ind:to_ind+1])
            for i in range(from_ind, to_ind + 1): wds[i] = ''


        who_inds = [i for i in range(len(pgs)) if pgs[i] == 'nh']
        if who_inds:
            from_ind = min(who_inds)
            to_ind = max(who_inds)
            if from_ind - 1 >= 0 and wds[from_ind - 1] in ['和','与','陪','同']: from_ind = from_ind - 1
            people = (wds[from_ind:to_ind + 1], pgs[from_ind:to_ind + 1])
            for i in range(from_ind, to_ind + 1): wds[i] = ''

        # print(people)

        if time or people:
            newstr = ''.join(wds)
            words = segmentor.segment(newstr)
            postags = postagger.postag(words)

        wds = list(words)
        pgs = list(postags)
        arcs = parser.parse(words, postags)
        roles = labeller.label(words, postags, arcs)

        place=()
        things=[]
        for role in roles:
            thingstr=wds[role.index]
            for arg in role.arguments:
                if arg.name=='LOC' and not place:
                    start,end=arg.range.start, arg.range.end
                    place=(wds[start:end+1],pgs[start:end+1])
                if arg.name == 'A1':
                    start, end = arg.range.start, arg.range.end
                    thingstr+=''.join(wds[start:end + 1])
            things.append(thingstr)

        # print(place)
        # print(things)

        start_day, start_time, end_time, freq=parseTime(time)

        returnDict['date']=start_day.strftime('%Y-%m-%d')
        if start_time:returnDict['start_time']='%02d:%02d'%start_time
        if end_time: returnDict['end_time'] = '%02d:%02d' % end_time
        if freq:returnDict['freq']=str(freq)
        if things:returnDict['things']='、'.join(things)
        if people:returnDict['people']='、'.join([people[0][i] for i in range(len(people[0])) if people[1][i]=='nh'])
        if place: returnDict['place'] = '、'.join([place[0][i] for i in range(len(place[0])) if place[1][i] == 'n'])

        return returnDict
    except:return returnDict


if __name__ == '__main__':
    sen="七月的每个星期三上午十点半到十二点和超哥、李克强去食堂开会并吃凉面"
    # sen="七月五日去食堂吃炒面"
    # sen="从明天开始每天十二点背单词"
    print(sen)
    print(getRelationship(sen))

    while True:
        sen=input('pls input your sentence')
        print(getRelationship(sen))


    # (['七月', '的', '每个', '星期三', '上午', '10点半', '到', '12点'], ['nt', 'u', 'r', 'nt', 'nt', 'nt', 'p', 'nt'])
    # (['和', '超哥', '、', '李克强'], ['c', 'nh', 'wp', 'nh'])
    # (['在', '食堂'], ['p', 'n'])
    # ['开会', '吃凉面']
    # 2019 - 07 - 03 (10, 30) (12, 0) 7



