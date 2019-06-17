# -*- coding: utf-8 -*-
import os
import re
from datetime import *
LTP_DATA_DIR = '/Users/markdana/Downloads/ltp_data_v3.4.0'  # ltp模型目录的路径


numdict={'一':'1','二':'2','两':'2','三':'3','四':'4','五':'5','六':'6','七':'7','八':'8','九':'9','十':'10','天':'7','日':'7','十一':'11','十二':'12'}

cws_model_path = os.path.join(LTP_DATA_DIR, 'cws.model')
pos_model_path = os.path.join(LTP_DATA_DIR, 'pos.model')
par_model_path = os.path.join(LTP_DATA_DIR, 'parser.model')
srl_model_path = os.path.join(LTP_DATA_DIR, 'pisrl.model')

from pyltp import Segmentor
from pyltp import Postagger
# from pyltp import Parser
# from pyltp import SementicRoleLabeller

segmentor = Segmentor()
postagger = Postagger()
# parser = Parser()
# labeller = SementicRoleLabeller()

segmentor.load(cws_model_path)
postagger.load(pos_model_path)
# parser.load(par_model_path)
# labeller.load(srl_model_path)

def parseTime(time_tuple):
    '''
    取出time信息
    :param time_tuple: 0或2维，第一维为词，第二维为词性
    :return: start_day, start_time, end_time
    '''
    print(time_tuple)
    wds,pgs=time_tuple
    start_day=datetime.today()
    start_time=(0,0)
    end_time=(0,0)
    time_flag=0
    #time_flag=0表示还未出现过时间，1为已出现一次上午的，2为第一次是下午

    for i in range(len(wds)):
        if '月' in wds[i]:
            month = int(wds[i].replace('月', ''))
            start_day = date(start_day.year, month, 1)
            continue
        if '日'in wds[i] or '号' in wds[i]:
            day = int(wds[i].replace('日', '').replace('号', ''))
            start_day = date(start_day.year, start_day.month, day)
            continue
        if '星期'in wds[i]:
            targetweek = int(numdict[wds[i][-1]])
            todayweek = start_day.isoweekday()
            if targetweek < todayweek:
                start_day = start_day + timedelta(days=7 + targetweek - todayweek)
            else:
                start_day = start_day + timedelta(days=targetweek - todayweek)
            continue
        if wds[i]=='明天':
            start_day=start_day + timedelta(days=1)
            continue
        if wds[i]=='后天':
            start_day = start_day + timedelta(days=2)
            continue
        if ':'in wds[i] and not time_flag:
            hour,minute=int(wds[i].split(':')[0]),int(wds[i].split(':')[1])
            if i-1>0 and hour<=12 and ('下午'in wds[i-1] or '晚上'in wds[i-1] or '傍晚'in wds[i-1]):
                hour += 12
                time_flag=2
                start_time = (hour, minute)
                end_time = (hour, minute)
            else:
                time_flag = 1
                start_time = (hour, minute)
                end_time = (hour, minute)
            continue

        if ':'in wds[i] and time_flag!=0:
            hour,minute=int(wds[i].split(':')[0]),int(wds[i].split(':')[1])
            if time_flag==2 or (i-1>0 and hour<=12 and ('下午'in wds[i-1] or '晚上'in wds[i-1] or '傍晚'in wds[i-1])):
                hour += 12
            end_time = (hour, minute)
            continue


    return start_day, start_time, end_time


def getRelationship(sentence):
    returnDict = {'beginTime': '', 'endTime': '', 'description': '', 'eventLocation': '', 'eventName': ''}
    try:
        sentence=sentence.replace('去','在')
        sentence = sentence.replace('礼拜', '星期')
        sentence = sentence.replace('周', '星期')

        # sentence = sentence.replace('十一点', '11点')
        # sentence = sentence.replace('十二点', '12点')
        # sentence = [x for x in sentence]
        # for i in range(len(sentence)-1):
        #     if sentence[i] in numdict.keys() and sentence[i+1]in ['点']:
        #         sentence[i]=numdict[sentence[i]]
        # sentence=''.join(sentence)

        words = segmentor.segment(sentence)
        postags = postagger.postag(words)

        wds = list(words)
        pgs = list(postags)

        print(wds,pgs)

        time=([],[])
        people=()


        time_ids=[]
        for i in range(len(pgs)):

            if (pgs[i]=='nt'):
                time[0].append(wds[i])
                time[1].append(pgs[i])
                time_ids.append(i)
                continue
            if ((pgs[i]=='m')and ':' in wds[i]):
                time[0].append(wds[i])
                time[1].append('m')
                time_ids.append(i)
                continue
            if (pgs[i]=='m' and pgs[i+1]=='wp' and pgs[i+2]=='m'):
                time[0].append(wds[i]+wds[i+1]+wds[i+2])
                time[1].append('m')
                time_ids.append(i)
                time_ids.append(i+2)
                i=i+2
                continue

        print(time_ids)
        if time_ids:
            from_ind=min(time_ids)
            to_ind=max(time_ids)
            # if from_ind-1>=0 and (pgs[from_ind-1]=='p' or (pgs[from_ind-1]=='r' and '每'in wds[from_ind-1])): from_ind=from_ind-1
            # if to_ind+1<len(pgs) and pgs[to_ind+1]=='r' and '每'in wds[to_ind+1]:to_ind=to_ind+1
            # time=(wds[from_ind:to_ind+1],pgs[from_ind:to_ind+1])
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

        print(wds,pgs)

        place,things='',''
        thinglist=[]
        for i in range(len(wds)):
            if wds[i]=='在' and pgs[i]=='p':
                place=wds[i+1]
                continue
            if pgs[i]=='v':
                if pgs[i+1]=='n':thinglist.append(wds[i]+wds[i+1])
                else:thinglist.append(wds[i])
        things='、'.join(thinglist)
        # arcs = parser.parse(words, postags)
        # roles = labeller.label(words, postags, arcs)
        #
        # place=()
        # things=[]
        # for role in roles:
        #     print(role)
        #     thingstr=wds[role.index]
        #     for arg in role.arguments:
        #         if arg.name=='LOC' and not place:
        #             start,end=arg.range.start, arg.range.end
        #             place=(wds[start:end+1],pgs[start:end+1])
        #         if arg.name == 'A1':
        #             start, end = arg.range.start, arg.range.end
        #             thingstr+=''.join(wds[start:end + 1])
        #     things.append(thingstr)

        # print(place)
        # print(things)

        # print(time)

        start_day, start_time, end_time=parseTime(time)

        date=start_day.strftime('%Y-%m-%d')
        if start_time:returnDict['beginTime']= date + ' %02d:%02d:00'%start_time
        if end_time: returnDict['endTime'] = date + ' %02d:%02d:00' % end_time
        #if freq:returnDict['freq']=str(freq)
        if things:returnDict['eventName']=things
        if people:returnDict['description']='、'.join([people[0][i] for i in range(len(people[0])) if people[1][i]=='nh'])
        if place: returnDict['eventLocation'] = place
        return returnDict
    except:return returnDict


if __name__ == '__main__':
    sen="礼拜一8:00至下午7:00和李晓在操场吃饭"
    # sen="七月五日去食堂吃炒面"
    # sen="从明天开始每天十二点背单词"
    print(sen)
    print(getRelationship(sen))

    while True:
        sen=input('pls input your sentence')
        print(getRelationship(sen))






