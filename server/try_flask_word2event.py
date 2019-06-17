from flask import Flask
from flask import request
from flask import Response
import json
import os, sys, threading
from word2event import *

app = Flask(__name__)
@app.route('/word2event',methods=['GET', 'POST'])
def word2event():
    res=None
    if request.method == 'POST':
        sen = request.form.get('sen')
        # res = '转换结果：'+str(getRelationship(sen))
        res = getRelationship(sen)
        
    # return render_template('word2event.html',res=res)
    return json.dumps(res,ensure_ascii=False)

if __name__ == '__main__':
    app.debug = True
    app.run(host="0.0.0.0", port=5000)
