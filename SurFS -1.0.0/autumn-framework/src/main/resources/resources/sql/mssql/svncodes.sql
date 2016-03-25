CREATE TABLE [dbo].[svncodes] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[host] [varchar] (25) DEFAULT ('') NOT NULL ,
	[title] [varchar] (50) DEFAULT ('') NOT NULL ,
	[url] [varchar] (200) DEFAULT ('') NOT NULL ,
	[dirname] [varchar] (35) DEFAULT ('NewDir') NOT NULL ,
	[username] [varchar] (20) DEFAULT ('') NOT NULL ,
	[password] [varchar] (50) DEFAULT ('') NOT NULL ,
	[dirtype] [tinyint] DEFAULT (0) NOT NULL ,
	[message] [text] DEFAULT ('') NOT NULL ,
	CONSTRAINT [PK_svncodes] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY] ,
	CONSTRAINT [IX_svncodes_url] UNIQUE  NONCLUSTERED ([host],[url])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

 CREATE  INDEX [IX_svncodes_title] ON [dbo].[svncodes]([title]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_svncodes_dirname] ON [dbo].[svncodes]([dirname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_svncodes_dirtype] ON [dbo].[svncodes]([dirtype]) ON [PRIMARY]
GO


exec sp_addextendedproperty N'MS_Description', N'序号', N'user', N'dbo', N'table', N'svncodes', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'所属机器名', N'user', N'dbo', N'table', N'svncodes', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'工程代码标题', N'user', N'dbo', N'table', N'svncodes', N'column', N'title'
GO
exec sp_addextendedproperty N'MS_Description', N'svn资源地址', N'user', N'dbo', N'table', N'svncodes', N'column', N'url'
GO
exec sp_addextendedproperty N'MS_Description', N'本地目录', N'user', N'dbo', N'table', N'svncodes', N'column', N'dirname'
GO
exec sp_addextendedproperty N'MS_Description', N'SVN认证帐号', N'user', N'dbo', N'table', N'svncodes', N'column', N'username'
GO
exec sp_addextendedproperty N'MS_Description', N'SVN认证密码', N'user', N'dbo', N'table', N'svncodes', N'column', N'password'
GO
exec sp_addextendedproperty N'MS_Description', N'目录类型，0.java源码 1web目录（jsp）', N'user', N'dbo', N'table', N'svncodes', N'column', N'dirtype'
GO
exec sp_addextendedproperty N'MS_Description', N'更新信息', N'user', N'dbo', N'table', N'svncodes', N'column', N'message'
GO