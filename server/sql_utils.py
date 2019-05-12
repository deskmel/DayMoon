# -*- coding: UTF-8 -*-
import pymysql
import json
import datetime,time

class Remind(object):
    '''
    需要功能完善，比如提醒时间、提醒间隔...此处先创建占位类
    '''
    def __init__(self):
        self.foo='bar'
        self.bar='foo'

    def str(self):
        return json.dumps(self.__dict__,ensure_ascii=False)

class DayMoonDB(object):
    def __init__(self,host='localhost',user="root", db="DayMoon"):
        self.db = pymysql.connect(host=host, user=user, db=db,charset="utf8",use_unicode=True)
        self.cur=self.db.cursor()

    def isValidLogin(self,logstr,password):
        '''
        登录时验证使用，可使用name,mail,phone中任一方法登录
        :param logstr: str
        :param password: str
        :return: 若成功返回int userID, 失败返回bool False
        '''
        sql="SELECT `userID` FROM `users` WHERE `userName`='%s' AND `userPassword`='%s'"%(logstr,password)
        self.cur.execute(sql)
        userIDset = self.cur.fetchone()
        if userIDset:return userIDset[0]
        else:
            sql = "SELECT `userID` FROM `users` WHERE `mailAddress`='%s' AND `userPassword`='%s'" % (logstr, password)
            self.cur.execute(sql)
            userIDset = self.cur.fetchone()
            if userIDset: return userIDset[0]
            else:
                sql = "SELECT `userID` FROM `users` WHERE `phoneNumber`='%s' AND `userPassword`='%s'" % (logstr, password)
                self.cur.execute(sql)
                userIDset = self.cur.fetchone()
                if userIDset: return userIDset[0]
        return False
    #-----------user的增、改、查-----------#
    def submitUserInfo(self,name,password,mail,phoneNumber):
        '''
        注册时调用
        :param name: str,不超过50个字符
        :param password: str,32位MD5
        :param mail: str,前端已确认是valid
        :param phoneNumber: str,前端已确认是valid
        :return: 若成功，返回userID，若失败，返回提示字符串(name,mail,phone不可重复)
        '''
        sql='''INSERT INTO `users` (`userID`, `userName`, `userPassword`, `mailAddress`, `phoneNumber`, `groupIDs`, `eventIDs`) VALUES (NULL, '%s', '%s', '%s', '%s', '[]', '[]');'''%(name,password,mail,phoneNumber)
        try:
            self.cur.execute(sql)
            self.db.commit()
            self.cur.execute('select max(`userID`) from `users`;')
            id=self.cur.fetchone()[0]
            return id
        except Exception as err:
            self.db.rollback()
            return str(err)

    def editUserInfo(self,userID,name='',password='',mail='',phoneNumber=''):
        '''
        修改用户信息，需要改的项不为空，其他默认为空
        :param userID: int，必选
        :param name: str
        :param password: str
        :param mail: str
        :param phoneNumber: str
        :return: 成功为True，失败为err
        '''
        str1="";str2="";str3="";str4=""
        if name:str1="`userName` = '%s',"%name
        if password:str2="`userPassword` = '%s',"%password
        if mail:str3="`mailAddress` = '%s',"%mail
        if phoneNumber:str4="`phoneNumber` = '%s',"%phoneNumber
        wholeStr=str1+str2+str3+str4
        wholeStr=wholeStr[:-1]#去除最后一个逗号
        sql="UPDATE `users` SET "+wholeStr+" WHERE `users`.`userID` = %d;"%userID
        print(sql)
        try:
            self.cur.execute(sql)
            self.db.commit()
            return True
        except Exception as err:
            self.db.rollback()
            return str(err)

    def getUserInfo(self,userID):
        '''

        :param userID: int
        :return: 若存在用户，返回dict_json(不含密码)，否则返回None
        '''
        sql = '''SELECT * FROM `users` WHERE `userID`=%d''' % userID
        self.cur.execute(sql)
        info=self.cur.fetchone()
        if not info:return None
        infodict={'userID':info[0],'userName':info[1],'mailAddress':info[3],'phoneNumber':info[4],'groupIDs':json.loads(info[5]),'eventIDs':json.loads(info[6])}
        return json.dumps(infodict,ensure_ascii=False)
    # -----------user的增、改、查-----------#

    # -----------personalEvent的增、改、查、删-----------#
    def submitEventInfo(self,userID,eventName,whetherProcess,beginTime,endTime='',description='',remind='{}'):
        '''
        增加个人事件
        :param userID: int
        :param eventName: str
        :param whetherProcess: bool, 表示是否需要endTime; 若无endTime, 即为endTime=1970-01-01 00:00:00
        :param beginTime: str, in format: %Y-%m-%d %H:%M:%S
        :param endTime: str可为空, in format: %Y-%m-%d %H:%M:%S
        :param description: str可为空
        :param remind: str可为空，通过r=Remind();r_str=r.str()得到
        :return: 若成功返回eventID，若失败返回err
        '''
        try:
            if whetherProcess:
                sql='''INSERT INTO `events` (`eventID`, `userID`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES (NULL, '%d', '%s', '%s', '%s', '%s', '1', '%s');'''%(userID,eventName,description,beginTime,endTime,remind)
            else:
                sql='''INSERT INTO `events` (`eventID`, `userID`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES (NULL, '%d', '%s', '%s', '%s', '1970-01-01 00:00:00', '0', '%s');'''%(userID,eventName,description,beginTime,remind)
            self.cur.execute(sql)
            self.db.commit()
            self.cur.execute('select max(`eventID`) from `events`;')
            eventID=self.cur.fetchone()[0]

            sql = '''SELECT `eventIDs` FROM `users` WHERE `userID`=%d''' % userID
            self.cur.execute(sql)
            events = json.loads(self.cur.fetchone()[0])
            events.append(eventID)
            events = json.dumps(list(set(events)),ensure_ascii=False)  # 防止重复
            sql = '''update `users` set `eventIDs`='%s' WHERE `userID`=%d;''' % (events, userID)
            self.cur.execute(sql)
            self.db.commit()

            return eventID
        except Exception as err:
            self.db.rollback()
            return str(err)

    def editEventInfo(self,eventID,userID,eventName='',beginTime='',endTime='',description='',remind=''):
        '''
        修改某个个人事件,若原本whetherProcess=False,新传进了endTime,则whetherProcess自动改为True
        :param eventID: int 必有
        :param userID: int 必有
        :param eventName: str
        :param beginTime: str
        :param endTime: str
        :param description: str
        :param remind: str
        :return: 成功为True，失败为err
        '''
        sql = '''SELECT `userID` FROM `events` WHERE `eventID`=%d''' % eventID
        self.cur.execute(sql)
        info=self.cur.fetchone()
        if not info:return None
        realuser = info[0]
        if realuser != userID: return 'NO ACCESS'

        str1 = "";str2 = "";str3 = "";str4 = "";str5=""
        if eventName: str1 = "`eventName` = '%s'," % eventName
        if beginTime: str2 = "`beginTime` = '%s'," % beginTime
        if endTime and endTime!='1970-01-01 00:00:00': str3 = "`whetherProcess` = '1', `endTime` = '%s'," % endTime
        if description: str4 = "`description` = '%s'," % description
        if remind: str5 = "`remind` = '%s'," % remind
        wholeStr = str1 + str2 + str3 + str4 + str5
        wholeStr = wholeStr[:-1]  # 去除最后一个逗号
        sql = "UPDATE `events` SET " + wholeStr + " WHERE `events`.`eventID` = %d;" % eventID
        try:
            self.cur.execute(sql)
            self.db.commit()
            return True
        except Exception as err:
            self.db.rollback()
            return str(err)

    def getEventInfo(self,eventID,userID):
        '''
        :param eventID: int
        :param userID: int
        :return: 当userID符合该eventID时才有权限. 若存在事件，返回dict_json，否则返回None
        '''
        sql = '''SELECT `userID` FROM `events` WHERE `eventID`=%d''' % eventID
        self.cur.execute(sql)
        realuser = self.cur.fetchone()[0]
        if realuser!=userID: return 'NO ACCESS'

        sql = '''SELECT * FROM `events` WHERE `eventID`=%d''' % eventID
        self.cur.execute(sql)
        info = self.cur.fetchone()
        if not info: return None
        infodict = {'eventID': info[0], 'userID': info[1], 'eventName':info[2], 'description': info[3], 'beginTime': info[4].strftime("%Y-%m-%d %H:%M:%S"),
                    'endTime': info[5].strftime("%Y-%m-%d %H:%M:%S"), 'whetherProcess':info[6], 'remind': json.loads(info[7])}
        return json.dumps(infodict,ensure_ascii=False)

    def deleteEventInfo(self,eventID,userID):
        '''
        删除某个个人事件，只有事件拥有者有权限
        :param eventID: int
        :param userID: int
        :return: 成功为True，失败为err
        '''
        try:
            sql = '''SELECT `userID` FROM `events` WHERE `eventID`=%d''' % eventID
            self.cur.execute(sql)
            realuser = self.cur.fetchone()[0]
            if realuser != userID: return 'NO ACCESS'

            sql='''DELETE FROM `events` WHERE `events`.`eventID` = %d'''%eventID
            self.cur.execute(sql)
            self.db.commit()

            sql = '''SELECT `eventIDs` FROM `users` WHERE `userID`=%d''' % userID
            self.cur.execute(sql)
            events = json.loads(self.cur.fetchone()[0])
            events.remove(eventID)
            events = json.dumps(events, ensure_ascii=False)  # 防止重复
            sql = '''update `users` set `eventIDs`='%s' WHERE `userID`=%d;''' % (events, userID)
            self.cur.execute(sql)
            self.db.commit()

            return True
        except Exception as err:
            self.db.rollback()
            return str(err)
    # -----------personalEvent的增、改、查、删-----------#

    # -----------group的创、删、进、退、踢、查-----------#
    def createGroup(self,leaderID,groupName,description=''):
        '''
        leader创建小组时调用
        :param leaderID: int
        :param groupName: str
        :param description: str,默认为空
        :return: 若成功，返回groupID，若失败，返回err
        '''
        sql = '''INSERT INTO `groups` (`groupID`, `groupName`, `description`, `memberIDs`, `eventIDs`, `leaderID`) VALUES (NULL, '%s', '%s', '[]', '[]', '%d');''' % (groupName,description,leaderID)
        try:
            self.cur.execute(sql)
            self.db.commit()
            self.cur.execute('select max(`groupID`) from `groups`;')
            groupID=self.cur.fetchone()[0]
            self.joinGroup(leaderID,groupID)
            return groupID
        except Exception as err:
            self.db.rollback()
            return str(err)

    def deleteGroup(self,leaderID,groupID):
        '''
        leader析构小组时调用，只有leader有权限
        :param leaderID: int
        :param groupID: int
        :return: 成功为True，失败为err
        '''
        try:
            sql='''SELECT `leaderID`, `memberIDs` FROM `groups` WHERE `groupID`=%d'''%groupID
            self.cur.execute(sql)
            info=self.cur.fetchone()
            if not info:return None
            realLeader,members = info
            members=json.loads(members)
            if realLeader!=leaderID:return 'NO ACCESS'
            for memberID in members:
                self.quitGroup(memberID,groupID)
            sql='''DELETE FROM `groups` WHERE `groups`.`groupID` = %d'''%groupID
            self.cur.execute(sql)
            self.db.commit()

            sql = '''SELECT `groupIDs` FROM `users` WHERE `userID`=%d''' % leaderID
            self.cur.execute(sql)
            groups = json.loads(self.cur.fetchone()[0])

            if groupID in groups:
                groups.remove(groupID)
                groups = json.dumps(groups, ensure_ascii=False)
                sql = '''update `users` set `groupIDs`='%s' WHERE `userID`=%d;''' % (groups, leaderID)
                self.cur.execute(sql)
                self.db.commit()

        except Exception as err:
            self.db.rollback()
            return str(err)

    def joinGroup(self,userID,groupID):
        '''
        小组其他成员加入组（扫码或被邀请等）
        :param userID: int
        :param groupID: int, 必须已存在
        :return: 成功为True，失败为err
        '''
        try:
            sql='''SELECT `groupIDs` FROM `users` WHERE `userID`=%d'''%userID
            self.cur.execute(sql)
            groups=json.loads(self.cur.fetchone()[0])
            groups.append(groupID)
            groups=json.dumps(list(set(groups)),ensure_ascii=False)#防止重复
            sql='''update `users` set `groupIDs`='%s' WHERE `userID`=%d;'''%(groups,userID)
            self.cur.execute(sql)
            self.db.commit()

            sql = '''SELECT `memberIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            members = json.loads(self.cur.fetchone()[0])
            members.append(userID)
            members = json.dumps(list(set(members)),ensure_ascii=False)  # 防止重复
            sql = '''update `groups` set `memberIDs`='%s' WHERE `groupID`=%d;''' % (members, groupID)
            self.cur.execute(sql)
            self.db.commit()

            return True
        except Exception as err:
            self.db.rollback()
            return str(err)

    def quitGroup(self,userID,groupID):
        '''
        小组其他成员退出组
        :param userID: int, 不可以是leader
        :param groupID: int, 应该已经存在
        :return: 成功为True，失败为err
        '''
        try:
            sql = '''SELECT `leaderID` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            realLeader = self.cur.fetchone()[0]
            if realLeader == userID: return 'LEADER不可以quit，你可以选择删除'

            sql='''SELECT `groupIDs` FROM `users` WHERE `userID`=%d'''%userID
            self.cur.execute(sql)
            groups=json.loads(self.cur.fetchone()[0])
            if groupID in groups:
                groups.remove(groupID)
                groups=json.dumps(groups,ensure_ascii=False)
                sql='''update `users` set `groupIDs`='%s' WHERE `userID`=%d;'''%(groups,userID)
                self.cur.execute(sql)
                self.db.commit()

            sql = '''SELECT `memberIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            members = json.loads(self.cur.fetchone()[0])
            if userID in members:
                members.remove(userID)
                members=json.dumps(members,ensure_ascii=False)
                sql = '''update `groups` set `memberIDs`='%s' WHERE `groupID`=%d;''' % (members, groupID)
                self.cur.execute(sql)
                self.db.commit()

            return True
        except Exception as err:
            self.db.rollback()
            return str(err)

    def kickFromGroup(self,foot,ball,groupID):
        '''
        组长将组员移出小组的操作
        :param foot: int
        :param ball: int
        :param groupID: int
        :return: 成功为True，失败为err
        '''
        sql = '''SELECT `leaderID`, `memberIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
        self.cur.execute(sql)
        realLeader, members = self.cur.fetchone()
        members = json.loads(members)
        if realLeader != foot: return 'NO ACCESS'
        if ball not in members: return '%d NOT EXIST IN GROUP'%ball
        return self.quitGroup(ball,groupID)

    def getGroupInfo(self,groupID,userID):
        '''
        查看小组信息，只有在user在小组内时才有权限。后续可以分化为更多子函数
        :param groupID: int
        :param groupID: int
        :return: 若成功，返回dict_json，否则返回None或err
        '''

        sql = '''SELECT * FROM `groups` WHERE `groupID`=%d''' % groupID
        self.cur.execute(sql)
        info = self.cur.fetchone()
        if not info:return None

        members = json.loads(info[3])
        if userID not in members:return 'NO ACCESS'

        groupID,groupName,description,eventIDs,leaderID=info[0],info[1],info[2],json.loads(info[4]),info[5]
        infoDict={'groupID':groupID,'groupName':groupName,'description':description,'memberIDs':members,'eventIDs':eventIDs,'leaderID':leaderID}
        return json.dumps(infoDict,ensure_ascii=False)
    # -----------group的创、删、进、退、查-----------#

    # -----------groupEvent的增、改、查、删-----------#
    def submitGroupEventInfo(self, groupID, eventName, whetherProcess, beginTime, endTime='', description='', remind='{}', dutyUserIDs=None):
        '''
        依附于已经存在的groupID，创建小组事件
        :param groupID: int
        :param eventName: str
        :param whetherProcess: bool
        :param beginTime: str
        :param endTime: str
        :param description: str
        :param remind: str
        :param dutyUserIDs: int[],该事件的责任用户，若为空，则默认为全组成员
        :return: 若成功，返回eventID，否则返回err
        '''
        try:
            sql = '''SELECT `memberIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            members = self.cur.fetchone()[0]
            members = json.loads(members)
            if not dutyUserIDs:dutyUserIDs=members
            else:
                dutyUserIDs=list(set(dutyUserIDs)&set(members))#取交集
                if not dutyUserIDs:dutyUserIDs=members

            if whetherProcess:
                sql = '''INSERT INTO `groupEvents` (`eventID`, `groupID`, `dutyUserIDs`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES (NULL, '%d', '%s', '%s', '%s', '%s', '%s', '1', '%s');''' % (
                groupID, json.dumps(dutyUserIDs,ensure_ascii=False), eventName, description, beginTime, endTime, remind)
            else:
                sql = '''INSERT INTO `groupEvents` (`eventID`, `groupID`, `dutyUserIDs`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES (NULL, '%d', '%s', '%s', '%s', '%s', '1970-01-01 00:00:00', '0', '%s');''' % (
                    groupID, json.dumps(dutyUserIDs, ensure_ascii=False), eventName, description, beginTime,remind)
            self.cur.execute(sql)
            self.db.commit()
            self.cur.execute('select max(`eventID`) from `groupEvents`;')
            eventID = self.cur.fetchone()[0]

            sql = '''SELECT `eventIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            events = json.loads(self.cur.fetchone()[0])
            events.append(eventID)
            events = json.dumps(list(set(events)), ensure_ascii=False)  # 防止重复
            sql = '''update `groups` set `eventIDs`='%s' WHERE `groupID`=%d;''' % (events, groupID)
            self.cur.execute(sql)
            self.db.commit()

            return eventID
        except Exception as err:
            return str(err)

    def editGroupEventInfo(self, eventID, userID, eventName='', beginTime='', endTime='', description='', remind='',dutyUserIDs=None):
        '''
        修改小组事件，只有该事件管理员（dutyUsers）才有权限
        :param eventID: int
        :param userID: int
        :param eventName: str
        :param beginTime: str
        :param endTime: str
        :param description: str
        :param remind: str
        :param dutyUserIDs: int[],需要是在group内的成员
        :return: 成功返回True，失败返回err
        '''
        sql = '''SELECT `dutyUserIDs`, `groupID` FROM `groupEvents` WHERE `eventID`=%d''' % eventID
        self.cur.execute(sql)
        info=self.cur.fetchone()
        if not info: return None
        dutys_now = json.loads(info[0])
        groupID=info[1]
        if userID not in dutys_now:return 'NO ACCESS'

        if dutyUserIDs:  # 若要改变该事件的管理员用户
            sql = '''SELECT `memberIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            members = self.cur.fetchone()[0]
            members = json.loads(members)
            dutyUserIDs = list(set(dutyUserIDs) & set(members))  # 取交集
            if not dutyUserIDs: dutyUserIDs = members

        str1 = "";
        str2 = "";
        str3 = "";
        str4 = "";
        str5 = "";
        str6 = "";
        if eventName: str1 = "`eventName` = '%s'," % eventName
        if beginTime: str2 = "`beginTime` = '%s'," % beginTime
        if endTime and endTime!='1970-01-01 00:00:00': str3 = "`whetherProcess` = '1', `endTime` = '%s'," % endTime
        if description: str4 = "`description` = '%s'," % description
        if remind: str5 = "`remind` = '%s'," % remind
        if dutyUserIDs: str6 = "`dutyUserIDs` = '%s'," % json.dumps(dutyUserIDs,ensure_ascii=False)
        wholeStr = str1 + str2 + str3 + str4 + str5+str6
        wholeStr = wholeStr[:-1]  # 去除最后一个逗号
        sql = "UPDATE `groupEvents` SET " + wholeStr + " WHERE `groupEvents`.`eventID` = %d;" % eventID

        try:
            self.cur.execute(sql)
            self.db.commit()
            return True
        except Exception as err:
            self.db.rollback()
            return str(err)

    def getGroupEventInfo(self, eventID,userID):
        '''
        查询小组事件信息，只有该事件的参与者以及事件所在小组的组长有权限查看
        :param eventID: int
        :param userID: int
        :return: 若存在事件，返回dict_json，否则返回None或err
        '''
        sql = '''SELECT * FROM `groupEvents` WHERE `eventID`=%d''' % eventID
        self.cur.execute(sql)
        info = self.cur.fetchone()
        if not info: return None
        realusers = json.loads(info[2])
        groupID=info[1]
        if userID not in realusers:
            sql = '''SELECT `leaderID` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            leader = self.cur.fetchone()[0]
            if userID!=leader:return 'NO ACCESS'

        infodict = {'eventID': info[0], 'groupID': info[1], 'dutyUserIDs': realusers, 'eventName': info[3],'description':info[4],
                    'beginTime': info[5].strftime("%Y-%m-%d %H:%M:%S"),
                    'endTime': info[6].strftime("%Y-%m-%d %H:%M:%S"), 'whetherProcess': info[7], 'remind': json.loads(info[8])}
        return json.dumps(infodict, ensure_ascii=False)

    def deleteGroupEventInfo(self,eventID,userID):
        '''
        删除小组事件，只有该事件的成员有权限
        :param eventID: int
        :param userID: int
        :return: 若成功返回True 失败返回err或None
        '''
        try:
            sql = '''SELECT `dutyUserIDs`,`groupID` FROM `groupEvents` WHERE `eventID`=%d''' % eventID
            self.cur.execute(sql)
            info=self.cur.fetchone()

            if not info:return None
            users = json.loads(info[0])
            groupID=info[1]
            if userID not in users:return 'NO ACCESS'

            sql='''DELETE FROM `groupEvents` WHERE `groupEvents`.`eventID` = %d'''%eventID
            self.cur.execute(sql)
            self.db.commit()

            sql = '''SELECT `eventIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            events = json.loads(self.cur.fetchone()[0])
            events.remove(eventID)
            events = json.dumps(events, ensure_ascii=False)
            sql = '''update `groups` set `eventIDs`='%s' WHERE `groupID`=%d;''' % (events, groupID)
            self.cur.execute(sql)
            self.db.commit()

            return True
        except Exception as err:
            self.db.rollback()
            return str(err)
    # -----------groupEvent的增、改、查、删-----------#

    # -----------个人信息汇总-----------#
    def getAllMyEvents(self,userID):
        '''
        查看某人所有个人事件信息
        :param userID: int
        :return: json_dict
        '''
        sql = '''SELECT `eventIDs` FROM `users` WHERE `userID`=%d''' % userID
        self.cur.execute(sql)
        events = json.loads(self.cur.fetchone()[0])

        allMyEvents=[]
        for eventID in events:
            allMyEvents.append(json.loads(self.getEventInfo(eventID,userID)))
        return json.dumps(allMyEvents,ensure_ascii=False)

    def getAllMyGroupEvents(self,userID):
        '''
        查看某人所有个人事件信息
        :param userID: int
        :return: json_dict
        '''
        sql = '''SELECT `groupIDs` FROM `users` WHERE `userID`=%d''' % userID
        self.cur.execute(sql)
        groups = json.loads(self.cur.fetchone()[0])

        allMyGroupEvents=[]
        for groupID in groups:
            sql = '''SELECT `eventIDs` FROM `groups` WHERE `groupID`=%d''' % groupID
            self.cur.execute(sql)
            events = json.loads(self.cur.fetchone()[0])
            for eventID in events:
                sql = '''SELECT * FROM `groupEvents` WHERE `eventID`=%d''' % eventID
                self.cur.execute(sql)
                info = self.cur.fetchone()
                realusers = json.loads(info[2])
                if userID in realusers:
                    infodict = {'eventID': info[0], 'groupID': info[1], 'dutyUserIDs': realusers, 'eventName': info[3],
                            'description': info[4],
                            'beginTime': info[5].strftime("%Y-%m-%d %H:%M:%S"),
                            'endTime': info[6].strftime("%Y-%m-%d %H:%M:%S"), 'whetherProcess': info[7],
                            'remind': json.loads(info[8])}
                    allMyGroupEvents.append(infodict)
        return json.dumps(allMyGroupEvents,ensure_ascii=False)
    # -----------个人信息汇总-----------#



