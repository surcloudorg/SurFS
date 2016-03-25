package main

import (
	"encoding/json"
	"fmt"
	"github.com/codegangsta/cli"
	"os"
	"surfs"
	"surfs/version"
)

var (
	defaultConfig string = "/etc/surfs/config.json"
	prettyJson    bool   = false
)

func cmdDumpConfig(c *cli.Context) {
	client := createClient(c)
	b, _ := json.Marshal(client.GetConfig())
	fmt.Printf("%s\n", string(b))
}

func createClient(ctx *cli.Context) *surfs.Client {
	cfg, err := loadConfig(ctx)
	if err != nil {
		printErrorAndExit("error: failed to load config: %v", err)
	}
	adjustConfig(ctx, cfg)
	c, err := surfs.NewClient(cfg)
	if err != nil {
		printErrorAndExit("error: failed to init client: %v", err)
	}

	err = c.ConnectToSurFS()
	if err != nil {
		printErrorAndExit("error: failed to init client: %v", err)
	}
	return c
}

func main() {
	app := cli.NewApp()
	app.Version = fmt.Sprintf("%s (Git: %s)",
		version.Version, version.GitRevision)
	app.Usage = "SurFS command line tool"
	app.Name = "surfs cli"
	app.Author = "SurDoc"
	app.CommandNotFound = func(ctx *cli.Context, command string) {
		printErrorAndExit("Command not found: %v", command)
	}
	app.Writer = os.Stdout
	app.Flags = []cli.Flag{
		&cli.BoolFlag{
			Name:   "debug",
			Usage:  "debug",
			EnvVar: "SURFS_DEBUG",
		},
		&cli.StringFlag{
			Name:   "proxy",
			Usage:  "proxy",
			Value:  "",
			EnvVar: "SURFS_PROXY",
		},
		&cli.IntFlag{
			Name:  "timeout",
			Usage: "timeout in second",
			Value: 0,
		},
		&cli.StringFlag{
			Name:  "config",
			Usage: "config file",
			Value: "",
		},

		&cli.StringFlag{
			Name:  "log",
			Usage: "log file",
			Value: "",
		},

		&cli.BoolFlag{
			Name:        "pretty",
			Usage:       "pretty json output",
			Destination: &prettyJson,
		},
	}
	app.Commands = []cli.Command{
		{
			Name:        "dumpconfig",
			Usage:       "Dump configuration",
			ArgsUsage:   " ",
			Description: "dump current configuration",
			Action:      cmdDumpConfig,
		},
	}
	app.Commands = append(app.Commands, miscCommands...)
	app.Commands = append(app.Commands, volumeCommands...)
	app.Commands = append(app.Commands, snapCommands...)

	err := app.Run(os.Args)
	if err != nil {
		os.Exit(1)
	}
}
