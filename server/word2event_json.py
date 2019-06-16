#!/usr/bin/python
# coding: utf-8

import json
import urllib.request
import urllib.parse

def get_json_event(sentence):
    '''
    用于从app调用
    :param sentence:string
    :return:json字典
    '''
    senddata = urllib.parse.urlencode({
        'sen': sentence
    }).encode()

    site = 'http://59.78.19.215:413/word2event'

    req = urllib.request.Request(
        url=site,data=senddata)

    result_str = urllib.request.urlopen(req).read().decode(encoding='utf-8')

    return json.dumps(eval(result_str),ensure_ascii=False)

if __name__ == '__main__':
    print(get_json_event('从七月开始每天十二点背单词'))
