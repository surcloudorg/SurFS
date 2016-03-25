CREATE TABLE [dbo].[hibernatemap] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[host] [varchar] (30) DEFAULT ('') NOT NULL ,
	[title] [varchar] (60) DEFAULT ('') NOT NULL ,
	[classname] [varchar] (80) DEFAULT ('') NOT NULL ,
	[datasource] [varchar] (20) DEFAULT ('') NOT NULL ,
	[tablename] [varchar] (20) DEFAULT ('') NOT NULL ,
	[catalogname] [varchar] (20) DEFAULT ('') NOT NULL ,
	[xmlmap] [text] DEFAULT ('') NOT NULL ,
	[createtime] [datetime] DEFAULT (getdate()) NOT NULL,
	CONSTRAINT [PK_hibernatemap] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY] ,
	CONSTRAINT [IX_hibernatemap_host] UNIQUE  NONCLUSTERED ([host],[classname],[datasource])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

 CREATE  INDEX [IX_hibernatemap_catalogname] ON [dbo].[hibernatemap]([catalogname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_hibernatemap_createtime] ON [dbo].[hibernatemap]([createtime]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_hibernatemap_title] ON [dbo].[hibernatemap]([title]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_hibernatemap_tablename] ON [dbo].[hibernatemap]([tablename]) ON [PRIMARY]
GO

exec sp_addextendedproperty N'MS_Description', N'序号', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'服务器机器名', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'标题', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'title'
GO
exec sp_addextendedproperty N'MS_Description', N'映射类名', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'classname'
GO
exec sp_addextendedproperty N'MS_Description', N'数据库连接池名', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'datasource'
GO
exec sp_addextendedproperty N'MS_Description', N'映射的表名', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'tablename'
GO
exec sp_addextendedproperty N'MS_Description', N'数据库目录', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'catalogname'
GO
exec sp_addextendedproperty N'MS_Description', N'映射配置', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'xmlmap'
GO
exec sp_addextendedproperty N'MS_Description', N'创建/最后修改时间', N'user', N'dbo', N'table', N'hibernatemap', N'column', N'createtime'
GO