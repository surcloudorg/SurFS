CREATE TABLE [dbo].[webdirectory] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[host] [varchar] (30) DEFAULT ('') NOT NULL ,
	[dirname] [varchar] (20) DEFAULT ('') NOT NULL ,
	[title] [varchar] (60) DEFAULT ('') NOT NULL ,
	[classname] [varchar] (80) DEFAULT ('') NOT NULL ,
	[defaultpage] [varchar] (20) DEFAULT ('') NOT NULL ,
	[iplist] [varchar] (100) DEFAULT ('') NOT NULL ,
	[logintype] [tinyint] DEFAULT (1) NOT NULL ,
	[params] [text] DEFAULT ('') NOT NULL ,
	[logname] [varchar] (20) DEFAULT ('system') NOT NULL ,
	[charset] [varchar] (20) DEFAULT ('') NOT NULL ,
	[memo] [text] DEFAULT ('') NOT NULL ,
	[createtime] [datetime] DEFAULT (getdate()) NOT NULL ,
	CONSTRAINT [PK_webdirectory] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY] ,
	CONSTRAINT [IX_webdirectory_dirname] UNIQUE  NONCLUSTERED ([host],[dirname])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO


 CREATE  INDEX [IX_webdirectory_title] ON [dbo].[webdirectory]([title]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webdirectory_classname] ON [dbo].[webdirectory]([classname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webdirectory_logintype] ON [dbo].[webdirectory]([logintype]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webdirectory_createtime] ON [dbo].[webdirectory]([createtime]) ON [PRIMARY]
GO


exec sp_addextendedproperty N'MS_Description', N'目录标识', N'user', N'dbo', N'table', N'webdirectory', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'主机', N'user', N'dbo', N'table', N'webdirectory', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'目录名', N'user', N'dbo', N'table', N'webdirectory', N'column', N'dirname'
GO
exec sp_addextendedproperty N'MS_Description', N'注释', N'user', N'dbo', N'table', N'webdirectory', N'column', N'title'
GO
exec sp_addextendedproperty N'MS_Description', N'类名', N'user', N'dbo', N'table', N'webdirectory', N'column', N'classname'
GO
exec sp_addextendedproperty N'MS_Description', N'首页', N'user', N'dbo', N'table', N'webdirectory', N'column', N'defaultpage'
GO
exec sp_addextendedproperty N'MS_Description', N'需要验证ip，null不验证', N'user', N'dbo', N'table', N'webdirectory', N'column', N'iplist'
GO
exec sp_addextendedproperty N'MS_Description', N'认证类型，0禁止访问，1需要登录，2不需登录，3公共目录,4需要basic认证', N'user', N'dbo', N'table', N'webdirectory', N'column', N'logintype'
GO
exec sp_addextendedproperty N'MS_Description', N'配置', N'user', N'dbo', N'table', N'webdirectory', N'column', N'params'
GO
exec sp_addextendedproperty N'MS_Description', N'日志目录', N'user', N'dbo', N'table', N'webdirectory', N'column', N'logname'
GO
exec sp_addextendedproperty N'MS_Description', N'http请求编码', N'user', N'dbo', N'table', N'webdirectory', N'column', N'charset'
GO
exec sp_addextendedproperty N'MS_Description', N'备注', N'user', N'dbo', N'table', N'webdirectory', N'column', N'memo'
GO
exec sp_addextendedproperty N'MS_Description', N'创建/修改时间', N'user', N'dbo', N'table', N'webdirectory', N'column', N'createtime'
GO