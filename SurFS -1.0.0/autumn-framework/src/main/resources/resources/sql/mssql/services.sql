CREATE TABLE [dbo].[services] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[host] [varchar] (30) DEFAULT('') NOT NULL ,
	[title] [varchar] (60) DEFAULT('') NOT NULL ,
	[classname] [varchar] (80) DEFAULT('') NOT NULL ,
	[params] [text] DEFAULT('') NOT NULL ,
	[logname] [varchar] (20) DEFAULT('system') NOT NULL ,
	[status] [tinyint] DEFAULT (1) NOT NULL ,
	[memo] [text] DEFAULT ('') NULL ,
	[createtime] [datetime] DEFAULT (getdate()) NOT NULL ,
	CONSTRAINT [PK_services] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

 CREATE  INDEX [IX_services_title] ON [dbo].[services]([host], [title]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_services_classname] ON [dbo].[services]([classname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_services_status] ON [dbo].[services]([status]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_services_createtime] ON [dbo].[services]([createtime]) ON [PRIMARY]
GO


exec sp_addextendedproperty N'MS_Description', N'服务标识', N'user', N'dbo', N'table', N'services', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'主机名', N'user', N'dbo', N'table', N'services', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'注释', N'user', N'dbo', N'table', N'services', N'column', N'title'
GO
exec sp_addextendedproperty N'MS_Description', N'类名', N'user', N'dbo', N'table', N'services', N'column', N'classname'
GO
exec sp_addextendedproperty N'MS_Description', N'配置', N'user', N'dbo', N'table', N'services', N'column', N'params'
GO
exec sp_addextendedproperty N'MS_Description', N'日志目录', N'user', N'dbo', N'table', N'services', N'column', N'logname'
GO
exec sp_addextendedproperty N'MS_Description', N'0随服务启动 1手动 2禁用', N'user', N'dbo', N'table', N'services', N'column', N'status'
GO
exec sp_addextendedproperty N'MS_Description', N'备注', N'user', N'dbo', N'table', N'services', N'column', N'memo'
GO
exec sp_addextendedproperty N'MS_Description', N'创建时间', N'user', N'dbo', N'table', N'services', N'column', N'createtime'
GO