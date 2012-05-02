GO
/****** Object:  Table [dbo].[calendar_reminder]    Script Date: 09/09/2008 16:07:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[calendar_reminder](
	[user_id] [varchar](100) COLLATE SQL_Latin1_General_CP1_CI_AI NOT NULL,
	[event_id] [numeric](19, 0) NOT NULL,
	[send_date] [datetime] NOT NULL,
 CONSTRAINT [PK_calendar_reminder] PRIMARY KEY CLUSTERED 
(
	[user_id] ASC,
	[event_id] ASC,
	[send_date] ASC
)WITH (IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF