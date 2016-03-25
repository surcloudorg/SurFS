CREATE TABLE [dbo].[webuser] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[dirid] [int] DEFAULT (0) NOT NULL ,
	[soapid] [int] DEFAULT (0) NOT NULL ,
	[username] [varchar] (30) DEFAULT ('') NOT NULL ,
	[password] [varchar] (30) DEFAULT ('') NOT NULL ,
	[realname] [varchar] (30) DEFAULT ('') NOT NULL ,
	[usergroup] [varchar] (30) DEFAULT ('') NOT NULL ,
	[mobile] [varchar] (30) DEFAULT ('') NOT NULL ,
	[email] [varchar] (60) DEFAULT ('') NOT NULL ,
	[permission] [varchar] (50) DEFAULT ('') NOT NULL ,
	[stimeout] [int] DEFAULT (0) NOT NULL ,
	[isactive] [bit] DEFAULT (1) NOT NULL ,
	[iplist] [varchar] (100) DEFAULT ('') NOT NULL ,
	[extparams] [varchar] (60) DEFAULT ('') NOT NULL ,
	[logintime] [datetime] DEFAULT ('1900-01-01') NOT NULL ,
	[memo] [text] DEFAULT ('') NOT NULL ,
	[createtime] [datetime] DEFAULT (getdate()) NOT NULL ,
	CONSTRAINT [PK_webuser] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY] ,
	CONSTRAINT [IX_webuser_username] UNIQUE  NONCLUSTERED ([username])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_realname] ON [dbo].[webuser]([realname]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_usergroup] ON [dbo].[webuser]([usergroup]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_mobile] ON [dbo].[webuser]([mobile]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_email] ON [dbo].[webuser]([email]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_logintime] ON [dbo].[webuser]([logintime]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_extparams] ON [dbo].[webuser]([extparams]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_createtime] ON [dbo].[webuser]([createtime]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_webuser_dirid] ON [dbo].[webuser]([dirid]) ON [PRIMARY]
GO


exec sp_addextendedproperty N'MS_Description', N'标识', N'user', N'dbo', N'table', N'webuser', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'WEB服务ID，内置目录（0=console,-1=services）', N'user', N'dbo', N'table', N'webuser', N'column', N'dirid'
GO
exec sp_addextendedproperty N'MS_Description', N'soap服务ID,dirid=-1（services目录）时有效', N'user', N'dbo', N'table', N'webuser', N'column', N'soapid'
GO
exec sp_addextendedproperty N'MS_Description', N'用户名', N'user', N'dbo', N'table', N'webuser', N'column', N'username'
GO
exec sp_addextendedproperty N'MS_Description', N'密码', N'user', N'dbo', N'table', N'webuser', N'column', N'password'
GO
exec sp_addextendedproperty N'MS_Description', N'真实名称', N'user', N'dbo', N'table', N'webuser', N'column', N'realname'
GO
exec sp_addextendedproperty N'MS_Description', N'组名，由具体web服务定义', N'user', N'dbo', N'table', N'webuser', N'column', N'usergroup'
GO
exec sp_addextendedproperty N'MS_Description', N'手机号', N'user', N'dbo', N'table', N'webuser', N'column', N'mobile'
GO
exec sp_addextendedproperty N'MS_Description', N'邮箱', N'user', N'dbo', N'table', N'webuser', N'column', N'email'
GO
exec sp_addextendedproperty N'MS_Description', N'权限，由具体WEB服务定义，如：console目录，由10位数字组成字符串表示', N'user', N'dbo', N'table', N'webuser', N'column', N'permission'
GO
exec sp_addextendedproperty N'MS_Description', N'对话超时，0默认值=servlet容器定义的值', N'user', N'dbo', N'table', N'webuser', N'column', N'stimeout'
GO
exec sp_addextendedproperty N'MS_Description', N'1激活0停用', N'user', N'dbo', N'table', N'webuser', N'column', N'isactive'
GO
exec sp_addextendedproperty N'MS_Description', N'不填不验证IP，否则只允许指定IP访问，,隔开如：192.168.*.*,211.232.*.*', N'user', N'dbo', N'table', N'webuser', N'column', N'iplist'
GO
exec sp_addextendedproperty N'MS_Description', N'预留字段', N'user', N'dbo', N'table', N'webuser', N'column', N'extparams'
GO
exec sp_addextendedproperty N'MS_Description', N'最后登录时间', N'user', N'dbo', N'table', N'webuser', N'column', N'logintime'
GO
exec sp_addextendedproperty N'MS_Description', N'备注', N'user', N'dbo', N'table', N'webuser', N'column', N'memo'
GO
exec sp_addextendedproperty N'MS_Description', N'创建时间', N'user', N'dbo', N'table', N'webuser', N'column', N'createtime'
GO