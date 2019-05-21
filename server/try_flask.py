from flask import Flask
from flask import render_template,redirect,url_for
from flask import request
from flask import Response

import uuid
import os
from sql_utils import *

app = Flask(__name__)
rem=Remind()
db=DayMoonDB()
basedir = os.path.abspath(os.path.dirname(__file__))

@app.route('/')
def index():
    return render_template('home.html')

@app.route('/signup',methods=['GET', 'POST'])
def signup():
    res=""
    if request.method == 'POST':
        name=request.form.get('name')
        password = request.form.get('password')
        mail = request.form.get('mail')
        phoneNumber = request.form.get('phoneNumber')
        userID=db.submitUserInfo(name,password,mail,phoneNumber)
        if userID:
            return str(userID)
    return res

@app.route('/login',methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        logstr=request.form.get('logstr')
        password = request.form.get('password')
        isValid=db.isValidLogin(logstr,password)
        if isValid:
            return str(isValid)
    return ""

@app.route('/edituser',methods=['GET', 'POST'])
def edituser():
    res=None
    if request.method == 'POST':
        userID=int(request.form.get('userID'))
        name=request.form.get('name')
        password = request.form.get('password')
        mail=request.form.get('mail')
        phoneNumber = request.form.get('phoneNumber')

        edt=db.editUserInfo(userID,name,password,mail,phoneNumber)
        res='修改结果:  '+str(edt)
    return render_template('edituser.html',res=res)

@app.route('/getuser',methods=['GET', 'POST'])
def getuser():
    res=None
    if request.method == 'POST':
        userID=int(request.form.get('userID'))

        info=db.getUserInfo(userID)
        res='查询结果:  '+str(info)
    return render_template('getuser.html',res=res)

@app.route('/getallmyevents',methods=['GET', 'POST'])
def getallmyevents():
    res=None
    if request.method == 'POST':
        userID=int(request.form.get('userID'))
        res=db.getAllMyEvents(userID)
    return res

@app.route('/getgroupevent',methods=['GET', 'POST'])
def getgroupevent():
    res='{}'
    if request.method == 'POST':
        userID=int(request.form.get('userID'))
        eventID=int(request.form.get('eventID'))
        res=db.getGroupEventInfo(eventID,userID)
    return res

@app.route('/getallmygroupevents',methods=['GET', 'POST'])
def getallmygroupevents():
    res='[]'
    if request.method == 'POST':
        userID = int(request.form.get('userID'))
        groupID = int(request.form.get('groupID'))
        res = db.getAllMyGroupEvents(groupID,userID)
    return res

@app.route('/getallmygroups',methods=['GET', 'POST'])
def getallmygroups():
    res=None
    if request.method == 'POST':
        userID=int(request.form.get('userID'))
        res=db.getAllMyGroups(userID)
    return res



@app.route('/submitevent',methods=['GET', 'POST'])
def submitevent():
    res=None
    if request.method == 'POST':

        userID=int(request.form.get('userID'))
        eventName = request.form.get('eventName')
        whetherProcess = bool(request.form.get('whetherProcess'))
        beginTime = request.form.get('beginTime')
        endTime = request.form.get('endTime')
        description = request.form.get('description')
        remind=rem.str()

        res=db.submitEventInfo(userID,eventName,whetherProcess,beginTime,endTime,description,remind)
    return str(res)

@app.route('/submitgroupevent',methods=['GET', 'POST'])
def submitgroupevent():
    res=None
    if request.method == 'POST':

        groupID=int(request.form.get('groupID'))
        eventName = request.form.get('eventName')
        eventType = int(request.form.get('eventType'))
        whetherProcess = bool(request.form.get('whetherProcess'))
        location = request.form.get('location')
        beginTime = request.form.get('beginTime')
        endTime = request.form.get('endTime')
        description = request.form.get('description')
        remind=rem.str()

        res=db.submitGroupEventInfo(groupID,eventName,eventType,whetherProcess,location,beginTime,endTime,description)
    print(str(res))
    return str(res)

@app.route('/deleteevent',methods=['GET', 'POST'])
def deleteevent():
    res=None
    if request.method == 'POST':
        userID = int(request.form.get('userID'))
        eventID = int(request.form.get('eventID'))
        res=db.deleteEventInfo(eventID,userID)
    return str(res)

@app.route('/editevent',methods=['GET', 'POST'])
def editevent():
    res=None
    if request.method == 'POST':
        eventID = int(request.form.get('eventID'))
        userID = int(request.form.get('userID'))
        eventName = request.form.get('eventName')
        whetherProcess = bool(request.form.get('whetherProcess'))
        beginTime = request.form.get('beginTime')
        endTime = request.form.get('endTime')
        description = request.form.get('description')
        remind = rem.str()
        res=db.editEventInfo(eventID, userID, eventName, beginTime, endTime, description, remind)
    return str(res)

@app.route('/creategroup',methods=['GET', 'POST'])
def creategroup():
    res=None
    if request.method == 'POST':
        groupName = request.form.get('groupName')
        description = request.form.get('description')
        leaderID = int(request.form.get('leaderID'))
        imgName = request.form.get('imgName')
        f = request.files['file']
        path = basedir + "/static/uploads/"
        file_path = path + f.filename + ".png"
        f.save(file_path)
        res = db.createGroup(leaderID, groupName, imgName)
    return str(res)

@app.route("/image/<imageName>")
def getimage(imageName):
    imagePath =basedir + '/static/uploads/' + imageName + '.png'
    with open(imagePath,"rb") as f:
        image = f.read()
    return Response(image, mimetype="image/png")

@app.route('/genqrcode', methods=['GET', 'POST'])
def genqrcode():
    res = ''
    if request.method == 'POST':
        groupID = int (request.form.get('groupID'))
        res = db.genQRCode(groupID)
    return res

@app.route('/joingroupbyqrcode', methods=['GET', 'POST'])
def joingroupbyqrcode():
    res = ''
    if request.method == 'POST':
        qrCodeKey = request.form.get('qrCodeKey')
        userID = int(request.form.get('userID'))
        res = db.joinGroupByQRCode(userID, qrCodeKey)
        if res:
            return str(res)
        return ''
    return res

from flask import render_template, jsonify
if __name__ == '__main__':
    app.debug = True
    app.run(host = "0.0.0.0", port = 5000)
