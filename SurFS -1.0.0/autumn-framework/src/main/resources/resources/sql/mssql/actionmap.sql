CREATE TABLE [dbo].[actionmap] (
	[id] [int] IDENTITY (1, 1) NOT NULL,
	[actionid] [varchar] (100) NOT NULL DEFAULT (''),
	[dirid] [int] NOT NULL DEFAULT ('0'),
	[subdir] [varchar] (20) NULL,
	[classname] [varchar] (80) NOT NULL DEFAULT (''),
	[permissionorder] [smallint] NOT NULL DEFAULT (-1),
	[params] [text] NULL,
	[menu] [varchar] (100) NOT NULL DEFAULT ('NA'),
	[memo] [text] NULL,
	[createtime] [datetime] NOT NULL DEFAULT (getdate()),
	CONSTRAINT [PK_actionmap] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY],
	CONSTRAINT [IX_actionmap_actionid] UNIQUE  NONCLUSTERED ([actionid],[dirid],[subdir])  ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

 CREATE  INDEX [IX_actionmap_classname] ON [dbo].[actionmap]([classname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_actionmap_menu] ON [dbo].[actionmap]([menu]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_actionmap_createtime] ON [dbo].[actionmap]([createtime]) ON [PRIMARY]
GO


exec sp_addextendedproperty N'MS_Description', N'标识', N'user', N'dbo', N'table', N'actionmap', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'actionid', N'user', N'dbo', N'table', N'actionmap', N'column', N'actionid'
GO
exec sp_addextendedproperty N'MS_Description', N'目录ID', N'user', N'dbo', N'table', N'actionmap', N'column', N'dirid'
GO
exec sp_addextendedproperty N'MS_Description', N'相对于dir的下一级目录', N'user', N'dbo', N'table', N'actionmap', N'column', N'subdir'
GO
exec sp_addextendedproperty N'MS_Description', N'类名', N'user', N'dbo', N'table', N'actionmap', N'column', N'classname'
GO
exec sp_addextendedproperty N'MS_Description', N'权限位', N'user', N'dbo', N'table', N'actionmap', N'column', N'permissionorder'
GO
exec sp_addextendedproperty N'MS_Description', N'配置', N'user', N'dbo', N'table', N'actionmap', N'column', N'params'
GO
exec sp_addextendedproperty N'MS_Description', N'菜单', N'user', N'dbo', N'table', N'actionmap', N'column', N'menu'
GO
exec sp_addextendedproperty N'MS_Description', N'创建/修改时间', N'user', N'dbo', N'table', N'actionmap', N'column', N'createtime'
GO