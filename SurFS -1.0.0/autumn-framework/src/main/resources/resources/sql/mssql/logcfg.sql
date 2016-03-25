CREATE TABLE [dbo].[logcfg] (
	[logname] [varchar] (20) DEFAULT ('system') NOT NULL ,
	[host] [varchar] (30) DEFAULT ('') NOT NULL ,
	[dateformatter] [varchar] (20) DEFAULT ('[MM-dd HH:mm:ss]') NOT NULL ,
	[filter] [varchar] (400) DEFAULT ('') NOT NULL ,
	[warnclass] [varchar] (80) DEFAULT ('') NOT NULL ,
	[warninteral] [int] DEFAULT (0) NOT NULL ,
	[level] [tinyint] DEFAULT (1) NOT NULL ,
	[addlevel] [bit] DEFAULT (1) NOT NULL ,
	[addclassname] [bit] DEFAULT (1) NOT NULL ,
	[outconsole] [bit] DEFAULT (0) NOT NULL ,
        [sysloghost] [varchar] (30) DEFAULT ('') NOT NULL ,
        [syslogfacility] [varchar] (10) DEFAULT ('') NOT NULL ,
	[params] [text] DEFAULT ('') NOT NULL ,
	CONSTRAINT [PK_logcfg] PRIMARY KEY  CLUSTERED ([logname],[host])  ON [PRIMARY] 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

exec sp_addextendedproperty N'MS_Description', N'日志目录', N'user', N'dbo', N'table', N'logcfg', N'column', N'logname'
GO
exec sp_addextendedproperty N'MS_Description', N'主机', N'user', N'dbo', N'table', N'logcfg', N'column', N'host'
GO
exec sp_addextendedproperty N'MS_Description', N'时间格式', N'user', N'dbo', N'table', N'logcfg', N'column', N'dateformatter'
GO
exec sp_addextendedproperty N'MS_Description', N'过滤器，正则表达式,符合条件一定输出', N'user', N'dbo', N'table', N'logcfg', N'column', N'filter'
GO
exec sp_addextendedproperty N'MS_Description', N'报警接口实现类名', N'user', N'dbo', N'table', N'logcfg', N'column', N'warnclass'
GO
exec sp_addextendedproperty N'MS_Description', N'报警间隔，0不间隔，只要产生fatal日志就报警', N'user', N'dbo', N'table', N'logcfg', N'column', N'warninteral'
GO
exec sp_addextendedproperty N'MS_Description', N'日志级别，0debug,1info,2warn,3error,4fatal', N'user', N'dbo', N'table', N'logcfg', N'column', N'level'
GO
exec sp_addextendedproperty N'MS_Description', N'日志里添加输出级别', N'user', N'dbo', N'table', N'logcfg', N'column', N'addlevel'
GO
exec sp_addextendedproperty N'MS_Description', N'日志里添加类名', N'user', N'dbo', N'table', N'logcfg', N'column', N'addclassname'
GO
exec sp_addextendedproperty N'MS_Description', N'是否输出到控制台', N'user', N'dbo', N'table', N'logcfg', N'column', N'outconsole'
GO
exec sp_addextendedproperty N'MS_Description', N'linux.syslog服务器名', N'user', N'dbo', N'table', N'logcfg', N'column', N'sysloghost'
GO
exec sp_addextendedproperty N'MS_Description', N'linux.syslog设备名', N'user', N'dbo', N'table', N'logcfg', N'column', N'syslogfacility'
GO
exec sp_addextendedproperty N'MS_Description', N'配置信息', N'user', N'dbo', N'table', N'logcfg', N'column', N'params'
GO