if __name__ == '__main__':
    rem=Remind()
    db=DayMoonDB()

    #测试注册用户，用户名、邮箱、手机不允许相同
    print('------------------')
    userID1=db.submitUserInfo(name='daego',password='123',mail='u@v.com',phoneNumber='1234567')
    userID2 =db.submitUserInfo(name='jun', password='123', mail='c@v.com', phoneNumber='1534567')
    userID3 =db.submitUserInfo(name='史王', password='123', mail='w@v.com', phoneNumber='1634567')
    userID4 =db.submitUserInfo(name='fan', password='123', mail='j@v.com', phoneNumber='1434567')
    userID5 =db.submitUserInfo(name='deskmel', password='123', mail='p@v.com', phoneNumber='1334567')
    print(db.submitUserInfo(name='deskmel2', password='123', mail='p@2v.com', phoneNumber='1334567'))

    #测试登录方式
    print('------------------')
    assert db.isValidLogin('史王','123')
    assert db.isValidLogin('c@v.com', '123')
    assert db.isValidLogin('1234567', '123')
    print('ALL PASS')

    #测试user改查
    print('------------------')
    print(db.editUserInfo(userID=userID1,name='daego_2'))
    print(db.editUserInfo(userID=userID1, phoneNumber='1434567'))
    print(db.getUserInfo(userID1))

    #测试personalEvent的增、改、查、删
    print('------------------')
    eventID1=db.submitEventInfo(userID=userID2,eventName='睡觉',whetherProcess=1,
                       beginTime='2019-04-25 00:00:00',endTime='2019-04-25 10:00:00',
                        description = '香', remind = rem.str())
    print(db.editEventInfo(eventID=eventID1,userID=userID2,eventName='睡大觉'))
    print(db.editEventInfo(eventID=eventID1, userID=userID1, eventName='睡大觉'))
    print(db.getEventInfo(eventID1,userID2))
    # print(db.deleteEventInfo(eventID1,userID2))

    # 测试group的创、删、进、退、踢、查
    print('------------------')
    groupID1=db.createGroup(leaderID=userID3,groupName='猛男学习小组')
    db.joinGroup(userID1,groupID1)
    db.joinGroup(userID2, groupID1)
    db.joinGroup(userID3, groupID1)#抗重复
    db.joinGroup(userID4, groupID1)
    db.joinGroup(userID5, groupID1)
    print(db.quitGroup(userID5,groupID1))
    print(db.quitGroup(userID3,groupID1))
    db.kickFromGroup(userID3,userID1,groupID1)
    print(db.getGroupInfo(groupID1,userID2))
    db.deleteGroup(userID3,groupID1)

    groupID1 = db.createGroup(leaderID=userID3, groupName='猛男学习小组')
    db.joinGroup(userID1, groupID1)
    db.joinGroup(userID2, groupID1)
    db.joinGroup(userID5, groupID1)

    # 测试groupEvent的增、改、查、删
    print('------------------')
    groupEventID1=db.submitGroupEventInfo(groupID=groupID1,eventName='一起睡觉',whetherProcess=0,
                            beginTime='2019-04-25 00:00:00',description='一起香')
    print(db.getGroupEventInfo(eventID=groupEventID1,userID=userID1))
    db.editGroupEventInfo(eventID=groupEventID1,userID=userID1,
                          endTime='2019-04-25 10:00:00',dutyUserIDs=[userID2,userID3,userID4,userID5])

    print(db.getGroupEventInfo(eventID=groupEventID1, userID=userID5))
    print(db.getGroupEventInfo(eventID=groupEventID1, userID=userID1))
    # print(db.deleteGroupEventInfo(eventID=groupEventID1,userID=userID5))



















