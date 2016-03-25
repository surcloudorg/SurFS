CREATE TABLE [dbo].[demo] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[name] [varchar] (20) DEFAULT ('') NOT NULL ,
	[age] [int] DEFAULT (0) NOT NULL ,
	[sex] [bit] DEFAULT (1) NOT NULL ,
	[mobile] [varchar] (20) DEFAULT ('') NOT NULL ,
	[regtime] [datetime] DEFAULT (getdate()),
	[memo] [text] DEFAULT (''),
	CONSTRAINT [PK_demo] PRIMARY KEY  CLUSTERED ([id])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

exec sp_addextendedproperty N'MS_Description', N'序号', N'user', N'dbo', N'table', N'demo', N'column', N'id'
GO
exec sp_addextendedproperty N'MS_Description', N'姓名', N'user', N'dbo', N'table', N'demo', N'column', N'name'
GO
exec sp_addextendedproperty N'MS_Description', N'年龄', N'user', N'dbo', N'table', N'demo', N'column', N'age'
GO
exec sp_addextendedproperty N'MS_Description', N'性别', N'user', N'dbo', N'table', N'demo', N'column', N'sex'
GO
exec sp_addextendedproperty N'MS_Description', N'电话', N'user', N'dbo', N'table', N'demo', N'column', N'mobile'
GO
exec sp_addextendedproperty N'MS_Description', N'时间', N'user', N'dbo', N'table', N'demo', N'column', N'regtime'
GO
exec sp_addextendedproperty N'MS_Description', N'备注', N'user', N'dbo', N'table', N'demo', N'column', N'memo'
GO