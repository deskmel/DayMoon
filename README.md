# sql-server的测试方法
### 2019/04/25
-

**1.进入`server`目录**

```
cd server/
```
**2.安装依赖库**

```
pip3 install -r requirements.txt
```
**3.移植数据库结构**

将`DayMoon.sql`拷贝到你的MySQL数据库中。localhost，root，无密码。

**4.测试数据库方法**

```
python3 sql_utils.py
```

**5.测试POST方法**

```
python3 try_flask.py
```
点击弹出的URL，进行操作，并监视数据库改动。