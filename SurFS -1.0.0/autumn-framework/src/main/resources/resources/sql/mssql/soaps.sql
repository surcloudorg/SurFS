CREATE TABLE [dbo].[soaps] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[host] [varchar] (30) DEFAULT ('') NOT NULL ,
	[title] [varchar] (60) DEFAULT ('') NOT NULL ,
	[servicename] [varchar] (30) DEFAULT ('') NOT NULL ,
	[implclass] [varchar] (80) DEFAULT ('') NOT NULL ,
	[classname] [varchar] (80) DEFAULT ('') NOT NULL ,
	[authtype] [tinyint] DEFAULT (0) NOT NULL ,
	[style] [varchar] (10) DEFAULT ('rpc') NOT NULL ,
	[usetype] [varchar] (10) DEFAULT ('literal') NOT NULL ,
	[iplist] [varchar] (100) DEFAULT ('') NOT NULL ,
	[infilter] [varchar] (80) DEFAULT ('') NOT NULL ,
	[outfilter] [varchar] (80) DEFAULT ('') NOT NULL ,
	[params] [text] DEFAULT ('') NOT NULL ,
	[aegis] [text] DEFAULT ('') NOT NULL ,
	[logname] [varchar] (20) DEFAULT ('system') NOT NULL ,
	[memo] [text] DEFAULT ('') NOT NULL ,
	[createtime] [datetime] DEFAULT (getdate()) NOT NULL ,
	CONSTRAINT [PK_soaps] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY],
	CONSTRAINT [IX_soaps_servicename] UNIQUE  NONCLUSTERED ([host],[servicename])  ON [PRIMARY] ,
	CONSTRAINT [IX_soaps_title] UNIQUE  NONCLUSTERED ([host],[title])  ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_implclass] ON [dbo].[soaps]([implclass]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_classname] ON [dbo].[soaps]([classname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_infilter] ON [dbo].[soaps]([infilter]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_outfilter] ON [dbo].[soaps]([outfilter]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_authtype] ON [dbo].[soaps]([authtype]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_style] ON [dbo].[soaps]([style]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_usetype] ON [dbo].[soaps]([usetype]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_soaps_createtime] ON [dbo].[soaps]([createtime]) ON [PRIMARY]
GO


exec sp_addextendedproperty N'MS_Description', N'序号,SoapID，服务id', N'user', N'dbo', N'table', N'soaps', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'服务所属机器名', N'user', N'dbo', N'table', N'soaps', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'服务标题', N'user', N'dbo', N'table', N'soaps', N'column', N'title'
GO
exec sp_addextendedproperty N'MS_Description', N'服务名，如myservice,则客户端访问的地址为:http://localhost/services/myservice', N'user', N'dbo', N'table', N'soaps', N'column', N'servicename'
GO
exec sp_addextendedproperty N'MS_Description', N'实现接口类中的函数声明(宽接口),如果className不是接口可以不填', N'user', N'dbo', N'table', N'soaps', N'column', N'implclass'
GO
exec sp_addextendedproperty N'MS_Description', N'包含公有函数提供给远端调用(窄接口)', N'user', N'dbo', N'table', N'soaps', N'column', N'classname'
GO
exec sp_addextendedproperty N'MS_Description', N'认证类型', N'user', N'dbo', N'table', N'soaps', N'column', N'authtype'
GO
exec sp_addextendedproperty N'MS_Description', N'文档style', N'user', N'dbo', N'table', N'soaps', N'column', N'style'
GO
exec sp_addextendedproperty N'MS_Description', N'文档use', N'user', N'dbo', N'table', N'soaps', N'column', N'usetype'
GO
exec sp_addextendedproperty N'MS_Description', N'ip验证（支持192.168.*.*），不填不验证', N'user', N'dbo', N'table', N'soaps', N'column', N'iplist'
GO
exec sp_addextendedproperty N'MS_Description', N'扩展SoapFilter/AbstractHandler,在这里可以解压更改请求', N'user', N'dbo', N'table', N'soaps', N'column', N'infilter'
GO
exec sp_addextendedproperty N'MS_Description', N'扩展SoapFilter/AbstractHandler,在这里可以压缩更改回应', N'user', N'dbo', N'table', N'soaps', N'column', N'outfilter'
GO
exec sp_addextendedproperty N'MS_Description', N'配置文件内容', N'user', N'dbo', N'table', N'soaps', N'column', N'params'
GO
exec sp_addextendedproperty N'MS_Description', N'如果是WSDL优先设计,可以依此控制输入输出文档的Namespace,Prefix,name等', N'user', N'dbo', N'table', N'soaps', N'column', N'aegis'
GO
exec sp_addextendedproperty N'MS_Description', N'日志目录', N'user', N'dbo', N'table', N'soaps', N'column', N'logname'
GO
exec sp_addextendedproperty N'MS_Description', N'备注', N'user', N'dbo', N'table', N'soaps', N'column', N'memo'
GO
exec sp_addextendedproperty N'MS_Description', N'创建时间', N'user', N'dbo', N'table', N'soaps', N'column', N'createtime'
GO