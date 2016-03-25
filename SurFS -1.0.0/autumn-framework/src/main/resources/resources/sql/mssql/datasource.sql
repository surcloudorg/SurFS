CREATE TABLE [dbo].[datasource] (
	[jndiname] [varchar] (20) DEFAULT ('') NOT NULL ,
	[host] [varchar] (30) DEFAULT ('') NOT NULL ,
	[driver] [varchar] (60) DEFAULT ('com.mysql.jdbc.Driver') NOT NULL ,
	[dburl] [varchar] (100) DEFAULT ('') NOT NULL ,
	[username] [varchar] (15) DEFAULT ('') NOT NULL ,
	[pwd] [varchar] (20) DEFAULT ('') NOT NULL ,
	[maxconnection] [int] DEFAULT (100) NOT NULL ,
	[minconnection] [int] DEFAULT (5) NOT NULL ,
	[timeoutvalue] [int] DEFAULT (600000) NOT NULL ,
	[testsql] [varchar] (30) DEFAULT ('select 1') NOT NULL ,
	[maxstatement] [int] DEFAULT (50) NOT NULL ,
	CONSTRAINT [PK_datasource] PRIMARY KEY  CLUSTERED ([jndiname],[host])  ON [PRIMARY] 
) ON [PRIMARY]
GO

exec sp_addextendedproperty N'MS_Description', N'数据库连接池名称', N'user', N'dbo', N'table', N'datasource', N'column', N'jndiname'
GO
exec sp_addextendedproperty N'MS_Description', N'主机', N'user', N'dbo', N'table', N'datasource', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'数据库驱动类名', N'user', N'dbo', N'table', N'datasource', N'column', N'driver'
GO
exec sp_addextendedproperty N'MS_Description', N'数据库URL地址', N'user', N'dbo', N'table', N'datasource', N'column', N'dburl'
GO
exec sp_addextendedproperty N'MS_Description', N'账号', N'user', N'dbo', N'table', N'datasource', N'column', N'username'
GO
exec sp_addextendedproperty N'MS_Description', N'密码', N'user', N'dbo', N'table', N'datasource', N'column', N'pwd'
GO
exec sp_addextendedproperty N'MS_Description', N'最大连接数', N'user', N'dbo', N'table', N'datasource', N'column', N'maxconnection'
GO
exec sp_addextendedproperty N'MS_Description', N'空闲时最小连接数', N'user', N'dbo', N'table', N'datasource', N'column', N'minconnection'
GO
exec sp_addextendedproperty N'MS_Description', N'登录/占用超时', N'user', N'dbo', N'table', N'datasource', N'column', N'timeoutvalue'
GO
exec sp_addextendedproperty N'MS_Description', N'测试指令，交付程序的连接首先执行此激活指令，不填则不进行激活测试', N'user', N'dbo', N'table', N'datasource', N'column', N'testsql'
GO
exec sp_addextendedproperty N'MS_Description', N'允许单个连接创建的最大声明数,0不限制', N'user', N'dbo', N'table', N'datasource', N'column', N'maxstatement'
GO