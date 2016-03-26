/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package main

import (
	"github.com/codegangsta/cli"
	"surfscli/version"
)

var miscCommands = []cli.Command{
	{
		Name:      "info",
		Usage:     "Get information of surfs cluster",
		ArgsUsage: " ",
		Action:    cmdInfo,
	},

	{
		Name:      "connect",
		Usage:     "Connect to specific pools or all pools if no pool specified",
		ArgsUsage: "[pool1] ...",
		Action:    cmdConnect,
	},

	{
		Name:      "disconnect",
		Usage:     "Disconnect a pool from surfs",
		ArgsUsage: "<pool_name>",
		Action:    cmdDisconnect,
	},

	{
		Name:        "disk_cab_list",
		Usage:       "List all pools",
		ArgsUsage:   " ",
		Description: "list all pools of SurFS cluster",
		Action:      cmdDiskCabList,
	},

	{
		Name:        "disk_cab_status",
		Usage:       "Get status of a pool",
		ArgsUsage:   "<poolname>",
		Description: "get status of a pool",
		Action:      cmdDiskCabStatus,
	},

	{
		Name:        "version",
		Usage:       "Print version information",
		ArgsUsage:   " ",
		Description: "show version",
		Action:      cmdFsVersion,
	},
}

func cmdFsVersion(c *cli.Context) {
	client := createClient(c)
	fsVersion, err := client.FsVersion()
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type surVersion struct {
			Version    string `json:"version"`
			CliVersion string `json:"cliVersion"`
		}
		data := &surVersion{fsVersion, version.Version}
		printData(data)
	}
}

func cmdInfo(c *cli.Context) {
	client := createClient(c)

	info, err := client.GetInfo()
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(info)
	}
}

func cmdConnect(c *cli.Context) {
	client := createClient(c)

	pools, err := client.Connect(c.Args())
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(pools)
	}
}

func cmdDisconnect(c *cli.Context) {
	client := createClient(c)

	if len(c.Args()) == 0 {
		printErrorAndExit("error: no pool name")
	}

	if len(c.Args()) > 1 {
		printErrorAndExit("error: too many pool names")
	}

	pools, err := client.Disconnect(c.Args().First())
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(pools)
	}
}

func cmdDiskCabList(c *cli.Context) {
	client := createClient(c)
	pools, err := client.GetPoolList()
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(pools)
	}
}

func cmdDiskCabStatus(c *cli.Context) {
	client := createClient(c)
	if len(c.Args()) == 0 {
		printErrorAndExit("error: no pool name")
	}

	info, err := client.GetPoolInfo(c.Args().First())
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(info)
	}
}
