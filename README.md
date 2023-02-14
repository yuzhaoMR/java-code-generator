# Java Code Generator

Java Code Generator 是一款基于数据库表生成Java代码的工具， 代码模板使用当前主流Java框架，能够减少繁琐的重复性工作，让开发人员更专注于技术和性能，提高工作效率和编码热情。

只需要在application.yaml配置数据库的地址, 在Main方法里配置数据库表名和映射类名，即可使用。

> * 根据数据库业务表生成实体类
> * 生成简单的增、删、查、改操作的Mapper文件
> * 生成Controller、Service、Dao、Mapper代码

## 核心依赖

| 依赖                   | 版本           |
| ---------------------- | ------------- |
| Java            | 11        |
| Maven            | 3    |
| freemarker            | 2.3.23       |
| snakeyaml           | 1.19      |
| lombok           | 1.18.20        |
| mysql-connector-java           | 8.0.30         |

## 使用手册 :

### Invoker 参数
| 生成策略 | 参数名 | 描述 |
|  ---   | ----  | ---- |
|公共参数  |database <br> username <br>password|数据库名  <br> 数据库用户名 <br>数据库密码 |
|单表  |tableName <br> className |业务表名  <br> 业务表对应实体类名 |


